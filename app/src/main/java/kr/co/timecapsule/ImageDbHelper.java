package kr.co.timecapsule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by mg556 on 2017-12-11.
 */

public class ImageDbHelper extends SQLiteOpenHelper{
    public ImageDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(SQLiteDatabase db) {
        firebaseAuth = FirebaseAuth.getInstance();
        System.out.println("ididididididididididididi: "+firebaseAuth.getCurrentUser().getUid());
        db.execSQL("CREATE TABLE IMAGETABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT, image BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS IMAGETABLE;");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS IMAGETABLE;");
        onCreate(db);
    }
}
