package com.oneplus.camera.ui;

import android.view.MotionEvent;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface GestureDetector
  extends Component
{
  public abstract void handleTouchEvent(MotionEvent paramMotionEvent);
  
  public abstract Handle setGestureHandler(GestureHandler paramGestureHandler, int paramInt);
  
  public static abstract interface GestureHandler
  {
    public static final long DURATION_TOUCH_UP_THRESHOLD = 0L;
    
    public abstract boolean onDoubleTap(MotionEvent paramMotionEvent);
    
    public abstract boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2);
    
    public abstract void onGestureEnd(MotionEvent paramMotionEvent);
    
    public abstract boolean onGestureStart(MotionEvent paramMotionEvent);
    
    public abstract boolean onLongPress(MotionEvent paramMotionEvent);
    
    public abstract boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2);
    
    public abstract boolean onSingleTapUp(MotionEvent paramMotionEvent);
    
    public abstract boolean onSlideDown(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2);
    
    public abstract boolean onSlideLeft(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2);
    
    public abstract boolean onSlideRight(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2);
    
    public abstract boolean onSlideUp(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/GestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */