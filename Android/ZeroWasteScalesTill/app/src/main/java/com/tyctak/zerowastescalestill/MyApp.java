package com.tyctak.zerowastescalestill;

import android.app.Application;
import android.content.Context;
import com.sumup.merchant.api.SumUpState;

public class MyApp extends Application {

    private static Context initialContext;
    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SumUpState.init(this);

        initialContext = getApplicationContext();
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public static Context getAppContext()
    {
        return initialContext;
    }
}
