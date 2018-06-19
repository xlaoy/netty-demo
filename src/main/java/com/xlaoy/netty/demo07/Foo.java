package com.xlaoy.netty.demo07;

/**
 * Created by Administrator on 2018/5/8 0008.
 */
public class Foo {

    private String name;

    public Foo() {
    }

    public Foo(Foo foo) {
        this.name = foo.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
