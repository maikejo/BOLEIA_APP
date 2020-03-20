package ao.app.boleia.motorista.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.activity.DriverHome;
import ao.app.boleia.motorista.activity.ViagemActivity;

/**
 * Created by maike.silva on 28/12/2017.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

      /*  LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);

        Intent intent = new Intent(getBaseContext(), ViagemActivity.class);
        intent.putExtra("lat",customer_location.latitude);
        intent.putExtra("lng",customer_location.longitude);
        intent.putExtra("customer",remoteMessage.getNotification().getTitle());
        intent.putExtra("key",remoteMessage.getNotification().getTag());
        intent.putExtra("init", "N");
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // startActivity(intent);
      */

      showArrivedNotification(remoteMessage.getNotification().getBody());

    }

    private void showArrivedNotification(String body) {
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(android.app.Notification.DEFAULT_LIGHTS| android.app.Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_van_01)
                .setContentTitle("ENVIO PELO CONSULTOR")
                .setContentText(body)
                .setContentIntent(contentIntent);

        NotificationManager manager =(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());
    }
}
