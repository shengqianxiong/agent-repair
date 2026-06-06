<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 520px">
      <el-form-item label="账号" prop="username">
        <el-input
          v-model="form.username"
          :disabled="isEdit"
          placeholder="请输入登录账号"
          maxlength="50"
        />
      </el-form-item>

      <el-form-item :label="isEdit ? '重置密码' : '初始密码'" :prop="isEdit ? '' : 'password'">
        <el-input
          v-model="form.password"
          type="password"
          :placeholder="isEdit ? '留空则不修改密码' : '请输入初始密码'"
          show-password
          maxlength="50"
        />
      </el-form-item>

      <el-form-item v-if="isEdit" label="账号状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="ACCOUNT_STATUS.NORMAL">正常</el-radio>
          <el-radio :value="ACCOUNT_STATUS.DISABLED">禁用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '新增账号' }}
        </el-button>
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
const accountId = computed(() => route.query.id)
const isEdit = computed(() => Boolean(accountId.value))

const form = reactive({
  id: null,
  username: '',
  password: '',
  status: ACCOUNT_STATUS.NORMAL
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  status: [{ required: true, message: '请选择账号状态', trigger: 'change' }]
}

/** 编辑模式下加载账号详情，账号名称不可修改 */
async function loadDetail() {
  if (!isEdit.value) return
  const detail = await getAccountDetail(accountId.value)
  form.id = detail.id
  form.username = detail.username
  form.status = detail.status
  form.password = ''
}

async function handleSubmit() {
  await formRef.value.validate()

  submitting.value = true
  try {
    if (isEdit.value) {
      const payload = { id: form.id, status: form.status }
      if (form.password) {
        payload.password = form.password
      }
      await updateAccount(payload)
      ElMessage.success('账号已更新')
    } else {
      await saveAccount({
        username: form.username,
        password: form.password
      })
      ElMessage.success('账号新增成功')
    }
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
