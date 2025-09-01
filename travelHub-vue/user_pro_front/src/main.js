//import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
//导入ElementPlus组件
import ElementPlus from 'element-plus'
import  ElLoading  from 'element-plus'
//引入ElementPlus的css
import 'element-plus/dist/index.css'
//引入图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//导入pinia持久化插件对象
import piniaPersist from 'pinia-plugin-persist'

import App from './App.vue'
import router from './router'

import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css'; // Quill 的样式
import BaiduMap from 'vue-baidu-map-3x'



const app = createApp(App)

const pinia = createPinia()
//pinia使用持久化插件
pinia.use(piniaPersist)
//vue对象使用pinia
app.use(pinia)
app.use(router)
app.component('QuillEditor', QuillEditor);
//使用ElementPlus组件
app.use(ElementPlus)
app.use(ElLoading)

//全局注册图标库
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    // 注册图标库中的每一个图标组件
    app.component(key, component)
}
app.use(BaiduMap,{
    ak:'9eOjEj6fWuHshpdGqVjO0lVesYdIsgab'
})
app.mount('#app')
