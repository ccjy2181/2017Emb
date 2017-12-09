package kr.co.timecapsule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mg556 on 2017-12-07.
 */

public class IDListDbHelper extends SQLiteOpenHelper {

    public IDListDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IDLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, loginID TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS IDLIST;");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS IDLIST;");
        onCreate(db);
    }

    public void update(String contents) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE IDLIST SET loginID='" + contents + "' WHERE _id=1; ");
        db.close();
    }
}
