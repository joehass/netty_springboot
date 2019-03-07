package com.weaver.manager.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Presence;
import com.weaver.session.LocalClientSession;
import com.weaver.util.StreamID;
import com.weaver.util.impl.BasicStreamIDFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 胡烨
 * @Date: 2018/12/29 14:05
 * @Version 1.0
 * 通过账号关联session
 */
@Component
public class SessionManager implements Serializable {


    @JsonIgnore
    private Channel channel;

    private JID serverAddress;

    @Value("weaver")
    private String serverName;

    @Autowired
    private BasicStreamIDFactory iFactory;

    @Autowired
    private LocalSessionManager localSessionManager;

    @Autowired
    private LocalClientSession localClientSession;


    /**
     * 记录连接数
     */
    private final AtomicInteger connectionsCounter = new AtomicInteger(0);

    public AtomicInteger getConnectionsCounter() {
        return connectionsCounter;
    }

    //    private List preAuthenticatedSessions(LocalClientSession session){
////
////       // redisUtil.setObjectList(,session);
////    }

    public StreamID nextStreamID(){
        return iFactory.createStreamID();
    }

    public LocalClientSession createClientSession(ChannelHandlerContext channel){
        return createClientSession(channel.channel(),nextStreamID());
    }

    public LocalClientSession createClientSession(Channel channel, StreamID streamID){

        localClientSession.setServerName(serverName);
        localClientSession.setChannel(channel);
        localClientSession.setStreamID(streamID);
        Presence presence = new Presence();
        presence.setType(Presence.Type.unavailable);
        String id = streamID.getID();
        localClientSession.setAddress(JID.jid(null,"weaver",id));
        //localClientSession.setLanguage(language);
        localClientSession.setPresence(presence);

        localSessionManager.setPreAuthenticatedSessions(localClientSession);
        connectionsCounter.incrementAndGet();
        return localClientSession;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public JID getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(JID serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public LocalClientSession getSession(JID from){
        if (from.getResource() != null){
            LocalClientSession session = localSessionManager.getPreAuthenticatedSessions(from);
            if (session != null){
                return session;
            }

        }

        return null;
    }

    public List<LocalClientSession> getAllSession(){
        return localSessionManager.getAllSession();
    }

//    public List<LocalClientSession> getSingleAllSession(){
//
//    }

}
