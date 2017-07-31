package com.android.server.am;

import android.app.ActivityManager.ProcessErrorStateInfo;
import android.app.ActivityOptions;
import android.app.ActivityThread;
import android.app.ApplicationErrorReport;
import android.app.ApplicationErrorReport.AnrInfo;
import android.app.ApplicationErrorReport.CrashInfo;
import android.app.Dialog;
import android.app.IActivityController;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.app.ProcessMap;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.ProcessCpuTracker;
import com.android.server.Watchdog;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

class AppErrors
{
  private static final String TAG = "ActivityManager";
  private ArraySet<String> mAppsNotReportingCrashes;
  private final ProcessMap<BadProcessInfo> mBadProcesses = new ProcessMap();
  private final Context mContext;
  private final ProcessMap<Long> mProcessCrashTimes = new ProcessMap();
  private final ProcessMap<Long> mProcessCrashTimesPersistent = new ProcessMap();
  private final ActivityManagerService mService;
  SimpleDateFormat mTraceDateFormat = new SimpleDateFormat("dd_MMM_HH_mm_ss.SSS");
  
  AppErrors(Context paramContext, ActivityManagerService paramActivityManagerService)
  {
    this.mService = paramActivityManagerService;
    this.mContext = paramContext;
  }
  
  private ApplicationErrorReport createAppErrorReportLocked(ProcessRecord paramProcessRecord, long paramLong, ApplicationErrorReport.CrashInfo paramCrashInfo)
  {
    boolean bool = false;
    if (paramProcessRecord.errorReportReceiver == null) {
      return null;
    }
    ApplicationErrorReport localApplicationErrorReport;
    if ((paramProcessRecord.crashing) || (paramProcessRecord.notResponding))
    {
      localApplicationErrorReport = new ApplicationErrorReport();
      localApplicationErrorReport.packageName = paramProcessRecord.info.packageName;
      localApplicationErrorReport.installerPackageName = paramProcessRecord.errorReportReceiver.getPackageName();
      localApplicationErrorReport.processName = paramProcessRecord.processName;
      localApplicationErrorReport.time = paramLong;
      if ((paramProcessRecord.info.flags & 0x1) != 0) {
        bool = true;
      }
      localApplicationErrorReport.systemApp = bool;
      if ((!paramProcessRecord.crashing) && (!paramProcessRecord.forceCrashReport)) {
        break label135;
      }
      localApplicationErrorReport.type = 1;
      localApplicationErrorReport.crashInfo = paramCrashInfo;
    }
    label135:
    while (!paramProcessRecord.notResponding)
    {
      return localApplicationErrorReport;
      if (paramProcessRecord.forceCrashReport) {
        break;
      }
      return null;
    }
    localApplicationErrorReport.type = 2;
    localApplicationErrorReport.anrInfo = new ApplicationErrorReport.AnrInfo();
    localApplicationErrorReport.anrInfo.activity = paramProcessRecord.notRespondingReport.tag;
    localApplicationErrorReport.anrInfo.cause = paramProcessRecord.notRespondingReport.shortMsg;
    localApplicationErrorReport.anrInfo.info = paramProcessRecord.notRespondingReport.longMsg;
    return localApplicationErrorReport;
  }
  
  private void doSysRq(char paramChar)
  {
    try
    {
      FileWriter localFileWriter = new FileWriter("/proc/sysrq-trigger");
      localFileWriter.write(paramChar);
      localFileWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w(TAG, "Failed to write to /proc/sysrq-trigger", localIOException);
    }
  }
  
  private ActivityManager.ProcessErrorStateInfo generateProcessError(ProcessRecord paramProcessRecord, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    ActivityManager.ProcessErrorStateInfo localProcessErrorStateInfo = new ActivityManager.ProcessErrorStateInfo();
    localProcessErrorStateInfo.condition = paramInt;
    localProcessErrorStateInfo.processName = paramProcessRecord.processName;
    localProcessErrorStateInfo.pid = paramProcessRecord.pid;
    localProcessErrorStateInfo.uid = paramProcessRecord.info.uid;
    localProcessErrorStateInfo.tag = paramString1;
    localProcessErrorStateInfo.shortMsg = paramString2;
    localProcessErrorStateInfo.longMsg = paramString3;
    localProcessErrorStateInfo.stackTrace = paramString4;
    return localProcessErrorStateInfo;
  }
  
  private boolean handleAppCrashInActivityController(ProcessRecord paramProcessRecord, ApplicationErrorReport.CrashInfo paramCrashInfo, String paramString1, String paramString2, String paramString3, long paramLong)
  {
    if (this.mService.mController == null) {
      return false;
    }
    if (paramProcessRecord != null) {}
    for (;;)
    {
      int i;
      int j;
      try
      {
        str = paramProcessRecord.processName;
        if (paramProcessRecord != null)
        {
          i = paramProcessRecord.pid;
          if (paramProcessRecord != null)
          {
            j = paramProcessRecord.info.uid;
            if (this.mService.mController.appCrashed(str, i, paramString1, paramString2, paramLong, paramCrashInfo.stackTrace)) {
              break label240;
            }
            if ((!"1".equals(SystemProperties.get("ro.debuggable", "0"))) || (!"Native crash".equals(paramCrashInfo.exceptionClassName))) {
              continue;
            }
            Slog.w(TAG, "Skip killing native crashed app " + str + "(" + i + ") during testing");
            break label254;
          }
        }
        else
        {
          i = Binder.getCallingPid();
          continue;
        }
        j = Binder.getCallingUid();
        continue;
        Slog.w(TAG, "Force-killing crashed app " + str + " at watcher's request");
        if (paramProcessRecord == null) {
          break label242;
        }
        if (makeAppCrashingLocked(paramProcessRecord, paramString1, paramString2, paramString3, null)) {
          break label254;
        }
        paramProcessRecord.kill("crash", true);
      }
      catch (RemoteException paramProcessRecord)
      {
        this.mService.mController = null;
        Watchdog.getInstance().setActivityController(null);
      }
      label240:
      return false;
      label242:
      Process.killProcess(i);
      ActivityManagerService.killProcessGroup(j, i);
      label254:
      return true;
      String str = null;
    }
  }
  
  private boolean isKernelLoadingHigh()
  {
    boolean bool = false;
    int i = this.mService.mProcessCpuTracker.getLastUserTime();
    int k = this.mService.mProcessCpuTracker.getLastSystemTime();
    int j = i + k + this.mService.mProcessCpuTracker.getLastIoWaitTime() + this.mService.mProcessCpuTracker.getLastIrqTime() + this.mService.mProcessCpuTracker.getLastSoftIrqTime() + this.mService.mProcessCpuTracker.getLastIdleTime();
    i = j;
    if (j == 0) {
      i = 1;
    }
    if (k * 100 / i > 80) {
      bool = true;
    }
    return bool;
  }
  
  private boolean makeAppCrashingLocked(ProcessRecord paramProcessRecord, String paramString1, String paramString2, String paramString3, AppErrorDialog.Data paramData)
  {
    paramProcessRecord.crashing = true;
    paramProcessRecord.crashingReport = generateProcessError(paramProcessRecord, 1, null, paramString1, paramString2, paramString3);
    startAppProblemLocked(paramProcessRecord);
    paramProcessRecord.stopFreezingAllLocked();
    return handleAppCrashLocked(paramProcessRecord, "force-crash", paramString1, paramString2, paramString3, paramData);
  }
  
  private void makeAppNotRespondingLocked(ProcessRecord paramProcessRecord, String paramString1, String paramString2, String paramString3)
  {
    paramProcessRecord.notResponding = true;
    paramProcessRecord.notRespondingReport = generateProcessError(paramProcessRecord, 2, paramString1, paramString2, paramString3, null);
    startAppProblemLocked(paramProcessRecord);
    paramProcessRecord.stopFreezingAllLocked();
  }
  
  /* Error */
  final void appNotResponding(ProcessRecord paramProcessRecord, ActivityRecord paramActivityRecord1, ActivityRecord arg3, boolean paramBoolean, String paramString)
  {
    // Byte code:
    //   0: new 347	java/util/ArrayList
    //   3: dup
    //   4: iconst_5
    //   5: invokespecial 349	java/util/ArrayList:<init>	(I)V
    //   8: astore 12
    //   10: new 351	android/util/SparseArray
    //   13: dup
    //   14: bipush 20
    //   16: invokespecial 352	android/util/SparseArray:<init>	(I)V
    //   19: astore 15
    //   21: aload_1
    //   22: getfield 355	com/android/server/am/ProcessRecord:permRequestCount	I
    //   25: ifle +4 -> 29
    //   28: return
    //   29: aload_0
    //   30: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   33: getfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   36: ifnull +46 -> 82
    //   39: aload_0
    //   40: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   43: getfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   46: aload_1
    //   47: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   50: aload_1
    //   51: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   54: aload 5
    //   56: invokeinterface 359 4 0
    //   61: ifge +21 -> 82
    //   64: aload_1
    //   65: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   68: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   71: if_icmpeq +11 -> 82
    //   74: aload_1
    //   75: ldc_w 364
    //   78: iconst_1
    //   79: invokevirtual 278	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   82: invokestatic 370	android/os/SystemClock:uptimeMillis	()J
    //   85: lstore 10
    //   87: aload_0
    //   88: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   91: invokevirtual 373	com/android/server/am/ActivityManagerService:updateCpuStatsNow	()V
    //   94: aload_0
    //   95: getfield 57	com/android/server/am/AppErrors:mContext	Landroid/content/Context;
    //   98: invokevirtual 379	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   101: ldc_w 381
    //   104: iconst_0
    //   105: invokestatic 387	android/provider/Settings$Secure:getInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)I
    //   108: ifeq +93 -> 201
    //   111: iconst_1
    //   112: istore 6
    //   114: aload_0
    //   115: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   118: astore 13
    //   120: aload 13
    //   122: monitorenter
    //   123: invokestatic 390	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   126: aload_0
    //   127: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   130: getfield 393	com/android/server/am/ActivityManagerService:mShuttingDown	Z
    //   133: ifeq +74 -> 207
    //   136: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   139: new 235	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   146: ldc_w 395
    //   149: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload_1
    //   153: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   156: ldc_w 400
    //   159: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: aload 5
    //   164: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   170: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   173: pop
    //   174: aload 13
    //   176: monitorexit
    //   177: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   180: return
    //   181: astore 13
    //   183: aload_0
    //   184: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   187: aconst_null
    //   188: putfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   191: invokestatic 284	com/android/server/Watchdog:getInstance	()Lcom/android/server/Watchdog;
    //   194: aconst_null
    //   195: invokevirtual 288	com/android/server/Watchdog:setActivityController	(Landroid/app/IActivityController;)V
    //   198: goto -116 -> 82
    //   201: iconst_0
    //   202: istore 6
    //   204: goto -90 -> 114
    //   207: aload_1
    //   208: getfield 72	com/android/server/am/ProcessRecord:notResponding	Z
    //   211: ifeq +48 -> 259
    //   214: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   217: new 235	java/lang/StringBuilder
    //   220: dup
    //   221: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   224: ldc_w 408
    //   227: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   230: aload_1
    //   231: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   234: ldc_w 400
    //   237: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: aload 5
    //   242: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   248: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   251: pop
    //   252: aload 13
    //   254: monitorexit
    //   255: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   258: return
    //   259: aload_1
    //   260: getfield 69	com/android/server/am/ProcessRecord:crashing	Z
    //   263: ifeq +48 -> 311
    //   266: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   269: new 235	java/lang/StringBuilder
    //   272: dup
    //   273: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   276: ldc_w 410
    //   279: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   282: aload_1
    //   283: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   286: ldc_w 400
    //   289: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: aload 5
    //   294: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   297: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   300: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   303: pop
    //   304: aload 13
    //   306: monitorexit
    //   307: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   310: return
    //   311: aload_1
    //   312: getfield 413	com/android/server/am/ProcessRecord:killedByAm	Z
    //   315: ifeq +48 -> 363
    //   318: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   321: new 235	java/lang/StringBuilder
    //   324: dup
    //   325: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   328: ldc_w 415
    //   331: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   334: aload_1
    //   335: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   338: ldc_w 400
    //   341: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   344: aload 5
    //   346: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   349: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   352: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   355: pop
    //   356: aload 13
    //   358: monitorexit
    //   359: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   362: return
    //   363: aload_1
    //   364: getfield 418	com/android/server/am/ProcessRecord:killed	Z
    //   367: ifeq +48 -> 415
    //   370: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   373: new 235	java/lang/StringBuilder
    //   376: dup
    //   377: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   380: ldc_w 420
    //   383: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: aload_1
    //   387: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   390: ldc_w 400
    //   393: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   396: aload 5
    //   398: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   401: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   404: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   407: pop
    //   408: aload 13
    //   410: monitorexit
    //   411: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   414: return
    //   415: aload_1
    //   416: iconst_1
    //   417: putfield 72	com/android/server/am/ProcessRecord:notResponding	Z
    //   420: sipush 30008
    //   423: iconst_5
    //   424: anewarray 4	java/lang/Object
    //   427: dup
    //   428: iconst_0
    //   429: aload_1
    //   430: getfield 423	com/android/server/am/ProcessRecord:userId	I
    //   433: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   436: aastore
    //   437: dup
    //   438: iconst_1
    //   439: aload_1
    //   440: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   443: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   446: aastore
    //   447: dup
    //   448: iconst_2
    //   449: aload_1
    //   450: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   453: aastore
    //   454: dup
    //   455: iconst_3
    //   456: aload_1
    //   457: getfield 79	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   460: getfield 106	android/content/pm/ApplicationInfo:flags	I
    //   463: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   466: aastore
    //   467: dup
    //   468: iconst_4
    //   469: aload 5
    //   471: aastore
    //   472: invokestatic 435	android/util/EventLog:writeEvent	(I[Ljava/lang/Object;)I
    //   475: pop
    //   476: aload 12
    //   478: aload_1
    //   479: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   482: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   485: invokevirtual 438	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   488: pop
    //   489: iload 6
    //   491: ifne +1316 -> 1807
    //   494: aload_1
    //   495: invokevirtual 441	com/android/server/am/ProcessRecord:isInterestingToUserLocked	()Z
    //   498: ifeq +263 -> 761
    //   501: goto +1306 -> 1807
    //   504: ldc_w 443
    //   507: iconst_0
    //   508: invokestatic 447	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   511: ifeq +6 -> 517
    //   514: iconst_0
    //   515: istore 6
    //   517: iload 6
    //   519: ifne +314 -> 833
    //   522: aload_1
    //   523: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   526: istore 8
    //   528: iload 8
    //   530: istore 7
    //   532: aload_3
    //   533: ifnull +37 -> 570
    //   536: iload 8
    //   538: istore 7
    //   540: aload_3
    //   541: getfield 453	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   544: ifnull +26 -> 570
    //   547: iload 8
    //   549: istore 7
    //   551: aload_3
    //   552: getfield 453	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   555: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   558: ifle +12 -> 570
    //   561: aload_3
    //   562: getfield 453	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   565: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   568: istore 7
    //   570: iload 7
    //   572: aload_1
    //   573: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   576: if_icmpeq +14 -> 590
    //   579: aload 12
    //   581: iload 7
    //   583: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   586: invokevirtual 438	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   589: pop
    //   590: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   593: aload_1
    //   594: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   597: if_icmpeq +23 -> 620
    //   600: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   603: iload 7
    //   605: if_icmpeq +15 -> 620
    //   608: aload 12
    //   610: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   613: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   616: invokevirtual 438	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   619: pop
    //   620: aload_0
    //   621: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   624: getfield 457	com/android/server/am/ActivityManagerService:mLruProcesses	Ljava/util/ArrayList;
    //   627: invokevirtual 460	java/util/ArrayList:size	()I
    //   630: iconst_1
    //   631: isub
    //   632: istore 8
    //   634: iload 8
    //   636: iflt +197 -> 833
    //   639: aload_0
    //   640: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   643: getfield 457	com/android/server/am/ActivityManagerService:mLruProcesses	Ljava/util/ArrayList;
    //   646: iload 8
    //   648: invokevirtual 463	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   651: checkcast 61	com/android/server/am/ProcessRecord
    //   654: astore 14
    //   656: aload 14
    //   658: ifnull +1155 -> 1813
    //   661: aload 14
    //   663: getfield 467	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   666: ifnull +1147 -> 1813
    //   669: aload 14
    //   671: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   674: istore 9
    //   676: iload 9
    //   678: ifle +1135 -> 1813
    //   681: iload 9
    //   683: aload_1
    //   684: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   687: if_icmpeq +1126 -> 1813
    //   690: iload 9
    //   692: iload 7
    //   694: if_icmpeq +1119 -> 1813
    //   697: iload 9
    //   699: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   702: if_icmpeq +1111 -> 1813
    //   705: aload 14
    //   707: getfield 470	com/android/server/am/ProcessRecord:persistent	Z
    //   710: ifeq +67 -> 777
    //   713: aload 12
    //   715: iload 9
    //   717: invokestatic 429	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   720: invokevirtual 438	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   723: pop
    //   724: getstatic 475	com/android/server/am/ActivityManagerDebugConfig:DEBUG_ANR	Z
    //   727: ifeq +1086 -> 1813
    //   730: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   733: new 235	java/lang/StringBuilder
    //   736: dup
    //   737: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   740: ldc_w 477
    //   743: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   746: aload 14
    //   748: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   751: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   754: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   757: pop
    //   758: goto +1055 -> 1813
    //   761: aload_1
    //   762: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   765: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   768: if_icmpeq +1039 -> 1807
    //   771: iconst_1
    //   772: istore 6
    //   774: goto -270 -> 504
    //   777: aload 15
    //   779: iload 9
    //   781: getstatic 483	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   784: invokevirtual 487	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   787: getstatic 475	com/android/server/am/ActivityManagerDebugConfig:DEBUG_ANR	Z
    //   790: ifeq +1023 -> 1813
    //   793: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   796: new 235	java/lang/StringBuilder
    //   799: dup
    //   800: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   803: ldc_w 489
    //   806: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   809: aload 14
    //   811: invokevirtual 398	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   814: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   817: invokestatic 403	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   820: pop
    //   821: goto +992 -> 1813
    //   824: astore_1
    //   825: aload 13
    //   827: monitorexit
    //   828: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   831: aload_1
    //   832: athrow
    //   833: aload 13
    //   835: monitorexit
    //   836: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   839: new 235	java/lang/StringBuilder
    //   842: dup
    //   843: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   846: astore 13
    //   848: aload 13
    //   850: iconst_0
    //   851: invokevirtual 492	java/lang/StringBuilder:setLength	(I)V
    //   854: aload 13
    //   856: ldc_w 494
    //   859: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   862: aload_1
    //   863: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   866: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   869: pop
    //   870: aload_2
    //   871: ifnull +32 -> 903
    //   874: aload_2
    //   875: getfield 497	com/android/server/am/ActivityRecord:shortComponentName	Ljava/lang/String;
    //   878: ifnull +25 -> 903
    //   881: aload 13
    //   883: ldc_w 499
    //   886: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   889: aload_2
    //   890: getfield 497	com/android/server/am/ActivityRecord:shortComponentName	Ljava/lang/String;
    //   893: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   896: ldc_w 501
    //   899: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   902: pop
    //   903: aload 13
    //   905: ldc_w 503
    //   908: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   911: pop
    //   912: aload 13
    //   914: ldc_w 505
    //   917: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   920: aload_1
    //   921: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   924: invokevirtual 247	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   927: ldc_w 503
    //   930: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   933: pop
    //   934: aload 5
    //   936: ifnull +23 -> 959
    //   939: aload 13
    //   941: ldc_w 507
    //   944: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   947: aload 5
    //   949: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   952: ldc_w 503
    //   955: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   958: pop
    //   959: aload_3
    //   960: ifnull +30 -> 990
    //   963: aload_3
    //   964: aload_2
    //   965: if_acmpeq +25 -> 990
    //   968: aload 13
    //   970: ldc_w 509
    //   973: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   976: aload_3
    //   977: getfield 497	com/android/server/am/ActivityRecord:shortComponentName	Ljava/lang/String;
    //   980: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   983: ldc_w 503
    //   986: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   989: pop
    //   990: new 305	com/android/internal/os/ProcessCpuTracker
    //   993: dup
    //   994: iconst_1
    //   995: invokespecial 512	com/android/internal/os/ProcessCpuTracker:<init>	(Z)V
    //   998: astore 14
    //   1000: getstatic 516	com/android/server/Watchdog:NATIVE_STACKS_OF_INTEREST	[Ljava/lang/String;
    //   1003: astore 16
    //   1005: iload 6
    //   1007: ifeq +175 -> 1182
    //   1010: aload_0
    //   1011: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1014: astore 16
    //   1016: iconst_1
    //   1017: aload 12
    //   1019: aconst_null
    //   1020: aload 15
    //   1022: aconst_null
    //   1023: invokestatic 520	com/android/server/am/ActivityManagerService:dumpStackTraces	(ZLjava/util/ArrayList;Lcom/android/internal/os/ProcessCpuTracker;Landroid/util/SparseArray;[Ljava/lang/String;)Ljava/io/File;
    //   1026: astore 12
    //   1028: aload_0
    //   1029: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1032: invokevirtual 373	com/android/server/am/ActivityManagerService:updateCpuStatsNow	()V
    //   1035: aload_0
    //   1036: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1039: getfield 303	com/android/server/am/ActivityManagerService:mProcessCpuTracker	Lcom/android/internal/os/ProcessCpuTracker;
    //   1042: astore 15
    //   1044: aload 15
    //   1046: monitorenter
    //   1047: aload_0
    //   1048: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1051: getfield 303	com/android/server/am/ActivityManagerService:mProcessCpuTracker	Lcom/android/internal/os/ProcessCpuTracker;
    //   1054: lload 10
    //   1056: invokevirtual 524	com/android/internal/os/ProcessCpuTracker:printCurrentState	(J)Ljava/lang/String;
    //   1059: astore 16
    //   1061: aload 15
    //   1063: monitorexit
    //   1064: aload 13
    //   1066: aload 14
    //   1068: invokevirtual 527	com/android/internal/os/ProcessCpuTracker:printCurrentLoad	()Ljava/lang/String;
    //   1071: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1074: pop
    //   1075: aload 13
    //   1077: aload 16
    //   1079: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1082: pop
    //   1083: aload 13
    //   1085: aload 14
    //   1087: lload 10
    //   1089: invokevirtual 524	com/android/internal/os/ProcessCpuTracker:printCurrentState	(J)Ljava/lang/String;
    //   1092: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1095: pop
    //   1096: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   1099: aload 13
    //   1101: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1104: invokestatic 530	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1107: pop
    //   1108: aload 12
    //   1110: ifnonnull +11 -> 1121
    //   1113: aload_1
    //   1114: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   1117: iconst_3
    //   1118: invokestatic 533	android/os/Process:sendSignal	(II)V
    //   1121: aload_0
    //   1122: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1125: ldc_w 364
    //   1128: aload_1
    //   1129: aload_1
    //   1130: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1133: aload_2
    //   1134: aload_3
    //   1135: aload 5
    //   1137: aload 16
    //   1139: aload 12
    //   1141: aconst_null
    //   1142: invokevirtual 537	com/android/server/am/ActivityManagerService:addErrorToDropBox	(Ljava/lang/String;Lcom/android/server/am/ProcessRecord;Ljava/lang/String;Lcom/android/server/am/ActivityRecord;Lcom/android/server/am/ActivityRecord;Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Landroid/app/ApplicationErrorReport$CrashInfo;)V
    //   1145: aload_1
    //   1146: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   1149: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   1152: if_icmpne +59 -> 1211
    //   1155: aload 5
    //   1157: ifnull +54 -> 1211
    //   1160: aload 5
    //   1162: ldc_w 539
    //   1165: invokevirtual 543	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1168: ifeq +43 -> 1211
    //   1171: getstatic 30	com/android/server/am/AppErrors:TAG	Ljava/lang/String;
    //   1174: ldc_w 545
    //   1177: invokestatic 530	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1180: pop
    //   1181: return
    //   1182: aload_0
    //   1183: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1186: astore 17
    //   1188: iconst_1
    //   1189: aload 12
    //   1191: aload 14
    //   1193: aload 15
    //   1195: aload 16
    //   1197: invokestatic 520	com/android/server/am/ActivityManagerService:dumpStackTraces	(ZLjava/util/ArrayList;Lcom/android/internal/os/ProcessCpuTracker;Landroid/util/SparseArray;[Ljava/lang/String;)Ljava/io/File;
    //   1200: astore 12
    //   1202: goto -174 -> 1028
    //   1205: astore_1
    //   1206: aload 15
    //   1208: monitorexit
    //   1209: aload_1
    //   1210: athrow
    //   1211: aload_0
    //   1212: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1215: getfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   1218: ifnull +102 -> 1320
    //   1221: aload_0
    //   1222: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1225: getfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   1228: aload_1
    //   1229: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1232: aload_1
    //   1233: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   1236: aload 13
    //   1238: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1241: invokeinterface 547 4 0
    //   1246: istore 7
    //   1248: iload 7
    //   1250: ifeq +70 -> 1320
    //   1253: iload 7
    //   1255: ifge +22 -> 1277
    //   1258: aload_1
    //   1259: getfield 183	com/android/server/am/ProcessRecord:pid	I
    //   1262: getstatic 362	com/android/server/am/ActivityManagerService:MY_PID	I
    //   1265: if_icmpeq +12 -> 1277
    //   1268: aload_1
    //   1269: ldc_w 364
    //   1272: iconst_1
    //   1273: invokevirtual 278	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   1276: return
    //   1277: aload_0
    //   1278: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1281: astore_3
    //   1282: aload_3
    //   1283: monitorenter
    //   1284: invokestatic 390	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   1287: aload_0
    //   1288: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1291: getfield 551	com/android/server/am/ActivityManagerService:mServices	Lcom/android/server/am/ActiveServices;
    //   1294: aload_1
    //   1295: invokevirtual 556	com/android/server/am/ActiveServices:scheduleServiceTimeoutLocked	(Lcom/android/server/am/ProcessRecord;)V
    //   1298: aload_3
    //   1299: monitorexit
    //   1300: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1303: return
    //   1304: astore_3
    //   1305: aload_0
    //   1306: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1309: aconst_null
    //   1310: putfield 201	com/android/server/am/ActivityManagerService:mController	Landroid/app/IActivityController;
    //   1313: invokestatic 284	com/android/server/Watchdog:getInstance	()Lcom/android/server/Watchdog;
    //   1316: aconst_null
    //   1317: invokevirtual 288	com/android/server/Watchdog:setActivityController	(Landroid/app/IActivityController;)V
    //   1320: aload_0
    //   1321: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1324: astore 12
    //   1326: aload 12
    //   1328: monitorenter
    //   1329: invokestatic 390	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   1332: aload_0
    //   1333: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1336: getfield 560	com/android/server/am/ActivityManagerService:mBatteryStatsService	Lcom/android/server/am/BatteryStatsService;
    //   1339: aload_1
    //   1340: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1343: aload_1
    //   1344: getfield 561	com/android/server/am/ProcessRecord:uid	I
    //   1347: invokevirtual 567	com/android/server/am/BatteryStatsService:noteProcessAnr	(Ljava/lang/String;I)V
    //   1350: ldc_w 569
    //   1353: iconst_0
    //   1354: invokestatic 447	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   1357: ifeq +133 -> 1490
    //   1360: ldc_w 571
    //   1363: aconst_null
    //   1364: invokestatic 222	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   1367: astore_3
    //   1368: aload_3
    //   1369: ifnull +121 -> 1490
    //   1372: aload_3
    //   1373: invokevirtual 574	java/lang/String:length	()I
    //   1376: ifeq +114 -> 1490
    //   1379: new 576	java/io/File
    //   1382: dup
    //   1383: aload_3
    //   1384: invokespecial 577	java/io/File:<init>	(Ljava/lang/String;)V
    //   1387: astore 14
    //   1389: aload_3
    //   1390: ldc_w 579
    //   1393: invokevirtual 583	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   1396: istore 7
    //   1398: iconst_m1
    //   1399: iload 7
    //   1401: if_icmpeq +119 -> 1520
    //   1404: new 235	java/lang/StringBuilder
    //   1407: dup
    //   1408: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   1411: aload_3
    //   1412: iconst_0
    //   1413: iload 7
    //   1415: invokevirtual 587	java/lang/String:substring	(II)Ljava/lang/String;
    //   1418: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1421: ldc_w 589
    //   1424: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1427: aload_1
    //   1428: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1431: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1434: ldc_w 589
    //   1437: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1440: aload_0
    //   1441: getfield 44	com/android/server/am/AppErrors:mTraceDateFormat	Ljava/text/SimpleDateFormat;
    //   1444: new 591	java/util/Date
    //   1447: dup
    //   1448: invokespecial 592	java/util/Date:<init>	()V
    //   1451: invokevirtual 596	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   1454: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1457: aload_3
    //   1458: iload 7
    //   1460: invokevirtual 599	java/lang/String:substring	(I)Ljava/lang/String;
    //   1463: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1466: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1469: astore_3
    //   1470: aload 14
    //   1472: new 576	java/io/File
    //   1475: dup
    //   1476: aload_3
    //   1477: invokespecial 577	java/io/File:<init>	(Ljava/lang/String;)V
    //   1480: invokevirtual 603	java/io/File:renameTo	(Ljava/io/File;)Z
    //   1483: pop
    //   1484: ldc2_w 604
    //   1487: invokestatic 609	android/os/SystemClock:sleep	(J)V
    //   1490: iload 6
    //   1492: ifeq +59 -> 1551
    //   1495: aload_1
    //   1496: ldc_w 611
    //   1499: iconst_1
    //   1500: invokevirtual 278	com/android/server/am/ProcessRecord:kill	(Ljava/lang/String;Z)V
    //   1503: aload 12
    //   1505: monitorexit
    //   1506: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1509: return
    //   1510: astore 12
    //   1512: aload_3
    //   1513: monitorexit
    //   1514: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1517: aload 12
    //   1519: athrow
    //   1520: new 235	java/lang/StringBuilder
    //   1523: dup
    //   1524: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   1527: aload_3
    //   1528: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1531: ldc_w 589
    //   1534: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1537: aload_1
    //   1538: getfield 97	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   1541: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1544: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1547: astore_3
    //   1548: goto -78 -> 1470
    //   1551: aload_2
    //   1552: ifnull +227 -> 1779
    //   1555: aload_2
    //   1556: getfield 497	com/android/server/am/ActivityRecord:shortComponentName	Ljava/lang/String;
    //   1559: astore_3
    //   1560: aload 5
    //   1562: ifnull +222 -> 1784
    //   1565: new 235	java/lang/StringBuilder
    //   1568: dup
    //   1569: invokespecial 236	java/lang/StringBuilder:<init>	()V
    //   1572: ldc_w 613
    //   1575: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1578: aload 5
    //   1580: invokevirtual 242	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1583: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1586: astore 5
    //   1588: aload_0
    //   1589: aload_1
    //   1590: aload_3
    //   1591: aload 5
    //   1593: aload 13
    //   1595: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1598: invokespecial 615	com/android/server/am/AppErrors:makeAppNotRespondingLocked	(Lcom/android/server/am/ProcessRecord;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   1601: invokestatic 621	android/os/Message:obtain	()Landroid/os/Message;
    //   1604: astore_3
    //   1605: new 623	java/util/HashMap
    //   1608: dup
    //   1609: invokespecial 624	java/util/HashMap:<init>	()V
    //   1612: astore 5
    //   1614: aload_3
    //   1615: iconst_2
    //   1616: putfield 627	android/os/Message:what	I
    //   1619: aload_3
    //   1620: aload 5
    //   1622: putfield 631	android/os/Message:obj	Ljava/lang/Object;
    //   1625: iload 4
    //   1627: ifeq +165 -> 1792
    //   1630: iconst_1
    //   1631: istore 6
    //   1633: aload_3
    //   1634: iload 6
    //   1636: putfield 634	android/os/Message:arg1	I
    //   1639: aload 5
    //   1641: ldc_w 635
    //   1644: aload_1
    //   1645: invokevirtual 638	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1648: pop
    //   1649: aload_2
    //   1650: ifnull +13 -> 1663
    //   1653: aload 5
    //   1655: ldc_w 639
    //   1658: aload_2
    //   1659: invokevirtual 638	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   1662: pop
    //   1663: aload_0
    //   1664: getfield 55	com/android/server/am/AppErrors:mService	Lcom/android/server/am/ActivityManagerService;
    //   1667: getfield 643	com/android/server/am/ActivityManagerService:mUiHandler	Lcom/android/server/am/ActivityManagerService$UiHandler;
    //   1670: aload_3
    //   1671: invokevirtual 649	com/android/server/am/ActivityManagerService$UiHandler:sendMessage	(Landroid/os/Message;)Z
    //   1674: pop
    //   1675: aload 12
    //   1677: monitorexit
    //   1678: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1681: iconst_1
    //   1682: newarray <illegal type>
    //   1684: dup
    //   1685: iconst_0
    //   1686: iconst_2
    //   1687: iastore
    //   1688: invokestatic 655	android/util/OpFeatures:isSupport	([I)Z
    //   1691: ifeq +46 -> 1737
    //   1694: new 657	net/oneplus/odm/insight/tracker/AppTracker
    //   1697: dup
    //   1698: aload_0
    //   1699: getfield 57	com/android/server/am/AppErrors:mContext	Landroid/content/Context;
    //   1702: invokespecial 660	net/oneplus/odm/insight/tracker/AppTracker:<init>	(Landroid/content/Context;)V
    //   1705: astore_1
    //   1706: new 623	java/util/HashMap
    //   1709: dup
    //   1710: invokespecial 624	java/util/HashMap:<init>	()V
    //   1713: astore_2
    //   1714: aload_2
    //   1715: ldc_w 662
    //   1718: aload 13
    //   1720: invokevirtual 252	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1723: invokeinterface 665 3 0
    //   1728: pop
    //   1729: aload_1
    //   1730: ldc_w 364
    //   1733: aload_2
    //   1734: invokevirtual 669	net/oneplus/odm/insight/tracker/AppTracker:onEvent	(Ljava/lang/String;Ljava/util/Map;)V
    //   1737: ldc_w 671
    //   1740: iconst_0
    //   1741: invokestatic 447	android/os/SystemProperties:getBoolean	(Ljava/lang/String;Z)Z
    //   1744: ifeq +34 -> 1778
    //   1747: aload_0
    //   1748: invokespecial 673	com/android/server/am/AppErrors:isKernelLoadingHigh	()Z
    //   1751: ifeq +27 -> 1778
    //   1754: aload_0
    //   1755: bipush 119
    //   1757: invokespecial 675	com/android/server/am/AppErrors:doSysRq	(C)V
    //   1760: aload_0
    //   1761: bipush 108
    //   1763: invokespecial 675	com/android/server/am/AppErrors:doSysRq	(C)V
    //   1766: ldc2_w 676
    //   1769: invokestatic 609	android/os/SystemClock:sleep	(J)V
    //   1772: aload_0
    //   1773: bipush 99
    //   1775: invokespecial 675	com/android/server/am/AppErrors:doSysRq	(C)V
    //   1778: return
    //   1779: aconst_null
    //   1780: astore_3
    //   1781: goto -221 -> 1560
    //   1784: ldc_w 679
    //   1787: astore 5
    //   1789: goto -201 -> 1588
    //   1792: iconst_0
    //   1793: istore 6
    //   1795: goto -162 -> 1633
    //   1798: astore_1
    //   1799: aload 12
    //   1801: monitorexit
    //   1802: invokestatic 406	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1805: aload_1
    //   1806: athrow
    //   1807: iconst_0
    //   1808: istore 6
    //   1810: goto -1306 -> 504
    //   1813: iload 8
    //   1815: iconst_1
    //   1816: isub
    //   1817: istore 8
    //   1819: goto -1185 -> 634
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1822	0	this	AppErrors
    //   0	1822	1	paramProcessRecord	ProcessRecord
    //   0	1822	2	paramActivityRecord1	ActivityRecord
    //   0	1822	4	paramBoolean	boolean
    //   0	1822	5	paramString	String
    //   112	1697	6	i	int
    //   530	929	7	j	int
    //   526	1292	8	k	int
    //   674	106	9	m	int
    //   85	1003	10	l	long
    //   1510	290	12	localObject2	Object
    //   181	653	13	localRemoteException	RemoteException
    //   846	873	13	localStringBuilder	StringBuilder
    //   654	817	14	localObject3	Object
    //   1003	193	16	localObject5	Object
    //   1186	1	17	localActivityManagerService2	ActivityManagerService
    // Exception table:
    //   from	to	target	type
    //   39	82	181	android/os/RemoteException
    //   123	174	824	finally
    //   207	252	824	finally
    //   259	304	824	finally
    //   311	356	824	finally
    //   363	408	824	finally
    //   415	489	824	finally
    //   494	501	824	finally
    //   504	514	824	finally
    //   522	528	824	finally
    //   540	547	824	finally
    //   551	570	824	finally
    //   570	590	824	finally
    //   590	620	824	finally
    //   620	634	824	finally
    //   639	656	824	finally
    //   661	676	824	finally
    //   681	690	824	finally
    //   697	758	824	finally
    //   761	771	824	finally
    //   777	821	824	finally
    //   1047	1061	1205	finally
    //   1221	1248	1304	android/os/RemoteException
    //   1258	1276	1304	android/os/RemoteException
    //   1277	1284	1304	android/os/RemoteException
    //   1298	1303	1304	android/os/RemoteException
    //   1512	1520	1304	android/os/RemoteException
    //   1284	1298	1510	finally
    //   1329	1368	1798	finally
    //   1372	1398	1798	finally
    //   1404	1470	1798	finally
    //   1470	1490	1798	finally
    //   1495	1503	1798	finally
    //   1520	1548	1798	finally
    //   1555	1560	1798	finally
    //   1565	1588	1798	finally
    //   1588	1625	1798	finally
    //   1633	1649	1798	finally
    //   1653	1663	1798	finally
    //   1663	1675	1798	finally
  }
  
  void clearBadProcessLocked(ApplicationInfo paramApplicationInfo)
  {
    this.mBadProcesses.remove(paramApplicationInfo.processName, paramApplicationInfo.uid);
  }
  
  void crashApplication(ProcessRecord paramProcessRecord, ApplicationErrorReport.CrashInfo paramCrashInfo)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      crashApplicationInner(paramProcessRecord, paramCrashInfo);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  void crashApplicationInner(ProcessRecord paramProcessRecord, ApplicationErrorReport.CrashInfo paramCrashInfo)
  {
    long l1 = System.currentTimeMillis();
    Object localObject2 = paramCrashInfo.exceptionClassName;
    Object localObject1 = paramCrashInfo.exceptionMessage;
    ??? = paramCrashInfo.stackTrace;
    if ((localObject2 != null) && (localObject1 != null)) {
      localObject1 = (String)localObject2 + ": " + (String)localObject1;
    }
    boolean bool;
    int i;
    for (;;)
    {
      AppErrorResult localAppErrorResult = new AppErrorResult();
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        bool = handleAppCrashInActivityController(paramProcessRecord, paramCrashInfo, (String)localObject2, (String)localObject1, (String)???, l1);
        if (bool)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
          if (localObject2 != null) {
            localObject1 = localObject2;
          }
        }
        else
        {
          if (paramProcessRecord != null)
          {
            localObject4 = paramProcessRecord.instrumentationClass;
            if (localObject4 != null)
            {
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
          }
          if (paramProcessRecord != null) {
            this.mService.mBatteryStatsService.noteProcessCrash(paramProcessRecord.processName, paramProcessRecord.uid);
          }
          Object localObject4 = new AppErrorDialog.Data();
          ((AppErrorDialog.Data)localObject4).result = localAppErrorResult;
          ((AppErrorDialog.Data)localObject4).proc = paramProcessRecord;
          if ((paramProcessRecord != null) && (makeAppCrashingLocked(paramProcessRecord, (String)localObject2, (String)localObject1, (String)???, (AppErrorDialog.Data)localObject4)))
          {
            localObject1 = Message.obtain();
            ((Message)localObject1).what = 1;
            localObject2 = ((AppErrorDialog.Data)localObject4).task;
            ((Message)localObject1).obj = localObject4;
            this.mService.mUiHandler.sendMessage((Message)localObject1);
            ActivityManagerService.resetPriorityAfterLockedSection();
            int j = localAppErrorResult.get();
            localObject1 = null;
            MetricsLogger.action(this.mContext, 316, j);
            if (j != 6)
            {
              i = j;
              if (j != 7) {
                break;
              }
            }
            else
            {
              i = 1;
            }
          }
        }
      }
    }
    for (;;)
    {
      long l2;
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if (i == 5) {
          stopReportingCrashesLocked(paramProcessRecord);
        }
        if (i == 3)
        {
          this.mService.removeProcessLocked(paramProcessRecord, false, true, "crash");
          if (localObject2 == null) {}
        }
        try
        {
          this.mService.startActivityFromRecents(((TaskRecord)localObject2).taskId, ActivityOptions.makeBasic().toBundle());
          if (i == 1) {
            l2 = Binder.clearCallingIdentity();
          }
          try
          {
            this.mService.mStackSupervisor.handleAppCrashLocked(paramProcessRecord);
            if (!paramProcessRecord.persistent)
            {
              this.mService.removeProcessLocked(paramProcessRecord, false, false, "crash");
              this.mService.mStackSupervisor.resumeFocusedStackTopActivityLocked();
            }
            Binder.restoreCallingIdentity(l2);
            if (i == 2) {
              localObject1 = createAppErrorIntentLocked(paramProcessRecord, l1, paramCrashInfo);
            }
            if (paramProcessRecord != null)
            {
              bool = paramProcessRecord.isolated;
              if (!bool) {}
            }
            else
            {
              ActivityManagerService.resetPriorityAfterLockedSection();
              if (localObject1 == null) {}
            }
          }
          finally
          {
            Set localSet;
            Binder.restoreCallingIdentity(l2);
          }
          try
          {
            this.mContext.startActivityAsUser((Intent)localObject1, new UserHandle(paramProcessRecord.userId));
            return;
          }
          catch (ActivityNotFoundException paramProcessRecord)
          {
            Slog.w(TAG, "bug report receiver dissappeared", paramProcessRecord);
          }
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
          paramProcessRecord = finally;
          ActivityManagerService.resetPriorityAfterLockedSection();
          throw paramProcessRecord;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          localSet = ((TaskRecord)localObject2).intent.getCategories();
          if ((localSet == null) || (!localSet.contains("android.intent.category.LAUNCHER"))) {
            continue;
          }
          this.mService.startActivityInPackage(((TaskRecord)localObject2).mCallingUid, ((TaskRecord)localObject2).mCallingPackage, ((TaskRecord)localObject2).intent, null, null, null, 0, 0, ActivityOptions.makeBasic().toBundle(), ((TaskRecord)localObject2).userId, null, null);
          continue;
        }
      }
      if (i != 3) {
        this.mProcessCrashTimes.put(paramProcessRecord.info.processName, paramProcessRecord.uid, Long.valueOf(SystemClock.uptimeMillis()));
      }
    }
  }
  
  Intent createAppErrorIntentLocked(ProcessRecord paramProcessRecord, long paramLong, ApplicationErrorReport.CrashInfo paramCrashInfo)
  {
    paramCrashInfo = createAppErrorReportLocked(paramProcessRecord, paramLong, paramCrashInfo);
    if (paramCrashInfo == null) {
      return null;
    }
    Intent localIntent = new Intent("android.intent.action.APP_ERROR");
    localIntent.setComponent(paramProcessRecord.errorReportReceiver);
    localIntent.putExtra("android.intent.extra.BUG_REPORT", paramCrashInfo);
    localIntent.addFlags(268435456);
    return localIntent;
  }
  
  boolean dumpLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, boolean paramBoolean, String paramString)
  {
    boolean bool1 = paramBoolean;
    int i;
    int n;
    int k;
    String str;
    SparseArray localSparseArray;
    int i1;
    int m;
    int i2;
    Object localObject;
    int j;
    if (!this.mProcessCrashTimes.getMap().isEmpty())
    {
      i = 0;
      long l = SystemClock.uptimeMillis();
      paramFileDescriptor = this.mProcessCrashTimes.getMap();
      n = paramFileDescriptor.size();
      k = 0;
      for (;;)
      {
        bool1 = paramBoolean;
        if (k >= n) {
          break;
        }
        str = (String)paramFileDescriptor.keyAt(k);
        localSparseArray = (SparseArray)paramFileDescriptor.valueAt(k);
        i1 = localSparseArray.size();
        m = 0;
        while (m < i1)
        {
          i2 = localSparseArray.keyAt(m);
          localObject = (ProcessRecord)this.mService.mProcessNames.get(str, i2);
          if (paramString != null)
          {
            j = i;
            bool1 = paramBoolean;
            if (localObject != null)
            {
              j = i;
              bool1 = paramBoolean;
              if (!((ProcessRecord)localObject).pkgList.containsKey(paramString)) {}
            }
          }
          else
          {
            j = i;
            bool1 = paramBoolean;
            if (i == 0)
            {
              if (paramBoolean) {
                paramPrintWriter.println();
              }
              bool1 = true;
              paramPrintWriter.println("  Time since processes crashed:");
              j = 1;
            }
            paramPrintWriter.print("    Process ");
            paramPrintWriter.print(str);
            paramPrintWriter.print(" uid ");
            paramPrintWriter.print(i2);
            paramPrintWriter.print(": last crashed ");
            TimeUtils.formatDuration(l - ((Long)localSparseArray.valueAt(m)).longValue(), paramPrintWriter);
            paramPrintWriter.println(" ago");
          }
          m += 1;
          i = j;
          paramBoolean = bool1;
        }
        k += 1;
      }
    }
    boolean bool2 = bool1;
    if (!this.mBadProcesses.getMap().isEmpty())
    {
      j = 0;
      paramFileDescriptor = this.mBadProcesses.getMap();
      i2 = paramFileDescriptor.size();
      k = 0;
      paramBoolean = bool1;
      for (;;)
      {
        bool2 = paramBoolean;
        if (k >= i2) {
          break;
        }
        str = (String)paramFileDescriptor.keyAt(k);
        localSparseArray = (SparseArray)paramFileDescriptor.valueAt(k);
        int i3 = localSparseArray.size();
        m = 0;
        while (m < i3)
        {
          i1 = localSparseArray.keyAt(m);
          localObject = (ProcessRecord)this.mService.mProcessNames.get(str, i1);
          if (paramString != null)
          {
            n = j;
            bool2 = paramBoolean;
            if (localObject != null)
            {
              n = j;
              bool2 = paramBoolean;
              if (!((ProcessRecord)localObject).pkgList.containsKey(paramString)) {}
            }
          }
          else
          {
            i = j;
            bool1 = paramBoolean;
            if (j == 0)
            {
              if (paramBoolean) {
                paramPrintWriter.println();
              }
              bool1 = true;
              paramPrintWriter.println("  Bad processes:");
              i = 1;
            }
            localObject = (BadProcessInfo)localSparseArray.valueAt(m);
            paramPrintWriter.print("    Bad process ");
            paramPrintWriter.print(str);
            paramPrintWriter.print(" uid ");
            paramPrintWriter.print(i1);
            paramPrintWriter.print(": crashed at time ");
            paramPrintWriter.println(((BadProcessInfo)localObject).time);
            if (((BadProcessInfo)localObject).shortMsg != null)
            {
              paramPrintWriter.print("      Short msg: ");
              paramPrintWriter.println(((BadProcessInfo)localObject).shortMsg);
            }
            if (((BadProcessInfo)localObject).longMsg != null)
            {
              paramPrintWriter.print("      Long msg: ");
              paramPrintWriter.println(((BadProcessInfo)localObject).longMsg);
            }
            n = i;
            bool2 = bool1;
            if (((BadProcessInfo)localObject).stack != null)
            {
              paramPrintWriter.println("      Stack:");
              j = 0;
              n = 0;
              while (n < ((BadProcessInfo)localObject).stack.length())
              {
                i1 = j;
                if (((BadProcessInfo)localObject).stack.charAt(n) == '\n')
                {
                  paramPrintWriter.print("        ");
                  paramPrintWriter.write(((BadProcessInfo)localObject).stack, j, n - j);
                  paramPrintWriter.println();
                  i1 = n + 1;
                }
                n += 1;
                j = i1;
              }
              n = i;
              bool2 = bool1;
              if (j < ((BadProcessInfo)localObject).stack.length())
              {
                paramPrintWriter.print("        ");
                paramPrintWriter.write(((BadProcessInfo)localObject).stack, j, ((BadProcessInfo)localObject).stack.length() - j);
                paramPrintWriter.println();
                bool2 = bool1;
                n = i;
              }
            }
          }
          m += 1;
          j = n;
          paramBoolean = bool2;
        }
        k += 1;
      }
    }
    return bool2;
  }
  
  boolean handleAppCrashLocked(ProcessRecord paramProcessRecord, String paramString1, String paramString2, String paramString3, String paramString4, AppErrorDialog.Data paramData)
  {
    long l = SystemClock.uptimeMillis();
    Long localLong2;
    Long localLong1;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "anr_show_background", 0) != 0)
    {
      i = 1;
      if (paramProcessRecord.isolated) {
        break label344;
      }
      localLong2 = (Long)this.mProcessCrashTimes.get(paramProcessRecord.info.processName, paramProcessRecord.uid);
      localLong1 = (Long)this.mProcessCrashTimesPersistent.get(paramProcessRecord.info.processName, paramProcessRecord.uid);
    }
    for (;;)
    {
      if ((localLong2 != null) && (l < localLong2.longValue() + 60000L))
      {
        Slog.w(TAG, "Process " + paramProcessRecord.info.processName + " has crashed too many times: killing!");
        EventLog.writeEvent(30032, new Object[] { Integer.valueOf(paramProcessRecord.userId), paramProcessRecord.info.processName, Integer.valueOf(paramProcessRecord.uid) });
        this.mService.mStackSupervisor.handleAppCrashLocked(paramProcessRecord);
        if (!paramProcessRecord.persistent)
        {
          EventLog.writeEvent(30015, new Object[] { Integer.valueOf(paramProcessRecord.userId), Integer.valueOf(paramProcessRecord.uid), paramProcessRecord.info.processName });
          if (!paramProcessRecord.isolated)
          {
            this.mBadProcesses.put(paramProcessRecord.info.processName, paramProcessRecord.uid, new BadProcessInfo(l, paramString2, paramString3, paramString4));
            this.mProcessCrashTimes.remove(paramProcessRecord.info.processName, paramProcessRecord.uid);
          }
          paramProcessRecord.bad = true;
          paramProcessRecord.removed = true;
          this.mService.removeProcessLocked(paramProcessRecord, false, false, "crash");
          this.mService.mStackSupervisor.resumeFocusedStackTopActivityLocked();
          if (i == 0)
          {
            return false;
            i = 0;
            break;
            label344:
            localLong1 = null;
            localLong2 = null;
            continue;
          }
        }
        this.mService.mStackSupervisor.resumeFocusedStackTopActivityLocked();
        if (paramProcessRecord.curProcState != 3) {
          break label512;
        }
      }
    }
    label512:
    for (int i = 1;; i = 0)
    {
      int j = paramProcessRecord.services.size() - 1;
      while (j >= 0)
      {
        paramString1 = (ServiceRecord)paramProcessRecord.services.valueAt(j);
        paramString1.crashCount += 1;
        if ((paramData != null) && (paramString1.crashCount <= 1) && ((paramString1.isForeground) || (i != 0))) {
          paramData.isRestartableForService = true;
        }
        j -= 1;
      }
      paramString1 = this.mService.mStackSupervisor.finishTopRunningActivityLocked(paramProcessRecord, paramString1);
      if (paramData != null) {
        paramData.task = paramString1;
      }
      if ((paramData == null) || (localLong1 == null) || (l >= localLong1.longValue() + 60000L)) {
        break;
      }
      paramData.repeating = true;
      break;
    }
    paramString1 = paramProcessRecord.activities;
    if ((paramProcessRecord == this.mService.mHomeProcess) && (paramString1.size() > 0) && ((this.mService.mHomeProcess.info.flags & 0x1) == 0)) {
      i = paramString1.size() - 1;
    }
    for (;;)
    {
      if (i >= 0)
      {
        paramString2 = (ActivityRecord)paramString1.get(i);
        if (paramString2.isHomeActivity()) {
          Log.i(TAG, "Clearing package preferred activities from " + paramString2.packageName);
        }
      }
      try
      {
        ActivityThread.getPackageManager().clearPackagePreferredActivities(paramString2.packageName);
        i -= 1;
        continue;
        if (!paramProcessRecord.isolated)
        {
          this.mProcessCrashTimes.put(paramProcessRecord.info.processName, paramProcessRecord.uid, Long.valueOf(l));
          this.mProcessCrashTimesPersistent.put(paramProcessRecord.info.processName, paramProcessRecord.uid, Long.valueOf(l));
        }
        if (paramProcessRecord.crashHandler != null) {
          this.mService.mHandler.post(paramProcessRecord.crashHandler);
        }
        return true;
      }
      catch (RemoteException paramString2)
      {
        for (;;) {}
      }
    }
  }
  
  void handleShowAnrUi(Message paramMessage)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject1 = (HashMap)paramMessage.obj;
      ProcessRecord localProcessRecord = (ProcessRecord)((HashMap)localObject1).get("app");
      if ((localProcessRecord != null) && (localProcessRecord.anrDialog != null))
      {
        Slog.e(TAG, "App already has anr dialog: " + localProcessRecord);
        MetricsLogger.action(this.mContext, 317, -2);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      Object localObject2 = new Intent("android.intent.action.ANR");
      if (!this.mService.mProcessesReady) {
        ((Intent)localObject2).addFlags(1342177280);
      }
      this.mService.broadcastIntentLocked(null, null, (Intent)localObject2, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, 0);
      int i;
      Context localContext;
      if (Settings.Secure.getInt(this.mContext.getContentResolver(), "anr_show_background", 0) != 0)
      {
        i = 1;
        if ((!this.mService.canShowErrorDialogs()) && (i == 0)) {
          break label269;
        }
        localObject2 = this.mService;
        localContext = this.mContext;
        localObject1 = (ActivityRecord)((HashMap)localObject1).get("activity");
        if (paramMessage.arg1 == 0) {
          break label264;
        }
      }
      label264:
      for (boolean bool = true;; bool = false)
      {
        paramMessage = new AppNotRespondingDialog((ActivityManagerService)localObject2, localContext, localProcessRecord, (ActivityRecord)localObject1, bool);
        try
        {
          localProcessRecord.anrDialog = paramMessage;
          ActivityManagerService.resetPriorityAfterLockedSection();
          if (paramMessage != null) {
            paramMessage.show();
          }
          return;
        }
        finally {}
        i = 0;
        break;
      }
      label269:
      MetricsLogger.action(this.mContext, 317, -1);
      this.mService.killAppAtUsersRequest(localProcessRecord, null);
      paramMessage = null;
    }
    ActivityManagerService.resetPriorityAfterLockedSection();
  }
  
  void handleShowAppErrorUi(Message arg1)
  {
    AppErrorDialog.Data localData = (AppErrorDialog.Data)???.obj;
    int j;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "anr_show_background", 0) != 0) {
      j = 1;
    }
    for (;;)
    {
      int i;
      int k;
      label164:
      int m;
      boolean bool1;
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ProcessRecord localProcessRecord = localData.proc;
        AppErrorResult localAppErrorResult = localData.result;
        if ((localProcessRecord != null) && (localProcessRecord.crashDialog != null))
        {
          Slog.e(TAG, "App already has crash dialog: " + localProcessRecord);
          if (localAppErrorResult != null) {
            localAppErrorResult.set(AppErrorDialog.ALREADY_SHOWING);
          }
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
          j = 0;
          continue;
        }
        if (UserHandle.getAppId(localProcessRecord.uid) >= 10000) {
          if (localProcessRecord.pid != ActivityManagerService.MY_PID)
          {
            i = 1;
            int[] arrayOfInt = this.mService.mUserController.getCurrentProfileIdsLocked();
            int n = arrayOfInt.length;
            k = 0;
            if (k >= n) {
              break label414;
            }
            m = arrayOfInt[k];
            if (localProcessRecord.userId == m) {
              break label408;
            }
            m = 1;
            break label384;
            boolean bool2;
            if (this.mAppsNotReportingCrashes != null)
            {
              bool1 = this.mAppsNotReportingCrashes.contains(localProcessRecord.info.packageName);
              bool2 = SystemProperties.getBoolean("persist.sys.assert.panic", false);
              if ((this.mService.canShowErrorDialogs()) || (j != 0)) {
                break label425;
              }
              if (localAppErrorResult != null) {
                localAppErrorResult.set(AppErrorDialog.CANT_SHOW);
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              if (localData.proc.crashDialog != null) {
                localData.proc.crashDialog.show();
              }
              return;
              Slog.w(TAG, "Skipping crash dialog of " + localProcessRecord + ": background");
              if (localAppErrorResult != null) {
                localAppErrorResult.set(AppErrorDialog.BACKGROUND_USER);
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            bool1 = false;
            continue;
            if (!bool2) {
              continue;
            }
            localProcessRecord.crashDialog = new AppErrorDialog(this.mContext, this.mService, localData);
          }
        }
      }
      for (;;)
      {
        label384:
        i &= m;
        k += 1;
        break label164;
        i = 0;
        break;
        i = 0;
        break;
        label408:
        m = 0;
      }
      label414:
      if (i != 0) {
        if (j != 0)
        {
          continue;
          label425:
          if (!bool1) {}
        }
      }
    }
  }
  
  boolean isBadProcessLocked(ApplicationInfo paramApplicationInfo)
  {
    return this.mBadProcesses.get(paramApplicationInfo.processName, paramApplicationInfo.uid) != null;
  }
  
  void killAppAtUserRequestLocked(ProcessRecord paramProcessRecord, Dialog paramDialog)
  {
    paramProcessRecord.crashing = false;
    paramProcessRecord.crashingReport = null;
    paramProcessRecord.notResponding = false;
    paramProcessRecord.notRespondingReport = null;
    if (paramProcessRecord.anrDialog == paramDialog) {
      paramProcessRecord.anrDialog = null;
    }
    if (paramProcessRecord.waitDialog == paramDialog) {
      paramProcessRecord.waitDialog = null;
    }
    if ((paramProcessRecord.pid > 0) && (paramProcessRecord.pid != ActivityManagerService.MY_PID))
    {
      handleAppCrashLocked(paramProcessRecord, "user-terminated", null, null, null, null);
      paramProcessRecord.kill("user request after error", true);
    }
  }
  
  void loadAppsNotReportingCrashesFromConfigLocked(String paramString)
  {
    if (paramString != null)
    {
      paramString = paramString.split(",");
      if (paramString.length > 0)
      {
        this.mAppsNotReportingCrashes = new ArraySet();
        Collections.addAll(this.mAppsNotReportingCrashes, paramString);
      }
    }
  }
  
  void resetProcessCrashTimeLocked(ApplicationInfo paramApplicationInfo)
  {
    this.mProcessCrashTimes.remove(paramApplicationInfo.processName, paramApplicationInfo.uid);
  }
  
  void resetProcessCrashTimeLocked(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    ArrayMap localArrayMap = this.mProcessCrashTimes.getMap();
    int j = localArrayMap.size() - 1;
    while (j >= 0)
    {
      SparseArray localSparseArray = (SparseArray)localArrayMap.valueAt(j);
      int k = localSparseArray.size() - 1;
      if (k >= 0)
      {
        int i = 0;
        int m = localSparseArray.keyAt(k);
        if (!paramBoolean) {
          if (paramInt2 == -1) {
            if (UserHandle.getAppId(m) == paramInt1) {
              i = 1;
            }
          }
        }
        for (;;)
        {
          if (i != 0) {
            localSparseArray.removeAt(k);
          }
          k -= 1;
          break;
          if (m == UserHandle.getUid(paramInt2, paramInt1))
          {
            i = 1;
            continue;
            if (UserHandle.getUserId(m) == paramInt2) {
              i = 1;
            }
          }
        }
      }
      if (localSparseArray.size() == 0) {
        localArrayMap.removeAt(j);
      }
      j -= 1;
    }
  }
  
  void scheduleAppCrashLocked(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    Object localObject1 = null;
    SparseArray localSparseArray = this.mService.mPidsSelfLocked;
    int i = 0;
    for (;;)
    {
      Object localObject2 = localObject1;
      try
      {
        if (i < this.mService.mPidsSelfLocked.size())
        {
          localObject2 = (ProcessRecord)this.mService.mPidsSelfLocked.valueAt(i);
          if (((ProcessRecord)localObject2).uid != paramInt1) {
            break label172;
          }
          int j = ((ProcessRecord)localObject2).pid;
          if (j != paramInt2) {}
        }
        else
        {
          if (localObject2 != null) {
            break label164;
          }
          Slog.w(TAG, "crashApplication: nothing for uid=" + paramInt1 + " initialPid=" + paramInt2 + " packageName=" + paramString1);
          return;
        }
        boolean bool = ((ProcessRecord)localObject2).pkgList.containsKey(paramString1);
        if (!bool) {
          break label172;
        }
        localObject1 = localObject2;
      }
      finally {}
      label164:
      ((ProcessRecord)localObject2).scheduleCrash(paramString2);
      return;
      label172:
      i += 1;
    }
  }
  
  void startAppProblemLocked(ProcessRecord paramProcessRecord)
  {
    paramProcessRecord.errorReportReceiver = null;
    int[] arrayOfInt = this.mService.mUserController.getCurrentProfileIdsLocked();
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      if (paramProcessRecord.userId == k) {
        paramProcessRecord.errorReportReceiver = ApplicationErrorReport.getErrorReportReceiver(this.mContext, paramProcessRecord.info.packageName, paramProcessRecord.info.flags);
      }
      i += 1;
    }
    this.mService.skipCurrentReceiverLocked(paramProcessRecord);
  }
  
  void stopReportingCrashesLocked(ProcessRecord paramProcessRecord)
  {
    if (this.mAppsNotReportingCrashes == null) {
      this.mAppsNotReportingCrashes = new ArraySet();
    }
    this.mAppsNotReportingCrashes.add(paramProcessRecord.info.packageName);
  }
  
  static final class BadProcessInfo
  {
    final String longMsg;
    final String shortMsg;
    final String stack;
    final long time;
    
    BadProcessInfo(long paramLong, String paramString1, String paramString2, String paramString3)
    {
      this.time = paramLong;
      this.shortMsg = paramString1;
      this.longMsg = paramString2;
      this.stack = paramString3;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppErrors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */