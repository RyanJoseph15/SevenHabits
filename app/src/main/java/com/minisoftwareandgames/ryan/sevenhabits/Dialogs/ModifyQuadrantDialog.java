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
import android.widget.Spinner;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantFragment;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

/**
 * Created by ryan on 12/18/15.
 */
public class ModifyQuadrantDialog extends DialogFragment {

    private QuadrantFragment fragment;
    private int position;

    public ModifyQuadrantDialog() {}
    public static ModifyQuadrantDialog newInstance(QuadrantFragment fragment, int position) {
        ModifyQuadrantDialog modifyQuadrantDialog = new ModifyQuadrantDialog();
        modifyQuadrantDialog.fragment = fragment;
        modifyQuadrantDialog.position = position;
        return modifyQuadrantDialog;
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_edit_task_info, null);

        SQLiteHelper helper = new SQLiteHelper(getActivity());
        final EditText mTitle = (EditText) view.findViewById(R.id.task_entered);
        mTitle.setText(helper.getDetails(fragment.getParentTitle(), Utilities.q2Q(fragment.getQuadrant())).get(position).getDetails());
        final Spinner changeQuadrant = (Spinner) view.findViewById(R.id.change_quadrant);
        changeQuadrant.setSelection(fragment.getQuadrant() - 1);    // array is 0 base but quadrants start at 1
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
                                        mTitle.getText().toString().trim(),
                                        changeQuadrant.getSelectedItemPosition() + 1);
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
