package kr.co.timecapsule;

import android.provider.BaseColumns;

/**
 * Created by mg556 on 2017-11-27.
 */

public final class FeedReaderContract {
    public FeedReaderContract(){};

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " ( " +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FeedEntry.COLUMN_NAME_NUMBER + " text, " +
                    FeedEntry.COLUMN_NAME_TITLE + " text, " +
                    FeedEntry.COLUMN_NAME_CONTENTS + " text, " +
                    FeedEntry.COLUMN_NAME_WRITER + " text, " +
                    FeedEntry.COLUMN_NAME_DATE + " text, " +
                    FeedEntry.COLUMN_NAME_VIEWS + " text " + " )" ;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "my_table";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_WRITER = "writer";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_VIEWS = "views";
    }
}