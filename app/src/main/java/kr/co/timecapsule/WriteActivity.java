package kr.co.timecapsule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.timecapsule.dto.MessageDTO;
import kr.co.timecapsule.firebase.MyFirebaseConnector;
import kr.co.timecapsule.system.ImageManager;
import kr.co.timecapsule.system.Property;

import static net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord;

public class WriteActivity extends Activity implements MapView.MapViewEventListener{
    double longitude;
    double latitude;

    MapView mapView;
    RelativeLayout mapViewContainer;
    MapPoint mapPoint;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String user_id;

    EditText year, month, day, hour, minute;
    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    MyFirebaseConnector myFirebaseConnector;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write);

        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        longitude = intent.getDoubleExtra("longitude",0);
        latitude = intent.getDoubleExtra("latitude",0);
        super.onCreate(savedInstanceState);

        ImageView map_img = (ImageView) findViewById(R.id.write_map);

        ImageManager imageManager = new ImageManager();

        mapPoint = mapPointWithGeoCoord(latitude,longitude);
        MapPoint.PlainCoordinate wcongMap = mapPoint.getMapPointWCONGCoord();

        map_img.setImageBitmap(imageManager.decodingImageData(imageManager.encodingImageData(Property.MAP_IMAGE_URL + "&MX=" + (int)wcongMap.x + "&MY=" + (int)wcongMap.y + "&CX=" + (int)wcongMap.x + "&CY=" + (int)wcongMap.y)));

        TextView tv_long = (TextView)findViewById(R.id.longitude);
        TextView tv_lati = (TextView)findViewById(R.id.latitude);
        tv_long.setText(Double.toString(longitude));
        tv_lati.setText(Double.toString(latitude));

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        user_id = firebaseUser.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }


    public String getTitleInput(){
        EditText title = (EditText)findViewById(R.id.write_title);
        String title_str = title.getText().toString();
        return title_str;
    }

    public String getContentInput(){
        EditText content = (EditText)findViewById(R.id.write_content);
        String content_str = content.getText().toString();
        return content_str;
    }

    public void ButtonWriteClicked(View view) {

        year = (EditText) findViewById(R.id.write_year);
        month = (EditText) findViewById(R.id.write_month);
        day = (EditText) findViewById(R.id.write_day);
        hour = (EditText) findViewById(R.id.write_hour);
        minute = (EditText) findViewById(R.id.write_minute);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(year.getText().equals("") || month.getText().equals("") || day.getText().equals("")||
                hour.getText().equals("") || minute.getText().equals("") ){
            Toast.makeText(getApplicationContext(), "비어있는 값이 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(month.getText().toString())> 12 ||
                Integer.parseInt(day.getText().toString()) > 31 ||
                Integer.parseInt(hour.getText().toString()) > 24 ||
                Integer.parseInt(minute.getText().toString()) > 60 ) {
            Toast.makeText(getApplicationContext(), "입력 가능 범위를 초과합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date_temp = year.getText().toString() + "-" + month.getText().toString() + "-" + day.getText().toString() + " " +
                            hour.getText().toString() + ":" + minute.getText().toString();
        try {
            Date date = transFormat.parse(date_temp);
            Date current_time = transFormat.parse(transFormat.format(new Date(System.currentTimeMillis())));

            int result = current_time.compareTo(date);
            if(result > 0 ) {
                Toast.makeText(getApplicationContext(), "현재 시간보다 입력한 날짜가 더 이전입니다.", Toast.LENGTH_SHORT).show();
                return;
            } else if(result < 0 ){
            } else {}

        } catch (ParseException e) {
            e.printStackTrace();
        }


//        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUser(firebaseUser.getUid());
        messageDTO.setTitle(getTitleInput());
        messageDTO.setContents(getContentInput());
        messageDTO.setLocation_latitude(latitude);
        messageDTO.setLocation_longitude(longitude);
        messageDTO.setReceive_date(new Date());
        MapPoint.PlainCoordinate wcongMap = mapPoint.getMapPointWCONGCoord();
        ImageManager imageManager = new ImageManager();
        messageDTO.setImage_string(imageManager.encodingImageData(Property.MAP_IMAGE_URL + "&MX=" + (int)wcongMap.x + "&MY=" + (int)wcongMap.y + "&CX=" + (int)wcongMap.x + "&CY=" + (int)wcongMap.y));

        myFirebaseConnector = new MyFirebaseConnector("message");
        String key = myFirebaseConnector.insertData(messageDTO).getKey();
        databaseReference.child("message").child(key+"/msgkey").setValue(key);
        databaseReference.child("writtenby").child(auth.getCurrentUser().getUid()+"/msgkey").setValue(key);

        finish();
    }

    public void ButtonCancelClicked(View view) {
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

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
        return;
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }
}
