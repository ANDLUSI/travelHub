//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"

const selectComment = (id) => {
    return request({
        url: `/api/comment/guideC/${id}`,
        method: 'get'
    })
}
const likeComment = (userId,commentId) => {
    return request({
        url: `/api/comment/like/${userId}/${commentId}`,
        method: 'get'
    })
}
const saveComment = (data) => {
    return request({
        url: `/api/comment/save`,
        method: 'post',
        //表示请求的数据格式为json
        // headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}
const deleteComment = (id) => {
    return request({
        url: `/api/comment/delete/${id}`,
        method: 'get'
    })
}
export { selectComment, likeComment, saveComment, deleteComment }