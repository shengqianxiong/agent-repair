<template>
  <div class="page-card home-page">
    <el-result icon="success" title="登录成功" :sub-title="welcomeText">
      <template #extra>
        <el-descriptions :column="1" border style="max-width: 400px; margin: 0 auto">
          <el-descriptions-item label="用户名">{{ username }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ userId || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getAdminInfo } from '@/api/modules/auth'
import { getAccount, getAccountId, setAccount, setAccountId } from '@/utils/auth'

const username = ref(getAccount())
const userId = ref(getAccountId())
const welcomeText = ref('欢迎使用极简登录管理系统')

onMounted(async () => {
  try {
    const data = await getAdminInfo()
    username.value = data.username
    userId.value = data.id
    setAccount(data.username)
    setAccountId(data.id)
  } catch (e) {
    // 接口不可用时使用本地缓存
  }
})
</script>

<style scoped>
.home-page {
  min-height: 360px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
