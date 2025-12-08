package com.myexample.mapper;

import com.myexample.pojo.MyUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserManagerMapper {
    @Select("select * from myuser")
    public List<MyUser> findall();
    @Insert("insert into myuser(username,password,identify)" +
            " values (#{username},#{password},#{identify})")
    public void addUser(MyUser user);
    @Delete("delete from myuser where id=#{id}")
    public void deleteUser(int id);
    @Update("update myuser set username=#{username},password=#{password},identify=#{identify} " +
            "where id=#{id}")
    public void updateUser(MyUser myuser);
    @Select("select * from myuser where id=#{id}")
    public MyUser findUserById(int id);
}
