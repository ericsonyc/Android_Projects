package com.android.contentapplication.app;

/**
 * Created by ericson on 2015/12/24 0024.
 */
public class Person {
    public static int _ID = 0;
    public String NAME = "";
    public String number = "";

    public Person(String name, String num) {
        this.NAME = name;
        this.number = num;
        this._ID++;
    }
}
