package kr.co.timecapsule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

// 강명구
public class NoticeListActivity extends AppCompatActivity {
    ListView listView;
    ListViewAdapter adapter;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb.db";
    private SQLiteDatabase mDB;
    timecapsuleDbHelper mDbHelper;
    Cursor mCursor;
    String index;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        // DB생성 및 오픈
        mDbHelper = new timecapsuleDbHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDbHelper.getWritableDatabase();
        index = null;

        showList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String numberInt = item.getNumber();
                String titleStr = item.getTitle();
                String writerStr = item.getWriter();
                String dateStr = item.getDate();
                String viewsInt = item.getViews();
            }
        });
    }

    public void showList(){
        // adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter 달기
        listView = (ListView)findViewById(R.id.list_notice);
        listView.setAdapter(adapter);

        Cursor result = mDbHelper.getResult();
        String id;
        String number;
        String title;
        String writer;
        String date;
        String views;
        while (result.moveToNext()) {
            id = result.getString(0);
            number = result.getString(1);
            title = result.getString(2);
            writer = result.getString(3);
            date = result.getString(4);
            views = result.getString(5);

            // 여기서 id를 넣어줘야 하는지..;
            adapter.addItem(number, title, writer, date, views);
        }

        //adapter.addItem("1", "명구", "나", "2017-11-24 01:42","3" );
        adapter.notifyDataSetChanged();
    }

    public void writeNoticeClick(View view){
        Intent intent = new Intent(this, NoticeBoardActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}