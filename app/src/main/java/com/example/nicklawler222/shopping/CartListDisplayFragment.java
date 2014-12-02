package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CartListDisplayFragment extends Fragment implements AbsListView.OnItemClickListener {
    private List CartItemList;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static CartListDisplayFragment newInstance(ArrayList productnumbers, ArrayList productnames, ArrayList productprice, ArrayList totalproductprice) {
        CartListDisplayFragment fragment = new CartListDisplayFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("product_numbers",productnumbers);
        args.putStringArrayList("product_names",productnames);
        args.putStringArrayList("product_price",productprice);
        args.putStringArrayList("total_product_price",totalproductprice);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CartListDisplayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        ArrayList productnames = args.getStringArrayList("product_names");
        ArrayList productnumbers = args.getStringArrayList("product_numbers");
        ArrayList productprice = args.getStringArrayList("product_price");
        ArrayList totalproductprice = args.getStringArrayList("total_product_price");

        CartItemList = new ArrayList();
        for (int i = 0; i < productnames.size(); i++) {
            CartItemList.add(new CartListItem(productnames.get(i).toString(),productnumbers.get(i).toString(), productprice.toString(), totalproductprice.toString()));
        }
        mAdapter = new CartListAdapter(getActivity(), CartItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CartListItem item = (CartListItem) this.CartItemList.get(position);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.frame_container,ProductFragment.newInstance(item.getItemNumber().toString()));
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
        public void onFragmentInteraction(String id);
    }

}
