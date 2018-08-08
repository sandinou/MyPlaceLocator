package com.myplacelocator.DataBase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MyPlaceProvider extends ContentProvider {

    MySqlHelper mySQLHelper;

    @Override
    public boolean onCreate() {
        mySQLHelper = new MySqlHelper(getContext());
        return true;
    }

    private String getTableName(Uri uri){
        List<String> segments = uri.getPathSegments();
        return segments.get(0);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        Cursor cursor = mySQLHelper.getReadableDatabase().query(table,projection,selection,selectionArgs,null,null,sortOrder);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getTableName(uri);
        mySQLHelper.getWritableDatabase().insert(table,null,values);
        getContext().getContentResolver().notifyChange(uri,null);

        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mySQLHelper.getWritableDatabase();
        int result = db.delete(getTableName(uri), selection, selectionArgs);

        // notify the change
        getContext().getContentResolver().notifyChange(uri, null);

        //return the number of rows deleted
        //it's what we got from the db.delete
        return result;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}
