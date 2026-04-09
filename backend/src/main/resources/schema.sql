-- ============================================
-- 合家云社区物业管理平台 - 数据库建表脚本
-- 数据库: hejiayun
-- ============================================

CREATE DATABASE IF NOT EXISTS hejiayun DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hejiayun;

-- ----------------------------
-- 1. 资产维度 - 小区表
-- ----------------------------
DROP TABLE IF EXISTS hjy_community;
CREATE TABLE hjy_community (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '小区名称',
    address VARCHAR(255) COMMENT '小区地址',
    area DECIMAL(10,2) COMMENT '占地面积(平方米)',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-正常, 0-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小区表';

-- ----------------------------
-- 2. 资产维度 - 楼栋表
-- ----------------------------
DROP TABLE IF EXISTS hjy_building;
CREATE TABLE hjy_building (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    community_id BIGINT(20) NOT NULL COMMENT '所属小区ID',
    name VARCHAR(50) NOT NULL COMMENT '楼栋名称/编号',
    floors INT COMMENT '楼层数',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_building_community FOREIGN KEY (community_id) REFERENCES hjy_community(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- ----------------------------
-- 3. 资产维度 - 单元表
-- ----------------------------
DROP TABLE IF EXISTS hjy_unit;
CREATE TABLE hjy_unit (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    building_id BIGINT(20) NOT NULL COMMENT '所属楼栋ID',
    name VARCHAR(50) NOT NULL COMMENT '单元名称/编号',
    floor_num INT COMMENT '单元楼层数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_unit_building FOREIGN KEY (building_id) REFERENCES hjy_building(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单元表';

-- ----------------------------
-- 4. 资产维度 - 房屋表
-- ----------------------------
DROP TABLE IF EXISTS hjy_room;
CREATE TABLE hjy_room (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    unit_id BIGINT(20) NOT NULL COMMENT '所属单元ID',
    room_no VARCHAR(50) NOT NULL COMMENT '房间号',
    floor INT COMMENT '所在楼层',
    area DECIMAL(10,2) COMMENT '建筑面积(平方米)',
    room_type VARCHAR(20) COMMENT '户型: 一室一厅, 两室一厅, 三室两厅等',
    status VARCHAR(20) DEFAULT 'vacant' COMMENT '状态: vacant-空置, occupied-已入住, decorated-装修中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_room_unit FOREIGN KEY (unit_id) REFERENCES hjy_unit(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋表';

-- ----------------------------
-- 5. 权限维度 - 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 6. 权限维度 - 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色标识',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 7. 权限维度 - 菜单表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT(20) DEFAULT 0 COMMENT '父菜单ID, 0为根',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50) COMMENT '菜单图标',
    sort_num INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 8. 权限维度 - 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    role_id BIGINT(20) NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 9. 权限维度 - 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT(20) NOT NULL COMMENT '角色ID',
    menu_id BIGINT(20) NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    CONSTRAINT fk_rm_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    CONSTRAINT fk_rm_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 10. 业主维度 - 业主档案表
-- ----------------------------
DROP TABLE IF EXISTS hjy_owner;
CREATE TABLE hjy_owner (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '业主姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    id_card VARCHAR(20) COMMENT '身份证号',
    gender TINYINT COMMENT '性别: 0-女, 1-男',
    birthday DATE COMMENT '出生日期',
    car_plate VARCHAR(20) COMMENT '车牌号',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-正常, 0-已迁出',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主档案表';

-- ----------------------------
-- 11. 业主维度 - 人房关联表
-- ----------------------------
DROP TABLE IF EXISTS hjy_owner_room;
CREATE TABLE hjy_owner_room (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    owner_id BIGINT(20) NOT NULL COMMENT '业主ID',
    room_id BIGINT(20) NOT NULL COMMENT '房屋ID',
    relation_type VARCHAR(20) DEFAULT 'owner' COMMENT '关系类型: owner-业主, family-家属, tenant-租客',
    bind_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    unbind_time DATETIME COMMENT '解绑时间',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-有效, 0-已解绑',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_owner_room (owner_id, room_id, relation_type),
    CONSTRAINT fk_or_owner FOREIGN KEY (owner_id) REFERENCES hjy_owner(id) ON DELETE CASCADE,
    CONSTRAINT fk_or_room FOREIGN KEY (room_id) REFERENCES hjy_room(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人房关联表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入超级管理员用户 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES
('admin', '123456', '系统管理员', '13800138000', 1),
('operator', '123456', '操作员', '13800138001', 1);

-- 插入角色 (简化: 管理员 和 普通用户)
INSERT INTO sys_role (role_name, role_key, description, status) VALUES
('管理员', 'admin', '拥有系统所有权限', 1),
('普通用户', 'user', '仅可查看数据', 1);

-- 插入菜单
INSERT INTO sys_menu (parent_id, name, path, component, perms, icon, sort_num) VALUES
(0, '系统管理', '/system', NULL, NULL, 'el-icon-setting', 1),
(1, '用户管理', '/system/user', 'system/User.vue', 'sys:user:list', 'el-icon-user', 1),
(1, '角色管理', '/system/role', 'system/Role.vue', 'sys:role:list', 'el-icon-postcard', 2),
(1, '菜单管理', '/system/menu', 'system/Menu.vue', 'sys:menu:list', 'el-icon-menu', 3),
(0, '社区管理', '/community', NULL, NULL, 'el-icon-office-building', 2),
(5, '小区管理', '/community/community', 'community/Community.vue', 'community:community:list', 'el-icon-location', 1),
(5, '楼栋管理', '/community/building', 'community/Building.vue', 'community:building:list', 'el-icon-house', 2),
(5, '单元管理', '/community/unit', 'community/Unit.vue', 'community:unit:list', 'el-icon-document', 3),
(5, '房屋管理', '/community/room', 'community/Room.vue', 'community:room:list', 'el-icon-house', 4),
(0, '业主管理', '/owner', NULL, NULL, 'el-icon-user-filled', 3),
(10, '业主档案', '/owner/list', 'owner/Owner.vue', 'owner:list', 'el-icon-notebook-2', 1),
(10, '人房绑定', '/owner/binding', 'owner/Binding.vue', 'owner:binding', 'el-icon-connection', 2),
(0, '数据统计', '/statistics', NULL, NULL, 'el-icon-data-analysis', 4),
(12, '资产统计', '/statistics/assets', 'statistics/Assets.vue', 'statistics:assets', 'el-icon-pie-chart', 1);

-- 给管理员(admin)分配所有菜单
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 给普通用户(operator)分配查看权限(只能看菜单，不能操作)
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
-- 普通用户只看二级菜单，不给写操作权限（前端控制）

-- 插入测试小区
INSERT INTO hjy_community (name, address, area, status) VALUES
('阳光花园小区', '北京市朝阳区望京街道1号', 50000.00, 1),
('和谐苑小区', '北京市海淀区中关村街道10号', 35000.00, 1);

-- 插入测试楼栋
INSERT INTO hjy_building (community_id, name, floors, remark) VALUES
(1, '1号楼', 33, '主楼'),
(1, '2号楼', 28, '副楼'),
(2, 'A座', 18, NULL);

-- 插入测试单元
INSERT INTO hjy_unit (building_id, name, floor_num) VALUES
(1, '1单元', 33),
(1, '2单元', 33),
(2, '1单元', 28),
(3, '1单元', 18);

-- 插入测试房屋
INSERT INTO hjy_room (unit_id, room_no, floor, area, room_type, status) VALUES
(1, '101', 1, 89.50, '两室一厅', 'occupied'),
(1, '102', 1, 120.00, '三室两厅', 'vacant'),
(1, '201', 2, 89.50, '两室一厅', 'occupied'),
(2, '101', 1, 75.00, '一室一厅', 'occupied'),
(3, '301', 3, 95.00, '两室一厅', 'vacant'),
(4, '501', 5, 110.00, '三室一厅', 'decorated');

-- 插入测试业主
INSERT INTO hjy_owner (name, phone, id_card, gender, car_plate, status) VALUES
('张三', '13900139000', '110101199001011234', 1, '京A12345', 1),
('李四', '13900139001', '110101199002022345', 0, '京B67890', 1),
('王五', '13900139002', '110101199003033456', 1, NULL, 1);

-- 人房绑定
INSERT INTO hjy_owner_room (owner_id, room_id, relation_type, status) VALUES
(1, 1, 'owner', 1),
(1, 4, 'family', 1),
(2, 3, 'owner', 1),
(3, 6, 'tenant', 1);
