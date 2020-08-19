package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Activity_Reports_Analysis extends AppCompatActivity {

    Activity activity = this;

    private EditText txtStartDate;
    private EditText txtEndDate;

    private DatePickerDialog.OnDateSetListener setStartDate;
    private DatePickerDialog.OnDateSetListener setEndDate;
    private _ClientStatus clientStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_reports_analysis);

        txtStartDate = (EditText) findViewById(R.id.txtStartDate);
        txtEndDate = (EditText) findViewById(R.id.txtEndDate);

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setStartDate, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setEndDate, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        if (clientStatus == null) clientStatus = getIntent().getParcelableExtra("clientstatus");

        txtStartDate.setText(XP_Library.now(clientStatus.DateFormat));
        txtEndDate.setText(XP_Library.now(clientStatus.DateFormat));
    }

    @Override
    protected void onStart() {
        super.onStart();

        setStartDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = XP_Library.date(XP_Library.getDate(year, month, dayOfMonth), clientStatus.DateFormat);
                txtStartDate.setText(date);
            }
        };

        setEndDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = XP_Library.date(XP_Library.getDate(year, month, dayOfMonth), clientStatus.DateFormat);
                txtEndDate.setText(date);
            }
        };
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
    protected void onResume() {
        super.onResume();
    }

    private void setMessage(String message) {
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public void btnSubmit(View view) {
        setMessage("");

        EditText txtStartDate = (EditText) findViewById(R.id.txtStartDate);
        final Long startDate = XP_Library.isValidDate(txtStartDate.getText().toString() + " 00:00", clientStatus.DateFormat);

        EditText txtEndDate = (EditText) findViewById(R.id.txtEndDate);
        final Long endDate = XP_Library.isValidDate(txtEndDate.getText().toString() + " 23:59", clientStatus.DateFormat);

        if (startDate == 0 || endDate == 0) {
            setMessage(getString(R.string.DateNotValid));
        } else {
            final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
            pb.setVisibility(View.VISIBLE);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    XP_Library_WS XPLIBWS = new XP_Library_WS();

                    if (XPLIBWS.reportAnalysis(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword(), (startDate / 1000), (endDate / 1000))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.GONE);
                                setMessage(getString(R.string.ReportProduced));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.GONE);
                                setMessage(getString(R.string.ReportNotProduced));
                            }
                        });
                    }
                }
            });

            thread.start();
        }
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Reports.class);
        startActivity(intent);
    }
}
