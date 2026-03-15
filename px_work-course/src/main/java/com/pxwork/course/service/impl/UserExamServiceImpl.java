package com.pxwork.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.course.entity.Certificate;
import com.pxwork.course.entity.Exam;
import com.pxwork.course.entity.ProcessEvaluation;
import com.pxwork.course.entity.UserExam;
import com.pxwork.course.mapper.UserExamMapper;
import com.pxwork.course.service.CertificateService;
import com.pxwork.course.service.ExamService;
import com.pxwork.course.service.ProcessEvaluationService;
import com.pxwork.course.service.UserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserExamServiceImpl extends ServiceImpl<UserExamMapper, UserExam> implements UserExamService {

    @Autowired
    private ExamService examService;

    @Autowired
    private ProcessEvaluationService processEvaluationService;

    @Autowired
    private CertificateService certificateService;

    @Override
    public Map<String, Object> calculateFinalResult(Long userExamId) {
        UserExam userExam = getById(userExamId);
        if (userExam == null) {
            throw new IllegalArgumentException("学员考试记录不存在");
        }
        Exam exam = examService.getById(userExam.getExamId());
        if (exam == null) {
            throw new IllegalArgumentException("考试配置不存在");
        }

        List<ProcessEvaluation> processEvaluationList = processEvaluationService.list(new LambdaQueryWrapper<ProcessEvaluation>()
                .eq(ProcessEvaluation::getUserId, userExam.getUserId())
                .eq(ProcessEvaluation::getCourseId, userExam.getCourseId())
                .orderByDesc(ProcessEvaluation::getId));
        ProcessEvaluation processEvaluation = processEvaluationList.isEmpty() ? null : processEvaluationList.get(0);
        BigDecimal processScore = valueOrZero(processEvaluation == null ? null : processEvaluation.getTotalScore());
        BigDecimal objectiveScore = valueOrZero(userExam.getObjectiveScore());
        BigDecimal subjectiveScore = valueOrZero(userExam.getSubjectiveScore());
        BigDecimal practicalScore = valueOrZero(userExam.getPracticalScore());
        BigDecimal endScore = objectiveScore.add(subjectiveScore);

        BigDecimal weightProcess = valueOrZero(exam.getWeightProcess());
        BigDecimal weightEnd = valueOrZero(exam.getWeightEnd());
        BigDecimal weightPractical = valueOrZero(exam.getWeightPractical());
        BigDecimal finalScore = processScore.multiply(weightProcess)
                .add(endScore.multiply(weightEnd))
                .add(practicalScore.multiply(weightPractical))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal passTotalScore = valueOrZero(exam.getPassTotalScore());
        BigDecimal passProcessScore = valueOrZero(exam.getPassProcessScore());
        BigDecimal passEndScore = valueOrZero(exam.getPassEndScore());
        BigDecimal passPracticalScore = valueOrZero(exam.getPassPracticalScore());

        boolean passed = finalScore.compareTo(passTotalScore) >= 0
                && processScore.compareTo(passProcessScore) >= 0
                && endScore.compareTo(passEndScore) >= 0
                && practicalScore.compareTo(passPracticalScore) >= 0;

        userExam.setFinalScore(finalScore);
        userExam.setStatus(2);
        userExam.setIsPassed(passed ? 1 : 0);
        updateById(userExam);

        boolean freeMakeUp = false;
        if (passed) {
            long certCount = certificateService.count(new LambdaQueryWrapper<Certificate>()
                    .eq(Certificate::getUserId, userExam.getUserId())
                    .eq(Certificate::getCourseId, userExam.getCourseId()));
            if (certCount == 0) {
                Certificate certificate = new Certificate();
                certificate.setUserId(userExam.getUserId());
                certificate.setCourseId(userExam.getCourseId());
                certificate.setCertNo(buildCertNo(userExam.getUserId(), userExam.getCourseId()));
                certificate.setIssueDate(LocalDate.now());
                certificate.setStatus(0);
                certificateService.save(certificate);
            }
        } else {
            Integer makeUpCount = userExam.getMakeUpCount();
            freeMakeUp = makeUpCount == null || makeUpCount < 1;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userExamId", userExamId);
        result.put("processScore", processScore);
        result.put("endScore", endScore);
        result.put("practicalScore", practicalScore);
        result.put("finalScore", finalScore);
        result.put("isPassed", passed);
        result.put("freeMakeUp", freeMakeUp);
        if (!passed && freeMakeUp) {
            result.put("message", "可免费补考");
        } else {
            result.put("message", passed ? "已通过并进入证书公示" : "未通过");
        }
        return result;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String buildCertNo(Long userId, Long courseId) {
        return "CERT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + userId + "-" + courseId;
    }
}
