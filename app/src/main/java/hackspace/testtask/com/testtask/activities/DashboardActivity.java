package hackspace.testtask.com.testtask.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import hackspace.testtask.com.testtask.R;
import hackspace.testtask.com.testtask.rss.RssBusiness;
import hackspace.testtask.com.testtask.rss.RssItem;

public class DashboardActivity extends AppCompatActivity {
    private final static String FILE_NAME = "rss.json";
    private Button mRssButton;
    private Button mBackupButton;
    private Button mRestoreButton;
    private Button mExitButton;

    private OnClickListener mButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mRssButton:
                    Intent intent = new Intent(DashboardActivity.this, RssActivity.class);
                    startActivity(intent);
                    break;
                case R.id.mBackupButton:
                    backup();
                    break;
                case R.id.mRestoreButton:
                    restore();
                    break;
                case R.id.mExitButton:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        initListeners();
    }

    private void initViews() {
        mRssButton = (Button) findViewById(R.id.mRssButton);
        mBackupButton = (Button) findViewById(R.id.mBackupButton);
        mRestoreButton = (Button) findViewById(R.id.mRestoreButton);
        mExitButton = (Button) findViewById(R.id.mExitButton);
    }

    private void initListeners() {
        mRssButton.setOnClickListener(mButtonListener);
        mBackupButton.setOnClickListener(mButtonListener);
        mRestoreButton.setOnClickListener(mButtonListener);
        mExitButton.setOnClickListener(mButtonListener);
    }

    private void backup() {
        List<RssItem> rssItems = new RssBusiness().getRssItems(this);
        Gson gson = new Gson();
        String rssItemInJson;

        this.deleteFile(FILE_NAME);
        try (FileOutputStream outputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            for (RssItem rssItem : rssItems) {
                rssItemInJson = gson.toJson(rssItem);
                bfWriter.write(rssItemInJson);
                bfWriter.newLine();
            }

            Toast.makeText(DashboardActivity.this, getString(R.string.backuped), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.error_message) + e.getMessage())
                    .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void restore() {
        List<RssItem> rssItems = new ArrayList<>();
        Gson gson = new Gson();

        try (FileInputStream fis = this.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            String rssItemFromJson;
            while ((rssItemFromJson = bufferedReader.readLine()) != null) {
                RssItem rssItem = gson.fromJson(rssItemFromJson, RssItem.class);
                rssItems.add(rssItem);
            }
            new RssBusiness().setRssItems(rssItems, this);

            Toast.makeText(DashboardActivity.this, getString(R.string.restored), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.error_message) + e.getMessage())
                    .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
