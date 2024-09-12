package com.product.thetimemachine.UI;

import static com.product.thetimemachine.AlarmService.snoozeButtonText;
import static com.product.thetimemachine.Application.TheTimeMachineApp.appContext;
import static com.product.thetimemachine.UI.SettingsFragment.pref_is24HourClock;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.compose.ui.platform.ComposeView;

import com.product.thetimemachine.AlarmReceiver;
import com.product.thetimemachine.AlarmService;
import com.product.thetimemachine.R;
import com.product.thetimemachine.databinding.ActivityStopSnoozeBinding;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.text.SimpleDateFormat;

public class StopSnoozeActivity extends AppCompatActivity {

   private static final boolean AUTO_HIDE = false;
   private Bundle extras = null;
   private String strCurrentTime = "99:99 am";
   BroadcastReceiver broadcastReceiver;
   private SimpleDateFormat dateFormat;

   private TextView footer;

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
   private final Handler mHideHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
   private View mContentView;
   private final Runnable mHidePart2Runnable = new Runnable() {
      @SuppressLint("InlinedApi")
      @Override
      public void run() {
         // Delayed removal of status and navigation bar
         if (Build.VERSION.SDK_INT >= 30) {
            Objects.requireNonNull(mContentView.getWindowInsetsController()).hide(
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
   private final Runnable mHideRunnable = this::hide;


      private final View.OnLongClickListener mSnoozeLongClickListener = new View.OnLongClickListener(){
      @Override
      public boolean onLongClick(View view) {

         // Get the current Snooze duration
         String strSnoozeDuration = extras.getString(appContext.getString(R.string.key_snooze_duration), "");

         /* * * Display Snooze duration pop-up menu * * */
         // Initializing the popup menu and giving the reference as current context
         PopupMenu popupMenu = new PopupMenu(StopSnoozeActivity.this , view);

         // Populate the pop-up menu
         TypedArray snoozeDurationEntries = getResources().obtainTypedArray(R.array.snooze_duration_entries);
         TypedArray snoozeDurationValues = getResources().obtainTypedArray(R.array.snooze_duration_values);
         for(int i=0; i<snoozeDurationEntries.length(); i++){
            popupMenu.getMenu().add(0,i, Menu.NONE, snoozeDurationEntries.getString(i));
         }
         snoozeDurationEntries.recycle();
         snoozeDurationValues.recycle();

         // Showing the popup menu
         popupMenu.show();
         popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
               // Log message on menu item clicked
               Log.d("THE_TIME_MACHINE", "You Clicked " + menuItem.getTitle() + " ID="+ menuItem.getItemId());
               // Update the bundle
               extras.putString(appContext.getString(R.string.key_snooze_duration), snoozeDurationValues.getString(menuItem.getItemId()));

               // Stop the Alarm by stopping the Alarm Service
               Context context = getApplicationContext();
               Intent snoozeIntent = new Intent(context, AlarmService.class);
               snoozeIntent.putExtras(extras);
               AlarmReceiver.snoozing(context, snoozeIntent );

               // Done
               finish();
               return true;
            }
         });

//         popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//               Log.d("THE_TIME_MACHINE", "Popup menu dismissed");
//               return;
//            }
//         });

         return true; // Do not run OnClick
      }
   };

   private final View.OnClickListener  mSnoozeClickListener = new View.OnClickListener(){
      @Override
      public void onClick(View view) {
         if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
         }
         // Stop the Alarm by stopping the Alarm Service
         Context context = getApplicationContext();
         Intent snoozeIntent = new Intent(context, AlarmService.class);
         snoozeIntent.putExtras(extras);
         AlarmReceiver.snoozing(context, snoozeIntent );

         // Kill this activity
         finish();
      }
   };

   public void onClickStop(){
      // Stop the Alarm by stopping the Alarm Service
      Context context = getApplicationContext();

      Intent stopIntent = new Intent(context, AlarmService.class);
      stopIntent.putExtras(extras);
      AlarmReceiver.stopping(context, stopIntent );

               /*
               // Leave this activity to main activity
               Intent intent = new Intent(context, MainActivity.class);
               startActivity(intent);
                */

      // Kill this activity
      finish();
   }

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
               Log.d("THE_TIME_MACHINE", "Snooze: ACTION_DOWN");

               if (AUTO_HIDE) {
                  delayedHide(AUTO_HIDE_DELAY_MILLIS);
               }
               // Stop the Alarm by stopping the Alarm Service
               Context context = getApplicationContext();

               Intent stopIntent = new Intent(context, AlarmService.class);
               stopIntent.putExtras(extras);
               AlarmReceiver.stopping(context, stopIntent );

               /*
               // Leave this activity to main activity
               Intent intent = new Intent(context, MainActivity.class);
               startActivity(intent);
                */

               // Kill this activity
               finish();
               return true;
            case MotionEvent.ACTION_UP:
               Log.d("THE_TIME_MACHINE", "Snooze: ACTION_UP");
               view.performClick();
               return true;
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
               // Stop the Alarm by stopping the Alarm Service

               Context context = getApplicationContext();
               Intent snoozeIntent = new Intent(context, AlarmService.class);
               snoozeIntent.putExtras(extras);
               AlarmReceiver.snoozing(context, snoozeIntent );
/*
               // Leave this activity to main activity
               Intent intent = new Intent(context, MainActivity.class);
               startActivity(intent);*/

               // Kill this activity
               finish();
               return true;

            case MotionEvent.ACTION_UP:
               view.performClick();
               return true;
            default:
               break;
         }
         return false;
      }
   };
   private ActivityStopSnoozeBinding binding;


   @Nullable
   @Override
   public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
      View view = super.onCreateView(parent, name, context, attrs);



      return view;
   }

   @SuppressLint("ClickableViewAccessibility")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      binding = ActivityStopSnoozeBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

      mVisible = true;
      mControlsView = binding.fullscreenContentControls;
      mContentView = binding.fullscreenContent;

      footer = findViewById(R.id.footnote);

      // Allow this activity on a locked screen
      allowOnLockScreen();

      // Set the default volume control as ALARM volume control
      setVolumeControlStream(AudioManager.STREAM_ALARM);

      // Hide Navigation Bar
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         Objects.requireNonNull(getWindow().getInsetsController()).hide(WindowInsets.Type.navigationBars());
      }
      else
         getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);



      // Set up the user interaction to manually show or hide the system UI.
      /*mContentView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            toggle();
         }
      });*/

      // Upon interacting with UI controls, delay any scheduled hide()
      // operations to prevent the jarring behavior of controls going away
      // while interacting with the UI.
      binding.stopButton.setOnTouchListener(mStopTouchListener);

      //binding.snoozeButton.setOnTouchListener(mSnoozeTouchListener);
      binding.snoozeButton.setOnLongClickListener(mSnoozeLongClickListener);
      binding.snoozeButton.setOnClickListener(mSnoozeClickListener);

      // Set The time format
      if (pref_is24HourClock())
         dateFormat = new SimpleDateFormat("H:mm",Locale.US);
      else
         dateFormat = new SimpleDateFormat("h:mm a",Locale.US);


   }


   @Override
   protected void onStart() {
      super.onStart();

      // Definition of the receiver that gets an ACTION_TIME_TICK every minute
      broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context ctx, Intent intent) {
            strCurrentTime = (dateFormat.format(new Date()));

            // Refresh Text
            DisplayScreenText();
         }
      };

      // Register the receiver
      registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

   }

   @Override
   protected void onStop() {
      unregisterReceiver(broadcastReceiver);
      super.onStop();
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);

      // Make controls, status bar and navigation visible
      //show();

      // Get the data passed from calling service
      extras = getIntent().getExtras();

      // Set the snooze button text
      View v = binding.getRoot();
      TextView snoozeButton = v.findViewById(R.id.snooze_button);
      String strSnoozeDuration = extras.getString(appContext.getString(R.string.key_snooze_duration), "");
      snoozeButton.setText(snoozeButtonText(strSnoozeDuration));

      //  Display the data on the screen
      strCurrentTime = (dateFormat.format(new Date()));


      // Compose Content
      hide(); // Immersive mode: Hide system bars
      ComposeView composeView = binding.stopSnoozeComposeView;
      StopSnoozeDisplay.setContent(composeView,  DisplayScreenText(), this);

   }

   @Override
   protected void onResume() {
      super.onResume();
      allowOnLockScreen();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      setShowWhenLocked(false);
      setTurnScreenOn(false);

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
         Objects.requireNonNull(mContentView.getWindowInsetsController()).show(
               WindowInsets.Type.statusBars()/* | WindowInsets.Type.navigationBars()*/);
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
      setShowWhenLocked(true);
      setTurnScreenOn(true);
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
         KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
         keyguardManager.requestDismissKeyguard(this, null); // API 28 Only
      }
   }

   public String DisplayScreenText()
   {
      String appName = extras.getString("APP_NAME","");
      String label = extras.getString("LABEL","");
      if (!label.isEmpty())
         label+=" - ";

      String displayText = String.format(Locale.US,"%s%s",  label, strCurrentTime);
      ((TextView)mContentView).setText(displayText);

      footer.setText(appName);

      return  displayText;
   }
}