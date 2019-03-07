package com.weaver.authentication;

import javax.security.auth.callback.*;
import javax.security.sasl.RealmCallback;
import java.io.IOException;

/**
 * @Author: 胡烨
 * @Date: 2019/2/11 17:49
 * @Version 1.0
 */

/**
 * sasal认证的回调类，handle方法将在sasl认证后被调用
 * 使用摘要的机制不包含密码，因此服务器需要使用用户存储的密码（以某种方式）将其与指定的摘要进行比较。
 * 此操作要求使用的用户提供程序支持密码检索
 * 。@link saslauthenitation如果正在使用的用户提供程序不支持密码检索，则不应提供此类sasl机制。
 *
 */

public class XMPPCallbackHandler implements CallbackHandler {

    public XMPPCallbackHandler() {
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        System.out.println("XMPPCallbackHandler 回调");
        System.out.println("XMPPCallbackHandler 回调");
        String realm;
        String name = null;

        for (Callback callback : callbacks) {
            if (callback instanceof RealmCallback) {
                realm = ((RealmCallback) callback).getText();
                if (realm == null) {
                    realm = ((RealmCallback) callback).getDefaultText();
                }
                //Log.debug("XMPPCallbackHandler: RealmCallback: " + realm);
            } else if (callback instanceof NameCallback) {
                name = ((NameCallback) callback).getName();
                if (name == null) {
                    name = ((NameCallback) callback).getDefaultName();
                }
                //Log.debug("XMPPCallbackHandler: NameCallback: " + name);
            }
        }
    }
}
