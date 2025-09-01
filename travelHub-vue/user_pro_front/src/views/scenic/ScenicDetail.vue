<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { findById } from '@/api/scenic';

const route = useRoute();
const router = useRouter();
const scenicDetail = ref({});

const colors = [
    '#FF5733', '#33FF57', '#3357FF', '#FF33A6', '#A633FF', '#33FFF7', '#FFD733', '#FF5733', '#33FF57'
];

onMounted(() => {
    const id = route.query.id;
    loadScenicDetail(id);
});

const loadScenicDetail = (id) => {
    findById(id).then((response) => {
        console.log(`output->response.data`, response.data);
        scenicDetail.value = response.data;

        // 假设 tags 是一个 JSON 字符串，我们需要先解析它
        try {
            const tagsArray = JSON.parse(scenicDetail.value.tags);
            scenicDetail.value.tags = tagsArray.map((tag, index) => ({
                name: tag,
                color: colors[index % colors.length]
            }));
        } catch (error) {
            console.error("Failed to parse tags JSON:", error);
            scenicDetail.value.tags = [];
        }
    }).catch((error) => {
        console.error("Failed to load scenic detail:", error);
    });
};

const goBack = () => {
    router.back();
};
</script>

<template>
    <div class="scenic-detail-container">
        <button @click="goBack" class="back-button">返回</button>
        <div class="scenic-image-container">
            <img :src="scenicDetail.imagePath" alt="Scenic Image" class="scenic-image" />
        </div>
        <div class="scenic-info-container">
            <h2 class="scenic-name">{{ scenicDetail.location }}</h2>
            <p class="scenic-description">{{ scenicDetail.description }}</p>
            <div class="scenic-info">
                <div class="info-item">
                    <strong>开放时间:</strong>
                    <span>{{ scenicDetail.openingHours }}</span>
                </div>
                <div class="info-item">
                    <strong>票价:</strong>
                    <span>{{ scenicDetail.ticketPrice }}</span>
                </div>
                <div class="info-item">
                    <strong>联系方式:</strong>
                    <span>{{ scenicDetail.contact }}</span>
                </div>
                <div class="info-item">
                    <strong>位置:</strong>
                    <span>{{ scenicDetail.location }}</span>
                </div>
                <div class="info-item">
                    <strong>评分:</strong>
                    <el-rate v-model="scenicDetail.stars" disabled></el-rate>
                </div>
                <div class="info-item">
                    <strong>官网:</strong>
                    <a :href="scenicDetail.www" target="_blank" class="scenic-www">{{ scenicDetail.www }}</a>
                </div>
                <div class="info-item tags">
                    <strong>标签:</strong>
                    <div class="tags-container">
                        <el-tag v-for="tag in scenicDetail.tags" :key="tag.name"
                            :style="{ backgroundColor: tag.color, color: '#fff' }">
                            {{ tag.name }}
                        </el-tag>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.scenic-detail-container {
    position: relative;
    width: 100%;
    height: 100vh;
    overflow: hidden;
}

.back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    z-index: 10;
}

.back-button:hover {
    background-color: rgba(0, 0, 0, 0.7);
}

.scenic-image-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
}

.scenic-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.scenic-info-container {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background: rgba(0, 0, 0, 0.5);
    padding: 20px;
    color: white;
    z-index: 2;
    box-shadow: 0 -4px 10px rgba(0, 0, 0, 0.5);
}

.scenic-name {
    font-size: 36px;
    font-weight: bold;
    margin-bottom: 10px;
    color: white;
}

.scenic-description {
    font-size: 18px;
    margin-bottom: 20px;
    color: white;
    max-width: 100%;
}

.scenic-info {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.info-item {
    font-size: 16px;
    color: white;
    flex: 1 1 45%;
}

.scenic-www {
    color: #1E90FF;
    text-decoration: underline;
}

.scenic-www:hover {
    color: #4682B4;
    text-decoration: none;
}

.el-rate {
    color: #ffd700;
}

.el-tag {
    margin-right: 5px;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 5px 10px;
    font-size: 14px;
}

.el-tag:hover {
    transform: scale(1.1);
    transition: transform 0.2s;
}
.info-item.tags {
    display: flex;
    align-items: center;
    gap: 10px;
    /* 控制“标签”字样与标签的间距 */
}

.tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    /* 控制标签之间的间距 */
}
</style>
