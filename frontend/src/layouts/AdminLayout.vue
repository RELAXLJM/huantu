<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" style="transition: width 0.3s; overflow: hidden">
      <div style="background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); height: 100%; display: flex; flex-direction: column">
        <div style="height: 64px; display: flex; align-items: center; justify-content: center; padding: 0 16px; border-bottom: 1px solid rgba(255,255,255,0.08)">
          <span v-if="!isCollapse" style="color: #fff; font-size: 20px; font-weight: 700; letter-spacing: 2px">🌍 寰途后台</span>
          <span v-else style="color: #fff; font-size: 22px">🌍</span>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="transparent"
          text-color="rgba(255,255,255,0.65)"
          active-text-color="#fff"
          router
          style="border-right: none; flex: 1; padding-top: 8px"
        >
          <el-menu-item index="/dashboard" style="margin: 4px 8px; border-radius: 8px">
            <el-icon size="18"><DataAnalysis /></el-icon>
            <template #title><span style="font-size: 14px; margin-left: 4px">数据概览</span></template>
          </el-menu-item>
          <el-menu-item index="/scenics" style="margin: 4px 8px; border-radius: 8px">
            <el-icon size="18"><Location /></el-icon>
            <template #title><span style="font-size: 14px; margin-left: 4px">景点管理</span></template>
          </el-menu-item>
          <el-menu-item index="/posts" style="margin: 4px 8px; border-radius: 8px">
            <el-icon size="18"><ChatDotSquare /></el-icon>
            <template #title><span style="font-size: 14px; margin-left: 4px">帖子管理</span></template>
          </el-menu-item>
          <el-menu-item index="/rankings" style="margin: 4px 8px; border-radius: 8px">
            <el-icon size="18"><Trophy /></el-icon>
            <template #title><span style="font-size: 14px; margin-left: 4px">榜单配置</span></template>
          </el-menu-item>
        </el-menu>

        <div style="padding: 12px; border-top: 1px solid rgba(255,255,255,0.08); text-align: center">
          <el-button text style="color: rgba(255,255,255,0.5); font-size: 18px" @click="isCollapse = !isCollapse">
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
        </div>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container style="background: #f5f7fa">
      <el-header style="background: #fff; border-bottom: 1px solid #ebeef5; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04)">
        <div style="display: flex; align-items: center; gap: 12px">
          <span style="font-size: 17px; font-weight: 600; color: #303133">{{ pageTitle }}</span>
          <el-tag size="small" type="info" effect="plain">v1.0</el-tag>
        </div>
        <div style="display: flex; align-items: center; gap: 12px">
          <el-tooltip content="刷新页面" placement="bottom">
            <el-button circle size="small" @click="refresh" :icon="Refresh" />
          </el-tooltip>
          <span v-if="loggedIn" style="color: #67c23a; font-size: 13px; cursor: pointer" @click="handleLogout">● 已授权 | 退出</span>
          <span v-else style="color: #f56c6c; font-size: 13px">● 未登录</span>
        </div>
      </el-header>

      <el-main style="padding: 24px">
        <!-- 未登录遮罩 -->
        <div v-if="!loggedIn" style="display: flex; align-items: center; justify-content: center; min-height: 400px; flex-direction: column; gap: 20px">
          <el-icon size="64" color="#c0c4cc"><Lock /></el-icon>
          <div style="font-size: 18px; color: #909399; font-weight: 500">请先登录管理后台</div>
          <div style="color: #c0c4cc; font-size: 13px">使用管理员账号登录后可查看和管理数据</div>
          <el-card shadow="never" style="width: 360px; border-radius: 12px; margin-top: 8px">
            <el-form @submit.prevent="handleLoginForm">
              <el-form-item>
                <el-input v-model="loginPhone" placeholder="手机号" size="large" clearable>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-input v-model="loginPwd" placeholder="密码" type="password" size="large" show-password @keyup.enter="handleLoginForm">
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-button type="primary" size="large" style="width: 100%" @click="handleLoginForm" :loading="loginLoading">
                登 录
              </el-button>
            </el-form>
          </el-card>
        </div>

        <!-- 已登录 → 正常显示页面 -->
        <router-view v-else />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh, Lock, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const loggedIn = ref(!!localStorage.getItem('sessionId'))

const loginPhone = ref('')
const loginPwd = ref('')
const loginLoading = ref(false)

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '')

function refresh() {
  router.go(0)
}

async function handleLoginForm() {
  if (!loginPhone.value || !loginPwd.value) return
  loginLoading.value = true
  try {
    const { default: api } = await import('../api/index.js')
    const res = await api.login(loginPhone.value, loginPwd.value)
    localStorage.setItem('sessionId', res.data.sessionId)
    loggedIn.value = true
  } catch (e) {
    alert(e.message)
  } finally {
    loginLoading.value = false
  }
}

function handleLogout() {
  localStorage.removeItem('sessionId')
  loggedIn.value = false
  loginPhone.value = ''
  loginPwd.value = ''
}
</script>

<style>
.el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.15) !important;
  border-left: 3px solid #409EFF !important;
}
.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.06) !important;
}
</style>
