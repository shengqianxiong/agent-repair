<template>
  <div class="page-container">
    <el-card shadow="never" class="form-page" v-loading="loading">
      <el-form ref="formRef" :model="form" label-width="120px">
        <el-form-item label="排行维度">
          <el-checkbox-group v-model="form.rankTypes">
            <el-checkbox label="消费" value="消费" />
            <el-checkbox label="积分" value="积分" />
            <el-checkbox label="游戏" value="游戏" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="统计周期">
          <el-radio-group v-model="form.period">
            <el-radio value="日">日榜</el-radio>
            <el-radio value="周">周榜</el-radio>
            <el-radio value="月">月榜</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Top 奖励积分">
          <el-input-number v-model="form.topRewardPoints" :min="0" />
        </el-form-item>
        <el-form-item label="奖励规则说明">
          <el-input v-model="form.rewardRule" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRankConfig, updateRankConfig } from '@/api/modules/rank'

const formRef = ref()
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  rankTypes: ['消费', '积分', '游戏'],
  period: '周',
  topRewardPoints: 100,
  rewardRule: '',
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
  saving.value = true
  try {
    await updateRankConfig({ ...form })
    ElMessage.success('配置已保存')
  } finally {
    saving.value = false
  }
}

onMounted(loadConfig)
</script>
