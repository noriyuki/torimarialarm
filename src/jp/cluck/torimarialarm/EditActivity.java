package jp.cluck.torimarialarm;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class EditActivity extends PreferenceActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.edit_preferences);
    setContentView(R.layout.activity_edit);

    mAlarmStorage = AlarmStorage.getInstance(getApplicationContext());

    mTurnedOnPref = (CheckBoxPreference) findPreference("alarm_turned_on");
    mTimePref = (TimePreference) findPreference("alarm_time");
    mVibrationPref = (CheckBoxPreference) findPreference("alarm_vibrate");
    mRingtonePref = (RingtonePreference) findPreference("alarm_ring");

    Intent intent = getIntent();
    final int alarmId = intent.getIntExtra("alarmId", -1);
    if (alarmId == -1) {
      setupWithDefaultValues();
      Button button = (Button) findViewById(R.id.editor_delete_button);
      LinearLayout layout = (LinearLayout) button.getParent();
      layout.removeView(button);
    } else {
      setupFromAlarmId(alarmId);
    }
  }

  private void setupWithDefaultValues() {
    mAlarm = null;
    mTurnedOnPref.setChecked(true);
    Calendar calendar = Calendar.getInstance();
    mTimePref.setHourOfDay(calendar.get(Calendar.HOUR_OF_DAY))
             .setMinute(calendar.get(Calendar.MINUTE));
    mRingtonePref.setValue(String.valueOf(0));
    mVibrationPref.setChecked(true);
  }

  private void setupFromAlarmId(int alarmId) {
    mAlarm = mAlarmStorage.getAlarmById(getApplicationContext(), alarmId);
    mTurnedOnPref.setChecked(mAlarm.isTurnedOn());
    mTimePref.setHourOfDay(mAlarm.getHourOfDay())
             .setMinute(mAlarm.getMinute());
    mRingtonePref.setValue(String.valueOf(mAlarm.getRingtoneId()));
    mVibrationPref.setChecked(mAlarm.isVibrationOn());
  }

  public void onClickCancel(View view) {
    finish();
  }

  public void onClickDelete(View view) {
    if (!mAlarm.delete()) {
      Log.e("Failed to delete alarm " + mAlarm.toString());
    }
    finish();
  }

  public void onClickDone(View view) {
    if (mAlarm == null) {
      mAlarm = mAlarmStorage.newAlarm(getApplicationContext());
      if (mAlarm == null) {
        Log.e("Failed to create a new alarm");
        finish();
      }
    }
    final boolean res = mAlarm.edit()
        .setTurnedOn(mTurnedOnPref.isChecked())
        .setTime(mTimePref.getHourOfDay(), mTimePref.getMinute())
        .setRingtoneId(Integer.valueOf(mRingtonePref.getValue()))
        .setVibrationOn(mVibrationPref.isChecked())
        .commit();
    if (!res) {
      Log.e("Failed to commit alarm " + mAlarm.toString());
    } else if (mAlarm.isTurnedOn()) {
      final long alarmTime = mAlarm.set();
      Intent intent = new Intent();
      intent.putExtra("alarmTime", alarmTime);
      setResult(RESULT_OK, intent);
    } else {
      mAlarm.cancel();
    }
    finish();
  }

  private AlarmStorage mAlarmStorage = null;
  private Alarm mAlarm = null;
  private CheckBoxPreference mTurnedOnPref = null;
  private TimePreference mTimePref = null;
  private RingtonePreference mRingtonePref = null;
  private CheckBoxPreference mVibrationPref = null;
}
