package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.nicklawler222.shopping.adapter.NavDrawerListAdapter;
import com.example.nicklawler222.shopping.model.NavDrawerItem;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CharSequence text;
        String username = DataHolder.getInstance().getData();
        if (username.equals("default")) {
            text = "Welcome to our Shoppe Guest!";
        } else {
            text = "Welcome to back to Le Shoppe " + username.substring(0, 1).toUpperCase() + username.substring(1) + "!";
        }

        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
        if(!username.equals("default")) {
            new getWishListNotifications().execute();
        }


        setContentView(R.layout.activity_main2);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        for (int i = 0; i < navMenuTitles.length; ++i) {
            if (i > 6) {
                if (DataHolder.getInstance().isLoggedIn()) {
                    if (navMenuTitles[i].equals("Login")) {
                        if (DataHolder.getInstance().isLoggedIn()) {
                            i = navMenuTitles.length - 1;
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                            break;
                        } else {
                            i = navMenuTitles.length - 2;
                            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                            break;
                        }
                    }
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                } else {
                    i = navMenuTitles.length - 2;
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                    break;
                }
            } else {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
            }
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }


    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
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
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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

//            case R.id.action_settings:
//                return true;
//            case R.id.action_search:
//                Intent intent = new Intent(Intent.ACTION_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new CarsFragment();
                break;
            case 2:
                fragment = new MensClothingFragment();
                break;
            case 3:
                fragment = new OfficeFragment();
                break;
            case 4:
                fragment = new PetsFragment();
                break;
            case 5:
                fragment = new VideoGamesFragment();
                break;
            case 6:
                fragment = new WomensClothingFragment();
                break;
            case 7:
                if (navDrawerItems.get(position).getTitle().equals("Login")) {
                    if (!DataHolder.getInstance().isLoggedIn()) { // if no one's logged in
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        fragment = new HomeFragment();
                    } else {
                        Toast.makeText(getApplicationContext(), "You're already logged in!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                } else {
                    fragment = new BrowseHistoryFragment();
                    break;
                }
            case 8:
                fragment = new SearchHistoryFragment();
                break;
            case 9:
                if (DataHolder.getInstance().isLoggedIn()) {
                    fragment = new RecommendationFragment();
                } else {
                    Toast.makeText(getApplicationContext(), "Must be logged in to view product recommendations",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 10:
                fragment = new WishListFragment();
                break;
            case 11:
                fragment = new CartFragment();
                break;
            case 12:
                if (navDrawerItems.get(position).getTitle().equals("Login")) {
                    if (!DataHolder.getInstance().isLoggedIn()) { // if no one's logged in
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        fragment = new HomeFragment();
                        break;
                    }
                } else if (navDrawerItems.get(position).getTitle().equals("Logout")) {
                    if (DataHolder.getInstance().isLoggedIn()) { // if logged in
                        DataHolder.getInstance().setData("default"); // log that nigga out
                        // only changing the intent to change the name at the top, kind of a hack...
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                        fragment = new HomeFragment();
                        Toast.makeText(getApplicationContext(), "Bye, come back another time!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You're already Logged out!",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void toRateReviewActivity(View view) {
        if (DataHolder.getInstance().isLoggedIn() && DataHolder.getInstance().hasPurchased()) {
            Intent i = new Intent(MainActivity.this, RateReviewActivity.class);
            startActivity(i);
        } else if (!DataHolder.getInstance().isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "Must Be Logged In To Rate/Review",
                    Toast.LENGTH_SHORT).show();
        } else if (!DataHolder.getInstance().hasPurchased()) {
            Toast.makeText(getApplicationContext(), "Must Have Purchased Item To Rate/Review",
                    Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(getApplicationContext(), "postexecute",   Toast.LENGTH_LONG).show();
    }

    public class AddToSomethingTask extends AsyncTask<Void, Void, Void> {
        private final String product_no;
        private final String username;
        private final String table;

        AddToSomethingTask(String tabl, String pno, String uname) {
            product_no = pno;
            username = uname;
            table = tabl;
        }

        protected Void doInBackground(Void... params) {

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
                    sql = "INSERT INTO " + table + " VALUES (";
                    sql += "'" + username + "', '" + product_no + "')";
                    int update_result = st.executeUpdate(sql);
                }

                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public class getWishListNotifications extends AsyncTask<Void, Void, String> {


        protected String doInBackground(Void... params) {

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url;
            url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
            Connection conn;
            String text = "";

            try {

                DriverManager.setLoginTimeout(5);
                conn = DriverManager.getConnection(url);
                Statement st = conn.createStatement();
                String sql;

                sql = "SELECT COUNT(*) FROM users u, products p, wish_list w WHERE p.last_update > u.last_login AND w.username = u.username AND w.product_no = p.product_no AND u.username = '" + DataHolder.getInstance().getData() + "'";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    text = rs.getString("count");
                }
                String update_login = "UPDATE users SET last_login = (now() at time zone 'utc') ";
                update_login += "WHERE username = '" + DataHolder.getInstance().getData() + "'";
                System.out.println(update_login);
                int result = st.executeUpdate(update_login);
                System.out.println(result);
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return text;
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (Integer.parseInt(result) > 1) {
                result += " items in your Wish List were updated.";
            }
            else if (Integer.parseInt(result) > 0) {
                result += " item in your Wish List was updated.";
            }
            else return;
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }

    }
    // Handles OnClick for "List of Ratings and Reviews" Button

    public void addToCart(View view) {
        String pno = DataHolder.getInstance().getPNO();
        String uname = DataHolder.getInstance().getData();


        AddToSomethingTask addToCart = new AddToSomethingTask("shopping_cart", pno, uname);
        addToCart.execute((Void) null);

        Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
    }

    public void addToWishList(View view) {
        String pno = DataHolder.getInstance().getPNO();
        String uname = DataHolder.getInstance().getData();


        Toast.makeText(getApplicationContext(), "Added to WishList", Toast.LENGTH_SHORT).show();

        AddToSomethingTask addToWishList = new AddToSomethingTask("wish_list", pno, uname);
        addToWishList.execute((Void) null);


        Toast.makeText(getApplicationContext(), "Added to WishList", Toast.LENGTH_SHORT).show();

    }

    public void executePurchase(View view) {

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
