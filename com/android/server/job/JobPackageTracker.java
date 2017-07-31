package com.android.server.job;

import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.util.RingBufferIndices;
import com.android.server.job.controllers.JobStatus;
import java.io.PrintWriter;

public final class JobPackageTracker
{
  static final long BATCHING_TIME = 1800000L;
  private static final int EVENT_BUFFER_SIZE = 100;
  public static final int EVENT_NULL = 0;
  public static final int EVENT_START_JOB = 1;
  public static final int EVENT_STOP_JOB = 2;
  static final int NUM_HISTORY = 5;
  DataSet mCurDataSet = new DataSet();
  private final int[] mEventCmds = new int[100];
  private final RingBufferIndices mEventIndices = new RingBufferIndices(100);
  private final String[] mEventTags = new String[100];
  private final long[] mEventTimes = new long[100];
  private final int[] mEventUids = new int[100];
  DataSet[] mLastDataSets = new DataSet[5];
  
  public void addEvent(int paramInt1, int paramInt2, String paramString)
  {
    int i = this.mEventIndices.add();
    this.mEventCmds[i] = paramInt1;
    this.mEventTimes[i] = SystemClock.elapsedRealtime();
    this.mEventUids[i] = paramInt2;
    this.mEventTags[i] = paramString;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, int paramInt)
  {
    long l1 = SystemClock.uptimeMillis();
    long l2 = SystemClock.elapsedRealtime();
    DataSet localDataSet;
    if (this.mLastDataSets[0] != null)
    {
      localDataSet = new DataSet(this.mLastDataSets[0]);
      this.mLastDataSets[0].addTo(localDataSet, l1);
    }
    for (;;)
    {
      this.mCurDataSet.addTo(localDataSet, l1);
      int i = 1;
      while (i < this.mLastDataSets.length)
      {
        if (this.mLastDataSets[i] != null)
        {
          this.mLastDataSets[i].dump(paramPrintWriter, "Historical stats", paramString, l1, l2, paramInt);
          paramPrintWriter.println();
        }
        i += 1;
      }
      localDataSet = new DataSet(this.mCurDataSet);
    }
    localDataSet.dump(paramPrintWriter, "Current stats", paramString, l1, l2, paramInt);
  }
  
  public boolean dumpHistory(PrintWriter paramPrintWriter, String paramString, int paramInt)
  {
    int j = this.mEventIndices.size();
    if (j <= 0) {
      return false;
    }
    paramPrintWriter.println("  Job history:");
    long l = SystemClock.elapsedRealtime();
    int i = 0;
    if (i < j)
    {
      int k = this.mEventIndices.indexOf(i);
      int m = this.mEventUids[k];
      if ((paramInt != -1) && (paramInt != UserHandle.getAppId(paramInt))) {}
      while (this.mEventCmds[k] == 0)
      {
        i += 1;
        break;
      }
      String str;
      switch (this.mEventCmds[k])
      {
      default: 
        str = "   ??";
      }
      for (;;)
      {
        paramPrintWriter.print(paramString);
        TimeUtils.formatDuration(this.mEventTimes[k] - l, paramPrintWriter, 19);
        paramPrintWriter.print(" ");
        paramPrintWriter.print(str);
        paramPrintWriter.print(": ");
        UserHandle.formatUid(paramPrintWriter, m);
        paramPrintWriter.print(" ");
        paramPrintWriter.println(this.mEventTags[k]);
        break;
        str = "START";
        continue;
        str = " STOP";
      }
    }
    return true;
  }
  
  public float getLoadFactor(JobStatus paramJobStatus)
  {
    int i = paramJobStatus.getSourceUid();
    paramJobStatus = paramJobStatus.getSourcePackageName();
    PackageEntry localPackageEntry = this.mCurDataSet.getEntry(i, paramJobStatus);
    if (this.mLastDataSets[0] != null) {}
    for (paramJobStatus = this.mLastDataSets[0].getEntry(i, paramJobStatus); (localPackageEntry == null) && (paramJobStatus == null); paramJobStatus = null) {
      return 0.0F;
    }
    long l5 = SystemClock.uptimeMillis();
    long l1 = 0L;
    if (localPackageEntry != null) {
      l1 = 0L + (localPackageEntry.getActiveTime(l5) + localPackageEntry.getPendingTime(l5));
    }
    long l4 = this.mCurDataSet.getTotalTime(l5);
    long l3 = l4;
    long l2 = l1;
    if (paramJobStatus != null)
    {
      l2 = l1 + (paramJobStatus.getActiveTime(l5) + paramJobStatus.getPendingTime(l5));
      l3 = l4 + this.mLastDataSets[0].getTotalTime(l5);
    }
    return (float)l2 / (float)l3;
  }
  
  public void noteActive(JobStatus paramJobStatus)
  {
    long l = SystemClock.uptimeMillis();
    rebatchIfNeeded(l);
    if (paramJobStatus.lastEvaluatedPriority >= 40) {
      this.mCurDataSet.incActiveTop(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
    }
    for (;;)
    {
      addEvent(1, paramJobStatus.getSourceUid(), paramJobStatus.getBatteryName());
      return;
      this.mCurDataSet.incActive(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
    }
  }
  
  public void noteConcurrency(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.mCurDataSet.mMaxTotalActive) {
      this.mCurDataSet.mMaxTotalActive = paramInt1;
    }
    if (paramInt2 > this.mCurDataSet.mMaxFgActive) {
      this.mCurDataSet.mMaxFgActive = paramInt2;
    }
  }
  
  public void noteInactive(JobStatus paramJobStatus)
  {
    long l = SystemClock.uptimeMillis();
    if (paramJobStatus.lastEvaluatedPriority >= 40) {
      this.mCurDataSet.decActiveTop(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
    }
    for (;;)
    {
      rebatchIfNeeded(l);
      addEvent(2, paramJobStatus.getSourceUid(), paramJobStatus.getBatteryName());
      return;
      this.mCurDataSet.decActive(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
    }
  }
  
  public void noteNonpending(JobStatus paramJobStatus)
  {
    long l = SystemClock.uptimeMillis();
    this.mCurDataSet.decPending(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
    rebatchIfNeeded(l);
  }
  
  public void notePending(JobStatus paramJobStatus)
  {
    long l = SystemClock.uptimeMillis();
    rebatchIfNeeded(l);
    this.mCurDataSet.incPending(paramJobStatus.getSourceUid(), paramJobStatus.getSourcePackageName(), l);
  }
  
  void rebatchIfNeeded(long paramLong)
  {
    long l = this.mCurDataSet.getTotalTime(paramLong);
    if (l > 1800000L)
    {
      DataSet localDataSet = this.mCurDataSet;
      localDataSet.mSummedTime = l;
      this.mCurDataSet = new DataSet();
      localDataSet.finish(this.mCurDataSet, paramLong);
      System.arraycopy(this.mLastDataSets, 0, this.mLastDataSets, 1, this.mLastDataSets.length - 1);
      this.mLastDataSets[0] = localDataSet;
    }
  }
  
  static final class DataSet
  {
    final SparseArray<ArrayMap<String, JobPackageTracker.PackageEntry>> mEntries = new SparseArray();
    int mMaxFgActive;
    int mMaxTotalActive;
    final long mStartClockTime;
    final long mStartElapsedTime;
    final long mStartUptimeTime;
    long mSummedTime;
    
    public DataSet()
    {
      this.mStartUptimeTime = SystemClock.uptimeMillis();
      this.mStartElapsedTime = SystemClock.elapsedRealtime();
      this.mStartClockTime = System.currentTimeMillis();
    }
    
    public DataSet(DataSet paramDataSet)
    {
      this.mStartUptimeTime = paramDataSet.mStartUptimeTime;
      this.mStartElapsedTime = paramDataSet.mStartElapsedTime;
      this.mStartClockTime = paramDataSet.mStartClockTime;
    }
    
    private JobPackageTracker.PackageEntry getOrCreateEntry(int paramInt, String paramString)
    {
      Object localObject2 = (ArrayMap)this.mEntries.get(paramInt);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayMap();
        this.mEntries.put(paramInt, localObject1);
      }
      JobPackageTracker.PackageEntry localPackageEntry = (JobPackageTracker.PackageEntry)((ArrayMap)localObject1).get(paramString);
      localObject2 = localPackageEntry;
      if (localPackageEntry == null)
      {
        localObject2 = new JobPackageTracker.PackageEntry();
        ((ArrayMap)localObject1).put(paramString, localObject2);
      }
      return (JobPackageTracker.PackageEntry)localObject2;
    }
    
    void addTo(DataSet paramDataSet, long paramLong)
    {
      paramDataSet.mSummedTime += getTotalTime(paramLong);
      int i = this.mEntries.size() - 1;
      while (i >= 0)
      {
        ArrayMap localArrayMap = (ArrayMap)this.mEntries.valueAt(i);
        int j = localArrayMap.size() - 1;
        while (j >= 0)
        {
          JobPackageTracker.PackageEntry localPackageEntry1 = (JobPackageTracker.PackageEntry)localArrayMap.valueAt(j);
          JobPackageTracker.PackageEntry localPackageEntry2 = paramDataSet.getOrCreateEntry(this.mEntries.keyAt(i), (String)localArrayMap.keyAt(j));
          localPackageEntry2.pastActiveTime += localPackageEntry1.pastActiveTime;
          localPackageEntry2.activeCount += localPackageEntry1.activeCount;
          localPackageEntry2.pastActiveTopTime += localPackageEntry1.pastActiveTopTime;
          localPackageEntry2.activeTopCount += localPackageEntry1.activeTopCount;
          localPackageEntry2.pastPendingTime += localPackageEntry1.pastPendingTime;
          localPackageEntry2.pendingCount += localPackageEntry1.pendingCount;
          if (localPackageEntry1.activeNesting > 0)
          {
            localPackageEntry2.pastActiveTime += paramLong - localPackageEntry1.activeStartTime;
            localPackageEntry2.hadActive = true;
          }
          if (localPackageEntry1.activeTopNesting > 0)
          {
            localPackageEntry2.pastActiveTopTime += paramLong - localPackageEntry1.activeTopStartTime;
            localPackageEntry2.hadActiveTop = true;
          }
          if (localPackageEntry1.pendingNesting > 0)
          {
            localPackageEntry2.pastPendingTime += paramLong - localPackageEntry1.pendingStartTime;
            localPackageEntry2.hadPending = true;
          }
          j -= 1;
        }
        i -= 1;
      }
      if (this.mMaxTotalActive > paramDataSet.mMaxTotalActive) {
        paramDataSet.mMaxTotalActive = this.mMaxTotalActive;
      }
      if (this.mMaxFgActive > paramDataSet.mMaxFgActive) {
        paramDataSet.mMaxFgActive = this.mMaxFgActive;
      }
    }
    
    void decActive(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.activeNesting == 1) {
        paramString.pastActiveTime += paramLong - paramString.activeStartTime;
      }
      paramString.activeNesting -= 1;
    }
    
    void decActiveTop(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.activeTopNesting == 1) {
        paramString.pastActiveTopTime += paramLong - paramString.activeTopStartTime;
      }
      paramString.activeTopNesting -= 1;
    }
    
    void decPending(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.pendingNesting == 1) {
        paramString.pastPendingTime += paramLong - paramString.pendingStartTime;
      }
      paramString.pendingNesting -= 1;
    }
    
    void dump(PrintWriter paramPrintWriter, String paramString1, String paramString2, long paramLong1, long paramLong2, int paramInt)
    {
      long l = getTotalTime(paramLong1);
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print(" at ");
      paramPrintWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", this.mStartClockTime).toString());
      paramPrintWriter.print(" (");
      TimeUtils.formatDuration(this.mStartElapsedTime, paramLong2, paramPrintWriter);
      paramPrintWriter.print(") over ");
      TimeUtils.formatDuration(l, paramPrintWriter);
      paramPrintWriter.println(":");
      int k = this.mEntries.size();
      int i = 0;
      if (i < k)
      {
        int m = this.mEntries.keyAt(i);
        if ((paramInt != -1) && (paramInt != UserHandle.getAppId(m))) {}
        for (;;)
        {
          i += 1;
          break;
          paramString1 = (ArrayMap)this.mEntries.valueAt(i);
          int n = paramString1.size();
          int j = 0;
          while (j < n)
          {
            JobPackageTracker.PackageEntry localPackageEntry = (JobPackageTracker.PackageEntry)paramString1.valueAt(j);
            paramPrintWriter.print(paramString2);
            paramPrintWriter.print("  ");
            UserHandle.formatUid(paramPrintWriter, m);
            paramPrintWriter.print(" / ");
            paramPrintWriter.print((String)paramString1.keyAt(j));
            paramPrintWriter.print(":");
            printDuration(paramPrintWriter, l, localPackageEntry.getPendingTime(paramLong1), localPackageEntry.pendingCount, "pending");
            printDuration(paramPrintWriter, l, localPackageEntry.getActiveTime(paramLong1), localPackageEntry.activeCount, "active");
            printDuration(paramPrintWriter, l, localPackageEntry.getActiveTopTime(paramLong1), localPackageEntry.activeTopCount, "active-top");
            if ((localPackageEntry.pendingNesting > 0) || (localPackageEntry.hadPending)) {
              paramPrintWriter.print(" (pending)");
            }
            if ((localPackageEntry.activeNesting > 0) || (localPackageEntry.hadActive)) {
              paramPrintWriter.print(" (active)");
            }
            if ((localPackageEntry.activeTopNesting > 0) || (localPackageEntry.hadActiveTop)) {
              paramPrintWriter.print(" (active-top)");
            }
            paramPrintWriter.println();
            j += 1;
          }
        }
      }
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print("  Max concurrency: ");
      paramPrintWriter.print(this.mMaxTotalActive);
      paramPrintWriter.print(" total, ");
      paramPrintWriter.print(this.mMaxFgActive);
      paramPrintWriter.println(" foreground");
    }
    
    void finish(DataSet paramDataSet, long paramLong)
    {
      int i = this.mEntries.size() - 1;
      while (i >= 0)
      {
        ArrayMap localArrayMap = (ArrayMap)this.mEntries.valueAt(i);
        int j = localArrayMap.size() - 1;
        if (j >= 0)
        {
          JobPackageTracker.PackageEntry localPackageEntry1 = (JobPackageTracker.PackageEntry)localArrayMap.valueAt(j);
          if ((localPackageEntry1.activeNesting > 0) || (localPackageEntry1.activeTopNesting > 0)) {}
          for (;;)
          {
            JobPackageTracker.PackageEntry localPackageEntry2 = paramDataSet.getOrCreateEntry(this.mEntries.keyAt(i), (String)localArrayMap.keyAt(j));
            localPackageEntry2.activeStartTime = paramLong;
            localPackageEntry2.activeNesting = localPackageEntry1.activeNesting;
            localPackageEntry2.activeTopStartTime = paramLong;
            localPackageEntry2.activeTopNesting = localPackageEntry1.activeTopNesting;
            localPackageEntry2.pendingStartTime = paramLong;
            localPackageEntry2.pendingNesting = localPackageEntry1.pendingNesting;
            if (localPackageEntry1.activeNesting > 0)
            {
              localPackageEntry1.pastActiveTime += paramLong - localPackageEntry1.activeStartTime;
              localPackageEntry1.activeNesting = 0;
            }
            if (localPackageEntry1.activeTopNesting > 0)
            {
              localPackageEntry1.pastActiveTopTime += paramLong - localPackageEntry1.activeTopStartTime;
              localPackageEntry1.activeTopNesting = 0;
            }
            if (localPackageEntry1.pendingNesting > 0)
            {
              localPackageEntry1.pastPendingTime += paramLong - localPackageEntry1.pendingStartTime;
              localPackageEntry1.pendingNesting = 0;
            }
            do
            {
              j -= 1;
              break;
            } while (localPackageEntry1.pendingNesting <= 0);
          }
        }
        i -= 1;
      }
    }
    
    public JobPackageTracker.PackageEntry getEntry(int paramInt, String paramString)
    {
      ArrayMap localArrayMap = (ArrayMap)this.mEntries.get(paramInt);
      if (localArrayMap == null) {
        return null;
      }
      return (JobPackageTracker.PackageEntry)localArrayMap.get(paramString);
    }
    
    long getTotalTime(long paramLong)
    {
      if (this.mSummedTime > 0L) {
        return this.mSummedTime;
      }
      return paramLong - this.mStartUptimeTime;
    }
    
    void incActive(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.activeNesting == 0)
      {
        paramString.activeStartTime = paramLong;
        paramString.activeCount += 1;
      }
      paramString.activeNesting += 1;
    }
    
    void incActiveTop(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.activeTopNesting == 0)
      {
        paramString.activeTopStartTime = paramLong;
        paramString.activeTopCount += 1;
      }
      paramString.activeTopNesting += 1;
    }
    
    void incPending(int paramInt, String paramString, long paramLong)
    {
      paramString = getOrCreateEntry(paramInt, paramString);
      if (paramString.pendingNesting == 0)
      {
        paramString.pendingStartTime = paramLong;
        paramString.pendingCount += 1;
      }
      paramString.pendingNesting += 1;
    }
    
    void printDuration(PrintWriter paramPrintWriter, long paramLong1, long paramLong2, int paramInt, String paramString)
    {
      int i = (int)(100.0F * ((float)paramLong2 / (float)paramLong1) + 0.5F);
      if (i > 0)
      {
        paramPrintWriter.print(" ");
        paramPrintWriter.print(i);
        paramPrintWriter.print("% ");
        paramPrintWriter.print(paramInt);
        paramPrintWriter.print("x ");
        paramPrintWriter.print(paramString);
      }
      while (paramInt <= 0) {
        return;
      }
      paramPrintWriter.print(" ");
      paramPrintWriter.print(paramInt);
      paramPrintWriter.print("x ");
      paramPrintWriter.print(paramString);
    }
  }
  
  static final class PackageEntry
  {
    int activeCount;
    int activeNesting;
    long activeStartTime;
    int activeTopCount;
    int activeTopNesting;
    long activeTopStartTime;
    boolean hadActive;
    boolean hadActiveTop;
    boolean hadPending;
    long pastActiveTime;
    long pastActiveTopTime;
    long pastPendingTime;
    int pendingCount;
    int pendingNesting;
    long pendingStartTime;
    
    public long getActiveTime(long paramLong)
    {
      long l2 = this.pastActiveTime;
      long l1 = l2;
      if (this.activeNesting > 0) {
        l1 = l2 + (paramLong - this.activeStartTime);
      }
      return l1;
    }
    
    public long getActiveTopTime(long paramLong)
    {
      long l2 = this.pastActiveTopTime;
      long l1 = l2;
      if (this.activeTopNesting > 0) {
        l1 = l2 + (paramLong - this.activeTopStartTime);
      }
      return l1;
    }
    
    public long getPendingTime(long paramLong)
    {
      long l2 = this.pastPendingTime;
      long l1 = l2;
      if (this.pendingNesting > 0) {
        l1 = l2 + (paramLong - this.pendingStartTime);
      }
      return l1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/JobPackageTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */