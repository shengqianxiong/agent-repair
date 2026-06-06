<template>
  <div class="page-container">
    <el-card shadow="never" class="form-page">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" v-loading="loading">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="封面图" prop="image">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="标签" prop="tag">
          <el-select v-model="form.tag" placeholder="选择标签">
            <el-option label="限时优惠" value="限时优惠" />
            <el-option label="热门" value="热门" />
            <el-option label="免费" value="免费" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="上架状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import { getActivityList, saveActivity, updateActivity } from '@/api/modules/activity'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const isEdit = !!route.query.id

const form = reactive({
  id: null,
  title: '',
  image: '',
  tag: '',
  price: 0,
  originalPrice: 0,
  description: '',
  status: 1,
  timeRange: [],
})

const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入活动价', trigger: 'blur' }],
}

async function loadDetail() {
  if (!isEdit) return
  loading.value = true
  try {
    const data = await getActivityList({ page: 1, pageSize: 1, id: route.query.id })
    const row = data?.list?.find((item) => String(item.id) === String(route.query.id))
    if (row) {
      Object.assign(form, row)
      if (row.startTime && row.endTime) {
        form.timeRange = [row.startTime, row.endTime]
      }
    }
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      ...form,
      startTime: form.timeRange?.[0],
      endTime: form.timeRange?.[1],
    }
    if (isEdit) {
      await updateActivity(payload)
    } else {
      await saveActivity(payload)
    }
    ElMessage.success('保存成功')
    router.push('/activity/list')
  } finally {
    saving.value = false
  }
}

onMounted(loadDetail)
</script>
