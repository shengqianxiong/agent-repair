<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col v-for="item in statCards" :key="item.key" :span="6">
        <el-card shadow="hover">
          <div class="card-stat">
            <div class="value">{{ stats[item.key] ?? 0 }}</div>
            <div class="label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>最近订单</template>
          <el-table v-loading="loading" :data="recentOrders" size="small" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="140" />
            <el-table-column prop="payAmount" label="金额" width="90">
              <template #default="{ row }">¥{{ formatMoney(row.payAmount) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="ORDER_STATUS_MAP[row.status]">{{ row.status }}</el-tag>
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
          <el-table v-loading="loading" :data="recentBookings" size="small" stripe>
            <el-table-column prop="bookingDate" label="日期" width="110" />
            <el-table-column prop="timeSlot" label="时段" width="100" />
            <el-table-column prop="guestCount" label="人数" width="70" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="BOOKING_STATUS_MAP[row.status]">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getDashboardStats, getRecentOrders, getRecentBookings } from '@/api/modules/dashboard'
import { formatDateTime, formatMoney, ORDER_STATUS_MAP, BOOKING_STATUS_MAP } from '@/utils/tool'

const loading = ref(false)
const stats = ref({})
const recentOrders = ref([])
const recentBookings = ref([])

const statCards = [
  { key: 'orderCount', label: '今日订单' },
  { key: 'bookingCount', label: '今日预约' },
  { key: 'salesAmount', label: '今日销售额' },
  { key: 'memberCount', label: '会员总数' },
]

async function loadData() {
  loading.value = true
  try {
    const [statsData, orders, bookings] = await Promise.all([
      getDashboardStats().catch(() => ({})),
      getRecentOrders({ limit: 10 }).catch(() => ({ list: [] })),
      getRecentBookings({ limit: 10 }).catch(() => ({ list: [] })),
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
