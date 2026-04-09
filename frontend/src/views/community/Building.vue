<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="所属小区">
            <el-select v-model="queryForm.communityId" placeholder="请选择小区" clearable>
              <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼栋名称">
            <el-input v-model="queryForm.name" placeholder="请输入楼栋名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <div style="margin-bottom: 10px;" v-if="isAdmin">
        <el-button type="primary" @click="handleAdd">新增楼栋</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="楼栋名称" />
        <el-table-column prop="floors" label="楼层数" />
        <el-table-column prop="remark" label="备注" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属小区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择小区">
            <el-option v-for="c in communities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { getBuildingList, addBuilding, updateBuilding, deleteBuilding, getAllCommunities } from '../../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const communities = ref([])
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const queryForm = reactive({ communityId: null, name: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('新增楼栋')
const formRef = ref()
const form = reactive({ id: null, communityId: null, name: '', floors: 1, remark: '' })
const rules = {
  communityId: [{ required: true, message: '请选择所属小区', trigger: 'change' }],
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBuildingList({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const loadCommunities = async () => {
  const res = await getAllCommunities()
  communities.value = res.data || []
}

const handleQuery = () => { pageNum.value = 1; loadData() }
const handleReset = () => { queryForm.communityId = null; queryForm.name = ''; handleQuery() }

const handleAdd = () => {
  form.id = null; form.communityId = null; form.name = ''; form.floors = 1; form.remark = ''
  dialogTitle.value = '新增楼栋'; dialogVisible.value = true
}

const handleEdit = (row) => { Object.assign(form, row); dialogTitle.value = '编辑楼栋'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) await updateBuilding(form)
    else await addBuilding(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该楼栋吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteBuilding(row.id)
      ElMessage.success('删除成功'); loadData()
    }).catch(() => {})
}

onMounted(() => { loadData(); loadCommunities() })
</script>
