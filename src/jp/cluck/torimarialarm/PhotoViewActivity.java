package jp.cluck.torimarialarm;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class PhotoViewActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_photoview);
    
    mPhotoId = getIntent().getIntExtra("photoid", -1);
    Log.i("Received photo id " + mPhotoId);
    if (mPhotoId == -1) finish();
    
    updateView();
  }

  private void updateView() {
    final int id = PhotoData.PICTURES[mPhotoId];
    Drawable picture = getResources().getDrawable(id);
    findViewById(R.id.photo_view_main_layout).setBackgroundDrawable(picture);
  }

  public void onNextButtonClick(View view) {
    ++mPhotoId;
    if (mPhotoId >= PhotoData.PICTURES.length) {
      mPhotoId = 0;
    }
    updateView();
  }

  public void onPrevButtonClick(View view) {
    --mPhotoId;
    if (mPhotoId < 0) {
      mPhotoId = PhotoData.PICTURES.length - 1;
    }
    updateView();
  }

  public void onBackButtonClick(View view) {
    finish();
  }

  private int mPhotoId;
}
