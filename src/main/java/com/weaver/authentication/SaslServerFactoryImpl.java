package com.weaver.authentication;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;
import java.util.Map;

/**
 * @Author: 胡烨
 * @Date: 2019/2/15 9:13
 * @Version 1.0
 */

/**
 * 验证机制
 */
public class SaslServerFactoryImpl implements SaslServerFactory {

    private static final String myMechs[] = { "PLAIN", "SCRAM-SHA-1" };
    private static final int PLAIN = 0;
    private static final int SCRAM_SHA_1 = 1;

    @Override
    public SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
        if (mechanism.equals(myMechs[SCRAM_SHA_1])){
            if (cbh == null){
                throw new SaslException("CallbackHandler with support for AuthorizeCallback required");
            }
            return new ScramSha1SaslServer();
        }
        return null;
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
        if (checkPolicy(props)){
            return myMechs;
        }
        return new String[] {};
    }

    /**
     * 登陆机制要求允许匿名登陆
     * @param props
     * @return
     */
    private boolean checkPolicy(Map<String,?> props){
        boolean result = true;
        if (props != null){
            //true：表示不允许匿名登陆
            //false：表示允许匿名登陆
            String policy = (String) props.get(Sasl.POLICY_NOANONYMOUS);
            if (Boolean.parseBoolean(policy)){
                result = false;
            }
        }
        return result;
    }
}
