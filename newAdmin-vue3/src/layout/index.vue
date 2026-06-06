<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">云享生活 · 管理端</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1d1e2c"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <template v-for="item in menuList" :key="item.path || item.title">
          <el-sub-menu v-if="item.children" :index="item.title">
            <template #title>
              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <span class="page-title">{{ pageTitle }}</span>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { menuList } from '@/utils/tool'

const route = useRoute()
const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '云享生活管理后台')
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}
.layout-aside {
  background: #1d1e2c;
}
.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1px solid #2d2f45;
}
.layout-header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.layout-main {
  background: #f5f7fa;
  padding: 16px;
}
</style>

<style>
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
.page-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}
.search-bar {
  margin-bottom: 16px;
}
.table-toolbar {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
