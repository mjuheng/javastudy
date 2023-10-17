package com.huangch.dynamicRequest.service;

import com.huangch.dynamicRequest.model.RequestMappingModel;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * @author huangch
 * @date 2023-10-08
 */
public interface DynamicRequestService {

    /**
     * 动态注册路径映射
     */
    RequestMappingInfo registerMapping(RequestMappingModel model);

    /**
     * 动态删除路径映射
     */
    RequestMappingInfo unregisterMapping(String path);
}
