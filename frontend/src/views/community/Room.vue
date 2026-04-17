<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="所属小区">
            <el-select v-model="queryForm.communityId" placeholder="请选择小区" clearable @change="handleCommunityChange" style="width: 160px">
              <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属楼栋">
            <el-select v-model="queryForm.buildingId" placeholder="请选择楼栋" clearable @change="handleBuildingChange" style="width: 160px">
              <el-option v-for="b in filteredBuildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属单元">
            <el-select v-model="queryForm.unitId" placeholder="请选择单元" clearable @change="handleUnitChange" style="width: 160px">
              <el-option v-for="u in filteredUnits" :key="u.id" :label="u.name" :value="u.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="房间号">
            <el-input v-model="queryForm.roomNo" placeholder="请输入房间号" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="空置" value="vacant" />
              <el-option label="已入住" value="occupied" />
              <el-option label="装修中" value="decorated" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <div style="margin-bottom: 10px;" v-if="isAdmin">
        <el-button type="primary" @click="handleAdd">新增房屋</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="communityName" label="小区" />
        <el-table-column prop="buildingName" label="楼栋" />
        <el-table-column prop="unitName" label="单元" />
        <el-table-column prop="roomNo" label="房间号" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="area" label="面积(m²)" width="100" />
        <el-table-column prop="roomType" label="户型" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'vacant'" type="success">空置</el-tag>
            <el-tag v-else-if="row.status === 'occupied'" type="primary">已入住</el-tag>
            <el-tag v-else type="warning">装修中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <template v-if="isAdmin">
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
            <template v-else>
              <el-tag type="info">仅查看</el-tag>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属小区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择小区" style="width: 100%" @change="handleFormCommunityChange">
            <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请先选择小区" style="width: 100%" :disabled="!form.communityId" @change="handleFormBuildingChange">
            <el-option v-for="b in formFilteredBuildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属单元" prop="unitId">
          <el-select v-model="form.unitId" placeholder="请先选择楼栋" style="width: 100%" :disabled="!form.buildingId">
            <el-option v-for="u in formFilteredUnits" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="0" />
        </el-form-item>
        <el-form-item label="建筑面积" prop="area">
          <el-input-number v-model="form.area" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="户型" prop="roomType">
          <el-select v-model="form.roomType" placeholder="请选择户型" style="width: 100%">
            <el-option label="一室一厅" value="一室一厅" />
            <el-option label="两室一厅" value="两室一厅" />
            <el-option label="三室一厅" value="三室一厅" />
            <el-option label="三室两厅" value="三室两厅" />
            <el-option label="四室两厅" value="四室两厅" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空置" value="vacant" />
            <el-option label="已入住" value="occupied" />
            <el-option label="装修中" value="decorated" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomList, addRoom, updateRoom, deleteRoom, getAllCommunities, getAllBuildings, getAllUnits } from '../../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const communities = ref([])
const buildings = ref([])
const units = ref([])
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const queryForm = reactive({ communityId: null, buildingId: null, unitId: null, roomNo: '', status: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('新增房屋')
const formRef = ref()
const form = reactive({ id: null, communityId: null, buildingId: null, unitId: null, roomNo: '', floor: 0, area: null, roomType: '', status: 'vacant' })
const rules = {
  communityId: [{ required: true, message: '请选择所属小区', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择所属楼栋', trigger: 'change' }],
  unitId: [{ required: true, message: '请选择所属单元', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

// 搜索条件联动
const filteredBuildings = computed(() => {
  if (!queryForm.communityId) return buildings.value
  return buildings.value.filter(b => b.communityId === queryForm.communityId)
})

const filteredUnits = computed(() => {
  if (!queryForm.buildingId) return units.value
  return units.value.filter(u => u.buildingId === queryForm.buildingId)
})

// 表单联动
const formFilteredBuildings = computed(() => {
  if (!form.communityId) return []
  return buildings.value.filter(b => b.communityId === form.communityId)
})

const formFilteredUnits = computed(() => {
  if (!form.buildingId) return []
  return units.value.filter(u => u.buildingId === form.buildingId)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomList({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const loadAll = async () => {
  const [commRes, buildRes, unitRes] = await Promise.all([getAllCommunities(), getAllBuildings(), getAllUnits()])
  communities.value = commRes.data || []
  buildings.value = buildRes.data || []
  units.value = unitRes.data || []
}

const handleCommunityChange = () => { queryForm.buildingId = null; queryForm.unitId = null; pageNum.value = 1; loadData() }
const handleBuildingChange = () => { queryForm.unitId = null; pageNum.value = 1; loadData() }
const handleUnitChange = () => { pageNum.value = 1; loadData() }
const handleQuery = () => { pageNum.value = 1; loadData() }
const handleReset = () => { queryForm.communityId = null; queryForm.buildingId = null; queryForm.unitId = null; queryForm.roomNo = ''; queryForm.status = ''; handleQuery() }

const handleFormCommunityChange = () => { form.buildingId = null; form.unitId = null }
const handleFormBuildingChange = () => { form.unitId = null }

const handleAdd = () => {
  form.id = null; form.communityId = null; form.buildingId = null; form.unitId = null
  form.roomNo = ''; form.floor = 0; form.area = null; form.roomType = ''; form.status = 'vacant'
  dialogTitle.value = '新增房屋'; dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  // 编辑时需要设置上级联动数据
  if (row.buildingId) {
    const building = buildings.value.find(b => b.id === row.buildingId)
    if (building) form.communityId = building.communityId
  }
  if (row.unitId) {
    const unit = units.value.find(u => u.id === row.unitId)
    if (unit) form.buildingId = unit.buildingId
  }
  dialogTitle.value = '编辑房屋'; dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) await updateRoom(form)
    else await addRoom(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该房屋吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteRoom(row.id)
      ElMessage.success('删除成功'); loadData()
    }).catch(() => {})
}

onMounted(() => { loadData(); loadAll() })
</script>
