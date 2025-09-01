<script setup>
import { ref, reactive, onMounted } from 'vue'
import { page, deleteById, save, send } from '@/api/notice' // 保留sendNotice API函数
import { Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';

const announcements = ref([])

const dialogVisible = ref(false)
const contentDialogVisible = ref(false)
const dialogTitle = ref('')
const formLabelWidth = '120px'
const selectedContent = ref('')

const total = ref(0)

const form = reactive({
    id: null,
    title: '',
    content: '',
    create_time: '',
    update_time: '',
    pageNo: 1,
    pageSize: 10
})

onMounted(() => {
    queryPage()
})

const queryPage = () => {
    page(form).then((res) => {
        console.log(`output-->res`, res)
        announcements.value = res.data.records
        total.value = res.data.total
    })
}

const openCreateDialog = () => {
    dialogTitle.value = '创建公告'
    form.id = null
    form.title = ''
    form.content = ''
    dialogVisible.value = true
}

// 编辑
const openEditDialog = (announcement) => {
    dialogTitle.value = '编辑公告'
    form.id = announcement.id;
    form.title = announcement.title;
    form.content = announcement.content;
    form.create_time = announcement.createTime;
    form.update_time = announcement.updateTime;
    dialogVisible.value = true;
}

// 删除
const deleteAnnouncement = (id) => {
    ElMessageBox.confirm('确定要删除此公告吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteById(id).then((res) => {
            ElMessage({
                message: '公告删除成功！',
                type: 'success',
            })
            queryPage()
        }).catch((error) => {
            ElMessage({
                message: `删除失败：${error.message}`,
                type: 'error',
            })
        })
    }).catch(() => {
        ElMessage({
            message: '删除已取消',
            type: 'info',
        })
    })
}

const formEl = ref();

const saveAnnouncement = (formEl) => {
    if (!formEl) return;
    formEl.validate((valid, fields) => {
        if (valid) {
            save(form).then((res) => {
                ElMessage({
                    message: res.message,
                    type: 'success'
                })
                window.location.reload();
            })
        } else {
            console.log('error submit!', fields)
        }
    })
    dialogVisible.value = false
}

const viewContent = (announcement) => {
    selectedContent.value = announcement.content
    contentDialogVisible.value = true
}

const handleSizeChange = (sizeValue) => {
    form.pageSize = sizeValue
    queryPage()
}

const handleCurrentChange = (currentValue) => {
    form.pageNo = currentValue
    queryPage()
}

const formatDateTime = (dateTime) => {
    return new Date(dateTime).toLocaleString();
}

// 发布公告，添加确认框
const sendM = (announcement) => {
    ElMessageBox.confirm('确定要发布此公告吗？', '发布确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        send(announcement.id).then((res) => {
            ElMessage({
                message: '公告发布成功！',
                type: 'success',
            })
            queryPage();
        }).catch((error) => {
            ElMessage({
                message: `发布失败：${error.message}`,
                type: 'error',
            })
        })
    }).catch(() => {
        ElMessage({
            message: '发布已取消',
            type: 'info',
        })
    })
}
</script>

<template>
    <div style="padding: 20px;">
        <el-row>
            <el-col :span="24">
                <el-card shadow="hover">
                    <div class="clearfix">
                        <span>公告管理</span>
                        <el-button type="primary" @click="openCreateDialog" style="float: right;">创建公告</el-button>
                    </div>
                    <el-table :data="announcements" style="width: 100%; margin-top: 20px;">
                        <el-table-column prop="title" label="公告标题" width="200" header-align="center" align="center" />
                        <el-table-column prop="createTime" label="创建时间" width="180" header-align="center"
                            align="center">
                            <template #default="scope">
                                {{ formatDateTime(scope.row.createTime) }}
                            </template>
                        </el-table-column>
                        <el-table-column fixed="right" label="操作" header-align="center" align="center">
                            <template v-slot="scope">
                                <el-button size="default" type="primary"
                                    @click="viewContent(scope.row)">查看内容</el-button>
                                <el-button size="default" :icon="Edit" @click="openEditDialog(scope.row)">编辑</el-button>
                                <el-button size="default" type="danger"
                                    @click="deleteAnnouncement(scope.row.id)">删除</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column fixed="right" label="发布" width="180" header-align="center" align="center">
                            <template v-slot="scope">
                                <el-button size="default" :icon="Edit" @click="sendM(scope.row)">发布</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>

        <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
            <el-form :model="form" ref="formEl">
                <el-form-item label="公告标题" :label-width="formLabelWidth">
                    <el-input v-model="form.title" autocomplete="off" placeholder="请输入公告标题" />
                </el-form-item>
                <el-form-item label="公告内容" :label-width="formLabelWidth">
                    <el-input type="textarea" v-model="form.content" rows="6" placeholder="请输入公告内容" />
                </el-form-item>
            </el-form>
            <template v-slot:footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveAnnouncement(formEl)">保存</el-button>
            </template>
        </el-dialog>

        <el-dialog title="公告内容" v-model="contentDialogVisible" width="600px" center>
            <div style="white-space: pre-wrap; padding: 10px; background-color: #f9f9f9; border-radius: 5px;">
                <p style="margin-top: 10px;">{{ selectedContent }}</p>
            </div>
            <template v-slot:footer>
                <el-button @click="contentDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
    <br>
    <el-pagination v-model:current-page="form.pageNo" v-model:page-size="form.pageSize" :page-sizes="[10, 20, 30, 40]"
        size="default" background layout="total, sizes, prev, pager, next, jumper" :total="total" prev-text="上一页"
        next-text="下一页" @size-change="handleSizeChange" @current-change="handleCurrentChange" class="pagination-bar" />
</template>

<style scoped>
.el-card {
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
}

.el-dialog__body {
    padding: 20px;
}

.el-empty {
    margin: 20px 0;
    text-align: center;
}

.pagination-bar {
    display: flex;
    justify-content: flex-end;
}
</style>
