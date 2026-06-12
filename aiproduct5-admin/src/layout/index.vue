<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <span class="logo-icon">豆</span>
        <span class="logo-text">豆评助手</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="var(--dp-bg-sidebar)"
        text-color="var(--dp-text-sidebar)"
        active-text-color="var(--dp-primary)"
      >
        <el-menu-item v-for="item in menuList" :key="item.path" :index="item.path">
          <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/seller/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="current-account">{{ currentAccount }}</span>
          <el-button type="danger" link @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { menuList } from '@/utils/tool'
import { getAccount, removeToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/seller/activity')) return '/seller/activity/list'
  return path
})

const pageTitle = computed(() => route.meta.title || '工作台')
const breadcrumb = computed(() => route.meta.breadcrumb || '')
const currentAccount = computed(() => getAccount() || '商家账号')

function handleLogout() {
  removeToken()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-aside {
  background: var(--dp-bg-sidebar);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border-radius: var(--dp-radius-sm);
  background: linear-gradient(135deg, var(--dp-primary), var(--dp-primary-light));
  color: #fff;
  font-weight: 700;
  font-size: 16px;
}

.logo-text {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--dp-bg-card);
  border-bottom: 1px solid var(--dp-border);
  height: 60px;
  padding: 0 var(--dp-spacing-lg);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.page-title {
  font-size: var(--dp-font-title);
  font-weight: 600;
  color: var(--dp-text-primary);
  line-height: 1.2;
}

.breadcrumb {
  font-size: var(--dp-font-caption);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--dp-spacing-md);
}

.current-account {
  color: var(--dp-text-regular);
  font-size: var(--dp-font-body);
}

.layout-main {
  background: var(--dp-bg-page);
  padding: var(--dp-spacing-md);
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item.is-active) {
  background: var(--dp-bg-sidebar-active) !important;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
