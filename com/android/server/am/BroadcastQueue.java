package com.android.server.am;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.server.AppOpsService;
import com.android.server.DeviceIdleController.LocalService;
import com.android.server.ServiceThread;
import com.android.server.firewall.IntentFirewall;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class BroadcastQueue
{
  static final int BROADCAST_INTENT_MSG = 200;
  private static final int BROADCAST_NEXT_MSG = 203;
  static final int BROADCAST_TIMEOUT_MSG = 201;
  private static final String CONFIG_NAME = "BroadcastOptimization";
  private static final int GET_ONLINECONFIG = 10000;
  static final int MAX_BROADCAST_HISTORY;
  static final int MAX_BROADCAST_SUMMARY_HISTORY;
  static final int SCHEDULE_TEMP_WHITELIST_MSG = 202;
  private static final String TAG = "BroadcastQueue";
  private static final String TAG_BROADCAST = "BroadcastQueue" + ActivityManagerDebugConfig.POSTFIX_BROADCAST;
  private static final String TAG_MU = "BroadcastQueue_MU";
  private static final Object mOptLock;
  private static final long mTimeoutPeriodForApp = 120000L;
  final BroadcastRecord[] mBroadcastHistory = new BroadcastRecord[MAX_BROADCAST_HISTORY];
  private ConfigObserver mBroadcastOptimizationConfigObserver;
  private ArrayList<String> mBroadcastOptimizeExcludeList = new ArrayList();
  private ArrayList<String> mBroadcastOptimizeIncludeList = new ArrayList();
  final Intent[] mBroadcastSummaryHistory = new Intent[MAX_BROADCAST_SUMMARY_HISTORY];
  boolean mBroadcastsScheduled = false;
  private Context mContext;
  final boolean mDelayBehindServices;
  final BroadcastHandler mHandler;
  private final ServiceThread mHandlerThread;
  int mHistoryNext = 0;
  final ArrayList<BroadcastRecord> mOrderedBroadcasts = new ArrayList();
  final ArrayList<BroadcastRecord> mParallelBroadcasts = new ArrayList();
  BroadcastRecord mPendingBroadcast = null;
  int mPendingBroadcastRecvIndex;
  boolean mPendingBroadcastTimeoutMessage;
  final String mQueueName;
  private final HashMap<Integer, ReceiverRecord> mReceiverRecords = new HashMap();
  final ActivityManagerService mService;
  final long[] mSummaryHistoryDispatchTime = new long[MAX_BROADCAST_SUMMARY_HISTORY];
  final long[] mSummaryHistoryEnqueueTime = new long[MAX_BROADCAST_SUMMARY_HISTORY];
  final long[] mSummaryHistoryFinishTime = new long[MAX_BROADCAST_SUMMARY_HISTORY];
  int mSummaryHistoryNext = 0;
  final long mTimeoutPeriod;
  private int s_flag = 0;
  
  static
  {
    if (ActivityManager.isLowRamDeviceStatic())
    {
      i = 10;
      MAX_BROADCAST_HISTORY = i;
      if (!ActivityManager.isLowRamDeviceStatic()) {
        break label68;
      }
    }
    label68:
    for (int i = 25;; i = 3000)
    {
      MAX_BROADCAST_SUMMARY_HISTORY = i;
      mOptLock = new Object();
      return;
      i = 500;
      break;
    }
  }
  
  BroadcastQueue(ActivityManagerService paramActivityManagerService, Handler paramHandler, String paramString, long paramLong, boolean paramBoolean)
  {
    this.mService = paramActivityManagerService;
    this.mContext = paramActivityManagerService.mContext;
    this.mHandler = new BroadcastHandler(paramHandler.getLooper());
    this.mQueueName = paramString;
    this.mTimeoutPeriod = paramLong;
    this.mDelayBehindServices = paramBoolean;
    this.mHandlerThread = new ServiceThread(paramString, 0, false);
    this.mHandlerThread.start();
    initialOptimizeIncludingList();
  }
  
  private final void addBroadcastToHistoryLocked(BroadcastRecord paramBroadcastRecord)
  {
    if (paramBroadcastRecord.callingUid < 0) {
      return;
    }
    paramBroadcastRecord.finishTime = SystemClock.uptimeMillis();
    this.mBroadcastHistory[this.mHistoryNext] = paramBroadcastRecord;
    this.mHistoryNext = ringAdvance(this.mHistoryNext, 1, MAX_BROADCAST_HISTORY);
    this.mBroadcastSummaryHistory[this.mSummaryHistoryNext] = paramBroadcastRecord.intent;
    this.mSummaryHistoryEnqueueTime[this.mSummaryHistoryNext] = paramBroadcastRecord.enqueueClockTime;
    this.mSummaryHistoryDispatchTime[this.mSummaryHistoryNext] = paramBroadcastRecord.dispatchClockTime;
    this.mSummaryHistoryFinishTime[this.mSummaryHistoryNext] = System.currentTimeMillis();
    this.mSummaryHistoryNext = ringAdvance(this.mSummaryHistoryNext, 1, MAX_BROADCAST_SUMMARY_HISTORY);
  }
  
  private void deliverToRegisteredReceiverLocked(BroadcastRecord paramBroadcastRecord, BroadcastFilter paramBroadcastFilter, boolean paramBoolean, int paramInt)
  {
    int j = 0;
    int i = j;
    int k;
    if (paramBroadcastFilter.requiredPermission != null)
    {
      if (this.mService.checkComponentPermission(paramBroadcastFilter.requiredPermission, paramBroadcastRecord.callingPid, paramBroadcastRecord.callingUid, -1, true) != 0)
      {
        Slog.w("BroadcastQueue", "Permission Denial: broadcasting " + paramBroadcastRecord.intent.toString() + " from " + paramBroadcastRecord.callerPackage + " (pid=" + paramBroadcastRecord.callingPid + ", uid=" + paramBroadcastRecord.callingUid + ")" + " requires " + paramBroadcastFilter.requiredPermission + " due to registered receiver " + paramBroadcastFilter);
        i = 1;
      }
    }
    else
    {
      j = i;
      if (i == 0)
      {
        j = i;
        if (paramBroadcastRecord.requiredPermissions != null)
        {
          j = i;
          if (paramBroadcastRecord.requiredPermissions.length > 0) {
            k = 0;
          }
        }
      }
    }
    for (;;)
    {
      j = i;
      String str;
      if (k < paramBroadcastRecord.requiredPermissions.length)
      {
        str = paramBroadcastRecord.requiredPermissions[k];
        if (this.mService.checkComponentPermission(str, paramBroadcastFilter.receiverList.pid, paramBroadcastFilter.receiverList.uid, -1, true) == 0) {
          break label1186;
        }
        Slog.w("BroadcastQueue", "Permission Denial: receiving " + paramBroadcastRecord.intent.toString() + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")" + " requires " + str + " due to sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")");
      }
      for (j = 1;; j = 1)
      {
        i = j;
        if (j == 0) {
          if (paramBroadcastRecord.requiredPermissions != null)
          {
            i = j;
            if (paramBroadcastRecord.requiredPermissions.length != 0) {}
          }
          else
          {
            i = j;
            if (this.mService.checkComponentPermission(null, paramBroadcastFilter.receiverList.pid, paramBroadcastFilter.receiverList.uid, -1, true) != 0)
            {
              Slog.w("BroadcastQueue", "Permission Denial: security check failed when receiving " + paramBroadcastRecord.intent.toString() + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")" + " due to sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")");
              i = 1;
            }
          }
        }
        j = i;
        if (i == 0)
        {
          j = i;
          if (paramBroadcastRecord.appOp != -1)
          {
            j = i;
            if (this.mService.mAppOpsService.noteOperation(paramBroadcastRecord.appOp, paramBroadcastFilter.receiverList.uid, paramBroadcastFilter.packageName) != 0)
            {
              Slog.w("BroadcastQueue", "Appop Denial: receiving " + paramBroadcastRecord.intent.toString() + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")" + " requires appop " + AppOpsManager.opToName(paramBroadcastRecord.appOp) + " due to sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")");
              j = 1;
            }
          }
        }
        i = j;
        if (j == 0)
        {
          i = j;
          if (this.mService.checkAllowBackgroundLocked(paramBroadcastFilter.receiverList.uid, paramBroadcastFilter.packageName, -1, true) == 2)
          {
            Slog.w("BroadcastQueue", "Background execution not allowed: receiving " + paramBroadcastRecord.intent + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")");
            i = 1;
          }
        }
        j = i;
        if (!this.mService.mIntentFirewall.checkBroadcast(paramBroadcastRecord.intent, paramBroadcastRecord.callingUid, paramBroadcastRecord.callingPid, paramBroadcastRecord.resolvedType, paramBroadcastFilter.receiverList.uid)) {
          j = 1;
        }
        i = j;
        if (j == 0) {
          if (paramBroadcastFilter.receiverList.app != null)
          {
            i = j;
            if (!paramBroadcastFilter.receiverList.app.crashing) {}
          }
          else
          {
            Slog.w("BroadcastQueue", "Skipping deliver [" + this.mQueueName + "] " + paramBroadcastRecord + " to " + paramBroadcastFilter.receiverList + ": process crashing");
            i = 1;
          }
        }
        j = i;
        if (i == 0)
        {
          j = i;
          if (OnePlusProcessManager.skipBroadcast(paramBroadcastFilter, paramBroadcastRecord, paramBoolean)) {
            j = 1;
          }
        }
        if (j == 0) {
          break label1381;
        }
        paramBroadcastRecord.delivery[paramInt] = 2;
        return;
        k = AppOpsManager.permissionToOpCode(paramBroadcastFilter.requiredPermission);
        i = j;
        if (k == -1) {
          break;
        }
        i = j;
        if (this.mService.mAppOpsService.noteOperation(k, paramBroadcastRecord.callingUid, paramBroadcastRecord.callerPackage) == 0) {
          break;
        }
        Slog.w("BroadcastQueue", "Appop Denial: broadcasting " + paramBroadcastRecord.intent.toString() + " from " + paramBroadcastRecord.callerPackage + " (pid=" + paramBroadcastRecord.callingPid + ", uid=" + paramBroadcastRecord.callingUid + ")" + " requires appop " + AppOpsManager.permissionToOp(paramBroadcastFilter.requiredPermission) + " due to registered receiver " + paramBroadcastFilter);
        i = 1;
        break;
        label1186:
        j = AppOpsManager.permissionToOpCode(str);
        if ((j == -1) || (j == paramBroadcastRecord.appOp) || (this.mService.mAppOpsService.noteOperation(j, paramBroadcastFilter.receiverList.uid, paramBroadcastFilter.packageName) == 0)) {
          break label1372;
        }
        Slog.w("BroadcastQueue", "Appop Denial: receiving " + paramBroadcastRecord.intent.toString() + " to " + paramBroadcastFilter.receiverList.app + " (pid=" + paramBroadcastFilter.receiverList.pid + ", uid=" + paramBroadcastFilter.receiverList.uid + ")" + " requires appop " + AppOpsManager.permissionToOp(str) + " due to sender " + paramBroadcastRecord.callerPackage + " (uid " + paramBroadcastRecord.callingUid + ")");
      }
      label1372:
      k += 1;
    }
    label1381:
    if ((Build.PERMISSIONS_REVIEW_REQUIRED) && (!requestStartTargetPermissionsReviewIfNeededLocked(paramBroadcastRecord, paramBroadcastFilter.packageName, paramBroadcastFilter.owningUserId)))
    {
      paramBroadcastRecord.delivery[paramInt] = 2;
      return;
    }
    paramBroadcastRecord.delivery[paramInt] = 1;
    if (paramBoolean)
    {
      paramBroadcastRecord.receiver = paramBroadcastFilter.receiverList.receiver.asBinder();
      paramBroadcastRecord.curFilter = paramBroadcastFilter;
      paramBroadcastFilter.receiverList.curBroadcast = paramBroadcastRecord;
      paramBroadcastRecord.state = 2;
      if (paramBroadcastFilter.receiverList.app != null)
      {
        paramBroadcastRecord.curApp = paramBroadcastFilter.receiverList.app;
        paramBroadcastFilter.receiverList.app.curReceiver = paramBroadcastRecord;
        this.mService.updateOomAdjLocked(paramBroadcastRecord.curApp);
      }
    }
    try
    {
      if (ActivityManagerDebugConfig.DEBUG_BROADCAST_LIGHT) {
        Slog.i(TAG_BROADCAST, "Delivering to " + paramBroadcastFilter + " : " + paramBroadcastRecord);
      }
      if ((paramBroadcastFilter.receiverList.app != null) && (paramBroadcastFilter.receiverList.app.inFullBackup)) {
        if (paramBoolean) {
          skipReceiverLocked(paramBroadcastRecord);
        }
      }
      while (paramBoolean)
      {
        paramBroadcastRecord.state = 3;
        return;
        performReceiveLocked(paramBroadcastFilter.receiverList.app, paramBroadcastFilter.receiverList.receiver, new Intent(paramBroadcastRecord.intent), paramBroadcastRecord.resultCode, paramBroadcastRecord.resultData, paramBroadcastRecord.resultExtras, paramBroadcastRecord.ordered, paramBroadcastRecord.initialSticky, paramBroadcastRecord.userId);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("BroadcastQueue", "Failure sending broadcast " + paramBroadcastRecord.intent, localRemoteException);
      if (paramBoolean)
      {
        paramBroadcastRecord.receiver = null;
        paramBroadcastRecord.curFilter = null;
        paramBroadcastFilter.receiverList.curBroadcast = null;
        if (paramBroadcastFilter.receiverList.app != null) {
          paramBroadcastFilter.receiverList.app.curReceiver = null;
        }
      }
    }
  }
  
  private void initialOptimizeIncludingList()
  {
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.ANY_DATA_STATE");
    this.mBroadcastOptimizeIncludeList.add("android.media.AUDIO_BECOMING_NOISY");
    this.mBroadcastOptimizeIncludeList.add("android.os.storage.action.VOLUME_STATE_CHANGED");
    this.mBroadcastOptimizeIncludeList.add("android.net.wifi.WIFI_STATE_CHANGED");
    this.mBroadcastOptimizeIncludeList.add("android.net.wifi.STATE_CHANGE");
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.MEDIA_MOUNTED");
    this.mBroadcastOptimizeIncludeList.add("android.settings.OEM_THEME_MODE.init");
    this.mBroadcastOptimizeIncludeList.add("android.appwidget.action.APPWIDGET_UPDATE");
    this.mBroadcastOptimizeIncludeList.add("android.appwidget.action.APPWIDGET_ENABLED");
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.SIM_STATE_CHANGED");
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.PROVIDER_CHANGED");
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.SERVICE_STATE");
    this.mBroadcastOptimizeIncludeList.add("android.net.wifi.supplicant.STATE_CHANGE");
    this.mBroadcastOptimizeIncludeList.add("android.intent.action.MEDIA_SCANNER_STARTED");
    this.mBroadcastOptimizeIncludeList.add("android.hardware.usb.action.USB_STATE");
    this.mBroadcastOptimizeIncludeList.add("android.media.SCO_AUDIO_STATE_CHANGED");
    this.mBroadcastOptimizeIncludeList.add("android.media.RINGER_MODE_CHANGED");
  }
  
  /* Error */
  private final void processCurBroadcastLocked(BroadcastRecord paramBroadcastRecord, ProcessRecord paramProcessRecord)
    throws RemoteException
  {
    // Byte code:
    //   0: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   3: ifeq +40 -> 43
    //   6: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   9: new 104	java/lang/StringBuilder
    //   12: dup
    //   13: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   16: ldc_w 530
    //   19: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: aload_1
    //   23: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   26: ldc_w 532
    //   29: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: aload_2
    //   33: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   36: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   42: pop
    //   43: aload_2
    //   44: getfield 539	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   47: ifnonnull +11 -> 58
    //   50: new 245	android/os/RemoteException
    //   53: dup
    //   54: invokespecial 540	android/os/RemoteException:<init>	()V
    //   57: athrow
    //   58: aload_2
    //   59: getfield 451	com/android/server/am/ProcessRecord:inFullBackup	Z
    //   62: ifeq +9 -> 71
    //   65: aload_0
    //   66: aload_1
    //   67: invokespecial 454	com/android/server/am/BroadcastQueue:skipReceiverLocked	(Lcom/android/server/am/BroadcastRecord;)V
    //   70: return
    //   71: aload_1
    //   72: aload_2
    //   73: getfield 539	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   76: invokeinterface 543 1 0
    //   81: putfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   84: aload_1
    //   85: aload_2
    //   86: putfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   89: aload_2
    //   90: aload_1
    //   91: putfield 434	com/android/server/am/ProcessRecord:curReceiver	Lcom/android/server/am/BroadcastRecord;
    //   94: aload_2
    //   95: bipush 11
    //   97: invokevirtual 547	com/android/server/am/ProcessRecord:forceProcessStateUpTo	(I)V
    //   100: aload_0
    //   101: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   104: aload_2
    //   105: iconst_0
    //   106: aconst_null
    //   107: invokevirtual 551	com/android/server/am/ActivityManagerService:updateLruProcessLocked	(Lcom/android/server/am/ProcessRecord;ZLcom/android/server/am/ProcessRecord;)V
    //   110: aload_0
    //   111: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   114: invokevirtual 553	com/android/server/am/ActivityManagerService:updateOomAdjLocked	()V
    //   117: aload_1
    //   118: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   121: aload_1
    //   122: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   125: invokevirtual 561	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   128: pop
    //   129: iconst_0
    //   130: istore 5
    //   132: iconst_0
    //   133: istore 4
    //   135: iload 5
    //   137: istore_3
    //   138: aload_0
    //   139: aload_2
    //   140: aload_1
    //   141: invokestatic 564	com/android/server/am/OnePlusProcessManager:checkBroadcast	(Lcom/android/server/am/BroadcastQueue;Lcom/android/server/am/ProcessRecord;Lcom/android/server/am/BroadcastRecord;)Z
    //   144: ifeq +661 -> 805
    //   147: iload 5
    //   149: istore_3
    //   150: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   153: ifeq +46 -> 199
    //   156: iload 5
    //   158: istore_3
    //   159: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   162: new 104	java/lang/StringBuilder
    //   165: dup
    //   166: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   169: ldc_w 566
    //   172: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload_1
    //   176: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   179: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   182: ldc_w 568
    //   185: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload_1
    //   189: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   192: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   195: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: iload 5
    //   201: istore_3
    //   202: aload_0
    //   203: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   206: aload_1
    //   207: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   210: invokevirtual 572	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   213: invokevirtual 577	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   216: iconst_3
    //   217: invokevirtual 581	com/android/server/am/ActivityManagerService:notifyPackageUse	(Ljava/lang/String;I)V
    //   220: iload 5
    //   222: istore_3
    //   223: new 150	android/content/Intent
    //   226: dup
    //   227: aload_1
    //   228: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   231: invokespecial 457	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   234: astore 7
    //   236: iload 5
    //   238: istore_3
    //   239: aload_0
    //   240: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   243: astore 8
    //   245: iload 5
    //   247: istore_3
    //   248: aload_1
    //   249: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   252: astore 9
    //   254: iload 5
    //   256: istore_3
    //   257: aload_2
    //   258: getfield 539	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   261: invokeinterface 543 1 0
    //   266: astore 10
    //   268: iload 5
    //   270: istore_3
    //   271: aload_0
    //   272: getfield 202	com/android/server/am/BroadcastQueue:mHandlerThread	Lcom/android/server/ServiceThread;
    //   275: invokevirtual 582	com/android/server/ServiceThread:getLooper	()Landroid/os/Looper;
    //   278: astore 11
    //   280: iload 5
    //   282: istore_3
    //   283: aload_0
    //   284: getfield 173	com/android/server/am/BroadcastQueue:s_flag	I
    //   287: iconst_1
    //   288: iadd
    //   289: istore 4
    //   291: iload 5
    //   293: istore_3
    //   294: aload_0
    //   295: iload 4
    //   297: putfield 173	com/android/server/am/BroadcastQueue:s_flag	I
    //   300: iload 5
    //   302: istore_3
    //   303: new 584	com/android/server/am/ReceiverRecord
    //   306: dup
    //   307: aload 8
    //   309: aload_0
    //   310: aload_1
    //   311: aload 9
    //   313: aload 10
    //   315: aload 7
    //   317: aload 11
    //   319: iload 4
    //   321: invokespecial 587	com/android/server/am/ReceiverRecord:<init>	(Lcom/android/server/am/ActivityManagerService;Lcom/android/server/am/BroadcastQueue;Lcom/android/server/am/BroadcastRecord;Lcom/android/server/am/ProcessRecord;Landroid/os/IBinder;Landroid/content/Intent;Landroid/os/Looper;I)V
    //   324: astore 8
    //   326: iload 5
    //   328: istore_3
    //   329: aload_2
    //   330: getfield 539	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   333: new 150	android/content/Intent
    //   336: dup
    //   337: aload_1
    //   338: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   341: invokespecial 457	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   344: aload_1
    //   345: getfield 590	com/android/server/am/BroadcastRecord:curReceiver	Landroid/content/pm/ActivityInfo;
    //   348: aload_0
    //   349: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   352: aload_1
    //   353: getfield 590	com/android/server/am/BroadcastRecord:curReceiver	Landroid/content/pm/ActivityInfo;
    //   356: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   359: invokevirtual 600	com/android/server/am/ActivityManagerService:compatibilityInfoForPackageLocked	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/CompatibilityInfo;
    //   362: aload_1
    //   363: getfield 460	com/android/server/am/BroadcastRecord:resultCode	I
    //   366: aload_1
    //   367: getfield 463	com/android/server/am/BroadcastRecord:resultData	Ljava/lang/String;
    //   370: aload_1
    //   371: getfield 467	com/android/server/am/BroadcastRecord:resultExtras	Landroid/os/Bundle;
    //   374: aload_1
    //   375: getfield 470	com/android/server/am/BroadcastRecord:ordered	Z
    //   378: aload_1
    //   379: getfield 476	com/android/server/am/BroadcastRecord:userId	I
    //   382: aload_2
    //   383: getfield 603	com/android/server/am/ProcessRecord:repProcState	I
    //   386: aload 8
    //   388: invokevirtual 607	com/android/server/am/ReceiverRecord:hashCode	()I
    //   391: invokeinterface 611 11 0
    //   396: iload 5
    //   398: istore_3
    //   399: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   402: ifeq +43 -> 445
    //   405: iload 5
    //   407: istore_3
    //   408: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   411: new 104	java/lang/StringBuilder
    //   414: dup
    //   415: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   418: ldc_w 530
    //   421: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: aload_1
    //   425: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   428: ldc_w 613
    //   431: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   434: aload_2
    //   435: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   438: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   441: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   444: pop
    //   445: iconst_1
    //   446: istore 6
    //   448: iconst_1
    //   449: istore 5
    //   451: iload 5
    //   453: istore 4
    //   455: iload 6
    //   457: istore_3
    //   458: aload_1
    //   459: getfield 470	com/android/server/am/BroadcastRecord:ordered	Z
    //   462: ifne +343 -> 805
    //   465: iload 6
    //   467: istore_3
    //   468: getstatic 132	com/android/server/am/BroadcastQueue:mOptLock	Ljava/lang/Object;
    //   471: astore 9
    //   473: iload 6
    //   475: istore_3
    //   476: aload 9
    //   478: monitorenter
    //   479: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   482: ifeq +59 -> 541
    //   485: ldc 35
    //   487: new 104	java/lang/StringBuilder
    //   490: dup
    //   491: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   494: ldc_w 615
    //   497: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   500: aload_0
    //   501: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   504: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: ldc_w 617
    //   510: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   513: aload_0
    //   514: getfield 171	com/android/server/am/BroadcastQueue:mReceiverRecords	Ljava/util/HashMap;
    //   517: invokevirtual 620	java/util/HashMap:size	()I
    //   520: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   523: ldc_w 622
    //   526: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   529: aload 8
    //   531: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   534: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   537: invokestatic 448	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   540: pop
    //   541: aload_0
    //   542: getfield 171	com/android/server/am/BroadcastQueue:mReceiverRecords	Ljava/util/HashMap;
    //   545: aload 8
    //   547: invokevirtual 607	com/android/server/am/ReceiverRecord:hashCode	()I
    //   550: invokestatic 628	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   553: aload 8
    //   555: invokevirtual 632	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   558: pop
    //   559: aload_2
    //   560: getfield 635	com/android/server/am/ProcessRecord:ReceiverRecords	Ljava/util/ArrayList;
    //   563: aload 8
    //   565: invokevirtual 491	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   568: pop
    //   569: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   572: ifeq +105 -> 677
    //   575: aload_0
    //   576: getfield 171	com/android/server/am/BroadcastQueue:mReceiverRecords	Ljava/util/HashMap;
    //   579: invokevirtual 620	java/util/HashMap:size	()I
    //   582: istore 4
    //   584: aload_0
    //   585: getfield 171	com/android/server/am/BroadcastQueue:mReceiverRecords	Ljava/util/HashMap;
    //   588: invokevirtual 639	java/util/HashMap:values	()Ljava/util/Collection;
    //   591: invokeinterface 645 1 0
    //   596: astore 10
    //   598: iconst_0
    //   599: istore_3
    //   600: aload 10
    //   602: invokeinterface 650 1 0
    //   607: ifeq +70 -> 677
    //   610: aload 10
    //   612: invokeinterface 654 1 0
    //   617: checkcast 584	com/android/server/am/ReceiverRecord
    //   620: astore 11
    //   622: ldc 35
    //   624: new 104	java/lang/StringBuilder
    //   627: dup
    //   628: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   631: ldc_w 656
    //   634: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   637: iload 4
    //   639: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   642: ldc_w 658
    //   645: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   648: iload_3
    //   649: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   652: ldc_w 660
    //   655: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   658: aload 11
    //   660: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   663: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   666: invokestatic 448	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   669: pop
    //   670: iload_3
    //   671: iconst_1
    //   672: iadd
    //   673: istore_3
    //   674: goto -74 -> 600
    //   677: iload 6
    //   679: istore_3
    //   680: aload 9
    //   682: monitorexit
    //   683: iload 6
    //   685: istore_3
    //   686: aload 7
    //   688: invokevirtual 663	android/content/Intent:getAction	()Ljava/lang/String;
    //   691: astore 9
    //   693: iload 6
    //   695: istore_3
    //   696: aload_0
    //   697: getfield 138	com/android/server/am/BroadcastQueue:mBroadcastOptimizeIncludeList	Ljava/util/ArrayList;
    //   700: astore 7
    //   702: iload 6
    //   704: istore_3
    //   705: aload 7
    //   707: monitorenter
    //   708: aload_0
    //   709: getfield 138	com/android/server/am/BroadcastQueue:mBroadcastOptimizeIncludeList	Ljava/util/ArrayList;
    //   712: aload 9
    //   714: invokevirtual 666	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   717: ifeq +78 -> 795
    //   720: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   723: ifeq +30 -> 753
    //   726: ldc 35
    //   728: new 104	java/lang/StringBuilder
    //   731: dup
    //   732: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   735: ldc_w 668
    //   738: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   741: aload 9
    //   743: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   746: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   749: invokestatic 671	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   752: pop
    //   753: aload 8
    //   755: ldc2_w 44
    //   758: invokevirtual 675	com/android/server/am/ReceiverRecord:setBroadcastTimeoutLocked	(J)V
    //   761: aload 8
    //   763: invokevirtual 678	com/android/server/am/ReceiverRecord:linkBinder	()V
    //   766: invokestatic 684	android/os/Message:obtain	()Landroid/os/Message;
    //   769: astore 8
    //   771: aload 8
    //   773: sipush 203
    //   776: putfield 687	android/os/Message:what	I
    //   779: aload 8
    //   781: aload_1
    //   782: putfield 690	android/os/Message:obj	Ljava/lang/Object;
    //   785: aload_0
    //   786: getfield 189	com/android/server/am/BroadcastQueue:mHandler	Lcom/android/server/am/BroadcastQueue$BroadcastHandler;
    //   789: aload 8
    //   791: invokevirtual 694	com/android/server/am/BroadcastQueue$BroadcastHandler:sendMessage	(Landroid/os/Message;)Z
    //   794: pop
    //   795: iload 6
    //   797: istore_3
    //   798: aload 7
    //   800: monitorexit
    //   801: iload 5
    //   803: istore 4
    //   805: iload 4
    //   807: ifne +57 -> 864
    //   810: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   813: ifeq +36 -> 849
    //   816: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   819: new 104	java/lang/StringBuilder
    //   822: dup
    //   823: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   826: ldc_w 530
    //   829: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   832: aload_1
    //   833: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   836: ldc_w 696
    //   839: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   842: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   845: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   848: pop
    //   849: aload_1
    //   850: aconst_null
    //   851: putfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   854: aload_1
    //   855: aconst_null
    //   856: putfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   859: aload_2
    //   860: aconst_null
    //   861: putfield 434	com/android/server/am/ProcessRecord:curReceiver	Lcom/android/server/am/BroadcastRecord;
    //   864: return
    //   865: astore 7
    //   867: iload 6
    //   869: istore_3
    //   870: aload 9
    //   872: monitorexit
    //   873: iload 6
    //   875: istore_3
    //   876: aload 7
    //   878: athrow
    //   879: astore 7
    //   881: iload_3
    //   882: ifne +57 -> 939
    //   885: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   888: ifeq +36 -> 924
    //   891: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   894: new 104	java/lang/StringBuilder
    //   897: dup
    //   898: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   901: ldc_w 530
    //   904: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   907: aload_1
    //   908: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   911: ldc_w 696
    //   914: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   917: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   920: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   923: pop
    //   924: aload_1
    //   925: aconst_null
    //   926: putfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   929: aload_1
    //   930: aconst_null
    //   931: putfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   934: aload_2
    //   935: aconst_null
    //   936: putfield 434	com/android/server/am/ProcessRecord:curReceiver	Lcom/android/server/am/BroadcastRecord;
    //   939: aload 7
    //   941: athrow
    //   942: astore 8
    //   944: iload 6
    //   946: istore_3
    //   947: aload 7
    //   949: monitorexit
    //   950: iload 6
    //   952: istore_3
    //   953: aload 8
    //   955: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	956	0	this	BroadcastQueue
    //   0	956	1	paramBroadcastRecord	BroadcastRecord
    //   0	956	2	paramProcessRecord	ProcessRecord
    //   137	816	3	i	int
    //   133	673	4	j	int
    //   130	672	5	k	int
    //   446	505	6	m	int
    //   234	565	7	localObject1	Object
    //   865	12	7	localObject2	Object
    //   879	69	7	localObject3	Object
    //   243	547	8	localObject4	Object
    //   942	12	8	localObject5	Object
    //   252	619	9	localObject6	Object
    //   266	345	10	localObject7	Object
    //   278	381	11	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   479	541	865	finally
    //   541	598	865	finally
    //   600	670	865	finally
    //   138	147	879	finally
    //   150	156	879	finally
    //   159	199	879	finally
    //   202	220	879	finally
    //   223	236	879	finally
    //   239	245	879	finally
    //   248	254	879	finally
    //   257	268	879	finally
    //   271	280	879	finally
    //   283	291	879	finally
    //   294	300	879	finally
    //   303	326	879	finally
    //   329	396	879	finally
    //   399	405	879	finally
    //   408	445	879	finally
    //   458	465	879	finally
    //   468	473	879	finally
    //   476	479	879	finally
    //   680	683	879	finally
    //   686	693	879	finally
    //   696	702	879	finally
    //   705	708	879	finally
    //   798	801	879	finally
    //   870	873	879	finally
    //   876	879	879	finally
    //   947	950	879	finally
    //   953	956	879	finally
    //   708	753	942	finally
    //   753	795	942	finally
  }
  
  private boolean requestStartTargetPermissionsReviewIfNeededLocked(BroadcastRecord paramBroadcastRecord, String paramString, final int paramInt)
  {
    if (!this.mService.getPackageManagerInternalLocked().isPermissionsReviewRequired(paramString, paramInt)) {
      return true;
    }
    int i;
    if (paramBroadcastRecord.callerApp != null) {
      if (paramBroadcastRecord.callerApp.setSchedGroup != 0)
      {
        i = 1;
        if ((i == 0) || (paramBroadcastRecord.intent.getComponent() == null)) {
          break label260;
        }
        final Object localObject = this.mService;
        String str = paramBroadcastRecord.callerPackage;
        i = paramBroadcastRecord.callingUid;
        int j = paramBroadcastRecord.userId;
        Intent localIntent = paramBroadcastRecord.intent;
        paramBroadcastRecord = paramBroadcastRecord.intent.resolveType(this.mService.mContext.getContentResolver());
        paramBroadcastRecord = ((ActivityManagerService)localObject).getIntentSenderLocked(1, str, i, j, null, null, 0, new Intent[] { localIntent }, new String[] { paramBroadcastRecord }, 1409286144, null);
        localObject = new Intent("android.intent.action.REVIEW_PERMISSIONS");
        ((Intent)localObject).addFlags(276824064);
        ((Intent)localObject).putExtra("android.intent.extra.PACKAGE_NAME", paramString);
        ((Intent)localObject).putExtra("android.intent.extra.INTENT", new IntentSender(paramBroadcastRecord));
        if (ActivityManagerDebugConfig.DEBUG_PERMISSIONS_REVIEW) {
          Slog.i("BroadcastQueue", "u" + paramInt + " Launching permission review for package " + paramString);
        }
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            BroadcastQueue.this.mService.mContext.startActivityAsUser(localObject, new UserHandle(paramInt));
          }
        });
      }
    }
    for (;;)
    {
      return false;
      i = 0;
      break;
      i = 1;
      break;
      label260:
      Slog.w("BroadcastQueue", "u" + paramInt + " Receiving a broadcast in package" + paramString + " requires a permissions review");
    }
  }
  
  private void resolveConfigFromJSON(JSONArray paramJSONArray)
  {
    if (paramJSONArray == null)
    {
      Slog.v("BroadcastQueue", "[OnlineConfig] Add to mBroadcastOptimizeIncludeList: " + this.mBroadcastOptimizeIncludeList);
      return;
    }
    int i = 0;
    for (;;)
    {
      try
      {
        if (i < paramJSONArray.length())
        {
          ??? = paramJSONArray.getJSONObject(i);
          if (!((JSONObject)???).getString("name").equals("broadcast_optimize_include_list")) {
            break label210;
          }
          JSONArray localJSONArray = ((JSONObject)???).getJSONArray("value");
          synchronized (this.mBroadcastOptimizeIncludeList)
          {
            this.mBroadcastOptimizeIncludeList.clear();
            int j = 0;
            if (j < localJSONArray.length())
            {
              this.mBroadcastOptimizeIncludeList.add(localJSONArray.getString(j));
              Slog.v("BroadcastQueue", "[OnlineConfig] Add to mBroadcastOptimizeIncludeList: " + localJSONArray.getString(j));
              j += 1;
              continue;
            }
          }
        }
        Slog.v("BroadcastQueue", "[OnlineConfig] BroadcastOptimization updated complete");
      }
      catch (JSONException paramJSONArray)
      {
        Slog.e("BroadcastQueue", "[OnlineConfig] resolveConfigFromJSON, error message:" + paramJSONArray.getMessage());
        return;
      }
      return;
      label210:
      i += 1;
    }
  }
  
  private final int ringAdvance(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 += paramInt2;
    if (paramInt1 < 0) {
      return paramInt3 - 1;
    }
    if (paramInt1 >= paramInt3) {
      return 0;
    }
    return paramInt1;
  }
  
  private void skipReceiverLocked(BroadcastRecord paramBroadcastRecord)
  {
    logBroadcastReceiverDiscardLocked(paramBroadcastRecord);
    finishReceiverLocked(paramBroadcastRecord, paramBroadcastRecord.resultCode, paramBroadcastRecord.resultData, paramBroadcastRecord.resultExtras, paramBroadcastRecord.resultAbort, false);
    scheduleBroadcastsLocked();
  }
  
  public void backgroundServicesFinishedLocked(int paramInt)
  {
    if (this.mOrderedBroadcasts.size() > 0)
    {
      BroadcastRecord localBroadcastRecord = (BroadcastRecord)this.mOrderedBroadcasts.get(0);
      if ((localBroadcastRecord.userId == paramInt) && (localBroadcastRecord.state == 4))
      {
        Slog.i("BroadcastQueue", "Resuming delayed broadcast");
        localBroadcastRecord.curComponent = null;
        localBroadcastRecord.state = 0;
        processNextBroadcast(false);
      }
    }
  }
  
  final void broadcastTimeoutLocked(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPendingBroadcastTimeoutMessage = false;
    }
    if (this.mOrderedBroadcasts.size() == 0) {
      return;
    }
    long l1 = SystemClock.uptimeMillis();
    BroadcastRecord localBroadcastRecord = (BroadcastRecord)this.mOrderedBroadcasts.get(0);
    if (paramBoolean)
    {
      if (this.mService.mDidDexOpt)
      {
        this.mService.mDidDexOpt = false;
        setBroadcastTimeoutLocked(SystemClock.uptimeMillis() + this.mTimeoutPeriod);
        return;
      }
      if (!this.mService.mProcessesReady) {
        return;
      }
      long l2 = localBroadcastRecord.receiverTime + this.mTimeoutPeriod;
      if (l2 > l1)
      {
        if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
          Slog.v(TAG_BROADCAST, "Premature timeout [" + this.mQueueName + "] @ " + l1 + ": resetting BROADCAST_TIMEOUT_MSG for " + l2);
        }
        setBroadcastTimeoutLocked(l2);
        return;
      }
    }
    Object localObject3 = (BroadcastRecord)this.mOrderedBroadcasts.get(0);
    if (((BroadcastRecord)localObject3).state == 4)
    {
      ??? = new StringBuilder().append("Waited long enough for: ");
      if (((BroadcastRecord)localObject3).curComponent != null) {}
      for (localObject1 = ((BroadcastRecord)localObject3).curComponent.flattenToShortString();; localObject1 = "(null)")
      {
        Slog.i("BroadcastQueue", (String)localObject1);
        ((BroadcastRecord)localObject3).curComponent = null;
        ((BroadcastRecord)localObject3).state = 0;
        processNextBroadcast(false);
        return;
      }
    }
    Slog.w("BroadcastQueue", "Timeout of broadcast " + localBroadcastRecord + " - receiver=" + localBroadcastRecord.receiver + ", started " + (l1 - localBroadcastRecord.receiverTime) + "ms ago");
    localBroadcastRecord.receiverTime = l1;
    localBroadcastRecord.anrCount += 1;
    if (localBroadcastRecord.nextReceiver <= 0)
    {
      Slog.w("BroadcastQueue", "Timeout on receiver with nextReceiver <= 0");
      return;
    }
    ??? = null;
    localObject3 = null;
    Object localObject1 = localBroadcastRecord.receivers.get(localBroadcastRecord.nextReceiver - 1);
    localBroadcastRecord.delivery[(localBroadcastRecord.nextReceiver - 1)] = 3;
    Slog.w("BroadcastQueue", "Receiver during timeout: " + localObject1);
    logBroadcastReceiverDiscardLocked(localBroadcastRecord);
    BroadcastFilter localBroadcastFilter;
    if ((localObject1 instanceof BroadcastFilter))
    {
      localBroadcastFilter = (BroadcastFilter)localObject1;
      localObject1 = ???;
      if (localBroadcastFilter.receiverList.pid != 0)
      {
        localObject1 = ???;
        if (localBroadcastFilter.receiverList.pid == ActivityManagerService.MY_PID) {}
      }
    }
    ProcessRecord localProcessRecord;
    for (;;)
    {
      synchronized (this.mService.mPidsSelfLocked)
      {
        localObject1 = (ProcessRecord)this.mService.mPidsSelfLocked.get(localBroadcastFilter.receiverList.pid);
        if (localObject1 != null) {
          localObject3 = "Broadcast of " + localBroadcastRecord.intent.toString();
        }
        if (this.mPendingBroadcast == localBroadcastRecord) {
          this.mPendingBroadcast = null;
        }
        finishReceiverLocked(localBroadcastRecord, localBroadcastRecord.resultCode, localBroadcastRecord.resultData, localBroadcastRecord.resultExtras, localBroadcastRecord.resultAbort, false);
        scheduleBroadcastsLocked();
        if (localObject3 == null) {
          return;
        }
        if (!OnePlusProcessManager.checkProcessWhileBroadcastTimeout((ProcessRecord)localObject1)) {
          break;
        }
        return;
      }
      localProcessRecord = localBroadcastRecord.curApp;
    }
    this.mHandler.post(new AppNotResponding(localProcessRecord, (String)localObject3));
  }
  
  final void cancelBroadcastTimeoutLocked()
  {
    if (this.mPendingBroadcastTimeoutMessage)
    {
      this.mHandler.removeMessages(201, this);
      this.mPendingBroadcastTimeoutMessage = false;
    }
  }
  
  boolean cleanupDisabledPackageReceiversLocked(String paramString, Set<String> paramSet, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    int i = this.mParallelBroadcasts.size() - 1;
    while (i >= 0)
    {
      bool |= ((BroadcastRecord)this.mParallelBroadcasts.get(i)).cleanupDisabledPackageReceiversLocked(paramString, paramSet, paramInt, paramBoolean);
      if ((!paramBoolean) && (bool)) {
        return true;
      }
      i -= 1;
    }
    i = this.mOrderedBroadcasts.size() - 1;
    while (i >= 0)
    {
      bool |= ((BroadcastRecord)this.mOrderedBroadcasts.get(i)).cleanupDisabledPackageReceiversLocked(paramString, paramSet, paramInt, paramBoolean);
      if ((!paramBoolean) && (bool)) {
        return true;
      }
      i -= 1;
    }
    return bool;
  }
  
  final boolean dumpLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, int paramInt, boolean paramBoolean1, String paramString, boolean paramBoolean2)
  {
    paramFileDescriptor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if ((this.mParallelBroadcasts.size() > 0) || (this.mOrderedBroadcasts.size() > 0)) {}
    int j;
    while (this.mPendingBroadcast != null)
    {
      paramInt = 0;
      j = this.mParallelBroadcasts.size() - 1;
      while (j >= 0)
      {
        paramArrayOfString = (BroadcastRecord)this.mParallelBroadcasts.get(j);
        if (paramString != null)
        {
          i = paramInt;
          bool1 = paramBoolean2;
          if (!paramString.equals(paramArrayOfString.callerPackage)) {}
        }
        else
        {
          i = paramInt;
          bool1 = paramBoolean2;
          if (paramInt == 0)
          {
            if (paramBoolean2) {
              paramPrintWriter.println();
            }
            bool1 = true;
            i = 1;
            paramPrintWriter.println("  Active broadcasts [" + this.mQueueName + "]:");
          }
          paramPrintWriter.println("  Active Broadcast " + this.mQueueName + " #" + j + ":");
          paramArrayOfString.dump(paramPrintWriter, "    ", paramFileDescriptor);
        }
        j -= 1;
        paramInt = i;
        paramBoolean2 = bool1;
      }
    }
    boolean bool1 = paramBoolean2;
    int i = 0;
    int m = -1;
    int n = this.mHistoryNext;
    int k = n;
    int i1;
    label282:
    do
    {
      i1 = ringAdvance(k, -1, MAX_BROADCAST_HISTORY);
      paramArrayOfString = this.mBroadcastHistory[i1];
      if (paramArrayOfString != null) {
        break;
      }
      paramBoolean2 = bool1;
      j = i;
      paramInt = m;
      m = paramInt;
      i = j;
      k = i1;
      bool1 = paramBoolean2;
    } while (i1 != n);
    boolean bool2 = paramBoolean2;
    if (paramString == null)
    {
      i1 = this.mSummaryHistoryNext;
      if (!paramBoolean1) {
        break label1083;
      }
      i = 0;
      k = -1;
      m = i1;
      bool1 = paramBoolean2;
      label339:
      j = ringAdvance(m, -1, MAX_BROADCAST_SUMMARY_HISTORY);
      paramArrayOfString = this.mBroadcastSummaryHistory[j];
      if (paramArrayOfString != null) {
        break label1174;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      m = j;
      bool1 = bool2;
      if (j != i1) {
        break label339;
      }
      for (;;)
      {
        if (this.mBroadcastOptimizeIncludeList.size() <= 0) {
          break label1496;
        }
        paramPrintWriter.println(" Broadcast optimization white list:");
        paramInt = 0;
        while (paramInt < this.mBroadcastOptimizeIncludeList.size())
        {
          paramPrintWriter.println("\t" + (String)this.mBroadcastOptimizeIncludeList.get(paramInt));
          paramInt += 1;
        }
        paramInt = 0;
        paramBoolean2 = true;
        j = this.mOrderedBroadcasts.size() - 1;
        while (j >= 0)
        {
          paramArrayOfString = (BroadcastRecord)this.mOrderedBroadcasts.get(j);
          if (paramString != null)
          {
            i = paramInt;
            bool1 = paramBoolean2;
            if (!paramString.equals(paramArrayOfString.callerPackage)) {}
          }
          else
          {
            i = paramInt;
            bool1 = paramBoolean2;
            if (paramInt == 0)
            {
              if (paramBoolean2) {
                paramPrintWriter.println();
              }
              bool1 = true;
              i = 1;
              paramPrintWriter.println("  Active ordered broadcasts [" + this.mQueueName + "]:");
            }
            paramPrintWriter.println("  Active Ordered Broadcast " + this.mQueueName + " #" + j + ":");
            ((BroadcastRecord)this.mOrderedBroadcasts.get(j)).dump(paramPrintWriter, "    ", paramFileDescriptor);
          }
          j -= 1;
          paramInt = i;
          paramBoolean2 = bool1;
        }
        if (paramString != null)
        {
          bool1 = paramBoolean2;
          if (this.mPendingBroadcast == null) {
            break;
          }
          bool1 = paramBoolean2;
          if (!paramString.equals(this.mPendingBroadcast.callerPackage)) {
            break;
          }
        }
        if (paramBoolean2) {
          paramPrintWriter.println();
        }
        paramPrintWriter.println("  Pending broadcast [" + this.mQueueName + "]:");
        if (this.mPendingBroadcast != null) {
          this.mPendingBroadcast.dump(paramPrintWriter, "    ", paramFileDescriptor);
        }
        for (;;)
        {
          bool1 = true;
          break;
          paramPrintWriter.println("    (null)");
        }
        m += 1;
        if (paramString != null)
        {
          paramInt = m;
          j = i;
          paramBoolean2 = bool1;
          if (!paramString.equals(paramArrayOfString.callerPackage)) {
            break label282;
          }
        }
        k = i;
        bool2 = bool1;
        if (i == 0)
        {
          if (bool1) {
            paramPrintWriter.println();
          }
          bool2 = true;
          paramPrintWriter.println("  Historical broadcasts [" + this.mQueueName + "]:");
          k = 1;
        }
        if (paramBoolean1)
        {
          paramPrintWriter.print("  Historical Broadcast " + this.mQueueName + " #");
          paramPrintWriter.print(m);
          paramPrintWriter.println(":");
          paramArrayOfString.dump(paramPrintWriter, "    ", paramFileDescriptor);
          paramInt = m;
          j = k;
          paramBoolean2 = bool2;
          break label282;
        }
        paramPrintWriter.print("  #");
        paramPrintWriter.print(m);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(paramArrayOfString);
        paramPrintWriter.print("    ");
        paramPrintWriter.println(paramArrayOfString.intent.toShortString(false, true, true, false));
        if ((paramArrayOfString.targetComp != null) && (paramArrayOfString.targetComp != paramArrayOfString.intent.getComponent()))
        {
          paramPrintWriter.print("    targetComp: ");
          paramPrintWriter.println(paramArrayOfString.targetComp.toShortString());
        }
        paramArrayOfString = paramArrayOfString.intent.getExtras();
        paramInt = m;
        j = k;
        paramBoolean2 = bool2;
        if (paramArrayOfString == null) {
          break label282;
        }
        paramPrintWriter.print("    extras: ");
        paramPrintWriter.println(paramArrayOfString.toString());
        paramInt = m;
        j = k;
        paramBoolean2 = bool2;
        break label282;
        label1083:
        int i2 = paramInt;
        n = i1;
        for (;;)
        {
          k = paramInt;
          i = j;
          m = n;
          bool1 = paramBoolean2;
          if (i2 <= 0) {
            break;
          }
          k = paramInt;
          i = j;
          m = n;
          bool1 = paramBoolean2;
          if (n == i1) {
            break;
          }
          i = ringAdvance(n, -1, MAX_BROADCAST_SUMMARY_HISTORY);
          n = i;
          if (this.mBroadcastHistory[i] != null)
          {
            i2 -= 1;
            n = i;
          }
        }
        label1174:
        paramInt = i;
        paramBoolean2 = bool1;
        if (i == 0)
        {
          if (bool1) {
            paramPrintWriter.println();
          }
          paramBoolean2 = true;
          paramPrintWriter.println("  Historical broadcasts summary [" + this.mQueueName + "]:");
          paramInt = 1;
        }
        if ((paramBoolean1) || (k < 50)) {
          break label1261;
        }
        paramPrintWriter.println("  ...");
        bool2 = paramBoolean2;
      }
      label1261:
      m = k + 1;
      paramPrintWriter.print("  #");
      paramPrintWriter.print(m);
      paramPrintWriter.print(": ");
      paramPrintWriter.println(paramArrayOfString.toShortString(false, true, true, false));
      paramPrintWriter.print("    ");
      TimeUtils.formatDuration(this.mSummaryHistoryDispatchTime[j] - this.mSummaryHistoryEnqueueTime[j], paramPrintWriter);
      paramPrintWriter.print(" dispatch ");
      TimeUtils.formatDuration(this.mSummaryHistoryFinishTime[j] - this.mSummaryHistoryDispatchTime[j], paramPrintWriter);
      paramPrintWriter.println(" finish");
      paramPrintWriter.print("    enq=");
      paramPrintWriter.print(paramFileDescriptor.format(new Date(this.mSummaryHistoryEnqueueTime[j])));
      paramPrintWriter.print(" disp=");
      paramPrintWriter.print(paramFileDescriptor.format(new Date(this.mSummaryHistoryDispatchTime[j])));
      paramPrintWriter.print(" fin=");
      paramPrintWriter.println(paramFileDescriptor.format(new Date(this.mSummaryHistoryFinishTime[j])));
      paramArrayOfString = paramArrayOfString.getExtras();
      k = m;
      i = paramInt;
      bool2 = paramBoolean2;
      if (paramArrayOfString != null)
      {
        paramPrintWriter.print("    extras: ");
        paramPrintWriter.println(paramArrayOfString.toString());
        k = m;
        i = paramInt;
        bool2 = paramBoolean2;
      }
    }
    label1496:
    return bool2;
  }
  
  public void enqueueOrderedBroadcastLocked(BroadcastRecord paramBroadcastRecord)
  {
    if ("AddRestartProcessWhiteList".equals(paramBroadcastRecord.intent.getAction()))
    {
      this.mService.addRestartWhitelist(paramBroadcastRecord.intent.getStringExtra("whitelist"));
      return;
    }
    if ("RemoveRestartProcessWhiteList".equals(paramBroadcastRecord.intent.getAction()))
    {
      this.mService.removeRestartWhitelist(paramBroadcastRecord.intent.getStringExtra("whitelist"));
      return;
    }
    this.mOrderedBroadcasts.add(paramBroadcastRecord);
    paramBroadcastRecord.enqueueClockTime = System.currentTimeMillis();
  }
  
  public void enqueueParallelBroadcastLocked(BroadcastRecord paramBroadcastRecord)
  {
    this.mParallelBroadcasts.add(paramBroadcastRecord);
    paramBroadcastRecord.enqueueClockTime = System.currentTimeMillis();
  }
  
  public boolean finishReceiverLocked(BroadcastRecord paramBroadcastRecord, int paramInt, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramBroadcastRecord.state;
    ActivityInfo localActivityInfo = paramBroadcastRecord.curReceiver;
    paramBroadcastRecord.state = 0;
    if (i == 0) {
      Slog.w("BroadcastQueue", "finishReceiver [" + this.mQueueName + "] called but state is IDLE");
    }
    paramBroadcastRecord.receiver = null;
    paramBroadcastRecord.intent.setComponent(null);
    if ((paramBroadcastRecord.curApp != null) && (paramBroadcastRecord.curApp.curReceiver == paramBroadcastRecord)) {
      paramBroadcastRecord.curApp.curReceiver = null;
    }
    if (paramBroadcastRecord.curFilter != null) {
      paramBroadcastRecord.curFilter.receiverList.curBroadcast = null;
    }
    paramBroadcastRecord.curFilter = null;
    paramBroadcastRecord.curReceiver = null;
    paramBroadcastRecord.curApp = null;
    this.mPendingBroadcast = null;
    paramBroadcastRecord.resultCode = paramInt;
    paramBroadcastRecord.resultData = paramString;
    paramBroadcastRecord.resultExtras = paramBundle;
    if ((paramBoolean1) && ((paramBroadcastRecord.intent.getFlags() & 0x8000000) == 0))
    {
      paramBroadcastRecord.resultAbort = paramBoolean1;
      if ((!paramBoolean2) || (paramBroadcastRecord.curComponent == null) || (!paramBroadcastRecord.queue.mDelayBehindServices) || (paramBroadcastRecord.queue.mOrderedBroadcasts.size() <= 0) || (paramBroadcastRecord.queue.mOrderedBroadcasts.get(0) != paramBroadcastRecord)) {
        break label388;
      }
    }
    for (;;)
    {
      try
      {
        if (paramBroadcastRecord.nextReceiver < paramBroadcastRecord.receivers.size())
        {
          paramString = paramBroadcastRecord.receivers.get(paramBroadcastRecord.nextReceiver);
          if ((paramString instanceof ActivityInfo))
          {
            paramString = (ActivityInfo)paramString;
            break label416;
            if (!this.mService.mServices.hasBackgroundServices(paramBroadcastRecord.userId)) {
              continue;
            }
            Slog.i("BroadcastQueue", "Delay finish: " + paramBroadcastRecord.curComponent.flattenToShortString());
            paramBroadcastRecord.state = 4;
            return false;
            paramBroadcastRecord.resultAbort = false;
            break;
          }
          paramString = null;
          break label416;
        }
        paramString = null;
        break label416;
        if (localActivityInfo.applicationInfo.uid != paramString.applicationInfo.uid) {
          continue;
        }
        paramBoolean1 = localActivityInfo.processName.equals(paramString.processName);
        if (!paramBoolean1) {
          continue;
        }
        label388:
        paramBroadcastRecord.curComponent = null;
        if (i != 1)
        {
          if (i != 3) {
            break label414;
          }
          return true;
        }
      }
      finally {}
      return true;
      label414:
      return false;
      label416:
      if (localActivityInfo != null) {
        if (paramString != null) {}
      }
    }
  }
  
  public ReceiverRecord getMatchingNotOrderedReceiver(IBinder arg1, int paramInt)
  {
    synchronized (mOptLock)
    {
      ReceiverRecord localReceiverRecord = (ReceiverRecord)this.mReceiverRecords.get(Integer.valueOf(paramInt));
      if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
        Slog.i("BroadcastQueue", "getMatchingNotOrderedReceiver # receiverRecord=" + localReceiverRecord);
      }
      if (localReceiverRecord != null)
      {
        ProcessRecord localProcessRecord = localReceiverRecord.getApp();
        if ((localProcessRecord != null) && (localProcessRecord.ReceiverRecords != null))
        {
          if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
            Slog.i("BroadcastQueue", "getMatchingNotOrderedReceiver # [" + this.mQueueName + "] # appSize = " + localProcessRecord.ReceiverRecords.size() + " mapSize = " + this.mReceiverRecords.size());
          }
          localProcessRecord.ReceiverRecords.remove(localReceiverRecord);
        }
        this.mReceiverRecords.remove(Integer.valueOf(paramInt));
        return localReceiverRecord;
      }
      return null;
    }
  }
  
  public BroadcastRecord getMatchingOrderedReceiver(IBinder paramIBinder)
  {
    if (this.mOrderedBroadcasts.size() > 0)
    {
      BroadcastRecord localBroadcastRecord = (BroadcastRecord)this.mOrderedBroadcasts.get(0);
      if ((localBroadcastRecord != null) && (localBroadcastRecord.receiver == paramIBinder)) {
        return localBroadcastRecord;
      }
    }
    return null;
  }
  
  public ArrayList<String> getOptimizationIncludingList()
  {
    return this.mBroadcastOptimizeIncludeList;
  }
  
  public void initialOnlineConfig()
  {
    this.mBroadcastOptimizationConfigObserver = new ConfigObserver(this.mContext, this.mHandler, new BroadcastOptimizationConfigUpdater(), "BroadcastOptimization");
    this.mBroadcastOptimizationConfigObserver.register();
    this.mHandler.sendMessage(this.mHandler.obtainMessage(10000));
  }
  
  public boolean isPendingBroadcastProcessLocked(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mPendingBroadcast != null)
    {
      bool1 = bool2;
      if (this.mPendingBroadcast.curApp.pid == paramInt) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  final void logBroadcastReceiverDiscardLocked(BroadcastRecord paramBroadcastRecord)
  {
    int i = paramBroadcastRecord.nextReceiver - 1;
    if ((i >= 0) && (i < paramBroadcastRecord.receivers.size()))
    {
      Object localObject = paramBroadcastRecord.receivers.get(i);
      if ((localObject instanceof BroadcastFilter))
      {
        localObject = (BroadcastFilter)localObject;
        EventLog.writeEvent(30024, new Object[] { Integer.valueOf(((BroadcastFilter)localObject).owningUserId), Integer.valueOf(System.identityHashCode(paramBroadcastRecord)), paramBroadcastRecord.intent.getAction(), Integer.valueOf(i), Integer.valueOf(System.identityHashCode(localObject)) });
        return;
      }
      localObject = (ResolveInfo)localObject;
      EventLog.writeEvent(30025, new Object[] { Integer.valueOf(UserHandle.getUserId(((ResolveInfo)localObject).activityInfo.applicationInfo.uid)), Integer.valueOf(System.identityHashCode(paramBroadcastRecord)), paramBroadcastRecord.intent.getAction(), Integer.valueOf(i), ((ResolveInfo)localObject).toString() });
      return;
    }
    if (i < 0) {
      Slog.w("BroadcastQueue", "Discarding broadcast before first receiver is invoked: " + paramBroadcastRecord);
    }
    EventLog.writeEvent(30025, new Object[] { Integer.valueOf(-1), Integer.valueOf(System.identityHashCode(paramBroadcastRecord)), paramBroadcastRecord.intent.getAction(), Integer.valueOf(paramBroadcastRecord.nextReceiver), "NONE" });
  }
  
  void performReceiveLocked(ProcessRecord paramProcessRecord, IIntentReceiver arg2, Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
    throws RemoteException
  {
    if (paramProcessRecord != null)
    {
      if (paramProcessRecord.thread != null) {
        try
        {
          paramProcessRecord.thread.scheduleRegisteredReceiver(???, paramIntent, paramInt1, paramString, paramBundle, paramBoolean1, paramBoolean2, paramInt2, paramProcessRecord.repProcState);
          return;
        }
        catch (RemoteException paramIntent)
        {
          synchronized (this.mService)
          {
            ActivityManagerService.boostPriorityForLockedSection();
            Slog.w("BroadcastQueue", "Can't deliver broadcast to " + paramProcessRecord.processName + " (pid " + paramProcessRecord.pid + "). Crashing it.");
            paramProcessRecord.scheduleCrash("can't deliver broadcast");
            ActivityManagerService.resetPriorityAfterLockedSection();
            throw paramIntent;
          }
        }
      }
      throw new RemoteException("app.thread must not be null");
    }
    ???.performReceive(paramIntent, paramInt1, paramString, paramBundle, paramBoolean1, paramBoolean2, paramInt2);
  }
  
  /* Error */
  final void processNextBroadcast(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   4: astore 13
    //   6: aload 13
    //   8: monitorenter
    //   9: invokestatic 1160	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   12: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   15: ifeq +71 -> 86
    //   18: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   21: new 104	java/lang/StringBuilder
    //   24: dup
    //   25: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   28: ldc_w 1192
    //   31: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: aload_0
    //   35: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   38: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: ldc_w 1194
    //   44: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: aload_0
    //   48: getfield 140	com/android/server/am/BroadcastQueue:mParallelBroadcasts	Ljava/util/ArrayList;
    //   51: invokevirtual 835	java/util/ArrayList:size	()I
    //   54: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   57: ldc_w 1196
    //   60: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload_0
    //   64: getfield 142	com/android/server/am/BroadcastQueue:mOrderedBroadcasts	Ljava/util/ArrayList;
    //   67: invokevirtual 835	java/util/ArrayList:size	()I
    //   70: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   73: ldc_w 1198
    //   76: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   82: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   85: pop
    //   86: aload_0
    //   87: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   90: invokevirtual 1201	com/android/server/am/ActivityManagerService:updateCpuStats	()V
    //   93: iload_1
    //   94: ifeq +8 -> 102
    //   97: aload_0
    //   98: iconst_0
    //   99: putfield 162	com/android/server/am/BroadcastQueue:mBroadcastsScheduled	Z
    //   102: aload_0
    //   103: getfield 140	com/android/server/am/BroadcastQueue:mParallelBroadcasts	Ljava/util/ArrayList;
    //   106: invokevirtual 835	java/util/ArrayList:size	()I
    //   109: ifle +270 -> 379
    //   112: aload_0
    //   113: getfield 140	com/android/server/am/BroadcastQueue:mParallelBroadcasts	Ljava/util/ArrayList;
    //   116: iconst_0
    //   117: invokevirtual 1203	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   120: checkcast 144	com/android/server/am/BroadcastRecord
    //   123: astore 11
    //   125: aload 11
    //   127: monitorenter
    //   128: aload 11
    //   130: invokestatic 219	android/os/SystemClock:uptimeMillis	()J
    //   133: putfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   136: aload 11
    //   138: invokestatic 241	java/lang/System:currentTimeMillis	()J
    //   141: putfield 236	com/android/server/am/BroadcastRecord:dispatchClockTime	J
    //   144: aload 11
    //   146: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   149: invokeinterface 1062 1 0
    //   154: istore_3
    //   155: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   158: ifeq +4569 -> 4727
    //   161: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   164: new 104	java/lang/StringBuilder
    //   167: dup
    //   168: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   171: ldc_w 1208
    //   174: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: aload_0
    //   178: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   181: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: ldc_w 371
    //   187: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload 11
    //   192: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   195: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   198: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   201: pop
    //   202: goto +4525 -> 4727
    //   205: iload_2
    //   206: iload_3
    //   207: if_icmpge +94 -> 301
    //   210: aload 11
    //   212: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   215: iload_2
    //   216: invokeinterface 897 2 0
    //   221: astore 12
    //   223: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   226: ifeq +55 -> 281
    //   229: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   232: new 104	java/lang/StringBuilder
    //   235: dup
    //   236: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   239: ldc_w 1210
    //   242: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: aload_0
    //   246: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   249: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: ldc_w 1212
    //   255: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: aload 12
    //   260: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   263: ldc_w 568
    //   266: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload 11
    //   271: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   274: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   277: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   280: pop
    //   281: aload_0
    //   282: aload 11
    //   284: aload 12
    //   286: checkcast 247	com/android/server/am/BroadcastFilter
    //   289: iconst_0
    //   290: iload_2
    //   291: invokespecial 1214	com/android/server/am/BroadcastQueue:deliverToRegisteredReceiverLocked	(Lcom/android/server/am/BroadcastRecord;Lcom/android/server/am/BroadcastFilter;ZI)V
    //   294: iload_2
    //   295: iconst_1
    //   296: iadd
    //   297: istore_2
    //   298: goto -93 -> 205
    //   301: aload_0
    //   302: aload 11
    //   304: invokespecial 1216	com/android/server/am/BroadcastQueue:addBroadcastToHistoryLocked	(Lcom/android/server/am/BroadcastRecord;)V
    //   307: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   310: ifeq +44 -> 354
    //   313: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   316: new 104	java/lang/StringBuilder
    //   319: dup
    //   320: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   323: ldc_w 1218
    //   326: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: aload_0
    //   330: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   333: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: ldc_w 371
    //   339: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: aload 11
    //   344: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   347: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   350: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: aload 11
    //   356: monitorexit
    //   357: goto -255 -> 102
    //   360: astore 11
    //   362: aload 13
    //   364: monitorexit
    //   365: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   368: aload 11
    //   370: athrow
    //   371: astore 12
    //   373: aload 11
    //   375: monitorexit
    //   376: aload 12
    //   378: athrow
    //   379: aload_0
    //   380: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   383: ifnull +4349 -> 4732
    //   386: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   389: ifeq +49 -> 438
    //   392: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   395: new 104	java/lang/StringBuilder
    //   398: dup
    //   399: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   402: ldc_w 1192
    //   405: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   408: aload_0
    //   409: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   412: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   415: ldc_w 1220
    //   418: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   421: aload_0
    //   422: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   425: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   428: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   431: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   434: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   437: pop
    //   438: aload_0
    //   439: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   442: getfield 906	com/android/server/am/ActivityManagerService:mPidsSelfLocked	Landroid/util/SparseArray;
    //   445: astore 11
    //   447: aload 11
    //   449: monitorenter
    //   450: aload_0
    //   451: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   454: getfield 906	com/android/server/am/ActivityManagerService:mPidsSelfLocked	Landroid/util/SparseArray;
    //   457: aload_0
    //   458: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   461: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   464: getfield 1127	com/android/server/am/ProcessRecord:pid	I
    //   467: invokevirtual 909	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   470: checkcast 364	com/android/server/am/ProcessRecord
    //   473: astore 12
    //   475: aload 12
    //   477: ifnull +23 -> 500
    //   480: aload 12
    //   482: getfield 367	com/android/server/am/ProcessRecord:crashing	Z
    //   485: istore_1
    //   486: aload 11
    //   488: monitorexit
    //   489: iload_1
    //   490: ifne +23 -> 513
    //   493: aload 13
    //   495: monitorexit
    //   496: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   499: return
    //   500: iconst_1
    //   501: istore_1
    //   502: goto -16 -> 486
    //   505: astore 12
    //   507: aload 11
    //   509: monitorexit
    //   510: aload 12
    //   512: athrow
    //   513: ldc 35
    //   515: new 104	java/lang/StringBuilder
    //   518: dup
    //   519: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   522: ldc_w 1222
    //   525: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload_0
    //   529: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   532: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: ldc_w 1224
    //   538: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   541: aload_0
    //   542: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   545: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   548: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   551: ldc_w 1226
    //   554: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   557: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   560: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   563: pop
    //   564: aload_0
    //   565: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   568: iconst_0
    //   569: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   572: aload_0
    //   573: getfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   576: aload_0
    //   577: getfield 1228	com/android/server/am/BroadcastQueue:mPendingBroadcastRecvIndex	I
    //   580: putfield 888	com/android/server/am/BroadcastRecord:nextReceiver	I
    //   583: aload_0
    //   584: aconst_null
    //   585: putfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   588: goto +4144 -> 4732
    //   591: aload_0
    //   592: getfield 142	com/android/server/am/BroadcastQueue:mOrderedBroadcasts	Ljava/util/ArrayList;
    //   595: invokevirtual 835	java/util/ArrayList:size	()I
    //   598: ifne +28 -> 626
    //   601: aload_0
    //   602: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   605: invokevirtual 1231	com/android/server/am/ActivityManagerService:scheduleAppGcsLocked	()V
    //   608: iload_2
    //   609: ifeq +10 -> 619
    //   612: aload_0
    //   613: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   616: invokevirtual 553	com/android/server/am/ActivityManagerService:updateOomAdjLocked	()V
    //   619: aload 13
    //   621: monitorexit
    //   622: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   625: return
    //   626: aload_0
    //   627: getfield 142	com/android/server/am/BroadcastQueue:mOrderedBroadcasts	Ljava/util/ArrayList;
    //   630: iconst_0
    //   631: invokevirtual 839	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   634: checkcast 144	com/android/server/am/BroadcastRecord
    //   637: astore 12
    //   639: iconst_0
    //   640: istore 5
    //   642: aload 12
    //   644: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   647: ifnull +293 -> 940
    //   650: aload 12
    //   652: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   655: invokeinterface 1062 1 0
    //   660: istore_3
    //   661: iload 5
    //   663: istore 4
    //   665: aload_0
    //   666: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   669: getfield 855	com/android/server/am/ActivityManagerService:mProcessesReady	Z
    //   672: ifeq +197 -> 869
    //   675: iload 5
    //   677: istore 4
    //   679: aload 12
    //   681: getfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   684: lconst_0
    //   685: lcmp
    //   686: ifle +183 -> 869
    //   689: invokestatic 219	android/os/SystemClock:uptimeMillis	()J
    //   692: lstore 9
    //   694: iload 5
    //   696: istore 4
    //   698: iload_3
    //   699: ifle +170 -> 869
    //   702: iload 5
    //   704: istore 4
    //   706: lload 9
    //   708: aload 12
    //   710: getfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   713: aload_0
    //   714: getfield 193	com/android/server/am/BroadcastQueue:mTimeoutPeriod	J
    //   717: ldc2_w 1232
    //   720: lmul
    //   721: iload_3
    //   722: i2l
    //   723: lmul
    //   724: ladd
    //   725: lcmp
    //   726: ifle +143 -> 869
    //   729: ldc 35
    //   731: new 104	java/lang/StringBuilder
    //   734: dup
    //   735: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   738: ldc_w 1235
    //   741: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   744: aload_0
    //   745: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   748: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   751: ldc_w 1237
    //   754: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   757: ldc_w 1239
    //   760: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   763: lload 9
    //   765: invokevirtual 865	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   768: ldc_w 1241
    //   771: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   774: aload 12
    //   776: getfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   779: invokevirtual 865	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   782: ldc_w 1243
    //   785: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   788: aload 12
    //   790: getfield 858	com/android/server/am/BroadcastRecord:receiverTime	J
    //   793: invokevirtual 865	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   796: ldc_w 1245
    //   799: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   802: aload 12
    //   804: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   807: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   810: ldc_w 1247
    //   813: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   816: iload_3
    //   817: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   820: ldc_w 1249
    //   823: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   826: aload 12
    //   828: getfield 888	com/android/server/am/BroadcastRecord:nextReceiver	I
    //   831: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   834: ldc_w 1251
    //   837: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   840: aload 12
    //   842: getfield 428	com/android/server/am/BroadcastRecord:state	I
    //   845: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   848: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   851: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   854: pop
    //   855: aload_0
    //   856: iconst_0
    //   857: invokevirtual 1253	com/android/server/am/BroadcastQueue:broadcastTimeoutLocked	(Z)V
    //   860: iconst_1
    //   861: istore 4
    //   863: aload 12
    //   865: iconst_0
    //   866: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   869: aload 12
    //   871: getfield 428	com/android/server/am/BroadcastRecord:state	I
    //   874: ifeq +71 -> 945
    //   877: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   880: ifeq +53 -> 933
    //   883: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   886: new 104	java/lang/StringBuilder
    //   889: dup
    //   890: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   893: ldc_w 1255
    //   896: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   899: aload_0
    //   900: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   903: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   906: ldc_w 1257
    //   909: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   912: aload 12
    //   914: getfield 428	com/android/server/am/BroadcastRecord:state	I
    //   917: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   920: ldc_w 274
    //   923: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   926: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   929: invokestatic 671	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   932: pop
    //   933: aload 13
    //   935: monitorexit
    //   936: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   939: return
    //   940: iconst_0
    //   941: istore_3
    //   942: goto -281 -> 661
    //   945: aload 12
    //   947: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   950: ifnull +12 -> 962
    //   953: aload 12
    //   955: getfield 888	com/android/server/am/BroadcastRecord:nextReceiver	I
    //   958: iload_3
    //   959: if_icmplt +639 -> 1598
    //   962: aload 12
    //   964: getfield 1260	com/android/server/am/BroadcastRecord:resultTo	Landroid/content/IIntentReceiver;
    //   967: astore 11
    //   969: aload 11
    //   971: ifnull +124 -> 1095
    //   974: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   977: ifeq +64 -> 1041
    //   980: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   983: new 104	java/lang/StringBuilder
    //   986: dup
    //   987: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   990: ldc_w 1262
    //   993: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   996: aload_0
    //   997: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1000: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1003: ldc_w 371
    //   1006: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1009: aload 12
    //   1011: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1014: invokevirtual 663	android/content/Intent:getAction	()Ljava/lang/String;
    //   1017: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1020: ldc_w 1264
    //   1023: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1026: aload 12
    //   1028: getfield 710	com/android/server/am/BroadcastRecord:callerApp	Lcom/android/server/am/ProcessRecord;
    //   1031: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1034: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1037: invokestatic 448	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1040: pop
    //   1041: aload_0
    //   1042: aload 12
    //   1044: getfield 710	com/android/server/am/BroadcastRecord:callerApp	Lcom/android/server/am/ProcessRecord;
    //   1047: aload 12
    //   1049: getfield 1260	com/android/server/am/BroadcastRecord:resultTo	Landroid/content/IIntentReceiver;
    //   1052: new 150	android/content/Intent
    //   1055: dup
    //   1056: aload 12
    //   1058: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1061: invokespecial 457	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   1064: aload 12
    //   1066: getfield 460	com/android/server/am/BroadcastRecord:resultCode	I
    //   1069: aload 12
    //   1071: getfield 463	com/android/server/am/BroadcastRecord:resultData	Ljava/lang/String;
    //   1074: aload 12
    //   1076: getfield 467	com/android/server/am/BroadcastRecord:resultExtras	Landroid/os/Bundle;
    //   1079: iconst_0
    //   1080: iconst_0
    //   1081: aload 12
    //   1083: getfield 476	com/android/server/am/BroadcastRecord:userId	I
    //   1086: invokevirtual 480	com/android/server/am/BroadcastQueue:performReceiveLocked	(Lcom/android/server/am/ProcessRecord;Landroid/content/IIntentReceiver;Landroid/content/Intent;ILjava/lang/String;Landroid/os/Bundle;ZZI)V
    //   1089: aload 12
    //   1091: aconst_null
    //   1092: putfield 1260	com/android/server/am/BroadcastRecord:resultTo	Landroid/content/IIntentReceiver;
    //   1095: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   1098: ifeq +13 -> 1111
    //   1101: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1104: ldc_w 1266
    //   1107: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1110: pop
    //   1111: aload_0
    //   1112: invokevirtual 1268	com/android/server/am/BroadcastQueue:cancelBroadcastTimeoutLocked	()V
    //   1115: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   1118: ifeq +31 -> 1149
    //   1121: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1124: new 104	java/lang/StringBuilder
    //   1127: dup
    //   1128: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1131: ldc_w 1270
    //   1134: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1137: aload 12
    //   1139: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1142: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1145: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1148: pop
    //   1149: aload_0
    //   1150: aload 12
    //   1152: invokespecial 1216	com/android/server/am/BroadcastQueue:addBroadcastToHistoryLocked	(Lcom/android/server/am/BroadcastRecord;)V
    //   1155: aload 12
    //   1157: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1160: invokevirtual 572	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   1163: ifnonnull +70 -> 1233
    //   1166: aload 12
    //   1168: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1171: invokevirtual 1273	android/content/Intent:getPackage	()Ljava/lang/String;
    //   1174: ifnonnull +59 -> 1233
    //   1177: aload 12
    //   1179: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1182: invokevirtual 1056	android/content/Intent:getFlags	()I
    //   1185: ldc_w 1274
    //   1188: iand
    //   1189: ifne +44 -> 1233
    //   1192: aload_0
    //   1193: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   1196: aload 12
    //   1198: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1201: invokevirtual 663	android/content/Intent:getAction	()Ljava/lang/String;
    //   1204: aload 12
    //   1206: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   1209: aload 12
    //   1211: getfield 1277	com/android/server/am/BroadcastRecord:manifestCount	I
    //   1214: aload 12
    //   1216: getfield 1280	com/android/server/am/BroadcastRecord:manifestSkipCount	I
    //   1219: aload 12
    //   1221: getfield 222	com/android/server/am/BroadcastRecord:finishTime	J
    //   1224: aload 12
    //   1226: getfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   1229: lsub
    //   1230: invokevirtual 1284	com/android/server/am/ActivityManagerService:addBroadcastStatLocked	(Ljava/lang/String;Ljava/lang/String;IIJ)V
    //   1233: aload_0
    //   1234: getfield 142	com/android/server/am/BroadcastQueue:mOrderedBroadcasts	Ljava/util/ArrayList;
    //   1237: iconst_0
    //   1238: invokevirtual 1203	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   1241: pop
    //   1242: aconst_null
    //   1243: astore 11
    //   1245: iconst_1
    //   1246: istore_2
    //   1247: aload 11
    //   1249: ifnull -658 -> 591
    //   1252: aload 11
    //   1254: monitorenter
    //   1255: aload 11
    //   1257: getfield 888	com/android/server/am/BroadcastRecord:nextReceiver	I
    //   1260: istore 5
    //   1262: aload 11
    //   1264: iload 5
    //   1266: iconst_1
    //   1267: iadd
    //   1268: putfield 888	com/android/server/am/BroadcastRecord:nextReceiver	I
    //   1271: aload 11
    //   1273: monitorexit
    //   1274: aload 11
    //   1276: invokestatic 219	android/os/SystemClock:uptimeMillis	()J
    //   1279: putfield 858	com/android/server/am/BroadcastRecord:receiverTime	J
    //   1282: iload 5
    //   1284: ifne +68 -> 1352
    //   1287: aload 11
    //   1289: aload 11
    //   1291: getfield 858	com/android/server/am/BroadcastRecord:receiverTime	J
    //   1294: putfield 1206	com/android/server/am/BroadcastRecord:dispatchTime	J
    //   1297: aload 11
    //   1299: invokestatic 241	java/lang/System:currentTimeMillis	()J
    //   1302: putfield 236	com/android/server/am/BroadcastRecord:dispatchClockTime	J
    //   1305: getstatic 441	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST_LIGHT	Z
    //   1308: ifeq +44 -> 1352
    //   1311: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1314: new 104	java/lang/StringBuilder
    //   1317: dup
    //   1318: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1321: ldc_w 1286
    //   1324: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1327: aload_0
    //   1328: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1331: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1334: ldc_w 371
    //   1337: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1340: aload 11
    //   1342: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1345: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1348: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1351: pop
    //   1352: aload_0
    //   1353: getfield 848	com/android/server/am/BroadcastQueue:mPendingBroadcastTimeoutMessage	Z
    //   1356: ifne +79 -> 1435
    //   1359: aload 11
    //   1361: getfield 858	com/android/server/am/BroadcastRecord:receiverTime	J
    //   1364: aload_0
    //   1365: getfield 193	com/android/server/am/BroadcastQueue:mTimeoutPeriod	J
    //   1368: ladd
    //   1369: lstore 9
    //   1371: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   1374: ifeq +55 -> 1429
    //   1377: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1380: new 104	java/lang/StringBuilder
    //   1383: dup
    //   1384: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1387: ldc_w 1288
    //   1390: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1393: aload_0
    //   1394: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1397: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1400: ldc_w 1290
    //   1403: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1406: aload 11
    //   1408: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1411: ldc_w 1292
    //   1414: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1417: lload 9
    //   1419: invokevirtual 865	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1422: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1425: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1428: pop
    //   1429: aload_0
    //   1430: lload 9
    //   1432: invokevirtual 852	com/android/server/am/BroadcastQueue:setBroadcastTimeoutLocked	(J)V
    //   1435: aload 11
    //   1437: getfield 1296	com/android/server/am/BroadcastRecord:options	Landroid/app/BroadcastOptions;
    //   1440: astore 16
    //   1442: aload 11
    //   1444: getfield 894	com/android/server/am/BroadcastRecord:receivers	Ljava/util/List;
    //   1447: iload 5
    //   1449: invokeinterface 897 2 0
    //   1454: astore 12
    //   1456: aload 12
    //   1458: instanceof 247
    //   1461: ifeq +298 -> 1759
    //   1464: aload 12
    //   1466: checkcast 247	com/android/server/am/BroadcastFilter
    //   1469: astore 12
    //   1471: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   1474: ifeq +55 -> 1529
    //   1477: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1480: new 104	java/lang/StringBuilder
    //   1483: dup
    //   1484: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1487: ldc_w 1298
    //   1490: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1493: aload_0
    //   1494: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1497: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1500: ldc_w 1212
    //   1503: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1506: aload 12
    //   1508: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1511: ldc_w 568
    //   1514: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1517: aload 11
    //   1519: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1522: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1525: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1528: pop
    //   1529: aload_0
    //   1530: aload 11
    //   1532: aload 12
    //   1534: aload 11
    //   1536: getfield 470	com/android/server/am/BroadcastRecord:ordered	Z
    //   1539: iload 5
    //   1541: invokespecial 1214	com/android/server/am/BroadcastQueue:deliverToRegisteredReceiverLocked	(Lcom/android/server/am/BroadcastRecord;Lcom/android/server/am/BroadcastFilter;ZI)V
    //   1544: aload 11
    //   1546: getfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   1549: ifnull +133 -> 1682
    //   1552: aload 11
    //   1554: getfield 470	com/android/server/am/BroadcastRecord:ordered	Z
    //   1557: ifeq +125 -> 1682
    //   1560: aload 16
    //   1562: ifnull +29 -> 1591
    //   1565: aload 16
    //   1567: invokevirtual 1303	android/app/BroadcastOptions:getTemporaryAppWhitelistDuration	()J
    //   1570: lconst_0
    //   1571: lcmp
    //   1572: ifle +19 -> 1591
    //   1575: aload_0
    //   1576: aload 12
    //   1578: getfield 1306	com/android/server/am/BroadcastFilter:owningUid	I
    //   1581: aload 16
    //   1583: invokevirtual 1303	android/app/BroadcastOptions:getTemporaryAppWhitelistDuration	()J
    //   1586: aload 11
    //   1588: invokevirtual 1310	com/android/server/am/BroadcastQueue:scheduleTempWhitelistLocked	(IJLcom/android/server/am/BroadcastRecord;)V
    //   1591: aload 13
    //   1593: monitorexit
    //   1594: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1597: return
    //   1598: aload 12
    //   1600: getfield 826	com/android/server/am/BroadcastRecord:resultAbort	Z
    //   1603: ifne -641 -> 962
    //   1606: aload 12
    //   1608: astore 11
    //   1610: iload 4
    //   1612: ifeq -365 -> 1247
    //   1615: goto -653 -> 962
    //   1618: astore 11
    //   1620: aload 12
    //   1622: aconst_null
    //   1623: putfield 1260	com/android/server/am/BroadcastRecord:resultTo	Landroid/content/IIntentReceiver;
    //   1626: ldc 35
    //   1628: new 104	java/lang/StringBuilder
    //   1631: dup
    //   1632: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1635: ldc_w 1312
    //   1638: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1641: aload_0
    //   1642: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1645: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1648: ldc_w 1314
    //   1651: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1654: aload 12
    //   1656: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1659: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1662: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1665: aload 11
    //   1667: invokestatic 485	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1670: pop
    //   1671: goto -576 -> 1095
    //   1674: astore 12
    //   1676: aload 11
    //   1678: monitorexit
    //   1679: aload 12
    //   1681: athrow
    //   1682: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   1685: ifeq +61 -> 1746
    //   1688: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   1691: new 104	java/lang/StringBuilder
    //   1694: dup
    //   1695: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1698: ldc_w 1316
    //   1701: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1704: aload_0
    //   1705: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   1708: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1711: ldc_w 1318
    //   1714: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1717: aload 11
    //   1719: getfield 470	com/android/server/am/BroadcastRecord:ordered	Z
    //   1722: invokevirtual 1321	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   1725: ldc_w 1323
    //   1728: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1731: aload 11
    //   1733: getfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   1736: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1739: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1742: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1745: pop
    //   1746: aload 11
    //   1748: iconst_0
    //   1749: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   1752: aload_0
    //   1753: invokevirtual 833	com/android/server/am/BroadcastQueue:scheduleBroadcastsLocked	()V
    //   1756: goto -165 -> 1591
    //   1759: aload 12
    //   1761: checkcast 1139	android/content/pm/ResolveInfo
    //   1764: astore 12
    //   1766: new 574	android/content/ComponentName
    //   1769: dup
    //   1770: aload 12
    //   1772: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1775: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1778: getfield 1324	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   1781: aload 12
    //   1783: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1786: getfield 1326	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   1789: invokespecial 1329	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   1792: astore 17
    //   1794: iconst_0
    //   1795: istore_2
    //   1796: iload_2
    //   1797: istore_3
    //   1798: aload 16
    //   1800: ifnull +46 -> 1846
    //   1803: aload 12
    //   1805: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1808: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1811: getfield 1332	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   1814: aload 16
    //   1816: invokevirtual 1335	android/app/BroadcastOptions:getMinManifestReceiverApiLevel	()I
    //   1819: if_icmplt +2918 -> 4737
    //   1822: iload_2
    //   1823: istore_3
    //   1824: aload 12
    //   1826: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1829: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1832: getfield 1332	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   1835: aload 16
    //   1837: invokevirtual 1338	android/app/BroadcastOptions:getMaxManifestReceiverApiLevel	()I
    //   1840: if_icmple +6 -> 1846
    //   1843: goto +2894 -> 4737
    //   1846: aload_0
    //   1847: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   1850: aload 12
    //   1852: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1855: getfield 1341	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   1858: aload 11
    //   1860: getfield 253	com/android/server/am/BroadcastRecord:callingPid	I
    //   1863: aload 11
    //   1865: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   1868: aload 12
    //   1870: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1873: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1876: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   1879: aload 12
    //   1881: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1884: getfield 1344	android/content/pm/ActivityInfo:exported	Z
    //   1887: invokevirtual 257	com/android/server/am/ActivityManagerService:checkComponentPermission	(Ljava/lang/String;IIIZ)I
    //   1890: istore_2
    //   1891: iload_3
    //   1892: ifne +1325 -> 3217
    //   1895: iload_2
    //   1896: ifeq +1321 -> 3217
    //   1899: aload 12
    //   1901: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1904: getfield 1344	android/content/pm/ActivityInfo:exported	Z
    //   1907: ifne +1195 -> 3102
    //   1910: ldc 35
    //   1912: new 104	java/lang/StringBuilder
    //   1915: dup
    //   1916: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   1919: ldc_w 259
    //   1922: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1925: aload 11
    //   1927: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   1930: invokevirtual 260	android/content/Intent:toString	()Ljava/lang/String;
    //   1933: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1936: ldc_w 262
    //   1939: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1942: aload 11
    //   1944: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   1947: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1950: ldc_w 267
    //   1953: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1956: aload 11
    //   1958: getfield 253	com/android/server/am/BroadcastRecord:callingPid	I
    //   1961: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1964: ldc_w 272
    //   1967: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1970: aload 11
    //   1972: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   1975: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1978: ldc_w 274
    //   1981: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1984: ldc_w 1346
    //   1987: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1990: aload 12
    //   1992: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1995: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   1998: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2001: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2004: ldc_w 1348
    //   2007: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2010: aload 17
    //   2012: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   2015: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2018: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2021: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   2024: pop
    //   2025: goto +2717 -> 4742
    //   2028: iload_2
    //   2029: istore_3
    //   2030: iload_2
    //   2031: ifne +200 -> 2231
    //   2034: iload_2
    //   2035: istore_3
    //   2036: aload 12
    //   2038: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2041: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2044: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2047: sipush 1000
    //   2050: if_icmpeq +181 -> 2231
    //   2053: iload_2
    //   2054: istore_3
    //   2055: aload 11
    //   2057: getfield 291	com/android/server/am/BroadcastRecord:requiredPermissions	[Ljava/lang/String;
    //   2060: ifnull +171 -> 2231
    //   2063: iload_2
    //   2064: istore_3
    //   2065: aload 11
    //   2067: getfield 291	com/android/server/am/BroadcastRecord:requiredPermissions	[Ljava/lang/String;
    //   2070: arraylength
    //   2071: ifle +160 -> 2231
    //   2074: iconst_0
    //   2075: istore 4
    //   2077: iload_2
    //   2078: istore_3
    //   2079: iload 4
    //   2081: aload 11
    //   2083: getfield 291	com/android/server/am/BroadcastRecord:requiredPermissions	[Ljava/lang/String;
    //   2086: arraylength
    //   2087: if_icmpge +144 -> 2231
    //   2090: aload 11
    //   2092: getfield 291	com/android/server/am/BroadcastRecord:requiredPermissions	[Ljava/lang/String;
    //   2095: iload 4
    //   2097: aaload
    //   2098: astore 14
    //   2100: invokestatic 1354	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   2103: aload 14
    //   2105: aload 12
    //   2107: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2110: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2113: getfield 1324	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   2116: aload 12
    //   2118: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2121: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2124: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2127: invokestatic 1148	android/os/UserHandle:getUserId	(I)I
    //   2130: invokeinterface 1360 4 0
    //   2135: istore_3
    //   2136: iload_3
    //   2137: ifeq +1267 -> 3404
    //   2140: ldc 35
    //   2142: new 104	java/lang/StringBuilder
    //   2145: dup
    //   2146: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2149: ldc_w 305
    //   2152: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2155: aload 11
    //   2157: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   2160: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2163: ldc_w 307
    //   2166: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2169: aload 17
    //   2171: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   2174: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2177: ldc_w 276
    //   2180: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2183: aload 14
    //   2185: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2188: ldc_w 313
    //   2191: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2194: aload 11
    //   2196: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   2199: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2202: ldc_w 315
    //   2205: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2208: aload 11
    //   2210: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   2213: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2216: ldc_w 274
    //   2219: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2222: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2225: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   2228: pop
    //   2229: iconst_1
    //   2230: istore_3
    //   2231: iload_3
    //   2232: istore 4
    //   2234: iload_3
    //   2235: ifne +153 -> 2388
    //   2238: iload_3
    //   2239: istore 4
    //   2241: aload 11
    //   2243: getfield 320	com/android/server/am/BroadcastRecord:appOp	I
    //   2246: iconst_m1
    //   2247: if_icmpeq +141 -> 2388
    //   2250: iload_3
    //   2251: istore 4
    //   2253: aload_0
    //   2254: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2257: getfield 324	com/android/server/am/ActivityManagerService:mAppOpsService	Lcom/android/server/AppOpsService;
    //   2260: aload 11
    //   2262: getfield 320	com/android/server/am/BroadcastRecord:appOp	I
    //   2265: aload 12
    //   2267: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2270: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2273: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2276: aload 12
    //   2278: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2281: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   2284: invokevirtual 333	com/android/server/AppOpsService:noteOperation	(IILjava/lang/String;)I
    //   2287: ifeq +101 -> 2388
    //   2290: ldc 35
    //   2292: new 104	java/lang/StringBuilder
    //   2295: dup
    //   2296: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2299: ldc_w 335
    //   2302: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2305: aload 11
    //   2307: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   2310: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2313: ldc_w 307
    //   2316: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2319: aload 17
    //   2321: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   2324: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2327: ldc_w 337
    //   2330: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2333: aload 11
    //   2335: getfield 320	com/android/server/am/BroadcastRecord:appOp	I
    //   2338: invokestatic 343	android/app/AppOpsManager:opToName	(I)Ljava/lang/String;
    //   2341: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2344: ldc_w 313
    //   2347: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2350: aload 11
    //   2352: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   2355: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2358: ldc_w 315
    //   2361: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2364: aload 11
    //   2366: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   2369: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2372: ldc_w 274
    //   2375: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2378: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2381: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   2384: pop
    //   2385: iconst_1
    //   2386: istore 4
    //   2388: iload 4
    //   2390: istore_2
    //   2391: iload 4
    //   2393: ifne +51 -> 2444
    //   2396: aload_0
    //   2397: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2400: getfield 353	com/android/server/am/ActivityManagerService:mIntentFirewall	Lcom/android/server/firewall/IntentFirewall;
    //   2403: aload 11
    //   2405: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   2408: aload 11
    //   2410: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   2413: aload 11
    //   2415: getfield 253	com/android/server/am/BroadcastRecord:callingPid	I
    //   2418: aload 11
    //   2420: getfield 356	com/android/server/am/BroadcastRecord:resolvedType	Ljava/lang/String;
    //   2423: aload 12
    //   2425: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2428: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2431: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2434: invokevirtual 362	com/android/server/firewall/IntentFirewall:checkBroadcast	(Landroid/content/Intent;IILjava/lang/String;I)Z
    //   2437: istore_1
    //   2438: iload_1
    //   2439: ifeq +2330 -> 4769
    //   2442: iconst_0
    //   2443: istore_2
    //   2444: iconst_0
    //   2445: istore_1
    //   2446: aload_0
    //   2447: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2450: aload 12
    //   2452: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2455: getfield 1080	android/content/pm/ActivityInfo:processName	Ljava/lang/String;
    //   2458: aload 12
    //   2460: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2463: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2466: aload 12
    //   2468: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2471: getfield 1326	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   2474: aload 12
    //   2476: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2479: getfield 1364	android/content/pm/ActivityInfo:flags	I
    //   2482: invokevirtual 1368	com/android/server/am/ActivityManagerService:isSingleton	(Ljava/lang/String;Landroid/content/pm/ApplicationInfo;Ljava/lang/String;I)Z
    //   2485: istore 7
    //   2487: iload 7
    //   2489: istore_1
    //   2490: iload_2
    //   2491: istore_3
    //   2492: iload_3
    //   2493: istore_2
    //   2494: aload 12
    //   2496: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2499: getfield 1364	android/content/pm/ActivityInfo:flags	I
    //   2502: ldc_w 1274
    //   2505: iand
    //   2506: ifeq +69 -> 2575
    //   2509: iload_3
    //   2510: istore_2
    //   2511: ldc_w 1370
    //   2514: aload 12
    //   2516: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2519: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2522: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2525: invokestatic 1374	android/app/ActivityManager:checkUidPermission	(Ljava/lang/String;I)I
    //   2528: ifeq +47 -> 2575
    //   2531: ldc 35
    //   2533: new 104	java/lang/StringBuilder
    //   2536: dup
    //   2537: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2540: ldc_w 1376
    //   2543: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2546: aload 17
    //   2548: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   2551: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2554: ldc_w 1378
    //   2557: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2560: ldc_w 1370
    //   2563: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2566: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2569: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   2572: pop
    //   2573: iconst_1
    //   2574: istore_2
    //   2575: iload_2
    //   2576: ifne +996 -> 3572
    //   2579: aload 11
    //   2581: aload 11
    //   2583: getfield 1277	com/android/server/am/BroadcastRecord:manifestCount	I
    //   2586: iconst_1
    //   2587: iadd
    //   2588: putfield 1277	com/android/server/am/BroadcastRecord:manifestCount	I
    //   2591: iload_2
    //   2592: istore_3
    //   2593: aload 11
    //   2595: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   2598: ifnull +78 -> 2676
    //   2601: iload_2
    //   2602: istore_3
    //   2603: aload 11
    //   2605: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   2608: getfield 367	com/android/server/am/ProcessRecord:crashing	Z
    //   2611: ifeq +65 -> 2676
    //   2614: ldc 35
    //   2616: new 104	java/lang/StringBuilder
    //   2619: dup
    //   2620: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2623: ldc_w 1380
    //   2626: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2629: aload_0
    //   2630: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   2633: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2636: ldc_w 371
    //   2639: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2642: aload 11
    //   2644: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2647: ldc_w 307
    //   2650: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2653: aload 11
    //   2655: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   2658: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2661: ldc_w 373
    //   2664: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2667: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2670: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   2673: pop
    //   2674: iconst_1
    //   2675: istore_3
    //   2676: iload_3
    //   2677: istore 4
    //   2679: iload_3
    //   2680: ifne +119 -> 2799
    //   2683: iconst_0
    //   2684: istore 7
    //   2686: invokestatic 1354	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   2689: aload 12
    //   2691: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2694: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   2697: aload 12
    //   2699: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2702: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2705: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2708: invokestatic 1148	android/os/UserHandle:getUserId	(I)I
    //   2711: invokeinterface 1383 3 0
    //   2716: istore 8
    //   2718: iload 8
    //   2720: istore 7
    //   2722: iload_3
    //   2723: istore 4
    //   2725: iload 7
    //   2727: ifne +72 -> 2799
    //   2730: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   2733: ifeq +2014 -> 4747
    //   2736: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   2739: new 104	java/lang/StringBuilder
    //   2742: dup
    //   2743: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2746: ldc_w 1385
    //   2749: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2752: aload 12
    //   2754: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2757: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   2760: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2763: ldc_w 1387
    //   2766: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2769: aload 12
    //   2771: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2774: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2777: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2780: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2783: ldc_w 1389
    //   2786: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2789: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2792: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   2795: pop
    //   2796: goto +1951 -> 4747
    //   2799: iload 4
    //   2801: istore_2
    //   2802: getstatic 398	android/os/Build:PERMISSIONS_REVIEW_REQUIRED	Z
    //   2805: ifeq +11 -> 2816
    //   2808: iload 4
    //   2810: ifeq +817 -> 3627
    //   2813: iload 4
    //   2815: istore_2
    //   2816: aload 12
    //   2818: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2821: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2824: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2827: istore 4
    //   2829: aload 11
    //   2831: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   2834: sipush 1000
    //   2837: if_icmpeq +42 -> 2879
    //   2840: iload_1
    //   2841: ifeq +38 -> 2879
    //   2844: aload_0
    //   2845: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2848: aload 11
    //   2850: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   2853: iload 4
    //   2855: invokevirtual 1393	com/android/server/am/ActivityManagerService:isValidSingletonCall	(II)Z
    //   2858: ifeq +21 -> 2879
    //   2861: aload 12
    //   2863: aload_0
    //   2864: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2867: aload 12
    //   2869: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2872: iconst_0
    //   2873: invokevirtual 1397	com/android/server/am/ActivityManagerService:getActivityInfoForUser	(Landroid/content/pm/ActivityInfo;I)Landroid/content/pm/ActivityInfo;
    //   2876: putfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2879: aload 12
    //   2881: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2884: getfield 1080	android/content/pm/ActivityInfo:processName	Ljava/lang/String;
    //   2887: astore 15
    //   2889: aload_0
    //   2890: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2893: aload 15
    //   2895: aload 12
    //   2897: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2900: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2903: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2906: iconst_0
    //   2907: invokevirtual 1401	com/android/server/am/ActivityManagerService:getProcessRecordLocked	(Ljava/lang/String;IZ)Lcom/android/server/am/ProcessRecord;
    //   2910: astore 14
    //   2912: iload_2
    //   2913: istore_3
    //   2914: iload_2
    //   2915: ifne +92 -> 3007
    //   2918: aload_0
    //   2919: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   2922: aload 12
    //   2924: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2927: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   2930: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   2933: aload 12
    //   2935: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   2938: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   2941: iconst_m1
    //   2942: iconst_0
    //   2943: invokevirtual 347	com/android/server/am/ActivityManagerService:checkAllowBackgroundLocked	(ILjava/lang/String;IZ)I
    //   2946: istore 6
    //   2948: iload_2
    //   2949: istore_3
    //   2950: iload 6
    //   2952: ifeq +55 -> 3007
    //   2955: iload 6
    //   2957: iconst_2
    //   2958: if_icmpne +708 -> 3666
    //   2961: ldc 35
    //   2963: new 104	java/lang/StringBuilder
    //   2966: dup
    //   2967: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   2970: ldc_w 1403
    //   2973: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2976: aload 11
    //   2978: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   2981: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2984: ldc_w 307
    //   2987: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2990: aload 17
    //   2992: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   2995: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2998: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3001: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3004: pop
    //   3005: iconst_1
    //   3006: istore_3
    //   3007: iload_3
    //   3008: ifeq +765 -> 3773
    //   3011: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   3014: ifeq +50 -> 3064
    //   3017: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   3020: new 104	java/lang/StringBuilder
    //   3023: dup
    //   3024: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3027: ldc_w 1405
    //   3030: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3033: aload_0
    //   3034: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   3037: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3040: ldc_w 371
    //   3043: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3046: aload 11
    //   3048: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   3051: ldc_w 1407
    //   3054: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3057: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3060: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   3063: pop
    //   3064: aload 11
    //   3066: getfield 383	com/android/server/am/BroadcastRecord:delivery	[I
    //   3069: iload 5
    //   3071: iconst_2
    //   3072: iastore
    //   3073: aload 11
    //   3075: aconst_null
    //   3076: putfield 418	com/android/server/am/BroadcastRecord:receiver	Landroid/os/IBinder;
    //   3079: aload 11
    //   3081: aconst_null
    //   3082: putfield 422	com/android/server/am/BroadcastRecord:curFilter	Lcom/android/server/am/BroadcastFilter;
    //   3085: aload 11
    //   3087: iconst_0
    //   3088: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   3091: aload_0
    //   3092: invokevirtual 833	com/android/server/am/BroadcastQueue:scheduleBroadcastsLocked	()V
    //   3095: aload 13
    //   3097: monitorexit
    //   3098: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   3101: return
    //   3102: ldc 35
    //   3104: new 104	java/lang/StringBuilder
    //   3107: dup
    //   3108: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3111: ldc_w 259
    //   3114: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3117: aload 11
    //   3119: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3122: invokevirtual 260	android/content/Intent:toString	()Ljava/lang/String;
    //   3125: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3128: ldc_w 262
    //   3131: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3134: aload 11
    //   3136: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   3139: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3142: ldc_w 267
    //   3145: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3148: aload 11
    //   3150: getfield 253	com/android/server/am/BroadcastRecord:callingPid	I
    //   3153: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3156: ldc_w 272
    //   3159: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3162: aload 11
    //   3164: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3167: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3170: ldc_w 274
    //   3173: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3176: ldc_w 276
    //   3179: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3182: aload 12
    //   3184: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3187: getfield 1341	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   3190: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3193: ldc_w 1348
    //   3196: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3199: aload 17
    //   3201: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   3204: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3207: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3210: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3213: pop
    //   3214: goto +1528 -> 4742
    //   3217: iload_3
    //   3218: istore_2
    //   3219: iload_3
    //   3220: ifne -1192 -> 2028
    //   3223: iload_3
    //   3224: istore_2
    //   3225: aload 12
    //   3227: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3230: getfield 1341	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   3233: ifnull -1205 -> 2028
    //   3236: aload 12
    //   3238: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3241: getfield 1341	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   3244: invokestatic 387	android/app/AppOpsManager:permissionToOpCode	(Ljava/lang/String;)I
    //   3247: istore 4
    //   3249: iload_3
    //   3250: istore_2
    //   3251: iload 4
    //   3253: iconst_m1
    //   3254: if_icmpeq -1226 -> 2028
    //   3257: iload_3
    //   3258: istore_2
    //   3259: aload_0
    //   3260: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   3263: getfield 324	com/android/server/am/ActivityManagerService:mAppOpsService	Lcom/android/server/AppOpsService;
    //   3266: iload 4
    //   3268: aload 11
    //   3270: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3273: aload 11
    //   3275: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   3278: invokevirtual 333	com/android/server/AppOpsService:noteOperation	(IILjava/lang/String;)I
    //   3281: ifeq -1253 -> 2028
    //   3284: ldc 35
    //   3286: new 104	java/lang/StringBuilder
    //   3289: dup
    //   3290: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3293: ldc_w 389
    //   3296: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3299: aload 11
    //   3301: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3304: invokevirtual 260	android/content/Intent:toString	()Ljava/lang/String;
    //   3307: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3310: ldc_w 262
    //   3313: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3316: aload 11
    //   3318: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   3321: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3324: ldc_w 267
    //   3327: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3330: aload 11
    //   3332: getfield 253	com/android/server/am/BroadcastRecord:callingPid	I
    //   3335: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3338: ldc_w 272
    //   3341: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3344: aload 11
    //   3346: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3349: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3352: ldc_w 274
    //   3355: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3358: ldc_w 337
    //   3361: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3364: aload 12
    //   3366: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3369: getfield 1341	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   3372: invokestatic 393	android/app/AppOpsManager:permissionToOp	(Ljava/lang/String;)Ljava/lang/String;
    //   3375: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3378: ldc_w 278
    //   3381: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3384: aload 17
    //   3386: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   3389: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3392: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3395: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3398: pop
    //   3399: iconst_1
    //   3400: istore_2
    //   3401: goto -1373 -> 2028
    //   3404: aload 14
    //   3406: invokestatic 387	android/app/AppOpsManager:permissionToOpCode	(Ljava/lang/String;)I
    //   3409: istore_3
    //   3410: iload_3
    //   3411: iconst_m1
    //   3412: if_icmpeq +1348 -> 4760
    //   3415: iload_3
    //   3416: aload 11
    //   3418: getfield 320	com/android/server/am/BroadcastRecord:appOp	I
    //   3421: if_icmpeq +1339 -> 4760
    //   3424: aload_0
    //   3425: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   3428: getfield 324	com/android/server/am/ActivityManagerService:mAppOpsService	Lcom/android/server/AppOpsService;
    //   3431: iload_3
    //   3432: aload 12
    //   3434: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3437: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   3440: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   3443: aload 12
    //   3445: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3448: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   3451: invokevirtual 333	com/android/server/AppOpsService:noteOperation	(IILjava/lang/String;)I
    //   3454: ifeq +1306 -> 4760
    //   3457: ldc 35
    //   3459: new 104	java/lang/StringBuilder
    //   3462: dup
    //   3463: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3466: ldc_w 335
    //   3469: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3472: aload 11
    //   3474: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3477: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   3480: ldc_w 307
    //   3483: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3486: aload 17
    //   3488: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   3491: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3494: ldc_w 337
    //   3497: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3500: aload 14
    //   3502: invokestatic 393	android/app/AppOpsManager:permissionToOp	(Ljava/lang/String;)Ljava/lang/String;
    //   3505: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3508: ldc_w 313
    //   3511: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3514: aload 11
    //   3516: getfield 265	com/android/server/am/BroadcastRecord:callerPackage	Ljava/lang/String;
    //   3519: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3522: ldc_w 315
    //   3525: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3528: aload 11
    //   3530: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3533: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3536: ldc_w 274
    //   3539: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3542: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3545: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3548: pop
    //   3549: iconst_1
    //   3550: istore_3
    //   3551: goto -1320 -> 2231
    //   3554: astore 14
    //   3556: ldc 35
    //   3558: aload 14
    //   3560: invokevirtual 1408	java/lang/SecurityException:getMessage	()Ljava/lang/String;
    //   3563: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3566: pop
    //   3567: iconst_1
    //   3568: istore_3
    //   3569: goto -1077 -> 2492
    //   3572: aload 11
    //   3574: aload 11
    //   3576: getfield 1280	com/android/server/am/BroadcastRecord:manifestSkipCount	I
    //   3579: iconst_1
    //   3580: iadd
    //   3581: putfield 1280	com/android/server/am/BroadcastRecord:manifestSkipCount	I
    //   3584: goto -993 -> 2591
    //   3587: astore 14
    //   3589: ldc 35
    //   3591: new 104	java/lang/StringBuilder
    //   3594: dup
    //   3595: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3598: ldc_w 1410
    //   3601: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3604: aload 12
    //   3606: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3609: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   3612: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3615: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3618: aload 14
    //   3620: invokestatic 485	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   3623: pop
    //   3624: goto -902 -> 2722
    //   3627: iload 4
    //   3629: istore_2
    //   3630: aload_0
    //   3631: aload 11
    //   3633: aload 12
    //   3635: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3638: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   3641: aload 12
    //   3643: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3646: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   3649: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   3652: invokestatic 1148	android/os/UserHandle:getUserId	(I)I
    //   3655: invokespecial 405	com/android/server/am/BroadcastQueue:requestStartTargetPermissionsReviewIfNeededLocked	(Lcom/android/server/am/BroadcastRecord;Ljava/lang/String;I)Z
    //   3658: ifne -842 -> 2816
    //   3661: iconst_1
    //   3662: istore_2
    //   3663: goto -847 -> 2816
    //   3666: aload 11
    //   3668: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3671: invokevirtual 1056	android/content/Intent:getFlags	()I
    //   3674: ldc_w 1411
    //   3677: iand
    //   3678: ifne +46 -> 3724
    //   3681: iload_2
    //   3682: istore_3
    //   3683: aload 11
    //   3685: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3688: invokevirtual 572	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   3691: ifnonnull -684 -> 3007
    //   3694: iload_2
    //   3695: istore_3
    //   3696: aload 11
    //   3698: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3701: invokevirtual 1273	android/content/Intent:getPackage	()Ljava/lang/String;
    //   3704: ifnonnull -697 -> 3007
    //   3707: iload_2
    //   3708: istore_3
    //   3709: aload 11
    //   3711: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3714: invokevirtual 1056	android/content/Intent:getFlags	()I
    //   3717: ldc_w 1412
    //   3720: iand
    //   3721: ifne -714 -> 3007
    //   3724: ldc 35
    //   3726: new 104	java/lang/StringBuilder
    //   3729: dup
    //   3730: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3733: ldc_w 349
    //   3736: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3739: aload 11
    //   3741: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   3744: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   3747: ldc_w 307
    //   3750: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3753: aload 17
    //   3755: invokevirtual 872	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   3758: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3761: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3764: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   3767: pop
    //   3768: iconst_1
    //   3769: istore_3
    //   3770: goto -763 -> 3007
    //   3773: aload 11
    //   3775: getfield 383	com/android/server/am/BroadcastRecord:delivery	[I
    //   3778: iload 5
    //   3780: iconst_1
    //   3781: iastore
    //   3782: aload 11
    //   3784: iconst_1
    //   3785: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   3788: aload 11
    //   3790: aload 17
    //   3792: putfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   3795: aload 11
    //   3797: aload 12
    //   3799: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3802: putfield 590	com/android/server/am/BroadcastRecord:curReceiver	Landroid/content/pm/ActivityInfo;
    //   3805: getstatic 1415	com/android/server/am/ActivityManagerDebugConfig:DEBUG_MU	Z
    //   3808: ifeq +78 -> 3886
    //   3811: aload 11
    //   3813: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3816: ldc_w 1416
    //   3819: if_icmple +67 -> 3886
    //   3822: ldc 39
    //   3824: new 104	java/lang/StringBuilder
    //   3827: dup
    //   3828: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   3831: ldc_w 1418
    //   3834: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3837: aload 12
    //   3839: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3842: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   3845: ldc_w 1420
    //   3848: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3851: aload 11
    //   3853: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3856: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3859: ldc_w 1422
    //   3862: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3865: aload 12
    //   3867: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3870: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   3873: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   3876: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3879: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3882: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   3885: pop
    //   3886: aload 16
    //   3888: ifnull +26 -> 3914
    //   3891: aload 16
    //   3893: invokevirtual 1303	android/app/BroadcastOptions:getTemporaryAppWhitelistDuration	()J
    //   3896: lconst_0
    //   3897: lcmp
    //   3898: ifle +16 -> 3914
    //   3901: aload_0
    //   3902: iload 4
    //   3904: aload 16
    //   3906: invokevirtual 1303	android/app/BroadcastOptions:getTemporaryAppWhitelistDuration	()J
    //   3909: aload 11
    //   3911: invokevirtual 1310	com/android/server/am/BroadcastQueue:scheduleTempWhitelistLocked	(IJLcom/android/server/am/BroadcastRecord;)V
    //   3914: invokestatic 1354	android/app/AppGlobals:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   3917: aload 11
    //   3919: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   3922: invokevirtual 577	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   3925: iconst_0
    //   3926: aload 11
    //   3928: getfield 213	com/android/server/am/BroadcastRecord:callingUid	I
    //   3931: invokestatic 1148	android/os/UserHandle:getUserId	(I)I
    //   3934: invokeinterface 1426 4 0
    //   3939: aload 14
    //   3941: ifnull +244 -> 4185
    //   3944: aload 14
    //   3946: getfield 539	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   3949: astore 16
    //   3951: aload 16
    //   3953: ifnull +232 -> 4185
    //   3956: aload 14
    //   3958: aload 12
    //   3960: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3963: getfield 1361	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   3966: aload 12
    //   3968: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   3971: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   3974: getfield 1429	android/content/pm/ApplicationInfo:versionCode	I
    //   3977: aload_0
    //   3978: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   3981: getfield 1433	com/android/server/am/ActivityManagerService:mProcessStats	Lcom/android/server/am/ProcessStatsService;
    //   3984: invokevirtual 1437	com/android/server/am/ProcessRecord:addPackage	(Ljava/lang/String;ILcom/android/server/am/ProcessStatsService;)Z
    //   3987: pop
    //   3988: aload_0
    //   3989: aload 11
    //   3991: aload 14
    //   3993: invokespecial 1439	com/android/server/am/BroadcastQueue:processCurBroadcastLocked	(Lcom/android/server/am/BroadcastRecord;Lcom/android/server/am/ProcessRecord;)V
    //   3996: aload 13
    //   3998: monitorexit
    //   3999: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4002: return
    //   4003: astore 16
    //   4005: ldc 35
    //   4007: new 104	java/lang/StringBuilder
    //   4010: dup
    //   4011: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4014: ldc_w 1441
    //   4017: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4020: aload 11
    //   4022: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   4025: invokevirtual 577	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   4028: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4031: ldc_w 568
    //   4034: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4037: aload 16
    //   4039: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4042: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4045: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   4048: pop
    //   4049: goto -110 -> 3939
    //   4052: astore 12
    //   4054: ldc 35
    //   4056: new 104	java/lang/StringBuilder
    //   4059: dup
    //   4060: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4063: ldc_w 1443
    //   4066: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4069: aload 11
    //   4071: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   4074: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4077: ldc_w 1445
    //   4080: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4083: aload 11
    //   4085: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   4088: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4091: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4094: aload 12
    //   4096: invokestatic 1448	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   4099: pop
    //   4100: aload_0
    //   4101: aload 11
    //   4103: invokevirtual 823	com/android/server/am/BroadcastQueue:logBroadcastReceiverDiscardLocked	(Lcom/android/server/am/BroadcastRecord;)V
    //   4106: aload_0
    //   4107: aload 11
    //   4109: aload 11
    //   4111: getfield 460	com/android/server/am/BroadcastRecord:resultCode	I
    //   4114: aload 11
    //   4116: getfield 463	com/android/server/am/BroadcastRecord:resultData	Ljava/lang/String;
    //   4119: aload 11
    //   4121: getfield 467	com/android/server/am/BroadcastRecord:resultExtras	Landroid/os/Bundle;
    //   4124: aload 11
    //   4126: getfield 826	com/android/server/am/BroadcastRecord:resultAbort	Z
    //   4129: iconst_0
    //   4130: invokevirtual 830	com/android/server/am/BroadcastQueue:finishReceiverLocked	(Lcom/android/server/am/BroadcastRecord;ILjava/lang/String;Landroid/os/Bundle;ZZ)Z
    //   4133: pop
    //   4134: aload_0
    //   4135: invokevirtual 833	com/android/server/am/BroadcastQueue:scheduleBroadcastsLocked	()V
    //   4138: aload 11
    //   4140: iconst_0
    //   4141: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   4144: aload 13
    //   4146: monitorexit
    //   4147: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4150: return
    //   4151: astore 16
    //   4153: ldc 35
    //   4155: new 104	java/lang/StringBuilder
    //   4158: dup
    //   4159: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4162: ldc_w 1450
    //   4165: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4168: aload 11
    //   4170: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   4173: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4176: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4179: aload 16
    //   4181: invokestatic 485	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   4184: pop
    //   4185: getstatic 528	com/android/server/am/ActivityManagerDebugConfig:DEBUG_BROADCAST	Z
    //   4188: ifeq +55 -> 4243
    //   4191: getstatic 89	com/android/server/am/BroadcastQueue:TAG_BROADCAST	Ljava/lang/String;
    //   4194: new 104	java/lang/StringBuilder
    //   4197: dup
    //   4198: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4201: ldc_w 1452
    //   4204: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4207: aload_0
    //   4208: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   4211: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4214: ldc_w 371
    //   4217: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4220: aload 15
    //   4222: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4225: ldc_w 1454
    //   4228: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4231: aload 11
    //   4233: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4236: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4239: invokestatic 535	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   4242: pop
    //   4243: getstatic 1459	com/android/server/am/OnePlusAppBootManager:DEBUG	Z
    //   4246: ifeq +51 -> 4297
    //   4249: new 104	java/lang/StringBuilder
    //   4252: dup
    //   4253: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4256: ldc_w 1461
    //   4259: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4262: aload_0
    //   4263: getfield 191	com/android/server/am/BroadcastQueue:mQueueName	Ljava/lang/String;
    //   4266: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4269: ldc_w 371
    //   4272: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4275: aload 15
    //   4277: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4280: ldc_w 1454
    //   4283: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4286: aload 11
    //   4288: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4291: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4294: invokestatic 1464	com/android/server/am/OnePlusAppBootManager:myLog	(Ljava/lang/String;)V
    //   4297: getstatic 1467	com/android/server/am/OnePlusAppBootManager:IN_USING	Z
    //   4300: ifeq +68 -> 4368
    //   4303: aconst_null
    //   4304: invokestatic 1471	com/android/server/am/OnePlusAppBootManager:getInstance	(Lcom/android/server/pm/PackageManagerService;)Lcom/android/server/am/OnePlusAppBootManager;
    //   4307: aload 12
    //   4309: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4312: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   4315: aload 11
    //   4317: invokevirtual 1475	com/android/server/am/OnePlusAppBootManager:canReceiverGo	(Landroid/content/pm/ApplicationInfo;Lcom/android/server/am/BroadcastRecord;)Z
    //   4320: ifne +48 -> 4368
    //   4323: aload_0
    //   4324: aload 11
    //   4326: aload 11
    //   4328: getfield 460	com/android/server/am/BroadcastRecord:resultCode	I
    //   4331: aload 11
    //   4333: getfield 463	com/android/server/am/BroadcastRecord:resultData	Ljava/lang/String;
    //   4336: aload 11
    //   4338: getfield 467	com/android/server/am/BroadcastRecord:resultExtras	Landroid/os/Bundle;
    //   4341: aload 11
    //   4343: getfield 826	com/android/server/am/BroadcastRecord:resultAbort	Z
    //   4346: iconst_0
    //   4347: invokevirtual 830	com/android/server/am/BroadcastQueue:finishReceiverLocked	(Lcom/android/server/am/BroadcastRecord;ILjava/lang/String;Landroid/os/Bundle;ZZ)Z
    //   4350: pop
    //   4351: aload_0
    //   4352: invokevirtual 833	com/android/server/am/BroadcastQueue:scheduleBroadcastsLocked	()V
    //   4355: aload 11
    //   4357: iconst_0
    //   4358: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   4361: aload 13
    //   4363: monitorexit
    //   4364: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4367: return
    //   4368: aload_0
    //   4369: aload 12
    //   4371: aload 11
    //   4373: invokestatic 1479	com/android/server/am/OnePlusProcessManager:checkBroadcastIsPackageCanStart	(Lcom/android/server/am/BroadcastQueue;Landroid/content/pm/ResolveInfo;Lcom/android/server/am/BroadcastRecord;)Z
    //   4376: istore_1
    //   4377: iload_1
    //   4378: ifeq +10 -> 4388
    //   4381: aload 13
    //   4383: monitorexit
    //   4384: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4387: return
    //   4388: aload_0
    //   4389: getfield 175	com/android/server/am/BroadcastQueue:mService	Lcom/android/server/am/ActivityManagerService;
    //   4392: astore 16
    //   4394: aload 12
    //   4396: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4399: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   4402: astore 17
    //   4404: aload 11
    //   4406: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   4409: invokevirtual 1056	android/content/Intent:getFlags	()I
    //   4412: istore_2
    //   4413: new 104	java/lang/StringBuilder
    //   4416: dup
    //   4417: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4420: ldc_w 1481
    //   4423: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4426: aload 11
    //   4428: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   4431: invokevirtual 663	android/content/Intent:getAction	()Ljava/lang/String;
    //   4434: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4437: ldc_w 1483
    //   4440: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4443: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4446: astore 18
    //   4448: aload 11
    //   4450: getfield 557	com/android/server/am/BroadcastRecord:curComponent	Landroid/content/ComponentName;
    //   4453: astore 19
    //   4455: aload 11
    //   4457: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   4460: invokevirtual 1056	android/content/Intent:getFlags	()I
    //   4463: ldc_w 1484
    //   4466: iand
    //   4467: ifeq +166 -> 4633
    //   4470: iconst_1
    //   4471: istore_1
    //   4472: aload 16
    //   4474: aload 15
    //   4476: aload 17
    //   4478: iconst_1
    //   4479: iload_2
    //   4480: iconst_4
    //   4481: ior
    //   4482: aload 18
    //   4484: aload 19
    //   4486: iload_1
    //   4487: iconst_0
    //   4488: iconst_0
    //   4489: invokevirtual 1488	com/android/server/am/ActivityManagerService:startProcessLocked	(Ljava/lang/String;Landroid/content/pm/ApplicationInfo;ZILjava/lang/String;Landroid/content/ComponentName;ZZZ)Lcom/android/server/am/ProcessRecord;
    //   4492: astore 15
    //   4494: aload 11
    //   4496: aload 15
    //   4498: putfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   4501: aload 15
    //   4503: ifnonnull +135 -> 4638
    //   4506: ldc 35
    //   4508: new 104	java/lang/StringBuilder
    //   4511: dup
    //   4512: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4515: ldc_w 1490
    //   4518: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4521: aload 12
    //   4523: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4526: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   4529: getfield 1324	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   4532: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4535: ldc_w 1492
    //   4538: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4541: aload 12
    //   4543: getfield 1142	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   4546: getfield 596	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   4549: getfield 1077	android/content/pm/ApplicationInfo:uid	I
    //   4552: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   4555: ldc_w 1454
    //   4558: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4561: aload 11
    //   4563: getfield 230	com/android/server/am/BroadcastRecord:intent	Landroid/content/Intent;
    //   4566: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4569: ldc_w 1494
    //   4572: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4575: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4578: invokestatic 287	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   4581: pop
    //   4582: aload_0
    //   4583: aload 11
    //   4585: invokevirtual 823	com/android/server/am/BroadcastQueue:logBroadcastReceiverDiscardLocked	(Lcom/android/server/am/BroadcastRecord;)V
    //   4588: aload_0
    //   4589: aload 11
    //   4591: aload 11
    //   4593: getfield 460	com/android/server/am/BroadcastRecord:resultCode	I
    //   4596: aload 11
    //   4598: getfield 463	com/android/server/am/BroadcastRecord:resultData	Ljava/lang/String;
    //   4601: aload 11
    //   4603: getfield 467	com/android/server/am/BroadcastRecord:resultExtras	Landroid/os/Bundle;
    //   4606: aload 11
    //   4608: getfield 826	com/android/server/am/BroadcastRecord:resultAbort	Z
    //   4611: iconst_0
    //   4612: invokevirtual 830	com/android/server/am/BroadcastQueue:finishReceiverLocked	(Lcom/android/server/am/BroadcastRecord;ILjava/lang/String;Landroid/os/Bundle;ZZ)Z
    //   4615: pop
    //   4616: aload_0
    //   4617: invokevirtual 833	com/android/server/am/BroadcastQueue:scheduleBroadcastsLocked	()V
    //   4620: aload 11
    //   4622: iconst_0
    //   4623: putfield 428	com/android/server/am/BroadcastRecord:state	I
    //   4626: aload 13
    //   4628: monitorexit
    //   4629: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4632: return
    //   4633: iconst_0
    //   4634: istore_1
    //   4635: goto -163 -> 4472
    //   4638: aload_0
    //   4639: aload 11
    //   4641: putfield 164	com/android/server/am/BroadcastQueue:mPendingBroadcast	Lcom/android/server/am/BroadcastRecord;
    //   4644: aload_0
    //   4645: iload 5
    //   4647: putfield 1228	com/android/server/am/BroadcastQueue:mPendingBroadcastRecvIndex	I
    //   4650: aload 11
    //   4652: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   4655: ifnull +26 -> 4681
    //   4658: aload 11
    //   4660: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   4663: getfield 1497	com/android/server/am/ProcessRecord:isEmbryo	Z
    //   4666: istore_1
    //   4667: iload_1
    //   4668: ifeq +13 -> 4681
    //   4671: aload_0
    //   4672: aload 11
    //   4674: getfield 431	com/android/server/am/BroadcastRecord:curApp	Lcom/android/server/am/ProcessRecord;
    //   4677: invokevirtual 1500	com/android/server/am/BroadcastQueue:sendPendingBroadcastsLocked	(Lcom/android/server/am/ProcessRecord;)Z
    //   4680: pop
    //   4681: aload 13
    //   4683: monitorexit
    //   4684: invokestatic 1175	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   4687: return
    //   4688: astore 11
    //   4690: ldc 35
    //   4692: new 104	java/lang/StringBuilder
    //   4695: dup
    //   4696: invokespecial 107	java/lang/StringBuilder:<init>	()V
    //   4699: ldc_w 1502
    //   4702: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4705: aload 14
    //   4707: invokevirtual 281	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4710: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4713: aload 11
    //   4715: invokestatic 1448	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   4718: pop
    //   4719: goto -38 -> 4681
    //   4722: astore 16
    //   4724: goto -785 -> 3939
    //   4727: iconst_0
    //   4728: istore_2
    //   4729: goto -4524 -> 205
    //   4732: iconst_0
    //   4733: istore_2
    //   4734: goto -4143 -> 591
    //   4737: iconst_1
    //   4738: istore_3
    //   4739: goto -2893 -> 1846
    //   4742: iconst_1
    //   4743: istore_2
    //   4744: goto -2716 -> 2028
    //   4747: iconst_1
    //   4748: istore 4
    //   4750: goto -1951 -> 2799
    //   4753: astore 15
    //   4755: iconst_m1
    //   4756: istore_3
    //   4757: goto -2621 -> 2136
    //   4760: iload 4
    //   4762: iconst_1
    //   4763: iadd
    //   4764: istore 4
    //   4766: goto -2689 -> 2077
    //   4769: iconst_1
    //   4770: istore_2
    //   4771: goto -2327 -> 2444
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	4774	0	this	BroadcastQueue
    //   0	4774	1	paramBoolean	boolean
    //   205	4566	2	i	int
    //   154	4603	3	j	int
    //   663	4102	4	k	int
    //   640	4006	5	m	int
    //   2946	13	6	n	int
    //   2485	241	7	bool1	boolean
    //   2716	3	8	bool2	boolean
    //   692	739	9	l	long
    //   123	232	11	localBroadcastRecord	BroadcastRecord
    //   360	14	11	localObject1	Object
    //   445	1164	11	localObject2	Object
    //   1618	3055	11	localRemoteException1	RemoteException
    //   4688	26	11	localException1	Exception
    //   221	64	12	localObject3	Object
    //   371	6	12	localObject4	Object
    //   473	8	12	localProcessRecord	ProcessRecord
    //   505	6	12	localObject5	Object
    //   637	1018	12	localObject6	Object
    //   1674	86	12	localObject7	Object
    //   1764	2203	12	localResolveInfo	ResolveInfo
    //   4052	490	12	localRuntimeException	RuntimeException
    //   4	4678	13	localActivityManagerService1	ActivityManagerService
    //   2098	1403	14	localObject8	Object
    //   3554	5	14	localSecurityException	SecurityException
    //   3587	1119	14	localException2	Exception
    //   2887	1615	15	localObject9	Object
    //   4753	1	15	localRemoteException2	RemoteException
    //   1440	2512	16	localObject10	Object
    //   4003	35	16	localIllegalArgumentException	IllegalArgumentException
    //   4151	29	16	localRemoteException3	RemoteException
    //   4392	81	16	localActivityManagerService2	ActivityManagerService
    //   4722	1	16	localRemoteException4	RemoteException
    //   1792	2685	17	localObject11	Object
    //   4446	37	18	str	String
    //   4453	32	19	localComponentName	ComponentName
    // Exception table:
    //   from	to	target	type
    //   9	86	360	finally
    //   86	93	360	finally
    //   97	102	360	finally
    //   102	128	360	finally
    //   354	357	360	finally
    //   373	379	360	finally
    //   379	438	360	finally
    //   438	450	360	finally
    //   486	489	360	finally
    //   507	513	360	finally
    //   513	588	360	finally
    //   591	608	360	finally
    //   612	619	360	finally
    //   626	639	360	finally
    //   642	661	360	finally
    //   665	675	360	finally
    //   679	694	360	finally
    //   706	860	360	finally
    //   863	869	360	finally
    //   869	933	360	finally
    //   945	962	360	finally
    //   962	969	360	finally
    //   974	1041	360	finally
    //   1041	1095	360	finally
    //   1095	1111	360	finally
    //   1111	1149	360	finally
    //   1149	1233	360	finally
    //   1233	1242	360	finally
    //   1252	1255	360	finally
    //   1271	1282	360	finally
    //   1287	1352	360	finally
    //   1352	1429	360	finally
    //   1429	1435	360	finally
    //   1435	1529	360	finally
    //   1529	1560	360	finally
    //   1565	1591	360	finally
    //   1598	1606	360	finally
    //   1620	1671	360	finally
    //   1676	1682	360	finally
    //   1682	1746	360	finally
    //   1746	1756	360	finally
    //   1759	1794	360	finally
    //   1803	1822	360	finally
    //   1824	1843	360	finally
    //   1846	1891	360	finally
    //   1899	2025	360	finally
    //   2036	2053	360	finally
    //   2055	2063	360	finally
    //   2065	2074	360	finally
    //   2079	2100	360	finally
    //   2100	2136	360	finally
    //   2140	2229	360	finally
    //   2241	2250	360	finally
    //   2253	2385	360	finally
    //   2396	2438	360	finally
    //   2446	2487	360	finally
    //   2494	2509	360	finally
    //   2511	2573	360	finally
    //   2579	2591	360	finally
    //   2593	2601	360	finally
    //   2603	2674	360	finally
    //   2686	2718	360	finally
    //   2730	2796	360	finally
    //   2802	2808	360	finally
    //   2816	2840	360	finally
    //   2844	2879	360	finally
    //   2879	2912	360	finally
    //   2918	2948	360	finally
    //   2961	3005	360	finally
    //   3011	3064	360	finally
    //   3064	3095	360	finally
    //   3102	3214	360	finally
    //   3225	3249	360	finally
    //   3259	3399	360	finally
    //   3404	3410	360	finally
    //   3415	3549	360	finally
    //   3556	3567	360	finally
    //   3572	3584	360	finally
    //   3589	3624	360	finally
    //   3630	3661	360	finally
    //   3666	3681	360	finally
    //   3683	3694	360	finally
    //   3696	3707	360	finally
    //   3709	3724	360	finally
    //   3724	3768	360	finally
    //   3773	3886	360	finally
    //   3891	3914	360	finally
    //   3914	3939	360	finally
    //   3944	3951	360	finally
    //   3956	3996	360	finally
    //   4005	4049	360	finally
    //   4054	4144	360	finally
    //   4153	4185	360	finally
    //   4185	4243	360	finally
    //   4243	4297	360	finally
    //   4297	4361	360	finally
    //   4368	4377	360	finally
    //   4388	4470	360	finally
    //   4472	4501	360	finally
    //   4506	4626	360	finally
    //   4638	4667	360	finally
    //   4671	4681	360	finally
    //   4690	4719	360	finally
    //   128	202	371	finally
    //   210	281	371	finally
    //   281	294	371	finally
    //   301	354	371	finally
    //   450	475	505	finally
    //   480	486	505	finally
    //   974	1041	1618	android/os/RemoteException
    //   1041	1095	1618	android/os/RemoteException
    //   1255	1271	1674	finally
    //   2446	2487	3554	java/lang/SecurityException
    //   2686	2718	3587	java/lang/Exception
    //   3914	3939	4003	java/lang/IllegalArgumentException
    //   3956	3996	4052	java/lang/RuntimeException
    //   3956	3996	4151	android/os/RemoteException
    //   4671	4681	4688	java/lang/Exception
    //   3914	3939	4722	android/os/RemoteException
    //   2100	2136	4753	android/os/RemoteException
  }
  
  public final boolean replaceOrderedBroadcastLocked(BroadcastRecord paramBroadcastRecord)
  {
    int i = this.mOrderedBroadcasts.size() - 1;
    while (i > 0)
    {
      if (paramBroadcastRecord.intent.filterEquals(((BroadcastRecord)this.mOrderedBroadcasts.get(i)).intent))
      {
        if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
          Slog.v(TAG_BROADCAST, "***** DROPPING ORDERED [" + this.mQueueName + "]: " + paramBroadcastRecord.intent);
        }
        this.mOrderedBroadcasts.set(i, paramBroadcastRecord);
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  public final boolean replaceParallelBroadcastLocked(BroadcastRecord paramBroadcastRecord)
  {
    int i = this.mParallelBroadcasts.size() - 1;
    while (i >= 0)
    {
      Object localObject = (BroadcastRecord)this.mParallelBroadcasts.get(i);
      if (localObject != null)
      {
        localObject = ((BroadcastRecord)localObject).intent;
        if (paramBroadcastRecord.intent.filterEquals((Intent)localObject))
        {
          if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
            Slog.v(TAG_BROADCAST, "***** DROPPING PARALLEL [" + this.mQueueName + "]: " + paramBroadcastRecord.intent);
          }
          this.mParallelBroadcasts.set(i, paramBroadcastRecord);
          return true;
        }
      }
      i -= 1;
    }
    return false;
  }
  
  public void scheduleBroadcastsLocked()
  {
    if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
      Slog.v(TAG_BROADCAST, "Schedule broadcasts [" + this.mQueueName + "]: current=" + this.mBroadcastsScheduled);
    }
    if (this.mBroadcastsScheduled) {
      return;
    }
    this.mHandler.sendMessage(this.mHandler.obtainMessage(200, this));
    this.mBroadcastsScheduled = true;
  }
  
  final void scheduleTempWhitelistLocked(int paramInt, long paramLong, BroadcastRecord paramBroadcastRecord)
  {
    long l = paramLong;
    if (paramLong > 2147483647L) {
      l = 2147483647L;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("broadcast:");
    UserHandle.formatUid(localStringBuilder, paramBroadcastRecord.callingUid);
    localStringBuilder.append(":");
    if (paramBroadcastRecord.intent.getAction() != null) {
      localStringBuilder.append(paramBroadcastRecord.intent.getAction());
    }
    for (;;)
    {
      this.mHandler.obtainMessage(202, paramInt, (int)l, localStringBuilder.toString()).sendToTarget();
      return;
      if (paramBroadcastRecord.intent.getComponent() != null) {
        localStringBuilder.append(paramBroadcastRecord.intent.getComponent().flattenToShortString());
      } else if (paramBroadcastRecord.intent.getData() != null) {
        localStringBuilder.append(paramBroadcastRecord.intent.getData());
      }
    }
  }
  
  public boolean sendPendingBroadcastsLocked(ProcessRecord paramProcessRecord)
  {
    boolean bool2 = false;
    BroadcastRecord localBroadcastRecord = this.mPendingBroadcast;
    boolean bool1 = bool2;
    if (localBroadcastRecord != null)
    {
      bool1 = bool2;
      if (localBroadcastRecord.curApp.pid == paramProcessRecord.pid) {
        if (localBroadcastRecord.curApp != paramProcessRecord)
        {
          Slog.e("BroadcastQueue", "App mismatch when sending pending broadcast to " + paramProcessRecord.processName + ", intended target is " + localBroadcastRecord.curApp.processName);
          return false;
        }
      }
    }
    StringBuilder localStringBuilder;
    try
    {
      this.mPendingBroadcast = null;
      processCurBroadcastLocked(localBroadcastRecord, paramProcessRecord);
      bool1 = true;
      return bool1;
    }
    catch (Exception localException)
    {
      localStringBuilder = new StringBuilder().append("Exception in new application when starting receiver ");
      if (localBroadcastRecord.curComponent == null) {}
    }
    for (paramProcessRecord = localBroadcastRecord.curComponent.flattenToShortString();; paramProcessRecord = localBroadcastRecord)
    {
      Slog.w("BroadcastQueue", paramProcessRecord, localException);
      logBroadcastReceiverDiscardLocked(localBroadcastRecord);
      finishReceiverLocked(localBroadcastRecord, localBroadcastRecord.resultCode, localBroadcastRecord.resultData, localBroadcastRecord.resultExtras, localBroadcastRecord.resultAbort, false);
      scheduleBroadcastsLocked();
      localBroadcastRecord.state = 0;
      throw new RuntimeException(localException.getMessage());
    }
  }
  
  final void setBroadcastTimeoutLocked(long paramLong)
  {
    if (!this.mPendingBroadcastTimeoutMessage)
    {
      Message localMessage = this.mHandler.obtainMessage(201, this);
      this.mHandler.sendMessageAtTime(localMessage, paramLong);
      this.mPendingBroadcastTimeoutMessage = true;
    }
  }
  
  public void skipCurrentReceiverLocked(ProcessRecord paramProcessRecord)
  {
    Object localObject2 = null;
    ??? = localObject2;
    Object localObject3;
    if (this.mOrderedBroadcasts.size() > 0)
    {
      localObject3 = (BroadcastRecord)this.mOrderedBroadcasts.get(0);
      ??? = localObject2;
      if (((BroadcastRecord)localObject3).curApp == paramProcessRecord) {
        ??? = localObject3;
      }
    }
    localObject2 = ???;
    if (??? == null)
    {
      localObject2 = ???;
      if (this.mPendingBroadcast != null)
      {
        localObject2 = ???;
        if (this.mPendingBroadcast.curApp == paramProcessRecord)
        {
          if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
            Slog.v(TAG_BROADCAST, "[" + this.mQueueName + "] skip & discard pending app " + ???);
          }
          localObject2 = this.mPendingBroadcast;
        }
      }
    }
    if ((localObject2 == null) && (paramProcessRecord.ReceiverRecords != null)) {}
    for (;;)
    {
      int i;
      synchronized (mOptLock)
      {
        i = paramProcessRecord.ReceiverRecords.size();
        if (ActivityManagerDebugConfig.DEBUG_BROADCAST)
        {
          Slog.i("BroadcastQueue", "skipCurrentReceiverLocked # [" + this.mQueueName + "] # appSize = " + i + ", mapSize = " + this.mReceiverRecords.size());
          break label348;
          if (i >= 0)
          {
            localObject3 = (ReceiverRecord)paramProcessRecord.ReceiverRecords.get(i);
            if ((localObject3 != null) && (((ReceiverRecord)localObject3).mQueue == this))
            {
              if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
                Slog.i("BroadcastQueue", "skipCurrentReceiverLocked # i=" + i + " -> receiverRecord = " + localObject3);
              }
              ((ReceiverRecord)localObject3).cancelBroadcastTimeoutLocked();
              this.mReceiverRecords.remove(Integer.valueOf(((ReceiverRecord)localObject3).hashCode()));
              paramProcessRecord.ReceiverRecords.remove(localObject3);
            }
            i -= 1;
            continue;
          }
          if (localObject2 != null) {
            skipReceiverLocked((BroadcastRecord)localObject2);
          }
          return;
        }
      }
      label348:
      i -= 1;
    }
  }
  
  public void skipPendingBroadcastForUid(int paramInt)
  {
    BroadcastRecord localBroadcastRecord = this.mPendingBroadcast;
    if ((localBroadcastRecord != null) && (localBroadcastRecord.curApp.uid == paramInt))
    {
      Slog.w("BroadcastQueue", "skipPendingBroadcastForUid: " + localBroadcastRecord);
      localBroadcastRecord.state = 0;
      localBroadcastRecord.nextReceiver = this.mPendingBroadcastRecvIndex;
      this.mPendingBroadcast = null;
      scheduleBroadcastsLocked();
    }
  }
  
  public void skipPendingBroadcastLocked(int paramInt)
  {
    BroadcastRecord localBroadcastRecord = this.mPendingBroadcast;
    if ((localBroadcastRecord != null) && (localBroadcastRecord.curApp.pid == paramInt))
    {
      localBroadcastRecord.state = 0;
      localBroadcastRecord.nextReceiver = this.mPendingBroadcastRecvIndex;
      this.mPendingBroadcast = null;
      scheduleBroadcastsLocked();
    }
  }
  
  private final class AppNotResponding
    implements Runnable
  {
    private final String mAnnotation;
    private final ProcessRecord mApp;
    
    public AppNotResponding(ProcessRecord paramProcessRecord, String paramString)
    {
      this.mApp = paramProcessRecord;
      this.mAnnotation = paramString;
    }
    
    public void run()
    {
      BroadcastQueue.this.mService.mAppErrors.appNotResponding(this.mApp, null, null, false, this.mAnnotation);
    }
  }
  
  private final class BroadcastHandler
    extends Handler
  {
    public BroadcastHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
      case 200: 
      case 201: 
      case 202: 
      case 203: 
        for (;;)
        {
          return;
          if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
            Slog.v(BroadcastQueue.-get0(), "Received BROADCAST_INTENT_MSG");
          }
          BroadcastQueue.this.processNextBroadcast(true);
          return;
          synchronized (BroadcastQueue.this.mService)
          {
            ActivityManagerService.boostPriorityForLockedSection();
            BroadcastQueue.this.broadcastTimeoutLocked(true);
            ActivityManagerService.resetPriorityAfterLockedSection();
            return;
          }
          Object localObject2 = BroadcastQueue.this.mService.mLocalDeviceIdleController;
          if (localObject2 != null)
          {
            ((DeviceIdleController.LocalService)localObject2).addPowerSaveTempWhitelistAppDirect(UserHandle.getAppId(???.arg1), ???.arg2, true, (String)???.obj);
            return;
            localObject2 = (BroadcastRecord)???.obj;
            if (ActivityManagerDebugConfig.DEBUG_BROADCAST) {
              Slog.v("BroadcastQueue", "Received BROADCAST_NEXT_MSG ,finishReceiver , broadcastRecord = " + localObject2);
            }
            synchronized (BroadcastQueue.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              boolean bool = BroadcastQueue.this.finishReceiverLocked((BroadcastRecord)localObject2, 0, null, null, false, true);
              ActivityManagerService.resetPriorityAfterLockedSection();
              if (bool)
              {
                BroadcastQueue.this.processNextBroadcast(false);
                return;
              }
            }
          }
        }
      }
      ??? = new ConfigGrabber(BroadcastQueue.-get1(BroadcastQueue.this), "BroadcastOptimization");
      BroadcastQueue.-wrap0(BroadcastQueue.this, ???.grabConfig());
    }
  }
  
  class BroadcastOptimizationConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    BroadcastOptimizationConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      BroadcastQueue.-wrap0(BroadcastQueue.this, paramJSONArray);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/BroadcastQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */