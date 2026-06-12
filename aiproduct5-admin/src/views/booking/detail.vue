<template>
  <div class="page-card" v-loading="loading">
    <el-descriptions title="预约信息" :column="2" border>
      <el-descriptions-item label="预约ID">{{ detail.id }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="bookingStatusMap[detail.status]">{{ detail.status }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
      <el-descriptions-item label="预约日期">{{ detail.bookingDate }}</el-descriptions-item>
      <el-descriptions-item label="时段">{{ detail.timeSlot }}</el-descriptions-item>
      <el-descriptions-item label="人数">{{ detail.guestCount }}</el-descriptions-item>
      <el-descriptions-item label="席位类型">{{ detail.seatType }}</el-descriptions-item>
      <el-descriptions-item label="分配桌位">{{ detail.tableId || '未分配' }}</el-descriptions-item>
      <el-descriptions-item label="预约时间" :span="2">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <el-form :inline="true">
      <el-form-item label="分配桌位">
        <el-select v-model="assignForm.tableId" placeholder="选择桌位" style="width: 160px">
          <el-option v-for="t in tables" :key="t.id" :label="`${t.tableNo} (${t.area})`" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleAssign">分配桌位</el-button>
        <el-button type="success" @click="handleConfirm">确认预约</el-button>
        <el-button type="danger" @click="handleCancel">取消预约</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assignTable, cancelBooking, confirmBooking, getBookingDetail } from '@/api/modules/booking'
import { getTableList } from '@/api/modules/table'
import { formatDateTime, bookingStatusMap } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const tables = ref([])
const assignForm = reactive({ bookingId: null, tableId: null })

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getBookingDetail(route.query.id) || {}
    assignForm.bookingId = detail.value.id
    assignForm.tableId = detail.value.tableId
  } finally {
    loading.value = false
  }
}

async function loadTables() {
  const res = await getTableList({ page: 1, pageSize: 200 })
  tables.value = res?.list || res || []
}

async function handleAssign() {
  if (!assignForm.tableId) {
    ElMessage.warning('请选择桌位')
    return
  }
  await assignTable({ ...assignForm })
  ElMessage.success('桌位已分配')
  loadDetail()
}

async function handleConfirm() {
  await confirmBooking(detail.value.id)
  ElMessage.success('预约已确认')
  loadDetail()
}

async function handleCancel() {
  await ElMessageBox.confirm('确认取消该预约？', '提示', { type: 'warning' })
  await cancelBooking(detail.value.id)
  ElMessage.success('预约已取消')
  loadDetail()
}

function goBack() {
  router.push('/booking/list')
}

onMounted(() => {
  loadDetail()
  loadTables()
})
</script>
