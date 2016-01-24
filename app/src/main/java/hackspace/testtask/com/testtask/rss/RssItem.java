package hackspace.testtask.com.testtask.rss;

public class RssItem {
    private String mTitle;
    private String mDescription;
    private String mImage;

    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getImage() {
        return mImage;
    }

    public RssItem(String title, String description, String image) {
        this.mTitle = title;
        this.mDescription = description;
        this.mImage = image;
    }
}
