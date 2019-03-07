package com.weaver.staticconst;

import com.weaver.session.LocalClientSession;
import io.netty.util.AttributeKey;

/**
 * @Author: 胡烨
 * @Date: 2018/12/27 15:51
 * @Version 1.0
 */

/**
 * 静态常量
 */
public class Attr {
    public final static AttributeKey<Boolean> READ = AttributeKey.valueOf("read");
    public final static AttributeKey<Boolean> WRITE = AttributeKey.valueOf("write");

    public final static String ROSTER = "roster";

    public final static AttributeKey<LocalClientSession> SESSION = AttributeKey.valueOf("session");
}
