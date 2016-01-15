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
    private static List<RssItem> rssItems;

    public static List<RssItem> getRssItems(Context context) {
        rssItems = new ArrayList<>();


        return rssItems;
    }

    public static List<RssItem> delete(RssItem rssItem, Context context) {

        rssItems.remove(rssItem);
        return rssItems;
    }
}
