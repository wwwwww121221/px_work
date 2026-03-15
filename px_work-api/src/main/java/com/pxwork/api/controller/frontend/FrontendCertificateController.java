package com.pxwork.api.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.utils.Result;
import com.pxwork.common.utils.StpUserUtil;
import com.pxwork.course.entity.Certificate;
import com.pxwork.course.entity.CertificateRequest;
import com.pxwork.course.service.CertificateRequestService;
import com.pxwork.course.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "前台证书")
@RestController
@RequestMapping("/frontend/certificates")
public class FrontendCertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRequestService certificateRequestService;

    @Operation(summary = "证书公示名单")
    @GetMapping("/public")
    public Result<List<Certificate>> publicList() {
        LocalDate fromDate = LocalDate.now().minusDays(3);
        List<Certificate> list = certificateService.list(new LambdaQueryWrapper<Certificate>()
                .eq(Certificate::getStatus, 0)
                .ge(Certificate::getIssueDate, fromDate)
                .orderByDesc(Certificate::getIssueDate));
        return Result.success(list);
    }

    @Operation(summary = "我的证书")
    @GetMapping("/my")
    public Result<List<Certificate>> myCertificates() {
        long userId = StpUserUtil.getLoginIdAsLong();
        List<Certificate> list = certificateService.list(new LambdaQueryWrapper<Certificate>()
                .eq(Certificate::getUserId, userId)
                .eq(Certificate::getStatus, 1)
                .orderByDesc(Certificate::getIssueDate));
        return Result.success(list);
    }

    @Operation(summary = "申请纸质邮寄")
    @PostMapping("/{id}/requests")
    public Result<Boolean> createRequest(@PathVariable Long id, @RequestBody @Validated CertificateRequestBody body) {
        long userId = StpUserUtil.getLoginIdAsLong();
        Certificate certificate = certificateService.getById(id);
        if (certificate == null || !certificate.getUserId().equals(userId)) {
            return Result.fail("证书不存在");
        }
        long exists = certificateRequestService.count(new LambdaQueryWrapper<CertificateRequest>()
                .eq(CertificateRequest::getUserId, userId)
                .eq(CertificateRequest::getCertificateId, id));
        if (exists > 0) {
            return Result.fail("已提交纸质申请");
        }
        CertificateRequest request = new CertificateRequest();
        request.setUserId(userId);
        request.setCertificateId(id);
        request.setReceiverName(body.getReceiverName());
        request.setPhone(body.getPhone());
        request.setAddress(body.getAddress());
        request.setStatus(0);
        return Result.success(certificateRequestService.save(request));
    }

    @Data
    public static class CertificateRequestBody {
        @NotBlank(message = "收件人不能为空")
        private String receiverName;
        @NotBlank(message = "联系电话不能为空")
        private String phone;
        @NotBlank(message = "收件地址不能为空")
        private String address;
    }
}
