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
    Button btnRss, btnBackup, btnRestore, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnRss = (Button) findViewById(R.id.btnRss);
        btnRss.setOnClickListener(btnListener);
        btnBackup = (Button) findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(btnListener);
        btnRestore = (Button) findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(btnListener);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(btnListener);
    }

    OnClickListener btnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRss:
                    Intent intent = new Intent(DashboardActivity.this, RssActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnBackup:
                    backup();
                    break;
                case R.id.btnRestore:
                    restore();
                    break;
                case R.id.btnExit:
                    finish();
            }
        }
    };

    public void backup() {
        String filename = getString(R.string.file_name);

        List<RssItem> rssItems = RssBusiness.getRssItems(this);
        Gson gson = new Gson();
        String rssItemInJson;

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

    public void restore() {
        String filename = getString(R.string.file_name);

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
