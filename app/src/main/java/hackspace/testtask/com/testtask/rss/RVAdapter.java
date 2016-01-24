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
    private static CardView sSelectedCardView = null;
    private static int sSelectedRow;
    private static List<RssItem> sRssItems;
    private static Context sContext;
    private static ActionMode sActionMode = null;

    public static class RssItemView extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTitle;
        private TextView mDescription;
        private ImageView mImage;

        RssItemView(final View itemView) {
            super(itemView);

            mCardView = (CardView)itemView.findViewById(R.id.mCardView);
            mTitle = (TextView)itemView.findViewById(R.id.mTitle);
            mDescription = (TextView)itemView.findViewById(R.id.mDescription);
            mImage = (ImageView)itemView.findViewById(R.id.mImage);
        }

        public void selectRssItemView(CardView cardView) {
            cardView.setCardBackgroundColor(0xFFC5D2C0);
            cardView.invalidate();
            sSelectedRow = this.getPosition();
            sSelectedCardView = cardView;
        }

        public static void deselectRssItemView(CardView cardView) {
            if (sSelectedCardView == null) {
                return;
            }
            cardView.setCardBackgroundColor(0xfffffbfb);
            cardView.invalidate();
            sSelectedRow = -1;
            sSelectedCardView = null;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

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
                case R.id.mDeleteButton:
                    removeAt(sSelectedRow);
                    sSelectedRow = -1;
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            sActionMode = null;
            RssItemView.deselectRssItemView(sSelectedCardView);
        }
    };

    public RVAdapter(List<RssItem> rssItems) {
        this.sRssItems = rssItems;
    }

    public static ActionMode getActionMode() {
        return sActionMode;
    }
    public ActionMode.Callback getActionModeCallback() {
        return mActionModeCallback;
    }

    @Override
    public int getItemCount() {
        return sRssItems.size();
    }

    @Override
    public RssItemView onCreateViewHolder(final ViewGroup viewGroup, int i) {
        sContext = viewGroup.getContext();
        View newCardView = LayoutInflater.from(sContext).inflate(R.layout.card_view, viewGroup, false);

        final RssItemView newRssItemView = new RssItemView(newCardView);

        newCardView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (sActionMode != null) {
                    return false;
                }
                sActionMode = ((Activity) sContext).startActionMode(mActionModeCallback);
                newRssItemView.selectRssItemView(newRssItemView.mCardView);
                return true;
            }
        });

        newCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sActionMode == null) {
                    return;
                }
                if (newRssItemView.mCardView == sSelectedCardView) {
                    RssItemView.deselectRssItemView(sSelectedCardView);
                } else {
                    RssItemView.deselectRssItemView(sSelectedCardView);
                    newRssItemView.selectRssItemView(newRssItemView.mCardView);
                }
            }
        });

        return newRssItemView;
    }

    @Override
    public void onBindViewHolder(RssItemView rssItemView, int i) {
        rssItemView.mTitle.setText(sRssItems.get(i).getTitle());
        rssItemView.mDescription.setText(sRssItems.get(i).getDescription());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(sContext));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.image_stub)
                .showImageForEmptyUri(R.drawable.image_empty)
                .build();
        imageLoader.displayImage(sRssItems.get(i).getImage(), rssItemView.mImage, options);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void removeAt(int position) {
        if (sRssItems.size() == 0) {
            Toast.makeText(sContext, sContext.getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
            return;
        } else if (sSelectedRow == -1) {
            return;
        }
        sRssItems = RssBusiness.delete(sRssItems.get(position), sContext);
        RssItemView.deselectRssItemView(sSelectedCardView);
        notifyItemRemoved(position);
    }
}
