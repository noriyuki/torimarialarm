package jp.cluck.torimarialarm;

public class Log {
  private static final String TAG = "TorimariAlarm";

  public static void i(final String s) {
    android.util.Log.i(TAG, s);
  }

  public static void w(final String s) {
    android.util.Log.w(TAG, s);
  }

  public static void e(final String s) {
    android.util.Log.e(TAG, s);
  }
}
