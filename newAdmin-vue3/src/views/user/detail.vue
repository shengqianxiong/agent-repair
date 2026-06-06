<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <el-descriptions title="会员信息" :column="2" border>
        <el-descriptions-item label="昵称">{{ detail.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="等级">{{ detail.memberLevelName || detail.memberLevel }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ detail.points }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-form :model="levelForm" label-width="100px" style="margin-top: 16px; max-width: 400px">
        <el-form-item label="调整等级">
          <el-input-number v-model="levelForm.memberLevel" :min="1" />
          <el-button type="primary" style="margin-left: 12px" @click="handleAdjustLevel">保存</el-button>
        </el-form-item>
      </el-form>

      <el-tabs v-model="activeTab" style="margin-top: 24px">
        <el-tab-pane label="消费记录" name="orders">
          <el-table :data="detail.orders || []" stripe size="small">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="payAmount" label="金额" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="积分流水" name="points">
          <el-table :data="detail.pointsRecords || []" stripe size="small">
            <el-table-column prop="type" label="类型" width="80" />
            <el-table-column prop="points" label="变动" width="80" />
            <el-table-column prop="source" label="来源" />
            <el-table-column prop="createTime" label="时间" min-width="160">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="预约记录" name="bookings">
          <el-table :data="detail.bookings || []" stripe size="small">
            <el-table-column prop="bookingDate" label="日期" />
            <el-table-column prop="timeSlot" label="时段" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <el-button style="margin-top: 16px" @click="router.back()">返回</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserDetail, adjustUserLevel } from '@/api/modules/user'
import { formatDateTime } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const activeTab = ref('orders')

const levelForm = reactive({ userId: route.query.id, memberLevel: 1 })

async function loadDetail() {
  loading.value = true
  try {
    detail.value = (await getUserDetail(route.query.id)) || {}
    levelForm.memberLevel = detail.value.memberLevel || 1
  } finally {
    loading.value = false
  }
}

async function handleAdjustLevel() {
  await adjustUserLevel({ userId: detail.value.id, memberLevel: levelForm.memberLevel })
  ElMessage.success('等级已调整')
  loadDetail()
}

onMounted(loadDetail)
</script>
