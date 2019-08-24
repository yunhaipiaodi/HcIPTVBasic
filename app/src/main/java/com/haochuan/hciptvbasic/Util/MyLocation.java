package com.haochuan.hciptvbasic.Util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class MyLocation {
    LocationManager locationManager;
    String TAG = "MyLocation";

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    public MyLocation(Context context){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void showProviders(){
        if(locationManager == null){
            Log.e(TAG,"LocationManager is null");
            return;
        }
        List<String> list = locationManager.getAllProviders();
        if(list != null){
            for(String name : list){
                Log.d(TAG,"provider name:" + name);
            }
        }
    }

    public void getLocation(Context context){
        if(locationManager == null){
            Log.e(TAG,"LocationManager is null");
            return;
        }
        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //如果是PASSIVE定位
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        }
        else {
            Log.e(TAG, "没有可用的位置提供器");
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 1000, 200, mListener);
        }
    }

    /**
     * 获取经纬度
     * @param location
     */
    private void showLocation(Location location) {
        String longtitude=String.valueOf(location.getLongitude());
        String latitude=String.valueOf(location.getLatitude());
        Log.e("经纬度信息：",longtitude+"  "+latitude);
    }
}
