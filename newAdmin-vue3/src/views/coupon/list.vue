<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="券码">
          <el-input v-model="query.code" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="未使用" value="未使用" />
            <el-option label="已核销" value="已核销" />
            <el-option label="已过期" value="已过期" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-toolbar">
        <span>团购券列表</span>
        <el-button type="primary" @click="router.push('/coupon/edit')">批量生成</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="code" label="券码" min-width="160" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="discountAmount" label="抵扣金额" width="110">
          <template #default="{ row }">¥{{ formatMoney(row.discountAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="expireTime" label="过期时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.expireTime) }}</template>
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

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>核销记录</template>
      <el-table v-loading="recordLoading" :data="verifyRecords" stripe border size="small">
        <el-table-column prop="code" label="券码" />
        <el-table-column prop="verifyTime" label="核销时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.verifyTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCouponList, getVerifyRecords } from '@/api/modules/coupon'
import { formatDateTime, formatMoney } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const recordLoading = ref(false)
const tableData = ref([])
const verifyRecords = ref([])
const total = ref(0)

const query = reactive({ code: '', status: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const data = await getCouponList({ ...query })
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

async function loadRecords() {
  recordLoading.value = true
  try {
    const data = await getVerifyRecords({ page: 1, pageSize: 20 })
    verifyRecords.value = data?.list || []
  } finally {
    recordLoading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.code = ''
  query.status = ''
  query.page = 1
  loadList()
}

onMounted(async () => {
  await loadList()
  await loadRecords()
})
</script>
