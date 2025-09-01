<template>
    <div>
        <el-row>
            <el-col :span="24">
                <el-button type="primary" @click="openCreateDialog">发送通知</el-button>
            </el-col>
        </el-row>
        <el-table :data="notifications" style="width: 100%; margin-top: 20px;">
            <el-table-column prop="user_name" label="用户名" width="180" />
            <el-table-column prop="title" label="通知标题" width="200" />
            <el-table-column prop="sent_at" label="发送时间" width="180" />
            <el-table-column prop="is_read" label="状态" width="100">
                <template v-slot="scope">
                    <el-tag :type="scope.row.is_read ? 'success' : 'warning'">
                        {{ scope.row.is_read ? '已读' : '未读' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template v-slot="scope">
                    <el-button size="small" type="success" @click="markAsRead(scope.row)">标记为已读</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="发送通知" :visible="dialogVisible">
            <el-form :model="form">
                <el-form-item label="用户" :label-width="formLabelWidth">
                    <el-select v-model="form.user_id" placeholder="选择用户">
                        <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="通知标题" :label-width="formLabelWidth">
                    <el-input v-model="form.title" autocomplete="off" />
                </el-form-item>
                <el-form-item label="通知内容" :label-width="formLabelWidth">
                    <el-input type="textarea" v-model="form.message" rows="4" />
                </el-form-item>
            </el-form>
            <template v-slot:footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="sendNotification">发送</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 假设这些数据从API获取
const notifications = ref([])

const dialogVisible = ref(false)
const formLabelWidth = '120px'

const form = reactive({
    user_id: null,
    title: '',
    message: ''
})

// 加载通知数据
onMounted(() => {
    fetchNotifications()
})

const fetchNotifications = () => {
    // 这里调用API获取数据，例如：
    // axios.get('/api/notifications').then(response => {
    //   notifications.value = response.data;
    // });
    notifications.value = [
        { id: 1, user_name: '用户1', title: '通知1', message: '内容1', is_read: false, sent_at: '2024-08-25' },
        { id: 2, user_name: '用户2', title: '通知2', message: '内容2', is_read: true, sent_at: '2024-08-24' }
    ]
}

const openCreateDialog = () => {
    form.user_id = null
    form.title = ''
    form.message = ''
    dialogVisible.value = true
}

const sendNotification = () => {
    // 发送通知API调用
    // axios.post('/api/notifications', form).then(() => {
    //   fetchNotifications();
    //   dialogVisible.value = false;
    //   ElMessage.success('通知已发送');
    // });
    console.log('发送通知', form)
    dialogVisible.value = false
}

const markAsRead = (notification) => {
    // 标记通知为已读API调用
    // axios.put(`/api/notifications/${notification.id}/read`).then(() => {
    //   fetchNotifications();
    //   ElMessage.success('通知已标记为已读');
    // });
    notification.is_read = true
}
</script>
