package com.product.thetimemachine.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class TheTimeMachineApp extends Application {
   public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
   public static Context appContext;

   public static final String[] PREFS = {
         "key_ring_duration", "key_ring_repeat", "key_snooze_duration", "key_vibration_pattern",
         "key_alarm_sound", "kay_gradual_volume"
   };

   @Override
   public void onCreate() {
      super.onCreate();

      createNotificationChannel();
      appContext = getApplicationContext();
   }

   private void createNotificationChannel() {

      NotificationChannel serviceChannel = new NotificationChannel(
            CHANNEL_ID,
            "The Time Machine: Alarm Service Channel",
            NotificationManager.IMPORTANCE_HIGH
      );

      serviceChannel.setSound(null, null);
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);

   }
}
