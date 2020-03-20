package ao.app.boleia.motorista.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by maike.silva on 10/01/2018.
 */

public class BootUpReceiver extends BroadcastReceiver {

    private final static String BATTERY_LEVEL = "level";

    @Override
    public void onReceive(Context context, Intent i) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
