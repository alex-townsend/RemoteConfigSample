package atownsend.analyticssample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;
import java.util.Locale;

import atownsend.analyticssample.base.BaseActivity;
import atownsend.analyticssample.model.User;
import atownsend.analyticssample.rest.GithubService;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

  private Call<User> currentCall;

  @BindView(R.id.constraintLayout) ConstraintLayout constraintLayout;
  @BindView(R.id.search_btn) Button searchButton;
  @BindView(R.id.username_edit_text) EditText usernameEditText;
  @BindView(R.id.progress_bar) ProgressBar progressBar;
  @BindView(R.id.company_value) TextView companyText;
  @BindView(R.id.email_value) TextView emailText;
  @BindView(R.id.name_value) TextView nameText;
  @BindView(R.id.num_gists_value) TextView numGistsText;
  @BindView(R.id.num_repos_value) TextView numReposText;
  @BindView(R.id.view_repos_btn) Button viewReposButton;
  // list all of the content views together so we can show/hide them all at once with ButterKnife
  @BindViews({
      R.id.num_gists_label, R.id.num_gists_value, R.id.num_repos_label, R.id.num_repos_value,
      R.id.name_label, R.id.name_value, R.id.email_label, R.id.email_value, R.id.company_label,
      R.id.company_value, R.id.view_repos_btn
  }) List<View> contentViews;

  private GithubService service;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    service = RemoteConfigSampleApplication.get(this).getGithubService();

    usernameEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void afterTextChanged(Editable editable) {
        // hide all the data fields when we change the username
        ButterKnife.apply(contentViews, SHOW_HIDE, false);
      }
    });
  }

  @OnClick(R.id.search_btn) void searchOnClick() {
    if (usernameEditText.getText().toString().isEmpty()) {
      Snackbar.make(constraintLayout, R.string.enter_username_text, Snackbar.LENGTH_SHORT).show();
    } else {

      if (currentCall != null) {
        currentCall.cancel();
      }

      ButterKnife.apply(contentViews, SHOW_HIDE, false);
      progressBar.setVisibility(View.VISIBLE);

      final String username = usernameEditText.getText().toString();
      currentCall = service.getUser(username);

      currentCall.enqueue(new Callback<User>() {
        @Override public void onResponse(Call<User> call, Response<User> response) {
          handleResponse(response);
        }

        @Override public void onFailure(Call<User> call, Throwable t) {
          progressBar.setVisibility(View.GONE);
          Snackbar.make(constraintLayout, R.string.retrofit_error, Snackbar.LENGTH_SHORT).show();
          FirebaseCrash.report(t);
        }
      });
      // log the search
      logUserSearch(username);
    }
  }

  @OnClick(R.id.view_repos_btn) void viewReposOnClick() {
    final String username = usernameEditText.getText().toString();
    ViewReposActivity.start(this, username);
  }

  private void handleResponse(Response<User> response) {
    progressBar.setVisibility(View.GONE);
    if (response.isSuccessful()) {
      ButterKnife.apply(contentViews, SHOW_HIDE, true);
      final User returnedUser = response.body();
      companyText.setText(returnedUser.company == null ? getString(R.string.not_applicable_text)
          : returnedUser.company);
      nameText.setText(
          returnedUser.name == null ? getString(R.string.not_applicable_text) : returnedUser.name);
      emailText.setText(returnedUser.email == null ? getString(R.string.not_applicable_text)
          : returnedUser.email);
      numGistsText.setText(String.format(Locale.US, "%d", returnedUser.numberOfPublicGists));
      numReposText.setText((String.format(Locale.US, "%d", returnedUser.numberOfPublicRepos)));

      // enable or disable the view repos button based off our Remote Config
      viewReposButton.setVisibility(firebaseRemoteConfig.getBoolean("view_repos_enabled") ? View.VISIBLE : View.GONE);
    } else {
      FirebaseCrash.log("User not found or error retrieving user: " + usernameEditText.getText());
      Snackbar.make(constraintLayout, "Error retrieving user: " + usernameEditText.getText().toString(), Snackbar.LENGTH_SHORT).show();
    }
  }

  private static final ButterKnife.Setter<View, Boolean> SHOW_HIDE =
      new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(@NonNull View view, Boolean show, int index) {
          view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
      };

  /**
   * Logs a custom Firebase Analytics Event for a user search
   *
   * @param username the username searched
   */
  private void logUserSearch(final String username) {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, username);
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
  }


}
