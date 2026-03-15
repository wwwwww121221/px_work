package com.pxwork.api.controller.backend;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pxwork.common.utils.Result;
import com.pxwork.system.entity.AdminRole;
import com.pxwork.system.service.AdminRoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 管理员角色 前端控制器
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Tag(name = "1.2 后台-角色权限管理")
@RestController
@RequestMapping("/admin-role")
public class AdminRoleController {

    @Autowired
    private AdminRoleService adminRoleService;

    @Operation(summary = "角色分页列表", description = "获取角色分页列表")
    @GetMapping("/list")
    public Result<Page<AdminRole>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        Page<AdminRole> page = new Page<>(current, size);
        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(AdminRole::getName, name);
        }
        queryWrapper.orderByDesc(AdminRole::getCreatedAt);
        
        return Result.success(adminRoleService.page(page, queryWrapper));
    }

    @Operation(summary = "新增角色", description = "创建新角色")
    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody AdminRole adminRole) {
        boolean success = adminRoleService.save(adminRole);
        return success ? Result.success(true) : Result.fail("创建失败");
    }

    @Operation(summary = "修改角色", description = "更新角色信息")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody AdminRole adminRole) {
        boolean success = adminRoleService.updateById(adminRole);
        return success ? Result.success(true) : Result.fail("更新失败");
    }

    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = adminRoleService.removeById(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
}
