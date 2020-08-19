package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_MainActivity extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_mainactivity);

        Button btnConfigure = (Button) findViewById(R.id.btnConfigure);
        btnConfigure.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                XP_Library.setPassword(null);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        XP_Library_UI XPLIBUI = new XP_Library_UI();
        XPLIBUI.confirmationDialogYesNo(getString(R.string.AreYouSure), getString(R.string.LeaveApplication), new Runnable() {
            @Override
            public void run() {
                finishAndRemoveTask();
            }
        }, null, activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(View.INVISIBLE);
    }

    public void setMessage(String message) {
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public void btnCustomer(View view) {
//        MyClass myc = new MyClass();
//        String h = myc.HelloWorld();
//
//        Intent intent = new Intent(activity, MainActivity2.class);
//        startActivity(intent);

        setMessage("");

//        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
//        pb.setVisibility(View.VISIBLE);

        XP_Library.RunIfConnected(activity, (TextView) findViewById(R.id.txtMessage), new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XP_Library_WS XPLIBWS = new XP_Library_WS();
                        final _Reward reward = XPLIBWS.getReward(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword());
                        final boolean fixedValid = XPLIBWS.checkFixed(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent;

                                if (!fixedValid) {
                                    intent = new Intent(activity, Activity_MessageReturn.class);
                                    intent.putExtra("message", getString(R.string.TitleFailedVerification));
                                    intent.putExtra("description", getString(R.string.DescriptionFailedVerification));
                                    intent.putExtra("source", Activity_MessageReturn.enmSource.Main.ordinal());
                                } else if (reward != null && reward.IsReward && reward.IsDisplayReward) {
                                    intent = new Intent(activity, Activity_Reward.class);
                                    intent.putExtra("reward", reward);
                                } else {
                                    intent = new Intent(activity, Activity_Customer.class);
                                }

                                startActivity(intent);
                            }
                        });
                    }
                });

                thread.start();
            }
        });
    }

    public void btnBackup(View view) {
        Intent intent = new Intent(activity, Activity_Backup.class);
        startActivity(intent);
    }

    public void btnRunProcess(View view) {
        Intent intent = new Intent(activity, Activity_Process.class);
        startActivity(intent);
    }

    public void btnReports(View view) {
        XP_Library.RunIfConnected(activity, (TextView) findViewById(R.id.txtMessage), new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, Activity_Reports.class);
                activity.startActivity(intent);
            }
        });
    }

    public void btnConfigure(View view) {
        Intent intent = new Intent(activity, Activity_Password.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }
}
