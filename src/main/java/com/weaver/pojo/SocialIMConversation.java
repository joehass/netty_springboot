package com.weaver.pojo;

/**
 * @Author: 胡烨
 * @Date: 2019/1/24 14:08
 * @Version 1.0
 */
public class SocialIMConversation {
    private String userid;

    private String targetid;

    private String targettype;

    private String targetname;

    private int unreadcnt;

    private String istop;

    private Long lasttime;

    private int isopenfire;

    private String msgcontent;

    private String msgid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTargettype() {
        return targettype;
    }

    public void setTargettype(String targettype) {
        this.targettype = targettype;
    }

    public String getTargetname() {
        return targetname;
    }

    public void setTargetname(String targetname) {
        this.targetname = targetname;
    }

    public int getUnreadcnt() {
        return unreadcnt;
    }

    public void setUnreadcnt(int unreadcnt) {
        this.unreadcnt = unreadcnt;
    }

    public String getIstop() {
        return istop;
    }

    public void setIstop(String istop) {
        this.istop = istop;
    }

    public Long getLasttime() {
        return lasttime;
    }

    public void setLasttime(Long lasttime) {
        this.lasttime = lasttime;
    }

    public int getIsopenfire() {
        return isopenfire;
    }

    public void setIsopenfire(int isopenfire) {
        this.isopenfire = isopenfire;
    }

    public String getMsgcontent() {
        return msgcontent;
    }

    public void setMsgcontent(String msgcontent) {
        this.msgcontent = msgcontent;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }
}
