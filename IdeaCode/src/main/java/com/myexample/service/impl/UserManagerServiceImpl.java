package com.myexample.service.impl;

import com.myexample.mapper.UserManagerMapper;
import com.myexample.pojo.MyUser;
import com.myexample.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
public class UserManagerServiceImpl implements UserManagerService {

    @Autowired
    private UserManagerMapper mymanager;

    @Override
    public MyUser findUserById(int id) {
        return mymanager.findUserById(id);
    }

    @Override
    public void addUser(MyUser user) {
        mymanager.addUser(user);
    }

    @Override
    public void deleteUser(int id) {
        mymanager.deleteUser(id);
    }

    @Override
    public void updateUser(MyUser user) {
        mymanager.updateUser(user);
    }

    @Override
    public List<MyUser> findall() {
        return mymanager.findall();
    }
}
