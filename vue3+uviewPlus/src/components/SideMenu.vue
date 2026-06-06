<template>
  <u-popup :show="show" mode="left" @close="onClose">
    <view class="side-menu">
      <view class="side-header">
        <u-avatar :src="userInfo.avatar" size="56" />
        <view class="side-user">
          <text class="nickname">{{ userInfo.nickname || '云享会员' }}</text>
          <u-tag :text="userInfo.memberLevelName || '普通会员'" size="mini" type="warning" />
        </view>
      </view>
      <u-cell-group>
        <u-cell
          v-for="item in menuList"
          :key="item.path"
          :title="item.title"
          :icon="item.icon"
          isLink
          @click="go(item.path)"
        />
      </u-cell-group>
    </view>
  </u-popup>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getUserInfo } from '@/api/user'

const props = defineProps({
  show: { type: Boolean, default: false }
})
const emit = defineEmits(['update:show', 'close'])

const userInfo = ref({})

const menuList = [
  { title: '首页', icon: 'home', path: '/pages/index/index' },
  { title: '自助点餐', icon: 'shopping-cart', path: '/pages/order/index' },
  { title: '在线预约', icon: 'calendar', path: '/pages/booking/index' },
  { title: '酒水套餐', icon: 'gift', path: '/pages/package/list' },
  { title: '精选活动', icon: 'star', path: '/pages/activity/detail?id=1' },
  { title: '会员中心', icon: 'level', path: '/pages/member/index' },
  { title: '我的积分', icon: 'integral', path: '/pages/points/index' },
  { title: '团购核销', icon: 'coupon', path: '/pages/coupon/verify' },
  { title: '附近搭子', icon: 'man-add', path: '/pages/partner/list' },
  { title: '互动游戏', icon: 'play-circle', path: '/pages/game/index' },
  { title: '排行榜', icon: 'list', path: '/pages/rank/index' },
  { title: '消息通知', icon: 'bell', path: '/pages/notification/list' },
  { title: '个人中心', icon: 'account', path: '/pages/profile/index' }
]

watch(() => props.show, (val) => {
  if (val) loadUser()
})

async function loadUser() {
  try {
    userInfo.value = await getUserInfo() || {}
  } catch {
    userInfo.value = { nickname: '云享会员', memberLevelName: '普通会员' }
  }
}

function onClose() {
  emit('update:show', false)
  emit('close')
}

function go(path) {
  onClose()
  const tabs = ['/pages/index/index', '/pages/order/index', '/pages/rank/index', '/pages/profile/index']
  if (tabs.includes(path)) {
    uni.switchTab({ url: path })
  } else {
    uni.navigateTo({ url: path })
  }
}
</script>

<style lang="scss" scoped>
.side-menu {
  width: 560rpx;
  min-height: 100vh;
  background: #1a1a2e;
  padding: 40rpx 24rpx;
}
.side-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #2a2a3e;
}
.side-user {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}
.nickname {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}
</style>
