package com.weaver.mybatis.configure;

import com.weaver.mybatis.IdSequenceInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 胡烨
 * @Date: 2019/1/22 19:29
 * @Version 1.0
 */
@Configuration
public class MybatisConfiguration {

    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                IdSequenceInterceptor idSequenceInterceptor = new IdSequenceInterceptor();
                configuration.addInterceptor(idSequenceInterceptor);
            }
        };
    }
}
