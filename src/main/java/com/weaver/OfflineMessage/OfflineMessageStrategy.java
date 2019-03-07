package com.weaver.OfflineMessage;

/**
 * @Author: 胡烨
 * @Date: 2019/1/28 14:16
 * @Version 1.0
 */

import com.weaver.netty.stanza.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 离线消息存储策略类
 */
@Component
public class OfflineMessageStrategy {

    private static List<OfflineMessageListener> listeners = new CopyOnWriteArrayList<>();

    @Autowired
    private OfflineMessageStore messageStore;

    public void storeOffline(Message message){
        messageStore.addMessage(message);

        if (!listeners.isEmpty()){
            for (OfflineMessageListener listener:listeners){
                listener.messageStored(message);
            }
        }
    }

    public void addListener(OfflineMessageListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        listeners.add(listener);
    }
}
