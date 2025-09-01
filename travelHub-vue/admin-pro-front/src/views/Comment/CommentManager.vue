<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageAll, getSubCommentsById, deleteCommentById } from '@/api/comment'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter()
const showSearch = ref(true) // 控制查询框的显示与隐藏

const toggleSearch = () => {
    showSearch.value = !showSearch.value
}
const tableData = ref([])
// 分页查询结果的数据总条数
const total = ref(0)
const queryPageParams = reactive({
    title: '',
    status: null,
    pageNo: 1,
    pageSize: 10
})
const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString();
}
// Vue挂载完成后指定onMounted钩子函数
onMounted(() => {
    queryPage()
})
// 分页查询
const queryPage = () => {
    pageAll(queryPageParams).then((res) => {
        const comments = res.data.records;
        console.log(`output->comments`,comments)
        comments.sort((a, b) => (a.root === null ? -1 : 1));

        // 创建一个映射对象，存储所有评论
        const commentMap = {};

        // 遍历所有评论，将它们添加到映射中
        comments.forEach(comment => {
            comment.showReplies = false;  // 初始化 showReplies 为 false
            comment.children = [];  // 初始化 children 数组
            commentMap[comment.id] = comment;
        });

        // 构建评论树，按照 root 和 parentId 进行组织
        comments.forEach(comment => {
            if (comment.root !== null) {
                const rootComment = commentMap[comment.root];
                if (rootComment) {
                    rootComment.children.push(comment);
                }
            }
        });

        // 过滤出顶级评论
        const topLevelComments = comments.filter(comment => comment.root === null);

        // 更新表格数据和总数
        tableData.value = topLevelComments;
        total.value = res.data.total;
    });
};

// 当展开行时加载子评论数据
const loadSubComments = (row) => {
    if (row.children.length === 0) { // 只有在没有加载过时才去加载数据
        getSubCommentsById(row.id).then((res) => {
            console.log(`output->resaaaaaaaaaaaaaaaaaaaaa`, res)
            row.children = res; // 将子评论数据填充到 children 中
            console.log(`output->resaaaaaaaaaaaaaaaaaaaaa`, row.children)
        }).catch(() => {
            ElMessage.error('加载子评论失败');
        });
    }
}


const filterList = ref([])

const handleQueryPage = () => {
    queryPage()
}

// 回复操作
const handleReply = (comment) => {
    console.log("Replying to:", comment);
}

// 删除操作
const handleDelete = (comment) => {
    ElMessageBox.confirm(
        `确定要删除此评论吗？`,
        '确认删除',
        {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        // 执行删除操作
        console.log(`output->comment.id`,comment.id)
        deleteComment(comment.id)
            .then(() => {
                ElMessage({
                    type: 'success',
                    message: '评论已标记为删除',
                });
                // 更新界面上的评论内容
                comment.content = '该评论已删除';
                comment.isDelete = 1;
            })
            .catch(() => {
                ElMessage({
                    type: 'error',
                    message: '删除评论失败',
                });
            });
    }).catch(() => {
    });
};


const deleteComment = (id) => {
    deleteCommentById(id).then(() => {
        ElMessage({
            message: '删除成功！',
            type: 'success',
        });
        queryPage()
    })

};
const toggleReply = (comment) => {
    comment.showReplies = !comment.showReplies;
}

// 修改每页数量的回调函数
const handleSizeChange = (sizeValue) => {
    queryPageParams.pageSize = sizeValue
    queryPage()
}

// 改变页码的回调函数
const handleCurrentChange = (currentValue) => {
    queryPageParams.pageNo = currentValue
    queryPage()
}

</script>

<template>
    <br>
    <div class="action-bar">
        <div class="search-container" v-if="showSearch">
            <label for="content">查询评论内容：</label>
            <el-input id="content" v-model="queryPageParams.title" style="width: 240px " />
            <p>&nbsp;&nbsp;&nbsp;</p>
            <el-button type="primary" round @click="handleQueryPage">查询</el-button>
        </div>
        <div class="pig">
            <el-button color="#626aef" @click="toggleSearch">搜索查询</el-button>
        </div>
    </div>
    <br>

    <el-table :data="tableData" style="width: 100%" stripe highlight-current-row row-key="id"
        @expand-change="loadSubComments">
        <!-- 攻略标题列 -->
        <el-table-column prop="guideTitle" label="攻略标题" width="150" header-align="center"
            align="center"></el-table-column>

        <!-- 用户列 -->
        <el-table-column prop="username" label="用户" width="150" header-align="center" align="center"></el-table-column>

        <!-- 展开符号列 -->
        <el-table-column type="expand" v-if="tableData.length > 0">
            <template v-slot="scope">
                <div v-if="scope.row.children && scope.row.children.length > 0">
                    <el-table :data="scope.row.children" style="width: 100%;border:none;">
                        <el-table-column width="132"></el-table-column>
                        <el-table-column prop="username" label="用户" width="200" header-align="center"
                            align="center"></el-table-column>
                        <el-table-column label="评论内容" header-align="center" align="center">
                            <template v-slot="scope">
                                <div style="display: flex; justify-content: space-between; align-items: center;">
                                    <span>回复@{{ scope.row.replyToUser }}: &nbsp; {{ scope.row.content }}</span>
                                    <div>
                                        <!-- <el-button @click="handleReply(scope.row)"
                                            style="margin-left: 10px;">回复</el-button> -->
                                        <el-button @click="handleDelete(scope.row)" type="danger">删除</el-button>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createdTime" label="发布时间" width="180" header-align="center"
                            align="center">
                            <template #default="scope">
                                {{ formatDateTime(scope.row.createdTime) }}
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </template>
        </el-table-column>

        <!-- 评论内容列 -->
        <el-table-column label="评论内容" header-align="center" align="center">
            <template v-slot="scope">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>{{ scope.row.content }}</span>
                    <div>
                        <!-- <el-button @click="handleReply(scope.row)" style="margin-left: 10px;">回复</el-button> -->
                        <el-button @click="handleDelete(scope.row)" type="danger">删除</el-button>
                    </div>
                </div>
            </template>
        </el-table-column>

        <!-- 发布时间列 -->
        <el-table-column prop="createdTime" label="发布时间" width="180" header-align="center"
            align="center"></el-table-column>

    </el-table>

    <br>
    <el-pagination v-model:current-page="queryPageParams.pageNo" v-model:page-size="queryPageParams.pageSize"
        :page-sizes="[10, 20, 30,40]" size="default" background layout="total, sizes, prev, pager, next, jumper"
        :total="total" prev-text="上一页" next-text="下一页" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" class="pagination-bar" />
</template>

<style scoped>
.pig {
    position: absolute;
    right: 0;
    top: 0;
}

.action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    min-height: 50px;
}

.search-container {
    display: flex;
    align-items: center;
}

.pagination-bar {
    display: flex;
    justify-content: flex-end;
}

.el-table {
    border: 2px solid #ccc;
}

.el-table th,
.el-table td {
    border: 2px solid #ccc;
}

.el-table .el-table__header,
.el-table .el-table__body {
    border: none;
}
</style>
