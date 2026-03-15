package com.pxwork.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.course.entity.ExamQuestion;
import com.pxwork.course.mapper.ExamQuestionMapper;
import com.pxwork.course.service.ExamQuestionService;
import org.springframework.stereotype.Service;

@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {
}
