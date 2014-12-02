package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nicklawler222.shopping.R;
import com.example.nicklawler222.shopping.RateReviewListItem;
import com.example.nicklawler222.shopping.SearchListItem;

import java.util.List;

public class RateReviewListAdapter extends ArrayAdapter{

    private Context context;
    private boolean useList = true;
    public RateReviewListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    } /** * Holder for the list items. */

    private class ViewHolder{
        TextView userText;
        TextView ratingText;
        TextView reviewText;
        RatingBar listRating;
        float float_list_rating;
    } /** * * @param position * @param convertView * @param parent * @return */

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        RateReviewListItem item = (RateReviewListItem) getItem(position);   // iffy about this part***
        View viewToUse = null; // This block exists to inflate the settings list item conditionally based on whether // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
                viewToUse = mInflater.inflate(R.layout.fragment_rate_review_list, null);
            holder = new ViewHolder();

            // userTextView, ratingTextView & reviewTextView are ids from the fragment_rate_review_list xml
            holder.userText = (TextView)viewToUse.findViewById(R.id.userTextView);
            holder.ratingText = (TextView)viewToUse.findViewById(R.id.ratingTextView);
            holder.reviewText = (TextView)viewToUse.findViewById(R.id.reviewTextView);
            holder.listRating = (RatingBar)viewToUse.findViewById(R.id.user_rating);

            viewToUse.setTag(holder);
        }
        else {
            viewToUse = convertView; holder = (ViewHolder) viewToUse.getTag();
        }

        holder.userText.setText(item.getUserText());
        holder.ratingText.setText(item.getRatingText());
        holder.reviewText.setText(item.getReviewText());

        // Deals with Displaying User's Rating Stars
        if(holder.ratingText.getText().toString() == "0.0")
            holder.listRating.setRating(0);
        else
            holder.listRating.setRating(Float.valueOf(holder.ratingText.getText().toString()));
        //holder.float_list_rating = holder.listRating.getRating();

        return viewToUse;
    }
}
