package catalogue.templates.contentful.loaders;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.adapters.NavigationAdapter;
import catalogue.templates.contentful.vault.CatalogueSpace;
import catalogue.templates.contentful.vault.Category;
import com.contentful.vault.Vault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavLoader extends AbsLoader<List<NavigationAdapter.Item>> {
  private static final String QUERY_CATEGORY_COUNT =
      "SELECT COUNT(*) FROM `links` WHERE `child` = ?";

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
    Vault vault = Vault.with(App.get(), CatalogueSpace.class);

    List<Category> categories = vault.fetch(Category.class).all();
    Map<Category, Integer> res = new HashMap<>();
    for (Category category : categories) {
      SQLiteDatabase db = vault.getReadableDatabase();
      int count = 0;
      Cursor cursor = db.rawQuery(QUERY_CATEGORY_COUNT, new String[] { category.remoteId() });
      try {
        if (cursor.moveToFirst()) {
          count = cursor.getInt(0);
        }
      } finally {
        cursor.close();
      }
      res.put(category, count);
    }

    return res;
  }
}