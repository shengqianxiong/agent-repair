<template>
  <div class="page-card">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>最近订单</template>
          <el-table :data="recentOrders" v-loading="loading" size="small" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="140" />
            <el-table-column prop="payAmount" label="实付" width="90">
              <template #default="{ row }">¥{{ formatMoney(row.payAmount) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="orderStatusMap[row.status]">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" min-width="160">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>最近预约</template>
          <el-table :data="recentBookings" v-loading="loading" size="small" stripe>
            <el-table-column prop="bookingDate" label="日期" width="110" />
            <el-table-column prop="timeSlot" label="时段" width="100" />
            <el-table-column prop="guestCount" label="人数" width="70" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="bookingStatusMap[row.status]">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约时间" min-width="160">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDashboardStats, getRecentBookings, getRecentOrders } from '@/api/modules/dashboard'
import { formatDateTime, formatMoney, orderStatusMap, bookingStatusMap } from '@/utils/tool'

const loading = ref(false)
const stats = ref({})
const recentOrders = ref([])
const recentBookings = ref([])

const statCards = computed(() => [
  { label: '订单量', value: stats.value.orderCount ?? 0 },
  { label: '预约量', value: stats.value.bookingCount ?? 0 },
  { label: '销售额', value: `¥${formatMoney(stats.value.salesAmount)}` },
  { label: '会员数', value: stats.value.memberCount ?? 0 }
])

async function loadData() {
  loading.value = true
  try {
    const [statsData, orders, bookings] = await Promise.all([
      getDashboardStats(),
      getRecentOrders({ limit: 10 }),
      getRecentBookings({ limit: 10 })
    ])
    stats.value = statsData || {}
    recentOrders.value = orders?.list || orders || []
    recentBookings.value = bookings?.list || bookings || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.stat-row {
  margin-bottom: 0;
}
.stat-card {
  text-align: center;
}
.stat-label {
  color: #909399;
  font-size: 14px;
}
.stat-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}
</style>
