package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.SysMenuMapper;
import com.hejiayun.model.entity.SysMenu;
import com.hejiayun.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = this.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSortNum));
        return buildTree(allMenus);
    }

    private List<SysMenu> buildTree(List<SysMenu> menus) {
        List<SysMenu> rootMenus = menus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .collect(Collectors.toList());

        for (SysMenu root : rootMenus) {
            buildChildren(root, menus);
        }
        return rootMenus;
    }

    private void buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(m -> parent.getId().equals(m.getParentId()))
                .collect(Collectors.toList());

        for (SysMenu child : children) {
            buildChildren(child, allMenus);
        }
        parent.setChildren(children);
    }
}
