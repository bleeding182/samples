package com.davidmedenjak.splashscreen;

import android.app.Application;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // register the util to remove splash screen after loading
    registerActivityLifecycleCallbacks(new SplashScreenHelper());
  }
}
