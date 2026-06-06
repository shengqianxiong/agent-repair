<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 520px">
      <el-form-item label="账号" prop="account">
        <el-input
          v-model="form.account"
          :disabled="isEdit"
          placeholder="请输入账号"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <el-form-item :label="isEdit ? '重置密码' : '初始密码'" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          :placeholder="isEdit ? '不修改请留空' : '请输入初始密码'"
          show-password
          clearable
        />
      </el-form-item>

      <el-form-item v-if="isEdit" label="账号状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">禁用</el-radio>
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
import { createAccount, getAccountList, updateAccount } from '@/api/modules/account'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const isEdit = computed(() => Boolean(route.query.id))

const form = reactive({
  id: null,
  account: '',
  password: '',
  status: 1
})

const rules = computed(() => ({
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: isEdit.value
    ? []
    : [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
}))

/**
 * 编辑模式下从列表接口回填账号信息（服务端无单独详情接口时复用列表查询）
 */
async function loadDetail() {
  const id = Number(route.query.id)
  if (!id) return
  const res = await getAccountList({ page: 1, pageSize: 1000 })
  const target = (res.list || []).find((item) => item.id === id)
  if (!target) {
    ElMessage.error('账号不存在')
    goBack()
    return
  }
  form.id = target.id
  form.account = target.account
  form.status = target.status
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
      await createAccount({
        account: form.account,
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
