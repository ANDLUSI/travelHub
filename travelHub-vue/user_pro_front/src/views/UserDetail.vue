<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref, reactive } from 'vue';
import { findById, saveUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const router = useRouter()
const route = useRoute()
const userView = ref({})
const originalUserView = ref({})  // 保存原始数据，用于取消编辑时恢复
const isEditing = ref(false)  // 控制编辑模式
const userForm = ref(null);  // 确保 userForm 正确初始化
const oldPassword = ref(''); // 临时存储旧密码
const headers = {
    'token': userStore.getToken
}
const provinces = [
    "北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江",
    "上海", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南",
    "湖北", "湖南", "广东", "广西", "海南", "重庆", "四川",
    "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏",
    "新疆", "台湾", "香港", "澳门"
]

const rules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名的长度是2-20', trigger: 'blur' }
    ],
    email: [
        { required: false, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    phone: [
        { required: false, message: '请输入联系方式', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 15, message: '密码的长度是6-15', trigger: 'blur' }
    ],
    location: [
        { required: true, message: '请选择IP地址', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择用户状态', trigger: 'change' }
    ]
})
const old = ref()
onMounted(() => {
    findById(userStore.user.id).then(res => {
        userView.value = res.data
        old.value = userView.value.password 
        originalUserView.value = { ...res.data }  // 保存一份原始数据的副本
    })
})

const handleEdit = () => {
    isEditing.value = true;
}

const handleSave = () => {
    if (userForm.value) {
        userForm.value.validate((valid) => {
            if (valid) {
                // 弹出确认旧密码的提示框
                ElMessageBox.prompt('请输入旧密码进行验证', '旧密码确认', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    inputType: 'password',
                    inputValue: '',
                }).then(({ value }) => {
                    oldPassword.value = value;
                    // 这里可以加入旧密码验证逻辑
                    if (oldPassword.value !== old.value) {
                        ElMessage.error('旧密码错误，请重试');
                        return;
                    }

                    // 继续保存操作
                    saveUser(userView.value).then(res => {
                        old.value = userView.value.password 
                        ElMessage({
                            message: '信息已更新',
                            type: 'success'
                        });
                        userStore.saveUser(userView.value)
                        originalUserView.value = { ...userView.value }  // 更新原始数据
                        isEditing.value = false;  // 退出编辑模式
                    }).catch(() => {
                        ElMessage({
                            message: '更新失败，请重试',
                            type: 'error'
                        });
                    });
                }).catch(() => {
                    ElMessage({
                        message: '操作已取消',
                        type: 'info'
                    });
                });
            } else {
                ElMessage.error('请确保所有输入数据都合法');
            }
        });
    }
}

const handleCancel = () => {
    userView.value = { ...originalUserView.value }  // 恢复到原始数据
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
        userView.value.imagePath = res.data;
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
    <div class="user-container">
        <h2 class="user-title">个人中心</h2>
        <div class="user-info">
            <div class="user-details">
                <el-form :model="userView" :rules="rules" ref="userForm" label-width="100px">
                    <el-form-item label="用户名:" prop="username">
                        <el-input v-if="isEditing" v-model="userView.username" class="short-input" />
                        <span v-else>{{ userView.username }}</span>
                    </el-form-item>
                    <el-form-item label="邮箱:" prop="email">
                        <el-input v-if="isEditing" v-model="userView.email" class="short-input" />
                        <span v-else>{{ userView.email }}</span>
                    </el-form-item>
                    <el-form-item label="联系方式:" prop="phone">
                        <el-input v-if="isEditing" v-model="userView.phone" class="short-input" />
                        <span v-else>{{ userView.phone }}</span>
                    </el-form-item>
                    <el-form-item label="密码:" prop="password">
                        <el-input v-if="isEditing" v-model="userView.password" class="short-input" type="password" />
                        <span v-else>{{ '******' }}</span>
                    </el-form-item>
                    <el-form-item label="省份地址:" prop="location">
                        <el-select v-if="isEditing" v-model="userView.location" class="short-input">
                            <el-option v-for="province in provinces" :key="province" :label="province"
                                :value="province"></el-option>
                        </el-select>
                        <span v-else>{{ userView.location }}</span>
                    </el-form-item>
                    <el-form-item label="头衔:" prop="title">
                        <span>{{ userView.title }}</span>
                    </el-form-item>
                    <!-- <el-form-item label="用户状态:" prop="status">
                        <el-select v-if="isEditing" v-model="userView.status" class="short-input">
                            <el-option label="正常" :value="1"></el-option>
                            <el-option label="封禁" :value="0"></el-option>
                        </el-select>
                        <span v-else>
                            <el-tag v-if="userView.status == 1" type="success">正常</el-tag>
                            <el-tag v-else type="danger">封禁</el-tag>
                        </span>
                    </el-form-item> -->
                    <el-form-item label="注册时间:">
                        <el-date-picker v-model="userView.createTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
                            format="YYYY-MM-DD HH:mm:ss" readonly />
                    </el-form-item>
                    <el-form-item label="最近登录时间:">
                        <el-date-picker v-model="userView.lastlogTime" type="datetime"
                            value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" readonly />
                    </el-form-item>
                </el-form>
            </div>
            <div class="user-avatar">
                <el-upload class="avatar-uploader" action="/api/user/upload/image" name="imagefile" :headers="headers"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                    <div class="avatar-frame">
                        <img v-if="userView.imagePath" :src="userView.imagePath" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </div>
                </el-upload>
            </div>
        </div>
        <div class="user-actions">
            <el-button v-if="!isEditing" type="primary" @click="handleEdit">修改</el-button>
            <el-button v-else type="success" @click="handleSave">保存</el-button>
            <el-button v-if="isEditing" type="warning" @click="handleCancel">取消</el-button>
            <el-button @click="handleBackup">返回</el-button>
        </div>
    </div>
</template>

<style scoped>
.user-container {
    max-width: 800px;
    margin: 50px auto;
    padding: 20px;
    border: 2px solid #dcdfe6;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    background-image: url('../assets/images/person1.jpg');
    background-size: cover;
    background-position: center;
}

.user-title {
    text-align: center;
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
}

.user-info {
    display: flex;
    justify-content: center;
    align-items: flex-start;
    margin-bottom: 20px;
    margin-left: 50px;
}

.user-avatar {
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

.user-details {
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

.user-actions {
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
