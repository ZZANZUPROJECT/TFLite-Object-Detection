package com.example.android.alarmapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 2017-01-26.
 */

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    private static LocationUtils _locationUtils;

    private Context mContext;

    private static double lat;
    private static double lon;

    private static int count=0;

    private LocationManager mLocationManager;

    private LocationUtils(Context c) {
        initLocation(c);
        mContext = c.getApplicationContext();
    }

    public static LocationUtils getInstance(Context c){
        if(_locationUtils==null){
            _locationUtils = new LocationUtils(c.getApplicationContext());
        }
        return _locationUtils;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void isStop(Context c) {
        if (Build.VERSION.SDK_INT >= 23&&
                ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
    }

    private void initLocation(Context c) {

        mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23&&
                ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEbalbe = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSEnable){
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    100,
                    1,
                    mLocationListener);
        }

        if(isNetworkEbalbe){
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    100,
                    1,
                    mLocationListener);
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            lat=location.getLatitude();
            lon=location.getLongitude();
            Log.d(TAG, "lat : "+lat+", lon : "+lon);
            Log.d(TAG, "count : "+ count);
            count++;
            if(count==3){
                if (Build.VERSION.SDK_INT >= 23&&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.removeUpdates(mLocationListener);
            }

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

    public static String getAddressFromCoordinate(Context context, double lat, double lon){
        String newAddress = "잘못된 위도, 경도이거나 네트워크가 불안정합니다.";
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> addresses;
        try{
            if(geocoder!=null){
                addresses=geocoder.getFromLocation(lat,lon,1);
                if(addresses!=null&&addresses.size()>0){
                    newAddress=addresses.get(0).getAddressLine(0).toString();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return newAddress;
    }
}