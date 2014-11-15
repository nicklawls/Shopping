package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.transform.Result;


public class RateReviewActivity extends Activity {

    private RatingBar ratingBar;
    private static TextView ratingValue;             // THIS IS WHERE THE RATING IS STORED
    private static TextView textReview;
    private Button submitButton;
    private Button cancelButton;
    private static String str;                        // THIS IS WHERE THE STRING IS STORED


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_review);

        addListenerOnRatingBar();
        addListenerOnButton();

    }

    public void addListenerOnButton() {
        ratingBar = (RatingBar) findViewById(R.id.rating);
        submitButton = (Button) findViewById(R.id.submit_review);
        cancelButton = (Button) findViewById(R.id.cancel_review);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textReview = (EditText) findViewById(R.id.review_post);
                str = textReview.getText().toString();
                if( ratingValue.getText().equals("")) return;

                String sql;
                String username = DataHolder.getInstance().getData();//////PRODUCT_NO,USERNAME,RATING<REVIEW
                String sqlrating = ratingValue.getText().toString();
                String product = DataHolder.getInstance().getPNO();
                String sqlreview = str;
                sql = "INSERT INTO ratings VALUES (" + product + ", '" + username + "', " +sqlrating + ", '" + sqlreview + "')";
                new FetchSQL().execute(sql, product, username, sqlrating, sqlreview );
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateReviewActivity.this.finish();
            }
        });
    }

    private class FetchSQL extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... sqldata ) {
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
                String sqltest;
                sqltest = "SELECT username FROM ratings WHERE product_no = " + sqldata[1] + " AND username = '" + sqldata[2] + "'";
                ResultSet rtest = st.executeQuery(sqltest);
                boolean already_sumbitted = false;
                while( rtest.next() ) {already_sumbitted = true; }
                String query;
                if( already_sumbitted ) {
                    query = "UPDATE ratings SET rating = " + sqldata[3] + ", review = '"
                            + sqldata[4] + "' WHERE product_no = " + sqldata[1] + " AND username = '" + sqldata[2] + "'";
                }
                else {
                    query = sqldata[0];
                }

                st.execute(query);

                rtest.close();
                st.close();
                conn.close();

                RateReviewActivity.this.finish();
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Void product) {
        }
    }

    public void addListenerOnRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.rating);
        ratingValue = (TextView) findViewById(R.id.rating_value);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue.setText(String.valueOf(rating));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
