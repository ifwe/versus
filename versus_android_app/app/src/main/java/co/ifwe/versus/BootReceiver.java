package co.ifwe.versus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.ifwe.versus.services.SubscribeService;

/**
 * Created by schoi on 3/18/16.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.i(TAG, "PubNub BootReceiver Starting");
        Intent intent = new Intent(context, SubscribeService.class);
        context.startService(intent);
        Log.i(TAG, "PubNub BootReceiver Started");
    }
}
