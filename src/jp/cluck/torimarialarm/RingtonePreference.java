package jp.cluck.torimarialarm;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class RingtonePreference extends ListPreference {
  public RingtonePreference(Context context) {
    this(context, null);
  }

  public RingtonePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    String[] ids = context.getResources().getStringArray(R.array.ringtoneId);
    mDefaultValue = ids[0];
  }

  @Override
  public CharSequence getSummary() {
    return getEntry();
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    notifyChanged();
  }

  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return mDefaultValue;
  }

  private String mDefaultValue = null;
}
