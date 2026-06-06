<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">RealCloudAgent</div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/home">
          <span>首页</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>欢迎，{{ username }}</span>
        <el-button type="danger" link @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth, getUsername } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const username = getUsername()
const activeMenu = computed(() => route.path)

function handleLogout() {
  clearAuth()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.aside {
  background: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: bold;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
}
</style>
