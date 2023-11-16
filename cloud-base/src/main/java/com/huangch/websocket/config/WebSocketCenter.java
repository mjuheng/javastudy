package com.huangch.websocket.config;

import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author huangch
 * @since 2023-10-24
 */
public class WebSocketCenter {

    /**
     * 用来存放每个客户端对应的 WebSocketServer 对象
     */
    public static ConcurrentMap<String, Session> SESSION_POOL = new ConcurrentHashMap<>();
}
