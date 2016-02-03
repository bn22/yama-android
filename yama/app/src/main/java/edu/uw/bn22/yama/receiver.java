package edu.uw.bn22.yama;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by bruceng on 2/3/16.
 */
public class receiver extends BroadcastReceiver {
    private static final int CLICK_NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle inbox = intent.getExtras();
        if (inbox != null) {
            //Grabs the SMS messages
            SmsMessage[] messagesArray = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String address = "";
            String body = "";
            for (SmsMessage message : messagesArray) {
                if (message == null) { //Checks to see if the message is null
                    Log.v("Receiver", "Empty");
                    break;
                } else {
                    //Gets the sender and body
                    address = message.getDisplayOriginatingAddress();
                    body = message.getDisplayMessageBody();

                    //Enables Notifications when new messages arrive
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.circle)
                                    .setContentTitle(address)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                    Intent resultIntent = new Intent(context, reading.class);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(CLICK_NOTIFICATION_ID, mBuilder.build());

                    //Looks at the user preference settings. If auto-reply is enabled, then a SMS will be sent
                    SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(context);
                    String auto = options.getString("reply", null);
                    boolean reply = options.getBoolean("switch", false);
                    if (reply) {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(address, null, auto, null, null);
                        Toast.makeText(context, "Auto-Reply Sent", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
