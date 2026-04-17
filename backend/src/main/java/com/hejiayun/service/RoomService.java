package com.hejiayun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hejiayun.model.entity.Room;

import java.util.List;

public interface RoomService extends IService<Room> {
    List<Room> list();
}
