<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { selectHotGuide, selectHotScenic, selectNearScenic } from '@/api/scenic';
import { addView } from '@/api/guide';
import { ArrowRight, ArrowLeft } from '@element-plus/icons-vue';

const store = useUserStore();

// 轮播图的热门景点数据
const bannerImages = ref([]);

// 所有景点数据和显示的随机景点数据
const allDestinations = reactive([]);
const destinations = reactive([]);

// 周边游数据
const nearbyTrips = reactive([]);
const allNearbyTrips = reactive([]);
// 攻略推荐数据
const recommendations = reactive([]);

// 控制侧边栏的显示与隐藏
const isSidebarVisible = ref(false);

// 路由器实例
const router = useRouter();

const toggleSidebar = () => {
  isSidebarVisible.value = !isSidebarVisible.value;
};

// 随机选取10个景点
const getRandomDestinations = () => {
  if (allDestinations.length > 0) {
    const shuffled = allDestinations.sort(() => 0.5 - Math.random());
    destinations.splice(0, destinations.length, ...shuffled.slice(0, 10));
  }
};

const getRandomNearbyTrips = () => {
  if (allNearbyTrips.length > 0) {
    const shuffled = allNearbyTrips.sort(() => 0.5 - Math.random());
    nearbyTrips.splice(0, nearbyTrips.length, ...shuffled.slice(0, 4));
  }
};


onMounted(() => {
  selectHotGuide().then((res) => {
    if (res && res.data) {
      recommendations.splice(0, recommendations.length, ...res.data);
    }
  });

  selectHotScenic().then((res) => {
    if (res && res.data) {
      bannerImages.value = [
        res.data[0],
        res.data[4],
        res.data[7],
        res.data[15],
        res.data[62]
      ].filter(item => item); 
      allDestinations.splice(0, allDestinations.length, ...res.data);
      getRandomDestinations();
    }
  });

  selectNearScenic(store.user).then((res) => {
    if (res && res.data) {
      allNearbyTrips.splice(0, allNearbyTrips.length, ...res.data); // 保存所有数据
      getRandomNearbyTrips(); // 随机选取4个景点
    }
  });
});

const tiao = (id) => {
  addView(store.user.id, id).then(() => {
    router.push({
      path: `/guide/detail`,
      query: { id }
    });
  }).catch(() => {
    router.push({
      path: `/guide/detail`,
      query: { id }
    });
  });
};

const refreshDestinations = () => {
  getRandomDestinations();
};
const refreshDestinations1 = () => {
  getRandomNearbyTrips();
};

const goToScenicDetail = (id) => {
  router.push({
    path: `/scenic/detail`,
    query: { id }
  });
};
</script>

<template>
  <div class="home-page">
    <div class="content-container">
      <!-- 侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: !isSidebarVisible }">
        <div class="sidebar-header">
          <el-button :icon="isSidebarVisible ? ArrowRight: ArrowLeft" @click="toggleSidebar"
            class="toggle-sidebar-button"></el-button>
        </div>
        <div v-if="isSidebarVisible" class="sidebar-content">
          <h3 class="tips-title">出行小贴士</h3>
          <ul class="sidebar-list">
            <li class="sidebar-item">
              <i class="el-icon-star-on"></i> 选择安全的交通工具
            </li>
            <li class="sidebar-item">
              <i class="el-icon-sunny"></i> 提前了解目的地天气
            </li>
            <li class="sidebar-item">
              <i class="el-icon-suitcase"></i> 行李打包轻便为主
            </li>
            <li class="sidebar-item">
              <i class="el-icon-phone"></i> 随时保持手机充电
            </li>
            <li class="sidebar-item">
              <i class="el-icon-map-location"></i> 下载离线地图备用
            </li>
            <li class="sidebar-item">
              <i class="el-icon-shoes"></i> 穿着舒适，便于活动
            </li>
            <li class="sidebar-item">
              <i class="el-icon-wallet"></i> 随身携带紧急现金
            </li>
            <li class="sidebar-item">
              <i class="el-icon-document-checked"></i> 检查旅行证件有效期
            </li>
            <li class="sidebar-item">
              <i class="el-icon-flag"></i> 尊重当地文化习俗
            </li>
            <li class="sidebar-item">
              <i class="el-icon-location-information"></i> 提前预定景点门票
            </li>
          </ul>
        </div>
      </aside>

      <!-- 主内容 -->
      <el-main>
        <!-- Banner 轮播图 -->
        <el-carousel :interval="3000" type="card" height="450px">
          <el-carousel-item v-for="item in bannerImages" :key="item.id">
            <div @click="goToScenicDetail(item.id)" style="position: relative; cursor: pointer;">
              <img :src="item.imagePath" class="banner-img" />
              <div class="banner-spot-name">{{ item.spotName }}</div>
            </div>
          </el-carousel-item>
        </el-carousel>

        <!-- 周边游 -->
        <section class="section">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <h2 class="section-title">周边游</h2>
            <a @click="refreshDestinations1" style="cursor: pointer; color: #409eff;">换一换</a>
          </div>
          <el-row :gutter="20">
            <el-col :span="6" v-for="trip in nearbyTrips" :key="trip.id">
              <div class="destination-card">
                <img :src="trip.imagePath" class="destination-img" />
                <div class="destination-info">
                  <h3 class="destination-name">{{ trip.spotName }}</h3>
                  <p class="destination-description">{{ trip.description }}</p>
                  <a @click.prevent="goToScenicDetail(trip.id)" class="destination-button">查看详情</a>
                </div>
              </div>
            </el-col>
          </el-row>
        </section>

        <!-- 攻略推荐 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12" style="width: 60%;">
            <section class="section">
              <h2 class="section-title">热门攻略推荐</h2>
              <el-row :gutter="20">
                <el-col :span="24" v-for="item in recommendations" :key="item.id">
                  <el-card shadow="hover" @click="tiao(item.id)" class="guide-card">
                    <el-row>
                      <el-col :span="6">
                        <img :src="item.imagePath" class="guide-img" />
                      </el-col>
                      <el-col :span="18">
                        <div class="guide-content">
                          <h3 class="guide-title">{{ item.title }}</h3>
                          <div style="display: flex; align-items: center; margin-top: 8px;">
                            <img :src="item.authorPath" style="width:30px; height: 30px; border-radius: 50%;" />
                            <span style="margin-left: 8px;">{{ item.authorName }}</span>
                          </div>
                          <br>
                          <div>
                            <span>{{ item.location }}</span>
                          </div>
                          <br>
                          <div style="display: flex; align-items: center; color: #666;">
                            <el-icon>
                              <View />
                            </el-icon>
                            <span style="margin-right: 12px;">{{ item.viewCount }} 浏览</span>
                            <el-icon>
                              <ChatLineRound />
                            </el-icon>
                            <span>{{ item.commentCount }} 评论</span>
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </el-card>
                </el-col>
              </el-row>
            </section>
          </el-col>
          <el-col :span="12" style="width: 60%;">
            <section class="section">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <h2 class="section-title">热门景点推荐</h2>
                <a @click="refreshDestinations" style="cursor: pointer; color: #409eff;">换一换</a>
              </div>
              <el-row :gutter="20">
                <el-col :span="12" v-for="destination in destinations" :key="destination.id">
                  <div class="destination-card">
                    <img :src="destination.imagePath" class="destination-img" />
                    <div class="destination-info">
                      <h3 class="destination-name">{{ destination.spotName }}</h3>
                      <p class="destination-description">{{ destination.description }}</p>
                      <a @click.prevent="goToScenicDetail(destination.id)" class="destination-button">查看详情</a>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </section>
          </el-col>
        </el-row>
      </el-main>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
}

.content-container {
  flex: 1;
  display: flex;
}

/* 侧边栏样式 */
.sidebar {
  position: fixed;
  top: 90px;
  right: 0;
  width: 300px;
  background-color: #ffffff; /* 白色背景 */
  padding: 20px;
  border-radius: 8px 0 0 8px;
  color: #000000;
  z-index: 2;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
  transform: translateX(0);
}

.sidebar.collapsed {
  transform: translateX(300px);
}

.sidebar-content {
  background: #cdd35c42;
  /* 柔和的背景色 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  /* 轻微阴影效果 */
  font-family: 'Arial', sans-serif;
}

.tips-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 15px;
  text-align: center;
  font-weight: bold;
  background: #f5f5f5;
  padding: 10px;
  border-radius: 8px;
  box-shadow: inset 0 -2px 4px rgba(0, 0, 0, 0.05);
  /* 内部阴影效果 */
}

.sidebar-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-item {
  font-size: 16px;
  color: #555;
  margin-bottom: 10px;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  transition: background 0.3s, box-shadow 0.3s;
}

.sidebar-item i {
  color: #409eff;
  margin-right: 10px;
  cursor: pointer;
}

.sidebar-item{
  cursor: pointer;
}

.sidebar-item:hover {
  background: #f0f0f0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed .sidebar-content {
  opacity: 0;
}

.sidebar-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  position: absolute;
  top: 0px;
  left: -40px;
  width: 40px;
  height: 40px;
  background-color: transparent;
  border-radius: 8px;
}

.toggle-sidebar-button {
  background-color: transparent;
  color: #000000;
  border: none;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 0;
  z-index: 3;
}

/* 轮播图背景样式 */
.banner-img {
  width: 100%;
  height: 450px;
  object-fit: cover;
}

.banner-spot-name {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 10px 20px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 28px;
  font-weight: bold;
  border-radius: 8px;
  box-shadow: 0px 4px 10px rgba(206, 206, 206, 0.3);
  z-index: 10;
}

/* 公共区域样式 */
.section {
  margin: 40px 0;
  background-color: #fffaf0;
  padding: 20px;
  border-radius: 8px;
}

/* 景点卡片样式 */
.destination-card {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  height: 250px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.destination-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 景点图片样式 */
.destination-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 景点信息样式 */
.destination-info {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
  color: #ffffff;
  text-shadow: 0px 0px 6px rgba(0, 0, 0, 0.7);
}

.destination-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.destination-description {
  font-size: 14px;
  line-height: 1.5;
  max-height: 3em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 按钮样式 */
.destination-button {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 16px;
  background-color: rgba(0, 0, 0, 0.4);
  color: #ffffff;
  border: 2px solid #ffffff;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  text-align: center;
  text-decoration: none;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.destination-button:hover {
  background-color: rgba(0, 0, 0, 0.6);
  cursor: pointer;
}

/* 标题样式 */
.section-title {
  font-size: 24px;
  color: #333333;
  margin-bottom: 20px;
}

/* 攻略卡片样式 */
.guide-card {
  margin-bottom: 20px;
  padding: 0;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: #fff5e6;
}

.guide-card:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  background-color: #ffecd1;
}

/* 攻略图片样式 */
.guide-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px 0 0 8px;
}

/* 攻略内容样式 */
.guide-content {
  padding: 14px;
}

.guide-title {
  font-size: 18px;
  color: #333333;
  margin-bottom: 10px;
}

.guide-description {
  font-size: 14px;
  color: #666666;
  margin-bottom: 10px;
}

/* 攻略元数据样式 */
.guide-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
