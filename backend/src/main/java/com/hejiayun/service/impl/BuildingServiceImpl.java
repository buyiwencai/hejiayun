package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.BuildingMapper;
import com.hejiayun.mapper.CommunityMapper;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public List<Building> list() {
        List<Building> buildings = baseMapper.selectList(null);
        for (Building b : buildings) {
            if (b.getCommunityId() != null) {
                Community c = communityMapper.selectById(b.getCommunityId());
                if (c != null) b.setCommunityName(c.getName());
            }
        }
        return buildings;
    }
}
