//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
const login = (data) => {
    //这个requset就是axios对象，调用axios
    return request({
        //这里与proxy配置有关系
        // /api/login 代理成http://localhost:8081/login
        url: '/api/login',
        method: 'post',
        //ES6简化属性
        data
    })
}
//分页获取admin数据
const page = (data) => {
    return request({
        url: '/api/admin/page',
        method: 'post',
        data
    })
}

//分页获取admin数据
const pageRole = (data) => {
    return request({
        url: '/api/admin/pageRole',
        method: 'post',
        data
    })
}

const findById = (id) => {
    return request({
        url: `/api/admin/detail/${id}`,
        method: 'get'
    })
}

const deleteById = (id) => {
    return request({
        url: `/api/admin/delete/${id}`,
        method: 'get'
    })
}

//保存admin数据(用于新增和更新)数据格式json
const save = (data) => {
    return request({
        url: `/api/admin/save`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8'},
        data
    })
}

//保存admin数据(用于新增和更新)数据格式json
const saveAdmin = (data) => {
    return request({
        url: `/api/admin/saveAdmin`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}

const saveTime = (id) => {
    return request({
        url: `/api/admin/updateTime/${id}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}

const editRole = (id,type) => {
    return request({
        url: `/api/admin/editRole/${id}/${type}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}
export { login, page, findById, deleteById, save, saveTime, editRole, saveAdmin, pageRole }