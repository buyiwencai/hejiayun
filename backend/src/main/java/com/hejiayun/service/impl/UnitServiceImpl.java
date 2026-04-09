package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.UnitMapper;
import com.hejiayun.model.entity.Unit;
import com.hejiayun.service.UnitService;
import org.springframework.stereotype.Service;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {
}
