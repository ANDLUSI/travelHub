<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, reactive } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { findById, saveScenic } from '@/api/scenic';
import { useAdminStore } from '@/stores/admin';

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();
const headers = {
    'token': adminStore.getToken
};

const ruleForm = reactive({
    spotName: '',
    location: '',
    description: '',
    openingHours: '',
    ticketPrice: '',
    province: '',
    stars: 0,
    featured: false,
    contact: '',
    imagePath: '',
    tags: []
});

const provinces = [
    '北京', '上海', '天津', '重庆', '河北', '河南', '云南', '辽宁', '黑龙江',
    '湖南', '安徽', '山东', '新疆', '江苏', '浙江', '江西', '湖北', '广西',
    '甘肃', '山西', '内蒙古', '陕西', '吉林', '福建', '贵州', '广东', '青海',
    '西藏', '四川', '宁夏', '海南', '台湾', '香港', '澳门'
];

const availableTags = [
    '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
    '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
    '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
    '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
];

onMounted(() => {
    ruleForm.id = route.query.id;
    if (ruleForm.id) {
        findById(ruleForm.id).then((res) => {
            Object.assign(ruleForm, res.data);
        });
    }
});

const handleAvatarSuccess = (res) => {
    if (res.state > 0) {
        ElMessage.error(res.message || "系统出错");
    } else if (res.state === -1) {
        ElMessageBox.alert('登录已经过期，请重新登录', '登录过期', {
            confirmButtonText: '重新登录',
            callback: () => {
                window.location.href = "/login";
            },
        });
    } else {
        ruleForm.imagePath = res.data;
    }
};

const beforeAvatarUpload = (rawFile) => {
    if (!(rawFile.type == 'image/jpeg' || rawFile.type == 'image/png')) {
        ElMessage.error('上传图片必须是 jpeg 或 png 格式!');
        return false;
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('图片的大小不能超过 2MB!');
        return false;
    }
    return true;
};

const rules = reactive({
    spotName: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
    location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
    openingHours: [{ required: true, message: '请输入开放时间', trigger: 'blur' }],
    ticketPrice: [
        { required: true, message: '票价不能为空', trigger: 'blur' },
        { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的票价（例如: 99.99）', trigger: 'blur' }
    ],
    province: [{ required: true, message: '请选择省份', trigger: 'change' }],
    stars: [{ required: true, message: '请为景点评分', trigger: 'change' }],
    contact: [
        { required: true, message: '联系方式不能为空', trigger: 'blur' },
        { pattern: /^\d{3,4}-?\d{7,8}$/, message: '请输入有效的联系方式（例如: 010-12345678）', trigger: 'blur' }
    ],
    tags: [{ required: true, message: '请选择至少一个标签', trigger: 'change' }],
    imagePath: [{ required: true, message: '请上传图片', trigger: 'change' }],
    description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
});

const ruleFormRef = ref(null);

const handleSubmitForm = (formEl) => {
    if (!formEl) return;
    formEl.validate((valid, fields) => {
        if (valid) {
            ruleForm.tags = JSON.stringify(ruleForm.tags);
            console.log(`output->ruleform`,ruleForm)
            saveScenic(ruleForm).then((res) => {
                ElMessage({
                    message: res.message,
                    type: 'success'
                });
                router.push({
                    path: '/scenic/scenicManager',
                });
            });
        } else {
            console.log('error submit!', fields);
        }
    });
};

const handleBackup = () => {
    router.back();
};
</script>

<template>
    <div class="form-container">
        <h2 class="form-title">添加景点信息</h2>
        <el-form ref="ruleFormRef" class="custom-form" :model="ruleForm" :rules="rules" label-width="120px" status-icon>

            <el-form-item label="景点名称" prop="spotName">
                <el-input v-model="ruleForm.spotName" placeholder="请输入景点名称" />
            </el-form-item>

            <el-form-item label="位置" prop="location">
                <el-input v-model="ruleForm.location" placeholder="请输入位置" />
            </el-form-item>

            <el-form-item label="开放时间" prop="openingHours">
                <el-input v-model="ruleForm.openingHours" placeholder="请输入开放时间" />
            </el-form-item>

            <el-form-item label="门票价格" prop="ticketPrice">
                <el-input v-model="ruleForm.ticketPrice" placeholder="请输入门票价格" />
            </el-form-item>

            <el-form-item label="省份" prop="province">
                <el-select v-model="ruleForm.province" placeholder="请选择省份">
                    <el-option v-for="province in provinces" :key="province" :label="province"
                        :value="province"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="星级" prop="stars">
                <el-rate v-model="ruleForm.stars"></el-rate>
            </el-form-item>

            <el-form-item label="推荐" prop="featured">
                <el-switch v-model="ruleForm.featured" :active-value="1" :inactive-value="0">
                </el-switch>
            </el-form-item>

            <el-form-item label="联系方式" prop="contact">
                <el-input v-model="ruleForm.contact" placeholder="请输入联系方式" />
            </el-form-item>

            <el-form-item label="标签" prop="tags">
                <el-select v-model="ruleForm.tags" multiple placeholder="请选择标签">
                    <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="图片" prop="imagePath">
                <el-upload class="avatar-uploader" action="/api/admin/upload/image" list-type="picture-card"
                    :headers="headers" :show-file-list="false" :on-success="handleAvatarSuccess" name="imagefile"
                    :before-upload="beforeAvatarUpload">
                    <img v-if="ruleForm.imagePath" :src="ruleForm.imagePath" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>

            <el-form-item label="描述" prop="description">
                <el-input type="textarea" v-model="ruleForm.description" :rows="3" placeholder="请输入描述" />
            </el-form-item>
        </el-form>

        <!-- 按钮容器 -->
        <div class="button-container">
            <el-button type="primary" @click="handleSubmitForm(ruleFormRef)">保存</el-button>
            <el-button @click="handleBackup">返回</el-button>
        </div>
    </div>
</template>

<style scoped>
.form-container {
    max-width: 900px;
    margin: 30px auto;
    padding: 20px;
    background-color: #fffef3;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    position: relative;
}

.form-title {
    text-align: center;
    font-size: 24px;
    color: #333;
    margin-bottom: 30px;
}

.custom-form {
    display: flex;
    flex-wrap: wrap;
}

.el-form-item {
    width: calc(50% - 20px);
    margin-bottom: 20px;
}

.el-form-item:nth-child(2n+1) {
    margin-right: 40px;
}

.el-input,
.el-select,
.el-rate,
.el-switch,
.el-upload {
    width: 100%;
}

.button-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    margin-left: auto;
    position: absolute;
    bottom: 50px;
    right: 150px;
}

.button-container .el-button {
    margin-left: 50px;
}

.avatar-uploader .avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 5px;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
    background-color: #f5f7fa;
}

.avatar-uploader .el-upload:hover {
    border-color: #626aef;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
}
</style>
