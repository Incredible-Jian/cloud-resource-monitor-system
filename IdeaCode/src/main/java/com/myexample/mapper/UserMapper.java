package com.myexample.mapper;

import com.myexample.pojo.MyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select *from myuser where username=#{username} and password=#{password}")
    public MyUser findUser(MyUser myuser);
}
