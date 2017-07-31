package com.android.server.job.controllers;

import android.app.AppGlobals;
import android.app.job.JobInfo;
import android.app.job.JobInfo.TriggerContentUri;
import android.content.ComponentName;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.DateUtils;
import android.util.ArraySet;
import android.util.TimeUtils;
import java.io.PrintWriter;

public final class JobStatus
{
  static final int CONSTRAINTS_OF_INTEREST = 699;
  static final int CONSTRAINT_APP_NOT_IDLE = 64;
  static final int CONSTRAINT_CHARGING = 1;
  static final int CONSTRAINT_CONNECTIVITY = 32;
  static final int CONSTRAINT_CONTENT_TRIGGER = 128;
  static final int CONSTRAINT_DEADLINE = 4;
  static final int CONSTRAINT_DEVICE_NOT_DOZING = 256;
  static final int CONSTRAINT_IDLE = 8;
  static final int CONSTRAINT_NOT_ROAMING = 512;
  static final int CONSTRAINT_TIMING_DELAY = 2;
  static final int CONSTRAINT_UNMETERED = 16;
  public static final long DEFAULT_TRIGGER_MAX_DELAY = 120000L;
  public static final long DEFAULT_TRIGGER_UPDATE_DELAY = 10000L;
  public static final long MIN_TRIGGER_MAX_DELAY = 1000L;
  public static final long MIN_TRIGGER_UPDATE_DELAY = 500L;
  public static final long NO_EARLIEST_RUNTIME = 0L;
  public static final long NO_LATEST_RUNTIME = Long.MAX_VALUE;
  public static final int OVERRIDE_FULL = 2;
  public static final int OVERRIDE_SOFT = 1;
  static final int SOFT_OVERRIDE_CONSTRAINTS = 11;
  final String batteryName;
  final int callingUid;
  public ArraySet<String> changedAuthorities;
  public ArraySet<Uri> changedUris;
  ContentObserverController.JobInstance contentObserverJobInstance;
  public boolean dozeWhitelisted;
  private final long earliestRunTimeElapsedMillis;
  final JobInfo job;
  public int lastEvaluatedPriority;
  private final long latestRunTimeElapsedMillis;
  private final int numFailures;
  public int overrideState = 0;
  final int requiredConstraints;
  int satisfiedConstraints = 0;
  final String sourcePackageName;
  final String sourceTag;
  final int sourceUid;
  final int sourceUserId;
  final String tag;
  
  private JobStatus(JobInfo paramJobInfo, int paramInt1, String paramString1, int paramInt2, String paramString2, int paramInt3, long paramLong1, long paramLong2)
  {
    this.job = paramJobInfo;
    this.callingUid = paramInt1;
    int j = -1;
    int i = j;
    if (paramInt2 != -1)
    {
      i = j;
      if (paramString1 == null) {}
    }
    try
    {
      i = AppGlobals.getPackageManager().getPackageUid(paramString1, 0, paramInt2);
      if (i == -1)
      {
        this.sourceUid = paramInt1;
        this.sourceUserId = UserHandle.getUserId(paramInt1);
        this.sourcePackageName = paramJobInfo.getService().getPackageName();
        this.sourceTag = null;
        if (this.sourceTag == null) {
          break label345;
        }
      }
      label345:
      for (paramString1 = this.sourceTag + ":" + paramJobInfo.getService().getPackageName();; paramString1 = paramJobInfo.getService().flattenToShortString())
      {
        this.batteryName = paramString1;
        this.tag = ("*job*/" + this.batteryName);
        this.earliestRunTimeElapsedMillis = paramLong1;
        this.latestRunTimeElapsedMillis = paramLong2;
        this.numFailures = paramInt3;
        paramInt2 = 0;
        if (paramJobInfo.getNetworkType() == 1) {
          paramInt2 = 32;
        }
        paramInt1 = paramInt2;
        if (paramJobInfo.getNetworkType() == 2) {
          paramInt1 = paramInt2 | 0x10;
        }
        paramInt2 = paramInt1;
        if (paramJobInfo.getNetworkType() == 3) {
          paramInt2 = paramInt1 | 0x200;
        }
        paramInt1 = paramInt2;
        if (paramJobInfo.isRequireCharging()) {
          paramInt1 = paramInt2 | 0x1;
        }
        paramInt2 = paramInt1;
        if (paramLong1 != 0L) {
          paramInt2 = paramInt1 | 0x2;
        }
        paramInt1 = paramInt2;
        if (paramLong2 != Long.MAX_VALUE) {
          paramInt1 = paramInt2 | 0x4;
        }
        paramInt2 = paramInt1;
        if (paramJobInfo.isRequireDeviceIdle()) {
          paramInt2 = paramInt1 | 0x8;
        }
        paramInt1 = paramInt2;
        if (paramJobInfo.getTriggerContentUris() != null) {
          paramInt1 = paramInt2 | 0x80;
        }
        this.requiredConstraints = paramInt1;
        return;
        this.sourceUid = i;
        this.sourceUserId = paramInt2;
        this.sourcePackageName = paramString1;
        this.sourceTag = paramString2;
        break;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        i = j;
      }
    }
  }
  
  public JobStatus(JobInfo paramJobInfo, int paramInt1, String paramString1, int paramInt2, String paramString2, long paramLong1, long paramLong2)
  {
    this(paramJobInfo, paramInt1, paramString1, paramInt2, paramString2, 0, paramLong1, paramLong2);
  }
  
  public JobStatus(JobStatus paramJobStatus)
  {
    this(paramJobStatus.getJob(), paramJobStatus.getUid(), paramJobStatus.getSourcePackageName(), paramJobStatus.getSourceUserId(), paramJobStatus.getSourceTag(), paramJobStatus.getNumFailures(), paramJobStatus.getEarliestRunTime(), paramJobStatus.getLatestRunTimeElapsed());
  }
  
  public JobStatus(JobStatus paramJobStatus, long paramLong1, long paramLong2, int paramInt)
  {
    this(paramJobStatus.job, paramJobStatus.getUid(), paramJobStatus.getSourcePackageName(), paramJobStatus.getSourceUserId(), paramJobStatus.getSourceTag(), paramInt, paramLong1, paramLong2);
  }
  
  public static JobStatus createFromJobInfo(JobInfo paramJobInfo, int paramInt1, String paramString1, int paramInt2, String paramString2)
  {
    long l2 = SystemClock.elapsedRealtime();
    long l1;
    if (paramJobInfo.isPeriodic())
    {
      l1 = l2 + paramJobInfo.getIntervalMillis();
      l2 = l1 - paramJobInfo.getFlexMillis();
    }
    for (;;)
    {
      return new JobStatus(paramJobInfo, paramInt1, paramString1, paramInt2, paramString2, 0, l2, l1);
      if (paramJobInfo.hasEarlyConstraint()) {}
      for (l1 = l2 + paramJobInfo.getMinLatencyMillis();; l1 = 0L)
      {
        if (!paramJobInfo.hasLateConstraint()) {
          break label98;
        }
        l3 = l2 + paramJobInfo.getMaxExecutionDelayMillis();
        l2 = l1;
        l1 = l3;
        break;
      }
      label98:
      long l3 = Long.MAX_VALUE;
      l2 = l1;
      l1 = l3;
    }
  }
  
  private String formatRunTime(long paramLong1, long paramLong2)
  {
    if (paramLong1 == paramLong2) {
      return "none";
    }
    paramLong1 -= SystemClock.elapsedRealtime();
    if (paramLong1 > 0L) {
      return DateUtils.formatElapsedTime(paramLong1 / 1000L);
    }
    return "-" + DateUtils.formatElapsedTime(paramLong1 / -1000L);
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString, boolean paramBoolean)
  {
    paramPrintWriter.print(paramString);
    UserHandle.formatUid(paramPrintWriter, this.callingUid);
    paramPrintWriter.print(" tag=");
    paramPrintWriter.println(this.tag);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Source: uid=");
    UserHandle.formatUid(paramPrintWriter, getSourceUid());
    paramPrintWriter.print(" user=");
    paramPrintWriter.print(getSourceUserId());
    paramPrintWriter.print(" pkg=");
    paramPrintWriter.println(getSourcePackageName());
    int i;
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("JobInfo:");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  Service: ");
      paramPrintWriter.println(this.job.getService().flattenToShortString());
      if (this.job.isPeriodic())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  PERIODIC: interval=");
        TimeUtils.formatDuration(this.job.getIntervalMillis(), paramPrintWriter);
        paramPrintWriter.print(" flex=");
        TimeUtils.formatDuration(this.job.getFlexMillis(), paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.job.isPersisted())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  PERSISTED");
      }
      if (this.job.getPriority() != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Priority: ");
        paramPrintWriter.println(this.job.getPriority());
      }
      if (this.job.getFlags() != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Flags: ");
        paramPrintWriter.println(Integer.toHexString(this.job.getFlags()));
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  Requires: charging=");
      paramPrintWriter.print(this.job.isRequireCharging());
      paramPrintWriter.print(" deviceIdle=");
      paramPrintWriter.println(this.job.isRequireDeviceIdle());
      if (this.job.getTriggerContentUris() != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  Trigger content URIs:");
        i = 0;
        while (i < this.job.getTriggerContentUris().length)
        {
          JobInfo.TriggerContentUri localTriggerContentUri = this.job.getTriggerContentUris()[i];
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    ");
          paramPrintWriter.print(Integer.toHexString(localTriggerContentUri.getFlags()));
          paramPrintWriter.print(' ');
          paramPrintWriter.println(localTriggerContentUri.getUri());
          i += 1;
        }
        if (this.job.getTriggerContentUpdateDelay() >= 0L)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  Trigger update delay: ");
          TimeUtils.formatDuration(this.job.getTriggerContentUpdateDelay(), paramPrintWriter);
          paramPrintWriter.println();
        }
        if (this.job.getTriggerContentMaxDelay() >= 0L)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  Trigger max delay: ");
          TimeUtils.formatDuration(this.job.getTriggerContentMaxDelay(), paramPrintWriter);
          paramPrintWriter.println();
        }
      }
      if (this.job.getNetworkType() != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Network type: ");
        paramPrintWriter.println(this.job.getNetworkType());
      }
      if (this.job.getMinLatencyMillis() != 0L)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Minimum latency: ");
        TimeUtils.formatDuration(this.job.getMinLatencyMillis(), paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.job.getMaxExecutionDelayMillis() != 0L)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  Max execution delay: ");
        TimeUtils.formatDuration(this.job.getMaxExecutionDelayMillis(), paramPrintWriter);
        paramPrintWriter.println();
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  Backoff: policy=");
      paramPrintWriter.print(this.job.getBackoffPolicy());
      paramPrintWriter.print(" initial=");
      TimeUtils.formatDuration(this.job.getInitialBackoffMillis(), paramPrintWriter);
      paramPrintWriter.println();
      if (this.job.hasEarlyConstraint())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  Has early constraint");
      }
      if (this.job.hasLateConstraint())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("  Has late constraint");
      }
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Required constraints:");
    dumpConstraints(paramPrintWriter, this.requiredConstraints);
    paramPrintWriter.println();
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Satisfied constraints:");
      dumpConstraints(paramPrintWriter, this.satisfiedConstraints);
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Unsatisfied constraints:");
      dumpConstraints(paramPrintWriter, this.requiredConstraints & this.satisfiedConstraints);
      paramPrintWriter.println();
      if (this.dozeWhitelisted)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Doze whitelisted: true");
      }
    }
    if (this.changedAuthorities != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Changed authorities:");
      i = 0;
      while (i < this.changedAuthorities.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  ");
        paramPrintWriter.println((String)this.changedAuthorities.valueAt(i));
        i += 1;
      }
      if (this.changedUris != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Changed URIs:");
        i = 0;
        while (i < this.changedUris.size())
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.println(this.changedUris.valueAt(i));
          i += 1;
        }
      }
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Earliest run time: ");
    paramPrintWriter.println(formatRunTime(this.earliestRunTimeElapsedMillis, 0L));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Latest run time: ");
    paramPrintWriter.println(formatRunTime(this.latestRunTimeElapsedMillis, Long.MAX_VALUE));
    if (this.numFailures != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Num failures: ");
      paramPrintWriter.println(this.numFailures);
    }
  }
  
  void dumpConstraints(PrintWriter paramPrintWriter, int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      paramPrintWriter.print(" CHARGING");
    }
    if ((paramInt & 0x2) != 0) {
      paramPrintWriter.print(" TIMING_DELAY");
    }
    if ((paramInt & 0x4) != 0) {
      paramPrintWriter.print(" DEADLINE");
    }
    if ((paramInt & 0x8) != 0) {
      paramPrintWriter.print(" IDLE");
    }
    if ((paramInt & 0x20) != 0) {
      paramPrintWriter.print(" CONNECTIVITY");
    }
    if ((paramInt & 0x10) != 0) {
      paramPrintWriter.print(" UNMETERED");
    }
    if ((paramInt & 0x200) != 0) {
      paramPrintWriter.print(" NOT_ROAMING");
    }
    if ((paramInt & 0x40) != 0) {
      paramPrintWriter.print(" APP_NOT_IDLE");
    }
    if ((paramInt & 0x80) != 0) {
      paramPrintWriter.print(" CONTENT_TRIGGER");
    }
    if ((paramInt & 0x100) != 0) {
      paramPrintWriter.print(" DEVICE_NOT_DOZING");
    }
  }
  
  public String getBatteryName()
  {
    return this.batteryName;
  }
  
  public long getEarliestRunTime()
  {
    return this.earliestRunTimeElapsedMillis;
  }
  
  public PersistableBundle getExtras()
  {
    return this.job.getExtras();
  }
  
  public int getFlags()
  {
    return this.job.getFlags();
  }
  
  public JobInfo getJob()
  {
    return this.job;
  }
  
  public int getJobId()
  {
    return this.job.getId();
  }
  
  public long getLatestRunTimeElapsed()
  {
    return this.latestRunTimeElapsedMillis;
  }
  
  public int getNumFailures()
  {
    return this.numFailures;
  }
  
  public int getPriority()
  {
    return this.job.getPriority();
  }
  
  public ComponentName getServiceComponent()
  {
    return this.job.getService();
  }
  
  public int getServiceToken()
  {
    return this.callingUid;
  }
  
  public String getSourcePackageName()
  {
    return this.sourcePackageName;
  }
  
  public String getSourceTag()
  {
    return this.sourceTag;
  }
  
  public int getSourceUid()
  {
    return this.sourceUid;
  }
  
  public int getSourceUserId()
  {
    return this.sourceUserId;
  }
  
  public String getTag()
  {
    return this.tag;
  }
  
  public long getTriggerContentMaxDelay()
  {
    long l = this.job.getTriggerContentMaxDelay();
    if (l < 0L) {
      return 120000L;
    }
    return Math.max(l, 1000L);
  }
  
  public long getTriggerContentUpdateDelay()
  {
    long l = this.job.getTriggerContentUpdateDelay();
    if (l < 0L) {
      return 10000L;
    }
    return Math.max(l, 500L);
  }
  
  public int getUid()
  {
    return this.callingUid;
  }
  
  public int getUserId()
  {
    return UserHandle.getUserId(this.callingUid);
  }
  
  public boolean hasChargingConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasConnectivityConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasContentTriggerConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x80) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasDeadlineConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasIdleConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasNotRoamingConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x200) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasTimingDelayConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasUnmeteredConstraint()
  {
    boolean bool = false;
    if ((this.requiredConstraints & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isConstraintSatisfied(int paramInt)
  {
    boolean bool = false;
    if ((this.satisfiedConstraints & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isConstraintsSatisfied()
  {
    if (this.overrideState == 2) {
      return true;
    }
    int k = this.requiredConstraints & 0x2BB;
    int j = this.satisfiedConstraints & 0x2BB;
    int i = j;
    if (this.overrideState == 1) {
      i = j | this.requiredConstraints & 0xB;
    }
    return (i & k) == k;
  }
  
  public boolean isPersisted()
  {
    return this.job.isPersisted();
  }
  
  public boolean isReady()
  {
    int i;
    int j;
    label40:
    boolean bool;
    if ((!this.job.isPeriodic()) && (hasDeadlineConstraint())) {
      if ((this.satisfiedConstraints & 0x4) != 0)
      {
        i = 1;
        if ((this.satisfiedConstraints & 0x40) == 0) {
          break label92;
        }
        j = 1;
        if ((this.satisfiedConstraints & 0x100) != 0) {
          break label97;
        }
        if ((this.job.getFlags() & 0x1) == 0) {
          break label102;
        }
        bool = true;
      }
    }
    for (;;)
    {
      if (((!isConstraintsSatisfied()) && (i == 0)) || (j == 0)) {
        break label107;
      }
      return bool;
      i = 0;
      break;
      i = 0;
      break;
      label92:
      j = 0;
      break label40;
      label97:
      bool = true;
      continue;
      label102:
      bool = false;
    }
    label107:
    return false;
  }
  
  public boolean matches(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.job.getId() == paramInt2)
    {
      bool1 = bool2;
      if (this.callingUid == paramInt1) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void printUniqueId(PrintWriter paramPrintWriter)
  {
    UserHandle.formatUid(paramPrintWriter, this.callingUid);
    paramPrintWriter.print("/");
    paramPrintWriter.print(this.job.getId());
  }
  
  boolean setAppNotIdleConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(64, paramBoolean);
  }
  
  boolean setChargingConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(1, paramBoolean);
  }
  
  boolean setConnectivityConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(32, paramBoolean);
  }
  
  boolean setConstraintSatisfied(int paramInt, boolean paramBoolean)
  {
    if ((this.satisfiedConstraints & paramInt) != 0) {}
    for (boolean bool = true; bool == paramBoolean; bool = false) {
      return false;
    }
    int j = this.satisfiedConstraints;
    if (paramBoolean) {}
    for (int i = paramInt;; i = 0)
    {
      this.satisfiedConstraints = (j & paramInt | i);
      return true;
    }
  }
  
  boolean setContentTriggerConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(128, paramBoolean);
  }
  
  boolean setDeadlineConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(4, paramBoolean);
  }
  
  boolean setDeviceNotDozingConstraintSatisfied(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.dozeWhitelisted = paramBoolean2;
    return setConstraintSatisfied(256, paramBoolean1);
  }
  
  boolean setIdleConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(8, paramBoolean);
  }
  
  boolean setNotRoamingConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(512, paramBoolean);
  }
  
  boolean setTimingDelayConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(2, paramBoolean);
  }
  
  boolean setUnmeteredConstraintSatisfied(boolean paramBoolean)
  {
    return setConstraintSatisfied(16, paramBoolean);
  }
  
  public boolean shouldDump(int paramInt)
  {
    if ((paramInt == -1) || (UserHandle.getAppId(getUid()) == paramInt)) {}
    while (UserHandle.getAppId(getSourceUid()) == paramInt) {
      return true;
    }
    return false;
  }
  
  public String toShortString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" #");
    UserHandle.formatUid(localStringBuilder, this.callingUid);
    localStringBuilder.append("/");
    localStringBuilder.append(this.job.getId());
    localStringBuilder.append(' ');
    localStringBuilder.append(this.batteryName);
    return localStringBuilder.toString();
  }
  
  public String toShortStringExceptUniqueId()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(' ');
    localStringBuilder.append(this.batteryName);
    return localStringBuilder.toString();
  }
  
  public String toString()
  {
    boolean bool2 = true;
    Object localObject = new StringBuilder().append(String.valueOf(hashCode()).substring(0, 3)).append("..").append(":[").append(this.job.getService()).append(",jId=").append(this.job.getId()).append(",u").append(getUserId()).append(",suid=").append(getSourceUid()).append(",R=(").append(formatRunTime(this.earliestRunTimeElapsedMillis, 0L)).append(",").append(formatRunTime(this.latestRunTimeElapsedMillis, Long.MAX_VALUE)).append(")").append(",N=").append(this.job.getNetworkType()).append(",C=").append(this.job.isRequireCharging()).append(",I=").append(this.job.isRequireDeviceIdle()).append(",U=");
    boolean bool1;
    label252:
    label277:
    StringBuilder localStringBuilder;
    if (this.job.getTriggerContentUris() != null)
    {
      bool1 = true;
      localObject = ((StringBuilder)localObject).append(bool1).append(",F=").append(this.numFailures).append(",P=").append(this.job.isPersisted()).append(",ANI=");
      if ((this.satisfiedConstraints & 0x40) == 0) {
        break label316;
      }
      bool1 = true;
      localObject = ((StringBuilder)localObject).append(bool1).append(",DND=");
      if ((this.satisfiedConstraints & 0x100) == 0) {
        break label321;
      }
      bool1 = bool2;
      localStringBuilder = ((StringBuilder)localObject).append(bool1);
      if (!isReady()) {
        break label326;
      }
    }
    label316:
    label321:
    label326:
    for (localObject = "(READY)";; localObject = "")
    {
      return (String)localObject + "]";
      bool1 = false;
      break;
      bool1 = false;
      break label252;
      bool1 = false;
      break label277;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/job/controllers/JobStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */