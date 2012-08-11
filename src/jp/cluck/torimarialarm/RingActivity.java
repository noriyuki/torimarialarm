package jp.cluck.torimarialarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class RingActivity extends Activity {
  private final static int MAX_SNOOZE = 3;
  private final static int DURATION = 60 * 1000;
  private final static int SNOOZE_PERIOD = 60 * 1000;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    final int alarmId = intent.getIntExtra("alarmId", -1);
    if (alarmId == -1) {
      Log.e("Recevied invalid alarm ID.");
      finish();
    }
    mAlarmStorage = AlarmStorage.getInstance(getApplicationContext());
    mAlarm = mAlarmStorage.getAlarmById(getApplicationContext(), alarmId);
    if (mAlarm == null) {
      Log.e("No alarm with ID = " + alarmId);
      finish();
    }
    mSnoozeTimes = intent.getIntExtra("snooze", 0);
    mAlarm.edit().setTurnedOn(false).commit();

    Util.showNotification(getApplicationContext(), alarmId, mAlarm.getTitle());

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                         WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                         WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                         WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    setContentView(R.layout.activity_ring);
    final int backgroundImageId = mAlarm.getImageResouceId();
    Drawable backgroundImage = getResources().getDrawable(backgroundImageId);
    findViewById(R.id.alarm_dialog_main_layout).setBackgroundDrawable(backgroundImage);
 
    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                               PowerManager.FULL_WAKE_LOCK, "TorimariAlarm");
    mWakeLock.acquire();
 
    mMediaPlayer = MediaPlayer.create(this, mAlarm.getSoundResourceId());
    mMediaPlayer.setLooping(true);

    mMediaPlayer.start();
    if (mAlarm.isVibrationOn()) {
      mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      final long[] pattern = {1500, 1000}; // OFF/ON/OFF/ON...
      mVibrator.vibrate(pattern, 0);
    }

    mOnTimeout = new Runnable() {
      @Override
      public void run() {
        setSnooze();
        finish();
      }
    };
    mHandler = new Handler();
    mHandler.postDelayed(mOnTimeout, DURATION);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mMediaPlayer != null) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.stop();
      }
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    if (mVibrator != null) {
      mVibrator.cancel();
      mVibrator = null;
    }
    mAlarm = null;
    mWakeLock.release();
  }

  private void setSnooze() {
    final int next = mSnoozeTimes + 1;
    if (next < MAX_SNOOZE) {
      Context context = getApplicationContext();
      final long alarmTime = Util.setAlarm(context, mAlarm.getId(), next, SNOOZE_PERIOD);
      Util.showAlarmConfirmation(context, alarmTime, next);
      Log.i("Set snooze for " + mAlarm.toString());
    } else {
      final String message = getResources().getString(R.string.snooze_quit_message);
      Util.showNotification(getApplicationContext(), mAlarm.getId(), message);
    }
  }

  public void onDismissButtonClick(View view) {
    mHandler.removeCallbacks(mOnTimeout);
    Util.clearNotification(getApplicationContext(), mAlarm.getId());
    Log.i("Dismissed alarm: " + mAlarm.toString());
    finish();
  }

  public void onSnoozeButtonClick(View view) {
    mHandler.removeCallbacks(mOnTimeout);
    Util.clearNotification(getApplicationContext(), mAlarm.getId());
    setSnooze();
    finish();
  }

  private AlarmStorage mAlarmStorage = null;
  private Alarm mAlarm = null;
  private Vibrator mVibrator = null;
  private MediaPlayer mMediaPlayer = null;
  private int mSnoozeTimes = -1;
  private Handler mHandler = null;
  private Runnable mOnTimeout = null;
  private WakeLock mWakeLock = null;
}
