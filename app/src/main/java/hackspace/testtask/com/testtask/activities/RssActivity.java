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
    RecyclerView rv;
    RVAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);


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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
