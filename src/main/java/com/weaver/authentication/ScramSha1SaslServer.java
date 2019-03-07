package com.weaver.authentication;

import com.weaver.manager.UserManager;
import com.weaver.util.SpringUtil;

import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 胡烨
 * @Date: 2019/2/12 17:07
 * @Version 1.0
 */
public class ScramSha1SaslServer implements SaslServer {

    //第一次进入
    private State state = State.INITIAL;

    private String clientFirstMessageBare;
    private String username;
    private String nonce;
    private String serverFirstMessage;


    public static ScramSha1SaslServer scramSha1SaslServer;

    public ScramSha1SaslServer(){
    }


    private static final Pattern
            CLIENT_FIRST_MESSAGE = Pattern.compile("^(([pny])=?([^,]*),([^,]*),)(m?=?[^,]*,?n=([^,]*),r=([^,]*),?.*)$"),
            CLIENT_FINAL_MESSAGE = Pattern.compile("(c=([^,]*),r=([^,]*)),p=(.*)$");

    private enum State {
        INITIAL,
        IN_PROGRESS,
        COMPLETE;
    }

    @Override
    public String getMechanismName() {
        return "SCRAM-SHA-1";
    }

    /**
     * 解析响应并生成challenge
     * @param response
     * @return
     * @throws SaslException
     */
    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException {
            byte[] challenge = new byte[0];
            switch (state){
                //根据响应生成challenge
                case INITIAL:
                    challenge = generateServerFirstMessage(response);
                    state = State.IN_PROGRESS;
                    break;
                case IN_PROGRESS:
                    challenge = generateServerFinalMessage(response);
                    state = State.COMPLETE;
                    break;
            }
            return challenge;
    }

    /**
     * 第一次生成challenge
     * @param response
     * @return
     */
    private byte[] generateServerFirstMessage(final byte[] response) throws SaslException {
        //根据响应解码
        String clientFirstMessage = new String(response, StandardCharsets.US_ASCII);
        //生成一个match对象，用于查找对应的内容
        Matcher m = CLIENT_FIRST_MESSAGE.matcher(clientFirstMessage);

        //如果没有匹配内容
        if (!m.matches()){
            throw new SaslException("Invalid first client message");
        }

        //group匹配不同括号内的值
        clientFirstMessageBare = m.group(5);
        username = m.group(6);
        String clientNonce = m.group(7);

        nonce = clientNonce + UUID.randomUUID().toString();
        serverFirstMessage = String.format("r=%s,s=%s,i=%d", nonce, DatatypeConverter.printBase64Binary(getSalt(username)),
                    getIterations(username));

        return serverFirstMessage.getBytes(StandardCharsets.US_ASCII);

    }

    /**
     * 最终响应返回服务器签名。
     * @param response
     * @return
     */
    private byte[] generateServerFinalMessage(final byte[] response) throws SaslException {
        String clientFinalMessage = new String(response, StandardCharsets.US_ASCII);
        Matcher m = CLIENT_FINAL_MESSAGE.matcher(clientFinalMessage);
        String clientFinalMessageWithoutProof = m.group(1);
//        String channelBinding = m.group(2);
        String clientNonce = m.group(3);
        String proof = m.group(4);

        try {
            String authMessage = clientFirstMessageBare + "," + serverFirstMessage + "," + clientFinalMessageWithoutProof;
            byte[] storedKey = getStoreKey(username);
            byte[] serverKey = getServerKey(username);

            byte[] clientSignature = ScramUtils.computeHmac(storedKey, authMessage);
            byte[] serverSignature = ScramUtils.computeHmac(serverKey, authMessage);

            byte[] clientKey = clientSignature.clone();
            byte[] decodedProof = DatatypeConverter.parseBase64Binary(proof);

            for (int i = 0; i < clientKey.length; i++) {
                clientKey[i] ^= decodedProof[i];
            }

            return ("v=" + DatatypeConverter.printBase64Binary(serverSignature))
                    .getBytes(StandardCharsets.US_ASCII);

        } catch (SaslException e) {
            throw new SaslException(e.getMessage(), e);
        }

    }

    private int getIterations(String username){
        UserManager userManager = (UserManager) SpringUtil.getApplicationContext().getBean("userManager");

        return userManager.getUserByUserName(username).getIterations();
    }

    /**
     * 通过给定的username在数据库检索对应的sale
     * 如果没有检索到，则返回一个随机的salt
     * @param username
     * @return
     */
    private byte[] getSalt(final String username) {
        //获得salt值
        UserManager userManager = (UserManager) SpringUtil.getApplicationContext().getBean("userManager");
        String saltshaker = userManager.getUserByUserName(username).getSalt();
        byte[] salt = new byte[0];
        if (saltshaker == null) {//从数据库中默认可以查出salt值
        } else {
            salt = DatatypeConverter.parseBase64Binary(saltshaker);
        }
        return salt;
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public String getAuthorizationID() {
        if (isComplete()){
            return username;
        }else {
            throw new IllegalStateException("SCRAM-SHA-1 authentication not completed");
        }
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
        return new byte[0];
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
        return new byte[0];
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
        return null;
    }

    @Override
    public void dispose() throws SaslException {

    }

    /**
     * 通过给定的用户名从数据库检索密码
     * @param username
     * @return
     */
    private byte[] getStoreKey(final String username){
        UserManager userManager = (UserManager) SpringUtil.getApplicationContext().getBean("userManager");
        final String storedKey = userManager.getUserByUserName(username).getStoredKey();
        return DatatypeConverter.parseBase64Binary( storedKey );
    }

    private byte[] getServerKey(final String username){
        UserManager userManager = (UserManager) SpringUtil.getApplicationContext().getBean("userManager");
        final String serverKey = userManager.getUserByUserName(username).getServerKey();
        return DatatypeConverter.parseBase64Binary( serverKey );
    }
}
