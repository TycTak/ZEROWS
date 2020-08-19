package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Password extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_password);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPassword.requestFocus();
    }

    private void setMessage(String message) {
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public void btnSubmit(View view) {
        setMessage("");

        EditText et = (EditText) findViewById(R.id.txtPassword);
        final String password = et.getText().toString();

        String existingPassword = XP_Library.getPassword();

        if (existingPassword.equals(password)) {
            Intent intent = new Intent(activity, Activity_Configure.class);
            startActivity(intent);
        } else {
            setMessage(getString(R.string.InvalidPassword));
        }
    }

    public void btnCancel(View view) {
        onBackPressed();
    }
}
