<template>
  <div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 16px">
      <h3>🏞️ 景点管理</h3>
      <el-space>
        <el-input v-model="searchKeyword" placeholder="搜索景点" style="width: 200px" clearable />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="syncDialogVisible = true">从高德同步</el-button>
      </el-space>
    </div>

    <el-table :data="scenics" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" width="180" />
      <el-table-column prop="poiType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.poiType === 'scenic'" type="success">景点</el-tag>
          <el-tag v-else-if="row.poiType === 'food'" type="warning">美食</el-tag>
          <el-tag v-else>{{ row.poiType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="city" label="城市" width="80" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 同步弹窗 -->
    <el-dialog v-model="syncDialogVisible" title="从高德同步景点" width="400px">
      <el-input v-model="syncCity" placeholder="输入城市名，如：北京" />
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSync" :loading="syncing">开始同步</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/index.js'

const scenics = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const syncCity = ref('')
const syncDialogVisible = ref(false)
const syncing = ref(false)

onMounted(() => loadData())

function loadData() {
  loading.value = true
  api.getScenics('', searchKeyword.value || '').then(res => {
    scenics.value = res.data || []
  }).finally(() => loading.value = false)
}

function handleDelete(id) {
  api.deleteScenic(id).then(() => {
    loadData()
  }).catch(e => alert(e.message))
}

function handleSync() {
  if (!syncCity.value) return alert('请输入城市名')
  syncing.value = true
  api.syncScenic(syncCity.value).then(res => {
    alert(res.message)
    syncDialogVisible.value = false
    loadData()
  }).finally(() => syncing.value = false)
}
</script>
