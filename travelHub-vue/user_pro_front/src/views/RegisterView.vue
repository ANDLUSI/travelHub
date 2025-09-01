<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { save } from '@/api/register'
const router = useRouter()
import { useUserStore } from '@/stores/user'


//获取userStore对象
const store = useUserStore()

const location = [
    '北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '上海',
    '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南',
    '广东', '广西', '海南', '重庆', '四川', '贵州', '云南', '西藏', '陕西',
    '甘肃', '青海', '宁夏', '新疆', '香港', '澳门', '台湾'
]

const ruleForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    location: [],
    email: '',
    phone: ''
})

const rules = reactive({
    username: [
        { required: true, message: '请输入登录名', trigger: 'blur' },
        { min: 2, max: 20, message: '登录名长度在2到20个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 15, message: '密码长度在6到15个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value !== ruleForm.password) {
                    callback(new Error('两次输入的密码不一致'))
                } else {
                    callback()
                }
            }, trigger: 'blur'
        }
    ],
    location: [
        { required: true, message: '请选择至少一个省份', trigger: 'change' }
    ]
})

const ruleFormRef = ref(null)

const submitForm = (formEl) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            save(ruleForm).then((res) => {
                ElMessage({
                    message: res.message,
                    type: 'success'
                })
                router.push('/login')
            })
        } else {
            console.log('error submit!', fields)
        }
    })
}

const resetForm = (formEl) => {
    if (!formEl) return
    formEl.resetFields()
}

const goToLogin = () => {
    router.push('/login')
}
</script>
<template>
    <div class="register-container">
        <el-card class="register-card">
            <el-button type="text" class="back-to-login-button" @click="goToLogin">返回登录</el-button>
            <h2 class="register-title">用户注册</h2>
            <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="100px" status-icon>
                <el-form-item label="登录名" prop="username">
                    <el-input v-model="ruleForm.username" placeholder="请输入登录名" />
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input v-model="ruleForm.password" placeholder="请输入密码" show-password />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="ruleForm.confirmPassword" placeholder="请再次输入密码" show-password />
                </el-form-item>

                <el-form-item label="省份" prop="location">
                    <el-select v-model="ruleForm.location" placeholder="请选择省份">
                        <el-option v-for="location in location" :key="location" :label="location" :value="location" />
                    </el-select>
                </el-form-item>

                <br><br>
                <el-form-item class="form-actions" label-width="90">
                    <el-button type="primary" @click="submitForm(ruleFormRef)">注册</el-button>
                    <el-button @click="resetForm(ruleFormRef)">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>
<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-image: url('../assets/images/person3.jpg');
    /* 使用你上传的图片作为背景 */
    background-size: cover;
    background-position: center;
}

.register-card {
    width: 500px;
    height: 420px;
    padding: 30px;
    position: relative;
    background: rgba(255, 255, 255, 0.4);
    /* 半透明白色背景 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    /* 增加阴影效果 */
    border-radius: 10px;
    backdrop-filter: blur(3px);
    /* 背景模糊效果 */
    border: 1px solid rgba(255, 255, 255, 0.2);
    /* 细边框 */
}

.register-title {
    text-align: center;
    font-size: 28px;
    font-weight: bold;
    color: #000000;
    margin-bottom: 20px;
}

.el-form-item {
    margin-bottom: 20px;
}

.form-actions {
    display: flex;
    justify-content: space-between;
}

.back-to-login {
    text-align: center;
}

.back-to-login-button {
    position: absolute;
    top: 10px;
    left: -40px;
    font-size:20px;
    font-weight: bold;
    color: #ff0101b1;
}

.el-button {
    font-size: 16px;
    width: 38%;
    height: 40px;
}

.el-button[type="text"] {
    width: auto;
}
</style>
