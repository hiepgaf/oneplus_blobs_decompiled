package com.android.server.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TouchExplorer
  implements EventStreamTransformation, AccessibilityGestureDetector.Listener
{
  private static final int ALL_POINTER_ID_BITS = -1;
  private static final int CLICK_LOCATION_ACCESSIBILITY_FOCUS = 1;
  private static final int CLICK_LOCATION_LAST_TOUCH_EXPLORED = 2;
  private static final int CLICK_LOCATION_NONE = 0;
  private static final boolean DEBUG = false;
  private static final int EXIT_GESTURE_DETECTION_TIMEOUT = 2000;
  private static final int INVALID_POINTER_ID = -1;
  private static final String LOG_TAG = "TouchExplorer";
  private static final float MAX_DRAGGING_ANGLE_COS = 0.52532196F;
  private static final int MAX_POINTER_COUNT = 32;
  private static final int MIN_POINTER_DISTANCE_TO_USE_MIDDLE_LOCATION_DIP = 200;
  private static final int STATE_DELEGATING = 4;
  private static final int STATE_DRAGGING = 2;
  private static final int STATE_GESTURE_DETECTING = 5;
  private static final int STATE_TOUCH_EXPLORING = 1;
  private final AccessibilityManagerService mAms;
  private final Context mContext;
  private int mCurrentState = 1;
  private final int mDetermineUserIntentTimeout;
  private final int mDoubleTapSlop;
  private int mDraggingPointerId;
  private final ExitGestureDetectionModeDelayed mExitGestureDetectionModeDelayed;
  private final AccessibilityGestureDetector mGestureDetector;
  private final Handler mHandler;
  private final InjectedPointerTracker mInjectedPointerTracker;
  private int mLastTouchedWindowId;
  private int mLongPressingPointerDeltaX;
  private int mLongPressingPointerDeltaY;
  private int mLongPressingPointerId = -1;
  private EventStreamTransformation mNext;
  private final ReceivedPointerTracker mReceivedPointerTracker;
  private final int mScaledMinPointerDistanceToUseMiddleLocation;
  private final SendHoverEnterAndMoveDelayed mSendHoverEnterAndMoveDelayed;
  private final SendHoverExitDelayed mSendHoverExitDelayed;
  private final SendAccessibilityEventDelayed mSendTouchExplorationEndDelayed;
  private final SendAccessibilityEventDelayed mSendTouchInteractionEndDelayed;
  private final Point mTempPoint = new Point();
  private boolean mTouchExplorationInProgress;
  
  public TouchExplorer(Context paramContext, AccessibilityManagerService paramAccessibilityManagerService)
  {
    this.mContext = paramContext;
    this.mAms = paramAccessibilityManagerService;
    this.mReceivedPointerTracker = new ReceivedPointerTracker();
    this.mInjectedPointerTracker = new InjectedPointerTracker();
    this.mDetermineUserIntentTimeout = ViewConfiguration.getDoubleTapTimeout();
    this.mDoubleTapSlop = ViewConfiguration.get(paramContext).getScaledDoubleTapSlop();
    this.mHandler = new Handler(paramContext.getMainLooper());
    this.mExitGestureDetectionModeDelayed = new ExitGestureDetectionModeDelayed(null);
    this.mSendHoverEnterAndMoveDelayed = new SendHoverEnterAndMoveDelayed();
    this.mSendHoverExitDelayed = new SendHoverExitDelayed();
    this.mSendTouchExplorationEndDelayed = new SendAccessibilityEventDelayed(1024, this.mDetermineUserIntentTimeout);
    this.mSendTouchInteractionEndDelayed = new SendAccessibilityEventDelayed(2097152, this.mDetermineUserIntentTimeout);
    this.mGestureDetector = new AccessibilityGestureDetector(paramContext, this);
    this.mScaledMinPointerDistanceToUseMiddleLocation = ((int)(200.0F * paramContext.getResources().getDisplayMetrics().density));
  }
  
  private void clear()
  {
    if (this.mReceivedPointerTracker.getLastReceivedEvent() != null) {
      clear(this.mReceivedPointerTracker.getLastReceivedEvent(), 33554432);
    }
  }
  
  private void clear(MotionEvent paramMotionEvent, int paramInt)
  {
    switch (this.mCurrentState)
    {
    }
    for (;;)
    {
      this.mSendHoverEnterAndMoveDelayed.cancel();
      this.mSendHoverExitDelayed.cancel();
      this.mExitGestureDetectionModeDelayed.cancel();
      this.mSendTouchExplorationEndDelayed.cancel();
      this.mSendTouchInteractionEndDelayed.cancel();
      this.mReceivedPointerTracker.clear();
      this.mInjectedPointerTracker.clear();
      this.mGestureDetector.clear();
      this.mLongPressingPointerId = -1;
      this.mLongPressingPointerDeltaX = 0;
      this.mLongPressingPointerDeltaY = 0;
      this.mCurrentState = 1;
      this.mTouchExplorationInProgress = false;
      this.mAms.onTouchInteractionEnd();
      return;
      sendHoverExitAndTouchExplorationGestureEndIfNeeded(paramInt);
      continue;
      this.mDraggingPointerId = -1;
      sendUpForInjectedDownPointers(paramMotionEvent, paramInt);
      continue;
      sendUpForInjectedDownPointers(paramMotionEvent, paramInt);
    }
  }
  
  private int computeClickLocation(Point paramPoint)
  {
    MotionEvent localMotionEvent = this.mInjectedPointerTracker.getLastInjectedHoverEventForClick();
    if (localMotionEvent != null)
    {
      int i = localMotionEvent.getActionIndex();
      paramPoint.x = ((int)localMotionEvent.getX(i));
      paramPoint.y = ((int)localMotionEvent.getY(i));
      if ((!this.mAms.accessibilityFocusOnlyInActiveWindow()) || (this.mLastTouchedWindowId == this.mAms.getActiveWindowId()))
      {
        if (this.mAms.getAccessibilityFocusClickPointInScreen(paramPoint)) {
          return 1;
        }
        return 2;
      }
    }
    if (this.mAms.getAccessibilityFocusClickPointInScreen(paramPoint)) {
      return 1;
    }
    return 0;
  }
  
  private int computeInjectionAction(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return paramInt1;
    case 0: 
    case 5: 
      if (this.mInjectedPointerTracker.getInjectedPointerDownCount() == 0) {
        return 0;
      }
      return paramInt2 << 8 | 0x5;
    }
    if (this.mInjectedPointerTracker.getInjectedPointerDownCount() == 1) {
      return 1;
    }
    return paramInt2 << 8 | 0x6;
  }
  
  private void endGestureDetection()
  {
    this.mAms.onTouchInteractionEnd();
    sendAccessibilityEvent(524288);
    sendAccessibilityEvent(2097152);
    this.mExitGestureDetectionModeDelayed.cancel();
    this.mCurrentState = 1;
  }
  
  private static String getStateSymbolicName(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("Unknown state: " + paramInt);
    case 1: 
      return "STATE_TOUCH_EXPLORING";
    case 2: 
      return "STATE_DRAGGING";
    case 4: 
      return "STATE_DELEGATING";
    }
    return "STATE_GESTURE_DETECTING";
  }
  
  private void handleMotionEventStateDelegating(MotionEvent paramMotionEvent, int paramInt)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    default: 
      sendMotionEvent(paramMotionEvent, paramMotionEvent.getAction(), -1, paramInt);
      return;
    case 0: 
      throw new IllegalStateException("Delegating state can only be reached if there is at least one pointer down!");
    }
    MotionEvent localMotionEvent = paramMotionEvent;
    if (this.mLongPressingPointerId >= 0)
    {
      localMotionEvent = offsetEvent(paramMotionEvent, -this.mLongPressingPointerDeltaX, -this.mLongPressingPointerDeltaY);
      this.mLongPressingPointerId = -1;
      this.mLongPressingPointerDeltaX = 0;
      this.mLongPressingPointerDeltaY = 0;
    }
    sendMotionEvent(localMotionEvent, localMotionEvent.getAction(), -1, paramInt);
    this.mAms.onTouchInteractionEnd();
    sendAccessibilityEvent(2097152);
    this.mCurrentState = 1;
  }
  
  private void handleMotionEventStateDragging(MotionEvent paramMotionEvent, int paramInt)
  {
    int i = 0;
    if (paramMotionEvent.findPointerIndex(this.mDraggingPointerId) == -1)
    {
      Slog.e("TouchExplorer", "mDraggingPointerId doesn't match any pointers on current event. mDraggingPointerId: " + Integer.toString(this.mDraggingPointerId) + ", Event: " + paramMotionEvent);
      this.mDraggingPointerId = -1;
    }
    switch (paramMotionEvent.getActionMasked())
    {
    case 3: 
    case 4: 
    default: 
    case 0: 
    case 5: 
    case 2: 
    case 6: 
      do
      {
        do
        {
          return;
          i = 1 << this.mDraggingPointerId;
          break;
          throw new IllegalStateException("Dragging state can be reached only if two pointers are already down");
          this.mCurrentState = 4;
          if (this.mDraggingPointerId != -1) {
            sendMotionEvent(paramMotionEvent, 1, i, paramInt);
          }
          sendDownForAllNotInjectedPointers(paramMotionEvent, paramInt);
          return;
        } while (this.mDraggingPointerId == -1);
        switch (paramMotionEvent.getPointerCount())
        {
        case 1: 
        default: 
          this.mCurrentState = 4;
          sendMotionEvent(paramMotionEvent, 1, i, paramInt);
          sendDownForAllNotInjectedPointers(paramMotionEvent, paramInt);
          return;
        }
        if (isDraggingGesture(paramMotionEvent))
        {
          float f3 = paramMotionEvent.getX(0);
          float f1 = paramMotionEvent.getY(0);
          float f4 = paramMotionEvent.getX(1);
          float f2 = paramMotionEvent.getY(1);
          f3 -= f4;
          f1 -= f2;
          if (Math.hypot(f3, f1) > this.mScaledMinPointerDistanceToUseMiddleLocation) {
            paramMotionEvent.setLocation(f3 / 2.0F, f1 / 2.0F);
          }
          sendMotionEvent(paramMotionEvent, 2, i, paramInt);
          return;
        }
        this.mCurrentState = 4;
        sendMotionEvent(paramMotionEvent, 1, i, paramInt);
        sendDownForAllNotInjectedPointers(paramMotionEvent, paramInt);
        return;
      } while (paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex()) != this.mDraggingPointerId);
      this.mDraggingPointerId = -1;
      sendMotionEvent(paramMotionEvent, 1, i, paramInt);
      return;
    }
    this.mAms.onTouchInteractionEnd();
    sendAccessibilityEvent(2097152);
    if (paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex()) == this.mDraggingPointerId)
    {
      this.mDraggingPointerId = -1;
      sendMotionEvent(paramMotionEvent, 1, i, paramInt);
    }
    this.mCurrentState = 1;
  }
  
  private void handleMotionEventStateTouchExploring(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    ReceivedPointerTracker localReceivedPointerTracker = this.mReceivedPointerTracker;
    switch (paramMotionEvent1.getActionMasked())
    {
    }
    for (;;)
    {
      return;
      this.mAms.onTouchInteractionStart();
      sendAccessibilityEvent(1048576);
      this.mSendHoverEnterAndMoveDelayed.cancel();
      this.mSendHoverExitDelayed.cancel();
      if (this.mSendTouchExplorationEndDelayed.isPending()) {
        this.mSendTouchExplorationEndDelayed.forceSendAndRemove();
      }
      if (this.mSendTouchInteractionEndDelayed.isPending()) {
        this.mSendTouchInteractionEndDelayed.forceSendAndRemove();
      }
      if ((!this.mGestureDetector.firstTapDetected()) && (!this.mTouchExplorationInProgress))
      {
        if (!SendHoverEnterAndMoveDelayed.-wrap0(this.mSendHoverEnterAndMoveDelayed))
        {
          i = localReceivedPointerTracker.getPrimaryPointerId();
          this.mSendHoverEnterAndMoveDelayed.post(paramMotionEvent1, true, 1 << i, paramInt);
          return;
        }
        this.mSendHoverEnterAndMoveDelayed.addEvent(paramMotionEvent1);
        return;
        this.mSendHoverEnterAndMoveDelayed.cancel();
        this.mSendHoverExitDelayed.cancel();
        return;
        int i = localReceivedPointerTracker.getPrimaryPointerId();
        int j = paramMotionEvent1.findPointerIndex(i);
        int k = 1 << i;
        switch (paramMotionEvent1.getPointerCount())
        {
        default: 
          if (SendHoverEnterAndMoveDelayed.-wrap0(this.mSendHoverEnterAndMoveDelayed))
          {
            this.mSendHoverEnterAndMoveDelayed.cancel();
            this.mSendHoverExitDelayed.cancel();
          }
          break;
        }
        for (;;)
        {
          this.mCurrentState = 4;
          sendDownForAllNotInjectedPointers(paramMotionEvent1, paramInt);
          return;
          if (SendHoverEnterAndMoveDelayed.-wrap0(this.mSendHoverEnterAndMoveDelayed))
          {
            this.mSendHoverEnterAndMoveDelayed.addEvent(paramMotionEvent1);
            return;
          }
          if (!this.mTouchExplorationInProgress) {
            break;
          }
          sendTouchExplorationGestureStartAndHoverEnterIfNeeded(paramInt);
          sendMotionEvent(paramMotionEvent1, 7, k, paramInt);
          return;
          if (SendHoverEnterAndMoveDelayed.-wrap0(this.mSendHoverEnterAndMoveDelayed))
          {
            this.mSendHoverEnterAndMoveDelayed.cancel();
            this.mSendHoverExitDelayed.cancel();
          }
          for (;;)
          {
            if (!isDraggingGesture(paramMotionEvent1)) {
              break label445;
            }
            this.mCurrentState = 2;
            this.mDraggingPointerId = i;
            paramMotionEvent1.setEdgeFlags(localReceivedPointerTracker.getLastReceivedDownEdgeFlags());
            sendMotionEvent(paramMotionEvent1, 0, k, paramInt);
            return;
            if (this.mTouchExplorationInProgress)
            {
              float f1 = localReceivedPointerTracker.getReceivedPointerDownX(i);
              float f2 = paramMotionEvent2.getX(j);
              float f3 = localReceivedPointerTracker.getReceivedPointerDownY(i);
              float f4 = paramMotionEvent2.getY(j);
              if (Math.hypot(f1 - f2, f3 - f4) < this.mDoubleTapSlop) {
                break;
              }
              sendHoverExitAndTouchExplorationGestureEndIfNeeded(paramInt);
            }
          }
          label445:
          this.mCurrentState = 4;
          sendDownForAllNotInjectedPointers(paramMotionEvent1, paramInt);
          return;
          sendHoverExitAndTouchExplorationGestureEndIfNeeded(paramInt);
        }
        this.mAms.onTouchInteractionEnd();
        i = paramMotionEvent1.getPointerId(paramMotionEvent1.getActionIndex());
        if (SendHoverEnterAndMoveDelayed.-wrap0(this.mSendHoverEnterAndMoveDelayed)) {
          this.mSendHoverExitDelayed.post(paramMotionEvent1, 1 << i, paramInt);
        }
        while (!this.mSendTouchInteractionEndDelayed.isPending())
        {
          this.mSendTouchInteractionEndDelayed.post();
          return;
          sendHoverExitAndTouchExplorationGestureEndIfNeeded(paramInt);
        }
      }
    }
  }
  
  private boolean isDraggingGesture(MotionEvent paramMotionEvent)
  {
    ReceivedPointerTracker localReceivedPointerTracker = this.mReceivedPointerTracker;
    float f1 = paramMotionEvent.getX(0);
    float f2 = paramMotionEvent.getY(0);
    float f3 = paramMotionEvent.getX(1);
    float f4 = paramMotionEvent.getY(1);
    return GestureUtils.isDraggingGesture(localReceivedPointerTracker.getReceivedPointerDownX(0), localReceivedPointerTracker.getReceivedPointerDownY(0), localReceivedPointerTracker.getReceivedPointerDownX(1), localReceivedPointerTracker.getReceivedPointerDownY(1), f1, f2, f3, f4, 0.52532196F);
  }
  
  private MotionEvent offsetEvent(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return paramMotionEvent;
    }
    int j = paramMotionEvent.findPointerIndex(this.mLongPressingPointerId);
    int k = paramMotionEvent.getPointerCount();
    MotionEvent.PointerProperties[] arrayOfPointerProperties = MotionEvent.PointerProperties.createArray(k);
    MotionEvent.PointerCoords[] arrayOfPointerCoords = MotionEvent.PointerCoords.createArray(k);
    int i = 0;
    while (i < k)
    {
      paramMotionEvent.getPointerProperties(i, arrayOfPointerProperties[i]);
      paramMotionEvent.getPointerCoords(i, arrayOfPointerCoords[i]);
      if (i == j)
      {
        MotionEvent.PointerCoords localPointerCoords = arrayOfPointerCoords[i];
        localPointerCoords.x += paramInt1;
        localPointerCoords = arrayOfPointerCoords[i];
        localPointerCoords.y += paramInt2;
      }
      i += 1;
    }
    return MotionEvent.obtain(paramMotionEvent.getDownTime(), paramMotionEvent.getEventTime(), paramMotionEvent.getAction(), paramMotionEvent.getPointerCount(), arrayOfPointerProperties, arrayOfPointerCoords, paramMotionEvent.getMetaState(), paramMotionEvent.getButtonState(), 1.0F, 1.0F, paramMotionEvent.getDeviceId(), paramMotionEvent.getEdgeFlags(), paramMotionEvent.getSource(), paramMotionEvent.getFlags());
  }
  
  private void sendAccessibilityEvent(int paramInt)
  {
    AccessibilityManager localAccessibilityManager = AccessibilityManager.getInstance(this.mContext);
    if (localAccessibilityManager.isEnabled())
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
      localAccessibilityEvent.setWindowId(this.mAms.getActiveWindowId());
      localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
    }
    switch (paramInt)
    {
    default: 
      return;
    case 512: 
      this.mTouchExplorationInProgress = true;
      return;
    }
    this.mTouchExplorationInProgress = false;
  }
  
  private void sendActionDownAndUp(MotionEvent paramMotionEvent, int paramInt, boolean paramBoolean)
  {
    int i = 1 << paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
    paramMotionEvent.setTargetAccessibilityFocus(paramBoolean);
    sendMotionEvent(paramMotionEvent, 0, i, paramInt);
    paramMotionEvent.setTargetAccessibilityFocus(paramBoolean);
    sendMotionEvent(paramMotionEvent, 1, i, paramInt);
  }
  
  private void sendDownForAllNotInjectedPointers(MotionEvent paramMotionEvent, int paramInt)
  {
    InjectedPointerTracker localInjectedPointerTracker = this.mInjectedPointerTracker;
    int j = 0;
    int m = paramMotionEvent.getPointerCount();
    int i = 0;
    while (i < m)
    {
      int n = paramMotionEvent.getPointerId(i);
      int k = j;
      if (!localInjectedPointerTracker.isInjectedPointerDown(n))
      {
        k = j | 1 << n;
        sendMotionEvent(paramMotionEvent, computeInjectionAction(0, i), k, paramInt);
      }
      i += 1;
      j = k;
    }
  }
  
  private void sendHoverExitAndTouchExplorationGestureEndIfNeeded(int paramInt)
  {
    MotionEvent localMotionEvent = this.mInjectedPointerTracker.getLastInjectedHoverEvent();
    if ((localMotionEvent != null) && (localMotionEvent.getActionMasked() != 10))
    {
      int i = localMotionEvent.getPointerIdBits();
      if (!this.mSendTouchExplorationEndDelayed.isPending()) {
        this.mSendTouchExplorationEndDelayed.post();
      }
      sendMotionEvent(localMotionEvent, 10, i, paramInt);
    }
  }
  
  private void sendMotionEvent(MotionEvent paramMotionEvent, int paramInt1, int paramInt2, int paramInt3)
  {
    paramMotionEvent.setAction(paramInt1);
    MotionEvent localMotionEvent1;
    if (paramInt2 == -1)
    {
      localMotionEvent1 = paramMotionEvent;
      if (paramInt1 != 0) {
        break label112;
      }
      localMotionEvent1.setDownTime(localMotionEvent1.getEventTime());
    }
    for (;;)
    {
      MotionEvent localMotionEvent2 = localMotionEvent1;
      if (this.mLongPressingPointerId >= 0) {
        localMotionEvent2 = offsetEvent(localMotionEvent1, -this.mLongPressingPointerDeltaX, -this.mLongPressingPointerDeltaY);
      }
      if (this.mNext != null) {
        this.mNext.onMotionEvent(localMotionEvent2, null, paramInt3 | 0x40000000);
      }
      this.mInjectedPointerTracker.onMotionEvent(localMotionEvent2);
      if (localMotionEvent2 != paramMotionEvent) {
        localMotionEvent2.recycle();
      }
      return;
      localMotionEvent1 = paramMotionEvent.split(paramInt2);
      break;
      label112:
      localMotionEvent1.setDownTime(this.mInjectedPointerTracker.getLastInjectedDownEventTime());
    }
  }
  
  private void sendTouchExplorationGestureStartAndHoverEnterIfNeeded(int paramInt)
  {
    MotionEvent localMotionEvent = this.mInjectedPointerTracker.getLastInjectedHoverEvent();
    if ((localMotionEvent != null) && (localMotionEvent.getActionMasked() == 10))
    {
      int i = localMotionEvent.getPointerIdBits();
      sendAccessibilityEvent(512);
      sendMotionEvent(localMotionEvent, 9, i, paramInt);
    }
  }
  
  private void sendUpForInjectedDownPointers(MotionEvent paramMotionEvent, int paramInt)
  {
    InjectedPointerTracker localInjectedPointerTracker = this.mInjectedPointerTracker;
    int j = 0;
    int k = paramMotionEvent.getPointerCount();
    int i = 0;
    if (i < k)
    {
      int m = paramMotionEvent.getPointerId(i);
      if (!localInjectedPointerTracker.isInjectedPointerDown(m)) {}
      for (;;)
      {
        i += 1;
        break;
        j |= 1 << m;
        sendMotionEvent(paramMotionEvent, computeInjectionAction(1, i), j, paramInt);
      }
    }
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
    int i = paramAccessibilityEvent.getEventType();
    if ((this.mSendTouchExplorationEndDelayed.isPending()) && (i == 256))
    {
      this.mSendTouchExplorationEndDelayed.cancel();
      sendAccessibilityEvent(1024);
    }
    if ((this.mSendTouchInteractionEndDelayed.isPending()) && (i == 256))
    {
      this.mSendTouchInteractionEndDelayed.cancel();
      sendAccessibilityEvent(2097152);
    }
    switch (i)
    {
    }
    for (;;)
    {
      if (this.mNext != null) {
        this.mNext.onAccessibilityEvent(paramAccessibilityEvent);
      }
      return;
      if (InjectedPointerTracker.-get0(this.mInjectedPointerTracker) != null)
      {
        InjectedPointerTracker.-get0(this.mInjectedPointerTracker).recycle();
        InjectedPointerTracker.-set0(this.mInjectedPointerTracker, null);
      }
      this.mLastTouchedWindowId = -1;
      continue;
      this.mLastTouchedWindowId = paramAccessibilityEvent.getWindowId();
    }
  }
  
  public void onDestroy()
  {
    clear();
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent, int paramInt)
  {
    if (this.mCurrentState != 1) {
      return false;
    }
    this.mSendHoverEnterAndMoveDelayed.cancel();
    this.mSendHoverExitDelayed.cancel();
    if (this.mSendTouchExplorationEndDelayed.isPending()) {
      this.mSendTouchExplorationEndDelayed.forceSendAndRemove();
    }
    if (this.mSendTouchInteractionEndDelayed.isPending()) {
      this.mSendTouchInteractionEndDelayed.forceSendAndRemove();
    }
    int i = paramMotionEvent.getActionIndex();
    paramMotionEvent.getPointerId(i);
    Point localPoint = this.mTempPoint;
    int j = computeClickLocation(localPoint);
    if (j == 0) {
      return true;
    }
    MotionEvent.PointerProperties[] arrayOfPointerProperties = new MotionEvent.PointerProperties[1];
    arrayOfPointerProperties[0] = new MotionEvent.PointerProperties();
    paramMotionEvent.getPointerProperties(i, arrayOfPointerProperties[0]);
    MotionEvent.PointerCoords[] arrayOfPointerCoords = new MotionEvent.PointerCoords[1];
    arrayOfPointerCoords[0] = new MotionEvent.PointerCoords();
    arrayOfPointerCoords[0].x = localPoint.x;
    arrayOfPointerCoords[0].y = localPoint.y;
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent.getDownTime(), paramMotionEvent.getEventTime(), 0, 1, arrayOfPointerProperties, arrayOfPointerCoords, 0, 0, 1.0F, 1.0F, paramMotionEvent.getDeviceId(), 0, paramMotionEvent.getSource(), paramMotionEvent.getFlags());
    if (j == 1) {}
    for (boolean bool = true;; bool = false)
    {
      sendActionDownAndUp(paramMotionEvent, paramInt, bool);
      paramMotionEvent.recycle();
      return true;
    }
  }
  
  public void onDoubleTapAndHold(MotionEvent paramMotionEvent, int paramInt)
  {
    if (this.mCurrentState != 1) {
      return;
    }
    if (this.mReceivedPointerTracker.getLastReceivedEvent().getPointerCount() == 0) {
      return;
    }
    int i = paramMotionEvent.getActionIndex();
    int j = paramMotionEvent.getPointerId(i);
    Point localPoint = this.mTempPoint;
    if (computeClickLocation(localPoint) == 0) {
      return;
    }
    this.mLongPressingPointerId = j;
    this.mLongPressingPointerDeltaX = ((int)paramMotionEvent.getX(i) - localPoint.x);
    this.mLongPressingPointerDeltaY = ((int)paramMotionEvent.getY(i) - localPoint.y);
    sendHoverExitAndTouchExplorationGestureEndIfNeeded(paramInt);
    this.mCurrentState = 4;
    sendDownForAllNotInjectedPointers(paramMotionEvent, paramInt);
  }
  
  public boolean onGestureCancelled(MotionEvent paramMotionEvent, int paramInt)
  {
    if (this.mCurrentState == 5)
    {
      endGestureDetection();
      return true;
    }
    if ((this.mCurrentState == 1) && (paramMotionEvent.getActionMasked() == 2))
    {
      int i = this.mReceivedPointerTracker.getPrimaryPointerId();
      this.mSendHoverEnterAndMoveDelayed.addEvent(paramMotionEvent);
      this.mSendHoverEnterAndMoveDelayed.forceSendAndRemove();
      this.mSendHoverExitDelayed.cancel();
      sendMotionEvent(paramMotionEvent, 7, 1 << i, paramInt);
      return true;
    }
    return false;
  }
  
  public boolean onGestureCompleted(int paramInt)
  {
    if (this.mCurrentState != 5) {
      return false;
    }
    endGestureDetection();
    this.mAms.onGesture(paramInt);
    return true;
  }
  
  public boolean onGestureStarted()
  {
    this.mCurrentState = 5;
    this.mSendHoverEnterAndMoveDelayed.cancel();
    this.mSendHoverExitDelayed.cancel();
    this.mExitGestureDetectionModeDelayed.post();
    sendAccessibilityEvent(262144);
    return false;
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
    this.mReceivedPointerTracker.onMotionEvent(paramMotionEvent2);
    if (this.mGestureDetector.onMotionEvent(paramMotionEvent2, paramInt)) {
      return;
    }
    if (paramMotionEvent1.getActionMasked() == 3)
    {
      clear(paramMotionEvent1, paramInt);
      return;
    }
    switch (this.mCurrentState)
    {
    case 3: 
    default: 
      throw new IllegalStateException("Illegal state: " + this.mCurrentState);
    case 1: 
      handleMotionEventStateTouchExploring(paramMotionEvent1, paramMotionEvent2, paramInt);
    case 5: 
      return;
    case 2: 
      handleMotionEventStateDragging(paramMotionEvent1, paramInt);
      return;
    }
    handleMotionEventStateDelegating(paramMotionEvent1, paramInt);
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation)
  {
    this.mNext = paramEventStreamTransformation;
  }
  
  public String toString()
  {
    return "TouchExplorer";
  }
  
  private final class ExitGestureDetectionModeDelayed
    implements Runnable
  {
    private ExitGestureDetectionModeDelayed() {}
    
    public void cancel()
    {
      TouchExplorer.-get1(TouchExplorer.this).removeCallbacks(this);
    }
    
    public void post()
    {
      TouchExplorer.-get1(TouchExplorer.this).postDelayed(this, 2000L);
    }
    
    public void run()
    {
      TouchExplorer.-wrap1(TouchExplorer.this, 524288);
      TouchExplorer.-wrap1(TouchExplorer.this, 512);
      TouchExplorer.-wrap0(TouchExplorer.this);
    }
  }
  
  class InjectedPointerTracker
  {
    private static final String LOG_TAG_INJECTED_POINTER_TRACKER = "InjectedPointerTracker";
    private int mInjectedPointersDown;
    private long mLastInjectedDownEventTime;
    private MotionEvent mLastInjectedHoverEvent;
    private MotionEvent mLastInjectedHoverEventForClick;
    
    InjectedPointerTracker() {}
    
    public void clear()
    {
      this.mInjectedPointersDown = 0;
    }
    
    public int getInjectedPointerDownCount()
    {
      return Integer.bitCount(this.mInjectedPointersDown);
    }
    
    public int getInjectedPointersDown()
    {
      return this.mInjectedPointersDown;
    }
    
    public long getLastInjectedDownEventTime()
    {
      return this.mLastInjectedDownEventTime;
    }
    
    public MotionEvent getLastInjectedHoverEvent()
    {
      return this.mLastInjectedHoverEvent;
    }
    
    public MotionEvent getLastInjectedHoverEventForClick()
    {
      return this.mLastInjectedHoverEventForClick;
    }
    
    public boolean isInjectedPointerDown(int paramInt)
    {
      return (this.mInjectedPointersDown & 1 << paramInt) != 0;
    }
    
    public void onMotionEvent(MotionEvent paramMotionEvent)
    {
      switch (paramMotionEvent.getActionMasked())
      {
      case 2: 
      case 3: 
      case 4: 
      case 8: 
      default: 
      case 0: 
      case 5: 
      case 1: 
      case 6: 
        do
        {
          return;
          int i = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
          this.mInjectedPointersDown |= 1 << i;
          this.mLastInjectedDownEventTime = paramMotionEvent.getDownTime();
          return;
          i = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
          this.mInjectedPointersDown &= 1 << i;
        } while (this.mInjectedPointersDown != 0);
        this.mLastInjectedDownEventTime = 0L;
        return;
      }
      if (this.mLastInjectedHoverEvent != null) {
        this.mLastInjectedHoverEvent.recycle();
      }
      this.mLastInjectedHoverEvent = MotionEvent.obtain(paramMotionEvent);
      if (this.mLastInjectedHoverEventForClick != null) {
        this.mLastInjectedHoverEventForClick.recycle();
      }
      this.mLastInjectedHoverEventForClick = MotionEvent.obtain(paramMotionEvent);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("=========================");
      localStringBuilder.append("\nDown pointers #");
      localStringBuilder.append(Integer.bitCount(this.mInjectedPointersDown));
      localStringBuilder.append(" [ ");
      int i = 0;
      while (i < 32)
      {
        if ((this.mInjectedPointersDown & i) != 0)
        {
          localStringBuilder.append(i);
          localStringBuilder.append(" ");
        }
        i += 1;
      }
      localStringBuilder.append("]");
      localStringBuilder.append("\n=========================");
      return localStringBuilder.toString();
    }
  }
  
  class ReceivedPointerTracker
  {
    private static final String LOG_TAG_RECEIVED_POINTER_TRACKER = "ReceivedPointerTracker";
    private int mLastReceivedDownEdgeFlags;
    private MotionEvent mLastReceivedEvent;
    private long mLastReceivedUpPointerDownTime;
    private float mLastReceivedUpPointerDownX;
    private float mLastReceivedUpPointerDownY;
    private int mPrimaryPointerId;
    private final long[] mReceivedPointerDownTime = new long[32];
    private final float[] mReceivedPointerDownX = new float[32];
    private final float[] mReceivedPointerDownY = new float[32];
    private int mReceivedPointersDown;
    
    ReceivedPointerTracker() {}
    
    private int findPrimaryPointerId()
    {
      int j = -1;
      long l1 = Long.MAX_VALUE;
      int i = this.mReceivedPointersDown;
      while (i > 0)
      {
        int m = Integer.numberOfTrailingZeros(i);
        int k = i & 1 << m;
        long l2 = this.mReceivedPointerDownTime[m];
        i = k;
        if (l2 < l1)
        {
          l1 = l2;
          j = m;
          i = k;
        }
      }
      return j;
    }
    
    private void handleReceivedPointerDown(int paramInt, MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getPointerId(paramInt);
      this.mLastReceivedUpPointerDownTime = 0L;
      this.mLastReceivedUpPointerDownX = 0.0F;
      this.mLastReceivedUpPointerDownX = 0.0F;
      this.mLastReceivedDownEdgeFlags = paramMotionEvent.getEdgeFlags();
      this.mReceivedPointersDown |= 1 << i;
      this.mReceivedPointerDownX[i] = paramMotionEvent.getX(paramInt);
      this.mReceivedPointerDownY[i] = paramMotionEvent.getY(paramInt);
      this.mReceivedPointerDownTime[i] = paramMotionEvent.getEventTime();
      this.mPrimaryPointerId = i;
    }
    
    private void handleReceivedPointerUp(int paramInt, MotionEvent paramMotionEvent)
    {
      paramInt = paramMotionEvent.getPointerId(paramInt);
      this.mLastReceivedUpPointerDownTime = getReceivedPointerDownTime(paramInt);
      this.mLastReceivedUpPointerDownX = this.mReceivedPointerDownX[paramInt];
      this.mLastReceivedUpPointerDownY = this.mReceivedPointerDownY[paramInt];
      this.mReceivedPointersDown &= 1 << paramInt;
      this.mReceivedPointerDownX[paramInt] = 0.0F;
      this.mReceivedPointerDownY[paramInt] = 0.0F;
      this.mReceivedPointerDownTime[paramInt] = 0L;
      if (this.mPrimaryPointerId == paramInt) {
        this.mPrimaryPointerId = -1;
      }
    }
    
    public void clear()
    {
      Arrays.fill(this.mReceivedPointerDownX, 0.0F);
      Arrays.fill(this.mReceivedPointerDownY, 0.0F);
      Arrays.fill(this.mReceivedPointerDownTime, 0L);
      this.mReceivedPointersDown = 0;
      this.mPrimaryPointerId = 0;
      this.mLastReceivedUpPointerDownTime = 0L;
      this.mLastReceivedUpPointerDownX = 0.0F;
      this.mLastReceivedUpPointerDownY = 0.0F;
    }
    
    public int getLastReceivedDownEdgeFlags()
    {
      return this.mLastReceivedDownEdgeFlags;
    }
    
    public MotionEvent getLastReceivedEvent()
    {
      return this.mLastReceivedEvent;
    }
    
    public long getLastReceivedUpPointerDownTime()
    {
      return this.mLastReceivedUpPointerDownTime;
    }
    
    public float getLastReceivedUpPointerDownX()
    {
      return this.mLastReceivedUpPointerDownX;
    }
    
    public float getLastReceivedUpPointerDownY()
    {
      return this.mLastReceivedUpPointerDownY;
    }
    
    public int getPrimaryPointerId()
    {
      if (this.mPrimaryPointerId == -1) {
        this.mPrimaryPointerId = findPrimaryPointerId();
      }
      return this.mPrimaryPointerId;
    }
    
    public int getReceivedPointerDownCount()
    {
      return Integer.bitCount(this.mReceivedPointersDown);
    }
    
    public long getReceivedPointerDownTime(int paramInt)
    {
      return this.mReceivedPointerDownTime[paramInt];
    }
    
    public float getReceivedPointerDownX(int paramInt)
    {
      return this.mReceivedPointerDownX[paramInt];
    }
    
    public float getReceivedPointerDownY(int paramInt)
    {
      return this.mReceivedPointerDownY[paramInt];
    }
    
    public boolean isReceivedPointerDown(int paramInt)
    {
      return (this.mReceivedPointersDown & 1 << paramInt) != 0;
    }
    
    public void onMotionEvent(MotionEvent paramMotionEvent)
    {
      if (this.mLastReceivedEvent != null) {
        this.mLastReceivedEvent.recycle();
      }
      this.mLastReceivedEvent = MotionEvent.obtain(paramMotionEvent);
      switch (paramMotionEvent.getActionMasked())
      {
      case 2: 
      case 3: 
      case 4: 
      default: 
        return;
      case 0: 
        handleReceivedPointerDown(paramMotionEvent.getActionIndex(), paramMotionEvent);
        return;
      case 5: 
        handleReceivedPointerDown(paramMotionEvent.getActionIndex(), paramMotionEvent);
        return;
      case 1: 
        handleReceivedPointerUp(paramMotionEvent.getActionIndex(), paramMotionEvent);
        return;
      }
      handleReceivedPointerUp(paramMotionEvent.getActionIndex(), paramMotionEvent);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("=========================");
      localStringBuilder.append("\nDown pointers #");
      localStringBuilder.append(getReceivedPointerDownCount());
      localStringBuilder.append(" [ ");
      int i = 0;
      while (i < 32)
      {
        if (isReceivedPointerDown(i))
        {
          localStringBuilder.append(i);
          localStringBuilder.append(" ");
        }
        i += 1;
      }
      localStringBuilder.append("]");
      localStringBuilder.append("\nPrimary pointer id [ ");
      localStringBuilder.append(getPrimaryPointerId());
      localStringBuilder.append(" ]");
      localStringBuilder.append("\n=========================");
      return localStringBuilder.toString();
    }
  }
  
  private class SendAccessibilityEventDelayed
    implements Runnable
  {
    private final int mDelay;
    private final int mEventType;
    
    public SendAccessibilityEventDelayed(int paramInt1, int paramInt2)
    {
      this.mEventType = paramInt1;
      this.mDelay = paramInt2;
    }
    
    public void cancel()
    {
      TouchExplorer.-get1(TouchExplorer.this).removeCallbacks(this);
    }
    
    public void forceSendAndRemove()
    {
      if (isPending())
      {
        run();
        cancel();
      }
    }
    
    public boolean isPending()
    {
      return TouchExplorer.-get1(TouchExplorer.this).hasCallbacks(this);
    }
    
    public void post()
    {
      TouchExplorer.-get1(TouchExplorer.this).postDelayed(this, this.mDelay);
    }
    
    public void run()
    {
      TouchExplorer.-wrap1(TouchExplorer.this, this.mEventType);
    }
  }
  
  class SendHoverEnterAndMoveDelayed
    implements Runnable
  {
    private final String LOG_TAG_SEND_HOVER_DELAYED = "SendHoverEnterAndMoveDelayed";
    private final List<MotionEvent> mEvents = new ArrayList();
    private int mPointerIdBits;
    private int mPolicyFlags;
    
    SendHoverEnterAndMoveDelayed() {}
    
    private void clear()
    {
      this.mPointerIdBits = -1;
      this.mPolicyFlags = 0;
      int i = this.mEvents.size() - 1;
      while (i >= 0)
      {
        ((MotionEvent)this.mEvents.remove(i)).recycle();
        i -= 1;
      }
    }
    
    private boolean isPending()
    {
      return TouchExplorer.-get1(TouchExplorer.this).hasCallbacks(this);
    }
    
    public void addEvent(MotionEvent paramMotionEvent)
    {
      this.mEvents.add(MotionEvent.obtain(paramMotionEvent));
    }
    
    public void cancel()
    {
      if (isPending())
      {
        TouchExplorer.-get1(TouchExplorer.this).removeCallbacks(this);
        clear();
      }
    }
    
    public void forceSendAndRemove()
    {
      if (isPending())
      {
        run();
        cancel();
      }
    }
    
    public void post(MotionEvent paramMotionEvent, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      cancel();
      addEvent(paramMotionEvent);
      this.mPointerIdBits = paramInt1;
      this.mPolicyFlags = paramInt2;
      TouchExplorer.-get1(TouchExplorer.this).postDelayed(this, TouchExplorer.-get0(TouchExplorer.this));
    }
    
    public void run()
    {
      TouchExplorer.-wrap1(TouchExplorer.this, 512);
      if (!this.mEvents.isEmpty())
      {
        TouchExplorer.-wrap2(TouchExplorer.this, (MotionEvent)this.mEvents.get(0), 9, this.mPointerIdBits, this.mPolicyFlags);
        int j = this.mEvents.size();
        int i = 1;
        while (i < j)
        {
          TouchExplorer.-wrap2(TouchExplorer.this, (MotionEvent)this.mEvents.get(i), 7, this.mPointerIdBits, this.mPolicyFlags);
          i += 1;
        }
      }
      clear();
    }
  }
  
  class SendHoverExitDelayed
    implements Runnable
  {
    private final String LOG_TAG_SEND_HOVER_DELAYED = "SendHoverExitDelayed";
    private int mPointerIdBits;
    private int mPolicyFlags;
    private MotionEvent mPrototype;
    
    SendHoverExitDelayed() {}
    
    private void clear()
    {
      this.mPrototype.recycle();
      this.mPrototype = null;
      this.mPointerIdBits = -1;
      this.mPolicyFlags = 0;
    }
    
    private boolean isPending()
    {
      return TouchExplorer.-get1(TouchExplorer.this).hasCallbacks(this);
    }
    
    public void cancel()
    {
      if (isPending())
      {
        TouchExplorer.-get1(TouchExplorer.this).removeCallbacks(this);
        clear();
      }
    }
    
    public void forceSendAndRemove()
    {
      if (isPending())
      {
        run();
        cancel();
      }
    }
    
    public void post(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
    {
      cancel();
      this.mPrototype = MotionEvent.obtain(paramMotionEvent);
      this.mPointerIdBits = paramInt1;
      this.mPolicyFlags = paramInt2;
      TouchExplorer.-get1(TouchExplorer.this).postDelayed(this, TouchExplorer.-get0(TouchExplorer.this));
    }
    
    public void run()
    {
      TouchExplorer.-wrap2(TouchExplorer.this, this.mPrototype, 10, this.mPointerIdBits, this.mPolicyFlags);
      if (!TouchExplorer.-get2(TouchExplorer.this).isPending())
      {
        TouchExplorer.-get2(TouchExplorer.this).cancel();
        TouchExplorer.-get2(TouchExplorer.this).post();
      }
      if (TouchExplorer.-get3(TouchExplorer.this).isPending())
      {
        TouchExplorer.-get3(TouchExplorer.this).cancel();
        TouchExplorer.-get3(TouchExplorer.this).post();
      }
      clear();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/TouchExplorer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */