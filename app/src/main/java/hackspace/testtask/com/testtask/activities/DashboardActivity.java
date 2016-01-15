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
                    break;
                case R.id.btnRestore:
                    break;
                case R.id.btnExit:
                    finish();
            }
        }
    };

