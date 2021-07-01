package com.example.myshop.InternetUtility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.myshop.InternetUtility.ConnectionChecker;
import com.example.myshop.R;

/* This class listens for internet connection and show the error pop dialog
 */
public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(!ConnectionChecker.isConnectedToInternet(context)) { //Internet is not connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View connection_error_layout = LayoutInflater.from(context)
                    .inflate(R.layout.activity_connection_error, null);
            builder.setView(connection_error_layout);

            AppCompatButton btnRetry = connection_error_layout.findViewById(R.id.ce_retry);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(v -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}
