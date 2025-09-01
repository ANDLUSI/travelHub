<script setup>
import { ref, onMounted } from 'vue';
import { findByName, selectFilteredScenic } from '@/api/scenic';

// 引入百度地图 API
const searchQuery = ref('');
const map = ref(null);
const localSearch = ref(null);
const scenicSpots = ref([]);
const selectedSpot = ref(null);

const initMap = () => {
    if (window.BMap) {
        // 创建地图实例
        map.value = new BMap.Map('map-container');
        const point = new BMap.Point(116.404, 39.915);
        map.value.centerAndZoom(point, 15);
        map.value.addControl(new BMap.NavigationControl());
        map.value.addControl(new BMap.ScaleControl());
        map.value.enableScrollWheelZoom(true);

        // 初始化本地搜索
        localSearch.value = new BMap.LocalSearch(map.value, {
            renderOptions: { map: map.value }
        });
    }
};

const searchPlaces = () => {
    if (searchQuery.value.trim() !== '') {
        localSearch.value.search(searchQuery.value);
        findByName({ spotName: searchQuery.value }).then((res) => {
            scenicSpots.value = res.data;
        });
    }
};

const recommendSpots = async () => {
    const currentSeason = new Date().getMonth();
    let tagsForSeason = [];

    if (currentSeason >= 2 && currentSeason <= 4) {
        tagsForSeason = ['自然风光', '徒步旅行']; // 春季
    } else if (currentSeason >= 5 && currentSeason <= 7) {
        tagsForSeason = ['海滩度假', '摄影之旅']; // 夏季
    } else if (currentSeason >= 8 && currentSeason <= 10) {
        tagsForSeason = ['徒步旅行', '探险旅游']; // 秋季
    } else {
        tagsForSeason = ['滑雪旅游', '温泉度假']; // 冬季
    }

    const [firstTagResult, secondTagResult] = await Promise.all([
        selectFilteredScenic({ tag: tagsForSeason[0] }),
        selectFilteredScenic({ tag: tagsForSeason[1] })
    ]);

    // 获取两个结果的交集
    const firstTagIds = new Set(firstTagResult.data.map(item => item.id));
    let commonResults = secondTagResult.data.filter(item => firstTagIds.has(item.id));

    // 如果搜索框有内容，对推荐景点进行名称和位置的模糊查询
    if (searchQuery.value.trim() !== '') {
        const searchTerm = searchQuery.value.trim().toLowerCase();
        commonResults = commonResults.filter(spot =>
            spot.spotName.toLowerCase().includes(searchTerm) ||
            spot.location.toLowerCase().includes(searchTerm)
        );
    }

    scenicSpots.value = commonResults;
};

const truncateDescription = (description) => {
    const maxLength = 30; // 最大字符数
    return description.length > maxLength ? description.slice(0, maxLength) + '...' : description;
};

const viewDetails = (spot) => {
    selectedSpot.value = spot;
    if (selectedSpot.value.tags) {
        selectedSpot.value.tags = JSON.parse(selectedSpot.value.tags);
    }
};

const closeModal = () => {
    selectedSpot.value = null;
};

const randomColor = () => {
    const colors = ['#FF5733', '#33FF57', '#F33FF5', '#FF33A1', '#F5FF33'];
    return colors[Math.floor(Math.random() * colors.length)];
};

onMounted(() => {
    initMap();
});
</script>




<template>
    <div class="container">
        <div class="map-search-container">
            <div class="map-box">
                <div id="search-container">
                    <input v-model="searchQuery" type="text" placeholder="搜索景点" />
                    <button @click="searchPlaces">搜索</button>
                    <button @click="recommendSpots">智能推荐</button>
                </div>
                <div id="map-container"></div>
            </div>
        </div>
        <div class="info-panel">
            <div class="list-box">
                <h2>景点列表</h2>
                <ul class="spot-list">
                    <li v-for="spot in scenicSpots" :key="spot.spotName" class="spot-item">
                        <img :src="spot.imagePath" alt="景点图片" class="spot-image" />
                        <div class="spot-details">
                            <h3 class="spot-name">{{ spot.spotName }}</h3>
                            <p class="spot-location">{{ spot.location }}</p>
                            <p class="spot-description">{{ truncateDescription(spot.description) }}</p>
                            <button @click="viewDetails(spot)" class="details-button">查看详情</button>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <div v-if="selectedSpot" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <button @click="closeModal" class="modal-close">返回</button>
                <h2>{{ selectedSpot.spotName }}</h2>
                <img :src="selectedSpot.imagePath" alt="景点图片" class="modal-image" />
                <div class="modal-body">
                    <p><strong>位置：</strong>{{ selectedSpot.location }}</p>
                    <p><strong>描述：</strong>{{ selectedSpot.description }}</p>
                    <strong>官网：</strong><a :href="selectedSpot.www" target="_blank">{{ selectedSpot.www }}</a>
                    <p><strong>联系方式：</strong>{{ selectedSpot.contact }}</p>
                    <p><strong>评分：</strong>
                        <span v-for="n in selectedSpot.stars" :key="n" class="star">★</span>
                    </p>
                    <p><strong>票价：</strong>{{ selectedSpot.ticketPrice }}元</p>
                    <p><strong>开放时间：</strong>{{ selectedSpot.openingHour }}</p>
                    <p><strong>标签：</strong>
                        <span v-for="(tag, index) in selectedSpot.tags" :key="index"
                            :style="{ color: randomColor(), marginLeft: '15px' }">
                            {{ tag }}
                        </span>
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>


<style scoped>
.container {
    display: flex;
    align-items: flex-start;
    padding: 20px;
    background-color: #f0f0f0;
}

.map-search-container {
    width: 70%;
    margin-right: 20px;
}

.map-box {
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    padding: 10px;
    display: flex;
    flex-direction: column;
}

#search-container {
    margin-bottom: 10px;
    text-align: left;
    display: flex;
    align-items: center;
}

#search-container input {
    padding: 8px;
    font-size: 16px;
    width: calc(50% - 120px);
    margin-right: 20px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

#search-container button {
    padding: 8px 16px;
    font-size: 16px;
    cursor: pointer;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s;
    margin-right: 100px;
    /* 增加间隔 */
}

#search-container button:last-child {
    margin-right: 0;
    /* 使最后一个按钮没有右边距 */
}

#search-container button:hover {
    background-color: #0056b3;
}

#map-container {
    width: 100%;
    height: 500px;
}

.info-panel {
    width: 30%;
}

.list-box {
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    padding: 10px;
    height: 100%;
}

.no-results {
    text-align: center;
    font-size: 18px;
    color: #888;
}

.spot-list {
    list-style: none;
    padding: 0;
    margin: 0;
    max-height: 475px;
    overflow-y: auto;
}

.spot-item {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.spot-image {
    width: 120px;
    height: 120px;
    object-fit: cover;
    margin-right: 15px;
}

.spot-details {
    flex: 1;
}

.spot-name {
    font-size: 18px;
    margin: 0;
}

.spot-location {
    color: #666;
    margin: 5px 0;
}

.spot-description {
    color: #444;
}

.details-button {
    margin-top: 10px;
    padding: 6px 12px;
    font-size: 14px;
    cursor: pointer;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    max-width: 600px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
    position: relative;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-body {
    margin-top: 50px;
    /* 留出返回按钮的空间 */
}

.modal-image {
    width: 100%;
    height: auto;
    margin-bottom: 15px;
}

.modal-close {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    font-size: 14px;
    cursor: pointer;
    background-color: #dc3545;
    color: white;
    border: none;
    border-radius: 4px;
}

.star {
    color: #ffd700;
    /* 金色星星 */
    font-size: 20px;
}
</style>
