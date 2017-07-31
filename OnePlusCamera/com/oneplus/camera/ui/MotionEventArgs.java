package com.oneplus.camera.ui;

import android.view.MotionEvent;
import com.oneplus.base.EventArgs;
import com.oneplus.base.RecyclableObject;
import java.util.ArrayDeque;

public class MotionEventArgs
  extends EventArgs
  implements RecyclableObject
{
  private static final ArrayDeque<MotionEventArgs> POOL = new ArrayDeque(8);
  private static final int POOL_SIZE = 8;
  private volatile int m_Action;
  private volatile boolean m_IsFreeInstance;
  private volatile MotionEvent m_MotionEvent;
  private volatile int m_PointerCount;
  private volatile float m_X;
  private volatile float m_Y;
  
  public static MotionEventArgs obtain(MotionEvent paramMotionEvent)
  {
    try
    {
      MotionEventArgs localMotionEventArgs2 = (MotionEventArgs)POOL.pollLast();
      MotionEventArgs localMotionEventArgs1 = localMotionEventArgs2;
      if (localMotionEventArgs2 == null) {
        localMotionEventArgs1 = new MotionEventArgs();
      }
      localMotionEventArgs1.m_MotionEvent = paramMotionEvent;
      localMotionEventArgs1.m_Action = paramMotionEvent.getAction();
      localMotionEventArgs1.m_PointerCount = paramMotionEvent.getPointerCount();
      localMotionEventArgs1.m_X = paramMotionEvent.getX();
      localMotionEventArgs1.m_Y = paramMotionEvent.getY();
      localMotionEventArgs1.m_IsFreeInstance = false;
      return localMotionEventArgs1;
    }
    finally {}
  }
  
  public final int getAction()
  {
    return this.m_Action;
  }
  
  public final MotionEvent getMotionEvent()
  {
    return this.m_MotionEvent;
  }
  
  public final int getPointerCount()
  {
    return this.m_PointerCount;
  }
  
  public final float getX()
  {
    return this.m_X;
  }
  
  public final float getY()
  {
    return this.m_Y;
  }
  
  public void recycle()
  {
    try
    {
      boolean bool = this.m_IsFreeInstance;
      if (bool) {
        return;
      }
      this.m_MotionEvent = null;
      this.m_IsFreeInstance = true;
      clearHandledState();
      if (POOL.size() < 8) {
        POOL.addLast(this);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/MotionEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */