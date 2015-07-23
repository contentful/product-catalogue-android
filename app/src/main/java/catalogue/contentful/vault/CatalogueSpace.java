package catalogue.contentful.vault;

import catalogue.contentful.lib.Const;
import com.contentful.vault.Space;

@Space(value = Const.SPACE_ID, models = { Brand.class, Category.class, Product.class})
public class CatalogueSpace {
}
