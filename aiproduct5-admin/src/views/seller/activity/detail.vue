<template>
  <div class="detail-page" v-loading="loading">
    <div class="page-card header-card">
      <div class="header-main">
        <div class="header-info">
          <h2>{{ detail.title || '活动详情' }}</h2>
          <div class="meta-row">
            <el-tag :type="activityStatusMap[detail.status]?.type">
              {{ activityStatusMap[detail.status]?.label || '-' }}
            </el-tag>
            <span>商品 ID：{{ detail.productId || '-' }}</span>
            <span>返利：¥{{ formatMoney(detail.rebateAmount) }}</span>
            <span>有效期：{{ formatDateTime(detail.expireTime) }}</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button @click="goBack">返回列表</el-button>
          <el-button type="primary" @click="goEdit">编辑活动</el-button>
          <el-button type="primary" plain @click="toggleStatus">
            {{ detail.status === 1 ? '下架活动' : '上架活动' }}
          </el-button>
        </div>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :span="16">
        <!-- 数据统计 -->
        <div class="page-card section-card">
          <h3 class="section-title">活动数据</h3>
          <el-row :gutter="16">
            <el-col :span="6" v-for="item in statItems" :key="item.key">
              <div class="mini-stat">
                <div class="mini-stat-value">{{ item.value }}</div>
                <div class="mini-stat-label">{{ item.label }}</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 文案预览 -->
        <div class="page-card section-card">
          <h3 class="section-title">推荐文案</h3>
          <div v-if="detail.copyList?.length" class="copy-preview">
            <div
              v-for="(item, index) in detail.copyList"
              :key="item.id || index"
              class="copy-block"
              :class="{ recommend: detail.recommendCopyId === (item.id ?? index) }"
            >
              <div class="copy-block-header">
                <span>文案 {{ index + 1 }}</span>
                <el-tag v-if="detail.recommendCopyId === (item.id ?? index)" size="small" type="success">推荐</el-tag>
              </div>
              <p>{{ item.content }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无文案" />
        </div>

        <!-- 图片预览 -->
        <div class="page-card section-card">
          <h3 class="section-title">商品图片</h3>
          <div class="image-grid">
            <div v-for="(img, index) in detail.images || []" :key="index" class="image-item">
              <el-image :src="img" fit="cover" :preview-src-list="detail.images" />
              <el-tag v-if="detail.recommendImage === img" size="small" type="success" class="rec-tag">推荐</el-tag>
            </div>
          </div>
          <el-empty v-if="!detail.images?.length" description="暂无图片" />
        </div>
      </el-col>

      <el-col :span="8">
        <!-- 二维码 -->
        <div class="page-card section-card qrcode-card">
          <h3 class="section-title">活动二维码</h3>
          <div class="qrcode-wrap">
            <el-image
              v-if="detail.qrcodeUrl"
              :src="detail.qrcodeUrl"
              fit="contain"
              style="width: 200px; height: 200px"
            />
            <el-empty v-else description="暂无二维码" />
          </div>
          <p v-if="detail.qrcodeCode" class="code-text">码值：{{ detail.qrcodeCode }}</p>
          <el-button v-if="detail.qrcodeUrl" type="primary" style="width: 100%" @click="downloadQr">
            下载二维码
          </el-button>
        </div>

        <!-- 基础信息 -->
        <div class="page-card section-card">
          <h3 class="section-title">基础信息</h3>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="活动 ID">{{ detail.id }}</el-descriptions-item>
            <el-descriptions-item label="商品链接">
              <el-link v-if="detail.productUrl" :href="detail.productUrl" target="_blank" type="primary">
                查看链接
              </el-link>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="抖音跳转">
              <el-link v-if="detail.douyinUrl" :href="detail.douyinUrl" target="_blank" type="primary">
                跳转链接
              </el-link>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(detail.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityDetail, getActivityStats, updateActivityStatus } from '@/api/modules/activity'
import { activityStatusMap, formatMoney, formatDateTime, downloadUrl } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref({})
const stats = ref({})

const statItems = computed(() => [
  { key: 'scanCount', label: '扫码量', value: stats.value.scanCount ?? detail.value.scanCount ?? 0 },
  { key: 'submitCount', label: '评价提交', value: stats.value.submitCount ?? detail.value.submitCount ?? 0 },
  { key: 'verifyCount', label: '核销量', value: stats.value.verifyCount ?? detail.value.verifyCount ?? 0 },
  { key: 'rebateAmount', label: '返利支出', value: `¥${formatMoney(stats.value.rebateAmount ?? detail.value.totalRebate ?? 0)}` }
])

async function loadData() {
  const id = route.query.id
  if (!id) {
    ElMessage.error('缺少活动 ID')
    goBack()
    return
  }
  loading.value = true
  try {
    const [detailData, statsData] = await Promise.all([
      getActivityDetail(id),
      getActivityStats(id).catch(() => ({}))
    ])
    detail.value = detailData || {}
    stats.value = statsData || {}
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/seller/activity/list')
}

function goEdit() {
  router.push({ path: '/seller/activity/edit', query: { id: detail.value.id } })
}

async function toggleStatus() {
  const newStatus = detail.value.status === 1 ? 2 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确认${action}该活动？`, '提示', { type: 'warning' })
  await updateActivityStatus(detail.value.id, newStatus)
  ElMessage.success(`${action}成功`)
  loadData()
}

function downloadQr() {
  if (detail.value.qrcodeUrl) {
    downloadUrl(detail.value.qrcodeUrl, `activity-${detail.value.qrcodeCode || detail.value.id}.png`)
  }
}

onMounted(loadData)
</script>

<style scoped>
.header-card {
  margin-bottom: 16px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-info h2 {
  margin: 0 0 12px;
  font-size: 20px;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: var(--dp-text-regular);
  font-size: var(--dp-font-body);
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.section-card {
  margin-bottom: 16px;
}

.section-title {
  margin: 0 0 16px;
  font-size: var(--dp-font-subtitle);
  font-weight: 600;
}

.mini-stat {
  text-align: center;
  padding: 16px;
  background: var(--dp-border-light);
  border-radius: var(--dp-radius-md);
}

.mini-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--dp-primary);
}

.mini-stat-label {
  margin-top: 4px;
  font-size: var(--dp-font-caption);
  color: var(--dp-text-secondary);
}

.copy-block {
  padding: 12px 16px;
  border: 1px solid var(--dp-border);
  border-radius: var(--dp-radius-md);
  margin-bottom: 12px;
}

.copy-block.recommend {
  border-color: var(--dp-primary);
  background: rgba(254, 44, 85, 0.04);
}

.copy-block-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: 500;
}

.copy-block p {
  margin: 0;
  color: var(--dp-text-regular);
  line-height: 1.6;
  white-space: pre-wrap;
}

.image-grid {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.image-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: var(--dp-radius-md);
  overflow: hidden;
}

.image-item :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.rec-tag {
  position: absolute;
  top: 4px;
  right: 4px;
}

.qrcode-card {
  text-align: center;
}

.qrcode-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

.code-text {
  color: var(--dp-text-secondary);
  font-size: var(--dp-font-body);
  margin-bottom: 16px;
}
</style>
