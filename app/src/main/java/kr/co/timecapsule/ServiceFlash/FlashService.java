package kr.co.timecapsule.ServiceFlash;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
public class FlashService extends Service{

    public IBinder onBind(Intent intent){
        return null;
    }

    public void onCreate(){
        super.onCreate();
        Flash.flash_on();
    }

    public void onDestroy(){
        super.onDestroy();
        Flash.flash_off();
    }
}