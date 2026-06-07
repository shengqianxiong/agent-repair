<template>
  <div class="page-card home-page">
    <el-result icon="success" title="登录成功" sub-title="欢迎使用 RealCloudAgent 管理后台">
      <template #extra>
        <el-descriptions :column="1" border class="account-info">
          <el-descriptions-item label="账号 ID">{{ accountId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="账号名称">{{ account || '-' }}</el-descriptions-item>
        </el-descriptions>
        <p class="home-tip">当前为极简登录模式，暂无其他业务功能。</p>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getAccount, getAccountId } from '@/utils/auth'

const account = computed(() => getAccount())
const accountId = computed(() => getAccountId())
</script>

<style scoped>
.home-page {
  max-width: 560px;
  margin: 40px auto;
}
.account-info {
  margin-bottom: 16px;
  text-align: left;
}
.home-tip {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style>
