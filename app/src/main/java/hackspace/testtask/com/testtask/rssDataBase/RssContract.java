package hackspace.testtask.com.testtask.rssDataBase;

import android.provider.BaseColumns;

public final class RssContract {
    public static abstract class RssEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    public RssContract() {}
}