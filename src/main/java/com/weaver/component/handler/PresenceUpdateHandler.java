package com.weaver.component.handler;

/**
 * @Author: 胡烨
 * @Date: 2019/1/12 14:27
 * @Version 1.0
 */

import com.weaver.component.roster.Roster;
import com.weaver.component.roster.RosterManager;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.Presence;
import com.weaver.netty.stanza.Stanza;
import com.weaver.session.LocalClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新出席信息和名册信息
 * 该handler 必须发现 presence 类型，更新用户名册和通知 presence 订阅者 对于session状态的改变
 *
 * 下面是4种 presence 的基础类型的改变
 *
 * 1、简单的状态更新-发往服务器，这些更新由服务器正确的发给对应的地址，并分发给所有名册上的订阅者，用 null类型，空类型，或者
 * "unavaliable" 类型来表示
 *
 * 2、定向状态更细，针对特定的实体这些状态更新被正确处理并直接发送给实体，而不向名册订阅者广播
 *
 * 3、订阅请求：这个更新请求表示订阅者的状态变化，这种请求经常会影响名册。服务器必须用适当的订阅信息来更新名册
 * 并且将名册的改变推送给正确参与的一方
 * 类型包括："subscribe", "subscribed", "unsubscribed" 和 "unsubscribe".
 *
 * 4、服务器探针：为服务器提供一种机制，用于查询其他服务器上用户的状态。这允许用户在联机时立即知道用户的状态，
 * 而不是从其他服务器广播状态更新或在收到状态更新时跟踪它们。
 */
@Component
public class PresenceUpdateHandler {

    @Autowired
    SessionManager sessionManager;

    @Autowired
    private RosterManager rosterManager;

    /**
     * 影响名册订阅的更新
     * @param packet
     */
    public void process(Stanza packet){
        process((Presence) packet,sessionManager.getSession(packet.getFrom()));
    }

    private void process(Presence presence, LocalClientSession session){
        Presence.Type type = presence.getType();

        if (type == null){
            broadcastUpdate(presence.createCopy(),session);
            if (session != null){
                session.setPresence1(presence);
            }
        }
    }

    /**
     * 广播给定的更新给所有订阅者，我们需要去查询所有的订阅者通过名册表，并发送更新给订阅者
     * @param update
     */
    private void broadcastUpdate(Presence update,LocalClientSession session) {
        if (update.getFrom() == null){
            return;
        }

        //本地更新可以通过本地的名册进行
        String name = update.getFrom().getNode();
        if (name != null && !"".equals(name)){
            Roster roster = rosterManager.getRoster(name);
            roster.broadcastPresence(update);
        }
    }
}
