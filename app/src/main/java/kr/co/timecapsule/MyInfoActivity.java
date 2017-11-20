package kr.co.timecapsule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MyInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_myinfo);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_myinfo);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_main){
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_notice) {
            // Handle the camera action
            intent = new Intent(this, CheckMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_write) {
            intent = new Intent(this, WriteActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {
            intent = new Intent(this, CheckMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_receive) {
            intent = new Intent(this, CheckMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_header) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_myinfo);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
