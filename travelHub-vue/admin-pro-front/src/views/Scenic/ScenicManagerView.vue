<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { page, pageTime, pageAll, deleteById, editFeatured } from '@/api/scenic'
import { useRouter } from 'vue-router'

const router = useRouter()

// 初始化的模拟数据
const tableData = ref([
    // { id: 1, spotName: '广州塔', province: 'guangdong', starRating: 5, isFeatured: true, updateTime: '2024-08-01 12:00' },
    // { id: 2, spotName: '西湖', province: 'zhejiang', starRating: 4, isFeatured: false, updateTime: '2024-07-15 09:30' },
    // { id: 3, spotName: '泰山', province: 'shandong', starRating: 5, isFeatured: true, updateTime: '2024-08-10 18:45' },
    // { id: 4, spotName: '故宫', province: 'beijing', starRating: 5, isFeatured: true, updateTime: '2024-08-05 14:20' },
    // { id: 5, spotName: '黄山', province: 'anhui', starRating: 4, isFeatured: false, updateTime: '2024-08-12 10:15' },
    // { id: 6, spotName: '乐山大佛', province: 'sichuan', starRating: 4, isFeatured: true, updateTime: '2024-08-08 11:00' },
    // { id: 7, spotName: '峨眉山', province: 'sichuan', starRating: 3, isFeatured: false, updateTime: '2024-07-20 16:30' },
    // // 添加更多初始化数据
])

const total = ref(tableData.value.length)

const queryPageParams = reactive({
    spotName: '',
    province: '',
    isFeatured: '',
    starSelect: '',
    pageNo: 1,
    pageSize: 10,
    sortOrder: ''
})

// Vue挂载完成后指定onMounted钩子函数
onMounted(() => {
    queryPage()
})

//分页查询
const queryPage = () => {
    if (queryPageParams.sortOrder == '') {
        pageAll(queryPageParams).then((res) => {
            console.log(`output-->res`, res)
            tableData.value = res.data.records
            total.value = res.data.total
        })
    } else {
        pageTime(queryPageParams).then((res) => {
            console.log(`output-->res`, res)
            tableData.value = res.data.records
            total.value = res.data.total
        })
    }
}
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

// 重置过滤器
const resetFilters = () => {
    queryPageParams.isFeatured = null,
        queryPageParams.province = '',
        queryPageParams.spotName = '',
        queryPageParams.starSelect = '',
        queryPageParams.sortOrder = '',
        queryPage()
}

// 编辑操作
const handleDetailClick = (id) => {
    console.log(`output->id`, id)
    console.log(`Type of id:`, typeof id);
    router.push({
        path: `/scenic/detail`,
        query: { id }
    })
    queryPage()
}

//确认删除
const handleDeleteConfirm = (id) => {
    // console.log(`output->id`, id)
    deleteById(id).then(() => {
        //删除成功，再次分页查询，从后端获取最新数据，解决数据显示最新的
        ElMessage({
            message: '删除成功！',
            type: 'success',
        });
        queryPage()
    })
}
//确认取消
const handleDeleteCancel = () => {
    console.log(`handleDeleteCancel`)
}

// 排序处理函数
const sortchange = ({ prop, order }) => {
    // console.log(`sort by ${prop} ${order}`);
    queryPageParams.sortOrder = order === 'ascending' ? 'asc' : (order === 'descending' ? 'desc' : '');
    queryPage();
}

const queryPageAll = () => {
    pageAll(queryPageParams).then((res) => {
        console.log(`output-->res`, res)
        tableData.value = res.data.records
        total.value = res.data.total
    })
}

const handleEditFeatured = (id, type) => {
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

</script>
<template>
    <el-card>
        <el-form :inline="true" :model="queryPageParams" class="demo-form-inline">
            <el-form-item label="景点名称">
                <el-input v-model="queryPageParams.spotName" placeholder="请输入景点名称" />
            </el-form-item>
            <el-form-item label="所在省份" style="width: 200px;">
                <el-select v-model="queryPageParams.province" placeholder="请选择省份">
                    <el-option label="所有" value="" />
                    <el-option label="北京" value="北京" />
                    <el-option label="天津" value="天津" />
                    <el-option label="河北" value="河北" />
                    <el-option label="山西" value="山西" />
                    <el-option label="内蒙古" value="内蒙古" />
                    <el-option label="辽宁" value="辽宁" />
                    <el-option label="吉林" value="吉林" />
                    <el-option label="黑龙江" value="黑龙江" />
                    <el-option label="上海" value="上海" />
                    <el-option label="江苏" value="江苏" />
                    <el-option label="浙江" value="浙江" />
                    <el-option label="安徽" value="安徽" />
                    <el-option label="福建" value="福建" />
                    <el-option label="江西" value="江西" />
                    <el-option label="山东" value="山东" />
                    <el-option label="河南" value="河南" />
                    <el-option label="湖北" value="湖北" />
                    <el-option label="湖南" value="湖南" />
                    <el-option label="广东" value="广东" />
                    <el-option label="广西" value="广西" />
                    <el-option label="海南" value="海南" />
                    <el-option label="重庆" value="重庆" />
                    <el-option label="四川" value="四川" />
                    <el-option label="贵州" value="贵州" />
                    <el-option label="云南" value="云南" />
                    <el-option label="西藏" value="西藏" />
                    <el-option label="陕西" value="陕西" />
                    <el-option label="甘肃" value="甘肃" />
                    <el-option label="青海" value="青海" />
                    <el-option label="宁夏" value="宁夏" />
                    <el-option label="新疆" value="新疆" />
                    <el-option label="台湾" value="台湾" />
                    <el-option label="香港" value="香港" />
                    <el-option label="澳门" value="澳门" />
                </el-select>
            </el-form-item>

            <el-form-item label="星级" style="width: 180px;">
                <el-select v-model="queryPageParams.starSelect" placeholder="请选择星级">
                    <el-option label="所有" value="" />
                    <el-option label="1星" value="1" />
                    <el-option label="2星" value="2" />
                    <el-option label="3星" value="3" />
                    <el-option label="4星" value="4" />
                    <el-option label="5星" value="5" />
                </el-select>
            </el-form-item>
            <el-form-item label="是否推荐" style="width: 220px;">
                <el-select v-model="queryPageParams.isFeatured" placeholder="请选择是否推荐">
                    <el-option label="所有" value="" />
                    <el-option label="是" value="1" />
                    <el-option label="否" value="0" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="queryPageAll">查询</el-button>
                <el-button @click="resetFilters">重置</el-button>
            </el-form-item>
        </el-form>

        <el-table :data="tableData" stripe style="width: 100%" @sort-change="sortchange">
            <el-table-column prop="spotName" label="景点名称" width="180" header-align="center" align="center" />
            <el-table-column prop="province" label="所在省份" width="160" header-align="center" align="center" />
            <el-table-column prop="stars" label="星级" width="150" header-align="center" align="center">
                <template v-slot="scope">
                    <el-rate v-model="scope.row.stars" :max="5" disabled />
                </template>
            </el-table-column>
            <el-table-column prop="featured" label="是否推荐" width="120" header-align="center" align="center">
                <template v-slot="scope">
                    <el-tag :type="scope.row.featured ? 'success' : 'info'">
                        {{ scope.row.featured ? '是' : '否' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="更新时间" sortable="custom" width="190" header-align="center"
                align="center" />
            <el-table-column fixed="right" label="操作" width="250" header-align="center" align="center">
                <template #default="scope">
                    <el-button color="#626aef" @click="handleDetailClick(scope.row.id)">查看编辑</el-button>
                    <el-popconfirm confirm-button-text="确定" cancel-button-text="取消" icon-color="#626AEF" title="是否确认删除?"
                        @confirm="handleDeleteConfirm(scope.row.id)" @cancel="handleDeleteCancel">

                        <template #reference>
                            <el-button type="danger" size="default">删除</el-button>
                        </template>
                    </el-popconfirm>

                </template>
            </el-table-column>
            <el-table-column fixed="right" label="推荐修改" min-width="130" header-align="center" align="center">
                <template #default="scope">
                    <el-popconfirm confirm-button-text="推荐" cancel-button-text="不推荐" icon-color="#FFDEAD"
                        title="是否推荐该攻略" @confirm="handleEditFeatured(scope.row.id, 1)"
                        @cancel="handleEditFeatured(scope.row.id, 0)" :popper-class="'custom-popconfirm'" width="250">
                        <template #reference>
                            <el-button size="default" style="background-color: #28a745; color: white;">推荐设置</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <br>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="queryPageParams.pageNo" :page-sizes="[10, 20, 30, 50]" :page-size="queryPageParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total" class="pagination-bar"/>
    </el-card>
</template>



<style scoped>
.pagination-bar {
    display: flex;
    justify-content: flex-end;
}
.demo-form-inline {
    margin-bottom: 20px;
}

.el-table .warning-row {
    background: #fdf5e6;
}

.el-table .success-row {
    background: #f0f9eb;
}
</style>
