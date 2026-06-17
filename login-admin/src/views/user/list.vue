<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="昵称">
        <el-input v-model="query.nickname" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="等级">
        <el-input-number v-model="query.memberLevel" :min="0" controls-position="right" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :src="row.avatar" :size="36">{{ row.nickname?.[0] }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="memberLevelName" label="等级" width="100" />
      <el-table-column prop="points" label="积分" width="90" />
      <el-table-column prop="createTime" label="注册时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
          <el-button link type="primary" @click="openPointsDialog(row)">调积分</el-button>
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

    <el-dialog v-model="pointsVisible" title="调整积分" width="400px">
      <el-form label-width="80px">
        <el-form-item label="变动积分">
          <el-input-number v-model="pointsForm.points" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="pointsForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPoints">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adjustUserPoints, getUserList } from '@/api/modules/user'
import { formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pointsVisible = ref(false)
const pointsForm = reactive({ userId: null, points: 0, remark: '' })
const query = reactive({ nickname: '', memberLevel: null, page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getUserList(query)
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
  query.nickname = ''
  query.memberLevel = null
  query.page = 1
  loadList()
}

function goDetail(id) {
  router.push({ path: '/user/detail', query: { id } })
}

function openPointsDialog(row) {
  pointsForm.userId = row.id
  pointsForm.points = 0
  pointsForm.remark = ''
  pointsVisible.value = true
}

async function submitPoints() {
  await adjustUserPoints({ ...pointsForm })
  ElMessage.success('积分已调整')
  pointsVisible.value = false
  loadList()
}

onMounted(loadList)
</script>
