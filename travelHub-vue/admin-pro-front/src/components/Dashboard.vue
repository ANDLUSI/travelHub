<template>
    <div class="dashboard">
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
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import { findByNewUser, findByHotGuide, findByHotTag } from '@/api/echarts';
import dayjs from 'dayjs';

const newUserChart = ref(null);
const guidePopularityChart = ref(null);
const hotTagsChart = ref(null);

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
            data: days, // 横轴使用近七天的日期
            axisLabel: {
                formatter: (value) => dayjs(value).format('DD'), // 仅显示日期的几号部分
                interval: 0, // 确保显示所有标签
            },
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: data,  // 使用传递进来的数据
                type: 'line',
                lineStyle: {
                    color: '#FF5733' // 设置折线的颜色，例如橙红色
                },
                itemStyle: {
                    color: '#FF5733' // 设置折线图每个数据点的颜色
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
            data: data.map(item => {
                // 对标题进行截断，如果超过10个字符则添加省略号
                return item.title.length > 10 ? item.title.substring(0, 10) + '...' : item.title;
            }),
            axisLabel: {
                interval: 0, // 确保每个标题都显示出来
                rotate: 45, // 旋转45度，避免标题重叠
            }
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: data.map(item => item.viewCount), // 使用 viewCount 作为数据
                type: 'bar'
            }
        ]
    };
    chart.setOption(option);
};

const initHotTagsChart = (data) => {
    const chart = echarts.init(hotTagsChart.value);

    const tags = [
        '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
        '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
        '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
        '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
    ];

    // 计算每个标签的出现次数
    const tagCountMap = data.reduce((acc, item) => {
        const itemTags = JSON.parse(item.tags); // 假设标签存储为JSON字符串
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

    // 转换为数组并排序
    const sortedTags = Object.entries(tagCountMap)
        .sort((a, b) => b[1] - a[1]);

    // 取出前五个标签，剩余的合并为“其他”
    const topTags = sortedTags.slice(0, 5).map(([name, value]) => ({ name, value }));
    const otherTagsCount = sortedTags.slice(5).reduce((acc, [, value]) => acc + value, 0);

    // 添加“其他”类别
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


onMounted(() => {
    findByNewUser().then((res) => {
        const users = res.data;  // 假设这是一个用户对象的数组

        // 生成近七天的日期数组
        const lastSevenDays = Array.from({ length: 7 }).map((_, i) =>
            dayjs().subtract(6 - i, 'day').format('YYYY-MM-DD')
        );

        // 计算每一天的注册人数
        const registrationData = lastSevenDays.map(day => {
            return users.filter(user => dayjs(user.createTime).format('YYYY-MM-DD') === day).length;
        });

        // 初始化图表，并传递日期和对应的数据
        initNewUserChart(lastSevenDays, registrationData);
    });

    findByHotGuide().then((res) => {
        const guideData = res.data; // 假设返回的数据是 [{title: '攻略A', viewCount: 1048}, ...]
        console.log(`output->guideData`,guideData)
        initGuidePopularityChart(guideData);
    });

    findByHotTag().then((res) => {
        const tagData = res.data; // 假设返回的数据是 [{tags: '["tag1", "tag2", ...]'}, ...]
        initHotTagsChart(tagData);
    });
});
</script>

<style scoped>
.dashboard {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.chart-container {
    width: 30%;
    padding-top: 10px;
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
