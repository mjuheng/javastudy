package com.huangch.dynamicRequest.controller;

import com.huangch.dynamicRequest.model.RequestMappingModel;
import com.huangch.dynamicRequest.service.DynamicRequestService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.annotation.Resource;

/**
 * @author huangch
 * @date 2023-10-08
 */
@RestController
@RequestMapping("/dynamic/request")
public class DynamicRequestController {

    @Resource
    private DynamicRequestService dynamicRequestService;

    @PostMapping("/registerMapping")
    public String registerMapping(@RequestBody RequestMappingModel model) {
        dynamicRequestService.registerMapping(model);
        return "ok";
    }

    @GetMapping("/unregisterMapping")
    public RequestMappingInfo unregisterMapping(String path) {
        return dynamicRequestService.unregisterMapping(path);
    }
}
