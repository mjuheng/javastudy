package com.huangch.dynamicRequest.service.impl;

import com.huangch.dynamicRequest.model.RequestMappingModel;
import com.huangch.dynamicRequest.service.DynamicRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author huangch
 * @date 2023-10-08
 */
@Slf4j
@Service
public class DynamicRequestServiceImpl implements DynamicRequestService {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;


    @Override
    public RequestMappingInfo registerMapping(RequestMappingModel model) {
        //从请求参数中构建RequestMappingInfo对象；包括请求的资源路径，执行控制器类以及方法等
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(model.getPath()).build();
        //通过Spring IOC容器获取对应的bean实例
        Object bean = applicationContext.getBean(model.getBeanName());
        try {
            Method method = bean.getClass().getMethod(model.getMethod());
            //注册Mapping信息逻辑
            requestMappingHandlerMapping.registerMapping(mappingInfo, bean, bean.getClass().getDeclaredMethod(model.getMethod()));
        } catch (NoSuchMethodException e) {
            log.error("error", e);
        }
        return mappingInfo;
    }


    @Override
    public RequestMappingInfo unregisterMapping(String path) {
        //执行删除注册逻辑
        for (RequestMappingInfo requestMappingInfo : requestMappingHandlerMapping.getHandlerMethods().keySet()) {
            if (requestMappingInfo.getPathPatternsCondition() == null) {
                continue;
            }
            for (PathPattern pattern : requestMappingInfo.getPathPatternsCondition().getPatterns()) {
                if (pattern.getPatternString().equals(path)) {
                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    return requestMappingInfo;
                }
            }
        }
        return null;
    }
}
