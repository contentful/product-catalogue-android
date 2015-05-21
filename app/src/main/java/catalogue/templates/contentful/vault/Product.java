package catalogue.templates.contentful.vault;

import catalogue.templates.contentful.lib.Const;
import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;
import java.util.List;
import org.parceler.Parcel;

@ContentType(Const.CONTENT_TYPE_PRODUCT)
@Parcel
public class Product extends Resource {
  @Field("productName")
  String name;

  @Field("productDescription")
  String description;

  @Field("sizetypecolor")
  String sizeTypeColor;

  @Field("image")
  List<Asset> images;

  @Field List<String> tags;

  @Field List<Category> categories;

  @Field Double price;

  @Field Brand brand;

  @Field Integer quantity;

  @Field String sku;

  @Field String website;

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public String sizeTypeColor() {
    return sizeTypeColor;
  }

  public List<Asset> images() {
    return images;
  }

  public List<String> tags() {
    return tags;
  }

  public List<Category> categories() {
    return categories;
  }

  public Double price() {
    return price;
  }

  public Brand brand() {
    return brand;
  }

  public Integer quantity() {
    return quantity;
  }

  public String sku() {
    return sku;
  }

  public String website() {
    return website;
  }
}
