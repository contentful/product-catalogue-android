package catalogue.templates.contentful.loaders;

import catalogue.templates.contentful.adapters.NavigationAdapter;
import catalogue.templates.contentful.dto.Category;
import catalogue.templates.contentful.lib.RealmConverter;
import catalogue.templates.contentful.sync.RealmCategory;
import catalogue.templates.contentful.sync.RealmProduct;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NavLoader.
 */
public class NavLoader extends AbsLoader<List<NavigationAdapter.Item>> {
  @Override protected List<NavigationAdapter.Item> performLoad() {
    List<NavigationAdapter.Item> res = null;
    Map<Category, Integer> categories = getCategoriesWithCount();
    if (categories != null) {
      res = new ArrayList<>(NavigationAdapter.PRIMARY_DATA);

      for (Map.Entry<Category, Integer> entry : categories.entrySet()) {
        res.add(NavigationAdapter.itemFromCategory(entry.getKey(), entry.getValue()));
      }
    }

    return res;
  }

  private Map<Category, Integer> getCategoriesWithCount() {
    Map<Category, Integer> res = new HashMap<>();

    Realm realm = Realm.getInstance(getContext());
    try {
      RealmResults<RealmCategory> categories = realm.where(RealmCategory.class).findAll();
      for (RealmCategory category : categories) {
        int count = Long.valueOf(realm.where(RealmProduct.class)
            .equalTo("categories.remoteId", category.getRemoteId())
            .count()).intValue();

        res.put(RealmConverter.category(category), count);
      }
    } finally {
      realm.close();
    }

    return res;
  }
}