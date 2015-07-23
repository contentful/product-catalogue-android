package catalogue.contentful.lib;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import catalogue.contentful.App;

public class Preferences {
  private Preferences() {
    throw new AssertionError();
  }

  public static final String KEY_SPACE_ID = "SPACE_ID";

  public static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";

  public static SharedPreferences get() {
    return PreferenceManager.getDefaultSharedPreferences(App.get());
  }

  public static String getSpaceId() {
    return get().getString(KEY_SPACE_ID, null);
  }

  public static String getAccessToken() {
    return get().getString(KEY_ACCESS_TOKEN, null);
  }
}