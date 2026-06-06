<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="table-toolbar">
        <span>互动游戏</span>
        <el-button type="primary" @click="goEdit()">新增游戏</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="name" label="游戏名称" min-width="140" />
        <el-table-column prop="rewardPoints" label="奖励积分" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
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
    const data = await getGameList({ page: 1, pageSize: 100 })
    tableData.value = data?.list || data || []
  } finally {
    loading.value = false
  }
}

function goEdit(id) {
  router.push({ path: '/game/edit', query: id ? { id } : {} })
}

onMounted(loadList)
</script>
