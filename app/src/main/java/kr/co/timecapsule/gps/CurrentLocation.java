package kr.co.timecapsule.gps;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.timecapsule.firebase.MyFirebaseConnector;

public class CurrentLocation extends AppCompatActivity {

    Activity activity;
    LocationListener locationListener; // 위치 정보 변경시 리스너
    LocationManager locationManager; // 위치 정보를 요청하는 개체

    public CurrentLocation(LocationManager locationManager, Activity activity){
        this.locationManager = locationManager;
        this.activity = activity;
    }

    public void setCurrentLocation(){
        //GpsPermissionCheckForMashMallo();
        //System.out.println("!!!!!!!!!!!!!!!!!!!");
        if(locationManager!=null){ // 위치정보 수집이 가능한 환경인지 검사.
            //gps 또는 네트워크를 이용하여 위치정보 수집
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled || isNetworkEnabled) {
                //locationManager.getProviders 현재 디바이스에서 위치정보를 얻을 수 있는 제공자 리스트를 리턴합니다. ("GPS","NETWORK" 등이 있습니다)
                //true로 설정하면 현재 활성화되어있는  Provider의 이름을 리턴합니다. (활성화 Provider만 포함)
                //false로 설정하면 모든 Provider의 이름을 리턴합니다.(비활성화 Provider도 모두포함)
                final List<String> m_lstProviders = locationManager.getProviders(false);
                locationListener = new LocationListener() {
                    @Override
                    //위치 정보를 가져올 수 있는 메소드입니다.
                    //위치 이동이나 시간 경과 등으로 인해 호출됩니다.
                    //최신 위치는 location 파라메터가 가지고 있습니다.
                    //최신 위치를 가져오려면, location 파라메터를 이용하시면 됩니다.
                    public void onLocationChanged(Location location) {
                        Log.e("onLocationChanged", "onLocationChanged");
                        location.getProvider(); //gps인지, 네트워크인지
                        location.getLatitude(); //위도 , location.getLongitude() 경도
                        Log.e("location", "[" + location.getProvider() + "] (" + location.getLatitude() + "," + location.getLongitude() + ")");
                            /*
                            SharedPreferences prefs = activity.getSharedPreferences("mju_sns", MODE_PRIVATE);
                            String id = prefs.getString("id", "");
                            param.put("mode", "updateLocation");
                            param.put("id", id);
                            param.put("location_latitude", location.getLatitude());
                            param.put("location_longitude", location.getLongitude());
                            */
//                        Toast.makeText(activity, "타입"+location.getProvider() + "," + location.getLatitude() + "," +location.getLongitude(), Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = activity.getSharedPreferences("user", MODE_PRIVATE);
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("location_latitude", location.getLatitude());
                        userMap.put("location_longitude", location.getLongitude());

                        MyFirebaseConnector myFirebaseConnector;
                        myFirebaseConnector = new MyFirebaseConnector("user");
                        myFirebaseConnector.updateData(prefs.getString("token", ""), userMap);

                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.removeUpdates(locationListener);
                    }

                    @Override
                    //위치 공급자의 상태가 바뀔 때 호출 됩니다.
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.e("onStatusChanged", "onStatusChanged");
                    }

                    @Override
                    //위치 공급자가 사용 가능해질(enabled) 때 호출 됩니다.
                    public void onProviderEnabled(String provider) {
                        Log.e("onProviderEnabled", "onProviderEnabled");
                    }

                    @Override
                    //위치 공급자가 사용 불가능해질(disabled) 때 호출 됩니다
                    public void onProviderDisabled(String provider) {
                        Log.e("onProviderDisabled", "onProviderDisabled");

                    }
                };

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String name : m_lstProviders) {
                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            // QQQ: 시간, 거리를 0 으로 설정하면 가급적 자주 위치 정보가 갱신되지만 베터리 소모가 많을 수 있다.
                            //최소시간, 최소거리
                            locationManager.requestLocationUpdates(name, 0, 0, locationListener);
                        }

                    }
                });
                //gps 사용 선택이 안되어 있을때 핸들러
            } else {
                Log.e("GPS Enable", "false");

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        //startActivity(intent);
//                        activity.finish();
//                    }
//                });
            }
        }
    }

    //권한 체크 메소드
    public void GpsPermissionCheckForMashMallo() {
        Log.e("Tag", this.toString());
        Log.e("Tag2", activity.toString());
        //마시멜로우 버전 이하면 if문에 걸리지 않습니다.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog.setTitle("GPS 사용 허가 요청");
            alertDialog.setMessage("MJU Timecapsule의 원활한 사용을 위해 사용자의 GPS 허가가 필요합니다.\n('허가'를 누르면 GPS 허가 요청창이 뜹니다.)");
            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("허가",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                    });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("거절",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }
    }
}