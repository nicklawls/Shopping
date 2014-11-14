package com.example.nicklawler222.shopping;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchHistoryFragment extends Fragment {

    public SearchHistoryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);
        if (!DataHolder.getInstance().getHistory().isEmpty()) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container, SearchListFragment.newInstance(DataHolder.getInstance().getHistory()));
            ft.commit();
            rootView = null;
        }

        return rootView;
    }
}
