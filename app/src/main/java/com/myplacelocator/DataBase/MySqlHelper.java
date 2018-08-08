package com.myplacelocator.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.myplacelocator.FragmentChanger;
import com.myplacelocator.Functions.CheckConnection;
import com.myplacelocator.Functions.Constants;
import com.myplacelocator.Functions.MyFunctions;
import com.myplacelocator.Functions.MyPlace;
import com.myplacelocator.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


/**
 * Created by sandramac on 12/02/2018.
 */

public class MySqlHelper extends SQLiteOpenHelper{

    public MySqlHelper(Context context) {
        super(context, "my.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE "+ Constants.FAVORITE_TABLE_NAME+
                " ("+Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ Constants.ID_PLACE+" TEXT, "
                + Constants.NAME_PLACE+" TEXT, "+Constants.LAT_PLACE+" REAL,"+ Constants.LONG_PLACE+" REAL, "
                + Constants.ADDRESS_PLACE+" TEXT, "+ Constants.IMAGE_URL+" TEXT, "+Constants.RATE_PLACE+" REAL, "+Constants.TEL_PLACE+" TEXT, "+Constants.SITE_PLACE+" TEXT)";
        db.execSQL(sql1);

        String sql2 = "CREATE TABLE "+Constants.LASTSEARCH_TABLE_NAME+
                " ("+Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ Constants.ID_PLACE+" TEXT, "
                + Constants.NAME_PLACE+" TEXT, "+Constants.LAT_PLACE+" REAL,"+ Constants.LONG_PLACE+" REAL, "
                + Constants.ADDRESS_PLACE+" TEXT, "+ Constants.IMAGE_URL+" TEXT, "+Constants.RATE_PLACE+" REAL, "+Constants.TEL_PLACE+" TEXT, "+Constants.SITE_PLACE+" TEXT, "+Constants.SEARCH+" TEXT, "+Constants.NEAR+" INTEGER)";
        db.execSQL(sql2);

        String sql3 = "CREATE TABLE "+Constants.HISTORY_TABLE_NAME+
                " ("+Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+Constants.SEARCH+" TEXT, "+Constants.NEAR+" INTEGER)";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * Function to add place into Favorite Table
     * @param myPlace
     * @param context
     */
    public void addToFavorite(MyPlace myPlace, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME_PLACE,myPlace.getName());
        contentValues.put(Constants.ID_PLACE,myPlace.getId());
        contentValues.put(Constants.ADDRESS_PLACE,myPlace.getAddress());
        contentValues.put(Constants.LAT_PLACE, myPlace.getLat());
        contentValues.put(Constants.LONG_PLACE,myPlace.getLng());
        contentValues.put(Constants.IMAGE_URL, myPlace.getImageUrl());
        contentValues.put(Constants.RATE_PLACE,myPlace.getRate());
        contentValues.put(Constants.TEL_PLACE, myPlace.getTel());
        contentValues.put(Constants.SITE_PLACE, myPlace.getWebsite());
        db.insert(Constants.FAVORITE_TABLE_NAME,null,contentValues);
        String message = context.getResources().getString(R.string.add_toFavorites);
        Toast addToFav = Toasty.custom(context, message,R.drawable.favorites_icon, Color.parseColor("#000000"), Color.parseColor("#feadf6d2"), 100, true, true);
        addToFav.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        addToFav.show();
    }

    /**
     * Function to add row into History Table
     * @param mySearch
     * @param near
     */
    public void addToHistory(String mySearch, int near){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.SEARCH, mySearch);
        contentValues.put(Constants.NEAR, near);
        db.insert(Constants.HISTORY_TABLE_NAME, null, contentValues);
    }

    /**
     * Function to add the last search into the LastSearch Table
     * @param myPlace
     * @param search
     * @param near
     * @param context
     */
    public void addToLastSearch(MyPlace myPlace, String search,int near, Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.ID_PLACE, myPlace.getId());
        contentValues.put(Constants.NAME_PLACE,myPlace.getName());
        contentValues.put(Constants.ADDRESS_PLACE,myPlace.getAddress());
        contentValues.put(Constants.LAT_PLACE,myPlace.getLat());
        contentValues.put(Constants.LONG_PLACE,myPlace.getLng());
        contentValues.put(Constants.IMAGE_URL,myPlace.getImageUrl());
        contentValues.put(Constants.RATE_PLACE,myPlace.getRate());
        contentValues.put(Constants.TEL_PLACE, myPlace.getTel());
        contentValues.put(Constants.SITE_PLACE, myPlace.getWebsite());
        contentValues.put(Constants.SEARCH, search);
        contentValues.put(Constants.NEAR,near);
        context.getContentResolver().insert(PlacesContract.LastSearchResults.CONTENT_URI,contentValues);
    }

    /**
     * Function to get all information about a favorite place
     * @param adapter
     * @param position
     * @return
     */
    public MyPlace getPlaceToFavory(SimpleCursorAdapter adapter, int position){
        adapter.getCursor().moveToPosition(position);
        String myname = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.NAME_PLACE));
        String myid = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.ID_PLACE));
        String myaddress = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.ADDRESS_PLACE));
        double mylat = adapter.getCursor().getDouble(adapter.getCursor().getColumnIndex(Constants.LAT_PLACE));
        double mylong = adapter.getCursor().getDouble(adapter.getCursor().getColumnIndex(Constants.LONG_PLACE));
        String myimage = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.IMAGE_URL));
        double myrate = adapter.getCursor().getDouble(adapter.getCursor().getColumnIndex(Constants.RATE_PLACE));
        String mytel = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.TEL_PLACE));
        String mysite = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.SITE_PLACE));

        MyPlace place = new MyPlace(myname,myaddress,myid,myimage,mylat,mylong,myrate,mytel,mysite);
        return place;
    }

    /**
     * Function to search from History item
     * @param adapter
     * @param position
     * @param fragmentChanger
     * @param context
     */
    public void searchFromHistory(SimpleCursorAdapter adapter, int position, FragmentChanger fragmentChanger, final Context context){
        adapter.getCursor().moveToPosition(position);
        String search = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(Constants.SEARCH));
        int near = adapter.getCursor().getInt(adapter.getCursor().getColumnIndex(Constants.NEAR));

        CheckConnection checkConnection = new CheckConnection(context);
        if (checkConnection.isNetworkAvailable())
            fragmentChanger.changeFragmentSearch(search,near,true);
        else
            new MyFunctions().historyMessage(context);
    }

    public ArrayList<MyPlace> displayLastSearch(Cursor cursor, ArrayList<MyPlace> allResults){
        cursor.moveToFirst();

        do {
            String myname = cursor.getString(cursor.getColumnIndex(Constants.NAME_PLACE));
            String myid = cursor.getString(cursor.getColumnIndex(Constants.ID_PLACE));
            String myaddress = cursor.getString(cursor.getColumnIndex(Constants.ADDRESS_PLACE));
            double mylat = cursor.getDouble(cursor.getColumnIndex(Constants.LAT_PLACE));
            double mylong = cursor.getDouble(cursor.getColumnIndex(Constants.LONG_PLACE));
            String myimage = cursor.getString(cursor.getColumnIndex(Constants.IMAGE_URL));
            double myrate = cursor.getDouble(cursor.getColumnIndex(Constants.RATE_PLACE));
            String mytel = cursor.getString(cursor.getColumnIndex(Constants.TEL_PLACE));
            String mysite = cursor.getString(cursor.getColumnIndex(Constants.SITE_PLACE));
            MyPlace place = new MyPlace(myname, myaddress, myid, myimage, mylat, mylong, myrate, mytel, mysite);
            allResults.add(place);
        } while (cursor.moveToNext());
        return allResults;
    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
