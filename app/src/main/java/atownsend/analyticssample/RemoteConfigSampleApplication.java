package atownsend.analyticssample;

import android.app.Application;
import android.content.Context;
import atownsend.analyticssample.rest.GithubService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Sample application class
 */
public class RemoteConfigSampleApplication extends Application {

  private Retrofit retrofit;
  private GithubService githubService;

  @Override public void onCreate() {
    super.onCreate();

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();

    retrofit = new Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    githubService = retrofit.create(GithubService.class);
  }

  public static RemoteConfigSampleApplication get(Context context) {
    return (RemoteConfigSampleApplication) context.getApplicationContext();
  }

  public GithubService getGithubService() {
    return githubService;
  }
}
