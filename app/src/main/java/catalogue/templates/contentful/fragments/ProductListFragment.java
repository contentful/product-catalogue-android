package catalogue.templates.contentful.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.Intents;
import catalogue.templates.contentful.R;
import catalogue.templates.contentful.activities.ProductActivity;
import catalogue.templates.contentful.adapters.ProductListAdapter;
import catalogue.templates.contentful.lib.LoaderId;
import catalogue.templates.contentful.loaders.ProductListLoader;
import catalogue.templates.contentful.vault.Category;
import catalogue.templates.contentful.vault.Product;
import org.parceler.Parcels;

/** Displays a list of products. */
public class ProductListFragment extends BaseFragment implements
    SwipeRefreshLayout.OnRefreshListener,
    LoaderManager.LoaderCallbacks<ProductListLoader.Result> {

  private final int LOADER_ID = LoaderId.forClass(ProductListFragment.class);

  private ProductListAdapter adapter;

  private Category category;

  @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

  @Bind(R.id.list) ListView listView;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle b = getArguments();
    if (b != null) {
      category = Parcels.unwrap(b.getParcelable(Intents.EXTRA_CATEGORY));
    }
    adapter = new ProductListAdapter();
    initLoader();
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_product_list, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    initList();
  }

  @Override public void onDestroyView() {
    ButterKnife.unbind(this);
    super.onDestroyView();
  }

  @Override protected void reload() {
    restartLoader();
  }

  @Override public void onRefresh() {
    App.requestSync();
  }

  @Override public final void onLoadFinished(Loader<ProductListLoader.Result> loader,
      ProductListLoader.Result data) {
    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }

    if (data != null) {
      adapter.setData(data.getProducts());
      adapter.notifyDataSetChanged();
    }
  }

  @Override public Loader<ProductListLoader.Result> onCreateLoader(int id, Bundle args) {
    if (category == null) {
      return ProductListLoader.newInstance();
    }

    return ProductListLoader.forCategory(category.remoteId());
  }

  @Override public void onLoaderReset(Loader<ProductListLoader.Result> loader) {
  }

  private void initLoader() {
    getLoaderManager().initLoader(LOADER_ID, null, this);
  }

  private void restartLoader() {
    getLoaderManager().restartLoader(LOADER_ID, null, this);
  }

  @OnItemClick(R.id.list)
  void onListItemClick(int position) {
    Product product = adapter.getItem(position);
    startActivity(new Intent(getActivity(), ProductActivity.class)
        .putExtra(Intents.EXTRA_PRODUCT, Parcels.wrap(product)));
  }

  private void initList() {
    swipeRefreshLayout.setOnRefreshListener(this);
    listView.setAdapter(adapter);
  }

  public void setCategory(@Nullable Category category) {
    this.category = category;
  }
}
