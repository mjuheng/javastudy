package com.huangch.cloud.controller;

import com.huangch.cloud.pojo.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
public class DemoController {

    @PostMapping("/idWork")
    public User idWork(@RequestBody User user) {
        return user;
    }
}
