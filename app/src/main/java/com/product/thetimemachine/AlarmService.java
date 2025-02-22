package com.product.thetimemachine;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
import static com.product.thetimemachine.Application.TheTimeMachineApp.CHANNEL_ID;
import static com.product.thetimemachine.Application.TheTimeMachineApp.KILL_STOP_SNOOZE;
import static com.product.thetimemachine.Application.TheTimeMachineApp.appContext;
import static com.product.thetimemachine.Data.AlarmItem.K_LABEL;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_SnoozeDuration;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_alarm_sound;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_gradual_volume;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_ring_duration;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_ring_repeat;
import static com.product.thetimemachine.Data.AlarmItem.Str2Int_vibration_pattern;
import static com.product.thetimemachine.Data.AlarmRoomDatabase.insertAlarm;
import static com.product.thetimemachine.ui.SettingsScreenKt.getPrefLanguage;
import static com.product.thetimemachine.ui.SettingsScreenKt.isPref24h;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.product.thetimemachine.Data.AlarmItem;
import com.product.thetimemachine.ui.StopSnoozeActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AlarmService  extends Service {

   private Vibrator vibrator;
   private static MediaPlayer mediaPlayer;
   private boolean selfKill;
   Handler handler;
   Runnable autoSnooze;

   public static final String K_TYPE = "TYPE";
   public static final String SNOOZE = "snooze";
   public static final String STOP = "stop";
   public static final String ALARM = "alarm";

   BroadcastReceiver broadcastReceiver;
   private SimpleDateFormat dateFormat;
   NotificationCompat.Builder builder;

   @SuppressLint("SimpleDateFormat")
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      Log.i("THE_TIME_MACHINE", "Service Started");

      /////  Get language from preferences and make App Context match

      // Set the current Locale
      String  language = getPrefLanguage(getBaseContext());

      // Get locale from the language and set default locale
      Locale locale = new Locale(language);
      Locale.setDefault(locale);

      // Set App Context
      Resources resources = this.getResources();
      Configuration config = resources.getConfiguration();
      config.setLocale(locale);
      appContext = createConfigurationContext(config);

      Log.i("THE_TIME_MACHINE", "onStartCommand(): locale = " + locale + " language = " + language);


      // Display Activity: Stop, Snooze and general data
      // TODO: Define activity Intent notificationIntent = new Intent(this, RingActivity.class);


      // Create notification: With Title, Icon and Stop button
      // Text will be added externally
      builder = CreateNotification(intent);
      if (builder == null ) return START_NOT_STICKY;

      // Set The time format
      if (isPref24h(appContext))
         dateFormat = new SimpleDateFormat("H:mm",locale);
      else
         dateFormat = new SimpleDateFormat("h:mm a",locale);

      // Strings to show as alarm text when the notification shows for the 1st time
      String alarmText;
      Bundle inBundle = intent.getExtras();
      if (inBundle == null) return START_NOT_STICKY;
      String label = inBundle.getString(K_LABEL);
      if (!Objects.requireNonNull(label).isEmpty())
         alarmText = String.format(locale,"%s - %s", label, dateFormat.format(new Date()));
      else
         alarmText = String.format(locale,"%s", dateFormat.format(new Date()));


      // Display Notification for the 1st time
      builder.setContentText(alarmText).build();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
         startForeground( 1, builder.build(), FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED); // TODO: Replace '1' with an ID
      }
      else
         startForeground(1,builder.build()); // TODO: Replace '1' with an ID


      // Definition of the receiver that gets an ACTION_TIME_TICK every minute
      broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context ctx, Intent intent) {
            String alarmText;

            // Refresh Text
            if (!label.isEmpty())
               alarmText = String.format(locale,"%s - %s", label, dateFormat.format(new Date()));
            else
               alarmText = String.format(locale,"%s", dateFormat.format(new Date()));

            // Update message text
            builder.setContentText(alarmText).build();

            // Display Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
               startForeground( 1, builder.build(), FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED); // TODO: Replace '1' with an ID
            }
            else
               startForeground(1,builder.build()); // TODO: Replace '1' with an ID
         }
      };

      // Register the ACTION_TIME_TICK receiver
      registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));


      Log.i("THE_TIME_MACHINE", "Service Started.2");

      // Get Alarm Data
      Bundle b = intent.getExtras();
      if (b == null) return START_NOT_STICKY;


      // Get preferences (Menu String Values)
      String StrRingDuration = b.getString(appContext.getString(R.string.key_ring_duration));
      String StrRingRepeat = b.getString(appContext.getString(R.string.key_ring_repeat));
      String StrVibrationPattern = b.getString(appContext.getString(R.string.key_vibration_pattern));
      String StrAlarmSound = b.getString(appContext.getString(R.string.key_alarm_sound));
      String StrGradualVolume = b.getString(appContext.getString(R.string.key_gradual_volume));


      // Start audio and vibration
      assert StrAlarmSound != null;
      assert StrGradualVolume != null;
      sound(this, Str2Int_alarm_sound(StrAlarmSound), Str2Int_gradual_volume(StrGradualVolume));

      // it is safe to cancel other vibrations currently taking place
      String pattern = Str2Int_vibration_pattern(StrVibrationPattern);
      vibrator.cancel();
      VibrateEffect(this, pattern);

      // TODO: If recurring alarm, schedule next alarm here


      // Start auto-snooze timer
      // After a short delay, an alarm is created and alarm.Snooze is called
      handler = new Handler(Looper.getMainLooper());
      assert StrRingDuration != null;
      int delayMillis = Str2Int_ring_duration(StrRingDuration);
      assert StrRingRepeat != null;
      int nRepeat = Str2Int_ring_repeat(StrRingRepeat);
      autoSnooze = () -> {
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
         selfKill = true;
         stopSelf();
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


      // Create vibrator
      vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      selfKill = false;

      Log.i("THE_TIME_MACHINE", "Service Created");
   }

   @Override
   public void onDestroy() {
      super.onDestroy();

      sound(this, null);
      vibrator.cancel();
      Log.i("THE_TIME_MACHINE", "Service Destroyed. Self Kill = " + selfKill);

      // If the service was killed by user (NOT by auto-snooze)
      // then kill the auto-snooze callback
      // If auto-snooze then kill StopSnoozeActivity
      if (!selfKill)
         handler.removeCallbacks(autoSnooze);
      else
         sendBroadcast(new Intent(KILL_STOP_SNOOZE));

      unregisterReceiver(broadcastReceiver);
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

   public static VibrationEffect getVibrationEffect(String effect, boolean hasAmplitudeControl) {
      final long[] timings;
      final long[] timingsNoAmp;
      final int[] amplitudes;
      final int repeatIndex ;
      final int repeatIndexNoAmp ;

      switch (effect) {
         case "ssb":  // Single Short Beat
            timings = new long[]{50, 50, 50, 50, 50, 100};
            timingsNoAmp = new long[] {300, 350, 400};//{0, 350, 400};
            amplitudes = new int[]{33, 51, 75, 113, 170, 255};
            repeatIndex = -1; // Do not repeat
            repeatIndexNoAmp = -1; // Do not repeat
            break;

         case "tsb":  // Three Short Beats
            timings = new long[]{
                    50, 50, 50, 100, 50, 50, 400,
                    50, 50, 50, 100, 50, 50, 400,
                    50, 50, 50, 100, 50, 50, 400
            };
            timingsNoAmp = new long[] {0, 350,400, 350, 400, 350};
            amplitudes = new int[]{
                    33, 113, 170, 255, 170, 113, 0,
                    33, 113, 170, 255, 170, 113, 0,
                    33, 113, 170, 255, 170, 113, 0
            };
            repeatIndex = -1; // Do not repeat
            repeatIndexNoAmp = -1; // Do not repeat
            break;

         case "slb":  // Single Long  Beat
            timings = new long[]{50, 50, 50, 400, 50, 50, 400};
            amplitudes = new int[]{33, 113, 170, 255, 170, 113, 0};
            timingsNoAmp = new long[] {0, 650, 400};
            repeatIndex = -1; // Do not repeat
            repeatIndexNoAmp = -1; // Do not repeat
            break;

         case "rsb":  // Repeating Short Beats
            timings = new long[]{
                    50, 50, 50, 100, 50, 50, 400,
                    50, 50, 50, 100, 50, 50, 400,
                    50, 50, 50, 100, 50, 50, 400
            };
            timingsNoAmp = new long[] {0, 350, 400};
            amplitudes = new int[]{
                    33, 113, 170, 255, 170, 113, 0,
                    33, 113, 170, 255, 170, 113, 0,
                    33, 113, 170, 255, 170, 113, 0
            };
            repeatIndex = 0; // repeat
            repeatIndexNoAmp = 0; // repeat
            break;

         case "rlb":  // Repeating Long  Beats
            timings = new long[]{50, 50, 50, 400, 50, 50, 600};
            timingsNoAmp = new long[] {0, 650, 600};
            amplitudes = new int[]{33, 113, 170, 255, 170, 113, 0};
            repeatIndex = 0; // repeat
            repeatIndexNoAmp = 0; // repeat
            break;

         case "cont":  // Continuous buzz
            timings = new   long[]{50, 50, 50,  50,  50, 100};
            timingsNoAmp = new long[] {0, 3000};
            amplitudes = new int[]{33, 51, 75, 113, 170, 255};
            repeatIndex = 5; // Stay at max
            repeatIndexNoAmp = 0; // Stay at max

            break;
         default:  // None
            timings = new long[]{100};
            timingsNoAmp = new long[] {100, 0};
            amplitudes = new int[]{0};
            repeatIndex = -1;
            repeatIndexNoAmp = -1; // Do not repeat

            break;
      }

      if (hasAmplitudeControl)
         return VibrationEffect.createWaveform(timings, amplitudes, repeatIndex);
      else
         return VibrationEffect.createWaveform(timingsNoAmp, repeatIndexNoAmp);

   }


   public static void sound(Context context, String pattern){
      sound(context, pattern, 0);
   }
   public static void sound(Context context, String pattern, int rampInMillis){

      // Stop currently playing before playing a new pattern
      if (mediaPlayer !=null && mediaPlayer.isPlaying() ) {
         mediaPlayer.stop();
         mediaPlayer.reset();
      }


      // Create a media player and make it stream as ALARM
      if (pattern!=null && !pattern.isEmpty()){
         mediaPlayer = new MediaPlayer();
         //mediaPlayer = MediaPlayer.create(context, getUriForMusicFilename(pattern));


         try {
            mediaPlayer.setDataSource(context, getUriForMusicFilename(pattern));
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                                 .setUsage(AudioAttributes.USAGE_ALARM)
                                                 .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                 .build());
            mediaPlayer.prepare();
         }
         catch (IOException e) {
            e.printStackTrace();
         }

         mediaPlayer.setLooping(true);
         mediaPlayer.start();

         Log.d("THE_TIME_MACHINE", "Sound(): rampInMillis = " + rampInMillis);

         if (rampInMillis>0) {
            // Create a volume envelop
            VolumeShaper.Configuration config =
                  new VolumeShaper.Configuration.Builder()
                        .setDuration(rampInMillis)
                        .setCurve(new float[]{0.f, 1.f}, new float[]{0.f, 1.f})
                        .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
                        .build();


               VolumeShaper volumeShaper = mediaPlayer.createVolumeShaper(config);
               volumeShaper.apply(VolumeShaper.Operation.PLAY);
         }

      }

   }


 /*  private  Uri getUriForMusicFilename(int id){

      // Get Package Name
      String packageName  = getPackageName();
      return Uri.parse("android.resource://" + packageName + "/" + id);
   }
*/

   static private  Uri getUriForMusicFilename(String filename){

      // Get Package Name
      String packageName  = appContext.getPackageName();
      return Uri.parse("android.resource://" + packageName + "/raw/" + filename);
   }

   public static String snoozeButtonText(String strSnoozeDuration){
      String strBase =  appContext.getString(R.string.snooze);
      if (strSnoozeDuration == null || strSnoozeDuration.isEmpty())
         return strBase;

      String units= appContext.getString(R.string.snooze_button_sec);
      int snoozeDuration =  Str2Int_SnoozeDuration(strSnoozeDuration)/1000;
      if (snoozeDuration>=60){
         snoozeDuration=snoozeDuration/60;
         units = appContext.getString(R.string.snooze_button_min);
      }
      return strBase + "  "+ snoozeDuration + units;
   }




   // Create notification builder to display when Alarm goes off
   // Will be called from onStartCommand()
   @Nullable
   private NotificationCompat.Builder CreateNotification(@NonNull Intent intent){

      // Strings to show as alarm text and Title
      Bundle inBundle = intent.getExtras();
      if (inBundle == null) return null;
      String strSnoozeDuration = inBundle.getString(appContext.getString(R.string.key_snooze_duration), "");

   // Create a random number for request code
      Random random = new Random();
      int requestCode = random.nextInt(10000000);

      // Prepare intent for a Stop/Snooze fullscreen activity
      Intent fullScreenIntent = new Intent(this, StopSnoozeActivity.class);
      // Set the Activity to start in a new, empty task.
      fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      fullScreenIntent.putExtra("APP_NAME", "The Time Machine"); // TODO: Replace with system string
      fullScreenIntent.putExtras(inBundle);
      PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, requestCode,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE );

      // Create the Stop action
      PendingIntent stopIntent = createStopPendingIntent(this, inBundle);
      NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(
            R.drawable.baseline_alarm_off_24, appContext.getString(R.string.stop), stopIntent).build();

      // Create the Snooze action
      PendingIntent snoozeIntent = createSnoozePendingIntent(this, inBundle);
      NotificationCompat.Action snoozeAction = new NotificationCompat.Action.Builder(
            R.drawable.snooze_fill0_wght400_grad0_opsz24, snoozeButtonText(strSnoozeDuration), snoozeIntent).build();

      // Notification
      NotificationCompat.Builder builder;
      builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            /* Title */                .setContentTitle(appContext.getString(R.string.notification_title))
            /* Content */              //.setContentText(alarmText)
            /* Status bar Icon */      .setSmallIcon(R.drawable.baseline_alarm_24)
            /* Always on top */        .setPriority(NotificationCompat.PRIORITY_MAX)
            /* Set category */         .setCategory(NotificationCompat.CATEGORY_ALARM)
            /* Full screen activity */ .setFullScreenIntent(fullScreenPendingIntent, true)
            /* View on locked screen*/ .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            //.setContentIntent(pendingIntent)
            /* Audio and vibration */  .setDefaults(Notification.DEFAULT_ALL)
            /* Stop Button */          .addAction(stopAction)
            /* Stop Button */          .addAction(snoozeAction)
            /* Not canceled by system*/.setTimeoutAfter(-1);

      return builder;
   }


}
