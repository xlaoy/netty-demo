package com.xlaoy.netty.demo05;


import org.msgpack.annotation.Message;

/**
 * Created by Administrator on 2018/4/19 0019.
 */
@Message
public class User {

    private Integer age;

    private String name;

    public User() {
    }

    public User(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
