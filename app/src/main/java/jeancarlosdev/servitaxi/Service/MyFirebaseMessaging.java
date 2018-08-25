package jeancarlosdev.servitaxi.Service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        /*
        * El siguiente Toast esta fuera del hilo principal, si queremos correr el Toast
        * Necesitamos crear un hilo.
        * */
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(
                        MyFirebaseMessaging.this,
                        remoteMessage.getNotification().getBody(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
