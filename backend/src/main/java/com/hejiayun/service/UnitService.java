package com.hejiayun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hejiayun.model.entity.Unit;

import java.util.List;

public interface UnitService extends IService<Unit> {
    List<Unit> list();
}
