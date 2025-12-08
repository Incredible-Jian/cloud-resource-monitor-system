package com.myexample.service.impl;

import com.myexample.mapper.UserMapper;
import com.myexample.pojo.MyUser;
import com.myexample.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyUserServiceImpl implements MyUserService {
    @Autowired
    private UserMapper myusermapper;
    @Override
    public MyUser findUser(MyUser myuser) {
        return myusermapper.findUser(myuser);
    }
}
