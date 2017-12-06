package kr.co.timecapsule.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Date;

import kr.co.timecapsule.dto.UserDTO;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        UserDTO userDTO = new UserDTO();
        userDTO.setToken(refreshedToken);
        userDTO.setRegdate(new Date());
//        MyFirebaseConnector myFirebaseConnector = new MyFirebaseConnector("user");
//        DatabaseReference resultData = myFirebaseConnector.insertData(userDTO);
//        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("token", resultData.getKey());
//        editor.commit();
    }
}
