package com.myplacelocator.Functions;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MySearchService extends IntentService {

    public MySearchService() {
        super("MySearchService");
    }

    public static void findByText(Context context, String text, int picMaxHeight, Location location){
        Intent intent = new Intent(context, MySearchService.class);
        intent.setAction("Find By Text");
        intent.putExtra("search", text);
        intent.putExtra("pic", picMaxHeight);
        if (location!=null) {
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
        }
        context.startService(intent);
    }

    public static void FindNearMe(Context context, String text, int picMaxHeight, Location location) {
        Intent intent = new Intent(context, MySearchService.class);
        intent.setAction("Find NearMe");
        intent.putExtra("search", text);
        intent.putExtra("pic", picMaxHeight);
        if (location!=null) {
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lng", location.getLongitude());
        }
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<MyPlace> places = new ArrayList<>();
        String action = intent.getAction();
        String text = intent.getStringExtra("search");
        int picMaxHeight = intent.getIntExtra("pic",0);
        String url="";
        String status = "";
        String error_message = null;

        switch (action){
            case "Find By Text":
                url="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+text+"&key="+ Constants.GOOGLE_PLACES_API_KEY;
                HandleResearchText(url,picMaxHeight, places,status,error_message);
                break;
            case "Find NearMe":
                double lat = intent.getDoubleExtra("lat",0);
                double lng = intent.getDoubleExtra("lng",0);
                url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=1000&keyword="+text+"&key="+Constants.GOOGLE_PLACES_API_KEY;
                HandleResearchText(url,picMaxHeight, places,status,error_message);
                break;
        }
    }

    private void HandleResearchText(String url, int picMaxHeight, ArrayList<MyPlace> places, String status, String error_message){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        String jsonResponse="";
        Bitmap image;

        try {
            Response response = client.newCall(request).execute();
            jsonResponse = response.body().string();

            JSONObject root = new JSONObject(jsonResponse);
            status = root.getString("status");
            if (root.has("error_message"))
                error_message=root.getString("error_message");

            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String address = null;
                if (result.has("formatted_address"))
                    address = result.getString("formatted_address");
                else
                    address = result.getString("vicinity");
                String name = result.getString("name");
                String imageUrl = null;
                String imageString = "";
                if (result.has("photos")) {
                    JSONArray photos = result.getJSONArray("photos");
                    if (photos != null && photos.length() > 0) {
                        JSONObject photo = photos.getJSONObject(0);
                        String photo_reference = photo.getString("photo_reference");
                        imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + picMaxHeight + "&photoreference=" + photo_reference + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
                        image = Picasso.with(this).load(imageUrl).get();
                        imageString = new MyFunctions().encodeToBase64(image,Bitmap.CompressFormat.JPEG,100);
                    }
                }

                String id = result.getString("place_id");
                double rate = 0;
                if (result.has("rating"))
                    rate = result.getDouble("rating");
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                MyPlace place = new MyPlace(name, address, id, imageString, lat, lng, rate,null,null);
                places.add(place);
            }

            Intent finishedSearchIntent = new Intent("Finished search");
            finishedSearchIntent.putExtra("status",status);
            finishedSearchIntent.putExtra("error_message",error_message);
            finishedSearchIntent.putExtra("allresults", places);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedSearchIntent);

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
