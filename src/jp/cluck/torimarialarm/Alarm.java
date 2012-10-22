package jp.cluck.torimarialarm;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

class AlarmResource {
  private static final int[][] RESOURCE_ID_TABLE = {
    { R.raw.ohayo, R.drawable.ohayo },
    { R.raw.ohayogozaimasu, R.drawable.ohayogozaimasu },
    { R.raw.okite, R.drawable.okite },
    { R.raw.asadesuyo, R.drawable.asadesuyo },
    { R.raw.okinaitochikokushichauyo, R.drawable.okinaitochikokusityauyo },
    { R.raw.gutentag, R.drawable.gutentag },
    { R.raw.kyoumoisshoniganbaroune, R.drawable.kyoumoissyoniganbaroune },
    { R.raw.otsukaresamadeshita, R.drawable.otsukaresamadesita },
    { R.raw.oyasuminasai, R.drawable.oyasuminasai },
  };

  public static int getSoundId(int id) {
    return RESOURCE_ID_TABLE[id][0];
  }

  public static int getImageId(int id) {
    return RESOURCE_ID_TABLE[id][1];
  }
}

public class Alarm {
  private static final String KEY_TURNED_ON = "t";
  private static final String KEY_HOUR_OF_DAY = "h";
  private static final String KEY_MINUTE = "m";
  private static final String KEY_RINGTONE = "r";
  private static final String KEY_VIBRATION_ON = "v";

  public class Editor {
    Editor(SharedPreferences.Editor editor) {
      mEditor = editor;
    }

    public Editor setTurnedOn(boolean turnedOn) {
      mEditor.putBoolean(KEY_TURNED_ON, turnedOn);
      return this;
    }

    public Editor setTime(int hourOfDay, int minute) {
      mEditor.putInt(KEY_HOUR_OF_DAY, hourOfDay)
             .putInt(KEY_MINUTE, minute);
      return this;
    }

    public Editor setRingtoneId(int id) {
      mEditor.putInt(KEY_RINGTONE, id);
      return this;
    }

    public Editor setVibrationOn(boolean vibrationOn) {
      mEditor.putBoolean(KEY_VIBRATION_ON, vibrationOn);
      return this;
    }

    public boolean commit() {
      return mEditor.commit();
    }

    private SharedPreferences.Editor mEditor = null;
  }

  public Alarm(Context context, SharedPreferences prefs, AlarmStorage storage, int id) {
    mContext = context;
    mPrefs = prefs;
    mAlarmStorage = storage;
    mId = id;
  }

  public int getId() {
    return mId;
  }

  public boolean isTurnedOn() {
    return mPrefs.getBoolean(KEY_TURNED_ON, true);
  }

  public int getHourOfDay() {
    Calendar calendar = Calendar.getInstance();
    return mPrefs.getInt(KEY_HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
  }

  public int getMinute() {
    Calendar calendar = Calendar.getInstance();
    return mPrefs.getInt(KEY_MINUTE, calendar.get(Calendar.MINUTE));
  }

  public boolean isVibrationOn() {
    return mPrefs.getBoolean(KEY_VIBRATION_ON, true);
  }

  public int getRingtoneId() {
    return mPrefs.getInt(KEY_RINGTONE, 0);
  }

  public String getTitle() {
    String[] titles = mContext.getResources().getStringArray(R.array.ringtoneTitle);
    return titles[getRingtoneId()];
  }

  int getImageResouceId() {
    return AlarmResource.getImageId(getRingtoneId());
  }

  int getSoundResourceId() {
    return AlarmResource.getSoundId(getRingtoneId());
  }

  public Editor edit() {
    return new Editor(mPrefs.edit());
  }

  public boolean delete() {
    cancel();
    mAlarmStorage.removeId(mId);
    return mPrefs.edit().clear().commit();
  }

  public long set() {
    if (!isTurnedOn()) {
      edit().setTurnedOn(true).commit();
    }
    final long millsLater = Util.calculateMillsecsToTimeFromNow(getHourOfDay(),
                                                                getMinute());
    return Util.setAlarm(mContext, mId, 0, millsLater);
  }

  public void cancel() {
    if (isTurnedOn()) {
      edit().setTurnedOn(false).commit();
    }
    Util.cancelAlarm(mContext, mId);
  }

  @Override
  public String toString() {
    return "Alarm(" + mId + ")";
  }

  private Context mContext = null;
  private SharedPreferences mPrefs = null;
  private AlarmStorage mAlarmStorage = null;
  private int mId = -1;
}
