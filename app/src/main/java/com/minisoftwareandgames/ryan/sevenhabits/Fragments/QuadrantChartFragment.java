package com.minisoftwareandgames.ryan.sevenhabits.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.minisoftwareandgames.ryan.sevenhabits.ChartListAdapter;
import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.ModifyQuadrantDialog;
import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.ModifyQuadrantListViewItemDialog;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.QuadrantDetail;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

import java.util.ArrayList;

/**
 * Created by ryan on 12/18/15.
 */
public class QuadrantChartFragment extends Fragment implements View.OnClickListener {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected String title;

    protected ListView chartListView;
    protected LinearLayout chartView;
    protected ChartListAdapter mAdapter;

    protected Button quadrant1;
    protected Button quadrant2;
    protected Button quadrant3;
    protected Button quadrant4;

    public static QuadrantChartFragment newInstance(int sectionNumber, String title) {
        QuadrantChartFragment qcFragment = new QuadrantChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        qcFragment.setArguments(args);
        qcFragment.setTitle(title);
        return qcFragment;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }
    public LinearLayout getChartView() {return this.chartView;}
    public ChartListAdapter getmAdapter() {return this.mAdapter;}
    public void updatemAdapterForNewList(String title) {
        if (mAdapter != null) {
            SQLiteHelper helper = new SQLiteHelper(getActivity());
            ArrayList<QuadrantDetail> elements = helper.getDetails(title, Utilities.QUADRANT.ALL);
            mAdapter.newList(elements);
        }
    }
    public ListView getChartListView() {return this.chartListView;}

    /* ------------------------------------------------------------------------------------ /
     *                                   Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quadrant_chart, container, false);
        (quadrant1 = (Button) view.findViewById(R.id.button)).setOnClickListener(this);
        (quadrant2 = (Button) view.findViewById(R.id.button2)).setOnClickListener(this);
        (quadrant3 = (Button) view.findViewById(R.id.button3)).setOnClickListener(this);
        (quadrant4 = (Button) view.findViewById(R.id.button4)).setOnClickListener(this);
        chartView = (LinearLayout) view.findViewById(R.id.chart_view);
        chartListView = (ListView) view.findViewById(R.id.chart_list_view);
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> elements = helper.getDetails(getTitle(), Utilities.QUADRANT.ALL);
        mAdapter = new ChartListAdapter(
                getActivity().getApplicationContext(),
                elements);
        chartListView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CHECK", "QuadrantChartFragment title: " + this.getTitle());
        ((MainActivity) getActivity()).updateTitle(this.getTitle());
    }

    /* ------------------------------------------------------------------------------------ /
     *                              Interface Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int quadrant = 1;                                               // default to most productive
        switch (v.getId()) {
            case R.id.button:
                quadrant = 1;
                break;
            case R.id.button2:
                quadrant = 2;
                break;
            case R.id.button3:
                quadrant = 3;
                break;
            case R.id.button4:
                quadrant = 4;
                break;
        }
        QuadrantFragment quadrantFragment;
        if (fragmentManager.findFragmentByTag(MainActivity.QUADRANTTAG) == null) {
            quadrantFragment = QuadrantFragment.newInstance(quadrant, this.title);
        } else {
            quadrantFragment = (QuadrantFragment) fragmentManager.findFragmentByTag(MainActivity.QUADRANTTAG);
            quadrantFragment.setParentTitle(this.title);
            quadrantFragment.setQuadrant(quadrant);
        }
        fragmentManager.beginTransaction()
                .add(R.id.container, quadrantFragment, MainActivity.QUADRANTTAG)
                .addToBackStack(null)
                .commit();
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Callback methods
     * ----------------------------------------------------------------------------------- */

//    public void NewTaskDialogCallback(int quadrant, String details) {
//        // add to the list of titles
//        SQLiteHelper helper = new SQLiteHelper(getActivity());
//        helper.addEntry(getTitle(), quadrant, details);
//        // update the view
//        if (mAdapter.getCount() > 0) mAdapter.clear();
//        mAdapter.addAll(Utilities.QuadrantDetails2StringsArray(helper.getDetails(parentTitle, Utilities.q2Q(quadrant))));
//        mAdapter.notifyDataSetChanged();
//    }

    public void ModifyQuadrantDialogEditCallback(int quadrant, int position, String newTitle, String newDetails, int quad) {
        // TODO: see commit comments
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> details = helper.getDetails(getTitle(), Utilities.QUADRANT.ALL);
        QuadrantDetail quadrantDetail = details.get(position);
        String oldDetails = quadrantDetail.getDetails();
        helper.updateEntry(getTitle(), quadrant, oldDetails, newTitle, quad, newDetails);
        if (mAdapter.getCount() > 0) mAdapter.clear();
        details.remove(position);                                           // for updating view
        if (quad == quadrant || "Summary".equals(newTitle)) details.add(QuadrantDetail.newInstance(
                quadrantDetail.getTitle(), quadrant, newDetails));
        mAdapter.addAll(details);
        mAdapter.notifyDataSetChanged();
    }

    public void ModifyQuadrantDialogDeleteCallback(int quadrant, int position) {
        // quadrant field necessary to remove from database
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> details = helper.getDetails(getTitle(), Utilities.QUADRANT.ALL);
        QuadrantDetail quadrantDetail = details.get(position);
        helper.removeEntry(getTitle(), quadrant, quadrantDetail.getDetails());
        // notify data set changed
        if (mAdapter.getCount() > 0) mAdapter.clear();
        details.remove(position);
        mAdapter.addAll(details);
        mAdapter.notifyDataSetChanged();
    }

}
