<template>
  <div class="dashboard-page">
    <!-- 时间筛选 -->
    <div class="page-card filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="统计时间">
          <el-date-picker
            v-model="query.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item class="create-entry">
          <el-button type="primary" @click="goCreate">
            <el-icon><Plus /></el-icon> 创建活动
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row" v-loading="loading">
      <el-col :span="4" v-for="item in statCards" :key="item.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-inner">
            <div>
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.display }}</div>
            </div>
            <div class="stat-icon" :style="{ background: item.gradient }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never" class="page-card inner-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button link type="primary" @click="$router.push('/seller/activity/list')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" v-loading="loading" size="small" stripe empty-text="暂无活动数据">
            <el-table-column prop="title" label="活动名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="scanCount" label="扫码量" width="80" align="center" />
            <el-table-column prop="submitCount" label="提交数" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="activityStatusMap[row.status]?.type">
                  {{ activityStatusMap[row.status]?.label || '-' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="page-card inner-card">
          <template #header>
            <div class="card-header">
              <span>待审核评价</span>
              <el-button link type="primary" @click="$router.push('/seller/verify/list')">去审核</el-button>
            </div>
          </template>
          <el-table :data="recentVerifies" v-loading="loading" size="small" stripe empty-text="暂无待审核记录">
            <el-table-column prop="activityTitle" label="活动" min-width="100" show-overflow-tooltip />
            <el-table-column prop="rebateAmount" label="返利" width="80">
              <template #default="{ row }">¥{{ formatMoney(row.rebateAmount) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="verifyStatusMap[row.status]?.type">
                  {{ verifyStatusMap[row.status]?.label || '-' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" min-width="140">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Document, View, EditPen, CircleCheck, Money } from '@element-plus/icons-vue'
import { getDashboardStats, getRecentActivities, getRecentVerifies } from '@/api/modules/dashboard'
import { activityStatusMap, verifyStatusMap, formatMoney, formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const stats = ref({})
const recentActivities = ref([])
const recentVerifies = ref([])

const query = reactive({
  dateRange: []
})

const statCards = computed(() => [
  {
    key: 'activityCount',
    label: '活动数',
    display: stats.value.activityCount ?? 0,
    icon: Document,
    gradient: 'linear-gradient(135deg, #fe2c55, #ff6b85)'
  },
  {
    key: 'scanCount',
    label: '扫码量',
    display: stats.value.scanCount ?? 0,
    icon: View,
    gradient: 'linear-gradient(135deg, #667eea, #764ba2)'
  },
  {
    key: 'submitCount',
    label: '评价提交',
    display: stats.value.submitCount ?? 0,
    icon: EditPen,
    gradient: 'linear-gradient(135deg, #f093fb, #f5576c)'
  },
  {
    key: 'verifyCount',
    label: '核销量',
    display: stats.value.verifyCount ?? 0,
    icon: CircleCheck,
    gradient: 'linear-gradient(135deg, #4facfe, #00f2fe)'
  },
  {
    key: 'rebateAmount',
    label: '返利支出',
    display: `¥${formatMoney(stats.value.rebateAmount)}`,
    icon: Money,
    gradient: 'linear-gradient(135deg, #43e97b, #38f9d7)'
  }
])

function buildParams() {
  const params = {}
  if (query.dateRange?.length === 2) {
    params.startDate = query.dateRange[0]
    params.endDate = query.dateRange[1]
  }
  return params
}

async function loadData() {
  loading.value = true
  try {
    const params = buildParams()
    const [statsData, activities, verifies] = await Promise.all([
      getDashboardStats(params),
      getRecentActivities({ limit: 5 }),
      getRecentVerifies({ limit: 5, status: 0 })
    ])
    stats.value = statsData || {}
    recentActivities.value = activities?.list || activities || []
    recentVerifies.value = verifies?.list || verifies || []
  } catch {
    stats.value = {}
    recentActivities.value = []
    recentVerifies.value = []
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.dateRange = []
  loadData()
}

function goCreate() {
  router.push('/seller/activity/create')
}

onMounted(loadData)
</script>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}

.filter-card :deep(.el-form) {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.create-entry {
  margin-left: auto;
}

.stat-row {
  margin-bottom: 0;
}

.stat-card-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.inner-card {
  box-shadow: none;
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
