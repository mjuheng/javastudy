package com.huangch.cloud.controller;

import com.huangch.cloud.utils.id.IdWorker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
public class DemoController {

    @GetMapping("/idWork")
    public String idWork() {
        IdWorker idWorker = new IdWorker(1L);
        return String.valueOf(idWorker.nextId());
    }
}
