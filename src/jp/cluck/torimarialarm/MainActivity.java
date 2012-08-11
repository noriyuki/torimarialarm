package jp.cluck.torimarialarm;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
  public static final int START_MANAGE_ACTIVITY = 0;

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
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      Log.i("TODO: handle touch event to play sound");
    }
    return true;
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

  private Timer mTimer;
  private Handler mHandler;
}
