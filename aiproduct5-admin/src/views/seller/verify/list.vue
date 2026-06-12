<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="活动名称/任务ID" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="审核状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="(item, key) in verifyStatusMap" :key="key" :label="item.label" :value="Number(key)" />
        </el-select>
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker
          v-model="query.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="YYYY-MM-DD"
          style="width: 260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span class="toolbar-title">核销审核列表</span>
      <el-tag type="warning">待审核 {{ pendingCount }} 条</el-tag>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe empty-text="暂无审核记录">
      <el-table-column prop="id" label="审核ID" width="80" />
      <el-table-column prop="taskId" label="任务ID" width="90" />
      <el-table-column prop="activityTitle" label="活动名称" min-width="120" show-overflow-tooltip />
      <el-table-column label="评价截图" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.screenshotUrl"
            :src="row.screenshotUrl"
            :preview-src-list="[row.screenshotUrl]"
            fit="cover"
            style="width: 56px; height: 56px; border-radius: 4px"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="rebateAmount" label="返利金额" width="100">
        <template #default="{ row }">¥{{ formatMoney(row.rebateAmount) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="verifyStatusMap[row.status]?.type">
            {{ verifyStatusMap[row.status]?.label || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="auditTime" label="审核时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.auditTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button link type="success" @click="openApprove(row)">通过</el-button>
            <el-button link type="danger" @click="openReject(row)">拒绝</el-button>
          </template>
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadList"
        @size-change="handleSearch"
      />
    </div>

    <!-- 审核详情弹窗 -->
    <el-dialog v-model="detailVisible" title="审核详情" width="560px" destroy-on-close>
      <el-descriptions v-if="currentRow" :column="1" border>
        <el-descriptions-item label="活动">{{ currentRow.activityTitle }}</el-descriptions-item>
        <el-descriptions-item label="返利金额">¥{{ formatMoney(currentRow.rebateAmount) }}</el-descriptions-item>
        <el-descriptions-item label="用户备注">{{ currentRow.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentRow.contact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价截图">
          <el-image
            v-if="currentRow.screenshotUrl"
            :src="currentRow.screenshotUrl"
            :preview-src-list="[currentRow.screenshotUrl]"
            fit="contain"
            style="max-width: 300px; max-height: 200px"
          />
        </el-descriptions-item>
        <el-descriptions-item v-if="currentRow.rejectReason" label="拒绝原因">
          <span class="reject-reason">{{ currentRow.rejectReason }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <template v-if="currentRow?.status === 0">
          <el-button type="success" @click="openApprove(currentRow)">通过</el-button>
          <el-button type="danger" @click="openReject(currentRow)">拒绝</el-button>
        </template>
      </template>
    </el-dialog>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectVisible" title="拒绝审核" width="480px" destroy-on-close>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="90px">
        <el-form-item label="拒绝原因" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请填写拒绝原因，用户可见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitLoading" @click="submitReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 通过确认弹窗 -->
    <el-dialog v-model="approveVisible" title="通过审核" width="420px" destroy-on-close>
      <p>确认通过该评价审核？返利 ¥{{ formatMoney(currentRow?.rebateAmount) }} 将发放给用户。</p>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="success" :loading="submitLoading" @click="submitApprove">确认通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getVerifyPage, approveVerify, rejectVerify } from '@/api/modules/verify'
import { verifyStatusMap, formatMoney, formatDateTime } from '@/utils/tool'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pendingCount = ref(0)
const detailVisible = ref(false)
const rejectVisible = ref(false)
const approveVisible = ref(false)
const currentRow = ref(null)
const rejectFormRef = ref()

const query = reactive({
  keyword: '',
  status: null,
  dateRange: [],
  pageNum: 1,
  pageSize: 10,
  orderBy: 'createTime desc'
})

const rejectForm = reactive({
  id: null,
  rejectReason: ''
})

const rejectRules = {
  rejectReason: [{ required: true, message: '请填写拒绝原因', trigger: 'blur' }]
}

function buildParams() {
  const params = {
    keyword: query.keyword || undefined,
    status: query.status ?? undefined,
    pageNum: query.pageNum,
    pageSize: query.pageSize,
    orderBy: query.orderBy
  }
  if (query.dateRange?.length === 2) {
    params.startDate = query.dateRange[0]
    params.endDate = query.dateRange[1]
  }
  return params
}

async function loadList() {
  loading.value = true
  try {
    const res = await getVerifyPage(buildParams())
    tableData.value = res.list || []
    total.value = res.total || 0
    pendingCount.value = res.pendingCount ?? tableData.value.filter((r) => r.status === 0).length
  } catch {
    tableData.value = []
    total.value = 0
    pendingCount.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNum = 1
  loadList()
}

function resetQuery() {
  query.keyword = ''
  query.status = null
  query.dateRange = []
  query.pageNum = 1
  loadList()
}

function openDetail(row) {
  currentRow.value = row
  detailVisible.value = true
}

function openApprove(row) {
  currentRow.value = row
  detailVisible.value = false
  approveVisible.value = true
}

function openReject(row) {
  currentRow.value = row
  detailVisible.value = false
  rejectForm.id = row.id
  rejectForm.rejectReason = ''
  rejectVisible.value = true
}

async function submitApprove() {
  submitLoading.value = true
  try {
    await approveVerify({ id: currentRow.value.id })
    ElMessage.success('审核已通过')
    approveVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

async function submitReject() {
  await rejectFormRef.value.validate()
  submitLoading.value = true
  try {
    await rejectVerify({ id: rejectForm.id, rejectReason: rejectForm.rejectReason })
    ElMessage.success('已拒绝该评价')
    rejectVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.reject-reason {
  color: var(--dp-danger);
}
</style>
