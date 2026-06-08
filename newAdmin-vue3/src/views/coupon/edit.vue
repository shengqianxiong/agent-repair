<template>
  <div class="page-card">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 560px">
      <el-form-item label="券名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" style="width: 100%">
          <el-option label="团购" value="团购" />
          <el-option label="兑换" value="兑换" />
        </el-select>
      </el-form-item>
      <el-form-item label="抵扣金额" prop="discountAmount">
        <el-input-number v-model="form.discountAmount" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="生成数量" prop="count">
        <el-input-number v-model="form.count" :min="1" :max="1000" />
      </el-form-item>
      <el-form-item label="有效期至" prop="expireTime">
        <el-date-picker
          v-model="form.expireTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择过期时间"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">生成</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateCoupon } from '@/api/modules/coupon'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const form = reactive({
  name: '',
  type: '团购',
  discountAmount: 0,
  count: 10,
  expireTime: ''
})

const rules = {
  name: [{ required: true, message: '请输入券名称', trigger: 'blur' }],
  discountAmount: [{ required: true, message: '请输入抵扣金额', trigger: 'blur' }],
  count: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  expireTime: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await generateCoupon({ ...form })
    ElMessage.success('券批次生成成功')
    goBack()
  } finally {
    submitting.value = false
  }
}

function goBack() {
  router.push('/coupon/list')
}
</script>
