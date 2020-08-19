package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class XP_Library {
    public static void FullScreen(Activity activity) {
        activity.overridePendingTransition(0, 0);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static Long isValidDate(String inDate, String dateFormat) {
        Long retval = 0L;

        SimpleDateFormat df = new SimpleDateFormat(dateFormat + " HH:mm");
        df.setLenient(false);

        try {
            Date date = df.parse(inDate.trim());
            retval = date.getTime();
        } catch (ParseException pe) { }

        return retval;
    }

    public static String getWebServicePassword() {  return XP_Library.getValue("wspassword", R.string.wsPassword); }
    public static void setWebServicePassword(String value) { XP_Library.setValue("wspassword", value); }

    public static String getBaseUrl() { return XP_Library.getValue("baseurl", R.string.baseUrl); }
    public static void setBaseUrl(String value) { XP_Library.setValue("baseurl", value); }

    public static String getPassword() { return XP_Library.getValue("configpassword", R.string.configPassword); }
    public static void setPassword(String value) {
        if (TextUtils.isEmpty(value)) value = MyApp.getAppContext().getString(R.string.configPassword);
        XP_Library.setValue("configpassword", value);
    }

    public static String getValue(String key, Integer defaultValue) {
        SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
        return sp.getString(key, MyApp.getAppContext().getString(defaultValue));
    }

    public static String getValue(String key, String defaultValue) {
        SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void clearLog() {
        String key = "log";

        SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putStringSet(key, new HashSet<String>());
        editor.commit();
    }

    public static Set<String> getLog() {
        String key = "log";

        SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
        return sp.getStringSet(key, new HashSet<String>());
    }

    public static void logValue(Activity_DisplayLog.enmLogArea logArea, String value) {
        Log.d("logValue", value);

        if (value.startsWith(logArea.name())) {
            String key = "log";

            SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
            String dateFormat = s.format(new Date());

            Set<String> list = new HashSet(getLog());
            list.add(dateFormat + " - " + value);

            editor.putStringSet(key, list);
            editor.apply();
        }
    }

    public static void RunIfConnected(final Activity parent, final TextView txtMessage, final Runnable runnable) {
        txtMessage.setText("");
        txtMessage.setVisibility(View.INVISIBLE);

        final ProgressBar pb = (ProgressBar) parent.findViewById(R.id.pbLoading);
        pb.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (XPLIBWS.ping(XP_Library.getBaseUrl())) {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
//                            pb.setVisibility(View.GONE);
                        }
                    });
                } else {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            txtMessage.setText(parent.getString(R.string.NoConnection));
                            txtMessage.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        thread.start();
    }

    public static void setValue(String key, String value) {
        SharedPreferences sp = MyApp.getAppContext().getSharedPreferences("ZEROWS", MyApp.getAppContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String now(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    public static String date(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public static String getDealCodeName(String dealCode) {
        String retval;

        switch (dealCode) {
            case "BOGOFF":
                retval = "Buy One get One FREE";
                break;
            default: retval = "";
            break;
        }

        return retval;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void setMessage(Activity activity, String message) {
        TextView txtMessage = (TextView) activity.findViewById(R.id.txtMessage);
        txtMessage.setText(TextUtils.isEmpty(message) ? "" : message);
        txtMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e("", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e("", "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("", "Exception on closing MD5 input stream", e);
            }
        }
    }
}
