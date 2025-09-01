<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElBreadcrumb, ElBreadcrumbItem } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 路由路径到显示文字的映射表
const routeMap = {
    '/home': '首页',
    '/demo1': '示例1',
    '/admin/person': '个人中心',
    '/login': '登录',
    '/admin/detail': '用户与权限管理 / 管理员详情',
    '/admin/save': '用户与权限管理 / 保存管理员',
    '/admin/saveAdmin': '用户与权限管理 / 添加管理员',
    '/user/saveUser': '用户与权限管理 / 添加用户',
    '/admin/adminManager': '用户与权限管理 / 管理员管理',
    '/user/userManager': '用户与权限管理 / 用户管理',
    '/user/detail': '用户与权限管理 / 用户详情',
    '/scenic/scenicManager': '景点与攻略详情 / 景点管理',
    '/scenic/detail': '景点与攻略详情 / 景点详情',
    '/scenic/Add': '景点与攻略详情 / 添加景点',
    '/guide/guideManager': '景点与攻略详情 / 攻略管理',
    '/comment/commentManager': '评论管理',
    '/guide/guideDetail': '景点与攻略详情 / 攻略详情',
    '/message/Notification': '网站通知管理 / 公告设置'
}

// 生成面包屑路径数组
const breadcrumbs = computed(() => {
    const currentPath = route.path
    const breadcrumbList = []

    // 如果当前路径是首页，则只显示首页
    if (currentPath === '/home') {
        breadcrumbList.push({
            text: '首页',
            to: '/home'
        })
    } else {
        // 非首页的情况，显示“首页 -> 详情页”
        breadcrumbList.push({
            text: '首页',
            to: '/home'
        })

        breadcrumbList.push({
            text: routeMap[currentPath] || '详情页',
            to: currentPath
        })
    }

    return breadcrumbList
})

// 面包屑点击跳转
const navigateTo = (to) => {
    router.push(to)
}
</script>

<template>
    <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(breadcrumb, index) in breadcrumbs" :key="index" @click="navigateTo(breadcrumb.to)">
            <span :style="{
                cursor: breadcrumb.clickable ? 'default' : 'pointer',
                color: index === breadcrumbs.length - 1 ? '#333' : '#666',
                fontWeight: index === breadcrumbs.length - 1 ? 'bold' : 'bold',
            }" :class="{ clickable: breadcrumb.clickable }">
                {{ breadcrumb.text }}
            </span>
        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<style scoped>
.el-breadcrumb {
    padding: 10px 20px;
    background-color: #effed6;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.el-breadcrumb-item {
    font-size: 20px;
    color: #666;
    cursor:pointer;
    /* 默认情况下，光标为默认状态 */
    transition: color 0.3s ease;
}

.el-breadcrumb-item .clickable {
    cursor: pointer;
    /* 可点击项的光标变为手型 */
}

.el-breadcrumb-item .clickable:hover {
    color: #797979;
    text-decoration: underline;
    /* 可点击项悬停时显示下划线 */
}

.el-breadcrumb-item:last-child span {
    font-weight: bold;
    color: #333;
}
</style>

