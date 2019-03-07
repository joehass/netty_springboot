package com.weaver.OfflineMessage;

/**
 * @Author: 胡烨
 * @Date: 2019/1/28 14:18
 * @Version 1.0
 */

import com.weaver.netty.stanza.Message;

/**
 * 离线消息监听器
 */
public interface OfflineMessageListener {

    /**
     * 存储广播消息
     * @param message
     */
    void messageBounced(Message message);

    /**
     * 存储离线消息
     * @param message
     */
    void messageStored(Message message);
}
