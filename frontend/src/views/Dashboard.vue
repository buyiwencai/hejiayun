<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>小区总数</template>
          <div class="stat-value">{{ stats.communityCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>楼栋总数</template>
          <div class="stat-value">{{ stats.buildingCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>房屋总数</template>
          <div class="stat-value">{{ roomStats.total || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>业主总数</template>
          <div class="stat-value">{{ ownerCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>小区资产统计</template>
          <el-table :data="communityStats" stripe>
            <el-table-column prop="communityName" label="小区名称" />
            <el-table-column prop="buildingCount" label="楼栋数" width="100" align="center" />
            <el-table-column prop="roomCount" label="房间数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>房屋状态分布</template>
          <div class="room-status">
            <div class="status-item">
              <span class="label">空置房</span>
              <span class="value vacant">{{ roomStats.vacant || 0 }}</span>
              <span class="percent">({{ (roomStats.vacantPercent || 0).toFixed(1) }}%)</span>
            </div>
            <div class="status-item">
              <span class="label">已入住</span>
              <span class="value occupied">{{ roomStats.occupied || 0 }}</span>
              <span class="percent">({{ (roomStats.occupiedPercent || 0).toFixed(1) }}%)</span>
            </div>
            <div class="status-item">
              <span class="label">装修中</span>
              <span class="value decorated">{{ roomStats.decorated || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCommunityStatistics, getRoomStatistics, getAllCommunities, getAllOwners } from '../api'

const communityStats = ref([])
const roomStats = ref({})
const ownerCount = ref(0)
const stats = ref({})

onMounted(async () => {
  try {
    // 获取小区统计
    const commRes = await getCommunityStatistics()
    communityStats.value = commRes.data || []
    stats.value.communityCount = communityStats.value.length
    stats.value.buildingCount = communityStats.value.reduce((sum, c) => sum + (c.buildingCount || 0), 0)

    // 获取房屋统计
    const roomRes = await getRoomStatistics()
    roomStats.value = roomRes.data || {}

    // 获取业主数量
    const ownerRes = await getAllOwners()
    ownerCount.value = ownerRes.data?.length || 0
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 20px 0;
}

.room-status {
  padding: 20px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 16px;
}

.status-item .label {
  width: 80px;
}

.status-item .value {
  font-weight: bold;
  font-size: 24px;
}

.status-item .value.vacant { color: #67C23A; }
.status-item .value.occupied { color: #409EFF; }
.status-item .value.decorated { color: #E6A23C; }

.status-item .percent {
  color: #999;
  font-size: 14px;
}
</style>
