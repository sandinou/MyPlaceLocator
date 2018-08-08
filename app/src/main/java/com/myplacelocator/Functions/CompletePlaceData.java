package com.myplacelocator.Functions;

import java.util.ArrayList;

/**
 * Created by sandramac on 12/02/2018.
 */

public class CompletePlaceData {
    public MyPlace fullPlace;
    public   ArrayList<String> allresult;

    public CompletePlaceData(MyPlace fullPlace, ArrayList<String> allresult) {
        this.fullPlace = fullPlace;
        this.allresult = allresult;
    }
}
