package com.android.server.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.accessibility.AccessibilityEvent;

public class AutoclickController
  implements EventStreamTransformation
{
  private static final String LOG_TAG = AutoclickController.class.getSimpleName();
  private ClickDelayObserver mClickDelayObserver;
  private ClickScheduler mClickScheduler;
  private final Context mContext;
  private EventStreamTransformation mNext;
  private final int mUserId;
  
  public AutoclickController(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.mUserId = paramInt;
  }
  
  private void handleMouseMotion(MotionEvent paramMotionEvent, int paramInt)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    case 8: 
    default: 
      this.mClickScheduler.cancel();
    case 9: 
    case 10: 
      return;
    }
    if (paramMotionEvent.getPointerCount() == 1)
    {
      this.mClickScheduler.update(paramMotionEvent, paramInt);
      return;
    }
    this.mClickScheduler.cancel();
  }
  
  public void clearEvents(int paramInt)
  {
    if ((paramInt == 8194) && (this.mClickScheduler != null)) {
      this.mClickScheduler.cancel();
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
    if (this.mClickDelayObserver != null)
    {
      this.mClickDelayObserver.stop();
      this.mClickDelayObserver = null;
    }
    if (this.mClickScheduler != null)
    {
      this.mClickScheduler.cancel();
      this.mClickScheduler = null;
    }
  }
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    if (this.mClickScheduler != null)
    {
      if (!KeyEvent.isModifierKey(paramKeyEvent.getKeyCode())) {
        break label47;
      }
      this.mClickScheduler.updateMetaState(paramKeyEvent.getMetaState());
    }
    for (;;)
    {
      if (this.mNext != null) {
        this.mNext.onKeyEvent(paramKeyEvent, paramInt);
      }
      return;
      label47:
      this.mClickScheduler.cancel();
    }
  }
  
  public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    if (paramMotionEvent1.isFromSource(8194))
    {
      if (this.mClickScheduler == null)
      {
        Handler localHandler = new Handler(this.mContext.getMainLooper());
        this.mClickScheduler = new ClickScheduler(localHandler, 600);
        this.mClickDelayObserver = new ClickDelayObserver(this.mUserId, localHandler);
        this.mClickDelayObserver.start(this.mContext.getContentResolver(), this.mClickScheduler);
      }
      handleMouseMotion(paramMotionEvent1, paramInt);
    }
    for (;;)
    {
      if (this.mNext != null) {
        this.mNext.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      }
      return;
      if (this.mClickScheduler != null) {
        this.mClickScheduler.cancel();
      }
    }
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation)
  {
    this.mNext = paramEventStreamTransformation;
  }
  
  private static final class ClickDelayObserver
    extends ContentObserver
  {
    private final Uri mAutoclickDelaySettingUri = Settings.Secure.getUriFor("accessibility_autoclick_delay");
    private AutoclickController.ClickScheduler mClickScheduler;
    private ContentResolver mContentResolver;
    private final int mUserId;
    
    public ClickDelayObserver(int paramInt, Handler paramHandler)
    {
      super();
      this.mUserId = paramInt;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (this.mAutoclickDelaySettingUri.equals(paramUri))
      {
        int i = Settings.Secure.getIntForUser(this.mContentResolver, "accessibility_autoclick_delay", 600, this.mUserId);
        this.mClickScheduler.updateDelay(i);
      }
    }
    
    public void start(ContentResolver paramContentResolver, AutoclickController.ClickScheduler paramClickScheduler)
    {
      if ((this.mContentResolver != null) || (this.mClickScheduler != null)) {
        throw new IllegalStateException("Observer already started.");
      }
      if (paramContentResolver == null) {
        throw new NullPointerException("contentResolver not set.");
      }
      if (paramClickScheduler == null) {
        throw new NullPointerException("clickScheduler not set.");
      }
      this.mContentResolver = paramContentResolver;
      this.mClickScheduler = paramClickScheduler;
      this.mContentResolver.registerContentObserver(this.mAutoclickDelaySettingUri, false, this, this.mUserId);
      onChange(true, this.mAutoclickDelaySettingUri);
    }
    
    public void stop()
    {
      if ((this.mContentResolver == null) || (this.mClickScheduler == null)) {
        throw new IllegalStateException("ClickDelayObserver not started.");
      }
      this.mContentResolver.unregisterContentObserver(this);
    }
  }
  
  private final class ClickScheduler
    implements Runnable
  {
    private static final double MOVEMENT_SLOPE = 20.0D;
    private boolean mActive;
    private MotionEvent.PointerCoords mAnchorCoords;
    private int mDelay;
    private int mEventPolicyFlags;
    private Handler mHandler;
    private MotionEvent mLastMotionEvent;
    private int mMetaState;
    private long mScheduledClickTime;
    private MotionEvent.PointerCoords[] mTempPointerCoords;
    private MotionEvent.PointerProperties[] mTempPointerProperties;
    
    public ClickScheduler(Handler paramHandler, int paramInt)
    {
      this.mHandler = paramHandler;
      this.mLastMotionEvent = null;
      resetInternalState();
      this.mDelay = paramInt;
      this.mAnchorCoords = new MotionEvent.PointerCoords();
    }
    
    private void cacheLastEvent(MotionEvent paramMotionEvent, int paramInt, boolean paramBoolean)
    {
      if (this.mLastMotionEvent != null) {
        this.mLastMotionEvent.recycle();
      }
      this.mLastMotionEvent = MotionEvent.obtain(paramMotionEvent);
      this.mEventPolicyFlags = paramInt;
      if (paramBoolean)
      {
        paramInt = this.mLastMotionEvent.getActionIndex();
        this.mLastMotionEvent.getPointerCoords(paramInt, this.mAnchorCoords);
      }
    }
    
    private boolean detectMovement(MotionEvent paramMotionEvent)
    {
      boolean bool = false;
      if (this.mLastMotionEvent == null) {
        return false;
      }
      int i = paramMotionEvent.getActionIndex();
      float f1 = this.mAnchorCoords.x;
      float f2 = paramMotionEvent.getX(i);
      float f3 = this.mAnchorCoords.y;
      float f4 = paramMotionEvent.getY(i);
      if (Math.hypot(f1 - f2, f3 - f4) > 20.0D) {
        bool = true;
      }
      return bool;
    }
    
    private void rescheduleClick(int paramInt)
    {
      long l = SystemClock.uptimeMillis() + paramInt;
      if ((this.mActive) && (l > this.mScheduledClickTime))
      {
        this.mScheduledClickTime = l;
        return;
      }
      if (this.mActive) {
        this.mHandler.removeCallbacks(this);
      }
      this.mActive = true;
      this.mScheduledClickTime = l;
      this.mHandler.postDelayed(this, paramInt);
    }
    
    private void resetInternalState()
    {
      this.mActive = false;
      if (this.mLastMotionEvent != null)
      {
        this.mLastMotionEvent.recycle();
        this.mLastMotionEvent = null;
      }
      this.mScheduledClickTime = -1L;
    }
    
    private void sendClick()
    {
      if ((this.mLastMotionEvent == null) || (AutoclickController.-get0(AutoclickController.this) == null)) {
        return;
      }
      int i = this.mLastMotionEvent.getActionIndex();
      if (this.mTempPointerProperties == null)
      {
        this.mTempPointerProperties = new MotionEvent.PointerProperties[1];
        this.mTempPointerProperties[0] = new MotionEvent.PointerProperties();
      }
      this.mLastMotionEvent.getPointerProperties(i, this.mTempPointerProperties[0]);
      if (this.mTempPointerCoords == null)
      {
        this.mTempPointerCoords = new MotionEvent.PointerCoords[1];
        this.mTempPointerCoords[0] = new MotionEvent.PointerCoords();
      }
      this.mLastMotionEvent.getPointerCoords(i, this.mTempPointerCoords[0]);
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent1 = MotionEvent.obtain(l, l, 0, 1, this.mTempPointerProperties, this.mTempPointerCoords, this.mMetaState, 1, 1.0F, 1.0F, this.mLastMotionEvent.getDeviceId(), 0, this.mLastMotionEvent.getSource(), this.mLastMotionEvent.getFlags());
      MotionEvent localMotionEvent2 = MotionEvent.obtain(localMotionEvent1);
      localMotionEvent2.setAction(1);
      AutoclickController.-get0(AutoclickController.this).onMotionEvent(localMotionEvent1, localMotionEvent1, this.mEventPolicyFlags);
      localMotionEvent1.recycle();
      AutoclickController.-get0(AutoclickController.this).onMotionEvent(localMotionEvent2, localMotionEvent2, this.mEventPolicyFlags);
      localMotionEvent2.recycle();
    }
    
    public void cancel()
    {
      if (!this.mActive) {
        return;
      }
      resetInternalState();
      this.mHandler.removeCallbacks(this);
    }
    
    public void run()
    {
      long l = SystemClock.uptimeMillis();
      if (l < this.mScheduledClickTime)
      {
        this.mHandler.postDelayed(this, this.mScheduledClickTime - l);
        return;
      }
      sendClick();
      resetInternalState();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ClickScheduler: { active=").append(this.mActive);
      localStringBuilder.append(", delay=").append(this.mDelay);
      localStringBuilder.append(", scheduledClickTime=").append(this.mScheduledClickTime);
      localStringBuilder.append(", anchor={x:").append(this.mAnchorCoords.x);
      localStringBuilder.append(", y:").append(this.mAnchorCoords.y).append("}");
      localStringBuilder.append(", metastate=").append(this.mMetaState);
      localStringBuilder.append(", policyFlags=").append(this.mEventPolicyFlags);
      localStringBuilder.append(", lastMotionEvent=").append(this.mLastMotionEvent);
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
    }
    
    public void update(MotionEvent paramMotionEvent, int paramInt)
    {
      this.mMetaState = paramMotionEvent.getMetaState();
      boolean bool2 = detectMovement(paramMotionEvent);
      if (this.mLastMotionEvent != null) {}
      for (boolean bool1 = bool2;; bool1 = true)
      {
        cacheLastEvent(paramMotionEvent, paramInt, bool1);
        if (bool2) {
          rescheduleClick(this.mDelay);
        }
        return;
      }
    }
    
    public void updateDelay(int paramInt)
    {
      this.mDelay = paramInt;
    }
    
    public void updateMetaState(int paramInt)
    {
      this.mMetaState = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/AutoclickController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */