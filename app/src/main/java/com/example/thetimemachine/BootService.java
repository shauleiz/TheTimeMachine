package com.example.thetimemachine;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

public class BootService extends LifecycleService{

   @Override
   public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
      super.onStartCommand(intent, flags, startId);
      Log.d("THE_TIME_MACHINE", "onStartCommand(): StartId="+ startId);
      stopSelf();
      Log.d("THE_TIME_MACHINE", "onStartCommand(): After stop???");
      return START_STICKY;
   }

   @Nullable
   @Override
   public IBinder onBind(@NonNull Intent intent) {
      return super.onBind(intent);
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
   }

   @Override
   public void onCreate() {
      super.onCreate();
   }
}
