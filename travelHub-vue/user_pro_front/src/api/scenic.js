//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"

const findByName = (data) => {
    return request({
        url: '/api/scenic/search',
        method: 'post',
        data
    })
}
const findById = (id) => {
    return request({
        url: `/api/scenic/detail/${id}`,
        method: 'get'
    })
}
const selectHotGuide =() => {
    return request({
        url: '/api/guide/hot',
        method: 'get'
    })
}

const selectHotScenic =() => {
    return request({
        url: '/api/scenic/hot',
        method: 'get'
    })
}

const selectNearScenic = (data) => {
    return request({
        url: '/api/scenic/near',
        method: 'post',
        data
    })
}
const selectFilteredScenic = (data) => {
    return request({
        url: '/api/scenic/select',
        method: 'post',
        data
    })
}
export { findByName, selectHotGuide, selectHotScenic, selectNearScenic, findById, selectFilteredScenic }