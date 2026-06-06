<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="login-title">Login 管理后台</h1>
      <p class="login-subtitle">管理员登录后进入账号管理</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleSubmit">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入账号"
            prefix-icon="User"
            clearable
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="submitting" @click="handleSubmit">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '@/api/modules/auth'
import { setAuth } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = await adminLogin({ ...form })
    setAuth(data.token, {
      id: data.accountId ?? data.id,
      username: data.username
    })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/account/list'
    router.replace(String(redirect))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1d1e2c 0%, #2b5876 100%);
}
.login-card {
  width: 400px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
}
.login-title {
  margin: 0 0 8px;
  text-align: center;
  font-size: 24px;
  color: #303133;
}
.login-subtitle {
  margin: 0 0 28px;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
.login-btn {
  width: 100%;
}
</style>
