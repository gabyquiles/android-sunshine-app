package com.gabyquiles.sunshine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by gabrielquiles-perez on 9/26/15.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private final String LOG_TAG = BroadcastReceiver.class.getSimpleName();

    private static final String EXTRA_SENDER = "from";
    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            // Ignore any other message types.
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if(MainActivity.PROJECT_NUMBER.equals(extras.getString(EXTRA_SENDER))) {
                    String weather = extras.getString(EXTRA_WEATHER);
                    String location = extras.getString(EXTRA_LOCATION);
                    String notification = "Heads Up: " + weather + " in " + location + "!";
                    sendNotification(context, notification);
                }
                Log.i(LOG_TAG, "Message Received");
            }
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with a GCM message.
    private void sendNotification(Context context, String msg) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.art_storm)
                        .setContentTitle("Weather Alert!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}


