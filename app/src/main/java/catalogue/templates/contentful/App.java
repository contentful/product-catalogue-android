package catalogue.templates.contentful;

import android.app.Application;
import catalogue.templates.contentful.lib.ClientProvider;
import catalogue.templates.contentful.vault.CatalogueSpace;
import com.contentful.vault.SyncConfig;
import com.contentful.vault.Vault;

/** Main Application class. */
public class App extends Application {
  private static App sInstance;

  @Override public void onCreate() {
    super.onCreate();
    sInstance = this;
    requestSync();
  }

  public static App get() {
    return sInstance;
  }

  public static void requestSync() {
    requestSync(false);
  }

  public static void requestSync(boolean invalidate) {
    Vault.with(get(), CatalogueSpace.class).requestSync(
        SyncConfig.builder()
            .setClient(ClientProvider.get())
            .setInvalidate(invalidate)
            .build());
  }
}
