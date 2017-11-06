package com.android.soloud.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.soloud.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 4/11/2017.
 */

public class ViewPagerTabListViewFragment extends Fragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        Activity parentActivity = getActivity();
        final ObservableListView listView = (ObservableListView) view.findViewById(R.id.scroll);
        setDummyDataWithHeader(listView, inflater.inflate(R.layout.padding, listView, false));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(listView, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        listView.setSelection(initialPosition);
                    }
                });
            }

            // TouchInterceptionViewGroup should be a parent view other than ViewPager.
            // This is a workaround for the issue #117:
            // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
            listView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.root));

            listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;

    }

    protected void setDummyDataWithHeader(ListView listView, View headerView) {
        listView.addHeaderView(headerView);
        setDummyData(listView);
    }

    protected void setDummyData(ListView listView) {
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getDummyData(20)));
    }

    public static ArrayList<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }
}
