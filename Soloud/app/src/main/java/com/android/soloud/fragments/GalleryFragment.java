package com.android.soloud.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.adapters.GalleryAdapter;
import com.android.soloud.dialogs.ImagePreviewDialog;
import com.android.soloud.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by f.stamopoulos on 5/2/2017.
 */

public class GalleryFragment extends Fragment {

    private GridView gridView;
    private TextView postsCounter_TV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (GridView) view.findViewById(R.id.gridview);
        postsCounter_TV = (TextView) view.findViewById(R.id.posts_counter_TV);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Integer[] mThumbIds = {
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
                R.drawable.bazo, R.drawable.bazo1,
        };

        GalleryAdapter galleryAdapter = new GalleryAdapter(getActivity(), mThumbIds);
        gridView.setAdapter(galleryAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showFullScreenImageDialog(mThumbIds[position].toString());
                ((MaterialNavigationDrawer)getActivity()).setFragmentChild(new GalleryPostDetailsFragment(), "Post Info");
            }
        });

        String countMessage = galleryAdapter.getCount() + " Posts";
        postsCounter_TV.setText(countMessage);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void showFullScreenImageDialog(String resId){
        DialogFragment dialogFragment = ImagePreviewDialog.newInstance(resId);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"imagePreview");
    }
}
