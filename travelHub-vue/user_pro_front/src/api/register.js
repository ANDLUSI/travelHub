//@ 表示 src路径
//导入axios对象
import request from "@/utils/request"
const save = (data) => {
    return request({
        url: `/api/register`,
        method: 'post',
        //表示请求的数据格式为json
        headers: { 'Content-Type': 'application/json;charset=utf-8' },
        data
    })
}

export { save }