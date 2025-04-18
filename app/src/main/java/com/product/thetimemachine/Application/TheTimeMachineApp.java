package com.product.thetimemachine.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.product.thetimemachine.ui.MainActivity;

public class TheTimeMachineApp extends Application {
   public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
   public static final String KILL_STOP_SNOOZE = "com.product.thetimemachine.kill.stopsnoozeactivity";
   public static Context appContext;
   public static MainActivity mainActivity;



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
