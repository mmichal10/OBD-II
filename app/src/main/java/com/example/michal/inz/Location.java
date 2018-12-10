package com.example.michal.inz;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.michal.inz.fragments.MapsFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Location implements LocationListener {

    private Context appContext;
    public LocationManager locationManager;
    boolean ableLocation = false;
    android.location.Location location;
    double latitude;
    MapsFragment mapa;
    public double longitude;
    private static final long DISTANCE_CHANGE = 0; // 1 meter
    private static final long TIME_CHANGE = 0; // 1 sec

    public Location(Context context, MapsFragment mapsFragment){
        this.appContext = context;
        this.mapa = mapsFragment;
        startLocation();
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        mapa.updateLocation();
    }

    public android.location.Location startLocation() {

        try {
            //pobranie serwisu
            locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
            //sprawdzenie wlaczenia GPS
            boolean checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!checkGPS) {
                //nie ma wlaczonego GPS, nic nie robimy
            } else {
                if (checkGPS) {
                    location = null;
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            TIME_CHANGE,
                            DISTANCE_CHANGE, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
