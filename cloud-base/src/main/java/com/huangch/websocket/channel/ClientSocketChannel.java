package com.huangch.websocket.channel;

import com.huangch.websocket.config.WebSocketCenter;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;


/**
 * @author huangch
 * @date 2023-10-24
 */
@Component
@Slf4j
@ServerEndpoint(value = "/channel/echo")
public class ClientSocketChannel {

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("[websocket] 收到消息：id={}, message={}", session.getId(), message);
        session.getAsyncRemote().sendText(message);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ConcurrentMap<String, Session> sessionPool = WebSocketCenter.SESSION_POOL;
        sessionPool.put(session.getId(), session);
        log.info("[websocket] 新的连接：id={}", session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        WebSocketCenter.SESSION_POOL.remove(session.getId());
        log.info("[websocket] 连接断开：id={}, reason={}", session.getId(), closeReason);
    }

    @OnError
    public void onError(Throwable throwable) {

        log.info("[websocket] 连接异常：throwable={}", throwable.getMessage());
    }
}
