package com.myplacelocator.ActivitiesAndFragments;


import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.myplacelocator.DataBase.MySqlHelper;
import com.myplacelocator.DataBase.PlacesContract;
import com.myplacelocator.FragmentChanger;
import com.myplacelocator.Functions.CheckConnection;
import com.myplacelocator.Functions.Constants;
import com.myplacelocator.Functions.MyAlertDialog;
import com.myplacelocator.Functions.MyPlace;
import com.myplacelocator.Functions.MySearchService;
import com.myplacelocator.Functions.SearchListAdapter;
import com.myplacelocator.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchListFragment extends Fragment implements LocationListener{

    protected int picMaxHeight;
    private EditText searchET;
    private RecyclerView searchListRV;
    private CheckBox nearMe;
    private ImageView searchIV;
    private ProgressBar loading;
    private String search, unit;
    private View view;
    private ArrayList<MyPlace> allResults;
    private SearchListAdapter adapter;

    private Location currentLocation;
    private LocationManager locationManager;
    private FragmentChanger fragmentChanger;
    private SharedPreferences sharedPreferences;
    private boolean nextme = false;

    public String mySearch;
    public int near;
    public boolean history;
    MySearchReciever mySearchReciever;
    int count=0;



    public SearchListFragment() {
        // Required empty public constructor
    }


    /**
     * Create the fragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getText(R.string.search_title));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Update the distance unit of preferences
        unit = sharedPreferences.getString("unitLP","Km");

        mySearchReciever = new MySearchReciever();

        // Inflate the layout for this fragment
        if (view==null)
            view = inflater.inflate(R.layout.fragment_search_list, container, false);


        loading = (ProgressBar)view.findViewById(R.id.loading);
        searchET = (EditText) view.findViewById(R.id.searchET);
        searchIV = (ImageView)view.findViewById(R.id.searchIV);
        searchListRV = (RecyclerView)view.findViewById(R.id.searchRV);
        nearMe = (CheckBox)view.findViewById(R.id.nearMeCB);

        allResults = new ArrayList<>();
        String s = sharedPreferences.getString("search",null);
        int n = sharedPreferences.getInt("near",0);
        Boolean h = sharedPreferences.getBoolean("hist",false);

        final CheckConnection checkConnection = new CheckConnection(getActivity());
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE & h==true & s!=null & checkConnection.isNetworkAvailable()) {
            mySearch = s;
            near=n;
            history=h;
            SearchFromHistory();
        }
        search = sharedPreferences.getString("search",null);


        fragmentChanger= (FragmentChanger) getActivity();

        // when we change the screen orientation
        if (savedInstanceState!=null){
            allResults.clear();
            ArrayList<MyPlace> test=savedInstanceState.getParcelableArrayList("places");
            allResults.addAll(test);
            currentLocation=savedInstanceState.getParcelable("location");
            searchListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            adapter = new SearchListAdapter(allResults, getActivity(), fragmentChanger, currentLocation);
            adapter.notifyDataSetChanged();
            searchListRV.setAdapter(adapter);
        }

        else {
            // If network is available
            if (checkConnection.isNetworkAvailable()) {
                // If comes from history, display the search ask
                if (history == true)
                    SearchFromHistory();

                    //Else, display the last search result
                else {
                    MySqlHelper mySQLHelper = new MySqlHelper(getActivity());
                    Cursor cursor = mySQLHelper.getReadableDatabase().query(Constants.HISTORY_TABLE_NAME, null, null, null, null, null, null, null);
                    if (cursor.moveToLast()) {
                        mySearch = cursor.getString(cursor.getColumnIndex(Constants.SEARCH));
                        near = cursor.getInt(cursor.getColumnIndex(Constants.NEAR));
                    }

                    if (mySearch!=null && near == 0) {
                        searchET.setText(mySearch);
                        search = mySearch;
                        searchByText();
                    }
                    if (mySearch!=null && near == 1) {
                        searchET.setText(mySearch);
                        nearMe.setChecked(true);
                        search = mySearch;
                        getCurrentLocation();
                        searchNearMe();
                    }
                }
            }
            // Case if network is unavailable
            else {
                // If comes from history, display the search ask
                if (history == true) {
                    if (mySearch!=null && near == 0) {
                        searchET.setText(mySearch);
                        search = mySearch;
                    }
                    if (mySearch!=null && near == 1) {
                        searchET.setText(mySearch);
                        nearMe.setChecked(true);
                        search = mySearch;
                    }
                }
                // Display the last search result
                else {
                    MySqlHelper mySQLHelper = new MySqlHelper(getActivity());
                    Cursor cursor = mySQLHelper.getReadableDatabase().query(Constants.LASTSEARCH_TABLE_NAME, null, null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        mySearch = cursor.getString(cursor.getColumnIndex(Constants.SEARCH));
                        near = cursor.getInt(cursor.getColumnIndex(Constants.NEAR));

                        if (mySearch!=null && near == 0)
                            searchET.setText(mySearch);

                        if (mySearch!=null && near == 1) {
                            searchET.setText(mySearch);
                            nearMe.setChecked(true);
                        }

                        allResults=new MySqlHelper(getActivity()).displayLastSearch(cursor,allResults);
                    }

                    searchListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    adapter = new SearchListAdapter(allResults, getActivity(), fragmentChanger, currentLocation);
                    searchListRV.setAdapter(adapter);
                }

                // Toast.makeText(getActivity(), getResources().getText(R.string.internet_connection_unavailable), Toast.LENGTH_SHORT).show();
            }
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                picMaxHeight=100;
                search = searchET.getText().toString();

                if (!search.equals("")) {
                    if (checkConnection.isNetworkAvailable()) {
                        if (nearMe.isChecked()) {
                            near = 1;
                            getCurrentLocation();
                            searchNearMe();
                        }
                        else {
                            near = 0;
                            searchByText();
                        }

                        new MySqlHelper(getActivity()).addToHistory(search,near);

                    } else{
                        MyAlertDialog.createAlertDialog(getActivity(),getString(R.string.network_connection),0,getString(R.string.internet_connection_unavailable),getString(R.string.settings),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                            @Override
                            public void onPositive(DialogInterface dialogInterface) {
                                getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }

                            @Override
                            public void onNegative(DialogInterface dialogInterface) {

                            }
                        });
                    }

                } else
                    Toasty.error(getActivity(), (String) getResources().getText(R.string.empy_place_search),Toast.LENGTH_SHORT,true).show();
            }
        });


        getCurrentLocation();

        return view;
    }

    /**
     * Function to search with full text
     */
    private void searchByText(){
        nextme=false;
        loading.setVisibility(View.VISIBLE);
        allResults.clear();
        searchListRV.setAdapter(adapter);
        MySearchService.findByText(getActivity(),search.replace(" ","+"),picMaxHeight,currentLocation);
    }

    /**
     * Function to search with the current location
     */
    private  void searchNearMe() {
        nextme=true;
        loading.setVisibility(View.VISIBLE);
        allResults.clear();
        searchListRV.setAdapter(adapter);
        MySearchService.FindNearMe(getActivity(),search,picMaxHeight,currentLocation);
    }

    /**
     *  Function who gets the current location of the user
     */
    protected void getCurrentLocation() {
        locationManager = (LocationManager)getActivity().getSystemService((getActivity()).LOCATION_SERVICE);

        //get last known location by gps
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        else
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if(currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            else
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        //wait for 60 seconds and try to get location using network provider (cell ro wifi)
        final Handler handler = new Handler();
        Runnable getLocationByNetwork = new Runnable() {
            @Override
            public void run() {
                if(currentLocation==null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    else
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, SearchListFragment.this);
                }
            }
        };
        handler.postDelayed(getLocationByNetwork  , 60000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startGPS();
            else{
                MyAlertDialog.createAlertDialog(getActivity(),getString(R.string.enable_location),0,getString(R.string.enable_location_msg),getString(R.string.settings),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface) {
                    }
                });
            }
            // Toast.makeText(getActivity(), getResources().getText(R.string.GPSPermissionMessage).toString(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Function who start the gps use
     */
    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mySearchReciever, new IntentFilter("Finished search"));

        searchListRV.setAdapter(adapter);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        else
            startGPS();
    }

    @Override
    public void onPause() {
        super.onPause();
        getCurrentLocation();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        locationManager.removeUpdates(this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mySearchReciever);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}


    /**
     * Save the fragment state when rotate the screen
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("places", allResults);
        outState.putParcelable("location",currentLocation);
    }

    /**
     * Function to return results based on historical searches
     */
    public void SearchFromHistory(){

        new MySqlHelper(getActivity()).addToHistory(mySearch,near);
        if (mySearch!=null && near == 0) {
            searchET.setText(mySearch);
            search = mySearch;
            searchByText();
        }
        if (mySearch!=null && near == 1) {
            searchET.setText(mySearch);
            nearMe.setChecked(true);
            search = mySearch;
            getCurrentLocation();
            searchNearMe();
        }

    }


    class MySearchReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            count+=1;
            String status = intent.getStringExtra("status");

            if (!status.equals("OK")) {
                String error = intent.getStringExtra("error_message");
                if (error!=null)
                    status = status+": "+error;
                if (status.equals("ZERO_RESULTS") && currentLocation==null){
                    MyAlertDialog.createAlertDialog(getActivity(),getString(R.string.enable_location),0,getString(R.string.enable_location_msg),getString(R.string.settings),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                        @Override
                        public void onPositive(DialogInterface dialogInterface) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                        @Override
                        public void onNegative(DialogInterface dialogInterface) {
                        }
                    });
                }
            }
            // Toast.makeText(context, "Status: "+status, Toast.LENGTH_SHORT).show();

            getActivity().getContentResolver().delete(PlacesContract.LastSearchResults.CONTENT_URI,null,null);

            ArrayList<MyPlace>newResults = intent.getParcelableArrayListExtra("allresults");
            allResults.clear();
            allResults.addAll(newResults);

            for (int i=0;i<allResults.size();i++) {
                MyPlace myPlace = new MyPlace(allResults.get(i).getName(),allResults.get(i).getAddress(),allResults.get(i).getId(),allResults.get(i).getImageUrl(),allResults.get(i).getLat(),allResults.get(i).getLng(),allResults.get(i).getRate(),allResults.get(i).getTel(),allResults.get(i).getWebsite());
                new MySqlHelper(context).addToLastSearch(myPlace,search,near,context);
            }


            /**
             * Sort the arrayList by distance before display the result
             */
            ArrayList<MyPlace>byDistance = new ArrayList<>();
            byDistance.addAll(allResults);
            Collections.sort(allResults, new Comparator<MyPlace>() {
                @Override
                public int compare(MyPlace p1, MyPlace p2) {
                    double distance1 = p1.getDistance(p1.getLat(),p1.getLng(),currentLocation.getLatitude(),currentLocation.getLongitude(),unit);
                    double distance2 = p2.getDistance(p2.getLat(),p2.getLng(),currentLocation.getLatitude(),currentLocation.getLongitude(),unit);
                    return Double.compare(distance1,distance2);
                }
            });


            loading.setVisibility(View.INVISIBLE);
            searchListRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
            adapter= new SearchListAdapter(allResults, context, fragmentChanger,currentLocation);
            searchListRV.setAdapter(adapter);

            // Toasty.info(getActivity(),"count: "+count,Toast.LENGTH_SHORT).show();
        }
    }
}
