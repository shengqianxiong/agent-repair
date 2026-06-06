<template>
  <div class="page-container">
    <el-card shadow="never" class="form-page">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" v-loading="loading">
        <el-form-item label="游戏名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <ImageUpload v-model="form.icon" />
        </el-form-item>
        <el-form-item label="游戏规则" prop="rules">
          <el-input v-model="form.rules" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="奖励积分" prop="rewardPoints">
          <el-input-number v-model="form.rewardPoints" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
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
import { getGameList, saveGame, updateGame } from '@/api/modules/game'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const isEdit = !!route.query.id

const form = reactive({
  id: null,
  name: '',
  icon: '',
  rules: '',
  rewardPoints: 0,
  status: 1,
})

const rules = {
  name: [{ required: true, message: '请输入游戏名称', trigger: 'blur' }],
}

async function loadDetail() {
  if (!isEdit) return
  loading.value = true
  try {
    const data = await getGameList({ page: 1, pageSize: 100 })
    const list = data?.list || data || []
    const row = list.find((item) => String(item.id) === String(route.query.id))
    if (row) Object.assign(form, row)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit) {
      await updateGame({ ...form })
    } else {
      await saveGame({ ...form })
    }
    ElMessage.success('保存成功')
    router.push('/game/list')
  } finally {
    saving.value = false
  }
}

onMounted(loadDetail)
</script>
