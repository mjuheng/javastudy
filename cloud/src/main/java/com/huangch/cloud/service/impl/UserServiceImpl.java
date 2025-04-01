package com.huangch.cloud.service.impl;

import com.huangch.cloud.pojo.User;
import com.huangch.cloud.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangch
 * @since 2023-11-10
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {


    @Override
    public void echo() {
        List<User> users = new ArrayList<User>();
        users.add(new User());
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
        System.out.println("hello world 111");
    }
}
