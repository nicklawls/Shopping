package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class PurchaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

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

        return super.onCreateOptionsMenu(menu);
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
                    finish();
                    setTitle("Checkout");
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Must be logged in to view Checkout",
                            Toast.LENGTH_SHORT).show();

                    return false;
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


}
