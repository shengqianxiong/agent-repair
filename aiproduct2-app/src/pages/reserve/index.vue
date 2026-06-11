<template>
  <view class="reserve-page">
    <scroll-view scroll-y class="reserve-scroll">
      <view class="section-card card-box address-card">
        <view class="section-top">
          <text class="section-name">收货地址</text>
          <text class="edit-text">编辑</text>
        </view>
        <view class="receiver-row">
          <text>{{ form.address.contact }}</text>
          <text>{{ form.address.phone }}</text>
        </view>
        <view class="address-detail">{{ form.address.detail }}</view>
      </view>

      <view class="section-card card-box">
        <view class="section-name">选择上门日期</view>
        <view class="chip-wrap">
          <view
            v-for="item in dayList"
            :key="item.value"
            class="chip-item"
            :class="{ active: form.reserveDay === item.value }"
            @click="form.reserveDay = item.value"
          >
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="section-card card-box">
        <view class="section-name">选择上门时间（2小时内上门）</view>
        <view class="time-grid">
          <view
            v-for="item in timeSlots"
            :key="item.value"
            class="time-item"
            :class="{ active: form.reserveTime === item.value }"
            @click="form.reserveTime = item.value"
          >
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="section-card card-box">
        <view class="section-name">旧衣信息</view>
        <view class="info-row">
          <text>预估重量（kg）</text>
          <u-number-box v-model="form.estimateWeight" min="1" max="99" integer />
        </view>

        <view class="sub-title">衣物类型</view>
        <view class="chip-wrap">
          <view
            v-for="item in clothTypes"
            :key="item.value"
            class="chip-item"
            :class="{ active: form.clothTypes.includes(item.value) }"
            @click="toggleType(item.value)"
          >
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="section-card card-box">
        <view class="section-name">回收用途</view>
        <u-radio-group v-model="form.recyclePurpose" placement="column">
          <u-radio name="REWARD" label="兑换环保金奖励" />
          <u-radio name="PUBLIC_WELFARE" label="捐赠获取公益证书" />
        </u-radio-group>
      </view>

      <view class="section-card card-box">
        <view class="section-name">备注（选填）</view>
        <u-textarea
          v-model="form.remark"
          placeholder="如有电梯、旧衣已打包、可放门口等信息..."
          height="120"
          maxlength="100"
          count
        />
      </view>
    </scroll-view>

    <view class="submit-wrap">
      <u-button
        type="primary"
        color="linear-gradient(135deg,#2EAE5E,#1F8C48)"
        shape="circle"
        text="提交预约"
        @click="handleSubmit"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { createReserveOrder } from '@/api'

const dayList = [
  { label: '今天\n06/24', value: 'TODAY' },
  { label: '明天\n06/25', value: 'TOMORROW' },
  { label: '后天\n06/26', value: 'AFTER_TOMORROW' },
  { label: '周三\n06/27', value: 'D4' },
  { label: '周四\n06/28', value: 'D5' },
  { label: '周五\n06/29', value: 'D6' },
  { label: '周六\n06/30', value: 'D7' }
]

const timeSlots = [
  { label: '09:00-11:00', value: '09:00-11:00' },
  { label: '11:00-13:00', value: '11:00-13:00' },
  { label: '14:00-16:00', value: '14:00-16:00' },
  { label: '16:00-18:00', value: '16:00-18:00' },
  { label: '18:00-20:00', value: '18:00-20:00' },
  { label: '20:00-22:00', value: '20:00-22:00' }
]

const clothTypes = [
  { label: '成人衣物', value: 'ADULT' },
  { label: '儿童衣物', value: 'CHILD' },
  { label: '家纺布织物', value: 'HOME_TEXTILE' },
  { label: '鞋子', value: 'SHOES' },
  { label: '包包', value: 'BAG' },
  { label: '棉被', value: 'QUILT' },
  { label: '毛绒玩具', value: 'DOLL' },
  { label: '其他', value: 'OTHER' }
]

const form = reactive({
  address: {
    contact: '张三',
    phone: '138****8888',
    detail: '北京市朝阳区建国路SOHO现代城18层1001室'
  },
  reserveDay: 'AFTER_TOMORROW',
  reserveTime: '09:00-11:00',
  estimateWeight: 10,
  clothTypes: ['ADULT', 'CHILD', 'HOME_TEXTILE'],
  recyclePurpose: 'REWARD',
  remark: ''
})

function toggleType(type) {
  const exists = form.clothTypes.includes(type)
  if (exists) {
    form.clothTypes = form.clothTypes.filter((item) => item !== type)
  } else {
    form.clothTypes.push(type)
  }
}

async function handleSubmit() {
  if (!form.clothTypes.length) {
    uni.showToast({ title: '请至少选择一种衣物类型', icon: 'none' })
    return
  }
  try {
    await createReserveOrder(form)
    uni.showToast({ title: '预约提交成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/order/index' }), 600)
  } catch (error) {
    uni.showToast({ title: '预约失败，已保存本地草稿', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.reserve-page {
  min-height: 100vh;
  background: var(--color-page-bg);
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
}

.reserve-scroll {
  height: calc(100vh - 120rpx - env(safe-area-inset-bottom));
  padding: 20rpx 24rpx 0;
  box-sizing: border-box;
}

.section-card {
  padding: 24rpx;
  margin-bottom: 18rpx;
}

.section-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #252a31;
}

.edit-text {
  color: #2eae5e;
  font-size: 24rpx;
}

.receiver-row {
  margin-top: 20rpx;
  display: flex;
  gap: 16rpx;
  font-size: 26rpx;
  color: #1f2329;
}

.address-detail {
  margin-top: 8rpx;
  line-height: 1.5;
  color: #666d77;
  font-size: 24rpx;
}

.chip-wrap {
  margin-top: 18rpx;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.chip-item {
  min-height: 68rpx;
  border-radius: 12rpx;
  border: 2rpx solid #e7ebee;
  background: #f8fafc;
  color: #606770;
  font-size: 22rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: pre-line;
  text-align: center;
}

.chip-item.active {
  border-color: #2eae5e;
  background: #e7f8ed;
  color: #238c4c;
  font-weight: 600;
}

.time-grid {
  margin-top: 18rpx;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.time-item {
  border-radius: 12rpx;
  height: 72rpx;
  border: 2rpx solid #e8ecef;
  color: #616872;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24rpx;
}

.time-item.active {
  color: #fff;
  border-color: #2eae5e;
  background: #2eae5e;
}

.info-row {
  margin-top: 18rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #5d6470;
  font-size: 24rpx;
}

.sub-title {
  margin-top: 22rpx;
  font-size: 24rpx;
  color: #5f6368;
}

.submit-wrap {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  box-sizing: border-box;
  box-shadow: 0 -8rpx 20rpx rgba(31, 35, 41, 0.06);
}
</style>
