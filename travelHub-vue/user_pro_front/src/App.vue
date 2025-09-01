<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Message } from '@element-plus/icons-vue'
import { findNum, fade } from '@/api/message'

const store = useUserStore()

const state = reactive({
  name: store.user.realName,
  username: store.user.username,
  imagePath: store.user.avatar,
  role: store.user.role,
  unreadCount: 0
})

const router = useRouter()
const dataInfo = reactive({
  showMenu: true,
  currentPath: '/',
})

// 控制下拉菜单的显示与隐藏
const showDropdown = ref(false)

router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') { // 修改此处
    next()
  } else {
    if (!store.getToken || store.getToken === '') {
      next({ path: '/login' })
    } else {
      next()
    }
  }
  dataInfo.showMenu = to.path !== '/login' && to.path !== '/register' // 修改此处
  dataInfo.currentPath = to.path
})

const goToPersonalCenter = () => {
  router.push('/person')
}

const handleSelect = (key) => {
  router.push(key)
  if (key !== '/message') {
    state.unreadCount = 0; // 点击导航项时清除气泡
  }
}
watch(
  () => store.token,
  (newToken, oldToken) => {
    if (newToken !== oldToken) {
      const currentRoute = router.currentRoute.value;
      if (currentRoute.name === 'home') {
        // 直接刷新整个页面
        window.location.reload();
      }
    }
  }
);

onMounted(() => {
  state.name = store.user.realName
  state.username = store.user.username
  state.imagePath = store.user.avatar
  state.role = store.user.role
  console.log(`output->store.user.id`, store.user.id)
  console.log(`output->currentPath`, store.getToken)

  if (store.getToken && store.getToken !== '') {
    findNum(store.user.id).then((res) => {
      console.log(`output-->resId--------------------------------------------`, res)
      state.unreadCount = res.data
    })
  }

})
watch(() => store.user.id, (newId) => {
  if (newId) {
    findNum(newId).then((res) => {
      console.log(`output-->resId--------------------------------------------`, res)
      state.unreadCount = res.data
    })
  }
})
watch(() => store.user.role, (newRole) => {
  state.role = newRole
})
const btnLogout = () => {
  //把token设置为空
  store.saveToken('')
  store.user.id = ''

  //跳转登录页面
  router.push({
    path: "/login"
  })
}
const goToMessageCenter = () => {
  router.push('/message')
  state.unreadCount = 0;
  fade(store.user.id).then((res) => {
    console.log(`output-->resId--------------------------------------------`, res)
  })
}
</script>

<template>
  <div>
    <el-container v-if="dataInfo.showMenu">
      <el-header class="header">
        <el-row align="middle" justify="space-between">
          <!-- 左侧网站名字 -->
          <el-col :span="3" class="site-name">
            <span class="title—top">基于SpringBoot的旅游信息交流系统</span>
          </el-col>

          <!-- 中间导航栏 -->
          <el-col :span="16">
            <el-menu mode="horizontal" :default-active="dataInfo.currentPath" @select="handleSelect" class="menu">
              <el-menu-item index="/home">
                <el-icon>
                  <House />
                </el-icon>首页</el-menu-item>
              <el-menu-item index="/guide">
                <el-icon>
                  <Memo />
                </el-icon>旅游攻略</el-menu-item>
              <el-menu-item index="/scenic/select">
                <el-icon>
                  <LocationInformation />
                </el-icon>景点探索</el-menu-item>
              <el-menu-item index="/guide/guideManager">
                <el-icon>
                  <Management />
                </el-icon>攻略管理</el-menu-item>
              <el-menu-item index="/aiChat">
                <el-icon>
                  <Management />
                </el-icon>AI助手</el-menu-item>
              <el-menu-item index="/plan">
                <el-icon>
                  <EditPen />
                </el-icon>旅行计划
              </el-menu-item>
              <el-menu-item index="/scenic">
                <el-icon>
                  <Search />
                </el-icon>景点查找</el-menu-item>
            </el-menu>
          </el-col>

          <!-- 右侧操作按钮 -->
          <el-col :span="5" class="right-section">
            <div class="icon-container" @click="goToMessageCenter">
              <el-icon>
                <Message />
              </el-icon>
              <span class="icon-text">消息</span>
              <span v-if="state.unreadCount > 0" class="badge">{{ state.unreadCount }}</span>
            </div>

            <!-- 头像和下拉菜单 -->
            <div class="user-avatar-container">
              <el-avatar :src="store.user.avatar" class="user-avatar" @click="showDropdown = !showDropdown"></el-avatar>
              <div v-show="showDropdown" class="dropdown-menu">
                <div @click="goToPersonalCenter" class="dropdown-item">个人中心</div>
                <div @click="btnLogout" class="dropdown-item">退出</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
    <div v-else>
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.header {
  background-color: #F5DEB3;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.site-name {
  display: flex;
  align-items: center;
  //font-family: 'Brush Script MT', cursive;
  font-size: 38px;
  font-weight: bold;
  //color: #ff0000;
}

.menu {
  background-color: #F5DEB3;
  display: flex;
  justify-content: flex-start;
  font-size: 16px;
  font-weight: 500;
}

.el-menu-item {
  margin: 0 15px;
  color: #333 !important;
  transition: color 0.3s ease, background-color 0.3s ease, transform 0.2s ease;
}

.el-menu-item:hover,
.el-menu-item.is-active {
  color: #D2691E !important;
  background-color: #F3C9A9 !important;
  transform: scale(1.05);
  border-bottom: 2px solid #D2691E !important;
}

.right-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.icon-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 20px;
}

.icon-container .icon-text {
  margin-left: 8px;
  color: #333;
  font-size: 16px;
  transition: color 0.3s ease;
}

.icon-container:hover .icon-text {
  color: #D2691E !important;
}

.user-avatar-container {
  position: relative;
}

.user-avatar {
  width: 40px;
  height: 40px;
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 100%;
  width: 120px;
  background: #FFFFFF;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 10;
  cursor: pointer;
}

.dropdown-item {
  padding: 10px 20px;
  font-size: 14px;
  color: #333;
  transition: background-color 0.3s ease;
}

.dropdown-item:hover {
  background-color: #F5F5F5;
}

.badge {
  background-color: red;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 10px;
  position: absolute;
  top: 18px;
  right: 60px;
  transform: translate(50%, -50%);
  display: inline-block;
  min-width: 6px;
  text-align: center;
}
.title—top{
  font-size: 20px;
  font-weight:200;
}
</style>
