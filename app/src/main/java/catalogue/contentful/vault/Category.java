package catalogue.contentful.vault;

import catalogue.contentful.lib.Const;
import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;
import org.parceler.Parcel;

@ContentType(Const.CONTENT_TYPE_CATEGORY)
@Parcel
public class Category extends Resource {
  @Field String title;

  @Field Asset icon;

  @Field String categoryDescription;

  public String title() {
    return title;
  }

  public Asset icon() {
    return icon;
  }

  public String categoryDescription() {
    return categoryDescription;
  }
}
