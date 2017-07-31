package com.android.server.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.util.Slog;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import java.util.ArrayList;

class AccessibilityGestureDetector
  extends GestureDetector.SimpleOnGestureListener
{
  private static final long CANCEL_ON_PAUSE_THRESHOLD_NOT_STARTED_MS = 200L;
  private static final long CANCEL_ON_PAUSE_THRESHOLD_STARTED_MS = 500L;
  private static final boolean DEBUG = false;
  private static final int GESTURE_CONFIRM_MM = 10;
  private static final String LOG_TAG = "AccessibilityGestureDetector";
  private static final float MIN_PREDICTION_SCORE = 2.0F;
  private static final int TOUCH_TOLERANCE = 3;
  private long mBaseTime;
  private float mBaseX;
  private float mBaseY;
  private boolean mDoubleTapDetected;
  private boolean mFirstTapDetected;
  private final float mGestureDetectionThreshold;
  private final GestureDetector mGestureDetector;
  private final GestureLibrary mGestureLibrary;
  private boolean mGestureStarted;
  private final Listener mListener;
  private int mPolicyFlags;
  private float mPreviousGestureX;
  private float mPreviousGestureY;
  private boolean mRecognizingGesture;
  private boolean mSecondFingerDoubleTap;
  private long mSecondPointerDownTime;
  private final ArrayList<GesturePoint> mStrokeBuffer = new ArrayList(100);
  
  AccessibilityGestureDetector(Context paramContext, Listener paramListener)
  {
    this.mListener = paramListener;
    this.mGestureDetector = new GestureDetector(paramContext, this);
    this.mGestureDetector.setOnDoubleTapListener(this);
    this.mGestureLibrary = GestureLibraries.fromRawResource(paramContext, 17825794);
    this.mGestureLibrary.setOrientationStyle(8);
    this.mGestureLibrary.setSequenceType(2);
    this.mGestureLibrary.load();
    this.mGestureDetectionThreshold = (TypedValue.applyDimension(5, 1.0F, paramContext.getResources().getDisplayMetrics()) * 10.0F);
  }
  
  private void cancelGesture()
  {
    this.mRecognizingGesture = false;
    this.mGestureStarted = false;
    this.mStrokeBuffer.clear();
  }
  
  private boolean finishDoubleTap(MotionEvent paramMotionEvent, int paramInt)
  {
    clear();
    return this.mListener.onDoubleTap(paramMotionEvent, paramInt);
  }
  
  private MotionEvent mapSecondPointerToFirstPointer(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getPointerCount() != 2) || ((paramMotionEvent.getActionMasked() != 5) && (paramMotionEvent.getActionMasked() != 6) && (paramMotionEvent.getActionMasked() != 2))) {
      return null;
    }
    int j = paramMotionEvent.getActionMasked();
    int i;
    if (j == 5) {
      i = 0;
    }
    for (;;)
    {
      return MotionEvent.obtain(this.mSecondPointerDownTime, paramMotionEvent.getEventTime(), i, paramMotionEvent.getX(1), paramMotionEvent.getY(1), paramMotionEvent.getPressure(1), paramMotionEvent.getSize(1), paramMotionEvent.getMetaState(), paramMotionEvent.getXPrecision(), paramMotionEvent.getYPrecision(), paramMotionEvent.getDeviceId(), paramMotionEvent.getEdgeFlags());
      i = j;
      if (j == 6) {
        i = 1;
      }
    }
  }
  
  private void maybeSendLongPress(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!this.mDoubleTapDetected) {
      return;
    }
    clear();
    this.mListener.onDoubleTapAndHold(paramMotionEvent, paramInt);
  }
  
  private boolean recognizeGesture(MotionEvent paramMotionEvent, int paramInt)
  {
    Object localObject = new Gesture();
    ((Gesture)localObject).addStroke(new GestureStroke(this.mStrokeBuffer));
    localObject = this.mGestureLibrary.recognize((Gesture)localObject);
    if (!((ArrayList)localObject).isEmpty())
    {
      localObject = (Prediction)((ArrayList)localObject).get(0);
      if (((Prediction)localObject).score >= 2.0D) {
        try
        {
          int i = Integer.parseInt(((Prediction)localObject).name);
          boolean bool = this.mListener.onGestureCompleted(i);
          return bool;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Slog.w("AccessibilityGestureDetector", "Non numeric gesture id:" + ((Prediction)localObject).name);
        }
      }
    }
    return this.mListener.onGestureCancelled(paramMotionEvent, paramInt);
  }
  
  public void clear()
  {
    this.mFirstTapDetected = false;
    this.mDoubleTapDetected = false;
    this.mSecondFingerDoubleTap = false;
    this.mGestureStarted = false;
    this.mGestureDetector.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0));
    cancelGesture();
  }
  
  public boolean firstTapDetected()
  {
    return this.mFirstTapDetected;
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    this.mDoubleTapDetected = true;
    return false;
  }
  
  public void onLongPress(MotionEvent paramMotionEvent)
  {
    maybeSendLongPress(paramMotionEvent, this.mPolicyFlags);
  }
  
  public boolean onMotionEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    long l2 = paramMotionEvent.getEventTime();
    this.mPolicyFlags = paramInt;
    switch (paramMotionEvent.getActionMasked())
    {
    }
    while (this.mSecondFingerDoubleTap)
    {
      paramMotionEvent = mapSecondPointerToFirstPointer(paramMotionEvent);
      if (paramMotionEvent == null)
      {
        return false;
        this.mDoubleTapDetected = false;
        this.mSecondFingerDoubleTap = false;
        this.mRecognizingGesture = true;
        this.mGestureStarted = false;
        this.mPreviousGestureX = f1;
        this.mPreviousGestureY = f2;
        this.mStrokeBuffer.clear();
        this.mStrokeBuffer.add(new GesturePoint(f1, f2, l2));
        this.mBaseX = f1;
        this.mBaseY = f2;
        this.mBaseTime = l2;
        continue;
        if (this.mRecognizingGesture)
        {
          float f3 = this.mBaseX;
          float f4 = this.mBaseY;
          if (Math.hypot(f3 - f1, f4 - f2) > this.mGestureDetectionThreshold)
          {
            this.mBaseX = f1;
            this.mBaseY = f2;
            this.mBaseTime = l2;
            this.mFirstTapDetected = false;
            this.mDoubleTapDetected = false;
            if (!this.mGestureStarted)
            {
              this.mGestureStarted = true;
              return this.mListener.onGestureStarted();
            }
          }
          else if (!this.mFirstTapDetected)
          {
            long l3 = this.mBaseTime;
            if (this.mGestureStarted) {}
            for (long l1 = 500L; l2 - l3 > l1; l1 = 200L)
            {
              cancelGesture();
              return this.mListener.onGestureCancelled(paramMotionEvent, paramInt);
            }
          }
          f3 = Math.abs(f1 - this.mPreviousGestureX);
          f4 = Math.abs(f2 - this.mPreviousGestureY);
          if ((f3 >= 3.0F) || (f4 >= 3.0F))
          {
            this.mPreviousGestureX = f1;
            this.mPreviousGestureY = f2;
            this.mStrokeBuffer.add(new GesturePoint(f1, f2, l2));
            continue;
            if (this.mDoubleTapDetected) {
              return finishDoubleTap(paramMotionEvent, paramInt);
            }
            if (this.mGestureStarted)
            {
              this.mStrokeBuffer.add(new GesturePoint(f1, f2, l2));
              return recognizeGesture(paramMotionEvent, paramInt);
              cancelGesture();
              if (paramMotionEvent.getPointerCount() == 2)
              {
                this.mSecondFingerDoubleTap = true;
                this.mSecondPointerDownTime = l2;
              }
              else
              {
                this.mSecondFingerDoubleTap = false;
                continue;
                if ((this.mSecondFingerDoubleTap) && (this.mDoubleTapDetected))
                {
                  return finishDoubleTap(paramMotionEvent, paramInt);
                  clear();
                }
              }
            }
          }
        }
      }
      else
      {
        boolean bool = this.mGestureDetector.onTouchEvent(paramMotionEvent);
        paramMotionEvent.recycle();
        return bool;
      }
    }
    if (!this.mRecognizingGesture) {
      return false;
    }
    return this.mGestureDetector.onTouchEvent(paramMotionEvent);
  }
  
  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    clear();
    return false;
  }
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    this.mFirstTapDetected = true;
    return false;
  }
  
  public static abstract interface Listener
  {
    public abstract boolean onDoubleTap(MotionEvent paramMotionEvent, int paramInt);
    
    public abstract void onDoubleTapAndHold(MotionEvent paramMotionEvent, int paramInt);
    
    public abstract boolean onGestureCancelled(MotionEvent paramMotionEvent, int paramInt);
    
    public abstract boolean onGestureCompleted(int paramInt);
    
    public abstract boolean onGestureStarted();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/AccessibilityGestureDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */