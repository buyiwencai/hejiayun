package com.hejiayun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hejiayun.mapper.SysUserMapper;
import com.hejiayun.model.entity.SysUser;
import com.hejiayun.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
