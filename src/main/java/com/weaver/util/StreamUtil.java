package com.weaver.util;

import java.util.Random;

/**
 * @Author: 胡烨
 * @Date: 2019/1/3 16:17
 * @Version 1.0
 */
public class StreamUtil {

    public static String genRandomNum(int pwd_len) {
        final int maxNum = 36;
        int i;
        int count = 0;
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0','1', '2', '3', '4', '5', '6', '7', '8', '9' };

        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {


            i = Math.abs(r.nextInt(maxNum));

            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }

        return pwd.toString();
    }
}
