package catalogue.templates.contentful.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import catalogue.templates.contentful.Intents;
import catalogue.templates.contentful.R;
import catalogue.templates.contentful.dto.Brand;
import catalogue.templates.contentful.dto.Product;
import catalogue.templates.contentful.lib.ZoomOutPageTransformer;
import com.squareup.picasso.Picasso;
import java.util.List;

/** Single product activity. */
public class ProductActivity extends AbsActivity {
  // Views
  @InjectView(R.id.view_pager) ViewPager imagesPager;
  @InjectView(R.id.thumbnails) ViewGroup thumbnails;
  @InjectView(R.id.product_name) TextView productNameView;
  @InjectView(R.id.company_name) TextView companyNameView;
  @InjectView(R.id.price) TextView priceView;
  @InjectView(R.id.quantity) TextView quantityView;
  @InjectView(R.id.description) TextView descriptionView;

  private Product product;
  private ImagePagerAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product);
    ButterKnife.inject(this);
    product = getIntent().getParcelableExtra(Intents.EXTRA_PRODUCT);

    initAdapter();
    initImages();
    setProductText();
    setBrandText();
    setPriceText();
    setQuantityText();
    setDescriptionText();
  }

  /** Initializes the adapter. */
  private void initAdapter() {
    adapter = new ImagePagerAdapter();
    adapter.setUrls(product.images());
  }

  /** Adds the product images as thumbnail view to the {@link #imagesPager} view pager. */
  private void initImages() {
    imagesPager.setAdapter(adapter);
    imagesPager.setPageTransformer(true, new ZoomOutPageTransformer());
    int size = getResources().getDimensionPixelSize(R.dimen.product_thumbnails_image_size);

    List<String> images = product.images();
    if (images != null) {
      for (int i = 0; i < images.size(); i++) {
        String url = images.get(i);
        generateThumbnail(url, i, size);
      }
    }
  }

  /**
   * Generates a thumbnail view for the given {@code url}.
   *
   * @param url image url
   * @param position position of thumbnail
   * @param size size
   */
  private void generateThumbnail(String url, final int position, int size) {
    ImageView imageView = new ImageView(this);
    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(size, size));
    imageView.setBackgroundResource(R.drawable.thumbnail_bg);
    Picasso.with(this).load(url).fit().centerInside().into(imageView);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        imagesPager.setCurrentItem(position, true);
      }
    });
    thumbnails.addView(imageView);
  }

  private void setDescriptionText() {
    String description = product.description();
    if (description == null) {
      descriptionView.setVisibility(View.GONE);
    } else {
      descriptionView.setText(description);
    }
  }

  private void setQuantityText() {
    Integer quantity = product.quantity();
    if (quantity != null) {
      quantityView.setText(getString(R.string.product_quantity, quantity));
    }
  }

  private void setProductText() {
    productNameView.setText(product.name());
  }

  private void setPriceText() {
    Double price = product.price();
    if (price == null) {
      priceView.setVisibility(View.GONE);
    } else {
      String priceStr = getString(R.string.product_price, String.format("%1$,.2f", price));
      priceView.setText(priceStr);
    }
  }

  private void setBrandText() {
    String companyName = null;
    Brand brand = product.brand();
    if (brand != null) {
      companyName = brand.companyName();
    }

    if (companyName == null) {
      companyNameView.setVisibility(View.GONE);
    } else {
      String link = brand.website();
      if (link == null) {
        link = "#";
      }

      companyNameView.setText(Html.fromHtml(
          getString(R.string.product_company_name, companyName, link)));

      companyNameView.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }

  /** Simple PagerAdapter to display a list of thumbnails. */
  static class ImagePagerAdapter extends PagerAdapter {
    private List<String> urls;

    @Override public int getCount() {
      if (urls == null) {
        return 0;
      }

      return urls.size();
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
      Context context = container.getContext();
      ImageView imageView = new ImageView(context);
      imageView.setBackgroundResource(android.R.color.white);
      Picasso.with(context).load(urls.get(position)).fit().centerInside().into(imageView);
      container.addView(imageView);
      return imageView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }

    @Override public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    public void setUrls(List<String> urls) {
      this.urls = urls;
    }
  }
}
