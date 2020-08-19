package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Activity_SplashScreen extends AppCompatActivity {

    Activity activity = this;
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_splashscreen);

        Button btnChangeBase = (Button) findViewById(R.id.btnConnection);

        btnChangeBase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Activity_ChangeScales scales = new Activity_ChangeScales();
                scales.changeBase( activity, false);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText txtBaseUrl = (EditText) findViewById(R.id.txtScalesLocation);
        txtBaseUrl.setText((TextUtils.isEmpty(txtBaseUrl.getText().toString()) ? XP_Library.getBaseUrl() : txtBaseUrl.getText().toString()));

        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = MyApp.getAppContext().getFilesDir();

                XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (!XPLIBWS.ping(XP_Library.getBaseUrl())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);

                            ScrollView sv = (ScrollView) findViewById(R.id.getScales);
                            sv.setVisibility(View.VISIBLE);

                            LinearLayout ll = (LinearLayout) findViewById(R.id.splashlogo);
                            ll.setVisibility(View.GONE);

                            TextView tv1 = (TextView) findViewById(R.id.txtTitle);
                            tv1.setText(getString(R.string.NoScalesFound));

                            TextView tv2 = (TextView) findViewById(R.id.txtMessage);
                            tv2.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    XPLIBWS.getLibrary(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScrollView sv = (ScrollView) findViewById(R.id.getScales);
                            sv.setVisibility(View.GONE);

                            LinearLayout ll = (LinearLayout) findViewById(R.id.splashlogo);
                            ll.setVisibility(View.VISIBLE);
                        }
                    });

                    mWaitHandler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 Intent intent = new Intent(activity, Activity_MainActivity.class);
                                 startActivity(intent);
                             }
                         }
                    , 2000);
                }
            }
        });

        thread.start();
    }

    public void btnSubmit(View view) {
        Activity_ChangeScales scales = new Activity_ChangeScales();
        scales.changeBase(activity, true);
    }

    public void btnCancel(View view) {
        finishAndRemoveTask();
    }
}