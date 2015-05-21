package catalogue.templates.contentful.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import catalogue.templates.contentful.App;
import catalogue.templates.contentful.R;
import catalogue.templates.contentful.vault.Category;
import java.util.Arrays;
import java.util.List;

public class NavigationAdapter extends BaseAdapter {
  // Primary data set
  public static List<Item> PRIMARY_DATA = Arrays.asList(
      new Item.Builder().setTitle(Title.PRODUCTS)
          .setLeftDrawable(R.drawable.nav_products_img_selector)
          .setLeftTitle(App.get().getString(R.string.nav_products))
          .create(),
      new Item.Builder().setTitle(Title.CATEGORIES)
          .setLeftDrawable(R.drawable.ic_categories_off)
          .setLeftTitle(App.get().getString(R.string.nav_categories))
          .create());

  // Secondary data set
  private List<Item> secondaryData;

  // Active data set
  private List<Item> activeData = PRIMARY_DATA;

  @Override public int getCount() {
    if (activeData == null) {
      return 0;
    }

    return activeData.size();
  }

  @Override public Item getItem(int position) {
    return activeData.get(position);
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(
          R.layout.drawer_item, parent, false);

      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Context context = holder.root.getContext();
    Item item = activeData.get(position);

    // Left image
    if (item.leftDrawable == null) {
      holder.imageView.setImageDrawable(null);
    } else {
      holder.imageView.setImageDrawable(context.getResources().getDrawable(item.leftDrawable));
    }

    // Left title
    holder.tvLeft.setText(item.leftTitle);

    // Right title
    holder.tvRight.setText(item.rightTitle);

    // Right image
    Drawable rightDrawable = null;
    if (item.rightDrawable != null) {
      rightDrawable = context.getResources().getDrawable(item.rightDrawable);
    }

    holder.tvRight.setCompoundDrawables(null, null, rightDrawable, null);

    return convertView;
  }

  public static Item itemFromCategory(Category category, int count) {
    return new Item.Builder().setTitle(Title.SINGLE_CATEGORY)
        .setLeftTitle(category.title())
        .setRightTitle(App.get().getString(R.string.category, count))
        .setObject(category)
        .create();
  }

  public void switchToPrimary() {
    activeData = PRIMARY_DATA;
  }

  public void switchToSecondary() {
    activeData = secondaryData;
  }

  public boolean isPrimary() {
    return activeData == PRIMARY_DATA;
  }

  public void setSecondaryData(List<Item> secondaryData) {
    this.secondaryData = secondaryData;
  }

  public void toggle() {
    if (activeData == PRIMARY_DATA) {
      switchToSecondary();
    } else {
      switchToPrimary();
    }
  }

  public enum Title {
    PRODUCTS,
    CATEGORIES,
    SINGLE_CATEGORY
  }

  public static class Item {
    final Integer leftDrawable;
    final Integer rightDrawable;
    final String leftTitle;
    final String rightTitle;
    final Title title;
    final Object object;

    private Item(Builder builder) {
      this.title = builder.title;
      this.leftDrawable = builder.leftDrawable;
      this.rightDrawable = builder.rightDrawable;
      this.leftTitle = builder.leftTitle;
      this.rightTitle = builder.rightTitle;
      this.object = builder.object;
    }

    public Title getTitle() {
      return title;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject() {
      return (T) object;
    }

    public static class Builder {
      private Integer leftDrawable;
      private Integer rightDrawable;
      private String leftTitle;
      private String rightTitle;
      private Title title;
      private Object object;

      public Builder setLeftDrawable(@DrawableRes Integer leftDrawable) {
        this.leftDrawable = leftDrawable;
        return this;
      }

      public Builder setRightDrawable(@DrawableRes Integer rightDrawable) {
        this.rightDrawable = rightDrawable;
        return this;
      }

      public Builder setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
        return this;
      }

      public Builder setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
        return this;
      }

      public Builder setTitle(Title title) {
        this.title = title;
        return this;
      }

      public Builder setObject(Object object) {
        this.object = object;
        return this;
      }

      public Item create() {
        return new Item(this);
      }
    }
  }

  static class ViewHolder {
    final View root;

    @InjectView(R.id.image) ImageView imageView;

    @InjectView(R.id.tv_left) TextView tvLeft;

    @InjectView(R.id.tv_right) TextView tvRight;

    ViewHolder(View root) {
      this.root = root;
      ButterKnife.inject(this, root);
    }
  }
}