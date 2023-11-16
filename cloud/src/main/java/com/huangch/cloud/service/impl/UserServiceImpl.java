package com.huangch.cloud.service.impl;

import com.huangch.cloud.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * @author huangch
 * @since 2023-11-10
 */
@Service
public class UserServiceImpl implements IUserService {

    @Override
    public void echo() {
        System.out.println("default user");
    }
}
