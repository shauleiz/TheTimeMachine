package com.product.thetimemachine.UI;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {


   public MyTimePicker(Context context, AttributeSet attrs) {
      super(context, attrs);
   }


   @Override
   public boolean onInterceptTouchEvent(MotionEvent ev) {
      double marginX=0.25;
      double marginY=0.9;

      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
         return false;

      if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
         final int width = getWidth();
         final int height = getHeight();
         final float evX = ev.getX();
         final float evY = ev.getY();

         //Excluding the rightmost quarter of the view
         // And the upper part of the leftmost quarter of the view
         return ((evX < width * marginX) && (evY < height*marginY) ) || (evX > width * (1-marginX));
      }

      return false;
   }



}
