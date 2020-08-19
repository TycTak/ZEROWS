package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Configure extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_configure);
    }

    public void btnBack(View view) {
        onBackPressed();
    }

    public void btnChangeScales(View view) {
        Intent intent = new Intent(activity, Activity_ChangeScales.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    public void btnChangePassword(View view) {
        Intent intent = new Intent(activity, Activity_ChangePassword.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    public void btnChangePayment(View view) {
        Intent intent = new Intent(activity, Activity_Payment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    public void btnDisplayLog(View view) {
        Intent intent = new Intent(activity, Activity_DisplayLog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }
}
