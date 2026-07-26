<template>
  <div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 16px">
      <h3>📝 帖子管理</h3>
      <el-button @click="loadData">刷新</el-button>
    </div>

    <el-table :data="posts" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="authorNickname" label="作者" width="100" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" show-overflow-tooltip width="300" />
      <el-table-column prop="city" label="城市" width="80" />
      <el-table-column prop="likeCount" label="👍" width="60" />
      <el-table-column prop="collectCount" label="⭐" width="60" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-popconfirm title="确定删除此帖子？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="total"
      @current-change="loadData"
      style="margin-top: 16px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
