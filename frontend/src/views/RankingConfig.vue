<template>
  <div>
    <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 24px">
      <h3 style="margin: 0">🏆 榜单配置</h3>
      <el-input v-model="city" placeholder="城市名" style="width: 180px" size="default" clearable>
        <template #prefix><el-icon><Location /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="loadRankings" :loading="loading">
        <el-icon><Search /></el-icon> 查看榜单
      </el-button>
    </div>

    <!-- 榜单结果 -->
    <el-card v-if="rankings.length" shadow="never" style="border-radius: 12px; margin-bottom: 20px">
      <template #header>
        <div style="display: flex; align-items: center; gap: 8px">
          <el-icon color="#E6A23C" size="20"><Trophy /></el-icon>
          <span style="font-weight: 600">{{ city || '全部' }} 热门榜单 TOP{{ rankings.length }}</span>
        </div>
      </template>
      <el-table :data="rankings" stripe style="width: 100%"
        :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 600 }">
        <el-table-column label="排名" width="80" align="center">
          <template #default="{ $index }">
            <span v-if="$index === 0" style="font-size: 22px">🥇</span>
            <span v-else-if="$index === 1" style="font-size: 22px">🥈</span>
            <span v-else-if="$index === 2" style="font-size: 22px">🥉</span>
            <span v-else style="color: #909399; font-weight: 600">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="景点名称" min-width="180">
          <template #default="{ row }">
            <span style="font-weight: 500">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="poiType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.poiType === 'scenic'" type="success" effect="light" size="small">🏔 景点</el-tag>
            <el-tag v-else-if="row.poiType === 'food'" type="warning" effect="light" size="small">🍜 美食</el-tag>
            <el-tag v-else effect="light" size="small">{{ row.poiType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80" align="center">
          <template #default="{ row }">
            <el-rate :model-value="row.rating || 0" disabled show-score text-color="#E6A23C" />
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-empty v-else description="输入城市名查看该城市的 TOP 榜单" />

    <!-- 提示 -->
    <el-card shadow="never" style="border-radius: 12px">
      <template #header>
        <span style="font-weight: 600">📌 榜单运营指南</span>
      </template>
      <p style="color: #606266; line-height: 1.8; margin: 0">
        榜单按景点<span style="color: #E6A23C; font-weight: 600">评分降序</span>自动生成。
        运营人员可通过 <span style="color: #409EFF">景点管理</span> 页面为景点添加标签（如"亲子""情侣""小众"等），
        App 端即可展示分类榜单：
      </p>
      <div style="margin-top: 12px">
        <el-tag style="margin: 4px" size="default">🏅 本地人私藏TOP5</el-tag>
        <el-tag style="margin: 4px" size="default" type="success">💑 情侣约会圣地</el-tag>
        <el-tag style="margin: 4px" size="default" type="warning">👶 亲子遛娃TOP3</el-tag>
        <el-tag style="margin: 4px" size="default" type="danger">📸 拍照打卡推荐</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api/index.js'

const city = ref('广州')
const rankings = ref([])
const loading = ref(false)

function loadRankings() {
  loading.value = true
  api.getRankings(city.value).then(res => {
    rankings.value = res.data || []
  }).finally(() => loading.value = false)
}
</script>
