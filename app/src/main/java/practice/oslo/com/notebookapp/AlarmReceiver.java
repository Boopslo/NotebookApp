package practice.oslo.com.notebookapp;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        /*
        String title = intent.getStringExtra("title");
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        */
        // (**) try to read the id then send the notification
        long id = intent.getIntExtra("id", 0);
        if(id != 0){
            sendNotice(context, id);
        }

    }

    // method to send notification
    // cause the lowest API level will be 15
    // use only normal notification
    private void sendNotice(Context context, long noticeId){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        ItemDB itemDB = new ItemDB(context.getApplicationContext());
        Item item = itemDB.getItem(noticeId);

        // set the notice builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.star_big_on).setWhen(System.currentTimeMillis())
                .setContentTitle(context.getString(R.string.app_name)).setContentText(item.getTitle());

        notificationManager.notify((int)item.getId(), builder.build());

    }



}
