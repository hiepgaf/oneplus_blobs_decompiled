package com.android.server.accessibility;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

abstract interface EventStreamTransformation
{
  public abstract void clearEvents(int paramInt);
  
  public abstract void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
  
  public abstract void onDestroy();
  
  public abstract void onKeyEvent(KeyEvent paramKeyEvent, int paramInt);
  
  public abstract void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt);
  
  public abstract void setNext(EventStreamTransformation paramEventStreamTransformation);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/EventStreamTransformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */