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
    UpdateBdReceiver updateBdReceiver = null;
    Boolean updateBdReceiverIsRegistered = false;
    Boolean serviceIsRunning = false;
    RecyclerView rv;
    RVAdapter adapter;
    private static final String SERVICE_IS_RUNNING = "hackspace.testtask.com.activities.SERVICE_IS_RUNNING";

    //TODO AL_PB You do not to pass Context in AsyncTask.
    //Init UI components in onCreate activity method. (new adapter, and setAdapter)
    private class UpdateRecycleViewTask extends AsyncTask<Context, Void, Void> {
        Context context;

        @Override
        protected Void doInBackground(Context... contexts) {
            context = contexts[0];
            List<RssItem> rssItems = RssBusiness.getRssItems(context);
            adapter = new RVAdapter(rssItems);
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            rv.setAdapter(adapter);
        }

        @Override
        protected void onPostExecute(Void value) {
        }
    }

    class UpdateBdReceiver extends BroadcastReceiver
    {
        ProgressDialog pd = null;
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(RssDownloadService.SERVICE_UPDATING_BD)){

                    pd = new ProgressDialog(context);
                    pd.setMessage(getString(R.string.please_wait));
                    pd.show();

                serviceIsRunning = true;

            } else if (action.equalsIgnoreCase(RssDownloadService.BD_UPDATED)) {
                new UpdateRecycleViewTask().execute(context);

            } else if (action.equalsIgnoreCase(RssDownloadService.SERVICE_FINISHED))  {
                pd.dismiss();

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

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        List<RssItem> rssItems = RssBusiness.getRssItems(this);

        adapter = new RVAdapter(rssItems);
        rv.setAdapter(adapter);
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

        adapter.mActionModeCallback.onDestroyActionMode(RVAdapter.getActionMode());
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rss, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshBtn:
                if (serviceIsRunning) break;

                Intent intent = new Intent(this, RssDownloadService.class);
                startService(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
