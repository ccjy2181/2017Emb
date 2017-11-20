package kr.co.timecapsule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NoticeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
    }

    public void WriteNoticeClick(){
        Intent intent = new Intent(this, NoticeBoardActivity.class);
        startActivity(intent);
    }
}
