<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { selectFilteredScenic } from '@/api/scenic';

const store = useUserStore();
const router = useRouter();

// Scenic spot tags for filtering
const tags = [
    '全部', '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
    '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
    '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
    '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
];

// State management for selected tag and scenic spots
const selectedTag = ref('全部');
const scenicSpots = ref([]);
const sce = ref({
    tag: ''
});

// Fetch scenic spots based on selected tag
const fetchScenicSpots = () => {
    sce.value.tag = selectedTag.value === '全部' ? '' : selectedTag.value;
    console.log(`output->params`, sce.value);
    selectFilteredScenic(sce.value).then(res => {
        scenicSpots.value = res.data
        console.log(`output->`, scenicSpots.value)
    });
};

// Change filter and fetch corresponding data
const selectFilter = (tag) => {
    selectedTag.value = tag;
    fetchScenicSpots();
};

// Navigate to scenic spot detail
const goToScenicDetail = (id) => {
    router.push({
        path: `/scenic/detail`,
        query: { id }
    });
};

// Initial data fetch
onMounted(() => {
    sce.value.tag = '';
    fetchScenicSpots();
});
</script>

<template>
    <div class="scenic-selection">
        <el-main>
            <!-- Filter section -->
            <section class="filter-section">
                <h2 class="section-title">选择景点标签</h2>
                <div class="filter-options">
                    <el-row :gutter="20">
                        <el-col :span="24">
                            <div class="filter-group">
                                <span v-for="tag in tags" :key="tag" @click="selectFilter(tag)"
                                    :class="{ active: selectedTag === tag }" class="filter-item">
                                    {{ tag }}
                                </span>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </section>

            <!-- Scenic spots display -->
            <section class="scenic-list">
                <el-row :gutter="20">
                    <el-col :span="6" v-for="spot in scenicSpots" :key="spot.id">
                        <div class="scenic-card" @click="goToScenicDetail(spot.id)">
                            <img :src="spot.imagePath" class="scenic-img" />
                            <div class="scenic-info">
                                <h3 class="scenic-name">{{ spot.spotName }}</h3>
                                <p class="scenic-description">{{ spot.description }}</p>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </section>
        </el-main>
    </div>
</template>

<style scoped>
/* 整体背景样式 */
.scenic-selection {
    background-color: #e6f7ff;
    min-height: 100vh;
    padding: 20px;
}

/* 公共区域样式 */
.filter-section {
    background-color: #ffffff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group {
    display: flex;
    flex-wrap: wrap;
}

.filter-item {
    margin-right: 10px;
    margin-bottom: 10px;
    padding: 8px 16px;
    border-radius: 16px;
    background-color: #f0f0f0;
    cursor: pointer;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.filter-item.active {
    background-color: #1e90ff;
    color: #fff;
}

.filter-item:hover {
    background-color: #4682b4;
    color: #fff;
}

/* 景点卡片样式 */
.scenic-list {
    background-color: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.scenic-card {
    cursor: pointer;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.scenic-card:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.scenic-img {
    width: 100%;
    height: 200px;
    /* 固定图片高度 */
    object-fit: cover;
    border-radius: 8px;
}

.scenic-info {
    text-align: center;
    margin-top: 10px;
}

.scenic-name {
    font-size: 18px;
    font-weight: bold;
}

.scenic-description {
    font-size: 14px;
    color: #666;
}
</style>
