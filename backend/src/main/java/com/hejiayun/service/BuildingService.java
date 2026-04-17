package com.hejiayun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hejiayun.model.entity.Building;

import java.util.List;

public interface BuildingService extends IService<Building> {
    List<Building> list();
}
