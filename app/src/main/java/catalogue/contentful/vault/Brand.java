package catalogue.contentful.vault;

import catalogue.contentful.lib.Const;
import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;
import java.util.List;
import org.parceler.Parcel;

@ContentType(Const.CONTENT_TYPE_BRAND)
@Parcel
public class Brand extends Resource {
  @Field String companyName;

  @Field String companyDescription;

  @Field Asset logo;

  @Field String website;

  @Field String twitter;

  @Field String email;

  @Field List<String> phone;

  public String companyName() {
    return companyName;
  }

  public String companyDescription() {
    return companyDescription;
  }

  public Asset logo() {
    return logo;
  }

  public String website() {
    return website;
  }

  public String twitter() {
    return twitter;
  }

  public String email() {
    return email;
  }

  public List<String> phone() {
    return phone;
  }
}
