<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const route = useRoute()
const activeMenu = computed(() =>
  route.path.startsWith('/work-orders') ? '/work-orders' : route.path
)
</script>

<template>
  <el-config-provider :locale="zhCn">
    <el-container class="app-shell">
      <el-header class="app-header">
        <div class="brand">
          <span class="brand-mark">WO</span>
          <div class="brand-text">
            <h1>设备故障智能工单助手</h1>
            <p>报修 · 分派 · 处理 · 关闭</p>
          </div>
        </div>
        <div class="identity">
          <span class="identity-label">当前身份</span>
          <strong>张伟</strong>
        </div>
      </el-header>
      <el-container class="app-body">
        <el-aside width="208px" class="app-aside">
          <el-menu :default-active="activeMenu" router class="side-menu">
            <el-menu-item index="/work-orders">工单列表</el-menu-item>
            <el-menu-item index="/statistics">工单统计</el-menu-item>
          </el-menu>
          <div class="aside-tip">
            <p>本机演示环境</p>
            <p>数据实时保存至 SQLite</p>
          </div>
        </el-aside>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </el-config-provider>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68px;
  padding: 0 24px;
  color: #fff;
  background: linear-gradient(90deg, #1f3a5f 0%, #2c5282 100%);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: #1f3a5f;
  background: #fff;
  border-radius: 8px;
}

.brand-text h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.brand-text p {
  margin: 2px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}

.identity {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.identity-label {
  color: rgba(255, 255, 255, 0.7);
}

.app-body {
  min-height: calc(100vh - 68px);
}

.app-aside {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.side-menu {
  border-right: none;
}

.aside-tip {
  padding: 16px 20px 24px;
  font-size: 12px;
  line-height: 1.6;
  color: #909399;
}

.aside-tip p {
  margin: 0;
}

.app-main {
  padding: 20px 24px 32px;
  background: #f5f7fa;
}
</style>
