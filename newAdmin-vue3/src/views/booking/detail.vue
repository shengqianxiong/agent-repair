<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <el-descriptions title="预约信息" :column="2" border>
        <el-descriptions-item label="预约日期">{{ detail.bookingDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ detail.timeSlot }}</el-descriptions-item>
        <el-descriptions-item label="人数">{{ detail.guestCount }}</el-descriptions-item>
        <el-descriptions-item label="席位类型">{{ detail.seatType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="BOOKING_STATUS_MAP[detail.status]">{{ detail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-form :model="assignForm" label-width="100px" style="margin-top: 24px; max-width: 480px">
        <el-form-item label="分配桌位">
          <el-select v-model="assignForm.tableId" placeholder="选择桌位" filterable style="width: 100%">
            <el-option
              v-for="item in tables"
              :key="item.id"
              :label="`${item.tableNo} (${item.area})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAssign">分配桌位</el-button>
          <el-button type="success" @click="handleConfirm">确认预约</el-button>
          <el-button type="danger" @click="handleCancel">取消预约</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getBookingDetail,
  confirmBooking,
  cancelBooking,
  assignTable,
} from '@/api/modules/booking'
import { getTableList } from '@/api/modules/table'
import { formatDateTime, BOOKING_STATUS_MAP } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const tables = ref([])

const assignForm = reactive({ bookingId: route.query.id, tableId: null })

async function loadDetail() {
  loading.value = true
  try {
    detail.value = (await getBookingDetail(route.query.id)) || {}
    assignForm.tableId = detail.value.tableId || null
  } finally {
    loading.value = false
  }
}

async function loadTables() {
  const data = await getTableList({ page: 1, pageSize: 200 })
  tables.value = data?.list || data || []
}

async function handleAssign() {
  if (!assignForm.tableId) {
    ElMessage.warning('请选择桌位')
    return
  }
  await assignTable({ bookingId: detail.value.id, tableId: assignForm.tableId })
  ElMessage.success('桌位已分配')
  loadDetail()
}

async function handleConfirm() {
  await confirmBooking(detail.value.id)
  ElMessage.success('预约已确认')
  loadDetail()
}

async function handleCancel() {
  await ElMessageBox.confirm('确定取消该预约？', '提示', { type: 'warning' })
  await cancelBooking(detail.value.id)
  ElMessage.success('预约已取消')
  loadDetail()
}

onMounted(async () => {
  await loadTables()
  await loadDetail()
})
</script>
