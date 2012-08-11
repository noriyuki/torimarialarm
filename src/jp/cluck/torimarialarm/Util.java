package jp.cluck.torimarialarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Util {
  public static long toMillsecs(final long hourOfDay, final long minute, final long second) {
    return ((hourOfDay * 60 + minute) * 60 + second) * 1000;
  }

  public static long calculateMillsecsToTimeFromNow(final long hour, final long minute) {
    Calendar now = Calendar.getInstance();
    final long currentElapsed = Util.toMillsecs(now.get(Calendar.HOUR_OF_DAY),
                                                now.get(Calendar.MINUTE),
                                                now.get(Calendar.SECOND));
    final long targetElapsed = Util.toMillsecs(hour, minute, 0);
    long mills = targetElapsed - currentElapsed;
    if (mills < 0) {
      mills += Util.toMillsecs(24, 0, 0);
    }
    return mills;
  }
 
  public static String getSimpeTimeString(final int hourOfDay, final int minute) {
    String s = new String();
    if (hourOfDay < 10) s += "0";
    s += hourOfDay + ":";
    if (minute < 10) s += "0";
    s += minute;
    return s;
  }

  public static String getSimpleTimeString() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    return sdf.format(Calendar.getInstance().getTime());
  }

  public static void showNotification(Context context, final int id, CharSequence message) {
    NotificationManager nm =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    final String from = context.getResources().getString(R.string.toribe_mariko);
    Intent intent = new Intent(context, ManageActivity.class);
    intent.putExtra("notificationId", id);
    PendingIntent contentIntent = PendingIntent.getActivity(
        context, id, intent,
        Intent.FLAG_ACTIVITY_NEW_TASK | PendingIntent.FLAG_UPDATE_CURRENT);
    Notification noti = new Notification(R.drawable.ic_launcher,
                                         message, System.currentTimeMillis());
    noti.setLatestEventInfo(context, from, message, contentIntent);
    nm.notify(id, noti);
  }

  public static void clearNotification(Context context, final int id) {
    if (id < 0) return;
    NotificationManager nm =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancel(id);   
  }

  public static long setAlarm(Context context, int alarmId,
                              final int nth_snooze, final long millsLater) {
    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.putExtra("alarmId", alarmId);
    intent.putExtra("snooze", nth_snooze);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent,
                                                             PendingIntent.FLAG_UPDATE_CURRENT);

    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    final long alarmTime = System.currentTimeMillis() + millsLater;
    am.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    Log.i("Set alarm: " + alarmId + " for " + nth_snooze + "-th snooze");
    return alarmTime;
  }

  public static void showAlarmConfirmation(Context context, long alarmTime, int nth_snooze) {
    final long delta = alarmTime - System.currentTimeMillis();
    final long hours = delta / (60 * 60 * 1000);
    long minutes = (delta - hours * 60 * 60 * 1000) / (60 * 1000);
    if (minutes < 59) minutes += 1;
    String message = (nth_snooze == 0) ? "Alarm set for " : "Snooze set for ";
    if (hours > 0) {
      message += hours + " hours and ";
    }
    message += minutes + " minutes from now";
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public static void cancelAlarm(Context context, int alarmId) {
    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.putExtra("alarmId", alarmId);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent,
                                                             PendingIntent.FLAG_UPDATE_CURRENT);
    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.cancel(pendingIntent);
    Log.i("Canceled alarm: " + alarmId);
  }
}
