package kr.co.timecapsule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import net.daum.mf.map.api.MapPoint;

import kr.co.timecapsule.fragments.FragmentMap;
import kr.co.timecapsule.fragments.FragmentMyInfo;
import kr.co.timecapsule.gps.CurrentLocation;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView chats;
    NavigationView navigationView, navigationViewBottom;
    DrawerLayout drawer;
    MapPoint current_mp;
    AlertDialog dialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private LocationManager locationManager;

    double[] location = {0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 현재 로그인 정보를 불러옴
        mAuth = FirebaseAuth.getInstance();

        // 현재 로그인한 유저의 정보를 불러옴
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // 만약 로그아웃이 수행되면 로그인화면으로 이동
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        setupToolbar(R.id.toolbar, "");

        FragmentTransaction ft;
        FragmentMap fragmentMap = new FragmentMap();
        ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.frameLayout, fragmentMap).commit();
        ft.replace(R.id.frameLayout, fragmentMap).commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                            signOut();
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
    public void signOut(){
        mAuth.signOut();
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
        } else if (id == R.id.nav_my_answer) {
        } else if (id == R.id.nav_bookmark) {
        } else if (id == R.id.nav_rank) {
        } else if (id == R.id.nav_manage){
        } else if (id == R.id.nav_logout){
            // 로그아웃
            signOut();
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