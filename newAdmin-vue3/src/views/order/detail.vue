<template>
  <div class="page-card" v-loading="loading">
    <el-descriptions title="订单信息" :column="2" border>
      <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="orderStatusMap[detail.status]">{{ detail.status }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
      <el-descriptions-item label="桌位ID">{{ detail.tableId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单总额">¥{{ formatMoney(detail.totalAmount) }}</el-descriptions-item>
      <el-descriptions-item label="实付金额">¥{{ formatMoney(detail.payAmount) }}</el-descriptions-item>
      <el-descriptions-item label="下单时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="支付时间">{{ formatDateTime(detail.payTime) }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <h4>商品明细</h4>
    <el-table :data="detail.items || []" stripe style="margin-bottom: 16px">
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="unitPrice" label="单价" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.unitPrice) }}</template>
      </el-table-column>
      <el-table-column prop="subtotal" label="小计" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.subtotal) }}</template>
      </el-table-column>
    </el-table>

    <el-space wrap>
      <el-select v-model="nextStatus" placeholder="更新状态" style="width: 140px">
        <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button type="primary" @click="handleStatusUpdate">更新状态</el-button>
      <el-button type="warning" @click="handleRefund">退款</el-button>
      <el-button @click="goBack">返回</el-button>
    </el-space>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, refundOrder, updateOrderStatus } from '@/api/modules/order'
import { formatDateTime, formatMoney, orderStatusMap } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const nextStatus = ref('')
const statusOptions = ['已支付', '制作中', '已完成', '已取消']

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getOrderDetail(route.query.id) || {}
    nextStatus.value = detail.value.status
  } finally {
    loading.value = false
  }
}

async function handleStatusUpdate() {
  if (!nextStatus.value) return
  await updateOrderStatus({ id: detail.value.id, status: nextStatus.value })
  ElMessage.success('状态已更新')
  loadDetail()
}

async function handleRefund() {
  await ElMessageBox.confirm('确认对该订单发起退款？', '提示', { type: 'warning' })
  await refundOrder(detail.value.id)
  ElMessage.success('退款成功')
  loadDetail()
}

function goBack() {
  router.push('/order/list')
}

onMounted(loadDetail)
</script>
