package com.android.server.am;

import android.app.ActivityManager.ProcessErrorStateInfo;
import android.app.Dialog;
import android.app.IApplicationThread;
import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.EventLog;
import android.util.PrintWriterPrinter;
import android.util.SeempLog;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats.ProcessStateHolder;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.BatteryStatsImpl.Uid.Proc;
import java.io.PrintWriter;
import java.util.ArrayList;

final class ProcessRecord
{
  private static final String TAG = "ActivityManager";
  final ArrayList<ReceiverRecord> ReceiverRecords = new ArrayList();
  final ArrayList<ActivityRecord> activities = new ArrayList();
  int adjSeq;
  Object adjSource;
  int adjSourceProcState;
  Object adjTarget;
  String adjType;
  int adjTypeCode;
  Dialog anrDialog;
  boolean bad;
  ProcessState baseProcessTracker;
  boolean cached;
  CompatibilityInfo compat;
  final ArrayList<ContentProviderConnection> conProviders = new ArrayList();
  final ArraySet<ConnectionRecord> connections = new ArraySet();
  Dialog crashDialog;
  Runnable crashHandler;
  boolean crashing;
  ActivityManager.ProcessErrorStateInfo crashingReport;
  int curAdj;
  long curCpuTime;
  long curCpuTimeBgMonitor;
  BatteryStatsImpl.Uid.Proc curProcBatteryStats;
  int curProcState = -1;
  int curRawAdj;
  BroadcastRecord curReceiver;
  int curSchedGroup;
  IBinder.DeathRecipient deathRecipient;
  boolean debugging;
  boolean empty;
  ComponentName errorReportReceiver;
  boolean execServicesFg;
  final ArraySet<ServiceRecord> executingServices = new ArraySet();
  long fgInteractionTime;
  boolean forceCrashReport;
  IBinder forcingToForeground;
  boolean foregroundActivities;
  boolean foregroundServices;
  int[] gids;
  boolean hasAboveClient;
  boolean hasClientActivities;
  boolean hasShownUi;
  boolean hasStartedServices;
  boolean hasTopUi;
  public boolean inFullBackup;
  final ApplicationInfo info;
  long initialIdlePss;
  String instructionSet;
  Bundle instrumentationArguments;
  ComponentName instrumentationClass;
  ApplicationInfo instrumentationInfo;
  String instrumentationProfileFile;
  ComponentName instrumentationResultClass;
  IUiAutomationConnection instrumentationUiAutomationConnection;
  IInstrumentationWatcher instrumentationWatcher;
  long interactionEventTime;
  boolean isEmbryo = false;
  final boolean isolated;
  boolean killed;
  boolean killedByAm;
  long lastActivityTime;
  long lastCachedPss;
  long lastCachedSwapPss;
  long lastContactProviderTime;
  long lastCpuTime;
  long[] lastCpuTimeBgMonitor = new long[4];
  long lastFgTime;
  long lastLowMemory;
  long lastProviderTime;
  long lastPss;
  long lastPssTime;
  long lastRequestedGc;
  long[] lastRxBytes = new long[4];
  long lastStateTime;
  long lastSwapPss;
  long[] lastTxBytes = new long[4];
  long lastWakeTime;
  int lruSeq;
  private final BatteryStatsImpl mBatteryStats;
  int maxAdj;
  long nextPssTime;
  boolean notCachedSinceIdle;
  boolean notResponding;
  ActivityManager.ProcessErrorStateInfo notRespondingReport;
  boolean pendingUiClean;
  int permRequestCount;
  boolean persistent;
  int pid;
  ArraySet<String> pkgDeps;
  final ArrayMap<String, ProcessStats.ProcessStateHolder> pkgList = new ArrayMap();
  String procStatFile;
  boolean procStateChanged;
  final String processName;
  int pssProcState = -1;
  final ArrayMap<String, ContentProviderRecord> pubProviders = new ArrayMap();
  final ArraySet<ReceiverList> receivers = new ArraySet();
  boolean removed;
  int renderThreadTid;
  boolean repForegroundActivities;
  int repProcState = -1;
  boolean reportLowMemory;
  boolean reportedInteraction;
  String requiredAbi;
  int savedPriority;
  boolean serviceHighRam;
  boolean serviceb;
  final ArraySet<ServiceRecord> services = new ArraySet();
  int setAdj;
  boolean setIsForeground;
  int setProcState = -1;
  int setRawAdj;
  int setSchedGroup;
  String shortStringName;
  boolean starting;
  String stringName;
  boolean systemNoUi;
  IApplicationThread thread;
  boolean treatLikeActivity;
  int trimMemoryLevel;
  final int uid;
  UidRecord uidRecord;
  boolean unlocked;
  final int userId;
  boolean usingWrapper;
  int verifiedAdj;
  int vrThreadTid;
  Dialog waitDialog;
  boolean waitedForDebugger;
  String waitingToKill;
  boolean whitelistManager;
  
  ProcessRecord(BatteryStatsImpl paramBatteryStatsImpl, ApplicationInfo paramApplicationInfo, String paramString, int paramInt)
  {
    this.mBatteryStats = paramBatteryStatsImpl;
    this.info = paramApplicationInfo;
    if (paramApplicationInfo.uid != paramInt) {}
    for (boolean bool = true;; bool = false)
    {
      this.isolated = bool;
      this.uid = paramInt;
      this.userId = UserHandle.getUserId(paramInt);
      this.processName = paramString;
      this.pkgList.put(paramApplicationInfo.packageName, new ProcessStats.ProcessStateHolder(paramApplicationInfo.versionCode));
      this.maxAdj = 1001;
      this.setRawAdj = 55536;
      this.curRawAdj = 55536;
      this.verifiedAdj = 55536;
      this.setAdj = 55536;
      this.curAdj = 55536;
      this.persistent = false;
      this.removed = false;
      long l = SystemClock.uptimeMillis();
      this.nextPssTime = l;
      this.lastPssTime = l;
      this.lastStateTime = l;
      this.lastFgTime = 0L;
      this.lastContactProviderTime = 0L;
      this.permRequestCount = 0;
      return;
    }
  }
  
  public boolean addPackage(String paramString, int paramInt, ProcessStatsService paramProcessStatsService)
  {
    if (!this.pkgList.containsKey(paramString))
    {
      ProcessStats.ProcessStateHolder localProcessStateHolder = new ProcessStats.ProcessStateHolder(paramInt);
      if (this.baseProcessTracker != null)
      {
        localProcessStateHolder.state = paramProcessStatsService.getProcessStateLocked(paramString, this.uid, paramInt, this.processName);
        this.pkgList.put(paramString, localProcessStateHolder);
        if (localProcessStateHolder.state != this.baseProcessTracker) {
          localProcessStateHolder.state.makeActive();
        }
      }
      for (;;)
      {
        return true;
        this.pkgList.put(paramString, localProcessStateHolder);
      }
    }
    return false;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    long l1 = SystemClock.uptimeMillis();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("user #");
    paramPrintWriter.print(this.userId);
    paramPrintWriter.print(" uid=");
    paramPrintWriter.print(this.info.uid);
    if (this.uid != this.info.uid)
    {
      paramPrintWriter.print(" ISOLATED uid=");
      paramPrintWriter.print(this.uid);
    }
    paramPrintWriter.print(" gids={");
    if (this.gids != null)
    {
      i = 0;
      while (i < this.gids.length)
      {
        if (i != 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print(this.gids[i]);
        i += 1;
      }
    }
    paramPrintWriter.println("}");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("requiredAbi=");
    paramPrintWriter.print(this.requiredAbi);
    paramPrintWriter.print(" instructionSet=");
    paramPrintWriter.println(this.instructionSet);
    if (this.info.className != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("class=");
      paramPrintWriter.println(this.info.className);
    }
    if (this.info.manageSpaceActivityName != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("manageSpaceActivityName=");
      paramPrintWriter.println(this.info.manageSpaceActivityName);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("dir=");
    paramPrintWriter.print(this.info.sourceDir);
    paramPrintWriter.print(" publicDir=");
    paramPrintWriter.print(this.info.publicSourceDir);
    paramPrintWriter.print(" data=");
    paramPrintWriter.println(this.info.dataDir);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("packageList={");
    int i = 0;
    while (i < this.pkgList.size())
    {
      if (i > 0) {
        paramPrintWriter.print(", ");
      }
      paramPrintWriter.print((String)this.pkgList.keyAt(i));
      i += 1;
    }
    paramPrintWriter.println("}");
    if (this.pkgDeps != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("packageDependencies={");
      i = 0;
      while (i < this.pkgDeps.size())
      {
        if (i > 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print((String)this.pkgDeps.valueAt(i));
        i += 1;
      }
      paramPrintWriter.println("}");
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("compat=");
    paramPrintWriter.println(this.compat);
    if ((this.instrumentationClass != null) || (this.instrumentationProfileFile != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("instrumentationClass=");
      paramPrintWriter.print(this.instrumentationClass);
      paramPrintWriter.print(" instrumentationProfileFile=");
      paramPrintWriter.println(this.instrumentationProfileFile);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("instrumentationArguments=");
      paramPrintWriter.println(this.instrumentationArguments);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("instrumentationInfo=");
      paramPrintWriter.println(this.instrumentationInfo);
      if (this.instrumentationInfo != null) {
        this.instrumentationInfo.dump(new PrintWriterPrinter(paramPrintWriter), paramString + "  ");
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("thread=");
      paramPrintWriter.println(this.thread);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("pid=");
      paramPrintWriter.print(this.pid);
      paramPrintWriter.print(" starting=");
      paramPrintWriter.println(this.starting);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("lastActivityTime=");
      TimeUtils.formatDuration(this.lastActivityTime, l1, paramPrintWriter);
      paramPrintWriter.print(" lastPssTime=");
      TimeUtils.formatDuration(this.lastPssTime, l1, paramPrintWriter);
      paramPrintWriter.print(" nextPssTime=");
      TimeUtils.formatDuration(this.nextPssTime, l1, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("adjSeq=");
      paramPrintWriter.print(this.adjSeq);
      paramPrintWriter.print(" lruSeq=");
      paramPrintWriter.print(this.lruSeq);
      paramPrintWriter.print(" lastPss=");
      DebugUtils.printSizeValue(paramPrintWriter, this.lastPss * 1024L);
      paramPrintWriter.print(" lastSwapPss=");
      DebugUtils.printSizeValue(paramPrintWriter, this.lastSwapPss * 1024L);
      paramPrintWriter.print(" lastCachedPss=");
      DebugUtils.printSizeValue(paramPrintWriter, this.lastCachedPss * 1024L);
      paramPrintWriter.print(" lastCachedSwapPss=");
      DebugUtils.printSizeValue(paramPrintWriter, this.lastCachedSwapPss * 1024L);
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("cached=");
      paramPrintWriter.print(this.cached);
      paramPrintWriter.print(" empty=");
      paramPrintWriter.println(this.empty);
      if (this.serviceb)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("serviceb=");
        paramPrintWriter.print(this.serviceb);
        paramPrintWriter.print(" serviceHighRam=");
        paramPrintWriter.println(this.serviceHighRam);
      }
      if (this.notCachedSinceIdle)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("notCachedSinceIdle=");
        paramPrintWriter.print(this.notCachedSinceIdle);
        paramPrintWriter.print(" initialIdlePss=");
        paramPrintWriter.println(this.initialIdlePss);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("oom: max=");
      paramPrintWriter.print(this.maxAdj);
      paramPrintWriter.print(" curRaw=");
      paramPrintWriter.print(this.curRawAdj);
      paramPrintWriter.print(" setRaw=");
      paramPrintWriter.print(this.setRawAdj);
      paramPrintWriter.print(" cur=");
      paramPrintWriter.print(this.curAdj);
      paramPrintWriter.print(" set=");
      paramPrintWriter.println(this.setAdj);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("curSchedGroup=");
      paramPrintWriter.print(this.curSchedGroup);
      paramPrintWriter.print(" setSchedGroup=");
      paramPrintWriter.print(this.setSchedGroup);
      paramPrintWriter.print(" systemNoUi=");
      paramPrintWriter.print(this.systemNoUi);
      paramPrintWriter.print(" trimMemoryLevel=");
      paramPrintWriter.println(this.trimMemoryLevel);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("vrThreadTid=");
      paramPrintWriter.print(this.vrThreadTid);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("curProcState=");
      paramPrintWriter.print(this.curProcState);
      paramPrintWriter.print(" repProcState=");
      paramPrintWriter.print(this.repProcState);
      paramPrintWriter.print(" pssProcState=");
      paramPrintWriter.print(this.pssProcState);
      paramPrintWriter.print(" setProcState=");
      paramPrintWriter.print(this.setProcState);
      paramPrintWriter.print(" lastStateTime=");
      TimeUtils.formatDuration(this.lastStateTime, l1, paramPrintWriter);
      paramPrintWriter.println();
      if ((this.hasShownUi) || (this.pendingUiClean) || (this.hasAboveClient) || (this.treatLikeActivity))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("hasShownUi=");
        paramPrintWriter.print(this.hasShownUi);
        paramPrintWriter.print(" pendingUiClean=");
        paramPrintWriter.print(this.pendingUiClean);
        paramPrintWriter.print(" hasAboveClient=");
        paramPrintWriter.print(this.hasAboveClient);
        paramPrintWriter.print(" treatLikeActivity=");
        paramPrintWriter.println(this.treatLikeActivity);
      }
      if ((this.setIsForeground) || (this.foregroundServices) || (this.forcingToForeground != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("setIsForeground=");
        paramPrintWriter.print(this.setIsForeground);
        paramPrintWriter.print(" foregroundServices=");
        paramPrintWriter.print(this.foregroundServices);
        paramPrintWriter.print(" forcingToForeground=");
        paramPrintWriter.println(this.forcingToForeground);
      }
      if ((this.reportedInteraction) || (this.fgInteractionTime != 0L))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("reportedInteraction=");
        paramPrintWriter.print(this.reportedInteraction);
        if (this.interactionEventTime != 0L)
        {
          paramPrintWriter.print(" time=");
          TimeUtils.formatDuration(this.interactionEventTime, SystemClock.elapsedRealtime(), paramPrintWriter);
        }
        if (this.fgInteractionTime != 0L)
        {
          paramPrintWriter.print(" fgInteractionTime=");
          TimeUtils.formatDuration(this.fgInteractionTime, SystemClock.elapsedRealtime(), paramPrintWriter);
        }
        paramPrintWriter.println();
      }
      if ((this.persistent) || (this.removed))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("persistent=");
        paramPrintWriter.print(this.persistent);
        paramPrintWriter.print(" removed=");
        paramPrintWriter.println(this.removed);
      }
      if ((this.hasClientActivities) || (this.foregroundActivities) || (this.repForegroundActivities))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("hasClientActivities=");
        paramPrintWriter.print(this.hasClientActivities);
        paramPrintWriter.print(" foregroundActivities=");
        paramPrintWriter.print(this.foregroundActivities);
        paramPrintWriter.print(" (rep=");
        paramPrintWriter.print(this.repForegroundActivities);
        paramPrintWriter.println(")");
      }
      if (this.lastProviderTime > 0L)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("lastProviderTime=");
        TimeUtils.formatDuration(this.lastProviderTime, l1, paramPrintWriter);
        paramPrintWriter.println();
      }
      if (this.hasStartedServices)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("hasStartedServices=");
        paramPrintWriter.println(this.hasStartedServices);
      }
      if (this.setProcState < 10) {}
    }
    for (;;)
    {
      synchronized (this.mBatteryStats)
      {
        long l2 = this.mBatteryStats.getProcessWakeTime(this.info.uid, this.pid, SystemClock.elapsedRealtime());
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("lastWakeTime=");
        paramPrintWriter.print(this.lastWakeTime);
        paramPrintWriter.print(" timeUsed=");
        TimeUtils.formatDuration(l2 - this.lastWakeTime, paramPrintWriter);
        paramPrintWriter.println("");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("lastCpuTime=");
        paramPrintWriter.print(this.lastCpuTime);
        paramPrintWriter.print(" timeUsed=");
        TimeUtils.formatDuration(this.curCpuTime - this.lastCpuTime, paramPrintWriter);
        paramPrintWriter.println("");
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("lastRequestedGc=");
        TimeUtils.formatDuration(this.lastRequestedGc, l1, paramPrintWriter);
        paramPrintWriter.print(" lastLowMemory=");
        TimeUtils.formatDuration(this.lastLowMemory, l1, paramPrintWriter);
        paramPrintWriter.print(" reportLowMemory=");
        paramPrintWriter.println(this.reportLowMemory);
        if ((this.killed) || (this.killedByAm) || (this.waitingToKill != null))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("killed=");
          paramPrintWriter.print(this.killed);
          paramPrintWriter.print(" killedByAm=");
          paramPrintWriter.print(this.killedByAm);
          paramPrintWriter.print(" waitingToKill=");
          paramPrintWriter.println(this.waitingToKill);
        }
        if ((this.debugging) || (this.crashing) || (this.crashDialog != null))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("debugging=");
          paramPrintWriter.print(this.debugging);
          paramPrintWriter.print(" crashing=");
          paramPrintWriter.print(this.crashing);
          paramPrintWriter.print(" ");
          paramPrintWriter.print(this.crashDialog);
          paramPrintWriter.print(" notResponding=");
          paramPrintWriter.print(this.notResponding);
          paramPrintWriter.print(" ");
          paramPrintWriter.print(this.anrDialog);
          paramPrintWriter.print(" bad=");
          paramPrintWriter.print(this.bad);
          if (this.errorReportReceiver != null)
          {
            paramPrintWriter.print(" errorReportReceiver=");
            paramPrintWriter.print(this.errorReportReceiver.flattenToShortString());
          }
          paramPrintWriter.println();
          if (this.whitelistManager)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("whitelistManager=");
            paramPrintWriter.println(this.whitelistManager);
          }
          if (this.activities.size() <= 0) {
            break label2166;
          }
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Activities:");
          i = 0;
          if (i >= this.activities.size()) {
            break label2166;
          }
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  - ");
          paramPrintWriter.println(this.activities.get(i));
          i += 1;
          continue;
          if (this.instrumentationArguments == null) {
            break;
          }
        }
      }
      if (this.anrDialog == null) {
        if (!this.bad) {}
      }
    }
    label2166:
    if (this.services.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Services:");
      i = 0;
      while (i < this.services.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println(this.services.valueAt(i));
        i += 1;
      }
    }
    if (this.executingServices.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Executing Services (fg=");
      paramPrintWriter.print(this.execServicesFg);
      paramPrintWriter.println(")");
      i = 0;
      while (i < this.executingServices.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println(this.executingServices.valueAt(i));
        i += 1;
      }
    }
    if (this.connections.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Connections:");
      i = 0;
      while (i < this.connections.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println(this.connections.valueAt(i));
        i += 1;
      }
    }
    if (this.pubProviders.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Published Providers:");
      i = 0;
      while (i < this.pubProviders.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println((String)this.pubProviders.keyAt(i));
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("    -> ");
        paramPrintWriter.println(this.pubProviders.valueAt(i));
        i += 1;
      }
    }
    if (this.conProviders.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Connected Providers:");
      i = 0;
      while (i < this.conProviders.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println(((ContentProviderConnection)this.conProviders.get(i)).toShortString());
        i += 1;
      }
    }
    if (this.curReceiver != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("curReceiver=");
      paramPrintWriter.println(this.curReceiver);
    }
    if (this.receivers.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Receivers:");
      i = 0;
      while (i < this.receivers.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        paramPrintWriter.println(this.receivers.valueAt(i));
        i += 1;
      }
    }
    if (this.hasTopUi)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("hasTopUi=");
      paramPrintWriter.print(this.hasTopUi);
    }
  }
  
  public void forceProcessStateUpTo(int paramInt)
  {
    if (this.repProcState > paramInt)
    {
      this.repProcState = paramInt;
      this.curProcState = paramInt;
    }
  }
  
  public String[] getPackageList()
  {
    int i = this.pkgList.size();
    if (i == 0) {
      return null;
    }
    String[] arrayOfString = new String[i];
    i = 0;
    while (i < this.pkgList.size())
    {
      arrayOfString[i] = ((String)this.pkgList.keyAt(i));
      i += 1;
    }
    return arrayOfString;
  }
  
  public int getSetAdjWithServices()
  {
    if ((this.setAdj >= 900) && (this.hasStartedServices)) {
      return 800;
    }
    return this.setAdj;
  }
  
  public boolean isInterestingToUserLocked()
  {
    int j = this.activities.size();
    int i = 0;
    while (i < j)
    {
      if (((ActivityRecord)this.activities.get(i)).isInterestingToUserLocked()) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  void kill(String paramString, boolean paramBoolean)
  {
    if (!this.killedByAm)
    {
      Trace.traceBegin(64L, "kill");
      if (paramBoolean) {
        Slog.i(TAG, "Killing " + toShortString() + " (adj " + this.setAdj + "): " + paramString);
      }
      EventLog.writeEvent(30023, new Object[] { Integer.valueOf(this.userId), Integer.valueOf(this.pid), this.processName, Integer.valueOf(this.setAdj), paramString });
      Process.killProcessQuiet(this.pid);
      ActivityManagerService.killProcessGroup(this.uid, this.pid);
      if (OnePlusAppBootManager.IN_USING) {
        OnePlusAppBootManager.getInstance(null).trackProcess(false, this, paramString);
      }
      if (!this.persistent)
      {
        this.killed = true;
        this.killedByAm = true;
      }
      Trace.traceEnd(64L);
    }
  }
  
  public void makeActive(IApplicationThread paramIApplicationThread, ProcessStatsService paramProcessStatsService)
  {
    int j = 1;
    Object localObject = new StringBuilder().append("app_uid=").append(this.uid).append(",app_pid=").append(this.pid).append(",oom_adj=").append(this.curAdj).append(",setAdj=").append(this.setAdj).append(",hasShownUi=");
    if (this.hasShownUi)
    {
      i = 1;
      localObject = ((StringBuilder)localObject).append(i).append(",cached=");
      if (!this.cached) {
        break label492;
      }
      i = 1;
      label102:
      localObject = ((StringBuilder)localObject).append(i).append(",fA=");
      if (!this.foregroundActivities) {
        break label497;
      }
      i = 1;
      label125:
      localObject = ((StringBuilder)localObject).append(i).append(",fS=");
      if (!this.foregroundServices) {
        break label502;
      }
      i = 1;
      label148:
      localObject = ((StringBuilder)localObject).append(i).append(",systemNoUi=");
      if (!this.systemNoUi) {
        break label507;
      }
      i = 1;
      label171:
      localObject = ((StringBuilder)localObject).append(i).append(",curSchedGroup=").append(this.curSchedGroup).append(",curProcState=").append(this.curProcState).append(",setProcState=").append(this.setProcState).append(",killed=");
      if (!this.killed) {
        break label512;
      }
      i = 1;
      label233:
      localObject = ((StringBuilder)localObject).append(i).append(",killedByAm=");
      if (!this.killedByAm) {
        break label517;
      }
      i = 1;
      label256:
      localObject = ((StringBuilder)localObject).append(i).append(",debugging=");
      if (!this.debugging) {
        break label522;
      }
    }
    label492:
    label497:
    label502:
    label507:
    label512:
    label517:
    label522:
    for (int i = j;; i = 0)
    {
      SeempLog.record_str(386, i);
      if (this.thread != null) {
        break label527;
      }
      localObject = this.baseProcessTracker;
      if (localObject != null)
      {
        ((ProcessState)localObject).setState(-1, paramProcessStatsService.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList);
        ((ProcessState)localObject).makeInactive();
      }
      this.baseProcessTracker = paramProcessStatsService.getProcessStateLocked(this.info.packageName, this.uid, this.info.versionCode, this.processName);
      this.baseProcessTracker.makeActive();
      i = 0;
      while (i < this.pkgList.size())
      {
        ProcessStats.ProcessStateHolder localProcessStateHolder = (ProcessStats.ProcessStateHolder)this.pkgList.valueAt(i);
        if ((localProcessStateHolder.state != null) && (localProcessStateHolder.state != localObject)) {
          localProcessStateHolder.state.makeInactive();
        }
        localProcessStateHolder.state = paramProcessStatsService.getProcessStateLocked((String)this.pkgList.keyAt(i), this.uid, this.info.versionCode, this.processName);
        if (localProcessStateHolder.state != this.baseProcessTracker) {
          localProcessStateHolder.state.makeActive();
        }
        i += 1;
      }
      i = 0;
      break;
      i = 0;
      break label102;
      i = 0;
      break label125;
      i = 0;
      break label148;
      i = 0;
      break label171;
      i = 0;
      break label233;
      i = 0;
      break label256;
    }
    label527:
    this.thread = paramIApplicationThread;
  }
  
  public String makeAdjReason()
  {
    if ((this.adjSource != null) || (this.adjTarget != null))
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append(' ');
      if ((this.adjTarget instanceof ComponentName))
      {
        localStringBuilder.append(((ComponentName)this.adjTarget).flattenToShortString());
        localStringBuilder.append("<=");
        if (!(this.adjSource instanceof ProcessRecord)) {
          break label144;
        }
        localStringBuilder.append("Proc{");
        localStringBuilder.append(((ProcessRecord)this.adjSource).toShortString());
        localStringBuilder.append("}");
      }
      for (;;)
      {
        return localStringBuilder.toString();
        if (this.adjTarget != null)
        {
          localStringBuilder.append(this.adjTarget.toString());
          break;
        }
        localStringBuilder.append("{null}");
        break;
        label144:
        if (this.adjSource != null) {
          localStringBuilder.append(this.adjSource.toString());
        } else {
          localStringBuilder.append("{null}");
        }
      }
    }
    return null;
  }
  
  public void makeInactive(ProcessStatsService paramProcessStatsService)
  {
    int j = 1;
    Object localObject = new StringBuilder().append("app_uid=").append(this.uid).append(",app_pid=").append(this.pid).append(",oom_adj=").append(this.curAdj).append(",setAdj=").append(this.setAdj).append(",hasShownUi=");
    if (this.hasShownUi)
    {
      i = 1;
      localObject = ((StringBuilder)localObject).append(i).append(",cached=");
      if (!this.cached) {
        break label407;
      }
      i = 1;
      label101:
      localObject = ((StringBuilder)localObject).append(i).append(",fA=");
      if (!this.foregroundActivities) {
        break label412;
      }
      i = 1;
      label124:
      localObject = ((StringBuilder)localObject).append(i).append(",fS=");
      if (!this.foregroundServices) {
        break label417;
      }
      i = 1;
      label147:
      localObject = ((StringBuilder)localObject).append(i).append(",systemNoUi=");
      if (!this.systemNoUi) {
        break label422;
      }
      i = 1;
      label170:
      localObject = ((StringBuilder)localObject).append(i).append(",curSchedGroup=").append(this.curSchedGroup).append(",curProcState=").append(this.curProcState).append(",setProcState=").append(this.setProcState).append(",killed=");
      if (!this.killed) {
        break label427;
      }
      i = 1;
      label232:
      localObject = ((StringBuilder)localObject).append(i).append(",killedByAm=");
      if (!this.killedByAm) {
        break label432;
      }
      i = 1;
      label255:
      localObject = ((StringBuilder)localObject).append(i).append(",debugging=");
      if (!this.debugging) {
        break label437;
      }
    }
    label407:
    label412:
    label417:
    label422:
    label427:
    label432:
    label437:
    for (int i = j;; i = 0)
    {
      SeempLog.record_str(387, i);
      this.thread = null;
      localObject = this.baseProcessTracker;
      if (localObject == null) {
        return;
      }
      if (localObject != null)
      {
        ((ProcessState)localObject).setState(-1, paramProcessStatsService.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList);
        ((ProcessState)localObject).makeInactive();
      }
      this.baseProcessTracker = null;
      i = 0;
      while (i < this.pkgList.size())
      {
        paramProcessStatsService = (ProcessStats.ProcessStateHolder)this.pkgList.valueAt(i);
        if ((paramProcessStatsService.state != null) && (paramProcessStatsService.state != localObject)) {
          paramProcessStatsService.state.makeInactive();
        }
        paramProcessStatsService.state = null;
        i += 1;
      }
      i = 0;
      break;
      i = 0;
      break label101;
      i = 0;
      break label124;
      i = 0;
      break label147;
      i = 0;
      break label170;
      i = 0;
      break label232;
      i = 0;
      break label255;
    }
  }
  
  int modifyRawOomAdj(int paramInt)
  {
    if ((!this.hasAboveClient) || (paramInt < 0)) {}
    do
    {
      return paramInt;
      if (paramInt < 100) {
        return 100;
      }
      if (paramInt < 200) {
        return 200;
      }
      if (paramInt < 900) {
        return 900;
      }
    } while (paramInt >= 906);
    return paramInt + 1;
  }
  
  public void permRequestDec()
  {
    this.permRequestCount -= 1;
    if (this.permRequestCount < 0)
    {
      Slog.w(TAG, "negative perRequestCount! reset to zero");
      this.permRequestCount = 0;
    }
    Slog.d(TAG, "permRequestCountDec: " + this.permRequestCount);
  }
  
  public void permRequestInc()
  {
    this.permRequestCount += 1;
    Slog.d(TAG, "permRequestCountInc: " + this.permRequestCount);
  }
  
  public void resetPackageList(ProcessStatsService paramProcessStatsService)
  {
    int j = this.pkgList.size();
    if (this.baseProcessTracker != null)
    {
      l = SystemClock.uptimeMillis();
      this.baseProcessTracker.setState(-1, paramProcessStatsService.getMemFactorLocked(), l, this.pkgList);
      if (j != 1)
      {
        i = 0;
        while (i < j)
        {
          localProcessStateHolder = (ProcessStats.ProcessStateHolder)this.pkgList.valueAt(i);
          if ((localProcessStateHolder.state != null) && (localProcessStateHolder.state != this.baseProcessTracker)) {
            localProcessStateHolder.state.makeInactive();
          }
          i += 1;
        }
        this.pkgList.clear();
        paramProcessStatsService = paramProcessStatsService.getProcessStateLocked(this.info.packageName, this.uid, this.info.versionCode, this.processName);
        localProcessStateHolder = new ProcessStats.ProcessStateHolder(this.info.versionCode);
        localProcessStateHolder.state = paramProcessStatsService;
        this.pkgList.put(this.info.packageName, localProcessStateHolder);
        if (paramProcessStatsService != this.baseProcessTracker) {
          paramProcessStatsService.makeActive();
        }
      }
    }
    while (j == 1)
    {
      long l;
      int i;
      ProcessStats.ProcessStateHolder localProcessStateHolder;
      return;
    }
    this.pkgList.clear();
    this.pkgList.put(this.info.packageName, new ProcessStats.ProcessStateHolder(this.info.versionCode));
  }
  
  void scheduleCrash(String paramString)
  {
    long l;
    if ((!this.killedByAm) && (this.thread != null))
    {
      if (this.pid == Process.myPid())
      {
        Slog.w(TAG, "scheduleCrash: trying to crash system process!");
        return;
      }
      l = Binder.clearCallingIdentity();
    }
    try
    {
      this.thread.scheduleCrash(paramString);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      kill("scheduleCrash for '" + paramString + "' failed", true);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void setPid(int paramInt)
  {
    this.pid = paramInt;
    this.procStatFile = null;
    this.shortStringName = null;
    this.stringName = null;
  }
  
  public void stopFreezingAllLocked()
  {
    int i = this.activities.size();
    while (i > 0)
    {
      i -= 1;
      ((ActivityRecord)this.activities.get(i)).stopFreezingScreenLocked(true);
    }
  }
  
  public String toShortString()
  {
    if (this.shortStringName != null) {
      return this.shortStringName;
    }
    Object localObject = new StringBuilder(128);
    toShortString((StringBuilder)localObject);
    localObject = ((StringBuilder)localObject).toString();
    this.shortStringName = ((String)localObject);
    return (String)localObject;
  }
  
  void toShortString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(this.pid);
    paramStringBuilder.append(':');
    paramStringBuilder.append(this.processName);
    paramStringBuilder.append('/');
    if (this.info.uid < 10000)
    {
      paramStringBuilder.append(this.uid);
      return;
    }
    paramStringBuilder.append('u');
    paramStringBuilder.append(this.userId);
    int i = UserHandle.getAppId(this.info.uid);
    if (i >= 10000)
    {
      paramStringBuilder.append('a');
      paramStringBuilder.append(i - 10000);
    }
    for (;;)
    {
      if (this.uid != this.info.uid)
      {
        paramStringBuilder.append('i');
        paramStringBuilder.append(UserHandle.getAppId(this.uid) - 99000);
      }
      if ((!OnePlusAppBootManager.DEBUG) || (this.info == null)) {
        break;
      }
      if ((this.info.flags & 0x81) == 0) {
        break label195;
      }
      paramStringBuilder.append("#sys-app");
      return;
      paramStringBuilder.append('s');
      paramStringBuilder.append(i);
    }
    label195:
    paramStringBuilder.append("#data-app");
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("ProcessRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(' ');
    toShortString((StringBuilder)localObject);
    ((StringBuilder)localObject).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
  
  public void unlinkDeathRecipient()
  {
    if ((this.deathRecipient != null) && (this.thread != null)) {
      this.thread.asBinder().unlinkToDeath(this.deathRecipient, 0);
    }
    this.deathRecipient = null;
  }
  
  void updateHasAboveClientLocked()
  {
    this.hasAboveClient = false;
    int i = this.connections.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        if ((((ConnectionRecord)this.connections.valueAt(i)).flags & 0x8) != 0) {
          this.hasAboveClient = true;
        }
      }
      else {
        return;
      }
      i -= 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ProcessRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */