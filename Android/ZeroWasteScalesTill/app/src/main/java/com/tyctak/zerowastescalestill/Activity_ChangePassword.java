package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_ChangePassword extends AppCompatActivity {

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_changepassword);
    }

    private void setMessage(String message) {
        XP_Library.setMessage(activity, message);
    }

    public void btnSubmit(View view) {
        setMessage("");

        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        String password = txtPassword.getText().toString();

        EditText txtNewPassword = (EditText) findViewById(R.id.txtNewPassword);
        String newPassword = txtNewPassword.getText().toString();

        String existingPassword = XP_Library.getPassword();

        if (existingPassword.equals(password)) {
            XP_Library.setPassword(newPassword);

            onBackPressed();
        } else {
            setMessage(getString(R.string.NoPasswordChange));
        }
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }
}
