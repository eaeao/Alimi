package com.tktv.alimi.Settings;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tktv.alimi.LoginActivity;
import com.tktv.alimi.MainActivity;
import com.tktv.alimi.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "AlimiService";

    Settings settings;

    @Override
    protected void onHandleIntent(Intent intent) {
        settings = (Settings) getApplicationContext();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //setNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //setNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                String mysid = settings.getPref("shop_id");
                Log.i("shop_id",""+mysid);
                Log.i("GCM-shop_id",""+extras.getString("shop_id").toString());
                Log.i("GCM-name",""+extras.getString("name").toString());

                try{
                    if (mysid.equals(extras.getString("shop_id").toString())) {
                        setNotification(extras.getString("name").toString());
                    }
                    Log.i(TAG, "Received: " + extras.getString("name").toString());
                }catch (Exception e){}
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @SuppressWarnings("deprecation")
    private void setNotification(String msg){
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification noti = new Notification(R.drawable.ic_launcher,"새로운 메세지가 도착했습니다.",System.currentTimeMillis());
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", msg);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
        noti.icon = R.drawable.ic_launcher;
        noti.setLatestEventInfo(this, "새로운 메세지가 도착했습니다.", msg, pending);
        noti.defaults |= Notification.DEFAULT_ALL;
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notiMgr.notify(0, noti);
    }
}
