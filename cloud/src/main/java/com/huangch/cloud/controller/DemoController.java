package com.huangch.cloud.controller;

import com.huangch.cloud.pojo.User;
import com.huangch.cloud.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
public class DemoController {

    @Resource
    private IUserService userService;

    @GetMapping("/echo")
    public void echo() {
        userService.echo();
    }

    @PostMapping(value = "/form/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User submit(User user) {
        return user;
    }



}
