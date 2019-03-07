package com.weaver.util.impl;

import com.weaver.util.StreamID;

/**
 * @Author: 胡烨
 * @Date: 2019/2/13 10:36
 * @Version 1.0
 */
public class BasicStreamID implements StreamID {

    String id;

    public BasicStreamID(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
