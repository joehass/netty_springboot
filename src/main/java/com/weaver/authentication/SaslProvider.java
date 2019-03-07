package com.weaver.authentication;

import java.security.Provider;

/**
 * @Author: 胡烨
 * @Date: 2019/2/15 10:13
 * @Version 1.0
 */
public class SaslProvider extends Provider {

    public SaslProvider() {
        super("JiveSoftware", 1.0, "JiveSoftware SASL provider v1.0, implementing server mechanisms for: PLAIN, SCRAM-SHA-1");
        // Add SaslServer supporting the SCRAM-SHA-1 SASL mechanism
        put("SaslServerFactory.SCRAM-SHA-1", "com.weaver.authentication.SaslServerFactoryImpl");
    }
}
