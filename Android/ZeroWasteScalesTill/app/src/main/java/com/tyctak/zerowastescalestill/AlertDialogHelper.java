package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class AlertDialogHelper extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return null;
    }

    private static Dialog getDialog(Activity activity, String title, String description, int resourceId) {
        AlertDialog alertDialog =  new AlertDialog.Builder(activity, resourceId).setTitle(title).setMessage(description).setPositiveButton("OK", null).create();
        return alertDialog;
    }

    public static void showDialog(Activity activity, String title, String description) {
        int resourceId = 0;

        try {
            resourceId = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).getThemeResource();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            getDialog(activity, title, description, resourceId).show();
        }
    }
}