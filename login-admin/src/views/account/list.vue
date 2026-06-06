<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="账号">
        <el-input v-model="query.username" placeholder="请输入账号关键词" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span>共 {{ total }} 条账号</span>
      <el-button type="primary" @click="goCreate">新增账号</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="账号" min-width="140" />
      <el-table-column prop="createTime" label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="账号状态" width="100">
        <template #default="{ row }">
          <el-tag :type="accountStatusMap[row.status]?.type || 'info'">
            {{ row.statusText || accountStatusMap[row.status]?.label || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
          <el-button
            link
            type="danger"
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
        @current-change="loadList"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAccount, getAccountList } from '@/api/modules/account'
import { getAccountId } from '@/utils/auth'
import { accountStatusMap, formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentAccountId = getAccountId()
const query = reactive({ username: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getAccountList(query)
    tableData.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function resetQuery() {
  query.username = ''
  query.page = 1
  loadList()
}

function handleSizeChange() {
  query.page = 1
  loadList()
}

function goCreate() {
  router.push('/account/form')
}

function goEdit(id) {
  router.push({ path: '/account/form', query: { id } })
}

async function handleDelete(row) {
  if (row.id === currentAccountId) {
    ElMessage.warning('不可删除当前登录的管理员账号')
    return
  }
  await ElMessageBox.confirm(`确定要永久删除账号「${row.username}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  })
  await deleteAccount(row.id)
  ElMessage.success('账号已删除')
  loadList()
}

onMounted(loadList)
</script>
