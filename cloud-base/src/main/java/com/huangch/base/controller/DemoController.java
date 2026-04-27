package com.huangch.base.controller;

import com.zyaud.fzhx.core.model.StringIdParam;
import com.zyaud.ns.base.client.NsFileClient;
import feign.Response;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author huangch
 * @since 2025-12-23
 */
@RequestMapping("/demo")
@RestController
public class DemoController {

    @Resource
    private NsFileClient nsFileClient;

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {

        StringIdParam stringIdParam = new StringIdParam();
        stringIdParam.setId("2001922760904757248");
        Response feignResp = nsFileClient.downloadOneFile(stringIdParam);


        // 2. 透传 Content-Type
        Collection<String> contentType = feignResp.headers().get("Content-Type");
        if (contentType != null && !contentType.isEmpty()) {
            response.setContentType(contentType.iterator().next());
        }

        // 3. ⭐ 关键：透传 Content-Disposition（文件名就在这里）
        Collection<String> disposition =
                feignResp.headers().get("Content-Disposition");

        if (disposition != null && !disposition.isEmpty()) {
            response.setHeader(
                    "Content-Disposition",
                    disposition.iterator().next()
            );
        }

        // 4. 输出文件流
        try (InputStream in = feignResp.body().asInputStream();
             OutputStream out = response.getOutputStream()) {

            IOUtils.copy(in, out);
        }
    }
}
