package com.tyctak.zerowastescalestill;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.squareup.sdk.pos.ChargeRequest;
import com.squareup.sdk.pos.CurrencyCode;

import java.io.Serializable;

public class Test_Activity_SquareUp extends AppCompatActivity {

    private String hello = "Hello string Test_Activity_SquareUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_squareup);
        Log.d("Test_Activity_SquareUp", "onCreate");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        hello = savedInstanceState.getString("hello");

        Log.d("Test_Activity_SquareUp", "onRestoreInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Test_Activity_SquareUp", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("Test_Activity_SquareUp", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("Test_Activity_SquareUp", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("Test_Activity_SquareUp", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("Test_Activity_SquareUp", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Test_Activity_SquareUp", "onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("hello", hello);

        Log.d("Test_Activity_SquareUp", "onSaveInstanceState");
    }

    public void btnDataReturned(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("com.squareup.pos.CLIENT_TRANSACTION_ID", "demo-transactionid-44f8-a952-b6f4465");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        Log.d("Test_Activity_SquareUp", "btnDataReturned");
    }

    public void btnDataReturnedNotOk(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("com.squareup.pos.ERROR_CODE", "com.squareup.pos.ERROR_TRANSACTION_CANCELED");
        returnIntent.putExtra("com.squareup.pos.ERROR_DESCRIPTION", "The transaction was cancelled.");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

        Log.d("Test_Activity_SquareUp", "btnDataReturnedNotOk");
    }

    public void btnNoDataReturnedNotOk(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, null);
        finish();

        Log.d("Test_Activity_SquareUp", "btnNoDataReturnedNotOk");
    }

    public void btnNoDataReturned(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, null);
        finish();

        Log.d("Test_Activity_SquareUp", "btnNoDataReturned");
    }
}
