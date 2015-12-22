package com.minisoftwareandgames.ryan.sevenhabits.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantChartFragment;
import com.minisoftwareandgames.ryan.sevenhabits.QuadrantDetail;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

import java.util.ArrayList;

/**
 * Created by ryan on 12/22/15.
 */
public class ModifyQuadrantListViewItemDialog extends DialogFragment {

    private QuadrantChartFragment fragment;
    private int position;
    private ArrayList<QuadrantDetail> elements;

    public ModifyQuadrantListViewItemDialog() {}
    public static ModifyQuadrantListViewItemDialog newInstance(
            QuadrantChartFragment fragment,
            ArrayList<QuadrantDetail> elements,
            int position) {
        ModifyQuadrantListViewItemDialog dialog = new ModifyQuadrantListViewItemDialog();
        dialog.fragment = fragment;
        dialog.elements = elements;
        dialog.position = position;
        return dialog;
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
        final QuadrantDetail quadrantDetail = elements.get(position);
        mTitle.setText(quadrantDetail.getDetails());
        final Spinner changeQuadrant = (Spinner) view.findViewById(R.id.change_quadrant);
        changeQuadrant.setSelection(quadrantDetail.getQuadrant() - 1);    // array is 0 base but quadrants start at 1
        builder.setView(view)
                .setNeutralButton("delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.ModifyQuadrantDialogDeleteCallback(
                                        quadrantDetail.getQuadrant(),
                                        position);
                                getDialog().dismiss();
                            }
                        })
                .setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                fragment.ModifyQuadrantDialogEditCallback(
                                        quadrantDetail.getQuadrant(),
                                        position,
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
