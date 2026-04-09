<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <h3>合家云</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="/system" v-if="isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/menu">菜单管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/community">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>社区管理</span>
          </template>
          <el-menu-item index="/community/community">小区管理</el-menu-item>
          <el-menu-item index="/community/building">楼栋管理</el-menu-item>
          <el-menu-item index="/community/unit">单元管理</el-menu-item>
          <el-menu-item index="/community/room">房屋管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/owner">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>业主管理</span>
          </template>
          <el-menu-item index="/owner/list">业主档案</el-menu-item>
          <el-menu-item index="/owner/binding">人房绑定</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="role-tag" :type="isAdmin ? 'danger' : 'info'">{{ isAdmin ? '管理员' : '普通用户' }}</span>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ realName }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, Setting, OfficeBuilding, UserFilled, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const realName = ref(localStorage.getItem('realName') || '管理员')
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '')

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.clear()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4d;
}

.logo h3 {
  color: #fff;
  font-size: 18px;
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,.1);
}

.header-left {
  font-size: 16px;
  font-weight: 500;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.role-tag {
  margin-right: 10px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
