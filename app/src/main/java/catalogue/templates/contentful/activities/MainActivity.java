package catalogue.templates.contentful.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import catalogue.templates.contentful.Intents;
import catalogue.templates.contentful.R;
import catalogue.templates.contentful.fragments.NavigationDrawerFragment;
import catalogue.templates.contentful.fragments.ProductListFragment;
import catalogue.templates.contentful.lib.ItemClickListener;
import catalogue.templates.contentful.vault.Category;
import java.util.HashMap;
import org.parceler.Parcels;

import static catalogue.templates.contentful.adapters.NavigationAdapter.Item;
import static catalogue.templates.contentful.adapters.NavigationAdapter.Title;

/**
 * MainActivity.
 */
public class MainActivity extends AbsActivity implements ItemClickListener<Item> {
  private ActionBarDrawerToggle drawerToggle;

  private NavigationDrawerFragment navFragment;

  private HashMap<Title, Fragment> fragments;

  private BroadcastReceiver errorReceiver;

  @InjectView(R.id.toolbar) Toolbar toolbar;

  @InjectView(R.id.drawer) DrawerLayout drawerLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    // Navigation
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
        R.string.drawer_close);

    drawerLayout.setDrawerListener(drawerToggle);

    // Configure custom Toolbar
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Fragments
    createFragments();

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.fragment_container, fragments.get(Title.PRODUCTS)).commit();
    }

    navFragment = (NavigationDrawerFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment_drawer);

    navFragment.setListener(this);

    // Receivers
    errorReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        showErrorDialog(intent.getIntExtra(Intents.EXTRA_STATUS_CODE, -1));
      }
    };
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override protected void onResume() {
    super.onResume();

    registerReceiver(errorReceiver, new IntentFilter(Intents.ACTION_SHOW_ERROR));
  }

  @Override protected void onPause() {
    unregisterReceiver(errorReceiver);

    super.onPause();
  }

  @Override protected void onDestroy() {
    if (navFragment != null) {
      // Clear Activity reference
      navFragment.setListener(null);
    }

    super.onDestroy();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    // Handle your other action bar items...
    return super.onOptionsItemSelected(item);
  }

  /** Handle item selection from nav drawer. */
  @Override public void onItemClick(Item item) {
    FragmentManager fm = getSupportFragmentManager();
    Title title = item.getTitle();

    if (Title.SINGLE_CATEGORY.equals(title)) {
      // Single Category selected, start ProductListFragment and pass the category.
      Category category = item.getObject();
      Bundle b = new Bundle();
      b.putParcelable(Intents.EXTRA_CATEGORY, Parcels.wrap(category));

      clearBackstack();

      // Create fragment
      Fragment fragment = ProductListFragment.instantiate(this,
          ProductListFragment.class.getName(), b);

      fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    } else {
      // Find Fragment matching the selected Title
      Fragment fragment = fragments.get(title);

      if (fm.findFragmentById(fragment.getId()) == null) {
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
      } else {
        clearBackstack();
      }
    }

    drawerLayout.closeDrawers();
  }

  /** Initialize Fragments. */
  private void createFragments() {
    fragments = new HashMap<>();

    fragments.put(Title.PRODUCTS,
        ProductListFragment.instantiate(this, ProductListFragment.class.getName()));
  }

  private void showErrorDialog(int httpStatusCode) {
    int title = R.string.dialog_general_error_title;
    int message = R.string.dialog_general_error_message;

    if (httpStatusCode == 401) {
      message = R.string.dialog_unauthorized_error_message;
    }

    new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, null)
        .show();
  }

  private void clearBackstack() {
    FragmentManager fm = getSupportFragmentManager();

    if (fm.getBackStackEntryCount() > 0) {
      fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
  }
}