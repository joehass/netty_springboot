package com.weaver.netty.Interceptor;

import com.weaver.Interceptor.PacketInterceptor;
import com.weaver.LoadService;
import com.weaver.netty.stanza.Stanza;
import com.weaver.session.LocalClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 胡烨
 * @Date: 2018/12/28 9:26
 * @Version 1.0
 */
@Component
public class InterceptorManager {

    @Autowired
    private LoadService loadService;

    public void invokeInterceptors(Stanza stanza, LocalClientSession session, boolean read, boolean processed){
        List<PacketInterceptor> globalInterceptors = loadService.globalInterceptors;
        for (PacketInterceptor interceptor:globalInterceptors){
            interceptor.interceptPacket(stanza,session,read,processed);
        }
    }
}
