package com.myplacelocator.Functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.myplacelocator.DataBase.MySqlHelper;
import com.myplacelocator.FragmentChanger;
import com.myplacelocator.OnLoadFinishedListener;
import com.myplacelocator.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by sandramac on 12/02/2018.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyPlaceVH> implements OnLoadFinishedListener {

    private ArrayList<MyPlace> allResults;
    private Context context;
    private Location location;
    private double distance;
    private boolean nextme;
    FragmentChanger fragmentChanger;
    private String unit;
    MyPlace myPlace;
    CheckConnection checkConnection;

    public SearchListAdapter(ArrayList<MyPlace> allResults, Context context, FragmentChanger fragmentChanger, Location location) {
        this.allResults = allResults;
        this.context = context;
        this.fragmentChanger = fragmentChanger;
        this.location = location;
    }

    @Override
    public SearchListAdapter.MyPlaceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View singleview = LayoutInflater.from(context).inflate(R.layout.search_result_item, null);

        return new MyPlaceVH(singleview);
    }

    @Override
    public void onBindViewHolder(final MyPlaceVH holder, int position) {
        checkConnection=new CheckConnection(context);
        myPlace = allResults.get(position);
        final MyPlace place = new MyPlace(myPlace.getName(),myPlace.getAddress(),myPlace.getId(),myPlace.getImageUrl(),myPlace.getLat(),myPlace.getLng(),myPlace.getRate(),myPlace.getTel(),myPlace.getWebsite());

        if (position%2==0)
            holder.view.setCardBackgroundColor(Color.parseColor("#FEB4EED1"));
        else
            holder.view.setCardBackgroundColor(Color.parseColor("#FFD0B9D4"));

        holder.nameTV.setText(place.getName());
        String address = place.getAddress();

//        holder.rateTV.setText(""+place.getRate());
        //      holder.rateIV.setImageResource(R.drawable.star);
        holder.addressTV.setText(address);
        holder.ratingBar.setRating((float) myPlace.getRate());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        unit = preferences.getString("unitLP","Km");

        if (location==null)
            distance=0;

        else
            distance = place.getDistance(place.getLat(),place.getLng(),location.getLatitude(),location.getLongitude(),unit);
        DecimalFormat df = new DecimalFormat("#.#");
        String distanceString = df.format(distance);

        String unitString = "";

        switch (unit){
            case "Km":
                unitString="km";
                break;
            case "Mi":
                unitString="miles";
                break;
        }

        holder.distanceTV.setText(distanceString+" "+unitString);

        if (place.getImageUrl()!=null)
            holder.iconIV.setImageBitmap(new MyFunctions().decodeBase64(place.getImageUrl()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChanger.changeFragmentsMap(place,false,false);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.saveItem:
                                if (checkConnection.isNetworkAvailable()) {
                                    MyDetailsDownloader myDetailsDownloader = new MyDetailsDownloader(new CompletePlaceData(place, null));
                                    myDetailsDownloader.execute();
                                    myDetailsDownloader.setOnLoadFinishedListener(SearchListAdapter.this);
                                }
                                else {
                                    MySqlHelper mySQLHelper = new MySqlHelper(context);
                                    mySQLHelper.addToFavorite(place,context);
                                }
                                break;
                            case R.id.shareItem:
                                new MyFunctions().sharePlace(place,context);
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return allResults.size();
    }

    @Override
    public void DataReady(CompletePlaceData completePlaceData) {
        MyPlace place =completePlaceData.fullPlace;
        place.setTel(completePlaceData.allresult.get(0));
        place.setWebsite(completePlaceData.allresult.get(1));
        MySqlHelper mySQLHelper = new MySqlHelper(context);
        mySQLHelper.addToFavorite(place,context);
    }

    public class MyPlaceVH extends RecyclerView.ViewHolder {

        private TextView nameTV,addressTV,distanceTV, rateTV;
        private ImageView iconIV, rateIV;
        private CardView view;
        private RatingBar ratingBar;

        public MyPlaceVH(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            addressTV = itemView.findViewById(R.id.addressTV);
            distanceTV = itemView.findViewById(R.id.distanceTV);
            iconIV = itemView.findViewById(R.id.imageView);
            view = itemView.findViewById(R.id.card_view);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            Drawable progress = ratingBar.getProgressDrawable();
            DrawableCompat.setTint(progress, Color.parseColor("#7a1ea1"));
        }
    }
}

