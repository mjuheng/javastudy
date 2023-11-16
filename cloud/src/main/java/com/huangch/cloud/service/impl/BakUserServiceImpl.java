package com.huangch.cloud.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @author huangch
 * @since 2023-11-10
 */
@Primary
@Service
public class BakUserServiceImpl extends UserServiceImpl{

    @Override
    public void echo() {
        System.out.println("bak user");
    }
}
