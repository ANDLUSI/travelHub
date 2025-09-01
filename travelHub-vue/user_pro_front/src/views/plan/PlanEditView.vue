<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { addPlan, deletePlan, findPlan } from "@/api/plan";
import { useUserStore } from '@/stores/user';

const store = useUserStore();

const newPlan = ref({
    userId: store.user.id,
    destination: '',
    startDate: '',
    endDate: '',
    vehicle: '',
    description: ''
});

const plans = ref([]);

const fetchPlans = async () => {
    try {
        findPlan(store.user.id).then((res) => {
            plans.value = res.data;
        });
    } catch (error) {
        ElMessage.error('获取计划列表失败');
    }
};

const submitPlan = async () => {
    if (newPlan.value.destination && newPlan.value.startDate && newPlan.value.endDate) {
        try {
            // 将日期对象转换为字符串格式
            const formattedPlan = {
                ...newPlan.value,
                userId: store.user.id,
                startDate: newPlan.value.startDate.toISOString().split('T')[0], // 转换为 YYYY-MM-DD 格式
                endDate: newPlan.value.endDate.toISOString().split('T')[0]      // 转换为 YYYY-MM-DD 格式
            };

            console.log(`output->formattedPlan`, formattedPlan);
            addPlan(formattedPlan).then((res) => {
                console.log(`output->newPlan`, newPlan);
                fetchPlans(); // Refresh the plan list after adding
            });
            ElMessage.success('计划已添加');
            newPlan.value = {
                destination: '',
                startDate: '',
                endDate: '',
                vehicle: '',
                description: ''
            };
        } catch (error) {
            ElMessage.error('添加计划失败');
        }
    } else {
        ElMessage.error('请填写所有必填信息');
    }
};


const handleDelete = async (id) => {
    try {
        deletePlan(id).then((res) => {
            fetchPlans(); // Refresh the plan list after deleting
        });
        ElMessage.success('计划已删除');
    } catch (error) {
        ElMessage.error('删除计划失败');
    }
};

// 弹出框的状态
const descriptionDialogVisible = ref(false);
const descriptionContent = ref('');

// 打开弹出框并显示描述内容
const openDescriptionDialog = (description) => {
    descriptionContent.value = description;
    descriptionDialogVisible.value = true;
};

onMounted(() => {
    fetchPlans(); // Fetch the list of plans when the component is mounted
});

const activePanel = ref('left'); // 默认显示左侧面板
</script>
<template>
    <div class="travel-plan-manager">
        <div class="container">
            <!-- 左侧：指定计划 -->
            <div class="side-panel left-panel" :class="{ active: activePanel === 'left' }"
                @click="activePanel = 'left'">
                <div class="content">
                    <h2>🌍 制定你的旅行计划</h2>
                    <div class="decorative-line"></div>
                    <el-form :model="newPlan" label-position="top">
                        <el-form-item label="🌐 目的地">
                            <el-input v-model="newPlan.destination" placeholder="请输入目的地"></el-input>
                        </el-form-item>
                        <el-form-item label="📅 出发日期">
                            <el-date-picker v-model="newPlan.startDate" type="date" placeholder="选择日期"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="📅 结束日期">
                            <el-date-picker v-model="newPlan.endDate" type="date" placeholder="选择日期"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="🚗 旅行方式">
                            <el-select v-model="newPlan.vehicle" placeholder="请选择旅行方式">
                                <el-option label="自驾" value="自驾"></el-option>
                                <el-option label="飞机" value="飞机"></el-option>
                                <el-option label="火车" value="火车"></el-option>
                                <el-option label="徒步" value="徒步"></el-option>
                                <el-option label="自行车" value="自行车"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="📝 描述">
                            <el-input type="textarea" v-model="newPlan.description" placeholder="请输入描述"></el-input>
                        </el-form-item>
                        <el-button type="primary" @click="submitPlan">添加计划</el-button>
                    </el-form>
                </div>
            </div>

            <!-- 右侧：计划列表 -->
            <div class="side-panel right-panel" :class="{ active: activePanel === 'right' }"
                @click="activePanel = 'right'">
                <div class="content-right">
                    <h2>📋 计划列表</h2>
                    <div class="decorative-line"></div>
                    <el-table :data="plans" stripe style="width: 100%">
                        <el-table-column prop="destination" label="目的地" width="150"></el-table-column>
                        <el-table-column prop="startDate" label="出发日期" width="150"></el-table-column>
                        <el-table-column prop="endDate" label="结束日期" width="150"></el-table-column>
                        <el-table-column prop="vehicle" label="旅行方式" width="150"></el-table-column>
                        <el-table-column label="描述" width="150" spellcheck="false">
                            <template #default="scope">
                                <div>
                                    <span v-if="scope.row.description.length <= 10">{{ scope.row.description }}</span>
                                    <span v-else>
                                        {{ scope.row.description.slice(0, 10) }}
                                        <el-tooltip class="item" effect="dark" :content="scope.row.description"
                                            placement="top">
                                            <span style="color: #409EFF; cursor: pointer;">...</span>
                                        </el-tooltip>
                                    </span>
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column label="操作" width="150">
                            <template #default="scope">
                                <div style="text-align: center">
                                    <el-button @click="handleDelete(scope.row.id)" type="danger"
                                        size="small">删除</el-button>
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>
        </div>

        <!-- 弹出框：显示描述详情 -->
        <el-dialog v-model:visible="descriptionDialogVisible" title="计划描述">
            <p>{{ descriptionContent }}</p>
            <template v-slot:footer>
                <el-button @click="descriptionDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>



<style scoped>
.travel-plan-manager {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 90vh;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    padding: 20px;
    box-sizing: border-box;
    overflow: hidden;
}

.container {
    display: flex;
    width: 80%;
    height: 100%;
    position: relative;
    overflow: hidden;
    background-image: url('/src/assets/images/person3.jpg');
    background-size: cover;
    background-position: center;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.side-panel {
    flex: 1;
    transition: flex 0.5s ease-in-out;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    overflow: hidden;
    cursor: pointer;
    border-radius: 15px;
    padding: 30px;
    box-sizing: border-box;
    overflow-y: auto;
}

.left-panel {
    background-color: rgba(255, 255, 255, 0.4);
    color: #333;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.right-panel {
    background-color: rgba(245, 245, 245, 0.4);
    color: #333;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.side-panel.active {
    flex: 3;
}

.side-panel:not(.active) {
    flex: 0.8;
}

.content {
    width: 100%;
    max-width: 400px;
    text-align: center;
    transition: opacity 0.3s ease-in-out;
}

.content-right {
    width: 100%;
    text-align: center;
    transition: opacity 0.3s ease-in-out;
}

.side-panel.active .content,
.side-panel.active .content-right {
    opacity: 1;
}

.side-panel:not(.active) .content,
.side-panel:not(.active) .content-right {
    opacity: 0.6;
}

h2 {
    margin-bottom: 20px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    font-size: 28px;
    color: #333;
}

.decorative-line {
    width: 50px;
    height: 5px;
    background-color: #ff8c00;
    margin: 20px auto;
    border-radius: 5px;
}

.el-form-item {
    margin-bottom: 20px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.el-input__inner,
.el-textarea__inner,
.el-select__inner {
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.el-button {
    background-color: #ff8c00;
    border-color: #ff8c00;
    color: #fff;
    border-radius: 8px;
    padding: 10px 20px;
    font-size: 16px;
    margin-top: 20px;
    transition: background-color 0.3s;
}

.el-button:hover {
    background-color: #ffa500;
    border-color: #ffa500;
}

.el-table {
    margin-top: 20px;
    width: 100%;
    /* 表格宽度占满右侧面板 */
}

.el-table th,
.el-table td {
    font-size: 14px;
    padding: 12px;
}

.el-button--danger {
    background-color: #e74c3c;
    border-color: #e74c3c;
    color: #fff;
    border-radius: 8px;
}

.el-button--danger:hover {
    background-color: #c0392b;
    border-color: #c0392b;
}
</style>
