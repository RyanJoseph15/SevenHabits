package com.minisoftwareandgames.ryan.sevenhabits.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantFragment;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ryan on 12/18/15.
 */
public class ModifyQuadrantDialog extends DialogFragment {

    /* from CHART VIEW */

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
        String[] thing = getActivity().getResources().getStringArray(R.array.quadrants);
        ArrayList<String> quadrants = new ArrayList<>(Arrays.asList(thing));
        changeQuadrant.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.list_view_item_dark, quadrants));
        changeQuadrant.setSelection(fragment.getQuadrant() - 1);    // 1 - 4 converted to base 0

        final Spinner changeTitle = (Spinner) view.findViewById(R.id.change_title);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        ArrayList<String> titles = Utilities.getElements(sharedPreferences, Utilities.TITLES);
        titles.remove(0);   // remove summary
        changeTitle.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.list_view_item_dark, titles));
        changeTitle.setSelection(titles.indexOf(fragment.getParentTitle()));

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
                                        changeTitle.getSelectedItem().toString(),
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
