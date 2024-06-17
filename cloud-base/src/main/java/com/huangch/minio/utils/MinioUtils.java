package com.huangch.minio.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.huangch.minio.config.MinioProperties;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author huangch
 * @since 2024-06-06
 */
@Slf4j
@SuppressWarnings({"unused", "AlibabaConstantFieldShouldBeUpperCase"})
public class MinioUtils {

    private final static MinioClient minioClient;
    private final static MinioProperties minioProperties;

    static {
        minioClient = SpringUtil.getBean(MinioClient.class);
        minioProperties = SpringUtil.getBean(MinioProperties.class);
    }

    /**
     * 上传文件
     *
     * @param bytes   文件数组
     * @param extName 文件后缀
     */
    public static String upload(byte[] bytes, String extName) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return upload(bais, extName);
    }

    public static String upload(InputStream inputStream, String extName) {
        String bucketName = minioProperties.getBucketName();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String fileName = UUID.randomUUID() + "." + extName;
            // Upload the file to the bucket
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            return minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("minio upload fail");
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 文件名
     * @param os         输出流
     */
    public static void download(String objectName, OutputStream os) {
        try {
            // 获取对象输入流
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );

            // 将输入流写入本地文件
            byte[] buffer = new byte[1024 * 4];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
            }

            // 关闭流
            inputStream.close();
            os.close();

        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("minio download fail");
        }
    }
}
