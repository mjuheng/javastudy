package com.huangch.cloud.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
@RequiredArgsConstructor
public class DemoController {


    @PostMapping("/demo")
    public void demo(@RequestParam("file") MultipartFile file) throws Exception {

    }



}
