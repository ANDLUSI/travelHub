<template>
    <div class="dashboard">
        <!-- 统计卡片部分 -->
        <div class="statistic-cards">
            <div class="card" v-for="(item, index) in statistics" :key="index">
                <el-icon :size="50">
                    <component :is="item.icon"></component>
                </el-icon>
                <div class="card-info">
                    <p>{{ item.label }}</p>
                    <h3>{{ animatedCounts[index] }}</h3>
                </div>
            </div>
        </div>

        <!-- 用户数量分布地图 -->
        <div class="china-map-container">
            <h3>用户数量分布图</h3>
            <div ref="chinaMapChart" class="china-map"></div>
        </div>

        <!-- 图表部分 -->
        <div class="chart-container">
            <h3>近一周新增用户</h3>
            <div ref="newUserChart" class="chart"></div>
        </div>
        <div class="chart-container">
            <h3>攻略热度排行</h3>
            <div ref="guidePopularityChart" class="chart"></div>
        </div>
        <div class="chart-container">
            <h3>热门标签及占比一览</h3>
            <div ref="hotTagsChart" class="chart"></div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import chinaMapJson from '@/assets/json/china.json'; // 假设你将 china.json 放在 src/assets/json 下
import { findByNewUser, findByHotGuide, findByHotTag, findUser, findComment,findGuide } from '@/api/echarts';
import dayjs from 'dayjs';
import { ChatDotRound, Message, Postcard } from '@element-plus/icons-vue';
// 定义统计数据和图标
const statistics = ref([
    { label: '今日活跃用户', value: 12, icon: ChatDotRound },
    { label: '今日新增评论数', value: 81212, icon: Message },
    { label: '待审核攻略数', value: 9280, icon: Postcard },
]);

const animatedCounts = ref([0, 0, 0]);

const animateCounts = () => {
    statistics.value.forEach((stat, index) => {
        let currentCount = 0;
        const step = Math.ceil(stat.value / 100);
        const interval = setInterval(() => {
            currentCount += step;
            if (currentCount >= stat.value) {
                currentCount = stat.value;
                clearInterval(interval);
            }
            animatedCounts.value[index] = currentCount;
        }, 20);
    });
};



// 图表初始化逻辑
const newUserChart = ref(null);
const guidePopularityChart = ref(null);
const hotTagsChart = ref(null);
const chinaMapChart = ref(null); // 中国地图的 chart 容器

// 初始化中国用户数量分布的地图
const initChinaMapChart = (data) => {
    const chart = echarts.init(chinaMapChart.value);
    echarts.registerMap('china', chinaMapJson); // 注册中国地图

    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} 人' // 显示地区名称和人数
        },
        visualMap: {
            min: 0,
            max: 10,
            left: 'left',
            top: 'bottom',
            text: ['高', '低'], // 文本，默认为数值文本
            inRange: {
                color: ['#e0ffff', '#006edd']
            },
            calculable: true
        },
        series: [
            {
                name: '用户数量',
                type: 'map',
                map: 'china',
                roam: true, // 启用滚轮缩放和平移
                label: {
                    show: false // 默认不显示文字标签
                },
                emphasis: {
                    label: {
                        show: true, // 鼠标悬停时显示标签
                        color: '#fff' // 标签文字颜色
                    }
                },
                data: data // 各省的用户数量数据
            }
        ]
    };

    chart.setOption(option);
};

// 初始化其他图表
const initNewUserChart = (days, data) => {
    const chart = echarts.init(newUserChart.value);
    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            type: 'category',
            data: days,
            axisLabel: {
                formatter: (value) => value.split('-').pop(), // 只显示日期
                interval: 0,
            },
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: data,
                type: 'line',
                lineStyle: {
                    color: '#FF5733'
                },
                itemStyle: {
                    color: '#FF5733'
                }
            }
        ]
    };
    chart.setOption(option);
};

const initGuidePopularityChart = (data) => {
    const chart = echarts.init(guidePopularityChart.value);
    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            type: 'category',
            data: data.map(item => item.title.length > 10 ? item.title.substring(0, 10) + '...' : item.title),
            axisLabel: {
                interval: 0,
                rotate: 45,
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: data.map(item => item.viewCount),
                type: 'bar'
            }
        ]
    };
    chart.setOption(option);
};

const initHotTagsChart = (data) => {
    const chart = echarts.init(hotTagsChart.value);
    const tags = ['文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行', '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光', '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行', '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'];

    const tagCountMap = data.reduce((acc, item) => {
        const itemTags = JSON.parse(item.tags);
        itemTags.forEach(tag => {
            if (tags.includes(tag)) {
                if (!acc[tag]) {
                    acc[tag] = 0;
                }
                acc[tag]++;
            }
        });
        return acc;
    }, {});

    const sortedTags = Object.entries(tagCountMap).sort((a, b) => b[1] - a[1]);
    const topTags = sortedTags.slice(0, 5).map(([name, value]) => ({ name, value }));
    const otherTagsCount = sortedTags.slice(5).reduce((acc, [, value]) => acc + value, 0);
    if (otherTagsCount > 0) {
        topTags.push({ name: '其他', value: otherTagsCount });
    }

    const option = {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            top: '-2%',
            left: 'center'
        },
        series: [
            {
                name: '标签热度',
                type: 'pie',
                radius: '50%',
                data: topTags,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };
    chart.setOption(option);
};
const provinceMap = {
    北京: "北京市",
    天津: "天津市",
    上海: "上海市",
    重庆: "重庆市",
    黑龙江: "黑龙江省",
    吉林: "吉林省",
    辽宁: "辽宁省",
    内蒙古: "内蒙古自治区",
    河北: "河北省",
    山西: "山西省",
    山东: "山东省",
    江苏: "江苏省",
    浙江: "浙江省",
    安徽: "安徽省",
    福建: "福建省",
    江西: "江西省",
    河南: "河南省",
    湖北: "湖北省",
    湖南: "湖南省",
    广东: "广东省",
    广西: "广西壮族自治区",
    海南: "海南省",
    四川: "四川省",
    贵州: "贵州省",
    云南: "云南省",
    西藏: "西藏自治区",
    陕西: "陕西省",
    甘肃: "甘肃省",
    青海: "青海省",
    宁夏: "宁夏回族自治区",
    新疆: "新疆维吾尔自治区",
    香港:"香港特别行政区",
    澳门:"澳门特别行政区",
    台湾:"台湾省"
    // 如果有其他省市，可以继续补充
};

// 在组件挂载时启动动画和初始化图表
onMounted(() => {
    

    findByNewUser().then((res) => {
        const users = res.data;
        const lastSevenDays = Array.from({ length: 7 }).map((_, i) => dayjs().subtract(6 - i, 'day').format('YYYY-MM-DD'));
        const registrationData = lastSevenDays.map(day => users.filter(user => dayjs(user.createTime).format('YYYY-MM-DD') === day).length);
        initNewUserChart(lastSevenDays, registrationData);
    });

    findByHotGuide().then((res) => {
        const guideData = res.data;
        initGuidePopularityChart(guideData);
    });

    findByHotTag().then((res) => {
        const tagData = res.data;
        initHotTagsChart(tagData);
    });

    // 调用 findUser 并处理用户数据
    findUser().then((res) => {
        const users = res.data;

        // 统计今日活跃用户人数
        const today = dayjs().format('YYYY-MM-DD');
        const activeUsers = users.filter(user => dayjs(user.lastlogTime).format('YYYY-MM-DD') === today).length;
        statistics.value[0].value = activeUsers; // 更新统计卡片中的今日活跃用户数

        // 统计各省份用户数量
        const locationMap = users.reduce((acc, user) => {
            // 先转换省份简称为全称
            const fullLocation = provinceMap[user.location] || user.location; // 如果找不到映射则使用原来的简称
            if (!acc[fullLocation]) {
                acc[fullLocation] = 0;
            }
            acc[fullLocation]++;
            return acc;
        }, {});

        const chinaUserData = Object.keys(locationMap).map(location => ({
            name: location,
            value: locationMap[location]
        }));
        animateCounts(); // 启动动画
        initChinaMapChart(chinaUserData); // 初始化地图并更新数据
    });
    findComment().then((res) => {
        const comments = res.data;

        // 统计今日新增评论数
        const today = dayjs().format('YYYY-MM-DD');
        const todayComments = comments.filter(comment => dayjs(comment.createdTime).format('YYYY-MM-DD') === today).length;

        statistics.value[1].value = todayComments; // 更新统计卡片中的今日新增评论数
        animateCounts(); // 启动动画
        initChinaMapChart(chinaUserData); // 初始化地图并更新数据
    });
    findGuide().then((res) => {
        const guides = res.data;

        const pendingGuides = guides.filter(guide => guide.status === 0).length;

        statistics.value[2].value = pendingGuides;
    });
});
</script>

<style scoped>
.dashboard {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    justify-content: center;
    padding: 20px;
}

.statistic-cards {
    display: flex;
    gap: 20px;
    justify-content: center;
    width: 100%;
}

.card {
    display: flex;
    align-items: center;
    width: 344px;
    padding: 20px;
    background: linear-gradient(135deg, #f6ffc1, #fefcb6);
    /* 渐变背景色 */
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    gap: 30px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.icon {
    font-size: 50px;
    margin-right: 20px;
}

.icon.active-users {
    color: #1abc9c;
}

.icon.new-comments {
    color: #3498db;
}

.icon.pending-guides {
    color: #e74c3c;
}

.card-info {
    text-align: left;
}

.card-info p {
    font-size: 18px;
    color: #7f8c8d;
    margin-bottom: 5px;
}

.card-info h3 {
    font-size: 32px;
    margin: 0;
}

/* 中国地图样式 */
.china-map-container {
    width: 100%;
    padding: 10px;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    margin-bottom: 20px;
}

.china-map {
    width: 100%;
    height: 400px;
}

/* 图表部分样式 */
.chart-container {
    width: 30%;
    padding: 10px;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
}

.chart {
    width: 100%;
    height: 250px;
}
</style>
