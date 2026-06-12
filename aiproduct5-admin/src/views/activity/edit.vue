<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 640px">
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="封面图" prop="image">
        <ImageUpload v-model="form.image" />
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tag" placeholder="请选择" style="width: 100%">
          <el-option label="限时优惠" value="限时优惠" />
          <el-option label="热门" value="热门" />
          <el-option label="免费" value="免费" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动价" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="原价">
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
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="上架状态">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
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
const submitting = ref(false)
const form = reactive({
  id: null,
  title: '',
  image: '',
  tag: '限时优惠',
  price: 0,
  originalPrice: 0,
  description: '',
  status: 1,
  timeRange: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入活动价', trigger: 'blur' }]
}

async function loadDetail() {
  const id = route.query.id
  if (!id) return
  const res = await getActivityList({ page: 1, pageSize: 1, id })
  const data = res?.list?.[0]
  if (!data && id) {
    const listRes = await getActivityList({ page: 1, pageSize: 200 })
    const found = (listRes?.list || []).find((item) => String(item.id) === String(id))
    if (found) Object.assign(form, found, { timeRange: [found.startTime, found.endTime] })
    return
  }
  if (data) {
    Object.assign(form, data, { timeRange: [data.startTime, data.endTime] })
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  const payload = {
    ...form,
    startTime: form.timeRange?.[0],
    endTime: form.timeRange?.[1]
  }
  delete payload.timeRange
  try {
    if (form.id) {
      await updateActivity(payload)
    } else {
      await saveActivity(payload)
    }
    ElMessage.success('保存成功')
    goBack()
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push('/activity/list')
}

onMounted(loadDetail)
</script>
