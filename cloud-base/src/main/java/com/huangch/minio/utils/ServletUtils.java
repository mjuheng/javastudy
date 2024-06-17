package com.huangch.minio.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author huangch
 * @since 2024-06-06
 */
public class ServletUtils {

    /**
     * 设置response为文件流
     *
     * @param fileName 文件名
     * @param response response
     */
    @SneakyThrows
    public static void setResponseToDownload(String fileName, HttpServletResponse response) {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        fileName = fileName.replaceAll("\\+", "%20");;
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Transfer-Encoding", "chunked");
        response.setContentType("application/octet-stream");
        response.flushBuffer();
    }
}
