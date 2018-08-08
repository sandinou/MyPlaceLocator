package com.myplacelocator.DataBase;

import android.net.Uri;

/**
 * Created by sandramac on 12/02/2018.
 */

public class PlacesContract {
    public static  class FavoritesPlaces{
        public static Uri CONTENT_URI = Uri.parse("content://com.myplacelocator/myfavorites");
    }
    public static  class LastSearchResults{
        public static Uri CONTENT_URI = Uri.parse("content://com.myplacelocator/mylastSearch");
    }

    public static class historySearch{
        public static Uri CONTENT_URI = Uri.parse("content://com.myplacelocator/myhistory");
    }

}
