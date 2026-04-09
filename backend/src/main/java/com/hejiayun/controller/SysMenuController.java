package com.hejiayun.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.SysMenu;
import com.hejiayun.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/list")
    public CommonResult<Page<SysMenu>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        Page<SysMenu> result = sysMenuService.page(page);
        return CommonResult.success(result);
    }

    @GetMapping("/tree")
    public CommonResult<List<SysMenu>> getMenuTree() {
        List<SysMenu> tree = sysMenuService.getMenuTree();
        return CommonResult.success(tree);
    }

    @GetMapping("/{id}")
    public CommonResult<SysMenu> getById(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        return CommonResult.success(menu);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody SysMenu sysMenu,
                                @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysMenu.setCreateTime(LocalDateTime.now());
        sysMenuService.save(sysMenu);
        return CommonResult.success("菜单添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody SysMenu sysMenu,
                                   @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysMenu.setUpdateTime(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);
        return CommonResult.success("菜单更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id,
                                   @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysMenuService.removeById(id);
        return CommonResult.success("菜单删除成功");
    }
}
