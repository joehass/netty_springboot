package com.weaver.authentication;

/**
 * @Author: 胡烨
 * @Date: 2019/2/15 14:45
 * @Version 1.0
 */

/**
 * 证明用户已成功通过身份验证的令牌。
 */
public class AuthToken {

    private String username;
    private String domain;
    private Boolean anonymous;

    public AuthToken(String jid){
        if (jid == null){
            this.domain = "weaver";
            return ;
        }
        int index = jid.indexOf("@");
        if (index > -1){
            this.username = jid.substring(0,index);
            this.domain = jid.substring(index+1);
        }else {
            this.username = jid;
            this.domain = "weaver";
        }
    }

    public AuthToken(String jid, Boolean anonymous) {
        int index = jid.indexOf("@");
        if (index > -1) {
            this.username = jid.substring(0,index);
            this.domain = jid.substring(index+1);
        } else {
            this.username = jid;
            this.domain = "weaver";
        }
        this.anonymous = anonymous;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
