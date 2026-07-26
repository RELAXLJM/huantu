<template>
  <div>
    <h3>📊 数据概览</h3>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 32px; color: #409EFF">{{ stats.users }}</div>
            <div style="color: #999; margin-top: 8px">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 32px; color: #67C23A">{{ stats.routes }}</div>
            <div style="color: #999; margin-top: 8px">生成路线</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 32px; color: #E6A23C">{{ stats.posts }}</div>
            <div style="color: #999; margin-top: 8px">社区帖子</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 32px; color: #F56C6C">{{ stats.scenics }}</div>
            <div style="color: #999; margin-top: 8px">景点数据</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>📈 快速操作</template>
      <el-space>
        <el-button type="primary" @click="syncData">同步高德景点数据</el-button>
        <el-input v-model="syncCity" placeholder="输入城市名" style="width: 150px" />
      </el-space>
      <div v-if="syncMsg" style="margin-top: 10px; color: #67C23A">{{ syncMsg }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import api from '../api/index.js'

const stats = reactive({ users: 0, routes: 0, posts: 0, scenics: 0 })
const syncCity = ref('')
const syncMsg = ref('')

// 加载统计（简化版，后续接管理API）
api.getScenics('', '').then(res => stats.scenics = res.data?.length || 0)
api.getPosts().then(res => stats.posts = res.data?.length || 0)

function syncData() {
  if (!syncCity.value) return alert('请输入城市名')
  api.syncScenic(syncCity.value).then(res => {
    syncMsg.value = res.message
  })
}
</script>
