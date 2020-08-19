package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

public class XP_Library_UI {

    private final String TAG = "XP_Library_UI";

    public void runSnackBarOnUIThread(final Activity parent, final Integer id) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                XP_Library_UI LIBUI = new XP_Library_UI();
                LIBUI.snackBar(parent, id);
            }
        };

        mainHandler.post(myRunnable);
    }

    private void snackBar(final Activity parent, final Integer id) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
                Snackbar snackbar = Snackbar.make(viewGroup, id, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };

        mainHandler.post(myRunnable);
    }

    public void confirmationDialogYesNo(String title, String message, final Runnable yesMethod, final Runnable noMethod, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (yesMethod != null) yesMethod.run();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (noMethod != null) noMethod.run();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    public void popupExitDialog(String title, String message, final Activity target) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(target);
//
//        builder.setTitle(title);
//        builder.setMessage(message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                target.setResult(Activity.RESULT_CANCELED);
//                target.finishAffinity();
//                System.exit(0);
//            }
//        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                target.setResult(Activity.RESULT_CANCELED);
//                target.finishAffinity();
//                System.exit(0);
//            }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

//    public void popupMessageDialog(String title, String message, final Activity target) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(target);
//
//        builder.setTitle(title);
//        builder.setMessage(message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                target.finishAffinity();
//            }
//        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
////                target.finishAffinity();
//            }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
}
