<template>
  <view class="profile-page page-container">
    <view class="user-card card-box">
      <view class="avatar">👤</view>
      <view>
        <view class="name">张三</view>
        <view class="phone">138****8888</view>
      </view>
    </view>

    <view class="menu-card card-box">
      <view class="menu-item" @click="goReserve">
        <text>📦 地址管理</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
      <view class="menu-item" @click="goOrder">
        <text>🧾 我的订单</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
      <view class="menu-item" @click="goReward">
        <text>💰 我的奖励</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
      <view class="menu-item" @click="goCertificate">
        <text>🏅 我的证书</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
      <view class="menu-item">
        <text>💬 在线客服</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
      <view class="menu-item">
        <text>❓ 常见问题</text>
        <u-icon name="arrow-right" color="#B0B5BC" />
      </view>
    </view>
  </view>
</template>

<script setup>
function goReserve() {
  uni.navigateTo({ url: '/pages/reserve/index' })
}

function goOrder() {
  uni.switchTab({ url: '/pages/order/index' })
}

function goReward() {
  uni.switchTab({ url: '/pages/reward/index' })
}

function goCertificate() {
  uni.navigateTo({ url: '/pages/certificate/index' })
}
</script>

<style lang="scss" scoped>
.user-card {
  padding: 26rpx;
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.avatar {
  width: 90rpx;
  height: 90rpx;
  border-radius: 50%;
  background: #e8f7ed;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 42rpx;
}

.name {
  font-size: 30rpx;
  color: #1f2329;
  font-weight: 700;
}

.phone {
  margin-top: 6rpx;
  color: #9097a0;
  font-size: 24rpx;
}

.menu-card {
  margin-top: 16rpx;
  padding: 0 24rpx;
}

.menu-item {
  height: 92rpx;
  border-bottom: 2rpx solid #edf1f3;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #353a42;
  font-size: 28rpx;
}

.menu-item:last-child {
  border-bottom: none;
}
</style>
