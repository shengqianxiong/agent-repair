<template>
  <view class="page">
    <u-list v-if="list.length" @scrolltolower="loadMore">
      <u-list-item v-for="item in list" :key="item.id">
        <u-cell
          :title="item.title"
          :label="item.content"
          :value="formatTime(item.createTime)"
          :isLink="true"
          @click="onItemClick(item)"
        >
          <template #icon>
            <u-badge v-if="!item.isRead" isDot />
          </template>
        </u-cell>
      </u-list-item>
    </u-list>
    <u-empty v-else mode="message" text="暂无通知" />
    <u-loadmore v-if="list.length" :status="loadStatus" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getNotificationList, markNotificationRead } from '@/api/notification'
import { formatDateTime } from '@/utils/navigate'

const list = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const finished = ref(false)

onShow(() => refresh())
onPullDownRefresh(async () => {
  await refresh()
  uni.stopPullDownRefresh()
})

async function refresh() {
  page.value = 1
  finished.value = false
  await fetchList(true)
}

async function fetchList(reset = false) {
  if (finished.value && !reset) return
  loadStatus.value = 'loading'
  try {
    const data = await getNotificationList({ page: page.value, pageSize: 20 })
    const items = data?.list || []
    list.value = reset ? items : [...list.value, ...items]
    finished.value = items.length < 20
    loadStatus.value = finished.value ? 'nomore' : 'loadmore'
  } catch {
    list.value = []
    loadStatus.value = 'nomore'
  }
}

function loadMore() {
  if (finished.value) return
  page.value++
  fetchList()
}

function formatTime(val) {
  return formatDateTime(val).slice(5, 16)
}

async function onItemClick(item) {
  if (!item.isRead) {
    try { await markNotificationRead(item.id) } catch { /* */ }
    item.isRead = true
  }
  if (item.type === '订单' && item.refId) {
    uni.navigateTo({ url: `/pages/order/detail?id=${item.refId}` })
  } else if (item.type === '活动' && item.refId) {
    uni.navigateTo({ url: `/pages/activity/detail?id=${item.refId}` })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
}
</style>
