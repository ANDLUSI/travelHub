<script setup>
import { ref, reactive, onMounted } from 'vue'
import { page } from '@/api/message' // 保留查询API函数
import { useUserStore } from '@/stores/user';

const store = useUserStore();
const announcements = ref([])
const total = ref(0)
const form = reactive({
    pageNo: 1,
    pageSize: 5,
})

onMounted(() => {
    queryPage()
})

const queryPage = () => {
    page({ userId: store.user.id, pageNo: form.pageNo, pageSize: form.pageSize }).then((res) => {
        console.log(`output->res`, res)
        announcements.value = res.data.records.map(record => {
            // 格式化 createTime，去除 'T'
            return {
                ...record,
                createTime: record.createTime.replace('T', ' ')
            }
        })
        total.value = res.data.total
    })
}

const handleSizeChange = (sizeValue) => {
    form.pageSize = sizeValue
    queryPage()
}

const handleCurrentChange = (currentValue) => {
    form.pageNo = currentValue
    queryPage()
}
</script>

<template>
    <div class="message-center">
        <div class="messages">
            <h2>公告</h2>
            <ul v-if="announcements.length">
                <li v-for="(announcement, index) in announcements" :key="index" class="announcement-item">
                    <div class="announcement-header">
                        <span class="announcement-title">{{ announcement.title }}</span>
                        <span class="announcement-time">{{ announcement.createTime }}</span>
                    </div>
                    <div class="announcement-content">
                        {{ announcement.content }}
                    </div>
                </li>
            </ul>
            <div v-else class="no-announcements">
                暂无公告
            </div>
        </div>
        <el-pagination v-model:current-page="form.pageNo" v-model:page-size="form.pageSize"
            :page-sizes="[5, 10, 20, 30]" size="default" background layout="total, sizes, prev, pager, next, jumper"
            :total="total" prev-text="上一页" next-text="下一页" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" class="pagination-bar" />
    </div>
</template>

<style scoped>
.message-center {
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
}

.messages {
    margin-bottom: 20px;
}

h2 {
    font-size: 1.5em;
    margin-bottom: 10px;
}

ul {
    list-style-type: none;
    padding: 0;
}

.announcement-item {
    margin-bottom: 10px;
    padding: 15px;
    border: 1px solid #eee;
    border-radius: 4px;
    background-color: #f9f9f9;
}

.announcement-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-weight: bold;
}

.announcement-title {
    font-size: 1.2em;
    color: #333;
}

.announcement-time {
    font-size: 0.9em;
    color: #999;
}

.announcement-content {
    font-size: 1em;
    color: #555;
}

.pagination-bar {
    display: flex;
    justify-content: flex-end;
}

.no-announcements {
    text-align: center;
    padding: 20px;
    color: #999;
    font-size: 1.2em;
}
</style>
