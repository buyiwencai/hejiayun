package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.SysRole;
import com.hejiayun.model.entity.SysRoleMenu;
import com.hejiayun.service.SysRoleMenuService;
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

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

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
        // 删除角色菜单关联
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, id));
        return CommonResult.success("角色删除成功");
    }

    @GetMapping("/{id}/menus")
    public CommonResult<List<Long>> getRoleMenus(@PathVariable Long id) {
        List<Long> menuIds = sysRoleMenuService.list(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, id)
        ).stream().map(SysRoleMenu::getMenuId).toList();
        return CommonResult.success(menuIds);
    }

    @PutMapping("/{id}/menus")
    @Transactional
    public CommonResult<?> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds,
                                        @RequestHeader(value = "X-User-Role", required = false) String roleKey) {
        if (!"admin".equals(roleKey)) {
            return CommonResult.error(403, "无权限操作");
        }
        // 删除原有菜单权限
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, id));
        // 分配新菜单权限
        for (Long menuId : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(id);
            rm.setMenuId(menuId);
            rm.setCreateTime(LocalDateTime.now());
            sysRoleMenuService.save(rm);
        }
        return CommonResult.success("菜单权限分配成功");
    }
}
