<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <el-form :inline="true">
          <el-form-item label="角色名称">
            <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <div style="margin-bottom: 10px;">
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleKey" label="角色标识" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAssignMenus(row)">分配菜单</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="500px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        :check-strictly="false"
        show-checkbox
        default-expand-all
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMenus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, getMenuTree, getRoleMenus, assignRoleMenus } from '../../api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref()
const form = reactive({
  id: null,
  roleName: '',
  roleKey: '',
  description: '',
  status: 1
})
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const queryForm = reactive({ roleName: '' })

const menuDialogVisible = ref(false)
const menuTree = ref([])
const menuTreeRef = ref()
const currentRoleId = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ pageNum: 1, pageSize: 100, ...queryForm })
    tableData.value = res.data.records
  } catch (e) {
    // error handled
  } finally {
    loading.value = false
  }
}

const handleQuery = () => loadData()
const handleReset = () => { queryForm.roleName = ''; loadData() }

const handleAdd = () => {
  form.id = null
  form.roleName = ''
  form.roleKey = ''
  form.description = ''
  form.status = 1
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) await updateRole(form)
    else await addRole(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该角色吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

const handleAssignMenus = async (row) => {
  currentRoleId.value = row.id
  const treeRes = await getMenuTree()
  menuTree.value = treeRes.data || []
  const menusRes = await getRoleMenus(row.id)
  menuTreeRef.value?.setCheckedKeys(menusRes.data || [])
  menuDialogVisible.value = true
}

const handleSubmitMenus = async () => {
  const menuIds = menuTreeRef.value?.getCheckedKeys() || []
  await assignRoleMenus(currentRoleId.value, menuIds)
  ElMessage.success('菜单分配成功')
  menuDialogVisible.value = false
}

onMounted(() => loadData())
</script>
