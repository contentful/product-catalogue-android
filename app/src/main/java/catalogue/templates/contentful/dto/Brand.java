package catalogue.templates.contentful.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.List;

/** Brand model. */
@AutoValue
public abstract class Brand implements Parcelable {
  public static Brand create(String remoteId, @Nullable String companyName,
      @Nullable String description, @Nullable String website, @Nullable String twitter,
      @Nullable String email, @Nullable List<String> phoneNumbers) {
    return new AutoValue_Brand(remoteId, companyName, description, website, twitter, email,
        phoneNumbers);
  }

  public abstract String remoteId();

  @Nullable
  public abstract String companyName();

  @Nullable
  public abstract String description();

  @Nullable
  public abstract String website();

  @Nullable
  public abstract String twitter();

  @Nullable
  public abstract String email();

  @Nullable
  public abstract List<String> phoneNumbers();

  @Override public int describeContents() {
    return 0;
  }

  // Parcelable :(
  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(remoteId());
    dest.writeValue(companyName());
    dest.writeValue(description());
    dest.writeValue(website());
    dest.writeValue(twitter());
    dest.writeValue(email());
    dest.writeValue(phoneNumbers());
  }

  public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
    @SuppressWarnings("unchecked")
    public Brand createFromParcel(Parcel in) {
      ClassLoader classLoader = getClass().getClassLoader();

      String remoteId = (String) in.readValue(classLoader);
      String companyName = (String) in.readValue(classLoader);
      String description = (String) in.readValue(classLoader);
      String website = (String) in.readValue(classLoader);
      String twitter = (String) in.readValue(classLoader);
      String email = (String) in.readValue(classLoader);
      List phoneNumbers = (List) in.readValue(classLoader);

      return Brand.create(remoteId, companyName, description, website, twitter, email,
          phoneNumbers);
    }

    public Brand[] newArray(int size) {
      return new Brand[size];
    }
  };
}
