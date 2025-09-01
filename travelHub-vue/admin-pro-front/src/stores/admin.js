import { ref, computed, reactive } from 'vue'
import { defineStore } from 'pinia'

export const useAdminStore = defineStore(
    //id
    'admin',
    //定义初始化
    () => {
        //定义token
        const token = ref('')


        //获取token
        const getToken = computed(() => token.value)
        //保存token
        const saveToken = (t) => {
            token.value = t
        }
        // 定义admin信息
        const admin = reactive({
            username: '',
            realName: '',
            imagePath: '',
            role: 1,
            id: 0,
            phone: '',
            email: '',
            createTime: '',
            updateTime: '',
            lastlogTime: ''
        })

        //保存admin信息
        const saveAdmin1 = (myAdmin) => {
            admin.username = myAdmin.username
            admin.realName = myAdmin.realName
            admin.imagePath = myAdmin.imagePath
            admin.id = myAdmin.id
            admin.role = myAdmin.role
            admin.phone = myAdmin.phone
            admin.email = myAdmin.email
            admin.createTime = myAdmin.createTime
            admin.updateTime = myAdmin.updateTime
            admin.lastlogTime = myAdmin.lastlogTime
        }
        //要返回token，因为持久化token常量
        return { getToken, saveToken, token, admin, saveAdmin1 }
    },
    //持久化
    {
        persist: {
            //开启持久化
            enabled: true,
            //策略
            strategies: [
                {
                    //持久化key名称
                    key: 'admin-info',
                    //存储方式
                    storage: localStorage,
                    //指定:state中哪些数据需要被持久化。[] 表示不持久化任何状态，undefined或null表示持久化整个 state
                    //paths: ['token']
                }
            ]
        }

    }
)
