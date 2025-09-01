<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageSelect, addView } from '@/api/guide'

import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const store = useUserStore()
// 搜索框数据
const searchQuery = ref('')
const router = useRouter()
const topHotGuides = ref([])
// 筛选数据
const month = ['全部', '1-3月', '4-6月', '7-9月', '10-12月']
const day = ['全部', '1-3天', '4-7天', '8-10天', '11-15天', '15天以上']
const fee = ['全部', '1-999', '1000-2999', '3000-4999', '5000-9999', '10000以上']
const people = ['全部', '独自一人', '三五好友', '亲子', '家庭', '情侣', '闺蜜', '学生']
const tags = [
    '全部', '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
    '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
    '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
    '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
]
const queryTopHotGuides = () => {
    // 克隆现有的参数
    const topHotParams = { ...queryPageParams, pageSize: 10, sortOrder: 0 }

    pageSelect(topHotParams).then((res) => {
        console.log(`output-->topHotGuides`, res)
        topHotGuides.value = res.data.records
    })
}
// 选中的筛选项
const selectedFilters = reactive({
    month: 0,
    day: 0,
    fee: 0,
    people: 0,
    tag: '全部'
})


// 接收和存储攻略数据
const tableData = ref([])

const queryPage = () => {
    pageSelect(queryPageParams).then((res) => {
        console.log(`output-->res`, res)
        res.data.records.forEach(guide => {
            // 解析 `tags` 字段为数组
            if (guide.tags) {
                try {
                    guide.tags = JSON.parse(guide.tags);
                } catch (error) {
                    console.error('Failed to parse tags:', error);
                    guide.tags = []; // 解析失败时使用空数组
                }
            }
        });
        tableData.value = res.data.records
        total.value = res.data.total
    })
    console.log(`output->queryPageParams`, queryPageParams)
}

// 排序函数
const sortGuides  = (type) => {
    if (type === 'hot') {
        queryPageParams.sortOrder = 0
    } else if (type === 'time') {
        queryPageParams.sortOrder = 1
    } else if (type === 'like') {
        queryPageParams.sortOrder = 2
    }
    selectedFilters.sortOrder = queryPageParams.sortOrder
    queryPage()
}

// 选择筛选项
const selectFilter = (type, value) => {
    selectedFilters[type] = value;
    if (type === 'tag') {
        console.log(`output->value`, value)
        if (value == '全部') {
            queryPageParams.tag = '';
        } else {
            queryPageParams.tag = value;
        }

    } else {
        if (value === 0) {
            queryPageParams[type] = '';
        } else {
            queryPageParams[type] = value;
        }
    }

    queryPage();
}

// 搜索功能的实现
const handleSearch = () => {
    console.log('搜索内容:', searchQuery.value)
    queryPageParams.title = searchQuery.value
    queryPage()
}

// 分页
const queryPageParams = reactive({
    month: '',
    day: '',
    fee: '',
    people: '',
    tag: '',
    sortOrder: 0, // 默认按热度排序
    pageNo: 1,
    pageSize: 8
})

const total = ref(0)

//修改每页数量的回调函数
const handleSizeChange = (sizeValue) => {
    console.log(`output -> sizeValue`, sizeValue)
    queryPageParams.pageSize = sizeValue
    queryPage()
}

//改变页码的回调函数
const handleCurrentChange = (currentValue) => {
    console.log(`output - > currentValue`, currentValue)
    queryPageParams.pageNo = currentValue
    queryPage()
}

onMounted(() => {
    queryPage()
    queryTopHotGuides()
})
const openGuideDetail = (id) => {
    console.log('Opening guide with ID:', id);
    addView(store.user.id, id).then((res) => {
        console.log('View count incremented');
        // 成功增加viewCount后导航到详情页面
        router.push({
            path: `/guide/detail`,
            query: { id }
        });
    }).catch((error) => {
        console.error('Failed to increment view count', error);
        // 即使增加viewCount失败，也可以导航到详情页面
        router.push({
            path: `/guide/detail`,
            query: { id }
        });
    });
};
const openWriteGuide = () => {
    router.push({
        path: `/guide/edit`
    })
}
const randomColor = () => {
    const colors = ['#FF5733', '#33FF57', '#F33FF5', '#FF33A1', '#F5FF33']
    return colors[Math.floor(Math.random() * colors.length)]
}
</script>

<template>
    <el-main>
        <!-- 筛选部分 -->
        <section class="filter-section">
            <div class="search-container">
                <el-input v-model="searchQuery" placeholder="搜索攻略..." clearable class="search-input" />
                <el-button type="primary" @click="handleSearch">
                    <el-icon>
                        <Search />
                    </el-icon>
                    搜索
                </el-button>
            </div>
            <div class="filter-options">
                <el-row :gutter="20">
                    <!-- 月份筛选 -->
                    <el-col :span="24">
                        <div class="filter-group">
                            <span class="filter-label">月份：</span>
                            <span v-for="(monthValue, index) in month" :key="index"
                                @click="selectFilter('month', index)"
                                :class="{ active: selectedFilters.month === index }" class="filter-item">
                                {{ monthValue }}
                            </span>
                        </div>
                    </el-col>

                    <!-- 天数筛选 -->
                    <el-col :span="24">
                        <div class="filter-group">
                            <span class="filter-label">天数：</span>
                            <span v-for="(dayValue, index) in day" :key="index" @click="selectFilter('day', index)"
                                :class="{ active: selectedFilters.day === index }" class="filter-item">
                                {{ dayValue }}
                            </span>
                        </div>
                    </el-col>

                    <!-- 费用筛选 -->
                    <el-col :span="24">
                        <div class="filter-group">
                            <span class="filter-label">费用：</span>
                            <span v-for="(feeValue, index) in fee" :key="index" @click="selectFilter('fee', index)"
                                :class="{ active: selectedFilters.fee === index }" class="filter-item">
                                {{ feeValue }}
                            </span>
                        </div>
                    </el-col>

                    <!-- 人物筛选 -->
                    <el-col :span="24">
                        <div class="filter-group">
                            <span class="filter-label">人物：</span>
                            <span v-for="(peopleValue, index) in people" :key="index"
                                @click="selectFilter('people', index)"
                                :class="{ active: selectedFilters.people === index }" class="filter-item">
                                {{ peopleValue }}
                            </span>
                        </div>
                    </el-col>

                    <!-- 玩法筛选 -->
                    <el-col :span="24">
                        <div class="filter-group">
                            <span class="filter-label">玩法：</span>
                            <span v-for="(tagValue, index) in tags" :key="index" @click="selectFilter('tag', tagValue)"
                                :class="{ active: selectedFilters.tag === tagValue }" class="filter-item">
                                {{ tagValue }}
                            </span>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </section>

        <el-button type="primary" class="write-guide-button" @click="openWriteGuide">
            <el-icon size="28" style="color:white">
                <Edit />
            </el-icon>
            &nbsp;写游记
        </el-button>

        <!-- 右侧补充内容 -->
        <section class="side-content">
            <el-card class="cao">
                <h3>热门旅游攻略排行榜</h3>
                <ul>
                    <li v-for="(guide, index) in topHotGuides" :key="index" :class="{ 'top-three': index < 3 }"
                        @click="openGuideDetail(guide.id)">
                        <div style="flex-grow: 1;">
                            <span v-if="index < 3">
                                <svg t="1724675254332" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                    xmlns="http://www.w3.org/2000/svg" p-id="4309" width="20" height="20">
                                    <path
                                        d="M360.725 33c3.526 1.457 7.052 2.909 10.575 4.374 24.202 10.07 48.704 19.484 72.535 30.362 36.75 16.774 71.604 37.031 104.774 60.142 26.75 18.64 51.616 39.576 75.127 62.087 8.946 8.564 17.269 17.852 25.224 27.354 11.313 13.515 22.512 27.179 32.88 41.417 16.44 22.572 30.486 46.615 41.868 72.193 9.157 20.583 17.27 41.491 22.289 63.505 1.506 6.607 2.988 13.217 4.803 21.235 22.57-30.853 27.756-66.112 34.339-102.473 12.856 20.155 25.33 38.876 36.948 58.113 19.011 31.48 37.17 63.487 51.968 97.194 10.17 23.167 20.189 46.494 28.429 70.384 7.825 22.688 14.158 45.995 19.464 69.411 4.837 21.353 8.16 43.128 8.012 65.212-0.008 1.095 0.677 2.192 1.04 3.287v28.275c-0.367 5.083-0.316 10.237-1.177 15.235-3.069 17.82-5.747 35.75-9.874 53.332-6.161 26.25-17.03 50.81-31.715 73.345-9.397 14.419-20.491 27.767-31.257 41.251-12.39 15.518-27.201 28.644-43.358 40.07-14.476 10.239-29.266 20.104-44.455 29.242-21.161 12.732-43.663 22.72-68 30.057 0.362-1.721 0.494-3.108 0.943-4.385 0.256-0.727-0.038-1.391-0.884-1.995 3.78-2.514 7.52-5.092 11.223-7.714 9.023-6.387 17.294-13.725 24.214-22.4 6.013-7.538 12.209-15.001 17.456-23.061 8.202-12.599 14.272-26.329 17.712-41.003 2.305-9.83 3.801-19.853 5.514-29.815 0.482-2.794 0.453-5.675 0.658-8.517v-15.806c-0.203-0.613-0.585-1.226-0.58-1.838 0.082-12.346-1.774-24.519-4.475-36.456-2.963-13.09-6.5-26.12-10.87-38.804-4.602-13.355-10.197-26.396-15.877-39.347-8.265-18.843-18.406-36.737-29.023-54.335-6.488-10.754-13.455-21.22-20.634-32.488-3.677 20.327-6.573 40.039-19.178 57.287-1.013-4.483-1.841-8.178-2.682-11.872-2.804-12.306-7.334-23.994-12.448-35.501-6.357-14.3-14.2-27.74-23.381-40.359-5.791-7.96-12.046-15.598-18.364-23.153-4.442-5.313-9.09-10.505-14.086-15.293-13.13-12.584-27.017-24.288-41.957-34.708-18.524-12.92-37.99-24.245-58.513-33.622-13.31-6.081-26.993-11.345-40.51-16.974-1.966-0.819-3.936-1.63-5.905-2.445h-1.823c2.422 5.634 5.001 11.206 7.237 16.915 5.352 13.666 9.691 27.673 12.132 42.149 1.853 10.98 3.211 22.066 4.24 33.156 0.716 7.715 1.045 15.546 0.599 23.27-1.31 22.64-5.744 44.64-14.715 65.61-7.112 16.63-16.064 32.158-27.791 45.931-7.035 8.261-14.661 16.02-22.068 23.96-0.871 0.934-2.038 1.592-3.418 2.645-9.25-20.868-19.72-40.931-33.244-60.231-0.746 3.928-1.463 7.05-1.916 10.21-1.707 11.923-5.19 23.362-9.133 34.7-5.79 16.647-13.894 32.063-24.987 45.738-4.47 5.51-10.332 9.877-14.917 15.309-3.614 4.28-6.848 9.116-9.181 14.197-4.825 10.504-7.04 21.814-8.917 33.158l-0.338 2.063c-0.528 3.25-0.448 6.599-0.698 9.898-0.033 0.422-0.378 0.821-0.579 1.231v24.319c0.404 2.901 0.709 5.82 1.23 8.7 1.621 8.97 2.588 18.146 5.19 26.827 2.969 9.909 6.878 19.636 11.374 28.962 5.008 10.388 11.799 19.73 19.437 28.488 12.581 14.425 27.58 25.594 44.386 34.512a198.767 198.767 0 0 0 6.817 3.451c-9.097 0.587-18.404 1.198-27.916 1.832-0.197-0.284-0.353-0.777-0.596-0.823-11.847-2.27-23.729-4.365-35.549-6.763-33.377-6.77-65.133-18.109-95.23-34.062-30.092-15.952-56.95-35.931-79.477-61.735-13.677-15.665-25.836-32.376-34.804-50.96-8.05-16.682-15.05-34.08-20.366-51.805-4.659-15.53-6.391-31.942-9.295-47.987-0.932-5.152-1.478-10.374-2.201-15.564v-43.5c0.36-0.734 0.978-1.448 1.036-2.204 0.449-5.901 0.305-11.89 1.25-17.705 3.502-21.524 7.41-43.074 16.572-63.002 4.178-9.089 9.969-17.74 16.44-25.396 8.21-9.716 18.708-17.527 26.71-27.384 19.863-24.463 34.374-52.038 44.742-81.817 7.06-20.28 13.297-40.742 16.354-62.07 0.81-5.652 2.095-11.237 3.43-18.264 24.216 34.524 42.964 70.413 59.527 107.742 2.47-1.884 4.56-3.06 6.12-4.732 13.263-14.204 26.918-28.082 39.515-42.86 20.998-24.637 37.028-52.413 49.763-82.159 16.064-37.512 24.004-76.865 26.348-117.362 0.8-13.818 0.21-27.827-1.07-41.626-1.844-19.838-4.276-39.669-7.593-59.31-4.371-25.893-12.142-50.95-21.725-75.394-4.003-10.213-8.62-20.18-12.959-30.258h3.265z"
                                        fill="#E50404" p-id="4310"></path>
                                </svg>
                            </span>
                            <span :class="{ 'top-three': index < 3 }"> {{ guide.title }}</span>
                        </div>
                        <span class="view-count">({{ guide.viewCount }} 浏览)</span>
                    </li>
                </ul>
            </el-card>
        </section>


        <!-- 攻略部分 -->
        <section class="guide-list">
            <h3>旅游攻略</h3>
            <div class="sort-options">
                <el-button @click="sortGuides('hot')"
                    :class="{ active: queryPageParams.sortOrder === 0 }">按热度排序</el-button>
                <el-button @click="sortGuides('time')"
                    :class="{ active: queryPageParams.sortOrder === 1 }">按时间排序</el-button>
                <el-button @click="sortGuides('like')"
                    :class="{ active: queryPageParams.sortOrder === 2 }">按点赞数排序</el-button>
            </div>

            <!-- 攻略列表展示 -->
            <el-row :gutter="20">
                <!-- 将每行展示一个攻略改为每行展示两个 -->
                <el-col :span="12" v-for="guide in tableData" :key="guide.id">
                    <el-card shadow="hover" @click="openGuideDetail(guide.id)" class="cursoner">
                        <div style="display: flex;">
                            <img :src="guide.imagePath" alt="cover"
                                style="width: 150px; height: 100px; border-radius: 8px; margin-right: 20px;" />
                            <div style="flex: 1;">
                                <h4 style="margin: 0;">{{ guide.title }}</h4>
                                <div style="display: flex; align-items: center; margin-top: 8px;">
                                    <img :src="guide.authorPath"
                                        style="width:30px; height: 30px; border-radius: 50%;" />
                                    <span>{{ guide.authorName }}</span>
                                </div>
                                <br>
                                <svg t="1724829200741" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                    xmlns="http://www.w3.org/2000/svg" p-id="2920" width="16" height="16">
                                    <path
                                        d="M938 458.8l-29.6-312.6c-1.5-16.2-14.4-29-30.6-30.6L565.2 86h-0.4c-3.2 0-5.7 1-7.6 2.9L88.9 557.2c-3.9 3.9-3.9 10.2 0 14.1l363.8 363.8c1.9 1.9 4.4 2.9 7.1 2.9s5.2-1 7.1-2.9l468.3-468.3c2-2.1 3-5 2.8-8zM459.7 834.7L189.3 564.3 589 164.6 836 188l23.4 247-399.7 399.7z"
                                        p-id="2921"></path>
                                    <path
                                        d="M680 256c-48.5 0-88 39.5-88 88s39.5 88 88 88 88-39.5 88-88-39.5-88-88-88z m0 120c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32z"
                                        p-id="2922"></path>
                                </svg>
                                <span v-for="(tag, index) in guide.tags" :key="index"
                                    :style="{ color: randomColor(), marginLeft: '15px' }">
                                    {{ tag }}
                                </span>
                                <br><br>
                                <div style="display: flex; align-items: center; margin-top: 12px; color: #666;">
                                    <svg t="1724653057212" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="1738" width="16" height="16">
                                        <path
                                            d="M518.4 149.290667c112.597333-80.789333 267.882667-69.397333 368.128 32 53.866667 54.528 84.138667 128.853333 84.138667 206.378666 0 77.525333-30.293333 151.850667-84.096 206.336l-294.421334 299.52a110.976 110.976 0 0 1-80.213333 34.474667 110.72 110.72 0 0 1-79.914667-34.176L137.322667 593.770667C83.562667 539.242667 53.333333 464.981333 53.333333 387.541333s30.229333-151.722667 84.010667-206.272c100.224-101.376 255.530667-112.768 368.128-31.978666l6.442667 4.778666 6.485333-4.778666z m322.602667 76.970666c-84.629333-85.589333-219.157333-88.64-307.328-6.954666l-21.76 20.138666-21.717334-20.138666c-88.192-81.685333-222.72-78.634667-307.306666 6.933333-41.92 42.496-65.557333 100.608-65.557334 161.28 0 60.693333 23.637333 118.805333 65.6 161.344l295.04 300.416c9.045333 9.450667 21.269333 14.72 33.962667 14.72 12.693333 0 24.917333-5.269333 34.261333-15.04L840.96 549.077333c42.005333-42.496 65.685333-100.650667 65.685333-161.408 0-60.736-23.68-118.912-65.664-161.408z"
                                            fill="#111111" p-id="1739"></path>
                                    </svg>
                                    <span style="margin-right: 12px;">&nbsp;{{ guide.likeCount }}</span>
                                    <el-icon>
                                        <ChatLineRound />
                                    </el-icon>
                                    <span style="margin-right: 12px;">{{ guide.commentCount }}</span>
                                    <el-icon>
                                        <View />
                                    </el-icon>
                                    <span>{{ guide.viewCount }}</span>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>


            <!-- 分页 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="queryPageParams.pageNo" :page-sizes="[8, 16, 20]" :page-size="queryPageParams.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total" class="pagination-bar" />
        </section>
    </el-main>
</template>

<style scoped>
/* 页面整体背景颜色 */
.el-main {
    background-color: #e6f7ff;
    /* 浅蓝色背景 */
    padding: 20px;
}

.write-guide-button {
    position: absolute;
    top: 120px;
    /* 调整这个数值来定位按钮的垂直位置 */
    right: 15%;
    background-color: #ff4500;
    /* 设置按钮颜色 */
    color: #ffffff;
    font-size: 20px;
    border-radius: 8px;
    padding: 10px 20px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    width: 180px;
    height: 60px;
}

.write-guide-button:hover {
    background-color: #d43f00;
    /* 改变按钮悬停时的背景颜色 */
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
    /* 增强阴影效果 */
    color: #ffffff;
    /* 保持字体颜色 */
}

/* 每个 section 的样式，背景为白色 */
.filter-section {
    width: 60%;
    background-color: #ffffff;
    /* 白色背景 */
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    /* 轻微阴影，增强对比 */
    margin-bottom: 20px;
}

.guide-list {
    background-color: #ffffff;
    /* 白色背景 */
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    /* 轻微阴影，增强对比 */
    margin-bottom: 20px;
}

/* 卡片样式 */
/* 攻略卡片样式 */
.cursoner {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    border-radius: 12px;
    cursor: pointer;
    background-color: #ffffff;
    color: #333333;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    position: relative;
    display: flex;
    height: 230px;
}

.cursoner:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.cursoner img {
    width: 250px;
    height: 150px;
    object-fit: cover;
    border-top-left-radius: 12px;
    border-bottom-left-radius: 12px;
}

.cursoner .content {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.cursoner h4 {
    margin: 0;
    font-size: 20px;
    font-weight: bold;
    color: #333;
}

.cursoner .tags {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.cursoner .tags span {
    background-color: #f0f0f0;
    padding: 5px 10px;
    border-radius: 20px;
    font-size: 12px;
    color: #555;
}

.cursoner .tags span:nth-child(odd) {
    background-color: #e6f7ff;
    color: #1e90ff;
}

.cursoner .tags span:nth-child(even) {
    background-color: #ffe6f7;
    color: #ff1e90;
}

.cursoner .info {
    display: flex;
    align-items: center;
    margin-top: 10px;
    font-size: 14px;
    color: #888;
}

.cursoner .info svg {
    margin-right: 8px;
    color: #1e90ff;
}

.cursoner .info span {
    margin-right: 20px;
}

/* 搜索栏样式 */
.search-container {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.search-input {
    width: 50%;
    margin-right: 30px;
    background-color: #ffffff;
    /* 白色输入框 */
    color: #333333;
    /* 深色字体 */
}

/* 筛选项样式 */
.filter-group {
    margin-bottom: 10px;
}

.filter-label {
    font-weight: bold;
    margin-right: 10px;
    color: #333333;
    /* 深色字体 */
}

.filter-item {
    margin-right: 10px;
    cursor: pointer;
    transition: color 0.3s ease;
    color: #333333;
    /* 深色字体 */
}

.filter-item.active {
    color: #1e90ff;
    /* 活跃状态颜色 */
    font-weight: bold;
}

.filter-item:hover {
    color: #4682b4;
    /* 悬停状态颜色 */
}

.side-content {
    width: 25%;
    position: fixed;
    right: 5%;
    top: 27%;
    /* 调整这个数值以控制距顶部的距离 */
}

.el-card {
    background: #ffffff;
    /* 白色背景 */
    box-shadow: 3px 3px 6px rgba(0, 0, 0, 0.1);
    /* 浅色阴影 */
    border-radius: 12px;
    padding: 20px;
    color: #333333;
    /* 深色字体 */
}

.el-card h3 {
    font-size: 18px;
    font-weight: bold;
    color: #333333;
    /* 深色字体 */
    margin-bottom: 15px;
    text-align: center;
}

.el-card ul {
    list-style-type: none;
    padding: 0;
}

.el-card li {
    margin-bottom: 12px;
    font-size: 16px;
    color: #333333 !important;
    /* 深色字体 */
    display: flex;
    cursor: pointer;
    transition: color 0.3s ease, transform 0.3s ease;
}

.el-card li:hover {
    color: #1e90ff;
    /* 悬停状态颜色 */
    transform: scale(1.05);
}

.top-three {
    font-weight: bold;
    color: #ff4500;
    /* 前三名颜色 */
}

.view-count {
    font-size: 14px;
    color: #888888;
    /* 灰色字体 */
}

.spot-image {
    width: 100%;
    height: auto;
    margin-bottom: 10px;
}

/* 攻略列表样式 */
.guide-list {
    width: 60%;
    margin-top: 20px;
}

.sort-options {
    margin-bottom: 10px;
}

/* 分页栏样式 */
.pagination-bar {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

/* 标题样式 */
h4 {
    font-size: 16px;
    font-weight: bold;
    color: #333333;
    /* 深色字体 */
}

span {
    font-size: 14px;
    color: #555555;
    /* 中灰色字体 */
}

.el-icon {
    font-size: 16px;
    margin-right: 4px;
    color: #333333;
    /* 深色图标 */
}

.el-button.active {
    color: #1e90ff;
    font-weight: bold;
    background-color: #e6f7ff;
    /* 浅蓝色背景 */
    border-color: #1e90ff;
}
</style>
