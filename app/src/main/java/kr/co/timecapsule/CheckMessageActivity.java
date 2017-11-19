package kr.co.timecapsule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CheckMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
