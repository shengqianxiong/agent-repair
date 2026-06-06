<template>
  <view class="bar-page">
    <view v-if="!list.length && !loading" class="empty-wrap">
      <u-empty mode="message" text="暂无通知" />
    </view>

    <view
      v-for="item in list"
      :key="item.id"
      class="bar-card notify-item"
      :class="{ unread: !item.isRead }"
      @click="onItemClick(item)"
    >
      <view class="notify-header">
        <u-tag :text="item.type || '系统'" size="mini" type="primary" />
        <text class="time">{{ formatDateTime(item.createTime) }}</text>
      </view>
      <text class="title">{{ item.title }}</text>
      <text class="content u-line-2">{{ item.content }}</text>
    </view>

    <u-loadmore v-if="list.length" :status="loadStatus" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getNotificationList, markNotificationRead } from '@/api/marketing'
import { formatDateTime } from '@/utils/request'

const list = ref([])
const loading = ref(false)
const loadStatus = ref('loadmore')
const page = ref(1)
const total = ref(0)

async function loadList(reset = true) {
  if (reset) {
    page.value = 1
    list.value = []
  }
  loading.value = true
  try {
    const res = await getNotificationList({ page: page.value, pageSize: 15 })
    const items = res?.list || []
    total.value = res?.total || 0
    list.value = reset ? items : [...list.value, ...items]
    loadStatus.value = list.value.length >= total.value ? 'nomore' : 'loadmore'
  } finally {
    loading.value = false
  }
}

async function onItemClick(item) {
  if (!item.isRead) {
    await markNotificationRead(item.id)
    item.isRead = true
  }
  if (item.type === '订单' && item.refId) {
    uni.navigateTo({ url: `/pages/order/detail?id=${item.refId}` })
  } else if (item.type === '活动' && item.refId) {
    uni.navigateTo({ url: `/pages/activity/detail?id=${item.refId}` })
  }
}

onShow(() => loadList(true))
onPullDownRefresh(async () => { await loadList(true); uni.stopPullDownRefresh() })
onReachBottom(() => {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadList(false)
  }
})
</script>

<style lang="scss" scoped>
.notify-item {
  &.unread {
    border-left: 6rpx solid #6c5ce7;
  }
}

.notify-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;

  .time {
    font-size: 22rpx;
    color: #999;
  }
}

.title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.content {
  font-size: 24rpx;
  color: #666;
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>
