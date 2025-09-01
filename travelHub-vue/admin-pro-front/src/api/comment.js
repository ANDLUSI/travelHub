//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
//登录，data对象，传后端数据
const pageAll = (data) => {
    return request({
        url: '/api/comment/page',
        method: 'post',
        data
    })
}

const getSubCommentsById = (id) => {
    return request({
        url: `/api/comment/page/${id}`,
        method: 'post'
    })
}

const deleteCommentById = (id) => {
    return request({
        url: `/api/comment/delete/${id}`,
        method: 'get'
    })
}
export { pageAll, getSubCommentsById, deleteCommentById }