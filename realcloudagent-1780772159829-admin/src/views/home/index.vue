<template>
  <div class="page-card home-page">
    <el-result icon="success" title="登录成功" :sub-title="welcomeText">
      <template #extra>
        <p class="tip">当前为极简登录 Demo，暂无其他业务功能。</p>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getUsername } from '@/utils/auth'

const welcomeText = computed(() => {
  const name = getUsername()
  return name ? `欢迎回来，${name}` : '欢迎使用 RealCloudAgent 管理端'
})
</script>

<style scoped>
.home-page {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tip {
  color: #909399;
  font-size: 14px;
  margin: 0;
}
</style>
