package catalogue.templates.contentful.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import catalogue.templates.contentful.R;
import catalogue.templates.contentful.adapters.NavigationAdapter;
import catalogue.templates.contentful.lib.ItemClickListener;
import catalogue.templates.contentful.lib.LoaderId;
import catalogue.templates.contentful.loaders.NavLoader;
import java.util.List;

public class NavigationDrawerFragment extends BaseFragment
    implements LoaderManager.LoaderCallbacks<List<NavigationAdapter.Item>> {

  private static final int LOADER_ID = LoaderId.forClass(NavigationDrawerFragment.class);

  private NavigationAdapter adapter;

  private ItemClickListener<NavigationAdapter.Item> listener;

  @Bind(R.id.list) ListView list;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new NavigationAdapter();
    initLoader();
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_nav_drawer, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    list.setAdapter(adapter);

    if (savedInstanceState == null) {
      list.setItemChecked(0, true);
    }
  }

  @Override public void onDestroyView() {
    ButterKnife.unbind(this);
    super.onDestroyView();
  }

  @Override public Loader<List<NavigationAdapter.Item>> onCreateLoader(int id, Bundle args) {
    return new NavLoader();
  }

  @Override public void onLoadFinished(Loader<List<NavigationAdapter.Item>> loader,
      List<NavigationAdapter.Item> data) {
    if (data != null) {
      adapter.setSecondaryData(data);
      adapter.switchToSecondary();
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void onLoaderReset(Loader<List<NavigationAdapter.Item>> loader) {
  }

  @Override protected void reload() {
    restartLoader();
  }

  private void initLoader() {
    getLoaderManager().initLoader(LOADER_ID, null, this);
  }

  private void restartLoader() {
    getLoaderManager().restartLoader(LOADER_ID, null, this);
  }

  public void setListener(ItemClickListener<NavigationAdapter.Item> listener) {
    this.listener = listener;
  }

  @OnItemClick(R.id.list)
  void onDrawerItemClick(int position) {
    NavigationAdapter.Item item = adapter.getItem(position);

    if (item.getTitle().equals(NavigationAdapter.Title.CATEGORIES)) {
      adapter.toggle();
      adapter.notifyDataSetChanged();
    } else {
      if (listener != null) {
        listener.onItemClick(item);
      }
    }
  }
}