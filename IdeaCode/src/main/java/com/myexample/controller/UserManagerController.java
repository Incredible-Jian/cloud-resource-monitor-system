package com.myexample.controller;

import com.myexample.pojo.MyUser;
import com.myexample.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserManagerController {
    @Autowired
    private UserManagerService myuserservice;
    @RequestMapping("/all")
    public String getalluser(Model model){
        List<MyUser> myusers = myuserservice.findall();
        model.addAttribute("alluser",myusers);
        return "manager";
    }
    @RequestMapping("del")
    public String deleteUser(int id){
        myuserservice.deleteUser(id);
        return "forward:/all";
    }
    @RequestMapping("/add")
    public String addpage(){
        return "adduser";
    }
    @RequestMapping("/adduser")
    public String addusermanager(MyUser myuser){
        myuserservice.addUser(myuser);
        return "forward:/all";
    }
    @RequestMapping("/update")
    public String updatepage(int id,Model model){
        MyUser myuser = myuserservice.findUserById(id);
        model.addAttribute("simpleuser",myuser);
        return "updateuser";
    }
    @RequestMapping("/updateuser")
    public String updateuser(MyUser myuser){
        myuserservice.updateUser(myuser);
        return "forward:/all";
    }
}
