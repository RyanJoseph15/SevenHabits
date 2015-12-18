package com.minisoftwareandgames.ryan.sevenhabits.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.NavigationDrawerFragment;
import com.minisoftwareandgames.ryan.sevenhabits.R;

/**
 * Created by ryan on 12/18/15.
 */
public class NewChartInfoDialog extends DialogFragment {

    private NavigationDrawerFragment fragment;

    public NewChartInfoDialog() {}
    public static NewChartInfoDialog newInstance(NavigationDrawerFragment fragment) {
        NewChartInfoDialog newChartInfoDialog = new NewChartInfoDialog();
        newChartInfoDialog.fragment = fragment;
        return newChartInfoDialog;
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_new_chart_info, null);

        final EditText mTitle = (EditText) view.findViewById(R.id.title_entered);
        builder.setView(view)
            .setPositiveButton("okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            fragment.NewChartInfoDialogCallback(mTitle.getText().toString().trim());
                            getDialog().dismiss();
                        }
                    })
            .setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            getDialog().cancel();
                        }
                    });
        return builder.create();
    }

}
