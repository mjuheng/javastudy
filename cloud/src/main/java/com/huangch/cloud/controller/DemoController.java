package com.huangch.cloud.controller;

import cn.hutool.core.io.IoUtil;
import com.huangch.cloud.pojo.User;
import com.huangch.cloud.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

    @PostMapping(value = "/form/submit")
    public void submit(User user, HttpServletResponse response) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream("hello world".getBytes());
        IoUtil.copy(bais, response.getOutputStream());
    }



}
