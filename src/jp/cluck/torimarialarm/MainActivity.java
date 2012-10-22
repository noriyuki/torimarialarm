package jp.cluck.torimarialarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
  public static final int START_MANAGE_ACTIVITY = 0;
  public static final int START_PHOTO_GALLERY_ACTIVITY = 1;
  public static final String FB_URL = "http://www.facebook.com/mariko.toribe";

  @Override 
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Initialize timer to update clock every minute.
    mHandler = new Handler();
    mTimer = new Timer(true);
    mTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              TextView v = (TextView) findViewById(R.id.clockView);
              v.setText(Util.getSimpleTimeString());
            }
          });
        }
      }, 0, 5 * 1000);  // Update clock every 5 seconds

    // Initialize touch sound.
    mVoiceData = VoiceData.getInstance(this);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN)
      mVoiceData.playRandomly();
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }
  
  public void onClickStartManageButton(View view) {
    Intent intent = new Intent(this, ManageActivity.class);
    startActivityForResult(intent, START_MANAGE_ACTIVITY);
  }

  public void onClickStartPhotoGalleryButton(View view) {
    Intent intent = new Intent(this, PhotoGalleryActivity.class);
    startActivityForResult(intent, START_PHOTO_GALLERY_ACTIVITY);
  }

  public void onClickFacebookButton(View view) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(FB_URL));
    startActivity(intent);
  }

  private Timer mTimer;
  private Handler mHandler;
  private VoiceData mVoiceData = null;
}
