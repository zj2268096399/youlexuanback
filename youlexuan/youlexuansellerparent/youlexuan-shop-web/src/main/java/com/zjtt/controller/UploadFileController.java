package com.zjtt.controller;

import com.zjtt.util.FastDFSClient;
import com.zjtt.util.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class UploadFileController {

    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file){
        try {
            // 服务器的ip
            String  SERVER_IP = "http://192.168.139.130/";
            // 获取文件的后缀名
            String oldFileName = file.getOriginalFilename();  // 1.jgp
            String substring = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
            // 上传文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/fdfs.conf");
            String oldPath = fastDFSClient.uploadFile(file.getBytes(), substring);
            String path = SERVER_IP + oldPath;
            return  new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"上传失败");
        }
    }
}
