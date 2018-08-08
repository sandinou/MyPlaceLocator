package com.myplacelocator.ActivitiesAndFragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.myplacelocator.DataBase.MySqlHelper;
import com.myplacelocator.Functions.CheckConnection;
import com.myplacelocator.Functions.CompletePlaceData;
import com.myplacelocator.Functions.MyAlertDialog;
import com.myplacelocator.Functions.MyDetailsDownloader;
import com.myplacelocator.Functions.MyFunctions;
import com.myplacelocator.Functions.MyPlace;
import com.myplacelocator.OnLoadFinishedListener;
import com.myplacelocator.R;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultMapFragment extends Fragment implements OnLoadFinishedListener{

    public MyPlace place;
    public boolean fav;

    private TextView addressTV, telTV;
    private LinearLayout shareLayout, saveLayout, telLayout1, siteLayout;
    private ImageView placeIV;
    private RatingBar ratingBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    View view;

    public ResultMapFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (view == null)
            view = inflater.inflate(R.layout.fragment_result_map, container, false);

        if (savedInstanceState!=null)
            place = (MyPlace) savedInstanceState.get("place");

        //Set title of the activity
        getActivity().setTitle(place.getName());

        final CheckConnection checkConnection = new CheckConnection(getActivity());

        // Define the CollapsingToolbarLayout to display the place map

        collapsingToolbarLayout = (CollapsingToolbarLayout)view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        telTV = (TextView)view.findViewById(R.id.telTV);
        saveLayout = (LinearLayout)view.findViewById(R.id.saveLL);

        telLayout1 = (LinearLayout) view.findViewById(R.id.telLL);
        siteLayout = (LinearLayout) view.findViewById(R.id.webLL);

        addressTV = (TextView)view.findViewById(R.id.addressTV);
        placeIV = (ImageView)view.findViewById(R.id.placeIV);

        ratingBar = (RatingBar)view.findViewById(R.id.ratingBar2);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor("#7a1ea1"));

        ratingBar.setRating((float) place.getRate());


        // Recover more place's informations if not come from favorites
        if (!fav & checkConnection.isNetworkAvailable()) {
            MyDetailsDownloader myDetailsDownloader = new MyDetailsDownloader(new CompletePlaceData(place,null));
            myDetailsDownloader.execute();
            myDetailsDownloader.setOnLoadFinishedListener(this);
        }
        else {
            if (place.getTel()==null)
                telTV.setText(getResources().getText(R.string.unrecoverable_data));
            else
                telTV.setText(place.getTel());

            telLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasty.error(getActivity(), (String) getResources().getText(R.string.failed_communication), Toast.LENGTH_LONG, true).show();
                }
            });
            siteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsite(place.getWebsite());
                }
            });
        }

        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySqlHelper mySQLHelper = new MySqlHelper(getActivity());
                mySQLHelper.addToFavorite(place,getActivity());
            }
        });

        shareLayout = (LinearLayout)view.findViewById(R.id.shareLL);
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyFunctions().sharePlace(place,view.getContext());
            }
        });

        String ad = place.getAddress();
        String[]tab = ad.split(",");
        String address = "";
        for (int i=0;i<tab.length-1;i++)
            address+=(tab[i]+"\n");
        address+=tab[tab.length-1];
        addressTV.setText(address);

        if (place.getImageUrl()!=null)
            placeIV.setImageBitmap(new MyFunctions().decodeBase64(place.getImageUrl()));

        // Define the google map fragment
        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().add(R.id.myMapContainer,mapFragment, "map").commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Double lat = place.getLat();
                Double lon = place.getLng();
                LatLng latLng = new LatLng(lat,lon);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, (float) 17);
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                googleMap.moveCamera(update);
            }
        });

        return view;
    }


    /**
     * Update the place's informations after checking from the API
     * @param completePlaceData
     */
    @Override
    public void DataReady(CompletePlaceData completePlaceData) {
        final MyPlace place =completePlaceData.fullPlace;
        place.setTel(completePlaceData.allresult.get(0));
        place.setWebsite(completePlaceData.allresult.get(1));
        telTV.setText(place.getTel());
        telLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!place.getTel().equals("N/A"))
                    new MyFunctions().callPlace(place.getTel(),getActivity());
                else
                    Toasty.info(getActivity(), (String) getResources().getText(R.string.no_phone),Toast.LENGTH_SHORT).show();

            }
        });

        siteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWebsite(place.getWebsite());
            }
        });
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySqlHelper mySQLHelper = new MySqlHelper(getActivity());
                mySQLHelper.addToFavorite(place,getActivity());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("place",place);
    }

    public void goToWebsite(String website){
        if (website.equals("N/A"))
            Toasty.info(getActivity(), (String) getResources().getText(R.string.no_website),Toast.LENGTH_SHORT).show();
        else {
            if (website==null) {
                MyAlertDialog.createAlertDialog(getActivity(),getString(R.string.network_connection),0,getString(R.string.failed_request),getString(R.string.settings),getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
                    @Override
                    public void onPositive(DialogInterface dialogInterface) {
                        getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }

                    @Override
                    public void onNegative(DialogInterface dialogInterface) {
                    }
                });
            }
            else
                new MyFunctions().webSite(website, getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyDetailsDownloader myDetailsDownloader = new MyDetailsDownloader(new CompletePlaceData(place,null));
        myDetailsDownloader.execute();
        myDetailsDownloader.setOnLoadFinishedListener(this);
    }
}
