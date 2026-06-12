<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="订单号">
        <el-input v-model="query.orderNo" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" min-width="150" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="tableId" label="桌位ID" width="90" />
      <el-table-column prop="totalAmount" label="总额" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
      </el-table-column>
      <el-table-column prop="payAmount" label="实付" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.payAmount) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="orderStatusMap[row.status]">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
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
import { getOrderList } from '@/api/modules/order'
import { formatDateTime, formatMoney, orderStatusMap } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const statusOptions = ['待支付', '已支付', '制作中', '已完成', '已取消']
const query = reactive({ orderNo: '', status: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const res = await getOrderList(query)
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
  query.orderNo = ''
  query.status = ''
  query.page = 1
  loadList()
}

function goDetail(id) {
  router.push({ path: '/order/detail', query: { id } })
}

onMounted(loadList)
</script>
