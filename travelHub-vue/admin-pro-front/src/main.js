//import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
//导入ElementPlus组件
import ElementPlus from 'element-plus'
//引入ElementPlus的css
import 'element-plus/dist/index.css'
//引入图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//导入pinia持久化插件对象
import piniaPersist from 'pinia-plugin-persist'

import App from './App.vue'
import router from './router'

const app = createApp(App)

const pinia = createPinia()
//pinia使用持久化插件
pinia.use(piniaPersist)
//vue对象使用pinia
app.use(pinia)
app.use(router)
//使用ElementPlus组件
app.use(ElementPlus)

//全局注册图标库
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    // 注册图标库中的每一个图标组件
    app.component(key, component)
}

app.mount('#app')
