<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="业主姓名">
            <el-input v-model="queryForm.name" placeholder="请输入业主姓名" clearable />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="queryForm.phone" placeholder="请输入手机号" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <div style="margin-bottom: 10px;" v-if="isAdmin">
        <el-button type="primary" @click="handleAdd">新增业主</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="业主姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="carPlate" label="车牌号" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已迁出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewRooms(row)">查看房屋</el-button>
            <template v-if="isAdmin">
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="车牌号" prop="carPlate">
          <el-input v-model="form.carPlate" placeholder="请输入车牌号" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">已迁出</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看房屋弹窗 -->
    <el-dialog v-model="roomDialogVisible" title="业主房屋" width="700px">
      <el-table :data="ownerRooms" stripe>
        <el-table-column prop="communityName" label="小区" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="unitName" label="单元" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="roomType" label="户型" />
        <el-table-column prop="relationType" label="关系" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.relationType === 'owner'" type="primary">业主</el-tag>
            <el-tag v-else-if="row.relationType === 'family'" type="success">家属</el-tag>
            <el-tag v-else type="warning">租客</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwnerList, addOwner, updateOwner, deleteOwner, getOwnerRooms } from '../../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const queryForm = reactive({ name: '', phone: '', idCard: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('新增业主')
const formRef = ref()
const form = reactive({
  id: null, name: '', phone: '', idCard: '', gender: 1, birthday: '',
  carPlate: '', emergencyContact: '', emergencyPhone: '', status: 1
})
const rules = {
  name: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const roomDialogVisible = ref(false)
const ownerRooms = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOwnerList({ pageNum: pageNum.value, pageSize: pageSize.value, ...queryForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const handleQuery = () => { pageNum.value = 1; loadData() }
const handleReset = () => { queryForm.name = ''; queryForm.phone = ''; queryForm.idCard = ''; handleQuery() }

const handleAdd = () => {
  form.id = null; form.name = ''; form.phone = ''; form.idCard = ''; form.gender = 1
  form.birthday = ''; form.carPlate = ''; form.emergencyContact = ''; form.emergencyPhone = ''; form.status = 1
  dialogTitle.value = '新增业主'; dialogVisible.value = true
}

const handleEdit = (row) => { Object.assign(form, row); dialogTitle.value = '编辑业主'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) await updateOwner(form)
    else await addOwner(form)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该业主吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteOwner(row.id)
      ElMessage.success('删除成功'); loadData()
    }).catch(() => {})
}

const handleViewRooms = async (row) => {
  const res = await getOwnerRooms(row.id)
  ownerRooms.value = res.data || []
  roomDialogVisible.value = true
}

onMounted(() => loadData())
</script>
