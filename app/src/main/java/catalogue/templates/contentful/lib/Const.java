package catalogue.templates.contentful.lib;

import catalogue.templates.contentful.App;
import catalogue.templates.contentful.R;

/** Const. */
public class Const {
  private Const() {
    throw new AssertionError();
  }

  // Content Type IDs
  public static final String CONTENT_TYPE_BRAND =
      App.get().getString(R.string.content_type_brand);

  public static final String CONTENT_TYPE_CATEGORY =
      App.get().getString(R.string.content_type_category);

  public static final String CONTENT_TYPE_PRODUCT =
      App.get().getString(R.string.content_type_product);
}
