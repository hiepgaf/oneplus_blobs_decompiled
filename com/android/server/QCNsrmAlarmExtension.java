package com.android.server;

import android.os.Binder;
import android.os.PowerManager.WakeLock;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public final class QCNsrmAlarmExtension
{
  private static final int BLOCKED_UID_CHECK_INTERVAL = 1000;
  static final String TAG = "QCNsrmAlarmExtn";
  static final boolean localLOGV = false;
  private static final ArrayList<Integer> mBlockedUids = new ArrayList();
  private static final ArrayList<Integer> mTriggeredUids = new ArrayList();
  private AlarmManagerService almHandle;
  
  public QCNsrmAlarmExtension(AlarmManagerService paramAlarmManagerService)
  {
    this.almHandle = paramAlarmManagerService;
  }
  
  protected void addTriggeredUid(int paramInt)
  {
    mTriggeredUids.add(new Integer(paramInt));
  }
  
  protected boolean hasBlockedUid(int paramInt)
  {
    return mBlockedUids.contains(Integer.valueOf(paramInt));
  }
  
  protected void processBlockedUids(int paramInt, boolean paramBoolean, PowerManager.WakeLock paramWakeLock)
  {
    if (Binder.getCallingUid() != 1000) {
      return;
    }
    if (paramBoolean)
    {
      mBlockedUids.add(new Integer(paramInt));
      new Timer().schedule(new CheckBlockedUidTimerTask(paramInt, paramWakeLock), 1000L);
      return;
    }
    mBlockedUids.clear();
  }
  
  protected void removeTriggeredUid(int paramInt)
  {
    mTriggeredUids.remove(new Integer(paramInt));
  }
  
  class CheckBlockedUidTimerTask
    extends TimerTask
  {
    private int mUid;
    PowerManager.WakeLock mWakeLock;
    
    CheckBlockedUidTimerTask(int paramInt, PowerManager.WakeLock paramWakeLock)
    {
      this.mUid = paramInt;
      this.mWakeLock = paramWakeLock;
    }
    
    public void run()
    {
      if ((QCNsrmAlarmExtension.-get1().contains(Integer.valueOf(this.mUid))) && (QCNsrmAlarmExtension.-get2().contains(Integer.valueOf(this.mUid)))) {
        synchronized (QCNsrmAlarmExtension.-get0(QCNsrmAlarmExtension.this).mLock)
        {
          if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
          }
          return;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/QCNsrmAlarmExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */