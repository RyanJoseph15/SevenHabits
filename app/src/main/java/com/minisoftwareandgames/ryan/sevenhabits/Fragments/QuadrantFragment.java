package com.minisoftwareandgames.ryan.sevenhabits.Fragments;

import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.ModifyQuadrantDialog;
import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.NewTaskDialog;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.QuadrantDetail;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

import java.util.ArrayList;

/**
 * Created by ryan on 12/18/15.
 */
public class QuadrantFragment extends Fragment {

    protected int quadrant = 0;                                           // values 1 - 4
    protected String details = null;
    protected ListView listView;
    protected ArrayAdapter<String> mAdapter;
    protected String parentTitle = null;
    protected String tag = null;

    public static QuadrantFragment newInstance(int quadrant, String parentTitle) {
        QuadrantFragment clFragment = new QuadrantFragment();
        clFragment.setQuadrant(quadrant);
        clFragment.setParentTitle(parentTitle);
        return clFragment;
    }

    public void setQuadrant(int quadrant) {
        this.quadrant = quadrant;
    }
    public int getQuadrant() {return this.quadrant;}
    public void setDetails(String details) {
        this.details = details;
    }
    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }
    public String getParentTitle() {return this.parentTitle;}

    public String getmTag() {return this.tag;}

    protected void setMyBackgroundColor(ListView view, int quadrant) {
        int color = -1;
        switch (quadrant) {
            case 1:
                color = R.color.green;
                break;
            case 2:
                color = R.color.blue;
                break;
            case 3:
                color = R.color.yellow;
                break;
            case 4:
                color = R.color.red;
                break;
        }
        view.setBackgroundColor(getResources().getColor(color));
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
        View view = inflater.inflate(R.layout.fragment_quadrant, container, false);
        listView = (ListView) view.findViewById(R.id.quadrant_list_view);
        setMyBackgroundColor(listView, quadrant);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModifyQuadrantDialog modifyQuadrantDialog = ModifyQuadrantDialog.newInstance(QuadrantFragment.this, position);
                modifyQuadrantDialog.show(getFragmentManager(), MainActivity.MODIFYQUADRANTTAG);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });
        tag = Utilities.TASKS + "_" + parentTitle + "_" + quadrant;
        //ArrayList<String> tasks = Utilities.getElements(getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE), tag);
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> details = helper.getDetails(parentTitle, Utilities.q2Q(quadrant));
        ArrayList<String> tasks = Utilities.QuadrantDetails2StringsArray(details);
        if (tasks == null) {
            tasks = new ArrayList<String>();
        }
        mAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                tasks);
        listView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).updateTitle(Utilities.quad2Title(quadrant));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_quadrant, menu);
        MenuItem switcher = menu.findItem(R.id.action_switch_view);
        if (switcher != null)
            switcher.setVisible(false);
        getActivity().invalidateOptionsMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            NewTaskDialog newTaskDialog = NewTaskDialog.newInstance(this);
            newTaskDialog.show(getFragmentManager(), MainActivity.NEWTASKTAG);
            return true;
        } else if (item.getItemId() == R.id.action_switch_view) {
            Toast.makeText(getActivity(), "action_switch_view", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateTitle(Utilities.quad2Title(quadrant));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).updateTitle(parentTitle);
    }

    @Override
    public void onDestroy() {
        Log.d("CHECK", "QuadrantChartFragment title: " + this.parentTitle);
        ((MainActivity) getActivity()).updateTitle(this.parentTitle);
        super.onDestroy();
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Callback methods
     * ----------------------------------------------------------------------------------- */

    public void NewTaskDialogCallback(String details) {
        setDetails(details);
        // add to the list of titles
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        helper.addEntry(parentTitle, quadrant, details);
        // update the view
        if (mAdapter.getCount() > 0) mAdapter.clear();
        mAdapter.addAll(Utilities.QuadrantDetails2StringsArray(helper.getDetails(parentTitle, Utilities.q2Q(quadrant))));
        mAdapter.notifyDataSetChanged();
        // notify data set changed for GONE list view
        QuadrantChartFragment quadrantChartFragment = (QuadrantChartFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(MainActivity.QUADRANTCHARTTAG);
        if (quadrantChartFragment != null && quadrantChartFragment.getmAdapter() != null) {
            // quadrantChartFragment should never be null - precautionary
            quadrantChartFragment.getmAdapter().add(QuadrantDetail.newInstance(parentTitle, quadrant, details));
        }
    }

    public void ModifyQuadrantDialogDeleteCallback(int position) {
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> details = helper.getDetails(parentTitle, Utilities.q2Q(quadrant));
        QuadrantDetail quadrantDetail = details.get(position);
        helper.removeEntry(parentTitle, quadrant, quadrantDetail.getDetails());
        // notify data set changed
        if (mAdapter.getCount() > 0) mAdapter.clear();
        details.remove(position);
        mAdapter.addAll(Utilities.QuadrantDetails2StringsArray(details));
        mAdapter.notifyDataSetChanged();
        // notify data set changed for GONE list view
        QuadrantChartFragment quadrantChartFragment = (QuadrantChartFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(MainActivity.QUADRANTCHARTTAG);
        if (quadrantChartFragment != null && quadrantChartFragment.getmAdapter() != null) {
            // quadrantChartFragment should never be null - precautionary
            quadrantChartFragment.getmAdapter().notifyDataSetChanged();
        }
    }

    public void ModifyQuadrantDialogEditCallback(int position, String newTitle, String newDetails, int quad) {
        // TODO: not getting updated in the view
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        ArrayList<QuadrantDetail> details = helper.getDetails(parentTitle, Utilities.q2Q(quadrant));
        QuadrantDetail quadrantDetail = details.get(position);
        String oldDetails = quadrantDetail.getDetails();
        helper.updateEntry(quadrantDetail.getTitle(), quadrant, oldDetails, newTitle, quad, newDetails);
        if (mAdapter.getCount() > 0) mAdapter.clear();
        details.remove(position);                                           // for updating view
        if (quad == quadrant && parentTitle != null && newTitle != null && parentTitle.equals(newTitle) || "Summary".equals(parentTitle)) {
            details.add(QuadrantDetail.newInstance(quadrantDetail.getTitle(), quadrant, newDetails));
        }
        mAdapter.addAll(Utilities.QuadrantDetails2StringsArray(details));
        mAdapter.notifyDataSetChanged();
        // notify data set changed for GONE list view
        QuadrantChartFragment quadrantChartFragment = (QuadrantChartFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentByTag(MainActivity.QUADRANTCHARTTAG);
        if (quadrantChartFragment != null && quadrantChartFragment.getmAdapter() != null) {
            // quadrantChartFragment should never be null - precautionary
            quadrantChartFragment.getmAdapter().add(QuadrantDetail.newInstance(
                    parentTitle, quadrant, newDetails));
        }
    }

}
