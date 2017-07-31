package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.os.RemoteCallback.OnResultListener;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.FastPrintWriter;
import com.android.server.AppOpsService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ActiveServices
{
  static final int BG_START_TIMEOUT = 15000;
  private static boolean DEBUG_DELAYED_SERVICE = false;
  private static boolean DEBUG_DELAYED_STARTS = false;
  static final int LAST_ANR_LIFETIME_DURATION_MSECS = 7200000;
  private static boolean LOG_SERVICE_START_STOP = false;
  static final int MAX_SERVICE_INACTIVITY = 1800000;
  static final int SERVICE_BACKGROUND_TIMEOUT = 200000;
  static final int SERVICE_MIN_RESTART_TIME_BETWEEN = 10000;
  private static final boolean SERVICE_OOM_TRICK = SystemProperties.getBoolean("persist.sys.service.oomtrick", true);
  private static final boolean SERVICE_RESCHEDULE;
  static final int SERVICE_RESET_RUN_DURATION = 60000;
  static final int SERVICE_RESTART_DURATION = 1000;
  static final int SERVICE_RESTART_DURATION_FACTOR = 4;
  static final int SERVICE_TIMEOUT = 20000;
  private static final String TAG = "ActivityManager";
  private static final String TAG_MU = TAG + "_MU";
  private static final String TAG_SERVICE = TAG + ActivityManagerDebugConfig.POSTFIX_SERVICE;
  private static final String TAG_SERVICE_EXECUTING = TAG + ActivityManagerDebugConfig.POSTFIX_SERVICE_EXECUTING;
  static boolean isSupport = false;
  final ActivityManagerService mAm;
  final ArrayList<ServiceRecord> mDestroyingServices = new ArrayList();
  String mLastAnrDump;
  final Runnable mLastAnrDumpClearer = new Runnable()
  {
    public void run()
    {
      synchronized (ActiveServices.this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActiveServices.this.mLastAnrDump = null;
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
  };
  final int mMaxStartingBackground;
  private ArrayList<String> mOomTrickPackagesWhiteList = new ArrayList(Arrays.asList(new String[] { "com.tencent.mm", "com.tencent.mobileqq" }));
  final ArrayList<ServiceRecord> mPendingServices = new ArrayList();
  final ArrayList<ServiceRecord> mRestartingServices = new ArrayList();
  final ArrayMap<IBinder, ArrayList<ConnectionRecord>> mServiceConnections = new ArrayMap();
  final SparseArray<ServiceMap> mServiceMap = new SparseArray();
  private ArrayList<ServiceRecord> mTmpCollectionResults = null;
  
  static
  {
    DEBUG_DELAYED_SERVICE = ActivityManagerDebugConfig.DEBUG_SERVICE;
    DEBUG_DELAYED_STARTS = DEBUG_DELAYED_SERVICE;
    LOG_SERVICE_START_STOP = false;
    SERVICE_RESCHEDULE = SystemProperties.getBoolean("ro.am.reschedule_service", false);
  }
  
  public ActiveServices(ActivityManagerService paramActivityManagerService)
  {
    this.mAm = paramActivityManagerService;
    int i = 0;
    try
    {
      int j = Integer.parseInt(SystemProperties.get("ro.config.max_starting_bg", "0"));
      i = j;
    }
    catch (RuntimeException paramActivityManagerService)
    {
      for (;;) {}
    }
    if (i > 0) {}
    for (;;)
    {
      this.mMaxStartingBackground = i;
      return;
      if (ActivityManager.isLowRamDeviceStatic()) {
        i = 1;
      } else {
        i = 8;
      }
    }
  }
  
  private final void bringDownServiceIfNeededLocked(ServiceRecord paramServiceRecord, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (isServiceNeeded(paramServiceRecord, paramBoolean1, paramBoolean2)) {
      return;
    }
    if (this.mPendingServices.contains(paramServiceRecord)) {
      return;
    }
    bringDownServiceLocked(paramServiceRecord);
  }
  
  private final void bringDownServiceLocked(ServiceRecord paramServiceRecord)
  {
    int i = paramServiceRecord.connections.size() - 1;
    Object localObject;
    int j;
    while (i >= 0)
    {
      localObject = (ArrayList)paramServiceRecord.connections.valueAt(i);
      j = 0;
      for (;;)
      {
        if (j < ((ArrayList)localObject).size())
        {
          ConnectionRecord localConnectionRecord = (ConnectionRecord)((ArrayList)localObject).get(j);
          localConnectionRecord.serviceDead = true;
          try
          {
            localConnectionRecord.conn.connected(paramServiceRecord.name, null);
            j += 1;
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Slog.w(TAG, "Failure disconnecting service " + paramServiceRecord.name + " to connection " + ((ConnectionRecord)((ArrayList)localObject).get(j)).conn.asBinder() + " (in " + ((ConnectionRecord)((ArrayList)localObject).get(j)).binding.client.processName + ")", localException2);
            }
          }
        }
      }
      i -= 1;
    }
    if ((paramServiceRecord.app != null) && (paramServiceRecord.app.thread != null))
    {
      i = paramServiceRecord.bindings.size() - 1;
      for (;;)
      {
        if (i >= 0)
        {
          localObject = (IntentBindRecord)paramServiceRecord.bindings.valueAt(i);
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "Bringing down binding " + localObject + ": hasBound=" + ((IntentBindRecord)localObject).hasBound);
          }
          if (((IntentBindRecord)localObject).hasBound) {}
          try
          {
            bumpServiceExecutingLocked(paramServiceRecord, false, "bring down unbind");
            this.mAm.updateOomAdjLocked(paramServiceRecord.app);
            ((IntentBindRecord)localObject).hasBound = false;
            paramServiceRecord.app.thread.scheduleUnbindService(paramServiceRecord, ((IntentBindRecord)localObject).intent.getIntent());
            i -= 1;
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              Slog.w(TAG, "Exception when unbinding service " + paramServiceRecord.shortName, localException1);
              serviceProcessGoneLocked(paramServiceRecord);
            }
          }
        }
      }
    }
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "Bringing down " + paramServiceRecord + " " + paramServiceRecord.intent);
    }
    paramServiceRecord.destroyTime = SystemClock.uptimeMillis();
    int k;
    if (LOG_SERVICE_START_STOP)
    {
      j = paramServiceRecord.userId;
      k = System.identityHashCode(paramServiceRecord);
      if (paramServiceRecord.app == null) {
        break label593;
      }
    }
    ServiceMap localServiceMap;
    label593:
    for (i = paramServiceRecord.app.pid;; i = -1)
    {
      EventLogTags.writeAmDestroyService(j, k, i);
      localServiceMap = getServiceMap(paramServiceRecord.userId);
      localServiceMap.mServicesByName.remove(paramServiceRecord.name);
      localServiceMap.mServicesByIntent.remove(paramServiceRecord.intent);
      paramServiceRecord.totalRestartCount = 0;
      unscheduleServiceRestartLocked(paramServiceRecord, 0, true);
      i = this.mPendingServices.size() - 1;
      while (i >= 0)
      {
        if (this.mPendingServices.get(i) == paramServiceRecord)
        {
          this.mPendingServices.remove(i);
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "Removed pending: " + paramServiceRecord);
          }
        }
        i -= 1;
      }
    }
    cancelForegroudNotificationLocked(paramServiceRecord);
    paramServiceRecord.isForeground = false;
    paramServiceRecord.foregroundId = 0;
    paramServiceRecord.foregroundNoti = null;
    paramServiceRecord.clearDeliveredStartsLocked();
    paramServiceRecord.pendingStarts.clear();
    if (paramServiceRecord.app != null) {
      synchronized (paramServiceRecord.stats.getBatteryStats())
      {
        paramServiceRecord.stats.stopLaunchedLocked();
        paramServiceRecord.app.services.remove(paramServiceRecord);
        if (paramServiceRecord.whitelistManager) {
          updateWhitelistManagerLocked(paramServiceRecord.app);
        }
        if (paramServiceRecord.app.thread != null) {
          updateServiceForegroundLocked(paramServiceRecord.app, false);
        }
      }
    }
    for (;;)
    {
      try
      {
        bumpServiceExecutingLocked(paramServiceRecord, false, "destroy");
        this.mDestroyingServices.add(paramServiceRecord);
        paramServiceRecord.destroying = true;
        this.mAm.updateOomAdjLocked(paramServiceRecord.app);
        paramServiceRecord.app.thread.scheduleStopService(paramServiceRecord);
        if (paramServiceRecord.bindings.size() > 0) {
          paramServiceRecord.bindings.clear();
        }
        if ((paramServiceRecord.restarter instanceof ServiceRestarter)) {
          ((ServiceRestarter)paramServiceRecord.restarter).setService(null);
        }
        i = this.mAm.mProcessStats.getMemFactorLocked();
        long l = SystemClock.uptimeMillis();
        if (paramServiceRecord.tracker != null)
        {
          paramServiceRecord.tracker.setStarted(false, i, l);
          paramServiceRecord.tracker.setBound(false, i, l);
          if (paramServiceRecord.executeNesting == 0)
          {
            paramServiceRecord.tracker.clearCurrentOwner(paramServiceRecord, false);
            paramServiceRecord.tracker = null;
          }
        }
        localServiceMap.ensureNotStartingBackground(paramServiceRecord);
        return;
        paramServiceRecord = finally;
        throw paramServiceRecord;
      }
      catch (Exception localException3)
      {
        Slog.w(TAG, "Exception when destroying service " + paramServiceRecord.shortName, localException3);
        serviceProcessGoneLocked(paramServiceRecord);
        continue;
      }
      if (ActivityManagerDebugConfig.DEBUG_SERVICE)
      {
        Slog.v(TAG_SERVICE, "Removed service that has no process: " + paramServiceRecord);
        continue;
        if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
          Slog.v(TAG_SERVICE, "Removed service that is not running: " + paramServiceRecord);
        }
      }
    }
  }
  
  private String bringUpServiceLocked(ServiceRecord paramServiceRecord, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws TransactionTooLargeException
  {
    if ((paramServiceRecord.app != null) && (paramServiceRecord.app.thread != null))
    {
      sendServiceArgsLocked(paramServiceRecord, paramBoolean1, false);
      return null;
    }
    if ((!paramBoolean2) && (paramServiceRecord.restartDelay > 0L)) {
      return null;
    }
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "Bringing up " + paramServiceRecord + " " + paramServiceRecord.intent);
    }
    if (this.mRestartingServices.remove(paramServiceRecord))
    {
      paramServiceRecord.resetRestartCounter();
      clearRestartingIfNeededLocked(paramServiceRecord);
    }
    if (paramServiceRecord.delayed)
    {
      if (DEBUG_DELAYED_STARTS) {
        Slog.v(TAG_SERVICE, "REM FR DELAY LIST (bring up): " + paramServiceRecord);
      }
      getServiceMap(paramServiceRecord.userId).mDelayedStartList.remove(paramServiceRecord);
      paramServiceRecord.delayed = false;
    }
    Object localObject1;
    if (!this.mAm.mUserController.hasStartedUserState(paramServiceRecord.userId))
    {
      localObject1 = "Unable to launch app " + paramServiceRecord.appInfo.packageName + "/" + paramServiceRecord.appInfo.uid + " for service " + paramServiceRecord.intent.getIntent() + ": user " + paramServiceRecord.userId + " is stopped";
      Slog.w(TAG, (String)localObject1);
      bringDownServiceLocked(paramServiceRecord);
      return (String)localObject1;
    }
    try
    {
      AppGlobals.getPackageManager().setPackageStoppedState(paramServiceRecord.packageName, false, paramServiceRecord.userId);
      if ((paramServiceRecord.serviceInfo.flags & 0x2) != 0)
      {
        paramBoolean2 = true;
        str = paramServiceRecord.processName;
        if (paramBoolean2) {
          break label642;
        }
        localProcessRecord = this.mAm.getProcessRecordLocked(str, paramServiceRecord.appInfo.uid, false);
        if (ActivityManagerDebugConfig.DEBUG_MU) {
          Slog.v(TAG_MU, "bringUpServiceLocked: appInfo.uid=" + paramServiceRecord.appInfo.uid + " app=" + localProcessRecord);
        }
        localObject1 = localProcessRecord;
        if (localProcessRecord == null) {
          break label542;
        }
        localObject1 = localProcessRecord;
        if (localProcessRecord.thread == null) {
          break label542;
        }
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      String str;
      for (;;)
      {
        try
        {
          localProcessRecord.addPackage(paramServiceRecord.appInfo.packageName, paramServiceRecord.appInfo.versionCode, this.mAm.mProcessStats);
          realStartServiceLocked(paramServiceRecord, localProcessRecord, paramBoolean1);
          return null;
        }
        catch (RemoteException localRemoteException1)
        {
          ProcessRecord localProcessRecord;
          Slog.w(TAG, "Exception when starting service " + paramServiceRecord.shortName, localRemoteException1);
          localObject2 = localProcessRecord;
          if (((localObject2 == null) || (((ProcessRecord)localObject2).pid == 0)) && (!paramBoolean3)) {
            break label651;
          }
          if (this.mPendingServices.contains(paramServiceRecord)) {
            continue;
          }
          this.mPendingServices.add(paramServiceRecord);
          if (!paramServiceRecord.delayedStop) {
            continue;
          }
          paramServiceRecord.delayedStop = false;
          if (!paramServiceRecord.startRequested) {
            continue;
          }
          if (!DEBUG_DELAYED_STARTS) {
            continue;
          }
          Slog.v(TAG_SERVICE, "Applying delayed stop (in bring up): " + paramServiceRecord);
          stopServiceLocked(paramServiceRecord);
          return null;
        }
        catch (TransactionTooLargeException paramServiceRecord)
        {
          throw paramServiceRecord;
        }
        localIllegalArgumentException = localIllegalArgumentException;
        Slog.w(TAG, "Failed trying to unstop package " + paramServiceRecord.packageName + ": " + localIllegalArgumentException);
        continue;
        paramBoolean2 = false;
      }
      for (;;)
      {
        Object localObject2 = paramServiceRecord.isolatedProc;
        continue;
        localObject2 = this.mAm.startProcessLocked(str, paramServiceRecord.appInfo, true, paramInt, "service for " + paramServiceRecord.intent.getIntent().getAction() + " to", paramServiceRecord.name, false, paramBoolean2, false);
        if (localObject2 == null)
        {
          localObject2 = "Unable to launch app " + paramServiceRecord.appInfo.packageName + "/" + paramServiceRecord.appInfo.uid + " for service " + paramServiceRecord.intent.getIntent() + ": process is bad";
          Slog.w(TAG, (String)localObject2);
          bringDownServiceLocked(paramServiceRecord);
          return (String)localObject2;
        }
        if (paramBoolean2) {
          paramServiceRecord.isolatedProc = ((ProcessRecord)localObject2);
        }
      }
    }
    catch (RemoteException localRemoteException2)
    {
      label542:
      label642:
      label651:
      for (;;) {}
    }
  }
  
  private final void bumpServiceExecutingLocked(ServiceRecord paramServiceRecord, boolean paramBoolean, String paramString)
  {
    long l;
    if (ActivityManagerDebugConfig.DEBUG_SERVICE)
    {
      Slog.v(TAG_SERVICE, ">>> EXECUTING " + paramString + " of " + paramServiceRecord + " in app " + paramServiceRecord.app);
      l = SystemClock.uptimeMillis();
      if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
        Slog.v(TAG, "bumpServiceExecutingLocked r.executeNesting " + paramServiceRecord.executeNesting);
      }
      if (paramServiceRecord.executeNesting != 0) {
        break label342;
      }
      paramServiceRecord.executeFg = paramBoolean;
      paramString = paramServiceRecord.getTracker();
      if (paramString != null) {
        paramString.setExecuting(true, this.mAm.mProcessStats.getMemFactorLocked(), l);
      }
      if (paramServiceRecord.app != null)
      {
        OnePlusProcessManager.resumeProcessByUID_out(paramServiceRecord.app.uid, "bumpServiceExecutingLocked = " + paramServiceRecord.shortName);
        paramServiceRecord.app.executingServices.add(paramServiceRecord);
        paramString = paramServiceRecord.app;
        paramString.execServicesFg |= paramBoolean;
        if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
          Slog.v(TAG, "bumpServiceExecutingLocked r.app.executingServices.size() " + paramServiceRecord.app.executingServices.size());
        }
        if (paramServiceRecord.app.executingServices.size() == 1) {
          scheduleServiceTimeoutLocked(paramServiceRecord.app);
        }
      }
    }
    for (;;)
    {
      paramServiceRecord.executeFg |= paramBoolean;
      paramServiceRecord.executeNesting += 1;
      paramServiceRecord.executingStart = l;
      return;
      if (!ActivityManagerDebugConfig.DEBUG_SERVICE_EXECUTING) {
        break;
      }
      Slog.v(TAG_SERVICE_EXECUTING, ">>> EXECUTING " + paramString + " of " + paramServiceRecord.shortName);
      break;
      label342:
      if ((paramServiceRecord.app != null) && (paramBoolean) && (!paramServiceRecord.app.execServicesFg))
      {
        paramServiceRecord.app.execServicesFg = true;
        scheduleServiceTimeoutLocked(paramServiceRecord.app);
      }
    }
  }
  
  private void cancelForegroudNotificationLocked(ServiceRecord paramServiceRecord)
  {
    if (paramServiceRecord.foregroundId != 0)
    {
      ServiceMap localServiceMap = getServiceMap(paramServiceRecord.userId);
      if (localServiceMap != null)
      {
        int i = localServiceMap.mServicesByName.size() - 1;
        while (i >= 0)
        {
          ServiceRecord localServiceRecord = (ServiceRecord)localServiceMap.mServicesByName.valueAt(i);
          if ((localServiceRecord != paramServiceRecord) && (localServiceRecord.foregroundId == paramServiceRecord.foregroundId) && (localServiceRecord.packageName.equals(paramServiceRecord.packageName))) {
            return;
          }
          i -= 1;
        }
      }
      paramServiceRecord.cancelNotification();
    }
  }
  
  private void clearRestartingIfNeededLocked(ServiceRecord paramServiceRecord)
  {
    int k;
    int i;
    if (paramServiceRecord.restartTracker != null)
    {
      k = 0;
      i = this.mRestartingServices.size() - 1;
    }
    for (;;)
    {
      int j = k;
      if (i >= 0)
      {
        if (((ServiceRecord)this.mRestartingServices.get(i)).restartTracker == paramServiceRecord.restartTracker) {
          j = 1;
        }
      }
      else
      {
        if (j == 0)
        {
          paramServiceRecord.restartTracker.setRestarting(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
          paramServiceRecord.restartTracker = null;
        }
        return;
      }
      i -= 1;
    }
  }
  
  private boolean collectPackageServicesLocked(String paramString, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ArrayMap<ComponentName, ServiceRecord> paramArrayMap)
  {
    boolean bool2 = false;
    int i = paramArrayMap.size() - 1;
    if (i >= 0)
    {
      ServiceRecord localServiceRecord = (ServiceRecord)paramArrayMap.valueAt(i);
      boolean bool1;
      label65:
      boolean bool3;
      if (paramString != null)
      {
        if (!localServiceRecord.packageName.equals(paramString)) {
          break label126;
        }
        if (paramSet == null) {
          break label120;
        }
        bool1 = paramSet.contains(localServiceRecord.name.getClassName());
        bool3 = bool2;
        if (bool1)
        {
          if ((localServiceRecord.app == null) || (paramBoolean1) || (!localServiceRecord.app.persistent)) {
            break label132;
          }
          bool3 = bool2;
        }
      }
      for (;;)
      {
        i -= 1;
        bool2 = bool3;
        break;
        bool1 = true;
        break label65;
        label120:
        bool1 = true;
        break label65;
        label126:
        bool1 = false;
        break label65;
        label132:
        if (!paramBoolean2) {
          return true;
        }
        bool3 = true;
        Slog.i(TAG, "  Force stopping service " + localServiceRecord);
        if (localServiceRecord.app != null)
        {
          localServiceRecord.app.removed = paramBoolean3;
          if (!localServiceRecord.app.persistent)
          {
            localServiceRecord.app.services.remove(localServiceRecord);
            if (localServiceRecord.whitelistManager) {
              updateWhitelistManagerLocked(localServiceRecord.app);
            }
          }
        }
        localServiceRecord.app = null;
        localServiceRecord.isolatedProc = null;
        if (this.mTmpCollectionResults == null) {
          this.mTmpCollectionResults = new ArrayList();
        }
        this.mTmpCollectionResults.add(localServiceRecord);
      }
    }
    return bool2;
  }
  
  private void dumpService(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, ServiceRecord paramServiceRecord, String[] paramArrayOfString, boolean paramBoolean)
  {
    String str = paramString + "  ";
    synchronized (this.mAm)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("SERVICE ");
      paramPrintWriter.print(paramServiceRecord.shortName);
      paramPrintWriter.print(" ");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(paramServiceRecord)));
      paramPrintWriter.print(" pid=");
      if (paramServiceRecord.app != null)
      {
        paramPrintWriter.println(paramServiceRecord.app.pid);
        if (paramBoolean) {
          paramServiceRecord.dump(paramPrintWriter, str);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if ((paramServiceRecord.app != null) && (paramServiceRecord.app.thread != null))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("  Client:");
          paramPrintWriter.flush();
        }
      }
    }
  }
  
  private final ServiceRecord findServiceLocked(ComponentName paramComponentName, IBinder paramIBinder, int paramInt)
  {
    paramComponentName = getServiceByName(paramComponentName, paramInt);
    if (paramComponentName == paramIBinder) {
      return paramComponentName;
    }
    return null;
  }
  
  private void foo() {}
  
  private ServiceMap getServiceMap(int paramInt)
  {
    ServiceMap localServiceMap2 = (ServiceMap)this.mServiceMap.get(paramInt);
    ServiceMap localServiceMap1 = localServiceMap2;
    if (localServiceMap2 == null)
    {
      localServiceMap1 = new ServiceMap(this.mAm.mHandler.getLooper(), paramInt);
      this.mServiceMap.put(paramInt, localServiceMap1);
    }
    return localServiceMap1;
  }
  
  private boolean isCoreApp(ProcessRecord paramProcessRecord)
  {
    if (!isSupport) {
      return true;
    }
    if ((paramProcessRecord.info.flags & 0x1) != 0) {
      return true;
    }
    if (paramProcessRecord.curAdj <= 200) {
      return true;
    }
    return OnePlusProcessManager.checkProcessCanRestart(paramProcessRecord);
  }
  
  private final boolean isServiceNeeded(ServiceRecord paramServiceRecord, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramServiceRecord.startRequested) {
      return true;
    }
    if (!paramBoolean1) {
      paramBoolean2 = paramServiceRecord.hasAutoCreateConnections();
    }
    return paramBoolean2;
  }
  
  /* Error */
  private final void realStartServiceLocked(ServiceRecord paramServiceRecord, ProcessRecord paramProcessRecord, boolean paramBoolean)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 318	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   4: ifnonnull +11 -> 15
    //   7: new 542	android/os/RemoteException
    //   10: dup
    //   11: invokespecial 883	android/os/RemoteException:<init>	()V
    //   14: athrow
    //   15: getstatic 634	com/android/server/am/ActivityManagerDebugConfig:DEBUG_MU	Z
    //   18: ifeq +49 -> 67
    //   21: getstatic 123	com/android/server/am/ActiveServices:TAG_MU	Ljava/lang/String;
    //   24: new 109	java/lang/StringBuilder
    //   27: dup
    //   28: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   31: ldc_w 885
    //   34: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_1
    //   38: getfield 583	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
    //   41: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   44: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   47: ldc_w 887
    //   50: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: getfield 704	com/android/server/am/ProcessRecord:uid	I
    //   57: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   60: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   66: pop
    //   67: aload_1
    //   68: aload_2
    //   69: putfield 314	com/android/server/am/ServiceRecord:app	Lcom/android/server/am/ProcessRecord;
    //   72: invokestatic 384	android/os/SystemClock:uptimeMillis	()J
    //   75: lstore 7
    //   77: aload_1
    //   78: lload 7
    //   80: putfield 890	com/android/server/am/ServiceRecord:lastActivity	J
    //   83: aload_1
    //   84: lload 7
    //   86: putfield 893	com/android/server/am/ServiceRecord:restartTime	J
    //   89: aload_2
    //   90: getfield 466	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   93: aload_1
    //   94: invokevirtual 716	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   97: istore 5
    //   99: aload_0
    //   100: aload_1
    //   101: iload_3
    //   102: ldc_w 895
    //   105: invokespecial 343	com/android/server/am/ActiveServices:bumpServiceExecutingLocked	(Lcom/android/server/am/ServiceRecord;ZLjava/lang/String;)V
    //   108: aload_0
    //   109: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   112: aload_2
    //   113: iconst_0
    //   114: aconst_null
    //   115: invokevirtual 899	com/android/server/am/ActivityManagerService:updateLruProcessLocked	(Lcom/android/server/am/ProcessRecord;ZLcom/android/server/am/ProcessRecord;)V
    //   118: aload_0
    //   119: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   122: invokevirtual 901	com/android/server/am/ActivityManagerService:updateOomAdjLocked	()V
    //   125: getstatic 138	com/android/server/am/ActiveServices:LOG_SERVICE_START_STOP	Z
    //   128: ifeq +57 -> 185
    //   131: aload_1
    //   132: getfield 370	com/android/server/am/ServiceRecord:shortName	Ljava/lang/String;
    //   135: bipush 46
    //   137: invokevirtual 905	java/lang/String:lastIndexOf	(I)I
    //   140: istore 4
    //   142: iload 4
    //   144: iflt +467 -> 611
    //   147: aload_1
    //   148: getfield 370	com/android/server/am/ServiceRecord:shortName	Ljava/lang/String;
    //   151: iload 4
    //   153: invokevirtual 908	java/lang/String:substring	(I)Ljava/lang/String;
    //   156: astore 9
    //   158: aload_1
    //   159: getfield 391	com/android/server/am/ServiceRecord:userId	I
    //   162: aload_1
    //   163: invokestatic 397	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   166: aload 9
    //   168: aload_1
    //   169: getfield 314	com/android/server/am/ServiceRecord:app	Lcom/android/server/am/ProcessRecord;
    //   172: getfield 704	com/android/server/am/ProcessRecord:uid	I
    //   175: aload_1
    //   176: getfield 314	com/android/server/am/ServiceRecord:app	Lcom/android/server/am/ProcessRecord;
    //   179: getfield 400	com/android/server/am/ProcessRecord:pid	I
    //   182: invokestatic 912	com/android/server/am/EventLogTags:writeAmCreateService	(IILjava/lang/String;II)V
    //   185: aload_1
    //   186: getfield 453	com/android/server/am/ServiceRecord:stats	Lcom/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv;
    //   189: invokevirtual 459	com/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv:getBatteryStats	()Lcom/android/internal/os/BatteryStatsImpl;
    //   192: astore 9
    //   194: aload 9
    //   196: monitorenter
    //   197: aload_1
    //   198: getfield 453	com/android/server/am/ServiceRecord:stats	Lcom/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv;
    //   201: invokevirtual 915	com/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv:startLaunchedLocked	()V
    //   204: aload 9
    //   206: monitorexit
    //   207: aload_0
    //   208: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   211: aload_1
    //   212: getfield 621	com/android/server/am/ServiceRecord:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   215: getfield 916	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   218: iconst_1
    //   219: invokevirtual 920	com/android/server/am/ActivityManagerService:notifyPackageUse	(Ljava/lang/String;I)V
    //   222: aload_2
    //   223: bipush 10
    //   225: invokevirtual 923	com/android/server/am/ProcessRecord:forceProcessStateUpTo	(I)V
    //   228: getstatic 136	com/android/server/am/ActivityManagerDebugConfig:DEBUG_SERVICE	Z
    //   231: ifeq +30 -> 261
    //   234: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   237: new 109	java/lang/StringBuilder
    //   240: dup
    //   241: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   244: ldc_w 925
    //   247: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: aload_1
    //   251: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   254: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   257: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   260: pop
    //   261: aload_2
    //   262: getfield 318	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   265: aload_1
    //   266: aload_1
    //   267: getfield 621	com/android/server/am/ServiceRecord:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   270: aload_0
    //   271: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   274: aload_1
    //   275: getfield 621	com/android/server/am/ServiceRecord:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   278: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   281: invokevirtual 932	com/android/server/am/ActivityManagerService:compatibilityInfoForPackageLocked	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/CompatibilityInfo;
    //   284: aload_2
    //   285: getfield 935	com/android/server/am/ProcessRecord:repProcState	I
    //   288: invokeinterface 939 5 0
    //   293: aload_1
    //   294: invokevirtual 942	com/android/server/am/ServiceRecord:postNotification	()V
    //   297: iconst_1
    //   298: ifne +114 -> 412
    //   301: aload_0
    //   302: getfield 194	com/android/server/am/ActiveServices:mDestroyingServices	Ljava/util/ArrayList;
    //   305: aload_1
    //   306: invokevirtual 234	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   309: istore 6
    //   311: aload_0
    //   312: aload_1
    //   313: iload 6
    //   315: iload 6
    //   317: invokespecial 945	com/android/server/am/ActiveServices:serviceDoneExecutingLocked	(Lcom/android/server/am/ServiceRecord;ZZ)V
    //   320: iload 5
    //   322: ifeq +78 -> 400
    //   325: aload_2
    //   326: getfield 466	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   329: aload_1
    //   330: invokevirtual 470	android/util/ArraySet:remove	(Ljava/lang/Object;)Z
    //   333: pop
    //   334: aload_1
    //   335: aconst_null
    //   336: putfield 314	com/android/server/am/ServiceRecord:app	Lcom/android/server/am/ProcessRecord;
    //   339: getstatic 148	com/android/server/am/ActiveServices:SERVICE_RESCHEDULE	Z
    //   342: ifeq +58 -> 400
    //   345: getstatic 80	com/android/server/am/ActiveServices:DEBUG_DELAYED_SERVICE	Z
    //   348: ifeq +52 -> 400
    //   351: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   354: new 109	java/lang/StringBuilder
    //   357: dup
    //   358: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   361: ldc_w 947
    //   364: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   367: aload_1
    //   368: getfield 370	com/android/server/am/ServiceRecord:shortName	Ljava/lang/String;
    //   371: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   374: ldc_w 949
    //   377: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   380: aload_1
    //   381: getfield 550	com/android/server/am/ServiceRecord:restartDelay	J
    //   384: invokevirtual 952	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   387: ldc_w 954
    //   390: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   393: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   396: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   399: pop
    //   400: iload 6
    //   402: ifne +10 -> 412
    //   405: aload_0
    //   406: aload_1
    //   407: iconst_0
    //   408: invokespecial 958	com/android/server/am/ActiveServices:scheduleServiceRestartLocked	(Lcom/android/server/am/ServiceRecord;Z)Z
    //   411: pop
    //   412: aload_1
    //   413: getfield 473	com/android/server/am/ServiceRecord:whitelistManager	Z
    //   416: ifeq +8 -> 424
    //   419: aload_2
    //   420: iconst_1
    //   421: putfield 959	com/android/server/am/ProcessRecord:whitelistManager	Z
    //   424: aload_0
    //   425: aload_1
    //   426: iload_3
    //   427: invokespecial 963	com/android/server/am/ActiveServices:requestServiceBindingsLocked	(Lcom/android/server/am/ServiceRecord;Z)V
    //   430: aload_0
    //   431: aload_2
    //   432: aconst_null
    //   433: iconst_1
    //   434: invokespecial 967	com/android/server/am/ActiveServices:updateServiceClientActivitiesLocked	(Lcom/android/server/am/ProcessRecord;Lcom/android/server/am/ConnectionRecord;Z)Z
    //   437: pop
    //   438: aload_1
    //   439: getfield 661	com/android/server/am/ServiceRecord:startRequested	Z
    //   442: ifeq +43 -> 485
    //   445: aload_1
    //   446: getfield 970	com/android/server/am/ServiceRecord:callStart	Z
    //   449: ifeq +36 -> 485
    //   452: aload_1
    //   453: getfield 446	com/android/server/am/ServiceRecord:pendingStarts	Ljava/util/ArrayList;
    //   456: invokevirtual 254	java/util/ArrayList:size	()I
    //   459: ifne +26 -> 485
    //   462: aload_1
    //   463: getfield 446	com/android/server/am/ServiceRecord:pendingStarts	Ljava/util/ArrayList;
    //   466: new 972	com/android/server/am/ServiceRecord$StartItem
    //   469: dup
    //   470: aload_1
    //   471: iconst_0
    //   472: aload_1
    //   473: invokevirtual 975	com/android/server/am/ServiceRecord:makeNextStartId	()I
    //   476: aconst_null
    //   477: aconst_null
    //   478: invokespecial 978	com/android/server/am/ServiceRecord$StartItem:<init>	(Lcom/android/server/am/ServiceRecord;ZILandroid/content/Intent;Lcom/android/server/am/ActivityManagerService$NeededUriGrants;)V
    //   481: invokevirtual 486	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   484: pop
    //   485: aload_0
    //   486: aload_1
    //   487: iload_3
    //   488: iconst_1
    //   489: invokespecial 547	com/android/server/am/ActiveServices:sendServiceArgsLocked	(Lcom/android/server/am/ServiceRecord;ZZ)V
    //   492: aload_1
    //   493: getfield 562	com/android/server/am/ServiceRecord:delayed	Z
    //   496: ifeq +57 -> 553
    //   499: getstatic 84	com/android/server/am/ActiveServices:DEBUG_DELAYED_STARTS	Z
    //   502: ifeq +30 -> 532
    //   505: getstatic 91	com/android/server/am/ActiveServices:TAG_SERVICE	Ljava/lang/String;
    //   508: new 109	java/lang/StringBuilder
    //   511: dup
    //   512: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   515: ldc_w 980
    //   518: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   521: aload_1
    //   522: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   525: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   528: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   531: pop
    //   532: aload_0
    //   533: aload_1
    //   534: getfield 391	com/android/server/am/ServiceRecord:userId	I
    //   537: invokespecial 97	com/android/server/am/ActiveServices:getServiceMap	(I)Lcom/android/server/am/ActiveServices$ServiceMap;
    //   540: getfield 567	com/android/server/am/ActiveServices$ServiceMap:mDelayedStartList	Ljava/util/ArrayList;
    //   543: aload_1
    //   544: invokevirtual 553	java/util/ArrayList:remove	(Ljava/lang/Object;)Z
    //   547: pop
    //   548: aload_1
    //   549: iconst_0
    //   550: putfield 562	com/android/server/am/ServiceRecord:delayed	Z
    //   553: aload_1
    //   554: getfield 658	com/android/server/am/ServiceRecord:delayedStop	Z
    //   557: ifeq +53 -> 610
    //   560: aload_1
    //   561: iconst_0
    //   562: putfield 658	com/android/server/am/ServiceRecord:delayedStop	Z
    //   565: aload_1
    //   566: getfield 661	com/android/server/am/ServiceRecord:startRequested	Z
    //   569: ifeq +41 -> 610
    //   572: getstatic 84	com/android/server/am/ActiveServices:DEBUG_DELAYED_STARTS	Z
    //   575: ifeq +30 -> 605
    //   578: getstatic 91	com/android/server/am/ActiveServices:TAG_SERVICE	Ljava/lang/String;
    //   581: new 109	java/lang/StringBuilder
    //   584: dup
    //   585: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   588: ldc_w 982
    //   591: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   594: aload_1
    //   595: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   598: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   601: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   604: pop
    //   605: aload_0
    //   606: aload_1
    //   607: invokespecial 666	com/android/server/am/ActiveServices:stopServiceLocked	(Lcom/android/server/am/ServiceRecord;)V
    //   610: return
    //   611: aload_1
    //   612: getfield 370	com/android/server/am/ServiceRecord:shortName	Ljava/lang/String;
    //   615: astore 9
    //   617: goto -459 -> 158
    //   620: astore 10
    //   622: aload 9
    //   624: monitorexit
    //   625: aload 10
    //   627: athrow
    //   628: astore 9
    //   630: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   633: new 109	java/lang/StringBuilder
    //   636: dup
    //   637: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   640: ldc_w 984
    //   643: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   646: aload_1
    //   647: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   650: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   653: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   656: pop
    //   657: aload_0
    //   658: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   661: aload_2
    //   662: invokevirtual 987	com/android/server/am/ActivityManagerService:appDiedLocked	(Lcom/android/server/am/ProcessRecord;)V
    //   665: aload 9
    //   667: athrow
    //   668: astore 9
    //   670: iconst_0
    //   671: ifne +110 -> 781
    //   674: aload_0
    //   675: getfield 194	com/android/server/am/ActiveServices:mDestroyingServices	Ljava/util/ArrayList;
    //   678: aload_1
    //   679: invokevirtual 234	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   682: istore_3
    //   683: aload_0
    //   684: aload_1
    //   685: iload_3
    //   686: iload_3
    //   687: invokespecial 945	com/android/server/am/ActiveServices:serviceDoneExecutingLocked	(Lcom/android/server/am/ServiceRecord;ZZ)V
    //   690: iload 5
    //   692: ifeq +78 -> 770
    //   695: aload_2
    //   696: getfield 466	com/android/server/am/ProcessRecord:services	Landroid/util/ArraySet;
    //   699: aload_1
    //   700: invokevirtual 470	android/util/ArraySet:remove	(Ljava/lang/Object;)Z
    //   703: pop
    //   704: aload_1
    //   705: aconst_null
    //   706: putfield 314	com/android/server/am/ServiceRecord:app	Lcom/android/server/am/ProcessRecord;
    //   709: getstatic 148	com/android/server/am/ActiveServices:SERVICE_RESCHEDULE	Z
    //   712: ifeq +58 -> 770
    //   715: getstatic 80	com/android/server/am/ActiveServices:DEBUG_DELAYED_SERVICE	Z
    //   718: ifeq +52 -> 770
    //   721: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   724: new 109	java/lang/StringBuilder
    //   727: dup
    //   728: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   731: ldc_w 947
    //   734: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   737: aload_1
    //   738: getfield 370	com/android/server/am/ServiceRecord:shortName	Ljava/lang/String;
    //   741: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   744: ldc_w 949
    //   747: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   750: aload_1
    //   751: getfield 550	com/android/server/am/ServiceRecord:restartDelay	J
    //   754: invokevirtual 952	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   757: ldc_w 954
    //   760: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   763: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   766: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   769: pop
    //   770: iload_3
    //   771: ifne +10 -> 781
    //   774: aload_0
    //   775: aload_1
    //   776: iconst_0
    //   777: invokespecial 958	com/android/server/am/ActiveServices:scheduleServiceRestartLocked	(Lcom/android/server/am/ServiceRecord;Z)Z
    //   780: pop
    //   781: aload 9
    //   783: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	784	0	this	ActiveServices
    //   0	784	1	paramServiceRecord	ServiceRecord
    //   0	784	2	paramProcessRecord	ProcessRecord
    //   0	784	3	paramBoolean	boolean
    //   140	12	4	i	int
    //   97	594	5	bool1	boolean
    //   309	92	6	bool2	boolean
    //   75	10	7	l	long
    //   628	38	9	localDeadObjectException	android.os.DeadObjectException
    //   668	114	9	localObject2	Object
    //   620	6	10	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   197	204	620	finally
    //   125	142	628	android/os/DeadObjectException
    //   147	158	628	android/os/DeadObjectException
    //   158	185	628	android/os/DeadObjectException
    //   185	197	628	android/os/DeadObjectException
    //   204	261	628	android/os/DeadObjectException
    //   261	297	628	android/os/DeadObjectException
    //   611	617	628	android/os/DeadObjectException
    //   622	628	628	android/os/DeadObjectException
    //   125	142	668	finally
    //   147	158	668	finally
    //   158	185	668	finally
    //   185	197	668	finally
    //   204	261	668	finally
    //   261	297	668	finally
    //   611	617	668	finally
    //   622	628	668	finally
    //   630	668	668	finally
  }
  
  private final boolean requestServiceBindingLocked(ServiceRecord paramServiceRecord, IntentBindRecord paramIntentBindRecord, boolean paramBoolean1, boolean paramBoolean2)
    throws TransactionTooLargeException
  {
    if ((paramServiceRecord.app == null) || (paramServiceRecord.app.thread == null)) {
      return false;
    }
    if (((!paramIntentBindRecord.requested) || (paramBoolean2)) && (paramIntentBindRecord.apps.size() > 0)) {}
    try
    {
      bumpServiceExecutingLocked(paramServiceRecord, paramBoolean1, "bind");
      paramServiceRecord.app.forceProcessStateUpTo(10);
      paramServiceRecord.app.thread.scheduleBindService(paramServiceRecord, paramIntentBindRecord.intent.getIntent(), paramBoolean2, paramServiceRecord.app.repProcState);
      if (!paramBoolean2) {
        paramIntentBindRecord.requested = true;
      }
      paramIntentBindRecord.hasBound = true;
      paramIntentBindRecord.doRebind = false;
      return true;
    }
    catch (RemoteException paramIntentBindRecord)
    {
      if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
        Slog.v(TAG_SERVICE, "Crashed while binding " + paramServiceRecord);
      }
      paramBoolean1 = this.mDestroyingServices.contains(paramServiceRecord);
      serviceDoneExecutingLocked(paramServiceRecord, paramBoolean1, paramBoolean1);
      return false;
    }
    catch (TransactionTooLargeException paramIntentBindRecord)
    {
      if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
        Slog.v(TAG_SERVICE, "Crashed while binding " + paramServiceRecord, paramIntentBindRecord);
      }
      paramBoolean1 = this.mDestroyingServices.contains(paramServiceRecord);
      serviceDoneExecutingLocked(paramServiceRecord, paramBoolean1, paramBoolean1);
      throw paramIntentBindRecord;
    }
  }
  
  private final void requestServiceBindingsLocked(ServiceRecord paramServiceRecord, boolean paramBoolean)
    throws TransactionTooLargeException
  {
    int i = paramServiceRecord.bindings.size() - 1;
    for (;;)
    {
      if ((i < 0) || (!requestServiceBindingLocked(paramServiceRecord, (IntentBindRecord)paramServiceRecord.bindings.valueAt(i), paramBoolean, false))) {
        return;
      }
      i -= 1;
    }
  }
  
  private boolean requestStartTargetPermissionsReviewIfNeededLocked(ServiceRecord paramServiceRecord, String paramString, int paramInt1, final Intent paramIntent, boolean paramBoolean, final int paramInt2)
  {
    if (this.mAm.getPackageManagerInternalLocked().isPermissionsReviewRequired(paramServiceRecord.packageName, paramServiceRecord.userId))
    {
      if (!paramBoolean)
      {
        Slog.w(TAG, "u" + paramServiceRecord.userId + " Starting a service in package" + paramServiceRecord.packageName + " requires a permissions review");
        return false;
      }
      ActivityManagerService localActivityManagerService = this.mAm;
      String str = paramIntent.resolveType(this.mAm.mContext.getContentResolver());
      paramString = localActivityManagerService.getIntentSenderLocked(4, paramString, paramInt1, paramInt2, null, null, 0, new Intent[] { paramIntent }, new String[] { str }, 1409286144, null);
      paramIntent = new Intent("android.intent.action.REVIEW_PERMISSIONS");
      paramIntent.addFlags(276824064);
      paramIntent.putExtra("android.intent.extra.PACKAGE_NAME", paramServiceRecord.packageName);
      paramIntent.putExtra("android.intent.extra.INTENT", new IntentSender(paramString));
      if (ActivityManagerDebugConfig.DEBUG_PERMISSIONS_REVIEW) {
        Slog.i(TAG, "u" + paramServiceRecord.userId + " Launching permission review for package " + paramServiceRecord.packageName);
      }
      this.mAm.mHandler.post(new Runnable()
      {
        public void run()
        {
          ActiveServices.this.mAm.mContext.startActivityAsUser(paramIntent, new UserHandle(paramInt2));
        }
      });
      return false;
    }
    return true;
  }
  
  /* Error */
  private ServiceLookupResult retrieveServiceLocked(Intent paramIntent, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    // Byte code:
    //   0: getstatic 136	com/android/server/am/ActivityManagerDebugConfig:DEBUG_SERVICE	Z
    //   3: ifeq +51 -> 54
    //   6: getstatic 91	com/android/server/am/ActiveServices:TAG_SERVICE	Ljava/lang/String;
    //   9: new 109	java/lang/StringBuilder
    //   12: dup
    //   13: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   16: ldc_w 1088
    //   19: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: aload_1
    //   23: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   26: ldc_w 1090
    //   29: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: aload_2
    //   33: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: ldc_w 1092
    //   39: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: iload 5
    //   44: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   47: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   50: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   53: pop
    //   54: aload_0
    //   55: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   58: getfield 571	com/android/server/am/ActivityManagerService:mUserController	Lcom/android/server/am/UserController;
    //   61: iload 4
    //   63: iload 5
    //   65: iload 6
    //   67: iconst_0
    //   68: iconst_1
    //   69: ldc_w 1094
    //   72: aconst_null
    //   73: invokevirtual 1098	com/android/server/am/UserController:handleIncomingUser	(IIIZILjava/lang/String;Ljava/lang/String;)I
    //   76: istore 10
    //   78: aload_0
    //   79: iload 10
    //   81: invokespecial 97	com/android/server/am/ActiveServices:getServiceMap	(I)Lcom/android/server/am/ActiveServices$ServiceMap;
    //   84: astore 15
    //   86: aload_1
    //   87: invokevirtual 1102	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   90: astore 11
    //   92: aload 11
    //   94: ifnull +1443 -> 1537
    //   97: aload 15
    //   99: getfield 409	com/android/server/am/ActiveServices$ServiceMap:mServicesByName	Landroid/util/ArrayMap;
    //   102: aload 11
    //   104: invokevirtual 1104	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   107: checkcast 242	com/android/server/am/ServiceRecord
    //   110: astore 12
    //   112: aload 12
    //   114: astore 11
    //   116: aload 12
    //   118: ifnonnull +12 -> 130
    //   121: iload 9
    //   123: ifeq +150 -> 273
    //   126: aload 12
    //   128: astore 11
    //   130: aload 11
    //   132: astore 12
    //   134: aload 11
    //   136: ifnull +36 -> 172
    //   139: aload 11
    //   141: astore 12
    //   143: aload 11
    //   145: getfield 621	com/android/server/am/ServiceRecord:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   148: getfield 626	android/content/pm/ServiceInfo:flags	I
    //   151: iconst_4
    //   152: iand
    //   153: ifeq +19 -> 172
    //   156: aload_3
    //   157: aload 11
    //   159: getfield 611	com/android/server/am/ServiceRecord:packageName	Ljava/lang/String;
    //   162: invokevirtual 734	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   165: ifeq +136 -> 301
    //   168: aload 11
    //   170: astore 12
    //   172: aload 12
    //   174: astore 11
    //   176: aload 12
    //   178: ifnonnull +233 -> 411
    //   181: aload 12
    //   183: astore 11
    //   185: invokestatic 610	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   188: aload_1
    //   189: aload_2
    //   190: ldc_w 1105
    //   193: iload 10
    //   195: invokeinterface 1109 5 0
    //   200: astore 13
    //   202: aload 13
    //   204: ifnull +103 -> 307
    //   207: aload 12
    //   209: astore 11
    //   211: aload 13
    //   213: getfield 1112	android/content/pm/ResolveInfo:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   216: astore 13
    //   218: aload 13
    //   220: ifnonnull +93 -> 313
    //   223: aload 12
    //   225: astore 11
    //   227: getstatic 91	com/android/server/am/ActiveServices:TAG_SERVICE	Ljava/lang/String;
    //   230: new 109	java/lang/StringBuilder
    //   233: dup
    //   234: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   237: ldc_w 1114
    //   240: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: aload_1
    //   244: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   247: ldc_w 1116
    //   250: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: iload 10
    //   255: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   258: ldc_w 1118
    //   261: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   267: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   270: pop
    //   271: aconst_null
    //   272: areturn
    //   273: new 355	android/content/Intent$FilterComparison
    //   276: dup
    //   277: aload_1
    //   278: invokespecial 1121	android/content/Intent$FilterComparison:<init>	(Landroid/content/Intent;)V
    //   281: astore 11
    //   283: aload 15
    //   285: getfield 416	com/android/server/am/ActiveServices$ServiceMap:mServicesByIntent	Landroid/util/ArrayMap;
    //   288: aload 11
    //   290: invokevirtual 1104	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   293: checkcast 242	com/android/server/am/ServiceRecord
    //   296: astore 11
    //   298: goto -168 -> 130
    //   301: aconst_null
    //   302: astore 12
    //   304: goto -132 -> 172
    //   307: aconst_null
    //   308: astore 13
    //   310: goto -92 -> 218
    //   313: aload 12
    //   315: astore 11
    //   317: new 747	android/content/ComponentName
    //   320: dup
    //   321: aload 13
    //   323: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   326: getfield 588	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   329: aload 13
    //   331: getfield 1123	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   334: invokespecial 1126	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   337: astore 14
    //   339: aload 12
    //   341: astore 11
    //   343: aload 13
    //   345: getfield 626	android/content/pm/ServiceInfo:flags	I
    //   348: iconst_4
    //   349: iand
    //   350: ifeq +859 -> 1209
    //   353: iload 9
    //   355: ifeq +821 -> 1176
    //   358: aload 12
    //   360: astore 11
    //   362: aload 13
    //   364: getfield 1129	android/content/pm/ServiceInfo:exported	Z
    //   367: ifne +196 -> 563
    //   370: aload 12
    //   372: astore 11
    //   374: new 1131	java/lang/SecurityException
    //   377: dup
    //   378: new 109	java/lang/StringBuilder
    //   381: dup
    //   382: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   385: ldc_w 1133
    //   388: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   391: aload 14
    //   393: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   396: ldc_w 1135
    //   399: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   402: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   405: invokespecial 1136	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   408: athrow
    //   409: astore 12
    //   411: aload 11
    //   413: ifnull +1108 -> 1521
    //   416: aload_0
    //   417: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   420: aload 11
    //   422: getfield 1139	com/android/server/am/ServiceRecord:permission	Ljava/lang/String;
    //   425: iload 4
    //   427: iload 5
    //   429: aload 11
    //   431: getfield 583	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
    //   434: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   437: aload 11
    //   439: getfield 1140	com/android/server/am/ServiceRecord:exported	Z
    //   442: invokevirtual 1144	com/android/server/am/ActivityManagerService:checkComponentPermission	(Ljava/lang/String;IIIZ)I
    //   445: ifeq +918 -> 1363
    //   448: aload 11
    //   450: getfield 1140	com/android/server/am/ServiceRecord:exported	Z
    //   453: ifne +828 -> 1281
    //   456: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   459: new 109	java/lang/StringBuilder
    //   462: dup
    //   463: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   466: ldc_w 1146
    //   469: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   472: aload 11
    //   474: getfield 269	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
    //   477: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   480: ldc_w 1148
    //   483: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: iload 4
    //   488: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   491: ldc_w 1150
    //   494: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   497: iload 5
    //   499: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   502: ldc_w 1152
    //   505: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   508: aload 11
    //   510: getfield 583	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
    //   513: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   516: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   519: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   522: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   525: pop
    //   526: new 17	com/android/server/am/ActiveServices$ServiceLookupResult
    //   529: dup
    //   530: aload_0
    //   531: aconst_null
    //   532: new 109	java/lang/StringBuilder
    //   535: dup
    //   536: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   539: ldc_w 1154
    //   542: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   545: aload 11
    //   547: getfield 583	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
    //   550: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   553: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   556: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   559: invokespecial 1157	com/android/server/am/ActiveServices$ServiceLookupResult:<init>	(Lcom/android/server/am/ActiveServices;Lcom/android/server/am/ServiceRecord;Ljava/lang/String;)V
    //   562: areturn
    //   563: aload 12
    //   565: astore 11
    //   567: aload 13
    //   569: getfield 626	android/content/pm/ServiceInfo:flags	I
    //   572: iconst_2
    //   573: iand
    //   574: ifne +42 -> 616
    //   577: aload 12
    //   579: astore 11
    //   581: new 1131	java/lang/SecurityException
    //   584: dup
    //   585: new 109	java/lang/StringBuilder
    //   588: dup
    //   589: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   592: ldc_w 1133
    //   595: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   598: aload 14
    //   600: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   603: ldc_w 1159
    //   606: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   609: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   612: invokespecial 1136	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   615: athrow
    //   616: aload 12
    //   618: astore 11
    //   620: invokestatic 610	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   623: aload_3
    //   624: sipush 1024
    //   627: iload 10
    //   629: invokeinterface 1163 4 0
    //   634: astore 16
    //   636: aload 16
    //   638: ifnonnull +35 -> 673
    //   641: aload 12
    //   643: astore 11
    //   645: new 1131	java/lang/SecurityException
    //   648: dup
    //   649: new 109	java/lang/StringBuilder
    //   652: dup
    //   653: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   656: ldc_w 1165
    //   659: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   662: aload_3
    //   663: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   666: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   669: invokespecial 1136	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   672: athrow
    //   673: aload 12
    //   675: astore 11
    //   677: new 623	android/content/pm/ServiceInfo
    //   680: dup
    //   681: aload 13
    //   683: invokespecial 1168	android/content/pm/ServiceInfo:<init>	(Landroid/content/pm/ServiceInfo;)V
    //   686: astore 13
    //   688: aload 12
    //   690: astore 11
    //   692: aload 13
    //   694: new 585	android/content/pm/ApplicationInfo
    //   697: dup
    //   698: aload 13
    //   700: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   703: invokespecial 1171	android/content/pm/ApplicationInfo:<init>	(Landroid/content/pm/ApplicationInfo;)V
    //   706: putfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   709: aload 12
    //   711: astore 11
    //   713: aload 13
    //   715: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   718: aload 16
    //   720: getfield 588	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   723: putfield 588	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   726: aload 12
    //   728: astore 11
    //   730: aload 13
    //   732: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   735: aload 16
    //   737: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   740: putfield 593	android/content/pm/ApplicationInfo:uid	I
    //   743: aload 12
    //   745: astore 11
    //   747: new 747	android/content/ComponentName
    //   750: dup
    //   751: aload 16
    //   753: getfield 588	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   756: aload 14
    //   758: invokevirtual 750	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   761: invokespecial 1126	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   764: astore 16
    //   766: aload 12
    //   768: astore 11
    //   770: aload_1
    //   771: aload 16
    //   773: invokevirtual 1175	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   776: pop
    //   777: iload 10
    //   779: ifle +751 -> 1530
    //   782: aload 12
    //   784: astore 11
    //   786: aload 15
    //   788: astore 14
    //   790: iload 10
    //   792: istore 6
    //   794: aload_0
    //   795: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   798: aload 13
    //   800: getfield 1176	android/content/pm/ServiceInfo:processName	Ljava/lang/String;
    //   803: aload 13
    //   805: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   808: aload 13
    //   810: getfield 1123	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   813: aload 13
    //   815: getfield 626	android/content/pm/ServiceInfo:flags	I
    //   818: invokevirtual 1180	com/android/server/am/ActivityManagerService:isSingleton	(Ljava/lang/String;Landroid/content/pm/ApplicationInfo;Ljava/lang/String;I)Z
    //   821: ifeq +49 -> 870
    //   824: aload 12
    //   826: astore 11
    //   828: aload 15
    //   830: astore 14
    //   832: iload 10
    //   834: istore 6
    //   836: aload_0
    //   837: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   840: iload 5
    //   842: aload 13
    //   844: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   847: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   850: invokevirtual 1184	com/android/server/am/ActivityManagerService:isValidSingletonCall	(II)Z
    //   853: ifeq +17 -> 870
    //   856: iconst_0
    //   857: istore 6
    //   859: aload 12
    //   861: astore 11
    //   863: aload_0
    //   864: iconst_0
    //   865: invokespecial 97	com/android/server/am/ActiveServices:getServiceMap	(I)Lcom/android/server/am/ActiveServices$ServiceMap;
    //   868: astore 14
    //   870: aload 12
    //   872: astore 11
    //   874: new 623	android/content/pm/ServiceInfo
    //   877: dup
    //   878: aload 13
    //   880: invokespecial 1168	android/content/pm/ServiceInfo:<init>	(Landroid/content/pm/ServiceInfo;)V
    //   883: astore 13
    //   885: aload 12
    //   887: astore 11
    //   889: aload 13
    //   891: aload_0
    //   892: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   895: aload 13
    //   897: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   900: iload 6
    //   902: invokevirtual 1188	com/android/server/am/ActivityManagerService:getAppInfoForUser	(Landroid/content/pm/ApplicationInfo;I)Landroid/content/pm/ApplicationInfo;
    //   905: putfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   908: aload 12
    //   910: astore 11
    //   912: aload 14
    //   914: getfield 409	com/android/server/am/ActiveServices$ServiceMap:mServicesByName	Landroid/util/ArrayMap;
    //   917: aload 16
    //   919: invokevirtual 1104	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   922: checkcast 242	com/android/server/am/ServiceRecord
    //   925: astore 12
    //   927: aload 12
    //   929: ifnonnull +328 -> 1257
    //   932: iload 7
    //   934: ifeq +589 -> 1523
    //   937: new 355	android/content/Intent$FilterComparison
    //   940: dup
    //   941: aload_1
    //   942: invokevirtual 1191	android/content/Intent:cloneFilter	()Landroid/content/Intent;
    //   945: invokespecial 1121	android/content/Intent$FilterComparison:<init>	(Landroid/content/Intent;)V
    //   948: astore 17
    //   950: new 23	com/android/server/am/ActiveServices$ServiceRestarter
    //   953: dup
    //   954: aload_0
    //   955: aconst_null
    //   956: invokespecial 1194	com/android/server/am/ActiveServices$ServiceRestarter:<init>	(Lcom/android/server/am/ActiveServices;Lcom/android/server/am/ActiveServices$ServiceRestarter;)V
    //   959: astore 18
    //   961: aload_0
    //   962: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   965: getfield 1198	com/android/server/am/ActivityManagerService:mBatteryStatsService	Lcom/android/server/am/BatteryStatsService;
    //   968: invokevirtual 1203	com/android/server/am/BatteryStatsService:getActiveStatistics	()Lcom/android/internal/os/BatteryStatsImpl;
    //   971: astore 11
    //   973: aload 11
    //   975: monitorenter
    //   976: aload 11
    //   978: aload 13
    //   980: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   983: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   986: aload 13
    //   988: getfield 916	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   991: aload 13
    //   993: getfield 1123	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   996: invokevirtual 1209	com/android/internal/os/BatteryStatsImpl:getServiceStatsLocked	(ILjava/lang/String;Ljava/lang/String;)Lcom/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv;
    //   999: astore 15
    //   1001: aload 11
    //   1003: monitorexit
    //   1004: new 242	com/android/server/am/ServiceRecord
    //   1007: dup
    //   1008: aload_0
    //   1009: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   1012: aload 15
    //   1014: aload 16
    //   1016: aload 17
    //   1018: aload 13
    //   1020: iload 8
    //   1022: aload 18
    //   1024: invokespecial 1212	com/android/server/am/ServiceRecord:<init>	(Lcom/android/server/am/ActivityManagerService;Lcom/android/internal/os/BatteryStatsImpl$Uid$Pkg$Serv;Landroid/content/ComponentName;Landroid/content/Intent$FilterComparison;Landroid/content/pm/ServiceInfo;ZLjava/lang/Runnable;)V
    //   1027: astore 15
    //   1029: aload 15
    //   1031: astore 11
    //   1033: aload 18
    //   1035: aload 15
    //   1037: invokevirtual 500	com/android/server/am/ActiveServices$ServiceRestarter:setService	(Lcom/android/server/am/ServiceRecord;)V
    //   1040: aload 15
    //   1042: astore 11
    //   1044: aload 14
    //   1046: getfield 409	com/android/server/am/ActiveServices$ServiceMap:mServicesByName	Landroid/util/ArrayMap;
    //   1049: aload 16
    //   1051: aload 15
    //   1053: invokevirtual 1215	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1056: pop
    //   1057: aload 15
    //   1059: astore 11
    //   1061: aload 14
    //   1063: getfield 416	com/android/server/am/ActiveServices$ServiceMap:mServicesByIntent	Landroid/util/ArrayMap;
    //   1066: aload 17
    //   1068: aload 15
    //   1070: invokevirtual 1215	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1073: pop
    //   1074: aload 15
    //   1076: astore 11
    //   1078: aload_0
    //   1079: getfield 190	com/android/server/am/ActiveServices:mPendingServices	Ljava/util/ArrayList;
    //   1082: invokevirtual 254	java/util/ArrayList:size	()I
    //   1085: iconst_1
    //   1086: isub
    //   1087: istore 6
    //   1089: aload 15
    //   1091: astore 11
    //   1093: iload 6
    //   1095: iflt -684 -> 411
    //   1098: aload 15
    //   1100: astore 11
    //   1102: aload_0
    //   1103: getfield 190	com/android/server/am/ActiveServices:mPendingServices	Ljava/util/ArrayList;
    //   1106: iload 6
    //   1108: invokevirtual 256	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1111: checkcast 242	com/android/server/am/ServiceRecord
    //   1114: astore 12
    //   1116: aload 15
    //   1118: astore 11
    //   1120: aload 12
    //   1122: getfield 621	com/android/server/am/ServiceRecord:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   1125: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1128: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   1131: aload 13
    //   1133: getfield 928	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1136: getfield 593	android/content/pm/ApplicationInfo:uid	I
    //   1139: if_icmpne +404 -> 1543
    //   1142: aload 15
    //   1144: astore 11
    //   1146: aload 12
    //   1148: getfield 269	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
    //   1151: aload 16
    //   1153: invokevirtual 1216	android/content/ComponentName:equals	(Ljava/lang/Object;)Z
    //   1156: ifeq +387 -> 1543
    //   1159: aload 15
    //   1161: astore 11
    //   1163: aload_0
    //   1164: getfield 190	com/android/server/am/ActiveServices:mPendingServices	Ljava/util/ArrayList;
    //   1167: iload 6
    //   1169: invokevirtual 425	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   1172: pop
    //   1173: goto +370 -> 1543
    //   1176: aload 12
    //   1178: astore 11
    //   1180: new 1131	java/lang/SecurityException
    //   1183: dup
    //   1184: new 109	java/lang/StringBuilder
    //   1187: dup
    //   1188: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   1191: ldc_w 1218
    //   1194: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1197: aload 14
    //   1199: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1202: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1205: invokespecial 1136	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   1208: athrow
    //   1209: aload 14
    //   1211: astore 16
    //   1213: iload 9
    //   1215: ifeq -438 -> 777
    //   1218: aload 12
    //   1220: astore 11
    //   1222: new 1131	java/lang/SecurityException
    //   1225: dup
    //   1226: new 109	java/lang/StringBuilder
    //   1229: dup
    //   1230: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   1233: ldc_w 1133
    //   1236: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1239: aload 14
    //   1241: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1244: ldc_w 1220
    //   1247: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1250: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1253: invokespecial 1136	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   1256: athrow
    //   1257: aload 12
    //   1259: astore 11
    //   1261: goto -850 -> 411
    //   1264: astore 13
    //   1266: aload 11
    //   1268: monitorexit
    //   1269: aload 13
    //   1271: athrow
    //   1272: astore 11
    //   1274: aload 12
    //   1276: astore 11
    //   1278: goto -867 -> 411
    //   1281: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   1284: new 109	java/lang/StringBuilder
    //   1287: dup
    //   1288: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   1291: ldc_w 1146
    //   1294: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1297: aload 11
    //   1299: getfield 269	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
    //   1302: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1305: ldc_w 1148
    //   1308: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1311: iload 4
    //   1313: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1316: ldc_w 1150
    //   1319: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1322: iload 5
    //   1324: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1327: ldc_w 1222
    //   1330: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1333: aload 11
    //   1335: getfield 1139	com/android/server/am/ServiceRecord:permission	Ljava/lang/String;
    //   1338: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1341: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1344: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1347: pop
    //   1348: new 17	com/android/server/am/ActiveServices$ServiceLookupResult
    //   1351: dup
    //   1352: aload_0
    //   1353: aconst_null
    //   1354: aload 11
    //   1356: getfield 1139	com/android/server/am/ServiceRecord:permission	Ljava/lang/String;
    //   1359: invokespecial 1157	com/android/server/am/ActiveServices$ServiceLookupResult:<init>	(Lcom/android/server/am/ActiveServices;Lcom/android/server/am/ServiceRecord;Ljava/lang/String;)V
    //   1362: areturn
    //   1363: aload 11
    //   1365: getfield 1139	com/android/server/am/ServiceRecord:permission	Ljava/lang/String;
    //   1368: ifnull +110 -> 1478
    //   1371: aload_3
    //   1372: ifnull +106 -> 1478
    //   1375: aload 11
    //   1377: getfield 1139	com/android/server/am/ServiceRecord:permission	Ljava/lang/String;
    //   1380: invokestatic 1227	android/app/AppOpsManager:permissionToOpCode	(Ljava/lang/String;)I
    //   1383: istore 6
    //   1385: iload 6
    //   1387: iconst_m1
    //   1388: if_icmpeq +90 -> 1478
    //   1391: aload_0
    //   1392: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   1395: getfield 1231	com/android/server/am/ActivityManagerService:mAppOpsService	Lcom/android/server/AppOpsService;
    //   1398: iload 6
    //   1400: iload 5
    //   1402: aload_3
    //   1403: invokevirtual 1237	com/android/server/AppOpsService:noteOperation	(IILjava/lang/String;)I
    //   1406: ifeq +72 -> 1478
    //   1409: getstatic 88	com/android/server/am/ActiveServices:TAG	Ljava/lang/String;
    //   1412: new 109	java/lang/StringBuilder
    //   1415: dup
    //   1416: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   1419: ldc_w 1239
    //   1422: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1425: aload 11
    //   1427: getfield 269	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
    //   1430: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1433: ldc_w 1148
    //   1436: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1439: iload 4
    //   1441: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1444: ldc_w 1150
    //   1447: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1450: iload 5
    //   1452: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1455: ldc_w 1241
    //   1458: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1461: iload 6
    //   1463: invokestatic 1244	android/app/AppOpsManager:opToName	(I)Ljava/lang/String;
    //   1466: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1469: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1472: invokestatic 604	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1475: pop
    //   1476: aconst_null
    //   1477: areturn
    //   1478: aload_0
    //   1479: getfield 203	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
    //   1482: getfield 1248	com/android/server/am/ActivityManagerService:mIntentFirewall	Lcom/android/server/firewall/IntentFirewall;
    //   1485: aload 11
    //   1487: getfield 269	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
    //   1490: aload_1
    //   1491: iload 5
    //   1493: iload 4
    //   1495: aload_2
    //   1496: aload 11
    //   1498: getfield 583	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
    //   1501: invokevirtual 1254	com/android/server/firewall/IntentFirewall:checkService	(Landroid/content/ComponentName;Landroid/content/Intent;IILjava/lang/String;Landroid/content/pm/ApplicationInfo;)Z
    //   1504: ifne +5 -> 1509
    //   1507: aconst_null
    //   1508: areturn
    //   1509: new 17	com/android/server/am/ActiveServices$ServiceLookupResult
    //   1512: dup
    //   1513: aload_0
    //   1514: aload 11
    //   1516: aconst_null
    //   1517: invokespecial 1157	com/android/server/am/ActiveServices$ServiceLookupResult:<init>	(Lcom/android/server/am/ActiveServices;Lcom/android/server/am/ServiceRecord;Ljava/lang/String;)V
    //   1520: areturn
    //   1521: aconst_null
    //   1522: areturn
    //   1523: aload 12
    //   1525: astore 11
    //   1527: goto -1116 -> 411
    //   1530: aload 15
    //   1532: astore 14
    //   1534: goto -626 -> 908
    //   1537: aconst_null
    //   1538: astore 12
    //   1540: goto -1428 -> 112
    //   1543: iload 6
    //   1545: iconst_1
    //   1546: isub
    //   1547: istore 6
    //   1549: goto -460 -> 1089
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1552	0	this	ActiveServices
    //   0	1552	1	paramIntent	Intent
    //   0	1552	2	paramString1	String
    //   0	1552	3	paramString2	String
    //   0	1552	4	paramInt1	int
    //   0	1552	5	paramInt2	int
    //   0	1552	6	paramInt3	int
    //   0	1552	7	paramBoolean1	boolean
    //   0	1552	8	paramBoolean2	boolean
    //   0	1552	9	paramBoolean3	boolean
    //   76	757	10	i	int
    //   1272	1	11	localRemoteException1	RemoteException
    //   1276	250	11	localObject2	Object
    //   110	261	12	localObject3	Object
    //   409	500	12	localRemoteException2	RemoteException
    //   925	614	12	localServiceRecord	ServiceRecord
    //   200	932	13	localObject4	Object
    //   1264	6	13	localObject5	Object
    //   337	1196	14	localObject6	Object
    //   84	1447	15	localObject7	Object
    //   634	578	16	localObject8	Object
    //   948	119	17	localFilterComparison	Intent.FilterComparison
    //   959	75	18	localServiceRestarter	ServiceRestarter
    // Exception table:
    //   from	to	target	type
    //   185	202	409	android/os/RemoteException
    //   211	218	409	android/os/RemoteException
    //   227	271	409	android/os/RemoteException
    //   317	339	409	android/os/RemoteException
    //   343	353	409	android/os/RemoteException
    //   362	370	409	android/os/RemoteException
    //   374	409	409	android/os/RemoteException
    //   567	577	409	android/os/RemoteException
    //   581	616	409	android/os/RemoteException
    //   620	636	409	android/os/RemoteException
    //   645	673	409	android/os/RemoteException
    //   677	688	409	android/os/RemoteException
    //   692	709	409	android/os/RemoteException
    //   713	726	409	android/os/RemoteException
    //   730	743	409	android/os/RemoteException
    //   747	766	409	android/os/RemoteException
    //   770	777	409	android/os/RemoteException
    //   794	824	409	android/os/RemoteException
    //   836	856	409	android/os/RemoteException
    //   863	870	409	android/os/RemoteException
    //   874	885	409	android/os/RemoteException
    //   889	908	409	android/os/RemoteException
    //   912	927	409	android/os/RemoteException
    //   1033	1040	409	android/os/RemoteException
    //   1044	1057	409	android/os/RemoteException
    //   1061	1074	409	android/os/RemoteException
    //   1078	1089	409	android/os/RemoteException
    //   1102	1116	409	android/os/RemoteException
    //   1120	1142	409	android/os/RemoteException
    //   1146	1159	409	android/os/RemoteException
    //   1163	1173	409	android/os/RemoteException
    //   1180	1209	409	android/os/RemoteException
    //   1222	1257	409	android/os/RemoteException
    //   976	1001	1264	finally
    //   937	976	1272	android/os/RemoteException
    //   1001	1029	1272	android/os/RemoteException
    //   1266	1272	1272	android/os/RemoteException
  }
  
  private final boolean scheduleServiceRestartLocked(ServiceRecord paramServiceRecord, boolean paramBoolean)
  {
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool1 = false;
    if (this.mAm.isShuttingDownLocked())
    {
      Slog.w(TAG, "Not scheduling restart of crashed service " + paramServiceRecord.shortName + " - system is shutting down");
      return false;
    }
    Object localObject = getServiceMap(paramServiceRecord.userId);
    if (((ServiceMap)localObject).mServicesByName.get(paramServiceRecord.name) != paramServiceRecord)
    {
      localObject = (ServiceRecord)((ServiceMap)localObject).mServicesByName.get(paramServiceRecord.name);
      Slog.wtf(TAG, "Attempting to schedule restart of " + paramServiceRecord + " when found in map: " + localObject);
      return false;
    }
    long l6 = SystemClock.uptimeMillis();
    int i;
    if (((paramServiceRecord.serviceInfo.applicationInfo.flags & 0x8) != 0) || ("android.permission.BIND_WALLPAPER".equals(paramServiceRecord.permission)))
    {
      paramServiceRecord.totalRestartCount += 1;
      paramServiceRecord.restartCount = 0;
      paramServiceRecord.restartDelay = 0L;
      paramServiceRecord.nextRestartTime = l6;
      paramBoolean = bool1;
      if (!this.mRestartingServices.contains(paramServiceRecord))
      {
        paramServiceRecord.createdFromFg = false;
        this.mRestartingServices.add(paramServiceRecord);
        paramServiceRecord.makeRestarting(this.mAm.mProcessStats.getMemFactorLocked(), l6);
      }
      cancelForegroudNotificationLocked(paramServiceRecord);
      if (DEBUG_DELAYED_SERVICE) {
        Slog.w(TAG, "r " + paramServiceRecord + " r.restartDelay " + paramServiceRecord.restartDelay + " r.nextRestartTime " + paramServiceRecord.nextRestartTime);
      }
      this.mAm.mHandler.removeCallbacks(paramServiceRecord.restarter);
      this.mAm.mHandler.postAtTime(paramServiceRecord.restarter, paramServiceRecord.nextRestartTime);
      paramServiceRecord.nextRestartTime = (SystemClock.uptimeMillis() + paramServiceRecord.restartDelay);
      Slog.w(TAG, "Scheduling restart of crashed service " + paramServiceRecord.shortName + " in " + paramServiceRecord.restartDelay + "ms");
      if ((SERVICE_RESCHEDULE) && (DEBUG_DELAYED_SERVICE)) {
        i = this.mRestartingServices.size() - 1;
      }
    }
    else
    {
      while (i >= 0)
      {
        localObject = (ServiceRecord)this.mRestartingServices.get(i);
        Slog.w(TAG, "Restarting list - i " + i + " r2.nextRestartTime " + ((ServiceRecord)localObject).nextRestartTime + " r2.name " + ((ServiceRecord)localObject).name);
        i -= 1;
        continue;
        long l1 = 1000L;
        long l3 = 60000L;
        int j = paramServiceRecord.deliveredStarts.size();
        if (DEBUG_DELAYED_SERVICE) {
          Slog.w(TAG, " scheduleServiceRestartLocked  N " + j + " now " + l6 + " r " + paramServiceRecord);
        }
        bool1 = bool3;
        long l4 = l1;
        long l2 = l3;
        if (j > 0)
        {
          i = j - 1;
          l2 = l3;
          bool1 = bool2;
          if (i >= 0)
          {
            localObject = (ServiceRecord.StartItem)paramServiceRecord.deliveredStarts.get(i);
            ((ServiceRecord.StartItem)localObject).removeUriPermissionsLocked();
            long l5;
            if (((ServiceRecord.StartItem)localObject).intent == null)
            {
              l5 = l2;
              bool2 = bool1;
            }
            for (;;)
            {
              i -= 1;
              bool1 = bool2;
              l2 = l5;
              break;
              if ((!paramBoolean) || ((((ServiceRecord.StartItem)localObject).deliveryCount < 3) && (((ServiceRecord.StartItem)localObject).doneExecutingCount < 6)))
              {
                paramServiceRecord.pendingStarts.add(0, localObject);
                l4 = (SystemClock.uptimeMillis() - ((ServiceRecord.StartItem)localObject).deliveredTime) * 2L;
                if ((SERVICE_RESCHEDULE) && (DEBUG_DELAYED_SERVICE)) {
                  Slog.w(TAG, "Can add more delay !!! si.deliveredTime " + ((ServiceRecord.StartItem)localObject).deliveredTime + " dur " + l4 + " si.deliveryCount " + ((ServiceRecord.StartItem)localObject).deliveryCount + " si.doneExecutingCount " + ((ServiceRecord.StartItem)localObject).doneExecutingCount + " allowCancel " + paramBoolean);
                }
                l3 = l1;
                if (l1 < l4) {
                  l3 = l4;
                }
                bool2 = bool1;
                l1 = l3;
                l5 = l2;
                if (l2 < l4)
                {
                  bool2 = bool1;
                  l1 = l3;
                  l5 = l4;
                }
              }
              else
              {
                Slog.w(TAG, "Canceling start item " + ((ServiceRecord.StartItem)localObject).intent + " in service " + paramServiceRecord.name);
                bool2 = true;
                l5 = l2;
              }
            }
          }
          paramServiceRecord.deliveredStarts.clear();
          l4 = l1;
        }
        paramServiceRecord.totalRestartCount += 1;
        if (DEBUG_DELAYED_SERVICE) {
          Slog.w(TAG, " scheduleServiceRestartLocked  r.totalRestartCount " + paramServiceRecord.totalRestartCount + " r " + paramServiceRecord);
        }
        if ((SERVICE_RESCHEDULE) && (DEBUG_DELAYED_SERVICE)) {
          Slog.w(TAG, "r.name " + paramServiceRecord.name + " N " + j + " minDuration " + l4 + " resetTime " + l2 + " now " + l6 + " r.restartDelay " + paramServiceRecord.restartDelay + " r.restartTime+resetTime " + (paramServiceRecord.restartTime + l2) + " allowCancel " + paramBoolean);
        }
        label1122:
        label1271:
        int k;
        if (paramServiceRecord.restartDelay == 0L)
        {
          paramServiceRecord.restartCount += 1;
          paramServiceRecord.restartDelay = l4;
          paramServiceRecord.nextRestartTime = (paramServiceRecord.restartDelay + l6);
          if ((SERVICE_RESCHEDULE) && (DEBUG_DELAYED_SERVICE)) {
            Slog.w(TAG, "r.name " + paramServiceRecord.name + " N " + j + " minDuration " + l4 + " resetTime " + l2 + " now " + l6 + " r.restartDelay " + paramServiceRecord.restartDelay + " r.restartTime+resetTime " + (paramServiceRecord.restartTime + l2) + " r.nextRestartTime " + paramServiceRecord.nextRestartTime + " allowCancel " + paramBoolean);
          }
          k = 0;
          i = this.mRestartingServices.size() - 1;
        }
        for (;;)
        {
          j = k;
          if (i >= 0)
          {
            localObject = (ServiceRecord)this.mRestartingServices.get(i);
            if ((localObject != paramServiceRecord) && (paramServiceRecord.nextRestartTime >= ((ServiceRecord)localObject).nextRestartTime - 10000L) && (paramServiceRecord.nextRestartTime < ((ServiceRecord)localObject).nextRestartTime + 10000L))
            {
              paramServiceRecord.nextRestartTime = (((ServiceRecord)localObject).nextRestartTime + 10000L);
              paramServiceRecord.restartDelay = (paramServiceRecord.nextRestartTime - l6);
              j = 1;
            }
          }
          else
          {
            paramBoolean = bool1;
            if (j == 0) {
              break;
            }
            break label1271;
            if (l6 > paramServiceRecord.restartTime + l2)
            {
              paramServiceRecord.restartCount = 1;
              paramServiceRecord.restartDelay = l4;
              break label1122;
            }
            paramServiceRecord.restartDelay *= 4L;
            if (paramServiceRecord.restartDelay >= l4) {
              break label1122;
            }
            paramServiceRecord.restartDelay = l4;
            break label1122;
          }
          i -= 1;
        }
      }
    }
    EventLog.writeEvent(30035, new Object[] { Integer.valueOf(paramServiceRecord.userId), paramServiceRecord.shortName, Long.valueOf(paramServiceRecord.restartDelay) });
    if (DEBUG_DELAYED_SERVICE) {
      Slog.v(TAG, "scheduleServiceRestartLocked r " + paramServiceRecord + " call by " + Debug.getCallers(8));
    }
    return paramBoolean;
  }
  
  private final void sendServiceArgsLocked(ServiceRecord paramServiceRecord, boolean paramBoolean1, boolean paramBoolean2)
    throws TransactionTooLargeException
  {
    int k = paramServiceRecord.pendingStarts.size();
    boolean bool1 = paramBoolean2;
    if (k == 0) {
      return;
    }
    Object localObject3;
    boolean bool2;
    boolean bool3;
    boolean bool4;
    for (;;)
    {
      if (paramServiceRecord.pendingStarts.size() > 0)
      {
        Object localObject4 = null;
        localObject3 = null;
        bool2 = bool1;
        bool3 = bool1;
        bool4 = bool1;
        try
        {
          localObject5 = (ServiceRecord.StartItem)paramServiceRecord.pendingStarts.remove(0);
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          if (ActivityManagerDebugConfig.DEBUG_SERVICE)
          {
            bool2 = bool1;
            bool3 = bool1;
            localObject3 = localObject5;
            bool4 = bool1;
            Slog.v(TAG_SERVICE, "Sending arguments to: " + paramServiceRecord + " " + paramServiceRecord.intent + " args=" + ((ServiceRecord.StartItem)localObject5).intent);
          }
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          if ((((ServiceRecord.StartItem)localObject5).intent == null) && (k > 1)) {
            continue;
          }
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          ((ServiceRecord.StartItem)localObject5).deliveredTime = SystemClock.uptimeMillis();
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          paramServiceRecord.deliveredStarts.add(localObject5);
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          ((ServiceRecord.StartItem)localObject5).deliveryCount += 1;
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          if (((ServiceRecord.StartItem)localObject5).neededGrants != null)
          {
            bool2 = bool1;
            bool3 = bool1;
            localObject3 = localObject5;
            bool4 = bool1;
            this.mAm.grantUriPermissionUncheckedFromIntentLocked(((ServiceRecord.StartItem)localObject5).neededGrants, ((ServiceRecord.StartItem)localObject5).getUriPermissionsLocked());
          }
          bool2 = bool1;
          bool3 = bool1;
          localObject3 = localObject5;
          bool4 = bool1;
          bumpServiceExecutingLocked(paramServiceRecord, paramBoolean1, "start");
          paramBoolean2 = bool1;
          if (!bool1)
          {
            bool2 = true;
            bool3 = true;
            bool4 = true;
            paramBoolean2 = true;
            localObject3 = localObject5;
            this.mAm.updateOomAdjLocked(paramServiceRecord.app);
          }
          int i = 0;
          bool2 = paramBoolean2;
          bool3 = paramBoolean2;
          localObject3 = localObject5;
          bool4 = paramBoolean2;
          if (((ServiceRecord.StartItem)localObject5).deliveryCount > 1) {
            i = 2;
          }
          int j = i;
          bool2 = paramBoolean2;
          bool3 = paramBoolean2;
          localObject3 = localObject5;
          bool4 = paramBoolean2;
          if (((ServiceRecord.StartItem)localObject5).doneExecutingCount > 0) {
            j = i | 0x1;
          }
          bool2 = paramBoolean2;
          bool3 = paramBoolean2;
          localObject3 = localObject5;
          bool4 = paramBoolean2;
          paramServiceRecord.app.thread.scheduleServiceArgs(paramServiceRecord, ((ServiceRecord.StartItem)localObject5).taskRemoved, ((ServiceRecord.StartItem)localObject5).id, j, ((ServiceRecord.StartItem)localObject5).intent);
          localObject1 = localObject4;
          bool1 = paramBoolean2;
          bool2 = paramBoolean2;
          bool3 = paramBoolean2;
          localObject3 = localObject5;
          bool4 = paramBoolean2;
          if (ActivityManagerDebugConfig.DEBUG_SERVICE)
          {
            bool2 = paramBoolean2;
            bool3 = paramBoolean2;
            localObject3 = localObject5;
            bool4 = paramBoolean2;
            Slog.d(TAG, "SVC-Sent arguments: " + paramServiceRecord + ", app=" + paramServiceRecord.app + ", args=" + ((ServiceRecord.StartItem)localObject5).intent + ", flags=" + j);
            bool1 = paramBoolean2;
            localObject1 = localObject4;
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Object localObject1;
            Slog.w(TAG, "Unexpected exception", localException);
            bool1 = bool2;
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
              Slog.v(TAG_SERVICE, "Crashed while sending args: " + paramServiceRecord);
            }
            bool1 = bool3;
          }
        }
        catch (TransactionTooLargeException localTransactionTooLargeException)
        {
          if (!ActivityManagerDebugConfig.DEBUG_SERVICE) {
            break label743;
          }
          Object localObject5 = TAG_SERVICE;
          StringBuilder localStringBuilder = new StringBuilder().append("Transaction too large: intent= ");
          if (localObject3 == null) {
            break label754;
          }
          for (Object localObject2 = ((ServiceRecord.StartItem)localObject3).intent;; localObject2 = null)
          {
            Slog.v((String)localObject5, localObject2);
            localObject2 = localTransactionTooLargeException;
            bool1 = bool4;
            break;
          }
        }
        if (localObject1 != null)
        {
          paramBoolean1 = this.mDestroyingServices.contains(paramServiceRecord);
          serviceDoneExecutingLocked(paramServiceRecord, paramBoolean1, paramBoolean1);
          if ((localObject1 instanceof TransactionTooLargeException)) {
            throw ((TransactionTooLargeException)localObject1);
          }
        }
      }
    }
    label743:
    label754:
    return;
  }
  
  private void serviceDoneExecutingLocked(ServiceRecord paramServiceRecord, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (ActivityManagerDebugConfig.DEBUG_SERVICE)
    {
      Slog.v(TAG_SERVICE, "<<< DONE EXECUTING " + paramServiceRecord + ": nesting=" + paramServiceRecord.executeNesting + ", inDestroying=" + paramBoolean1 + ", app=" + paramServiceRecord.app);
      paramServiceRecord.executeNesting -= 1;
      if (paramServiceRecord.executeNesting <= 0)
      {
        if (paramServiceRecord.app != null)
        {
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "Nesting at 0 of " + paramServiceRecord.shortName);
          }
          paramServiceRecord.app.execServicesFg = false;
          paramServiceRecord.app.executingServices.remove(paramServiceRecord);
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG, "r.app.executingServices.size(): " + paramServiceRecord.app.executingServices.size());
          }
          if (paramServiceRecord.app.executingServices.size() != 0) {
            break label475;
          }
          if ((ActivityManagerDebugConfig.DEBUG_SERVICE) || (ActivityManagerDebugConfig.DEBUG_SERVICE_EXECUTING)) {
            Slog.v(TAG_SERVICE_EXECUTING, "No more executingServices of " + paramServiceRecord.shortName);
          }
          this.mAm.mHandler.removeMessages(12, paramServiceRecord.app);
          if ((paramServiceRecord != null) && (paramServiceRecord.app != null)) {
            OnePlusProcessManager.continueSuspendUid(paramServiceRecord.app.uid);
          }
          label283:
          if (paramBoolean1)
          {
            if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
              Slog.v(TAG_SERVICE, "doneExecuting remove destroying " + paramServiceRecord);
            }
            this.mDestroyingServices.remove(paramServiceRecord);
            paramServiceRecord.bindings.clear();
          }
          this.mAm.updateOomAdjLocked(paramServiceRecord.app);
        }
        paramServiceRecord.executeFg = false;
        if (paramServiceRecord.tracker != null)
        {
          paramServiceRecord.tracker.setExecuting(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
          if (paramBoolean2)
          {
            paramServiceRecord.tracker.clearCurrentOwner(paramServiceRecord, false);
            paramServiceRecord.tracker = null;
          }
        }
        if (paramBoolean2) {
          if ((paramServiceRecord.app != null) && (!paramServiceRecord.app.persistent)) {
            break label542;
          }
        }
      }
    }
    for (;;)
    {
      paramServiceRecord.app = null;
      paramServiceRecord.relativeRestartCount = 0;
      paramServiceRecord.relativeRestartTime = 0L;
      return;
      if (!ActivityManagerDebugConfig.DEBUG_SERVICE_EXECUTING) {
        break;
      }
      Slog.v(TAG_SERVICE_EXECUTING, "<<< DONE EXECUTING " + paramServiceRecord.shortName);
      break;
      label475:
      if (!paramServiceRecord.executeFg) {
        break label283;
      }
      int i = paramServiceRecord.app.executingServices.size() - 1;
      for (;;)
      {
        if (i < 0) {
          break label540;
        }
        if (((ServiceRecord)paramServiceRecord.app.executingServices.valueAt(i)).executeFg)
        {
          paramServiceRecord.app.execServicesFg = true;
          break;
        }
        i -= 1;
      }
      label540:
      break label283;
      label542:
      paramServiceRecord.app.services.remove(paramServiceRecord);
      if (paramServiceRecord.whitelistManager) {
        updateWhitelistManagerLocked(paramServiceRecord.app);
      }
    }
  }
  
  private void serviceProcessGoneLocked(ServiceRecord paramServiceRecord)
  {
    if (paramServiceRecord.tracker != null)
    {
      int i = this.mAm.mProcessStats.getMemFactorLocked();
      long l = SystemClock.uptimeMillis();
      paramServiceRecord.tracker.setExecuting(false, i, l);
      paramServiceRecord.tracker.setBound(false, i, l);
      paramServiceRecord.tracker.setStarted(false, i, l);
    }
    serviceDoneExecutingLocked(paramServiceRecord, true, true);
  }
  
  private void stopServiceLocked(ServiceRecord paramServiceRecord)
  {
    if (paramServiceRecord.delayed)
    {
      if (DEBUG_DELAYED_STARTS) {
        Slog.v(TAG_SERVICE, "Delaying stop of pending: " + paramServiceRecord);
      }
      paramServiceRecord.delayedStop = true;
      return;
    }
    synchronized (paramServiceRecord.stats.getBatteryStats())
    {
      paramServiceRecord.stats.stopRunningLocked();
      paramServiceRecord.startRequested = false;
      if (paramServiceRecord.tracker != null) {
        paramServiceRecord.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
      }
      paramServiceRecord.callStart = false;
      bringDownServiceIfNeededLocked(paramServiceRecord, false, false);
      return;
    }
  }
  
  private final boolean unscheduleServiceRestartLocked(ServiceRecord paramServiceRecord, int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) && (paramServiceRecord.restartDelay == 0L)) {
      return false;
    }
    paramBoolean = this.mRestartingServices.remove(paramServiceRecord);
    if ((paramBoolean) || (paramInt != paramServiceRecord.appInfo.uid)) {
      paramServiceRecord.resetRestartCounter();
    }
    if (paramBoolean) {
      clearRestartingIfNeededLocked(paramServiceRecord);
    }
    this.mAm.mHandler.removeCallbacks(paramServiceRecord.restarter);
    return true;
  }
  
  private boolean updateServiceClientActivitiesLocked(ProcessRecord paramProcessRecord, ConnectionRecord paramConnectionRecord, boolean paramBoolean)
  {
    if ((paramConnectionRecord != null) && (paramConnectionRecord.binding.client != null) && (paramConnectionRecord.binding.client.activities.size() <= 0)) {
      return false;
    }
    boolean bool1 = false;
    int i = paramProcessRecord.services.size() - 1;
    if ((i < 0) || (bool1))
    {
      if (bool1 != paramProcessRecord.hasClientActivities)
      {
        paramProcessRecord.hasClientActivities = bool1;
        if (paramBoolean) {
          this.mAm.updateLruProcessLocked(paramProcessRecord, bool1, null);
        }
        return true;
      }
    }
    else
    {
      paramConnectionRecord = (ServiceRecord)paramProcessRecord.services.valueAt(i);
      int j = paramConnectionRecord.connections.size() - 1;
      for (;;)
      {
        if ((j < 0) || (bool1))
        {
          i -= 1;
          break;
        }
        ArrayList localArrayList = (ArrayList)paramConnectionRecord.connections.valueAt(j);
        int k = localArrayList.size() - 1;
        boolean bool2 = bool1;
        if (k >= 0)
        {
          ConnectionRecord localConnectionRecord = (ConnectionRecord)localArrayList.get(k);
          if ((localConnectionRecord.binding.client == null) || (localConnectionRecord.binding.client == paramProcessRecord)) {}
          while (localConnectionRecord.binding.client.activities.size() <= 0)
          {
            k -= 1;
            break;
          }
          bool2 = true;
        }
        j -= 1;
        bool1 = bool2;
      }
    }
    return false;
  }
  
  private void updateServiceForegroundLocked(ProcessRecord paramProcessRecord, boolean paramBoolean)
  {
    boolean bool2 = false;
    int i = paramProcessRecord.services.size() - 1;
    for (;;)
    {
      boolean bool1 = bool2;
      if (i >= 0)
      {
        if (((ServiceRecord)paramProcessRecord.services.valueAt(i)).isForeground) {
          bool1 = true;
        }
      }
      else
      {
        this.mAm.updateProcessForegroundLocked(paramProcessRecord, bool1, paramBoolean);
        return;
      }
      i -= 1;
    }
  }
  
  private void updateWhitelistManagerLocked(ProcessRecord paramProcessRecord)
  {
    paramProcessRecord.whitelistManager = false;
    int i = paramProcessRecord.services.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        if (((ServiceRecord)paramProcessRecord.services.valueAt(i)).whitelistManager) {
          paramProcessRecord.whitelistManager = true;
        }
      }
      else {
        return;
      }
      i -= 1;
    }
  }
  
  boolean attachApplicationLocked(ProcessRecord paramProcessRecord, String paramString)
    throws RemoteException
  {
    boolean bool2 = false;
    boolean bool1 = false;
    ServiceRecord localServiceRecord1;
    int i;
    if (this.mPendingServices.size() > 0)
    {
      localServiceRecord1 = null;
      i = 0;
      for (;;)
      {
        ServiceRecord localServiceRecord2 = localServiceRecord1;
        bool2 = bool1;
        try
        {
          if (i < this.mPendingServices.size())
          {
            localServiceRecord2 = localServiceRecord1;
            localServiceRecord1 = (ServiceRecord)this.mPendingServices.get(i);
            localServiceRecord2 = localServiceRecord1;
            int j;
            if (paramProcessRecord != localServiceRecord1.isolatedProc)
            {
              bool2 = bool1;
              j = i;
              localServiceRecord2 = localServiceRecord1;
              if (paramProcessRecord.uid == localServiceRecord1.appInfo.uid)
              {
                bool2 = bool1;
                j = i;
                localServiceRecord2 = localServiceRecord1;
                if (!paramString.equals(localServiceRecord1.processName)) {}
              }
            }
            else
            {
              localServiceRecord2 = localServiceRecord1;
              this.mPendingServices.remove(i);
              i -= 1;
              localServiceRecord2 = localServiceRecord1;
              paramProcessRecord.addPackage(localServiceRecord1.appInfo.packageName, localServiceRecord1.appInfo.versionCode, this.mAm.mProcessStats);
              localServiceRecord2 = localServiceRecord1;
              realStartServiceLocked(localServiceRecord1, paramProcessRecord, localServiceRecord1.createdFromFg);
              bool1 = true;
              bool2 = bool1;
              j = i;
              localServiceRecord2 = localServiceRecord1;
              if (!isServiceNeeded(localServiceRecord1, false, false))
              {
                localServiceRecord2 = localServiceRecord1;
                bringDownServiceLocked(localServiceRecord1);
                j = i;
                bool2 = bool1;
              }
            }
            i = j + 1;
            bool1 = bool2;
          }
        }
        catch (RemoteException paramProcessRecord)
        {
          Slog.w(TAG, "Exception in new application when starting service " + localServiceRecord2.shortName, paramProcessRecord);
          throw paramProcessRecord;
        }
      }
    }
    if (this.mRestartingServices.size() > 0)
    {
      i = 0;
      while (i < this.mRestartingServices.size())
      {
        localServiceRecord1 = (ServiceRecord)this.mRestartingServices.get(i);
        if ((paramProcessRecord == localServiceRecord1.isolatedProc) || ((paramProcessRecord.uid == localServiceRecord1.appInfo.uid) && (paramString.equals(localServiceRecord1.processName))))
        {
          this.mAm.mHandler.removeCallbacks(localServiceRecord1.restarter);
          this.mAm.mHandler.post(localServiceRecord1.restarter);
        }
        i += 1;
      }
    }
    return bool2;
  }
  
  int bindServiceLocked(IApplicationThread paramIApplicationThread, final IBinder paramIBinder, final Intent paramIntent, String paramString1, final IServiceConnection paramIServiceConnection, int paramInt1, String paramString2, final int paramInt2)
    throws TransactionTooLargeException
  {
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "bindService: " + paramIntent + " type=" + paramString1 + " conn=" + paramIServiceConnection.asBinder() + " flags=0x" + Integer.toHexString(paramInt1));
    }
    ProcessRecord localProcessRecord = this.mAm.getRecordForAppLocked(paramIApplicationThread);
    if (localProcessRecord == null) {
      throw new SecurityException("Unable to find app for caller " + paramIApplicationThread + " (pid=" + Binder.getCallingPid() + ") when binding service " + paramIntent);
    }
    Object localObject1 = null;
    final Object localObject2;
    if (paramIBinder != null)
    {
      localObject2 = ActivityRecord.isInStackLocked(paramIBinder);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        Slog.w(TAG, "Binding with unknown activity: " + paramIBinder);
        return 0;
      }
    }
    int k = 0;
    Object localObject3 = null;
    int i;
    int j;
    final boolean bool1;
    if (localProcessRecord.info.uid == 1000)
    {
      i = 1;
      j = k;
      localObject2 = paramIntent;
      if (i != 0)
      {
        paramIntent.setDefusable(true);
        PendingIntent localPendingIntent = (PendingIntent)paramIntent.getParcelableExtra("android.intent.extra.client_intent");
        localObject3 = localPendingIntent;
        j = k;
        localObject2 = paramIntent;
        if (localPendingIntent != null)
        {
          k = paramIntent.getIntExtra("android.intent.extra.client_label", 0);
          localObject3 = localPendingIntent;
          j = k;
          localObject2 = paramIntent;
          if (k != 0)
          {
            localObject2 = paramIntent.cloneFilter();
            j = k;
            localObject3 = localPendingIntent;
          }
        }
      }
      if ((0x8000000 & paramInt1) != 0) {
        this.mAm.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_STACKS", "BIND_TREAT_LIKE_ACTIVITY");
      }
      if (((0x1000000 & paramInt1) != 0) && (i == 0)) {
        break label393;
      }
      if (localProcessRecord.setSchedGroup == 0) {
        break label444;
      }
      bool1 = true;
      label345:
      if ((0x80000000 & paramInt1) == 0) {
        break label450;
      }
    }
    label393:
    label444:
    label450:
    for (boolean bool2 = true;; bool2 = false)
    {
      paramIApplicationThread = retrieveServiceLocked((Intent)localObject2, paramString1, paramString2, Binder.getCallingPid(), Binder.getCallingUid(), paramInt2, true, bool1, bool2);
      if (paramIApplicationThread != null) {
        break label456;
      }
      return 0;
      i = 0;
      break;
      throw new SecurityException("Non-system caller " + paramIApplicationThread + " (pid=" + Binder.getCallingPid() + ") set BIND_ALLOW_WHITELIST_MANAGEMENT when binding service " + localObject2);
      bool1 = false;
      break label345;
    }
    label456:
    if (paramIApplicationThread.record == null) {
      return -1;
    }
    paramIntent = paramIApplicationThread.record;
    if (OnePlusAppBootManager.DEBUG) {
      OnePlusAppBootManager.myLog(" bindServiceLocked # callerApp= " + localProcessRecord + ", service = " + localObject2 + ", resolvedType=" + paramString1 + ", callingPackage=" + paramString2 + ", token=" + paramIBinder);
    }
    if ((OnePlusAppBootManager.IN_USING) && (!OnePlusAppBootManager.getInstance(null).canServiceGo(localProcessRecord, (Intent)localObject2, paramIntent, 0, paramString2))) {
      return 0;
    }
    boolean bool3 = false;
    bool2 = bool3;
    if (Build.PERMISSIONS_REVIEW_REQUIRED)
    {
      bool2 = bool3;
      if (this.mAm.getPackageManagerInternalLocked().isPermissionsReviewRequired(paramIntent.packageName, paramIntent.userId))
      {
        bool2 = true;
        if (!bool1)
        {
          Slog.w(TAG, "u" + paramIntent.userId + " Binding to a service in package" + paramIntent.packageName + " requires a permissions review");
          return 0;
        }
        paramIApplicationThread = new RemoteCallback(new RemoteCallback.OnResultListener()
        {
          /* Error */
          public void onResult(android.os.Bundle paramAnonymousBundle)
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 25	com/android/server/am/ActiveServices$3:this$0	Lcom/android/server/am/ActiveServices;
            //   4: getfield 45	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
            //   7: astore_1
            //   8: aload_1
            //   9: monitorenter
            //   10: invokestatic 50	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
            //   13: invokestatic 56	android/os/Binder:clearCallingIdentity	()J
            //   16: lstore_2
            //   17: aload_0
            //   18: getfield 25	com/android/server/am/ActiveServices$3:this$0	Lcom/android/server/am/ActiveServices;
            //   21: getfield 60	com/android/server/am/ActiveServices:mPendingServices	Ljava/util/ArrayList;
            //   24: aload_0
            //   25: getfield 27	com/android/server/am/ActiveServices$3:val$serviceRecord	Lcom/android/server/am/ServiceRecord;
            //   28: invokevirtual 66	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
            //   31: istore 4
            //   33: iload 4
            //   35: ifne +13 -> 48
            //   38: lload_2
            //   39: invokestatic 70	android/os/Binder:restoreCallingIdentity	(J)V
            //   42: aload_1
            //   43: monitorexit
            //   44: invokestatic 73	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
            //   47: return
            //   48: aload_0
            //   49: getfield 25	com/android/server/am/ActiveServices$3:this$0	Lcom/android/server/am/ActiveServices;
            //   52: getfield 45	com/android/server/am/ActiveServices:mAm	Lcom/android/server/am/ActivityManagerService;
            //   55: invokevirtual 77	com/android/server/am/ActivityManagerService:getPackageManagerInternalLocked	()Landroid/content/pm/PackageManagerInternal;
            //   58: aload_0
            //   59: getfield 27	com/android/server/am/ActiveServices$3:val$serviceRecord	Lcom/android/server/am/ServiceRecord;
            //   62: getfield 83	com/android/server/am/ServiceRecord:packageName	Ljava/lang/String;
            //   65: aload_0
            //   66: getfield 27	com/android/server/am/ActiveServices$3:val$serviceRecord	Lcom/android/server/am/ServiceRecord;
            //   69: getfield 87	com/android/server/am/ServiceRecord:userId	I
            //   72: invokevirtual 93	android/content/pm/PackageManagerInternal:isPermissionsReviewRequired	(Ljava/lang/String;I)Z
            //   75: istore 4
            //   77: iload 4
            //   79: ifne +38 -> 117
            //   82: aload_0
            //   83: getfield 25	com/android/server/am/ActiveServices$3:this$0	Lcom/android/server/am/ActiveServices;
            //   86: aload_0
            //   87: getfield 27	com/android/server/am/ActiveServices$3:val$serviceRecord	Lcom/android/server/am/ServiceRecord;
            //   90: aload_0
            //   91: getfield 29	com/android/server/am/ActiveServices$3:val$serviceIntent	Landroid/content/Intent;
            //   94: invokevirtual 99	android/content/Intent:getFlags	()I
            //   97: aload_0
            //   98: getfield 31	com/android/server/am/ActiveServices$3:val$callerFg	Z
            //   101: iconst_0
            //   102: iconst_0
            //   103: invokestatic 103	com/android/server/am/ActiveServices:-wrap1	(Lcom/android/server/am/ActiveServices;Lcom/android/server/am/ServiceRecord;IZZZ)Ljava/lang/String;
            //   106: pop
            //   107: lload_2
            //   108: invokestatic 70	android/os/Binder:restoreCallingIdentity	(J)V
            //   111: aload_1
            //   112: monitorexit
            //   113: invokestatic 73	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
            //   116: return
            //   117: aload_0
            //   118: getfield 25	com/android/server/am/ActiveServices$3:this$0	Lcom/android/server/am/ActiveServices;
            //   121: aload_0
            //   122: getfield 33	com/android/server/am/ActiveServices$3:val$connection	Landroid/app/IServiceConnection;
            //   125: invokevirtual 107	com/android/server/am/ActiveServices:unbindServiceLocked	(Landroid/app/IServiceConnection;)Z
            //   128: pop
            //   129: goto -22 -> 107
            //   132: astore 5
            //   134: lload_2
            //   135: invokestatic 70	android/os/Binder:restoreCallingIdentity	(J)V
            //   138: aload 5
            //   140: athrow
            //   141: astore 5
            //   143: aload_1
            //   144: monitorexit
            //   145: invokestatic 73	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
            //   148: aload 5
            //   150: athrow
            //   151: astore 5
            //   153: goto -46 -> 107
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	156	0	this	3
            //   0	156	1	paramAnonymousBundle	android.os.Bundle
            //   16	119	2	l	long
            //   31	47	4	bool	boolean
            //   132	7	5	localObject1	Object
            //   141	8	5	localObject2	Object
            //   151	1	5	localRemoteException	RemoteException
            // Exception table:
            //   from	to	target	type
            //   17	33	132	finally
            //   48	77	132	finally
            //   82	107	132	finally
            //   117	129	132	finally
            //   10	17	141	finally
            //   38	42	141	finally
            //   107	111	141	finally
            //   134	141	141	finally
            //   82	107	151	android/os/RemoteException
          }
        });
        paramIBinder = new Intent("android.intent.action.REVIEW_PERMISSIONS");
        paramIBinder.addFlags(276824064);
        paramIBinder.putExtra("android.intent.extra.PACKAGE_NAME", paramIntent.packageName);
        paramIBinder.putExtra("android.intent.extra.REMOTE_CALLBACK", paramIApplicationThread);
        if (ActivityManagerDebugConfig.DEBUG_PERMISSIONS_REVIEW) {
          Slog.i(TAG, "u" + paramIntent.userId + " Launching permission review for package " + paramIntent.packageName);
        }
        this.mAm.mHandler.post(new Runnable()
        {
          public void run()
          {
            ActiveServices.this.mAm.mContext.startActivityAsUser(paramIBinder, new UserHandle(paramInt2));
          }
        });
      }
    }
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        if ((unscheduleServiceRestartLocked(paramIntent, localProcessRecord.info.uid, false)) && (ActivityManagerDebugConfig.DEBUG_SERVICE)) {
          Slog.v(TAG_SERVICE, "BIND SERVICE WHILE RESTART PENDING: " + paramIntent);
        }
        if ((paramInt1 & 0x1) != 0)
        {
          paramIntent.lastActivity = SystemClock.uptimeMillis();
          if (!paramIntent.hasAutoCreateConnections())
          {
            paramIApplicationThread = paramIntent.getTracker();
            if (paramIApplicationThread != null) {
              paramIApplicationThread.setBound(true, this.mAm.mProcessStats.getMemFactorLocked(), paramIntent.lastActivity);
            }
          }
        }
        this.mAm.startAssociationLocked(localProcessRecord.uid, localProcessRecord.processName, localProcessRecord.curProcState, paramIntent.appInfo.uid, paramIntent.name, paramIntent.processName);
        paramString1 = paramIntent.retrieveAppBindingLocked((Intent)localObject2, localProcessRecord);
        paramString2 = new ConnectionRecord(paramString1, (ActivityRecord)localObject1, paramIServiceConnection, paramInt1, j, (PendingIntent)localObject3);
        paramIServiceConnection = paramIServiceConnection.asBinder();
        paramIBinder = (ArrayList)paramIntent.connections.get(paramIServiceConnection);
        paramIApplicationThread = paramIBinder;
        if (paramIBinder == null)
        {
          paramIApplicationThread = new ArrayList();
          paramIntent.connections.put(paramIServiceConnection, paramIApplicationThread);
        }
        paramIApplicationThread.add(paramString2);
        paramString1.connections.add(paramString2);
        if (localObject1 != null)
        {
          if (((ActivityRecord)localObject1).connections == null) {
            ((ActivityRecord)localObject1).connections = new HashSet();
          }
          ((ActivityRecord)localObject1).connections.add(paramString2);
        }
        paramString1.client.connections.add(paramString2);
        if (paramIntent.appInfo != null) {
          OnePlusProcessManager.resumeProcessByUID_out(paramIntent.appInfo.uid, "addConnection = " + paramString2.binding);
        }
        if ((paramString2.flags & 0x8) != 0) {
          paramString1.client.hasAboveClient = true;
        }
        if ((paramString2.flags & 0x1000000) != 0) {
          paramIntent.whitelistManager = true;
        }
        if (paramIntent.app != null) {
          updateServiceClientActivitiesLocked(paramIntent.app, paramString2, true);
        }
        paramIBinder = (ArrayList)this.mServiceConnections.get(paramIServiceConnection);
        paramIApplicationThread = paramIBinder;
        if (paramIBinder == null)
        {
          paramIApplicationThread = new ArrayList();
          this.mServiceConnections.put(paramIServiceConnection, paramIApplicationThread);
        }
        paramIApplicationThread.add(paramString2);
        if ((paramInt1 & 0x1) != 0)
        {
          paramIntent.lastActivity = SystemClock.uptimeMillis();
          paramIApplicationThread = bringUpServiceLocked(paramIntent, ((Intent)localObject2).getFlags(), bool1, false, bool2);
          if (paramIApplicationThread != null) {
            return 0;
          }
        }
        if (paramIntent.app != null)
        {
          if ((0x8000000 & paramInt1) != 0) {
            paramIntent.app.treatLikeActivity = true;
          }
          if (paramIntent.whitelistManager) {
            paramIntent.app.whitelistManager = true;
          }
          paramIApplicationThread = this.mAm;
          paramIBinder = paramIntent.app;
          if (!paramIntent.app.hasClientActivities)
          {
            bool2 = paramIntent.app.treatLikeActivity;
            paramIApplicationThread.updateLruProcessLocked(paramIBinder, bool2, paramString1.client);
            this.mAm.updateOomAdjLocked(paramIntent.app);
          }
        }
        else
        {
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "Bind " + paramIntent + " with " + paramString1 + ": received=" + paramString1.intent.received + " apps=" + paramString1.intent.apps.size() + " doRebind=" + paramString1.intent.doRebind);
          }
          if (paramIntent.app == null) {
            break label1699;
          }
          bool2 = paramString1.intent.received;
          if (!bool2) {
            break label1699;
          }
        }
        try
        {
          if (paramIntent.appInfo != null) {
            OnePlusProcessManager.resumeProcessByUID_out(paramIntent.appInfo.uid, "addConnection = " + paramString2.binding);
          }
          paramString2.conn.connected(paramIntent.name, paramString1.intent.binder);
        }
        catch (Exception paramIApplicationThread)
        {
          Slog.w(TAG, "Failure sending service " + paramIntent.shortName + " to connection " + paramString2.conn.asBinder() + " (in " + paramString2.binding.client.processName + ")", paramIApplicationThread);
          continue;
        }
        if ((paramString1.intent.apps.size() == 1) && (paramString1.intent.doRebind)) {
          requestServiceBindingLocked(paramIntent, paramString1.intent, bool1, true);
        }
        getServiceMap(paramIntent.userId).ensureNotStartingBackground(paramIntent);
        return 1;
        bool2 = true;
        continue;
        if (paramString1.intent.requested) {
          continue;
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      label1699:
      requestServiceBindingLocked(paramIntent, paramString1.intent, bool1, false);
    }
  }
  
  boolean bringDownDisabledPackageServicesLocked(String paramString, Set<String> paramSet, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    bool2 = false;
    boolean bool1 = false;
    if (this.mTmpCollectionResults != null) {
      this.mTmpCollectionResults.clear();
    }
    if (paramInt == -1)
    {
      paramInt = this.mServiceMap.size() - 1;
      for (;;)
      {
        bool2 = bool1;
        if (paramInt < 0) {
          break;
        }
        bool1 |= collectPackageServicesLocked(paramString, paramSet, paramBoolean1, paramBoolean3, paramBoolean2, ((ServiceMap)this.mServiceMap.valueAt(paramInt)).mServicesByName);
        if ((!paramBoolean3) && (bool1)) {
          return true;
        }
        paramInt -= 1;
      }
    }
    ServiceMap localServiceMap = (ServiceMap)this.mServiceMap.get(paramInt);
    if (localServiceMap != null) {
      bool2 = collectPackageServicesLocked(paramString, paramSet, paramBoolean1, paramBoolean3, paramBoolean2, localServiceMap.mServicesByName);
    }
    if (this.mTmpCollectionResults != null) {
      try
      {
        paramInt = this.mTmpCollectionResults.size() - 1;
        while (paramInt >= 0)
        {
          bringDownServiceLocked((ServiceRecord)this.mTmpCollectionResults.get(paramInt));
          paramInt -= 1;
        }
        return bool2;
      }
      catch (IndexOutOfBoundsException paramSet)
      {
        Slog.i(TAG, "catch IndexOutOfBoundsException pkg " + paramString, paramSet);
        this.mTmpCollectionResults.clear();
      }
    }
  }
  
  void cleanUpRemovedTaskLocked(TaskRecord paramTaskRecord, ComponentName paramComponentName, Intent paramIntent)
  {
    ArrayList localArrayList = new ArrayList();
    paramTaskRecord = getServices(paramTaskRecord.userId);
    int i = paramTaskRecord.size() - 1;
    while (i >= 0)
    {
      ServiceRecord localServiceRecord = (ServiceRecord)paramTaskRecord.valueAt(i);
      if (localServiceRecord.packageName.equals(paramComponentName.getPackageName())) {
        localArrayList.add(localServiceRecord);
      }
      i -= 1;
    }
    i = localArrayList.size() - 1;
    if (i >= 0)
    {
      paramTaskRecord = (ServiceRecord)localArrayList.get(i);
      if (paramTaskRecord.startRequested)
      {
        if ((paramTaskRecord.serviceInfo.flags & 0x1) == 0) {
          break label168;
        }
        Slog.i(TAG, "Stopping service " + paramTaskRecord.shortName + ": remove task");
        stopServiceLocked(paramTaskRecord);
      }
      for (;;)
      {
        i -= 1;
        break;
        label168:
        paramTaskRecord.pendingStarts.add(new ServiceRecord.StartItem(paramTaskRecord, true, paramTaskRecord.makeNextStartId(), paramIntent, null));
        if ((paramTaskRecord.app != null) && (paramTaskRecord.app.thread != null)) {
          try
          {
            sendServiceArgsLocked(paramTaskRecord, true, false);
          }
          catch (TransactionTooLargeException paramTaskRecord) {}
        }
      }
    }
  }
  
  void clearPendingServiceForUid(int paramInt)
  {
    int i = 0;
    while (i < this.mPendingServices.size())
    {
      ServiceRecord localServiceRecord = (ServiceRecord)this.mPendingServices.get(i);
      if ((localServiceRecord != null) && (paramInt == localServiceRecord.appInfo.uid))
      {
        Slog.w(TAG, "clearPendingServiceForUid: " + localServiceRecord);
        if ((localServiceRecord.app != null) && (localServiceRecord.app.executingServices != null))
        {
          localServiceRecord.app.executingServices.clear();
          this.mAm.mHandler.removeMessages(12, localServiceRecord.app);
        }
      }
      i += 1;
    }
  }
  
  List<ServiceRecord> collectServicesToDumpLocked(ActivityManagerService.ItemMatcher paramItemMatcher, String paramString)
  {
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt = this.mAm.mUserController.getUsers();
    int k = arrayOfInt.length;
    while (i < k)
    {
      ServiceMap localServiceMap = getServiceMap(arrayOfInt[i]);
      if (localServiceMap.mServicesByName.size() > 0)
      {
        int j = 0;
        if (j < localServiceMap.mServicesByName.size())
        {
          ServiceRecord localServiceRecord = (ServiceRecord)localServiceMap.mServicesByName.valueAt(j);
          if (!paramItemMatcher.match(localServiceRecord, localServiceRecord.name)) {}
          for (;;)
          {
            j += 1;
            break;
            if ((paramString == null) || (paramString.equals(localServiceRecord.appInfo.packageName))) {
              localArrayList.add(localServiceRecord);
            }
          }
        }
      }
      i += 1;
    }
    return localArrayList;
  }
  
  protected boolean dumpService(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      synchronized (this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int[] arrayOfInt = this.mAm.mUserController.getUsers();
        if ("all".equals(paramString))
        {
          paramInt = 0;
          j = arrayOfInt.length;
          if (paramInt >= j) {
            break label330;
          }
          i = arrayOfInt[paramInt];
          paramString = (ServiceMap)this.mServiceMap.get(i);
          if (paramString == null) {
            break label408;
          }
          paramString = paramString.mServicesByName;
          i = 0;
          if (i >= paramString.size()) {
            break label408;
          }
          localArrayList.add((ServiceRecord)paramString.valueAt(i));
          i += 1;
          continue;
        }
        if (paramString == null) {
          break label426;
        }
        localObject1 = ComponentName.unflattenFromString(paramString);
        i = 0;
        localObject2 = localObject1;
        paramInt = i;
        String str1 = paramString;
        if (localObject1 == null) {}
        try
        {
          paramInt = Integer.parseInt(paramString, 16);
          str1 = null;
          localObject2 = null;
        }
        catch (RuntimeException localRuntimeException)
        {
          int k;
          localObject2 = localObject1;
          paramInt = i;
          String str2 = paramString;
          continue;
          j += 1;
          continue;
        }
        i = 0;
        k = arrayOfInt.length;
        if (i >= k) {
          break label330;
        }
        j = arrayOfInt[i];
        paramString = (ServiceMap)this.mServiceMap.get(j);
        if (paramString == null) {
          break label417;
        }
        paramString = paramString.mServicesByName;
        j = 0;
        if (j >= paramString.size()) {
          break label417;
        }
        localObject1 = (ServiceRecord)paramString.valueAt(j);
        if (localObject2 != null)
        {
          if (((ServiceRecord)localObject1).name.equals(localObject2)) {
            localArrayList.add(localObject1);
          }
        }
        else if (str1 != null)
        {
          if (!((ServiceRecord)localObject1).name.flattenToString().contains(str1)) {
            break;
          }
          localArrayList.add(localObject1);
        }
      }
      if (System.identityHashCode(localObject1) != paramInt) {
        break;
      }
      localArrayList.add(localObject1);
      break;
      label330:
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (localArrayList.size() <= 0) {
        return false;
      }
      i = 0;
      paramInt = 0;
      while (paramInt < localArrayList.size())
      {
        if (i != 0) {
          paramPrintWriter.println();
        }
        i = 1;
        dumpService("", paramFileDescriptor, paramPrintWriter, (ServiceRecord)localArrayList.get(paramInt), paramArrayOfString, paramBoolean);
        paramInt += 1;
      }
      return true;
      label408:
      paramInt += 1;
      continue;
      label417:
      i += 1;
      continue;
      label426:
      localObject1 = null;
    }
  }
  
  public void dynamicallyUpdateLogTag(boolean paramBoolean)
  {
    DEBUG_DELAYED_SERVICE = paramBoolean;
    DEBUG_DELAYED_STARTS = paramBoolean;
    LOG_SERVICE_START_STOP = paramBoolean;
  }
  
  public PendingIntent getRunningServiceControlPanelLocked(ComponentName paramComponentName)
  {
    paramComponentName = getServiceByName(paramComponentName, UserHandle.getUserId(Binder.getCallingUid()));
    if (paramComponentName != null)
    {
      int i = paramComponentName.connections.size() - 1;
      while (i >= 0)
      {
        ArrayList localArrayList = (ArrayList)paramComponentName.connections.valueAt(i);
        int j = 0;
        while (j < localArrayList.size())
        {
          if (((ConnectionRecord)localArrayList.get(j)).clientIntent != null) {
            return ((ConnectionRecord)localArrayList.get(j)).clientIntent;
          }
          j += 1;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  List<ActivityManager.RunningServiceInfo> getRunningServiceInfoLocked(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    paramInt2 = Binder.getCallingUid();
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        Object localObject2;
        Object localObject3;
        int i;
        if (ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", paramInt2) == 0)
        {
          localObject2 = this.mAm.mUserController.getUsers();
          paramInt2 = 0;
          if ((paramInt2 >= localObject2.length) || (localArrayList.size() >= paramInt1)) {
            break label338;
          }
          localObject3 = getServices(localObject2[paramInt2]);
          i = 0;
          if ((i >= ((ArrayMap)localObject3).size()) || (localArrayList.size() >= paramInt1)) {
            break label331;
          }
          localArrayList.add(makeRunningServiceInfoLocked((ServiceRecord)((ArrayMap)localObject3).valueAt(i)));
          i += 1;
          continue;
          if ((paramInt2 < this.mRestartingServices.size()) && (localArrayList.size() < paramInt1))
          {
            localObject2 = (ServiceRecord)this.mRestartingServices.get(paramInt2);
            localObject3 = makeRunningServiceInfoLocked((ServiceRecord)localObject2);
            ((ActivityManager.RunningServiceInfo)localObject3).restarting = ((ServiceRecord)localObject2).nextRestartTime;
            localArrayList.add(localObject3);
            paramInt2 += 1;
            continue;
          }
        }
        else
        {
          i = UserHandle.getUserId(paramInt2);
          localObject2 = getServices(i);
          paramInt2 = 0;
          if ((paramInt2 >= ((ArrayMap)localObject2).size()) || (localArrayList.size() >= paramInt1)) {
            break label343;
          }
          localArrayList.add(makeRunningServiceInfoLocked((ServiceRecord)((ArrayMap)localObject2).valueAt(paramInt2)));
          paramInt2 += 1;
          continue;
          if ((paramInt2 < this.mRestartingServices.size()) && (localArrayList.size() < paramInt1))
          {
            localObject2 = (ServiceRecord)this.mRestartingServices.get(paramInt2);
            if (((ServiceRecord)localObject2).userId == i)
            {
              localObject3 = makeRunningServiceInfoLocked((ServiceRecord)localObject2);
              ((ActivityManager.RunningServiceInfo)localObject3).restarting = ((ServiceRecord)localObject2).nextRestartTime;
              localArrayList.add(localObject3);
            }
            paramInt2 += 1;
            continue;
          }
        }
        return localArrayList;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      label331:
      paramInt2 += 1;
      continue;
      label338:
      paramInt2 = 0;
      continue;
      label343:
      paramInt2 = 0;
    }
  }
  
  ServiceRecord getServiceByName(ComponentName paramComponentName, int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_MU) {
      Slog.v(TAG_MU, "getServiceByName(" + paramComponentName + "), callingUser = " + paramInt);
    }
    return (ServiceRecord)getServiceMap(paramInt).mServicesByName.get(paramComponentName);
  }
  
  ArrayMap<ComponentName, ServiceRecord> getServices(int paramInt)
  {
    return getServiceMap(paramInt).mServicesByName;
  }
  
  boolean hasBackgroundServices(int paramInt)
  {
    boolean bool2 = false;
    ServiceMap localServiceMap = (ServiceMap)this.mServiceMap.get(paramInt);
    boolean bool1 = bool2;
    if (localServiceMap != null)
    {
      bool1 = bool2;
      if (localServiceMap.mStartingBackground.size() >= this.mMaxStartingBackground) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  final void killServicesLocked(ProcessRecord paramProcessRecord, boolean paramBoolean)
  {
    int i = paramProcessRecord.connections.size() - 1;
    while (i >= 0)
    {
      removeConnectionLocked((ConnectionRecord)paramProcessRecord.connections.valueAt(i), paramProcessRecord, null);
      i -= 1;
    }
    updateServiceConnectionActivitiesLocked(paramProcessRecord);
    paramProcessRecord.connections.clear();
    paramProcessRecord.whitelistManager = false;
    i = paramProcessRecord.services.size() - 1;
    int j;
    Object localObject3;
    while (i >= 0)
    {
      localObject1 = (ServiceRecord)paramProcessRecord.services.valueAt(i);
      for (;;)
      {
        int k;
        synchronized (((ServiceRecord)localObject1).stats.getBatteryStats())
        {
          ((ServiceRecord)localObject1).stats.stopLaunchedLocked();
          if ((((ServiceRecord)localObject1).app == paramProcessRecord) || (((ServiceRecord)localObject1).app == null) || (((ServiceRecord)localObject1).app.persistent))
          {
            ((ServiceRecord)localObject1).app = null;
            ((ServiceRecord)localObject1).isolatedProc = null;
            ((ServiceRecord)localObject1).executeNesting = 0;
            ((ServiceRecord)localObject1).forceClearTracker();
            if ((this.mDestroyingServices.remove(localObject1)) && (ActivityManagerDebugConfig.DEBUG_SERVICE)) {
              Slog.v(TAG_SERVICE, "killServices remove destroying " + localObject1);
            }
            j = ((ServiceRecord)localObject1).bindings.size() - 1;
            if (j < 0) {
              break;
            }
            ??? = (IntentBindRecord)((ServiceRecord)localObject1).bindings.valueAt(j);
            if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
              Slog.v(TAG_SERVICE, "Killing binding " + ??? + ": shouldUnbind=" + ((IntentBindRecord)???).hasBound);
            }
            ((IntentBindRecord)???).binder = null;
            ((IntentBindRecord)???).hasBound = false;
            ((IntentBindRecord)???).received = false;
            ((IntentBindRecord)???).requested = false;
            k = ((IntentBindRecord)???).apps.size() - 1;
            if (k < 0) {
              break label469;
            }
            localObject3 = (ProcessRecord)((IntentBindRecord)???).apps.keyAt(k);
            if ((!((ProcessRecord)localObject3).killedByAm) && (((ProcessRecord)localObject3).thread != null)) {
              break label387;
            }
            k -= 1;
          }
        }
        continue;
        label387:
        localObject3 = (AppBindRecord)((IntentBindRecord)???).apps.valueAt(k);
        int i1 = 0;
        int m = ((AppBindRecord)localObject3).connections.size() - 1;
        for (;;)
        {
          int n = i1;
          if (m >= 0)
          {
            if ((((ConnectionRecord)((AppBindRecord)localObject3).connections.valueAt(m)).flags & 0x31) == 1) {
              n = 1;
            }
          }
          else
          {
            if (n != 0) {
              break;
            }
            break;
          }
          m -= 1;
        }
        label469:
        j -= 1;
      }
      i -= 1;
    }
    Object localObject1 = getServiceMap(paramProcessRecord.userId);
    i = paramProcessRecord.services.size() - 1;
    if (i >= 0)
    {
      ??? = (ServiceRecord)paramProcessRecord.services.valueAt(i);
      if (!paramProcessRecord.persistent) {
        paramProcessRecord.services.removeAt(i);
      }
      localObject3 = (ServiceRecord)((ServiceMap)localObject1).mServicesByName.get(((ServiceRecord)???).name);
      if (localObject3 != ???) {
        if (localObject3 != null) {
          Slog.wtf(TAG, "Service " + ??? + " in process " + paramProcessRecord + " not same as in map: " + localObject3);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        long l1 = SystemClock.uptimeMillis();
        long l2;
        if (((ServiceRecord)???).relativeRestartCount > 0)
        {
          l2 = (l1 - ((ServiceRecord)???).relativeRestartTime) / ((ServiceRecord)???).relativeRestartCount;
          l1 = l2;
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "avgRestartTime: " + l2);
          }
        }
        for (l1 = l2;; l1 = Long.MAX_VALUE)
        {
          if ((paramBoolean) && ((((ServiceRecord)???).crashCount >= 2) || ((((ServiceRecord)???).relativeRestartCount > 5) && (l1 < 10000L))) && ((((ServiceRecord)???).serviceInfo.applicationInfo.flags & 0x8) == 0) && (!((ServiceRecord)???).shortName.equals("com.google.android.marvin.talkback/com.android.switchaccess.SwitchAccessService"))) {
            break label908;
          }
          if ((!paramBoolean) || (!this.mAm.mUserController.isUserRunningLocked(((ServiceRecord)???).userId, 0))) {
            break label1021;
          }
          if (isCoreApp(paramProcessRecord)) {
            break label1030;
          }
          Slog.w(TAG, "Service crashed " + ((ServiceRecord)???).crashCount + " times, stopping: " + ??? + "this is not core app" + paramProcessRecord.processName);
          EventLog.writeEvent(30031, new Object[] { Integer.valueOf(((ServiceRecord)???).crashCount), ((ServiceRecord)???).shortName, Integer.valueOf(paramProcessRecord.pid), paramProcessRecord.processName });
          bringDownServiceLocked((ServiceRecord)???);
          break;
        }
        label908:
        Slog.w(TAG, "Service crashed " + ((ServiceRecord)???).crashCount + " times, stopping: " + ??? + ", avgRestartTime: " + l1);
        EventLog.writeEvent(30034, new Object[] { Integer.valueOf(((ServiceRecord)???).userId), Integer.valueOf(((ServiceRecord)???).crashCount), ((ServiceRecord)???).shortName, Integer.valueOf(paramProcessRecord.pid) });
        bringDownServiceLocked((ServiceRecord)???);
        continue;
        label1021:
        bringDownServiceLocked((ServiceRecord)???);
        continue;
        label1030:
        boolean bool = scheduleServiceRestartLocked((ServiceRecord)???, true);
        if ((((ServiceRecord)???).startRequested) && ((((ServiceRecord)???).stopIfKilled) || (bool)) && (((ServiceRecord)???).pendingStarts.size() == 0))
        {
          ((ServiceRecord)???).startRequested = false;
          if (((ServiceRecord)???).tracker != null) {
            ((ServiceRecord)???).tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
          }
          if (!((ServiceRecord)???).hasAutoCreateConnections()) {
            bringDownServiceLocked((ServiceRecord)???);
          }
        }
      }
    }
    if (!paramBoolean)
    {
      paramProcessRecord.services.clear();
      i = this.mRestartingServices.size() - 1;
      while (i >= 0)
      {
        localObject1 = (ServiceRecord)this.mRestartingServices.get(i);
        if ((((ServiceRecord)localObject1).processName.equals(paramProcessRecord.processName)) && (((ServiceRecord)localObject1).serviceInfo.applicationInfo.uid == paramProcessRecord.info.uid))
        {
          this.mRestartingServices.remove(i);
          clearRestartingIfNeededLocked((ServiceRecord)localObject1);
        }
        i -= 1;
      }
      i = this.mPendingServices.size() - 1;
      while (i >= 0)
      {
        localObject1 = (ServiceRecord)this.mPendingServices.get(i);
        if ((((ServiceRecord)localObject1).processName.equals(paramProcessRecord.processName)) && (((ServiceRecord)localObject1).serviceInfo.applicationInfo.uid == paramProcessRecord.info.uid)) {
          this.mPendingServices.remove(i);
        }
        i -= 1;
      }
    }
    i = this.mDestroyingServices.size();
    while (i > 0)
    {
      j = i - 1;
      localObject1 = (ServiceRecord)this.mDestroyingServices.get(j);
      i = j;
      if (((ServiceRecord)localObject1).app == paramProcessRecord)
      {
        ((ServiceRecord)localObject1).forceClearTracker();
        this.mDestroyingServices.remove(j);
        i = j;
        if (ActivityManagerDebugConfig.DEBUG_SERVICE)
        {
          Slog.v(TAG_SERVICE, "killServices remove destroying " + localObject1);
          i = j;
        }
      }
    }
    paramProcessRecord.executingServices.clear();
  }
  
  ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked(ServiceRecord paramServiceRecord)
  {
    ActivityManager.RunningServiceInfo localRunningServiceInfo = new ActivityManager.RunningServiceInfo();
    localRunningServiceInfo.service = paramServiceRecord.name;
    if (paramServiceRecord.app != null) {
      localRunningServiceInfo.pid = paramServiceRecord.app.pid;
    }
    localRunningServiceInfo.uid = paramServiceRecord.appInfo.uid;
    localRunningServiceInfo.process = paramServiceRecord.processName;
    localRunningServiceInfo.foreground = paramServiceRecord.isForeground;
    localRunningServiceInfo.activeSince = paramServiceRecord.createTime;
    localRunningServiceInfo.started = paramServiceRecord.startRequested;
    localRunningServiceInfo.clientCount = paramServiceRecord.connections.size();
    localRunningServiceInfo.crashCount = paramServiceRecord.crashCount;
    localRunningServiceInfo.lastActivityTime = paramServiceRecord.lastActivity;
    if (paramServiceRecord.isForeground) {
      localRunningServiceInfo.flags |= 0x2;
    }
    if (paramServiceRecord.startRequested) {
      localRunningServiceInfo.flags |= 0x1;
    }
    if ((paramServiceRecord.app != null) && (paramServiceRecord.app.pid == ActivityManagerService.MY_PID)) {
      localRunningServiceInfo.flags |= 0x4;
    }
    if ((paramServiceRecord.app != null) && (paramServiceRecord.app.persistent)) {
      localRunningServiceInfo.flags |= 0x8;
    }
    int i = paramServiceRecord.connections.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = (ArrayList)paramServiceRecord.connections.valueAt(i);
      int j = 0;
      while (j < localArrayList.size())
      {
        ConnectionRecord localConnectionRecord = (ConnectionRecord)localArrayList.get(j);
        if (localConnectionRecord.clientLabel != 0)
        {
          localRunningServiceInfo.clientPackage = localConnectionRecord.binding.client.info.packageName;
          localRunningServiceInfo.clientLabel = localConnectionRecord.clientLabel;
          return localRunningServiceInfo;
        }
        j += 1;
      }
      i -= 1;
    }
    return localRunningServiceInfo;
  }
  
  ServiceDumper newServiceDumperLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt, boolean paramBoolean, String paramString)
  {
    return new ServiceDumper(paramFileDescriptor, paramPrintWriter, paramArrayOfString, paramInt, paramBoolean, paramString);
  }
  
  IBinder peekServiceLocked(Intent paramIntent, String paramString1, String paramString2)
  {
    paramString2 = retrieveServiceLocked(paramIntent, paramString1, paramString2, Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getCallingUserId(), false, false, false);
    paramString1 = null;
    paramIntent = paramString1;
    if (paramString2 != null)
    {
      if (paramString2.record == null) {
        throw new SecurityException("Permission Denial: Accessing service from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + paramString2.permission);
      }
      paramString2 = (IntentBindRecord)paramString2.record.bindings.get(paramString2.record.intent);
      paramIntent = paramString1;
      if (paramString2 != null) {
        paramIntent = paramString2.binder;
      }
    }
    return paramIntent;
  }
  
  final void performServiceRestartLocked(ServiceRecord paramServiceRecord)
  {
    if (!this.mRestartingServices.contains(paramServiceRecord)) {
      return;
    }
    if (!isServiceNeeded(paramServiceRecord, false, false))
    {
      Slog.wtf(TAG, "Restarting service that is not needed: " + paramServiceRecord);
      return;
    }
    if (paramServiceRecord.relativeRestartCount == 0)
    {
      paramServiceRecord.relativeRestartTime = SystemClock.uptimeMillis();
      paramServiceRecord.relativeRestartCount += 1;
    }
    for (;;)
    {
      if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
        Slog.d(TAG, "relativeRestartTime = " + paramServiceRecord.relativeRestartTime + ", relativeRestartCount = " + paramServiceRecord.relativeRestartCount);
      }
      try
      {
        if (SERVICE_RESCHEDULE)
        {
          int j = 0;
          ActivityRecord localActivityRecord = null;
          ActivityStack localActivityStack = this.mAm.getFocusedStack();
          if (localActivityStack != null) {
            localActivityRecord = localActivityStack.topRunningActivityLocked();
          }
          int i = j;
          if (localActivityRecord != null)
          {
            i = j;
            if (!localActivityRecord.nowVisible) {
              if (!paramServiceRecord.shortName.contains(localActivityRecord.packageName)) {
                break label229;
              }
            }
          }
          label229:
          for (i = j;; i = 1)
          {
            if (i != 0) {
              break label234;
            }
            bringUpServiceLocked(paramServiceRecord, paramServiceRecord.intent.getIntent().getFlags(), paramServiceRecord.createdFromFg, true, false);
            return;
            paramServiceRecord.relativeRestartCount += 1;
            break;
          }
          label234:
          if (DEBUG_DELAYED_SERVICE) {
            Slog.v(TAG, "Reschedule service restart due to app launch r.shortName " + paramServiceRecord.shortName + " r.app = " + paramServiceRecord.app);
          }
          paramServiceRecord.resetRestartCounter();
          scheduleServiceRestartLocked(paramServiceRecord, true);
          return;
        }
        bringUpServiceLocked(paramServiceRecord, paramServiceRecord.intent.getIntent().getFlags(), paramServiceRecord.createdFromFg, true, false);
        return;
      }
      catch (TransactionTooLargeException paramServiceRecord) {}
    }
  }
  
  void processStartTimedOutLocked(ProcessRecord paramProcessRecord)
  {
    int j;
    for (int i = 0; i < this.mPendingServices.size(); i = j + 1)
    {
      ServiceRecord localServiceRecord = (ServiceRecord)this.mPendingServices.get(i);
      if ((paramProcessRecord.uid != localServiceRecord.appInfo.uid) || (!paramProcessRecord.processName.equals(localServiceRecord.processName)))
      {
        j = i;
        if (localServiceRecord.isolatedProc != paramProcessRecord) {}
      }
      else
      {
        Slog.w(TAG, "Forcing bringing down service: " + localServiceRecord);
        localServiceRecord.isolatedProc = null;
        this.mPendingServices.remove(i);
        j = i - 1;
        bringDownServiceLocked(localServiceRecord);
      }
    }
  }
  
  void publishServiceLocked(ServiceRecord paramServiceRecord, Intent paramIntent, IBinder paramIBinder)
  {
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      int j;
      try
      {
        if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
          Slog.v(TAG_SERVICE, "PUBLISHING " + paramServiceRecord + " " + paramIntent + ": " + paramIBinder);
        }
        Intent.FilterComparison localFilterComparison;
        if (paramServiceRecord != null)
        {
          localFilterComparison = new Intent.FilterComparison(paramIntent);
          localObject = (IntentBindRecord)paramServiceRecord.bindings.get(localFilterComparison);
          if ((localObject == null) || (((IntentBindRecord)localObject).received)) {
            serviceDoneExecutingLocked(paramServiceRecord, this.mDestroyingServices.contains(paramServiceRecord), false);
          }
        }
        else
        {
          return;
        }
        ((IntentBindRecord)localObject).binder = paramIBinder;
        ((IntentBindRecord)localObject).requested = true;
        ((IntentBindRecord)localObject).received = true;
        int i = paramServiceRecord.connections.size() - 1;
        if (i < 0) {
          continue;
        }
        Object localObject = (ArrayList)paramServiceRecord.connections.valueAt(i);
        j = 0;
        if (j < ((ArrayList)localObject).size())
        {
          ConnectionRecord localConnectionRecord = (ConnectionRecord)((ArrayList)localObject).get(j);
          if (!localFilterComparison.equals(localConnectionRecord.binding.intent.intent))
          {
            if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
              Slog.v(TAG_SERVICE, "Not publishing to: " + localConnectionRecord);
            }
            if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
              Slog.v(TAG_SERVICE, "Bound intent: " + localConnectionRecord.binding.intent.intent);
            }
            if (!ActivityManagerDebugConfig.DEBUG_SERVICE) {
              break label516;
            }
            Slog.v(TAG_SERVICE, "Published intent: " + paramIntent);
            break label516;
          }
          if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
            Slog.v(TAG_SERVICE, "Publishing to: " + localConnectionRecord);
          }
          try
          {
            if (paramServiceRecord.appInfo != null) {
              OnePlusProcessManager.resumeProcessByUID_out(paramServiceRecord.appInfo.uid, "addConnection = " + localConnectionRecord.binding);
            }
            localConnectionRecord.conn.connected(paramServiceRecord.name, paramIBinder);
          }
          catch (Exception localException)
          {
            Slog.w(TAG, "Failure sending service " + paramServiceRecord.name + " to connection " + localConnectionRecord.conn.asBinder() + " (in " + localConnectionRecord.binding.client.processName + ")", localException);
          }
        }
        i -= 1;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      continue;
      label516:
      j += 1;
    }
  }
  
  void removeConnectionLocked(ConnectionRecord paramConnectionRecord, ProcessRecord paramProcessRecord, ActivityRecord paramActivityRecord)
  {
    IBinder localIBinder = paramConnectionRecord.conn.asBinder();
    AppBindRecord localAppBindRecord = paramConnectionRecord.binding;
    localServiceRecord = localAppBindRecord.service;
    ArrayList localArrayList = (ArrayList)localServiceRecord.connections.get(localIBinder);
    if (localArrayList != null)
    {
      localArrayList.remove(paramConnectionRecord);
      if (localArrayList.size() == 0) {
        localServiceRecord.connections.remove(localIBinder);
      }
    }
    localAppBindRecord.connections.remove(paramConnectionRecord);
    if ((paramConnectionRecord.activity != null) && (paramConnectionRecord.activity != paramActivityRecord) && (paramConnectionRecord.activity.connections != null)) {
      paramConnectionRecord.activity.connections.remove(paramConnectionRecord);
    }
    if (localAppBindRecord.client != paramProcessRecord)
    {
      localAppBindRecord.client.connections.remove(paramConnectionRecord);
      if ((paramConnectionRecord.flags & 0x8) != 0) {
        localAppBindRecord.client.updateHasAboveClientLocked();
      }
      if ((paramConnectionRecord.flags & 0x1000000) != 0)
      {
        localServiceRecord.updateWhitelistManager();
        if ((!localServiceRecord.whitelistManager) && (localServiceRecord.app != null)) {
          updateWhitelistManagerLocked(localServiceRecord.app);
        }
      }
      if (localServiceRecord.app != null) {
        updateServiceClientActivitiesLocked(localServiceRecord.app, paramConnectionRecord, true);
      }
    }
    paramProcessRecord = (ArrayList)this.mServiceConnections.get(localIBinder);
    if (paramProcessRecord != null)
    {
      paramProcessRecord.remove(paramConnectionRecord);
      if (paramProcessRecord.size() == 0) {
        this.mServiceConnections.remove(localIBinder);
      }
    }
    this.mAm.stopAssociationLocked(localAppBindRecord.client.uid, localAppBindRecord.client.processName, localServiceRecord.appInfo.uid, localServiceRecord.name);
    if (localAppBindRecord.connections.size() == 0) {
      localAppBindRecord.intent.apps.remove(localAppBindRecord.client);
    }
    if (!paramConnectionRecord.serviceDead)
    {
      if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
        Slog.v(TAG_SERVICE, "Disconnecting binding " + localAppBindRecord.intent + ": shouldUnbind=" + localAppBindRecord.intent.hasBound);
      }
      if ((localServiceRecord.app == null) || (localServiceRecord.app.thread == null) || (localAppBindRecord.intent.apps.size() != 0) || (!localAppBindRecord.intent.hasBound)) {}
    }
    try
    {
      bumpServiceExecutingLocked(localServiceRecord, false, "unbind");
      if ((localAppBindRecord.client != localServiceRecord.app) && ((paramConnectionRecord.flags & 0x20) == 0) && (localServiceRecord.app.setProcState <= 11)) {
        this.mAm.updateLruProcessLocked(localServiceRecord.app, false, null);
      }
      this.mAm.updateOomAdjLocked(localServiceRecord.app);
      localAppBindRecord.intent.hasBound = false;
      localAppBindRecord.intent.doRebind = false;
      localServiceRecord.app.thread.scheduleUnbindService(localServiceRecord, localAppBindRecord.intent.intent.getIntent());
    }
    catch (Exception paramProcessRecord)
    {
      for (;;)
      {
        boolean bool;
        Slog.w(TAG, "Exception when unbinding service " + localServiceRecord.shortName, paramProcessRecord);
        serviceProcessGoneLocked(localServiceRecord);
      }
    }
    this.mPendingServices.remove(localServiceRecord);
    if ((paramConnectionRecord.flags & 0x1) != 0)
    {
      bool = localServiceRecord.hasAutoCreateConnections();
      if ((!bool) && (localServiceRecord.tracker != null)) {
        localServiceRecord.tracker.setBound(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
      }
      bringDownServiceIfNeededLocked(localServiceRecord, true, bool);
    }
  }
  
  void scheduleServiceTimeoutLocked(ProcessRecord paramProcessRecord)
  {
    if ((paramProcessRecord.executingServices.size() == 0) || (paramProcessRecord.thread == null)) {
      return;
    }
    long l = SystemClock.uptimeMillis();
    Message localMessage = this.mAm.mHandler.obtainMessage(12);
    localMessage.obj = paramProcessRecord;
    ActivityManagerService.MainHandler localMainHandler = this.mAm.mHandler;
    if (paramProcessRecord.execServicesFg) {}
    for (l = 20000L + l;; l = 200000L + l)
    {
      localMainHandler.sendMessageAtTime(localMessage, l);
      return;
    }
  }
  
  void serviceDoneExecutingLocked(ServiceRecord paramServiceRecord, int paramInt1, int paramInt2, int paramInt3)
  {
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG, "serviceDoneExecutingLocked ServiceRecord= " + paramServiceRecord + " type= " + paramInt1 + " startId= " + paramInt2 + " res= " + paramInt3);
    }
    boolean bool = this.mDestroyingServices.contains(paramServiceRecord);
    if (paramServiceRecord != null)
    {
      if (paramInt1 == 1)
      {
        paramServiceRecord.callStart = true;
        switch (paramInt3)
        {
        default: 
          throw new IllegalArgumentException("Unknown service start result: " + paramInt3);
        case 0: 
        case 1: 
          paramServiceRecord.findDeliveredStart(paramInt2, true);
          paramServiceRecord.stopIfKilled = false;
          if (paramInt3 == 0) {
            paramServiceRecord.callStart = false;
          }
          break;
        }
      }
      for (;;)
      {
        long l = Binder.clearCallingIdentity();
        serviceDoneExecutingLocked(paramServiceRecord, bool, bool);
        Binder.restoreCallingIdentity(l);
        return;
        paramServiceRecord.findDeliveredStart(paramInt2, true);
        if (paramServiceRecord.getLastStartId() != paramInt2) {
          break;
        }
        paramServiceRecord.stopIfKilled = true;
        break;
        ServiceRecord.StartItem localStartItem = paramServiceRecord.findDeliveredStart(paramInt2, false);
        if (localStartItem == null) {
          break;
        }
        localStartItem.deliveryCount = 0;
        localStartItem.doneExecutingCount += 1;
        paramServiceRecord.stopIfKilled = true;
        break;
        paramServiceRecord.findDeliveredStart(paramInt2, true);
        break;
        if (paramInt1 == 2) {
          if (!bool)
          {
            if (paramServiceRecord.app != null) {
              Slog.w(TAG, "Service done with onDestroy, but not inDestroying: " + paramServiceRecord + ", app=" + paramServiceRecord.app);
            }
          }
          else if (paramServiceRecord.executeNesting != 1)
          {
            Slog.w(TAG, "Service done with onDestroy, but executeNesting=" + paramServiceRecord.executeNesting + ": " + paramServiceRecord);
            paramServiceRecord.executeNesting = 1;
          }
        }
      }
    }
    Slog.w(TAG, "Done executing unknown service from pid " + Binder.getCallingPid());
  }
  
  void serviceTimeout(ProcessRecord paramProcessRecord)
  {
    StringWriter localStringWriter = null;
    for (;;)
    {
      Object localObject1;
      int i;
      long l2;
      synchronized (this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if (paramProcessRecord.executingServices.size() != 0)
        {
          localObject1 = paramProcessRecord.thread;
          if (localObject1 != null) {}
        }
        else
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        long l3 = SystemClock.uptimeMillis();
        if (paramProcessRecord.execServicesFg)
        {
          i = 20000;
          long l4 = i;
          localObject2 = null;
          l1 = 0L;
          i = paramProcessRecord.executingServices.size() - 1;
          localObject1 = localObject2;
          if (i >= 0)
          {
            localObject1 = (ServiceRecord)paramProcessRecord.executingServices.valueAt(i);
            if (((ServiceRecord)localObject1).executingStart >= l3 - l4) {}
          }
          else
          {
            if ((localObject1 == null) || (!this.mAm.mLruProcesses.contains(paramProcessRecord))) {
              continue;
            }
            Slog.w(TAG, "Timeout executing service: " + localObject1);
            localStringWriter = new StringWriter();
            localObject2 = new FastPrintWriter(localStringWriter, false, 1024);
            ((PrintWriter)localObject2).println(localObject1);
            ((ServiceRecord)localObject1).dump((PrintWriter)localObject2, "    ");
            ((PrintWriter)localObject2).close();
            this.mLastAnrDump = localStringWriter.toString();
            this.mAm.mHandler.removeCallbacks(this.mLastAnrDumpClearer);
            this.mAm.mHandler.postDelayed(this.mLastAnrDumpClearer, 7200000L);
            localObject1 = "executing service " + ((ServiceRecord)localObject1).shortName;
            ActivityManagerService.resetPriorityAfterLockedSection();
            if (localObject1 == null) {
              break label440;
            }
            if (!OnePlusProcessManager.checkProcessWhileTimeout(paramProcessRecord)) {
              break label424;
            }
            Slog.d(TAG, "serviceTimeout(): --anr  :  --> suspend : " + paramProcessRecord);
          }
        }
        else
        {
          i = 200000;
          continue;
        }
        l2 = l1;
        if (((ServiceRecord)localObject1).executingStart <= l1) {
          break label441;
        }
        l2 = ((ServiceRecord)localObject1).executingStart;
        break label441;
        localObject1 = this.mAm.mHandler.obtainMessage(12);
        ((Message)localObject1).obj = paramProcessRecord;
        Object localObject2 = this.mAm.mHandler;
        if (paramProcessRecord.execServicesFg)
        {
          l1 = 20000L + l1;
          ((ActivityManagerService.MainHandler)localObject2).sendMessageAtTime((Message)localObject1, l1);
          localObject1 = localStringWriter;
        }
      }
      long l1 = 200000L + l1;
      continue;
      label424:
      this.mAm.mAppErrors.appNotResponding(paramProcessRecord, null, null, false, (String)localObject1);
      label440:
      return;
      label441:
      i -= 1;
      l1 = l2;
    }
  }
  
  public void setServiceForegroundLocked(ComponentName paramComponentName, IBinder paramIBinder, int paramInt1, Notification paramNotification, int paramInt2)
  {
    int i = UserHandle.getCallingUserId();
    long l = Binder.clearCallingIdentity();
    try
    {
      paramComponentName = findServiceLocked(paramComponentName, paramIBinder, i);
      if (paramComponentName == null) {
        break label140;
      }
      if (paramInt1 == 0) {
        break label146;
      }
      if (paramNotification == null) {
        throw new IllegalArgumentException("null notification");
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    if (paramComponentName.foregroundId != paramInt1)
    {
      cancelForegroudNotificationLocked(paramComponentName);
      paramComponentName.foregroundId = paramInt1;
    }
    paramNotification.flags |= 0x40;
    paramComponentName.foregroundNoti = paramNotification;
    paramComponentName.isForeground = true;
    paramComponentName.postNotification();
    if (paramComponentName.app != null) {
      updateServiceForegroundLocked(paramComponentName.app, true);
    }
    getServiceMap(paramComponentName.userId).ensureNotStartingBackground(paramComponentName);
    this.mAm.notifyPackageUse(paramComponentName.serviceInfo.packageName, 2);
    label140:
    Binder.restoreCallingIdentity(l);
    return;
    label146:
    if (paramComponentName.isForeground)
    {
      paramComponentName.isForeground = false;
      if (paramComponentName.app != null) {
        if (isSupport)
        {
          paramIBinder = paramComponentName.app;
          paramInt1 = paramIBinder.services.size() - 1;
        }
      }
    }
    for (;;)
    {
      if (paramInt1 >= 0)
      {
        paramNotification = (ServiceRecord)paramIBinder.services.valueAt(paramInt1);
        if ((paramNotification.isForeground) && (paramNotification.foregroundId == paramComponentName.foregroundId))
        {
          paramNotification.isForeground = false;
          Slog.d(TAG, String.format("%s(foregroundId=%d) not foreground anymore", new Object[] { paramNotification, Integer.valueOf(paramNotification.foregroundId) }));
        }
      }
      else
      {
        this.mAm.updateLruProcessLocked(paramComponentName.app, false, null);
        updateServiceForegroundLocked(paramComponentName.app, true);
        if ((paramInt2 & 0x1) != 0)
        {
          cancelForegroudNotificationLocked(paramComponentName);
          paramComponentName.foregroundId = 0;
          paramComponentName.foregroundNoti = null;
          break;
        }
        if (paramComponentName.appInfo.targetSdkVersion < 21) {
          break;
        }
        paramComponentName.stripForegroundServiceFlagFromNotification();
        if ((paramInt2 & 0x2) == 0) {
          break;
        }
        paramComponentName.foregroundId = 0;
        paramComponentName.foregroundNoti = null;
        break;
      }
      paramInt1 -= 1;
    }
  }
  
  ComponentName startServiceInnerLocked(ServiceMap paramServiceMap, Intent paramIntent, ServiceRecord paramServiceRecord, boolean paramBoolean1, boolean paramBoolean2)
    throws TransactionTooLargeException
  {
    ??? = paramServiceRecord.getTracker();
    if (??? != null) {
      ((ServiceState)???).setStarted(true, this.mAm.mProcessStats.getMemFactorLocked(), paramServiceRecord.lastActivity);
    }
    paramServiceRecord.callStart = false;
    synchronized (paramServiceRecord.stats.getBatteryStats())
    {
      paramServiceRecord.stats.startRunningLocked();
      paramIntent = bringUpServiceLocked(paramServiceRecord, paramIntent.getFlags(), paramBoolean1, false, false);
      if (paramIntent != null) {
        return new ComponentName("!!", paramIntent);
      }
    }
    if ((paramServiceRecord.startRequested) && (paramBoolean2)) {
      if (paramServiceMap.mStartingBackground.size() == 0)
      {
        paramBoolean1 = true;
        paramServiceMap.mStartingBackground.add(paramServiceRecord);
        paramServiceRecord.startingBgTimeout = (SystemClock.uptimeMillis() + 15000L);
        if (!DEBUG_DELAYED_SERVICE) {
          break label220;
        }
        paramIntent = new RuntimeException("here");
        paramIntent.fillInStackTrace();
        Slog.v(TAG_SERVICE, "Starting background (first=" + paramBoolean1 + "): " + paramServiceRecord, paramIntent);
        label200:
        if (paramBoolean1) {
          paramServiceMap.rescheduleDelayedStarts();
        }
      }
    }
    for (;;)
    {
      return paramServiceRecord.name;
      paramBoolean1 = false;
      break;
      label220:
      if (!DEBUG_DELAYED_STARTS) {
        break label200;
      }
      Slog.v(TAG_SERVICE, "Starting background (first=" + paramBoolean1 + "): " + paramServiceRecord);
      break label200;
      if (paramBoolean1) {
        paramServiceMap.ensureNotStartingBackground(paramServiceRecord);
      }
    }
  }
  
  ComponentName startServiceLocked(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString1, int paramInt1, int paramInt2, String paramString2, int paramInt3)
    throws TransactionTooLargeException
  {
    if (DEBUG_DELAYED_STARTS) {
      Slog.v(TAG_SERVICE, "startService: " + paramIntent + " type=" + paramString1 + " args=" + paramIntent.getExtras());
    }
    Object localObject = null;
    boolean bool1;
    if (paramIApplicationThread != null)
    {
      localObject = this.mAm.getRecordForAppLocked(paramIApplicationThread);
      if (localObject == null) {
        throw new SecurityException("Unable to find app for caller " + paramIApplicationThread + " (pid=" + Binder.getCallingPid() + ") when starting service " + paramIntent);
      }
      if (((ProcessRecord)localObject).setSchedGroup != 0)
      {
        bool1 = true;
        paramIApplicationThread = (IApplicationThread)localObject;
      }
    }
    for (;;)
    {
      localObject = retrieveServiceLocked(paramIntent, paramString1, paramString2, paramInt1, paramInt2, paramInt3, true, bool1, false);
      if (localObject != null) {
        break;
      }
      return null;
      bool1 = false;
      paramIApplicationThread = (IApplicationThread)localObject;
      continue;
      bool1 = true;
      paramIApplicationThread = (IApplicationThread)localObject;
    }
    if (((ServiceLookupResult)localObject).record == null)
    {
      if (((ServiceLookupResult)localObject).permission != null) {}
      for (paramIApplicationThread = ((ServiceLookupResult)localObject).permission;; paramIApplicationThread = "private to package") {
        return new ComponentName("!", paramIApplicationThread);
      }
    }
    localObject = ((ServiceLookupResult)localObject).record;
    if (!this.mAm.mUserController.exists(((ServiceRecord)localObject).userId))
    {
      Slog.w(TAG, "Trying to start service with non-existent user! " + ((ServiceRecord)localObject).userId);
      return null;
    }
    long l;
    if (!((ServiceRecord)localObject).startRequested) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      if (this.mAm.checkAllowBackgroundLocked(((ServiceRecord)localObject).appInfo.uid, ((ServiceRecord)localObject).packageName, paramInt1, true) != 0)
      {
        Slog.w(TAG, "Background start not allowed: service " + paramIntent + " to " + ((ServiceRecord)localObject).name.flattenToShortString() + " from pid=" + paramInt1 + " uid=" + paramInt2 + " pkg=" + paramString2);
        return null;
      }
      Binder.restoreCallingIdentity(l);
      if (OnePlusAppBootManager.DEBUG) {
        OnePlusAppBootManager.myLog(" startServiceLocked # callerApp= " + paramIApplicationThread + ",callingPackage=" + paramString2 + ", service = " + paramIntent + ", resolvedType=" + paramString1 + ", callingPid=" + paramInt1 + ", callingUid=" + paramInt2 + ", r=" + localObject + ", callerFg=" + bool1);
      }
      if ((OnePlusAppBootManager.IN_USING) && (!OnePlusAppBootManager.getInstance(null).canServiceGo(paramIApplicationThread, paramIntent, (ServiceRecord)localObject, paramInt2, paramString2))) {
        return null;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    paramIApplicationThread = this.mAm.checkGrantUriPermissionFromIntentLocked(paramInt2, ((ServiceRecord)localObject).packageName, paramIntent, paramIntent.getFlags(), null, ((ServiceRecord)localObject).userId);
    if ((Build.PERMISSIONS_REVIEW_REQUIRED) && (!requestStartTargetPermissionsReviewIfNeededLocked((ServiceRecord)localObject, paramString2, paramInt2, paramIntent, bool1, paramInt3))) {
      return null;
    }
    if ((unscheduleServiceRestartLocked((ServiceRecord)localObject, paramInt2, false)) && (ActivityManagerDebugConfig.DEBUG_SERVICE)) {
      Slog.v(TAG_SERVICE, "START SERVICE WHILE RESTART PENDING: " + localObject);
    }
    ((ServiceRecord)localObject).lastActivity = SystemClock.uptimeMillis();
    ((ServiceRecord)localObject).startRequested = true;
    ((ServiceRecord)localObject).delayedStop = false;
    ((ServiceRecord)localObject).pendingStarts.add(new ServiceRecord.StartItem((ServiceRecord)localObject, false, ((ServiceRecord)localObject).makeNextStartId(), paramIntent, paramIApplicationThread));
    paramIApplicationThread = getServiceMap(((ServiceRecord)localObject).userId);
    boolean bool3 = false;
    boolean bool2;
    if ((!bool1) && (((ServiceRecord)localObject).app == null) && (this.mAm.mUserController.hasStartedUserState(((ServiceRecord)localObject).userId)))
    {
      paramString2 = this.mAm.getProcessRecordLocked(((ServiceRecord)localObject).processName, ((ServiceRecord)localObject).appInfo.uid, false);
      if ((paramString2 == null) || (paramString2.curProcState > 11))
      {
        if (DEBUG_DELAYED_SERVICE) {
          Slog.v(TAG_SERVICE, "Potential start delay of " + localObject + " in " + paramString2);
        }
        if (((ServiceRecord)localObject).delayed)
        {
          if (DEBUG_DELAYED_STARTS) {
            Slog.v(TAG_SERVICE, "Continuing to delay: " + localObject);
          }
          return ((ServiceRecord)localObject).name;
        }
        if (paramIApplicationThread.mStartingBackground.size() >= this.mMaxStartingBackground)
        {
          Slog.i(TAG_SERVICE, "Delaying start of: " + localObject);
          paramIApplicationThread.mDelayedStartList.add(localObject);
          ((ServiceRecord)localObject).delayed = true;
          return ((ServiceRecord)localObject).name;
        }
        if (DEBUG_DELAYED_STARTS) {
          Slog.v(TAG_SERVICE, "Not delaying: " + localObject);
        }
        bool2 = true;
      }
    }
    for (;;)
    {
      return startServiceInnerLocked(paramIApplicationThread, paramIntent, (ServiceRecord)localObject, bool1, bool2);
      if (paramString2.curProcState >= 10)
      {
        bool3 = true;
        bool2 = bool3;
        if (DEBUG_DELAYED_STARTS)
        {
          Slog.v(TAG_SERVICE, "Not delaying, but counting as bg: " + localObject);
          bool2 = bool3;
        }
      }
      else
      {
        bool2 = bool3;
        if (DEBUG_DELAYED_STARTS)
        {
          paramString1 = new StringBuilder(128);
          paramString1.append("Not potential delay (state=").append(paramString2.curProcState).append(' ').append(paramString2.adjType);
          paramString2 = paramString2.makeAdjReason();
          if (paramString2 != null)
          {
            paramString1.append(' ');
            paramString1.append(paramString2);
          }
          paramString1.append("): ");
          paramString1.append(((ServiceRecord)localObject).toString());
          Slog.v(TAG_SERVICE, paramString1.toString());
          bool2 = bool3;
          continue;
          bool2 = bool3;
          if (DEBUG_DELAYED_STARTS) {
            if (bool1)
            {
              Slog.v(TAG_SERVICE, "Not potential delay (callerFg=" + bool1 + " uid=" + paramInt2 + " pid=" + paramInt1 + "): " + localObject);
              bool2 = bool3;
            }
            else if (((ServiceRecord)localObject).app != null)
            {
              Slog.v(TAG_SERVICE, "Not potential delay (cur app=" + ((ServiceRecord)localObject).app + "): " + localObject);
              bool2 = bool3;
            }
            else
            {
              Slog.v(TAG_SERVICE, "Not potential delay (user " + ((ServiceRecord)localObject).userId + " not started): " + localObject);
              bool2 = bool3;
            }
          }
        }
      }
    }
  }
  
  void stopInBackgroundLocked(int paramInt)
  {
    ServiceMap localServiceMap = (ServiceMap)this.mServiceMap.get(UserHandle.getUserId(paramInt));
    Object localObject1 = null;
    if (localServiceMap != null)
    {
      int i = localServiceMap.mServicesByName.size() - 1;
      Object localObject2;
      while (i >= 0)
      {
        ServiceRecord localServiceRecord = (ServiceRecord)localServiceMap.mServicesByName.valueAt(i);
        localObject2 = localObject1;
        if (localServiceRecord.appInfo.uid == paramInt)
        {
          localObject2 = localObject1;
          if (localServiceRecord.startRequested)
          {
            localObject2 = localObject1;
            if (this.mAm.mAppOpsService.noteOperation(64, paramInt, localServiceRecord.packageName) != 0)
            {
              localObject2 = localObject1;
              if (localObject1 == null)
              {
                localObject2 = new ArrayList();
                ((ArrayList)localObject2).add(localServiceRecord);
              }
            }
          }
        }
        i -= 1;
        localObject1 = localObject2;
      }
      if (localObject1 != null)
      {
        paramInt = ((ArrayList)localObject1).size() - 1;
        while (paramInt >= 0)
        {
          localObject2 = (ServiceRecord)((ArrayList)localObject1).get(paramInt);
          ((ServiceRecord)localObject2).delayed = false;
          localServiceMap.ensureNotStartingBackground((ServiceRecord)localObject2);
          stopServiceLocked((ServiceRecord)localObject2);
          paramInt -= 1;
        }
      }
    }
  }
  
  int stopServiceLocked(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString, int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "stopService: " + paramIntent + " type=" + paramString);
    }
    ProcessRecord localProcessRecord = this.mAm.getRecordForAppLocked(paramIApplicationThread);
    if ((paramIApplicationThread != null) && (localProcessRecord == null)) {
      throw new SecurityException("Unable to find app for caller " + paramIApplicationThread + " (pid=" + Binder.getCallingPid() + ") when stopping service " + paramIntent);
    }
    paramIApplicationThread = retrieveServiceLocked(paramIntent, paramString, null, Binder.getCallingPid(), Binder.getCallingUid(), paramInt, false, false, false);
    if (paramIApplicationThread != null)
    {
      if (paramIApplicationThread.record != null)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          stopServiceLocked(paramIApplicationThread.record);
          return 1;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return -1;
    }
    return 0;
  }
  
  boolean stopServiceTokenLocked(ComponentName paramComponentName, IBinder arg2, int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "stopServiceToken: " + paramComponentName + " " + ??? + " startId=" + paramInt);
    }
    paramComponentName = findServiceLocked(paramComponentName, ???, UserHandle.getCallingUserId());
    if (paramComponentName != null)
    {
      if (paramInt >= 0)
      {
        ??? = paramComponentName.findDeliveredStart(paramInt, false);
        if (??? != null)
        {
          ServiceRecord.StartItem localStartItem;
          do
          {
            if (paramComponentName.deliveredStarts.size() <= 0) {
              break;
            }
            localStartItem = (ServiceRecord.StartItem)paramComponentName.deliveredStarts.remove(0);
            localStartItem.removeUriPermissionsLocked();
          } while (localStartItem != ???);
        }
        if (paramComponentName.getLastStartId() != paramInt) {
          return false;
        }
        if (paramComponentName.deliveredStarts.size() > 0) {
          Slog.w(TAG, "stopServiceToken startId " + paramInt + " is last, but have " + paramComponentName.deliveredStarts.size() + " remaining args");
        }
      }
      synchronized (paramComponentName.stats.getBatteryStats())
      {
        paramComponentName.stats.stopRunningLocked();
        paramComponentName.startRequested = false;
        if (paramComponentName.tracker != null) {
          paramComponentName.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
        }
        paramComponentName.callStart = false;
        long l = Binder.clearCallingIdentity();
        bringDownServiceIfNeededLocked(paramComponentName, false, false);
        Binder.restoreCallingIdentity(l);
        return true;
      }
    }
    return false;
  }
  
  /* Error */
  void unbindFinishedLocked(ServiceRecord paramServiceRecord, Intent paramIntent, boolean paramBoolean)
  {
    // Byte code:
    //   0: invokestatic 1609	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore 8
    //   5: aload_1
    //   6: ifnull +141 -> 147
    //   9: new 355	android/content/Intent$FilterComparison
    //   12: dup
    //   13: aload_2
    //   14: invokespecial 1121	android/content/Intent$FilterComparison:<init>	(Landroid/content/Intent;)V
    //   17: astore_2
    //   18: aload_1
    //   19: getfield 321	com/android/server/am/ServiceRecord:bindings	Landroid/util/ArrayMap;
    //   22: aload_2
    //   23: invokevirtual 1104	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   26: checkcast 323	com/android/server/am/IntentBindRecord
    //   29: astore_2
    //   30: getstatic 136	com/android/server/am/ActivityManagerDebugConfig:DEBUG_SERVICE	Z
    //   33: ifeq +72 -> 105
    //   36: getstatic 91	com/android/server/am/ActiveServices:TAG_SERVICE	Ljava/lang/String;
    //   39: astore 10
    //   41: new 109	java/lang/StringBuilder
    //   44: dup
    //   45: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   48: ldc_w 2197
    //   51: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: aload_1
    //   55: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   58: ldc_w 2199
    //   61: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   64: aload_2
    //   65: invokevirtual 280	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   68: ldc_w 2201
    //   71: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: astore 11
    //   76: aload_2
    //   77: ifnull +76 -> 153
    //   80: aload_2
    //   81: getfield 995	com/android/server/am/IntentBindRecord:apps	Landroid/util/ArrayMap;
    //   84: invokevirtual 249	android/util/ArrayMap:size	()I
    //   87: istore 4
    //   89: aload 10
    //   91: aload 11
    //   93: iload 4
    //   95: invokevirtual 596	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   98: invokevirtual 121	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokestatic 337	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: aload_0
    //   106: getfield 194	com/android/server/am/ActiveServices:mDestroyingServices	Ljava/util/ArrayList;
    //   109: aload_1
    //   110: invokevirtual 234	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   113: istore 7
    //   115: aload_2
    //   116: ifnull +23 -> 139
    //   119: aload_2
    //   120: getfield 995	com/android/server/am/IntentBindRecord:apps	Landroid/util/ArrayMap;
    //   123: invokevirtual 249	android/util/ArrayMap:size	()I
    //   126: ifle +8 -> 134
    //   129: iload 7
    //   131: ifeq +28 -> 159
    //   134: aload_2
    //   135: iconst_1
    //   136: putfield 1004	com/android/server/am/IntentBindRecord:doRebind	Z
    //   139: aload_0
    //   140: aload_1
    //   141: iload 7
    //   143: iconst_0
    //   144: invokespecial 945	com/android/server/am/ActiveServices:serviceDoneExecutingLocked	(Lcom/android/server/am/ServiceRecord;ZZ)V
    //   147: lload 8
    //   149: invokestatic 1648	android/os/Binder:restoreCallingIdentity	(J)V
    //   152: return
    //   153: iconst_0
    //   154: istore 4
    //   156: goto -67 -> 89
    //   159: iconst_0
    //   160: istore 6
    //   162: aload_2
    //   163: getfield 995	com/android/server/am/IntentBindRecord:apps	Landroid/util/ArrayMap;
    //   166: invokevirtual 249	android/util/ArrayMap:size	()I
    //   169: iconst_1
    //   170: isub
    //   171: istore 4
    //   173: iload 6
    //   175: istore_3
    //   176: iload 4
    //   178: iflt +39 -> 217
    //   181: aload_2
    //   182: getfield 995	com/android/server/am/IntentBindRecord:apps	Landroid/util/ArrayMap;
    //   185: iload 4
    //   187: invokevirtual 253	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   190: checkcast 294	com/android/server/am/AppBindRecord
    //   193: getfield 298	com/android/server/am/AppBindRecord:client	Lcom/android/server/am/ProcessRecord;
    //   196: astore 10
    //   198: aload 10
    //   200: ifnull +33 -> 233
    //   203: aload 10
    //   205: getfield 1545	com/android/server/am/ProcessRecord:setSchedGroup	I
    //   208: istore 5
    //   210: iload 5
    //   212: ifeq +21 -> 233
    //   215: iconst_1
    //   216: istore_3
    //   217: aload_0
    //   218: aload_1
    //   219: aload_2
    //   220: iload_3
    //   221: iconst_1
    //   222: invokespecial 1010	com/android/server/am/ActiveServices:requestServiceBindingLocked	(Lcom/android/server/am/ServiceRecord;Lcom/android/server/am/IntentBindRecord;ZZ)Z
    //   225: pop
    //   226: goto -87 -> 139
    //   229: astore_2
    //   230: goto -91 -> 139
    //   233: iload 4
    //   235: iconst_1
    //   236: isub
    //   237: istore 4
    //   239: goto -66 -> 173
    //   242: astore_1
    //   243: lload 8
    //   245: invokestatic 1648	android/os/Binder:restoreCallingIdentity	(J)V
    //   248: aload_1
    //   249: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	250	0	this	ActiveServices
    //   0	250	1	paramServiceRecord	ServiceRecord
    //   0	250	2	paramIntent	Intent
    //   0	250	3	paramBoolean	boolean
    //   87	151	4	i	int
    //   208	3	5	j	int
    //   160	14	6	bool1	boolean
    //   113	29	7	bool2	boolean
    //   3	241	8	l	long
    //   39	165	10	localObject	Object
    //   74	18	11	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   217	226	229	android/os/TransactionTooLargeException
    //   9	76	242	finally
    //   80	89	242	finally
    //   89	105	242	finally
    //   105	115	242	finally
    //   119	129	242	finally
    //   134	139	242	finally
    //   139	147	242	finally
    //   162	173	242	finally
    //   181	198	242	finally
    //   203	210	242	finally
    //   217	226	242	finally
  }
  
  boolean unbindServiceLocked(IServiceConnection paramIServiceConnection)
  {
    IBinder localIBinder = paramIServiceConnection.asBinder();
    if (ActivityManagerDebugConfig.DEBUG_SERVICE) {
      Slog.v(TAG_SERVICE, "unbindService: conn=" + localIBinder);
    }
    ArrayList localArrayList = (ArrayList)this.mServiceConnections.get(localIBinder);
    if (localArrayList == null)
    {
      Slog.w(TAG, "Unbind failed: could not find connection for " + paramIServiceConnection.asBinder());
      return false;
    }
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        if (localArrayList.size() <= 0) {
          break;
        }
        paramIServiceConnection = (ConnectionRecord)localArrayList.get(0);
        removeConnectionLocked(paramIServiceConnection, null, null);
        if ((localArrayList.size() > 0) && (localArrayList.get(0) == paramIServiceConnection))
        {
          Slog.wtf(TAG, "Connection " + paramIServiceConnection + " not removed for binder " + localIBinder);
          localArrayList.remove(0);
        }
        if (paramIServiceConnection.binding.service.app != null)
        {
          if (paramIServiceConnection.binding.service.app.whitelistManager) {
            updateWhitelistManagerLocked(paramIServiceConnection.binding.service.app);
          }
          if ((paramIServiceConnection.flags & 0x8000000) != 0)
          {
            paramIServiceConnection.binding.service.app.treatLikeActivity = true;
            ActivityManagerService localActivityManagerService = this.mAm;
            ProcessRecord localProcessRecord = paramIServiceConnection.binding.service.app;
            if (!paramIServiceConnection.binding.service.app.hasClientActivities)
            {
              bool = paramIServiceConnection.binding.service.app.treatLikeActivity;
              localActivityManagerService.updateLruProcessLocked(localProcessRecord, bool, null);
            }
          }
          else
          {
            this.mAm.updateOomAdjLocked(paramIServiceConnection.binding.service.app);
            continue;
          }
          boolean bool = true;
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    Binder.restoreCallingIdentity(l);
    return true;
  }
  
  public void updateServiceConnectionActivitiesLocked(ProcessRecord paramProcessRecord)
  {
    Object localObject1 = null;
    int i = 0;
    if (i < paramProcessRecord.connections.size())
    {
      ProcessRecord localProcessRecord = ((ConnectionRecord)paramProcessRecord.connections.valueAt(i)).binding.service.app;
      Object localObject2 = localObject1;
      if (localProcessRecord != null)
      {
        if (localProcessRecord != paramProcessRecord) {
          break label64;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label64:
        if (localObject1 == null) {
          localObject2 = new ArraySet();
        }
        do
        {
          ((ArraySet)localObject2).add(localProcessRecord);
          updateServiceClientActivitiesLocked(localProcessRecord, null, false);
          break;
          localObject2 = localObject1;
        } while (!((ArraySet)localObject1).contains(localProcessRecord));
        localObject2 = localObject1;
      }
    }
  }
  
  final class ServiceDumper
  {
    private final String[] args;
    private final boolean dumpAll;
    private final String dumpPackage;
    private final FileDescriptor fd;
    private final ActivityManagerService.ItemMatcher matcher;
    private boolean needSep = false;
    private final long nowReal = SystemClock.elapsedRealtime();
    private final int opti;
    private boolean printed = false;
    private boolean printedAnything = false;
    private final PrintWriter pw;
    private final ArrayList<ServiceRecord> services = new ArrayList();
    
    ServiceDumper(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt, boolean paramBoolean, String paramString)
    {
      this.fd = paramFileDescriptor;
      this.pw = paramPrintWriter;
      this.args = paramArrayOfString;
      this.opti = paramInt;
      this.dumpAll = paramBoolean;
      this.dumpPackage = paramString;
      this.matcher = new ActivityManagerService.ItemMatcher();
      this.matcher.build(paramArrayOfString, paramInt);
      paramFileDescriptor = ActiveServices.this.mAm.mUserController.getUsers();
      paramInt = 0;
      int j = paramFileDescriptor.length;
      while (paramInt < j)
      {
        paramPrintWriter = ActiveServices.-wrap0(ActiveServices.this, paramFileDescriptor[paramInt]);
        if (paramPrintWriter.mServicesByName.size() > 0)
        {
          int i = 0;
          if (i < paramPrintWriter.mServicesByName.size())
          {
            paramArrayOfString = (ServiceRecord)paramPrintWriter.mServicesByName.valueAt(i);
            if (!this.matcher.match(paramArrayOfString, paramArrayOfString.name)) {}
            for (;;)
            {
              i += 1;
              break;
              if ((paramString == null) || (paramString.equals(paramArrayOfString.appInfo.packageName))) {
                this.services.add(paramArrayOfString);
              }
            }
          }
        }
        paramInt += 1;
      }
    }
    
    private void dumpHeaderLocked()
    {
      this.pw.println("ACTIVITY MANAGER SERVICES (dumpsys activity services)");
      if (ActiveServices.this.mLastAnrDump != null)
      {
        this.pw.println("  Last ANR service:");
        this.pw.print(ActiveServices.this.mLastAnrDump);
        this.pw.println();
      }
    }
    
    private void dumpRemainsLocked()
    {
      int i;
      Object localObject;
      if (ActiveServices.this.mPendingServices.size() > 0)
      {
        this.printed = false;
        i = 0;
        if (i < ActiveServices.this.mPendingServices.size())
        {
          localObject = (ServiceRecord)ActiveServices.this.mPendingServices.get(i);
          if (!this.matcher.match(localObject, ((ServiceRecord)localObject).name)) {}
          for (;;)
          {
            i += 1;
            break;
            if ((this.dumpPackage == null) || (this.dumpPackage.equals(((ServiceRecord)localObject).appInfo.packageName)))
            {
              this.printedAnything = true;
              if (!this.printed)
              {
                if (this.needSep) {
                  this.pw.println();
                }
                this.needSep = true;
                this.pw.println("  Pending services:");
                this.printed = true;
              }
              this.pw.print("  * Pending ");
              this.pw.println(localObject);
              ((ServiceRecord)localObject).dump(this.pw, "    ");
            }
          }
        }
        this.needSep = true;
      }
      if (ActiveServices.this.mRestartingServices.size() > 0)
      {
        this.printed = false;
        i = 0;
        if (i < ActiveServices.this.mRestartingServices.size())
        {
          localObject = (ServiceRecord)ActiveServices.this.mRestartingServices.get(i);
          if (!this.matcher.match(localObject, ((ServiceRecord)localObject).name)) {}
          for (;;)
          {
            i += 1;
            break;
            if ((this.dumpPackage == null) || (this.dumpPackage.equals(((ServiceRecord)localObject).appInfo.packageName)))
            {
              this.printedAnything = true;
              if (!this.printed)
              {
                if (this.needSep) {
                  this.pw.println();
                }
                this.needSep = true;
                this.pw.println("  Restarting services:");
                this.printed = true;
              }
              this.pw.print("  * Restarting ");
              this.pw.println(localObject);
              ((ServiceRecord)localObject).dump(this.pw, "    ");
            }
          }
        }
        this.needSep = true;
      }
      if (ActiveServices.this.mDestroyingServices.size() > 0)
      {
        this.printed = false;
        i = 0;
        if (i < ActiveServices.this.mDestroyingServices.size())
        {
          localObject = (ServiceRecord)ActiveServices.this.mDestroyingServices.get(i);
          if (!this.matcher.match(localObject, ((ServiceRecord)localObject).name)) {}
          for (;;)
          {
            i += 1;
            break;
            if ((this.dumpPackage == null) || (this.dumpPackage.equals(((ServiceRecord)localObject).appInfo.packageName)))
            {
              this.printedAnything = true;
              if (!this.printed)
              {
                if (this.needSep) {
                  this.pw.println();
                }
                this.needSep = true;
                this.pw.println("  Destroying services:");
                this.printed = true;
              }
              this.pw.print("  * Destroy ");
              this.pw.println(localObject);
              ((ServiceRecord)localObject).dump(this.pw, "    ");
            }
          }
        }
        this.needSep = true;
      }
      if (this.dumpAll)
      {
        this.printed = false;
        i = 0;
        while (i < ActiveServices.this.mServiceConnections.size())
        {
          localObject = (ArrayList)ActiveServices.this.mServiceConnections.valueAt(i);
          int j = 0;
          if (j < ((ArrayList)localObject).size())
          {
            ConnectionRecord localConnectionRecord = (ConnectionRecord)((ArrayList)localObject).get(j);
            if (!this.matcher.match(localConnectionRecord.binding.service, localConnectionRecord.binding.service.name)) {}
            for (;;)
            {
              j += 1;
              break;
              if ((this.dumpPackage == null) || ((localConnectionRecord.binding.client != null) && (this.dumpPackage.equals(localConnectionRecord.binding.client.info.packageName))))
              {
                this.printedAnything = true;
                if (!this.printed)
                {
                  if (this.needSep) {
                    this.pw.println();
                  }
                  this.needSep = true;
                  this.pw.println("  Connection bindings to services:");
                  this.printed = true;
                }
                this.pw.print("  * ");
                this.pw.println(localConnectionRecord);
                localConnectionRecord.dump(this.pw, "    ");
              }
            }
          }
          i += 1;
        }
      }
      if (!this.printedAnything) {
        this.pw.println("  (nothing)");
      }
    }
    
    private void dumpServiceClient(ServiceRecord paramServiceRecord)
    {
      Object localObject = paramServiceRecord.app;
      if (localObject == null) {
        return;
      }
      IApplicationThread localIApplicationThread = ((ProcessRecord)localObject).thread;
      if (localIApplicationThread == null) {
        return;
      }
      this.pw.println("    Client:");
      this.pw.flush();
      for (;;)
      {
        try
        {
          localObject = new TransferPipe();
        }
        catch (IOException paramServiceRecord)
        {
          this.pw.println("      Failure while dumping the service: " + paramServiceRecord);
          continue;
        }
        catch (RemoteException paramServiceRecord)
        {
          this.pw.println("      Got a RemoteException while dumping the service");
          continue;
        }
        try
        {
          localIApplicationThread.dumpService(((TransferPipe)localObject).getWriteFd().getFileDescriptor(), paramServiceRecord, this.args);
          ((TransferPipe)localObject).setBufferPrefix("      ");
          ((TransferPipe)localObject).go(this.fd, 2000L);
          ((TransferPipe)localObject).kill();
          this.needSep = true;
          return;
        }
        finally
        {
          ((TransferPipe)localObject).kill();
        }
      }
    }
    
    private void dumpServiceLocalLocked(ServiceRecord paramServiceRecord)
    {
      dumpUserHeaderLocked(paramServiceRecord.userId);
      this.pw.print("  * ");
      this.pw.println(paramServiceRecord);
      if (this.dumpAll)
      {
        paramServiceRecord.dump(this.pw, "    ");
        this.needSep = true;
      }
      for (;;)
      {
        return;
        this.pw.print("    app=");
        this.pw.println(paramServiceRecord.app);
        this.pw.print("    created=");
        TimeUtils.formatDuration(paramServiceRecord.createTime, this.nowReal, this.pw);
        this.pw.print(" started=");
        this.pw.print(paramServiceRecord.startRequested);
        this.pw.print(" connections=");
        this.pw.println(paramServiceRecord.connections.size());
        if (paramServiceRecord.connections.size() > 0)
        {
          this.pw.println("    Connections:");
          int i = 0;
          while (i < paramServiceRecord.connections.size())
          {
            ArrayList localArrayList = (ArrayList)paramServiceRecord.connections.valueAt(i);
            int j = 0;
            if (j < localArrayList.size())
            {
              Object localObject = (ConnectionRecord)localArrayList.get(j);
              this.pw.print("      ");
              this.pw.print(((ConnectionRecord)localObject).binding.intent.intent.getIntent().toShortString(false, false, false, false));
              this.pw.print(" -> ");
              localObject = ((ConnectionRecord)localObject).binding.client;
              PrintWriter localPrintWriter = this.pw;
              if (localObject != null) {}
              for (localObject = ((ProcessRecord)localObject).toShortString();; localObject = "null")
              {
                localPrintWriter.println((String)localObject);
                j += 1;
                break;
              }
            }
            i += 1;
          }
        }
      }
    }
    
    private void dumpUserHeaderLocked(int paramInt)
    {
      if (!this.printed)
      {
        if (this.printedAnything) {
          this.pw.println();
        }
        this.pw.println("  User " + paramInt + " active services:");
        this.printed = true;
      }
      this.printedAnything = true;
      if (this.needSep) {
        this.pw.println();
      }
    }
    
    private void dumpUserRemainsLocked(int paramInt)
    {
      ActiveServices.ServiceMap localServiceMap = ActiveServices.-wrap0(ActiveServices.this, paramInt);
      this.printed = false;
      int i = 0;
      int j = localServiceMap.mDelayedStartList.size();
      ServiceRecord localServiceRecord;
      if (i < j)
      {
        localServiceRecord = (ServiceRecord)localServiceMap.mDelayedStartList.get(i);
        if (!this.matcher.match(localServiceRecord, localServiceRecord.name)) {}
        for (;;)
        {
          i += 1;
          break;
          if ((this.dumpPackage == null) || (this.dumpPackage.equals(localServiceRecord.appInfo.packageName)))
          {
            if (!this.printed)
            {
              if (this.printedAnything) {
                this.pw.println();
              }
              this.pw.println("  User " + paramInt + " delayed start services:");
              this.printed = true;
            }
            this.printedAnything = true;
            this.pw.print("  * Delayed start ");
            this.pw.println(localServiceRecord);
          }
        }
      }
      this.printed = false;
      i = 0;
      j = localServiceMap.mStartingBackground.size();
      if (i < j)
      {
        localServiceRecord = (ServiceRecord)localServiceMap.mStartingBackground.get(i);
        if (!this.matcher.match(localServiceRecord, localServiceRecord.name)) {}
        for (;;)
        {
          i += 1;
          break;
          if ((this.dumpPackage == null) || (this.dumpPackage.equals(localServiceRecord.appInfo.packageName)))
          {
            if (!this.printed)
            {
              if (this.printedAnything) {
                this.pw.println();
              }
              this.pw.println("  User " + paramInt + " starting in background:");
              this.printed = true;
            }
            this.printedAnything = true;
            this.pw.print("  * Starting bg ");
            this.pw.println(localServiceRecord);
          }
        }
      }
    }
    
    void dumpLocked()
    {
      dumpHeaderLocked();
      for (;;)
      {
        int k;
        int j;
        int i;
        int m;
        try
        {
          int[] arrayOfInt = ActiveServices.this.mAm.mUserController.getUsers();
          k = arrayOfInt.length;
          j = 0;
        }
        catch (Exception localException)
        {
          ServiceRecord localServiceRecord;
          Slog.w(ActiveServices.-get2(), "Exception in dumpServicesLocked", localException);
          dumpRemainsLocked();
          return;
        }
        if ((i < this.services.size()) && (((ServiceRecord)this.services.get(i)).userId != m))
        {
          i += 1;
        }
        else
        {
          this.printed = false;
          if (i < this.services.size())
          {
            this.needSep = false;
            if (i < this.services.size())
            {
              localServiceRecord = (ServiceRecord)this.services.get(i);
              i += 1;
              if (localServiceRecord.userId == m) {}
            }
            else
            {
              this.needSep |= this.printed;
            }
          }
          else
          {
            dumpUserRemainsLocked(m);
            j += 1;
            break label178;
          }
          dumpServiceLocalLocked(localServiceRecord);
          continue;
          label178:
          if (j < k)
          {
            m = localException[j];
            i = 0;
          }
        }
      }
    }
    
    void dumpWithClient()
    {
      synchronized (ActiveServices.this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        dumpHeaderLocked();
        ActivityManagerService.resetPriorityAfterLockedSection();
        try
        {
          ??? = ActiveServices.this.mAm.mUserController.getUsers();
          k = ???.length;
          j = 0;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            int k;
            int j;
            synchronized (ActiveServices.this.mAm)
            {
              int n;
              ServiceRecord localServiceRecord;
              ActivityManagerService.boostPriorityForLockedSection();
              dumpUserRemainsLocked(m);
              ActivityManagerService.resetPriorityAfterLockedSection();
              j += 1;
              break label316;
              synchronized (ActiveServices.this.mAm)
              {
                ActivityManagerService.boostPriorityForLockedSection();
                dumpServiceLocalLocked(localServiceRecord);
                ActivityManagerService.resetPriorityAfterLockedSection();
                dumpServiceClient(localServiceRecord);
                continue;
                localException = localException;
                Slog.w(ActiveServices.-get2(), "Exception in dumpServicesLocked", localException);
              }
            }
            do
            {
              synchronized (ActiveServices.this.mAm)
              {
                ActivityManagerService.boostPriorityForLockedSection();
                dumpRemainsLocked();
                ActivityManagerService.resetPriorityAfterLockedSection();
                return;
                localObject2 = finally;
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw ((Throwable)localObject2);
                localObject3 = finally;
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw ((Throwable)localObject3);
              }
            } while (j >= k);
            int m = localObject3[j];
            int i = 0;
          }
        }
        if (i < this.services.size())
        {
          n = ((ServiceRecord)this.services.get(i)).userId;
          if (n != m) {
            i += 1;
          }
        }
      }
    }
  }
  
  private final class ServiceLookupResult
  {
    final String permission;
    final ServiceRecord record;
    
    ServiceLookupResult(ServiceRecord paramServiceRecord, String paramString)
    {
      this.record = paramServiceRecord;
      this.permission = paramString;
    }
  }
  
  class ServiceMap
    extends Handler
  {
    static final int MSG_BG_START_TIMEOUT = 1;
    final ArrayList<ServiceRecord> mDelayedStartList = new ArrayList();
    final ArrayMap<Intent.FilterComparison, ServiceRecord> mServicesByIntent = new ArrayMap();
    final ArrayMap<ComponentName, ServiceRecord> mServicesByName = new ArrayMap();
    final ArrayList<ServiceRecord> mStartingBackground = new ArrayList();
    final int mUserId;
    
    ServiceMap(Looper paramLooper, int paramInt)
    {
      super();
      this.mUserId = paramInt;
    }
    
    void ensureNotStartingBackground(ServiceRecord paramServiceRecord)
    {
      if (this.mStartingBackground.remove(paramServiceRecord))
      {
        if (ActiveServices.-get1()) {
          Slog.v(ActiveServices.-get3(), "No longer background starting: " + paramServiceRecord);
        }
        rescheduleDelayedStarts();
      }
      if ((this.mDelayedStartList.remove(paramServiceRecord)) && (ActiveServices.-get1())) {
        Slog.v(ActiveServices.-get3(), "No longer delaying start: " + paramServiceRecord);
      }
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        return;
      }
      synchronized (ActiveServices.this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        rescheduleDelayedStarts();
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    void rescheduleDelayedStarts()
    {
      removeMessages(1);
      long l = SystemClock.uptimeMillis();
      int i = 0;
      ServiceRecord localServiceRecord;
      int k;
      for (int j = this.mStartingBackground.size(); i < j; j = k)
      {
        localServiceRecord = (ServiceRecord)this.mStartingBackground.get(i);
        k = j;
        int m = i;
        if (localServiceRecord.startingBgTimeout <= l)
        {
          Slog.i(ActiveServices.-get2(), "Waited long enough for: " + localServiceRecord);
          this.mStartingBackground.remove(i);
          k = j - 1;
          m = i - 1;
        }
        i = m + 1;
      }
      for (;;)
      {
        localServiceRecord.delayed = false;
        try
        {
          ActiveServices.this.startServiceInnerLocked(this, ((ServiceRecord.StartItem)localServiceRecord.pendingStarts.get(0)).intent, localServiceRecord, false, true);
          if ((this.mDelayedStartList.size() > 0) && (this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground))
          {
            localServiceRecord = (ServiceRecord)this.mDelayedStartList.remove(0);
            if (ActiveServices.-get1()) {
              Slog.v(ActiveServices.-get3(), "REM FR DELAY LIST (exec next): " + localServiceRecord);
            }
            if (localServiceRecord.pendingStarts.size() <= 0) {
              Slog.w(ActiveServices.-get2(), "**** NO PENDING STARTS! " + localServiceRecord + " startReq=" + localServiceRecord.startRequested + " delayedStop=" + localServiceRecord.delayedStop);
            }
            if ((!ActiveServices.-get0()) || (this.mDelayedStartList.size() <= 0)) {
              continue;
            }
            Slog.v(ActiveServices.-get3(), "Remaining delayed list:");
            i = 0;
            while (i < this.mDelayedStartList.size())
            {
              Slog.v(ActiveServices.-get3(), "  #" + i + ": " + this.mDelayedStartList.get(i));
              i += 1;
            }
          }
          if (this.mStartingBackground.size() > 0)
          {
            localServiceRecord = (ServiceRecord)this.mStartingBackground.get(0);
            if (localServiceRecord.startingBgTimeout <= l) {
              break label495;
            }
            l = localServiceRecord.startingBgTimeout;
          }
          label495:
          for (;;)
          {
            if (ActiveServices.-get0()) {
              Slog.v(ActiveServices.-get3(), "Top bg start is " + localServiceRecord + ", can delay others up to " + l);
            }
            sendMessageAtTime(obtainMessage(1), l);
            if (this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
              ActiveServices.this.mAm.backgroundServicesFinishedLocked(this.mUserId);
            }
            return;
          }
        }
        catch (TransactionTooLargeException localTransactionTooLargeException)
        {
          for (;;) {}
        }
      }
    }
  }
  
  private class ServiceRestarter
    implements Runnable
  {
    private ServiceRecord mService;
    
    private ServiceRestarter() {}
    
    public void run()
    {
      synchronized (ActiveServices.this.mAm)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActiveServices.this.performServiceRestartLocked(this.mService);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    void setService(ServiceRecord paramServiceRecord)
    {
      this.mService = paramServiceRecord;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActiveServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */