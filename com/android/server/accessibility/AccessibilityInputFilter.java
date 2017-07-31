package com.android.server.accessibility;

import android.content.Context;
import android.os.PowerManager;
import android.util.Pools.SimplePool;
import android.util.SparseBooleanArray;
import android.view.Choreographer;
import android.view.InputEvent;
import android.view.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

class AccessibilityInputFilter
  extends InputFilter
  implements EventStreamTransformation
{
  private static final boolean DEBUG = false;
  static final int FEATURES_AFFECTING_MOTION_EVENTS = 27;
  static final int FLAG_FEATURE_AUTOCLICK = 8;
  static final int FLAG_FEATURE_CONTROL_SCREEN_MAGNIFIER = 32;
  static final int FLAG_FEATURE_FILTER_KEY_EVENTS = 4;
  static final int FLAG_FEATURE_INJECT_MOTION_EVENTS = 16;
  static final int FLAG_FEATURE_SCREEN_MAGNIFIER = 1;
  static final int FLAG_FEATURE_TOUCH_EXPLORATION = 2;
  private static final String TAG = AccessibilityInputFilter.class.getSimpleName();
  private final AccessibilityManagerService mAms;
  private AutoclickController mAutoclickController;
  private final Choreographer mChoreographer;
  private final Context mContext;
  private int mEnabledFeatures;
  private EventStreamTransformation mEventHandler;
  private MotionEventHolder mEventQueue;
  private boolean mInstalled;
  private KeyboardInterceptor mKeyboardInterceptor;
  private EventStreamState mKeyboardStreamState;
  private MagnificationGestureHandler mMagnificationGestureHandler;
  private MotionEventInjector mMotionEventInjector;
  private EventStreamState mMouseStreamState;
  private final PowerManager mPm;
  private final Runnable mProcessBatchedEventsRunnable = new Runnable()
  {
    public void run()
    {
      long l = AccessibilityInputFilter.-get0(AccessibilityInputFilter.this).getFrameTimeNanos();
      AccessibilityInputFilter.-wrap0(AccessibilityInputFilter.this, l);
      if (AccessibilityInputFilter.-get1(AccessibilityInputFilter.this) != null) {
        AccessibilityInputFilter.-wrap1(AccessibilityInputFilter.this);
      }
    }
  };
  private TouchExplorer mTouchExplorer;
  private EventStreamState mTouchScreenStreamState;
  private int mUserId;
  
  AccessibilityInputFilter(Context paramContext, AccessibilityManagerService paramAccessibilityManagerService)
  {
    super(paramContext.getMainLooper());
    this.mContext = paramContext;
    this.mAms = paramAccessibilityManagerService;
    this.mPm = ((PowerManager)paramContext.getSystemService("power"));
    this.mChoreographer = Choreographer.getInstance();
  }
  
  private void addFirstEventHandler(EventStreamTransformation paramEventStreamTransformation)
  {
    if (this.mEventHandler != null) {
      paramEventStreamTransformation.setNext(this.mEventHandler);
    }
    for (;;)
    {
      this.mEventHandler = paramEventStreamTransformation;
      return;
      paramEventStreamTransformation.setNext(this);
    }
  }
  
  private void batchMotionEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    if (this.mEventQueue == null)
    {
      this.mEventQueue = MotionEventHolder.obtain(paramMotionEvent, paramInt);
      scheduleProcessBatchedEvents();
      return;
    }
    if (this.mEventQueue.event.addBatch(paramMotionEvent)) {
      return;
    }
    paramMotionEvent = MotionEventHolder.obtain(paramMotionEvent, paramInt);
    paramMotionEvent.next = this.mEventQueue;
    this.mEventQueue.previous = paramMotionEvent;
    this.mEventQueue = paramMotionEvent;
  }
  
  private void disableFeatures()
  {
    processBatchedEvents(Long.MAX_VALUE);
    if (this.mMotionEventInjector != null)
    {
      this.mAms.setMotionEventInjector(null);
      this.mMotionEventInjector.onDestroy();
      this.mMotionEventInjector = null;
    }
    if (this.mAutoclickController != null)
    {
      this.mAutoclickController.onDestroy();
      this.mAutoclickController = null;
    }
    if (this.mTouchExplorer != null)
    {
      this.mTouchExplorer.onDestroy();
      this.mTouchExplorer = null;
    }
    if (this.mMagnificationGestureHandler != null)
    {
      this.mMagnificationGestureHandler.onDestroy();
      this.mMagnificationGestureHandler = null;
    }
    if (this.mKeyboardInterceptor != null)
    {
      this.mKeyboardInterceptor.onDestroy();
      this.mKeyboardInterceptor = null;
    }
    this.mEventHandler = null;
    resetStreamState();
  }
  
  private void enableFeatures()
  {
    resetStreamState();
    if ((this.mEnabledFeatures & 0x8) != 0)
    {
      this.mAutoclickController = new AutoclickController(this.mContext, this.mUserId);
      addFirstEventHandler(this.mAutoclickController);
    }
    if ((this.mEnabledFeatures & 0x2) != 0)
    {
      this.mTouchExplorer = new TouchExplorer(this.mContext, this.mAms);
      addFirstEventHandler(this.mTouchExplorer);
    }
    if (((this.mEnabledFeatures & 0x20) != 0) || ((this.mEnabledFeatures & 0x1) != 0)) {
      if ((this.mEnabledFeatures & 0x1) == 0) {
        break label215;
      }
    }
    label215:
    for (boolean bool = true;; bool = false)
    {
      this.mMagnificationGestureHandler = new MagnificationGestureHandler(this.mContext, this.mAms, bool);
      addFirstEventHandler(this.mMagnificationGestureHandler);
      if ((this.mEnabledFeatures & 0x10) != 0)
      {
        this.mMotionEventInjector = new MotionEventInjector(this.mContext.getMainLooper());
        addFirstEventHandler(this.mMotionEventInjector);
        this.mAms.setMotionEventInjector(this.mMotionEventInjector);
      }
      if ((this.mEnabledFeatures & 0x4) != 0)
      {
        this.mKeyboardInterceptor = new KeyboardInterceptor(this.mAms);
        addFirstEventHandler(this.mKeyboardInterceptor);
      }
      return;
    }
  }
  
  private EventStreamState getEventStreamState(InputEvent paramInputEvent)
  {
    if ((paramInputEvent instanceof MotionEvent))
    {
      if (paramInputEvent.isFromSource(4098))
      {
        if (this.mTouchScreenStreamState == null) {
          this.mTouchScreenStreamState = new TouchScreenEventStreamState();
        }
        return this.mTouchScreenStreamState;
      }
      if (paramInputEvent.isFromSource(8194))
      {
        if (this.mMouseStreamState == null) {
          this.mMouseStreamState = new MouseEventStreamState();
        }
        return this.mMouseStreamState;
      }
    }
    else if (((paramInputEvent instanceof KeyEvent)) && (paramInputEvent.isFromSource(257)))
    {
      if (this.mKeyboardStreamState == null) {
        this.mKeyboardStreamState = new KeyboardEventStreamState();
      }
      return this.mKeyboardStreamState;
    }
    return null;
  }
  
  private void handleMotionEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    if (this.mEventHandler != null)
    {
      this.mPm.userActivity(paramMotionEvent.getEventTime(), false);
      MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
      this.mEventHandler.onMotionEvent(localMotionEvent, paramMotionEvent, paramInt);
      localMotionEvent.recycle();
    }
  }
  
  private void processBatchedEvents(long paramLong)
  {
    Object localObject2 = this.mEventQueue;
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      return;
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (((MotionEventHolder)localObject1).next == null) {
        break;
      }
      localObject1 = ((MotionEventHolder)localObject1).next;
    }
    do
    {
      handleMotionEvent(((MotionEventHolder)localObject1).event, ((MotionEventHolder)localObject1).policyFlags);
      localObject2 = ((MotionEventHolder)localObject1).previous;
      ((MotionEventHolder)localObject1).recycle();
      localObject1 = localObject2;
      if (localObject1 == null)
      {
        this.mEventQueue = null;
        return;
      }
    } while (((MotionEventHolder)localObject1).event.getEventTimeNano() < paramLong);
    ((MotionEventHolder)localObject1).next = null;
  }
  
  private void processKeyEvent(EventStreamState paramEventStreamState, KeyEvent paramKeyEvent, int paramInt)
  {
    if (!paramEventStreamState.shouldProcessKeyEvent(paramKeyEvent)) {
      return;
    }
    this.mEventHandler.onKeyEvent(paramKeyEvent, paramInt);
  }
  
  private void processMotionEvent(EventStreamState paramEventStreamState, MotionEvent paramMotionEvent, int paramInt)
  {
    if ((!paramEventStreamState.shouldProcessScroll()) && (paramMotionEvent.getActionMasked() == 8))
    {
      super.onInputEvent(paramMotionEvent, paramInt);
      return;
    }
    if (!paramEventStreamState.shouldProcessMotionEvent(paramMotionEvent)) {
      return;
    }
    batchMotionEvent(paramMotionEvent, paramInt);
  }
  
  private void scheduleProcessBatchedEvents()
  {
    this.mChoreographer.postCallback(0, this.mProcessBatchedEventsRunnable, null);
  }
  
  public void clearEvents(int paramInt) {}
  
  void notifyAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (this.mEventHandler != null) {
      this.mEventHandler.onAccessibilityEvent(paramAccessibilityEvent);
    }
  }
  
  public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {}
  
  public void onDestroy() {}
  
  public void onInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    if (this.mEventHandler == null)
    {
      super.onInputEvent(paramInputEvent, paramInt);
      return;
    }
    EventStreamState localEventStreamState = getEventStreamState(paramInputEvent);
    if (localEventStreamState == null)
    {
      super.onInputEvent(paramInputEvent, paramInt);
      return;
    }
    int i = paramInputEvent.getSource();
    if ((0x40000000 & paramInt) == 0)
    {
      localEventStreamState.reset();
      this.mEventHandler.clearEvents(i);
      super.onInputEvent(paramInputEvent, paramInt);
      return;
    }
    if (localEventStreamState.updateDeviceId(paramInputEvent.getDeviceId())) {
      this.mEventHandler.clearEvents(i);
    }
    if (!localEventStreamState.deviceIdValid())
    {
      super.onInputEvent(paramInputEvent, paramInt);
      return;
    }
    if ((paramInputEvent instanceof MotionEvent))
    {
      if ((this.mEnabledFeatures & 0x1B) != 0)
      {
        processMotionEvent(localEventStreamState, (MotionEvent)paramInputEvent, paramInt);
        return;
      }
      super.onInputEvent(paramInputEvent, paramInt);
    }
    while (!(paramInputEvent instanceof KeyEvent)) {
      return;
    }
    processKeyEvent(localEventStreamState, (KeyEvent)paramInputEvent, paramInt);
  }
  
  public void onInstalled()
  {
    this.mInstalled = true;
    disableFeatures();
    enableFeatures();
    super.onInstalled();
  }
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    sendInputEvent(paramKeyEvent, paramInt);
  }
  
  public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    sendInputEvent(paramMotionEvent1, paramInt);
  }
  
  public void onUninstalled()
  {
    this.mInstalled = false;
    disableFeatures();
    super.onUninstalled();
  }
  
  void resetStreamState()
  {
    if (this.mTouchScreenStreamState != null) {
      this.mTouchScreenStreamState.reset();
    }
    if (this.mMouseStreamState != null) {
      this.mMouseStreamState.reset();
    }
    if (this.mKeyboardStreamState != null) {
      this.mKeyboardStreamState.reset();
    }
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation) {}
  
  void setUserAndEnabledFeatures(int paramInt1, int paramInt2)
  {
    if ((this.mEnabledFeatures == paramInt2) && (this.mUserId == paramInt1)) {
      return;
    }
    if (this.mInstalled) {
      disableFeatures();
    }
    this.mUserId = paramInt1;
    this.mEnabledFeatures = paramInt2;
    if (this.mInstalled) {
      enableFeatures();
    }
  }
  
  private static class EventStreamState
  {
    private int mDeviceId = -1;
    
    public boolean deviceIdValid()
    {
      boolean bool = false;
      if (this.mDeviceId >= 0) {
        bool = true;
      }
      return bool;
    }
    
    public void reset()
    {
      this.mDeviceId = -1;
    }
    
    public boolean shouldProcessKeyEvent(KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public boolean shouldProcessMotionEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean shouldProcessScroll()
    {
      return false;
    }
    
    public boolean updateDeviceId(int paramInt)
    {
      if (this.mDeviceId == paramInt) {
        return false;
      }
      reset();
      this.mDeviceId = paramInt;
      return true;
    }
  }
  
  private static class KeyboardEventStreamState
    extends AccessibilityInputFilter.EventStreamState
  {
    private SparseBooleanArray mEventSequenceStartedMap = new SparseBooleanArray();
    
    public KeyboardEventStreamState()
    {
      reset();
    }
    
    public boolean deviceIdValid()
    {
      return true;
    }
    
    public final void reset()
    {
      super.reset();
      this.mEventSequenceStartedMap.clear();
    }
    
    public final boolean shouldProcessKeyEvent(KeyEvent paramKeyEvent)
    {
      int i = paramKeyEvent.getDeviceId();
      if (this.mEventSequenceStartedMap.get(i, false)) {
        return true;
      }
      if (paramKeyEvent.getAction() == 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.mEventSequenceStartedMap.put(i, bool);
        return bool;
      }
    }
    
    public boolean updateDeviceId(int paramInt)
    {
      return false;
    }
  }
  
  private static class MotionEventHolder
  {
    private static final int MAX_POOL_SIZE = 32;
    private static final Pools.SimplePool<MotionEventHolder> sPool = new Pools.SimplePool(32);
    public MotionEvent event;
    public MotionEventHolder next;
    public int policyFlags;
    public MotionEventHolder previous;
    
    public static MotionEventHolder obtain(MotionEvent paramMotionEvent, int paramInt)
    {
      MotionEventHolder localMotionEventHolder2 = (MotionEventHolder)sPool.acquire();
      MotionEventHolder localMotionEventHolder1 = localMotionEventHolder2;
      if (localMotionEventHolder2 == null) {
        localMotionEventHolder1 = new MotionEventHolder();
      }
      localMotionEventHolder1.event = MotionEvent.obtain(paramMotionEvent);
      localMotionEventHolder1.policyFlags = paramInt;
      return localMotionEventHolder1;
    }
    
    public void recycle()
    {
      this.event.recycle();
      this.event = null;
      this.policyFlags = 0;
      this.next = null;
      this.previous = null;
      sPool.release(this);
    }
  }
  
  private static class MouseEventStreamState
    extends AccessibilityInputFilter.EventStreamState
  {
    private boolean mMotionSequenceStarted;
    
    public MouseEventStreamState()
    {
      reset();
    }
    
    public final void reset()
    {
      super.reset();
      this.mMotionSequenceStarted = false;
    }
    
    public final boolean shouldProcessMotionEvent(MotionEvent paramMotionEvent)
    {
      boolean bool2 = true;
      if (this.mMotionSequenceStarted) {
        return true;
      }
      int i = paramMotionEvent.getActionMasked();
      boolean bool1 = bool2;
      if (i != 0) {
        if (i != 7) {
          break label43;
        }
      }
      label43:
      for (bool1 = bool2;; bool1 = false)
      {
        this.mMotionSequenceStarted = bool1;
        return this.mMotionSequenceStarted;
      }
    }
    
    public final boolean shouldProcessScroll()
    {
      return true;
    }
  }
  
  private static class TouchScreenEventStreamState
    extends AccessibilityInputFilter.EventStreamState
  {
    private boolean mHoverSequenceStarted;
    private boolean mTouchSequenceStarted;
    
    public TouchScreenEventStreamState()
    {
      reset();
    }
    
    public final void reset()
    {
      super.reset();
      this.mTouchSequenceStarted = false;
      this.mHoverSequenceStarted = false;
    }
    
    public final boolean shouldProcessMotionEvent(MotionEvent paramMotionEvent)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      if (paramMotionEvent.isTouchEvent())
      {
        if (this.mTouchSequenceStarted) {
          return true;
        }
        if (paramMotionEvent.getActionMasked() == 0) {}
        for (;;)
        {
          this.mTouchSequenceStarted = bool1;
          return this.mTouchSequenceStarted;
          bool1 = false;
        }
      }
      if (this.mHoverSequenceStarted) {
        return true;
      }
      if (paramMotionEvent.getActionMasked() == 9) {}
      for (bool1 = bool2;; bool1 = false)
      {
        this.mHoverSequenceStarted = bool1;
        return this.mHoverSequenceStarted;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/AccessibilityInputFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */