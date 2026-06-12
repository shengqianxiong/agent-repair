<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" label-width="120px" style="max-width: 640px" v-loading="loading">
      <el-form-item label="排行维度">
        <el-checkbox-group v-model="form.rankTypes">
          <el-checkbox label="消费" value="消费" />
          <el-checkbox label="积分" value="积分" />
          <el-checkbox label="游戏" value="游戏" />
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="统计周期">
        <el-radio-group v-model="form.period">
          <el-radio label="日">日榜</el-radio>
          <el-radio label="周">周榜</el-radio>
          <el-radio label="月">月榜</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="Top1 奖励积分">
        <el-input-number v-model="form.rewardTop1" :min="0" />
      </el-form-item>
      <el-form-item label="Top3 奖励积分">
        <el-input-number v-model="form.rewardTop3" :min="0" />
      </el-form-item>
      <el-form-item label="Top10 奖励积分">
        <el-input-number v-model="form.rewardTop10" :min="0" />
      </el-form-item>
      <el-form-item label="规则说明">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存配置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRankConfig, updateRankConfig } from '@/api/modules/rank'

const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const form = reactive({
  rankTypes: ['消费', '积分', '游戏'],
  period: '周',
  rewardTop1: 100,
  rewardTop3: 50,
  rewardTop10: 20,
  description: ''
})

async function loadConfig() {
  loading.value = true
  try {
    const data = await getRankConfig()
    if (data) Object.assign(form, data)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    await updateRankConfig({ ...form })
    ElMessage.success('配置已保存')
  } finally {
    submitting.value = false
  }
}

onMounted(loadConfig)
</script>
