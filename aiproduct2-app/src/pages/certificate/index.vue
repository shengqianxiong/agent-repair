<template>
  <view class="certificate-page page-container">
    <view class="certificate-wrapper card-box">
      <view class="certificate-title">公益捐赠证书</view>
      <view class="certificate-sub">证书编号：PUB202405240001</view>
      <view class="certificate-body">
        <text>{{ certificate.username }}</text>
        <text>先生/女士：</text>
        <text class="paragraph">
          感谢您于 {{ certificate.date }} 捐赠旧衣 {{ certificate.weight }}kg，为公益事业贡献爱心，助力环保与慈善事业发展。
        </text>
        <text class="paragraph">特颁此证，以资鼓励！</text>
      </view>
      <view class="certificate-footer">
        <view>{{ certificate.organization }}</view>
        <view>{{ certificate.date }}</view>
      </view>
      <view class="stamp">爱心认证</view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchCertificate } from '@/api'

const certificate = ref({
  username: '张',
  weight: '15.2',
  date: '2024年05月24日',
  organization: '旧衣回收公益平台'
})

async function loadCertificate() {
  try {
    const data = await fetchCertificate()
    if (data) {
      certificate.value = data
    }
  } catch (error) {
    // mock 数据兜底
  }
}

onMounted(() => {
  loadCertificate()
})
</script>

<style lang="scss" scoped>
.certificate-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.certificate-wrapper {
  margin-top: 24rpx;
  width: 100%;
  max-width: 680rpx;
  min-height: 860rpx;
  border: 4rpx solid #d9e6ff;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 18%);
  padding: 48rpx 40rpx;
  position: relative;
  box-sizing: border-box;
}

.certificate-title {
  text-align: center;
  color: #3366a9;
  font-size: 58rpx;
  font-weight: 700;
}

.certificate-sub {
  margin-top: 16rpx;
  text-align: center;
  color: #9aa3ba;
  font-size: 22rpx;
}

.certificate-body {
  margin-top: 80rpx;
  color: #374151;
  line-height: 1.8;
  font-size: 28rpx;
}

.paragraph {
  display: block;
  margin-top: 14rpx;
}

.certificate-footer {
  position: absolute;
  right: 40rpx;
  bottom: 96rpx;
  text-align: right;
  color: #5b6476;
  font-size: 24rpx;
  line-height: 1.8;
}

.stamp {
  position: absolute;
  right: 52rpx;
  bottom: 16rpx;
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(219, 70, 80, 0.6);
  color: rgba(219, 70, 80, 0.78);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28rpx;
  font-weight: 700;
  transform: rotate(-12deg);
}
</style>
