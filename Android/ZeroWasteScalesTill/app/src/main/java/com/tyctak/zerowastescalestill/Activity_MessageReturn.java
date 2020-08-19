package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_MessageReturn extends AppCompatActivity {

    public enum enmSource {
        Customer,
        Main
    }

    Activity activity = this;
    enmSource source = enmSource.Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_messagereturn);

        String message = getIntent().getStringExtra("message");
        String description = getIntent().getStringExtra("description");
        source = enmSource.values()[getIntent().getIntExtra("source", enmSource.Main.ordinal())];

        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);

        txtMessage.setText(message);
        txtDescription.setText(TextUtils.isEmpty(description) ? "" : description);
        txtDescription.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("source", source.ordinal());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        source = enmSource.values()[savedInstanceState.getInt("source")];
    }

    private void returnSource() {
        if (source == enmSource.Customer) {
            Intent intent = new Intent(activity, Activity_Customer.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(activity, Activity_MainActivity.class);
            startActivity(intent);
        }
    }

    public void btnReturn(View view) {
        returnSource();
    }
}