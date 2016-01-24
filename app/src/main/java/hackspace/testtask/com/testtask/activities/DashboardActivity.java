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
                    // TODO SM_PB: break; + default must be here - that's a rule
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // TODO SM_PB: create initViews(for field initialization + findViewById) & initListeners(for setting listeners onClick/onTouch/.. events)
        mRssButton = (Button) findViewById(R.id.mRssButton);
        mRssButton.setOnClickListener(mButtonListener);
        mBackupButton = (Button) findViewById(R.id.mBackupButton);
        mBackupButton.setOnClickListener(mButtonListener);
        mRestoreButton = (Button) findViewById(R.id.mRestoreButton);
        mRestoreButton.setOnClickListener(mButtonListener);
        mExitButton = (Button) findViewById(R.id.mExitButton);
        mExitButton.setOnClickListener(mButtonListener);
    }

    public void backup() {
        String filename = getString(R.string.file_name);

        List<RssItem> rssItems = RssBusiness.getRssItems(this);
        Gson gson = new Gson();
        String rssItemInJson;

        //TODO AL_PB Possible resource leak outputStream never closed.
        //you can use try with resources in Java 1.7.+ like this:
        // try (FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE)){
        //     .....
        // }
        try {
            this.deleteFile(filename);
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            for (RssItem rssItem : rssItems) {
                rssItemInJson = gson.toJson(rssItem);
                bfWriter.write(rssItemInJson);
                bfWriter.newLine();
            }
            bfWriter.close();
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

    //TODO AL_PB Possible resource leak Stream never closed, see comment above.
    public void restore() {
        String filename = getString(R.string.file_name); // TODO SM_PB: :) file name must not be internationalized ) it must be a java constant

        List<RssItem> rssItems = new ArrayList<>();
        Gson gson = new Gson();

        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String rssItemFromJson;
            while ((rssItemFromJson = bufferedReader.readLine()) != null) {
                RssItem rssItem = gson.fromJson(rssItemFromJson, RssItem.class);
                rssItems.add(rssItem);
            }
            RssBusiness.setRssItems(rssItems, this);

            bufferedReader.close();
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
