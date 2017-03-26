package com.android.soloud.training;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.soloud.R;
import com.android.soloud.adapters.GalleryAdapter;
import com.android.soloud.models.Training;
import com.android.soloud.utils.GridViewSquareItem;

import java.util.ArrayList;

/**
 * Created by f.stamopoulos on 26/3/2017.
 */

public class TrainingFragment extends Fragment implements TrainingContract.View {

    private GridView gridView;
    private TrainingContract.UserActionsListener mActionsListener;
    private TrainingAdapter trainingAdapter;

    public TrainingFragment(){
        // Empty Costructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionsListener = new TrainingPresenter(this);

        ArrayList<Training> trainingArrayList = new ArrayList<>();
        trainingArrayList.add(new Training("Beginner", R.drawable.bazo, 100));
        trainingArrayList.add(new Training("Intermediate", R.drawable.bazo1, 200));
        trainingArrayList.add(new Training("Pro", R.drawable.bazo, 300));


        trainingAdapter = new TrainingAdapter(trainingArrayList);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.training_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (GridView) view.findViewById(R.id.training_grid_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        gridView.setAdapter(trainingAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showFullScreenImageDialog(mThumbIds[position].toString());
                //((MaterialNavigationDrawer)getActivity()).setFragmentChild(new GalleryPostDetailsFragment(), "Post Info");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    /**
     * Listener for clicks on notes in the ListView.
     */
    TrainingItemListener mItemListener = new TrainingItemListener() {

        @Override
        public void onTrainingClick(Training clickedTraining) {

        }
    };

    @Override
    public void showTrainings(ArrayList<Training> trainingArrayList) {

    }

    @Override
    public void showTrainingDetails(@NonNull Training training) {

    }


    public class TrainingAdapter extends BaseAdapter {

        /*private Context context;
        private Integer[] imageIdArray;*/
        private ArrayList<Training> mTrainingArrayList;

        public TrainingAdapter(ArrayList<Training> trainingArrayList){

            this.mTrainingArrayList = trainingArrayList;
        }

        @Override
        public int getCount() {
            return mTrainingArrayList.size();
        }

        @Override
        public Training getItem(int position) {
            return mTrainingArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {

                //grid = new View(context);
                convertView = inflater.inflate(R.layout.training_item, null);
                /*GridViewSquareItem imageView = (GridViewSquareItem)grid.findViewById(R.id.square_item_image);
                imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());*/
            } /*else {
                grid = (View) convertView;
            }*/
            ImageView imageView = (ImageView) convertView.findViewById(R.id.training_IV);
            TextView textView = (TextView) convertView.findViewById(R.id.training_title_TV);
            imageView.setImageResource(mTrainingArrayList.get(position).getImageResourceId());
            textView.setText(mTrainingArrayList.get(position).getTitle());


            return convertView;
        }
    }

    public interface TrainingItemListener {

        void onTrainingClick(Training clickedTraining);
    }

}
