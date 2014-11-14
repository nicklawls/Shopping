package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


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
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateReviewActivity.this.finish();
            }
        });


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
        getMenuInflater().inflate(R.menu.menu_rate_review, menu);
        return true;
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
}
