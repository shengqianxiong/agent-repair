<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="我的钱包" right-icon="bell" bg-color="#F8F9FB" />

    <scroll-view scroll-y class="content">
      <view class="asset-card">
        <view class="asset-header">
          <text>我的资产(元)</text>
          <u-icon :name="showBalance ? 'eye' : 'eye-off'" color="#999" size="18" @click="showBalance = !showBalance"></u-icon>
        </view>
        <text class="balance">{{ showBalance ? '¥' + formatMoney(balance) : '****' }}</text>
        <view class="asset-btns">
          <view class="btn-withdraw" @click="showToast('提现')">提现</view>
          <view class="btn-recharge" @click="showToast('充值')">充值</view>
        </view>
      </view>

      <view class="menu-card">
        <view v-for="item in menuItems" :key="item.name" class="menu-item" @click="showToast(item.name)">
          <view class="menu-icon"><u-icon :name="item.icon" color="#7B61FF" size="20"></u-icon></view>
          <text class="menu-name">{{ item.name }}</text>
          <u-icon name="arrow-right" color="#CCC" size="14"></u-icon>
        </view>
      </view>

      <view class="member-banner">
        <view class="banner-text">
          <text class="banner-title">尊享会员特权</text>
          <text class="banner-desc">开通会员享更多优惠</text>
        </view>
        <view class="banner-btn" @click="showToast('去开启')">去开启</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getWalletBalance } from '@/api/index.js'
import { formatMoney, showToast } from '@/utils/common.js'

const loading = ref(false)
const balance = ref(0)
const showBalance = ref(true)

const menuItems = [
  { name: '资金明细', icon: 'list' },
  { name: '提现记录', icon: 'clock' },
  { name: '提现账号', icon: 'account' }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getWalletBalance()
    balance.value = res?.balance ?? 0
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { padding: 24rpx 32rpx; }
.asset-card {
  background: #FFF; border-radius: 48rpx; padding: 40rpx; text-align: center; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .asset-header { display: flex; align-items: center; justify-content: center; gap: 12rpx; font-size: 28rpx; color: #666; }
  .balance { font-size: 64rpx; font-weight: 700; color: #1A1A1A; display: block; margin: 24rpx 0 32rpx; }
  .asset-btns { display: flex; gap: 24rpx; }
  .btn-withdraw { flex: 1; background: #7B61FF; color: #FFF; padding: 20rpx; border-radius: 200rpx; font-size: 28rpx; font-weight: 600; }
  .btn-recharge { flex: 1; background: #EBE4FF; color: #7B61FF; padding: 20rpx; border-radius: 200rpx; font-size: 28rpx; }
}
.menu-card {
  background: #FFF; border-radius: 32rpx; overflow: hidden; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .menu-item { display: flex; align-items: center; padding: 32rpx; border-bottom: 1rpx solid #F0F0F0;
    &:last-child { border-bottom: none; }
    .menu-icon { width: 72rpx; height: 72rpx; border-radius: 50%; background: #F0EDFF; display: flex; align-items: center; justify-content: center; margin-right: 20rpx; }
    .menu-name { flex: 1; font-size: 30rpx; color: #333; }
  }
}
.member-banner {
  display: flex; align-items: center; justify-content: space-between;
  background: #F2F1F0; border-radius: 40rpx; padding: 32rpx;
  .banner-title { font-size: 30rpx; font-weight: 600; display: block; }
  .banner-desc { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
  .banner-btn { background: #7B61FF; color: #FFF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx; }
}
</style>
