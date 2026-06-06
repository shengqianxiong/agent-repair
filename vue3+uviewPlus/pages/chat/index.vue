<template>
  <view class="chat-page">
    <scroll-view scroll-y class="message-list" :scroll-into-view="scrollInto">
      <view v-for="msg in messages" :key="msg.id" :id="'msg-' + msg.id" class="message" :class="{ mine: msg.isMine }">
        <view class="bubble">{{ msg.content }}</view>
        <text class="time">{{ formatDateTime(msg.createTime) }}</text>
      </view>
    </scroll-view>

    <view class="input-bar safe-bottom">
      <u-input v-model="inputText" placeholder="输入消息..." border="surround" />
      <u-button type="primary" text="发送" size="small" @click="send" />
    </view>
  </view>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getChatMessages, sendChatMessage } from '@/api/social'
import { formatDateTime } from '@/utils/request'
import { getUser } from '@/utils/auth'

const partnerId = ref('')
const partnerName = ref('')
const messages = ref([])
const inputText = ref('')
const scrollInto = ref('')
let pollTimer = null

async function loadMessages() {
  try {
    const list = await getChatMessages(partnerId.value)
    const user = getUser()
    messages.value = (list?.list || list || []).map((m) => ({
      ...m,
      isMine: m.fromUserId === user?.id || m.senderId === user?.id
    }))
    if (messages.value.length) {
      scrollInto.value = 'msg-' + messages.value[messages.value.length - 1].id
    }
  } catch (e) {}
}

async function send() {
  const content = inputText.value.trim()
  if (!content) return
  await sendChatMessage({ partnerId: partnerId.value, content })
  inputText.value = ''
  loadMessages()
}

onLoad((opts) => {
  partnerId.value = opts.partnerId
  partnerName.value = opts.name
  uni.setNavigationBarTitle({ title: partnerName.value || '聊天' })
})

onShow(() => {
  loadMessages()
  pollTimer = setInterval(loadMessages, 5000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style lang="scss" scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.message-list {
  flex: 1;
  padding: 24rpx;
  box-sizing: border-box;
}

.message {
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  &.mine {
    align-items: flex-end;

    .bubble {
      background: #6c5ce7;
      color: #fff;
    }
  }
}

.bubble {
  max-width: 70%;
  background: #fff;
  padding: 16rpx 24rpx;
  border-radius: 16rpx;
  font-size: 28rpx;
  line-height: 1.5;
}

.time {
  font-size: 20rpx;
  color: #999;
  margin-top: 8rpx;
}

.input-bar {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  background: #fff;
  align-items: center;
}
</style>
