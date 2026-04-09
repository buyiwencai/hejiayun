<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>人房绑定管理</span>
      </template>

      <div style="margin-bottom: 10px;" v-if="isAdmin">
        <el-button type="primary" @click="handleAdd">新增绑定</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="ownerName" label="业主姓名" />
        <el-table-column prop="ownerPhone" label="联系电话" />
        <el-table-column prop="communityName" label="小区" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="unitName" label="单元" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="relationType" label="关系" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.relationType === 'owner'" type="primary">业主</el-tag>
            <el-tag v-else-if="row.relationType === 'family'" type="success">家属</el-tag>
            <el-tag v-else type="warning">租客</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bindTime" label="绑定时间" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <template v-if="isAdmin">
              <el-button link type="danger" @click="handleUnbind(row)">解绑</el-button>
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

    <!-- 新增绑定弹窗 -->
    <el-dialog v-model="dialogVisible" title="人房绑定" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择业主" prop="ownerId">
          <el-select v-model="form.ownerId" filterable placeholder="请输入业主姓名或手机号搜索" @change="handleOwnerChange">
            <el-option v-for="o in owners" :key="o.id" :label="`${o.name} - ${o.phone}`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择房屋" prop="roomId">
          <el-cascader
            v-model="form.roomIds"
            :options="roomTree"
            :props="{ label: 'name', value: 'id', checkStrictly: true }"
            placeholder="请选择小区-楼栋-单元-房屋"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="关系类型" prop="relationType">
          <el-select v-model="form.relationType" placeholder="请选择关系类型">
            <el-option label="业主" value="owner" />
            <el-option label="家属" value="family" />
            <el-option label="租客" value="tenant" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwnerRoomList, addOwnerRoom, deleteOwnerRoom, getAllOwners, getAllCommunities, getAllBuildings, getAllUnits, getAllRooms } from '../../api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const owners = ref([])
const isAdmin = computed(() => localStorage.getItem('roleKey') === 'admin')

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ ownerId: null, roomId: null, roomIds: [], relationType: 'owner' })
const rules = {
  ownerId: [{ required: true, message: '请选择业主', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房屋', trigger: 'change' }],
  relationType: [{ required: true, message: '请选择关系类型', trigger: 'change' }]
}

// 房屋级联选择器数据
const roomTree = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOwnerRoomList({ pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const loadOwners = async () => {
  const res = await getAllOwners()
  owners.value = res.data || []
}

const loadRoomTree = async () => {
  const communities = await getAllCommunities()
  const tree = []
  for (const c of communities.data || []) {
    const buildings = await getAllBuildings(c.id)
    const cNode = { id: 'c_' + c.id, name: c.name, children: [] }
    for (const b of buildings.data || []) {
      const units = await getAllUnits(b.id)
      const bNode = { id: 'b_' + b.id, name: b.name, children: [] }
      for (const u of units.data || []) {
        const rooms = await getAllRooms(u.id)
        const uNode = { id: 'u_' + u.id, name: u.name, children: [] }
        for (const r of rooms.data || []) {
          uNode.children.push({ id: r.id, name: r.roomNo })
        }
        bNode.children.push(uNode)
      }
      cNode.children.push(bNode)
    }
    tree.push(cNode)
  }
  roomTree.value = tree
}

const handleOwnerChange = (val) => { console.log('owner selected:', val) }
const handleRoomChange = (val) => {
  if (val && val.length > 0) {
    form.roomId = val[val.length - 1]
  }
}

const handleAdd = () => {
  form.ownerId = null; form.roomId = null; form.roomIds = []; form.relationType = 'owner'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await addOwnerRoom({ ownerId: form.ownerId, roomId: form.roomId, relationType: form.relationType })
    ElMessage.success('绑定成功'); dialogVisible.value = false; loadData()
  } catch (e) {}
}

const handleUnbind = (row) => {
  ElMessageBox.confirm('确认解除该绑定关系吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteOwnerRoom(row.id)
      ElMessage.success('解绑成功'); loadData()
    }).catch(() => {})
}

onMounted(() => { loadData(); loadOwners(); loadRoomTree() })
</script>
