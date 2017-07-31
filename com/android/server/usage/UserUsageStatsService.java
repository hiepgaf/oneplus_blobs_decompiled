package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.TimeSparseArray;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class UserUsageStatsService
{
  private static final boolean DEBUG = false;
  private static final long[] INTERVAL_LENGTH;
  private static final String TAG = "UsageStatsService";
  private static final UsageStatsDatabase.StatCombiner<ConfigurationStats> sConfigStatsCombiner = new UsageStatsDatabase.StatCombiner()
  {
    public void combine(IntervalStats paramAnonymousIntervalStats, boolean paramAnonymousBoolean, List<ConfigurationStats> paramAnonymousList)
    {
      if (!paramAnonymousBoolean)
      {
        paramAnonymousList.addAll(paramAnonymousIntervalStats.configurations.values());
        return;
      }
      int j = paramAnonymousIntervalStats.configurations.size();
      int i = 0;
      while (i < j)
      {
        paramAnonymousList.add(new ConfigurationStats((ConfigurationStats)paramAnonymousIntervalStats.configurations.valueAt(i)));
        i += 1;
      }
    }
  };
  private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final int sDateFormatFlags = 131093;
  private static final UsageStatsDatabase.StatCombiner<UsageStats> sUsageStatsCombiner;
  private final Context mContext;
  private final IntervalStats[] mCurrentStats;
  private final UnixCalendar mDailyExpiryDate;
  private final UsageStatsDatabase mDatabase;
  private final StatsUpdatedListener mListener;
  private final String mLogPrefix;
  private boolean mStatsChanged = false;
  private final int mUserId;
  
  static
  {
    INTERVAL_LENGTH = new long[] { 86400000L, 604800000L, 2592000000L, 31536000000L };
    sUsageStatsCombiner = new UsageStatsDatabase.StatCombiner()
    {
      public void combine(IntervalStats paramAnonymousIntervalStats, boolean paramAnonymousBoolean, List<UsageStats> paramAnonymousList)
      {
        if (!paramAnonymousBoolean)
        {
          paramAnonymousList.addAll(paramAnonymousIntervalStats.packageStats.values());
          return;
        }
        int j = paramAnonymousIntervalStats.packageStats.size();
        int i = 0;
        while (i < j)
        {
          paramAnonymousList.add(new UsageStats((UsageStats)paramAnonymousIntervalStats.packageStats.valueAt(i)));
          i += 1;
        }
      }
    };
  }
  
  UserUsageStatsService(Context paramContext, int paramInt, File paramFile, StatsUpdatedListener paramStatsUpdatedListener)
  {
    this.mContext = paramContext;
    this.mDailyExpiryDate = new UnixCalendar(0L);
    this.mDatabase = new UsageStatsDatabase(paramFile);
    this.mCurrentStats = new IntervalStats[4];
    this.mListener = paramStatsUpdatedListener;
    this.mLogPrefix = ("User[" + Integer.toString(paramInt) + "] ");
    this.mUserId = paramInt;
  }
  
  private static String eventToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 0: 
      return "NONE";
    case 2: 
      return "MOVE_TO_BACKGROUND";
    case 1: 
      return "MOVE_TO_FOREGROUND";
    case 3: 
      return "END_OF_DAY";
    case 4: 
      return "CONTINUE_PREVIOUS_DAY";
    case 5: 
      return "CONFIGURATION_CHANGE";
    case 6: 
      return "SYSTEM_INTERACTION";
    case 7: 
      return "USER_INTERACTION";
    }
    return "SHORTCUT_INVOCATION";
  }
  
  private String formatDateTime(long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {
      return "\"" + DateUtils.formatDateTime(this.mContext, paramLong, 131093) + "\"";
    }
    return Long.toString(paramLong);
  }
  
  private String formatElapsedTime(long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {
      return "\"" + DateUtils.formatElapsedTime(paramLong / 1000L) + "\"";
    }
    return Long.toString(paramLong);
  }
  
  private static String intervalToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "?";
    case 0: 
      return "daily";
    case 1: 
      return "weekly";
    case 2: 
      return "monthly";
    }
    return "yearly";
  }
  
  private void loadActiveStats(long paramLong)
  {
    int i = 0;
    if (i < this.mCurrentStats.length)
    {
      IntervalStats localIntervalStats = this.mDatabase.getLatestUsageStats(i);
      if ((localIntervalStats != null) && (paramLong - 500L >= localIntervalStats.endTime) && (paramLong < localIntervalStats.beginTime + INTERVAL_LENGTH[i])) {
        this.mCurrentStats[i] = localIntervalStats;
      }
      for (;;)
      {
        i += 1;
        break;
        this.mCurrentStats[i] = new IntervalStats();
        this.mCurrentStats[i].beginTime = paramLong;
        this.mCurrentStats[i].endTime = (1L + paramLong);
      }
    }
    this.mStatsChanged = false;
    updateRolloverDeadline();
    this.mListener.onStatsReloaded();
  }
  
  private void notifyNewUpdate()
  {
    this.mListener.onNewUpdate(this.mUserId);
  }
  
  private void notifyStatsChanged()
  {
    if (!this.mStatsChanged)
    {
      this.mStatsChanged = true;
      this.mListener.onStatsUpdated();
    }
  }
  
  private <T> List<T> queryStats(int paramInt, long paramLong1, long paramLong2, UsageStatsDatabase.StatCombiner<T> paramStatCombiner)
  {
    int i = paramInt;
    if (paramInt == 4)
    {
      paramInt = this.mDatabase.findBestFitBucket(paramLong1, paramLong2);
      i = paramInt;
      if (paramInt < 0) {
        i = 0;
      }
    }
    if ((i < 0) || (i >= this.mCurrentStats.length)) {
      return null;
    }
    IntervalStats localIntervalStats = this.mCurrentStats[i];
    if (paramLong1 >= localIntervalStats.endTime) {
      return null;
    }
    long l = Math.min(localIntervalStats.beginTime, paramLong2);
    List localList = this.mDatabase.queryUsageStats(i, paramLong1, l, paramStatCombiner);
    Object localObject = localList;
    if (paramLong1 < localIntervalStats.endTime)
    {
      localObject = localList;
      if (paramLong2 > localIntervalStats.beginTime)
      {
        localObject = localList;
        if (localList == null) {
          localObject = new ArrayList();
        }
        paramStatCombiner.combine(localIntervalStats, true, (List)localObject);
      }
    }
    return (List<T>)localObject;
  }
  
  private void rolloverStats(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    Slog.i("UsageStatsService", this.mLogPrefix + "Rolling over usage stats");
    Configuration localConfiguration = this.mCurrentStats[0].activeConfiguration;
    ArraySet localArraySet = new ArraySet();
    Object localObject1 = this.mCurrentStats;
    int i = 0;
    int k = localObject1.length;
    IntervalStats[] arrayOfIntervalStats;
    int m;
    int j;
    Object localObject2;
    while (i < k)
    {
      arrayOfIntervalStats = localObject1[i];
      m = arrayOfIntervalStats.packageStats.size();
      j = 0;
      while (j < m)
      {
        localObject2 = (UsageStats)arrayOfIntervalStats.packageStats.valueAt(j);
        if ((((UsageStats)localObject2).mLastEvent == 1) || (((UsageStats)localObject2).mLastEvent == 4))
        {
          localArraySet.add(((UsageStats)localObject2).mPackageName);
          arrayOfIntervalStats.update(((UsageStats)localObject2).mPackageName, this.mDailyExpiryDate.getTimeInMillis() - 1L, 3);
          notifyStatsChanged();
        }
        j += 1;
      }
      arrayOfIntervalStats.updateConfigurationStats(null, this.mDailyExpiryDate.getTimeInMillis() - 1L);
      i += 1;
    }
    persistActiveStats();
    this.mDatabase.prune(paramLong);
    loadActiveStats(paramLong);
    k = localArraySet.size();
    i = 0;
    while (i < k)
    {
      localObject1 = (String)localArraySet.valueAt(i);
      paramLong = this.mCurrentStats[0].beginTime;
      arrayOfIntervalStats = this.mCurrentStats;
      j = 0;
      m = arrayOfIntervalStats.length;
      while (j < m)
      {
        localObject2 = arrayOfIntervalStats[j];
        ((IntervalStats)localObject2).update((String)localObject1, paramLong, 4);
        ((IntervalStats)localObject2).updateConfigurationStats(localConfiguration, paramLong);
        notifyStatsChanged();
        j += 1;
      }
      i += 1;
    }
    persistActiveStats();
    paramLong = SystemClock.elapsedRealtime();
    Slog.i("UsageStatsService", this.mLogPrefix + "Rolling over usage stats complete. Took " + (paramLong - l) + " milliseconds");
  }
  
  private void updateRolloverDeadline()
  {
    this.mDailyExpiryDate.setTimeInMillis(this.mCurrentStats[0].beginTime);
    this.mDailyExpiryDate.addDays(1);
    Slog.i("UsageStatsService", this.mLogPrefix + "Rollover scheduled @ " + sDateFormat.format(Long.valueOf(this.mDailyExpiryDate.getTimeInMillis())) + "(" + this.mDailyExpiryDate.getTimeInMillis() + ")");
  }
  
  void applyRestoredPayload(String paramString, byte[] paramArrayOfByte)
  {
    this.mDatabase.applyRestoredPayload(paramString, paramArrayOfByte);
  }
  
  void checkin(final IndentingPrintWriter paramIndentingPrintWriter)
  {
    this.mDatabase.checkinDailyFiles(new UsageStatsDatabase.CheckinAction()
    {
      public boolean checkin(IntervalStats paramAnonymousIntervalStats)
      {
        UserUsageStatsService.this.printIntervalStats(paramIndentingPrintWriter, paramAnonymousIntervalStats, false);
        return true;
      }
    });
  }
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    int i = 0;
    while (i < this.mCurrentStats.length)
    {
      paramIndentingPrintWriter.print("In-memory ");
      paramIndentingPrintWriter.print(intervalToString(i));
      paramIndentingPrintWriter.println(" stats");
      printIntervalStats(paramIndentingPrintWriter, this.mCurrentStats[i], true);
      i += 1;
    }
  }
  
  byte[] getBackupPayload(String paramString)
  {
    return this.mDatabase.getBackupPayload(paramString);
  }
  
  void init(long paramLong)
  {
    int m = 0;
    this.mDatabase.init(paramLong);
    int j = 0;
    int i = 0;
    int k;
    while (i < this.mCurrentStats.length)
    {
      this.mCurrentStats[i] = this.mDatabase.getLatestUsageStats(i);
      k = j;
      if (this.mCurrentStats[i] == null) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    IntervalStats[] arrayOfIntervalStats;
    if (j > 0)
    {
      if (j != this.mCurrentStats.length) {
        Slog.w("UsageStatsService", this.mLogPrefix + "Some stats have no latest available");
      }
      loadActiveStats(paramLong);
      arrayOfIntervalStats = this.mCurrentStats;
      k = arrayOfIntervalStats.length;
      i = m;
    }
    for (;;)
    {
      if (i >= k) {
        break label251;
      }
      IntervalStats localIntervalStats = arrayOfIntervalStats[i];
      m = localIntervalStats.packageStats.size();
      j = 0;
      for (;;)
      {
        if (j < m)
        {
          UsageStats localUsageStats = (UsageStats)localIntervalStats.packageStats.valueAt(j);
          if ((localUsageStats.mLastEvent == 1) || (localUsageStats.mLastEvent == 4))
          {
            localIntervalStats.update(localUsageStats.mPackageName, localIntervalStats.lastTimeSaved, 3);
            notifyStatsChanged();
          }
          j += 1;
          continue;
          updateRolloverDeadline();
          break;
        }
      }
      localIntervalStats.updateConfigurationStats(null, localIntervalStats.lastTimeSaved);
      i += 1;
    }
    label251:
    if (this.mDatabase.isNewUpdate()) {
      notifyNewUpdate();
    }
  }
  
  void onTimeChanged(long paramLong1, long paramLong2)
  {
    persistActiveStats();
    this.mDatabase.onTimeChanged(paramLong2 - paramLong1);
    loadActiveStats(paramLong2);
  }
  
  void persistActiveStats()
  {
    int i;
    if (this.mStatsChanged)
    {
      Slog.i("UsageStatsService", this.mLogPrefix + "Flushing usage stats to disk");
      i = 0;
    }
    try
    {
      while (i < this.mCurrentStats.length)
      {
        this.mDatabase.putUsageStats(i, this.mCurrentStats[i]);
        i += 1;
      }
      this.mStatsChanged = false;
      return;
    }
    catch (IOException localIOException)
    {
      Slog.e("UsageStatsService", this.mLogPrefix + "Failed to persist active stats", localIOException);
    }
  }
  
  void printIntervalStats(IndentingPrintWriter paramIndentingPrintWriter, IntervalStats paramIntervalStats, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramIndentingPrintWriter.printPair("timeRange", "\"" + DateUtils.formatDateRange(this.mContext, paramIntervalStats.beginTime, paramIntervalStats.endTime, 131093) + "\"");
    }
    Object localObject2;
    for (;;)
    {
      paramIndentingPrintWriter.println();
      paramIndentingPrintWriter.increaseIndent();
      paramIndentingPrintWriter.println("packages");
      paramIndentingPrintWriter.increaseIndent();
      localObject1 = paramIntervalStats.packageStats;
      j = ((ArrayMap)localObject1).size();
      i = 0;
      while (i < j)
      {
        localObject2 = (UsageStats)((ArrayMap)localObject1).valueAt(i);
        paramIndentingPrintWriter.printPair("package", ((UsageStats)localObject2).mPackageName);
        paramIndentingPrintWriter.printPair("totalTime", formatElapsedTime(((UsageStats)localObject2).mTotalTimeInForeground, paramBoolean));
        paramIndentingPrintWriter.printPair("lastTime", formatDateTime(((UsageStats)localObject2).mLastTimeUsed, paramBoolean));
        paramIndentingPrintWriter.println();
        i += 1;
      }
      paramIndentingPrintWriter.printPair("beginTime", Long.valueOf(paramIntervalStats.beginTime));
      paramIndentingPrintWriter.printPair("endTime", Long.valueOf(paramIntervalStats.endTime));
    }
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println("configurations");
    paramIndentingPrintWriter.increaseIndent();
    Object localObject1 = paramIntervalStats.configurations;
    int j = ((ArrayMap)localObject1).size();
    int i = 0;
    while (i < j)
    {
      localObject2 = (ConfigurationStats)((ArrayMap)localObject1).valueAt(i);
      paramIndentingPrintWriter.printPair("config", Configuration.resourceQualifierString(((ConfigurationStats)localObject2).mConfiguration));
      paramIndentingPrintWriter.printPair("totalTime", formatElapsedTime(((ConfigurationStats)localObject2).mTotalTimeActive, paramBoolean));
      paramIndentingPrintWriter.printPair("lastTime", formatDateTime(((ConfigurationStats)localObject2).mLastTimeActive, paramBoolean));
      paramIndentingPrintWriter.printPair("count", Integer.valueOf(((ConfigurationStats)localObject2).mActivationCount));
      paramIndentingPrintWriter.println();
      i += 1;
    }
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.println("events");
    paramIndentingPrintWriter.increaseIndent();
    paramIntervalStats = paramIntervalStats.events;
    if (paramIntervalStats != null) {}
    for (i = paramIntervalStats.size();; i = 0)
    {
      j = 0;
      while (j < i)
      {
        localObject1 = (UsageEvents.Event)paramIntervalStats.valueAt(j);
        paramIndentingPrintWriter.printPair("time", formatDateTime(((UsageEvents.Event)localObject1).mTimeStamp, paramBoolean));
        paramIndentingPrintWriter.printPair("type", eventToString(((UsageEvents.Event)localObject1).mEventType));
        paramIndentingPrintWriter.printPair("package", ((UsageEvents.Event)localObject1).mPackage);
        if (((UsageEvents.Event)localObject1).mClass != null) {
          paramIndentingPrintWriter.printPair("class", ((UsageEvents.Event)localObject1).mClass);
        }
        if (((UsageEvents.Event)localObject1).mConfiguration != null) {
          paramIndentingPrintWriter.printPair("config", Configuration.resourceQualifierString(((UsageEvents.Event)localObject1).mConfiguration));
        }
        if (((UsageEvents.Event)localObject1).mShortcutId != null) {
          paramIndentingPrintWriter.printPair("shortcutId", ((UsageEvents.Event)localObject1).mShortcutId);
        }
        paramIndentingPrintWriter.println();
        j += 1;
      }
    }
    paramIndentingPrintWriter.decreaseIndent();
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  List<ConfigurationStats> queryConfigurationStats(int paramInt, long paramLong1, long paramLong2)
  {
    return queryStats(paramInt, paramLong1, paramLong2, sConfigStatsCombiner);
  }
  
  UsageEvents queryEvents(final long paramLong1, long paramLong2)
  {
    final Object localObject = new ArraySet();
    List localList = queryStats(0, paramLong1, paramLong2, new UsageStatsDatabase.StatCombiner()
    {
      public void combine(IntervalStats paramAnonymousIntervalStats, boolean paramAnonymousBoolean, List<UsageEvents.Event> paramAnonymousList)
      {
        if (paramAnonymousIntervalStats.events == null) {
          return;
        }
        int i = paramAnonymousIntervalStats.events.closestIndexOnOrAfter(paramLong1);
        if (i < 0) {
          return;
        }
        int j = paramAnonymousIntervalStats.events.size();
        while (i < j)
        {
          if (paramAnonymousIntervalStats.events.keyAt(i) >= localObject) {
            return;
          }
          UsageEvents.Event localEvent = (UsageEvents.Event)paramAnonymousIntervalStats.events.valueAt(i);
          this.val$names.add(localEvent.mPackage);
          if (localEvent.mClass != null) {
            this.val$names.add(localEvent.mClass);
          }
          paramAnonymousList.add(localEvent);
          i += 1;
        }
      }
    });
    if ((localList == null) || (localList.isEmpty())) {
      return null;
    }
    localObject = (String[])((ArraySet)localObject).toArray(new String[((ArraySet)localObject).size()]);
    Arrays.sort((Object[])localObject);
    return new UsageEvents(localList, (String[])localObject);
  }
  
  List<UsageStats> queryUsageStats(int paramInt, long paramLong1, long paramLong2)
  {
    return queryStats(paramInt, paramLong1, paramLong2, sUsageStatsCombiner);
  }
  
  void reportEvent(UsageEvents.Event paramEvent)
  {
    int i = 0;
    if (paramEvent.mTimeStamp >= this.mDailyExpiryDate.getTimeInMillis()) {
      rolloverStats(paramEvent.mTimeStamp);
    }
    Object localObject1 = this.mCurrentStats[0];
    Configuration localConfiguration = paramEvent.mConfiguration;
    if ((paramEvent.mEventType == 5) && (((IntervalStats)localObject1).activeConfiguration != null)) {
      paramEvent.mConfiguration = Configuration.generateDelta(((IntervalStats)localObject1).activeConfiguration, localConfiguration);
    }
    if (((IntervalStats)localObject1).events == null) {
      ((IntervalStats)localObject1).events = new TimeSparseArray();
    }
    if (paramEvent.mEventType != 6) {
      ((IntervalStats)localObject1).events.put(paramEvent.mTimeStamp, paramEvent);
    }
    localObject1 = this.mCurrentStats;
    int j = localObject1.length;
    if (i < j)
    {
      Object localObject2 = localObject1[i];
      if (paramEvent.mEventType == 5) {
        ((IntervalStats)localObject2).updateConfigurationStats(localConfiguration, paramEvent.mTimeStamp);
      }
      for (;;)
      {
        i += 1;
        break;
        ((IntervalStats)localObject2).update(paramEvent.mPackage, paramEvent.mTimeStamp, paramEvent.mEventType);
      }
    }
    notifyStatsChanged();
  }
  
  static abstract interface StatsUpdatedListener
  {
    public abstract void onNewUpdate(int paramInt);
    
    public abstract void onStatsReloaded();
    
    public abstract void onStatsUpdated();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UserUsageStatsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */