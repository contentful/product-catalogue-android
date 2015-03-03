package catalogue.templates.contentful.loaders;

import android.support.annotation.Nullable;
import catalogue.templates.contentful.dto.Category;
import catalogue.templates.contentful.dto.Product;
import catalogue.templates.contentful.lib.RealmConverter;
import catalogue.templates.contentful.sync.RealmCategory;
import catalogue.templates.contentful.sync.RealmProduct;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

/** Loader for a list of products. */
public class ProductListLoader extends AbsLoader<ProductListLoader.Result> {
  private final String categoryRemoteId;

  private ProductListLoader(@Nullable String categoryRemoteId) {
    super();
    this.categoryRemoteId = categoryRemoteId;
  }

  public static ProductListLoader newInstance() {
    return new ProductListLoader(null);
  }

  public static ProductListLoader forCategory(String remoteId) {
    return new ProductListLoader(remoteId);
  }

  @Override protected Result performLoad() {
    Result result;
    Realm realm = Realm.getInstance(getContext());

    try {
      List<Product> products = getProductList(realm, categoryRemoteId);

      Category category = null;
      if (categoryRemoteId != null) {
        category = getCategory(realm, categoryRemoteId);
      }

      result = new Result(products, category);
    } finally {
      realm.close();
    }

    return result;
  }

  private Category getCategory(Realm realm, String remoteId) {
    RealmCategory realmCategory =
        realm.where(RealmCategory.class).equalTo("remoteId", remoteId).findFirst();

    if (realmCategory != null) {
      return RealmConverter.category(realmCategory);
    }
    
    return null;
  }

  private List<Product> getProductList(Realm realm, String categoryRemoteId) {
    RealmResults<RealmProduct> products;

    if (categoryRemoteId == null) {
      products = realm.allObjects(RealmProduct.class);
    } else {
      products = realm.where(RealmProduct.class)
          .equalTo("categories.remoteId", categoryRemoteId)
          .findAll();
    }

    return convert(products);
  }

  private List<Product> convert(RealmResults<RealmProduct> products) {
    List<Product> result = new ArrayList<>();

    for (RealmProduct product : products) {
      result.add(RealmConverter.product(product));
    }

    return result;
  }

  public static class Result {
    private final List<Product> products;
    private final Category category;

    public Result(List<Product> products, Category category) {
      this.products = products;
      this.category = category;
    }

    public List<Product> getProducts() {
      return products;
    }

    public Category getCategory() {
      return category;
    }
  }
}
