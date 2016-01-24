package hackspace.testtask.com.testtask.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import hackspace.testtask.com.testtask.rssDataBase.RssContract.RssEntry;

import java.util.ArrayList;
import java.util.List;

import hackspace.testtask.com.testtask.rssDataBase.RssDbHelper;

public class RssBusiness {
    public List<RssItem> getRssItems(Context context) {
        List<RssItem> rssItems = new ArrayList<>();

        RssDbHelper mDbHelper = new RssDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor c = db.query(RssEntry.TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int imageColIndex = c.getColumnIndex(RssEntry.COLUMN_NAME_IMAGE);
            int titleColIndex = c.getColumnIndex(RssEntry.COLUMN_NAME_TITLE);
            int descriptionColIndex = c.getColumnIndex(RssEntry.COLUMN_NAME_DESCRIPTION);

            do {
                rssItems.add(
                        new RssItem(
                                c.getString(titleColIndex),
                                c.getString(descriptionColIndex),
                                c.getString(imageColIndex)
                        ));
            } while (c.moveToNext());
        }

        c.close();
        mDbHelper.close();

        return rssItems;
    }

    public boolean setRssItems(List<RssItem> rssItems, Context context) {
        boolean isBdUpdated = true;
        RssDbHelper mDbHelper = new RssDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(RssEntry.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        for (int i = 0; i < rssItems.size(); i++) {
            values.put(RssEntry.COLUMN_NAME_IMAGE, rssItems.get(i).getImage());
            values.put(RssEntry.COLUMN_NAME_TITLE, rssItems.get(i).getTitle());
            values.put(RssEntry.COLUMN_NAME_DESCRIPTION, rssItems.get(i).getDescription());

            long newRowId = db.insert(
                    RssEntry.TABLE_NAME,
                    null,
                    values);
            if (newRowId == -1) {
                isBdUpdated = false;
            }
        }

        mDbHelper.close();
        return isBdUpdated;
    }

    public void delete(RssItem rssItem, Context context) {
        RssDbHelper mDbHelper = new RssDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        String selection = RssEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { rssItem.getTitle() };

        db.delete(RssEntry.TABLE_NAME, selection, selectionArgs);
    }
}
