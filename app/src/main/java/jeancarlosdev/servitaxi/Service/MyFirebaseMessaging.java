package jeancarlosdev.servitaxi.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import jeancarlosdev.servitaxi.Modelos.Notification;
import jeancarlosdev.servitaxi.R;


public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification().getTitle().equals("Lo sentimos")){
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
        }else if(remoteMessage.getNotification().getTitle().equals("Llegada")){

            showArrivedNotification(remoteMessage.getNotification().getBody());
        }

    }

    private void showArrivedNotification(String body) {
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                                0, new Intent(), PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true).setDefaults(android.app.Notification.DEFAULT_LIGHTS| android.app.Notification.DEFAULT_SOUND)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Llegada")
                                    .setContentText(body)
                                    .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
