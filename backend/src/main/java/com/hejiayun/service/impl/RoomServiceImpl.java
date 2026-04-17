package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.BuildingMapper;
import com.hejiayun.mapper.CommunityMapper;
import com.hejiayun.mapper.RoomMapper;
import com.hejiayun.mapper.UnitMapper;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.model.entity.Room;
import com.hejiayun.model.entity.Unit;
import com.hejiayun.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private UnitMapper unitMapper;
    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public List<Room> list() {
        List<Room> rooms = baseMapper.selectList(null);
        for (Room r : rooms) {
            if (r.getUnitId() != null) {
                Unit u = unitMapper.selectById(r.getUnitId());
                if (u != null) {
                    r.setUnitName(u.getName());
                    Building b = buildingMapper.selectById(u.getBuildingId());
                    if (b != null) {
                        r.setBuildingName(b.getName());
                        if (b.getCommunityId() != null) {
                            Community c = communityMapper.selectById(b.getCommunityId());
                            if (c != null) r.setCommunityName(c.getName());
                        }
                    }
                }
            }
        }
        return rooms;
    }
}
