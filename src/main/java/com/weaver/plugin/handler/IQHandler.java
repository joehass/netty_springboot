package com.weaver.plugin.handler;

import com.weaver.netty.stanza.IQ;

public abstract class IQHandler {

    public IQ process(IQ iq){

        IQ reply = handleIQ(iq);

        if (reply != null){
            return reply;
        }else
            return null;
    }

    public abstract IQ handleIQ(IQ packet);
}
