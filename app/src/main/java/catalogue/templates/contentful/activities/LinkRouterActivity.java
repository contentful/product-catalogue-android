package catalogue.templates.contentful.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import catalogue.templates.contentful.sync.SyncService;

/**
 * Responsible for intercepting {@link Intent#ACTION_VIEW} intents for a pre-defined schema.
 */
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

  /**
   * Change space credentials.
   *
   * @param data data uri
   */
  private void openSpace(Uri data) {
    String spaceId = data.getLastPathSegment();
    String token = data.getQueryParameter("access_token");

    if (spaceId != null && token != null) {
      SyncService.changeSpace(spaceId, token);
    }
  }
}
