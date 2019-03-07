package com.weaver.plugin.util;

/**
 * @Author: 胡烨
 * @Date: 2019/1/23 10:18
 * @Version 1.0
 */
public class JIDUtil {

    public static String conNodeToID(String node){
        //如果是推送id
        if (node.startsWith("push|")){
            return "-1";
        }else {
            int i = node.indexOf("|");
            if (i > 0){
                return node.substring(0,i);
            }else if (i == 0){
                return "-1";
            } else {
                return node;
            }
        }
    }
}
