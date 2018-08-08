package com.myplacelocator.Functions;

import android.os.AsyncTask;

import com.myplacelocator.OnLoadFinishedListener;

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

public class MyDetailsDownloader extends AsyncTask<String, Object, CompletePlaceData> {

    private CompletePlaceData completePlaceData;

    private OnLoadFinishedListener listener;


    public MyDetailsDownloader(CompletePlaceData placeData) {
        this.completePlaceData = placeData;
    }

    public void setOnLoadFinishedListener(OnLoadFinishedListener listener){
        this.listener = listener;
    }

    @Override
    protected CompletePlaceData doInBackground(String... params) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+completePlaceData.fullPlace.getId()+"&key="+Constants.GOOGLE_PLACES_API_KEY;
        Request request = new Request.Builder().url(url).build();
        String jsonResponse="";

        try {
            ArrayList<String> data = new ArrayList<>();
            client.newCall(request).execute();
            Response response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject result = root.getJSONObject("result");

            if (result.has("international_phone_number"))
                data.add(result.getString("international_phone_number"));

            else
                data.add("N/A");

            if (result.has("website"))
                data.add(result.getString("website"));
            else
                data.add("N/A");

            completePlaceData.allresult=data;
            return  completePlaceData;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(CompletePlaceData data) {
        super.onPostExecute(data);
        if (data!=null){
            listener.DataReady(data);
        }
    }
}
