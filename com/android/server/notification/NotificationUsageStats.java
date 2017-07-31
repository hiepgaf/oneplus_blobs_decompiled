package com.android.server.notification;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationUsageStats
{
  private static final boolean DEBUG = false;
  private static final String DEVICE_GLOBAL_STATS = "__global";
  private static final long EMIT_PERIOD = 14400000L;
  private static final AggregatedStats[] EMPTY_AGGREGATED_STATS = new AggregatedStats[0];
  private static final boolean ENABLE_AGGREGATED_IN_MEMORY_STATS = true;
  private static final boolean ENABLE_SQLITE_LOG = true;
  public static final int FOUR_HOURS = 14400000;
  private static final int MSG_EMIT = 1;
  private static final String TAG = "NotificationUsageStats";
  public static final int TEN_SECONDS = 10000;
  private final Context mContext;
  private final Handler mHandler;
  private long mLastEmitTime;
  private final SQLiteLog mSQLiteLog;
  private ArraySet<String> mStatExpiredkeys = new ArraySet();
  private final Map<String, AggregatedStats> mStats = new HashMap();
  private final ArrayDeque<AggregatedStats[]> mStatsArrays = new ArrayDeque();
  
  public NotificationUsageStats(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLastEmitTime = SystemClock.elapsedRealtime();
    this.mSQLiteLog = new SQLiteLog(paramContext);
    this.mHandler = new Handler(this.mContext.getMainLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        default: 
          Log.wtf("NotificationUsageStats", "Unknown message type: " + paramAnonymousMessage.what);
          return;
        }
        NotificationUsageStats.this.emit();
      }
    };
    this.mHandler.sendEmptyMessageDelayed(1, 14400000L);
  }
  
  private AggregatedStats[] getAggregatedStatsLocked(NotificationRecord paramNotificationRecord)
  {
    return getAggregatedStatsLocked(paramNotificationRecord.sbn.getPackageName());
  }
  
  private AggregatedStats[] getAggregatedStatsLocked(String paramString)
  {
    AggregatedStats[] arrayOfAggregatedStats2 = (AggregatedStats[])this.mStatsArrays.poll();
    AggregatedStats[] arrayOfAggregatedStats1 = arrayOfAggregatedStats2;
    if (arrayOfAggregatedStats2 == null) {
      arrayOfAggregatedStats1 = new AggregatedStats[2];
    }
    arrayOfAggregatedStats1[0] = getOrCreateAggregatedStatsLocked("__global");
    arrayOfAggregatedStats1[1] = getOrCreateAggregatedStatsLocked(paramString);
    return arrayOfAggregatedStats1;
  }
  
  private AggregatedStats getOrCreateAggregatedStatsLocked(String paramString)
  {
    AggregatedStats localAggregatedStats2 = (AggregatedStats)this.mStats.get(paramString);
    AggregatedStats localAggregatedStats1 = localAggregatedStats2;
    if (localAggregatedStats2 == null)
    {
      localAggregatedStats1 = new AggregatedStats(this.mContext, paramString);
      this.mStats.put(paramString, localAggregatedStats1);
    }
    localAggregatedStats1.mLastAccessTime = SystemClock.elapsedRealtime();
    return localAggregatedStats1;
  }
  
  private void releaseAggregatedStatsLocked(AggregatedStats[] paramArrayOfAggregatedStats)
  {
    int i = 0;
    while (i < paramArrayOfAggregatedStats.length)
    {
      paramArrayOfAggregatedStats[i] = null;
      i += 1;
    }
    this.mStatsArrays.offer(paramArrayOfAggregatedStats);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter)
  {
    try
    {
      Iterator localIterator = this.mStats.values().iterator();
      while (localIterator.hasNext())
      {
        AggregatedStats localAggregatedStats = (AggregatedStats)localIterator.next();
        if ((paramDumpFilter == null) || (paramDumpFilter.matches(localAggregatedStats.key))) {
          localAggregatedStats.dump(paramPrintWriter, paramString);
        }
      }
      paramPrintWriter.println(paramString + "mStatsArrays.size(): " + this.mStatsArrays.size());
    }
    finally {}
    paramPrintWriter.println(paramString + "mStats.size(): " + this.mStats.size());
    this.mSQLiteLog.dump(paramPrintWriter, paramString, paramDumpFilter);
  }
  
  /* Error */
  public JSONObject dumpJson(NotificationManagerService.DumpFilter paramDumpFilter)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 238	org/json/JSONObject
    //   5: dup
    //   6: invokespecial 239	org/json/JSONObject:<init>	()V
    //   9: astore_2
    //   10: new 241	org/json/JSONArray
    //   13: dup
    //   14: invokespecial 242	org/json/JSONArray:<init>	()V
    //   17: astore_3
    //   18: aload_0
    //   19: getfield 80	com/android/server/notification/NotificationUsageStats:mStats	Ljava/util/Map;
    //   22: invokeinterface 175 1 0
    //   27: invokeinterface 181 1 0
    //   32: astore 4
    //   34: aload 4
    //   36: invokeinterface 187 1 0
    //   41: ifeq +64 -> 105
    //   44: aload 4
    //   46: invokeinterface 190 1 0
    //   51: checkcast 11	com/android/server/notification/NotificationUsageStats$AggregatedStats
    //   54: astore 5
    //   56: aload_1
    //   57: ifnull +15 -> 72
    //   60: aload_1
    //   61: aload 5
    //   63: getfield 193	com/android/server/notification/NotificationUsageStats$AggregatedStats:key	Ljava/lang/String;
    //   66: invokevirtual 199	com/android/server/notification/NotificationManagerService$DumpFilter:matches	(Ljava/lang/String;)Z
    //   69: ifeq -35 -> 34
    //   72: aload_3
    //   73: aload 5
    //   75: invokevirtual 245	com/android/server/notification/NotificationUsageStats$AggregatedStats:dumpJson	()Lorg/json/JSONObject;
    //   78: invokevirtual 248	org/json/JSONArray:put	(Ljava/lang/Object;)Lorg/json/JSONArray;
    //   81: pop
    //   82: goto -48 -> 34
    //   85: astore_3
    //   86: aload_2
    //   87: ldc -6
    //   89: aload_0
    //   90: getfield 104	com/android/server/notification/NotificationUsageStats:mSQLiteLog	Lcom/android/server/notification/NotificationUsageStats$SQLiteLog;
    //   93: aload_1
    //   94: invokevirtual 252	com/android/server/notification/NotificationUsageStats$SQLiteLog:dumpJson	(Lcom/android/server/notification/NotificationManagerService$DumpFilter;)Lorg/json/JSONObject;
    //   97: invokevirtual 255	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   100: pop
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_2
    //   104: areturn
    //   105: aload_2
    //   106: ldc_w 257
    //   109: aload_3
    //   110: invokevirtual 255	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   113: pop
    //   114: goto -28 -> 86
    //   117: astore_1
    //   118: aload_0
    //   119: monitorexit
    //   120: aload_1
    //   121: athrow
    //   122: astore_1
    //   123: goto -22 -> 101
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	NotificationUsageStats
    //   0	126	1	paramDumpFilter	NotificationManagerService.DumpFilter
    //   9	97	2	localJSONObject	JSONObject
    //   17	56	3	localJSONArray	JSONArray
    //   85	25	3	localJSONException	JSONException
    //   32	13	4	localIterator	Iterator
    //   54	20	5	localAggregatedStats	AggregatedStats
    // Exception table:
    //   from	to	target	type
    //   10	34	85	org/json/JSONException
    //   34	56	85	org/json/JSONException
    //   60	72	85	org/json/JSONException
    //   72	82	85	org/json/JSONException
    //   105	114	85	org/json/JSONException
    //   2	10	117	finally
    //   10	34	117	finally
    //   34	56	117	finally
    //   60	72	117	finally
    //   72	82	117	finally
    //   86	101	117	finally
    //   105	114	117	finally
    //   86	101	122	org/json/JSONException
  }
  
  public void emit()
  {
    String str;
    try
    {
      getOrCreateAggregatedStatsLocked("__global").emit();
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessageDelayed(1, 14400000L);
      Iterator localIterator1 = this.mStats.keySet().iterator();
      while (localIterator1.hasNext())
      {
        str = (String)localIterator1.next();
        if (((AggregatedStats)this.mStats.get(str)).mLastAccessTime < this.mLastEmitTime) {
          this.mStatExpiredkeys.add(str);
        }
      }
      localIterator2 = this.mStatExpiredkeys.iterator();
    }
    finally {}
    Iterator localIterator2;
    while (localIterator2.hasNext())
    {
      str = (String)localIterator2.next();
      this.mStats.remove(str);
    }
    this.mStatExpiredkeys.clear();
    this.mLastEmitTime = SystemClock.elapsedRealtime();
  }
  
  public float getAppEnqueueRate(String paramString)
  {
    try
    {
      paramString = getOrCreateAggregatedStatsLocked(paramString);
      if (paramString != null)
      {
        float f = paramString.getEnqueueRate(SystemClock.elapsedRealtime());
        return f;
      }
      return 0.0F;
    }
    finally {}
  }
  
  public void registerBlocked(NotificationRecord paramNotificationRecord)
  {
    try
    {
      paramNotificationRecord = getAggregatedStatsLocked(paramNotificationRecord);
      int i = 0;
      int j = paramNotificationRecord.length;
      while (i < j)
      {
        Object localObject = paramNotificationRecord[i];
        ((AggregatedStats)localObject).numBlocked += 1;
        i += 1;
      }
      releaseAggregatedStatsLocked(paramNotificationRecord);
      return;
    }
    finally {}
  }
  
  public void registerClickedByUser(NotificationRecord paramNotificationRecord)
  {
    try
    {
      MetricsLogger.histogram(this.mContext, "note_click_longevity", (int)(System.currentTimeMillis() - paramNotificationRecord.getRankingTimeMs()) / 60000);
      paramNotificationRecord.stats.onClick();
      this.mSQLiteLog.logClicked(paramNotificationRecord);
      return;
    }
    finally
    {
      paramNotificationRecord = finally;
      throw paramNotificationRecord;
    }
  }
  
  public void registerDismissedByUser(NotificationRecord paramNotificationRecord)
  {
    try
    {
      MetricsLogger.histogram(this.mContext, "note_dismiss_longevity", (int)(System.currentTimeMillis() - paramNotificationRecord.getRankingTimeMs()) / 60000);
      paramNotificationRecord.stats.onDismiss();
      this.mSQLiteLog.logDismissed(paramNotificationRecord);
      return;
    }
    finally
    {
      paramNotificationRecord = finally;
      throw paramNotificationRecord;
    }
  }
  
  public void registerEnqueuedByApp(String paramString)
  {
    try
    {
      paramString = getAggregatedStatsLocked(paramString);
      int i = 0;
      int j = paramString.length;
      while (i < j)
      {
        Object localObject = paramString[i];
        ((AggregatedStats)localObject).numEnqueuedByApp += 1;
        i += 1;
      }
      releaseAggregatedStatsLocked(paramString);
      return;
    }
    finally {}
  }
  
  public void registerOverCountQuota(String paramString)
  {
    try
    {
      paramString = getAggregatedStatsLocked(paramString);
      int i = 0;
      int j = paramString.length;
      while (i < j)
      {
        Object localObject = paramString[i];
        ((AggregatedStats)localObject).numQuotaViolations += 1;
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  public void registerOverRateQuota(String paramString)
  {
    try
    {
      paramString = getAggregatedStatsLocked(paramString);
      int i = 0;
      int j = paramString.length;
      while (i < j)
      {
        Object localObject = paramString[i];
        ((AggregatedStats)localObject).numRateViolations += 1;
        i += 1;
      }
      return;
    }
    finally {}
  }
  
  public void registerPeopleAffinity(NotificationRecord paramNotificationRecord, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    for (;;)
    {
      int i;
      try
      {
        paramNotificationRecord = getAggregatedStatsLocked(paramNotificationRecord);
        i = 0;
        int j = paramNotificationRecord.length;
        if (i < j)
        {
          Object localObject = paramNotificationRecord[i];
          if (paramBoolean1) {
            ((AggregatedStats)localObject).numWithValidPeople += 1;
          }
          if (paramBoolean2) {
            ((AggregatedStats)localObject).numWithStaredPeople += 1;
          }
          if (paramBoolean3) {
            ((AggregatedStats)localObject).numPeopleCacheHit += 1;
          } else {
            ((AggregatedStats)localObject).numPeopleCacheMiss += 1;
          }
        }
      }
      finally {}
      releaseAggregatedStatsLocked(paramNotificationRecord);
      return;
      i += 1;
    }
  }
  
  public void registerPostedByApp(NotificationRecord paramNotificationRecord)
  {
    try
    {
      long l = SystemClock.elapsedRealtime();
      paramNotificationRecord.stats.posttimeElapsedMs = l;
      AggregatedStats[] arrayOfAggregatedStats = getAggregatedStatsLocked(paramNotificationRecord);
      int i = 0;
      int j = arrayOfAggregatedStats.length;
      while (i < j)
      {
        AggregatedStats localAggregatedStats = arrayOfAggregatedStats[i];
        localAggregatedStats.numPostedByApp += 1;
        localAggregatedStats.updateInterarrivalEstimate(l);
        localAggregatedStats.countApiUse(paramNotificationRecord);
        i += 1;
      }
      releaseAggregatedStatsLocked(arrayOfAggregatedStats);
      this.mSQLiteLog.logPosted(paramNotificationRecord);
      return;
    }
    finally {}
  }
  
  public void registerRemovedByApp(NotificationRecord paramNotificationRecord)
  {
    try
    {
      paramNotificationRecord.stats.onRemoved();
      AggregatedStats[] arrayOfAggregatedStats = getAggregatedStatsLocked(paramNotificationRecord);
      int i = 0;
      int j = arrayOfAggregatedStats.length;
      while (i < j)
      {
        AggregatedStats localAggregatedStats = arrayOfAggregatedStats[i];
        localAggregatedStats.numRemovedByApp += 1;
        i += 1;
      }
      releaseAggregatedStatsLocked(arrayOfAggregatedStats);
      this.mSQLiteLog.logRemoved(paramNotificationRecord);
      return;
    }
    finally {}
  }
  
  public void registerSuspendedByAdmin(NotificationRecord paramNotificationRecord)
  {
    try
    {
      paramNotificationRecord = getAggregatedStatsLocked(paramNotificationRecord);
      int i = 0;
      int j = paramNotificationRecord.length;
      while (i < j)
      {
        Object localObject = paramNotificationRecord[i];
        ((AggregatedStats)localObject).numSuspendedByAdmin += 1;
        i += 1;
      }
      releaseAggregatedStatsLocked(paramNotificationRecord);
      return;
    }
    finally {}
  }
  
  public void registerUpdatedByApp(NotificationRecord paramNotificationRecord1, NotificationRecord paramNotificationRecord2)
  {
    try
    {
      paramNotificationRecord1.stats.updateFrom(paramNotificationRecord2.stats);
      paramNotificationRecord2 = getAggregatedStatsLocked(paramNotificationRecord1);
      int i = 0;
      int j = paramNotificationRecord2.length;
      while (i < j)
      {
        Object localObject = paramNotificationRecord2[i];
        ((AggregatedStats)localObject).numUpdatedByApp += 1;
        ((AggregatedStats)localObject).updateInterarrivalEstimate(SystemClock.elapsedRealtime());
        ((AggregatedStats)localObject).countApiUse(paramNotificationRecord1);
        i += 1;
      }
      releaseAggregatedStatsLocked(paramNotificationRecord2);
      this.mSQLiteLog.logPosted(paramNotificationRecord1);
      return;
    }
    finally {}
  }
  
  public static class Aggregate
  {
    double avg;
    long numSamples;
    double sum2;
    double var;
    
    public void addSample(long paramLong)
    {
      this.numSamples += 1L;
      double d1 = this.numSamples;
      double d2 = paramLong - this.avg;
      this.avg += 1.0D / d1 * d2;
      this.sum2 += (d1 - 1.0D) / d1 * d2 * d2;
      if (this.numSamples == 1L) {}
      for (d1 = 1.0D;; d1 -= 1.0D)
      {
        this.var = (this.sum2 / d1);
        return;
      }
    }
    
    public String toString()
    {
      return "Aggregate{numSamples=" + this.numSamples + ", avg=" + this.avg + ", var=" + this.var + '}';
    }
  }
  
  private static class AggregatedStats
  {
    public RateEstimator enqueueRate;
    public NotificationUsageStats.ImportanceHistogram finalImportance;
    public final String key;
    private final Context mContext;
    private final long mCreated;
    public long mLastAccessTime;
    private AggregatedStats mPrevious;
    public NotificationUsageStats.ImportanceHistogram noisyImportance;
    public int numAutoCancel;
    public int numBlocked;
    public int numEnqueuedByApp;
    public int numForegroundService;
    public int numInterrupt;
    public int numOngoing;
    public int numPeopleCacheHit;
    public int numPeopleCacheMiss;
    public int numPostedByApp;
    public int numPrivate;
    public int numQuotaViolations;
    public int numRateViolations;
    public int numRemovedByApp;
    public int numSecret;
    public int numSuspendedByAdmin;
    public int numUpdatedByApp;
    public int numWithActions;
    public int numWithBigPicture;
    public int numWithBigText;
    public int numWithInbox;
    public int numWithInfoText;
    public int numWithLargeIcon;
    public int numWithMediaSession;
    public int numWithStaredPeople;
    public int numWithSubText;
    public int numWithText;
    public int numWithTitle;
    public int numWithValidPeople;
    public NotificationUsageStats.ImportanceHistogram quietImportance;
    
    public AggregatedStats(Context paramContext, String paramString)
    {
      this.key = paramString;
      this.mContext = paramContext;
      this.mCreated = SystemClock.elapsedRealtime();
      this.noisyImportance = new NotificationUsageStats.ImportanceHistogram(paramContext, "note_imp_noisy_");
      this.quietImportance = new NotificationUsageStats.ImportanceHistogram(paramContext, "note_imp_quiet_");
      this.finalImportance = new NotificationUsageStats.ImportanceHistogram(paramContext, "note_importance_");
      this.enqueueRate = new RateEstimator();
    }
    
    private void maybePut(JSONObject paramJSONObject, String paramString, float paramFloat)
      throws JSONException
    {
      if (paramFloat > 0.0D) {
        paramJSONObject.put(paramString, paramFloat);
      }
    }
    
    private void maybePut(JSONObject paramJSONObject, String paramString, int paramInt)
      throws JSONException
    {
      if (paramInt > 0) {
        paramJSONObject.put(paramString, paramInt);
      }
    }
    
    private String toStringWithIndent(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString).append("AggregatedStats{\n");
      String str = paramString + "  ";
      localStringBuilder.append(str);
      localStringBuilder.append("key='").append(this.key).append("',\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numEnqueuedByApp=").append(this.numEnqueuedByApp).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numPostedByApp=").append(this.numPostedByApp).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numUpdatedByApp=").append(this.numUpdatedByApp).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numRemovedByApp=").append(this.numRemovedByApp).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numPeopleCacheHit=").append(this.numPeopleCacheHit).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithStaredPeople=").append(this.numWithStaredPeople).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithValidPeople=").append(this.numWithValidPeople).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numPeopleCacheMiss=").append(this.numPeopleCacheMiss).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numBlocked=").append(this.numBlocked).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numSuspendedByAdmin=").append(this.numSuspendedByAdmin).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithActions=").append(this.numWithActions).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numPrivate=").append(this.numPrivate).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numSecret=").append(this.numSecret).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numInterrupt=").append(this.numInterrupt).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithBigText=").append(this.numWithBigText).append(",\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithBigPicture=").append(this.numWithBigPicture).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numForegroundService=").append(this.numForegroundService).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numOngoing=").append(this.numOngoing).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numAutoCancel=").append(this.numAutoCancel).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithLargeIcon=").append(this.numWithLargeIcon).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithInbox=").append(this.numWithInbox).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithMediaSession=").append(this.numWithMediaSession).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithTitle=").append(this.numWithTitle).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithText=").append(this.numWithText).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithSubText=").append(this.numWithSubText).append("\n");
      localStringBuilder.append(str);
      localStringBuilder.append("numWithInfoText=").append(this.numWithInfoText).append("\n");
      localStringBuilder.append("numRateViolations=").append(this.numRateViolations).append("\n");
      localStringBuilder.append("numQuotaViolations=").append(this.numQuotaViolations).append("\n");
      localStringBuilder.append(str).append(this.noisyImportance.toString()).append("\n");
      localStringBuilder.append(str).append(this.quietImportance.toString()).append("\n");
      localStringBuilder.append(str).append(this.finalImportance.toString()).append("\n");
      localStringBuilder.append(paramString).append("}");
      return localStringBuilder.toString();
    }
    
    public void countApiUse(NotificationRecord paramNotificationRecord)
    {
      Notification localNotification = paramNotificationRecord.getNotification();
      if (localNotification.actions != null) {
        this.numWithActions += 1;
      }
      if ((localNotification.flags & 0x40) != 0) {
        this.numForegroundService += 1;
      }
      if ((localNotification.flags & 0x2) != 0) {
        this.numOngoing += 1;
      }
      if ((localNotification.flags & 0x10) != 0) {
        this.numAutoCancel += 1;
      }
      if (((localNotification.defaults & 0x1) != 0) || ((localNotification.defaults & 0x2) != 0))
      {
        this.numInterrupt += 1;
        label109:
        switch (localNotification.visibility)
        {
        default: 
          label136:
          if (paramNotificationRecord.stats.isNoisy)
          {
            this.noisyImportance.increment(paramNotificationRecord.stats.requestedImportance);
            label160:
            this.finalImportance.increment(paramNotificationRecord.getImportance());
            paramNotificationRecord = localNotification.extras.keySet();
            if (paramNotificationRecord.contains("android.bigText")) {
              this.numWithBigText += 1;
            }
            if (paramNotificationRecord.contains("android.picture")) {
              this.numWithBigPicture += 1;
            }
            if (paramNotificationRecord.contains("android.largeIcon")) {
              this.numWithLargeIcon += 1;
            }
            if (paramNotificationRecord.contains("android.textLines")) {
              this.numWithInbox += 1;
            }
            if (paramNotificationRecord.contains("android.mediaSession")) {
              this.numWithMediaSession += 1;
            }
            if ((paramNotificationRecord.contains("android.title")) && (!TextUtils.isEmpty(localNotification.extras.getCharSequence("android.title")))) {
              break label462;
            }
            label317:
            if ((paramNotificationRecord.contains("android.text")) && (!TextUtils.isEmpty(localNotification.extras.getCharSequence("android.text")))) {
              break label475;
            }
            label345:
            if ((paramNotificationRecord.contains("android.subText")) && (!TextUtils.isEmpty(localNotification.extras.getCharSequence("android.subText")))) {
              break label488;
            }
          }
          break;
        }
      }
      for (;;)
      {
        if ((paramNotificationRecord.contains("android.infoText")) && (!TextUtils.isEmpty(localNotification.extras.getCharSequence("android.infoText")))) {
          break label501;
        }
        return;
        if (localNotification.sound != null) {
          break;
        }
        if (localNotification.vibrate == null) {
          break label109;
        }
        break;
        this.numPrivate += 1;
        break label136;
        this.numSecret += 1;
        break label136;
        this.quietImportance.increment(paramNotificationRecord.stats.requestedImportance);
        break label160;
        label462:
        this.numWithTitle += 1;
        break label317;
        label475:
        this.numWithText += 1;
        break label345;
        label488:
        this.numWithSubText += 1;
      }
      label501:
      this.numWithInfoText += 1;
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.println(toStringWithIndent(paramString));
    }
    
    public JSONObject dumpJson()
      throws JSONException
    {
      AggregatedStats localAggregatedStats = getPrevious();
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("key", this.key);
      localJSONObject.put("duration", SystemClock.elapsedRealtime() - this.mCreated);
      maybePut(localJSONObject, "numEnqueuedByApp", this.numEnqueuedByApp);
      maybePut(localJSONObject, "numPostedByApp", this.numPostedByApp);
      maybePut(localJSONObject, "numUpdatedByApp", this.numUpdatedByApp);
      maybePut(localJSONObject, "numRemovedByApp", this.numRemovedByApp);
      maybePut(localJSONObject, "numPeopleCacheHit", this.numPeopleCacheHit);
      maybePut(localJSONObject, "numPeopleCacheMiss", this.numPeopleCacheMiss);
      maybePut(localJSONObject, "numWithStaredPeople", this.numWithStaredPeople);
      maybePut(localJSONObject, "numWithValidPeople", this.numWithValidPeople);
      maybePut(localJSONObject, "numBlocked", this.numBlocked);
      maybePut(localJSONObject, "numSuspendedByAdmin", this.numSuspendedByAdmin);
      maybePut(localJSONObject, "numWithActions", this.numWithActions);
      maybePut(localJSONObject, "numPrivate", this.numPrivate);
      maybePut(localJSONObject, "numSecret", this.numSecret);
      maybePut(localJSONObject, "numInterrupt", this.numInterrupt);
      maybePut(localJSONObject, "numWithBigText", this.numWithBigText);
      maybePut(localJSONObject, "numWithBigPicture", this.numWithBigPicture);
      maybePut(localJSONObject, "numForegroundService", this.numForegroundService);
      maybePut(localJSONObject, "numOngoing", this.numOngoing);
      maybePut(localJSONObject, "numAutoCancel", this.numAutoCancel);
      maybePut(localJSONObject, "numWithLargeIcon", this.numWithLargeIcon);
      maybePut(localJSONObject, "numWithInbox", this.numWithInbox);
      maybePut(localJSONObject, "numWithMediaSession", this.numWithMediaSession);
      maybePut(localJSONObject, "numWithTitle", this.numWithTitle);
      maybePut(localJSONObject, "numWithText", this.numWithText);
      maybePut(localJSONObject, "numWithSubText", this.numWithSubText);
      maybePut(localJSONObject, "numWithInfoText", this.numWithInfoText);
      maybePut(localJSONObject, "numRateViolations", this.numRateViolations);
      maybePut(localJSONObject, "numQuotaLViolations", this.numQuotaViolations);
      maybePut(localJSONObject, "notificationEnqueueRate", getEnqueueRate());
      this.noisyImportance.maybePut(localJSONObject, localAggregatedStats.noisyImportance);
      this.quietImportance.maybePut(localJSONObject, localAggregatedStats.quietImportance);
      this.finalImportance.maybePut(localJSONObject, localAggregatedStats.finalImportance);
      return localJSONObject;
    }
    
    public void emit()
    {
      AggregatedStats localAggregatedStats = getPrevious();
      maybeCount("note_enqueued", this.numEnqueuedByApp - localAggregatedStats.numEnqueuedByApp);
      maybeCount("note_post", this.numPostedByApp - localAggregatedStats.numPostedByApp);
      maybeCount("note_update", this.numUpdatedByApp - localAggregatedStats.numUpdatedByApp);
      maybeCount("note_remove", this.numRemovedByApp - localAggregatedStats.numRemovedByApp);
      maybeCount("note_with_people", this.numWithValidPeople - localAggregatedStats.numWithValidPeople);
      maybeCount("note_with_stars", this.numWithStaredPeople - localAggregatedStats.numWithStaredPeople);
      maybeCount("people_cache_hit", this.numPeopleCacheHit - localAggregatedStats.numPeopleCacheHit);
      maybeCount("people_cache_miss", this.numPeopleCacheMiss - localAggregatedStats.numPeopleCacheMiss);
      maybeCount("note_blocked", this.numBlocked - localAggregatedStats.numBlocked);
      maybeCount("note_suspended", this.numSuspendedByAdmin - localAggregatedStats.numSuspendedByAdmin);
      maybeCount("note_with_actions", this.numWithActions - localAggregatedStats.numWithActions);
      maybeCount("note_private", this.numPrivate - localAggregatedStats.numPrivate);
      maybeCount("note_secret", this.numSecret - localAggregatedStats.numSecret);
      maybeCount("note_interupt", this.numInterrupt - localAggregatedStats.numInterrupt);
      maybeCount("note_big_text", this.numWithBigText - localAggregatedStats.numWithBigText);
      maybeCount("note_big_pic", this.numWithBigPicture - localAggregatedStats.numWithBigPicture);
      maybeCount("note_fg", this.numForegroundService - localAggregatedStats.numForegroundService);
      maybeCount("note_ongoing", this.numOngoing - localAggregatedStats.numOngoing);
      maybeCount("note_auto", this.numAutoCancel - localAggregatedStats.numAutoCancel);
      maybeCount("note_large_icon", this.numWithLargeIcon - localAggregatedStats.numWithLargeIcon);
      maybeCount("note_inbox", this.numWithInbox - localAggregatedStats.numWithInbox);
      maybeCount("note_media", this.numWithMediaSession - localAggregatedStats.numWithMediaSession);
      maybeCount("note_title", this.numWithTitle - localAggregatedStats.numWithTitle);
      maybeCount("note_text", this.numWithText - localAggregatedStats.numWithText);
      maybeCount("note_sub_text", this.numWithSubText - localAggregatedStats.numWithSubText);
      maybeCount("note_info_text", this.numWithInfoText - localAggregatedStats.numWithInfoText);
      maybeCount("note_over_rate", this.numRateViolations - localAggregatedStats.numRateViolations);
      maybeCount("note_over_quota", this.numQuotaViolations - localAggregatedStats.numQuotaViolations);
      this.noisyImportance.maybeCount(localAggregatedStats.noisyImportance);
      this.quietImportance.maybeCount(localAggregatedStats.quietImportance);
      this.finalImportance.maybeCount(localAggregatedStats.finalImportance);
      localAggregatedStats.numEnqueuedByApp = this.numEnqueuedByApp;
      localAggregatedStats.numPostedByApp = this.numPostedByApp;
      localAggregatedStats.numUpdatedByApp = this.numUpdatedByApp;
      localAggregatedStats.numRemovedByApp = this.numRemovedByApp;
      localAggregatedStats.numPeopleCacheHit = this.numPeopleCacheHit;
      localAggregatedStats.numPeopleCacheMiss = this.numPeopleCacheMiss;
      localAggregatedStats.numWithStaredPeople = this.numWithStaredPeople;
      localAggregatedStats.numWithValidPeople = this.numWithValidPeople;
      localAggregatedStats.numBlocked = this.numBlocked;
      localAggregatedStats.numSuspendedByAdmin = this.numSuspendedByAdmin;
      localAggregatedStats.numWithActions = this.numWithActions;
      localAggregatedStats.numPrivate = this.numPrivate;
      localAggregatedStats.numSecret = this.numSecret;
      localAggregatedStats.numInterrupt = this.numInterrupt;
      localAggregatedStats.numWithBigText = this.numWithBigText;
      localAggregatedStats.numWithBigPicture = this.numWithBigPicture;
      localAggregatedStats.numForegroundService = this.numForegroundService;
      localAggregatedStats.numOngoing = this.numOngoing;
      localAggregatedStats.numAutoCancel = this.numAutoCancel;
      localAggregatedStats.numWithLargeIcon = this.numWithLargeIcon;
      localAggregatedStats.numWithInbox = this.numWithInbox;
      localAggregatedStats.numWithMediaSession = this.numWithMediaSession;
      localAggregatedStats.numWithTitle = this.numWithTitle;
      localAggregatedStats.numWithText = this.numWithText;
      localAggregatedStats.numWithSubText = this.numWithSubText;
      localAggregatedStats.numWithInfoText = this.numWithInfoText;
      localAggregatedStats.numRateViolations = this.numRateViolations;
      localAggregatedStats.numQuotaViolations = this.numQuotaViolations;
      this.noisyImportance.update(localAggregatedStats.noisyImportance);
      this.quietImportance.update(localAggregatedStats.quietImportance);
      this.finalImportance.update(localAggregatedStats.finalImportance);
    }
    
    public float getEnqueueRate()
    {
      return getEnqueueRate(SystemClock.elapsedRealtime());
    }
    
    public float getEnqueueRate(long paramLong)
    {
      return this.enqueueRate.getRate(paramLong);
    }
    
    public AggregatedStats getPrevious()
    {
      if (this.mPrevious == null) {
        this.mPrevious = new AggregatedStats(this.mContext, this.key);
      }
      return this.mPrevious;
    }
    
    void maybeCount(String paramString, int paramInt)
    {
      if (paramInt > 0) {
        MetricsLogger.count(this.mContext, paramString, paramInt);
      }
    }
    
    public String toString()
    {
      return toStringWithIndent("");
    }
    
    public void updateInterarrivalEstimate(long paramLong)
    {
      this.enqueueRate.update(paramLong);
    }
  }
  
  private static class ImportanceHistogram
  {
    private static final String[] IMPORTANCE_NAMES = { "none", "min", "low", "default", "high", "max" };
    private static final int NUM_IMPORTANCES = 6;
    private final Context mContext;
    private int[] mCount;
    private final String[] mCounterNames;
    private final String mPrefix;
    
    ImportanceHistogram(Context paramContext, String paramString)
    {
      this.mContext = paramContext;
      this.mCount = new int[6];
      this.mCounterNames = new String[6];
      this.mPrefix = paramString;
      int i = 0;
      while (i < 6)
      {
        this.mCounterNames[i] = (this.mPrefix + IMPORTANCE_NAMES[i]);
        i += 1;
      }
    }
    
    void increment(int paramInt)
    {
      int i;
      if (paramInt < 0) {
        i = 0;
      }
      for (;;)
      {
        int[] arrayOfInt = this.mCount;
        arrayOfInt[i] += 1;
        return;
        i = paramInt;
        if (paramInt > 6) {
          i = 6;
        }
      }
    }
    
    void maybeCount(ImportanceHistogram paramImportanceHistogram)
    {
      int i = 0;
      while (i < 6)
      {
        int j = this.mCount[i] - paramImportanceHistogram.mCount[i];
        if (j > 0) {
          MetricsLogger.count(this.mContext, this.mCounterNames[i], j);
        }
        i += 1;
      }
    }
    
    public void maybePut(JSONObject paramJSONObject, ImportanceHistogram paramImportanceHistogram)
      throws JSONException
    {
      paramJSONObject.put(this.mPrefix, new JSONArray(this.mCount));
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.mPrefix).append(": [");
      int i = 0;
      while (i < 6)
      {
        localStringBuilder.append(this.mCount[i]);
        if (i < 5) {
          localStringBuilder.append(", ");
        }
        i += 1;
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    
    void update(ImportanceHistogram paramImportanceHistogram)
    {
      int i = 0;
      while (i < 6)
      {
        this.mCount[i] = paramImportanceHistogram.mCount[i];
        i += 1;
      }
    }
  }
  
  private static class SQLiteLog
  {
    private static final String COL_ACTION_COUNT = "action_count";
    private static final String COL_AIRTIME_EXPANDED_MS = "expansion_airtime_ms";
    private static final String COL_AIRTIME_MS = "airtime_ms";
    private static final String COL_CATEGORY = "category";
    private static final String COL_DEFAULTS = "defaults";
    private static final String COL_DEMOTED = "demoted";
    private static final String COL_EVENT_TIME = "event_time_ms";
    private static final String COL_EVENT_TYPE = "event_type";
    private static final String COL_EVENT_USER_ID = "event_user_id";
    private static final String COL_EXPAND_COUNT = "expansion_count";
    private static final String COL_FIRST_EXPANSIONTIME_MS = "first_expansion_time_ms";
    private static final String COL_FLAGS = "flags";
    private static final String COL_IMPORTANCE_FINAL = "importance_final";
    private static final String COL_IMPORTANCE_REQ = "importance_request";
    private static final String COL_KEY = "key";
    private static final String COL_MUTED = "muted";
    private static final String COL_NOISY = "noisy";
    private static final String COL_NOTIFICATION_ID = "nid";
    private static final String COL_PKG = "pkg";
    private static final String COL_POSTTIME_MS = "posttime_ms";
    private static final String COL_TAG = "tag";
    private static final String COL_WHEN_MS = "when_ms";
    private static final long DAY_MS = 86400000L;
    private static final String DB_NAME = "notification_log.db";
    private static final int DB_VERSION = 5;
    private static final int EVENT_TYPE_CLICK = 2;
    private static final int EVENT_TYPE_DISMISS = 4;
    private static final int EVENT_TYPE_POST = 1;
    private static final int EVENT_TYPE_REMOVE = 3;
    private static final long HORIZON_MS = 604800000L;
    private static final int MSG_CLICK = 2;
    private static final int MSG_DISMISS = 4;
    private static final int MSG_POST = 1;
    private static final int MSG_REMOVE = 3;
    private static final long PRUNE_MIN_DELAY_MS = 21600000L;
    private static final long PRUNE_MIN_WRITES = 1024L;
    private static final String STATS_QUERY = "SELECT event_user_id, pkg, CAST(((%d - event_time_ms) / 86400000) AS int) AS day, COUNT(*) AS cnt, SUM(muted) as muted, SUM(noisy) as noisy, SUM(demoted) as demoted FROM log WHERE event_type=1 AND event_time_ms > %d  GROUP BY event_user_id, day, pkg";
    private static final String TAB_LOG = "log";
    private static final String TAG = "NotificationSQLiteLog";
    private static long sLastPruneMs;
    private static long sNumWrites;
    private final SQLiteOpenHelper mHelper;
    private final Handler mWriteHandler;
    
    public SQLiteLog(Context paramContext)
    {
      HandlerThread localHandlerThread = new HandlerThread("notification-sqlite-log", 10);
      localHandlerThread.start();
      this.mWriteHandler = new Handler(localHandlerThread.getLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          NotificationRecord localNotificationRecord = (NotificationRecord)paramAnonymousMessage.obj;
          long l = System.currentTimeMillis();
          switch (paramAnonymousMessage.what)
          {
          default: 
            Log.wtf("NotificationSQLiteLog", "Unknown message type: " + paramAnonymousMessage.what);
            return;
          case 1: 
            NotificationUsageStats.SQLiteLog.-wrap0(NotificationUsageStats.SQLiteLog.this, localNotificationRecord.sbn.getPostTime(), 1, localNotificationRecord);
            return;
          case 2: 
            NotificationUsageStats.SQLiteLog.-wrap0(NotificationUsageStats.SQLiteLog.this, l, 2, localNotificationRecord);
            return;
          case 3: 
            NotificationUsageStats.SQLiteLog.-wrap0(NotificationUsageStats.SQLiteLog.this, l, 3, localNotificationRecord);
            return;
          }
          NotificationUsageStats.SQLiteLog.-wrap0(NotificationUsageStats.SQLiteLog.this, l, 4, localNotificationRecord);
        }
      };
      this.mHelper = new SQLiteOpenHelper(paramContext, "notification_log.db", null, 5)
      {
        public void onCreate(SQLiteDatabase paramAnonymousSQLiteDatabase)
        {
          paramAnonymousSQLiteDatabase.execSQL("CREATE TABLE log (_id INTEGER PRIMARY KEY AUTOINCREMENT,event_user_id INT,event_type INT,event_time_ms INT,key TEXT,pkg TEXT,nid INT,tag TEXT,when_ms INT,defaults INT,flags INT,importance_request INT,importance_final INT,noisy INT,muted INT,demoted INT,category TEXT,action_count INT,posttime_ms INT,airtime_ms INT,first_expansion_time_ms INT,expansion_airtime_ms INT,expansion_count INT)");
        }
        
        public void onUpgrade(SQLiteDatabase paramAnonymousSQLiteDatabase, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (paramAnonymousInt1 != paramAnonymousInt2)
          {
            paramAnonymousSQLiteDatabase.execSQL("DROP TABLE IF EXISTS log");
            onCreate(paramAnonymousSQLiteDatabase);
          }
        }
      };
    }
    
    private long getMidnightMs()
    {
      GregorianCalendar localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.set(localGregorianCalendar.get(1), localGregorianCalendar.get(2), localGregorianCalendar.get(5), 23, 59, 59);
      return localGregorianCalendar.getTimeInMillis();
    }
    
    private JSONArray jsonPostFrequencies(NotificationManagerService.DumpFilter paramDumpFilter)
      throws JSONException
    {
      JSONArray localJSONArray = new JSONArray();
      Cursor localCursor = this.mHelper.getReadableDatabase().rawQuery(String.format("SELECT event_user_id, pkg, CAST(((%d - event_time_ms) / 86400000) AS int) AS day, COUNT(*) AS cnt, SUM(muted) as muted, SUM(noisy) as noisy, SUM(demoted) as demoted FROM log WHERE event_type=1 AND event_time_ms > %d  GROUP BY event_user_id, day, pkg", new Object[] { Long.valueOf(getMidnightMs()), Long.valueOf(paramDumpFilter.since) }), null);
      try
      {
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast())
        {
          int i = localCursor.getInt(0);
          String str = localCursor.getString(1);
          if ((paramDumpFilter == null) || (paramDumpFilter.matches(str)))
          {
            int j = localCursor.getInt(2);
            int k = localCursor.getInt(3);
            int m = localCursor.getInt(4);
            int n = localCursor.getInt(5);
            int i1 = localCursor.getInt(6);
            JSONObject localJSONObject = new JSONObject();
            localJSONObject.put("user_id", i);
            localJSONObject.put("package", str);
            localJSONObject.put("day", j);
            localJSONObject.put("count", k);
            localJSONObject.put("noisy", n);
            localJSONObject.put("muted", m);
            localJSONObject.put("demoted", i1);
            localJSONArray.put(localJSONObject);
          }
          localCursor.moveToNext();
        }
      }
      finally
      {
        localCursor.close();
      }
      return localJSONArray;
    }
    
    private void pruneIfNecessary(SQLiteDatabase paramSQLiteDatabase)
    {
      long l = System.currentTimeMillis();
      if ((sNumWrites > 1024L) || (l - sLastPruneMs > 21600000L))
      {
        sNumWrites = 0L;
        sLastPruneMs = l;
        int i = paramSQLiteDatabase.delete("log", "event_time_ms < ?", new String[] { String.valueOf(l - 604800000L) });
        Log.d("NotificationSQLiteLog", "Pruned event entries: " + i);
      }
    }
    
    private static void putNotificationDetails(NotificationRecord paramNotificationRecord, ContentValues paramContentValues)
    {
      int j = 0;
      paramContentValues.put("nid", Integer.valueOf(paramNotificationRecord.sbn.getId()));
      if (paramNotificationRecord.sbn.getTag() != null) {
        paramContentValues.put("tag", paramNotificationRecord.sbn.getTag());
      }
      paramContentValues.put("when_ms", Long.valueOf(paramNotificationRecord.sbn.getPostTime()));
      paramContentValues.put("flags", Integer.valueOf(paramNotificationRecord.getNotification().flags));
      int i = paramNotificationRecord.stats.requestedImportance;
      int k = paramNotificationRecord.getImportance();
      boolean bool = paramNotificationRecord.stats.isNoisy;
      paramContentValues.put("importance_request", Integer.valueOf(i));
      paramContentValues.put("importance_final", Integer.valueOf(k));
      if (k < i)
      {
        i = 1;
        paramContentValues.put("demoted", Integer.valueOf(i));
        paramContentValues.put("noisy", Boolean.valueOf(bool));
        if ((!bool) || (k >= 4)) {
          break label227;
        }
        paramContentValues.put("muted", Integer.valueOf(1));
      }
      for (;;)
      {
        if (paramNotificationRecord.getNotification().category != null) {
          paramContentValues.put("category", paramNotificationRecord.getNotification().category);
        }
        i = j;
        if (paramNotificationRecord.getNotification().actions != null) {
          i = paramNotificationRecord.getNotification().actions.length;
        }
        paramContentValues.put("action_count", Integer.valueOf(i));
        return;
        i = 0;
        break;
        label227:
        paramContentValues.put("muted", Integer.valueOf(0));
      }
    }
    
    private static void putNotificationIdentifiers(NotificationRecord paramNotificationRecord, ContentValues paramContentValues)
    {
      paramContentValues.put("key", paramNotificationRecord.sbn.getKey());
      paramContentValues.put("pkg", paramNotificationRecord.sbn.getPackageName());
    }
    
    private static void putPosttimeVisibility(NotificationRecord paramNotificationRecord, ContentValues paramContentValues)
    {
      paramContentValues.put("posttime_ms", Long.valueOf(paramNotificationRecord.stats.getCurrentPosttimeMs()));
      paramContentValues.put("airtime_ms", Long.valueOf(paramNotificationRecord.stats.getCurrentAirtimeMs()));
      paramContentValues.put("expansion_count", Long.valueOf(paramNotificationRecord.stats.userExpansionCount));
      paramContentValues.put("expansion_airtime_ms", Long.valueOf(paramNotificationRecord.stats.getCurrentAirtimeExpandedMs()));
      paramContentValues.put("first_expansion_time_ms", Long.valueOf(paramNotificationRecord.stats.posttimeToFirstVisibleExpansionMs));
    }
    
    private void writeEvent(long paramLong, int paramInt, NotificationRecord paramNotificationRecord)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("event_user_id", Integer.valueOf(paramNotificationRecord.sbn.getUser().getIdentifier()));
      localContentValues.put("event_time_ms", Long.valueOf(paramLong));
      localContentValues.put("event_type", Integer.valueOf(paramInt));
      putNotificationIdentifiers(paramNotificationRecord, localContentValues);
      if (paramInt == 1) {
        putNotificationDetails(paramNotificationRecord, localContentValues);
      }
      for (;;)
      {
        paramNotificationRecord = this.mHelper.getWritableDatabase();
        if (paramNotificationRecord.insert("log", null, localContentValues) < 0L) {
          Log.wtf("NotificationSQLiteLog", "Error while trying to insert values: " + localContentValues);
        }
        sNumWrites += 1L;
        pruneIfNecessary(paramNotificationRecord);
        return;
        putPosttimeVisibility(paramNotificationRecord, localContentValues);
      }
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter)
    {
      printPostFrequencies(paramPrintWriter, paramString, paramDumpFilter);
    }
    
    public JSONObject dumpJson(NotificationManagerService.DumpFilter paramDumpFilter)
    {
      JSONObject localJSONObject = new JSONObject();
      try
      {
        localJSONObject.put("post_frequency", jsonPostFrequencies(paramDumpFilter));
        localJSONObject.put("since", paramDumpFilter.since);
        localJSONObject.put("now", System.currentTimeMillis());
        return localJSONObject;
      }
      catch (JSONException paramDumpFilter) {}
      return localJSONObject;
    }
    
    public void logClicked(NotificationRecord paramNotificationRecord)
    {
      this.mWriteHandler.sendMessage(this.mWriteHandler.obtainMessage(2, paramNotificationRecord));
    }
    
    public void logDismissed(NotificationRecord paramNotificationRecord)
    {
      this.mWriteHandler.sendMessage(this.mWriteHandler.obtainMessage(4, paramNotificationRecord));
    }
    
    public void logPosted(NotificationRecord paramNotificationRecord)
    {
      this.mWriteHandler.sendMessage(this.mWriteHandler.obtainMessage(1, paramNotificationRecord));
    }
    
    public void logRemoved(NotificationRecord paramNotificationRecord)
    {
      this.mWriteHandler.sendMessage(this.mWriteHandler.obtainMessage(3, paramNotificationRecord));
    }
    
    public void printPostFrequencies(PrintWriter paramPrintWriter, String paramString, NotificationManagerService.DumpFilter paramDumpFilter)
    {
      Cursor localCursor = this.mHelper.getReadableDatabase().rawQuery(String.format("SELECT event_user_id, pkg, CAST(((%d - event_time_ms) / 86400000) AS int) AS day, COUNT(*) AS cnt, SUM(muted) as muted, SUM(noisy) as noisy, SUM(demoted) as demoted FROM log WHERE event_type=1 AND event_time_ms > %d  GROUP BY event_user_id, day, pkg", new Object[] { Long.valueOf(getMidnightMs()), Long.valueOf(paramDumpFilter.since) }), null);
      try
      {
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast())
        {
          int i = localCursor.getInt(0);
          String str = localCursor.getString(1);
          if ((paramDumpFilter == null) || (paramDumpFilter.matches(str)))
          {
            int j = localCursor.getInt(2);
            int k = localCursor.getInt(3);
            int m = localCursor.getInt(4);
            int n = localCursor.getInt(5);
            int i1 = localCursor.getInt(6);
            paramPrintWriter.println(paramString + "post_frequency{user_id=" + i + ",pkg=" + str + ",day=" + j + ",count=" + k + ",muted=" + m + "/" + n + ",demoted=" + i1 + "}");
          }
          localCursor.moveToNext();
        }
      }
      finally
      {
        localCursor.close();
      }
    }
  }
  
  public static class SingleNotificationStats
  {
    public long airtimeCount = 0L;
    public long airtimeExpandedMs = 0L;
    public long airtimeMs = 0L;
    public long currentAirtimeExpandedStartElapsedMs = -1L;
    public long currentAirtimeStartElapsedMs = -1L;
    private boolean isExpanded = false;
    public boolean isNoisy;
    private boolean isVisible = false;
    public int naturalImportance;
    public long posttimeElapsedMs = -1L;
    public long posttimeToDismissMs = -1L;
    public long posttimeToFirstAirtimeMs = -1L;
    public long posttimeToFirstClickMs = -1L;
    public long posttimeToFirstVisibleExpansionMs = -1L;
    public int requestedImportance;
    public long userExpansionCount = 0L;
    
    private void updateVisiblyExpandedStats()
    {
      long l = SystemClock.elapsedRealtime();
      if ((this.isExpanded) && (this.isVisible))
      {
        if (this.currentAirtimeExpandedStartElapsedMs < 0L) {
          this.currentAirtimeExpandedStartElapsedMs = l;
        }
        if (this.posttimeToFirstVisibleExpansionMs < 0L) {
          this.posttimeToFirstVisibleExpansionMs = (l - this.posttimeElapsedMs);
        }
      }
      while (this.currentAirtimeExpandedStartElapsedMs < 0L) {
        return;
      }
      this.airtimeExpandedMs += l - this.currentAirtimeExpandedStartElapsedMs;
      this.currentAirtimeExpandedStartElapsedMs = -1L;
    }
    
    public void finish()
    {
      onVisibilityChanged(false);
    }
    
    public long getCurrentAirtimeExpandedMs()
    {
      long l2 = this.airtimeExpandedMs;
      long l1 = l2;
      if (this.currentAirtimeExpandedStartElapsedMs >= 0L) {
        l1 = l2 + (SystemClock.elapsedRealtime() - this.currentAirtimeExpandedStartElapsedMs);
      }
      return l1;
    }
    
    public long getCurrentAirtimeMs()
    {
      long l2 = this.airtimeMs;
      long l1 = l2;
      if (this.currentAirtimeStartElapsedMs >= 0L) {
        l1 = l2 + (SystemClock.elapsedRealtime() - this.currentAirtimeStartElapsedMs);
      }
      return l1;
    }
    
    public long getCurrentPosttimeMs()
    {
      if (this.posttimeElapsedMs < 0L) {
        return 0L;
      }
      return SystemClock.elapsedRealtime() - this.posttimeElapsedMs;
    }
    
    public void onCancel()
    {
      finish();
    }
    
    public void onClick()
    {
      if (this.posttimeToFirstClickMs < 0L) {
        this.posttimeToFirstClickMs = (SystemClock.elapsedRealtime() - this.posttimeElapsedMs);
      }
    }
    
    public void onDismiss()
    {
      if (this.posttimeToDismissMs < 0L) {
        this.posttimeToDismissMs = (SystemClock.elapsedRealtime() - this.posttimeElapsedMs);
      }
      finish();
    }
    
    public void onExpansionChanged(boolean paramBoolean1, boolean paramBoolean2)
    {
      this.isExpanded = paramBoolean2;
      if ((this.isExpanded) && (paramBoolean1)) {
        this.userExpansionCount += 1L;
      }
      updateVisiblyExpandedStats();
    }
    
    public void onRemoved()
    {
      finish();
    }
    
    public void onVisibilityChanged(boolean paramBoolean)
    {
      long l = SystemClock.elapsedRealtime();
      boolean bool = this.isVisible;
      this.isVisible = paramBoolean;
      if (paramBoolean)
      {
        if (this.currentAirtimeStartElapsedMs < 0L)
        {
          this.airtimeCount += 1L;
          this.currentAirtimeStartElapsedMs = l;
        }
        if (this.posttimeToFirstAirtimeMs < 0L) {
          this.posttimeToFirstAirtimeMs = (l - this.posttimeElapsedMs);
        }
      }
      for (;;)
      {
        if (bool != this.isVisible) {
          updateVisiblyExpandedStats();
        }
        return;
        if (this.currentAirtimeStartElapsedMs >= 0L)
        {
          this.airtimeMs += l - this.currentAirtimeStartElapsedMs;
          this.currentAirtimeStartElapsedMs = -1L;
        }
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SingleNotificationStats{");
      localStringBuilder.append("posttimeElapsedMs=").append(this.posttimeElapsedMs).append(", ");
      localStringBuilder.append("posttimeToFirstClickMs=").append(this.posttimeToFirstClickMs).append(", ");
      localStringBuilder.append("posttimeToDismissMs=").append(this.posttimeToDismissMs).append(", ");
      localStringBuilder.append("airtimeCount=").append(this.airtimeCount).append(", ");
      localStringBuilder.append("airtimeMs=").append(this.airtimeMs).append(", ");
      localStringBuilder.append("currentAirtimeStartElapsedMs=").append(this.currentAirtimeStartElapsedMs).append(", ");
      localStringBuilder.append("airtimeExpandedMs=").append(this.airtimeExpandedMs).append(", ");
      localStringBuilder.append("posttimeToFirstVisibleExpansionMs=").append(this.posttimeToFirstVisibleExpansionMs).append(", ");
      localStringBuilder.append("currentAirtimeExpandedStartElapsedMs=").append(this.currentAirtimeExpandedStartElapsedMs).append(", ");
      localStringBuilder.append("requestedImportance=").append(this.requestedImportance).append(", ");
      localStringBuilder.append("naturalImportance=").append(this.naturalImportance).append(", ");
      localStringBuilder.append("isNoisy=").append(this.isNoisy);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void updateFrom(SingleNotificationStats paramSingleNotificationStats)
    {
      this.posttimeElapsedMs = paramSingleNotificationStats.posttimeElapsedMs;
      this.posttimeToFirstClickMs = paramSingleNotificationStats.posttimeToFirstClickMs;
      this.airtimeCount = paramSingleNotificationStats.airtimeCount;
      this.posttimeToFirstAirtimeMs = paramSingleNotificationStats.posttimeToFirstAirtimeMs;
      this.currentAirtimeStartElapsedMs = paramSingleNotificationStats.currentAirtimeStartElapsedMs;
      this.airtimeMs = paramSingleNotificationStats.airtimeMs;
      this.posttimeToFirstVisibleExpansionMs = paramSingleNotificationStats.posttimeToFirstVisibleExpansionMs;
      this.currentAirtimeExpandedStartElapsedMs = paramSingleNotificationStats.currentAirtimeExpandedStartElapsedMs;
      this.airtimeExpandedMs = paramSingleNotificationStats.airtimeExpandedMs;
      this.userExpansionCount = paramSingleNotificationStats.userExpansionCount;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/NotificationUsageStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */