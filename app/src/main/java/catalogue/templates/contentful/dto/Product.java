package catalogue.templates.contentful.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.List;

/** Product model. */
@AutoValue
public abstract class Product implements Parcelable {
  public static Product create(String remoteId, @Nullable String name, @Nullable String slug,
      @Nullable String description, @Nullable String sizeTypeColor, @Nullable List<String> images,
      @Nullable List<String> tags, @Nullable List<Category> categories, @Nullable Double price,
      @Nullable Brand brand, @Nullable Integer quantity, @Nullable String sku,
      @Nullable String website) {
    return new AutoValue_Product(remoteId, name, slug, description, sizeTypeColor, images, tags,
        categories, price, brand, quantity, sku, website);
  }

  public abstract String remoteId();

  @Nullable
  public abstract String name();

  @Nullable
  public abstract String slug();

  @Nullable
  public abstract String description();

  @Nullable
  public abstract String sizeTypeColor();

  @Nullable
  public abstract List<String> images();

  @Nullable
  public abstract List<String> tags();

  @Nullable
  public abstract List<Category> categories();

  @Nullable
  public abstract Double price();

  @Nullable
  public abstract Brand brand();

  @Nullable
  public abstract Integer quantity();

  @Nullable
  public abstract String sku();

  @Nullable
  public abstract String website();

  // Parcelable :(
  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(remoteId());
    dest.writeValue(name());
    dest.writeValue(slug());
    dest.writeValue(description());
    dest.writeValue(sizeTypeColor());
    dest.writeValue(images());
    dest.writeValue(tags());
    dest.writeValue(categories());
    dest.writeValue(price());
    dest.writeValue(brand());
    dest.writeValue(quantity());
    dest.writeValue(sku());
    dest.writeValue(website());
  }

  public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
    @SuppressWarnings("unchecked")
    public Product createFromParcel(Parcel in) {
      ClassLoader classLoader = getClass().getClassLoader();

      String remoteId = (String) in.readValue(classLoader);
      String name = (String) in.readValue(classLoader);
      String slug = (String) in.readValue(classLoader);
      String description = (String) in.readValue(classLoader);
      String sizeTypeColor = (String) in.readValue(classLoader);
      List images = (List) in.readValue(classLoader);
      List tags = (List) in.readValue(classLoader);
      List categories = (List) in.readValue(classLoader);
      Double price = (Double) in.readValue(classLoader);
      Brand brand = (Brand) in.readValue(classLoader);
      Integer quantity = (Integer) in.readValue(classLoader);
      String sku = (String) in.readValue(classLoader);
      String website = (String) in.readValue(classLoader);

      return Product.create(remoteId, name, slug, description, sizeTypeColor, images, tags,
          categories, price, brand, quantity, sku, website);
    }

    public Product[] newArray(int size) {
      return new Product[size];
    }
  };
}
