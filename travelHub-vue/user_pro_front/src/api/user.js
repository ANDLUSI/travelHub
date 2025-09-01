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

const findById = (id) => {
    return request({
        url: `/api/user/detail/${id}`,
        method: 'get'
    })
}



const saveUser = (data) => {
    return request({
        url: `/api/user/saveUser`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}


const saveTime = (id) => {
    return request({
        url: `/api/user/updateTime/${id}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}

const editRole = (id, type) => {
    return request({
        url: `/api/admin/editRole/${id}/${type}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}


export { login, findById, saveTime, editRole, saveUser}