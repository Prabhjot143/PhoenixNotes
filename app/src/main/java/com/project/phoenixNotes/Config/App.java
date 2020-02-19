package com.project.phoenixNotes.Config;

import android.app.Application;

/**
 * Created by iapp on 27/6/18.
 */

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static App getInstance() {
        return instance;
    }

}
