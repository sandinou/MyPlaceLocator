package com.myplacelocator.Functions;

/**
 * Created by sandramac on 12/02/2018.
 */

public class Constants {

    public static final String GOOGLE_PLACES_API_KEY ="AIzaSyD66CkPYwPnSXMfmdPTRr_g5_758iQouS0";
    public static final String SHARED_PREFERENCES_UNIT = "unit";

    public static int FAVORITES_LOADER_ID = 12;
    public static int HISTORY_LOADER_ID = 77;

    public static final String FAVORITE_TABLE_NAME = "myfavorites";   // name of the sql table
    public static final String LASTSEARCH_TABLE_NAME = "mylastSearch";   // name of the sql table
    public static final String HISTORY_TABLE_NAME = "myhistory";    // name of the sql table

    // Columns of myfavorites and mylast Search tables
    public static String ID = "_id";
    public static String ID_PLACE = "place_id";
    public static String NAME_PLACE = "name";
    public static String ADDRESS_PLACE = "address";
    public static String LAT_PLACE = "lat";
    public static String LONG_PLACE = "lng";
    public static String IMAGE_URL = "image";
    public static String RATE_PLACE = "rate";
    public static String TEL_PLACE = "tel";
    public static String SITE_PLACE = "website";

    // Colums of myhistory table
    public static String SEARCH = "search";
    public static String NEAR = "near";


}
