<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="昵称">
          <el-input v-model="query.nickname" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="等级">
          <el-input-number v-model="query.memberLevel" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="memberLevelName" label="等级" width="100" />
        <el-table-column prop="points" label="积分" width="90" />
        <el-table-column prop="createTime" label="注册时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
            <el-button link @click="openPointsDialog(row)">调整积分</el-button>
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

    <el-dialog v-model="pointsDialogVisible" title="调整积分" width="400px">
      <el-form :model="pointsForm" label-width="80px">
        <el-form-item label="变动积分">
          <el-input-number v-model="pointsForm.points" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="pointsForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPoints">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserList, adjustUserPoints } from '@/api/modules/user'
import { formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ nickname: '', memberLevel: null, page: 1, pageSize: 10 })

const pointsDialogVisible = ref(false)
const pointsForm = reactive({ userId: null, points: 0, remark: '' })

async function loadList() {
  loading.value = true
  try {
    const data = await getUserList({ ...query })
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
  pointsDialogVisible.value = true
}

async function submitPoints() {
  await adjustUserPoints({ ...pointsForm })
  ElMessage.success('积分已调整')
  pointsDialogVisible.value = false
  loadList()
}

onMounted(loadList)
</script>
