package com.product.thetimemachine.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {
   public MyTimePicker(Context context, AttributeSet attrs) {
      super(context, attrs);
   }


   @Override
   public boolean onInterceptTouchEvent(MotionEvent ev) {

      if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
         //Excluding the rightmost and the leftmost quarters of the view
         if ((ev.getX() < getWidth() * 0.25 ) ||(ev.getX() > getWidth() * 0.75))
            return true;
      }

      return false;
   }



}
