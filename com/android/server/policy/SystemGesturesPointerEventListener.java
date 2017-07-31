package com.android.server.policy;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManagerPolicy.PointerEventListener;
import android.widget.OverScroller;

public class SystemGesturesPointerEventListener
  implements WindowManagerPolicy.PointerEventListener
{
  private static final boolean DEBUG = false;
  private static final int MAX_FLING_TIME_MILLIS = 5000;
  private static final int MAX_TRACKED_POINTERS = 32;
  private static final int SWIPE_FROM_BOTTOM = 2;
  private static final int SWIPE_FROM_LEFT = 4;
  private static final int SWIPE_FROM_RIGHT = 3;
  private static final int SWIPE_FROM_TOP = 1;
  private static final int SWIPE_NONE = 0;
  private static final long SWIPE_TIMEOUT_MS = 500L;
  private static final String TAG = "SystemGestures";
  private static final int UNTRACKED_POINTER = -1;
  private final Callbacks mCallbacks;
  private final Context mContext;
  private boolean mDebugFireable;
  private final int[] mDownPointerId = new int[32];
  private int mDownPointers;
  private final long[] mDownTime = new long[32];
  private final float[] mDownX = new float[32];
  private final float[] mDownY = new float[32];
  private GestureDetector mGestureDetector;
  private long mLastFlingTime;
  private boolean mMouseHoveringAtEdge;
  private OverScroller mOverscroller;
  private final int mSwipeDistanceThreshold;
  private boolean mSwipeFireable;
  private final int mSwipeStartThreshold;
  int screenHeight;
  int screenWidth;
  
  public SystemGesturesPointerEventListener(Context paramContext, Callbacks paramCallbacks)
  {
    this.mContext = paramContext;
    this.mCallbacks = ((Callbacks)checkNull("callbacks", paramCallbacks));
    this.mSwipeStartThreshold = ((Context)checkNull("context", paramContext)).getResources().getDimensionPixelSize(17104921);
    this.mSwipeDistanceThreshold = this.mSwipeStartThreshold;
  }
  
  private void captureDown(MotionEvent paramMotionEvent, int paramInt)
  {
    int i = findIndex(paramMotionEvent.getPointerId(paramInt));
    if (i != -1)
    {
      this.mDownX[i] = paramMotionEvent.getX(paramInt);
      this.mDownY[i] = paramMotionEvent.getY(paramInt);
      this.mDownTime[i] = paramMotionEvent.getEventTime();
    }
  }
  
  private static <T> T checkNull(String paramString, T paramT)
  {
    if (paramT == null) {
      throw new IllegalArgumentException(paramString + " must not be null");
    }
    return paramT;
  }
  
  private int detectSwipe(int paramInt, long paramLong, float paramFloat1, float paramFloat2)
  {
    float f1 = this.mDownX[paramInt];
    float f2 = this.mDownY[paramInt];
    paramLong -= this.mDownTime[paramInt];
    if ((f2 <= this.mSwipeStartThreshold) && (paramFloat2 > this.mSwipeDistanceThreshold + f2) && (paramLong < 500L)) {
      return 1;
    }
    if ((f2 >= this.screenHeight - this.mSwipeStartThreshold) && (paramFloat2 < f2 - this.mSwipeDistanceThreshold) && (paramLong < 500L)) {
      return 2;
    }
    if ((f1 >= this.screenWidth - this.mSwipeStartThreshold) && (paramFloat1 < f1 - this.mSwipeDistanceThreshold) && (paramLong < 500L)) {
      return 3;
    }
    if ((f1 <= this.mSwipeStartThreshold) && (paramFloat1 > this.mSwipeDistanceThreshold + f1) && (paramLong < 500L)) {
      return 4;
    }
    return 0;
  }
  
  private int detectSwipe(MotionEvent paramMotionEvent)
  {
    int k = paramMotionEvent.getHistorySize();
    int m = paramMotionEvent.getPointerCount();
    int i = 0;
    while (i < m)
    {
      int n = findIndex(paramMotionEvent.getPointerId(i));
      if (n != -1)
      {
        int j = 0;
        while (j < k)
        {
          int i1 = detectSwipe(n, paramMotionEvent.getHistoricalEventTime(j), paramMotionEvent.getHistoricalX(i, j), paramMotionEvent.getHistoricalY(i, j));
          if (i1 != 0) {
            return i1;
          }
          j += 1;
        }
        j = detectSwipe(n, paramMotionEvent.getEventTime(), paramMotionEvent.getX(i), paramMotionEvent.getY(i));
        if (j != 0) {
          return j;
        }
      }
      i += 1;
    }
    return 0;
  }
  
  private int findIndex(int paramInt)
  {
    int i = 0;
    while (i < this.mDownPointers)
    {
      if (this.mDownPointerId[i] == paramInt) {
        return i;
      }
      i += 1;
    }
    if ((this.mDownPointers == 32) || (paramInt == -1)) {
      return -1;
    }
    int[] arrayOfInt = this.mDownPointerId;
    i = this.mDownPointers;
    this.mDownPointers = (i + 1);
    arrayOfInt[i] = paramInt;
    return this.mDownPointers - 1;
  }
  
  public void onPointerEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if ((this.mGestureDetector != null) && (paramMotionEvent.isTouchEvent())) {
      this.mGestureDetector.onTouchEvent(paramMotionEvent);
    }
    switch (paramMotionEvent.getActionMasked())
    {
    case 4: 
    case 6: 
    default: 
    case 0: 
    case 5: 
    case 2: 
    case 7: 
      do
      {
        do
        {
          int i;
          do
          {
            do
            {
              do
              {
                return;
                this.mSwipeFireable = true;
                this.mDebugFireable = true;
                this.mDownPointers = 0;
                captureDown(paramMotionEvent, 0);
                if (this.mMouseHoveringAtEdge)
                {
                  this.mMouseHoveringAtEdge = false;
                  this.mCallbacks.onMouseLeaveFromEdge();
                }
                this.mCallbacks.onDown();
                return;
                captureDown(paramMotionEvent, paramMotionEvent.getActionIndex());
              } while (!this.mDebugFireable);
              if (paramMotionEvent.getPointerCount() < 5) {}
              for (;;)
              {
                this.mDebugFireable = bool1;
                if (this.mDebugFireable) {
                  break;
                }
                this.mCallbacks.onDebug();
                return;
                bool1 = false;
              }
            } while (!this.mSwipeFireable);
            i = detectSwipe(paramMotionEvent);
            bool1 = bool2;
            if (i == 0) {
              bool1 = true;
            }
            this.mSwipeFireable = bool1;
            if (i == 1)
            {
              this.mCallbacks.onSwipeFromTop();
              return;
            }
            if (i == 2)
            {
              this.mCallbacks.onSwipeFromBottom();
              return;
            }
            if (i == 3)
            {
              this.mCallbacks.onSwipeFromRight();
              return;
            }
          } while (i != 4);
          this.mCallbacks.onSwipeFromLeft();
          return;
        } while (!paramMotionEvent.isFromSource(8194));
        if ((!this.mMouseHoveringAtEdge) && (paramMotionEvent.getY() == 0.0F))
        {
          this.mCallbacks.onMouseHoverAtTop();
          this.mMouseHoveringAtEdge = true;
          return;
        }
        if ((!this.mMouseHoveringAtEdge) && (paramMotionEvent.getY() >= this.screenHeight - 1))
        {
          this.mCallbacks.onMouseHoverAtBottom();
          this.mMouseHoveringAtEdge = true;
          return;
        }
      } while ((!this.mMouseHoveringAtEdge) || (paramMotionEvent.getY() <= 0.0F) || (paramMotionEvent.getY() >= this.screenHeight - 1));
      this.mCallbacks.onMouseLeaveFromEdge();
      this.mMouseHoveringAtEdge = false;
      return;
    }
    this.mSwipeFireable = false;
    this.mDebugFireable = false;
    this.mCallbacks.onUpOrCancel();
  }
  
  public void systemReady()
  {
    Handler localHandler = new Handler(Looper.myLooper());
    this.mGestureDetector = new GestureDetector(this.mContext, new FlingGestureDetector(null), localHandler);
    this.mOverscroller = new OverScroller(this.mContext);
  }
  
  static abstract interface Callbacks
  {
    public abstract void onDebug();
    
    public abstract void onDown();
    
    public abstract void onFling(int paramInt);
    
    public abstract void onMouseHoverAtBottom();
    
    public abstract void onMouseHoverAtTop();
    
    public abstract void onMouseLeaveFromEdge();
    
    public abstract void onSwipeFromBottom();
    
    public abstract void onSwipeFromLeft();
    
    public abstract void onSwipeFromRight();
    
    public abstract void onSwipeFromTop();
    
    public abstract void onUpOrCancel();
  }
  
  private final class FlingGestureDetector
    extends GestureDetector.SimpleOnGestureListener
  {
    private FlingGestureDetector() {}
    
    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).computeScrollOffset();
      long l = SystemClock.uptimeMillis();
      if ((SystemGesturesPointerEventListener.-get1(SystemGesturesPointerEventListener.this) != 0L) && (l > SystemGesturesPointerEventListener.-get1(SystemGesturesPointerEventListener.this) + 5000L)) {
        SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).forceFinished(true);
      }
      SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).fling(0, 0, (int)paramFloat1, (int)paramFloat2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      int j = SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).getDuration();
      int i = j;
      if (j > 5000) {
        i = 5000;
      }
      SystemGesturesPointerEventListener.-set0(SystemGesturesPointerEventListener.this, l);
      SystemGesturesPointerEventListener.-get0(SystemGesturesPointerEventListener.this).onFling(i);
      return true;
    }
    
    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      if (!SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).isFinished()) {
        SystemGesturesPointerEventListener.-get2(SystemGesturesPointerEventListener.this).forceFinished(true);
      }
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/SystemGesturesPointerEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */