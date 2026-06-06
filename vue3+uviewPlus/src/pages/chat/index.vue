<template>
  <view class="page">
    <scroll-view scroll-y class="msg-list" :scroll-into-view="scrollTo">
      <view
        v-for="msg in messages"
        :key="msg.id"
        :id="`msg-${msg.id}`"
        class="msg-item"
        :class="{ mine: msg.isMine }"
      >
        <view class="bubble">{{ msg.content }}</view>
        <text class="time">{{ formatTime(msg.createTime) }}</text>
      </view>
    </scroll-view>

    <view class="input-bar">
      <u-input v-model="inputText" placeholder="输入消息..." border="surround" />
      <u-button type="primary" size="small" text="发送" :disabled="!inputText.trim()" @click="send" />
    </view>
  </view>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { onLoad, onShow, onHide } from '@dcloudio/uni-app'
import { getChatMessages, sendChatMessage } from '@/api/chat'

const partnerId = ref('')
const partnerName = ref('')
const messages = ref([])
const inputText = ref('')
const scrollTo = ref('')
let pollTimer = null

onLoad((options) => {
  partnerId.value = options.partnerId || ''
  partnerName.value = options.name || '聊天'
  uni.setNavigationBarTitle({ title: partnerName.value })
})

onShow(() => {
  loadMessages()
  pollTimer = setInterval(loadMessages, 5000)
})

onHide(() => {
  if (pollTimer) clearInterval(pollTimer)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})

async function loadMessages() {
  try {
    const data = await getChatMessages(partnerId.value)
    messages.value = data?.list || data || []
    if (messages.value.length) {
      scrollTo.value = `msg-${messages.value[messages.value.length - 1].id}`
    }
  } catch { /* */ }
}

async function send() {
  const content = inputText.value.trim()
  if (!content) return
  try {
    await sendChatMessage({ partnerId: partnerId.value, content })
    inputText.value = ''
    await loadMessages()
  } catch (e) {
    uni.showToast({ title: e.msg || '发送失败', icon: 'none' })
  }
}

function formatTime(val) {
  if (!val) return ''
  const d = new Date(val)
  return `${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style lang="scss" scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0f0f1a;
}
.msg-list {
  flex: 1;
  padding: 16rpx;
}
.msg-item {
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  &.mine {
    align-items: flex-end;
    .bubble {
      background: #7c3aed;
    }
  }
}
.bubble {
  max-width: 70%;
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 16rpx 20rpx;
  font-size: 28rpx;
  color: #fff;
}
.time {
  font-size: 22rpx;
  color: #6b6b80;
  margin-top: 4rpx;
}
.input-bar {
  display: flex;
  gap: 12rpx;
  padding: 16rpx;
  background: #1a1a2e;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}
</style>
