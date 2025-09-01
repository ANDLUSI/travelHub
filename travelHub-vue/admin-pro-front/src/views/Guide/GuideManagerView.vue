<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageAll, editFeatured, editStatus } from '@/api/guide'
import { useRouter } from 'vue-router'
import GuideDetail from '@/views/Guide/GuideDetail.vue'
import { RefreshRight } from '@element-plus/icons-vue'

const router = useRouter()

const tableData = ref([]) // 初始化的模拟数据
const total = ref(0)
const multipleSelection = ref([]) // 存储多选项
const showBatchReapply = ref(false) // 控制选择框和批量重审按钮的显示
const batchButtonText = ref('批量重审') // 按钮文本

const queryPageParams = reactive({
    title: '',
    author: '',
    isFeatured: '',
    status: '', // 新增审核状态筛选条件
    pageNo: 1,
    pageSize: 10,
    sortOrder: ''
})

// 控制弹出框显示的状态
const isDialogVisible = ref(false)
// 存储当前选中攻略的ID
const selectedGuideId = ref(null)

onMounted(() => {
    queryPage()
})

// 分页查询
const queryPage = () => {
    pageAll(queryPageParams).then((res) => {
        tableData.value = res.data.records
        total.value = res.data.total
    })
}

// 修改每页数量的回调函数，参数表示修改完之后的每页数量
const handleSizeChange = (sizeValue) => {
    queryPageParams.pageSize = sizeValue
    queryPage()
}

// 改变页码的回调函数，参数表示修改完之后的页码
const handleCurrentChange = (currentValue) => {
    queryPageParams.pageNo = currentValue
    queryPage()
}

// 重置过滤器
const resetFilters = () => {
    queryPageParams.title = ''
    queryPageParams.isFeatured = ''
    queryPageParams.status = ''
    queryPageParams.sortOrder = ''
    queryPage()
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

// 处理表格选择项变化
const handleSelectionChange = (val) => {
    multipleSelection.value = val
}

// 切换批量操作状态
const toggleBatchReapply = () => {
    showBatchReapply.value = !showBatchReapply.value
    batchButtonText.value = showBatchReapply.value ? '退出批量操作' : '批量重审'
}

// 批量重审处理函数
const handleBatchReapply = () => {
    if (multipleSelection.value.length === 0) {
        ElMessage.warning('请至少选择一项进行重审');
        return;
    }

    // 确认对话框
    ElMessageBox.confirm('确定要批量重审选中的攻略吗?', '批量重审', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        // 存储每次操作的结果
        const promises = multipleSelection.value.map(item => {
            return handleEditStatus(item.id, 0);
        });

        // 处理所有操作完成后的逻辑
        Promise.all(promises).then(results => {
            // 只显示一个汇总的提示
            ElMessage({
                message: `成功重审了 ${results.length} 个攻略！`,
                type: 'success'
            });

            showBatchReapply.value = false; // 重置状态
            batchButtonText.value = '批量重审' // 恢复按钮文本
            queryPage(); // 刷新页面
        }).catch(error => {
            // 错误处理
            ElMessage({
                message: '批量重审过程中出现错误，请重试。',
                type: 'error'
            });
        });
    }).catch(() => {
        ElMessage.info('已取消批量重审');
    });
}

const handleEditStatus = (id, type) => {
    // 返回一个 Promise 以便在批量操作中处理
    return editStatus(id, type).then((res) => {
        queryPage()
        return res; // 返回操作结
    }).catch(error => {
        // 错误处理
        ElMessage({
            message: '修改失败，请重试。',
            type: 'error'
        });
        
        throw error; // 抛出错误以便在批量操作中处理
    });
}
// 修改推荐状态函数
const handleEditIsFeatured = (id, type) => {
    editFeatured(id, type).then((res) => {
        ElMessage({
            message: res.message,
            type: 'success'
        });
        queryPage()
    }).catch(error => {
        // 错误处理
        ElMessage({
            message: '修改失败，请重试。',
            type: 'error'
        });
    });
}

// 排序处理函数
const sortchange = ({ prop, order }) => {
    queryPageParams.sortOrder = order === 'ascending' ? 'asc' : (order === 'descending' ? 'desc' : '')
    queryPage()
}
</script>

<template>
    <el-card>

        <el-form :inline="true" :model="queryPageParams" class="demo-form-inline">
            <el-form-item label="攻略标题">
                <el-input v-model="queryPageParams.title" placeholder="请输入攻略标题" />
            </el-form-item>
            <el-form-item label="是否推荐" style="width: 220px;">
                <el-select v-model="queryPageParams.isFeatured" placeholder="请选择是否推荐">
                    <el-option label="所有" value="" />
                    <el-option label="是" value="1" />
                    <el-option label="否" value="0" />
                </el-select>
            </el-form-item>
            <el-form-item label="审核状态" style="width: 220px;">
                <el-select v-model="queryPageParams.status" placeholder="请选择审核状态">
                    <el-option label="所有" value="" />
                    <el-option label="待审核" value="0" />
                    <el-option label="审核通过" value="1" />
                    <el-option label="审核未通过" value="-1" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="queryPage">查询</el-button>
                <el-button @click="resetFilters">重置</el-button>
                <div style="display: inline-block; width: 140px;"></div>
                <el-button type="primary" @click="toggleBatchReapply">{{ batchButtonText }}</el-button>
            </el-form-item>

        </el-form>

        <el-table :data="tableData" stripe style="width: 100%" @sort-change="sortchange"
            @selection-change="handleSelectionChange">
            <el-table-column v-if="showBatchReapply" type="selection" width="55" />

            <el-table-column prop="title" label="攻略标题" width="200" header-align="center" align="center" />
            <el-table-column prop="authorId" label="作者ID" width="150" header-align="center" align="center" />
            <el-table-column prop="isFeatured" label="是否推荐" width="120" header-align="center" align="center">
                <template v-slot="scope">
                    <el-tag :type="scope.row.isFeatured ? 'success' : 'info'">
                        {{ scope.row.isFeatured ? '是' : '否' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="审核状态" width="120" header-align="center" align="center">
                <template v-slot="scope">
                    <el-tag
                        :type="scope.row.status === 1 ? 'success' : (scope.row.status === -1 ? 'danger' : 'warning')">
                        {{ scope.row.status === 1 ? '审核通过' : (scope.row.status === -1 ? '审核未通过' : '待审核') }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" sortable="custom" width="200" header-align="center"
                align="center" />
            <el-table-column fixed="right" label="操作" width="120" header-align="center" align="center">
                <template #default="scope">
                    <el-button color="#626aef" @click="handleDetailClick(scope.row.id)">查看内容</el-button>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="编辑" min-width="200" header-align="center" align="center">
                <template #default="scope">
                    <el-popconfirm confirm-button-text="推荐" cancel-button-text="不推荐" icon-color="#FFDEAD"
                        :title="`是否推荐`" @confirm="handleEditIsFeatured(scope.row.id, 1)"
                        @cancel="handleEditIsFeatured(scope.row.id, 0)" width="250">
                        <template #reference>
                            <el-button size="default" style="background-color:#28a745; color: white;">推荐</el-button>
                        </template>
                    </el-popconfirm>

                    <el-popconfirm confirm-button-text="通过" cancel-button-text="拒绝" icon-color="#FFDEAD" :title="`审核结果`"
                        @confirm="handleEditStatus(scope.row.id, 1)" @cancel="handleEditStatus(scope.row.id, -1)"
                        width="250">
                        <template #reference>
                            <el-button size="default" style="background-color: #FF7F50; color: white;">审核</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="重审" min-width="50" header-align="center" align="center">
                <template #default="scope">
                    <el-button icon="RefreshRight" size="default" type="text"
                        @click="handleEditStatus(scope.row.id, 0)">
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <div v-if="showBatchReapply" style="text-align: right; margin-top: 20px;">
            <el-button type="primary" @click="handleBatchReapply">确认重审</el-button>
        </div>

        <br />
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="queryPageParams.pageNo" :page-sizes="[10, 20, 30, 50]" :page-size="queryPageParams.pageSize"
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
