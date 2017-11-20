package kr.co.timecapsule;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//로딩화면 2초후 메인엑티비티전환

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        try{
            Thread.sleep(2000); //
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
