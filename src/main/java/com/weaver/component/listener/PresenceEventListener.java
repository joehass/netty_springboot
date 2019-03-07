package com.weaver.component.listener;

/**
 * @Author: 胡烨
 * @Date: 2019/1/11 9:19
 * @Version 1.0
 */

import com.weaver.netty.stanza.Presence;
import com.weaver.session.LocalClientSession;

/**
 * presence事件监听器
 */
public interface PresenceEventListener{

    /**
     * 用户上线处理
     * @param session
     * @param presence
     */
    public void availableSession(LocalClientSession session, Presence presence);
}
