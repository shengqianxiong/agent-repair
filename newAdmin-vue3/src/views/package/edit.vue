<template>
  <div class="page-container">
    <el-card shadow="never" class="form-page">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" v-loading="loading">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="套餐图片" prop="image">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="套餐价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="包含商品" prop="items">
          <div v-for="(item, index) in form.items" :key="index" class="package-item-row">
            <el-select v-model="item.productId" placeholder="选择商品" filterable style="width: 200px">
              <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
            <el-input-number v-model="item.quantity" :min="1" style="margin-left: 8px" />
            <el-button link type="danger" @click="removeItem(index)">移除</el-button>
          </div>
          <el-button type="primary" link @click="addItem">添加商品</el-button>
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="上架状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ImageUpload from '@/components/ImageUpload.vue'
import { getPackageList, savePackage, updatePackage } from '@/api/modules/package'
import { getProductList } from '@/api/modules/product'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const saving = ref(false)
const products = ref([])
const isEdit = !!route.query.id

const form = reactive({
  id: null,
  name: '',
  image: '',
  price: 0,
  originalPrice: 0,
  description: '',
  status: 1,
  items: [{ productId: null, quantity: 1 }],
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入套餐价', trigger: 'blur' }],
}

function addItem() {
  form.items.push({ productId: null, quantity: 1 })
}

function removeItem(index) {
  form.items.splice(index, 1)
}

async function loadProducts() {
  const data = await getProductList({ page: 1, pageSize: 200, status: 1 })
  products.value = data?.list || []
}

async function loadDetail() {
  if (!isEdit) return
  loading.value = true
  try {
    const data = await getPackageList({ page: 1, pageSize: 1, id: route.query.id })
    const row = data?.list?.find((item) => String(item.id) === String(route.query.id))
    if (row) Object.assign(form, row)
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit) {
      await updatePackage({ ...form })
    } else {
      await savePackage({ ...form })
    }
    ElMessage.success('保存成功')
    router.push('/package/list')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadProducts()
  await loadDetail()
})
</script>

<style scoped>
.package-item-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
</style>
