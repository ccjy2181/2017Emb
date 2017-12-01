package kr.co.timecapsule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NoticeBoardActivity extends AppCompatActivity {
    timecapsuleDbHelper mSQLHelper;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb.db";
    EditText titleEt;
    EditText contentsEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard);

        Intent intent = getIntent();
        String position = intent.getStringExtra("position");

        mSQLHelper = new timecapsuleDbHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);

        if(position == null){
        }else{
            titleEt = (EditText)findViewById(R.id.titletext);
            contentsEt = (EditText)findViewById(R.id.contentstext);

            titleEt.setText(mSQLHelper.getTitle(position));
            contentsEt.setText(mSQLHelper.getContents(position));
        }
    }

    protected void onStart(){
        super.onStart();
    }

    public void postButtonClick(View view){

        EditText editTitleText = (EditText) findViewById(R.id.titletext);
        String titletext = editTitleText.getText().toString();

        EditText editContentsText = (EditText) findViewById(R.id.contentstext);
        String contentstext = editContentsText.getText().toString();

        // 실험용 update바꺼야됨
        // id랑 number랑 writer, date를 여기서 받아야겠네요.
        mSQLHelper.update("1", "1", titletext, "명구", contentstext, "2017-11-29", "3");

        Intent intent = new Intent(this, NoticeListActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
