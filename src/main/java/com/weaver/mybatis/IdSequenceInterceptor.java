package com.weaver.mybatis;

import com.weaver.mapper.HistoryMsgRecentlyMapper;
import com.weaver.pojo.HistoryMsg;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: 胡烨
 * @Date: 2019/1/22 18:38
 * @Version 1.0
 */
@Intercepts({@Signature(type = StatementHandler.class,method = "update",args={Statement.class})})
public class IdSequenceInterceptor implements Interceptor {

    @Autowired
    private HistoryMsgRecentlyMapper historyMsgRecentlyMapper;

    /**
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("11111");
        StatementHandler  statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        Map parameterObject = (Map) boundSql.getParameterObject();

        if (parameterObject.containsKey("historyMsg")){
            HistoryMsg historyMsg = (HistoryMsg) parameterObject.get("historyMsg");
            historyMsgRecentlyMapper.insertHistoryMsgRecently(historyMsg);
        }
        String sql = boundSql.getSql();

        Object o = invocation.proceed();
        System.out.println("222222");
        return null;
    }

    @Override
    public Object plugin(Object target) {
        System.out.println("StatementHandlerInterceptor...plugin:mybatis将要包装的对象" + target);
        Object wrap = Plugin.wrap(target, this);

        return wrap;
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("插件配置的信息："+properties);
    }
}
