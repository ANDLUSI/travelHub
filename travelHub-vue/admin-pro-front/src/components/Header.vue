<script setup>
import { reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import Breadcrumb from '@/components/Breadcrumb.vue'
const store = useAdminStore()
const router = useRouter()
const route = useRoute()
const state = reactive({
    name: store.admin.realName,
    username: store.admin.username, // 用户名
    hasBack: false, // 是否展示返回icon
    imagePath: store.admin.imagePath,
    role: store.admin.role,
    headerTitle: '默认标题'
})

// 根据路由设置标题
const updateHeaderTitle = () => {
    console.log(`output->route.name`, route.name)
    switch (route.name) {
        case 'home':
            state.headerTitle = '首页'
        default:
            state.headerTitle = 'TripMate管理系统'
    }
}
watch(() => store.admin.username, (newUsername) => {
    state.username = newUsername;
    console.log(`Username updated to: ${newUsername}`);
})
// 监听路由变化
watch(route, () => {
    updateHeaderTitle()

}, { immediate: true })

watch(store.admin.username, () => {
    state.username = store.admin.username
})

// 退出登录
const btnLogout = () => {
    //把token设置为空
    store.saveToken('')
    //跳转登录页面
    router.push({
        path: "/login"
    })
}
//全局后置路由守卫，组件初始化的时候调用，每次路由切换后执行(to,from) 没有next函数因为已经路由切换完毕
router.afterEach((to) => {
    const { id } = to.query
    console.log("===========afterEach 的to对象=============");
    console.log(to);
    // state.name = pathMap[to.name]
    // level2 和 level3 需要展示返回icon
    console.log('to.name', to.name)
    state.hasBack = ['level2', 'level3'].includes(to.name)
})

// 返回方法
const back = () => {
    router.back()
}
</script>
<template>
    <div class="header">
        <div class="left">
            <el-icon class="back" v-if="state.hasBack" @click="back">
                <Back />
            </el-icon>
            <span style="font-size: 24px">基于SpringBoot的旅游信息交流系统-后台管理系统</span>
        </div>
        <div class="right">
            <el-popover placement="bottom" :width="300" trigger="click" popper-class="popper-user-box" >
                <template #reference>
                    <div class="author" style="font-size: 18px; font-weight: bold;">
                        <i class="icon el-icon-s-custom" />
                        <el-avatar :src="state.imagePath"></el-avatar>
                        <p>&nbsp;</p>
                        {{ state.username || '' }}
                        <i class="el-icon-caret-bottom" />
                    </div>
                </template>
                <div class="nickname" >
                    <el-avatar :src="state.imagePath"></el-avatar>
                    <p>用户名：{{ state.username || '' }}</p>
                    <p>用户权限：{{ state.role == 1 ? '超级管理员' : '管理员' }}</p>
                    <el-tag size="small" effect="dark" class="logout" @click="btnLogout">退出</el-tag>
                </div>
            </el-popover>
        </div>

    </div>
    <Breadcrumb />
</template>

<style scoped>
.header {
    background-color: #FFFFE0;
    /* 更改背景色 */
    color: #000000b6;
    /* 字体颜色改为白色 */
    height: 60px;
    /* 增加高度 */
    border-bottom: 2px solid #66800033;
    /* 边框颜色与背景协调 */
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 30px;
    /* 增加内边距 */
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 增加阴影效果 */
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    /* 字体调整 */
}

.header .left .back {
    border: 2px solid #ffffff;
    /* 白色边框 */
    padding: 6px;
    /* 调整内边距 */
    border-radius: 50%;
    /* 圆形边框 */
    margin-right: 10px;
    /* 增加右边距 */
    cursor: pointer;
    transition: background-color 0.3s, transform 0.3s;
    /* 增加过渡效果 */
}

.header .left .back:hover {
    background-color: #004080;
    /* 悬停背景色 */
    transform: scale(1.1);
    /* 悬停放大效果 */
}

.header .left span {
    font-size: 22px;
    /* 增大字体 */
    font-weight: bold;
    /* 加粗字体 */
}

.right>div>.icon {
    font-size: 20px;
    /* 调整图标大小 */
    margin-right: 8px;
    /* 增加间距 */
}

.author {
    margin-left: 10px;
    cursor: pointer;
    display: flex;
    align-items: center;
    /* 垂直居中 */
    transition: color 0.3s;
    border: 1px solid transparent;
    /* 增加过渡效果 */

    /* 悬停时显示细细的边框，颜色为浅灰 */
}

.author:hover {
    color: #d64d03;
    /* 悬停字体颜色 */
    transform: scale(1.05);
    border-color: rgba(0, 0, 0, 0.1);
    /* 悬停时稍微放大 */
}

.el-icon-caret-bottom {
    margin-left: 6px;
    /* 调整下拉箭头的间距 */
}
.popper-user-box {
    /* 渐变背景色 */
    background-size: cover;
    border-radius: 8px;
    padding: 10px;
}



.popper-user-box .nickname .logout {
    position: absolute;
    right: 10px;
    /* 调整右边距 */
    top: 10px;
    /* 调整顶部边距 */
    cursor: pointer;
    background-color: #ff4d4f;
    /* 增加退出按钮背景色 */
    color: #ffffff;
    /* 调整字体颜色 */
    border-radius: 4px;
    /* 调整为圆角按钮 */
    padding: 4px 12px;
    /* 调整按钮内边距 */
    transition: background-color 0.3s;
    /* 增加过渡效果 */
}

.popper-user-box .nickname .logout:hover {
    background-color: #d9363e;
    /* 悬停背景色 */
}


.popper-user-box .nickname {
    position: relative;
    color: #000000;
}


</style>
