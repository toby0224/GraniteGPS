package com.granite.granitegps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    static class GpsInfo {
        String emei;
        String dateTime;
        String latitude;
        String longitude;
    }

    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextView textTime;
    TextView textImei;
    TextView textLocation;
    TextView txtPermissionsRequire;
    String text_time = "Date & Time";
    String imeiNumber = "";
    Date tempStartTime = new Date();


    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        textTime = (TextView) findViewById(R.id.txt_time);
        textImei = (TextView) findViewById(R.id.txt_imei);
        textLocation = (TextView) findViewById(R.id.txt_location);
        txtPermissionsRequire = (TextView) findViewById(R.id.txt_permissionsRequire);

        getAllPermissions();

    }




    private void getAllPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener(){

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

                            imeiNumber =  getDeviceIMEI();
                            updateLocation();
                        }
                        else {
                            txtPermissionsRequire.setText("ALL PERMISSIONS ARE REQUIRED TO USE THE APP");
                            txtPermissionsRequire.setTextColor(Color.parseColor("#000000"));
                            txtPermissionsRequire.setBackgroundColor(Color.parseColor("#FFFF00"));
                            showSettingsDialog();
                        }

                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permissions Required");
        builder.setMessage("This app requires both permissions to work. Please grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }




    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);         //  20 seconds
        locationRequest.setFastestInterval(10 * 1000);   //  20 seconds
    }

    @SuppressLint("HardwareIds")
    public String getDeviceIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return telephonyManager.getDeviceId();
    }


    // This method send update to server
    public void updateLocationToServer(final String lat, final String lon) {
        String pattern = "MM/dd/yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentDateTime = simpleDateFormat.format(new Date());
        String latitude = lat;
        String longitude = lon;

        // send currentDateTime, imeiNumber, latitude, longitude to server.

    }





        // This method just to update Text View on Screen
        public void updateLocationText(final String lat, final String lon) {
            String pattern = "MM/dd/yyyy hh:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String currentDateTime = simpleDateFormat.format(new Date());
            text_time = currentDateTime;

            final String latitude = lat;
            final String longitude = lon;

            final String locationString = "Lat: " + latitude + " Long: "+ longitude;
            // count seconds
            int seconds = 0;
            Date endTime = new Date();
            Date startTime = tempStartTime;  // time seconds from last Toast message popped up.

            seconds = (int)((endTime.getTime() - startTime.getTime()) / 1000);

            Toast.makeText(MainActivity.this, "Update Location: " + seconds + "s", Toast.LENGTH_SHORT).show();
            // start timer since last Toast message popped up.
            tempStartTime = new Date();
            // update screen with values
            MainActivity.this.runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    textTime.setText(text_time);
                    textImei.setText( "IMEI: " + imeiNumber);
                    textLocation.setText(locationString);

                }
            });
        }

}

