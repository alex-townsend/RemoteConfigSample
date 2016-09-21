package atownsend.analyticssample.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import atownsend.analyticssample.BuildConfig;
import atownsend.analyticssample.R;

/**
 * Base Activity to load the current theme of the application
 */
public class BaseActivity extends AppCompatActivity {

  protected FirebaseRemoteConfig firebaseRemoteConfig;
  protected FirebaseAnalytics firebaseAnalytics;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    // load remote config before super.onCreate() so we can set theme values
    firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    // create config settings to set developer mode when we are in debug
    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
        .setDeveloperModeEnabled(BuildConfig.DEBUG)
        .build();
    firebaseRemoteConfig.setConfigSettings(configSettings);

    firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

    determineAppTheme();

    super.onCreate(savedInstanceState);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);
  }

  private void determineAppTheme() {
    final String theme = firebaseRemoteConfig.getString("theme");
    final int themeId;
    switch (theme) {
      case "yellow":
        themeId = R.style.AppTheme_Yellow;
        break;
      case "purple":
        themeId = R.style.AppTheme_Purple;
        break;
      default:
        themeId = R.style.AppTheme;
    }
    setTheme(themeId);
  }
}
