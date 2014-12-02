package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CartListAdapter extends ArrayAdapter {
    private Context context;
    private boolean useList = true;
    public CartListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    } /** * Holder for the list items. */

    private class ViewHolder{ TextView titleText; TextView itemPrice; TextView itemTotalPrice;} /** * * @param position * @param convertView * @param parent * @return */

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        CartListItem item = (CartListItem) getItem(position);
        View viewToUse = null; // This block exists to inflate the settings list item conditionally based on whether // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.fragment_cart_list, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) viewToUse.findViewById(R.id.titleTextView);
            holder.itemPrice = (TextView) viewToUse.findViewById(R.id.itemPriceView);
            holder.itemTotalPrice = (TextView) viewToUse.findViewById(R.id.totalPriceView);
            viewToUse.setTag(holder);
        }
        else {
            viewToUse = convertView; holder = (ViewHolder) viewToUse.getTag();
        }
        holder.titleText.setText(item.getItemTitle());
        holder.itemPrice.setText(item.getItemPrice());
        holder.itemTotalPrice.setText(item.getTotalItemPrice());

        viewToUse.setTag(holder);
        Button cart_view = (Button) viewToUse.findViewById(R.id.cart_view_button);
        cart_view.setOnClickListener(mClickListener);
        cart_view.setTag(item.getItemNumber());

        return viewToUse;
    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.cart_view_button :
                    // btn2 clicked
                    FragmentManager manager = ((Activity)context).getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame_container,ProductFragment.newInstance(v.getTag().toString()));
                    ft.addToBackStack(null);
                    ft.commit();
                    break;
            }
        }
    };


    

}