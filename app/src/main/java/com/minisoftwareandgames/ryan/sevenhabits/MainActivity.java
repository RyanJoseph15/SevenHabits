package com.minisoftwareandgames.ryan.sevenhabits;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.minisoftwareandgames.ryan.sevenhabits.Fragments.NavigationDrawerFragment;
import com.minisoftwareandgames.ryan.sevenhabits.Fragments.QuadrantChartFragment;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    public static final String QUADRANTCHARTTAG = "quadrantcharttag";
    public static final String QUADRANTTAG = "quadranttag";
    public static final String NEWCHARTINFOTAG = "newchartinfotag";
    public static final String NEWTASKTAG = "newtasktag";
    public static final String MODIFYQUADRANTTAG = "modifyquadranttag";
    public static final String MODIFYTITLETAG = "modifytitletag";

    public void updateTitle(String title) {
        mNavigationDrawerFragment.getActionBar().setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        int selected = PreferenceManager.getDefaultSharedPreferences(this).getInt("selected", -1);
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
        SharedPreferences sharedPreferences = getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        Log.d("CHECK", "position: " + position);
        if (position < 0) {

        } else {
            QuadrantChartFragment quadrantChartFragment;
            if (fragmentManager.findFragmentByTag(QUADRANTCHARTTAG) == null) {
                quadrantChartFragment = QuadrantChartFragment.newInstance(position + 1, Utilities.getElement(sharedPreferences, position, Utilities.TITLES));
            } else {
                quadrantChartFragment = (QuadrantChartFragment) fragmentManager.findFragmentByTag(QUADRANTCHARTTAG);
                quadrantChartFragment.setTitle(Utilities.getElement(sharedPreferences, position, Utilities.TITLES));
            }
            fragmentManager.beginTransaction()
                .replace(R.id.container, quadrantChartFragment, QUADRANTCHARTTAG)
                .commit();
//            Log.d("CHECK", "QuadrantChartFragment title: " + quadrantChartFragment.getTitle());
//            updateTitle(quadrantChartFragment.getTitle());
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
//            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            /* had to put this in so that the home button would work again.
             * It stopped after I added onBackPressed() settings. */
            this.onPause();
            return true;
        } else return super.onOptionsItemSelected(item);
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
