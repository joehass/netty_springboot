package com.weaver.component.listener;

import com.weaver.netty.stanza.Presence;
import com.weaver.session.LocalClientSession;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 胡烨
 * @Date: 2019/1/11 9:27
 * @Version 1.0
 */
@Component
public class PresenceEventDispatcher implements Serializable {

    private static List<PresenceEventListener> listeners = new ArrayList<>();

    private static volatile PresenceEventDispatcher presenceEventDispatcher;

    public static synchronized PresenceEventDispatcher getInstance(){
        if (presenceEventDispatcher == null){
            synchronized (PresenceEventDispatcher.class){
                if (presenceEventDispatcher == null){
                    presenceEventDispatcher = new PresenceEventDispatcher();
                }
            }
        }
        return presenceEventDispatcher;
    }

    /**
     * 增加listener
     * @param listener
     */
    public void addListener(PresenceEventListener listener){

        listeners.add(listener);
    }

    public void removeListener(String key){
        listeners.remove(key);
    }

    public void availableSession(LocalClientSession session, Presence presence){

        if (!listeners.isEmpty()){
            for (PresenceEventListener listener : listeners){
                listener.availableSession(session,presence);
            }
        }
    }
}