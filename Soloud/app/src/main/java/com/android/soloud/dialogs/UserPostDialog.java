package com.android.soloud.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.soloud.R;
import com.android.soloud.utils.SharedPrefsHelper;

/**
 * Created by f.stamopoulos on 13/11/2016.
 */

public class UserPostDialog extends android.support.v4.app.DialogFragment {

    public interface OnOkPressedListener{
        void onOkPressed();
    }

    private OnOkPressedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditNameDialogListener so we can send events to the host
            mListener = (OnOkPressedListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement EditNameDialogListener");
        }
    }

    public UserPostDialog() {
        // Empty constructor required for DialogFragment
    }


    public static UserPostDialog newInstance() {
        return new UserPostDialog();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.dialog_title_post_validation));
        alertDialogBuilder.setMessage(getString(R.string.dialog_description_post_validation));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok),  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPrefsHelper.storeBooleanInPrefs(getActivity(), true, SharedPrefsHelper.POST_POP_UP_DISPLAYED);
                mListener.onOkPressed();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
