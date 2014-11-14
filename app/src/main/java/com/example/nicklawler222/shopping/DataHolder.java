package com.example.nicklawler222.shopping;

/**
 * Created by Eric on 11/13/2014.
 */
public class DataHolder {
    private String data_username;
    public String getData() { return data_username; }
    public void setData(String data) { this.data_username = data; }

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
