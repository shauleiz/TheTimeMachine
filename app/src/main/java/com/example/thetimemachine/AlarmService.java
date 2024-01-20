package com.example.thetimemachine;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
import static com.example.thetimemachine.Application.TheTimeMachineApp.CHANNEL_ID;
import static com.example.thetimemachine.Data.AlarmItem.K_HOUR;
import static com.example.thetimemachine.Data.AlarmItem.K_LABEL;
import static com.example.thetimemachine.Data.AlarmItem.K_MINUTE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.UI.StopSnoozeActivity;

public class AlarmService  extends Service {

   private Vibrator vibrator;
   private MediaPlayer mediaPlayer;
   private boolean selfKill;
   Handler handler;
   Runnable autoSnooze;

   public static final String K_TYPE = "TYPE";
   public static final String SNOOZE = "snooze";
   public static final String STOP = "stop";
   public static final String ALARM = "alarm";


   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      Log.i("THE_TIME_MACHINE", "Service Started");
      // Display Activity: Stop, Snooze and general data
      // TODO: Define activity Intent notificationIntent = new Intent(this, RingActivity.class);
      //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);



      // TODO: Add full screen activity
      // Create notification: With Text, Icon and Stop button
      Notification notification = CreateNotification(intent);
      if (notification == null) return START_NOT_STICKY;
      // Display Notification
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
         startForeground( 1, notification, FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED);
      }

      else
         startForeground(1,notification);

      Log.i("THE_TIME_MACHINE", "Service Started.2");


      // Start audio and vibration
      mediaPlayer.start();
      ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, 10));

      // TODO: If recurring alarm, schedule next alarm here


      // Start auto-snooze timer
      // After a short delay, an alarm is created and alarm.Snooze is called
      handler = new Handler(Looper.getMainLooper());
      int delayMillis = 20000; // TODO: Take this from setup (20 Sec)
      autoSnooze = new Runnable() {
         @Override
         public void run() {
            Log.i("THE_TIME_MACHINE", "autoSnooze(): "+ delayMillis/1000 + " Sec Delay");
            Bundle b = intent.getExtras();
            if (b==null) return;
            AlarmItem alarm = new AlarmItem(b);
            if (alarm.getSnoozeCounter() > 6) //TODO: Take this from setup (6 times)
            {
               alarm.setActive(false);
               Context context = getApplicationContext();
               Intent stopIntent = new Intent(context, AlarmService.class);
               stopIntent.putExtras(b);
               AlarmReceiver.stopping(context, stopIntent );
            }
            alarm.Exec();
            selfKill = true;
            stopSelf();
         }
      };
      // auto-snooze starts running after delayMillis milliseconds
      handler.postDelayed(autoSnooze, delayMillis);

      Log.i("THE_TIME_MACHINE", "Service Started.3");

      return START_STICKY;
   }
   @Override
   public void onCreate() {
      super.onCreate();

      // Create Media player - Plays Alarm
      // TODO: Select MP3 to play
      mediaPlayer = MediaPlayer.create(this, R.raw.rooster);
      mediaPlayer.setLooping(true);

      // Create vibrator
      vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      selfKill = false;

      Log.i("THE_TIME_MACHINE", "Service Created");
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      mediaPlayer.stop();
      mediaPlayer.reset();
      mediaPlayer.release();
      vibrator.cancel();
      Log.i("THE_TIME_MACHINE", "Service Destroyed. Self Kill = " + selfKill);

      // If the service was killed by user (NOT by auto-snooze)
      // then kill the auto-snooze callback
      if (!selfKill)
         handler.removeCallbacks(autoSnooze);
   }
   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   // Create Pending intent for the Stop button that is on the notification
   static public PendingIntent createStopPendingIntent(Context context, Bundle bundle){
      Intent stopIntent = new Intent(context, AlarmReceiver.class);
      stopIntent.setAction("stop");
      stopIntent.putExtras(bundle);
      stopIntent.removeExtra(K_TYPE);
      stopIntent.putExtra(K_TYPE, "stop");
      return PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
   }

   // Create Pending intent for the Snooze button that is on the notification
   static public PendingIntent createSnoozePendingIntent(Context context, Bundle bundle){
      Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
      snoozeIntent.setAction("snooze");
      snoozeIntent.putExtras(bundle);
      snoozeIntent.removeExtra(K_TYPE);
      snoozeIntent.putExtra(K_TYPE, "snooze");
      return PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
   }

   // Create notification to display when Alarm goes off
   // Will be called from onStartCommand()
   private Notification CreateNotification(Intent intent){

      // Strings to show as alarm text and Title
      Bundle inBundle = intent.getExtras();
      if (inBundle == null) return null;
      String label = inBundle.getString(K_LABEL);
      int h = inBundle.getInt(K_HOUR, -1);
      int m = inBundle.getInt(K_MINUTE, -1);
      @SuppressLint("DefaultLocale") String alarmText = String.format("%s - %d:%02d", label, h, m);
      String alarmTitle = getResources().getString(R.string.notification_title);
      Log.i("THE_TIME_MACHINE", alarmText);

      // Prepare intent for a Stop/Snooze fullscreen activity
      Intent fullScreenIntent = new Intent(this, StopSnoozeActivity.class);
      fullScreenIntent.putExtra("APP_NAME", "The Time Machine");
      fullScreenIntent.putExtras(inBundle);
      PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE );

      // Create the Stop action
      PendingIntent stopIntent = createStopPendingIntent(this, inBundle);
      NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(
            R.drawable.baseline_alarm_off_24, getString(R.string.stop), stopIntent).build();

      // Create the Snooze action
      PendingIntent snoozeIntent = createSnoozePendingIntent(this, inBundle);
      NotificationCompat.Action snoozeAction = new NotificationCompat.Action.Builder(
            R.drawable.snooze_fill0_wght400_grad0_opsz24, getString(R.string.snooze), snoozeIntent).build();

      // Notification
      return  new NotificationCompat.Builder(this, CHANNEL_ID)
            /* Title */                .setContentTitle(alarmTitle)
            /* Content */              .setContentText(alarmText)
            /* Status bar Icon */      .setSmallIcon(R.drawable.baseline_alarm_add_24)
            /* Always on top */        .setPriority(NotificationCompat.PRIORITY_MAX)
            /* Set category */         .setCategory(NotificationCompat.CATEGORY_ALARM)
            /* Full screen activity */ .setFullScreenIntent(fullScreenPendingIntent, true)
            /* View on locked screen*/ .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            //.setContentIntent(pendingIntent)
            /* Audio and vibration */  .setDefaults(Notification.DEFAULT_ALL)
            /* Stop Button */          .addAction(stopAction)
            /* Stop Button */          .addAction(snoozeAction)
            .setTimeoutAfter(-1)
            .build();
   }
}
