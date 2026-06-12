<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="活动名称/商品ID" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="(item, key) in activityStatusMap" :key="key" :label="item.label" :value="Number(key)" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="query.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="YYYY-MM-DD"
          style="width: 260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span class="toolbar-title">活动列表</span>
      <el-button type="primary" @click="goCreate">
        <el-icon><Plus /></el-icon> 创建活动
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe empty-text="暂无活动，点击右上角创建">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImage || row.images?.[0]"
            :src="row.coverImage || row.images?.[0]"
            style="width: 48px; height: 48px; border-radius: 6px"
            fit="cover"
          />
          <span v-else class="no-image">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="活动名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="productId" label="商品ID" width="120" show-overflow-tooltip />
      <el-table-column prop="rebateAmount" label="返利金额" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.rebateAmount) }}</template>
      </el-table-column>
      <el-table-column prop="scanCount" label="扫码量" width="80" align="center" />
      <el-table-column prop="verifyCount" label="核销量" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="activityStatusMap[row.status]?.type">
            {{ activityStatusMap[row.status]?.label || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expireTime" label="有效期" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.expireTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
          <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
          <el-button link type="primary" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadList"
        @size-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getActivityPage, updateActivityStatus } from '@/api/modules/activity'
import { activityStatusMap, formatMoney, formatDateTime } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: null,
  dateRange: [],
  pageNum: 1,
  pageSize: 10,
  orderBy: 'createTime desc'
})

function buildParams() {
  const params = {
    keyword: query.keyword || undefined,
    status: query.status ?? undefined,
    pageNum: query.pageNum,
    pageSize: query.pageSize,
    orderBy: query.orderBy
  }
  if (query.dateRange?.length === 2) {
    params.startDate = query.dateRange[0]
    params.endDate = query.dateRange[1]
  }
  return params
}

async function loadList() {
  loading.value = true
  try {
    const res = await getActivityPage(buildParams())
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNum = 1
  loadList()
}

function resetQuery() {
  query.keyword = ''
  query.status = null
  query.dateRange = []
  query.pageNum = 1
  loadList()
}

function goCreate() {
  router.push('/seller/activity/create')
}

function goEdit(id) {
  router.push({ path: '/seller/activity/edit', query: { id } })
}

function goDetail(id) {
  router.push({ path: '/seller/activity/detail', query: { id } })
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 2 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确认${action}该活动？`, '提示', { type: 'warning' })
  await updateActivityStatus(row.id, newStatus)
  ElMessage.success(`${action}成功`)
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.no-image {
  color: var(--dp-text-secondary);
}
</style>
