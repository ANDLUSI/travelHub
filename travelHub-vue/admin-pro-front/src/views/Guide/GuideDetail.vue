<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { findById } from '@/api/guide'
import { ArrowRight, ArrowLeft } from '@element-plus/icons-vue';


const route = useRoute()
const router = useRouter()

const guideData = ref({
  id: '',
  title: '',
  content: ``,
  authorId: '',
  viewCount: '',
  likeCount: '',
  commentCount: '',
  tags: '',
  imagePath: '',
  createTime: ''
})

const props = defineProps({
  id: {
    type: String,
    required: true
  }
})
const loadGuideDetail = () => {
  isSidebarVisible.value = true;
  findById(props.id).then((res) => {
    guideData.value = res.data;
    if (guideData.value.tags) {
      guideData.value.tags = JSON.parse(guideData.value.tags)
      // console.log(`output->guideData.value.tags之后`,guideData.value.tags)
    }
  });

};

// 每次 id 变化时，重新加载详情
watch(() => props.id, (newId) => {
  if (newId) {
    loadGuideDetail();
  }
});
onMounted(() => {
  const id = props.id
  findById(id).then((res) => {
    guideData.value = res.data
    console.log(`output->re`, guideData.value)
    // console.log(`output->guideData.value.tags之前的`,guideData.value.tags)
    if (guideData.value.tags) {
      guideData.value.tags = JSON.parse(guideData.value.tags)
      // console.log(`output->guideData.value.tags之后`,guideData.value.tags)
    }
  })
  
})

const isSidebarVisible = ref(true)

const toggleSidebar = () => {
  isSidebarVisible.value = !isSidebarVisible.value
}

const randomColor = () => {
  const colors = ['#FF5733', '#33FF57', '#F33FF5', '#FF33A1', '#33FFF5', '#F5FF33']
  return colors[Math.floor(Math.random() * colors.length)]
}

const handleBack = () => {
  router.back()
}
</script>
<template>
  <div class="guide-detail-page">
    <!-- 主内容区域 -->

    <div class="content-container">
      <p>封面：</p>
      <div v-if="guideData.imagePath" class="guide-cover">
        <img :src="guideData.imagePath" alt="攻略封面" />
      </div>
      <div class="guide-content" v-html="guideData.content"></div>
    </div>

    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: !isSidebarVisible }">
      <div class="sidebar-header">
        <el-button :icon="isSidebarVisible ? ArrowRight : ArrowLeft" @click="toggleSidebar"
          class="toggle-sidebar-button"></el-button>
      </div>
      <div v-if="isSidebarVisible" class="sidebar-content">
        <h3>攻略信息</h3>
        <p><strong>标题:</strong> {{ guideData.title }}</p>
        <p><strong>作者:</strong> {{ guideData.authorName }}</p>
        <p><strong>点赞数:</strong> {{ guideData.likeCount }}</p>
        <p><strong>评论数:</strong> {{ guideData.commentCount }}</p>
        <p><strong>标签:</strong>
          <span v-for="(tag, index) in guideData.tags" :key="index"
            :style="{ color: randomColor(), marginLeft: '15px' }">
            {{ tag }}
          </span>
        </p>
        <p><strong>发布时间:</strong> {{ guideData.createTime }}</p>
      </div>
    </aside>
  </div>
</template>



<style scoped>
.guide-cover {
  text-align: center;
  margin-bottom: 20px;
}

.guide-cover img {
  max-width: 80%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.guide-detail-page {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
}

.content-container {
  flex: 1;
  margin-right: 20px;
}

.guide-content {
  background-color: #fffef3;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.sidebar {
  position: fixed;
  top: 100px;
  right: 0;
  width: 300px;
  background-color: rgba(40, 40, 40, 0.8);
  padding: 20px;
  border-radius: 8px 0 0 8px;
  color: #ffffff;
  z-index: 2;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
  transform: translateX(0);
}

.sidebar.collapsed {
  transform: translateX(260px);
}

.sidebar-content {
  opacity: 1;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100% - 40px);
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

</style>
