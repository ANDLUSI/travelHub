//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
//分页获取user数据
const page = (data) => {
    return request({
        url: '/api/user/page',
        method: 'post',
        data
    })
}
const pageStatus = (data) => {
    return request({
        url: '/api/user/pageStatus',
        method: 'post',
        data
    })
}
const editStatus = (id,type) => {
    return request({
        url: `/api/user/editStatus/${id}/${type}`,
        method: 'post'
    })
}
const findById = (id) => {
    return request({
        url: `/api/user/detail/${id}`,
        method: 'get'
    })
}
const deleteById = (id) => {
    return request({
        url: `/api/user/delete/${id}`,
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

export { page, editStatus, findById, deleteById,saveUser,pageStatus}