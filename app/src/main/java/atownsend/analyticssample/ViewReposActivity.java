package atownsend.analyticssample;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import atownsend.analyticssample.base.BaseActivity;
import atownsend.analyticssample.model.Repo;
import atownsend.analyticssample.rest.GithubService;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.crash.FirebaseCrash;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by atownsend on 7/11/16.
 */

public class ViewReposActivity extends BaseActivity {

  private static final String USERNAME_EXTRA = "username_extra";

  @BindView(R.id.progress_bar) ProgressBar progressBar;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  private RepoRecyclerAdapter adapter;

  public static void start(final Context context, final String username) {
    Intent starter = new Intent(context, ViewReposActivity.class);
    starter.putExtra(USERNAME_EXTRA, username);
    context.startActivity(starter);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_repos);
    ButterKnife.bind(this);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    adapter = new RepoRecyclerAdapter(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
      // let's trigger an out of memory exception
      List<Integer> integers = new ArrayList<>();
      while (true) {
        integers.add(1);
      }
    }

    final GithubService service = RemoteConfigSampleApplication.get(this).getGithubService();
    final String username = getIntent().getStringExtra(USERNAME_EXTRA);
    recyclerView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    service.getRepos(username).enqueue(new Callback<List<Repo>>() {
      @Override public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
        if (response.isSuccessful()) {
          adapter.setRepos(response.body());
          recyclerView.setVisibility(View.VISIBLE);
          progressBar.setVisibility(View.GONE);
        } else {
          // log a non-fatal exception
          FirebaseCrash.log("Failure attempting to retrieve repo list for user: " + username);
        }
      }

      @Override public void onFailure(Call<List<Repo>> call, Throwable t) {
        // report the exception to firebase
        FirebaseCrash.report(t);
      }
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
