package jp.cluck.torimarialarm;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class AlarmStorage {
  private final static int CAPACITY = 5;

  public static synchronized AlarmStorage getInstance(Context context) {
    if (sAlarmStorage == null) {
      sAlarmStorage = new AlarmStorage();
      sAlarmStorage.init(context);
    }
    return sAlarmStorage;
  }

  public AlarmStorage() {}

  private void init(Context context) {
    if (mAlarmIds != null) return;
    mAlarmIds = new ArrayList<Integer>();
    for (int id = 0; id < CAPACITY; ++id) {
      final String key = idToStringKey(id);
      SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
      if (prefs.contains(key)) {
        mAlarmIds.add(id);
      }
    }
  }
  public int size() {
    return mAlarmIds.size();
  }

  public Alarm getAlarmByIndex(Context context, int alarmIndex) {
    if (alarmIndex < 0 || alarmIndex >= mAlarmIds.size()) return null;
    final int alarmId = mAlarmIds.get(alarmIndex);
    return getAlarmById(context, alarmId);
  }

  private String idToStringKey(int alarmId) {
    return "alarm" + alarmId;
  }

  public Alarm getAlarmById(Context context, int alarmId) {
    final String key = idToStringKey(alarmId);
    SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    if (prefs.contains(key)) {
      return new Alarm(context, prefs, this, alarmId);
    }
    return null;
  }

  public synchronized Alarm newAlarm(Context context) {
    if (isFull()) {
      Log.e("Alarm list is full.");
      return null;
    }
    boolean[] used = new boolean[CAPACITY];
    for (int i = 0; i < mAlarmIds.size(); ++i) {
      used[mAlarmIds.get(i)] = true;
    }
    for (int id = 0; id < used.length; ++id) {
      if (!used[id]) {
        final String key = idToStringKey(id);
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        prefs.edit().putString(key, "").commit();
        mAlarmIds.add(id);
        return new Alarm(context, prefs, this, id);
      }
    }
    return null;
  }

  public boolean isFull() {
    return mAlarmIds.size() >= CAPACITY;
  }

  public void removeId(int id) {
    for (int i = 0; i < mAlarmIds.size(); ++i) {
      if (mAlarmIds.get(i) == id) {
        mAlarmIds.remove(i);
        return;
      }
    }
  }

  @Override
  public String toString() {
    String s = new String();
    for (int i = 0; i < mAlarmIds.size(); ++i) {
      s += "index " + i + " = " + mAlarmIds.get(i);
    }
    return s;
  }

  private ArrayList<Integer> mAlarmIds = null;
  private static AlarmStorage sAlarmStorage = null;
}
