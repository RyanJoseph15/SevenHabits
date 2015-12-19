package com.minisoftwareandgames.ryan.sevenhabits.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.NavigationDrawerFragment;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantFragment;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

/**
 * Created by ryan on 12/18/15.
 */
public class ModifyTitleDialog extends DialogFragment {

    private NavigationDrawerFragment fragment;
    private int position;

    public ModifyTitleDialog() {}
    public static ModifyTitleDialog newInstance(NavigationDrawerFragment fragment, int position) {
        ModifyTitleDialog modifyTitleDialog = new ModifyTitleDialog();
        modifyTitleDialog.fragment = fragment;
        modifyTitleDialog.position = position;
        return modifyTitleDialog;
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_edit_chart_info, null);

        final EditText mTitle = (EditText) view.findViewById(R.id.task_entered);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        mTitle.setText(Utilities.getElement(sharedPreferences, position, Utilities.TITLES));
        builder.setView(view)
                .setNeutralButton("delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.ModifyQuadrantDialogDeleteCallback(position);
                                getDialog().dismiss();
                            }
                        })
                .setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.ModifyQuadrantDialogEditCallback(position,
                                        mTitle.getText().toString().trim());
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
