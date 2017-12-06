package kr.co.timecapsule.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "";
        String message = "";
        if( remoteMessage.getNotification() == null   ){
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
        }
        else{
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }
    }
}
