<template>
  <u-tabbar
    :value="current"
    :fixed="true"
    :placeholder="true"
    :safeAreaInsetBottom="true"
    activeColor="#1463ff"
    inactiveColor="#98a2b3"
    @change="onChange"
  >
    <u-tabbar-item name="task" text="任务" icon="scan"></u-tabbar-item>
    <u-tabbar-item name="records" text="记录" icon="order"></u-tabbar-item>
    <u-tabbar-item name="center" text="我的" icon="account"></u-tabbar-item>
  </u-tabbar>
</template>

<script setup>
const props = defineProps({
  current: {
    type: String,
    default: 'task'
  }
})

const tabMap = {
  task: '/pages/task/index',
  records: '/pages/user/records',
  center: '/pages/user/center'
}

function onChange(name) {
  if (name === props.current) return
  uni.switchTab({ url: tabMap[name] })
}
</script>
