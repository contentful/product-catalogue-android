package catalogue.templates.contentful.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;

/** Category model. */
@AutoValue
public abstract class Category implements Parcelable {
  public static Category create(String remoteId, @Nullable String title,
      @Nullable String icon, @Nullable String description) {
    return new AutoValue_Category(remoteId, title, icon, description);
  }

  public abstract String remoteId();

  @Nullable
  public abstract String title();

  @Nullable
  public abstract String icon();

  @Nullable
  public abstract String description();

  // Parcelable :(
  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(remoteId());
    dest.writeValue(title());
    dest.writeValue(icon());
    dest.writeValue(description());
  }

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
    @SuppressWarnings("unchecked")
    public Category createFromParcel(Parcel in) {
      ClassLoader classLoader = getClass().getClassLoader();

      String remoteId = (String) in.readValue(classLoader);
      String title = (String) in.readValue(classLoader);
      String icon = (String) in.readValue(classLoader);
      String description = (String) in.readValue(classLoader);

      return Category.create(remoteId, title, icon, description);
    }

    public Category[] newArray(int size) {
      return new Category[size];
    }
  };
}
