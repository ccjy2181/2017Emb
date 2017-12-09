package kr.co.timecapsule;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.MapPoint;

import kr.co.timecapsule.dto.UserDTO;
import kr.co.timecapsule.firebase.MyFirebaseConnector;
import kr.co.timecapsule.fragments.FragmentMap;
import kr.co.timecapsule.fragments.FragmentMyInfo;
import kr.co.timecapsule.fragments.FragmentWriteMessage;
import kr.co.timecapsule.gps.CurrentLocation;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView chats, tv_nickname;
    NavigationView navigationView, navigationViewBottom;
    DrawerLayout drawer;
    MapPoint current_mp;
    AlertDialog dialog;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "loginID.db";
    private SQLiteDatabase ID_DB;
    private IDListDbHelper loginIDListDbHelper;  // login된 현재 current ID를 저장할 local DB
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private LocationManager locationManager;

    double[] location = {0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB초기화
        loginIDListDbHelper = new IDListDbHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        // DB를 읽고 쓰기 위해 DB를 연다
        ID_DB = loginIDListDbHelper.getWritableDatabase();
        // 현재 로그인 정보를 불러옴
        mAuth = FirebaseAuth.getInstance();

        // SQLiteDB에 저장할 current ID(이메일)
        String loginID = mAuth.getCurrentUser().getEmail();

        // ContentValues를 사용해 SQLite에 ID(이메일)을 저장
        ContentValues v = new ContentValues();
        v.put("loginID", loginID);
        ID_DB.update("IDLIST", v,"_id=1",null);  // 새로운loginID를 localDB에 update한다.

        // 현재 로그인한 유저의 정보를 불러옴
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };

        setupToolbar(R.id.toolbar, "");

        NavigationView nv = (NavigationView)findViewById(R.id.navigation_drawer_container);
        nv.bringToFront();

        FragmentTransaction ft;
        FragmentMap fragmentMap = new FragmentMap();
        ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.frameLayout, fragmentMap).commit();
        ft.replace(R.id.frameLayout, fragmentMap).commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.bringToFront();
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeaderItemSelected(v);
            }
        });

        navigationViewBottom = (NavigationView) findViewById(R.id.nav_view_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);
        // 사용자 정보(닉네임)을 받아오기 위해 firebase에 접근
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //firebase에 접근하여 실제 데이터를 받아와서 textview에 출력
        databaseReference.child("user").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tv_nickname = (TextView) findViewById(R.id.sidebar_nickname);
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);

                        if(user!=null) {
                            String nickname = user.getNickname();
                            tv_nickname.setText(nickname);
                        } else {
                            tv_nickname.setText("익명");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        setLocation();

//        chats =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
//                findItem(R.id.nav_chats));
//        initializeCountDrawer();

    }

    private void setLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        CurrentLocation currentLocation = new CurrentLocation(locationManager, this);
        currentLocation.setCurrentLocation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

            // 여기서 부터는 알림창의 속성 설정
            builder.setMessage("앱을 종료하시겠습니까?")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            // 앱 종료 확인 시 로그아웃 수행
                            mAuth.signOut();
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton){
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();    // 알림창 띄우기
        }
    }

    // 로그아웃 수행
    public void logOut(){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    // onStart에서 인증상태를 불러옴
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    // onStop시 인증상태 제거
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction ft;
        int id = item.getItemId();

        if (id == R.id.nav_all_question) {
            FragmentMap fragmentMap = new FragmentMap();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, fragmentMap).commit();
        } else if (id == R.id.nav_my_question) {
            FragmentWriteMessage fragmentWriteMessage = new FragmentWriteMessage();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, fragmentWriteMessage).commit();
        } else if (id == R.id.nav_my_answer) {
        } else if (id == R.id.nav_bookmark) {
        } else if (id == R.id.nav_rank) {
        } else if (id == R.id.nav_manage){
        } else if (id == R.id.nav_logout){
            // 로그아웃
            logOut();
        } else if (id == R.id.nav_exit){
            super.onBackPressed();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onHeaderItemSelected(View view) {
        // Handle navigation view item clicks here.
        FragmentTransaction ft;
        FragmentMyInfo fragmentMyInfo = new FragmentMyInfo();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragmentMyInfo).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}