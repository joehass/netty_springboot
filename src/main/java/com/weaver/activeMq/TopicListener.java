package com.weaver.activeMq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @Author: 胡烨
 *  @Date: 2019/1/25 17:52
 * @Version 1.0
 */
@Component
public class TopicListener {

    @JmsListener(destination = "publish.topic", containerFactory = "jmsListenerContainerTopic")
    public void receive(String text){
        System.out.println("TopicListener: consumer-a 收到一条信息: " + text);
    }
}
