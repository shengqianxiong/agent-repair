<template>
  <view class="reward-page page-container">
    <view class="wallet-card">
      <view class="wallet-label">我的环保奖励（元）</view>
      <view class="wallet-amount">¥ {{ summary.balance }}</view>
      <view class="wallet-stats">
        <view>
          <view class="stats-value">{{ summary.totalReward }}</view>
          <view class="stats-label">累计奖励（元）</view>
        </view>
        <view>
          <view class="stats-value">{{ summary.totalWeight }}</view>
          <view class="stats-label">累计回收（kg）</view>
        </view>
      </view>
      <view class="withdraw-btn" @click="showWithdraw = true">提现</view>
    </view>

    <view class="quick-entry card-box">
      <view class="entry-item" @click="goRedeem">
        <text class="entry-icon">🎁</text>
        <view>
          <view class="entry-title">兑换礼品</view>
          <view class="entry-desc">积分兑换礼品卡券</view>
        </view>
      </view>
      <view class="entry-item" @click="goCertificate">
        <text class="entry-icon">💗</text>
        <view>
          <view class="entry-title">公益捐赠</view>
          <view class="entry-desc">领取捐赠证书</view>
        </view>
      </view>
    </view>

    <view class="log-card card-box">
      <view class="log-title">奖励明细</view>
      <view v-for="item in rewardLogs" :key="item.id" class="log-item">
        <view>
          <view class="log-name">{{ item.title }}</view>
          <view class="log-time">{{ item.time }} ｜ 订单号：{{ item.orderNo }}</view>
        </view>
        <view class="log-amount" :class="{ minus: item.amount < 0 }">
          {{ item.amount > 0 ? '+' : '' }}{{ item.amount.toFixed(2) }}
        </view>
      </view>
      <view class="load-more">查看更多 ></view>
    </view>

    <u-popup :show="showWithdraw" mode="bottom" round="20" @close="showWithdraw = false">
      <view class="withdraw-popup">
        <view class="popup-header">
          <text @click="showWithdraw = false">取消</text>
          <text class="popup-title">提现</text>
          <text @click="submitWithdraw">提现</text>
        </view>
        <view class="popup-balance card-box">可提现金额（元） <text class="num">{{ summary.balance }}</text></view>
        <view class="amount-box">
          <view class="amount-label">提现金额</view>
          <view class="amount-list">
            <view
              v-for="item in withdrawOptions"
              :key="item"
              class="amount-item"
              :class="{ active: withdrawAmount === item }"
              @click="withdrawAmount = item"
            >
              {{ item }}元
            </view>
          </view>
        </view>
        <view class="way-box">提现方式：微信零钱</view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchRewardLogs, fetchRewardSummary } from '@/api'

const showWithdraw = ref(false)
const withdrawOptions = [10, 20, 50, 100, 200]
const withdrawAmount = ref(20)

const summary = ref({
  balance: 28.6,
  totalReward: 128.6,
  totalWeight: 237.2
})

const rewardLogs = ref([
  { id: 1, title: '上门回收奖励', time: '2024-05-20 10:45', orderNo: '202405200089', amount: 6.3 },
  { id: 2, title: '上门回收奖励', time: '2024-05-18 15:10', orderNo: '202405180056', amount: 4.2 },
  { id: 3, title: '礼品兑换', time: '2024-05-15 11:20', orderNo: 'LP20240516001', amount: -20 },
  { id: 4, title: '上门回收奖励', time: '2024-05-12 09:30', orderNo: '202405120033', amount: 5.6 }
])

async function loadData() {
  try {
    const [summaryData, logsData] = await Promise.all([fetchRewardSummary(), fetchRewardLogs({ pageNo: 1, pageSize: 10 })])
    if (summaryData) summary.value = summaryData
    if (Array.isArray(logsData?.records)) rewardLogs.value = logsData.records
  } catch (error) {
    // mock 数据兜底
  }
}

function submitWithdraw() {
  if (withdrawAmount.value > summary.value.balance) {
    uni.showToast({ title: '提现金额不能超过可提现余额', icon: 'none' })
    return
  }
  showWithdraw.value = false
  uni.showToast({ title: '提现申请已提交', icon: 'success' })
}

function goRedeem() {
  uni.navigateTo({ url: '/pages/reward/redeem' })
}

function goCertificate() {
  uni.navigateTo({ url: '/pages/certificate/index' })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.wallet-card {
  padding: 28rpx;
  border-radius: 24rpx;
  color: #ffffff;
  background: radial-gradient(circle at right top, rgba(255, 255, 255, 0.2), transparent 45%),
    linear-gradient(135deg, #1f9d4c, #127e3a);
  position: relative;
}

.wallet-label {
  font-size: 24rpx;
  opacity: 0.9;
}

.wallet-amount {
  margin-top: 12rpx;
  font-size: 62rpx;
  line-height: 1;
  font-weight: 700;
}

.wallet-stats {
  margin-top: 22rpx;
  display: flex;
  justify-content: space-between;
  width: 78%;
}

.stats-value {
  font-size: 34rpx;
  font-weight: 600;
}

.stats-label {
  margin-top: 6rpx;
  font-size: 20rpx;
  opacity: 0.85;
}

.withdraw-btn {
  position: absolute;
  right: 24rpx;
  top: 28rpx;
  width: 112rpx;
  text-align: center;
  line-height: 54rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: #1f8c48;
  background: #ffffff;
}

.quick-entry {
  margin-top: 16rpx;
  padding: 20rpx 24rpx;
  display: flex;
  justify-content: space-between;
}

.entry-item {
  width: 48%;
  display: flex;
  gap: 12rpx;
  align-items: center;
}

.entry-icon {
  font-size: 42rpx;
}

.entry-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #2f343c;
}

.entry-desc {
  margin-top: 4rpx;
  font-size: 20rpx;
  color: #9198a1;
}

.log-card {
  margin-top: 16rpx;
  padding: 24rpx;
}

.log-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #2f3338;
}

.log-item {
  padding: 18rpx 0;
  border-bottom: 2rpx solid #edf0f3;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.log-item:last-child {
  border-bottom: none;
}

.log-name {
  font-size: 24rpx;
  color: #2f3338;
}

.log-time {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #9aa0a6;
}

.log-amount {
  color: #ff8a00;
  font-size: 28rpx;
  font-weight: 700;
}

.log-amount.minus {
  color: #e34d59;
}

.load-more {
  text-align: center;
  margin-top: 14rpx;
  font-size: 22rpx;
  color: #9aa0a6;
}

.withdraw-popup {
  padding: 24rpx;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  color: #5c626a;
}

.popup-title {
  color: #1f2329;
  font-weight: 600;
}

.popup-balance {
  margin-top: 22rpx;
  padding: 20rpx;
  background: linear-gradient(135deg, #2ba659, #178846);
  color: #ffffff;
  font-size: 24rpx;
}

.popup-balance .num {
  margin-left: 12rpx;
  font-size: 52rpx;
  font-weight: 700;
}

.amount-box {
  margin-top: 22rpx;
}

.amount-label {
  font-size: 24rpx;
  color: #5f646d;
}

.amount-list {
  margin-top: 14rpx;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.amount-item {
  height: 66rpx;
  border-radius: 12rpx;
  border: 2rpx solid #e9edf2;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #5e646d;
  font-size: 24rpx;
}

.amount-item.active {
  border-color: #2eae5e;
  color: #1f8c48;
  background: #e8f7ed;
}

.way-box {
  margin: 24rpx 0 12rpx;
  color: #6a727d;
  font-size: 24rpx;
}
</style>
