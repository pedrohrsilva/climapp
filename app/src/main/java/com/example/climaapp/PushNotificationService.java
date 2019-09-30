package com.example.climaapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inlocomedia.android.engagement.InLocoEngagement;
import com.inlocomedia.android.engagement.PushMessage;
import com.inlocomedia.android.engagement.request.FirebasePushProvider;
import com.inlocomedia.android.engagement.request.PushProvider;

import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {

    public PushNotificationService() {
    }

    public void onNewToken(String token) {
        Log.d("FIREBASE_TEST", "Refreshed token: " + token);

        final PushProvider pushProvider = new FirebasePushProvider.Builder()
                .setFirebaseToken(token)
                .build();
        InLocoEngagement.setPushProvider(this, pushProvider);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Getting the Notification Data
        final Map<String, String> data = remoteMessage.getData();

        if (data != null) {
            // Decoding the notification data HashMap
            final PushMessage pushContent = InLocoEngagement.decodeReceivedMessage(this, data);

            if (pushContent != null) {
                // Presenting the notification
                InLocoEngagement.presentNotification(
                        this, // Context
                        pushContent,  // The notification message hash
                        R.drawable.ic_notification, // The notification icon drawable resource to display on the status bar. Put your own icon here. You can also use R.drawable.ic_notification for testing.
                        1111111  // Optional: The notification identifier
                );
            } else {
                // It's your regular message. Do as you used to do.
            }
        }
    }






}
