package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.BuildingMapper;
import com.hejiayun.mapper.CommunityMapper;
import com.hejiayun.mapper.UnitMapper;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.model.entity.Unit;
import com.hejiayun.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {

    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public List<Unit> list() {
        List<Unit> units = baseMapper.selectList(null);
        for (Unit u : units) {
            if (u.getBuildingId() != null) {
                Building b = buildingMapper.selectById(u.getBuildingId());
                if (b != null) {
                    u.setBuildingName(b.getName());
                    if (b.getCommunityId() != null) {
                        Community c = communityMapper.selectById(b.getCommunityId());
                        if (c != null) u.setCommunityName(c.getName());
                    }
                }
            }
        }
        return units;
    }
}
