package com.tyctak.zerowastescalestill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    private String hello = "Hello string TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.d("TestActivity", "onCreate");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        hello = savedInstanceState.getString("hello");

        Log.d("TestActivity", "onRestoreInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("TestActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("TestActivity", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("TestActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("TestActivity", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("TestActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("TestActivity", "onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("hello", hello);

        Log.d("TestActivity", "onSaveInstanceState");
    }

    public void btnTest(View view) {
        Intent intent = new Intent(this, Test_Activity_SquareUp.class);
        startActivityForResult(intent, 0);

        Log.d("TestActivity", "btnTest");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TestActivity", "onActivityResult");
    }
}
