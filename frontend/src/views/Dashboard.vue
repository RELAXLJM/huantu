<template>
  <div>
    <!-- 欢迎区 -->
    <div style="background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%); border-radius: 12px; padding: 28px 32px; color: #fff; margin-bottom: 24px">
      <div style="font-size: 22px; font-weight: 700; margin-bottom: 6px">👋 欢迎回来</div>
      <div style="font-size: 14px; opacity: 0.85">寰途旅游管理后台 — 数据概览</div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66b1ff)">
            <el-icon size="24" color="#fff"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95d475)">
            <el-icon size="24" color="#fff"><MapLocation /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.routes }}</div>
            <div class="stat-label">生成路线</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #f3d19e)">
            <el-icon size="24" color="#fff"><ChatDotSquare /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.posts }}</div>
            <div class="stat-label">社区帖子</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #fab6b6)">
            <el-icon size="24" color="#fff"><Location /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.scenics }}</div>
            <div class="stat-label">景点数据</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 同步工具 -->
    <div style="display: flex; gap: 20px; margin-top: 24px">
      <el-card shadow="never" style="flex: 1; border-radius: 12px">
        <template #header>
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#409EFF" size="18"><Download /></el-icon>
            <span style="font-weight: 600">从腾讯地图同步景点</span>
          </div>
        </template>
        <div style="display: flex; gap: 12px; align-items: center">
          <el-input v-model="syncCity" placeholder="输入城市名，如：北京" size="large" style="width: 220px" clearable>
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" size="large" @click="syncData" :loading="syncing">
            开始同步
          </el-button>
        </div>
        <div v-if="syncMsg" style="margin-top: 12px; padding: 10px 14px; background: #f0f9eb; border-radius: 8px; color: #67C23A; font-size: 13px">
          ✅ {{ syncMsg }}
        </div>
        <div style="margin-top: 12px; color: #909399; font-size: 12px">
          使用腾讯地图 WebService API 自动拉取景点、美食、购物等 POI 数据
        </div>
      </el-card>

      <el-card shadow="never" style="flex: 1; border-radius: 12px">
        <template #header>
          <div style="display: flex; align-items: center; gap: 8px">
            <el-icon color="#67C23A" size="18"><InfoFilled /></el-icon>
            <span style="font-weight: 600">系统信息</span>
          </div>
        </template>
        <el-descriptions :column="1" size="small" border>
          <el-descriptions-item label="后端框架">Spring Boot 4.0.7</el-descriptions-item>
          <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
          <el-descriptions-item label="缓存">Redis</el-descriptions-item>
          <el-descriptions-item label="地图服务">腾讯地图 API</el-descriptions-item>
          <el-descriptions-item label="API 地址">http://localhost:8080</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import api from '../api/index.js'

const stats = reactive({ users: 0, routes: 0, posts: 0, scenics: 0 })
const syncCity = ref('')
const syncMsg = ref('')
const syncing = ref(false)

api.getScenics('', '').then(res => stats.scenics = res.data?.length || 0)
api.getPosts().then(res => stats.posts = res.data?.length || 0)

function syncData() {
  if (!syncCity.value) return alert('请输入城市名')
  syncing.value = true
  syncMsg.value = ''
  api.syncScenic(syncCity.value).then(res => {
    syncMsg.value = res.message
    api.getScenics('', '').then(r => stats.scenics = r.data?.length || 0)
  }).finally(() => syncing.value = false)
}
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}
</style>
