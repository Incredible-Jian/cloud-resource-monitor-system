package com.myexample.service;

import com.myexample.pojo.MyUser;

import java.util.List;

public interface UserManagerService {
    public void addUser(MyUser user);
    public void deleteUser(int id);
    public void updateUser(MyUser user);
    public List<MyUser> findall();
    public MyUser findUserById(int id);
}
