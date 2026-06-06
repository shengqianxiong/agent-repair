<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 560px">
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入账号" />
      </el-form-item>
      <el-form-item :label="isEdit ? '重置密码' : '初始密码'" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          show-password
          :placeholder="isEdit ? '留空则不修改密码' : '请输入初始密码'"
        />
      </el-form-item>
      <el-form-item v-if="isEdit" label="账号状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="ACCOUNT_STATUS.NORMAL">正常</el-radio>
          <el-radio :value="ACCOUNT_STATUS.DISABLED">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAccountDetail, saveAccount, updateAccount } from '@/api/modules/account'
import { ACCOUNT_STATUS } from '@/utils/tool'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const isEdit = computed(() => route.name === 'AccountEdit')
const form = reactive({
  id: null,
  username: '',
  password: '',
  status: ACCOUNT_STATUS.NORMAL
})

const rules = computed(() => ({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: isEdit.value
    ? []
    : [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  status: isEdit.value
    ? [{ required: true, message: '请选择账号状态', trigger: 'change' }]
    : []
}))

async function loadDetail() {
  const id = route.query.id
  if (!id) return
  const data = await getAccountDetail(id)
  form.id = data.id
  form.username = data.username
  form.password = ''
  form.status = data.status
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      const payload = {
        id: form.id,
        status: form.status
      }
      if (form.password) {
        payload.password = form.password
      }
      await updateAccount(payload)
    } else {
      await saveAccount({
        username: form.username,
        password: form.password
      })
    }
    ElMessage.success('保存成功')
    goBack()
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push('/account/list')
}

onMounted(loadDetail)
</script>
