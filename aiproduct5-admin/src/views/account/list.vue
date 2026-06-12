<template>
  <div class="page-card">
    <div class="search-bar">
      <el-form :inline="true" @submit.prevent="handleSearch">
        <el-form-item label="账号">
          <el-input v-model="query.account" placeholder="请输入账号关键词" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-toolbar">
      <span>账号列表</span>
      <el-button type="primary" @click="handleAdd">新增账号</el-button>
    </div>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="account" label="账号" min-width="140" />
      <el-table-column prop="createTime" label="创建时间" min-width="180" />
      <el-table-column prop="statusText" label="账号状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button
            type="danger"
            link
            :disabled="row.id === currentAccountId"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="loadData"
      />
    </div>

    <!-- 新增账号弹窗 -->
    <el-dialog v-model="addVisible" title="新增账号" width="420px" destroy-on-close>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="账号" prop="account">
          <el-input v-model="addForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入初始密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAccountList, saveAccount, deleteAccount } from '@/api/modules/account'
import { getAccountId } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentAccountId = getAccountId()
const addVisible = ref(false)
const addFormRef = ref()

const query = reactive({
  account: '',
  page: 1,
  pageSize: 10
})

const addForm = reactive({
  account: '',
  password: ''
})

const addRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const data = await getAccountList(query)
    tableData.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.account = ''
  query.page = 1
  loadData()
}

function handleAdd() {
  addForm.account = ''
  addForm.password = ''
  addVisible.value = true
}

async function submitAdd() {
  await addFormRef.value.validate()
  submitLoading.value = true
  try {
    await saveAccount(addForm)
    ElMessage.success('新增成功')
    addVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

function handleEdit(row) {
  router.push({ path: '/account/form', query: { id: row.id } })
}

function handleDelete(row) {
  if (row.id === currentAccountId) {
    ElMessage.warning('不可删除当前登录的管理员账号')
    return
  }
  ElMessageBox.confirm(`确认永久删除账号「${row.account}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteAccount(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>
