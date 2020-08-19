package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import static com.tyctak.zerowastescalestill.XP_Library.getLog;

public class Activity_DisplayLog extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Activity activity = this;

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter ;
    private enmLogArea logArea;

    public enum enmLogArea {
        None,
        Activity_Customer
    }

    public class StringComparator implements Comparator<String>
    {
        public int compare(String left, String right) {
            return right.compareTo(left);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_displaylog);

        mainListView = (ListView) findViewById( R.id.mainListView );

        logArea = enmLogArea.values()[Integer.parseInt(XP_Library.getValue("displaylog", String.valueOf(enmLogArea.None.ordinal())))];
        setSpinnerList(logArea);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerLogArea);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) activity);

        ArrayList<String> logAreas = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.logareas)));
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(activity, R.layout.spinner_text, (String[]) logAreas.toArray(new String[0]));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setSelection(logArea.ordinal());
    }

    private ArrayList<String> getLogLines(enmLogArea logArea) {
        Set<String> list = new HashSet(getLog());
        ArrayList<String> logLines = new ArrayList<String>(list);

        ArrayList<String> logLinesTemp = new ArrayList<>();
        for (String log : logLines) {
            if (log.contains(logArea.name())) {
                logLinesTemp.add(log);
            }
        }

        logLinesTemp.sort(new StringComparator());

        return logLinesTemp;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Activity_DisplayLog.enmLogArea logArea = Activity_DisplayLog.enmLogArea.values()[position];

        if (!this.logArea.equals(logArea)) {
            setSpinnerList(logArea);
            XP_Library.clearLog();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerLogArea);
        outState.putInt("logarea", spinner.getSelectedItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerLogArea);
        spinner.setSelection(savedInstanceState.getInt("logarea"));
        logArea = enmLogArea.values()[spinner.getSelectedItemPosition()];
    }

    private void setSpinnerList(enmLogArea logArea) {
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, getLogLines(logArea));
        mainListView.setAdapter(listAdapter);
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }

    public void btnSubmit(View view) {
        XP_Library.clearLog();
        Spinner spinner = (Spinner) findViewById(R.id.spinnerLogArea);

        Activity_DisplayLog.enmLogArea logArea = Activity_DisplayLog.enmLogArea.values()[spinner.getSelectedItemPosition()];
        XP_Library.setValue("displaylog", String.valueOf(logArea.ordinal()));

        Intent intent = new Intent(activity, Activity_Configure.class);
        startActivity(intent);
    }
}