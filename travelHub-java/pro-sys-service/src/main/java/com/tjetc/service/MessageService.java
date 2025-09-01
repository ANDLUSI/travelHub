package com.tjetc.service;

import com.tjetc.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
    JsonResult findById(Long id);
    JsonResult fadeById(Long id);

    JsonResult page(Long userId,Integer pageNo, Integer pageSize);
}
