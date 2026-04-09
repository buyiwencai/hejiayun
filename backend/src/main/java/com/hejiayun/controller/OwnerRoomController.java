package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.*;
import com.hejiayun.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/room")
public class OwnerRoomController {

    @Autowired
    private OwnerRoomService ownerRoomService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private CommunityService communityService;

    @GetMapping("/list")
    public CommonResult<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String relationType) {
        Page<OwnerRoom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OwnerRoom> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(OwnerRoom::getOwnerId, ownerId);
        }
        if (roomId != null) {
            wrapper.eq(OwnerRoom::getRoomId, roomId);
        }
        if (relationType != null && !relationType.isEmpty()) {
            wrapper.eq(OwnerRoom::getRelationType, relationType);
        }
        wrapper.eq(OwnerRoom::getStatus, 1); // 只查询有效的绑定

        Page<OwnerRoom> result = ownerRoomService.page(page, wrapper);

        // 组装详细信息
        List<Map<String, Object>> dataList = result.getRecords().stream().map(binding -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", binding.getId());
            map.put("ownerId", binding.getOwnerId());
            map.put("roomId", binding.getRoomId());
            map.put("relationType", binding.getRelationType());
            map.put("bindTime", binding.getBindTime());
            map.put("status", binding.getStatus());

            // 业主信息
            Owner owner = ownerService.getById(binding.getOwnerId());
            if (owner != null) {
                map.put("ownerName", owner.getName());
                map.put("ownerPhone", owner.getPhone());
            }

            // 房屋信息
            Room room = roomService.getById(binding.getRoomId());
            if (room != null) {
                map.put("roomNo", room.getRoomNo());
                map.put("roomStatus", room.getStatus());

                // 单元信息
                Unit unit = unitService.getById(room.getUnitId());
                if (unit != null) {
                    map.put("unitName", unit.getName());
                    map.put("floor", room.getFloor());

                    // 楼栋信息
                    Building building = buildingService.getById(unit.getBuildingId());
                    if (building != null) {
                        map.put("buildingName", building.getName());

                        // 小区信息
                        Community community = communityService.getById(building.getCommunityId());
                        if (community != null) {
                            map.put("communityName", community.getName());
                            map.put("communityAddress", community.getAddress());
                        }
                    }
                }
            }

            return map;
        }).toList();

        Page<Map<String, Object>> finalPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        finalPage.setRecords(dataList);

        return CommonResult.success(finalPage);
    }

    @GetMapping("/owner/{ownerId}")
    public CommonResult<List<Map<String, Object>>> getRoomsByOwner(@PathVariable Long ownerId) {
        List<OwnerRoom> bindings = ownerRoomService.list(
                new LambdaQueryWrapper<OwnerRoom>()
                        .eq(OwnerRoom::getOwnerId, ownerId)
                        .eq(OwnerRoom::getStatus, 1));

        List<Map<String, Object>> result = bindings.stream().map(binding -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bindingId", binding.getId());
            map.put("roomId", binding.getRoomId());
            map.put("relationType", binding.getRelationType());
            map.put("bindTime", binding.getBindTime());

            Room room = roomService.getById(binding.getRoomId());
            if (room != null) {
                map.put("roomNo", room.getRoomNo());
                map.put("roomStatus", room.getStatus());
                map.put("area", room.getArea());
                map.put("roomType", room.getRoomType());

                Unit unit = unitService.getById(room.getUnitId());
                if (unit != null) {
                    map.put("unitName", unit.getName());

                    Building building = buildingService.getById(unit.getBuildingId());
                    if (building != null) {
                        map.put("buildingName", building.getName());

                        Community community = communityService.getById(building.getCommunityId());
                        if (community != null) {
                            map.put("communityName", community.getName());
                        }
                    }
                }
            }
            return map;
        }).toList();

        return CommonResult.success(result);
    }

    @PostMapping
    @Transactional
    public CommonResult<?> add(@RequestBody OwnerRoom ownerRoom) {
        // 检查是否已存在有效绑定
        long existCount = ownerRoomService.count(
                new LambdaQueryWrapper<OwnerRoom>()
                        .eq(OwnerRoom::getOwnerId, ownerRoom.getOwnerId())
                        .eq(OwnerRoom::getRoomId, ownerRoom.getRoomId())
                        .eq(OwnerRoom::getStatus, 1));
        if (existCount > 0) {
            return CommonResult.error("该业主已绑定此房屋");
        }

        ownerRoom.setBindTime(LocalDateTime.now());
        ownerRoom.setStatus(1);
        ownerRoom.setCreateTime(LocalDateTime.now());
        ownerRoomService.save(ownerRoom);

        // 如果是业主关系，更新房屋状态为已入住
        if ("owner".equals(ownerRoom.getRelationType())) {
            Room room = roomService.getById(ownerRoom.getRoomId());
            if (room != null) {
                room.setStatus("occupied");
                roomService.updateById(room);
            }
        }

        return CommonResult.success("绑定成功");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public CommonResult<?> unbind(@PathVariable Long id) {
        OwnerRoom binding = ownerRoomService.getById(id);
        if (binding != null) {
            binding.setStatus(0);
            binding.setUnbindTime(LocalDateTime.now());
            ownerRoomService.updateById(binding);

            // 检查是否还有其他业主绑定此房屋
            long otherBindings = ownerRoomService.count(
                    new LambdaQueryWrapper<OwnerRoom>()
                            .eq(OwnerRoom::getRoomId, binding.getRoomId())
                            .eq(OwnerRoom::getStatus, 1)
                            .ne(OwnerRoom::getId, id));
            if (otherBindings == 0) {
                // 没有其他绑定，更新房屋状态为空置
                Room room = roomService.getById(binding.getRoomId());
                if (room != null) {
                    room.setStatus("vacant");
                    roomService.updateById(room);
                }
            }
        }
        return CommonResult.success("解绑成功");
    }
}
