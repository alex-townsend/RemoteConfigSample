package atownsend.analyticssample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import atownsend.analyticssample.model.Repo;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by atownsend on 7/11/16.
 */
public class RepoRecyclerAdapter extends RecyclerView.Adapter<RepoRecyclerAdapter.RepoViewHolder> {

  private final Context context;

  private List<Repo> repoList = new ArrayList<>();

  public RepoRecyclerAdapter(Context context) {
    this.context = context;
  }

  @Override public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RepoViewHolder(
        LayoutInflater.from(context).inflate(R.layout.layout_repo_view_holder, parent, false));
  }

  @Override public void onBindViewHolder(RepoViewHolder holder, int position) {
    final Repo repo = repoList.get(position);
    holder.nameText.setText(repo.name);
    holder.descriptionText.setText(repo.description);
  }

  @Override public int getItemCount() {
    return repoList.size();
  }

  public void setRepos(final List<Repo> repos) {
    repoList.addAll(repos);
    notifyItemRangeInserted(0, repoList.size());
  }

  static class RepoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.repo_name) TextView nameText;
    @BindView(R.id.repo_description) TextView descriptionText;

    RepoViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
