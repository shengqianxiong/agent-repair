<template>
  <div class="page-container">
    <el-card shadow="never" class="form-page">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="系统" value="系统" />
            <el-option label="活动" value="活动" />
            <el-option label="订单" value="订单" />
          </el-select>
        </el-form-item>
        <el-form-item label="推送范围" prop="scope">
          <el-radio-group v-model="form.scope">
            <el-radio value="all">全体用户</el-radio>
            <el-radio value="user">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.scope === 'user'" label="用户ID" prop="userId">
          <el-input-number v-model="form.userId" :min="1" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="定时发送">
          <el-date-picker
            v-model="form.scheduledTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="留空则立即发送"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">推送</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pushNotification } from '@/api/modules/notification'

const router = useRouter()
const formRef = ref()
const saving = ref(false)

const form = reactive({
  title: '',
  type: '系统',
  scope: 'all',
  userId: null,
  content: '',
  scheduledTime: '',
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

async function handleSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    await pushNotification({ ...form })
    ElMessage.success('通知已推送')
    router.push('/notification/list')
  } finally {
    saving.value = false
  }
}
</script>
