package hackspace.testtask.com.testtask.services;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import hackspace.testtask.com.testtask.rss.RssItem;

public class RssParser {
    private ArrayList<RssItem> parsedItems = new ArrayList<>();
    private String title = "";
    private String description = "";
    private String image = null;
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public RssParser(String url){
        this.urlString = url;
    }

    public ArrayList<RssItem> getParsedItems() {
        return parsedItems;
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

                            if (title != "" && description != "") {
                                parsedItems.add(new RssItem(title, description, image));
                                title = "";
                                description = "";
                                image = null;
                            }

                            title = text;
                        }

                        else if(name.equals("description")){
                            description = text;
                        }

                        else if (name.equals("enclosure") && image == null) {
                            attributeName = myParser.getAttributeName(0);
                            if (attributeName.equals("url")) {
                                text = myParser.getAttributeValue(0);
                                image = text;
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
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

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
