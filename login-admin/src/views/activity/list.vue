<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="标题">
        <el-input v-model="query.title" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span>活动列表</span>
      <el-button type="primary" @click="goEdit()">新增活动</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image v-if="row.image" :src="row.image" style="width: 48px; height: 48px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column prop="tag" label="标签" width="100" />
      <el-table-column prop="price" label="活动价" width="90">
        <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
          <el-button link type="primary" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
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
import { deleteActivity, getActivityList, updateActivityStatus } from '@/api/modules/activity'
import { formatDateTime, formatMoney } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ title: '', status: null, page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getActivityList(query)
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
  query.title = ''
  query.status = null
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

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该活动？', '提示', { type: 'warning' })
  await deleteActivity(id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
