package com.hejiayun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hejiayun.model.common.CommonResult;
import com.hejiayun.model.entity.Owner;
import com.hejiayun.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/list")
    public CommonResult<Page<Owner>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String idCard) {
        Page<Owner> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Owner> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Owner::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(Owner::getPhone, phone);
        }
        if (idCard != null && !idCard.isEmpty()) {
            wrapper.like(Owner::getIdCard, idCard);
        }
        Page<Owner> result = ownerService.page(page, wrapper);
        return CommonResult.success(result);
    }

    @GetMapping("/all")
    public CommonResult<List<Owner>> getAll() {
        List<Owner> owners = ownerService.list();
        return CommonResult.success(owners);
    }

    @GetMapping("/{id}")
    public CommonResult<Owner> getById(@PathVariable Long id) {
        Owner owner = ownerService.getById(id);
        return CommonResult.success(owner);
    }

    @PostMapping
    public CommonResult<?> add(@RequestBody Owner owner) {
        if (owner.getName() == null || owner.getPhone() == null) {
            return CommonResult.error("姓名和手机号不能为空");
        }
        owner.setCreateTime(LocalDateTime.now());
        ownerService.save(owner);
        return CommonResult.success("业主添加成功");
    }

    @PutMapping
    public CommonResult<?> update(@RequestBody Owner owner) {
        owner.setUpdateTime(LocalDateTime.now());
        ownerService.updateById(owner);
        return CommonResult.success("业主更新成功");
    }

    @DeleteMapping("/{id}")
    public CommonResult<?> delete(@PathVariable Long id) {
        ownerService.removeById(id);
        return CommonResult.success("业主删除成功");
    }

    @GetMapping("/search")
    public CommonResult<List<Owner>> search(@RequestParam String keyword) {
        LambdaQueryWrapper<Owner> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Owner::getName, keyword)
                .or()
                .like(Owner::getPhone, keyword)
                .or()
                .like(Owner::getIdCard, keyword);
        List<Owner> owners = ownerService.list(wrapper);
        return CommonResult.success(owners);
    }
}
