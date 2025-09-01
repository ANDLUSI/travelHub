<script setup>
import { ref, onMounted,reactive } from 'vue';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox,ElLoading  } from 'element-plus';
import {addView, pageSelect, saveTravel} from '@/api/guide';
import { useUserStore } from '@/stores/user';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

// 添加到其他的函数之后
const handleBack = () => {
    router.back();
};

const userStore = useUserStore();
const headers = {
    'token': userStore.getToken
};

// 数据定义
const form = ref({
    title: '',
    content: '',  // 将内容初始化为空字符串
    tags: [],  // 选择的标签数组
    imagePath: '',
    authorId: userStore.user.id,
    month: null,  // 存储选择的月份
    day: null,   // 存储选择的天数
    fee: null,   // 存储选择的费用
    people: null, // 存储选择的人物
});
const formRef = ref(null); // 用于绑定 el-form
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
const tags = ref([
    '文化旅游', '美食探索', '自驾游', '徒步旅行', '背包客', '探险旅游', '蜜月旅行',
    '家庭度假', '浪漫假期', '滑雪旅游', '温泉度假', '海滩度假', '城市探索', '自然风光',
    '摄影之旅', '历史遗迹', '博物馆之旅', '宗教朝圣', '生态旅游', '徒步露营', '公路旅行',
    '海岛游', '邮轮旅行', '高山徒步', '古镇探秘', '节日庆典', '购物之旅', '奢华度假', '极限运动'
]);

const rules = ref({
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { max: 30, message: '标题不能超过30个字', trigger: 'blur' }
    ],
    month: [
        { required: true, message: '请选择月份', trigger: 'change' }
    ],
    day: [
        { required: true, message: '请选择天数', trigger: 'change' }
    ],
    fee: [
        { required: true, message: '请选择费用', trigger: 'change' }
    ],
    people: [
        { required: true, message: '请选择人物', trigger: 'change' }
    ]
});

let quillInstance = null;

onMounted(() => {
    const quillElement = document.querySelector('.quill-editor');
    quillInstance = new Quill(quillElement, {
        theme: 'snow',
        modules: {
            toolbar: {
                container: [
                    [{ header: [1, 2, 3, false] }],
                    ['bold', 'italic', 'underline'],
                    ['image'], // 保留图片上传按钮
                    [{ list: 'ordered' }, { list: 'bullet' }],
                ],
                handlers: {
                    image: () => {
                        const input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');
                        input.click();

                        input.onchange = async () => {
                            const file = input.files[0];
                            if (beforeUpload(file)) {
                                const formData = new FormData();
                                formData.append('imagefile', file);

                                try {
                                    const response = await axios.post('/api/user/upload/image', formData, {
                                        headers: {
                                            'Content-Type': 'multipart/form-data',
                                            ...headers
                                        }
                                    });

                                    if (response.data.state === 0) {
                                        const url = response.data.data;

                                        // 强制编辑器获得焦点
                                        quillInstance.focus();

                                        let range = quillInstance.getSelection();

                                        if (!range) {
                                            range = { index: quillInstance.getLength(), length: 0 };
                                        }

                                        quillInstance.insertEmbed(range.index, 'image', url);

                                        // 添加样式限制图片大小
                                        const img = quillInstance.root.querySelector(`img[src="${url}"]`);
                                        if (img) {
                                            img.style.maxWidth = '400px'; // 限制图片最大宽度为200px
                                            img.style.height = 'auto';    // 保持图片比例
                                            img.style.display = 'block';  // 图片独占一行
                                        }
                                    } else {
                                        handleUploadSuccess(response.data);
                                    }
                                } catch (error) {
                                    ElMessage.error('图片上传失败');
                                }
                            }
                        };
                    }
                }
            }
        },
    });
    console.log('Quill editor initialized:', quillInstance);
});


// 图片上传前的验证
const beforeUpload = (rawFile) => {
    if (!(rawFile.type === 'image/jpeg' || rawFile.type === 'image/jpg' || rawFile.type === 'image/png')) {
        ElMessage.error('上传图片必须是jpeg、jpg、png格式!');
        return false;
    } else if (rawFile.size / 1024 / 1024 > 50) {
        ElMessage.error('图片的大小不能超过50M!');
        return false;
    }
    return true;
};

// 图片上传成功后的处理
const handleUploadSuccess = (res) => {
    if (res.state > 0) {
        ElMessage.error(res.message || "系统出错");
    } else if (res.state === -1) {
        ElMessageBox.alert('登录已经过期，请重新登录', '登录过期', {
            confirmButtonText: '重新登录',
            callback: () => {
                window.location.href = "/login";
            },
        });
    } else {
        form.value.imagePath = res.data;  // 保存返回的图片路径
    }
};

// 提交处理
const handleSave = async () => { // 改为async函数
  if (!form.value.imagePath) {
    ElMessage.error('请上传封面图片');
    return;
  }
  if (!form.value.fee||!form.value.people || !form.value.day || !form.value.month) {
    ElMessage.error('请选择标签');
    return;
  }
  if (formRef.value) {
    formRef.value.validate(async (valid) => { // 验证改为async
      if (valid) {
        // 显示加载状态
        const loadingInstance = ElLoading.service({
          lock: true,
          text: '正在发表游记，请稍候...',
          background: 'rgba(0, 0, 0, 0.7)',
        });

        try {
          form.value.content = quillInstance.root.innerHTML;
          form.value.tags = JSON.stringify(form.value.tags);

          // 等待保存结果
          await saveTravel(form.value);

          // 关闭加载状态
          loadingInstance.close();

          ElMessage({
            message: '请求成功，等待审核，可在攻略管理中查看结果！',
            type: 'success',
            duration: 3000
          });

          // 可选：重置表单
          formRef.value.resetFields();
          quillInstance.root.innerHTML = '';
          form.value.imagePath = '';

        } catch (error) {
          // 关闭加载状态
          loadingInstance.close();

          ElMessage({
            message: error.response?.data?.message || '保存失败，请重试',
            type: 'error',
            duration: 3000
          });
        }
      } else {
        ElMessage.error('请确保所有输入数据都合法');
      }
    });
  }
};

// 选中的筛选项
const selectedFilters = reactive({
  month: 0,
  day: 0,
  fee: 0,
  people: 0,
  tag: '全部'
})

// 搜索框数据
const searchQuery = ref('')

// 搜索功能的实现
const handleSearch = () => {
  console.log('搜索内容:', searchQuery.value)
  // 判断搜索框是否为空
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索内容');
    return;
  }

  // 如果搜索框不为空，继续执行搜索
  console.log('搜索内容:', searchQuery.value);
  // 在这里执行你的搜索逻辑，例如调用接口或筛选数据等
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

// 接收和存储攻略数据
const tableData = ref([])
const total = ref(0)

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
const openGuideDetail = (id) => {
  console.log('Opening guide with ID:', id);

  // 发起请求获取攻略详情
  axios.get(`http://localhost:5173/api/guide/UGdetail/${id}`, {
    headers: {
      'token': userStore.getToken // 加上 token 进行身份验证
    }
  })
      .then((response) => {
        const data = response.data;
        if (data.state === 0) {
          const guideDetail = data.data;
          // 假设返回的数据包括 title、content 和其他属性
          form.value.title = guideDetail.title;  // 更新标题
          form.value.content = guideDetail.content;  // 更新内容
          form.value.imagePath = guideDetail.imagePath;  // 更新封面图片路径
          // form.value.tags = guideDetail.tags || [];  // 更新标签数组
          form.value.month = guideDetail.month;  // 更新月份
          form.value.day = guideDetail.day;  // 更新天数
          form.value.fee = guideDetail.fee;  // 更新费用
          form.value.people = guideDetail.people;  // 更新人物

          // 更新富文本编辑器的内容
          quillInstance.root.innerHTML = form.value.content;

          // 你也可以在这里做其他更新，比如显示图片预览等
          dialogImageUrl.value = guideDetail.imagePath;  // 显示图片预览
        } else {
          ElMessage.error('获取攻略详情失败');
        }
      })
      .catch((error) => {
        console.error('Error fetching guide detail:', error);
        ElMessage.error('请求失败，请重试');
      });
};



const randomColor = () => {
  const colors = ['#FF5733', '#33FF57', '#F33FF5', '#FF33A1', '#F5FF33']
  return colors[Math.floor(Math.random() * colors.length)]
}
</script>


<template>
    <el-main>
        <el-button @click="router.back()" type="primary" class="back-button">
            <el-icon>
                <ArrowLeft />
            </el-icon>
            返回
        </el-button>

      <!-- 攻略部分 -->
      <section class="guide-list">
        <h3>一起写游记</h3>
        <div class="search-container">
          <el-input v-model="searchQuery" placeholder="搜索攻略..." clearable class="search-input" />
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search />
            </el-icon>
            搜索
          </el-button>
        </div>

        <!-- 攻略列表展示 -->
        <el-row :gutter="10">
          <!-- 将每行展示一个攻略改为每行展示两个 -->
          <el-col :span="8" v-for="guide in tableData" :key="guide.id">
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

        <section class="section">
            <h2>发表新游记</h2>
            <el-row>
                <!-- 编辑区域 -->
                <el-col :span="14" class="editor-container">
                    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                        <el-form-item label="标题：" label-width="80px" prop="title">
                            <el-input v-model="form.title" placeholder="请输入攻略标题" spellcheck="false"></el-input>
                        </el-form-item>
                        <el-form-item label="正文：" label-width="80px">
                            <div class="quill-editor-container">
                                <div ref="editorContainer" class="quill-editor" spellcheck="false"></div>
                            </div>
                        </el-form-item>
                    </el-form>
                </el-col>

                <!-- 封面选择区域 -->
                <el-col :span="8" class="upload-section">
                    <div class="cover-uploader">
                        <el-upload class="avatar-uploader" action="/api/user/upload/image" name="imagefile"
                            :headers="headers" :show-file-list="false" :on-success="handleUploadSuccess"
                            :before-upload="beforeUpload">
                            <div class="cover-frame">
                                <img v-if="form.imagePath" :src="form.imagePath" class="cover" />
                                <el-icon v-else class="cover-uploader-icon">
                                    <Plus />
                                </el-icon>
                            </div>
                        </el-upload>
                    </div>
                    <p class="tip">封面添加，单张不大于10M，一次最多50张</p>
                </el-col>
            </el-row>
        </section>

        <section class="section tags-section">
            <h3>游记玩法与标签</h3>

            <el-row :gutter="20">
                <el-col :span="1"></el-col>
                <el-col :span="11" class="tags-container">
                  <el-checkbox-group v-model="form.tags">
                    <el-row :gutter="10">
                      <el-col
                          v-for="tag in tags"
                          :key="tag"
                          :span="8"
                      style="margin-bottom: 10px"
                      >
                          <el-checkbox :label="tag" border class="custom-tag">
                            {{ tag }}
                          </el-checkbox>
                       </el-col>
                    </el-row>
                  </el-checkbox-group>
                </el-col>
                <el-col :span="1"></el-col>
                <!-- 左侧玩法选择 -->
                <el-col :span="11" class="single-choice-container">
                    <el-form-item label="月份：" prop="month">
                        <el-radio-group v-model="form.month">
                            <el-radio :label="1">1-3月</el-radio>
                            <el-radio :label="2">4-6月</el-radio>
                            <el-radio :label="3">7-9月</el-radio>
                            <el-radio :label="4">10-12月</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="天数：" prop="day">
                        <el-radio-group v-model="form.day">
                            <el-radio :label="1">1-3天</el-radio>
                            <el-radio :label="2">4-7天</el-radio>
                            <el-radio :label="3">8-10天</el-radio>
                            <el-radio :label="4">11-15天</el-radio>
                            <el-radio :label="5">15天以上</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="费用：" prop="fee">
                        <el-radio-group v-model="form.fee">
                            <el-radio :label="1">1-999</el-radio>
                            <el-radio :label="2">1000-2999</el-radio>
                            <el-radio :label="3">3000-4999</el-radio>
                            <el-radio :label="4">5000-9999</el-radio>
                            <el-radio :label="5">10000以上</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="人物：" prop="people">
                        <el-radio-group v-model="form.people">
                            <el-radio :label="1">独自一人</el-radio>
                            <el-radio :label="2">三五好友</el-radio>
                            <el-radio :label="3">亲子</el-radio>
                            <el-radio :label="4">家庭</el-radio>
                            <el-radio :label="5">情侣</el-radio>
                            <el-radio :label="6">闺蜜</el-radio>
                            <el-radio :label="7">学生</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>

                <!-- 右侧标签选择 -->

            </el-row>
        </section>

        <section class="section actions-section">
            <el-button type="primary" @click="handleSave">发表</el-button>
        </section>
    </el-main>

    <!-- 图片预览模态框 -->
    <el-dialog :visible="dialogVisible">
        <img :src="dialogImageUrl" width="100%" alt="" />
    </el-dialog>
</template>

<style scoped>
.el-main {
    background-color: #ebf8fe;
    /* 浅蓝色背景 */
    padding: 20px;
}
.single-choice-container {
    padding-right: 20px;
    border: 1px solid #c0c0c0;
    padding: 10px;
    background-color: #fffbed;
    /* 改为柔和的浅灰色背景 */
    box-sizing: border-box;
    border-radius: 10px;
}

.tags-container {
    padding-left: 20px;
    border: 1px solid #c0c0c0;
    padding: 10px;
    background-color: #fffbed;
    box-sizing: border-box;
    border-radius: 10px;
}

.tags-container .el-checkbox {
    margin-bottom: 20px;
    color: #333333;
    background-color: transparent;
    /* 改为温暖的浅橙色背景 */
    border-radius: 4px;
    width: 88%;
    text-align: center;
}

.section {
    margin: 30px 0;
}

.editor-container {
    width: 60%;
    float: left;
}

.quill-editor-container {
    width: 100%;
}

.quill-editor {
    width: 100%;
    height: 300px;
    border: 1px solid #c0c0c0;
    padding: 10px;
    box-sizing: border-box;
    background-color: #ffffff;
    /* 改为纯白背景，保持干净的编辑环境 */
}

.upload-section {
    width: 40%;
    float: right;
    text-align: center;
    padding-top: 30px;
}

.cover-uploader {
    width: 200px;
    height: 200px;
    margin: 0 auto;
    border: 1px solid #c0c0c0;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    /* 改为浅灰色背景 */
}

.cover {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
}

.cover-uploader-icon {
    font-size: 50px;
    color: #4a90e2;
    /* 改为亮眼的蓝色，吸引用户上传图片 */
}

.el-upload-list__item {
    display: none;
}

.actions-section {
    text-align: left;
    margin-top: 20px;
}

.tip {
    font-size: 14px;
    color: #666666;
    /* 改为深灰色，提供更柔和的提示文字 */
    margin-top: 10px;
}

.back-button {
    background-color: #4a90e2;
    /* 改为亮眼的蓝色 */
    color: #ffffff;
    border-radius: 8px;
    padding: 10px 20px;
    font-size: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: background-color 0.3s;
}

.back-button:hover {
    background-color: #357ab8;
    /* 按钮悬停时颜色加深 */
}

.el-button--primary {
    background-color: #4a90e2;
    /* 改为与返回按钮一致的蓝色 */
    border-color: #4a90e2;
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s, box-shadow 0.3s;
}

.el-button--primary:hover {
    background-color: #357ab8;
    /* 悬停时颜色加深 */
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    /* 增强按钮悬停时的阴影效果 */
}

.search-input {
  width: 50%;
  margin-right: 30px;
  background-color: #ffffff;
  /* 白色输入框 */
  color: #333333;
  /* 深色字体 */
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
  height: 180px;
  margin: 20px;
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

.search-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
  margin-right: 10px;
}

.tags-container {
  max-height: 300px;  /* 限制容器高度 */
  overflow-y: auto;   /* 添加滚动条 */
}

.custom-tag {
  width: 100%;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}
</style>
