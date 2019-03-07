package com.weaver.route;

/**
 * @Author: 胡烨
 * @Date: 2019/1/21 10:24
 * @Version 1.0
 */

/**
 * 路由表用于保留对由该JVM承载的路由的引用的内部组件。
 * 在集群中运行时，每个集群成员将拥有自己的路由表，
 * 其中包含此类的实例。每个localroutingtable负责存储到本地群集节点承载的组件、
 * 客户端会话和传出服务器会话的路由。
 */
public class LocalRoutingTable {
}
