package com.weaver.util;

/**
 * @Author: 胡烨
 * @Date: 2019/2/14 13:41
 * @Version 1.0
 */

import java.io.*;
import java.util.Properties;

/**
 * 读取Sm3的配置文件
 */
public class Sm3FileConf {
    private static final String SM3_SWITCH_FILENAME = "Sm3Switch.properties" ;

    public static Properties loadFile(){
        InputStream is = null;
        Properties properties = new Properties();
        File configFile = new File(SM3_SWITCH_FILENAME);
        try {
            is = new FileInputStream(configFile);
            properties.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return properties;
    }
}
