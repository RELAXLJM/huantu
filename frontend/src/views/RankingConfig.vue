<template>
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 16px; align-items: center">
      <h3>🏆 榜单配置</h3>
      <el-input v-model="city" placeholder="城市名，如：广州" style="width: 150px" />
      <el-button type="primary" @click="loadRankings">查看榜单</el-button>
    </div>

    <el-card v-if="rankings.length">
      <template #header>{{ city || '全部' }} 必玩榜单 TOP5</template>
      <el-table :data="rankings" stripe style="width: 100%">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="name" label="景点" width="180" />
        <el-table-column prop="poiType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.poiType === 'scenic'" type="success">景点</el-tag>
            <el-tag v-else-if="row.poiType === 'food'" type="warning">美食</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-empty v-else description="输入城市名查看榜单" />

    <el-divider />

    <el-card>
      <template #header>📌 运营提示</template>
      <p style="color: #999">
        榜单数据来源于景点评分排序。运营人员可通过"景点管理"页面调整景点标签，
        如添加"亲子""情侣"等标签，即可在App端展示分类榜单：
      </p>
      <el-tag style="margin: 4px">本地人私藏TOP5</el-tag>
      <el-tag style="margin: 4px" type="success">情侣约会圣地</el-tag>
      <el-tag style="margin: 4px" type="warning">亲子遛娃TOP3</el-tag>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api/index.js'

const city = ref('广州')
const rankings = ref([])

function loadRankings() {
  api.getRankings(city.value).then(res => {
    rankings.value = res.data || []
  })
}
</script>
