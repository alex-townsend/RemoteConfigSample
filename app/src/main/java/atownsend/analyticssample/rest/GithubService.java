package atownsend.analyticssample.rest;

import atownsend.analyticssample.model.Repo;
import atownsend.analyticssample.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Basic Retrofit service for connecting to github
 */
public interface GithubService {

  @Headers("Accept: application/vnd.github.v3+json")
  @GET("/users/{user}")
  Call<User> getUser(@Path("user") String user);

  @Headers("Accept: application/vnd.github.v3+json")
  @GET("/users/{user}/repos")
  Call<List<Repo>> getRepos(@Path("user") String user);
}
