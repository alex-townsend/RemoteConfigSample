package atownsend.analyticssample.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by atownsend on 6/14/16.
 */
public class User {
  public String name;
  @SerializedName("public_repos")
  public int numberOfPublicRepos;
  @SerializedName("public_gists")
  public int numberOfPublicGists;
  public String company;
  public String email;
}
