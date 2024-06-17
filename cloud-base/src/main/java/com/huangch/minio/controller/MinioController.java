package com.huangch.minio.controller;

import com.huangch.minio.utils.MinioUtils;
import com.huangch.minio.utils.ServletUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author huangch
 * @since 2024-06-06
 */
@RestController
@RequestMapping("/minio")
public class MinioController {

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        ServletUtils.setResponseToDownload("demo.xlsx", response);
        MinioUtils.download("3829dc40-041a-42a6-b137-85689b572679.xlsx", response.getOutputStream());
    }
}
