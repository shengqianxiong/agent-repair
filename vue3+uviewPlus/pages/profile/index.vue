<template>
  <view class="bar-page">
    <view class="profile-header bar-gradient-header">
      <u-avatar :src="userInfo.avatar" size="70" />
      <view class="user-meta">
        <text class="nickname">{{ userInfo.nickname || '云享生活用户' }}</text>
        <view class="tags">
          <u-tag v-if="userInfo.memberLevelName" :text="userInfo.memberLevelName" type="warning" size="mini" />
          <text class="points">积分 {{ userInfo.points || 0 }}</text>
        </view>
      </view>
    </view>

    <view class="bar-card">
      <u-grid :col="4" :border="false">
        <u-grid-item @click="go('/pages/order/list')">
          <u-icon name="order" color="#6c5ce7" size="28" />
          <text class="grid-text">我的订单</text>
        </u-grid-item>
        <u-grid-item @click="go('/pages/booking/index')">
          <u-icon name="calendar" color="#6c5ce7" size="28" />
          <text class="grid-text">我的预约</text>
        </u-grid-item>
        <u-grid-item @click="go('/pages/member/index')">
          <u-icon name="level" color="#6c5ce7" size="28" />
          <text class="grid-text">会员中心</text>
        </u-grid-item>
        <u-grid-item @click="go('/pages/points/index')">
          <u-icon name="red-packet" color="#6c5ce7" size="28" />
          <text class="grid-text">我的积分</text>
        </u-grid-item>
      </u-grid>
    </view>

    <view class="bar-card">
      <u-cell-group>
        <u-cell title="消息通知" icon="bell" isLink @click="go('/pages/notification/list')" />
        <u-cell title="团购核销" icon="scan" isLink @click="go('/pages/coupon/verify')" />
        <u-cell title="附近搭子" icon="account" isLink @click="go('/pages/partner/list')" />
        <u-cell title="互动游戏" icon="play-circle" isLink @click="go('/pages/game/index')" />
        <u-cell title="酒水套餐" icon="gift" isLink @click="go('/pages/package/list')" />
      </u-cell-group>
    </view>

    <BarTabbar current="profile" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import BarTabbar from '@/components/BarTabbar.vue'
import { getUserInfo } from '@/api/home'
import { setUser } from '@/utils/auth'

const userInfo = ref({})

async function loadUser() {
  try {
    const data = await getUserInfo()
    userInfo.value = data || {}
    setUser(data)
  } catch (e) {}
}

function go(url) {
  uni.navigateTo({ url })
}

onShow(() => loadUser())
onPullDownRefresh(async () => { await loadUser(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.profile-header {
  padding: 80rpx 32rpx 40rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.user-meta {
  .nickname {
    color: #fff;
    font-size: 36rpx;
    font-weight: bold;
    display: block;
    margin-bottom: 12rpx;
  }

  .tags {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .points {
    color: rgba(255, 255, 255, 0.85);
    font-size: 24rpx;
  }
}

.grid-text {
  font-size: 22rpx;
  color: #666;
  margin-top: 8rpx;
}
</style>
