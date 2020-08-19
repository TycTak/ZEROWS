package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Process extends AppCompatActivity {

    Activity activity = this;
    Activity_MessageReturn.enmSource source = Activity_MessageReturn.enmSource.Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_process);

        source = Activity_MessageReturn.enmSource.values()[getIntent().getIntExtra("source", Activity_MessageReturn.enmSource.Main.ordinal())];
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView txtTrace = (TextView) findViewById(R.id.txtTrace);
        txtTrace.setText(source == Activity_MessageReturn.enmSource.Customer ? getString(R.string.Menu_Activity_Process_Customer) : getString(R.string.Menu_Activity_Process));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("source", source.ordinal());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        source = Activity_MessageReturn.enmSource.values()[savedInstanceState.getInt("source")];
    }

    private void setMessage(String message) {
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public void btnRunProcess(View view) {
        setMessage("");

        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TextView txtProcessCode = (TextView) findViewById(R.id.txtProcessCode);

                XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (!XPLIBWS.ping(XP_Library.getBaseUrl())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            setMessage(getString(R.string.NoConnection));
                        }
                    });
                } else if (!TextUtils.isEmpty(txtProcessCode.getText())) {
                    final _Process process = XPLIBWS.runProcess(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), txtProcessCode.getText().toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);

                            if (process != null) {
                                Intent intent = new Intent(activity, Activity_MessageReturn.class);
                                intent.putExtra("message", process.Message);
                                intent.putExtra("description", process.Description);
                                intent.putExtra("source", source.ordinal());
                                activity.startActivity(intent);
                            } else {
                                setMessage(getString(R.string.NoProcess));
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            setMessage(getString(R.string.NoCode));
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