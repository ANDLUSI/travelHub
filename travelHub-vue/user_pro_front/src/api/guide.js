//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"

const pageSelect = (data) => {
    return request({
        url: '/api/guide/pageSelect',
        method: 'post',
        data
    })
}
const findUserGuide = (id) => {
    return request({
        url: `/api/guide/UGdetail/${id}`,
        method: 'get'
    })
}
const likeGuide = (userId, guideId) => {
    return request({
        url: `/api/guide/like/${userId}/${guideId}`,
        method: 'get'
    })
}
const saveTravel = (data) => {
    return request({
        url: `/api/guide/saveTravel`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}

const page = (data) => {
    return request({
        url: '/api/guide/page',
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
const addView = (userId, guideId) => {
    return request({
        url: `/api/guide/view/${userId}/${guideId}`,
        method: 'get'
    })
}
export { pageSelect, findUserGuide, likeGuide, saveTravel, page, deleteById, findById, addView, }