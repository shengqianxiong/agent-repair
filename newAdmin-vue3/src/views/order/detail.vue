<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <el-descriptions title="订单信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="ORDER_STATUS_MAP[detail.status]">{{ detail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总额">¥{{ formatMoney(detail.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="实付">¥{{ formatMoney(detail.payAmount) }}</el-descriptions-item>
        <el-descriptions-item label="桌号">{{ detail.tableNo || detail.tableId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatDateTime(detail.payTime) }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin-top: 24px">商品明细</h4>
      <el-table :data="detail.items || []" stripe border size="small">
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.unitPrice) }}</template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.subtotal) }}</template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 24px">
        <el-select v-model="nextStatus" placeholder="更新状态" style="width: 160px; margin-right: 12px">
          <el-option label="制作中" value="制作中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
        <el-button type="primary" :disabled="!nextStatus" @click="handleUpdateStatus">更新状态</el-button>
        <el-button type="danger" @click="handleRefund">退款</el-button>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, updateOrderStatus, refundOrder } from '@/api/modules/order'
import { formatDateTime, formatMoney, ORDER_STATUS_MAP } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const nextStatus = ref('')

async function loadDetail() {
  loading.value = true
  try {
    detail.value = (await getOrderDetail(route.query.id)) || {}
  } finally {
    loading.value = false
  }
}

async function handleUpdateStatus() {
  await updateOrderStatus({ id: detail.value.id, status: nextStatus.value })
  ElMessage.success('状态已更新')
  loadDetail()
}

async function handleRefund() {
  await ElMessageBox.confirm('确定发起退款？', '提示', { type: 'warning' })
  await refundOrder(detail.value.id)
  ElMessage.success('退款成功')
  loadDetail()
}

onMounted(loadDetail)
</script>
