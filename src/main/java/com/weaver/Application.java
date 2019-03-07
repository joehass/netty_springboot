package com.weaver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@ComponentScan(basePackages = "com.weaver")
@EnableCaching
@MapperScan("com.weaver")
public class Application {



    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);

    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


}
