# 合家云社区物业管理平台

基于 Spring Boot + Vue.js + MySQL 的社区物业管理 B/S 系统。

## 功能模块

### 系统管理
- **用户管理** - 用户增删改查，角色分配
- **角色管理** - 角色增删改查，菜单权限分配
- **菜单管理** - 菜单增删改查

### 社区管理
- **小区管理** - 小区增删改查
- **楼栋管理** - 楼栋增删改查，隶属小区
- **单元管理** - 单元增删改查，隶属楼栋
- **房屋管理** - 房屋增删改查，隶属单元，状态管理（空置/已入住/装修中）

### 业主管理
- **业主档案** - 业主增删改查，支持多条件搜索
- **人房绑定** - 业主与房屋的绑定关系管理（业主/家属/租客）

### 数据统计
- **资产统计** - 小区资产汇总、房屋状态分布

## 权限说明

| 角色 | 说明 |
|------|------|
| 管理员 | 拥有系统所有权限，可进行所有增删改操作 |
| 普通用户 | 仅可查看数据，无操作权限 |

默认账号：
- 管理员：`admin` / `123456`
- 普通用户：`operator` / `123456`

## 技术栈

**后端**
- JDK 21
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0

**前端**
- Vue 3
- Vite
- Element Plus

## 项目结构

```
backend/                          # Spring Boot 后端
├── src/main/java/com/hejiayun/
│   ├── controller/              # REST 控制器
│   ├── service/                 # 业务逻辑
│   ├── mapper/                  # 数据访问层
│   └── model/                   # 实体类
├── src/main/resources/
│   ├── schema.sql               # 建库脚本
│   └── application.yml         # 配置文件
└── pom.xml

frontend/                         # Vue 前端
├── src/
│   ├── api/                     # API 接口封装
│   ├── router/                  # 路由配置
│   ├── utils/                   # 工具函数
│   └── views/                  # 页面组件
└── package.json
```

## 运行说明

### 1. 初始化数据库

```sql
CREATE DATABASE hejiayun DEFAULT CHARACTER SET utf8mb4;
-- 执行 backend/src/main/resources/schema.sql
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
# 访问 http://localhost:8080/api
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

## 数据库表设计（11张）

**资产维度（4张）**
- `hjy_community` - 小区表
- `hjy_building` - 楼栋表
- `hjy_unit` - 单元表
- `hjy_room` - 房屋表

**权限维度（5张）**
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `sys_user_role` - 用户角色关联表
- `sys_role_menu` - 角色菜单关联表

**业主维度（2张）**
- `hjy_owner` - 业主档案表
- `hjy_owner_room` - 人房关联表

---

课程设计作品 · 北京邮电大学
