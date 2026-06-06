<template>
  <div class="page-card">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="券列表" name="list">
        <el-form :inline="true" :model="query" class="search-bar">
          <el-form-item label="券码">
            <el-input v-model="query.code" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
              <el-option label="未使用" value="未使用" />
              <el-option label="已核销" value="已核销" />
              <el-option label="已过期" value="已过期" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="success" @click="goEdit">批量生成</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="code" label="券码" min-width="140" />
          <el-table-column prop="name" label="名称" min-width="120" />
          <el-table-column prop="type" label="类型" width="90" />
          <el-table-column prop="discountAmount" label="抵扣" width="90">
            <template #default="{ row }">¥{{ formatMoney(row.discountAmount) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90" />
          <el-table-column prop="expireTime" label="过期时间" min-width="170">
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
      </el-tab-pane>

      <el-tab-pane label="核销记录" name="records">
        <el-table :data="records" v-loading="recordsLoading" stripe>
          <el-table-column prop="code" label="券码" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="verifyTime" label="核销时间">
            <template #default="{ row }">{{ formatDateTime(row.verifyTime) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCouponList, getVerifyRecords } from '@/api/modules/coupon'
import { formatDateTime, formatMoney } from '@/utils/tool'

const router = useRouter()
const activeTab = ref('list')
const loading = ref(false)
const recordsLoading = ref(false)
const tableData = ref([])
const records = ref([])
const total = ref(0)
const query = reactive({ code: '', status: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getCouponList(query)
    tableData.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadRecords() {
  recordsLoading.value = true
  try {
    const res = await getVerifyRecords({ page: 1, pageSize: 50 })
    records.value = res?.list || res || []
  } finally {
    recordsLoading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function goEdit() {
  router.push('/coupon/edit')
}

watch(activeTab, (val) => {
  if (val === 'records') loadRecords()
})

onMounted(loadList)
</script>
