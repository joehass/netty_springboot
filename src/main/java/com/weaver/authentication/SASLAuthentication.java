package com.weaver.authentication;

import com.weaver.netty.xml.XMLElement;
import com.weaver.session.LocalClientSession;
import com.weaver.util.StringUtils;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.security.Security;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * @Author: 胡烨
 * @Date: 2019/1/6 10:48
 * @Version 1.0
 */
@Component
public class SASLAuthentication {

    private static final String SASL_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    private static Map<String, ElementType> typeMap = new TreeMap<>();

    private static final Pattern BASE64_ENCODED = Pattern.compile("^(=|([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==))$");

    private static Set<String> mechanisms = null;

    private enum Failure {

        ABORTED("aborted"),
        ACCOUNT_DISABLED("account-disabled"),
        CREDENTIALS_EXPIRED("credentials-expired"),
        ENCRYPTION_REQUIRED("encryption-required"),
        INCORRECT_ENCODING("incorrect-encoding"),
        INVALID_AUTHZID("invalid-authzid"),
        INVALID_MECHANISM("invalid-mechanism"),
        MALFORMED_REQUEST("malformed-request"),
        MECHANISM_TOO_WEAK("mechanism-too-weak"),
        NOT_AUTHORIZED("not-authorized"),
        TEMPORARY_AUTH_FAILURE("temporary-auth-failure");

        private String name = null;

        private Failure(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ElementType {
        ABORT("abort"),AUTH("auth"),RESPONSE("response"),CHALLENGE("challenge"), FAILURE("failure"), UNDEF("");
        private String name = null;

        ElementType(String name){
            this.name = name;
            typeMap.put(this.name,this);
        }

        public static ElementType valueof(String name){
            if (name == null){
                return UNDEF;
            }

            ElementType e = typeMap.get(name);

            return e != null ? e : UNDEF;
        }

    }

    static {
        initMechanisms();
    }

    private static void initMechanisms(){
        mechanisms = new HashSet<>();
        mechanisms.add("ANONYMOUS");
        mechanisms.add("PLAIN");
        mechanisms.add("DIGEST-MD5");
        mechanisms.add("CRAM-MD5");
        mechanisms.add("SCRAM-SHA-1");
        mechanisms.add("JIVE-SHAREDSECRET");

        Security.addProvider(new SaslProvider());
    }

    public enum Status {
        /**
         * 需要回复，session仍然需要验证
         */
        needResponse,

        /**
         * 认证失败
         */
        failed,

        /**
         * 认证成功
         */
        authenticated;
    }

    public Status handle(LocalClientSession session, XMLElement doc){
        Status status = null;
        String mechanism;
        if (doc.getNamespace().equals(SASL_NAMESPACE)){
            ElementType abort = ElementType.ABORT;
            ElementType type = ElementType.valueOf(doc.getTagName().toUpperCase());

            switch (type) {
                case ABORT:
                    authenticationFailed(session,Failure.ABORTED);
                    status = Status.failed;
                    break;
                case AUTH:
                    mechanism = doc.getAttribute("mechanism");

                    if (mechanism == null){
                        authenticationFailed(session,Failure.ABORTED);
                        status = Status.failed;
                        break;
                    }
                    session.setSessionData("SaslMechanism", mechanism);

                    if (mechanisms.contains(mechanism)){
                        try{
                            Map<String,String> props = new TreeMap<>();
                            props.put(Sasl.QOP,"auth");
                            SaslServer ss = Sasl.createSaslServer(mechanism,"xmpp",
                                    session.getServerName(),props,
                                    new XMPPCallbackHandler());

                            if (ss == null){
                                authenticationFailed(session, Failure.INVALID_MECHANISM);
                                return Status.failed;
                            }

                            byte[]token = new byte[0];
                            String value = doc.getText();//拿到response的值
                            if (value.length() > 0){
                                if (!BASE64_ENCODED.matcher(value).matches()) {
                                    authenticationFailed(session, Failure.INCORRECT_ENCODING);
                                    return Status.failed;
                                }
                                token = StringUtils.decodeBase64(value);
                                if (token == null){
                                    token = new byte[0];
                                }
                            }
                            if (mechanism.equals("DIGEST-MD5")) {
                                // RFC2831 (DIGEST-MD5) says the client MAY provide an initial response on subsequent
                                // authentication. Java SASL does not (currently) support this and thows an exception
                                // if we try.  This violates the RFC, so we just strip any initial token.
                                token = new byte[0];
                            }

                            byte[] challenge = ss.evaluateResponse(token);
                            //向客户端发送challenge
                            sendChallenge(session,challenge);
                            status = Status.needResponse;//需要客户端返回内容
                            session.setSessionData("SaslServer", ss);

                        } catch (SaslException e) {
                            authenticationFailed(session, Failure.NOT_AUTHORIZED);
                            status = Status.failed;
                        }
                    }
                    break;
                case RESPONSE:
                    mechanism = (String) session.getSessionData("SaslMechanism");
                    if (mechanisms.contains(mechanism)) {
                        SaslServer ss = (SaslServer) session.getSessionData("SaslServer");
                        //判断是否完成认证
                        boolean ssComplete = ss.isComplete();
                        String response = doc.getText();
                        try {
                            if (ssComplete) {

                            } else {
                                byte[] data = StringUtils.decodeBase64(response);
                                if (data == null) {
                                    data = new byte[0];
                                }
                                byte[] challenge = ss.evaluateResponse(data);
                                if (ss.isComplete()){
                                    authenticationSuccessful(session,ss.getAuthorizationID(),challenge);
                                    status = Status.authenticated;
                                }
                            }
                        } catch (SaslException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }

        if (status == Status.failed || status == Status.authenticated) {
            // Remove the SaslServer from the Session
            session.removeSessionData("SaslServer");
            // Remove the requested SASL mechanism by the client
            session.removeSessionData("SaslMechanism");
        }
        return status;
    }

    /**
     * 认证成功
     * @param session
     * @param username
     * @param successData
     */
    private void authenticationSuccessful(LocalClientSession session,String username,byte[] successData){
        StringBuilder reply = new StringBuilder(80);
        reply.append("<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"");
        if (successData != null) {
            String successData_b64 = StringUtils.encodeBase64(successData).trim();
            reply.append('>').append(successData_b64).append("</success>");
        }
        session.getChannel().writeAndFlush(new TextWebSocketFrame(reply.toString()));

        session.setAuthToken(new AuthToken(username));
    }

    /**
     * 向客户端发送challenge
     * @param session
     * @param challenge
     */
    private void sendChallenge(LocalClientSession session,byte[] challenge){
        StringBuilder reply = new StringBuilder(250);
        String challenge_b64 = StringUtils.encodeBase64(challenge).trim();

        if ("".equals(challenge_b64)){
            challenge_b64 = "=";
        }
        reply.append(
                "<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
        reply.append(challenge_b64);
        reply.append("</challenge>");
        session.getChannel().writeAndFlush(new TextWebSocketFrame(reply.toString()));
    }


    private void authenticationFailed(LocalClientSession session, Failure failure){
        StringBuilder reply = new StringBuilder(80);
        reply.append("<failure xmln=\"urn:ietf:params:xml:ns:xmpp-sasl\"><");
        reply.append(failure.toString());
        reply.append("/></failure>");
        session.getChannel().writeAndFlush(reply.toString());
    }


}
