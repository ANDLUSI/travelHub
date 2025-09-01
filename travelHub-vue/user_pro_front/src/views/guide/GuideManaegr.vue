<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { page, deleteById,findById } from '@/api/guide' // 假设有这些API
import { useRouter } from 'vue-router'
import GuideDetail from '@/views/guide/GuideUserDetail.vue'
import { useUserStore } from '@/stores/user';
const store = useUserStore();
const router = useRouter()

const tableData = ref([]) // 用户自己的攻略数据
const total = ref(0)
const queryParams = reactive({
    title: '',
    status: '', // 新增审核状态筛选条件
    pageNo: 1,
    pageSize: 10,
    sortOrder: '',
    userId:store.user.id
})

// 控制弹出框显示的状态
const isDialogVisible = ref(false)
// 存储当前选中攻略的ID
const selectedGuideId = ref(null)

onMounted(() => {
    fetchGuides()
})

// 分页查询
const fetchGuides = () => {
    page(queryParams).then((res) => {
        tableData.value = res.data.records
        total.value = res.data.total
    })
}

// 修改每页数量的回调函数，参数表示修改完之后的每页数量
const handleSizeChange = (sizeValue) => {
    queryParams.pageSize = sizeValue
    fetchGuides()
}

// 改变页码的回调函数，参数表示修改完之后的页码
const handleCurrentChange = (currentValue) => {
    queryParams.pageNo = currentValue
    fetchGuides()
}

// 重置过滤器
const resetFilters = () => {
    queryParams.title = ''
    queryParams.status = ''
    queryParams.sortOrder = ''
    fetchGuides()
}

// 显示详情弹出框
const handleDetailClick = (id) => {
    selectedGuideId.value = id
    isDialogVisible.value = true
}

// 关闭弹出框
const handleCloseDialog = () => {
    isDialogVisible.value = false
}

// 删除攻略
const handleDeleteGuide = (id) => {
    ElMessageBox.confirm('确定要删除这条攻略吗?', '删除攻略', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteById(id).then(() => {
            ElMessage({
                message: '攻略删除成功',
                type: 'success'
            });
            fetchGuides(); // 刷新页面
        }).catch(() => {
            ElMessage({
                message: '攻略删除失败，请重试。',
                type: 'error'
            });
        });
    }).catch(() => {
        ElMessage.info('已取消删除');
    });
}

// 排序处理函数
const sortchange = ({ prop, order }) => {
    queryParams.sortOrder = order === 'ascending' ? 'asc' : (order === 'descending' ? 'desc' : '')
    fetchGuides()
}
// 新增跳转详情页的函数
const jumpToDetail = (id) => {
    findById(id).then((res) => {
        router.push({ path: `/guide/detail`, query: { id } });
    }).catch(() => {
        ElMessage({
            message: '获取详情失败，请重试。',
            type: 'error'
        });
    });
}

</script>

<template>
    <el-card>

        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
            <el-form-item label="题目">
                <el-input v-model="queryParams.title" placeholder="请输入题目" />
            </el-form-item>
            <el-form-item label="状态" style="width: 220px; ">
                <el-select v-model="queryParams.status" placeholder="请选择状态">
                    <el-option label="所有" value="" />
                    <el-option label="待审核" value="0" />
                    <el-option label="审核通过" value="1" />
                    <el-option label="审核未通过" value="-1" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="fetchGuides">查询</el-button>
                <el-button @click="resetFilters">重置</el-button>
            </el-form-item>
        </el-form>

        <el-table :data="tableData" stripe style="width: 100%" @sort-change="sortchange">
            <el-table-column prop="title" label="题目" width="300" header-align="center" align="center" />
            <el-table-column prop="createTime" label="创建时间" sortable="custom" width="300" header-align="center"
                align="center" />
            <el-table-column prop="status" label="状态" width="300" header-align="center" align="center">
                <template v-slot="scope">
                    <el-tag
                        :type="scope.row.status === 1 ? 'success' : (scope.row.status === -1 ? 'danger' : 'warning')">
                        {{ scope.row.status === 1 ? '审核通过' : (scope.row.status === -1 ? '审核未通过' : '待审核') }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="300" header-align="center" align="center">
                <template #default="scope">
                    <el-button color="#626aef" @click="handleDetailClick(scope.row.id)">查看内容</el-button>
                    <el-button type="danger" @click="handleDeleteGuide(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="action" label="跳转详情页" width="200" header-align="center" align="center">
                <template v-slot="scope">
                    <el-button v-if="scope.row.status === 1" @click="jumpToDetail(scope.row.id)">跳转详情</el-button>
                    <el-tag v-else type="info">审核未通过</el-tag>
                </template>
            </el-table-column>
        </el-table>



        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="queryParams.pageNo" :page-sizes="[10, 20, 30, 50]" :page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total" />
    </el-card>

    <!-- 弹出框部分 -->
    <el-dialog v-model="isDialogVisible" title="攻略详情" width="800px" @close="handleCloseDialog">
        <GuideDetail :id="selectedGuideId" />

        <template v-slot:footer>
            <span class="dialog-footer">
                <el-button @click="handleCloseDialog">关闭</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped>
.demo-form-inline {
    margin-bottom: 20px;
}

.pagination-bar {
    display: flex;
    justify-content: flex-end;
}
</style>
