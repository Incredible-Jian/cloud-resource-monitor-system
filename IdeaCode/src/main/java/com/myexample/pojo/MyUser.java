package com.myexample.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MyUser {
    private int id;
    private String username;
    private String password;
    private int identify;
}
