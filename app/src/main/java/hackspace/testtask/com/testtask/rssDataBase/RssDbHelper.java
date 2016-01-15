package hackspace.testtask.com.testtask.rssDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import hackspace.testtask.com.testtask.rssDataBase.RssContract.RssEntry;

public class RssDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyRss";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RssEntry.TABLE_NAME + " (" +
                    RssEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RssEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    RssEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    RssEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RssEntry.TABLE_NAME;

    public RssDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
