package com.tjetc.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjetc.common.JsonResult;
import com.tjetc.common.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper objectMapper;
    //执行时期：在执行Controller方法之前执行preHandler所以可以校验用户是否登录，已经登录过了，放行，未登录就返回未登录，阻止访问controller的方法
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        //获取session
        //HttpSession session = request.getSession();
        //Object username = session.getAttribute("username");
        //if (username == null) {
        //    //未登录，返回json提示未登录
        //    String result = "{\"code\":2000,\"msg\":\"未登录或者登录已过期，请重新登录\"}";
        //    //解决中文乱码
        //    response.setHeader("Content-Type","application/json;charset=utf-8");
        //    //把信息输出到页面
        //    response.getWriter().write(result);
        //    //禁止执行后面controller方法
        //    return false;
        //}

        //校验token
        //访问token（请求头中获取token，定义key是token，value是token的值）
        String tokenValue = request.getHeader("token");
        if(tokenValue!=null&&!"".equals(tokenValue.trim())) {
            try {
                JwtTokenUtil.parseJwt(tokenValue);
                //放行
                return true;
            } catch (Exception e) {
                log.error("解析token异常，{}", e.getMessage());
            }
        }
        //解决中文乱码
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        //返回json 未登录或者登录过期
        JsonResult result = JsonResult.fail(-1,"未登录或登录状态已经过期，请重新登录");
        //使用jackson的0bjectMapper对象把JsonResult对象转换成json字符串
        String json = objectMapper.writeValueAsString(result);
        //把json字符串输出到前端
        response.getWriter().write(json);
        return false;
    }
}
