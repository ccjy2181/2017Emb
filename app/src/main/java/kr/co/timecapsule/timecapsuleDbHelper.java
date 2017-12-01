package kr.co.timecapsule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static kr.co.timecapsule.FeedReaderContract.SQL_CREATE_ENTRIES;
import static kr.co.timecapsule.FeedReaderContract.SQL_DELETE_ENTRIES;

/**
 * Created by mg556 on 2017-11-29.
 */

public class timecapsuleDbHelper extends SQLiteOpenHelper {
    String _id;
    String _number;
    String _title;
    String _writer;
    String _contents;
    String _date;
    String _views;

    public timecapsuleDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // 이부분 잘 모르겠네요 일단은 안드로이드 developers(SQL 데이터베이스에 데이터 저장)를 참고
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
        onCreate(db);
    }

    // insert
    public void insert(String number, String title, String writer, String contents, String date, String views) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO my_table VALUES (null, '" + number + "', '" + title + "', '" + contents + "' , '" +
                writer + "', '" + date + "', '" + views +"');");
        db.close();
    }

    // update
    // NoticeBoardActivity에서는 title이랑 contents만 받는데 여기서 이렇게 많이 필요한가?
    // writer랑 date랑 views는 어떻게 업데이트가 되어야 되는가?
    // id와 number는 하나씩 자동으로 증가해주어야한다.
    public void update(String id, String number, String title, String writer, String contents, String date, String views) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE my_table SET number='" + number + "' WHERE _id= " + id + ";");
        db.execSQL("UPDATE my_table SET title='" + title + "' WHERE _id= " + id + ";");
        db.execSQL("UPDATE my_table SET contents='" + contents + "' WHERE _id= " + id + ";");
        db.execSQL("UPDATE my_table SET writer='" + writer + "' WHERE _id= " + id + ";");
        db.execSQL("UPDATE my_table SET date='" + date + "' WHERE _id= " + id + ";");
        db.execSQL("UPDATE my_table SET title='" + views + "' WHERE _id= " + id + ";");
        db.close();
    }

    // delete
    public void delete(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM my_table WHERE _id="+ id + ";");
        db.close();
    }

    public Cursor getResult() {
        SQLiteDatabase db = getWritableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + "my_table", null);
        return cursor;
    }

    // getId는 못하는건가??

    public String getNumber(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _number;
    }

    public String getTitle(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _title;
    }

    public String getWriter(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _writer;
    }

    public String getContents(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _writer;
    }

    public String getDate(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _date;
    }

    public String getViews(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "my_table WHERE _id =" + id, null);
        while (result.moveToNext()) {
            _id = result.getString(0);
            _number = result.getString(1);
            _title = result.getString(2);
            _contents = result.getString(3);
            _writer = result.getString(4);
            _date = result.getString(5);
            _views = result.getString(6);
        }
        return _views;
    }

}
