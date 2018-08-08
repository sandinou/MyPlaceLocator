package com.myplacelocator.Functions;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

/**
 * Created by sandramac on 12/02/2018.
 */

public class MyAlertDialog {

    public MyAlertDialog() {
    }

    public  static void createAlertDialog(Context context, String title,int icon, String msg, String positiveText, String negativeText, final AlertDialogListener alertDialogListener){
        DialogInterface.OnClickListener clickListener = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title!=null)
            builder.setTitle(title);
        if (msg!=null)
            builder.setMessage(msg);
        if (icon!=0)
            builder.setIcon(icon);

        if (alertDialogListener!=null){
            clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            alertDialogListener.onPositive(dialog);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            alertDialogListener.onNegative(dialog);
                            break;
                    }
                }
            };
        }
        builder.setPositiveButton(positiveText,clickListener);
        builder.setNegativeButton(negativeText,clickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF000000, Color.parseColor("#d0b9d4")));

        TextView textView = (TextView)alertDialog.findViewById(android.R.id.message);
        textView.setTextSize(18);
    }


    public interface  AlertDialogListener{
        void onPositive(DialogInterface dialogInterface);
        void onNegative(DialogInterface dialogInterface);
    }
}
