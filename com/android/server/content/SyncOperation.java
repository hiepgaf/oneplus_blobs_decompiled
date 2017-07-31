package com.android.server.content;

import android.accounts.Account;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Slog;
import java.util.Iterator;
import java.util.Set;

public class SyncOperation
{
  public static final int NO_JOB_ID = -1;
  public static final int REASON_ACCOUNTS_UPDATED = -2;
  public static final int REASON_BACKGROUND_DATA_SETTINGS_CHANGED = -1;
  public static final int REASON_IS_SYNCABLE = -5;
  public static final int REASON_MASTER_SYNC_AUTO = -7;
  private static String[] REASON_NAMES = { "DataSettingsChanged", "AccountsUpdated", "ServiceChanged", "Periodic", "IsSyncable", "AutoSync", "MasterSyncAuto", "UserStart" };
  public static final int REASON_PERIODIC = -4;
  public static final int REASON_SERVICE_CHANGED = -3;
  public static final int REASON_SYNC_AUTO = -6;
  public static final int REASON_USER_START = -8;
  public static final String TAG = "SyncManager";
  public final boolean allowParallelSyncs;
  public long expectedRuntime;
  public final Bundle extras;
  public final long flexMillis;
  public final boolean isPeriodic;
  public int jobId;
  public final String key;
  public final String owningPackage;
  public final int owningUid;
  public final long periodMillis;
  public final int reason;
  int retries;
  public final int sourcePeriodicId;
  public final int syncSource;
  public final SyncStorageEngine.EndPoint target;
  public String wakeLockName;
  
  public SyncOperation(Account paramAccount, int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    this(new SyncStorageEngine.EndPoint(paramAccount, paramString2, paramInt1), paramInt2, paramString1, paramInt3, paramInt4, paramBundle, paramBoolean);
  }
  
  public SyncOperation(SyncOperation paramSyncOperation)
  {
    this.target = paramSyncOperation.target;
    this.owningUid = paramSyncOperation.owningUid;
    this.owningPackage = paramSyncOperation.owningPackage;
    this.reason = paramSyncOperation.reason;
    this.syncSource = paramSyncOperation.syncSource;
    this.allowParallelSyncs = paramSyncOperation.allowParallelSyncs;
    this.extras = new Bundle(paramSyncOperation.extras);
    this.wakeLockName = paramSyncOperation.wakeLockName();
    this.isPeriodic = paramSyncOperation.isPeriodic;
    this.sourcePeriodicId = paramSyncOperation.sourcePeriodicId;
    this.periodMillis = paramSyncOperation.periodMillis;
    this.flexMillis = paramSyncOperation.flexMillis;
    this.key = paramSyncOperation.key;
  }
  
  public SyncOperation(SyncOperation paramSyncOperation, long paramLong1, long paramLong2)
  {
    this(paramSyncOperation.target, paramSyncOperation.owningUid, paramSyncOperation.owningPackage, paramSyncOperation.reason, paramSyncOperation.syncSource, new Bundle(paramSyncOperation.extras), paramSyncOperation.allowParallelSyncs, paramSyncOperation.isPeriodic, paramSyncOperation.sourcePeriodicId, paramLong1, paramLong2);
  }
  
  private SyncOperation(SyncStorageEngine.EndPoint paramEndPoint, int paramInt1, String paramString, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
  {
    this(paramEndPoint, paramInt1, paramString, paramInt2, paramInt3, paramBundle, paramBoolean, false, -1, 0L, 0L);
  }
  
  public SyncOperation(SyncStorageEngine.EndPoint paramEndPoint, int paramInt1, String paramString, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, long paramLong1, long paramLong2)
  {
    this.target = paramEndPoint;
    this.owningUid = paramInt1;
    this.owningPackage = paramString;
    this.reason = paramInt2;
    this.syncSource = paramInt3;
    this.extras = new Bundle(paramBundle);
    this.allowParallelSyncs = paramBoolean1;
    this.isPeriodic = paramBoolean2;
    this.sourcePeriodicId = paramInt4;
    this.periodMillis = paramLong1;
    this.flexMillis = paramLong2;
    this.jobId = -1;
    this.key = toKey();
  }
  
  private static void extrasToStringBuilder(Bundle paramBundle, StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("[");
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramStringBuilder.append(str).append("=").append(paramBundle.get(str)).append(" ");
    }
    paramStringBuilder.append("]");
  }
  
  static SyncOperation maybeCreateFromJobExtras(PersistableBundle paramPersistableBundle)
  {
    if (!paramPersistableBundle.getBoolean("SyncManagerJob", false)) {
      return null;
    }
    Object localObject1 = paramPersistableBundle.getString("accountName");
    String str1 = paramPersistableBundle.getString("accountType");
    String str2 = paramPersistableBundle.getString("provider");
    int i = paramPersistableBundle.getInt("userId", Integer.MAX_VALUE);
    int j = paramPersistableBundle.getInt("owningUid");
    String str3 = paramPersistableBundle.getString("owningPackage");
    int k = paramPersistableBundle.getInt("reason", Integer.MAX_VALUE);
    int m = paramPersistableBundle.getInt("source", Integer.MAX_VALUE);
    boolean bool1 = paramPersistableBundle.getBoolean("allowParallelSyncs", false);
    boolean bool2 = paramPersistableBundle.getBoolean("isPeriodic", false);
    int n = paramPersistableBundle.getInt("sourcePeriodicId", -1);
    long l1 = paramPersistableBundle.getLong("periodMillis");
    long l2 = paramPersistableBundle.getLong("flexMillis");
    Bundle localBundle = new Bundle();
    Object localObject2 = paramPersistableBundle.getPersistableBundle("syncExtras");
    if (localObject2 != null) {
      localBundle.putAll((PersistableBundle)localObject2);
    }
    localObject2 = paramPersistableBundle.keySet().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject3 = (String)((Iterator)localObject2).next();
      if ((localObject3 != null) && (((String)localObject3).startsWith("ACCOUNT:")))
      {
        String str4 = ((String)localObject3).substring(8);
        localObject3 = paramPersistableBundle.getPersistableBundle((String)localObject3);
        localBundle.putParcelable(str4, new Account(((PersistableBundle)localObject3).getString("accountName"), ((PersistableBundle)localObject3).getString("accountType")));
      }
    }
    localObject1 = new SyncOperation(new SyncStorageEngine.EndPoint(new Account((String)localObject1, str1), str2, i), j, str3, k, m, localBundle, bool1, bool2, n, l1, l2);
    ((SyncOperation)localObject1).jobId = paramPersistableBundle.getInt("jobId");
    ((SyncOperation)localObject1).expectedRuntime = paramPersistableBundle.getLong("expectedRuntime");
    ((SyncOperation)localObject1).retries = paramPersistableBundle.getInt("retries");
    return (SyncOperation)localObject1;
  }
  
  static String reasonToString(PackageManager paramPackageManager, int paramInt)
  {
    if (paramInt >= 0)
    {
      if (paramPackageManager != null)
      {
        String[] arrayOfString = paramPackageManager.getPackagesForUid(paramInt);
        if ((arrayOfString != null) && (arrayOfString.length == 1)) {
          return arrayOfString[0];
        }
        paramPackageManager = paramPackageManager.getNameForUid(paramInt);
        if (paramPackageManager != null) {
          return paramPackageManager;
        }
        return String.valueOf(paramInt);
      }
      return String.valueOf(paramInt);
    }
    int i = -paramInt - 1;
    if (i >= REASON_NAMES.length) {
      return String.valueOf(paramInt);
    }
    return REASON_NAMES[i];
  }
  
  private String toKey()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("provider: ").append(this.target.provider);
    localStringBuilder.append(" account {name=").append(this.target.account.name).append(", user=").append(this.target.userId).append(", type=").append(this.target.account.type).append("}");
    localStringBuilder.append(" isPeriodic: ").append(this.isPeriodic);
    localStringBuilder.append(" period: ").append(this.periodMillis);
    localStringBuilder.append(" flex: ").append(this.flexMillis);
    localStringBuilder.append(" extras: ");
    extrasToStringBuilder(this.extras, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public SyncOperation createOneTimeSyncOperation()
  {
    if (!this.isPeriodic) {
      return null;
    }
    return new SyncOperation(this.target, this.owningUid, this.owningPackage, this.reason, this.syncSource, new Bundle(this.extras), this.allowParallelSyncs, false, this.jobId, this.periodMillis, this.flexMillis);
  }
  
  String dump(PackageManager paramPackageManager, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("JobId: ").append(this.jobId).append(", ").append(this.target.account.name).append(" u").append(this.target.userId).append(" (").append(this.target.account.type).append(")").append(", ").append(this.target.provider).append(", ");
    localStringBuilder.append(SyncStorageEngine.SOURCES[this.syncSource]);
    if (this.extras.getBoolean("expedited", false)) {
      localStringBuilder.append(", EXPEDITED");
    }
    localStringBuilder.append(", reason: ");
    localStringBuilder.append(reasonToString(paramPackageManager, this.reason));
    if (this.isPeriodic) {
      localStringBuilder.append(", period: ").append(this.periodMillis).append(", flexMillis: ").append(this.flexMillis);
    }
    if (!paramBoolean)
    {
      localStringBuilder.append("\n    ");
      localStringBuilder.append("owningUid=");
      UserHandle.formatUid(localStringBuilder, this.owningUid);
      localStringBuilder.append(" owningPackage=");
      localStringBuilder.append(this.owningPackage);
    }
    if ((paramBoolean) || (this.extras.keySet().isEmpty())) {}
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append("\n    ");
      extrasToStringBuilder(this.extras, localStringBuilder);
    }
  }
  
  int findPriority()
  {
    if (isInitialization()) {
      return 20;
    }
    if (isExpedited()) {
      return 10;
    }
    return 0;
  }
  
  boolean ignoreBackoff()
  {
    return this.extras.getBoolean("ignore_backoff", false);
  }
  
  boolean isConflict(SyncOperation paramSyncOperation)
  {
    paramSyncOperation = paramSyncOperation.target;
    if ((this.target.account.type.equals(paramSyncOperation.account.type)) && (this.target.provider.equals(paramSyncOperation.provider)) && (this.target.userId == paramSyncOperation.userId))
    {
      if (this.allowParallelSyncs) {
        return this.target.account.name.equals(paramSyncOperation.account.name);
      }
      return true;
    }
    return false;
  }
  
  boolean isDerivedFromFailedPeriodicSync()
  {
    return this.sourcePeriodicId != -1;
  }
  
  boolean isExpedited()
  {
    return this.extras.getBoolean("expedited", false);
  }
  
  boolean isIgnoreSettings()
  {
    return this.extras.getBoolean("ignore_settings", false);
  }
  
  boolean isInitialization()
  {
    return this.extras.getBoolean("initialize", false);
  }
  
  boolean isManual()
  {
    return this.extras.getBoolean("force", false);
  }
  
  boolean isNotAllowedOnMetered()
  {
    return this.extras.getBoolean("allow_metered", false);
  }
  
  boolean isReasonPeriodic()
  {
    return this.reason == -4;
  }
  
  boolean matchesPeriodicOperation(SyncOperation paramSyncOperation)
  {
    if ((this.target.matchesSpec(paramSyncOperation.target)) && (SyncManager.syncExtrasEquals(this.extras, paramSyncOperation.extras, true)) && (this.periodMillis == paramSyncOperation.periodMillis)) {
      return this.flexMillis == paramSyncOperation.flexMillis;
    }
    return false;
  }
  
  public Object[] toEventLog(int paramInt)
  {
    int i = this.syncSource;
    return new Object[] { this.target.provider, Integer.valueOf(paramInt), Integer.valueOf(i), Integer.valueOf(this.target.account.name.hashCode()) };
  }
  
  PersistableBundle toJobInfoExtras()
  {
    PersistableBundle localPersistableBundle1 = new PersistableBundle();
    PersistableBundle localPersistableBundle2 = new PersistableBundle();
    Iterator localIterator = this.extras.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = this.extras.get(str);
      if ((localObject instanceof Account))
      {
        localObject = (Account)localObject;
        PersistableBundle localPersistableBundle3 = new PersistableBundle();
        localPersistableBundle3.putString("accountName", ((Account)localObject).name);
        localPersistableBundle3.putString("accountType", ((Account)localObject).type);
        localPersistableBundle1.putPersistableBundle("ACCOUNT:" + str, localPersistableBundle3);
      }
      else if ((localObject instanceof Long))
      {
        localPersistableBundle2.putLong(str, ((Long)localObject).longValue());
      }
      else if ((localObject instanceof Integer))
      {
        localPersistableBundle2.putInt(str, ((Integer)localObject).intValue());
      }
      else if ((localObject instanceof Boolean))
      {
        localPersistableBundle2.putBoolean(str, ((Boolean)localObject).booleanValue());
      }
      else if ((localObject instanceof Float))
      {
        localPersistableBundle2.putDouble(str, ((Float)localObject).floatValue());
      }
      else if ((localObject instanceof Double))
      {
        localPersistableBundle2.putDouble(str, ((Double)localObject).doubleValue());
      }
      else if ((localObject instanceof String))
      {
        localPersistableBundle2.putString(str, (String)localObject);
      }
      else if (localObject == null)
      {
        localPersistableBundle2.putString(str, null);
      }
      else
      {
        Slog.e("SyncManager", "Unknown extra type.");
      }
    }
    localPersistableBundle1.putPersistableBundle("syncExtras", localPersistableBundle2);
    localPersistableBundle1.putBoolean("SyncManagerJob", true);
    localPersistableBundle1.putString("provider", this.target.provider);
    localPersistableBundle1.putString("accountName", this.target.account.name);
    localPersistableBundle1.putString("accountType", this.target.account.type);
    localPersistableBundle1.putInt("userId", this.target.userId);
    localPersistableBundle1.putInt("owningUid", this.owningUid);
    localPersistableBundle1.putString("owningPackage", this.owningPackage);
    localPersistableBundle1.putInt("reason", this.reason);
    localPersistableBundle1.putInt("source", this.syncSource);
    localPersistableBundle1.putBoolean("allowParallelSyncs", this.allowParallelSyncs);
    localPersistableBundle1.putInt("jobId", this.jobId);
    localPersistableBundle1.putBoolean("isPeriodic", this.isPeriodic);
    localPersistableBundle1.putInt("sourcePeriodicId", this.sourcePeriodicId);
    localPersistableBundle1.putLong("periodMillis", this.periodMillis);
    localPersistableBundle1.putLong("flexMillis", this.flexMillis);
    localPersistableBundle1.putLong("expectedRuntime", this.expectedRuntime);
    localPersistableBundle1.putInt("retries", this.retries);
    return localPersistableBundle1;
  }
  
  public String toString()
  {
    return dump(null, true);
  }
  
  String wakeLockName()
  {
    if (this.wakeLockName != null) {
      return this.wakeLockName;
    }
    String str = this.target.provider + "/" + this.target.account.type + "/" + this.target.account.name;
    this.wakeLockName = str;
    return str;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/content/SyncOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */