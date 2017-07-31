package com.android.server.am;

import android.os.SystemClock;
import android.os.UserHandle;
import android.util.TimeUtils;

public final class UidRecord
{
  static final int CHANGE_ACTIVE = 4;
  static final int CHANGE_GONE = 1;
  static final int CHANGE_GONE_IDLE = 2;
  static final int CHANGE_IDLE = 3;
  static final int CHANGE_PROCSTATE = 0;
  int curProcState;
  boolean idle;
  long lastBackgroundTime;
  int numProcs;
  ChangeItem pendingChange;
  int setProcState = -1;
  final int uid;
  
  public UidRecord(int paramInt)
  {
    this.uid = paramInt;
    reset();
  }
  
  public void reset()
  {
    this.curProcState = 16;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("UidRecord{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(' ');
    UserHandle.formatUid(localStringBuilder, this.uid);
    localStringBuilder.append(' ');
    localStringBuilder.append(ProcessList.makeProcStateString(this.curProcState));
    if (this.lastBackgroundTime > 0L)
    {
      localStringBuilder.append(" bg:");
      TimeUtils.formatDuration(SystemClock.elapsedRealtime() - this.lastBackgroundTime, localStringBuilder);
    }
    if (this.idle) {
      localStringBuilder.append(" idle");
    }
    localStringBuilder.append(" procs:");
    localStringBuilder.append(this.numProcs);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  static final class ChangeItem
  {
    int change;
    int processState;
    int uid;
    UidRecord uidRecord;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UidRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */