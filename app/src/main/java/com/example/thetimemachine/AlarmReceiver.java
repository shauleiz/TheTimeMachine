package com.example.thetimemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
   @Override
   public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      String Label = intent.getStringExtra("LABEL");
   }
}
