<template>
  <div class="image-upload">
    <el-input v-model="imageUrl" placeholder="图片 URL" clearable />
    <div v-if="imageUrl" class="preview">
      <el-image :src="imageUrl" fit="cover" style="width: 120px; height: 120px" />
    </div>
  </div>
</template>

<script setup>
const imageUrl = defineModel({ type: String, default: '' })
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.preview {
  margin-top: 8px;
}
</style>
