package com.example.thetimemachine.UI;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.example.thetimemachine.AlarmService;
import com.example.thetimemachine.databinding.ActivityStopSnoozeBinding;
import com.example.thetimemachine.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StopSnoozeActivity extends AppCompatActivity {
   /**
    * Whether or not the system UI should be auto-hidden after
    * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
    */
   private static final boolean AUTO_HIDE = false;
   private Bundle extras = null;

   /**
    * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
    * user interaction before hiding the system UI.
    */
   private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

   /**
    * Some older devices needs a small delay between UI widget updates
    * and a change of the status and navigation bar.
    */
   private static final int UI_ANIMATION_DELAY = 300;
   private final Handler mHideHandler = new Handler(Looper.myLooper());
   private View mContentView;
   private final Runnable mHidePart2Runnable = new Runnable() {
      @SuppressLint("InlinedApi")
      @Override
      public void run() {
         // Delayed removal of status and navigation bar
         if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().hide(
                  WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
         } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
         }
      }
   };
   private View mControlsView;
   private final Runnable mShowPart2Runnable = new Runnable() {
      @Override
      public void run() {
         // Delayed display of UI elements
         ActionBar actionBar = getSupportActionBar();
         if (actionBar != null) {
            actionBar.show();
         }
         mControlsView.setVisibility(View.VISIBLE);
      }
   };
   private boolean mVisible;
   private final Runnable mHideRunnable = new Runnable() {
      @Override
      public void run() {
         hide();
      }
   };
   /**
    * Touch listener to use for in-layout UI controls to delay hiding the
    * system UI. This is to prevent the jarring behavior of controls going away
    * while interacting with activity UI.
    */
   private final View.OnTouchListener mStopTouchListener = new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
         switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
               if (AUTO_HIDE) {
                  delayedHide(AUTO_HIDE_DELAY_MILLIS);
               }
               // Stop the Alarm by stopping the Alarm Service
               Context context = getApplicationContext();
               context.stopService(new Intent(context, AlarmService.class));

               // Kill this activity
               finish();
               break;
            case MotionEvent.ACTION_UP:
               view.performClick();
               break;
            default:
               break;
         }
         return false;
      }
   };
   private final View.OnTouchListener mSnoozeTouchListener = new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
         switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
               if (AUTO_HIDE) {
                  delayedHide(AUTO_HIDE_DELAY_MILLIS);
               }
               break;
            case MotionEvent.ACTION_UP:
               view.performClick();
               break;
            default:
               break;
         }
         return false;
      }
   };
   private ActivityStopSnoozeBinding binding;

   @SuppressLint("ClickableViewAccessibility")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      binding = ActivityStopSnoozeBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

      mVisible = true;
      mControlsView = binding.fullscreenContentControls;
      mContentView = binding.fullscreenContent;

      // Allow this activity on a locked screen
      allowOnLockScreen();

      // Set up the user interaction to manually show or hide the system UI.
      mContentView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            toggle();
         }
      });

      // Upon interacting with UI controls, delay any scheduled hide()
      // operations to prevent the jarring behavior of controls going away
      // while interacting with the UI.
      binding.stopButton.setOnTouchListener(mStopTouchListener);

      binding.snoozeButton.setOnTouchListener(mSnoozeTouchListener);
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);

      // Make controls, status bar and navigation visible
      show();

      // Get the data passed from calling service
      extras = getIntent().getExtras();

      //  Display the data on the screen
      DisplayScreenText();
   }

   private void toggle() {
      if (mVisible) {
         hide();
      } else {
         show();
      }
   }

   private void hide() {
      // Hide UI first
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.hide();
      }
      mControlsView.setVisibility(View.GONE);
      mVisible = false;

      // Schedule a runnable to remove the status and navigation bar after a delay
      mHideHandler.removeCallbacks(mShowPart2Runnable);
      mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
   }

   private void show() {
      // Show the system bar
      if (Build.VERSION.SDK_INT >= 30) {
         mContentView.getWindowInsetsController().show(
               WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
      } else {
         mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
               | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
      }
      mVisible = true;

      // Schedule a runnable to display UI elements after a delay
      mHideHandler.removeCallbacks(mHidePart2Runnable);
      mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
   }

   /**
    * Schedules a call to hide() in delay milliseconds, canceling any
    * previously scheduled calls.
    */
   private void delayedHide(int delayMillis) {
      mHideHandler.removeCallbacks(mHideRunnable);
      mHideHandler.postDelayed(mHideRunnable, delayMillis);
   }

   /* Based on: https://stackoverflow.com/questions/55913495/open-activity-above-lockscreen
   *  Allow this activity to be displayed on a locked machine and to accept user input.
   */
   private void allowOnLockScreen() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
         setShowWhenLocked(true);
         setTurnScreenOn(true);
         KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
         keyguardManager.requestDismissKeyguard(this, null);
      } else {
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
               WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
               WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
      }
   }

   private void DisplayScreenText()
   {
      String appName = extras.getString("APP_NAME","");
      String label = extras.getString("LABEL","");
      if (label.length() > 0)
         label+=" - ";

      String time = "";
      int minute = extras.getInt("MINUTE",-1);
      int hour = extras.getInt("HOUR",-1);
      if (minute!=-1 && hour!=-1)
         time = String.format("%d:%02d", hour, minute);

      String displayText = String.format("%s\n\n%s%s", appName, label, time);
      binding.fullscreenContent.setText(displayText);
   }
}