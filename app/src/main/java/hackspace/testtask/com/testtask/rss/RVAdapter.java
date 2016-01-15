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

        public void selectRssItemView(CardView cv) {
            cv.setCardBackgroundColor(0xFFC5D2C0);
            cv.invalidate();
            selectedRow = this.getPosition();
            selectedCardView = cv;
        }

        public static void deselectRssItemView(CardView cv) {
            if (selectedCardView == null) {
                return;
            }
            cv.setCardBackgroundColor(0xfffffbfb);
            cv.invalidate();
            selectedRow = -1;
            selectedCardView = null;
        }
    }

    static CardView selectedCardView = null;
    static int selectedRow;
    static List<RssItem> rssItems;
    public RVAdapter(List<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    static Context context;
    static ActionMode actionMode = null;

    public static ActionMode getActionMode() {
        return actionMode;
    }

    @Override
    public RssItemView onCreateViewHolder(final ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.card_view, viewGroup, false);

        final RssItemView riv = new RssItemView(v);

        v.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = ((Activity) context).startActionMode(mActionModeCallback);
                riv.selectRssItemView(riv.cv);
                return true;
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionMode == null) {
                    return;
                }
                if (riv.cv == selectedCardView) {
                    RssItemView.deselectRssItemView(selectedCardView);
                } else {
                    RssItemView.deselectRssItemView(selectedCardView);
                    riv.selectRssItemView(riv.cv);
                }
            }
        });

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

    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_rss_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteBtn:
                    removeAt(selectedRow);
                    selectedRow = -1;
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            RssItemView.deselectRssItemView(selectedCardView);
        }
    };

    public void removeAt(int position) {
        if (rssItems.size() == 0) {
            Toast.makeText(context, context.getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedRow == -1) {
            return;
        }
        rssItems = RssBusiness.delete(rssItems.get(position), context);
        RssItemView.deselectRssItemView(selectedCardView);
        notifyItemRemoved(position);
    }
}
