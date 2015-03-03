package catalogue.templates.contentful.sync;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/** RealmProduct. */
public class RealmProduct extends RealmObject {
  @PrimaryKey
  private String remoteId;

  private String name;
  private String slug;
  private String description;
  private String sizeTypeColor;
  private String images;
  private String tags;
  private RealmList<RealmCategory> categories;
  private double price;
  private RealmBrand brand;
  private int quantity;
  private String sku;
  private String website;

  public String getRemoteId() {
    return remoteId;
  }

  public void setRemoteId(String remoteId) {
    this.remoteId = remoteId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSizeTypeColor() {
    return sizeTypeColor;
  }

  public void setSizeTypeColor(String sizeTypeColor) {
    this.sizeTypeColor = sizeTypeColor;
  }

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public RealmList<RealmCategory> getCategories() {
    return categories;
  }

  public void setCategories(RealmList<RealmCategory> categories) {
    this.categories = categories;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public RealmBrand getBrand() {
    return brand;
  }

  public void setBrand(RealmBrand brand) {
    this.brand = brand;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }
}
