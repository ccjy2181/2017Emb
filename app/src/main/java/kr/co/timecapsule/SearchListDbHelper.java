package kr.co.timecapsule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mg556 on 2017-12-07.
 */

public class SearchListDbHelper extends SQLiteOpenHelper {
    String _id;
    String _searchedList;

    public SearchListDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SEARCHLIST_DB (_id INTEGER PRIMARY KEY AUTOINCREMENT, searchedList TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SEARCHEDLIST_DB;");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS SEARCHEDLIST_DB;");
        onCreate(db);
    }

    public void insert(String searchedList){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO SEARCHEDLIST_DB VALUES (null, '" + searchedList + "');");
        db.close();
    }

    public void update(String id, String searchedList) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE SEARCHEDLIST_DB SET loginID='" + searchedList + "' WHERE _id= " + id + ";");
        db.close();
    }

    public void delete(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM SEARCHEDLIST_DB WHERE _id=" +id + ";");
        db.close();
    }

    public Cursor getResult(){
        SQLiteDatabase db = getWritableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + "SEARCHEDLIST_DB", null);
        return cursor;
    }

    public String getLoginID(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + "SEARCHEDLIST_DB WHERE _ID =" + id, null);
        while (result.moveToNext()){
            _id = result.getString(0);
            _searchedList= result.getString(1);
        }
        return _searchedList;
    }
}
