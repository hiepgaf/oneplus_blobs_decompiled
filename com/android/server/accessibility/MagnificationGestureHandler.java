package com.android.server.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.MathUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

class MagnificationGestureHandler
  implements EventStreamTransformation
{
  private static final boolean DEBUG_DETECTING = false;
  private static final boolean DEBUG_PANNING = false;
  private static final boolean DEBUG_STATE_TRANSITIONS = false;
  private static final String LOG_TAG = "MagnificationEventHandler";
  private static final float MAX_SCALE = 5.0F;
  private static final float MIN_SCALE = 2.0F;
  private static final int STATE_DELEGATING = 1;
  private static final int STATE_DETECTING = 2;
  private static final int STATE_MAGNIFIED_INTERACTION = 4;
  private static final int STATE_VIEWPORT_DRAGGING = 3;
  private int mCurrentState;
  private long mDelegatingStateDownTime;
  private final boolean mDetectControlGestures;
  private final DetectingStateHandler mDetectingStateHandler;
  private final MagnificationController mMagnificationController;
  private final MagnifiedContentInteractionStateHandler mMagnifiedContentInteractionStateHandler;
  private EventStreamTransformation mNext;
  private int mPreviousState;
  private final StateViewportDraggingHandler mStateViewportDraggingHandler;
  private MotionEvent.PointerCoords[] mTempPointerCoords;
  private MotionEvent.PointerProperties[] mTempPointerProperties;
  private boolean mTranslationEnabledBeforePan;
  
  public MagnificationGestureHandler(Context paramContext, AccessibilityManagerService paramAccessibilityManagerService, boolean paramBoolean)
  {
    this.mMagnificationController = paramAccessibilityManagerService.getMagnificationController();
    this.mDetectingStateHandler = new DetectingStateHandler(paramContext);
    this.mStateViewportDraggingHandler = new StateViewportDraggingHandler(null);
    this.mMagnifiedContentInteractionStateHandler = new MagnifiedContentInteractionStateHandler(paramContext);
    this.mDetectControlGestures = paramBoolean;
    transitionToState(2);
  }
  
  private void clear()
  {
    this.mCurrentState = 2;
    this.mDetectingStateHandler.clear();
    this.mStateViewportDraggingHandler.clear();
    this.mMagnifiedContentInteractionStateHandler.clear();
  }
  
  private void dispatchTransformedEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    float f1 = paramMotionEvent1.getX();
    float f2 = paramMotionEvent1.getY();
    Object localObject = paramMotionEvent1;
    if (this.mMagnificationController.isMagnifying())
    {
      localObject = paramMotionEvent1;
      if (this.mMagnificationController.magnificationRegionContains(f1, f2))
      {
        f1 = this.mMagnificationController.getScale();
        f2 = this.mMagnificationController.getOffsetX();
        float f3 = this.mMagnificationController.getOffsetY();
        int j = paramMotionEvent1.getPointerCount();
        localObject = getTempPointerCoordsWithMinSize(j);
        MotionEvent.PointerProperties[] arrayOfPointerProperties = getTempPointerPropertiesWithMinSize(j);
        int i = 0;
        while (i < j)
        {
          paramMotionEvent1.getPointerCoords(i, localObject[i]);
          localObject[i].x = ((localObject[i].x - f2) / f1);
          localObject[i].y = ((localObject[i].y - f3) / f1);
          paramMotionEvent1.getPointerProperties(i, arrayOfPointerProperties[i]);
          i += 1;
        }
        localObject = MotionEvent.obtain(paramMotionEvent1.getDownTime(), paramMotionEvent1.getEventTime(), paramMotionEvent1.getAction(), j, arrayOfPointerProperties, (MotionEvent.PointerCoords[])localObject, 0, 0, 1.0F, 1.0F, paramMotionEvent1.getDeviceId(), 0, paramMotionEvent1.getSource(), paramMotionEvent1.getFlags());
      }
    }
    this.mNext.onMotionEvent((MotionEvent)localObject, paramMotionEvent2, paramInt);
  }
  
  private MotionEvent.PointerCoords[] getTempPointerCoordsWithMinSize(int paramInt)
  {
    if (this.mTempPointerCoords != null) {}
    for (int i = this.mTempPointerCoords.length;; i = 0)
    {
      if (i < paramInt)
      {
        MotionEvent.PointerCoords[] arrayOfPointerCoords = this.mTempPointerCoords;
        this.mTempPointerCoords = new MotionEvent.PointerCoords[paramInt];
        if (arrayOfPointerCoords != null) {
          System.arraycopy(arrayOfPointerCoords, 0, this.mTempPointerCoords, 0, i);
        }
      }
      while (i < paramInt)
      {
        this.mTempPointerCoords[i] = new MotionEvent.PointerCoords();
        i += 1;
      }
    }
    return this.mTempPointerCoords;
  }
  
  private MotionEvent.PointerProperties[] getTempPointerPropertiesWithMinSize(int paramInt)
  {
    if (this.mTempPointerProperties != null) {}
    for (int i = this.mTempPointerProperties.length;; i = 0)
    {
      if (i < paramInt)
      {
        MotionEvent.PointerProperties[] arrayOfPointerProperties = this.mTempPointerProperties;
        this.mTempPointerProperties = new MotionEvent.PointerProperties[paramInt];
        if (arrayOfPointerProperties != null) {
          System.arraycopy(arrayOfPointerProperties, 0, this.mTempPointerProperties, 0, i);
        }
      }
      while (i < paramInt)
      {
        this.mTempPointerProperties[i] = new MotionEvent.PointerProperties();
        i += 1;
      }
    }
    return this.mTempPointerProperties;
  }
  
  private void handleMotionEventStateDelegating(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    switch (paramMotionEvent1.getActionMasked())
    {
    }
    for (;;)
    {
      if (this.mNext != null)
      {
        paramMotionEvent1.setDownTime(this.mDelegatingStateDownTime);
        dispatchTransformedEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      }
      return;
      this.mDelegatingStateDownTime = paramMotionEvent1.getDownTime();
      continue;
      if (DetectingStateHandler.-get0(this.mDetectingStateHandler) == null) {
        transitionToState(2);
      }
    }
  }
  
  private void transitionToState(int paramInt)
  {
    this.mPreviousState = this.mCurrentState;
    this.mCurrentState = paramInt;
  }
  
  public void clearEvents(int paramInt)
  {
    if (paramInt == 4098) {
      clear();
    }
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
  
  public void onDestroy()
  {
    clear();
  }
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    if (this.mNext != null) {
      this.mNext.onKeyEvent(paramKeyEvent, paramInt);
    }
  }
  
  public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    if (!paramMotionEvent1.isFromSource(4098))
    {
      if (this.mNext != null) {
        this.mNext.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      }
      return;
    }
    if (!this.mDetectControlGestures)
    {
      if (this.mNext != null) {
        dispatchTransformedEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      }
      return;
    }
    this.mMagnifiedContentInteractionStateHandler.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
    switch (this.mCurrentState)
    {
    default: 
      throw new IllegalStateException("Unknown state: " + this.mCurrentState);
    case 1: 
      handleMotionEventStateDelegating(paramMotionEvent1, paramMotionEvent2, paramInt);
    case 4: 
      return;
    case 2: 
      this.mDetectingStateHandler.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      return;
    }
    this.mStateViewportDraggingHandler.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation)
  {
    this.mNext = paramEventStreamTransformation;
  }
  
  private final class DetectingStateHandler
    implements MagnificationGestureHandler.MotionEventHandler
  {
    private static final int ACTION_TAP_COUNT = 3;
    private static final int MESSAGE_ON_ACTION_TAP_AND_HOLD = 1;
    private static final int MESSAGE_TRANSITION_TO_DELEGATING_STATE = 2;
    private MagnificationGestureHandler.MotionEventInfo mDelayedEventQueue;
    private final Handler mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        int i = paramAnonymousMessage.what;
        switch (i)
        {
        default: 
          throw new IllegalArgumentException("Unknown message type: " + i);
        case 1: 
          MotionEvent localMotionEvent = (MotionEvent)paramAnonymousMessage.obj;
          i = paramAnonymousMessage.arg1;
          MagnificationGestureHandler.DetectingStateHandler.-wrap0(MagnificationGestureHandler.DetectingStateHandler.this, localMotionEvent, i);
          return;
        }
        MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 1);
        MagnificationGestureHandler.DetectingStateHandler.-wrap1(MagnificationGestureHandler.DetectingStateHandler.this);
        MagnificationGestureHandler.DetectingStateHandler.this.clear();
      }
    };
    private MotionEvent mLastDownEvent;
    private MotionEvent mLastTapUpEvent;
    private final int mMultiTapDistanceSlop;
    private final int mMultiTapTimeSlop;
    private int mTapCount;
    private final int mTapDistanceSlop;
    private final int mTapTimeSlop = ViewConfiguration.getJumpTapTimeout();
    
    public DetectingStateHandler(Context paramContext)
    {
      this.mMultiTapTimeSlop = (ViewConfiguration.getDoubleTapTimeout() + paramContext.getResources().getInteger(17694875));
      this.mTapDistanceSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
      this.mMultiTapDistanceSlop = ViewConfiguration.get(paramContext).getScaledDoubleTapSlop();
    }
    
    private void cacheDelayedMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      paramMotionEvent2 = MagnificationGestureHandler.MotionEventInfo.obtain(paramMotionEvent1, paramMotionEvent2, paramInt);
      if (this.mDelayedEventQueue == null)
      {
        this.mDelayedEventQueue = paramMotionEvent2;
        return;
      }
      for (paramMotionEvent1 = this.mDelayedEventQueue; MagnificationGestureHandler.MotionEventInfo.-get0(paramMotionEvent1) != null; paramMotionEvent1 = MagnificationGestureHandler.MotionEventInfo.-get0(paramMotionEvent1)) {}
      MagnificationGestureHandler.MotionEventInfo.-set0(paramMotionEvent1, paramMotionEvent2);
    }
    
    private void clearDelayedMotionEvents()
    {
      while (this.mDelayedEventQueue != null)
      {
        MagnificationGestureHandler.MotionEventInfo localMotionEventInfo = this.mDelayedEventQueue;
        this.mDelayedEventQueue = MagnificationGestureHandler.MotionEventInfo.-get0(localMotionEventInfo);
        localMotionEventInfo.recycle();
      }
    }
    
    private void clearLastDownEvent()
    {
      if (this.mLastDownEvent != null)
      {
        this.mLastDownEvent.recycle();
        this.mLastDownEvent = null;
      }
    }
    
    private void clearLastTapUpEvent()
    {
      if (this.mLastTapUpEvent != null)
      {
        this.mLastTapUpEvent.recycle();
        this.mLastTapUpEvent = null;
      }
    }
    
    private void clearTapDetectionState()
    {
      this.mTapCount = 0;
      clearLastTapUpEvent();
      clearLastDownEvent();
    }
    
    private void onActionTap(MotionEvent paramMotionEvent, int paramInt)
    {
      if (!MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).isMagnifying())
      {
        float f = MathUtils.constrain(MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).getPersistedScale(), 2.0F, 5.0F);
        MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).setScaleAndCenter(f, paramMotionEvent.getX(), paramMotionEvent.getY(), true, 0);
        return;
      }
      MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).reset(true);
    }
    
    private void onActionTapAndHold(MotionEvent paramMotionEvent, int paramInt)
    {
      clear();
      MagnificationGestureHandler.-set0(MagnificationGestureHandler.this, MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).isMagnifying());
      float f = MathUtils.constrain(MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).getPersistedScale(), 2.0F, 5.0F);
      MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).setScaleAndCenter(f, paramMotionEvent.getX(), paramMotionEvent.getY(), true, 0);
      MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 3);
    }
    
    private void sendDelayedMotionEvents()
    {
      while (this.mDelayedEventQueue != null)
      {
        MagnificationGestureHandler.MotionEventInfo localMotionEventInfo = this.mDelayedEventQueue;
        this.mDelayedEventQueue = MagnificationGestureHandler.MotionEventInfo.-get0(localMotionEventInfo);
        MagnificationGestureHandler.this.onMotionEvent(localMotionEventInfo.mEvent, localMotionEventInfo.mRawEvent, localMotionEventInfo.mPolicyFlags);
        localMotionEventInfo.recycle();
      }
    }
    
    private void transitionToDelegatingStateAndClear()
    {
      MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 1);
      sendDelayedMotionEvents();
      clear();
    }
    
    public void clear()
    {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      clearTapDetectionState();
      clearDelayedMotionEvents();
    }
    
    public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      cacheDelayedMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      switch (paramMotionEvent1.getActionMasked())
      {
      case 3: 
      case 4: 
      case 6: 
      default: 
      case 0: 
      case 5: 
      case 2: 
        do
        {
          return;
          this.mHandler.removeMessages(2);
          if (!MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).magnificationRegionContains(paramMotionEvent1.getX(), paramMotionEvent1.getY()))
          {
            transitionToDelegatingStateAndClear();
            return;
          }
          if ((this.mTapCount == 2) && (this.mLastDownEvent != null) && (GestureUtils.isMultiTap(this.mLastDownEvent, paramMotionEvent1, this.mMultiTapTimeSlop, this.mMultiTapDistanceSlop, 0)))
          {
            paramMotionEvent2 = this.mHandler.obtainMessage(1, paramInt, 0, paramMotionEvent1);
            this.mHandler.sendMessageDelayed(paramMotionEvent2, ViewConfiguration.getLongPressTimeout());
          }
          for (;;)
          {
            clearLastDownEvent();
            this.mLastDownEvent = MotionEvent.obtain(paramMotionEvent1);
            return;
            if (this.mTapCount < 3)
            {
              paramMotionEvent2 = this.mHandler.obtainMessage(2);
              this.mHandler.sendMessageDelayed(paramMotionEvent2, this.mMultiTapTimeSlop);
            }
          }
          if (MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).isMagnifying())
          {
            MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 4);
            clear();
            return;
          }
          transitionToDelegatingStateAndClear();
          return;
        } while ((this.mLastDownEvent == null) || (this.mTapCount >= 2) || (Math.abs(GestureUtils.computeDistance(this.mLastDownEvent, paramMotionEvent1, 0)) <= this.mTapDistanceSlop));
        transitionToDelegatingStateAndClear();
        return;
      }
      if (this.mLastDownEvent == null) {
        return;
      }
      this.mHandler.removeMessages(1);
      if (!MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).magnificationRegionContains(paramMotionEvent1.getX(), paramMotionEvent1.getY()))
      {
        transitionToDelegatingStateAndClear();
        return;
      }
      if (!GestureUtils.isTap(this.mLastDownEvent, paramMotionEvent1, this.mTapTimeSlop, this.mTapDistanceSlop, 0))
      {
        transitionToDelegatingStateAndClear();
        return;
      }
      if ((this.mLastTapUpEvent == null) || (GestureUtils.isMultiTap(this.mLastTapUpEvent, paramMotionEvent1, this.mMultiTapTimeSlop, this.mMultiTapDistanceSlop, 0)))
      {
        this.mTapCount += 1;
        if (this.mTapCount == 3)
        {
          clear();
          onActionTap(paramMotionEvent1, paramInt);
        }
      }
      else
      {
        transitionToDelegatingStateAndClear();
        return;
      }
      clearLastTapUpEvent();
      this.mLastTapUpEvent = MotionEvent.obtain(paramMotionEvent1);
    }
  }
  
  private final class MagnifiedContentInteractionStateHandler
    extends GestureDetector.SimpleOnGestureListener
    implements ScaleGestureDetector.OnScaleGestureListener, MagnificationGestureHandler.MotionEventHandler
  {
    private final GestureDetector mGestureDetector;
    private float mInitialScaleFactor = -1.0F;
    private final ScaleGestureDetector mScaleGestureDetector;
    private boolean mScaling;
    private final float mScalingThreshold;
    
    public MagnifiedContentInteractionStateHandler(Context paramContext)
    {
      this$1 = new TypedValue();
      paramContext.getResources().getValue(17104919, MagnificationGestureHandler.this, false);
      this.mScalingThreshold = MagnificationGestureHandler.this.getFloat();
      this.mScaleGestureDetector = new ScaleGestureDetector(paramContext, this);
      this.mScaleGestureDetector.setQuickScaleEnabled(false);
      this.mGestureDetector = new GestureDetector(paramContext, this);
    }
    
    public void clear()
    {
      this.mInitialScaleFactor = -1.0F;
      this.mScaling = false;
    }
    
    public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      this.mScaleGestureDetector.onTouchEvent(paramMotionEvent1);
      this.mGestureDetector.onTouchEvent(paramMotionEvent1);
      if (MagnificationGestureHandler.-get0(MagnificationGestureHandler.this) != 4) {
        return;
      }
      if (paramMotionEvent1.getActionMasked() == 1)
      {
        clear();
        MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).persistScale();
        if (MagnificationGestureHandler.-get2(MagnificationGestureHandler.this) == 3) {
          MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 3);
        }
      }
      else
      {
        return;
      }
      MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 2);
    }
    
    public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
    {
      if (!this.mScaling)
      {
        if (this.mInitialScaleFactor < 0.0F) {
          this.mInitialScaleFactor = paramScaleGestureDetector.getScaleFactor();
        }
        while (Math.abs(paramScaleGestureDetector.getScaleFactor() - this.mInitialScaleFactor) <= this.mScalingThreshold) {
          return false;
        }
        this.mScaling = true;
        return true;
      }
      float f2 = MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).getScale();
      float f1 = f2 * paramScaleGestureDetector.getScaleFactor();
      if ((f1 > 5.0F) && (f1 > f2)) {
        f1 = 5.0F;
      }
      for (;;)
      {
        f2 = paramScaleGestureDetector.getFocusX();
        float f3 = paramScaleGestureDetector.getFocusY();
        MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).setScale(f1, f2, f3, false, 0);
        return true;
        if ((f1 < 2.0F) && (f1 < f2)) {
          f1 = 2.0F;
        }
      }
    }
    
    public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
    {
      return MagnificationGestureHandler.-get0(MagnificationGestureHandler.this) == 4;
    }
    
    public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
    {
      clear();
    }
    
    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
      if (MagnificationGestureHandler.-get0(MagnificationGestureHandler.this) != 4) {
        return true;
      }
      MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).offsetMagnifiedRegionCenter(paramFloat1, paramFloat2, 0);
      return true;
    }
  }
  
  private static abstract interface MotionEventHandler
  {
    public abstract void clear();
    
    public abstract void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt);
  }
  
  private static final class MotionEventInfo
  {
    private static final int MAX_POOL_SIZE = 10;
    private static final Object sLock = new Object();
    private static MotionEventInfo sPool;
    private static int sPoolSize;
    public MotionEvent mEvent;
    private boolean mInPool;
    private MotionEventInfo mNext;
    public int mPolicyFlags;
    public MotionEvent mRawEvent;
    
    private void clear()
    {
      this.mEvent.recycle();
      this.mEvent = null;
      this.mRawEvent.recycle();
      this.mRawEvent = null;
      this.mPolicyFlags = 0;
    }
    
    private void initialize(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      this.mEvent = MotionEvent.obtain(paramMotionEvent1);
      this.mRawEvent = MotionEvent.obtain(paramMotionEvent2);
      this.mPolicyFlags = paramInt;
    }
    
    public static MotionEventInfo obtain(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      synchronized (sLock)
      {
        if (sPoolSize > 0)
        {
          sPoolSize -= 1;
          localMotionEventInfo = sPool;
          sPool = localMotionEventInfo.mNext;
          localMotionEventInfo.mNext = null;
          localMotionEventInfo.mInPool = false;
          localMotionEventInfo.initialize(paramMotionEvent1, paramMotionEvent2, paramInt);
          return localMotionEventInfo;
        }
        MotionEventInfo localMotionEventInfo = new MotionEventInfo();
      }
    }
    
    public void recycle()
    {
      synchronized (sLock)
      {
        if (this.mInPool) {
          throw new IllegalStateException("Already recycled.");
        }
      }
      clear();
      if (sPoolSize < 10)
      {
        sPoolSize += 1;
        this.mNext = sPool;
        sPool = this;
        this.mInPool = true;
      }
    }
  }
  
  private final class StateViewportDraggingHandler
    implements MagnificationGestureHandler.MotionEventHandler
  {
    private boolean mLastMoveOutsideMagnifiedRegion;
    
    private StateViewportDraggingHandler() {}
    
    public void clear()
    {
      this.mLastMoveOutsideMagnifiedRegion = false;
    }
    
    public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
    {
      switch (paramMotionEvent1.getActionMasked())
      {
      case 3: 
      case 4: 
      default: 
        return;
      case 0: 
        throw new IllegalArgumentException("Unexpected event type: ACTION_DOWN");
      case 5: 
        clear();
        MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 4);
        return;
      case 2: 
        if (paramMotionEvent1.getPointerCount() != 1) {
          throw new IllegalStateException("Should have one pointer down.");
        }
        float f1 = paramMotionEvent1.getX();
        float f2 = paramMotionEvent1.getY();
        if (MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).magnificationRegionContains(f1, f2))
        {
          if (this.mLastMoveOutsideMagnifiedRegion)
          {
            this.mLastMoveOutsideMagnifiedRegion = false;
            MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).setCenter(f1, f2, true, 0);
            return;
          }
          MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).setCenter(f1, f2, false, 0);
          return;
        }
        this.mLastMoveOutsideMagnifiedRegion = true;
        return;
      case 1: 
        if (!MagnificationGestureHandler.-get3(MagnificationGestureHandler.this)) {
          MagnificationGestureHandler.-get1(MagnificationGestureHandler.this).reset(true);
        }
        clear();
        MagnificationGestureHandler.-wrap0(MagnificationGestureHandler.this, 2);
        return;
      }
      throw new IllegalArgumentException("Unexpected event type: ACTION_POINTER_UP");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/MagnificationGestureHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */