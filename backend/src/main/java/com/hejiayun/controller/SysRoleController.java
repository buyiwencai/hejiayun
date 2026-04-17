package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.SysRole;
import com.hejiayun.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public CommonResult<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        Page<SysRole> result = sysRoleService.page(page, wrapper);
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<SysRole>> getAll() {
        List<SysRole> roles = sysRoleService.list();
        return CommonResult.success(roles);
    }

    @GetMapping("/{id}")
    public CommonResult<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return CommonResult.success(role);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody SysRole sysRole,
                                @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysRole.setCreateTime(LocalDateTime.now());
        sysRoleService.save(sysRole);
        return CommonResult.success("角色添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody SysRole sysRole,
                                   @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysRole.setUpdateTime(LocalDateTime.now());
        sysRoleService.updateById(sysRole);
        return CommonResult.success("角色更新成功");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public CommonResult<?> delete(@PathVariable Long id,
                                   @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        sysRoleService.removeById(id);
        return CommonResult.success("角色删除成功");
    }
}
