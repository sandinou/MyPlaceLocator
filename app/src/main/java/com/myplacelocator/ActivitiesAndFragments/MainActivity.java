package com.myplacelocator.ActivitiesAndFragments;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.myplacelocator.DataBase.PlacesContract;
import com.myplacelocator.FragmentChanger;
import com.myplacelocator.Functions.Constants;
import com.myplacelocator.Functions.MyAlertDialog;
import com.myplacelocator.Functions.MyPlace;
import com.myplacelocator.Functions.MyPowerReceiver;
import com.myplacelocator.R;

public class MainActivity extends AppCompatActivity implements FragmentChanger, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RelativeLayout layout;
    private LinearLayout root;
    private MyPowerReceiver myPowerReceiver;
    private NavigationView navigationView;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        myPowerReceiver = new MyPowerReceiver();

        /**************** Design******************/

        //Define the toolbar of the application
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Define the navigation drawer menu of the application
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        layout = (RelativeLayout)findViewById(R.id.secondFrag);
        root = (LinearLayout)findViewById(R.id.root_layout);

        /************** Fragments ****************/
        // Set the default fragment when opening the application
        SearchListFragment searchListFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("list");
        if(searchListFragment==null){
            searchListFragment = new SearchListFragment();
            searchListFragment.picMaxHeight = 100;
            getFragmentManager().beginTransaction().replace(R.id.main_layout, searchListFragment,"list").commit();
        }

    }

    /**
     * Change the current fragment to MapFragment
     * @param place
     * @param fav
     * @param full
     */
    @Override
    public void changeFragmentsMap(MyPlace place, boolean fav, boolean full) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack("changeFrags");
        // if we use the application on smartphone
        if (layout==null){
            ResultMapFragment resultMapFragment = new ResultMapFragment();
            resultMapFragment.place = place;
            resultMapFragment.fav = fav;
            transaction.replace(R.id.main_layout,resultMapFragment);
        }
        // if we use the application on tablet
        else {
            ResultMapFragment resultMapFragment = new ResultMapFragment();
            resultMapFragment.place = place;
            resultMapFragment.fav = fav;
            // if we come from favorites fragment
            if (full)
                transaction.replace(R.id.root_layout,resultMapFragment);
                // if we come from search
            else
                transaction.replace(R.id.secondFrag, resultMapFragment);
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Change the current fragment to Searchfragment
     * @param search
     * @param near
     * @param history
     */
    @Override
    public void changeFragmentSearch(String search, int near, boolean history) {
        SearchListFragment searchListFragment = new SearchListFragment();
        searchListFragment.mySearch = search;
        searchListFragment.near = near;
        searchListFragment.history = history;
        searchListFragment.picMaxHeight = 100;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack("changeFrags");

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putString("search",search).commit();
            preferences.edit().putInt("near",near).commit();
            preferences.edit().putBoolean("hist",true).commit();
            Intent intent = new Intent(this,MainActivity.class);

            startActivity(intent);
        }
        //
        else
            transaction.replace(R.id.main_layout, searchListFragment);
        transaction.commit();

    }

    /**
     * Change the current fragment to FavoriteFragment
     */
    public void changeFragmentFavorites(){
        FavoritesListFragment favoritesListFragment = new FavoritesListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack("changeFrags");

        // if we use the application on smartphone
        if (root==null)
            transaction.replace(R.id.main_layout,favoritesListFragment);
            // if we use the application on tablet
        else
            transaction.replace(R.id.root_layout,favoritesListFragment);

        transaction.commit();
    }


    /**
     * Change the current fragment to HistoryFragment
     */
    public void changeFragmentHistory(){
        HistoryListFragment historyListFragment = new HistoryListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack("changeFrags");
        // if we use the application on smartphone
        if (root ==null)
            transaction.replace(R.id.main_layout, historyListFragment);
            // if we use the application on tablet
        else
            transaction.replace(R.id.root_layout,historyListFragment);
        transaction.commit();
    }

    /**
     * Change the current fragment to SettingsFragment
     */
    public void changeFragmentSettings(){
        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack("changeFrags");
        // if we use the application on smartphone
        if (root==null)
            transaction.replace(R.id.main_layout,settingsFragment);
            // if we use the application on tablet
        else
            transaction.replace(R.id.root_layout,settingsFragment);
        transaction.commit();
    }


    /**
     * Hide the navigation drawer menu
     */
    private void hideDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();
        else
            super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }


    /**
     * Actions of the menu's item
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        hideDrawer();
        switch (item.getItemId()){
            case R.id.item_home:
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.item_settings:
                changeFragmentSettings();
                break;
            case R.id.item_favorites:
                changeFragmentFavorites();
                break;
            case R.id.item_history:
                changeFragmentHistory();
                break;
            case R.id.item_delete_all_favorites:

                final SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this,android.R.layout.simple_list_item_1,null,new String[]{Constants.NAME_PLACE},new int[]{android.R.id.text1});

                //Delete all favorites
                MyAlertDialog.createAlertDialog(this,getString(R.string.delete_alert_dialog),R.drawable.alert_icon,getString(R.string.favorite_alldelete_message),getString(R.string.alert_dialog_positive),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface) {
                        getContentResolver().delete(PlacesContract.FavoritesPlaces.CONTENT_URI,null, null);
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface) {
                        Toast.makeText(MainActivity.this, getResources().getText(R.string.cancel_delete_allfavorites), Toast.LENGTH_SHORT).show();
                    }
                });
                adapter.swapCursor(adapter.getCursor());
                break;

            case R.id.item_delete_all_history:
                final SimpleCursorAdapter adapter1 = new SimpleCursorAdapter(MainActivity.this,android.R.layout.simple_list_item_1,null,new String[]{Constants.SEARCH},new int[]{android.R.id.text1});

                MyAlertDialog.createAlertDialog(this,getString(R.string.delete_alert_dialog),R.drawable.alert_icon,getString(R.string.history_delete),getString(R.string.alert_dialog_positive),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface) {
                        getContentResolver().delete(PlacesContract.historySearch.CONTENT_URI,null, null);
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface) {
                        Toast.makeText(MainActivity.this, getResources().getText(R.string.cancel_delete_history), Toast.LENGTH_SHORT).show();
                    }
                });
                adapter1.swapCursor(adapter1.getCursor());
                break;
        }
        return true;
    }



    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(myPowerReceiver,filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myPowerReceiver);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
