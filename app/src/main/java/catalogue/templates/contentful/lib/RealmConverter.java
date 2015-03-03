package catalogue.templates.contentful.lib;

import catalogue.templates.contentful.dto.Brand;
import catalogue.templates.contentful.dto.Category;
import catalogue.templates.contentful.dto.Product;
import catalogue.templates.contentful.sync.RealmBrand;
import catalogue.templates.contentful.sync.RealmCategory;
import catalogue.templates.contentful.sync.RealmProduct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Converts realm objects to POJOs. */
public class RealmConverter {
  private RealmConverter() {
    throw new AssertionError();
  }

  public static Brand brand(RealmBrand brand) {
    List<String> phoneNumbers = split(brand.getPhoneNumbers());

    return Brand.create(brand.getRemoteId(), brand.getCompanyName(), brand.getDescription(),
        brand.getWebsite(), brand.getTwitter(), brand.getEmail(), phoneNumbers);
  }

  public static Category category(RealmCategory category) {
    return Category.create(category.getRemoteId(), category.getTitle(),
        category.getIcon(), category.getDescription());
  }

  public static Product product(RealmProduct product) {
    List<String> images = split(product.getImages());
    List<String> tags = split(product.getTags());

    // Categories
    List<Category> categories = new ArrayList<>();
    for (RealmCategory realmCategory : product.getCategories()) {
      categories.add(category(realmCategory));
    }

    Brand brand = brand(product.getBrand());

    return Product.create(product.getRemoteId(), product.getName(), product.getWebsite(),
        product.getDescription(), product.getSizeTypeColor(), images, tags, categories,
        product.getPrice(), brand, product.getQuantity(), product.getSku(), product.getWebsite());
  }

  private static List<String> split(String str) {
    return Arrays.asList(str.split("\\s*,\\s*"));
  }
}
