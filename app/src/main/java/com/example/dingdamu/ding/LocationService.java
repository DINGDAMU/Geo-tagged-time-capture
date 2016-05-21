package com.example.dingdamu.ding;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by dingdamu on 10/05/16.
 */
public class LocationService extends Service implements LocationListener {
    LocationManager manager;
    Location location;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
    LocationListener listener;


    public LocationService(Context context) {
        manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation(String s) {
        if (manager.isProviderEnabled(s)) {
            try {
                manager.requestLocationUpdates(s, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                if (manager != null) {
                    location = manager.getLastKnownLocation(s);
                    return location;
                }
            } catch (SecurityException e) {

            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void removeUpdates() {
        if (manager != null) {
            try {
                manager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        manager = null;
    }


    public void unregisterlistener() {
        if (listener != null) {
            listener = null;
        }
    }
}