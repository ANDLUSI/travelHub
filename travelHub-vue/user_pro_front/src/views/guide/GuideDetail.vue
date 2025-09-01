<script setup>
import { ref, onMounted } from 'vue'
import { findUserGuide, likeGuide } from '@/api/guide'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { selectComment, likeComment, saveComment, deleteComment } from '@/api/comment';
import { ElMessage, ElMessageBox,ElLoading } from 'element-plus';

const route = useRoute()
const userStore = useUserStore()
const info = ref({})
const comments = ref([])

const expandedComments = ref({})
const expandedReplies = ref({})

const router = useRouter()

const goBack = () => {
    router.go(-1)
}

const replyingTo = ref(null)
const replyContent = ref('')
const sortType = ref('time')

onMounted(() => {
    window.scrollTo(0, 0);

    const id = route.query.id
    findUserGuide(id).then((res) => {
        info.value = res.data
    })
    selectComment(id).then((res) => {
        console.log(`output->res`,res)
        const commentsArray = Object.values(res.data)
        const topLevelComments = commentsArray.filter(comment => comment.root === null)
        topLevelComments.forEach(topComment => {
            topComment.createdTime = formatTime(topComment.createdTime)
            const replies = commentsArray.filter(comment => comment.root === topComment.id)
            replies.forEach(reply => {
                reply.createdTime = formatTime(reply.createdTime)
            })
            topComment.replies = replies
        })
        comments.value = sortComments(topLevelComments)
    }).catch((error) => {
        console.error('Error fetching comments:', error);
    })
})

// 在需要使用的组件中声明loading实例变量
let loadingInstance = null;

// 启动全屏加载动画
const startLoading = () => {
  loadingInstance = ElLoading.service({
    lock: true,        // 锁定屏幕禁止操作[5](@ref)
    text: '内容审核中', // 自定义加载文案[2,5](@ref)
    background: 'rgba(0, 0, 0, 0.5)' // 半透明遮罩层[1](@ref)
  });
};

// 关闭加载动画
const closeLoading = () => {
  if (loadingInstance) {
    loadingInstance.close(); // 调用实例的close方法[2,5](@ref)
  }
};

const formatTime = (timeString) => {
    return timeString.replace('T', ' ')
}

const sortComments = (commentsArray) => {
    return commentsArray.sort((a, b) => {
        if (sortType.value === 'time') {
            return new Date(b.createdTime) - new Date(a.createdTime);
        } else if (sortType.value === 'hot') {
            return b.likeCount - a.likeCount;
        }
    });
}

const changeSortType = (type) => {
    sortType.value = type;
    comments.value = sortComments(comments.value);
}

const handleGuideLike = () => {
    likeGuide(userStore.user.id, route.query.id).then((res) => {
        if (res.data === null) {
            info.value.likeCount++;
        } else {
            ElMessage({
                message: '你已经点赞过了',
                type: 'danger'
            });
        }
    })
}

const handleLike = (comment) => {
    likeComment(userStore.user.id, comment.id).then((res) => {
        if (res.data === null) {
            comment.likeCount++;
        } else {
            ElMessage({
                message: '你已经点赞过了',
                type: 'danger'
            });
        }
    })
}

const toggleExpand = (commentId) => {
    expandedComments.value[commentId] = !expandedComments.value[commentId];
}

const toggleExpandReplies = (commentId) => {
    expandedReplies.value[commentId] = !expandedReplies.value[commentId];
}

const startReply = (commentId) => {
    if (replyingTo.value === commentId) {
        replyingTo.value = null;
    } else {
        replyingTo.value = commentId;
    }
    replyContent.value = '';
}
//提交评论
const submitReply = async (comment = null) => {
    try{
      const content = replyContent.value.trim();

      if (!content) {
        ElMessage({
          message: '回复内容不能为空',
          type: 'warning'
        });
        return;
      }
      startLoading();// 开始显示加载动画

      //加入AI审核,此处应该有返回值，拿返回值做判断
      const auditResult = await toAI(content);
      if (!auditResult) { // 审核不通过
        ElMessage({
          message: '请文明评论！',
          type: 'warning'
        });
        return;
      }

      if (content ) {
        const newComment = {
          userId: userStore.user.id,
          guideId: route.query.id,
          content: content,
          root: comment ? (comment.root || comment.id) : null,
          parentId: comment ? comment.id : null,
          likeCount: 0,
          replies: []
        };
        saveComment(newComment).then((res) => {
          if (res.data) {
            selectComment(route.query.id).then((res) => {
              const commentsArray = Object.values(res.data);

              const topLevelComments = commentsArray.filter(comment => comment.root === null);
              topLevelComments.forEach(topComment => {
                topComment.createdTime = formatTime(topComment.createdTime);

                const replies = commentsArray.filter(comment => comment.root === topComment.id);
                replies.forEach(reply => {
                  reply.createdTime = formatTime(reply.createdTime);
                });
                topComment.replies = replies;
              });

              comments.value = sortComments(topLevelComments);
            });

            replyContent.value = '';
            replyingTo.value = null;
            ElMessage({
              message: '回复成功',
              type: 'success'
            });
          } else {
            ElMessage({
              message: '回复失败，请稍后再试',
              type: 'error'
            });
          }
        });
      } else {
        ElMessage({
          message: '回复内容不能为空',
          type: 'warning'
        });
      }
    }catch (err){
      console.error('审核异常:', err);
      ElMessage.error('审核服务异常，请重试');
    }finally {
      closeLoading(); // 无论成功失败都关闭加载
    }
};

//AI审核
const toAI = async (content) => {

  const endContent = "评论审核：" + content
  try {
    const payload = {
      assistant_id: '67f0c2c30e96300fa3874977',
      prompt: endContent
    };

    const res = await fetch('https://chatglm.cn/chatglm/assistant-api/v1/stream_sync', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTc0NzY0NTA1MSwianRpIjoiZmRlZjI0ODUtM2FiYy00Y2RhLTg5NDYtYTM2YzkwYzRkYTVmIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6IkFQSV82NzI0NjRhNjMxMDIzNTg1NDhjMDBhYzlfMWRjNzhhYzMiLCJuYmYiOjE3NDc2NDUwNTEsImV4cCI6MTc0ODUwOTA1MSwidWlkIjoiNjc1NmQ2M2VkOGQ5ZmMwZmZhYzFjYWNlIiwidXBsYXRmb3JtIjoiIiwiYXBpX3JvbGUiOiJkZXZlbG9wZXIiLCJyb2xlcyI6WyJhdXRoZWRfdXNlciJdfQ.RwOsSpDhGgv90F_h6dPGLPKyW10f5JVPL5jY1I5cPSk'
      },
      body: JSON.stringify(payload)
    });

    const json = await res.json();
    const text = json?.result?.output?.[0]?.content?.[0]?.text;

    if (!text) {
      ElMessage.error('审核服务异常');
      return false;
    }

    // 根据实际AI返回的审核规则判断
    const isApproved = !text.includes("未通过") &&
        !text.includes("违规") &&
        !text.includes("敏感词");

    return isApproved; // 返回布尔值审核结果

  } catch (err) {
    console.error('审核请求失败:', err);
    ElMessage.error('审核服务暂不可用');
    return false; // 网络异常时默认不通过
  }
};

// 删除评论函数
const handleDelete = (comment) => {
    ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteComment(comment.id).then(() => {
            // 重新获取评论列表
            selectComment(route.query.id).then((res) => {
                const commentsArray = Object.values(res.data);
                const topLevelComments = commentsArray.filter(comment => comment.root === null);
                topLevelComments.forEach(topComment => {
                    topComment.createdTime = formatTime(topComment.createdTime);
                    const replies = commentsArray.filter(comment => comment.root === topComment.id);
                    replies.forEach(reply => {
                        reply.createdTime = formatTime(reply.createdTime);
                    });
                    topComment.replies = replies;
                });
                comments.value = sortComments(topLevelComments);
            });
            ElMessage({
                type: 'success',
                message: '删除成功',
            });
        }).catch(() => {
            ElMessage({
                type: 'error',
                message: '删除失败，请稍后再试',
            });
        });
    }).catch(() => {
        ElMessage({
            type: 'info',
            message: '已取消删除',
        });
    });
};
</script>

<template>
    <div class="guide-container">
        <el-button class="back-button" @click="goBack">
            <i class="el-icon-arrow-left"></i> 返回
        </el-button>
        <!-- 用户信息区 -->
        <div class="user-info-section">
            <div class="user-background">
                <img :src="'/image/spot/背景图.jpg'" alt="User Background" class="background-image" />
            </div>
            <div class="user-details">
                <el-avatar :src="info.authorPath" :size="90" class="user-avatar" />
                <div class="user-info">
                    <div class="user-header">
                        <h2 class="user-title">{{ info.authorName }}</h2>
                        <el-tag type="success" class="user-title-tag">{{ info.authorTitle }}</el-tag>
                    </div>
                    <p class="user-location">位置: {{ info.authorLocation }}</p>
                    <p class="user-last-log-time">最后登录: {{ info.lastlogTime }} </p>
                </div>
            </div>
        </div>

        <div class="content-wrapper">
            <!-- 攻略内容区 -->
            <div class="content-section" v-html="info.content"></div>

            <!-- 评论区 -->
            <div class="comment-section" :class="{ 'empty-comments': comments.length === 0 }">
                <h2>评论区</h2>
                <div class="sort-options">
                    <el-button type="text" @click="changeSortType('time')">按时间排序</el-button>
                    <el-button type="text" @click="changeSortType('hot')">按热度排序</el-button>
                </div>
                <div v-if="comments.length === 0" class="no-data">暂无评论</div>
                <div v-for="comment in comments" :key="comment.id" class="comment">
                    <div class="comment-header">
                        <el-avatar :src="comment.uipath"
                            :style="{ filter: comment.isDelete === 1 ? 'grayscale(100%)' : 'none' }" />
                        <span class="username">{{ comment.isDelete === 1 ? 'XXX' : comment.username }}</span>
                        <span class="location"> {{ comment.useLocation }}</span>
                        <span class="comment-time">{{ comment.createdTime }}</span>
                    </div>
                    <div class="comment-body">
                        <p>
                            <span v-if="!expandedComments[comment.id] && comment.content.length > 100">
                                {{ comment.content.slice(0, 100) }}...
                                <el-button type="text" @click="toggleExpand(comment.id)">展开</el-button>
                            </span>
                            <span v-else>
                                {{ comment.content }}
                                <el-button type="text" v-if="comment.content.length > 100"
                                    @click="toggleExpand(comment.id)">收起</el-button>
                            </span>
                        </p>
                        <el-button type="text" @click="handleLike(comment)" v-if="comment.isDelete !== 1">点赞 {{
                            comment.likeCount }}</el-button>
                        <el-button type="text" @click="startReply(comment.id)"
                            v-if="comment.isDelete !== 1">回复</el-button>
                        <!-- 新增：删除评论按钮 -->
                        <el-button type="text" @click="handleDelete(comment)"
                            v-if="comment.userId === userStore.user.id">删除</el-button>
                    </div>

                    <!-- 子评论展示，支持折叠 -->
                    <div v-if="comment.replies && comment.replies.length" class="sub-comments">
                        <el-button type="text" @click="toggleExpandReplies(comment.id)">
                            {{ expandedReplies[comment.id] ? '收起回复' : `展开 ${comment.replies.filter(reply =>
                                reply.isDelete !== 1).length} 条回复` }}
                        </el-button>

                        <div v-if="expandedReplies[comment.id]" class="sub-comment-list">
                            <div v-for="reply in comment.replies" :key="reply.id" class="sub-comment">
                                <div class="comment-header">
                                    <el-avatar :src="reply.uipath"
                                        :style="{ filter: reply.isDelete === 1 ? 'grayscale(100%)' : 'none' }" />
                                    <span class="username">{{ reply.isDelete === 1 ? 'XXX' : reply.username }}</span>
                                    <span class="location"> {{ reply.useLocation }}</span>
                                    <span class="comment-time">{{ reply.createdTime }}</span>
                                </div>
                                <div class="comment-body">
                                    <p>回复@{{ reply.replyToUser }} : {{ reply.content }}</p>
                                    <el-button type="text" @click="handleLike(reply)" v-if="reply.isDelete !== 1">点赞 {{
                                        reply.likeCount }}</el-button>
                                    <el-button type="text" @click="startReply(reply.id)"
                                        v-if="reply.isDelete !== 1">回复</el-button>
                                    <!-- 新增：删除子评论按钮 -->
                                    <el-button type="text" @click="handleDelete(reply)"
                                        v-if="reply.userId === userStore.user.id">删除</el-button>
                                </div>

                                <!-- 子评论的回复框展示 -->
                                <div v-if="replyingTo === reply.id" class="reply-box">
                                    <el-input v-model="replyContent" placeholder="输入回复内容..." type="textarea" />
                                    <el-button type="primary" @click="submitReply(reply)">提交</el-button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 顶级评论的回复框展示 -->
                    <div v-if="replyingTo === comment.id" class="reply-box">
                        <el-input v-model="replyContent" placeholder="输入回复内容..." type="textarea" />
                        <el-button type="primary" @click="submitReply(comment)">提交</el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 攻略的点赞和评论功能 -->
        <div class="guide-actions">
            <el-button type="text" @click="handleGuideLike">点赞 {{ info.likeCount }}</el-button>
            <el-input v-model="replyContent" placeholder="对攻略发表评论..." type="textarea" />
            <el-button type="primary" @click="submitReply">提交评论</el-button>
        </div>
    </div>
</template>
<style scoped>
.guide-container {
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: #f0f8ff;
}

.back-button {
    align-self: flex-start;
    margin-bottom: 20px;
    background-color: #4a90e2;
    color: #ffffff;
    border-radius: 8px;
    padding: 10px 20px;
    font-size: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    cursor: pointer;
    transition: background-color 0.3s;
}

.back-button:hover {
    background-color: #357ab8;
}

.user-header {
    display: flex;
    align-items: center;
    gap: 10px;
    height: 25px;
}

.user-info-section {
    position: relative;
    width: 100%;
    margin-bottom: 20px;
}

.user-background {
    width: 100%;
    height: 400px;
    overflow: hidden;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.background-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.user-details {
    display: flex;
    align-items: center;
    position: absolute;
    bottom: 30px;
    left: 20px;
}

.user-avatar {
    border: 4px solid #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info {
    margin-left: 15px;
    color: #ffffff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.user-title {
    font-size: 24px;
    font-weight: bold;
}

.user-location,
.user-last-log-time {
    font-size: 16px;
}

.content-wrapper {
    display: flex;
    justify-content: space-between;
}

.content-section {
    width: 60%;
    padding: 20px;
    margin-top: 20px;
    background-color: #fdfff3;
    border: 2px solid #4a90e2;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.comment-section {
    width: 35%;
    padding: 20px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-top: 20px;
    max-height: 600px;
    overflow-y: auto;
    border-left: 4px solid #4a90e2;
    transition: max-height 0.3s ease;
}
.empty-comments {
    max-height: 180px;
    /* 没有评论时，评论区高度减少 */
}

.no-data {
    text-align: center;
    color: #888;
    padding: 20px 0;
}


.sort-options {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 10px;
}

.comment {
    margin-bottom: 20px;
    border-bottom: 1px solid #dcdcdc;
    padding-bottom: 10px;
}

.comment-header {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
}

.username {
    font-weight: bold;
    margin-left: 10px;
    color: #4a90e2;
    /* 改为蓝色 */
}

.location {
    margin-left: 20px;
    color: #888;
    font-size: 12px;
}

.comment-time {
    color: #888;
    margin-left: auto;
}

.comment-body {
    margin-left: 40px;
    color: #555;
    /* 改为更深的灰色 */
}

.sub-comments {
    margin-left: 40px;
    border-left: 2px solid #dcdcdc;
    padding-left: 10px;
    font-size: 14px;
}

.sub-comment-list {
    margin-top: 10px;
    border-top: 1px solid #dcdcdc;
    padding-top: 10px;
}

.sub-comment {
    margin-bottom: 10px;
}

.reply-box {
    margin-top: 10px;
    padding-left: 40px;
}

.guide-actions {
    margin-top: 20px;
    width: 60%;
    padding: 20px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    align-self: flex-start;
}
</style>
