package com.huangch.cloud.controller;

import com.huangch.cloud.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
@RequiredArgsConstructor
public class DemoController {

    private final IUserService userService;

    @GetMapping("/echo")
    public void echo(HttpServletResponse response) throws Exception {
        userService.echo();
    }



}
