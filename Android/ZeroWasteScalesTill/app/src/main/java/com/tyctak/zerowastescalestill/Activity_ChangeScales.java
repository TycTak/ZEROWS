package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_ChangeScales extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_changescales);

        Button btnChangeBase = (Button) findViewById(R.id.btnConnection);

        btnChangeBase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeBase( activity, false);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText txtBaseUrl = (EditText) findViewById(R.id.txtScalesLocation);
        txtBaseUrl.setText((TextUtils.isEmpty(txtBaseUrl.getText().toString()) ? XP_Library.getBaseUrl() : txtBaseUrl.getText().toString()));
    }

    private void setMessage(String message) {
        XP_Library.setMessage(activity, message);
    }

    public void changeBase(final Activity parent, Boolean checkAccess) {
        XP_Library.setMessage(parent, "");

        EditText txtBaseUrl = (EditText) parent.findViewById(R.id.txtScalesLocation);
        final String baseUrl = txtBaseUrl.getText().toString();

        EditText txtLinkCode = (EditText) parent.findViewById(R.id.txtLinkCode);
        final String linkCode = txtLinkCode.getText().toString();

        if (checkAccess) {
            if (TextUtils.isEmpty(linkCode) || TextUtils.isEmpty(baseUrl)) {
                XP_Library.setMessage(parent, getString(R.string.UrlLinkCode));
            } else {
                final ProgressBar pb = (ProgressBar) parent.findViewById(R.id.pbLoading);
                pb.setVisibility(View.VISIBLE);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XP_Library_WS XPLIBWS = new XP_Library_WS();

                        if (XPLIBWS.ping(baseUrl) && XPLIBWS.checkLink(baseUrl, XP_Library.getWebServicePassword(), linkCode)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setVisibility(View.GONE);

                                    XP_Library.setBaseUrl(baseUrl);
                                    Intent intent = new Intent(parent, Activity_MainActivity.class);
                                    parent.startActivity(intent);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setVisibility(View.GONE);

                                    WifiManager wm = (WifiManager) parent.getSystemService(WIFI_SERVICE);
                                    final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                                    XP_Library.setMessage(parent, String.format(parent.getString(R.string.NoIP), ip));
                                }
                            });
                        }
                    }
                });

                thread.start();
            }
        } else {
            XP_Library.setBaseUrl(baseUrl);
            Intent intent = new Intent(parent, Activity_MainActivity.class);
            parent.startActivity(intent);
        }
    }

    public void btnSubmit(View view) {
        changeBase(activity, true);
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }
}