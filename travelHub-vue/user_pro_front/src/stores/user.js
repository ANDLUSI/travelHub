import { ref, computed, reactive } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore(
    //id
    'user',
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
        const user = reactive({
            id: '',
            username: '',
            realName: '',
            avatar: '',
            aminId: 0,
            role: 1,
            email: '',
            phone: '',
            location: '',
            status: 0,
            title: ''
        })

        const getUser = computed(() => user)

        //保存user信息
        const saveUser = (myUser) => {
            user.id = myUser.id
            user.username = myUser.username
            user.realName = myUser.realName
            user.avatar = myUser.imagePath
            user.userId = myUser.id
            user.role = myUser.role
            user.phone = myUser.phone
            user.email = myUser.email
            user.status = myUser.status
            user.title = myUser.title
            user.location = myUser.location
        }
        //要返回token，因为持久化token常量
        return { getToken, saveToken, token, user, saveUser, getUser }
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
                    key: 'user-info',
                    //存储方式
                    storage: localStorage,
                    //指定:state中哪些数据需要被持久化。[] 表示不持久化任何状态，undefined或null表示持久化整个 state
                    //paths: ['token']
                }
            ]
        }

    }
)
