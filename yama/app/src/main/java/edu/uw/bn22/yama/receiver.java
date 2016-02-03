package edu.uw.bn22.yama;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by bruceng on 2/3/16.
 */
public class receiver extends BroadcastReceiver {
    private static final int CLICK_NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle inbox = intent.getExtras();
        if (inbox != null) {
            SmsMessage[] messagesArray = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String address = "";
            String body = "";
            String result = "";
            for (SmsMessage message : messagesArray) {
                if (message == null) {
                    Log.v("Receiver", "Empty");
                    break;
                } else {
                    address = message.getDisplayOriginatingAddress();
                    body = message.getDisplayMessageBody();

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setContentTitle(address)
                                    .setContentText(body);

                    Intent resultIntent = new Intent(context, reading.class);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(CLICK_NOTIFICATION_ID, mBuilder.build());
                }
            }
        }
    }
}
