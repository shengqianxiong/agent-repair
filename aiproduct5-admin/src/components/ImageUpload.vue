<template>
  <div class="image-upload">
    <div v-if="limit > 1" class="multi-upload">
      <div v-for="(item, index) in imageList" :key="index" class="upload-item">
        <el-image v-if="item" :src="item" fit="cover" class="preview-img" />
        <div v-else class="upload-placeholder">
          <el-icon><Plus /></el-icon>
        </div>
        <el-input
          :model-value="item"
          placeholder="图片 URL"
          size="small"
          @update:model-value="(val) => updateImage(index, val)"
        />
        <el-button v-if="imageList.length > 1" link type="danger" @click="removeImage(index)">删除</el-button>
      </div>
      <el-button v-if="imageList.length < limit" type="primary" plain @click="addImage">
        <el-icon><Plus /></el-icon> 添加图片
      </el-button>
    </div>
    <div v-else class="single-upload">
      <el-input :model-value="modelValue" placeholder="图片 URL" clearable @update:model-value="emit('update:modelValue', $event)" />
      <div v-if="modelValue" class="preview">
        <el-image :src="modelValue" fit="cover" style="width: 120px; height: 120px; border-radius: 8px" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: [String, Array], default: '' },
  limit: { type: Number, default: 1 }
})

const emit = defineEmits(['update:modelValue'])

const imageList = computed({
  get() {
    if (props.limit > 1) {
      return Array.isArray(props.modelValue) ? props.modelValue : props.modelValue ? [props.modelValue] : ['']
    }
    return []
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

function updateImage(index, val) {
  const list = [...imageList.value]
  list[index] = val
  emit('update:modelValue', list.filter(Boolean).length ? list : [''])
}

function addImage() {
  if (imageList.value.length < props.limit) {
    emit('update:modelValue', [...imageList.value, ''])
  }
}

function removeImage(index) {
  const list = imageList.value.filter((_, i) => i !== index)
  emit('update:modelValue', list.length ? list : [''])
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.multi-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-item {
  width: 160px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.preview-img {
  width: 120px;
  height: 120px;
  border-radius: var(--dp-radius-md);
  border: 1px solid var(--dp-border);
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed var(--dp-border);
  border-radius: var(--dp-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--dp-text-secondary);
  font-size: 24px;
}

.single-upload .preview {
  margin-top: 8px;
}
</style>
