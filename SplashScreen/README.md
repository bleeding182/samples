## Splash Screens on Android using themes

There is also a blog post about [How to Add a Splash Screen&mdash;The Right Way][1] with further information.

### Setup

Create a splash theme

    <!-- Splash screen that gets shown until the app finishes loading -->
    <style name="SplashTheme" parent="Theme.AppCompat">
        <!-- Fullscreen -->
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
        <item name="android:windowFullscreen">true</item>

        <item name="android:windowBackground">@drawable/splash_screen</item>

        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

Apply the splash theme to all activities and use `meta-data` to specify the theme

    <application
        android:name=".App"
        android:icon="@mipmap/ic_launcher"
        android:label="Splash Screen"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:theme="@style/SplashTheme">

        <activity
            android:name=".DarkActivity"
            android:label="Splash Dark">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>

            <!-- Add theme information to show after the splash screen -->
            <meta-data
                android:name="theme"
                android:resource="@style/AppTheme.Dark"/>
        </activity>
    </application>

Register a callback in your application and swap themes after loading

    public class App extends Application {

      @Override
      public void onCreate() {
        super.onCreate();

        // register the util to remove splash screen after loading
        registerActivityLifecycleCallbacks(new SplashScreenHelper());
      }
    }

    class SplashScreenHelper implements Application.ActivityLifecycleCallbacks {

      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        try {
          ActivityInfo activityInfo = activity.getPackageManager()
              .getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);

          Bundle metaData = activityInfo.metaData;

          int theme;
          if (metaData != null) {
            theme = metaData.getInt("theme", R.style.AppTheme);
          } else {
            theme = R.style.AppTheme;
          }

          activity.setTheme(theme);
        } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
        }
      }
    }

  [1]:http://blog.davidmedenjak.com/android/2017/09/02/splash-screens.html
