package com.weaver.controller;

import com.weaver.mapper.OfUserMapper;
import com.weaver.pojo.OfUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 胡烨
 * @Date: 2019/1/3 11:30
 * @Version 1.0
 */
@RestController
public class UserController {

    @Autowired
    private OfUserMapper mapper;

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public List<OfUser> query(){
        List<OfUser> ofUsers = mapper.loadUser();
        return  ofUsers;
    }

    @RequestMapping(value = "/socialIMMain/{frommain}/{isAero}/{pcOS}",method = RequestMethod.GET)
    public void main(String frommain,String isAero,String pcOS){

    }
}
