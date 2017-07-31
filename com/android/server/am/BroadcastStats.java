package com.android.server.am;

import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class BroadcastStats
{
  static final Comparator<ActionEntry> ACTIONS_COMPARATOR = new Comparator()
  {
    public int compare(BroadcastStats.ActionEntry paramAnonymousActionEntry1, BroadcastStats.ActionEntry paramAnonymousActionEntry2)
    {
      if (paramAnonymousActionEntry1.mTotalDispatchTime < paramAnonymousActionEntry2.mTotalDispatchTime) {
        return -1;
      }
      if (paramAnonymousActionEntry1.mTotalDispatchTime > paramAnonymousActionEntry2.mTotalDispatchTime) {
        return 1;
      }
      return 0;
    }
  };
  static final String TAG = "BroadcastStats";
  final ArrayMap<String, ActionEntry> mActions = new ArrayMap();
  long mEndRealtime;
  long mEndUptime;
  final long mStartRealtime = SystemClock.elapsedRealtime();
  final long mStartUptime = SystemClock.uptimeMillis();
  
  public void addBroadcast(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong)
  {
    Object localObject2 = (ActionEntry)this.mActions.get(paramString1);
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = new ActionEntry(paramString1);
    }
    try
    {
      this.mActions.put(paramString1, localObject1);
      ((ActionEntry)localObject1).mReceiveCount += paramInt1;
      ((ActionEntry)localObject1).mSkipCount += paramInt2;
      ((ActionEntry)localObject1).mTotalDispatchTime += paramLong;
      if (((ActionEntry)localObject1).mMaxDispatchTime < paramLong) {
        ((ActionEntry)localObject1).mMaxDispatchTime = paramLong;
      }
      localObject2 = (PackageEntry)((ActionEntry)localObject1).mPackages.get(paramString2);
      paramString1 = (String)localObject2;
      if (localObject2 == null)
      {
        paramString1 = new PackageEntry();
        ((ActionEntry)localObject1).mPackages.put(paramString2, paramString1);
      }
      paramString1.mSendCount += 1;
      return;
    }
    catch (ArrayIndexOutOfBoundsException paramString1)
    {
      for (;;)
      {
        Log.w("BroadcastStats", "Error while adding action entry: " + paramString1);
      }
    }
  }
  
  public void dumpCheckinStats(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print("broadcast-stats,1,");
    paramPrintWriter.print(this.mStartRealtime);
    paramPrintWriter.print(",");
    long l;
    label60:
    int i;
    if (this.mEndRealtime == 0L)
    {
      l = SystemClock.elapsedRealtime();
      paramPrintWriter.print(l);
      paramPrintWriter.print(",");
      if (this.mEndUptime != 0L) {
        break label291;
      }
      l = SystemClock.uptimeMillis();
      paramPrintWriter.println(l - this.mStartUptime);
      i = this.mActions.size() - 1;
    }
    for (;;)
    {
      if (i < 0) {
        return;
      }
      ActionEntry localActionEntry = (ActionEntry)this.mActions.valueAt(i);
      if ((paramString == null) || (localActionEntry.mPackages.containsKey(paramString)))
      {
        paramPrintWriter.print("a,");
        paramPrintWriter.print((String)this.mActions.keyAt(i));
        paramPrintWriter.print(",");
        paramPrintWriter.print(localActionEntry.mReceiveCount);
        paramPrintWriter.print(",");
        paramPrintWriter.print(localActionEntry.mSkipCount);
        paramPrintWriter.print(",");
        paramPrintWriter.print(localActionEntry.mTotalDispatchTime);
        paramPrintWriter.print(",");
        paramPrintWriter.print(localActionEntry.mMaxDispatchTime);
        paramPrintWriter.println();
        int j = localActionEntry.mPackages.size() - 1;
        for (;;)
        {
          if (j >= 0)
          {
            paramPrintWriter.print("p,");
            paramPrintWriter.print((String)localActionEntry.mPackages.keyAt(j));
            PackageEntry localPackageEntry = (PackageEntry)localActionEntry.mPackages.valueAt(j);
            paramPrintWriter.print(",");
            paramPrintWriter.print(localPackageEntry.mSendCount);
            paramPrintWriter.println();
            j -= 1;
            continue;
            l = this.mEndRealtime;
            break;
            label291:
            l = this.mEndUptime;
            break label60;
          }
        }
      }
      i -= 1;
    }
  }
  
  public boolean dumpStats(PrintWriter paramPrintWriter, String paramString1, String paramString2)
  {
    boolean bool1 = false;
    ArrayList localArrayList = new ArrayList(this.mActions.size());
    int i = this.mActions.size() - 1;
    while (i >= 0)
    {
      localArrayList.add((ActionEntry)this.mActions.valueAt(i));
      i -= 1;
    }
    Collections.sort(localArrayList, ACTIONS_COMPARATOR);
    i = localArrayList.size() - 1;
    while (i >= 0)
    {
      ActionEntry localActionEntry = (ActionEntry)localArrayList.get(i);
      if ((paramString2 == null) || (localActionEntry.mPackages.containsKey(paramString2)))
      {
        boolean bool2 = true;
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print(localActionEntry.mAction);
        paramPrintWriter.println(":");
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  Number received: ");
        paramPrintWriter.print(localActionEntry.mReceiveCount);
        paramPrintWriter.print(", skipped: ");
        paramPrintWriter.println(localActionEntry.mSkipCount);
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  Total dispatch time: ");
        TimeUtils.formatDuration(localActionEntry.mTotalDispatchTime, paramPrintWriter);
        paramPrintWriter.print(", max: ");
        TimeUtils.formatDuration(localActionEntry.mMaxDispatchTime, paramPrintWriter);
        paramPrintWriter.println();
        int j = localActionEntry.mPackages.size() - 1;
        for (;;)
        {
          bool1 = bool2;
          if (j < 0) {
            break;
          }
          paramPrintWriter.print(paramString1);
          paramPrintWriter.print("  Package ");
          paramPrintWriter.print((String)localActionEntry.mPackages.keyAt(j));
          paramPrintWriter.print(": ");
          paramPrintWriter.print(((PackageEntry)localActionEntry.mPackages.valueAt(j)).mSendCount);
          paramPrintWriter.println(" times");
          j -= 1;
        }
      }
      i -= 1;
    }
    return bool1;
  }
  
  static final class ActionEntry
  {
    final String mAction;
    long mMaxDispatchTime;
    final ArrayMap<String, BroadcastStats.PackageEntry> mPackages = new ArrayMap();
    int mReceiveCount;
    int mSkipCount;
    long mTotalDispatchTime;
    
    ActionEntry(String paramString)
    {
      this.mAction = paramString;
    }
  }
  
  static final class PackageEntry
  {
    int mSendCount;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BroadcastStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */