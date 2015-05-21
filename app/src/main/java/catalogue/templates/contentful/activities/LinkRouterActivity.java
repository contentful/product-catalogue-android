package catalogue.templates.contentful.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.Intents;
import catalogue.templates.contentful.lib.ClientProvider;
import catalogue.templates.contentful.lib.Preferences;

/** Interceptor for {@link Intent#ACTION_VIEW} intents with a pre-defined schema. */
public class LinkRouterActivity extends Activity {
  public static final String CMD_OPEN = "open";

  public static final String PATH_SPACE = "space";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri data = getIntent().getData();

    if (data != null) {
      if (CMD_OPEN.equals(data.getHost())) {
        String firstPathSegment = data.getPathSegments().get(0);

        switch (firstPathSegment) {
          case PATH_SPACE:
            openSpace(data);
            break;
        }
      }
    }

    finish();
  }

  private void openSpace(Uri data) {
    String spaceId = data.getLastPathSegment();
    String token = data.getQueryParameter("access_token");

    if (spaceId != null && token != null) {
      // Save credentials to Shared Preferences.
      Preferences.get().edit()
          .putString(Preferences.KEY_SPACE_ID, spaceId)
          .putString(Preferences.KEY_ACCESS_TOKEN, token)
          .commit();

      // Reset ClientProvider singleton.
      ClientProvider.reset();

      // Request sync, invalidating any existing data.
      App.requestSync(true);

      startActivity(new Intent(this, MainActivity.class)
          .setAction(Intents.ACTION_CHANGE_SPACE)
          .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
  }
}
