package com.android.server.power;

import android.os.Binder;
import android.os.Process;
import android.os.WorkSource;
import java.util.ArrayList;

public final class QCNsrmPowerExtension
{
  static final String TAG = "QCNsrmPowerExtn";
  static final boolean localLOGV = false;
  private final ArrayList<Integer> mPmsBlockedUids = new ArrayList();
  private PowerManagerService pmHandle;
  
  public QCNsrmPowerExtension(PowerManagerService paramPowerManagerService)
  {
    this.pmHandle = paramPowerManagerService;
  }
  
  private boolean checkWorkSourceObjectId(int paramInt, PowerManagerService.WakeLock paramWakeLock)
  {
    int i = 0;
    try
    {
      while (i < paramWakeLock.mWorkSource.size())
      {
        int j = paramWakeLock.mWorkSource.get(i);
        if (paramInt == j) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    catch (Exception paramWakeLock)
    {
      return false;
    }
  }
  
  private void updatePmsBlockedUids(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mPmsBlockedUids.add(new Integer(paramInt));
      return;
    }
    this.mPmsBlockedUids.clear();
  }
  
  private boolean updatePmsBlockedWakelock(PowerManagerService.WakeLock paramWakeLock, boolean paramBoolean)
  {
    if ((paramWakeLock != null) && ((paramWakeLock.mFlags & 0xFFFF) == 1) && (paramWakeLock.mDisabled != paramBoolean) && (this.pmHandle != null))
    {
      paramWakeLock.mDisabled = paramBoolean;
      if (paramWakeLock.mDisabled)
      {
        this.pmHandle.notifyWakeLockReleasedLocked(paramWakeLock);
        return true;
      }
      this.pmHandle.notifyWakeLockAcquiredLocked(paramWakeLock);
      return true;
    }
    return false;
  }
  
  protected void checkPmsBlockedWakelocks(int paramInt1, int paramInt2, int paramInt3, String paramString, PowerManagerService.WakeLock paramWakeLock)
  {
    if ((this.mPmsBlockedUids.contains(new Integer(paramInt1))) && (paramInt1 != Process.myUid())) {
      updatePmsBlockedWakelock(paramWakeLock, true);
    }
  }
  
  protected boolean processPmsBlockedUid(int paramInt, boolean paramBoolean, ArrayList<PowerManagerService.WakeLock> paramArrayList)
  {
    boolean bool1 = false;
    if (updatePmsBlockedUidAllowed(paramInt, paramBoolean)) {
      return false;
    }
    int i = 0;
    while (i < paramArrayList.size())
    {
      PowerManagerService.WakeLock localWakeLock = (PowerManagerService.WakeLock)paramArrayList.get(i);
      boolean bool2 = bool1;
      if (localWakeLock != null) {
        if ((localWakeLock.mOwnerUid != paramInt) && (!checkWorkSourceObjectId(paramInt, localWakeLock)))
        {
          bool2 = bool1;
          if (localWakeLock.mTag.startsWith("*sync*"))
          {
            bool2 = bool1;
            if (localWakeLock.mOwnerUid != 1000) {}
          }
        }
        else
        {
          bool2 = bool1;
          if (updatePmsBlockedWakelock(localWakeLock, paramBoolean)) {
            bool2 = true;
          }
        }
      }
      i += 1;
      bool1 = bool2;
    }
    if (bool1)
    {
      paramArrayList = this.pmHandle;
      paramArrayList.mDirty |= 0x1;
      this.pmHandle.updatePowerStateLocked();
    }
    return bool1;
  }
  
  protected boolean updatePmsBlockedUidAllowed(int paramInt, boolean paramBoolean)
  {
    if (Binder.getCallingUid() != 1000) {
      return true;
    }
    updatePmsBlockedUids(paramInt, paramBoolean);
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/power/QCNsrmPowerExtension.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */