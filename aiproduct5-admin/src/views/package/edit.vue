<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 720px">
      <el-form-item label="套餐名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="封面图">
        <ImageUpload v-model="form.image" />
      </el-form-item>
      <el-form-item label="套餐价" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="原价">
        <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="包含商品">
        <div v-for="(item, index) in form.items" :key="index" class="item-row">
          <el-select v-model="item.productId" placeholder="选择商品" style="width: 220px">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
          <el-input-number v-model="item.quantity" :min="1" style="margin: 0 8px" />
          <el-button link type="danger" @click="removeItem(index)">移除</el-button>
        </div>
        <el-button type="primary" link @click="addItem">+ 添加商品</el-button>
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="上架状态">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import { getPackageList, getProductOptions, savePackage, updatePackage } from '@/api/modules/package'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const products = ref([])
const form = reactive({
  id: null,
  name: '',
  image: '',
  price: 0,
  originalPrice: 0,
  description: '',
  status: 1,
  items: [{ productId: null, quantity: 1 }]
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入套餐价', trigger: 'blur' }]
}

function addItem() {
  form.items.push({ productId: null, quantity: 1 })
}

function removeItem(index) {
  form.items.splice(index, 1)
}

async function loadProducts() {
  const res = await getProductOptions()
  products.value = res?.list || []
}

async function loadDetail() {
  const id = route.query.id
  if (!id) return
  const res = await getPackageList({ page: 1, pageSize: 200 })
  const found = (res?.list || []).find((item) => String(item.id) === String(id))
  if (found) {
    Object.assign(form, found)
    if (!form.items?.length) form.items = [{ productId: null, quantity: 1 }]
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      ...form,
      items: form.items.map((item) => ({
        productId: item.productId,
        productName: products.value.find((p) => p.id === item.productId)?.name,
        quantity: item.quantity
      }))
    }
    if (form.id) {
      await updatePackage(payload)
    } else {
      await savePackage(payload)
    }
    ElMessage.success('保存成功')
    goBack()
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push('/package/list')
}

onMounted(async () => {
  await loadProducts()
  await loadDetail()
})
</script>

<style scoped>
.item-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
</style>
