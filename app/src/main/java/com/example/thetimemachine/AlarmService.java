package com.example.thetimemachine;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
import static com.example.thetimemachine.Application.TheTimeMachineApp.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.thetimemachine.UI.MainActivity;

public class AlarmService  extends Service {

   private Vibrator vibrator;

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      Log.i("THE_TIME_MACHINE", "Service Started");
      // Display Activity: Stop, Snooze and general data
      // TODO: Define activity Intent notificationIntent = new Intent(this, RingActivity.class);
      //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

      // Display notification: With Text, Icon and option to call activity
      String alarmTitle = String.format("Alarm:  %s", intent.getStringExtra("LABEL"));
      // request code and flags not added for demo purposes
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
      Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(alarmTitle)
            .setContentText("Ring Ring .. Ring Ring")
            .setSmallIcon(R.drawable.baseline_alarm_add_24)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .build();



      Log.i("THE_TIME_MACHINE", alarmTitle);
      // Display Notification
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
         startForeground( 1, notification, FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED);
      }
      else
         startForeground(1,notification);

      Log.i("THE_TIME_MACHINE", "Service Started.2");
      // mediaPlayer.start();
 /*
      if (Build.VERSION.SDK_INT >= 26) {
         ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
      } else {
         ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
      }


      Intent i = new Intent();
      i.setClass(this, MainActivity.class);
      i.setAction(Intent.ACTION_MAIN);
      i.addCategory(Intent.CATEGORY_LAUNCHER);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);
       */

      Log.i("THE_TIME_MACHINE", "Service Started.3");

      return START_STICKY;
   }
   @Override
   public void onCreate() {
      super.onCreate();

      //startForeground(1, null);
      //mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
      //mediaPlayer.setLooping(true);

      Log.i("THE_TIME_MACHINE", "Service Created");
      vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

   }

   @Override
   public void onDestroy() {
      super.onDestroy();

     // mediaPlayer.stop();
      vibrator.cancel();
   }
   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
}
