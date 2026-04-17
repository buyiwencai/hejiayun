<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="所属小区">
            <el-select v-model="queryForm.communityId" placeholder="请选择小区" clearable @change="handleCommunityChange" style="width: 180px">
              <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属楼栋">
            <el-select v-model="queryForm.buildingId" placeholder="请选择楼栋" clearable @change="handleBuildingChange" style="width: 180px">
              <el-option v-for="b in filteredBuildings" :key="b.id" :label="b.name" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="单元名称">
            <el-input v-model="queryForm.name" placeholder="请输入单元名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <div style="margin-bottom: 10px;" v-if="isAdmin">
        <el-button type="primary" @click="handleAdd">新增单元</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="communityName" label="所属小区" />
        <el-table-column prop="buildingName" label="所属楼栋" />
        <el-table-column prop="name" label="单元名称" />
        <el-table-column prop="floorNum" label="楼层数" />
        <el-table-column prop="createTime" label="创建时间" />
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
          <el-select v-model="form.buildingId" placeholder="请先选择小区" style="width: 100%" :disabled="!form.communityId">
            <el-option v-for="b in formFilteredBuildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入单元名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floorNum">
          <el-input-number v-model="form.floorNum" :min="1" />
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
import { getUnitList, addUnit, updateUnit, deleteUnit, getAllCommunities, getAllBuildings } from '../../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const communities = ref([])
const buildings = ref([])
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const queryForm = reactive({ communityId: null, buildingId: null, name: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('新增单元')
const formRef = ref()
const form = reactive({ id: null, communityId: null, buildingId: null, name: '', floorNum: 1 })
const rules = {
  communityId: [{ required: true, message: '请选择所属小区', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择所属楼栋', trigger: 'change' }],
  name: [{ required: true, message: '请输入单元名称', trigger: 'blur' }]
}

// 搜索条件联动
const filteredBuildings = computed(() => {
  if (!queryForm.communityId) return buildings.value
  return buildings.value.filter(b => b.communityId === queryForm.communityId)
})

// 表单联动
const formFilteredBuildings = computed(() => {
  if (!form.communityId) return []
  return buildings.value.filter(b => b.communityId === form.communityId)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUnitList({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const loadAll = async () => {
  const [commRes, buildRes] = await Promise.all([getAllCommunities(), getAllBuildings()])
  communities.value = commRes.data || []
  buildings.value = buildRes.data || []
}

const handleCommunityChange = () => { queryForm.buildingId = null; pageNum.value = 1; loadData() }
const handleBuildingChange = () => { pageNum.value = 1; loadData() }
const handleQuery = () => { pageNum.value = 1; loadData() }
const handleReset = () => { queryForm.communityId = null; queryForm.buildingId = null; queryForm.name = ''; handleQuery() }

const handleFormCommunityChange = () => { form.buildingId = null }

const handleAdd = () => {
  form.id = null; form.communityId = null; form.buildingId = null; form.name = ''; form.floorNum = 1
  dialogTitle.value = '新增单元'; dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  if (row.buildingId) {
    const building = buildings.value.find(b => b.id === row.buildingId)
    if (building) form.communityId = building.communityId
  }
  dialogTitle.value = '编辑单元'; dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) await updateUnit(form)
    else await addUnit(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该单元吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteUnit(row.id)
      ElMessage.success('删除成功'); loadData()
    }).catch(() => {})
}

onMounted(() => { loadData(); loadAll() })
</script>
