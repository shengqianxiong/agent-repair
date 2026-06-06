<template>
  <div class="page-container">
    <el-card shadow="never">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="预约日期">
          <el-date-picker v-model="query.bookingDate" type="date" value-format="YYYY-MM-DD" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已到店" value="已到店" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bookingDate" label="日期" width="120" />
        <el-table-column prop="timeSlot" label="时段" width="100" />
        <el-table-column prop="guestCount" label="人数" width="80" />
        <el-table-column prop="seatType" label="席位" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="BOOKING_STATUS_MAP[row.status]">{{ row.status }}</el-tag>
          </template>
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
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getBookingList } from '@/api/modules/booking'
import { BOOKING_STATUS_MAP } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ bookingDate: '', status: '', page: 1, pageSize: 10 })

async function loadList() {
  loading.value = true
  try {
    const data = await getBookingList({ ...query })
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.bookingDate = ''
  query.status = ''
  query.page = 1
  loadList()
}

function goDetail(id) {
  router.push({ path: '/booking/detail', query: { id } })
}

onMounted(loadList)
</script>
