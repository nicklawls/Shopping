package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class PurchaseActivity extends Activity {
    ArrayList productnumbers = new ArrayList();
    ArrayList productnames = new ArrayList();
    ArrayList productprice = new ArrayList();
    ArrayList totalproductprice = new ArrayList();

    private TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_purchase);

        String uname = DataHolder.getInstance().getData();

        totalPrice = (TextView) findViewById(R.id.order_total_num);

        executeTotalCart(findViewById(R.id.order_total_num));


    }

    public void executeTotalCart(View view) {
        String uname = DataHolder.getInstance().getData();

        getTotalCart task = new getTotalCart(uname);
        task.execute((Void) null);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

            inflater.inflate(R.menu.menu_search, menu);



        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frame_container, SearchFragment.newInstance(s));
                ft.addToBackStack(null);
                ft.commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_cart:
                // TO GO TO CART FRAGMENT!!!!!!!
                if (DataHolder.getInstance().isLoggedIn()) {
                    Intent i = new Intent(PurchaseActivity.this, MainActivity.class);
                    startActivity(i);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, new CartFragment()).commit();

                    setTitle("Cart");
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Must be logged in to view Cart",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            case R.id.action_buy:
                if (DataHolder.getInstance().isLoggedIn()) {
                    Intent i = new Intent(PurchaseActivity.this, PurchaseActivity.class);
                    startActivity(i);
                    setTitle("Checkout");
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Must be logged in to view Checkout",
                            Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void executePurchase(View view) {
        String uname = DataHolder.getInstance().getData();

        PurchaseTask task = new PurchaseTask(uname);
        task.execute((Void) null);
        Toast.makeText(getApplicationContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(PurchaseActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }

    public class PurchaseTask extends AsyncTask<Void, Void, Void> {
        private final String username;

        PurchaseTask(String uname) {
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
                String sql;

                if (DataHolder.getInstance().isLoggedIn()) {
                    // insert into purchased
                    sql = "INSERT INTO purchased (username, product_no) SELECT username, product_no FROM shopping_cart WHERE username = '";
                    sql += username + "'";
                    st.executeUpdate(sql);

                    // delete from shopping_cart
                    sql = "DELETE FROM shopping_cart WHERE username = '" + username + "'";
                    st.executeUpdate(sql);
                }

                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class getTotalCart extends AsyncTask<Void, Void, Bundle> {
        private final String username;

        getTotalCart(String uname) {
            username = uname;
        }

        protected Bundle doInBackground(Void... params) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            productnumbers.clear();
            productnames.clear();
            productprice.clear();
            totalproductprice.clear();
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
                    product.putString("product_name", rs.getString("name"));
                    product.putString("product_price", rs.getString("price"));
                    product.putString("product_no",rs.getString("product_no"));

                }

                sql = "SELECT sum(p.price) FROM products p, shopping_cart sc WHERE username = '" + username;
                sql += "' AND p.product_no = sc.product_no";

                rs = st.executeQuery(sql);
                rs.next();

                product.putString("total_price",rs.getString("sum"));

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

            totalPrice.setText(product.getString("total_price",""));

        }
    }

}
