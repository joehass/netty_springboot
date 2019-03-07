package com.weaver.route;

import com.weaver.netty.stanza.IQ;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.xml.XMLElement;
import com.weaver.plugin.handler.IQHandler;
import com.weaver.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IQRoute {

    @Autowired
    private RedisUtil redisUtil;

    public void route(IQ packet){
        if (packet == null){
        }

        JID sender = packet.getFrom();
        String query = packet.getQuery();
        XMLElement xml = packet.getXML();
        String namespace = xml.getNamespace();

        IQHandler handler = getHandler(namespace);

        handler.process(packet);
    }

    public IQHandler getHandler(String namespace){
        IQHandler iqHandler = redisUtil.getIQHandler(namespace);

        return iqHandler;
    }

}
