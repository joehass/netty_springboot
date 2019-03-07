package com.weaver.activeMq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @Author: 胡烨
 * @Date: 2019/1/25 17:51
 * @Version 1.0
 */
@Component
public class QueueListener {

    @JmsListener(destination = "11", containerFactory = "jmsListenerContainerQueue")
    @SendTo("out.queue")
    public String receive(String text){
        System.out.println("QueueListener: consumer-a 收到一条信息: " + text);
        return "consumer-a received : " + text;
    }

}
