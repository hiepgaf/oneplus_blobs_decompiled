package com.android.server.am;

import android.graphics.Rect;
import android.os.Handler;

class ResizeDockedStackTimeout
{
  private static final long TIMEOUT_MS = 10000L;
  private final Rect mCurrentDockedBounds = new Rect();
  private final Handler mHandler;
  private final ActivityManagerService mService;
  private final ActivityStackSupervisor mSupervisor;
  private final Runnable mTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      synchronized (ResizeDockedStackTimeout.-get1(ResizeDockedStackTimeout.this))
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ResizeDockedStackTimeout.-get2(ResizeDockedStackTimeout.this).resizeDockedStackLocked(ResizeDockedStackTimeout.-get0(ResizeDockedStackTimeout.this), null, null, null, null, true);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
  };
  
  ResizeDockedStackTimeout(ActivityManagerService paramActivityManagerService, ActivityStackSupervisor paramActivityStackSupervisor, Handler paramHandler)
  {
    this.mService = paramActivityManagerService;
    this.mSupervisor = paramActivityStackSupervisor;
    this.mHandler = paramHandler;
  }
  
  void notifyResizing(Rect paramRect, boolean paramBoolean)
  {
    this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    if (!paramBoolean) {
      return;
    }
    this.mCurrentDockedBounds.set(paramRect);
    this.mHandler.postDelayed(this.mTimeoutRunnable, 10000L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ResizeDockedStackTimeout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */