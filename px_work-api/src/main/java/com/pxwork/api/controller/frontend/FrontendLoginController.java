package com.pxwork.api.controller.frontend;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pxwork.common.entity.User;
import com.pxwork.common.request.FrontendLoginRequest;
import com.pxwork.common.service.UserService;
import com.pxwork.common.utils.Result;
import com.pxwork.common.utils.StpUserUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "前台学员登录")
@RestController
@RequestMapping("/frontend")
public class FrontendLoginController {

    @Autowired
    private UserService userService;

    @Operation(summary = "学员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated FrontendLoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest);
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("tokenName", StpUserUtil.getStpLogic().getTokenName());
            tokenInfo.put("tokenValue", token);
            return Result.success(tokenInfo);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("登录失败，请稍后重试");
        }
    }

    @Operation(summary = "学员注销登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUserUtil.logout();
        return Result.success("注销成功");
    }

    @Operation(summary = "获取当前学员信息")
    @GetMapping("/user/info")
    public Result<User> userInfo() {
        long userId = StpUserUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
