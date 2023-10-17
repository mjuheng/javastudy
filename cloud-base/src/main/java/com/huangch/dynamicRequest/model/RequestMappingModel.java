package com.huangch.dynamicRequest.model;

import lombok.Data;

/**
 * @author huangch
 * @date 2023-10-08
 */
@Data
public class RequestMappingModel {

    private String path;

    private String beanName;

    private String method;
}
