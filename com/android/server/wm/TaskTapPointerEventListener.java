package com.android.server.wm;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.Message;
import android.util.BoostFramework;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.WindowManagerPolicy.PointerEventListener;
import com.android.server.am.ActivityManagerService;

public class TaskTapPointerEventListener
  implements WindowManagerPolicy.PointerEventListener
{
  private final DisplayContent mDisplayContent;
  private GestureDetector mGestureDetector;
  private boolean mInGestureDetection;
  private final Region mNonResizeableRegion = new Region();
  private int mPointerIconType = 1;
  private final WindowManagerService mService;
  private final Rect mTmpRect = new Rect();
  private final Region mTouchExcludeRegion = new Region();
  private boolean mTwoFingerScrolling;
  
  public TaskTapPointerEventListener(WindowManagerService paramWindowManagerService, DisplayContent paramDisplayContent)
  {
    this.mService = paramWindowManagerService;
    this.mDisplayContent = paramDisplayContent;
  }
  
  private void doGestureDetection(MotionEvent paramMotionEvent)
  {
    boolean bool1 = true;
    if ((this.mGestureDetector == null) || (this.mNonResizeableRegion.isEmpty())) {
      return;
    }
    int i = paramMotionEvent.getAction() & 0xFF;
    int j = (int)paramMotionEvent.getX();
    int k = (int)paramMotionEvent.getY();
    boolean bool2 = this.mNonResizeableRegion.contains(j, k);
    if ((this.mInGestureDetection) || ((i == 0) && (bool2)))
    {
      if ((!bool2) || (i == 1) || (i == 6)) {
        break label121;
      }
      if (i == 3) {
        break label115;
      }
    }
    for (;;)
    {
      this.mInGestureDetection = bool1;
      if (!this.mInGestureDetection) {
        break;
      }
      this.mGestureDetector.onTouchEvent(paramMotionEvent);
      return;
      label115:
      bool1 = false;
      continue;
      label121:
      bool1 = false;
    }
    paramMotionEvent = paramMotionEvent.copy();
    paramMotionEvent.cancel();
    this.mGestureDetector.onTouchEvent(paramMotionEvent);
    stopTwoFingerScroll();
  }
  
  private void onTwoFingerScroll(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX(0);
    int j = (int)paramMotionEvent.getY(0);
    if (!this.mTwoFingerScrolling)
    {
      this.mTwoFingerScrolling = true;
      this.mService.mH.obtainMessage(44, i, j, this.mDisplayContent).sendToTarget();
    }
  }
  
  private void stopTwoFingerScroll()
  {
    if (this.mTwoFingerScrolling)
    {
      this.mTwoFingerScrolling = false;
      this.mService.mH.obtainMessage(40).sendToTarget();
    }
  }
  
  void init()
  {
    this.mGestureDetector = new GestureDetector(this.mService.mContext, new TwoFingerScrollListener(null), this.mService.mH);
  }
  
  public void onPointerEvent(MotionEvent paramMotionEvent)
  {
    doGestureDetection(paramMotionEvent);
    if (ActivityManagerService.sIsFreqAggrBoostSet)
    {
      ActivityManagerService.sFreqAggr_init.perfLockRelease();
      ActivityManagerService.sFreqAggr.perfLockRelease();
      ActivityManagerService.sIsFreqAggrBoostSet = false;
    }
    if (ActivityManagerService.sIsLaunchBoostv2_set)
    {
      ActivityManagerService.sPerfBoost_v2.perfLockRelease();
      ActivityManagerService.sIsLaunchBoostv2_set = false;
    }
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    case 3: 
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    default: 
    case 0: 
    case 2: 
    case 7: 
    case 10: 
      do
      {
        for (;;)
        {
          return;
          int i = (int)paramMotionEvent.getX();
          int j = (int)paramMotionEvent.getY();
          try
          {
            if (!this.mTouchExcludeRegion.contains(i, j)) {
              this.mService.mH.obtainMessage(31, i, j, this.mDisplayContent).sendToTarget();
            }
            return;
          }
          finally
          {
            paramMotionEvent = finally;
            throw paramMotionEvent;
          }
          if (paramMotionEvent.getPointerCount() != 2)
          {
            stopTwoFingerScroll();
            return;
            int k = (int)paramMotionEvent.getX();
            j = (int)paramMotionEvent.getY();
            Task localTask = this.mDisplayContent.findTaskForControlPoint(k, j);
            paramMotionEvent = paramMotionEvent.getDevice();
            if ((localTask == null) || (paramMotionEvent == null))
            {
              this.mPointerIconType = 1;
              return;
            }
            localTask.getDimBounds(this.mTmpRect);
            if ((this.mTmpRect.isEmpty()) || (this.mTmpRect.contains(k, j)))
            {
              this.mPointerIconType = 1;
              return;
            }
            i = 1000;
            if (k < this.mTmpRect.left) {
              if (j < this.mTmpRect.top) {
                i = 1017;
              }
            }
            while (this.mPointerIconType != i)
            {
              this.mPointerIconType = i;
              paramMotionEvent.setPointerType(i);
              return;
              if (j > this.mTmpRect.bottom)
              {
                i = 1016;
              }
              else
              {
                i = 1014;
                continue;
                if (k > this.mTmpRect.right)
                {
                  if (j < this.mTmpRect.top) {
                    i = 1016;
                  } else if (j > this.mTmpRect.bottom) {
                    i = 1017;
                  } else {
                    i = 1014;
                  }
                }
                else if ((j < this.mTmpRect.top) || (j > this.mTmpRect.bottom)) {
                  i = 1015;
                }
              }
            }
          }
        }
        this.mPointerIconType = 1;
        paramMotionEvent = paramMotionEvent.getDevice();
      } while (paramMotionEvent == null);
      paramMotionEvent.setPointerType(1000);
      return;
    }
    stopTwoFingerScroll();
  }
  
  void setTouchExcludeRegion(Region paramRegion1, Region paramRegion2)
  {
    try
    {
      this.mTouchExcludeRegion.set(paramRegion1);
      this.mNonResizeableRegion.set(paramRegion2);
      return;
    }
    finally
    {
      paramRegion1 = finally;
      throw paramRegion1;
    }
  }
  
  private final class TwoFingerScrollListener
    extends GestureDetector.SimpleOnGestureListener
  {
    private TwoFingerScrollListener() {}
    
    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      if (paramMotionEvent2.getPointerCount() == 2)
      {
        TaskTapPointerEventListener.-wrap0(TaskTapPointerEventListener.this, paramMotionEvent2);
        return true;
      }
      TaskTapPointerEventListener.-wrap1(TaskTapPointerEventListener.this);
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/TaskTapPointerEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */