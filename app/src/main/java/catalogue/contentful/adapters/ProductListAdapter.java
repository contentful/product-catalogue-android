package catalogue.contentful.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import catalogue.contentful.App;
import catalogue.contentful.R;
import catalogue.contentful.lib.CircleTransform;
import catalogue.contentful.vault.Product;
import com.contentful.vault.Asset;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductListAdapter extends AbsListAdapter<Product, ProductListAdapter.ViewHolder> {
  @Override protected void bindView(ViewHolder holder, Product product, View rootView) {
    List<Asset> images = product.images();
    if (images.size() == 0) {
      holder.photo.setImageDrawable(null);
    } else {
      Picasso.with(rootView.getContext())
          .load(images.get(0).url())
          .fit()
          .centerInside()
          .transform(new CircleTransform())
          .into(holder.photo);
    }

    holder.title.setText(product.name());
    holder.price.setText(getPriceText(product.price()));
  }

  @Override protected ViewHolder createViewHolder(View rootView) {
    return new ViewHolder(rootView);
  }

  @Override protected int getLayoutResId() {
    return R.layout.list_item_product;
  }

  private String getPriceText(Double price) {
    if (price == null) {
      return App.get().getString(R.string.product_price_empty);
    } else {
      return App.get().getString(R.string.product_price, String.format("%1$,.2f", price));
    }
  }

  static class ViewHolder {
    @Bind(R.id.photo) ImageView photo;

    @Bind(R.id.title) TextView title;

    @Bind(R.id.price) TextView price;

    ViewHolder(View rootView) {
      ButterKnife.bind(this, rootView);
    }
  }
}
