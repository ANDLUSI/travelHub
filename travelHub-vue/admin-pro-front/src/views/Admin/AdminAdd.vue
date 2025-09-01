<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, reactive } from 'vue'
import { findById, saveAdmin } from '@/api/admin'
import { useAdminStore } from '@/stores/admin'
import { ElMessageBox, ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();
const headers = {
    'token': adminStore.getToken
}

const ruleForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    phone: '',
    role: ''
})

onMounted(() => {
    ruleForm.id = route.query.id
    if (ruleForm.id) {
        findById(ruleForm.id).then((res) => {
            ruleForm.username = res.data.username
            ruleForm.imagePath = res.data.imagePath
            ruleForm.email = res.data.email
            ruleForm.phone = res.data.phone
            ruleForm.role = res.data.role
            ruleForm.id = res.data.id
        })
    }
})

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
        ruleForm.imagePath = res.data;
    }
}

const handleAvatarRemove = () => {
    ruleForm.imagePath = '';
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

const rules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名的长度是3-20', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 3, max: 15, message: '密码的长度是3-15', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value !== ruleForm.password) {
                    callback(new Error('两次输入的密码不一致'));
                } else {
                    callback();
                }
            }, trigger: 'blur'
        }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    role: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ]
})

const ruleFormRef = ref();
const handleSubmitForm = (formEl) => {
    if (!formEl) return;
    formEl.validate((valid, fields) => {
        if (valid) {
            saveAdmin(ruleForm).then((res) => {
                ElMessage({
                    message: res.message,
                    type: 'success'
                })
                window.location.reload();
            })
        } else {
            console.log('error submit!', fields)
        }
    })
}

const handleBackup = () => {
    router.back();
}
</script>
<template>
    <div class="form-container">
        <h2 class="form-title">添加管理员</h2>
        <el-form ref="ruleFormRef" class="custom-form" :model="ruleForm" :rules="rules" label-width="120px" status-icon>
            <el-form-item label="用户名" prop="username">
                <el-input v-model="ruleForm.username" placeholder="请输入用户名" />
            </el-form-item>

            <el-form-item label="密码" prop="password" v-if="!ruleForm.id">
                <el-input v-model="ruleForm.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword" v-if="!ruleForm.id">
                <el-input v-model="ruleForm.confirmPassword" type="password" show-password placeholder="请确认密码" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
                <el-input v-model="ruleForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
                <el-input v-model="ruleForm.phone" placeholder="请输入手机号" />
            </el-form-item>

            <el-form-item label="角色" prop="role">
                <el-radio-group v-model="ruleForm.role">
                    <el-radio label="1">超级管理员</el-radio>
                    <el-radio label="0">管理员</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="头像">
                <el-upload class="avatar-uploader" action="/api/admin/upload/image" name="imagefile" :headers="headers"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                    :on-remove="handleAvatarRemove">
                    <img v-if="ruleForm.imagePath" :src="ruleForm.imagePath" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                    <el-button v-if="ruleForm.imagePath" type="text" @click="handleAvatarRemove">取消上传</el-button>
                </el-upload>
            </el-form-item>

            <el-form-item class="button-container">
                <el-button type="primary" @click="handleSubmitForm(ruleFormRef)">保存</el-button>
                <el-button @click="handleBackup">返回</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>



<style scoped>
.form-container {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    border: 2px solid #626aef;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    max-height: calc(100vh - 100px);
    overflow-y: auto;
}

.form-title {
    text-align: center;
    font-size: 24px;
    color: #626aef;
    margin-bottom: 20px;
}

.custom-form {
    display: flex;
    flex-direction: column;
}

.el-form-item {
    margin-bottom: 15px;
}

.el-input,
.el-radio-group,
.el-upload {
    width: 100%;
}

.button-container {
    display: flex;
    justify-content: flex-end;
}

.button-container .el-button {
    margin-left: 10px;
    padding: 8px 16px;
    font-size: 14px;
}

.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
    border-radius: 50%;
    margin: 0 auto 10px;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
    text-align: center;
    line-height: 178px;
    margin: 0 auto;
    background-color: #f5f7fa;
}

.avatar-uploader .el-upload:hover {
    border-color: #626aef;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
}

.avatar-uploader .el-button {
    display: block;
    margin: 10px auto 0;
    font-size: 12px;
}
</style>
