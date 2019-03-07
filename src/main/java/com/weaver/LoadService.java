package com.weaver;

import com.weaver.Interceptor.PacketInterceptor;
import com.weaver.mapper.OfUserMapper;
import com.weaver.plugin.group.IQ.GroupIQHandler;
import com.weaver.plugin.multiRouter.MultiRouterPlugin;
import com.weaver.pojo.OfUser;
import com.weaver.redis.RedisUtil;
import com.weaver.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LoadService implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    RedisUtil redisUtil;

    @Qualifier("ofUserMapper")
    @Autowired
    private OfUserMapper ofUserMapper;

    public List<PacketInterceptor> globalInterceptors =
            new CopyOnWriteArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {//所有的bean被成功装载后调用
        if (event.getApplicationContext().getParent() == null) {
            addIQHandler();
            addInterceptor();
            addUser();
            addPlugin();

        }
    }



    public void addIQHandler(){
        GroupIQHandler groupIQHandler = (GroupIQHandler) SpringUtil.getApplicationContext().getBean("groupIQHandler");
        redisUtil.setIQHandler(groupIQHandler.getModuleName(),groupIQHandler);
    }

    public void addInterceptor(){
        MultiRouterPlugin multiRouterPlugin = (MultiRouterPlugin) SpringUtil.getApplicationContext().getBean("multiRouterPlugin");
        globalInterceptors.add(multiRouterPlugin);
    }

    public void addUser(){
        List<OfUser> ofUsers = ofUserMapper.loadUser();
        redisUtil.delete("User");
        redisUtil.setObjectList("User",ofUsers);
    }

    public void addPlugin() {
        MultiRouterPlugin multiRouterPlugin = (MultiRouterPlugin) SpringUtil.getApplicationContext().getBean("multiRouterPlugin");
        multiRouterPlugin.initializePlugin();
    }

}
