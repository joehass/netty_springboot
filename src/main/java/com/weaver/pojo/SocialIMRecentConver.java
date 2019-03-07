package com.weaver.pojo;

/**
 * @Author: 胡烨
 * @Date: 2019/1/25 9:41
 * @Version 1.0
 */
public class SocialIMRecentConver {

    private String userid;

    private String targetid;

    private int isdel;

    private int istop;

    private int unreadcount;

    private Long updatetime;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }

    public int getIstop() {
        return istop;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }

    public int getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(int unreadcount) {
        this.unreadcount = unreadcount;
    }

    public Long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Long updatetime) {
        this.updatetime = updatetime;
    }
}
