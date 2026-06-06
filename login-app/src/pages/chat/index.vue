<template>
  <view class="page">
    <scroll-view scroll-y class="message-list" :scroll-into-view="scrollIntoView">
      <view
        v-for="msg in messages"
        :key="msg.id"
        :id="`msg-${msg.id}`"
        class="message"
        :class="{ mine: msg.isMine }"
      >
        <view class="bubble">{{ msg.content }}</view>
        <text class="time">{{ formatTime(msg.createTime) }}</text>
      </view>
    </scroll-view>

    <view class="input-bar">
      <u-input v-model="inputText" placeholder="输入消息..." border="surround" @confirm="send"></u-input>
      <u-button type="primary" text="发送" size="small" :loading="sending" @click="send"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getChatMessages, sendChatMessage } from '@/api/index.js'

const partnerId = ref('')
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const scrollIntoView = ref('')
let pollTimer = null

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(11, 16)
}

async function loadMessages() {
  try {
    const data = await getChatMessages(partnerId.value)
    messages.value = data?.list || data || []
    if (messages.value.length) {
      scrollIntoView.value = `msg-${messages.value[messages.value.length - 1].id}`
    }
  } catch (e) {}
}

async function send() {
  const content = inputText.value.trim()
  if (!content) return
  sending.value = true
  try {
    await sendChatMessage({ partnerId: partnerId.value, content })
    inputText.value = ''
    await loadMessages()
  } catch (e) {
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  partnerId.value = pages[pages.length - 1].options?.partnerId || ''
  loadMessages()
  pollTimer = setInterval(loadMessages, 5000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  display: flex;
  flex-direction: column;
}
.message-list {
  flex: 1;
  padding: 24rpx;
  height: calc(100vh - 120rpx);
}
.message {
  margin-bottom: 24rpx;
  &.mine {
    text-align: right;
    .bubble {
      background: #7c3aed;
      margin-left: auto;
    }
  }
}
.bubble {
  display: inline-block;
  max-width: 70%;
  padding: 16rpx 24rpx;
  background: #1a1a2e;
  border-radius: 16rpx;
  color: #e8e8f0;
  text-align: left;
}
.time { font-size: 20rpx; color: #6b6b80; margin-top: 8rpx; display: block; }
.input-bar {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  background: #1a1a2e;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}
</style>
