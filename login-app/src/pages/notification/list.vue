<template>
  <view class="page">
    <scroll-view scroll-y class="page-body" @scrolltolower="loadMore">
    <view v-if="list.length" class="list">
      <u-cell
        v-for="item in list"
        :key="item.id"
        :title="item.title"
        :label="item.content"
        :value="formatTime(item.createTime)"
        :isLink="!!item.linkType"
        @click="onItemClick(item)"
      >
        <template #icon>
          <u-badge :isDot="!item.isRead" absolute :offset="[0, 0]"></u-badge>
          <u-icon :name="typeIcon(item.type)" color="#7c3aed" size="20" style="margin-right:16rpx"></u-icon>
        </template>
      </u-cell>
      <u-loadmore :status="loadStatus"></u-loadmore>
    </view>
    <u-empty v-else mode="message" text="暂无通知"></u-empty>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getNotificationList, markNotificationRead } from '@/api/index.js'

const list = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')

function typeIcon(type) {
  const map = { '系统': 'info-circle', '活动': 'star', '订单': 'order' }
  return map[type] || 'bell'
}

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(0, 16).replace('T', ' ')
}

async function loadData(reset = false) {
  if (reset) {
    page.value = 1
    list.value = []
  }
  loadStatus.value = 'loading'
  try {
    const data = await getNotificationList({ page: page.value, pageSize: 20 })
    const items = data?.list || data || []
    list.value = reset ? items : [...list.value, ...items]
    loadStatus.value = items.length < 20 ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  }
}

async function onItemClick(item) {
  if (!item.isRead) {
    try {
      await markNotificationRead(item.id)
      item.isRead = true
    } catch (e) {}
  }
  if (item.type === '订单' && item.refId) {
    uni.navigateTo({ url: `/pages/order/detail?id=${item.refId}` })
  } else if (item.type === '活动' && item.refId) {
    uni.navigateTo({ url: `/pages/activity/detail?id=${item.refId}` })
  }
}

onMounted(() => loadData(true))
onPullDownRefresh(async () => {
  await loadData(true)
  uni.stopPullDownRefresh()
})
function loadMore() {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadData()
  }
}
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.list { padding: 16rpx 0; }
</style>
