<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="活动名称/商品ID" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="生成状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="(item, key) in aiStatusMap" :key="key" :label="item.label" :value="Number(key)" />
        </el-select>
      </el-form-item>
      <el-form-item label="生成时间">
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
      <span class="toolbar-title">AI 生成记录</span>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe empty-text="暂无 AI 生成记录">
      <el-table-column prop="id" label="记录ID" width="80" />
      <el-table-column prop="activityTitle" label="关联活动" min-width="120" show-overflow-tooltip />
      <el-table-column prop="productId" label="商品ID" width="120" show-overflow-tooltip />
      <el-table-column prop="copyCount" label="文案数" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="aiStatusMap[row.status]?.type">
            {{ aiStatusMap[row.status]?.label || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="生成时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="failReason" label="失败原因" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.status === 2" class="fail-reason">{{ row.failReason || '-' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">查看</el-button>
          <el-button v-if="row.status === 2" link type="warning" @click="handleRetry(row)">重试</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="AI 生成详情" width="640px" destroy-on-close>
      <template v-if="detailData">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
          <el-descriptions-item label="活动">{{ detailData.activityTitle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="商品ID">{{ detailData.productId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="aiStatusMap[detailData.status]?.type" size="small">
              {{ aiStatusMap[detailData.status]?.label }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detailData.images?.length" class="detail-section">
          <h4>输入图片</h4>
          <div class="image-row">
            <el-image
              v-for="(img, i) in detailData.images"
              :key="i"
              :src="img"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 6px"
            />
          </div>
        </div>

        <div v-if="detailData.copyList?.length" class="detail-section">
          <h4>生成文案</h4>
          <div v-for="(item, index) in detailData.copyList" :key="index" class="copy-item">
            <span class="copy-index">文案 {{ index + 1 }}</span>
            <p>{{ item.content }}</p>
          </div>
        </div>

        <el-alert v-if="detailData.status === 2" type="error" :title="detailData.failReason || '生成失败'" show-icon />
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailData?.status === 2" type="warning" :loading="retryLoading" @click="handleRetry(detailData)">
          重新生成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAiHistoryPage, getAiHistoryDetail, retryAiGenerate } from '@/api/modules/ai'
import { aiStatusMap, formatDateTime } from '@/utils/tool'

const loading = ref(false)
const retryLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref(null)

const query = reactive({
  keyword: '',
  status: null,
  dateRange: [],
  pageNum: 1,
  pageSize: 10,
  orderBy: 'createTime desc'
})

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
    const res = await getAiHistoryPage(buildParams())
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch {
    tableData.value = []
    total.value = 0
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

async function openDetail(row) {
  try {
    detailData.value = await getAiHistoryDetail(row.id)
  } catch {
    detailData.value = row
  }
  detailVisible.value = true
}

async function handleRetry(row) {
  retryLoading.value = true
  try {
    await retryAiGenerate({ id: row.id })
    ElMessage.success('已提交重新生成')
    detailVisible.value = false
    loadList()
  } finally {
    retryLoading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.fail-reason {
  color: var(--dp-danger);
}

.detail-section {
  margin-bottom: 16px;
}

.detail-section h4 {
  margin: 0 0 8px;
  font-size: var(--dp-font-body);
  color: var(--dp-text-primary);
}

.image-row {
  display: flex;
  gap: 8px;
}

.copy-item {
  padding: 10px 12px;
  background: var(--dp-border-light);
  border-radius: var(--dp-radius-sm);
  margin-bottom: 8px;
}

.copy-index {
  font-weight: 500;
  font-size: var(--dp-font-caption);
  color: var(--dp-text-secondary);
}

.copy-item p {
  margin: 6px 0 0;
  line-height: 1.6;
  color: var(--dp-text-regular);
}
</style>
