package com.myexample.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.myexample.pojo.MyUser;
import com.myexample.service.BlockedIPService;
import com.myexample.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

//Controller返回的是页面，RestController返回的是数据
@Controller
@RequestMapping("/")
public class MyUserController extends BaseController {
    @Autowired
    private MyUserService myuserservice;
    @RequestMapping("/login")
    public String mylogin(MyUser myuser){
        MyUser myuser1 = myuserservice.findUser(myuser);
        if(myuser1!=null) {
             if(myuser1.getIdentify()==1 ){
                 return "forward:/all";
             }else{
                 return "index";
             }
        }else{
            return "login";
        }
    }
    @RequestMapping("/")
    public String login(){
        return "login";
    }
    @Autowired
    private BlockedIPService blockedIPService;

}
