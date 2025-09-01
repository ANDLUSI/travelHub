<script setup>
import { ref, onMounted } from 'vue';
import { useRoute,useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { findById, saveScenic } from '@/api/scenic'; // 假设 updateSpot 是用于更新景点信息的 API

const route = useRoute();

const router = useRouter();
const originalView = ref({})
const spotData = ref({
    spotName: '',
    location: '',
    description: '',
    openingHours: '',
    ticketPrice: '',
    province: [],
    stars: '',
    featured: '',
    contact: '',
    imagePath: '',
    updateTime: '',
    createTime: '',
    tags: '' // 新增字段用于存储标签的 JSON 字符串
});

const availableTags = [
    '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
    '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
    '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
    '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
]

const isEditing = ref(false);
const formRef = ref(null);
const selectedTags = ref([]);

onMounted(() => {
    const id = route.query.id;
    findById(id).then(res => {
        spotData.value = res.data;
        // 将 tags 字符串转换为数组
        if (spotData.value.tags) {
            console.log(`output->spot,values.tags`, spotData.value.tags)
            selectedTags.value = JSON.parse(spotData.value.tags);
            console.log(`output->spot,values.tags`, selectedTags.value)
        }
    });
});

const toggleEdit = () => {
    isEditing.value = !isEditing.value;
};

const saveChanges = async () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            spotData.value.tags = JSON.stringify(selectedTags.value);
            saveScenic(spotData.value).then(res => {
                ElMessage({
                    message: '信息已更新',
                    type: 'success'
                });
                originalView.value = { ...spotData.value }  // 更新原始数据
                isEditing.value = false;  // 退出编辑模式
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
};

const rules = {
    ticketPrice: [
        { required: true, message: '票价不能为空', trigger: 'blur' },
        { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的票价（例如: 99.99）', trigger: 'blur' }
    ]
};

const handleBack =() => {
    router.back()
}
    
</script>


<template>
    <div class="spot-detail-page">
        <!-- 景点名称和修改按钮 -->
        <header class="header">
            <h1>{{ spotData.spotName }}</h1>
            <div>
                <el-button type="primary" @click="toggleEdit">
                    {{ isEditing ? '取消' : '修改' }}
                </el-button>
                <el-button @click="handleBack">返回</el-button>
            </div>
        </header>

        <!-- 图片展示 -->
        <div class="image-container">
            <img :src="spotData.imagePath" alt="景点图片" />
        </div>

        <!-- 景点主要信息展示 -->
        <div class="spot-info">
            <el-form ref="formRef" :model="spotData" :rules="rules" label-width="120px" :disabled="!isEditing">
                <!-- 其他表单项 -->
                <el-form-item label="地点">
                    <el-input v-model="spotData.location" />
                </el-form-item>

                <!-- 标签选择 -->
                <el-form-item label="标签">
                    <el-select v-model="selectedTags" multiple placeholder="请选择标签">
                        <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="描述">
                    <el-input type="textarea" v-model="spotData.description" />
                </el-form-item>
                <el-form-item label="开放时间">
                    <el-input v-model="spotData.openingHours" />
                </el-form-item>
                <el-form-item label="票价" prop="ticketPrice">
                    <el-input v-model="spotData.ticketPrice" placeholder="请输入票价" />
                </el-form-item>
                <el-form-item label="星级">
                    <el-rate v-model="spotData.stars" />
                </el-form-item>
                <el-form-item label="联系方式">
                    <el-input v-model="spotData.contact" />
                </el-form-item>
                <el-form-item label="创建时间">
                    <el-input v-model="spotData.createTime" disabled />
                </el-form-item>
                <el-form-item label="更新时间">
                    <el-input v-model="spotData.updateTime" disabled />
                </el-form-item>
            </el-form>
        </div>

        <!-- 保存按钮 -->
        <div v-if="isEditing" class="actions">
            <el-button type="success" @click="saveChanges">保存</el-button>
            <el-button @click="toggleEdit">取消</el-button>
        </div>
    </div>
</template>


<style scoped>
.spot-detail-page {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.header h1 {
    font-size: 24px;
    font-weight: bold;
    margin: 0;
}

.image-container {
    text-align: center;
    margin-bottom: 20px;
}

.image-container img {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.spot-info {
    background-color: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.spot-info .el-form-item {
    margin-bottom: 20px;
}

.spot-info .el-form-item label {
    font-weight: bold;
}

.actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.actions .el-button {
    margin-left: 10px;
}
</style>
