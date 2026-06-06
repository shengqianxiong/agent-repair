<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="账号">
        <el-input v-model="query.account" placeholder="请输入账号关键词" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span>账号列表</span>
      <el-button type="primary" @click="goForm()">新增账号</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="account" label="账号" min-width="160" />
      <el-table-column prop="createTime" label="创建时间" min-width="180">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="账号状态" width="120">
        <template #default="{ row }">
          <el-tag :type="accountStatusMap[row.status]?.type">
            {{ accountStatusMap[row.status]?.label || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goForm(row.id)">编辑</el-button>
          <el-button
            link
            type="danger"
            :disabled="isCurrentAccount(row)"
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
        layout="total, prev, pager, next"
        @current-change="loadList"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAccount, getAccountList } from '@/api/modules/account'
import { getUserInfo } from '@/utils/auth'
import { accountStatusMap, formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ account: '', page: 1, pageSize: 10 })

/** 判断是否为当前登录管理员，禁止删除自身账号 */
function isCurrentAccount(row) {
  const currentUser = getUserInfo()
  if (!currentUser) return false
  return currentUser.id === row.id || currentUser.account === row.account
}

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
  query.account = ''
  query.page = 1
  loadList()
}

function goForm(id) {
  router.push({ path: '/account/form', query: id ? { id } : {} })
}

async function handleDelete(row) {
  if (isCurrentAccount(row)) {
    ElMessage.warning('不可删除当前登录的管理员账号')
    return
  }
  await ElMessageBox.confirm(`确认永久删除账号「${row.account}」？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  })
  await deleteAccount(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
