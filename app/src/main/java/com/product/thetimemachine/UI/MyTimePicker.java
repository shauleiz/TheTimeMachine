package com.product.thetimemachine.UI;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {
   public MyTimePicker(Context context, AttributeSet attrs) {
      super(context, attrs);
   }


   @Override
   public boolean onInterceptTouchEvent(MotionEvent ev) {
      double margin=0.25;

      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
         margin =0;

      if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
         //Excluding the rightmost and the leftmost quarters of the view
         return (ev.getX() < getWidth() * margin) || (ev.getX() > getWidth() * (1-margin));
      }

      return false;
   }



}
