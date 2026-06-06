<template>
  <u-popup :show="show" mode="left" @close="onClose">
    <view class="side-menu">
      <view class="side-header bar-gradient-header">
        <u-avatar :src="user?.avatar" size="60" />
        <text class="nickname">{{ user?.nickname || '云享生活用户' }}</text>
        <u-tag v-if="user?.memberLevelName" :text="user.memberLevelName" type="warning" size="mini" />
      </view>
      <u-cell-group>
        <u-cell title="首页" icon="home" isLink @click="go('/pages/index/index')" />
        <u-cell title="自助点餐" icon="shopping-cart" isLink @click="go('/pages/order/index')" />
        <u-cell title="在线预约" icon="calendar" isLink @click="go('/pages/booking/index')" />
        <u-cell title="酒水套餐" icon="gift" isLink @click="go('/pages/package/list')" />
        <u-cell title="精选活动" icon="star" isLink @click="goActivity" />
        <u-cell title="会员中心" icon="level" isLink @click="go('/pages/member/index')" />
        <u-cell title="我的积分" icon="red-packet" isLink @click="go('/pages/points/index')" />
        <u-cell title="团购核销" icon="scan" isLink @click="go('/pages/coupon/verify')" />
        <u-cell title="附近搭子" icon="account" isLink @click="go('/pages/partner/list')" />
        <u-cell title="互动游戏" icon="play-circle" isLink @click="go('/pages/game/index')" />
        <u-cell title="排行榜" icon="list" isLink @click="go('/pages/rank/index')" />
        <u-cell title="消息通知" icon="bell" isLink @click="go('/pages/notification/list')" />
        <u-cell title="个人中心" icon="account-fill" isLink @click="go('/pages/profile/index')" />
      </u-cell-group>
    </view>
  </u-popup>
</template>

<script setup>
import { computed } from 'vue'
import { getUser } from '@/utils/auth'

const props = defineProps({
  show: { type: Boolean, default: false }
})
const emit = defineEmits(['update:show', 'close'])

const user = computed(() => getUser())

function onClose() {
  emit('update:show', false)
  emit('close')
}

function go(url) {
  onClose()
  uni.navigateTo({ url })
}

function goActivity() {
  onClose()
  uni.showToast({ title: '请从首页活动轮播进入', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.side-menu {
  width: 560rpx;
  height: 100vh;
  background: #fff;
}

.side-header {
  padding: 80rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 16rpx;

  .nickname {
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
  }
}
</style>
