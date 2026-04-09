package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.Room;
import com.hejiayun.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/list")
    public CommonResult<Page<Room>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String roomNo,
            @RequestParam(required = false) String status) {
        Page<Room> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (unitId != null) {
            wrapper.eq(Room::getUnitId, unitId);
        }
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like(Room::getRoomNo, roomNo);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Room::getStatus, status);
        }
        Page<Room> result = roomService.page(page, wrapper);
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<Room>> getAll(@RequestParam(required = false) Long unitId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (unitId != null) {
            wrapper.eq(Room::getUnitId, unitId);
        }
        List<Room> rooms = roomService.list(wrapper);
        return CommonResult.success(rooms);
    }

    @GetMapping("/{id}")
    public CommonResult<Room> getById(@PathVariable Long id) {
        Room room = roomService.getById(id);
        return CommonResult.success(room);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody Room room) {
        if (room.getUnitId() == null) {
            return CommonResult.error("请选择所属单元");
        }
        room.setCreateTime(LocalDateTime.now());
        roomService.save(room);
        return CommonResult.success("房屋添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody Room room) {
        room.setUpdateTime(LocalDateTime.now());
        roomService.updateById(room);
        return CommonResult.success("房屋更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id) {
        roomService.removeById(id);
        return CommonResult.success("房屋删除成功");
    }

    @GetMapping("/statistics/status")
    public CommonResult<Map<String, Object>> getStatusStatistics() {
        long vacantCount = roomService.count(
                new LambdaQueryWrapper<Room>().eq(Room::getStatus, "vacant"));
        long occupiedCount = roomService.count(
                new LambdaQueryWrapper<Room>().eq(Room::getStatus, "occupied"));
        long decoratedCount = roomService.count(
                new LambdaQueryWrapper<Room>().eq(Room::getStatus, "decorated"));
        long totalCount = roomService.count();

        Map<String, Object> result = new HashMap<>();
        result.put("vacant", vacantCount);
        result.put("occupied", occupiedCount);
        result.put("decorated", decoratedCount);
        result.put("total", totalCount);
        result.put("vacantPercent", totalCount > 0 ? (vacantCount * 100.0 / totalCount) : 0);
        result.put("occupiedPercent", totalCount > 0 ? (occupiedCount * 100.0 / totalCount) : 0);

        return CommonResult.success(result);
    }
}
