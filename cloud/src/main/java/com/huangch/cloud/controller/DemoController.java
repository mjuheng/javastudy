package com.huangch.cloud.controller;

import com.huangch.cloud.utils.file.ZipUtils;
import com.huangch.cloud.utils.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
public class DemoController {


    @GetMapping("/echo")
    public void echo(HttpServletResponse response) throws Exception {

        Map<String, List<ZipUtils.FileUrl>> fileUrlMap = new HashMap<>();
        // fileUrlMap.put("folder1", Lists.newArrayList(new ZipUtils.FileUrl("http://localhost:8082/minio/download", "demo.xlsx"), new ZipUtils.FileUrl("http://localhost:8082/minio/download", "demo.xlsx")));
        // fileUrlMap.put("folder2", Lists.newArrayList(new ZipUtils.FileUrl("http://localhost:8082/minio/download", "demo.xlsx")));

        ServletUtils.setResponseToDownload("a.zip", response);
        ZipUtils.toZipByUrl(fileUrlMap, response.getOutputStream());

    }



}
