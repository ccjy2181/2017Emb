package kr.co.timecapsule.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import java.util.List;

import kr.co.timecapsule.CodeConfig;
import kr.co.timecapsule.DetailMessageActivity;
import kr.co.timecapsule.MainActivity;
import kr.co.timecapsule.R;
import kr.co.timecapsule.WriteActivity;
import kr.co.timecapsule.dto.MessageDTO;
import kr.co.timecapsule.firebase.MyFirebaseConnector;
import kr.co.timecapsule.gps.CurrentLocation;
import kr.co.timecapsule.gps.GPSTracker;

import static android.content.Context.MODE_PRIVATE;

public class FragmentMap extends Fragment implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener {
    private MyFirebaseConnector myFirebaseConnector;
    private SharedPreferences sharedPreferences;

    // GPSTracker class
    GPSTracker gps = null;

    public Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;

    CodeConfig codeConfig = new CodeConfig();
    MapView mapView;
    RelativeLayout mapViewContainer;
    MapPoint mapPoint;
    FloatingActionButton fab_write;
    String token;

    MapPoint myLocation;

    double[] location = {0,0};
    private double latitude, longitude;

    public FragmentMap(){
        setHasOptionsMenu(true);
    }
    public void onCreate(Bundle a){
        super.onCreate(a);

        sharedPreferences = this.getContext().getSharedPreferences("user", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        //view.bringToFront();

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "남겨진 메시지");

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( getActivity() , new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        fab_write = (FloatingActionButton) view.findViewById(R.id.map_write);
        fab_write.setVisibility(View.VISIBLE);
        fab_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPoint = mapView.getMapCenterPoint();
                location[0] = mapPoint.getMapPointGeoCoord().longitude;
                location[1] = mapPoint.getMapPointGeoCoord().latitude;
                Intent intent = new Intent(getContext(), WriteActivity.class);
                intent.putExtra("longitude", location[0]);
                intent.putExtra("latitude", location[1]);
                startActivity(intent);
            }
        });

        mapView = new MapView(this.getContext());

        mapViewContainer = (RelativeLayout)view.findViewById(R.id.map_view);

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.refresh){
            if(gps == null) {
                gps = new GPSTracker(getContext(), mHandler);
            }else{
                gps.Update();
            }

            // check if GPS enabled
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
            }
            setMyLocationMarker();
        }
        return true;
    }

    public void makeNewGpsService(){
        if(gps == null) {
            gps = new GPSTracker(getContext(),mHandler);
        }else{
            gps.Update();
        }

    }

    public void setMyLocationMarker() {
        myLocation = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        System.out.println("####################################");
        System.out.println(latitude + "," + longitude);
        System.out.println("####################################");

        MapPOIItem customMarker = new MapPOIItem();
        makeCustomMarker(myLocation, customMarker);
    }

    public void makeCustomMarker(MapPoint mapPoint, MapPOIItem customMarker){
        deleteCustomMarker(customMarker);
        customMarker.setItemName("현 위치");
        customMarker.setTag(1);
        customMarker.setMapPoint(mapPoint);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.my_location_marker); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        customMarker.setShowCalloutBalloonOnTouch(false);

        mapView.addPOIItem(customMarker);
    }

    public void deleteCustomMarker(MapPOIItem customMarker){
        mapView.removePOIItem(customMarker);
    }

    public void resetFragment(){
        mapViewContainer.removeView(mapView);
        if(mapViewContainer.indexOfChild(mapView) == -1) {
            initMapView();
        }
    }

    public void initMapView(){
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setPOIItemEventListener(this);
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(), true);

        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//        mapViewContainer.addView(mapView, 0);

        mapViewContainer.addView(this.mapView, 0);
        //mapViewContainer.setVisibility(View.GONE);

        myFirebaseConnector = new MyFirebaseConnector("message", this.getContext());
        myFirebaseConnector.getMarkerData(mapView);
    }

    @Override
    public void onResume() {
        if(mapViewContainer.indexOfChild(mapView) == -1) {
            initMapView();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // MapView.MapViewEventListener
    @Override
    public void onMapViewInitialized(MapView mapView) {
        location[0] = location[0] == 0 ? CodeConfig.MJU_LATITUDE : location[0];
        location[1] = location[1] == 0 ? CodeConfig.MJU_LONGITUDE : location[1];
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(location[0], location[1]), 3, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        MessageDTO messageDTO = (MessageDTO) mapPOIItem.getUserObject();
//        if(!messageDTO.getUser().equals(token)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("messageDTO", messageDTO);
//
            Intent intent = new Intent(getContext(), DetailMessageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
//        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        this.mapView.setMapCenterPointAndZoomLevel(mapPoint, 3, true);
        this.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapViewContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }
}
