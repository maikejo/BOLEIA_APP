package ao.app.boleia.motorista.service;

/**
 * Created by maike.silva on 24/03/2018.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by roberto on 9/29/16.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;


import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.activity.ViagemActivity;

public class MyLocationService extends Service {

    private Handler h;
    private Runnable r;

    int counter = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification() {
        counter++;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ViagemActivity.class), 0);

        String info = counter + "";
        return new NotificationCompat.Builder(this)
                .setContentTitle(info)
                .setTicker(info)
                .setContentText(info)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().contains("start")) {
            h = new Handler();
            r = new Runnable() {
                @Override
                public void run() {
                    startForeground(101, updateNotification());
                    h.postDelayed(this, 1000);
                }
            };

            h.post(r);
        } else {
            h.removeCallbacks(r);
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }
}
