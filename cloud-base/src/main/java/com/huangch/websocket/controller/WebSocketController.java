package com.huangch.websocket.controller;

import com.huangch.websocket.config.SpringWebSocketHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;

/**
 * @author huangch
 * @date 2023-10-08
 */
@Controller
@RequestMapping("/websocket")
public class WebSocketController {


    @Resource
    private SpringWebSocketHandler springWebSocketHandler;

    /**
     * 登录将username放入session中，然后在拦截器HandshakeInterceptor中取出
     */
    @ResponseBody
    @RequestMapping("/login")
    public String login(HttpServletRequest request, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        System.out.println("登录：" + username + "：" + password);
        HttpSession session = request.getSession();
        if (null != session) {
            session.setAttribute("SESSION_USERNAME", username);
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * 指定发送
     */
    @ResponseBody
    @RequestMapping("/sendToUser")
    public String send(@RequestParam(value = "username") String username, @RequestParam(value = "info") String info) {
        springWebSocketHandler.sendMessageToUser(username, new TextMessage(info));
        System.out.println("发送至：" + username);
        return "success";
    }

    /**
     * 广播
     */
    @ResponseBody
    @RequestMapping("/broadcast")
    public String broadcast(@RequestParam(value = "info") String info) {
        springWebSocketHandler.sendMessageToUsers(new TextMessage("广播消息：" + info));
        System.out.println("广播成功");
        return "success";
    }
}
