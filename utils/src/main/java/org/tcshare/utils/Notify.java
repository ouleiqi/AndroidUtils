package org.tcshare.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by FallRain on 2017/8/22.
 */

public class Notify {
    private static int MessageID = 0;

    public static void notifcation(Context context, CharSequence notificationTitle, CharSequence messageString){
        notifcation(context, notificationTitle, messageString, null, -1);
    }

    public static void notifcation(Context context, CharSequence notificationTitle, CharSequence messageString, Intent intent, int smallIconResID) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

        long when = System.currentTimeMillis();
        String ticker = notificationTitle + " " + messageString;

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentIntent(pendingIntent)
                .setContentText(messageString)
                .setTicker(ticker)
                .setWhen(when);
        if(smallIconResID != -1){
            notificationCompat.setSmallIcon(smallIconResID);
        }

        Notification notification = notificationCompat.build();
        //display the notification
        mNotificationManager.notify(MessageID, notification);
        MessageID++;

    }
}
