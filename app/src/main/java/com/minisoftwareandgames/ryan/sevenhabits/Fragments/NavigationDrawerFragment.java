package com.minisoftwareandgames.ryan.sevenhabits.Fragments;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.ModifyTitleDialog;
import com.minisoftwareandgames.ryan.sevenhabits.MainActivity;
import com.minisoftwareandgames.ryan.sevenhabits.Dialogs.NewChartInfoDialog;
import com.minisoftwareandgames.ryan.sevenhabits.R;
import com.minisoftwareandgames.ryan.sevenhabits.SQLiteHelper;
import com.minisoftwareandgames.ryan.sevenhabits.Utilities;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = -1;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private ArrayAdapter<String> mAdapter;

    public NavigationDrawerFragment() {
    }
    public DrawerLayout getmDrawerLayout() {return this.mDrawerLayout;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

//        if (savedInstanceState != null) {
//            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
//            mFromSavedInstanceState = true;
//        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        mCurrentSelectedPosition = sharedPreferences.getInt("mCurrentSelectedPosition", -1);

        // Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
//        mDrawerListView = (ListView) inflater.inflate(
//                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) view.findViewById(R.id.list_view);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ModifyTitleDialog modifyTitleDialog = ModifyTitleDialog.newInstance(NavigationDrawerFragment.this, position);
                modifyTitleDialog.show(getFragmentManager(), MainActivity.MODIFYTITLETAG);
                return true;
            }
        });
        ArrayList<String> titles = Utilities.getElements(getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE), Utilities.TITLES);
//        Log.d("CHECK", "adapter: " + Utilities.Strings2JSON(titles, Utilities.TITLES));
        if (titles == null) {
//            Log.d("CHECK", "titles is null");
            titles = new ArrayList<String>();
        }
        mAdapter = new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                titles);
        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectItem(mCurrentSelectedPosition);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }
    public void openDrawer() {
        if (mDrawerLayout != null && mFragmentContainerView != null) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
//        actionBar.hide();

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                QuadrantChartFragment quadrantChartFragment = (QuadrantChartFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag(MainActivity
                                .QUADRANTCHARTTAG);
                if (quadrantChartFragment != null) {
                    Log.d("CHECK", "QuadrantChartFragment title: " + quadrantChartFragment.getTitle());
                    ((MainActivity) getActivity()).updateTitle(quadrantChartFragment.getTitle());
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("mCurrentSelectedPosition", mCurrentSelectedPosition).apply();
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.menu_drawer, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_add) {
            NewChartInfoDialog newChartInfoDialog = NewChartInfoDialog.newInstance(this);
            newChartInfoDialog.show(getFragmentManager(), MainActivity.NEWCHARTINFOTAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    public ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Callback methods
     * ----------------------------------------------------------------------------------- */

    public void NewChartInfoDialogCallback(String title) {
//        Log.d("CHECK", "callback");
        // add to the list of titles
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        if (helper.uniqueTitle(title)) {
            Utilities.addElement(getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE), title, Utilities.TITLES);
            // update the view
            if (mAdapter.getCount() > 0) mAdapter.clear();
            mAdapter.addAll(Utilities.getElements(getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE), Utilities.TITLES));
            mAdapter.notifyDataSetChanged();
        } else {
            String notUnique = title + " " + getString(R.string.not_unique);
            Toast.makeText(getActivity(), notUnique, Toast.LENGTH_SHORT).show();
        }
    }

    public void ModifyQuadrantDialogDeleteCallback(int position) {
        // TODO: update current fragment after completing this task
        // remove JSON sharedpreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        ArrayList<String> elements = Utilities.getElements(sharedPreferences, Utilities.TITLES);
        sharedPreferences.edit().remove(Utilities.TITLES).apply();
        String title = elements.get(position);
        elements.remove(title);
        Utilities.addElements(sharedPreferences, elements, Utilities.TITLES);
        // delete quadrant entries from the database
        SQLiteHelper helper = new SQLiteHelper(getActivity());
        helper.removeTitle(title);
        // notify data set changed
        if (mAdapter.getCount() > 0) mAdapter.clear();
        mAdapter.addAll(elements);
        mAdapter.notifyDataSetChanged();

        // update to previous fragment (should work back to welcome/tutorial fragment)
        if (elements.size() > 1) {
            selectItem(position);
        } else {
            // we are down to none and need to open up a welcome/tutorial fragment
        }

    }

    public void ModifyQuadrantDialogEditCallback(int position, String newElement) {
        // TODO: update current fragment after completing this task

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utilities.SEVENHABITS, Context.MODE_PRIVATE);
        String oldElement = Utilities.getElement(sharedPreferences, position, Utilities.TITLES);
        String notUnique = newElement + " " + getString(R.string.not_unique);

        if (!oldElement.equals(newElement)) {
            // update JSON sharedpreferences
            ArrayList<String> elements = Utilities.getElements(sharedPreferences, Utilities.TITLES);
            sharedPreferences.edit().remove(Utilities.TITLES).apply();                       // out with the old
            elements.remove(oldElement);
            elements.add(position, newElement);
            Utilities.addElements(sharedPreferences, elements, Utilities.TITLES);            // in with the new
            // notify data set changed
            if (mAdapter.getCount() > 0) mAdapter.clear();
            mAdapter.addAll(elements);
            mAdapter.notifyDataSetChanged();
            // we need to update our database for each quadrant to match the new title
            SQLiteHelper helper = new SQLiteHelper(getActivity());
            boolean unique = helper.updateTitle(oldElement, newElement);
            if (!unique) Toast.makeText(getActivity(), notUnique, Toast.LENGTH_SHORT).show();
            else selectItem(position);
        } else {
            Toast.makeText(getActivity(), notUnique, Toast.LENGTH_SHORT).show();
        }
    }

}
