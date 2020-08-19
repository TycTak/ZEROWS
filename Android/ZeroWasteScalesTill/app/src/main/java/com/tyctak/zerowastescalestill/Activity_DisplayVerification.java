package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_DisplayVerification extends AppCompatActivity {

    Activity activity = this;
    Handler handler = new Handler();
    Handler txtHandler = new Handler();

    ArrayList<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_displayverification);
    }

    @Override
    protected void onResume() {
        super.onResume();

        messages.add("Accessing the server");
        messages.add("Running web service");
        messages.add("Compiling a list of modules");
        messages.add("Running checksum against each module");
        messages.add("Comparing checksums");
        messages.add("Please wait a little longer");
        messages.add("Making sure that all modules included");
        messages.add("Not long now");
        messages.add("Formatting display");

        txtHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                String txtMessageValue = txtMessage.getText().toString();

                for (Integer i = 0; i < (messages.size() - 1); i++) {
                    String text = messages.get(i);
                    if (TextUtils.isEmpty(txtMessageValue)) {
                        txtMessage.setText(text);
                        txtHandler.postDelayed(this, 5000);
                        break;
                    } else if (txtMessageValue.equals(text)) {
                        txtMessage.setText(messages.get(i + 1));

                        if (i < (messages.size() - 1)) {
                            txtHandler.postDelayed(this, 5000);
                        }
                        break;
                    }
                }
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XP_Library_WS XPLIBWS = new XP_Library_WS();
                        final _Verification verification = XPLIBWS.getVericationList(XP_Library.getBaseUrl(), XP_Library.getWebServicePassword());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtVersion = (TextView) findViewById(R.id.txtVersion);
                                TextView txtBuilt = (TextView) findViewById(R.id.txtBuilt);
                                TextView txtPackage = (TextView) findViewById(R.id.txtPackage);
                                txtVersion.setText("ZEROWS Version: " + verification.Version + "." + verification.Build);
                                txtBuilt.setText("Built: " + verification.Built);
                                txtPackage.setText("Package: " + verification.ZipFile);

                                ListView verificationListView = (ListView) findViewById( R.id.verificationListView);
                                verificationListView.setTextFilterEnabled(true);

                                verificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ArrayList<HashMap<String,String>> row = (ArrayList<HashMap<String,String>>) parent.getItemAtPosition(position);
                                        String checksum = (String) ((HashMap) parent.getItemAtPosition(position)).entrySet().toArray()[0];
                                        String name = (String) ((HashMap) parent.getItemAtPosition(position)).entrySet().toArray()[1];
                                        Boolean match = (Boolean) ((HashMap) parent.getItemAtPosition(position)).entrySet().toArray()[3];
                                    }
                                });

                                ArrayList<HashMap<String,String>> aryVerification = new ArrayList<>();

                                for (_Verification._File file : verification.Fixeds) {
                                    HashMap<String,String> hashMap=new HashMap<>();
                                    hashMap.put("name", file.Name);
                                    hashMap.put("fixed", "> LOCKED <");
                                    hashMap.put("currentchecksum", (TextUtils.isEmpty(file.CurrentChecksum) ? "MISSING" : file.CurrentChecksum));
                                    hashMap.put("match", (file.IsMatch ? Integer.toString(R.drawable.ic_tick) : Integer.toString(R.drawable.ic_cross)));
                                    aryVerification.add(hashMap);
                                }

                                for (_Verification._File file : verification.Files) {
                                    HashMap<String,String> hashMap=new HashMap<>();
                                    hashMap.put("name", file.Name);
                                    hashMap.put("fixed", "");
                                    hashMap.put("currentchecksum", (TextUtils.isEmpty(file.CurrentChecksum) ? "MISSING" : file.CurrentChecksum));
                                    hashMap.put("match", (file.IsMatch ? Integer.toString(R.drawable.ic_tick) : Integer.toString(R.drawable.ic_cross)));
                                    aryVerification.add(hashMap);
                                }

                                String[] from = {"name","fixed", "currentchecksum", "match"};
                                int[] to = {R.id.txtName, R.id.txtFixed, R.id.txtCurrentChecksum, R.id.match};
                                SimpleAdapter verificationAdapter = new SimpleAdapter(activity, aryVerification, R.layout.custom_listview_row, from, to);
                                verificationListView.setAdapter(verificationAdapter);

                                LinearLayout listItems = (LinearLayout) findViewById(R.id.listItems);
                                listItems.setVisibility(View.VISIBLE);

                                TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
                                txtDescription.setVisibility(View.GONE);

                                ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
                                pb.setVisibility(View.GONE);

                                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                                txtMessage.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                thread.start();
            }
        }, 1000);
    }

    public void btnCancel(View view) {
        txtHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);

        onBackPressed();
    }
}