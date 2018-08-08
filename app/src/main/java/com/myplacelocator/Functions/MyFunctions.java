package com.myplacelocator.Functions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.myplacelocator.ActivitiesAndFragments.MainActivity;
import com.myplacelocator.R;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MyFunctions{

    /**
     * Function to transform Bitmap into String
     * @param image
     * @param compressFormat
     * @param quality
     * @return string
     */
    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Function to transform String into Bitmap
     * @param input
     * @return bitmap
     */
    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Function to send place's information
     * @param place
     * @return
     */
    public String placeInfo(MyPlace place){
        String link = "https://www.google.co.il/maps/"+place.getId();
        String string = place.getName()+"\n"+place.getAddress()+"\n"+place.getTel()+"\n"+place.getWebsite()+"\n"+link;
        return string;
    }


    /**
     * Function to share place
     * @param myPlace
     * @param context
     */
    public void sharePlace(MyPlace myPlace, Context context){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, myPlace.getName());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, new MyFunctions().placeInfo(myPlace));
        context.startActivity(sharingIntent);
    }

    /**
     * Function to call the place number
     * @param num
     * @param context
     */
    public void callPlace(String num,Context context){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + num));
        context.startActivity(callIntent);

    }

    /**
     * Function to open the place's website
     * @param site
     * @param context
     */
    public void webSite(String site, Context context){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(site));
        context.startActivity(browserIntent);
    }

    public void historyMessage(final Context context){

        MyAlertDialog.createAlertDialog(context,context.getResources().getString(R.string.search_impossible),0,context.getString(R.string.no_history_search),context.getString(R.string.settings),context.getString(R.string.alert_dialog_negative), new MyAlertDialog.AlertDialogListener() {
            @Override
            public void onPositive(DialogInterface dialogInterface) {
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }

            @Override
            public void onNegative(DialogInterface dialogInterface) {
                String message = context.getResources().getString(R.string.no_connection);
                Toast connect = Toasty.custom(context, message,R.drawable.alert_icon, Color.parseColor("#000000"), Color.RED, Toast.LENGTH_SHORT, true, true);
                connect.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                connect.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(context,MainActivity.class);
                        context.startActivity(i);
                    }
                }, 2000);
            }
        });
    }

}
