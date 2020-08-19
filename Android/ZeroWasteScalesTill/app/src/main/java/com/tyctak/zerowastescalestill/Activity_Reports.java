package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Reports extends AppCompatActivity {

    Activity activity = this;

    private _ClientStatus clientStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_reports);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("clientstatus", clientStatus);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        clientStatus = savedInstanceState.getParcelable("clientstatus");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                clientStatus = XPLIBWS.getClientStatus(XP_Library.getBaseUrl());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button btn1 = (Button) findViewById(R.id.btnReportAnalysis);
                        btn1.setEnabled(true);
                    }
                });
            }
        });

        thread.start();
    }

    public void btnBack(View view) {
        onBackPressed();
    }

    public void btnAnalysis(View view) {
        Intent intent = new Intent(activity, Activity_Reports_Analysis.class);
        intent.putExtra("clientstatus", clientStatus);
        startActivity(intent);
    }
}