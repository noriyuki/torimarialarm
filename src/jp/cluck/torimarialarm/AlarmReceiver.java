package jp.cluck.torimarialarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    final int alarmId = intent.getIntExtra("alarmId", -1);
    if (alarmId == -1) {
      Log.e("Received no alarm ID or invalid one.");
      return;
    }
    Intent newIntent = new Intent(context, RingActivity.class);
    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    newIntent.putExtra("alarmId", alarmId);
    newIntent.putExtra("snooze", intent.getIntExtra("snooze", 0));
    context.startActivity(newIntent);
  }
}
