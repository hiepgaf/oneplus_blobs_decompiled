package com.android.server.os;

import android.os.Binder;
import android.os.ISchedulingPolicyService.Stub;
import android.os.Process;

public class SchedulingPolicyService
  extends ISchedulingPolicyService.Stub
{
  private static final int PRIORITY_MAX = 3;
  private static final int PRIORITY_MIN = 1;
  private static final String TAG = "SchedulingPolicyService";
  
  private boolean isPermittedCallingUid()
  {
    switch ()
    {
    default: 
      return false;
    }
    return true;
  }
  
  public int requestPriority(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 3;
    if ((!isPermittedCallingUid()) || (paramInt3 < 1)) {}
    while ((paramInt3 > 3) || (Process.getThreadGroupLeader(paramInt2) != paramInt1)) {
      return -1;
    }
    try
    {
      if (Binder.getCallingPid() == paramInt1) {
        i = 4;
      }
      Process.setThreadGroup(paramInt2, i);
      Process.setThreadScheduler(paramInt2, 1073741825, paramInt3);
      return 0;
    }
    catch (RuntimeException localRuntimeException) {}
    return -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/os/SchedulingPolicyService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */