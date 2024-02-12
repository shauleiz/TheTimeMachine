package com.example.thetimemachine.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class TheTimeMachineApp extends Application {
   public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
   public static Context appContext;

   @Override
   public void onCreate() {
      super.onCreate();

      createNotificationChannel();
      appContext = getApplicationContext();
   }

   private void createNotificationChannel() {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel serviceChannel = new NotificationChannel(
               CHANNEL_ID,
               "The Time Machine: Alarm Service Channel",
               NotificationManager.IMPORTANCE_HIGH
         );

         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(serviceChannel);
      }
   }
}