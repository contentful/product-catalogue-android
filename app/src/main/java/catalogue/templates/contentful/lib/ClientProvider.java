package catalogue.templates.contentful.lib;

import com.contentful.java.cda.CDAClient;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class ClientProvider {
  private static CDAClient instance;

  private static final Object LOCK = new Object();

  private ClientProvider() {
    throw new AssertionError();
  }

  @SuppressWarnings("ConstantConditions")
  public static CDAClient get() {
    synchronized (LOCK) {
      if (instance == null) {
        // Extract credentials from SharedPreferences
        String spaceId = defaultIfBlank(Preferences.getSpaceId(), Const.SPACE_ID);

        String accessToken = defaultIfBlank(Preferences.getAccessToken(), Const.ACCESS_TOKEN);

        instance = CDAClient.builder()
            .setSpace(spaceId)
            .setToken(accessToken)
            .build();
      }

      return instance;
    }
  }

  public static void reset() {
    synchronized (LOCK) {
      instance = null;
    }
  }
}
