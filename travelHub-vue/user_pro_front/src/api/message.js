//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"

const findNum = (id) => {
    return request({
        url: `/api/message/${id}`,
        method: 'get'
    })
}

const fade = (id) => {
    return request({
        url: `/api/message/fade/${id}`,
        method: 'get'
    })
}
const page = (data) => {
    return request({
        url: '/api/message/page',
        method: 'post',
        data
    })
}
export { findNum,fade,page }