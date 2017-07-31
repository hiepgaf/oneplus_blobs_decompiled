package android.support.v4.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class GestureDetectorCompat
{
  private final GestureDetectorCompatImpl mImpl;
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener)
  {
    this(paramContext, paramOnGestureListener, null);
  }
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
  {
    if (Build.VERSION.SDK_INT <= 17)
    {
      this.mImpl = new GestureDetectorCompatImplBase(paramContext, paramOnGestureListener, paramHandler);
      return;
    }
    this.mImpl = new GestureDetectorCompatImplJellybeanMr2(paramContext, paramOnGestureListener, paramHandler);
  }
  
  public boolean isLongpressEnabled()
  {
    return this.mImpl.isLongpressEnabled();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return this.mImpl.onTouchEvent(paramMotionEvent);
  }
  
  public void setIsLongpressEnabled(boolean paramBoolean)
  {
    this.mImpl.setIsLongpressEnabled(paramBoolean);
  }
  
  public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener)
  {
    this.mImpl.setOnDoubleTapListener(paramOnDoubleTapListener);
  }
  
  static abstract interface GestureDetectorCompatImpl
  {
    public abstract boolean isLongpressEnabled();
    
    public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);
    
    public abstract void setIsLongpressEnabled(boolean paramBoolean);
    
    public abstract void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener);
  }
  
  static class GestureDetectorCompatImplBase
    implements GestureDetectorCompat.GestureDetectorCompatImpl
  {
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int LONGPRESS_TIMEOUT = ;
    private static final int LONG_PRESS = 2;
    private static final int SHOW_PRESS = 1;
    private static final int TAP = 3;
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    private MotionEvent mCurrentDownEvent;
    private boolean mDeferConfirmSingleTap;
    private GestureDetector.OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private float mDownFocusX;
    private float mDownFocusY;
    private final Handler mHandler;
    private boolean mInLongPress;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private float mLastFocusX;
    private float mLastFocusY;
    private final GestureDetector.OnGestureListener mListener;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private MotionEvent mPreviousUpEvent;
    private boolean mStillDown;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;
    
    public GestureDetectorCompatImplBase(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
    {
      if (paramHandler == null)
      {
        this.mHandler = new GestureHandler();
        this.mListener = paramOnGestureListener;
        if ((paramOnGestureListener instanceof GestureDetector.OnDoubleTapListener)) {
          break label54;
        }
      }
      for (;;)
      {
        init(paramContext);
        return;
        this.mHandler = new GestureHandler(paramHandler);
        break;
        label54:
        setOnDoubleTapListener((GestureDetector.OnDoubleTapListener)paramOnGestureListener);
      }
    }
    
    private void cancel()
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      this.mIsDoubleTapping = false;
      this.mStillDown = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (!this.mInLongPress) {
        return;
      }
      this.mInLongPress = false;
    }
    
    private void cancelTaps()
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mIsDoubleTapping = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (!this.mInLongPress) {
        return;
      }
      this.mInLongPress = false;
    }
    
    private void dispatchLongPress()
    {
      this.mHandler.removeMessages(3);
      this.mDeferConfirmSingleTap = false;
      this.mInLongPress = true;
      this.mListener.onLongPress(this.mCurrentDownEvent);
    }
    
    private void init(Context paramContext)
    {
      if (paramContext != null)
      {
        if (this.mListener != null)
        {
          this.mIsLongpressEnabled = true;
          paramContext = ViewConfiguration.get(paramContext);
          int i = paramContext.getScaledTouchSlop();
          int j = paramContext.getScaledDoubleTapSlop();
          this.mMinimumFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
          this.mMaximumFlingVelocity = paramContext.getScaledMaximumFlingVelocity();
          this.mTouchSlopSquare = (i * i);
          this.mDoubleTapSlopSquare = (j * j);
        }
      }
      else {
        throw new IllegalArgumentException("Context must not be null");
      }
      throw new IllegalArgumentException("OnGestureListener must not be null");
    }
    
    private boolean isConsideredDoubleTap(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, MotionEvent paramMotionEvent3)
    {
      if (this.mAlwaysInBiggerTapRegion) {
        if (paramMotionEvent3.getEventTime() - paramMotionEvent2.getEventTime() > DOUBLE_TAP_TIMEOUT) {
          break label36;
        }
      }
      label36:
      for (int i = 1; i == 0; i = 0)
      {
        return false;
        return false;
      }
      i = (int)paramMotionEvent1.getX() - (int)paramMotionEvent3.getX();
      int j = (int)paramMotionEvent1.getY() - (int)paramMotionEvent3.getY();
      return i * i + j * j < this.mDoubleTapSlopSquare;
    }
    
    public boolean isLongpressEnabled()
    {
      return this.mIsLongpressEnabled;
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool2 = true;
      int i1 = paramMotionEvent.getAction();
      int i;
      label38:
      int k;
      label46:
      float f2;
      float f1;
      int n;
      int m;
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.addMovement(paramMotionEvent);
        if ((i1 & 0xFF) == 6) {
          break label148;
        }
        i = 0;
        if (i != 0) {
          break label154;
        }
        k = -1;
        f2 = 0.0F;
        f1 = 0.0F;
        n = MotionEventCompat.getPointerCount(paramMotionEvent);
        m = 0;
        label59:
        if (m < n) {
          break label163;
        }
        if (i != 0) {
          break label211;
        }
        i = n;
        label75:
        f2 /= i;
        f1 /= i;
      }
      label148:
      label154:
      label163:
      label211:
      label427:
      boolean bool3;
      label517:
      label554:
      label618:
      label628:
      label777:
      int j;
      switch (i1 & 0xFF)
      {
      case 4: 
      default: 
      case 5: 
      case 6: 
      case 0: 
      case 2: 
        do
        {
          return false;
          this.mVelocityTracker = VelocityTracker.obtain();
          break;
          i = 1;
          break label38;
          k = MotionEventCompat.getActionIndex(paramMotionEvent);
          break label46;
          f4 = f1;
          f3 = f2;
          if (k != m)
          {
            f3 = f2 + MotionEventCompat.getX(paramMotionEvent, m);
            f4 = f1 + MotionEventCompat.getY(paramMotionEvent, m);
          }
          m += 1;
          f1 = f4;
          f2 = f3;
          break label59;
          i = n - 1;
          break label75;
          this.mLastFocusX = f2;
          this.mDownFocusX = f2;
          this.mLastFocusY = f1;
          this.mDownFocusY = f1;
          cancelTaps();
          return false;
          this.mLastFocusX = f2;
          this.mDownFocusX = f2;
          this.mLastFocusY = f1;
          this.mDownFocusY = f1;
          this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
          k = MotionEventCompat.getActionIndex(paramMotionEvent);
          i = MotionEventCompat.getPointerId(paramMotionEvent, k);
          f1 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, i);
          f2 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, i);
          i = 0;
          while (i < n)
          {
            if (i != k)
            {
              m = MotionEventCompat.getPointerId(paramMotionEvent, i);
              f3 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, m);
              if (VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, m) * f2 + f3 * f1 < 0.0F)
              {
                this.mVelocityTracker.clear();
                return false;
              }
            }
            i += 1;
          }
          if (this.mDoubleTapListener == null)
          {
            i = 0;
            this.mLastFocusX = f2;
            this.mDownFocusX = f2;
            this.mLastFocusY = f1;
            this.mDownFocusY = f1;
            if (this.mCurrentDownEvent != null) {
              break label618;
            }
            this.mCurrentDownEvent = MotionEvent.obtain(paramMotionEvent);
            this.mAlwaysInTapRegion = true;
            this.mAlwaysInBiggerTapRegion = true;
            this.mStillDown = true;
            this.mInLongPress = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mIsLongpressEnabled) {
              break label628;
            }
          }
          for (;;)
          {
            this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + TAP_TIMEOUT);
            return this.mListener.onDown(paramMotionEvent) | i;
            bool3 = this.mHandler.hasMessages(3);
            if (!bool3) {
              if (this.mCurrentDownEvent != null) {
                break label554;
              }
            }
            while ((this.mPreviousUpEvent == null) || (!bool3) || (!isConsideredDoubleTap(this.mCurrentDownEvent, this.mPreviousUpEvent, paramMotionEvent)))
            {
              this.mHandler.sendEmptyMessageDelayed(3, DOUBLE_TAP_TIMEOUT);
              i = 0;
              break;
              this.mHandler.removeMessages(3);
              break label517;
            }
            this.mIsDoubleTapping = true;
            bool1 = this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | false | this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent);
            break;
            this.mCurrentDownEvent.recycle();
            break label427;
            this.mHandler.removeMessages(2);
            this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
          }
        } while (this.mInLongPress);
        float f3 = this.mLastFocusX - f2;
        float f4 = this.mLastFocusY - f1;
        if (!this.mIsDoubleTapping)
        {
          if (this.mAlwaysInTapRegion) {
            break label777;
          }
          if (Math.abs(f3) < 1.0F) {
            break label902;
          }
        }
        for (boolean bool1 = bool2; (bool1) || (Math.abs(f4) >= 1.0F); j = 0)
        {
          bool3 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f3, f4);
          this.mLastFocusX = f2;
          this.mLastFocusY = f1;
          return bool3;
          return this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent) | false;
          j = (int)(f2 - this.mDownFocusX);
          k = (int)(f1 - this.mDownFocusY);
          j = j * j + k * k;
          if (j <= this.mTouchSlopSquare)
          {
            bool3 = false;
            if (j > this.mTouchSlopSquare) {
              break label894;
            }
          }
          for (;;)
          {
            return bool3;
            bool3 = this.mListener.onScroll(this.mCurrentDownEvent, paramMotionEvent, f3, f4);
            this.mLastFocusX = f2;
            this.mLastFocusY = f1;
            this.mAlwaysInTapRegion = false;
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            break;
            this.mAlwaysInBiggerTapRegion = false;
          }
        }
      case 1: 
        label894:
        label902:
        this.mStillDown = false;
        MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
        if (!this.mIsDoubleTapping)
        {
          if (this.mInLongPress) {
            break label1100;
          }
          if (this.mAlwaysInTapRegion) {
            break label1119;
          }
          VelocityTracker localVelocityTracker = this.mVelocityTracker;
          j = MotionEventCompat.getPointerId(paramMotionEvent, 0);
          localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
          f1 = VelocityTrackerCompat.getYVelocity(localVelocityTracker, j);
          f2 = VelocityTrackerCompat.getXVelocity(localVelocityTracker, j);
          if (Math.abs(f1) <= this.mMinimumFlingVelocity) {
            break label1171;
          }
          j = 1;
          if ((j == 0) && (Math.abs(f2) <= this.mMinimumFlingVelocity)) {
            break label1177;
          }
          bool3 = this.mListener.onFling(this.mCurrentDownEvent, paramMotionEvent, f2, f1);
          label1034:
          if (this.mPreviousUpEvent != null) {
            break label1183;
          }
          label1041:
          this.mPreviousUpEvent = localMotionEvent;
          if (this.mVelocityTracker != null) {
            break label1193;
          }
        }
        for (;;)
        {
          this.mIsDoubleTapping = false;
          this.mDeferConfirmSingleTap = false;
          this.mHandler.removeMessages(1);
          this.mHandler.removeMessages(2);
          return bool3;
          bool3 = this.mDoubleTapListener.onDoubleTapEvent(paramMotionEvent) | false;
          break label1034;
          label1100:
          this.mHandler.removeMessages(3);
          this.mInLongPress = false;
          bool3 = false;
          break label1034;
          label1119:
          boolean bool4 = this.mListener.onSingleTapUp(paramMotionEvent);
          bool3 = bool4;
          if (!this.mDeferConfirmSingleTap) {
            break label1034;
          }
          bool3 = bool4;
          if (this.mDoubleTapListener == null) {
            break label1034;
          }
          this.mDoubleTapListener.onSingleTapConfirmed(paramMotionEvent);
          bool3 = bool4;
          break label1034;
          label1171:
          j = 0;
          break;
          label1177:
          bool3 = false;
          break label1034;
          label1183:
          this.mPreviousUpEvent.recycle();
          break label1041;
          label1193:
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        }
      }
      cancel();
      return false;
    }
    
    public void setIsLongpressEnabled(boolean paramBoolean)
    {
      this.mIsLongpressEnabled = paramBoolean;
    }
    
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener)
    {
      this.mDoubleTapListener = paramOnDoubleTapListener;
    }
    
    private class GestureHandler
      extends Handler
    {
      GestureHandler() {}
      
      GestureHandler(Handler paramHandler)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          throw new RuntimeException("Unknown message " + paramMessage);
        case 1: 
          GestureDetectorCompat.GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
        }
        do
        {
          return;
          GestureDetectorCompat.GestureDetectorCompatImplBase.this.dispatchLongPress();
          return;
        } while (GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener == null);
        if (GestureDetectorCompat.GestureDetectorCompatImplBase.this.mStillDown)
        {
          GestureDetectorCompat.GestureDetectorCompatImplBase.access$502(GestureDetectorCompat.GestureDetectorCompatImplBase.this, true);
          return;
        }
        GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
      }
    }
  }
  
  static class GestureDetectorCompatImplJellybeanMr2
    implements GestureDetectorCompat.GestureDetectorCompatImpl
  {
    private final GestureDetector mDetector;
    
    public GestureDetectorCompatImplJellybeanMr2(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler)
    {
      this.mDetector = new GestureDetector(paramContext, paramOnGestureListener, paramHandler);
    }
    
    public boolean isLongpressEnabled()
    {
      return this.mDetector.isLongpressEnabled();
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return this.mDetector.onTouchEvent(paramMotionEvent);
    }
    
    public void setIsLongpressEnabled(boolean paramBoolean)
    {
      this.mDetector.setIsLongpressEnabled(paramBoolean);
    }
    
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener)
    {
      this.mDetector.setOnDoubleTapListener(paramOnDoubleTapListener);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/view/GestureDetectorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */