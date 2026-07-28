<template>
  <div>
    <!-- 顶部操作栏 -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px">
      <div style="display: flex; align-items: center; gap: 12px">
        <h3 style="margin: 0">🏞️ 景点管理</h3>
        <el-tag size="small" effect="plain">{{ scenics.length }} 条数据</el-tag>
      </div>
      <div style="display: flex; gap: 10px">
        <el-input v-model="searchKeyword" placeholder="搜索景点名称" style="width: 220px" clearable size="default"
          @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="syncDialogVisible = true">
          <el-icon><Download /></el-icon>从腾讯地图同步
        </el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-card shadow="never" style="border-radius: 12px">
      <el-table :data="scenics" stripe v-loading="loading" style="width: 100%" :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 600 }">
        <el-table-column prop="id" label="ID" width="65" align="center" />
        <el-table-column prop="name" label="景点名称" min-width="180">
          <template #default="{ row }">
            <span style="font-weight: 500">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="poiType" label="类型" width="85" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.poiType === 'scenic'" type="success" effect="light" size="small">🏔 景点</el-tag>
            <el-tag v-else-if="row.poiType === 'food'" type="warning" effect="light" size="small">🍜 美食</el-tag>
            <el-tag v-else-if="row.poiType === 'hotel'" effect="light" size="small">🏨 住宿</el-tag>
            <el-tag v-else-if="row.poiType === 'shopping'" type="danger" effect="light" size="small">🛍 购物</el-tag>
            <el-tag v-else effect="light" size="small">{{ row.poiType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="city" label="城市" width="80" align="center" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.rating" style="color: #E6A23C; font-weight: 600">⭐ {{ row.rating }}</span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除此景点？" confirm-button-text="删除" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="scenics.length === 0 && !loading" style="text-align: center; padding: 60px 0; color: #c0c4cc">
        <el-icon size="48"><Search /></el-icon>
        <div style="margin-top: 12px">暂无景点数据，试试从腾讯地图同步</div>
      </div>
    </el-card>

    <!-- 同步弹窗 -->
    <el-dialog v-model="syncDialogVisible" title="从腾讯地图同步景点" width="420px" destroy-on-close>
      <div style="padding: 8px 0">
        <div style="margin-bottom: 16px; color: #606266; font-size: 14px">
          输入城市名称，系统将自动调用腾讯地图 API 拉取景点、美食等 POI 数据
        </div>
        <el-input v-model="syncCity" placeholder="如：北京、上海、成都" size="large" clearable>
          <template #prefix><el-icon><Location /></el-icon></template>
        </el-input>
      </div>
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSync" :loading="syncing" size="default">
          <el-icon><Download /></el-icon> 开始同步
        </el-button>
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
  api.deleteScenic(id).then(() => loadData()).catch(e => alert(e.message))
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
