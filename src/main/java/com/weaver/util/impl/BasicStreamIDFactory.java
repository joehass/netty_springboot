package com.weaver.util.impl;

import com.weaver.util.StreamID;
import com.weaver.util.StreamIDFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @Author: 胡烨
 * @Date: 2019/2/11 10:18
 * @Version 1.0
 */
@Service
public class BasicStreamIDFactory implements StreamIDFactory {

    private static final int MAX_STARING_SIZE = 10;

    /**
     * 使用Java的随机数，如果能够猜测当前的种子，则可以预测流ID。
     */
    Random random = new SecureRandom();

    @Override
    public StreamID createStreamID() {
        String s = new BigInteger(MAX_STARING_SIZE * 5, random).toString(36);


        return new BasicStreamID(s);
    }
}
