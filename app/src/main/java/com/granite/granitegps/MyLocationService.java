package com.granite.granitegps;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;


public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.granite.granitegps.UPDATE_LOCATION";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    Location location = result.getLastLocation();
//                    String string_location = new StringBuilder("" + location.getLatitude())
//                            .append(" " + location.getLongitude()).toString();
                    try{

                        String latitude = " " + location.getLatitude();
                        String longitude = " " + location.getLongitude();

                        // trigger to end update to Server on MainActivity
                        MainActivity.getInstance().updateLocationToServer(latitude, longitude);
                        // trigger to update TextViews on MainActivity
                        MainActivity.getInstance().updateLocationText(latitude, longitude);
                    }
                    catch (Exception ex){

                    }
                }
            }
        }
    }


}
