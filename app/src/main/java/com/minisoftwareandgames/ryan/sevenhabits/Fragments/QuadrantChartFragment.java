package com.minisoftwareandgames.ryan.sevenhabits.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.minisoftwareandgames.ryan.sevenhabits.ChartListAdapter;
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

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String title;

    private ListView chartListView;
    private LinearLayout chartView;

    private Button quadrant1;
    private Button quadrant2;
    private Button quadrant3;
    private Button quadrant4;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch_view) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    Utilities.SEVENHABITS, Context.MODE_PRIVATE);
            String title = item.getTitle().toString();
            String[] options = {getResources().getString(R.string.chart_view),
                    getResources().getString(R.string.chart_list_view)};
            if (title.equals(options[0])) {
                // un-hide LinearLayout
                item.setTitle(options[1]);
                chartView.setVisibility(View.VISIBLE);
                sharedPreferences.edit().putBoolean(MainActivity.DISPLAYCHART, true).apply();
            } else {
                // hide LinearLayout
                item.setTitle(options[0]);
                chartView.setVisibility(View.GONE);
                SQLiteHelper helper = new SQLiteHelper(getActivity());
                ArrayList<QuadrantDetail> elements = helper.getDetails(
                        getTitle(), Utilities.QUADRANT.ALL);
                ChartListAdapter mAdapter = new ChartListAdapter(
                        getActivity().getApplicationContext(),
                        elements);
                chartListView.setAdapter(mAdapter);
                sharedPreferences.edit().putBoolean(MainActivity.DISPLAYCHART, false).apply();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}
