package com.beaker.reciperoulette;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.beaker.reciperoulette.requests.IngredientRequestSelfView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private int notifs = 0;

    public static void saveFCMTokentoSharedPref(Context c) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    assert token != null;

                    //Writing token and credentials to settings file
                    SharedPreferences sharedPref =
                            c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(c.getString(R.string.prf_fcmtoken), token);
                    editor.apply();

                    // Log and toast
                    String msg = c.getString(R.string.fcm_msg_ok);
                    Log.d(TAG, msg);
                    Log.d(TAG, token);
                    Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: \n");
        Log.d(TAG, token);

        //Writing token and credentials to settings file
        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.prf_fcmtoken), token);
        editor.apply();
        Log.d(TAG, "FCM TOKEN: " + token);
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String title = remoteMessage.getData().get(getString(R.string.fcm_msg_title));
            String body = remoteMessage.getData().get(getString(R.string.fcm_msg_text));
            createNotification(title, body);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            createNotification(title, body);
        }

        super.onMessageReceived(remoteMessage);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }


    private void createNotification(String title, String body)
    {
        String notifTitle = title == null ? "" : title;
        String notifBody = body == null ? "" : body;

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "FCM_NOTIF");
        Intent ii = new Intent(getApplicationContext(), IngredientRequestSelfView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_IMMUTABLE);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(notifTitle);
        mBuilder.setContentText(notifBody);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setChannelId(getString(R.string.not_ch));

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.createNotificationChannel(new NotificationChannel(getString(R.string.not_ch), getString(R.string.not_ch_desc), NotificationManager.IMPORTANCE_HIGH));

        mNotificationManager.notify(notifs++, mBuilder.build());

    }
}