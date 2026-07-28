<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px">
      <div style="display: flex; align-items: center; gap: 12px">
        <h3 style="margin: 0">📝 帖子管理</h3>
        <el-tag size="small" effect="plain">{{ posts.length }} 条帖子</el-tag>
      </div>
      <el-button @click="loadData" :icon="Refresh">刷新</el-button>
    </div>

    <el-card shadow="never" style="border-radius: 12px">
      <el-table :data="posts" stripe v-loading="loading" style="width: 100%"
        :header-cell-style="{ background: '#fafafa', color: '#606266', fontWeight: 600 }">
        <el-table-column prop="id" label="ID" width="65" align="center" />
        <el-table-column prop="authorNickname" label="作者" width="110">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 6px">
              <el-avatar :size="28" style="background: #409EFF; font-size: 12px">
                {{ (row.authorNickname || '?')[0] }}
              </el-avatar>
              <span>{{ row.authorNickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" type="info">{{ row.city || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="互动" width="180" align="center">
          <template #default="{ row }">
            <span style="margin-right: 12px">👍 {{ row.likeCount || 0 }}</span>
            <span style="margin-right: 12px">⭐ {{ row.collectCount || 0 }}</span>
            <span>💡 {{ row.usefulCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除此帖子？" confirm-button-text="删除" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="posts.length === 0 && !loading" style="text-align: center; padding: 60px 0; color: #c0c4cc">
        <el-icon size="48"><ChatDotSquare /></el-icon>
        <div style="margin-top: 12px">暂无社区帖子</div>
      </div>
    </el-card>

    <div style="display: flex; justify-content: center; margin-top: 16px">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        layout="prev, pager, next, total"
        :total="total"
        @current-change="loadData"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import api from '../api/index.js'

const posts = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)

onMounted(() => loadData())

function loadData() {
  loading.value = true
  api.getPosts(page.value, pageSize).then(res => {
    posts.value = res.data || []
    total.value = res.data?.length || 0
  }).finally(() => loading.value = false)
}

function handleDelete(id) {
  api.deletePost(id).then(() => loadData()).catch(e => alert(e.message))
}
</script>
