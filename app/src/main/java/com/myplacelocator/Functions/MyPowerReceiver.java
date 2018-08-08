package com.myplacelocator.Functions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.myplacelocator.R;

import es.dmoral.toasty.Toasty;

import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MyPowerReceiver extends BroadcastReceiver {
    public MyPowerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String action = intent.getAction();
        if(action.equalsIgnoreCase(ACTION_POWER_CONNECTED)) {
            String message = context.getResources().getString(R.string.cable_connected);
            Toast connect = Toasty.custom(context, message, R.drawable.charge_battery, Color.parseColor("#000000"), Color.parseColor("#feadf6d2"), Toast.LENGTH_SHORT, true, true);
            connect.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            connect.show();
        }

        else if(action.equalsIgnoreCase(ACTION_POWER_DISCONNECTED)) {
            try {
                String message = context.getResources().getString(R.string.cable_disconnected);
                Toast disconnect = Toasty.custom(context, message, R.drawable.charge_battery, Color.parseColor("#000000"), Color.parseColor("#feadf6d2"), 100, true, true);
                disconnect.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                disconnect.show();
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
