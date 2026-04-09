package com.hejiayun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hejiayun.model.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getMenuTree();
}
