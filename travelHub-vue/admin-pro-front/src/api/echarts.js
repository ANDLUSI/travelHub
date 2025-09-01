import request from "@/utils/request"

const findByNewUser = () => {
    return request({
        url: `/api/echarts/user`,
        method: 'get'
    })
}
const findByHotGuide = () => {
    return request({
        url: `/api/echarts/guide`,
        method: 'get'
    })
}
const findByHotTag = () => {
    return request({
        url: `/api/echarts/tag`,
        method: 'get'
    })
}
const findUser = () => {
    return request({
        url: `/api/user/findUser`,
        method: 'get',
    })
}
const findComment = () => {
    return request({
        url: `/api/comment/findComment`,
        method: 'get',
    })
}
const findGuide = () => {
    return request({
        url: `/api/guide/findGuide`,
        method: 'get',
    })
}
export { findByNewUser, findByHotGuide, findByHotTag, findUser,findComment, findGuide}
