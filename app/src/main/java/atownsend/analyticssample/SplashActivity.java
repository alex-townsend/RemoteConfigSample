package atownsend.analyticssample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Splash activity for loading remote config values before forwarding to our normal activities
 */
public class SplashActivity extends AppCompatActivity {

    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*
         * put this in post resume because of this issue
         * http://stackoverflow.com/questions/37501124/firebaseremoteconfig-fetch-does-not-trigger-oncompletelistener-every-time
         */
        firebaseRemoteConfig.fetch(0 /* cache expiration time -- using 0 for sample purposes */)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // successful fetch, activate the new values
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(SplashActivity.this, "Remote config fetch failed", Toast.LENGTH_SHORT).show();
                        }
                        moveForward();
                    }
                });
    }

    private void moveForward() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
