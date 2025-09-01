<template>
	<view>
		<view class="navbar">
			<image class="logo" src="../../static/index/logo.png" mode="widthFix" style="width: 70rpx; height: 70rpx;">
			</image>

			<input type="text" class="search" value="" placeholder="搜索更多去处" />

			<!-- 图片将被放置在 input 内部 -->
			<image class="pic" src="../../static/index/search.png" mode=""></image>
		</view>

		<swiper :indicator-dots="true" :autoplay="true" style="text-align: center;margin-top: 6px;" :interval="6000"
			:duration="1000">
			<swiper-item>
				<view class="swiper-item">
					<image src="../../static/index/故宫.jpg" mode="" style="width: 700rpx; height: 500rpx;"></image>
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="../../static/index/桂林1.jpg" mode="aspectFit" style="width: 700rpx; height: 600rpx;">
					</image>
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="../../static/index/布达拉宫.jpg" mode="aspectFit" style="width: 700rpx; height: 600rpx;">
					</image>
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="../../static/index/秦淮河2.jpg" mode="aspectFit" style="width: 700rpx; height: 600rpx;">
					</image>
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="../../static/index/稻城亚丁.jpg" mode="aspectFit" style="width: 700rpx; height: 600rpx;">
					</image>
				</view>
			</swiper-item>
		</swiper>
		<view>
			<view class="photo-grid">
				<view class="photo-container" v-for="(pic, index) in pics" :key="index"
					@click="navigateToPage(pic.id,pic.text)">
					<image :src="pic.src" class="photo" mode="aspectFit"></image>
					<text class="photo-caption" v-text="pic.text"></text>
				</view>
			</view>
		</view>
		<!-- 	<view class="container">
			<!-- 悬浮标签栏 -->

		<!-- <scroll-view scroll-x class="tag-bar" :show-scrollbar="false" ref="scrollViewRef">
				<view class="tags-container">
					<view v-for="(pic, index) in pics" :key="index" @click="changeTab(index)"
						:class="['tag-item', { 'active': currentIndex === index }]"
						:ref="el => { if (el) tagRefs[index] = el }">
						<uni-link :href="''" :text="pic.text"></uni-link>
					</view>
				</view>
				<view class="underline"
					:style="{ transform: `translateX(${underlinePosition}px)`, width: `${underlineWidth}px` }"></view>
			</scroll-view> -->


		<!-- 内容展示区 -->
		<!-- <view class="content-area"> -->
		<!-- 使用v-if根据currentIndex显示不同的内容 -->
		<!-- <template v-if="courseDetail">
					<image :src="courseDetail.coverPic" class="course" mode="aspectFit"></image>

					<view class="words">
						<p>{{courseDetail.courseName }}</p>
					</view>

				</template>
			</view>
		</view> -->

		<view class="other-items">
			<view v-for="(item, index) in otherItems" :key="index" class="other-item">
				<image :src="item.image" mode="aspectFit" class="other-item-image"></image>
				<view class="other-item-content">
					<text class="other-item-title">{{ item.title }}</text>
					<text class="other-item-description">{{ item.description }}</text>
					<view class="other-item-tags">
						<text v-for="(tag, tagIndex) in item.tags" :key="tagIndex"
							class="other-item-tag">{{ tag }}</text>
					</view>
				</view>
			</view>
		</view>
		<view>
			<view class="afford">
				<view class="guess">猜你喜欢</view>
				<view class="change" @click="switchContent">
					换一个
					<image src="../../static/index/refresh.png" mode="" style="width: 40rpx; height: 40rpx;"></image>
				</view>
			</view>


			<!-- 使用 swiper 实现滑动效果 -->
			<swiper class="swiper-container" :current="swiperCurrent" @change="onSwiperChange" circular duration="500">
				<swiper-item v-for="(item, index) in recommendItems" :key="index" class="swiper-item">
					<view class="recommend-item">
						<image :src="item.coverPic" class="item-image" mode="aspectFit"></image>
						<text class="item-caption">{{ item.courseName }}</text>
						<text class="caption2">{{ item.courseDescription }}</text>
					</view>
				</swiper-item>
			</swiper>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted,
		watch,
		nextTick
	} from 'vue';



	const pics = ref([{
			id: '',
			src: '../../static/index/pic1.png',
			text: ''

		},
		{
			id: '',
			src: '../../static/index/pic2.png',
			text: ''

		},
		{
			id: '',
			src: '../../static/index/pic5.png',
			text: ''

		},
		{
			id: '',
			src: '../../static/index/pic4.png',
			text: ''

		},
		{
			id: '',
			src: '../../static/index/pic3.png',
			text: ''

		}
	]);
	// //存储从后端获取的分类信息
	// const categories = ref([]);
	// //存储分类名称的方法，基于ID查找
	// const getCategoryNameById = (id) => {
	// 	const category = categories.value.find(cat => cat.id === id);
	// 	return category ? category.categoryName : '未知分类';
	// };




	// 定义页面映射表
	const pageMapping = {
		'一流课程': '/pages/firstCourse/firstCourse',
		'计算机': '/pages/firstCourse/firstCourse',
		'职场商学': '/pages/firstCourse/firstCourse',
		'训练营': '/pages/firstCourse/firstCourse',
		'考研四六级': '/pages/firstCourse/firstCourse',
		'名校认证': '/pages/firstCourse/firstCourse',
		'同等学力': '/pages/firstCourse/firstCourse',
		'OnlineMBA': '/pages/firstCourse/firstCourse',
		'SLELE': '/pages/firstCourse/firstCourse',
		'更多': '/pages/firstCourse/firstCourse'
	};

	// 定义跳转方法
	const navigateToPage = (id, title) => {
		console.log('==== 进来了 :', id);
		uni.navigateTo({
			url: '/pages/firstCourse/firstCourse?id=' + id + '&title=' + title
		});

	};
	//在组件挂载时发起请求
	onMounted(async () => {
		uni.request({
			url: 'http://localhost:8080/category/categoryList', // 确保URL正确
			method: 'GET',
			success: (res) => {
				if (res.statusCode === 200 && res.data.code === 1001) {
					res.data.data.forEach((item, i) => {
						pics.value[i].id = item.id;
						pics.value[i].text = item.categoryName;

					})
					console.log('==== pics :', pics.value);
				} else {
					console.error('没找到当前页面:', res.data.message);
				}
			},
			fail: (err) => {
				console.error('错误的请求:', err);
			}
		});
	})
	// 使用ref定义当前选中的标签索引
	const currentIndex = ref(0);
	//为了布局设计的死数据
	// const tags = [{
	// 		text: "热门课程"
	// 	},
	// 	{
	// 		text: "研究生课"
	// 	},
	// 	{
	// 		text: "职业教育"
	// 	},
	// 	{
	// 		text: "计算机"
	// 	},
	// 	{
	// 		text: "经济管理"
	// 	},
	// 	{
	// 		text: "文史哲"
	// 	},
	// 	{
	// 		text: "医药卫生"
	// 	},
	// 	{
	// 		text: "外语"
	// 	}
	// ];


	// 获取标签元素的引用和scroll-view的引用
	const tagRefs = ref([]);
	const scrollViewRef = ref(null);
	const courseDetail = ref(null);
	const studentId = ref(uni.getStorageSync('studentId'));
	const error = ref(null);
	// 推荐项目的数据源
	const recommendItems = ref([{

		"courseName": "桂林山水甲天下",

		"courseDescription": "桂林山水值得一看",
		"coverPic": "/static/index/桂林1.jpg"

	}, {

		"courseName": "北京故宫",
		"courseDescription": "北京故宫值得一看",

		"coverPic": "/static/index/故宫.jpg"

	}, {
		"courseName": "杭州西湖",
		"courseDescription": "来尝尝很火的西湖醋鱼吧",


		"coverPic": "/static/index/杭州.jpg"

	}]);


	// 计算下划线的位置和宽度
	const underlinePosition = ref(0);
	const underlineWidth = ref(0);

	const updateUnderlinePosition = async () => {
		await nextTick(); // 确保DOM更新完成
		if (!tagRefs.value[currentIndex.value]) return;

		const rect = tagRefs.value[currentIndex.value].$el?.getBoundingClientRect();
		if (!rect) return;

		const parentRect = scrollViewRef.value?.$el.getBoundingClientRect();
		const scrollLeft = scrollViewRef.value?.scrollLeft || 0;

		underlinePosition.value = rect.left - (parentRect?.left || 0) + scrollLeft;
		underlineWidth.value = rect.width;
	};

	// 组件加载时执行
	onMounted(async () => {
		if (!studentId.value) {
			error.value = '用户ID未找到';
			return;
		}

		getUserLikes();

	});

	watch(currentIndex, updateUnderlinePosition);
	const getCourseImage = (index) => {
		const courseImages = [
			'../../static/index/course1.jpg',
			'../../static/index/course2.jpg',
			'../../static/index/course3.jpg',
			'../../static/index/course4.jpg',
			'../../static/index/course5.jpg',
			'../../static/index/course1.jpg', // 文史哲
			'../../static/index/course1.jpg', // 医药卫生
			'../../static/index/course1.jpg' // 外语
		];

		return courseImages[index] || '../../static/index/course1.jpg';
	};

	const getContentText = (index) => {
		const contentTexts = [
			'热门课程的内容',
			'研究生课的内容',
			'职业教育的内容',
			'计算机的内容',
			'经济管理的内容',
			'文史哲的内容',
			'医药卫生的内容',
			'外语的内容'
		];
		return contentTexts[index] || '默认内容';
	};



	// 当前swiper索引
	const swiperCurrent = ref(0);

	// 切换内容的方法
	const switchContent = () => {
		const nextIndex = (swiperCurrent.value + 1) % recommendItems.value.length;
		swiperCurrent.value = nextIndex;
	};

	// 监听swiper的变化
	const onSwiperChange = (e) => {
		swiperCurrent.value = e.detail.current;
	};
	// 获取用户喜好信息的函数
	async function getUserLikes() {
		try {
			const response = await uni.request({
				url: 'http://localhost:8080/course/guest',
				method: 'GET',
				data: {
					studentId: studentId.value
				},
				header: {
					'content-type': 'application/x-www-form-urlencoded'
				}
			});

			if (response.data.code === 1001) {
				recommendItems.value = response.data.data;
				console.log('==== recommendItems.value :', response.data.data);
				console.log('==== recommendItems.value :', recommendItems.value);

				// 返回用户的喜好列表


			} else {
				throw new Error('无法获取用户喜好');
			}
		} catch (err) {
			console.error('Error fetching user likes:', err);
			error.value = '无法获取用户喜好';
			return [];
		}
	}
	// 定义静态其他项目数据
	const otherItems = ref([{
			image: '../../static/index/北海银滩.jpg',
			title: '北海银滩 ',
			description: '广西夏天很不错的去处',
			tags: ['海边推荐']
		},
		{
			image: '../../static/index/颐和园.jpg',
			title: '北京颐和园 ',
			description: '来北京感受历史文化',
			tags: ['历史建筑']
		},
		{
			image: '../../static/index/少林寺.jpg',
			title: '少林寺',
			description: '看看影视剧中的真实样子',
			tags: ['历史建筑', '影视文化']
		},
		{
			image: '../../static/index/龙虎山.jpg',
			title: '龙虎山',
			description: '惊艳眼球的自然景观',
			tags: ['自然景观', '大自然']
		}

		// 添加更多静态其他项目...
	]);
</script>

<style scoped lang="scss">
	.navbar {
		margin-left: 10px;
		margin-top: 5px;
		display: flex;
		align-items: center;
	}

	.logo {
		margin-left: 5px;
		padding-right: 13rpx;
		margin-top: 2px;

	}

	.search {
		border: 1px solid gainsboro;
		width: 370rpx;
		height: 90rpx;
		border-radius: 1.2em;
		margin-left: 10px;
		padding-left: 100rpx;
		margin-right: 10px;
		/* 为图片留出空间 */
		position: relative;
		text-align: left;
	}

	.pic {
		position: absolute;
		left: 155rpx;
		/* 调整图片的位置 */
		top: 29px;
		transform: translateY(-50%);
		width: 55rpx;
		/* 调整图片大小 */
		height: 50rpx;
	}

	.swiper-item {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: 100%;
	}

	/* 图片样式 */
	.swiper-image {
		width: 100%;
		height: 100%;
		object-fit: cover;
		/* 保持图片的宽高比，并填充整个容器 */
	}

	.sec {
		display: flex;
		flex-wrap: wrap;
		/* 允许换行 */
	}

	// .photo-container {
	// 	display: flex;
	// 	flex-direction: column;
	// 	align-items: center;
	// 	/* 水平居中对齐 */
	// 	margin-left: 5px;
	// 	margin-right: 19px;
	// 	/* 图片之间的间距 */
	// 	margin-bottom: 10px;
	// 	/* 行与行之间的间距 */
	// }

	// .photo {
	// 	width: 70rpx;
	// 	height: 70rpx;
	// 	margin-bottom: 6px;
	// 	/* 图片与描述之间的间距 */
	// }

	.photo-grid {
		display: grid;
		grid-template-columns: repeat(5, 1fr);
		/* 创建5列 */
		gap: 4rpx;
		row-gap: -100rpx;
		/* 设置列与列之间的间距 */
		padding: 3px;
		/* 内边距以避免边缘紧贴容器 */
	}

	.photo-container {
		position: relative;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		text-align: center;
		padding: 2rpx;
		margin-top: 12rpx;
		/* 内边距以提供额外的空间 */
		// border-radius: 8px;
		/* 圆角效果 */
		min-height: 70px;
		/* 设置最小高度以确保有足够空间 */
	}

	.photo {
		width: 60%;
		/* 图片宽度为父容器的100% */
		height: 34%;
		/* 自动调整高度以保持比例 */
		object-fit: cover;
		/* 保持图片比例并裁剪 */
		margin-bottom: 7px;
		/* 图片与描述之间的间距 */
	}

	.photo-caption {
		font-size: 12px;
		/* 文字大小 */
		white-space: nowrap;
		/* 防止文字换行 */
		text-overflow: ellipsis;
		/* 超出部分用省略号表示 */
		overflow: hidden;
		/* 超出部分隐藏 */
		width: 100%;
		/* 确保文字宽度与容器一致 */
	}

	.container {
		padding: 20px;
	}

	.tag-bar {
		display: flex;
		overflow-x: auto;
		margin-bottom: 20px;
		padding: 10px 0;
		background-color: #fff;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		border-radius: 8px;
		white-space: nowrap; //确保标签内的文本不会换行
		position: relative;
	}

	.tags-container {
		display: flex;
		flex-wrap: nowrap;
		/* 防止换行 */
	}

	.tag-item {
		padding: 10px 20px;
		margin-right: 10px;
		cursor: pointer;
		display: inline-block;
		background-color: #f9f9f9;
		border-radius: 20px;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease;
		min-width: 70px;
		/* 设置最小宽度 */
		text-align: center; //居中对齐。
		/* 居中文本 */
		white-space: nowrap;
		/* 防止文本换行 */
		height: 30px;
		/* 设置固定高度 */
		line-height: 30px;
		/* 垂直居中文本 */
	}

	.tag-item.active {
		color: #34A2E8;
		font-weight: bold;
		background-color: #eaf6ff;
		box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
	}

	.underline {
		position: absolute;
		bottom: 0;
		height: 3px;
		background-color: #34A2E8;
		left: 0;
		transition: transform 0.3s ease, width 0.3s ease;
	}

	.course {
		width: 300rpx;
		height: 270rpx;
		margin-top: -30px;
		// border: 1px solid gainsboro;
		border-radius: 2em;
	}

	.words p {
		display: block;
		margin-top: -20px;
	}

	.afford {
		display: flex;
		flex-direction: row;

	}

	.guess {
		display: flex;
		flex-direction: row;
		flex-flow: wrap-reverse;
		// border: 1px solid gainsboro;
		margin-left: 48rpx;
	}

	.change {
		// border: 1px solid gainsboro;
		display: flex;
		// flex-direction: ;

		margin-left: 400rpx;
	}

	// .swiper-container {
	// 	width: 100%;
	// 	overflow: hidden;
	// }

	.recommend-item {
		display: flex;
		flex-direction: unset;
		;

		// align-items: center;
		padding: 10px;
	}

	.item-image {
		width: 160px;
		/* 根据需要调整 */
		height: 100px;
		/* 根据需要调整 */
		margin-bottom: 5px;
	}

	.item-caption {
		text-align: center;
		font-size: 14px;
		font-weight: 30em;
		display: block;
		// border: 1px solid gainsboro;
		margin-top: 4px;
		width: 150px;
	}

	.caption2 {
		font-size: 12px;
		margin-top: 25px;
		margin-left: -136px;
		width: 270rpx;
		height: 150rpx;
		// border: 1px solid gainsboro
	}

	.other-items {
		display: flex;
		flex-wrap: wrap;
		gap: 20px;
		padding: 20px;
	}

	.other-item {
		width: calc(50% - 10px);
		/* Adjust based on the number of columns you want */
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		border-radius: 8px;
		overflow: hidden;
		background-color: #fff;
		display: flex;
		flex-direction: column;
	}

	.other-item-image {
		width: 100%;
		height: 150px;
		/* You can adjust this to fit your design */
		object-fit: contain;
		/* Ensure the image fits within the container without distorting */
	}

	.other-item-content {
		padding: 16px;
		flex-grow: 1;
	}

	.other-item-title {
		font-size: 18px;
		font-weight: bold;
		margin-bottom: 8px;
	}

	.other-item-description {
		font-size: 14px;
		color: #666;
		margin-bottom: 12px;
	}

	.other-item-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 8px;
	}

	.other-item-tag {
		background-color: #f0f0f0;
		border-radius: 15px;
		padding: 4px 8px;
		font-size: 12px;
		color: #666;
		border: 1px solid #e0e0e0;
	}
</style>