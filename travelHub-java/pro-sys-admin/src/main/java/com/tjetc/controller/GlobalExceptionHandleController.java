package com.tjetc.controller;

import com.tjetc.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
//@RestControllerAdvice=@ControllerAdvice +@Response
//全局异常处理
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandleController {
    @ExceptionHandler({Exception.class})
    public JsonResult exception(Exception e) {
        e.printStackTrace();
        log.error(e.toString());
        return JsonResult.fail("服务端异常，请联系管理员检查服务端");
    }

}
