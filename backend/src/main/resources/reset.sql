-- ============================================
-- 合家云社区物业管理平台 - 重置脚本
-- ============================================

USE hejiayun;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空所有数据
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE hjy_owner_room;
TRUNCATE TABLE hjy_owner;
TRUNCATE TABLE hjy_room;
TRUNCATE TABLE hjy_unit;
TRUNCATE TABLE hjy_building;
TRUNCATE TABLE hjy_community;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 重新插入数据
-- ============================================

-- 插入角色
INSERT INTO sys_role (id, role_name, role_key, description, status) VALUES
(1, '管理员', 'admin', '拥有系统所有权限', 1),
(2, '普通用户', 'user', '仅可查看数据', 1);

-- 给 admin 用户分配管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 给 operator 用户分配普通用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);

-- 插入测试小区
INSERT INTO hjy_community (id, name, address, area, status) VALUES
(1, '阳光花园小区', '北京市朝阳区望京街道1号', 50000.00, 1),
(2, '和谐苑小区', '北京市海淀区中关村街道10号', 35000.00, 1);

-- 插入测试楼栋
INSERT INTO hjy_building (id, community_id, name, floors, remark) VALUES
(1, 1, '1号楼', 33, '主楼'),
(2, 1, '2号楼', 28, '副楼'),
(3, 2, 'A座', 18, NULL);

-- 插入测试单元
INSERT INTO hjy_unit (id, building_id, name, floor_num) VALUES
(1, 1, '1单元', 33),
(2, 1, '2单元', 33),
(3, 2, '1单元', 28),
(4, 3, '1单元', 18);

-- 插入测试房屋
INSERT INTO hjy_room (id, unit_id, room_no, floor, area, room_type, status) VALUES
(1, 1, '101', 1, 89.50, '两室一厅', 'occupied'),
(2, 1, '102', 1, 120.00, '三室两厅', 'vacant'),
(3, 1, '201', 2, 89.50, '两室一厅', 'occupied'),
(4, 2, '101', 1, 75.00, '一室一厅', 'occupied'),
(5, 3, '301', 3, 95.00, '两室一厅', 'vacant'),
(6, 4, '501', 5, 110.00, '三室一厅', 'decorated');

-- 插入测试业主
INSERT INTO hjy_owner (id, name, phone, id_card, gender, car_plate, status) VALUES
(1, '张三', '13900139000', '110101199001011234', 1, '京A12345', 1),
(2, '李四', '13900139001', '110101199002022345', 0, '京B67890', 1),
(3, '王五', '13900139002', '110101199003033456', 1, NULL, 1);

-- 人房绑定
INSERT INTO hjy_owner_room (id, owner_id, room_id, relation_type, status) VALUES
(1, 1, 1, 'owner', 1),
(2, 1, 4, 'family', 1),
(3, 2, 3, 'owner', 1),
(4, 3, 6, 'tenant', 1);
