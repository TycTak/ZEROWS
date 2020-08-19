package com.tyctak.zerowastescalestill;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class Fragment_Header extends Fragment {

    public Fragment_Header() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        PackageInfo pInfo = null;

        try {
            pInfo = view.getContext().getPackageManager().getPackageInfo(view.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = pInfo.versionName;

        TextView tv = (TextView) view.findViewById(R.id.txtAppTitle);
        tv.setText(getString(R.string.appTitle) + " - v" + version + "");

        ImageView logo = (ImageView) view.findViewById(R.id.logo);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MyApp.getAppContext(), Activity_DisplayVerification.class);
                startActivity(intent);

                return false;
            }
        });

        return view;
    }

    public void displayRunProcess(Boolean value) {
        ImageView iv = (ImageView) getActivity().findViewById(R.id.runProcess);
        ImageView btnCancel = (ImageView) getActivity().findViewById(R.id.btnCancel);
        iv.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        btnCancel.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
