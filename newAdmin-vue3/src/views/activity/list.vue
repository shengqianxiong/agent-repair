<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="活动标题">
          <el-input v-model="query.title" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-toolbar">
        <span>活动列表</span>
        <el-button type="primary" @click="goEdit()">新增活动</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="tag" label="标签" width="100" />
        <el-table-column prop="price" label="活动价" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
            <el-button link @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, deleteActivity, updateActivityStatus } from '@/api/modules/activity'
import { formatDateTime, formatMoney } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ title: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const data = await getActivityList({ ...query })
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.title = ''
  query.page = 1
  loadList()
}

function goEdit(id) {
  router.push({ path: '/activity/edit', query: id ? { id } : {} })
}

async function toggleStatus(row) {
  const status = row.status === 1 ? 0 : 1
  await updateActivityStatus(row.id, status)
  ElMessage.success('状态已更新')
  loadList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除活动「${row.title}」？`, '提示', { type: 'warning' })
  await deleteActivity(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
