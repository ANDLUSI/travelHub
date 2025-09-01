<script setup>
import { ref, reactive, onMounted } from 'vue'
import { page, deleteById, editRole, pageRole } from '@/api/admin'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus';

const router = useRouter()
const showSearch = ref(true) // 控制查询框的显示与隐藏

const toggleSearch = () => {
    showSearch.value = !showSearch.value
}
//分页查询结果的数据总条数
const total = ref(0)
const queryPageParams = reactive({
    username: '',
    role: null,
    pageNo: 1,
    pageSize: 5
})

//vue挂载完成后 指定onMounted钩子函数
onMounted(() => {
    queryPage()
})

//分页查询
const queryPage = () => {
    if (queryPageParams.role == null) {
        page(queryPageParams).then((res) => {
            console.log(`output-->res`, res)
            tableData.value = res.data.records
            total.value = res.data.total
        })
    } else {
        pageRole(queryPageParams).then((res) => {
            console.log(`output-->res`, res)
            tableData.value = res.data.records
            total.value = res.data.total
        })
    }
}


//筛选权限查询
const filterTag = (roleParam) => {
    queryPageParams.role = roleParam == -1 ? '' : roleParam
    queryPageParams.role = roleParam
    queryPage()
};

const filterList = ref([])

const filterMethod = () => {
    // console.log(`output->key`, key)
    // console.log(`output->bb.value`, bb.value)
    // console.log(`output->bb.value[0]`, bb.value[0])
    console.log(`output->filterList.value[0]`, filterList.value[0])
    queryPageParams.role = filterList.value[0]
    queryPage()
}

const handleQueryPage = () => {
    queryPage()
}

//查看
const handleDetailClick = (id) => {
    router.push({
        path: '/admin/detail',
        query: { id }
    })
}

//编辑
const handleEditRole = (id, type) => {

    editRole(id, type).then((res) => {
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

//新增
const handleAdd = () => {
    //跳转到新增或者更新的页面
    router.push({
        path: '/admin/saveAdmin'
    })
    queryPage()
}


const tableData = ref([
    {}
])

//修改每页数量的回调函数，参数表示修改完之后的每页数量
const handleSizeChange = (sizeValue) => {
    console.log(`output -> sizeValue`, sizeValue)
    queryPageParams.pageSize = sizeValue
    //请求后端api
    queryPage()
}
//改变页码的回调函数，参数表示修改完之后的页码
const handleCurrentChange = (currentValue) => {
    console.log(`output - > currentValue`, currentValue)
    queryPageParams.pageNo = currentValue
    queryPage()
}


//确认删除
const handleDeleteConfirm = (id) => {
    console.log(`output->id`, id)
    deleteById(id).then(() => {
        //删除成功，再次分页查询，从后端获取最新数据，解决数据显示最新的
        queryPage()
    })
}
//确认取消
const handleDeleteCancel = () => {
    console.log(`handleDeleteCancel`)
}


</script>

<template>
    <br>
    <div class="action-bar">
        <div class="search-container" v-if="showSearch">
            <label for="username">查询管理员：</label>
            <el-input id="username" v-model="queryPageParams.username" style="width: 240px " />
            <p>&nbsp;&nbsp;&nbsp;</p>
            <el-button type="primary" round @click="handleQueryPage">查询</el-button>
        </div>
        <div class="pig">
            <el-button color="#626aef" @click="toggleSearch">搜索查询</el-button>
            <el-button type="primary" round @click="handleAdd" class="add-button">新增</el-button>
        </div>
    </div>
    <br>
    <!-- <div style="margin-bottom: 10px; margin-top: 20px;">
        <el-select v-model="Role" placeholder="选择角色" @change="filterTag" clearable>
            <el-option label="全部" value="-1"></el-option>
            <el-option label="超级管理员" value="1"></el-option>
            <el-option label="管理员" value="0"></el-option>
        </el-select>
    </div> -->
    <el-table :data="tableData" style="width: 100%" stripe highlight-current-row @filter-change="filterMethod">
        <el-table-column fixed prop="username" label="管理员名称" width="150" header-align="center" align="center" />
        <!--el-table-column插槽: scope.row 获取到一行数据-->
        <el-table-column label="头像" width="150" header-align="center" align="center">
            <template #default="scope">
                <el-image style="width: 60px; height: 60px" :src="scope.row.imagePath" />
            </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" width="180" header-align="center" align="center" />
        <el-table-column prop="lastlogTime" label="最近登录时间" width="180" header-align="center" align="center" />
        <!-- <el-table-column prop="role" label="角色权限" width="100" header-align="center" align="center">
            <template #default="scope">
                <span v-if="scope.row.role == 1">超级管理员</span>
                <span v-else-if="scope.row.role == 0">管理员</span>
            </template>
        </el-table-column> -->
        <el-table-column fixed="right" label="操作" min-width="200" header-align="center" align="center">
            <template #default="scope">
                <el-button color="#626aef" @click="handleDetailClick(scope.row.id)">查看信息</el-button>
                <!-- <el-button type=" primary" size="small" @click="handleEdit(scope.row.id)">编辑</el-button> -->
                <el-popconfirm confirm-button-text="确定" cancel-button-text="取消" icon-color="#626AEF" title="是否确认删除?"
                    @confirm="handleDeleteConfirm(scope.row.id)" @cancel="handleDeleteCancel">

                    <template #reference>
                        <el-button type="danger" size="default">删除</el-button>
                    </template>
                </el-popconfirm>

            </template>
        </el-table-column>
        <el-table-column fixed="right" label="权限修改" min-width="150" header-align="center" align="center">
            <template #default="scope">
                <el-popconfirm confirm-button-text="超级管理员" cancel-button-text="管理员" icon-color="#FFDEAD"
                    title="修改管理员权限为" @confirm="handleEditRole(scope.row.id, 1)"
                    @cancel="handleEditRole(scope.row.id, 0)" :popper-class="'custom-popconfirm'" width="250">
                    <template #reference>
                        <el-button type="danger" size="default"
                            style="background-color: #FFD700; color: white;">编辑</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
        <el-table-column fixed="right" label="角色权限" width="150" :filters="[
            { text: '超级管理员', value: 1 },
            { text: '管理员', value: 0 }
        ]" filter-placement="bottom-end" :filter-multiple="false" :filtered-value="filterList">

            <template #default="scope">
                <el-tag :type="scope.row.role === 1 ? 'success' : 'info'">
                    {{ scope.row.role == 1 ? '超级管理员' : '管理员' }}
                </el-tag>
            </template>
        </el-table-column>

    </el-table>
    <!-- 不加冒号值写死 -->
    <!--:total 绑定返回结果总条数变量-->

    <br>
    <el-pagination v-model:current-page="queryPageParams.pageNo" v-model:page-size="queryPageParams.pageSize"
        :page-sizes="[5, 10, 20, 30]" size="default" background layout="total, sizes, prev, pager, next, jumper"
        :total="total" prev-text="上一页" next-text="下一页" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" class="pagination-bar" />
</template>


<style scoped>
.custom-popconfirm {
    background-color: #f5f5f5;
    /* 修改背景颜色 */
    border-radius: 8px;
    /* 圆角 */
    padding: 10px;
    /* 内边距 */
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    /* 添加阴影 */
    max-width: 250px;
    /* 限制最大宽度 */
    font-size: 14px;
    /* 字体大小 */
    white-space: nowrap;
    /* 禁止换行 */
}

.custom-popconfirm .el-popconfirm__action {
    text-align: center;
    /* 按钮居中 */
}

.custom-popconfirm .el-popconfirm__main {
    font-weight: bold;
    /* 粗体显示标题 */
    text-align: center;
    /* 标题居中 */
}

.pig {
    position: absolute;
    /* 绝对定位 */
    right: 0;
    /* 靠右对齐 */
    top: 0;
    /* 根据需要设置顶部位置 */
}

.action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    min-height: 50px;
    /* 用于绝对定位 */
}

.search-container {
    display: flex;
    align-items: center;
}

.add-button {
    background-color: #626aef;
    color: white;
    border-radius: 20px;
    padding: 10px 20px;
    margin-left: 20px;
}

.pagination-bar {
    display: flex;
    justify-content: flex-end;
}

.el-table {
    border: 2px solid #ccc;
    /* 表格外边框 */
    border-collapse: collapse;
    /* 合并边框，避免双线效果 */
}

.el-table th,
.el-table td {
    border: 2px solid #ccc;
    /* 表格内部各行的边框 */
}

.el-table .el-table__header,
.el-table .el-table__body {
    border: none;
    /* 去除默认边框样式，避免与自定义边框冲突 */
}

</style>