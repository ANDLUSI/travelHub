<script setup>
import {useRoute,useRouter} from 'vue-router'
import { onMounted,ref } from 'vue';
import {findById} from '@/api/admin'

const router = useRouter() 
const route = useRoute()
const adminView = ref({})

onMounted(() => {
    const id = route.query.id
    console.log(`output->id`,id)
    findById(id).then(res => {
        console.log(`output->res`,res)
        adminView.value = res.data
    })
})

const handleBackup = () => {
    router.back()
}
</script>

<template>
    <div>管理员用户名：<el-input readonly v-model="adminView.username"></el-input></div>
    <div>管理员头像：<el-image style="width: 100px; height: 100px" :src="adminView.imagePath" /></div>
    <el-date-picker v-model="adminView.createTime" type="datetime" placeholder="Select date and time"
        value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" readonly />
    <div>
        <el-button type=" primary" @click="handleBackup">返回</el-button>
    </div>

</template>


<style scoped>

</style>