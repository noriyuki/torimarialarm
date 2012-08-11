package jp.cluck.torimarialarm;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

class AlarmRow extends CheckBoxPreference {
  public AlarmRow(ManageActivity activity, Alarm alarm) {
    super(activity);
    mActivity = activity;
    mAlarm = alarm;
    setChecked(alarm.isTurnedOn());
    setTitle(Util.getSimpeTimeString(alarm.getHourOfDay(), alarm.getMinute()));
    setSummary(alarm.getTitle());
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);
      
    TextView checkbox = (TextView) view.findViewById(android.R.id.checkbox);
    if (checkbox == null) {
      return;
    }
    checkbox.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        CheckBox cb = (CheckBox) view.findViewById(android.R.id.checkbox);
        if (cb.isChecked()) {
          final long alarmTime = mAlarm.set();
          Util.showAlarmConfirmation(mActivity, alarmTime, 0);
        } else {
          mAlarm.cancel();
        }
      }
    });
  }

  @Override
  protected void onClick() {
    mActivity.startEditActivity(mAlarm.getId());
  }

  private ManageActivity mActivity = null;
  private Alarm mAlarm = null;
}

public class ManageActivity extends PreferenceActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.manage_preferences);
    setContentView(R.layout.activity_manage);
    mAlarmStorage = AlarmStorage.getInstance(getApplicationContext());
  }

  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateView();

    Intent intent = getIntent();
    final int notificationId = intent.getIntExtra("notificationId", -1);
    if (notificationId != -1) {
      Util.clearNotification(getApplicationContext(), notificationId);
      intent.removeExtra("notificationId");
    }
  }

  private void updateView() {
    PreferenceCategory category = (PreferenceCategory) findPreference("alarm_list");
    category.removeAll();
    for (int i = 0; i < mAlarmStorage.size(); ++i) {
      Alarm alarm = mAlarmStorage.getAlarmByIndex(getApplicationContext(), i);
      if (alarm == null) continue;
      AlarmRow pref = new AlarmRow(this, alarm);
      category.addPreference(pref);
    }
    Button button = (Button) findViewById(R.id.activity_manage2_add_button);
    button.setEnabled(!mAlarmStorage.isFull());
  }

  public void onClickAdd(View view) {
    if (mAlarmStorage.isFull()) {
      Log.e("Alarm list is full");
      return;
    }
    startEditActivity(-1);
  }

  public void onClickBack(View view) {
    finish();
  }
  
  public void startEditActivity(int alarmId) {
    Intent intent = new Intent(this, EditActivity.class);
    if (alarmId >= 0) {
      intent.putExtra("alarmId", alarmId);
    }
    startActivityForResult(intent, 0);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (intent == null) return;
    final long alarmTime = intent.getLongExtra("alarmTime", -1);
    if (alarmTime > 0) {
      Util.showAlarmConfirmation(this, alarmTime, 0);
    }
  }

  private AlarmStorage mAlarmStorage = null;
}
