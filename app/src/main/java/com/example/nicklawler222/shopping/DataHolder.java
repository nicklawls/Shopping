package com.example.nicklawler222.shopping;

import java.util.ArrayList;

/**
 * Created by Eric on 11/13/2014.
 */
public class DataHolder {

    private String data_username;
    private String pno_forreview;
    private ArrayList searchHistory = new ArrayList();
    private String sqlreview;
    private boolean needtoup_sql = false;
    public String SEARCH_URL;
    public String TOKEN;


    public String getData() { return data_username; }
    public ArrayList getHistory() {return searchHistory; }
    public String getPNO() { return pno_forreview; }
    public String getSQL() { return sqlreview; }
    public boolean getUpCheck() { return needtoup_sql; }
    public String getURL() { return SEARCH_URL; }
    public String getTOKEN() { return TOKEN; }


    public void setData(String data) { this.data_username = data; }
    public void addHistory(String text) {searchHistory.add(text);}
    public void setPNO( String product ) { pno_forreview = product; }
    public void setSQL( String sql ) { sqlreview = sql; }
    public void setUpCheck( boolean x ) { needtoup_sql = x; }
    public void setURL( String x ) { SEARCH_URL = x; }
    public void setTOKEN( String x ) { TOKEN = x; }


    public static final String MY_IMGUR_CLIENT_ID = "20538314177cb5f";
    public static final String MY_IMGUR_CLIENT_SECRET = "99fb936ab1862c209f86c2d4f837591ccbef2b4a";
    public static final String MY_IMGUR_REDIRECT_URL = "http://imgur.com";

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
