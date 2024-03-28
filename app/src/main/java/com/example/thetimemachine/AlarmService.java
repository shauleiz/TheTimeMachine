package com.example.thetimemachine;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.thetimemachine.Application.TheTimeMachineApp.CHANNEL_ID;
import static com.example.thetimemachine.Application.TheTimeMachineApp.appContext;
import static com.example.thetimemachine.Data.AlarmItem.K_HOUR;
import static com.example.thetimemachine.Data.AlarmItem.K_LABEL;
import static com.example.thetimemachine.Data.AlarmItem.K_MINUTE;
import static com.example.thetimemachine.Data.AlarmRoomDatabase.insertAlarm;
import static com.example.thetimemachine.UI.SettingsFragment.pref_is24HourClock;
import static com.example.thetimemachine.UI.SettingsFragment.pref_ring_duration;
import static com.example.thetimemachine.UI.SettingsFragment.pref_ring_repeat;
import static com.example.thetimemachine.UI.SettingsFragment.pref_vibration_pattern;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.thetimemachine.Data.AlarmDao;
import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.Data.AlarmRoomDatabase;
import com.example.thetimemachine.UI.StopSnoozeActivity;

import java.util.Locale;

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


      // TODO: Improve vibration timing , Add Vibration control (On/Off) and selection of patterns
      // this effect creates the vibration of default amplitude for 1000ms(1 sec)


      // it is safe to cancel other vibrations currently taking place
      String pattern = pref_vibration_pattern();
      vibrator.cancel();
      VibrateEffect(this, pattern);


      // TODO: If recurring alarm, schedule next alarm here


      // Start auto-snooze timer
      // After a short delay, an alarm is created and alarm.Snooze is called
      handler = new Handler(Looper.getMainLooper());
      int delayMillis = pref_ring_duration();
      int nRepeat = pref_ring_repeat();
      autoSnooze = new Runnable() {
         @Override
         public void run() {

            Bundle b = intent.getExtras();
            if (b==null) return;
            AlarmItem alarm = new AlarmItem(b);
            alarm.incSnoozeCounter();

            // Check if the alarm has exceeded the number of times it should go off
            if (nRepeat<100 && (alarm.getSnoozeCounter()) > nRepeat)
            {
               alarm.resetSnoozeCounter();
               alarm.setActive(false);
               Context context = getApplicationContext();
               Intent stopIntent = new Intent(context, AlarmService.class);
               stopIntent.putExtras(b);
               AlarmReceiver.stopping(context, stopIntent );
            }

            alarm.Exec();
            // Force update of UI
            insertAlarm(alarm,  getApplicationContext());
            /*AlarmRoomDatabase db = AlarmRoomDatabase.getDatabase(getApplicationContext());
            AlarmDao alarmDao = db.alarmDao();
            AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(alarm));*/
            selfKill = true;
            stopSelf();
         }
      };
      // auto-snooze starts running after delayMillis milliseconds
      Log.i("THE_TIME_MACHINE", "autoSnooze(): Ring for "+ delayMillis/1000 + " Seconds");
      handler.postDelayed(autoSnooze, delayMillis);

      Log.i("THE_TIME_MACHINE", "Service Started.3");

      return START_STICKY;
   }
   @Override
   public void onCreate() {
      super.onCreate();

      // Create Media player - Plays Alarm
      // TODO: Select MP3 to play
      mediaPlayer = MediaPlayer.create(this, R.raw.alarm1000);
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
   public PendingIntent createStopPendingIntent(Context context, Bundle bundle){
      Intent stopIntent = new Intent(context, AlarmReceiver.class);
      stopIntent.setAction("stop");
      stopIntent.putExtras(bundle);
      stopIntent.removeExtra(K_TYPE);
      stopIntent.putExtra(K_TYPE, "stop");
      return PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
   }

   // Create Pending intent for the Snooze button that is on the notification
    public PendingIntent createSnoozePendingIntent(Context context, Bundle bundle){
      Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
      snoozeIntent.setAction("snooze");
      snoozeIntent.putExtras(bundle);
      snoozeIntent.removeExtra(K_TYPE);
      snoozeIntent.putExtra(K_TYPE, "snooze");
      return PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
   }

   // Activate vibration effect
   static public void VibrateEffect(Context context, String effect){

      Vibrator vibrator;
      long[] timings;
      int[] amplitudes;
      int repeatIndex;

      VibrationEffect vibrationEffect;

      // Get the Vibrator according to the Android version
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
         vibrator = vibratorManager.getDefaultVibrator();
      } else {
         vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      }

      // Stop all other vibrations
      vibrator.cancel();

      // Create the Vibration Effect
      boolean hasAmplitudeControl = vibrator.hasAmplitudeControl();
      vibrationEffect = getVibrationEffect(effect, hasAmplitudeControl);

      // Vibrate
      vibrator.vibrate(vibrationEffect);
   }

   private static VibrationEffect getVibrationEffect(String effect, boolean hasAmplitudeControl) {
      long[] timings;
      int[] amplitudes;
      int repeatIndex ;

      if (effect.equals("ssb")) { // Single Short Beat
         timings = new long[]  { 50, 50, 50, 50,  50,  100 };
         amplitudes = new int[]{ 33, 51, 75, 113, 170, 255 };
         repeatIndex = -1; // Do not repeat
      }
      else if (effect.equals("tsb")) { // Three Short Beats
         timings = new long[]  {
               50,  50,  50,  100, 50,  50,  400,
               50,  50,  50,  100, 50,  50,  400,
               50,  50,  50,  100, 50,  50,  400
         };
         amplitudes = new int[]{
               33,  113, 170, 255, 170, 113, 0,
               33,  113, 170, 255, 170, 113, 0,
               33,  113, 170, 255, 170, 113, 0
         };
         repeatIndex = -1; // Do not repeat
      }
      else if (effect.equals("slb")) { // Single Long  Beat
         timings = new long[]  {50,  50,  50,  400, 50,  50,  400};
         amplitudes = new int[]{33,  113, 170, 255, 170, 113, 0};
         repeatIndex = -1; // Do not repeat
      }

      else if (effect.equals("rsb")) { // Repeating Short Beats
         timings = new long[]  {
               50,  50,  50,  100, 50,  50,  400,
               50,  50,  50,  100, 50,  50,  400,
               50,  50,  50,  100, 50,  50,  400
         };
         amplitudes = new int[]{
               33,  113, 170, 255, 170, 113, 0,
               33,  113, 170, 255, 170, 113, 0,
               33,  113, 170, 255, 170, 113, 0
         };
         repeatIndex = 0; // repeat
      }
      else if (effect.equals("rlb")) { // Repeating Long  Beats
         timings = new long[]  {50,  50,  50,  400, 50,  50,  600};
         amplitudes = new int[]{33,  113, 170, 255, 170, 113, 0};
         repeatIndex = 0; // repeat
      }

      else if (effect.equals("cont")) { // Continuous buzz
         timings = new long[]  { 50, 50, 50, 50,  50,  100 };
         amplitudes = new int[]{ 33, 51, 75, 113, 170, 255 };
         repeatIndex = 5; // Stay at max
      }
      else { // None
         timings = new long[]{100};
         amplitudes = new int[]{0};
         repeatIndex = -1;
      }

      return VibrationEffect.createWaveform(timings, amplitudes, repeatIndex);

   }

   private VibrationEffect getCoarseVibrationEffect(String effect) {
      long[] timings;
      int[] amplitudes;
      int repeatIndex;

      if (effect.equals("ssb")) { // Single Short Beat
         timings = new long[]  { 50, 50, 50, 50,  50,  100 };
         amplitudes = new int[]{ 33, 51, 75, 113, 170, 255 };
         repeatIndex = -1; // Stay at max
      }
      else if (effect.equals("cont")) { // Single Short Beat
         timings = new long[]{0, 200};
         repeatIndex = 1;
      }
      else { // None
         timings = new long[]{0};
         repeatIndex = 1;
      }

      return VibrationEffect.createWaveform(timings, repeatIndex);
   }

   private VibrationEffect getFineVibrationEffect(String effect) {
      long[] timings = new long[] {50, 50, 50, 50,  50,  100};
      int[] amplitudes = new int[]{33, 51, 75, 113, 170, 255};
      int repeatIndex = -1; // Stay at max
      return VibrationEffect.createWaveform(timings, amplitudes, repeatIndex);
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

      // Time format
      String ampm;
      if (pref_is24HourClock())
         ampm = "";
      else{
         if (h==0) {
            ampm = getString(R.string.format_am);
            h=12;
         }
         else if (h<12)
            ampm = getString(R.string.format_am);
         else {
            ampm = getString(R.string.format_pm);
            if (h!=12)
               h-=12;
         }
      }

      String alarmText;
      assert label != null;
      if (!label.isEmpty())
         alarmText = String.format(Locale.ENGLISH,"%s - %d:%02d%s", label, h, m,ampm);
      else
         alarmText = String.format(Locale.ENGLISH,"%d:%02d%s", h, m,ampm);
      String alarmTitle = getResources().getString(R.string.notification_title);
      Log.i("THE_TIME_MACHINE", alarmText);

      // Prepare intent for a Stop/Snooze fullscreen activity
      Intent fullScreenIntent = new Intent(this, StopSnoozeActivity.class);
      fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            /* Status bar Icon */      .setSmallIcon(R.drawable.baseline_alarm_24)
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
