package kr.co.timecapsule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
