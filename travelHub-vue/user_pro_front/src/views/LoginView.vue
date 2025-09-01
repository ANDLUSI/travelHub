<script setup>
import { reactive, ref, onMounted, onUnmounted, } from 'vue'
import { login, saveTime } from '@/api/user'
import { useUserStore } from '@/stores/user'
//导入useRouter函数
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'


//获取userStore对象
const userStore = useUserStore()
//获取router对象
const router = useRouter()

const ruleForm = reactive({
    username: '',
    password: ''
})
const rules = reactive({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名的长度是2-20', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 3, max: 15, message: '密码的长度是3-15', trigger: 'blur' }
    ]
})
const saveTimeMethod = (id) => {
    saveTime(id).then((res) => {
        ElMessage({
            message: '欢迎登录！',
            type: 'success'
        });
    }).catch(error => {
        // 错误处理
        ElMessage({
            message: '保存失败，请重试。',
            type: 'error'
        });
    });
}
//获取到el-form元素的原生的DOM对象
const ruleFormRef = ref()
const submitForm = (formEl) => {
    if (!formEl) return
    // el-form元素的原生的DOM对象，然后进行验证，validate是DOM对象的函数
    formEl.validate((valid, fields) => {
        if (valid) {
            console.log('submit!')
            //axios把用户名和密码请求后端api
            login(ruleForm).then((res) => {
                console.log(`output->res`, res)
                //后端返回的token数据赋值全局变量中
                if (res.status == 0) {
                    ElMessage({
                        message: '该账户封禁中！',
                        type: 'fail'
                    });
                }
                else {
                    userStore.saveToken(res.message)
                    //保存user信息
                    userStore.saveUser(res.data)
                    saveTimeMethod(res.data.id)
                    //跳转/home页面
                    router.push({
                        path: "/home"
                    })
                }
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

const methods = {
    async onSubmit() {
        state.loginLoading = true
        try {
            await useUser.login({ ...state.form }, router.currentRoute.value.query.replace || '/')
        }
        catch (error) {

        }

        state.loginLoading = false
    },
    enterKey(event) {
        const code = event.keyCode
            ? event.keyCode
            : event.which
                ? event.which
                : event.charCode
        if (code === 13 && !state.loginLoading)
            methods.onSubmit()
    },
    onPasswordFocus() {
        state.passwordClass = 'owl password'
    },
    onPasswordBlur() {
        state.passwordClass = 'owl'
    },
}

const state = reactive({
    loginLoading: false,
    form: {
        username: 'user',
        password: 'user',
    },
    passwordClass: 'owl',
})

onMounted(() => {
    document.addEventListener('keyup', methods.enterKey)
})
onUnmounted(() => {
    document.removeEventListener('keyup', methods.enterKey)
})
</script>

<template>
    <div class="login-body">
        <div class="login-title">
            欢迎登录基于SpringBoot的旅游信息交流系统
        </div>
        <div class="login-box">
            <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" status-icon label-width="90px">
                <div id="owl" :class="state.passwordClass">
                    <div class="hand" />
                    <div class="hand hand-r" />
                    <div class="arms">
                        <div class="arm" />
                        <div class="arm arm-r" />
                    </div>
                </div>
                <div class="input-box">
                    <el-form-item label="用户名:" prop="username" style="width: 320px;font-weight: bold;">
                        <el-input id="username" v-model="ruleForm.username" type="text" placeholder="账号" />
                    </el-form-item>
                    <el-form-item label="密&nbsp&nbsp&nbsp码:" prop="password" style="width: 320px;font-weight: bold;">
                        <el-input id="password" v-model="ruleForm.password" type="password" placeholder="密码"
                            @focus="methods.onPasswordFocus" @blur="methods.onPasswordBlur" show-password />
                    </el-form-item>

                    <el-button type="primary" @click="submitForm(ruleFormRef)" class="login-button"
                        style="width: 280px;" plain>
                        登录
                    </el-button>
                </div>
            </el-form>
            <div class="register-link">
                <span>还没有账号？</span>
                <el-button type="text" @click="router.push('/register')">立即注册</el-button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-title {
    position: absolute;
    top: 10%;
    left: 50%;
    transform: translateX(-50%);
    font-size: 44px;
    color: #eccaca;
    font-weight: bold;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-family: 'Times New Roman', Times, serif;
    letter-spacing: 2px;
    z-index: 100;
    width: 1000px;
}
.register-link {
    margin-top: 20px;
    font-size: 14px;
    color: #333;
    display: flex;
    justify-content: center;
    align-items: center;
}

.register-link span {
    margin-right: 5px;
    font-weight: normal;
}

.register-link .el-button {
    padding: 0;
    font-size: 14px;
    color: #72afd3;
}

.register-link .el-button:hover {
    color: #5a9abc;
    text-decoration: underline;
}

/* 基础布局 */
.login-body {
    height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url('../assets/images/back.jpg');
    background-size: cover;
    background-position: center;
    font-family: 'Roboto', sans-serif;
    font-size: 10px;
}

.login-body .el-form,
.login-body .el-input,
.login-body .el-button {
    font-size: 17px;
}

.login-box {
    position: relative;
    width: 350px;
    padding: 30px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 10px;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    text-align: center;
}

/* 输入框样式 */
.input-box {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.input-box input {
    width: 100%;
    height: 45px;
    border-radius: 25px;
    text-indent: 20px;
    outline: none;
    border: 1px solid #ddd;
    margin-bottom: 20px;
    font-size: 16px;
}

.input-box input:focus {
    outline: none;
    border-color: #72afd3;
    box-shadow: 0 0 8px rgba(114, 175, 211, 0.4);
}

/* 按钮样式 */
.login-button {
    width: 100%;
    height: 45px;
    border-radius: 25px;
    background-color: #72afd3;
    color: #fff;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.login-button:hover {
    background-color: #5a9abc;
}

/* 标签样式 */
.el-form-item__label {
    text-align: justify;
}

.el-form-item__label:after {
    content: '';
    display: inline-block;
    width: 100%;
}

/* 猫头鹰样式 */
.owl {
    width: 211px;
    height: 108px;
    background: url("/src/assets/images/owl-login.png") no-repeat;
    position: absolute;
    top: -100px;
    left: 50%;
    transform: translateX(-50%);
}

.owl .hand {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background-color: #472d20;
    position: absolute;
    left: 12px;
    bottom: -8px;
    transform: scaleY(0.6);
    transition: 0.3s ease-out;
}

.owl .hand.hand-r {
    left: 170px;
}

.owl.password .hand {
    transform: translateX(42px) translateY(-15px) scale(0.7);
}

.owl.password .hand.hand-r {
    transform: translateX(-42px) translateY(-15px) scale(0.7);
}

.owl .arms {
    position: absolute;
    top: 58px;
    width: 100%;
    height: 41px;
    overflow: hidden;
}

.owl .arms .arm {
    width: 40px;
    height: 65px;
    position: absolute;
    left: 20px;
    top: 40px;
    background: url("/src/assets/images/owl-login-arm.png") no-repeat;
    transform: rotate(-20deg);
    transition: 0.3s ease-out;
}

.owl .arms .arm.arm-r {
    transform: rotate(20deg) scaleX(-1);
    left: 158px;
}

.owl.password .arms .arm {
    transform: translateY(-40px) translateX(40px);
}

.owl.password .arms .arm.arm-r {
    transform: translateY(-40px) translateX(-40px) scaleX(-1);
}
</style>
