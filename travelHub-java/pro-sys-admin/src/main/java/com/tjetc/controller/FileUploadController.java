package com.tjetc.controller;



import com.tjetc.common.JsonResult;
import com.tjetc.common.OSSUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {

    @PostMapping("/file/upload")
    public Object uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        try {
            String path = OSSUtil.upload(uploadFile.getOriginalFilename(), uploadFile.getInputStream());
            return JsonResult.success("2001",path);
        } catch (IOException e) {
            return JsonResult.fail("4001");
        }
    }

}
