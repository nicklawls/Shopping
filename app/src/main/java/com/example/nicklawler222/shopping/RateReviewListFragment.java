package com.example.nicklawler222.shopping;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import com.example.nicklawler222.shopping.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */

public class RateReviewListFragment extends Fragment {

    private List RateReviewListItemList;

    private String mParam1;
    private String mParam2;

    private AbsListView RRListView;
    private OnFragmentInteractionListener RRListener;
    private ListAdapter mAdapter;

    public static RateReviewListFragment newInstance(ArrayList users, ArrayList ratings, ArrayList reviews) {
        RateReviewListFragment fragment = new RateReviewListFragment();
        Bundle args = new Bundle();

        args.putStringArrayList("array_users", users);
        args.putStringArrayList("array_ratings", ratings);
        args.putStringArrayList("array_reviews", reviews);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RateReviewListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* NOT SURE IF THIS PART IS RIGHT...
        Bundle args = getArguments();
        ArrayList user_names = args.getStringArrayList("array_users");
        ArrayList user_ratings = args.getStringArrayList("array_ratings");
        ArrayList user_reviews = args.getStringArrayList("array_reviews");

        RateReviewListItemList = new ArrayList();
        for (int i = 0; i < user_names.size(); i++) {
            RateReviewListItemList.add(new RateReviewListItem(user_names.get(i).toString(),user_ratings.get(i).toString(), user_reviews.get(i).toString()));
        }
        mAdapter = new RateReviewListAdapter(getActivity(), RateReviewListItemList);**/

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//        // TODO: Change Adapter to display your content
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_review_list, container, false);

        // Set the adapter
       // RRListView = (AbsListView) view.findViewById(android.R.id.list);
        //((AdapterView<ListAdapter>) RRListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
       //RRListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RRListener = null;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
