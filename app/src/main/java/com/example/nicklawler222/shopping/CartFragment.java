package com.example.nicklawler222.shopping;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_cart, menu);



    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ArrayList productnumbers = new ArrayList();
            ArrayList productnames = new ArrayList();
            ArrayList productprice = new ArrayList();
            ArrayList totalproductprice = new ArrayList();
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
                sql = "SELECT sc.product_no, p.name, p.price FROM shopping_cart sc, products p WHERE username = '" + username + "' AND sc.product_no = p.product_no";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    productnumbers.add(rs.getString("product_no"));
                    productnames.add(rs.getString("name"));
                    productprice.add(rs.getString("price"));
                }

                sql = "SELECT sum(p.price) FROM products p, shopping_cart sc WHERE username = '" + username;
                sql += "' AND p.product_no = sc.product_no";

                rs = st.executeQuery(sql);
                rs.next();

                totalproductprice.add(rs.getString("sum"));

                rs.close();
                st.close();
                conn.close();
                product.putStringArrayList("product_no", productnumbers);
                product.putStringArrayList("productname", productnames);
                product.putStringArrayList("productprice", productprice);
                product.putStringArrayList("totalproductprice",totalproductprice);
                return product;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bundle product) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container,CartListDisplayFragment.newInstance(product.getStringArrayList("product_no"),product.getStringArrayList("productname"), product.getStringArrayList("productprice"), product.getStringArrayList("totalproductprice")));
            ft.addToBackStack("CartFragment");
            ft.commit();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment previousInstance = getFragmentManager().findFragmentByTag("CartFragment");
        if (previousInstance != null)
            ft.remove(previousInstance);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}