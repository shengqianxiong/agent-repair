<template>
  <div class="image-upload">
    <el-input :model-value="modelValue" placeholder="图片 URL" clearable @update:model-value="emit('update:modelValue', $event)" />
    <div v-if="modelValue" class="preview">
      <el-image :src="modelValue" fit="cover" style="width: 120px; height: 120px" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: String, default: '' }
})
defineEmits(['update:modelValue'])
</script>

<style scoped>
.image-upload {
  width: 100%;
}
.preview {
  margin-top: 8px;
}
</style>
