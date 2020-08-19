package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Payment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Activity activity = this;

    public enum enmProvider {
        Square,
        SumUp,
        None,
        Debug
    }

    public static String PAYMENT_TYPE = "payment_type";
    public static String SQUARE_APPLICATION_ID = "square_applicationid";
    public static String SUMUP_AFFILIATE_KEY = "sumup_affiliatekey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_payment);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        enmProvider selectedIndex = enmProvider.values()[position];
        displayPaymentType(selectedIndex);
    }

    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    protected void onResume() {
        super.onResume();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerPaymentType);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

        ArrayList<String> payments = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.paymenttypes)));
        if (BuildConfig.DEBUG) payments.add("Debug");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(activity, R.layout.spinner_text, (String[]) payments.toArray(new String[0]));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);

        Spinner spinnerPaymentType = (Spinner) findViewById(R.id.spinnerPaymentType);
        EditText txtApplicationId = (EditText) findViewById(R.id.txtApplicationId);
        EditText txtAffiliateKey = (EditText) findViewById(R.id.txtAffiliateKey);

        enmProvider selectedIndex = enmProvider.values()[Integer.parseInt(XP_Library.getValue(PAYMENT_TYPE, String.valueOf(enmProvider.Square.ordinal())))];
        txtApplicationId.setText((TextUtils.isEmpty(txtApplicationId.getText().toString()) ? XP_Library.getValue(SQUARE_APPLICATION_ID, "") : txtApplicationId.getText().toString()));
        txtAffiliateKey.setText((TextUtils.isEmpty(txtAffiliateKey.getText().toString()) ? XP_Library.getValue(SUMUP_AFFILIATE_KEY, "") : txtAffiliateKey.getText().toString()));

        spinnerPaymentType.setSelection(selectedIndex.ordinal());
        displayPaymentType(selectedIndex);
    }

    private void displayPaymentType(enmProvider selectedIndex) {
        LinearLayout llSquare = (LinearLayout) findViewById(R.id.llSquare);
        LinearLayout llSumup = (LinearLayout) findViewById(R.id.llSumUp);

        if (selectedIndex == enmProvider.Square) {
            llSquare.setVisibility(View.VISIBLE);
            llSumup.setVisibility(View.GONE);
        } else if (selectedIndex == enmProvider.SumUp) {
            llSquare.setVisibility(View.GONE);
            llSumup.setVisibility(View.VISIBLE);
        } else {
            llSquare.setVisibility(View.GONE);
            llSumup.setVisibility(View.GONE);
        }
    }

    private void setMessage(String message) {
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public void btnSubmit(View view) {
        setMessage("");

        Spinner spinnerPaymentType = (Spinner) findViewById(R.id.spinnerPaymentType);
        EditText txtApplicationId = (EditText) findViewById(R.id.txtApplicationId);
        EditText txtAffiliateKey = (EditText) findViewById(R.id.txtAffiliateKey);
        String applicationId = txtApplicationId.getText().toString();
        String affiliateKey = txtAffiliateKey.getText().toString();

        XP_Library.setValue(PAYMENT_TYPE, String.valueOf(spinnerPaymentType.getSelectedItemPosition()));
        XP_Library.setValue(SQUARE_APPLICATION_ID, applicationId);
        XP_Library.setValue(SUMUP_AFFILIATE_KEY, affiliateKey);

        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }
}