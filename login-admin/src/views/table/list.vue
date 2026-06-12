<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="区域">
        <el-input v-model="query.area" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <span>桌位列表</span>
      <el-button type="primary" @click="openDialog()">新增桌位</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="tableNo" label="桌号" width="100" />
      <el-table-column prop="area" label="区域" width="120" />
      <el-table-column prop="capacity" label="容纳人数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="tableStatusMap[row.status]">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-dropdown trigger="click" @command="(cmd) => handleStatus(row.id, cmd)">
            <el-button link type="primary">改状态</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="s in statusOptions" :key="s" :command="s">{{ s }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑桌位' : '新增桌位'" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="桌号">
          <el-input v-model="form.tableNo" />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="form.area" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="form.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTableList, saveTable, updateTable, updateTableStatus } from '@/api/modules/table'
import { tableStatusMap } from '@/utils/tool'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const statusOptions = ['空闲', '占用', '预约中']
const query = reactive({ area: '', status: '', page: 1, pageSize: 100 })
const form = reactive({ id: null, tableNo: '', area: '', capacity: 4, status: '空闲' })

async function loadList() {
  loading.value = true
  try {
    const res = await getTableList(query)
    tableData.value = res?.list || res || []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadList()
}

function resetQuery() {
  query.area = ''
  query.status = ''
  loadList()
}

function openDialog(row) {
  if (row) {
    Object.assign(form, row)
  } else {
    form.id = null
    form.tableNo = ''
    form.area = ''
    form.capacity = 4
    form.status = '空闲'
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (form.id) {
    await updateTable({ ...form })
  } else {
    await saveTable({ ...form })
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}

async function handleStatus(id, status) {
  await updateTableStatus({ id, status })
  ElMessage.success('状态已更新')
  loadList()
}

onMounted(loadList)
</script>
