package com.weaver.component.roster;

/**
 * @Author: 胡烨
 * @Date: 2019/1/12 13:36
 * @Version 1.0
 */

/**
 * 代表一个单个用户名册
 */
public class RosterItem {

    public enum SubType {

        /**
         * 表示名册应该移除
         */
        REMOVE(-1),

        /**
         * 没有建立订阅
         */
        NONE(0),

        /**
         * 名册拥有者订阅出席状态
         */
        TO(1),

        /**
         * 单个名册已经订阅了拥有着的出席状态
         */
        FROM(2),

        /**
         * 单个名册者和拥有者互相订阅
         */
        BOTH(3);

        private final int value;

        SubType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
