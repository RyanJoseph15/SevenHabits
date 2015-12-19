package com.minisoftwareandgames.ryan.sevenhabits.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.NewTaskDialog;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.R;

/**
 * Created by ryan on 12/18/15.
 */
public class QuadrantChartFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String title;

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
            Toast.makeText(getActivity(), "action_switch_view", Toast.LENGTH_SHORT).show();
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
