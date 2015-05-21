package catalogue.templates.contentful;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import catalogue.templates.contentful.activities.MainActivity;
import catalogue.templates.contentful.lib.ClientProvider;
import catalogue.templates.contentful.lib.Preferences;

public class SyncService extends IntentService {
  public SyncService() {
    super(SyncService.class.getName());
  }

  public static void changeSpace(String spaceId, String accessToken) {
    Context context = App.get();
    context.startService(new Intent(context, SyncService.class)
        .setAction(Intents.ACTION_CHANGE_SPACE)
        .putExtra(Intents.EXTRA_SPACE_ID, spaceId)
        .putExtra(Intents.EXTRA_ACCESS_TOKEN, accessToken));
  }

  @Override protected void onHandleIntent(Intent intent) {
    String action = intent.getAction();
    if (Intents.ACTION_CHANGE_SPACE.equals(action)) {
      actionChangeSpace(intent);
    }
  }

  private void actionChangeSpace(Intent intent) {
    String spaceId = intent.getStringExtra(Intents.EXTRA_SPACE_ID);
    String token = intent.getStringExtra(Intents.EXTRA_ACCESS_TOKEN);

    // Persist credentials to Shared Preferences.
    setCredentials(spaceId, token);

    // Request sync, invalidating any existing data.
    App.requestSync(true);

    // Launch / refresh MainActivity.
    startActivity(new Intent(getApplicationContext(), MainActivity.class)
        .setAction(intent.getAction())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  private void setCredentials(String spaceId, String token) {
    Preferences.get().edit()
        .putString(Preferences.KEY_SPACE_ID, spaceId)
        .putString(Preferences.KEY_ACCESS_TOKEN, token)
        .commit();

    // Reset ClientProvider singleton.
    ClientProvider.reset();
  }
}
