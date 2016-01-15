package hackspace.testtask.com.testtask.rss;

public class RssItem {
    private String title;
    private String description;
    private String  image;

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getImage() {
        return image;
    }

    public RssItem(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }
}
