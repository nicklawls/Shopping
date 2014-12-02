package com.example.nicklawler222.shopping;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CartFragment extends Fragment {

    public CartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.fragment_browse_history, container, false);

        new FetchSQL().execute();
        return null;
    }
    private class FetchSQL extends AsyncTask<Void,Void,Bundle> {

        protected Bundle doInBackground(Void... params) {
            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ArrayList productnumbers = new ArrayList();
            ArrayList productnames = new ArrayList();
            Bundle product = new Bundle();
            String url;
            url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
            Connection conn;
            try {
                DriverManager.setLoginTimeout(5);
                conn = DriverManager.getConnection(url);
                Statement st = conn.createStatement();
                String username = DataHolder.getInstance().getData();
                String sql;
                sql = "SELECT DISTINCT sc.product_no, p.name  FROM shopping_cart sc, products p WHERE username = '" + username + "' AND sc.product_no = p.product_no";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    productnumbers.add(rs.getString("product_no"));
                    productnames.add(rs.getString("name"));
                }
                rs.close();
                st.close();
                conn.close();
                product.putStringArrayList("product_no",productnumbers);
                product.putStringArrayList("productname",productnames);
                return product;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bundle product) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container,ProductListFragment.newInstance(product.getStringArrayList("product_no"),product.getStringArrayList("productname")));
            ft.commit();
        }
    }
}