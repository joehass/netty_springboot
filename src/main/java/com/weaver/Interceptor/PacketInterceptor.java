package com.weaver.Interceptor;

import com.weaver.netty.stanza.Stanza;
import com.weaver.session.LocalClientSession;

public interface PacketInterceptor {

    void interceptPacket(Stanza stanza, LocalClientSession session, Boolean incoming, Boolean processed);
}
