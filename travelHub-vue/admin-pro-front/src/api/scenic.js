//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
const page = (data) => {
    return request({
        url: '/api/scenic/page',
        method: 'post',
        data
    })
}

const pageTime = (data) => {
    return request({
        url: '/api/scenic/pageTime',
        method: 'post',
        data
    })
}

const pageAll = (data) => {
    return request({
        url: '/api/scenic/pageAll',
        method: 'post',
        data
    })
}

const deleteById = (id) => {
    return request({
        url: `/api/scenic/delete/${id}`,
        method: 'get'
    })
}

const findById = (id) => {
    return request({
        url: `/api/scenic/detail/${id}`,
        method: 'get'
    })
}
const saveScenic = (data) => {
    return request({
        url: `/api/scenic/saveScenic`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}
const editFeatured = (id, type) => {
    return request({
        url: `/api/scenic/editFeatured/${id}/${type}`,
        method: 'post'
    })
}

export { page, pageTime, pageAll, deleteById, findById, saveScenic, editFeatured}