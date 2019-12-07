package edu.uwi.comp6107.emrrespondant.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertHelper {

    /**
     * Method which presents a simple alert dialogue when called
     * @param context context where the call is made
     * @param title String. The title of the alert
     * @param message String. The alert message
     */
    public static void showSimpleAlertDiag(Context context, String title, String message) {

        // create an alert dialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // configure title and message for the alert
        builder.setTitle(title);
        builder.setMessage(message);

        // add acknowledgement button
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show alert
        builder.create().show();
    }
}

