package com.android.soloud.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.android.soloud.R;
import com.android.soloud.utils.ImageHelper;
import com.squareup.picasso.Picasso;

/**
 * Created by f.stamopoulos on 24/1/2017.
 */

public class ImagePreviewDialog extends DialogFragment {

    public static ImagePreviewDialog newInstance(String photoUri) {
        Bundle args = new Bundle();
        args.putString("photoUri", photoUri);
        ImagePreviewDialog fragment = new ImagePreviewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String photoUri = getArguments().getString("photoUri");

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.image_preview_dialog, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imagePreview_IV);

        if (photoUri != null){

            ImageHelper imageHelper = new ImageHelper(getActivity());
            Bitmap resizedImage = imageHelper.getResizedImage(imageHelper.getBitmapFromUri(Uri.parse(photoUri)));
            imageView.setImageBitmap(resizedImage);

            /*Picasso.with(getActivity()).load(photoUri).placeholder(R.drawable.ic_account_circle_white_24dp).
                    error(R.drawable.ic_account_circle_white_24dp).into(imageView);*/
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

 /*   @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }*/


}
