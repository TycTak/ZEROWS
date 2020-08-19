package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Backup extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_backup);
    }

    private void setMessage(String message) {
        XP_Library.setMessage(activity, message);
    }

    public void btnSubmit(View view) {
        setMessage("");

        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (!XPLIBWS.ping(XP_Library.getBaseUrl())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            setMessage(getString(R.string.NoConnection));
                        }
                    });
                } else {
                    _ClientStatus clientStatus = XPLIBWS.getClientStatus(XP_Library.getBaseUrl());
                    final String[] backupInfo = XPLIBWS.backup(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), clientStatus.ClientCode);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);

                            if (backupInfo.length == 2 && !XPLIBWS.isBlank(backupInfo[1]) && !XPLIBWS.isBlank(backupInfo[0])) {
                                setMessage(String.format(getString(R.string.YourBackupIs), backupInfo[1], backupInfo[0]));
                            } else {
                                setMessage(getString(R.string.NoBackup));
                            }
                        }
                    });
                }
            }
        });

        thread.start();
    }

    public void btnCancel(View view) {
        onBackPressed();
    }
}