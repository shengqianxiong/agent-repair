<template>
  <div class="page-card product-page">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card shadow="never" header="商品分类">
          <div class="table-toolbar">
            <el-button type="primary" size="small" @click="openCategoryDialog()">新增分类</el-button>
          </div>
          <el-tree
            :data="categoryTree"
            node-key="id"
            highlight-current
            :props="{ label: 'name', children: 'children' }"
            @node-click="onCategoryClick"
          />
        </el-card>
      </el-col>
      <el-col :span="19">
        <el-form :inline="true" :model="query" class="search-bar">
          <el-form-item label="商品名称">
            <el-input v-model="query.name" placeholder="请输入" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="table-toolbar">
          <span>商品列表</span>
          <el-button type="primary" @click="goEdit()">新增商品</el-button>
        </div>

        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              <el-image v-if="row.image" :src="row.image" style="width: 48px; height: 48px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" min-width="140" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column prop="price" label="售价" width="90">
            <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="goEdit(row.id)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadList"
          />
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="categoryVisible" title="分类" width="420px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="categoryForm.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="categoryForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategoryForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteProduct, getCategoryList, getProductList, saveCategory } from '@/api/modules/product'
import { formatMoney } from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryTree = ref([])
const categoryVisible = ref(false)
const categoryForm = reactive({ id: null, name: '', sort: 0, status: 1 })
const query = reactive({ name: '', categoryId: null, status: null, page: 1, pageSize: 10 })

async function loadCategories() {
  const list = await getCategoryList()
  categoryTree.value = (list || []).map((item) => ({ ...item, children: [] }))
}

async function loadList() {
  loading.value = true
  try {
    const res = await getProductList(query)
    tableData.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function resetQuery() {
  query.name = ''
  query.categoryId = null
  query.status = null
  query.page = 1
  loadList()
}

function onCategoryClick(node) {
  query.categoryId = node.id
  handleSearch()
}

function goEdit(id) {
  router.push({ path: '/product/edit', query: id ? { id } : {} })
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确认删除该商品？', '提示', { type: 'warning' })
  await deleteProduct(id)
  ElMessage.success('删除成功')
  loadList()
}

function openCategoryDialog() {
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.sort = 0
  categoryForm.status = 1
  categoryVisible.value = true
}

async function saveCategoryForm() {
  await saveCategory({ ...categoryForm })
  ElMessage.success('保存成功')
  categoryVisible.value = false
  loadCategories()
}

onMounted(() => {
  loadCategories()
  loadList()
})
</script>
