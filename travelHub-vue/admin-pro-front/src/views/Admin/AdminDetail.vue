<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, reactive } from 'vue';
import { findById, saveAdmin } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()
const adminView = ref({})
const originalAdminView = ref({})  // 保存原始数据，用于取消编辑时恢复
const isEditing = ref(false)  // 控制编辑模式
const adminForm = ref(null);  // 确保 adminForm 正确初始化
const headers = {
    'token': adminStore.getToken
}
const rules = reactive({
    username: [
        { required: false, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名的长度是2-20', trigger: 'blur' }
    ],
    email: [
        { required: false, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    phone: [
        { required: false, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    password: [
        { required: false, message: '请输入密码', trigger: 'blur' },
        { min: 3, max: 15, message: '密码的长度是3-15', trigger: 'blur' }
    ]
})

onMounted(() => {
    const id = route.query.id
    findById(id).then(res => {
        adminView.value = res.data
        console.log(`output->res`, adminView.value)
        originalAdminView.value = { ...res.data }  // 保存一份原始数据的副本
    })
})

const handleEdit = () => {
    isEditing.value = true;
}

const handleSave = () => {
    if (adminForm.value) {
        adminForm.value.validate((valid) => {
            if (valid) {
                saveAdmin(adminView.value).then(res => {
                    ElMessage({
                        message: '信息已更新',
                        type: 'success'
                    });
                    originalAdminView.value = { ...adminView.value }  // 更新原始数据
                    isEditing.value = false;  // 退出编辑模式
                    adminStore.saveAdmin1(adminView.value)  // 持久化修改
                }).catch(() => {
                    ElMessage({
                        message: '更新失败，请重试',
                        type: 'error'
                    });
                });
            } else {
                ElMessage.error('请确保所有输入数据都合法');
            }
        });
    }
}

const handleCancel = () => {
    adminView.value = { ...originalAdminView.value }  // 恢复到原始数据
    isEditing.value = false;  // 退出编辑模式
}

const handleBackup = () => {
    router.back()
}

const handleAvatarSuccess = (res) => {
    if (res.state > 0) {
        ElMessage.error(res.message || "系统出错");
        return new Promise(() => { });
    } else if (res.state === -1) {
        ElMessageBox.alert('登录已经过期，请重新登录', '登录过期', {
            confirmButtonText: '重新登录',
            callback: () => {
                window.location.href = "/login";
            },
        })
        return new Promise(() => { });
    } else {
        adminView.value.imagePath = res.data;
    }
}

const beforeAvatarUpload = (rawFile) => {
    if (!(rawFile.type == 'image/jpeg' || rawFile.type == 'image/jpg' || rawFile.type == 'image/png')) {
        ElMessage.error('上传图片必须是jpeg、 jpg、 png格式!')
        return false
    } else if (rawFile.size / 1024 / 1024 > 50) {
        ElMessage.error('图片的大小不能超过50M!')
        return false
    }
    return true
}
</script>

<template>
    <div class="admin-container">
        <h2 class="admin-title">管理员详情</h2>
        <div class="admin-info">
            <div class="admin-details">
                <el-form :model="adminView" :rules="rules" ref="adminForm" label-width="100px">
                    <el-form-item label="用户名:" prop="username">
                        <el-input v-if="isEditing" v-model="adminView.username" class="short-input" />
                        <span v-else>{{ adminView.username }}</span>
                    </el-form-item>
                    <el-form-item label="邮箱:" prop="email">
                        <el-input v-if="isEditing" v-model="adminView.email" class="short-input" />
                        <span v-else>{{ adminView.email }}</span>
                    </el-form-item>
                    <el-form-item label="手机号:" prop="phone">
                        <el-input v-if="isEditing" v-model="adminView.phone" class="short-input" />
                        <span v-else>{{ adminView.phone }}</span>
                    </el-form-item>
                    <el-form-item label="密码:" prop="password">
                        <el-input v-if="isEditing" v-model="adminView.password" class="short-input" />
                        <span v-else>{{ adminView.password }}</span>
                    </el-form-item>
                    <el-form-item label="角色:">
                        <el-select v-if="isEditing" v-model="adminView.role" class="short-input">
                            <el-option label="超级管理员" :value="1"></el-option>
                            <el-option label="管理员" :value="0"></el-option>
                        </el-select>
                        <span v-else>
                            <el-tag v-if="adminView.role === 1" type="success">超级管理员</el-tag>
                            <el-tag v-else type="info">管理员</el-tag>
                        </span>
                    </el-form-item>
                    <el-form-item label="创建时间:">
                        <el-date-picker v-model="adminView.createTime" type="datetime"
                            value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" readonly />
                    </el-form-item>
                    <el-form-item label="最近登录时间:">
                        <el-date-picker v-model="adminView.lastlogTime" type="datetime"
                            value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" readonly />
                    </el-form-item>
                    <el-form-item label="更新时间:">
                        <el-date-picker v-model="adminView.updateTime" type="datetime"
                            value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" readonly />
                    </el-form-item>
                </el-form>
            </div>
            <div class="admin-avatar">
                <el-upload class="avatar-uploader" action="/api/admin/upload/image" name="imagefile" :headers="headers"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                    <div class="avatar-frame">
                        <img v-if="adminView.imagePath" :src="adminView.imagePath" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </div>
                </el-upload>
            </div>
        </div>
        <div class="admin-actions">
            <el-button v-if="!isEditing" type="primary" @click="handleEdit">修改</el-button>
            <el-button v-else type="success" @click="handleSave">保存</el-button>
            <el-button v-if="isEditing" type="warning" @click="handleCancel">取消</el-button>
            <el-button @click="handleBackup">返回</el-button>
        </div>
    </div>
</template>

<style scoped>
.admin-container {
    max-width: 800px;
    margin: 50px auto;
    padding: 20px;
    border: 2px solid #dcdfe6;
    border-radius: 10px;
    background-color: #fffef3;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.admin-title {
    text-align: center;
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
}

.admin-info {
    display: flex;
    justify-content: center;
    align-items: flex-start;
    margin-bottom: 20px;
    margin-left: 50px;
}

.admin-avatar {
    flex: 0 0 300px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 20px;
}

.avatar-uploader .avatar-frame {
    width: 250px;
    height: 250px;
    border: 5px solid #409EFF;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    background-color: #fff;
}

.avatar-uploader .avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 50%;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s;
    text-align: center;
    line-height: 250px;
    background-color: #f5f7fa;
}

.avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.el-icon.avatar-uploader-icon {
    font-size: 36px;
    color: #8c939d;
}

.admin-details {
    flex: 1;
    margin-right: 20px;
}

.el-form-item {
    margin-bottom: 15px;
    font-size: 16px;
    line-height: 24px;
    color: #606266;
}

.short-input {
    max-width: 300px;
}

.admin-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.el-button {
    margin-left: 10px;
    padding: 8px 16px;
    font-size: 14px;
}
</style>
