<script setup>
import { reactive, onMounted, watch, Comment } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useAdminStore } from '@/stores/admin'


const store = useAdminStore()
const state = reactive({
  name: store.admin.realName,
  username: store.admin.username, // 用户名
  hasBack: false, // 是否展示返回icon
  imagePath: store.admin.avatar,
  role: store.admin.role
})
//不显示左边菜单栏、头、脚等,url路径有哪些，需要配置
const noMenu = ['/login']
const router = useRouter()
const dataInfo = reactive({
  showMenu: true,
  defaultOpen: [],
  currentPath: '/',
})
//全局前置路由守卫，初始化的时候调用，每次路由切换之前被调用
router.beforeEach((to, from, next) => {
  if (to.path == '/login') {
    // 如果路径是 /login 则正常执行
    next()
  } else {
    // 如果不是 /login，判断是否有 token
    if (!store.getToken || store.getToken == '') {
      // 如果没有token，说明没有登录过，则跳至登录页面
      next({ path: '/login' }) //相当于 router.push({path: '/login'})
    } else {
      // 否则继续执行
      next()
    }
  }
  dataInfo.showMenu = !noMenu.includes(to.path)
  dataInfo.currentPath = to.path
})
const goToPersonalCenter = () => {
  router.push({
    path: '/admin/person',

  });
}

// 初始化时加载 store 数据
onMounted(() => {
  state.name = store.admin.realName,
    state.username = store.admin.username,
    state.imagePath = store.admin.avatar,
    state.role = store.admin.role
})
watch(() => store.admin.role, (newRole) => {
  state.role = newRole
})

</script>

<template>
  <div class="layout">
    <!-- 左边菜单栏、顶部、底部-->
    <el-container v-if="dataInfo.showMenu" class="container">
      <!-- 左边菜单栏 -->
      <!-- <el-header>ddd</el-header> -->
      <!-- <el-container> -->
      <el-aside class="aside">
        <div class="head">
          <div>
            <img src="./assets/images/logo.png" alt="logo">
            <!-- <span>Admin</span> -->
          </div>
        </div>
        <div></div>
        <div class="line" style="color:white" />
        <el-menu background-color="#f7f2cd" text-color="black" :router="true" :default-openeds="dataInfo.defaultOpen"
          :default-active='dataInfo.currentPath' unique-opened>
          <el-menu-item index="/home" class="menu-item">
            <el-icon>
              <Odometer />
            </el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="2" class="menu-item">
            <template #title>
              <el-icon>
                <Avatar />
              </el-icon>
              <span>用户与权限管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/user/userManager">
                <el-icon>
                  <User />
                </el-icon>
                用户管理
              </el-menu-item>
              <el-menu-item v-if="state.role == 1" index="/admin/adminManager">
                <el-icon>
                  <UserFilled />
                </el-icon>
                管理员管理
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>
          <el-sub-menu index="3" class="menu-item">
            <template #title>
              <el-icon>
                <LocationInformation />
              </el-icon>
              <span>景点与攻略管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/scenic/scenicManager">
                <el-icon>
                  <Place />
                </el-icon>
                景点信息管理
              </el-menu-item>
              <el-menu-item index="/scenic/Add">
                <el-icon>
                  <Plus />
                </el-icon>
                景点添加
              </el-menu-item>
              <el-menu-item index="/guide/guideManager">
                <el-icon>
                  <Guide />
                </el-icon>
                攻略管理
              </el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>
          <el-menu-item index="/comment/commentManager" class="menu-item">
            <el-icon>
              <ChatLineSquare />
            </el-icon>
            <span>评论管理</span>
          </el-menu-item>
          <el-sub-menu index="4" class="menu-item">
            <template #title>
              <el-icon>
                <Notification />
              </el-icon>
              <span>网站通知管理</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="/message/Notification">
                <el-icon>
                  <ChatLineRound />
                </el-icon>
                公告设置
              </el-menu-item>
              <!-- <el-menu-item index="/message/Send">
                <el-icon>
                  <Message />
                </el-icon>
                用户通知
              </el-menu-item> -->
            </el-menu-item-group>
          </el-sub-menu>
          <el-menu-item index="/data" class="menu-item">
            <el-icon>
              <Odometer />
            </el-icon>
            <span>数据工作台</span>
          </el-menu-item>

        </el-menu>




        <div class="personal-center" @click="goToPersonalCenter">
          <el-icon>
            <House />
          </el-icon>
          <span>&nbsp;&nbsp;个人中心</span>
        </div>
      </el-aside>
      <el-container class="content">
        <!-- Header 组件-->
        <Header />
        <!-- 变化的内部部分 -->
        <div class="main">
          <!-- 显示菜单功能对应路由的 数据区 -->
          <router-view />
        </div>
        <!-- Footer 组件-->
        <Footer />
      </el-container>
      <!-- </el-container> -->
    </el-container>
    <!-- 没有 左边菜单栏、顶部、底部 ，例如：登录、注册 -->
    <el-container v-else class="container">
      <!--显示路由，除了菜单的功能之外，例如：登录、注册等等-->
      <router-view />
    </el-container>
  </div>
</template>

<style scoped>
.personal-center {
  display: flex;
  /* 使用 flex 布局 */
  align-items: center;
  justify-content: center;
  /* 垂直居中 */
  /* 垂直居中 */
  margin-top: auto;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  height: 35px;
  transition: background-color 0.3s ease, transform 0.3s ease;
  border: 1px solid #ccc;
  /* 添加细细的边框，颜色为浅灰色 */
  border-radius: 5px;
  /* 添加轻微的圆角，让边框更加柔和 */
}

.personal-center span {
  color: #000000;
  font-size: 16px;
  ;
}

.personal-center:hover {
  transform: translateY(-2px);
  /* 悬停时微微上移 */
}

.personal-center el-icon {
  margin-bottom: 5px;
  color: #127dff;
}

.personal-center:hover el-icon {
  color: #0c75f6;
  /* 悬停时图标颜色变化 */
}


.layout {
  min-height: 100vh;
  background-color: #ffffff;
}

.container {
  height: 100vh;
}

.aside {
  width: 230px !important;
  background-color: #f7f2cd;
  display: flex;
  flex-direction: column;
}

.head {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80px;
  background-color: #f7f2cd;
}

.head>div {
  display: flex;
  align-items: center;
}

.head img {
  width: 220px;
  height: 180px;
  margin-right: 10px;
}

.head span {
  font-size: 20px;
  color: #127dff;
}

.line {
  border-top: 1px solid hsla(0, 0%, 100%, .05);
  border-bottom: 1px solid rgba(0, 0, 0, .2);
}

.content {
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  overflow: hidden;
}

.main {
  height: calc(100vh - 100px);
  overflow: auto;
  padding: 10px;
  background-color: #F5FFFA;
}

.menu-item {
  border-bottom: 1px solid #dcdcdc;
  /* 添加分割线 */
}

.menu-item:last-child {
  border-bottom: none;
  /* 移除最后一个菜单项的分割线 */
}
</style>
