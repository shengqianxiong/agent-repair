<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">我的订单</text>
      <view class="nav-right"><u-icon name="bell" color="#7B61FF" size="20"></u-icon></view>
    </view>

    <scroll-view scroll-x class="status-tabs">
      <view
        v-for="tab in statusTabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeStatus === tab.key }"
        @click="switchStatus(tab.key)"
      >
        {{ tab.label }}
        <view v-if="activeStatus === tab.key" class="tab-dot"></view>
      </view>
    </scroll-view>

    <scroll-view scroll-y class="order-list" @scrolltolower="loadMore">
      <view v-if="orders.length === 0 && !loading" class="empty-wrap"><EmptyState text="暂无订单" button-text="去点餐" @action="goMenu" /></view>

      <view v-for="order in orders" :key="order.id">
        <!-- 制作中紫色高亮卡 -->
        <view v-if="order.status === 'making'" class="order-card active-card">
          <view class="active-header">
            <view class="status-tag">{{ order.statusText }}</view>
            <text class="queue-text">前面还有{{ order.queueAhead }}位</text>
          </view>
          <text class="pickup-no">点餐号码 {{ order.pickupCode }}</text>
          <view class="nested-card">
            <view v-for="item in order.items" :key="item.name" class="nested-item">
              <text>{{ item.name }} x{{ item.quantity }}</text>
              <text>¥{{ item.price * item.quantity }}</text>
            </view>
          </view>
          <view class="card-actions">
            <view class="btn-outline" @click="goDetail(order.id)">查看详情</view>
            <view class="btn-primary" @click="showCode(order)">取餐码</view>
          </view>
        </view>

        <!-- 待出餐白卡 -->
        <view v-else-if="order.status === 'pending_pickup'" class="order-card">
          <text class="order-date">{{ order.createTime }}</text>
          <view v-for="item in order.items" :key="item.name" class="item-brief">
            <image :src="item.image" class="brief-img" />
            <text>{{ item.name }} x{{ item.quantity }}</text>
          </view>
          <view class="card-actions">
            <view class="btn-outline" @click="goDetail(order.id)">查看详情</view>
            <view class="btn-primary" @click="showCode(order)">取餐码</view>
          </view>
        </view>

        <!-- 已完成 -->
        <view v-else-if="order.status === 'completed'" class="order-card">
          <view v-for="item in order.items" :key="item.name" class="completed-item">
            <image :src="item.image" class="brief-img" />
            <view class="completed-info">
              <text class="item-name">{{ item.name }}</text>
              <text class="store-name">{{ order.storeName }}</text>
            </view>
            <text class="item-price">¥{{ order.payAmount }}</text>
          </view>
          <view class="card-actions">
            <view class="btn-outline" @click="reorder(order.id)">再来一单</view>
            <view class="btn-primary light" @click="showToast('评价赢积分')">评价赢积分</view>
          </view>
        </view>

        <!-- 已取消 -->
        <view v-else-if="order.status === 'cancelled'" class="order-card cancelled">
          <view class="cancelled-icon"><u-icon name="close-circle" color="#CCC" size="32"></u-icon></view>
          <view class="cancelled-info">
            <text class="cancelled-title">订单已取消</text>
            <text class="cancelled-note">{{ order.refundNote }}</text>
          </view>
          <text class="cancelled-price">¥{{ order.payAmount }}</text>
        </view>
      </view>
    </scroll-view>
    <AppTabbar current="my" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, reorder as reorderApi } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const activeStatus = ref('all')
const orders = ref([])

const statusTabs = [
  { key: 'all', label: '全部' },
  { key: 'making', label: '制作中' },
  { key: 'pending_pickup', label: '待出餐' },
  { key: 'completed', label: '已完成' },
  { key: 'cancelled', label: '已取消' }
]

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderList({ status: activeStatus.value })
    orders.value = res?.list || []
  } finally { loading.value = false }
}

function switchStatus(key) { activeStatus.value = key; loadOrders() }
function goDetail(id) { uni.navigateTo({ url: `/pages/order/detail?id=${id}` }) }
function showCode(order) { showToast(`取餐码: ${order.pickupCode}`) }
async function reorder(id) { await reorderApi(id); showToast('已加入购物车'); uni.switchTab({ url: '/pages/menu/index' }) }
function goMenu() { uni.switchTab({ url: '/pages/menu/index' }) }
function loadMore() {}

onMounted(loadOrders)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx; background: #FFF;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; }
}
.status-tabs { white-space: nowrap; padding: 20rpx 32rpx; background: #FFF; }
.tab-item {
  display: inline-block; padding: 12rpx 28rpx; font-size: 28rpx; color: #666; position: relative;
  &.active { color: #7B61FF; font-weight: 600; }
  .tab-dot { position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 8rpx; height: 8rpx; border-radius: 50%; background: #7B61FF; }
}
.order-list { height: calc(100vh - 280rpx); padding: 20rpx 32rpx; }
.order-card {
  background: #FFF; border-radius: 32rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  &.active-card { background: #7B61FF; color: #FFF; }
  &.cancelled { border: 2rpx dashed #DDD; display: flex; align-items: center; gap: 16rpx; box-shadow: none; }
}
.active-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.status-tag { background: rgba(255,255,255,0.2); padding: 6rpx 16rpx; border-radius: 20rpx; font-size: 24rpx; }
.queue-text { font-size: 24rpx; opacity: 0.9; }
.pickup-no { font-size: 36rpx; font-weight: 700; display: block; margin-bottom: 16rpx; }
.nested-card { background: #FFF; border-radius: 24rpx; padding: 20rpx; color: #333; margin-bottom: 20rpx; }
.nested-item { display: flex; justify-content: space-between; font-size: 26rpx; padding: 8rpx 0; }
.order-date { font-size: 24rpx; color: #999; display: block; margin-bottom: 16rpx; }
.item-brief { display: flex; align-items: center; gap: 12rpx; margin-bottom: 12rpx; font-size: 28rpx; }
.brief-img { width: 80rpx; height: 80rpx; border-radius: 16rpx; }
.completed-item { display: flex; align-items: center; gap: 16rpx; margin-bottom: 16rpx; }
.completed-info { flex: 1; }
.item-name { font-size: 28rpx; display: block; }
.store-name { font-size: 24rpx; color: #999; }
.item-price { font-size: 30rpx; font-weight: 600; color: #7B61FF; }
.card-actions { display: flex; gap: 16rpx; justify-content: flex-end; margin-top: 16rpx; }
.btn-outline { border: 2rpx solid #7B61FF; color: #7B61FF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx;
  .active-card & { border-color: #FFF; color: #FFF; }
}
.btn-primary { background: #7B61FF; color: #FFF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx;
  &.light { background: #EBE4FF; color: #7B61FF; }
  .active-card & { background: #FFF; color: #7B61FF; }
}
.cancelled-title { font-size: 28rpx; color: #999; display: block; }
.cancelled-note { font-size: 24rpx; color: #CCC; }
.cancelled-price { font-size: 28rpx; color: #CCC; text-decoration: line-through; }
</style>
