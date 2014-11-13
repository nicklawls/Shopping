package com.example.nicklawler222.shopping;

import android.app.Activity;
import java.sql.*;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class HomeActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,CategoryFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public void onFragmentInteraction(String id) {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        ArrayList productnumbers = new ArrayList();
        ArrayList productnames = new ArrayList();
        productnumbers.add("1");
        productnumbers.add("2");
        productnames.add("Shoes");
        productnames.add("Ford Mustang");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, CategoryFragment.newInstance(productnumbers,productnames))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class product_fragment extends Fragment {
    //private WeakReference<FetchSQL> asyncTaskWeakRef;


    TextView productno;
    TextView productname;
    TextView productcategory;
    TextView productprice;
    TextView productdescription;
    TextView productfeatures;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static product_fragment newInstance(int sectionNumber,int productNumber) {
            product_fragment fragment = new product_fragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("product_number",productNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public product_fragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            int productnumber = args.getInt("product_number",0);



            View rootView = inflater.inflate(R.layout.product_fragment, container, false);
            productno = (TextView) rootView.findViewById(R.id.productno);
            productno.setText(Integer.toString(productnumber));

            productname = (TextView) rootView.findViewById(R.id.productname);

            productcategory = (TextView) rootView.findViewById(R.id.productcategory);

            productprice = (TextView) rootView.findViewById(R.id.productprice);

            productdescription = (TextView) rootView.findViewById(R.id.productdescription);

            productfeatures = (TextView) rootView.findViewById(R.id.productfeatures);
            new FetchSQL().execute(productnumber);



            return rootView;
        }

        private class FetchSQL extends AsyncTask <Integer,Void,Bundle> {
            protected Bundle doInBackground(Integer... productnumbers) {
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Bundle product = new Bundle();
                String url;
                url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
                Connection conn;
                try {
                    DriverManager.setLoginTimeout(5);
                    conn = DriverManager.getConnection(url);
                    Statement st = conn.createStatement();
                    String sql;
                    sql = "SELECT * FROM products WHERE product_no = '" + productnumbers[0] + "'";
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        product.putString("product_name", rs.getString("name"));
                        product.putString("product_category", rs.getString("category"));
                        product.putString("product_price", rs.getString("price"));
                        product.putString("product_description", rs.getString("description"));
                        product.putString("product_features", rs.getString("features"));
                        product.putString("product_imgurl", rs.getString("img_url"));
                    }

                    rs.close();
                    st.close();
                    conn.close();
                    return product;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

                protected void onPostExecute(Bundle product) {
                    productname.setText(product.getString("product_name",""));
                    productcategory.setText(product.getString("product_category",""));
                    productprice.setText(product.getString("product_price",""));
                    productdescription.setText(product.getString("product_description",""));
                    productfeatures.setText(product.getString("product_features",""));

                }


            }






        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
