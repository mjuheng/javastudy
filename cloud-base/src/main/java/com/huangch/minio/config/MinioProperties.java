package com.huangch.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author huangch
 * @since 2024-06-06
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {

    /**
     * 连接地址
     */
    private String endpoint;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 桶名
     */
    private String bucketName;

}
