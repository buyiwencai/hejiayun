package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.SysUser;
import com.hejiayun.model.entity.SysUserRole;
import com.hejiayun.service.SysUserRoleService;
import com.hejiayun.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    private static final String ADMIN_USERNAME = "admin";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @GetMapping("/list")
    public CommonResult<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(SysUser::getPhone, phone);
        }
        Page<SysUser> result = sysUserService.page(page, wrapper);
        return CommonResult.success(result);
    }

    @GetMapping("/{id}")
    public CommonResult<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return CommonResult.success(user);
    }

    @PostMapping
    @Transactional
    public CommonResult<?> add(@RequestBody SysUser sysUser) {
        sysUser.setCreateTime(LocalDateTime.now());
        sysUserService.save(sysUser);
        return CommonResult.success("用户添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody SysUser sysUser,
                                  @RequestHeader(value = "X-Username", required = false) String currentUsername) {
        // 保护 admin 账号
        SysUser existingUser = sysUserService.getById(sysUser.getId());
        if (existingUser != null && ADMIN_USERNAME.equals(existingUser.getUsername())) {
            if (!ADMIN_USERNAME.equals(currentUsername)) {
                return CommonResult.error(403, "无权限修改 admin 账号");
            }
        }
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return CommonResult.success("用户更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id,
                                  @RequestHeader(value = "X-Username", required = false) String currentUsername) {
        SysUser user = sysUserService.getById(id);
        if (user != null && ADMIN_USERNAME.equals(user.getUsername())) {
            if (!ADMIN_USERNAME.equals(currentUsername)) {
                return CommonResult.error(403, "无权限删除 admin 账号");
            }
        }
        sysUserService.removeById(id);
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        return CommonResult.success("用户删除成功");
    }

    @PutMapping("/status/{id}")
    public CommonResult<?> updateStatus(@PathVariable Long id,
                                        @RequestParam Integer status,
                                        @RequestHeader(value = "X-Username", required = false) String currentUsername) {
        SysUser user = sysUserService.getById(id);
        if (user != null && ADMIN_USERNAME.equals(user.getUsername())) {
            if (!ADMIN_USERNAME.equals(currentUsername)) {
                return CommonResult.error(403, "无权限修改 admin 账号状态");
            }
        }
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        sysUserService.updateById(updateUser);
        return CommonResult.success("状态更新成功");
    }

    @GetMapping("/{id}/roles")
    public CommonResult<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = sysUserRoleService.list(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        ).stream().map(SysUserRole::getRoleId).toList();
        return CommonResult.success(roleIds);
    }

    @PutMapping("/{id}/roles")
    @Transactional
    public CommonResult<?> assignRoles(@PathVariable Long id,
                                       @RequestBody List<Long> roleIds,
                                       @RequestHeader(value = "X-Username", required = false) String currentUsername) {
        SysUser user = sysUserService.getById(id);
        if (user != null && ADMIN_USERNAME.equals(user.getUsername())) {
            if (!ADMIN_USERNAME.equals(currentUsername)) {
                return CommonResult.error(403, "无权限修改 admin 账号角色");
            }
        }
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(id);
            ur.setRoleId(roleId);
            ur.setCreateTime(LocalDateTime.now());
            sysUserRoleService.save(ur);
        }
        return CommonResult.success("角色分配成功");
    }
}
