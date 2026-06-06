<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 640px">
      <el-form-item label="游戏名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="图标URL">
        <ImageUpload v-model="form.icon" />
      </el-form-item>
      <el-form-item label="游戏规则">
        <el-input v-model="form.rules" type="textarea" :rows="5" />
      </el-form-item>
      <el-form-item label="奖励积分" prop="rewardPoints">
        <el-input-number v-model="form.rewardPoints" :min="0" />
      </el-form-item>
      <el-form-item label="状态">
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
import { getGameList, saveGame, updateGame } from '@/api/modules/game'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({
  id: null,
  name: '',
  icon: '',
  rules: '',
  rewardPoints: 10,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入游戏名称', trigger: 'blur' }],
  rewardPoints: [{ required: true, message: '请输入奖励积分', trigger: 'blur' }]
}

async function loadDetail() {
  const id = route.query.id
  if (!id) return
  const res = await getGameList({ page: 1, pageSize: 200 })
  const found = (res?.list || res || []).find((item) => String(item.id) === String(id))
  if (found) Object.assign(form, found)
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) {
      await updateGame({ ...form })
    } else {
      await saveGame({ ...form })
    }
    ElMessage.success('保存成功')
    goBack()
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push('/game/list')
}

onMounted(loadDetail)
</script>
