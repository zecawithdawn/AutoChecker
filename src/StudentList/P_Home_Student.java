package com.example.kyh.real.StudentList;

/**
 * Created by park on 2015. 1. 9..
 */
public class P_Home_Student {
    public String name;
    public String id;
    public String state;

    public P_Home_Student(String name, String id, String state) {
        this.name = name;
        this.id = id;
        this.state = state;
    }

    public void setState (String s) {
        state = s;
    }

    public String getState () { return state; }

    public String getName () {
        return name;
    }

    public String getId () { return id; }
}
