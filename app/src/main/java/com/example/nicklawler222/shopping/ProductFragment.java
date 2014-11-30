package com.example.nicklawler222.shopping;

import com.androidquery.AQuery;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.Date;

import java.sql.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFragment.OnFragmentInteractionListener} interface
 * to handle   interaction events.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductFragment extends Fragment {
    TextView productrating;
    TextView productname;
    TextView productcategory;
    TextView productprice;
    TextView productdescription;
    TextView productfeatures;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AQuery aq;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productNumber Parameter 1.
     * @return A new instance of fragment ProductFragment.
     */
    public static ProductFragment newInstance(String productNumber) {
        ProductFragment fragment = new ProductFragment();

        // TODO: Figure out how to only call this once
        DataHolder.getInstance().setPNO(productNumber);

        Bundle args = new Bundle();
        args.putString("product_number", productNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        String productnumber = args.getString("product_number","");

        // TODO: Figure out how to only call this once
        DataHolder.getInstance().setPNO(productnumber);


        View rootView = inflater.inflate(R.layout.product_fragment, container, false);
        aq = new AQuery(getActivity(), rootView);
        productrating = (TextView) rootView.findViewById(R.id.productrating);

        productname = (TextView) rootView.findViewById(R.id.productname);

        productcategory = (TextView) rootView.findViewById(R.id.productcategory);

        productprice = (TextView) rootView.findViewById(R.id.productprice);

        productdescription = (TextView) rootView.findViewById(R.id.productdescription);

        productfeatures = (TextView) rootView.findViewById(R.id.productfeatures);

        new FetchSQL().execute(productnumber);

        // only logged in users can view rate review, add to cart, and add to wish list button
        Button addtoCart = (Button) rootView.findViewById(R.id.addToCartButton);
        Button addtoWish = (Button) rootView.findViewById(R.id.addToWishListbutton);
        Button rate = (Button) rootView.findViewById(R.id.rate_review);

        if(!DataHolder.getInstance().isLoggedIn()){
            addtoCart.setVisibility(View.GONE);
            addtoWish.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
        }
        else
        {
            addtoCart.setVisibility(View.VISIBLE);
            addtoWish.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
        }

        return rootView;
    }



    private class FetchSQL extends AsyncTask<String,Void,Bundle> {
        protected Bundle doInBackground(String... productnumbers) {
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

                String sql_store_browse_history;
                String username = DataHolder.getInstance().getData();

                if (username != "default") {
                    username = "'" + username + "'";
                    java.util.Date date = new Date();
                    String timestamp = (new Timestamp(date.getTime())).toString();
                    sql_store_browse_history = "INSERT INTO browsings VALUES (";
                    sql_store_browse_history += username + ", '" + productnumbers[0] + "', '" + timestamp + "')";
                    int update_result = st.executeUpdate(sql_store_browse_history);
                }

                String average_rating_query;
                average_rating_query = "SELECT round(avg(rating),1) FROM ratings WHERE product_no =";
                average_rating_query += "'" + productnumbers[0] + "'";
                ResultSet rating_rs = st.executeQuery(average_rating_query);
                rating_rs.next(); // only need one invocation, assuming one row in ResultSet

                String rating;
                rating = rating_rs.getString("round");
                if (rating == "" || rating == null) {
                    rating = "No Ratings";
                }
                product.putString("product_avg_rating",rating);

                rating_rs.close();
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
            aq.id(R.id.imageView).image(product.getString("product_imgurl",""),false, true);
            productrating.setText(product.getString("product_avg_rating",""));
            productname.setText(product.getString("product_name",""));
            productcategory.setText(product.getString("product_category",""));
            productprice.setText(product.getString("product_price",""));
            productdescription.setText(product.getString("product_description",""));
            productfeatures.setText(product.getString("product_features",""));
        }


    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

}
