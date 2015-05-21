package catalogue.templates.contentful.loaders;

import android.support.annotation.Nullable;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.vault.CatalogueSpace;
import catalogue.templates.contentful.vault.Category;
import catalogue.templates.contentful.vault.Product;
import com.contentful.vault.Vault;
import java.util.ArrayList;
import java.util.List;

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
    List<Product> products = Vault.with(App.get(), CatalogueSpace.class).fetch(Product.class).all();
    if (categoryRemoteId == null) {
      return new Result(products, null);
    }

    List<Product> filtered = new ArrayList<>();
    Category targetCategory = null;
    for (Product product : products) {
      for (Category category : product.categories()) {
        if (categoryRemoteId.equals(category.remoteId())) {
          filtered.add(product);
          targetCategory = category;
          break;
        }
      }
    }
    return new Result(filtered, targetCategory);
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
