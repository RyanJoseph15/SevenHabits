package com.minisoftwareandgames.ryan.sevenhabits;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.ModifyQuadrantListViewItemDialog;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.NavigationDrawerFragment;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantChartFragment;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantFragment;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.WelcomeFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private ProgressDialog pDialog;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private boolean doNotSwitch = true;

    public static final String QUADRANTCHARTTAG = "quadrantcharttag";
    public static final int progress_bar_type = 0;
    public static final String QUADRANTTAG = "quadranttag";
    public static final String NEWCHARTINFOTAG = "newchartinfotag";
    public static final String NEWTASKTAG = "newtasktag";
    public static final String MODIFYQUADRANTTAG = "modifyquadranttag";
    public static final String MODIFYTITLETAG = "modifytitletag";
    public static final String WELCOMETAG = "welcometag";
    public static final String DISPLAYCHART = "chart";

    public void updateTitle(String title) {
        mNavigationDrawerFragment.getActionBar().setTitle(title);
    }
    public void doNotSwitch(boolean value) {
        this.doNotSwitch = value;
    }
    public boolean doNotSwitch() {return this.doNotSwitch;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        SharedPreferences sharedPreferences = getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        mNavigationDrawerFragment.NewChartInfoDialogCallback("Summary");
        int selected = sharedPreferences.getInt("mCurrentSelectedPosition", -1);
        this.onNavigationDrawerItemSelected(selected);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading configuration files. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SharedPreferences sharedPreferences = getSharedPreferences(
                Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        Log.d("CHECK", "position: " + position);
        if (position < 0) {
            // TODO: welcome and tutorial page
            WelcomeFragment welcomeFragment;
            if (fragmentManager.findFragmentByTag(WELCOMETAG) == null) {
                welcomeFragment = WelcomeFragment.newInstance(position);
            } else {
                welcomeFragment = (WelcomeFragment) fragmentManager.findFragmentByTag(WELCOMETAG);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, welcomeFragment, WELCOMETAG)
                    .commit();
        } else {
            // chart fragment
            QuadrantChartFragment quadrantChartFragment;
            if (fragmentManager.findFragmentByTag(QUADRANTCHARTTAG) == null) {
                quadrantChartFragment = QuadrantChartFragment.newInstance(
                        position + 1,
                        Utilities.getElement(sharedPreferences,
                                position,
                                Utilities.TITLES));
            } else {
                quadrantChartFragment = (QuadrantChartFragment) fragmentManager
                        .findFragmentByTag(QUADRANTCHARTTAG);
                String title = Utilities.getElement(
                        sharedPreferences,
                        position,
                        Utilities.TITLES);
                Log.d("actions-title", title);
                quadrantChartFragment.setTitle(title);
                quadrantChartFragment.updatemAdapterForNewList(title);
            }
            fragmentManager.beginTransaction()
                .replace(R.id.container, quadrantChartFragment, QUADRANTCHARTTAG)
                .commit();
        }
        QuadrantFragment quadrantFragment = (QuadrantFragment) fragmentManager
                .findFragmentByTag(MainActivity.QUADRANTTAG);
        if (quadrantFragment != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(quadrantFragment)
                    .commit();
            fragmentManager.popBackStack();
        }
    }

    public void onSectionAttached(String title) {
        mTitle = title;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem switcher = menu.findItem(R.id.action_switch_view);

            // configure action text based on current value (gets reset with open/close NavBar)
            // gets configured here, selecting a title, and when it is selected
            // in QuadrantChartFragment.java
            SharedPreferences sharedPreferences = getSharedPreferences(
                    Utilities.SEVENHABITS, Context.MODE_PRIVATE);
            boolean displayChart = sharedPreferences.getBoolean(DISPLAYCHART, false);
            if (displayChart) {
                switcher.setTitle(getResources().getString(R.string.chart_list_view));
            } else {
                switcher.setTitle(getResources().getString(R.string.chart_view));
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // TODO: Clean this up and make sense of it. This looks gross.
        int id = item.getItemId();
        if (id == R.id.action_switch_view) {
            final QuadrantChartFragment quadrantChartFragment = (QuadrantChartFragment)
                    getSupportFragmentManager().findFragmentByTag(QUADRANTCHARTTAG);
            if (quadrantChartFragment != null) {
                Log.d("actions", "quadrantChartFragment != null");
                SharedPreferences sharedPreferences = getSharedPreferences(
                        Utilities.SEVENHABITS, Context.MODE_PRIVATE);
                String action = item.getTitle().toString();
                String[] options = {getResources().getString(R.string.chart_view),
                        getResources().getString(R.string.chart_list_view)};

                LinearLayout chartView = quadrantChartFragment.getChartView();
                ListView chartListView = quadrantChartFragment.getChartListView();
                ChartListAdapter adapter = quadrantChartFragment.getmAdapter();

                if (action.equals(options[0])) {
                    Log.d("actions", "show LinearLayout");
                    // show LinearLayout
                    item.setTitle(options[1]);
                    chartView.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean(MainActivity.DISPLAYCHART, true).apply();
                } else {
                    Log.d("actions", "Hide LinearLayout");
                    // hide LinearLayout
                    item.setTitle(options[0]);
                    chartView.setVisibility(View.GONE);
                    if (chartListView != null) {
                        Log.d("actions", "charListView != null");
                        SQLiteHelper helper = new SQLiteHelper(this);
                        final ArrayList<QuadrantDetail> elements = helper.getDetails(
                                quadrantChartFragment.getTitle(), Utilities.QUADRANT.ALL);
                        if (adapter == null) {
                            Log.d("actions", "adapter == null");
                            adapter = new ChartListAdapter(
                                    this.getApplicationContext(),
                                    elements);
                            chartListView.setAdapter(adapter);
                            chartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ModifyQuadrantListViewItemDialog dialog =
                                            ModifyQuadrantListViewItemDialog.newInstance(
                                                    quadrantChartFragment,
                                                    elements,
                                                    position);
                                    dialog.show(getSupportFragmentManager(), MainActivity.MODIFYQUADRANTTAG);
                                }
                            });
                        } else {
                            Log.d("actions", "adapter != null");
                            quadrantChartFragment.updatemAdapterForNewList(quadrantChartFragment.getTitle());
                            chartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ModifyQuadrantListViewItemDialog dialog =
                                            ModifyQuadrantListViewItemDialog.newInstance(
                                                    quadrantChartFragment,
                                                    quadrantChartFragment.getmAdapter().getElements(),
                                                    position);
                                    dialog.show(getSupportFragmentManager(), MainActivity.MODIFYQUADRANTTAG);
                                }
                            });
                        }
                    }
                    sharedPreferences.edit().putBoolean(MainActivity.DISPLAYCHART, false).apply();
                }
            }
            return true;
        }

        //noinspection SimplifiableIfStatement
        else if (id == R.id.home) {
            /* had to put this in so that the home button would work again.
             * It stopped after I added onBackPressed() settings. */
            this.onPause();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: add soft back button goes back when in quadrant fragment (not sure if this needs to happen right here or somewhere within NavigationDrawerFragment
        // added to avoid accidental quiting of the application.
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
                mNavigationDrawerFragment != null &&
                mNavigationDrawerFragment.isDrawerOpen()) {
            /* user probably is trying to actually leave. */
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
                mNavigationDrawerFragment != null &&
                getSupportFragmentManager().findFragmentByTag(QUADRANTCHARTTAG) != null) {
            /* might be an accidental click of the back button trying to go back to previous fragment */
            mNavigationDrawerFragment.openDrawer();
        } else {
            /* normal behavior */
            super.onBackPressed();
        }
    }
}
