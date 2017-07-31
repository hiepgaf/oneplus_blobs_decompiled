package com.oneplus.camera.ui;

import android.view.MotionEvent;

public abstract class BaseGestureHandler
  implements GestureDetector.GestureHandler
{
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public void onGestureEnd(MotionEvent paramMotionEvent) {}
  
  public boolean onGestureStart(MotionEvent paramMotionEvent)
  {
    return true;
  }
  
  public boolean onLongPress(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onSlideDown(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    return false;
  }
  
  public boolean onSlideLeft(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    return false;
  }
  
  public boolean onSlideRight(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    return false;
  }
  
  public boolean onSlideUp(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2)
  {
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BaseGestureHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */