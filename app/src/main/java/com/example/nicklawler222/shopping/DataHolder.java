package com.example.nicklawler222.shopping;

import java.util.ArrayList;

/**
 * Created by Eric on 11/13/2014.
 */
public class DataHolder {
    private String data_username;
    private ArrayList searchHistory;
    public String getData() { return data_username; }
    public ArrayList getHistory() {return searchHistory; }
    public void setData(String data) { this.data_username = data; }
    public void addHistory(String text) {searchHistory.add(text);}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
