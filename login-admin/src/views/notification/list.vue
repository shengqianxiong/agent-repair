<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="标题">
        <el-input v-model="query.title" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.type" placeholder="全部" clearable style="width: 120px">
          <el-option label="系统" value="系统" />
          <el-option label="活动" value="活动" />
          <el-option label="订单" value="订单" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="goEdit">推送通知</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="type" label="类型" width="90" />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="发送时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
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
import { getNotificationList } from '@/api/modules/notification'
import { formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ title: '', type: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getNotificationList(query)
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

function goEdit() {
  router.push('/notification/edit')
}

onMounted(loadList)
</script>
