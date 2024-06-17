package com.huangch.cloud.utils.http;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求工具类
 *
 * @author huangch
 * @date 2023-09-19
 */
@SuppressWarnings({"ExtractMethodRecommender", "unchecked", "unused", "UnusedReturnValue"})
public class HttpUtils {

    /**
     * 发送GET请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @param clazz   返回值强转的类型
     * @param <T>     返回值强转的类型
     * @return Response Data
     * @throws IOException 连接异常
     */
    public static <T> T sendGet(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) throws IOException {
        InputStream inputStream = sendGet(url, params, headers);

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (clazz == String.class) {
            return (T) response.toString();
        } else {
            return JSON.parseObject(response.toString(), clazz);
        }
    }

    /**
     * 发送POST JSON请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @param clazz   返回值强转的类型
     * @param <T>     返回值强转的类型
     * @return Response Data
     * @throws IOException 连接异常
     */
    public static <T> T sendPost(String url, Map<String, String> params, Map<String, String> headers, Class<T> clazz) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.putIfAbsent("Content-Type", "application/json");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (params != null) {
            conn.getOutputStream().write(JSON.toJSONString(params).getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (clazz == String.class) {
            return (T) response.toString();
        } else {
            return JSON.parseObject(response.toString(), clazz);
        }
    }


    /**
     * 发送GET请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @return Response Data
     * @throws IOException 连接异常
     */
    public static InputStream sendGet(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        StringBuilder sb = new StringBuilder(url);
        if (params != null) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        URL obj = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn.getInputStream();

    }
}
