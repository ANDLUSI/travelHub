//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
const page = (data) => {
    return request({
        url: '/api/notice/page',
        method: 'post',
        data
    })
}

const deleteById = (id) => {
    return request({
        url: `/api/notice/delete/${id}`,
        method: 'get'
    })
}

const editById = (id) => {
    return request({
        url: `/api/notice/edit/${id}`,
        method: 'get'
    })
}

const save = (data) => {
    return request({
        url: `/api/notice/save`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}
const send = (id) => {
    return request({
        url: `/api/notice/send/${id}`,
        method: 'post'
    })
}

export { page, deleteById, editById,save,send }