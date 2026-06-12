<template>
  <div class="page-card" v-loading="loading">
    <el-descriptions title="会员信息" :column="2" border>
      <el-descriptions-item label="用户ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detail.nickname }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
      <el-descriptions-item label="会员等级">{{ detail.memberLevelName || detail.memberLevel }}</el-descriptions-item>
      <el-descriptions-item label="积分">{{ detail.points }}</el-descriptions-item>
      <el-descriptions-item label="注册时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <el-form :inline="true">
      <el-form-item label="调整等级">
        <el-input-number v-model="levelForm.memberLevel" :min="1" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLevelAdjust">保存等级</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="消费记录" name="orders">
        <el-table :data="detail.orders || []" stripe size="small">
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column prop="payAmount" label="实付">
            <template #default="{ row }">¥{{ formatMoney(row.payAmount) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="积分流水" name="points">
        <el-table :data="detail.pointsRecords || []" stripe size="small">
          <el-table-column prop="type" label="类型" />
          <el-table-column prop="points" label="变动" />
          <el-table-column prop="source" label="来源" />
          <el-table-column prop="createTime" label="时间">
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adjustUserLevel, getUserDetail } from '@/api/modules/user'
import { formatDateTime, formatMoney } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const activeTab = ref('orders')
const levelForm = reactive({ userId: null, memberLevel: 1 })

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getUserDetail(route.query.id) || {}
    levelForm.userId = detail.value.id
    levelForm.memberLevel = detail.value.memberLevel || 1
  } finally {
    loading.value = false
  }
}

async function handleLevelAdjust() {
  await adjustUserLevel({ ...levelForm })
  ElMessage.success('等级已调整')
  loadDetail()
}

function goBack() {
  router.push('/user/list')
}

onMounted(loadDetail)
</script>
