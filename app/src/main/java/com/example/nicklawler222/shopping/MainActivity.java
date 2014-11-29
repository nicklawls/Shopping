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
import android.os.Bundle;
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
        if(username.equals("default")){
            text = "Welcome to our Shoppe Guest!";
        }
        else {
            text = "Welcome to back to Le Shoppe " + username.substring(0,1).toUpperCase() + username.substring(1) + "!";
        }

        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();

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

        for( int i = 0; i < navMenuTitles.length; ++i) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i,-1)));
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
     * */
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
                ft.replace(R.id.frame_container,SearchFragment.newInstance(s));
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
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment cart = new CartFragment();
                ft.replace(R.id.frame_container,cart);
                ft.addToBackStack(null);
                ft.commit();
                return false;
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
     * */
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
                fragment = new BrowseHistoryFragment();
                break;
            case 8:
                fragment = new SearchHistoryFragment();
                break;
            case 9:
                Intent temp = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(temp);
                break;
            case 10:
                if (DataHolder.getInstance().getData() == "default") { // if no one's logged in
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    fragment = new HomeFragment();
                } else {
                    Toast.makeText(getApplicationContext(), "You're already logged in!",
                                   Toast.LENGTH_SHORT).show();
                }

                break;
            case 11:
                if (DataHolder.getInstance().getData() != "default") { // if logged in
                    DataHolder.getInstance().setData("default"); // log that nigga out
                    // only changing the intent to change the name at the top, kind of a hack...
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(i);
                    fragment = new HomeFragment();
                } else {
                    Toast.makeText(getApplicationContext(), "You're already Logged out!",
                                   Toast.LENGTH_SHORT).show();
                }
                break;
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
        if (DataHolder.getInstance().getData() != "default") {
            Intent i = new Intent(MainActivity.this, RateReviewActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Must Be Logged In To Rate/Review",
                    Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "postexecute",   Toast.LENGTH_LONG).show();
    }


    public void addToCart(View view) {
        // CURRENTLY ADD TO CART GOES TO RECENTLY VIEWED FRAGMENT
//        FragmentManager manager = getFragmentManager();
//        FragmentTransaction ft = manager.beginTransaction();
//        Fragment cart = new CartFragment();
//        ft.replace(R.id.frame_container,cart);
//        ft.addToBackStack(null);
//        ft.commit();


        Context context = getApplicationContext();
        CharSequence text = "ADDED TO CART";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

//    public void insertReview(View view) {
//
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFouhttps://github.com/jgrana/CreditCardEntryndException e) {
//            e.printStackTrace();
//        }
//        String url;
//        url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
//        Connection conn;
//        try {
//            DriverManager.setLoginTimeout(5);
//            conn = DriverManager.getConnection(url);
//            Statement st = conn.createStatement();
//            String sql;
//            String username = "'" + DataHolder.getInstance().getData() + "'";
//            String sqlrating = getApplicationContext().getRatingValue();
//            String product = DataHolder.getInstance().getPNO();
//
//            String sql2 = "SELECT * FROM users";
//            //Toast.makeText(getApplicationContext(), "running",   Toast.LENGTH_LONG).show();
//            ResultSet rs = st.executeQuery(sql2);
////                    while( rs.next() ){
////                        String tmp;
////                        tmp = rs.getString("password");
////                        Toast.makeText(getApplicationContext(), tmp,   Toast.LENGTH_LONG).show();
////                    }
//            //sql = "INSERT INTO ratings VALUES (" + product + "," + username + "," + sqlrating + ",'" + str + "')";
//            //Toast.makeText(getApplicationContext(), sql,   Toast.LENGTH_LONG).show();
//
//
//            //st.executeUpdate(sql);//p_no, usernmae, rating, review
//            //rs.close();
//            rs.close();
//            st.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//
//    }



