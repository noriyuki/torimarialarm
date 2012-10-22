package jp.cluck.torimarialarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

class ImageAdapter extends BaseAdapter {
  private Context mContext;

  public ImageAdapter(Context c) {
    mContext = c;
  }

  public int getCount() {
    return PhotoData.THUMBS.length;
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  private static final float IMAGE_HEIGHT_DP = 150.0f;
  private static final float IMAGE_WIDTH_DP = 100.0f;

  // Creates a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    final float scale = mContext.getResources().getDisplayMetrics().density;

    ImageView imageView;
    if (convertView == null) {  // if it's not recycled, initialize some attributes
      imageView = new ImageView(mContext);
      final int width = (int) (IMAGE_WIDTH_DP * scale + 0.5f);
      final int height = (int) (IMAGE_HEIGHT_DP * scale + 0.5f);
      imageView.setLayoutParams(new GridView.LayoutParams(width, height));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setPadding(8, 8, 8, 8);
    } else {
      imageView = (ImageView) convertView;
    }

    imageView.setImageResource(PhotoData.THUMBS[position]);
    return imageView;
  }

}

public class PhotoGalleryActivity extends Activity {
  private static int START_PHOTO_VIEW_ACTIVITY = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);

    GridView gridview = (GridView) findViewById(R.id.photo_gallery_grid);
    gridview.setAdapter(new ImageAdapter(this));

    gridview.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), PhotoViewActivity.class);
        intent.putExtra("photoid", position);
        startActivityForResult(intent, START_PHOTO_VIEW_ACTIVITY);
      }
    });
  }
}
