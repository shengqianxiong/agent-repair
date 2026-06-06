<template>
  <el-card>
    <h3>登录成功</h3>
    <p>欢迎回来，{{ username }}！您已成功登录 RealCloudAgent 管理端。</p>
  </el-card>
</template>

<script setup>
import { getUsername } from '@/utils/auth'

const username = getUsername()
</script>
