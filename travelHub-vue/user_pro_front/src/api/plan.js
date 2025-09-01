//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"

const addPlan = (data) => {
    return request({
        url: `/api/plan/add`,
        method: 'post',
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}
const deletePlan = (id) => {
    return request({
        url: `/api/plan/delete/${id}`,
        method: 'get'
    })
}
const findPlan = (userId) => {
    return request({
        url: `/api/plan/find/${userId}`,
        method: 'get'
    })
}
export { addPlan, deletePlan, findPlan}