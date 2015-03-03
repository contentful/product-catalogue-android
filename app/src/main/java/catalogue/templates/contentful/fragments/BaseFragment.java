package catalogue.templates.contentful.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import catalogue.templates.contentful.Intents;

/**
 * Base fragment implementation.
 */
public abstract class BaseFragment extends Fragment {
  private BroadcastReceiver reloadReceiver;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    createReceivers();
    getActivity().registerReceiver(reloadReceiver, new IntentFilter(Intents.ACTION_RELOAD));
  }

  @Override public void onDestroy() {
    getActivity().unregisterReceiver(reloadReceiver);

    super.onDestroy();
  }

  private void createReceivers() {
    reloadReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        reload();
      }
    };
  }

  protected abstract void reload();
}