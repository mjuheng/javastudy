package com.huangch.cloud.utils.event;

import com.huangch.cloud.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author huangch
 * @since 2024-02-26
 */
@Slf4j
@Component
public class BaseEventListener {

    @EventListener
    public void handleBase(BaseEvent<User> baseEvent) {
        User data = baseEvent.getData();
        log.info("监听到base事件.......");
    }
}
