package com.madprateek.dummyproject.HelperClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class DeviceLocation extends AppCompatActivity{

    String mLocation;
    int REQUEST_LOCATION = 10;
    LocationListener locationListener;
    LocationManager locationManager;
    Context context;

    public DeviceLocation(Context context) {
        this.context = context;
    }

    public String getLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLocation = "Latitude : " + location.getLatitude() + " , " + "Longitude : " + location.getLongitude();
                Log.v("TAG","Value of mLocation in onLocationChanged :" + mLocation);
                Toast.makeText(context, "Return inside onLocation" + mLocation, Toast.LENGTH_SHORT).show();
                Log.v("TAG", "Latitude : " + location.getLatitude() + " , " + "Longitude : " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_LOCATION);

        } else {
            Log.v("TAG","Entered in else");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            return mLocation;
        }

        Log.v("TAG","returning value is :" + mLocation);
        Toast.makeText(context, "Return outside onLocation" + mLocation, Toast.LENGTH_SHORT).show();
        return mLocation;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }

        }
    }

}








