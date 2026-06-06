<template>
  <div class="page-card">
    <div class="table-toolbar">
      <span>游戏列表</span>
      <el-button type="primary" @click="goEdit()">新增游戏</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" min-width="140" />
      <el-table-column prop="rewardPoints" label="奖励积分" width="100" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getGameList } from '@/api/modules/game'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])

async function loadList() {
  loading.value = true
  try {
    const res = await getGameList({ page: 1, pageSize: 100 })
    tableData.value = res?.list || res || []
  } finally {
    loading.value = false
  }
}

function goEdit(id) {
  router.push({ path: '/game/edit', query: id ? { id } : {} })
}

onMounted(loadList)
</script>
