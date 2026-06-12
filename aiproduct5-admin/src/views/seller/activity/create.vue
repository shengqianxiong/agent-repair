<template>
  <div class="page-card create-page">
    <el-steps :active="currentStep" finish-status="success" align-center class="steps-bar">
      <el-step title="商品信息" />
      <el-step title="AI 文案" />
      <el-step title="返利设置" />
      <el-step title="完成" />
    </el-steps>

    <!-- Step 1: 商品信息 -->
    <div v-show="currentStep === 0" class="step-content">
      <el-form ref="step1Ref" :model="form" :rules="step1Rules" label-width="110px" style="max-width: 720px">
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="商品链接" prop="productUrl">
          <el-input v-model="form.productUrl" placeholder="抖音商品链接" />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="抖音商品 ID" />
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <ImageUpload v-model="form.images" :limit="3" />
          <div class="form-tip">支持上传 1-3 张商品图片，用于 AI 生成文案</div>
        </el-form-item>
        <el-form-item label="抖音跳转链接">
          <el-input v-model="form.douyinUrl" placeholder="用户跳转抖音的链接（可选）" />
        </el-form-item>
      </el-form>
    </div>

    <!-- Step 2: AI 文案 -->
    <div v-show="currentStep === 1" class="step-content">
      <div class="ai-toolbar">
        <el-button type="primary" :loading="aiLoading" @click="handleGenerate">
          <el-icon><MagicStick /></el-icon> AI 生成文案
        </el-button>
        <span class="form-tip">基于商品信息自动生成多条评价文案</span>
      </div>

      <el-empty v-if="!form.copyList.length && !aiLoading" description="点击上方按钮生成文案" />

      <div v-else class="copy-list">
        <div
          v-for="(item, index) in form.copyList"
          :key="item.id || index"
          class="copy-item"
          :class="{ active: form.recommendCopyId === (item.id || index) }"
          @click="selectCopy(item, index)"
        >
          <div class="copy-header">
            <el-radio :model-value="form.recommendCopyId" :label="item.id || index">文案 {{ index + 1 }}</el-radio>
            <el-tag v-if="form.recommendCopyId === (item.id || index)" size="small" type="success">推荐</el-tag>
          </div>
          <p class="copy-text">{{ item.content }}</p>
        </div>
      </div>

      <el-divider>推荐图片</el-divider>
      <div class="recommend-images">
        <div
          v-for="(img, index) in validImages"
          :key="index"
          class="img-item"
          :class="{ active: form.recommendImage === img }"
          @click="form.recommendImage = img"
        >
          <el-image :src="img" fit="cover" class="img-preview" />
          <el-tag v-if="form.recommendImage === img" size="small" type="success" class="img-tag">推荐</el-tag>
        </div>
      </div>
    </div>

    <!-- Step 3: 返利设置 -->
    <div v-show="currentStep === 2" class="step-content">
      <el-form ref="step3Ref" :model="form" :rules="step3Rules" label-width="110px" style="max-width: 520px">
        <el-form-item label="返利金额" prop="rebateAmount">
          <el-input-number v-model="form.rebateAmount" :min="0.01" :precision="2" :step="1" />
          <span class="unit">元</span>
        </el-form-item>
        <el-form-item label="有效期" prop="expireTime">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择活动截止时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="内部备注（可选）" />
        </el-form-item>
      </el-form>
    </div>

    <!-- Step 4: 完成 -->
    <div v-show="currentStep === 3" class="step-content result-step">
      <el-result icon="success" title="活动创建成功" :sub-title="resultTip">
        <template #extra>
          <div v-if="result.qrcodeUrl" class="qrcode-box">
            <el-image :src="result.qrcodeUrl" fit="contain" style="width: 200px; height: 200px" />
            <p>扫码码值：{{ result.qrcodeCode }}</p>
            <el-button type="primary" @click="downloadQr">下载二维码</el-button>
          </div>
          <div class="result-actions">
            <el-button @click="goList">返回列表</el-button>
            <el-button type="primary" @click="goDetail">查看详情</el-button>
            <el-button v-if="!isEdit" @click="createAnother">继续创建</el-button>
          </div>
        </template>
      </el-result>
    </div>

    <!-- 底部操作 -->
    <div v-if="currentStep < 3" class="step-footer">
      <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
      <el-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="currentStep === 2" type="primary" :loading="submitting" @click="handleSubmit">
        {{ isEdit ? '保存修改' : '创建活动' }}
      </el-button>
      <el-button @click="goList">取消</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import ImageUpload from '@/components/ImageUpload.vue'
import {
  createActivity,
  updateActivity,
  getActivityDetail,
  generateCopy
} from '@/api/modules/activity'
import { downloadUrl } from '@/utils/tool'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)
const currentStep = ref(0)
const aiLoading = ref(false)
const submitting = ref(false)
const step1Ref = ref()
const step3Ref = ref()
const result = ref({})

const form = reactive({
  id: null,
  title: '',
  productUrl: '',
  productId: '',
  images: [''],
  douyinUrl: '',
  copyList: [],
  recommendCopyId: null,
  recommendImage: '',
  rebateAmount: 5,
  expireTime: '',
  remark: ''
})

const step1Rules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  productId: [{ required: true, message: '请输入商品 ID', trigger: 'blur' }],
  images: [{ required: true, message: '请上传至少一张图片', trigger: 'change' }]
}

const step3Rules = {
  rebateAmount: [{ required: true, message: '请设置返利金额', trigger: 'blur' }],
  expireTime: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const validImages = computed(() => {
  const imgs = Array.isArray(form.images) ? form.images : [form.images]
  return imgs.filter(Boolean)
})

const resultTip = computed(() => {
  if (isEdit.value) return '活动信息已更新'
  return '二维码已生成，可下载分享给用户扫码评价'
})

async function loadDetail() {
  const id = route.query.id
  if (!id) return
  const data = await getActivityDetail(id)
  if (!data) return
  Object.assign(form, {
    id: data.id,
    title: data.title,
    productUrl: data.productUrl,
    productId: data.productId,
    images: data.images?.length ? data.images : [''],
    douyinUrl: data.douyinUrl,
    copyList: data.copyList || [],
    recommendCopyId: data.recommendCopyId,
    recommendImage: data.recommendImage || data.images?.[0],
    rebateAmount: data.rebateAmount,
    expireTime: data.expireTime,
    remark: data.remark
  })
}

async function handleGenerate() {
  if (!form.productId) {
    ElMessage.warning('请先填写商品 ID')
    return
  }
  aiLoading.value = true
  try {
    const res = await generateCopy({
      productId: form.productId,
      productUrl: form.productUrl,
      images: validImages.value,
      title: form.title
    })
    form.copyList = res?.copyList || res?.list || []
    if (form.copyList.length && form.recommendCopyId == null) {
      form.recommendCopyId = form.copyList[0].id || 0
    }
    if (!form.recommendImage && validImages.value.length) {
      form.recommendImage = validImages.value[0]
    }
    ElMessage.success('文案生成成功')
  } finally {
    aiLoading.value = false
  }
}

function selectCopy(item, index) {
  form.recommendCopyId = item.id ?? index
}

async function nextStep() {
  if (currentStep.value === 0) {
    await step1Ref.value.validate()
    if (!validImages.value.length) {
      ElMessage.warning('请上传至少一张商品图片')
      return
    }
    if (!form.recommendImage) {
      form.recommendImage = validImages.value[0]
    }
  }
  if (currentStep.value === 1 && !form.copyList.length) {
    ElMessage.warning('请先生成或添加评价文案')
    return
  }
  currentStep.value++
}

function prevStep() {
  currentStep.value--
}

function buildPayload() {
  return {
    id: form.id,
    title: form.title,
    productUrl: form.productUrl,
    productId: form.productId,
    images: validImages.value,
    douyinUrl: form.douyinUrl,
    copyList: form.copyList,
    recommendCopyId: form.recommendCopyId,
    recommendImage: form.recommendImage,
    rebateAmount: form.rebateAmount,
    expireTime: form.expireTime,
    remark: form.remark
  }
}

async function handleSubmit() {
  await step3Ref.value.validate()
  submitting.value = true
  try {
    const payload = buildPayload()
    let res
    if (isEdit.value) {
      res = await updateActivity(payload)
      ElMessage.success('保存成功')
      router.push({ path: '/seller/activity/detail', query: { id: form.id } })
    } else {
      res = await createActivity(payload)
      result.value = {
        activityId: res?.id || res?.activityId,
        qrcodeUrl: res?.qrcodeUrl,
        qrcodeCode: res?.qrcodeCode || res?.code
      }
      currentStep.value = 3
    }
  } finally {
    submitting.value = false
  }
}

function downloadQr() {
  if (result.value.qrcodeUrl) {
    downloadUrl(result.value.qrcodeUrl, `activity-${result.value.qrcodeCode}.png`)
  }
}

function goList() {
  router.push('/seller/activity/list')
}

function goDetail() {
  const id = result.value.activityId || form.id
  router.push({ path: '/seller/activity/detail', query: { id } })
}

function createAnother() {
  Object.assign(form, {
    id: null,
    title: '',
    productUrl: '',
    productId: '',
    images: [''],
    douyinUrl: '',
    copyList: [],
    recommendCopyId: null,
    recommendImage: '',
    rebateAmount: 5,
    expireTime: '',
    remark: ''
  })
  result.value = {}
  currentStep.value = 0
}

onMounted(loadDetail)
</script>

<style scoped>
.create-page {
  padding-bottom: 80px;
}

.steps-bar {
  margin-bottom: 32px;
}

.step-content {
  min-height: 360px;
}

.form-tip {
  font-size: var(--dp-font-caption);
  color: var(--dp-text-secondary);
  margin-top: 4px;
}

.ai-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.copy-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.copy-item {
  padding: 16px;
  border: 1px solid var(--dp-border);
  border-radius: var(--dp-radius-md);
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.copy-item.active {
  border-color: var(--dp-primary);
  box-shadow: var(--dp-shadow-hover);
}

.copy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.copy-text {
  margin: 0;
  color: var(--dp-text-regular);
  line-height: 1.6;
  white-space: pre-wrap;
}

.recommend-images {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.img-item {
  position: relative;
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: var(--dp-radius-md);
  overflow: hidden;
}

.img-item.active {
  border-color: var(--dp-primary);
}

.img-preview {
  width: 120px;
  height: 120px;
  display: block;
}

.img-tag {
  position: absolute;
  top: 4px;
  right: 4px;
}

.unit {
  margin-left: 8px;
  color: var(--dp-text-secondary);
}

.result-step {
  text-align: center;
}

.qrcode-box {
  margin-bottom: 24px;
}

.qrcode-box p {
  color: var(--dp-text-secondary);
  font-size: var(--dp-font-body);
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.step-footer {
  position: sticky;
  bottom: 0;
  margin-top: 24px;
  padding: 16px 0;
  background: var(--dp-bg-card);
  border-top: 1px solid var(--dp-border-light);
  display: flex;
  gap: 12px;
}
</style>
