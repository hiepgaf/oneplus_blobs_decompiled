package com.android.server.accessibility;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

public class KeyboardInterceptor
  implements EventStreamTransformation
{
  private AccessibilityManagerService mAms;
  private EventStreamTransformation mNext;
  
  public KeyboardInterceptor(AccessibilityManagerService paramAccessibilityManagerService)
  {
    this.mAms = paramAccessibilityManagerService;
  }
  
  public void clearEvents(int paramInt)
  {
    if (this.mNext != null) {
      this.mNext.clearEvents(paramInt);
    }
  }
  
  public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (this.mNext != null) {
      this.mNext.onAccessibilityEvent(paramAccessibilityEvent);
    }
  }
  
  public void onDestroy() {}
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    this.mAms.notifyKeyEvent(paramKeyEvent, paramInt);
  }
  
  public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    if (this.mNext != null) {
      this.mNext.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
    }
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation)
  {
    this.mNext = paramEventStreamTransformation;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/KeyboardInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */