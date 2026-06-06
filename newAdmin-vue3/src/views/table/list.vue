<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="table-toolbar">
        <span>桌位管理</span>
        <el-button type="primary" @click="openDialog()">新增桌位</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="tableNo" label="桌号" width="100" />
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="TABLE_STATUS_MAP[row.status]" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-dropdown @command="(cmd) => updateStatus(row, cmd)">
              <el-button link>改状态</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="空闲">空闲</el-dropdown-item>
                  <el-dropdown-item command="占用">占用</el-dropdown-item>
                  <el-dropdown-item command="预约中">预约中</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑桌位' : '新增桌位'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="桌号" prop="tableNo">
          <el-input v-model="form.tableNo" />
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model="form.area" placeholder="如：大厅、VIP" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="空闲" value="空闲" />
            <el-option label="占用" value="占用" />
            <el-option label="预约中" value="预约中" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTableList, saveTable, updateTable, updateTableStatus } from '@/api/modules/table'
import { TABLE_STATUS_MAP } from '@/utils/tool'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  tableNo: '',
  area: '',
  capacity: 4,
  status: '空闲',
})

const rules = {
  tableNo: [{ required: true, message: '请输入桌号', trigger: 'blur' }],
  area: [{ required: true, message: '请输入区域', trigger: 'blur' }],
}

async function loadList() {
  loading.value = true
  try {
    const data = await getTableList({ page: 1, pageSize: 200 })
    tableData.value = data?.list || data || []
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, row || { id: null, tableNo: '', area: '', capacity: 4, status: '空闲' })
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) {
      await updateTable({ ...form })
    } else {
      await saveTable({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function updateStatus(row, status) {
  await updateTableStatus({ id: row.id, status })
  ElMessage.success('状态已更新')
  loadList()
}

onMounted(loadList)
</script>
