//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
const page = (data) => {
    return request({
        url: '/api/guide/page',
        method: 'post',
        data
    })
}

const pageTime = (data) => {
    return request({
        url: '/api/guide/pageTime',
        method: 'post',
        data
    })
}

const pageAll = (data) => {
    return request({
        url: '/api/guide/pageAll',
        method: 'post',
        data
    })
}

const deleteById = (id) => {
    return request({
        url: `/api/guide/delete/${id}`,
        method: 'get'
    })
}

const findById = (id) => {
    return request({
        url: `/api/guide/detail/${id}`,
        method: 'get'
    })
}
const saveguide = (data) => {
    return request({
        url: `/api/guide/saveguide`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}
const editStatus = (id, type) => {
    return request({
        url: `/api/guide/editStatus/${id}/${type}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}
const editFeatured = (id, type) => {
    return request({
        url: `/api/guide/editFeatured/${id}/${type}`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' }
    })
}
export { page, pageTime, pageAll, deleteById, findById, saveguide,editFeatured, editStatus }