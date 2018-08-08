package com.myplacelocator;

import com.myplacelocator.Functions.MyPlace;

/**
 * Created by sandramac on 12/02/2018.
 */

public interface FragmentChanger {

    void changeFragmentsMap(MyPlace place, boolean fav, boolean full);
    void changeFragmentSearch(String mySearch, int near, boolean history);
}
