package catalogue.templates.contentful.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.Intents;
import catalogue.templates.contentful.activities.MainActivity;
import catalogue.templates.contentful.lib.ClientProvider;
import catalogue.templates.contentful.lib.Const;
import catalogue.templates.contentful.lib.Preferences;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.Constants;
import com.contentful.java.cda.model.CDAAsset;
import com.contentful.java.cda.model.CDAEntry;
import com.contentful.java.cda.model.CDAResource;
import com.contentful.java.cda.model.CDASyncedSpace;
import io.realm.Realm;
import io.realm.RealmObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit.RetrofitError;

/** Synchronize data from Contentful to a local realm db via the Contentful Sync API. */
public class SyncService extends IntentService {
  public SyncService() {
    super(SyncService.class.getName());
  }

  public static void sync() {
    Context context = App.get();
    context.startService(new Intent(context, SyncService.class).setAction(Intents.ACTION_SYNC));
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

    if (Intents.ACTION_SYNC.equals(action)) {
      actionSync();
    } else if (Intents.ACTION_CHANGE_SPACE.equals(action)) {
      actionChangeSpace(intent);
    }
  }

  private void actionChangeSpace(Intent intent) {
    String spaceId = intent.getStringExtra(Intents.EXTRA_SPACE_ID);
    String token = intent.getStringExtra(Intents.EXTRA_ACCESS_TOKEN);

    // Clear database records.
    clearDb();

    // Persist credentials to Shared Preferences.
    setCredentials(spaceId, token);

    // Launch / refresh MainActivity.
    startActivity(new Intent(getApplicationContext(), MainActivity.class)
        .setAction(intent.getAction())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

    // Trigger sync.
    SyncService.sync();
  }

  private void clearDb() {
    Realm realm = Realm.getInstance(this);

    try {
      realm.beginTransaction();
      realm.allObjects(RealmBrand.class).clear();
      realm.allObjects(RealmCategory.class).clear();
      realm.allObjects(RealmProduct.class).clear();
      realm.commitTransaction();
    } finally {
      realm.close();
    }
  }

  private void setCredentials(String spaceId, String token) {
    Preferences.get().edit()
        .remove(Preferences.KEY_SYNC_TOKEN)
        .putString(Preferences.KEY_SPACE_ID, spaceId)
        .putString(Preferences.KEY_ACCESS_TOKEN, token)
        .commit();

    // Reset ClientProvider singleton.
    ClientProvider.reset();
  }

  /**
   * Handles an {@link Intents#ACTION_SYNC} intent.
   */
  private void actionSync() {
    try {
      performSync();
    } catch (RetrofitError e) {
      e.printStackTrace();
      sendErrorBroadcast(e);
    } finally {
      sendReloadBroadcast();
    }
  }

  /**
   * Sends an error broadcast to any subscribers.
   *
   * @param e original error that triggered this
   */
  private static void sendErrorBroadcast(RetrofitError e) {
    Context context = App.get();
    Integer statusCode = null;

    if (RetrofitError.Kind.HTTP.equals(e.getKind())) {
      statusCode = e.getResponse().getStatus();
    }

    context.sendBroadcast(
        new Intent(Intents.ACTION_SHOW_ERROR).putExtra(Intents.EXTRA_STATUS_CODE, statusCode));
  }

  /** Sends a reload request broadcast to any subscribers. */
  private static void sendReloadBroadcast() {
    Context context = App.get();
    context.sendOrderedBroadcast(new Intent(Intents.ACTION_RELOAD), null);
  }

  /** Synchronizes the local realm database with the remote space data. */
  private static void performSync() {
    CDAClient client = ClientProvider.get();
    String syncToken = Preferences.getSyncToken();
    boolean initial = syncToken == null;
    CDASyncedSpace space;

    if (initial) {
      space = client.synchronization().performInitial();
    } else {
      space = client.synchronization().performWithToken(syncToken);
    }

    ArrayList<CDAResource> items = space.getItems();
    if (items.size() > 0) {
      Realm realm = Realm.getInstance(App.get());

      try {
        realm.beginTransaction();

        for (CDAResource resource : items) {
          if (isOfType(resource, Constants.CDAResourceType.Entry)) {
            CDAEntry entry = (CDAEntry) resource;

            if (!initial && isDeleted(resource)) {
              delete(realm, entry);
            } else {
              save(realm, entry);
            }
          }
        }

        realm.commitTransaction();
      } finally {
        realm.close();
      }
    }

    saveSyncToken(space.getSyncToken());
  }
  
  private static void save(Realm realm, CDAEntry entry) {
    String contentTypeId = extractContentTypeId(entry);

    if (Const.CONTENT_TYPE_BRAND.equals(contentTypeId)) {
      saveBrand(realm, entry);
    } else if (Const.CONTENT_TYPE_CATEGORY.equals(contentTypeId)) {
      saveCategory(realm, entry);
    } else if (Const.CONTENT_TYPE_PRODUCT.equals(contentTypeId)) {
      saveProduct(realm, entry);
    }
  }

  private static RealmBrand saveBrand(Realm realm, CDAEntry entry) {
    String remoteId = (String) entry.getSys().get("id");
    Map fields = entry.getFields();

    RealmBrand brand = realm.where(RealmBrand.class)
        .equalTo("remoteId", remoteId)
        .findFirst();

    if (brand == null) {
      brand = realm.createObject(RealmBrand.class);
    }

    brand.setRemoteId(remoteId);
    brand.setCompanyName(ifNull((String) fields.get("companyName")));

    // Logo
    CDAAsset logo = (CDAAsset) fields.get("logo");
    if (logo != null) {
      brand.setLogo(logo.getUrl());
    }

    brand.setDescription(ifNull((String) fields.get("companyDescription")));
    brand.setWebsite(ifNull((String) fields.get("website")));
    brand.setTwitter(ifNull((String) fields.get("twitter")));
    brand.setEmail(ifNull((String) fields.get("email")));

    // Phone numbers
    List phoneNumbers = (List) fields.get("phone");
    if (phoneNumbers != null) {
      brand.setPhoneNumbers(TextUtils.join(",", phoneNumbers));
    }

    return brand;
  }

  private static RealmCategory saveCategory(Realm realm, CDAEntry entry) {
    String remoteId = (String) entry.getSys().get("id");
    Map fields = entry.getFields();

    RealmCategory category = realm.where(RealmCategory.class)
        .equalTo("remoteId", remoteId)
        .findFirst();

    if (category == null) {
      category = realm.createObject(RealmCategory.class);
    }

    category.setRemoteId(remoteId);
    category.setTitle(ifNull((String) fields.get("title")));

    // Icon
    CDAAsset asset = (CDAAsset) fields.get("icon");
    if (asset != null) {
      category.setIcon(asset.getUrl());
    }

    category.setDescription(ifNull((String) fields.get("categoryDescription")));
    return category;
  }

  private static RealmProduct saveProduct(Realm realm, CDAEntry entry) {
    String remoteId = (String) entry.getSys().get("id");
    Map fields = entry.getFields();

    RealmProduct product = realm.where(RealmProduct.class)
        .equalTo("remoteId", remoteId)
        .findFirst();

    if (product == null) {
      product = realm.createObject(RealmProduct.class);
    }

    product.setRemoteId(remoteId);
    product.setName(ifNull((String) fields.get("productName")));
    product.setDescription(ifNull((String) fields.get("productDescription")));
    product.setSizeTypeColor(ifNull((String) fields.get("sizetypecolor")));

    // Images
    List images = (List) fields.get("image");
    if (images != null) {
      ArrayList<String> imageList = new ArrayList<>();
      for (Object obj : images) {
        imageList.add(((CDAAsset) obj).getUrl());
      }
      product.setImages(TextUtils.join(",", imageList));
    }

    // Tags
    List tags = (List) fields.get("tags");
    if (tags != null) {
      product.setTags(TextUtils.join(",", tags));
    }

    // Categories
    List categories = (List) fields.get("categories");
    if (categories != null) {
      product.getCategories().clear();

      for (Object res : categories) {
        product.getCategories().add(saveCategory(realm, (CDAEntry) res));
      }
    }

    // Price
    Double price = (Double) fields.get("price");
    if (price != null) {
      product.setPrice(price);
    }

    // Brand
    CDAEntry brand = (CDAEntry) fields.get("brand");
    if (brand != null) {
      product.setBrand(saveBrand(realm, brand));
    }

    // Quantity
    Double quantity = (Double) fields.get("quantity");
    if (quantity != null) {
      product.setQuantity(quantity.intValue());
    }

    product.setSku(ifNull((String) fields.get("sku")));
    product.setWebsite(ifNull((String) fields.get("website")));

    return product;
  }

  private static void saveSyncToken(String syncToken) {
    Preferences.get().edit().putString(Preferences.KEY_SYNC_TOKEN, syncToken).commit();
  }

  private static void delete(Realm realm, CDAEntry entry) {
    String contentTypeId = extractContentTypeId(entry);
    Class<? extends RealmObject> clazz = classForContentType(contentTypeId);
    String remoteId = (String) entry.getSys().get("id");
    realm.where(clazz).equalTo("remoteId", remoteId).findAll().clear();
  }

  private static boolean isDeleted(CDAResource resource) {
    String type = (String) resource.getSys().get("type");
    return Constants.CDAResourceType.DeletedAsset.toString().equals(type) ||
        Constants.CDAResourceType.DeletedEntry.toString().equals(type);
  }

  private static boolean isOfType(CDAResource resource, Constants.CDAResourceType type) {
    return type.equals(Constants.CDAResourceType.valueOf((String) resource.getSys().get("type")));
  }

  public static String extractContentTypeId(CDAEntry entry) {
    Map map = (Map) entry.getSys().get("contentType");
    map = (Map) map.get("sys");
    return (String) map.get("id");
  }

  public static Class<? extends RealmObject> classForContentType(String contentTypeId) {
    if (Const.CONTENT_TYPE_BRAND.equals(contentTypeId)) {
      return RealmBrand.class;
    } else if (Const.CONTENT_TYPE_CATEGORY.equals(contentTypeId)) {
      return RealmCategory.class;
    } else if (Const.CONTENT_TYPE_PRODUCT.equals(contentTypeId)) {
      return RealmProduct.class;
    }

    throw new IllegalArgumentException("Invalid content type id " + contentTypeId);
  }

  public static String ifNull(String str) {
    if (str == null) {
      return "";
    }

    return str;
  }
}
