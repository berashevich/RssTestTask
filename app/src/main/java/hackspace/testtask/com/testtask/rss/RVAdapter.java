package hackspace.testtask.com.testtask.rss;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ActionMode;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import hackspace.testtask.com.testtask.R;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RssItemView>{
    public static class RssItemView extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        ImageView image;
        RssItemView(final View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            image = (ImageView)itemView.findViewById(R.id.image);
        }

    }

    static List<RssItem> rssItems;
    public RVAdapter(List<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    static Context context;

    @Override
    public RssItemView onCreateViewHolder(final ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.card_view, viewGroup, false);

        final RssItemView riv = new RssItemView(v);


        return riv;
    }

    @Override
    public void onBindViewHolder(RssItemView rssItemView, int i) {
        rssItemView.title.setText(rssItems.get(i).getTitle());
        rssItemView.description.setText(rssItems.get(i).getDescription());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.image_stub)
                .showImageForEmptyUri(R.drawable.image_empty)
                .build();
        imageLoader.displayImage(rssItems.get(i).getImage(), rssItemView.image, options);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

