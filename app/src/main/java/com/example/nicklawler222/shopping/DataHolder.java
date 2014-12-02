package com.example.nicklawler222.shopping;

import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 11/13/2014.
 */
public class DataHolder {

    private String data_username;
    private String pno_forreview;
    private ArrayList searchHistory = new ArrayList();
    private String sqlreview;
    private boolean needtoup_sql = false;
    private boolean has_purchased = false;
    private ArrayList<String> object_data = new ArrayList<String>();
    public String SEARCH_URL;
    public String TOKEN;

    public String getData() { return data_username; }
    public ArrayList getHistory() {return searchHistory; }
    public ArrayList<String> getObjectData() { return object_data; }
    public String getPNO() { return pno_forreview; }
    public String getURL() { return SEARCH_URL; }
    public String getTOKEN() { return TOKEN; }


    public void setData(String data) { this.data_username = data; }
    // checks to see if user is logged in
    public boolean isLoggedIn() {return (!data_username.equals("default"));}
    public void addHistory(String text) {searchHistory.add(text);}
    public void setSQL( String sql ) { sqlreview = sql; }
    public void setUpCheck( boolean x ) { needtoup_sql = x; }
    public void addOData(String text) {object_data.add(text);}
    public void clearOdata() { object_data.clear(); }
    public void setURL( String x ) { SEARCH_URL = x; }
    public void setTOKEN( String x ) { TOKEN = x; }

    public void setPNO( String product ) {
        // setting has_purchased on update of product_no to avoid race condition
        if (isLoggedIn()) {
            checkPurchasedTask task = new checkPurchasedTask(pno_forreview, data_username);
            task.execute((Void) null );
        }
        pno_forreview = product;
    }

    public Boolean hasPurchased() { // checks if logged in user has purchased current item
        return has_purchased;
    }

    public class checkPurchasedTask extends AsyncTask<Void, Void, Void> {
        private final String username;
        private final String product_no;

        checkPurchasedTask(String pno, String uname) {
            product_no = pno;
            username = uname;
        }

        protected Void doInBackground(Void ...params) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String url;
            url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
            Connection conn;

            try {

                DriverManager.setLoginTimeout(5);
                conn = DriverManager.getConnection(url);
                Statement st = conn.createStatement();
                ResultSet rs = null;

                String sql =  "SELECT count(*) FROM purchased WHERE username = ";
                sql += "'" + username + "' AND product_no = '" + product_no + "'";
                //System.out.println(sql);
                rs = st.executeQuery(sql);
                rs.next(); // assumes one row in result

                has_purchased = (!rs.getString("count").equals("0"));
                //System.out.println(rs.getString("count"));

                rs.close();
                st.close();
                conn.close();
            }

            catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static final String MY_IMGUR_CLIENT_ID = "20538314177cb5f";
    public static final String MY_IMGUR_CLIENT_SECRET = "99fb936ab1862c209f86c2d4f837591ccbef2b4a";
    public static final String MY_IMGUR_REDIRECT_URL = "http://imgur.com";

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
