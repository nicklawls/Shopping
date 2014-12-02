package com.example.nicklawler222.shopping;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
public class WishListAdapter extends ArrayAdapter {
    private Context context;
    private boolean useList = true;
    public WishListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    } /** * Holder for the list items. */

    private class ViewHolder{ TextView titleText; } /** * * @param position * @param convertView * @param parent * @return */

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        WishListItem item = (WishListItem) getItem(position);
        View viewToUse = null; // This block exists to inflate the settings list item conditionally based on whether // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.fragment_wish_list, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) viewToUse.findViewById(R.id.titleTextView);
            viewToUse.setTag(holder);
        }

        else {
            viewToUse = convertView; holder = (ViewHolder) viewToUse.getTag();
        }
        holder.titleText.setText(item.getItemTitle());
        Button wish_buy = (Button) viewToUse.findViewById(R.id.wish_buy_button);
        Button wish_view = (Button) viewToUse.findViewById(R.id.wish_view_button);
        wish_buy.setOnClickListener(mClickListener);
        wish_buy.setTag(item.getItemNumber());
        wish_view.setOnClickListener(mClickListener);
        wish_view.setTag(item.getItemNumber());

        return viewToUse;
    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.wish_buy_button :
                    // btn clicked
                    String pno = v.getTag().toString();
                    String uname = DataHolder.getInstance().getData();
                    AddToSomethingTask addToCart = new AddToSomethingTask("shopping_cart", pno, uname);
                    addToCart.execute((Void) null);
                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.wish_view_button :
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

                if (username != "default") {
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

}