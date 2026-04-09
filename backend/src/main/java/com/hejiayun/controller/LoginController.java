package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.SysRole;
import com.hejiayun.model.entity.SysUser;
import com.hejiayun.model.entity.SysUserRole;
import com.hejiayun.service.SysRoleService;
import com.hejiayun.service.SysUserRoleService;
import com.hejiayun.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    @PostMapping("/login")
    public CommonResult<Map<String, Object>> login(@RequestBody SysUser user) {
        SysUser dbUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, user.getUsername())
                        .eq(SysUser::getPassword, user.getPassword())
                        .eq(SysUser::getStatus, 1)
        );

        if (dbUser == null) {
            return CommonResult.error("用户名或密码错误");
        }

        // 获取用户角色
        String roleKey = "";
        SysUserRole userRole = sysUserRoleService.getOne(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, dbUser.getId())
        );
        if (userRole != null) {
            SysRole role = sysRoleService.getById(userRole.getRoleId());
            if (role != null) {
                roleKey = role.getRoleKey();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", dbUser.getId());
        data.put("username", dbUser.getUsername());
        data.put("realName", dbUser.getRealName());
        data.put("roleKey", roleKey);

        return CommonResult.success(data);
    }

    @GetMapping("/user/info/{id}")
    public CommonResult<SysUser> getUserInfo(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return CommonResult.success(user);
    }
}
