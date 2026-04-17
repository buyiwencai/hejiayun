package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.mapper.CommunityMapper;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.model.entity.Unit;
import com.hejiayun.service.BuildingService;
import com.hejiayun.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/community/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private CommunityMapper communityMapper;

    @GetMapping("/list")
    public CommonResult<Page<Building>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) String name) {
        Page<Building> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (communityId != null) {
            wrapper.eq(Building::getCommunityId, communityId);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(Building::getName, name);
        }
        Page<Building> result = buildingService.page(page, wrapper);
        for (Building b : result.getRecords()) {
            if (b.getCommunityId() != null) {
                Community c = communityMapper.selectById(b.getCommunityId());
                if (c != null) b.setCommunityName(c.getName());
            }
        }
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<Building>> getAll(@RequestParam(required = false) Long communityId) {
        List<Building> buildings = buildingService.list();
        return CommonResult.success(buildings);
    }

    @GetMapping("/{id}")
    public CommonResult<Building> getById(@PathVariable Long id) {
        Building building = buildingService.getById(id);
        if (building != null && building.getCommunityId() != null) {
            Community c = communityMapper.selectById(building.getCommunityId());
            if (c != null) building.setCommunityName(c.getName());
        }
        return CommonResult.success(building);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody Building building) {
        if (building.getCommunityId() == null) {
            return CommonResult.error("请选择所属小区");
        }
        building.setCreateTime(LocalDateTime.now());
        buildingService.save(building);
        return CommonResult.success("楼栋添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody Building building) {
        building.setUpdateTime(LocalDateTime.now());
        buildingService.updateById(building);
        return CommonResult.success("楼栋更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id) {
        long unitCount = unitService.count(
                new LambdaQueryWrapper<Unit>().eq(Unit::getBuildingId, id));
        if (unitCount > 0) {
            return CommonResult.error("该楼栋下存在单元，无法删除");
        }
        buildingService.removeById(id);
        return CommonResult.success("楼栋删除成功");
    }
}
