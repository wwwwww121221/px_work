package com.pxwork.api.controller.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pxwork.common.utils.Result;
import com.pxwork.resource.entity.ResourceCategory;
import com.pxwork.resource.service.ResourceCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 资源分类 前端控制器
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Tag(name = "2.5 后台-素材资源管理")
@RestController
@RequestMapping("/resource-category")
public class ResourceCategoryController {

    @Autowired
    private ResourceCategoryService resourceCategoryService;

    @Operation(summary = "获取分类树", description = "获取资源分类的树形结构")
    @GetMapping("/tree")
    public Result<List<ResourceCategory>> tree() {
        return Result.success(resourceCategoryService.listTree());
    }

    @Operation(summary = "新增分类", description = "创建新的资源分类")
    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody ResourceCategory category) {
        boolean success = resourceCategoryService.save(category);
        return success ? Result.success(true) : Result.fail("创建失败");
    }

    @Operation(summary = "修改分类", description = "更新资源分类信息")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ResourceCategory category) {
        boolean success = resourceCategoryService.updateById(category);
        return success ? Result.success(true) : Result.fail("更新失败");
    }

    @Operation(summary = "删除分类", description = "根据ID删除资源分类")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // TODO: Check if has children or resources before delete
        boolean success = resourceCategoryService.removeById(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }
}
