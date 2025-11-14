package com.huangch.cloud.controller;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.huangch.cloud.pojo.User;
import com.huangch.cloud.utils.excel.easyexcel.listener.EasyExcelListener;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author huangch
 * @date 2023-09-13
 */
@RestController
@RequiredArgsConstructor
public class DemoController {


    @PostMapping("/demo")
    public void demo(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Set<String>> fieldDictMap = new HashMap<>();
        fieldDictMap.put(LambdaUtil.getFieldName(User::getGender), Set.of("男", "女"));

        EasyExcelListener<User> listener = new EasyExcelListener<>(fieldDictMap);
        EasyExcel.read(file.getInputStream(), User.class, listener).sheet(0).doRead();

        System.out.println(JSONUtil.toJsonStr(listener.getDataList()));
    }



}
