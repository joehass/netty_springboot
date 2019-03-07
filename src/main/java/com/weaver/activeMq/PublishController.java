package com.weaver.activeMq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @Author: 胡烨
 * @Date: 2019/1/28 11:04
 * @Version 1.0
 */
@Controller
@RequestMapping("/publish")
public class PublishController {


    @Autowired
    private JmsMessagingTemplate jms;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    @RequestMapping("/queue")
    public String queue(){

        for (int i = 0; i < 10 ; i++){
            String queue1 = "11";
            jms.convertAndSend(queue1, "queue"+i);
        }

        return "queue 发送成功";
    }

    @JmsListener(destination = "out.queue")
    public void consumerMsg(String msg){
        System.out.println(msg);
    }

    @RequestMapping("/topic")
    public String topic(){

        for (int i = 0; i < 10 ; i++){
            jms.convertAndSend(topic, "topic"+i);
        }

        return "topic 发送成功";
    }
}
