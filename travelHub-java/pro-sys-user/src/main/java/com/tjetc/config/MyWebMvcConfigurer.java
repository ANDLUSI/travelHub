package com.tjetc.config;

import com.tjetc.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    String[] excludePaths = {
            "/login","/**/*.js","/image/**","/error","/register"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                //对所有的请求路径进行拦截，执行loginInterceptor的preHandle方法
                .addPathPatterns("/**")
                //排除掉哪些url不被拦截处理
                .excludePathPatterns(excludePaths);
    }
}
