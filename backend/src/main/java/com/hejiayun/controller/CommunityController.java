package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.Building;
import com.hejiayun.model.entity.Community;
import com.hejiayun.service.BuildingService;
import com.hejiayun.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    public CommonResult<Page<Community>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Page<Community> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Community::getName, name);
        }
        Page<Community> result = communityService.page(page, wrapper);
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<Community>> getAll() {
        List<Community> communities = communityService.list();
        return CommonResult.success(communities);
    }

    @GetMapping("/{id}")
    public CommonResult<Community> getById(@PathVariable Long id) {
        Community community = communityService.getById(id);
        return CommonResult.success(community);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody Community community) {
        community.setCreateTime(LocalDateTime.now());
        communityService.save(community);
        return CommonResult.success("小区添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody Community community) {
        community.setUpdateTime(LocalDateTime.now());
        communityService.updateById(community);
        return CommonResult.success("小区更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id) {
        // 检查是否有下楼栋
        long buildingCount = buildingService.count(
                new LambdaQueryWrapper<Building>().eq(Building::getCommunityId, id));
        if (buildingCount > 0) {
            return CommonResult.error("该小区下存在楼栋，无法删除");
        }
        communityService.removeById(id);
        return CommonResult.success("小区删除成功");
    }

    @GetMapping("/statistics")
    public CommonResult<List<Map<String, Object>>> getStatistics() {
        List<Community> communities = communityService.list();
        List<Map<String, Object>> statsList = communities.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("communityId", c.getId());
            map.put("communityName", c.getName());
            map.put("address", c.getAddress());
            map.put("area", c.getArea());

            // 统计楼栋数
            long buildingCount = buildingService.count(
                    new LambdaQueryWrapper<Building>().eq(Building::getCommunityId, c.getId()));
            map.put("buildingCount", buildingCount);

            // 统计房间数
            List<Building> buildings = buildingService.list(
                    new LambdaQueryWrapper<Building>().eq(Building::getCommunityId, c.getId()));
            long roomCount = 0;
            for (Building b : buildings) {
                roomCount += buildingService.count(
                        new LambdaQueryWrapper<Building>().eq(Building::getId, b.getId()));
            }
            map.put("roomCount", roomCount);

            return map;
        }).toList();
        return CommonResult.success(statsList);
    }
}
