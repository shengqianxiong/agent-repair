<template>
  <div class="page-card home-page">
    <el-result icon="success" title="登录成功" :sub-title="welcomeText">
      <template #extra>
        <p class="home-tip">当前为极简登录模式，暂无其他业务功能。</p>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAdminInfo } from '@/api/modules/auth'
import { getUsername, setUsername } from '@/utils/auth'

const nickname = ref('')

const welcomeText = computed(() => {
  const name = nickname.value || getUsername() || '管理员'
  return `欢迎回来，${name}`
})

/** 加载当前管理员信息（接口可选，失败时使用本地缓存用户名） */
onMounted(async () => {
  try {
    const info = await getAdminInfo()
    if (info?.username) {
      nickname.value = info.nickname || info.username
      setUsername(info.username)
    }
  } catch {
    // 用户信息接口未就绪时静默降级
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
.home-tip {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style>
