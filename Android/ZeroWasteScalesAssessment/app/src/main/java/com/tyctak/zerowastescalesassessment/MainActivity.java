package com.tyctak.zerowastescalesassessment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URLClassLoader child = new URLClassLoader(
                new URL[] {myJar.toURI().toURL()},
                this.getClass().getClassLoader()
        );

        Class classToLoad = Class.forName("com.MyClass", true, child);
        Method method = classToLoad.getDeclaredMethod("myMethod");
        Object instance = classToLoad.newInstance();
        Object result = method.invoke(instance);

    }
}
