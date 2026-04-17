package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.mapper.BuildingMapper;
import com.hejiayun.mapper.CommunityMapper;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.model.entity.Room;
import com.hejiayun.model.entity.Unit;
import com.hejiayun.service.RoomService;
import com.hejiayun.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/community/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @GetMapping("/list")
    public CommonResult<Page<Unit>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String name) {
        Page<Unit> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(Unit::getBuildingId, buildingId);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(Unit::getName, name);
        }
        Page<Unit> result = unitService.page(page, wrapper);
        for (Unit u : result.getRecords()) {
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
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<Unit>> getAll(@RequestParam(required = false) Long buildingId) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(Unit::getBuildingId, buildingId);
        }
        List<Unit> units = unitService.list(wrapper);
        for (Unit u : units) {
            if (u.getBuildingId() != null) {
                Building b = buildingMapper.selectById(u.getBuildingId());
                if (b != null) u.setBuildingName(b.getName());
            }
        }
        return CommonResult.success(units);
    }

    @GetMapping("/{id}")
    public CommonResult<Unit> getById(@PathVariable Long id) {
        Unit unit = unitService.getById(id);
        if (unit != null && unit.getBuildingId() != null) {
            Building b = buildingMapper.selectById(unit.getBuildingId());
            if (b != null) {
                unit.setBuildingName(b.getName());
                if (b.getCommunityId() != null) {
                    Community c = communityMapper.selectById(b.getCommunityId());
                    if (c != null) unit.setCommunityName(c.getName());
                }
            }
        }
        return CommonResult.success(unit);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody Unit unit) {
        if (unit.getBuildingId() == null) {
            return CommonResult.error("请选择所属楼栋");
        }
        unit.setCreateTime(LocalDateTime.now());
        unitService.save(unit);
        return CommonResult.success("单元添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody Unit unit) {
        unit.setUpdateTime(LocalDateTime.now());
        unitService.updateById(unit);
        return CommonResult.success("单元更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id) {
        long roomCount = roomService.count(
                new LambdaQueryWrapper<Room>().eq(Room::getUnitId, id));
        if (roomCount > 0) {
            return CommonResult.error("该单元下存在房屋，无法删除");
        }
        unitService.removeById(id);
        return CommonResult.success("单元删除成功");
    }
}
