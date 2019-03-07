package com.weaver.session;

import com.weaver.manager.session.SessionManager;
import com.weaver.util.StreamID;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Locale;

/**
 * @Author: 胡烨
 * @Date: 2019/1/3 16:36
 * @Version 1.0
 */
@Component
public class LocalSession implements Serializable {


    private Channel channel;

    private SessionManager sessionManager;

    private String serverName;

    private long startDate = System.currentTimeMillis();

    private StreamID streamID;

    private Locale language;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public StreamID getStreamID() {
        return streamID;
    }

    public void setStreamID(StreamID streamID) {
        this.streamID = streamID;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }
}
