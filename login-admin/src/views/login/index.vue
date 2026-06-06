<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">管理员登录</h2>
      <p class="login-subtitle">登录后可进行账号增删改查管理</p>
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
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '@/api/modules/auth'
import { setAuthSession } from '@/utils/auth'

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
    setAuthSession({
      token: data.token,
      username: data.username,
      accountId: data.accountId
    })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/account/list'
    router.replace(redirect)
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
  background: linear-gradient(135deg, #1d1e2c 0%, #2d3a5c 100%);
}
.login-card {
  width: 400px;
  padding: 12px 8px 4px;
}
.login-title {
  margin: 0 0 8px;
  text-align: center;
  font-size: 22px;
  color: #303133;
}
.login-subtitle {
  margin: 0 0 24px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}
.login-btn {
  width: 100%;
}
</style>
