package com.weaver.activeMq.configuration;

import javax.jms.*;

/**
 * @Author: 胡烨
 * @Date: 2019/1/30 10:20
 * @Version 1.0
 */
public class QueueListener1 implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            Destination destination=message.getJMSDestination();
            String topci=destination.toString();
            System.out.println("topc---44444------"+topci);
            TextMessage textMessage = (TextMessage) message;
            System.out.println("=======商品添加成功,从消息队列中读取同步索引请求================"+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
