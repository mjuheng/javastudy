package com.huangch.cloud.utils.http;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author huangch
 * @since 2024-12-05
 */
@SuppressWarnings("unchecked")
@Slf4j
public class OkHttpUtils {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static <T> T get(String url, Map<String, Object> params, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder(url);

        if (params != null && !params.isEmpty()) {
            sb.append("?");

            for (Map.Entry<String, Object> entry : params.entrySet()) {

                if (ObjectUtil.isEmpty(entry.getValue())) {
                    continue;
                }

                String key = entry.getKey();

                if (entry.getValue() instanceof List) {
                    List<?> list = (List<?>) entry.getValue();

                    for (Object item : list) {
                        if (item != null) {
                            sb.append(URLEncoder.encode(key + "[]", StandardCharsets.UTF_8));
                            sb.append("=");
                            sb.append(URLEncoder.encode(String.valueOf(item), StandardCharsets.UTF_8));
                            sb.append("&");
                        }
                    }
                } else {
                    sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                    sb.append("=");
                    sb.append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8));
                    sb.append("&");
                }
            }

            if (sb.charAt(sb.length() - 1) == '&') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        Request request = new Request.Builder()
                .url(sb.toString())
                .build();

        logRequest(sb.toString(), params);

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, clazz);
        }
    }

    public static <T> T postJson(String url, Map<String, String> headers, Map<String, Object> params, Class<T> clazz) throws IOException {
        String json = JSON.toJSONString(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        Request request = requestBuilder
                .url(url)
                .post(body)
                .build();

        logRequest(url, params);

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, clazz);
        }
    }

    public static <T> T postFormData(String url, Map<String, String> params, Class<T> clazz) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody body = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        logRequest(url, params);

        try (Response response = client.newCall(request).execute()) {
            return handleResponse(response, clazz);
        }
    }

    private static <T> T handleResponse(Response response, Class<T> clazz) throws IOException {
        int statusCode = response.code();
        String responseBody = response.body() != null ? response.body().string() : null;

        logResponse(statusCode, responseBody);

        if (clazz == String.class) {
            return (T) responseBody;
        } else {
            return com.alibaba.fastjson2.JSON.parseObject(responseBody, clazz);
        }
    }

    private static void logRequest(String url, Object params) {
        log.info("----- HTTP Request -----");
        log.info("URL: {}", url);
        if (params != null) {
            log.info("Parameters: {}", params instanceof String ? params : com.alibaba.fastjson2.JSON.toJSONString(params));
        }
        log.info("------------------------");
    }

    private static void logResponse(int statusCode, String responseBody) {
        log.info("----- HTTP Response -----");
        log.info("Status Code: {}", statusCode);
        log.info("Response Body: {}", responseBody);
        log.info("-------------------------");
    }
}
