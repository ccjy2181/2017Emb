package kr.co.timecapsule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.co.timecapsule.dto.MessageDTO;
import kr.co.timecapsule.dto.UserDTO;
import kr.co.timecapsule.system.ImageManager;

public class DetailMessageActivity extends Activity{
    String title, contents, user_id, writer;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    MessageDTO messageDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_message);

        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        Bundle bundle = intent.getExtras();
        messageDTO = (MessageDTO) bundle.getSerializable("messageDTO");

        title = messageDTO.getTitle();
        user_id = messageDTO.getUser();
        contents = messageDTO.getContents();

        databaseReference.child("user").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDTO user = dataSnapshot.getValue(UserDTO.class);
                if(user!=null) {
                    writer = user.getNickname();
                } else {
                    writer = "익명";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        super.onCreate(savedInstanceState);

        ImageView img = (ImageView)findViewById(R.id.detail_message_map);
        TextView tv_title = (TextView) findViewById(R.id.detail_message_title);
        TextView tv_contents = (TextView) findViewById(R.id.detail_message_contents);
        TextView tv_writer = (TextView) findViewById(R.id.detail_message_writer);

        ImageManager imageManager = new ImageManager();
        img.setImageBitmap(imageManager.decodingImageData(messageDTO.getImage_string()));
        tv_title.setText(title);
        tv_contents.setText(contents);
        tv_writer.setText(writer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public void ButtonCancelClicked(View view) {
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}