package jp.cluck.torimarialarm;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
  private int mHour = 0;
  private int mMinute = 0;
  private TimePicker mPicker = null;

  public TimePreference setHourOfDay(int hourOfDay) {
    mHour = hourOfDay;
    return this;
  }

  public TimePreference setMinute(int minute) {
    mMinute = minute;
    return this;
  }

  public int getHourOfDay() {
    return getHour(getSummary().toString());
  }

  public int getMinute() {
    return getMinute(getSummary().toString());
  }

  public static int getHour(String time) {
    String[] pieces = time.split(":");
    return Integer.parseInt(pieces[0]);
  }

  public static int getMinute(String time) {
    String[] pieces = time.split(":");
    return Integer.parseInt(pieces[1]);
  }

  public TimePreference(Context context) {
    this(context, null);
  }

  public TimePreference(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TimePreference(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    Resources res = context.getResources();
    setPositiveButtonText(res.getString(R.string.time_picker_set));
    setNegativeButtonText(res.getString(R.string.time_picker_cancel));
  }

  @Override
  protected View onCreateDialogView() {
    mPicker = new TimePicker(getContext());
    // mPicker.setIs24HourView(true);
    return mPicker;
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);
    mPicker.setCurrentHour(mHour);
    mPicker.setCurrentMinute(mMinute);
  }

  @Override
  protected void onDialogClosed(boolean positiveResult) {
    super.onDialogClosed(positiveResult);

    if (positiveResult) {
      mHour = mPicker.getCurrentHour();
      mMinute = mPicker.getCurrentMinute();

      final String time = Util.getSimpeTimeString(mHour, mMinute);
      setSummary(getSummary());
      if (callChangeListener(time)) {
        persistString(time);
      }
    }
  }

  @Override
  public CharSequence getSummary() {
    return Util.getSimpeTimeString(mHour, mMinute);
  }
}