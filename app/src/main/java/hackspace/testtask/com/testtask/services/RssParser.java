package hackspace.testtask.com.testtask.services;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import hackspace.testtask.com.testtask.rss.RssItem;

public class RssParser {
    private ArrayList<RssItem> mParsedItems = new ArrayList<>();
    private String mTitle = "";
    private String mDescription = "";
    private String mImage = null;
    private String mUrl = null;
    private XmlPullParserFactory mXmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public RssParser(String url){
        this.mUrl = url;
    }

    public ArrayList<RssItem> getParsedItems() {
        return mParsedItems;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text = null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                String attributeName;

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(name.equals("title")){

                            if (mTitle != "" && mDescription != "") {
                                mParsedItems.add(new RssItem(mTitle, mDescription, mImage));
                                mTitle = "";
                                mDescription = "";
                                mImage = null;
                            }

                            mTitle = text;
                        }

                        else if(name.equals("description")){
                            mDescription = text;
                        }

                        else if (name.equals("enclosure") && mImage == null) {
                            attributeName = myParser.getAttributeName(0);
                            if (attributeName.equals("url")) {
                                text = myParser.getAttributeValue(0);
                                mImage = text;
                            }
                        }

                        break;
                }

                event = myParser.next();
            }

            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    URL url = new URL(mUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    mXmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = mXmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                }

                catch (Exception e) {
                }
            }
        });
        thread.start();
    }
}
