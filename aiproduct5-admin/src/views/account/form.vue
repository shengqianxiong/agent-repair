<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 480px">
      <el-form-item label="账号">
        <el-input v-model="form.account" disabled />
      </el-form-item>
      <el-form-item label="重置密码" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="留空则不修改密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="账号状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
        <el-button @click="handleBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAccountDetail, updateAccount } from '@/api/modules/account'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  id: null,
  account: '',
  password: '',
  status: 1
})

const rules = {
  status: [{ required: true, message: '请选择账号状态', trigger: 'change' }]
}

async function loadDetail() {
  const id = route.query.id
  if (!id) {
    ElMessage.error('缺少账号ID')
    handleBack()
    return
  }
  const data = await getAccountDetail(id)
  form.id = data.id
  form.account = data.account
  form.status = data.status
  form.password = ''
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await updateAccount({
      id: form.id,
      password: form.password || undefined,
      status: form.status
    })
    ElMessage.success('保存成功')
    handleBack()
  } finally {
    loading.value = false
  }
}

function handleBack() {
  router.push('/account/list')
}

onMounted(() => {
  loadDetail()
})
</script>
