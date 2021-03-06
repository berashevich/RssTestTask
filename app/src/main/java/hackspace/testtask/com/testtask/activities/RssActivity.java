package hackspace.testtask.com.testtask.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import hackspace.testtask.com.testtask.R;
import hackspace.testtask.com.testtask.rss.RssBusiness;
import hackspace.testtask.com.testtask.rss.RssItem;
import hackspace.testtask.com.testtask.rss.RVAdapter;
import hackspace.testtask.com.testtask.services.RssDownloadService;

public class RssActivity extends AppCompatActivity{
    private static final String SERVICE_IS_RUNNING = "hackspace.testtask.com.activities.SERVICE_IS_RUNNING";
    private UpdateBdReceiver updateBdReceiver = null;
    private Boolean updateBdReceiverIsRegistered = false;
    private Boolean serviceIsRunning = false;
    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;

    private class UpdateRecycleViewTask extends AsyncTask<Void, Void, List<RssItem>> {
        @Override
        protected List<RssItem> doInBackground(Void... values) {
            List<RssItem> rssItems = new RssBusiness().getRssItems(getApplicationContext());
            publishProgress();
            return rssItems;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(List<RssItem> items) {
            mAdapter.addAll(items);
        }
    }

    class UpdateBdReceiver extends BroadcastReceiver
    {
        private ProgressDialog mProgressDialog = null;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(RssDownloadService.SERVICE_UPDATING_BD)){

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(getString(R.string.please_wait));
                mProgressDialog.show();

                serviceIsRunning = true;

            } else if (action.equalsIgnoreCase(RssDownloadService.BD_UPDATED)) {
                new UpdateRecycleViewTask().execute();

            } else if (action.equalsIgnoreCase(RssDownloadService.SERVICE_FINISHED))  {
                mProgressDialog.dismiss();

                if (intent.getStringExtra(RssDownloadService.SERVICE_ERROR) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(intent.getStringExtra(RssDownloadService.SERVICE_ERROR))
                            .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                serviceIsRunning = false;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        serviceIsRunning = sharedPref.getBoolean(SERVICE_IS_RUNNING, false);

        if (serviceIsRunning) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SERVICE_IS_RUNNING, false);
            editor.commit();

            Intent intent = new Intent(this, RssDownloadService.class);
            startService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        updateBdReceiver = new UpdateBdReceiver();

        mRecyclerView = (RecyclerView)findViewById(R.id.mRecyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        List<RssItem> rssItems = new RssBusiness().getRssItems(this);

        mAdapter = new RVAdapter(rssItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!updateBdReceiverIsRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(RssDownloadService.SERVICE_UPDATING_BD);
            intentFilter.addAction(RssDownloadService.BD_UPDATED);
            intentFilter.addAction(RssDownloadService.SERVICE_FINISHED);
            registerReceiver(updateBdReceiver, intentFilter);
            updateBdReceiverIsRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (updateBdReceiverIsRegistered) {
            unregisterReceiver(updateBdReceiver);
            updateBdReceiverIsRegistered = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceIsRunning) {
            Intent intent = new Intent(this, RssDownloadService.class);
            stopService(intent);

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SERVICE_IS_RUNNING, true);
            editor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdapter.getActionModeCallback().onDestroyActionMode(RVAdapter.getActionMode());
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rss, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mRefreshButton:
                if (serviceIsRunning) break;

                Intent intent = new Intent(this, RssDownloadService.class);
                startService(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
