package practice.oslo.com.notebookapp;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;


public class RebootReceiver extends BroadcastReceiver {
    /*
        because every time when we reboot the device, AlarmReceiver will expire
        so create this class the deal wtih that condition
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        // because we are storing data int the database, so use itemDatabase to catch data
        ItemDB itemDB = new ItemDB(context.getApplicationContext());
        // items read from list
        List<Item> list = itemDB.getAll();

        // read the current time
        long currentTime = Calendar.getInstance().getTimeInMillis();
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for(Item item: list){
            long alarm = item.getAlarmDateTime();
            // if didnt set alarm or the alarm already expired
            if(alarm == 0 || alarm <= currentTime){
                continue;
            }
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            // change the way, not receiving the title, but the id of the items
            // alarmIntent.putExtra("title", item.getTitle());
            intent.putExtra("id", item.getId());
            PendingIntent pi = PendingIntent.getBroadcast(context, (int)item.getId(), alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, item.getAlarmDateTime(), pi);
        }

    }
}
