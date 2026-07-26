<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center">
        <h2 style="color: #fff; font-size: 18px">🌍 寰途后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/scenics">
          <el-icon><Location /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="/posts">
          <el-icon><Document /></el-icon>
          <span>帖子管理</span>
        </el-menu-item>
        <el-menu-item index="/rankings">
          <el-icon><Trophy /></el-icon>
          <span>榜单配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header style="border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between">
        <span style="font-size: 16px; font-weight: bold">{{ pageTitle }}</span>
        <div>
          <el-button @click="handleLogin" type="primary" size="small" v-if="!loggedIn">登录</el-button>
          <span v-else style="color: #666">管理员</span>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const loggedIn = ref(!!localStorage.getItem('sessionId'))

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '')

function handleLogin() {
  // 简单登录弹框
  const phone = prompt('手机号')
  const pwd = prompt('密码')
  if (phone && pwd) {
    import('../api/index.js').then(({ default: api }) => {
      api.login(phone, pwd).then(res => {
        localStorage.setItem('sessionId', res.data.sessionId)
        loggedIn.value = true
        location.reload()
      }).catch(e => alert(e.message))
    })
  }
}
</script>
