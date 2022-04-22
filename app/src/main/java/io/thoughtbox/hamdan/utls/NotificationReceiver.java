package io.thoughtbox.hamdan.utls;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.legacy.content.WakefulBroadcastReceiver;

import io.thoughtbox.hamdan.R;

public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        playNotificationSound(context);
    }

    public void playNotificationSound(Context context){

        try {

            Uri notification = Uri.parse("android.resource://"+context.getPackageName()+"/"+ R.raw.notification);
            Ringtone r = RingtoneManager.getRingtone(context,notification);
            r.play();

        }catch (Exception e) {

            e.printStackTrace();
        }
    }
}
