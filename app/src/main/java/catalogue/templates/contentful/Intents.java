package catalogue.templates.contentful;

/**
 * Intent constants.
 */
public class Intents {
  private Intents() {
    throw new AssertionError();
  }

  private static final String PACKAGE_NAME = App.get().getPackageName();

  public static final String ACTION_RELOAD = PACKAGE_NAME + ".ACTION_RELOAD";
  public static final String ACTION_SHOW_ERROR = PACKAGE_NAME + ".ACTION_SHOW_ERROR";
  public static final String ACTION_CHANGE_SPACE = PACKAGE_NAME + ".ACTION_CHANGE_SPACE";
  public static final String ACTION_SYNC = PACKAGE_NAME + ".ACTION_SYNC";

  public static final String EXTRA_CATEGORY = PACKAGE_NAME + ".EXTRA_CATEGORY";
  public static final String EXTRA_PRODUCT = PACKAGE_NAME + ".EXTRA_PRODUCT";
  public static final String EXTRA_STATUS_CODE = PACKAGE_NAME + ".EXTRA_STATUS_CODE";
  public static final String EXTRA_SPACE_ID = PACKAGE_NAME + ".EXTRA_SPACE_ID";
  public static final String EXTRA_ACCESS_TOKEN = PACKAGE_NAME + ".EXTRA_ACCESS_TOKEN";
}
