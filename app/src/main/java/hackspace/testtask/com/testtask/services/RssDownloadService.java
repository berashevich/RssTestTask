package hackspace.testtask.com.testtask.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import hackspace.testtask.com.testtask.R;
import hackspace.testtask.com.testtask.rss.RssBusiness;
import hackspace.testtask.com.testtask.rssDataBase.RssDbHelper;
import hackspace.testtask.com.testtask.rssDataBase.RssContract.RssEntry;

public class RssDownloadService extends IntentService {
    public static final String SERVICE_UPDATING_BD = "hackspace.testtask.com.services.UPDATING";
    public static final String BD_UPDATED = "hackspace.testtask.com.services.UPDATED";
    public static final String SERVICE_FINISHED = "hackspace.testtask.com.services.FINISHED";
    public static final String SERVICE_ERROR = "ERROR";

    private RssParser mRssParser;
    private String mUrl="http://lenta.ru/rss/news";

    public RssDownloadService() {
        super("RssDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Intent intentToSend = new Intent(SERVICE_UPDATING_BD);
            sendBroadcast(intentToSend);

            mRssParser = new RssParser(mUrl);
            mRssParser.fetchXML();

            while (mRssParser.parsingComplete);

            boolean isBdUpdated = new RssBusiness().setRssItems(mRssParser.getParsedItems(), this);

            if (isBdUpdated) {
                intentToSend = new Intent(BD_UPDATED);
                sendBroadcast(intentToSend);
            }

            intentToSend = new Intent(SERVICE_FINISHED);
            sendBroadcast(intentToSend);

        } catch (Exception e) {
            Intent intentToSend = new Intent(SERVICE_FINISHED);
            intentToSend.putExtra(SERVICE_ERROR, getString(R.string.error_message) + e.getMessage());
            sendBroadcast(intentToSend);
        } finally {
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }
}
