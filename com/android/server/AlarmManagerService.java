package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.app.IActivityManager;
import android.app.IAlarmCompleteListener.Stub;
import android.app.IAlarmListener;
import android.app.IAlarmManager.Stub;
import android.app.IUidObserver.Stub;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.PendingIntent.OnFinished;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import com.android.internal.util.LocalLog;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeSet;

class AlarmManagerService
  extends SystemService
{
  static final int ALARM_EVENT = 1;
  static boolean DEBUG_ALARM_CLOCK = false;
  static boolean DEBUG_BATCH = false;
  static boolean DEBUG_LISTENER_CALLBACK = false;
  public static boolean DEBUG_ONEPLUS = false;
  static boolean DEBUG_VALIDATE = false;
  private static final String DESKCLOCK_PACKAGE_NAME = "com.android.deskclock";
  private static final int ELAPSED_REALTIME_MASK = 8;
  private static final int ELAPSED_REALTIME_WAKEUP_MASK = 4;
  private static final String ENCRYPTED_STATE = "1";
  private static final String ENCRYPTING_STATE = "trigger_restart_min_framework";
  static final int IS_WAKEUP_MASK = 37;
  static final long MIN_FUZZABLE_INTERVAL = 10000L;
  private static final Intent NEXT_ALARM_CLOCK_CHANGED_INTENT;
  static final int PRIO_NORMAL = 2;
  static final int PRIO_TICK = 0;
  static final int PRIO_WAKEUP = 1;
  static final boolean RECORD_ALARMS_IN_HISTORY = true;
  static final boolean RECORD_DEVICE_IDLE_ALARMS = false;
  private static final int RTC_MASK = 2;
  private static final int RTC_POWEROFF_WAKEUP_MASK = 32;
  private static final int RTC_WAKEUP_MASK = 1;
  static final String TAG = "AlarmManager";
  static final String TIMEZONE_PROPERTY = "persist.sys.timezone";
  static final int TIME_CHANGED_MASK = 65536;
  static final int TYPE_NONWAKEUP_MASK = 1;
  static final boolean WAKEUP_STATS;
  static boolean isDozeChangeSupport;
  static boolean localLOGV;
  static List<String> mBlackAlarmOperation;
  static ArrayMap<String, Long> mFrozeenTimeUids;
  static ArrayMap<String, Integer> mFrozeenUids;
  static final BatchTimeOrder sBatchOrder;
  static final IncreasingTimeOrder sIncreasingTimeOrder;
  final boolean DELAY_SUCCESS = true;
  final long RECENT_WAKEUP_PERIOD = 86400000L;
  final ArrayList<Batch> mAlarmBatches = new ArrayList();
  final Comparator<Alarm> mAlarmDispatchComparator = new Comparator()
  {
    public int compare(AlarmManagerService.Alarm paramAnonymousAlarm1, AlarmManagerService.Alarm paramAnonymousAlarm2)
    {
      if (paramAnonymousAlarm1.priorityClass.priority < paramAnonymousAlarm2.priorityClass.priority) {
        return -1;
      }
      if (paramAnonymousAlarm1.priorityClass.priority > paramAnonymousAlarm2.priorityClass.priority) {
        return 1;
      }
      if (paramAnonymousAlarm1.whenElapsed < paramAnonymousAlarm2.whenElapsed) {
        return -1;
      }
      if (paramAnonymousAlarm1.whenElapsed > paramAnonymousAlarm2.whenElapsed) {
        return 1;
      }
      return 0;
    }
  };
  final ArrayList<IdleDispatchEntry> mAllowWhileIdleDispatches = new ArrayList();
  long mAllowWhileIdleMinTime;
  AppOpsManager mAppOps;
  private final Intent mBackgroundIntent = new Intent().addFlags(4);
  int mBroadcastRefCount = 0;
  final SparseArray<ArrayMap<String, BroadcastStats>> mBroadcastStats = new SparseArray();
  ClockReceiver mClockReceiver;
  final Constants mConstants = new Constants(this.mHandler);
  int mCurrentSeq = 0;
  PendingIntent mDateChangeSender;
  final DeliveryTracker mDeliveryTracker = new DeliveryTracker();
  int[] mDeviceIdleUserWhitelist = new int[0];
  private List<String> mDozeBlackList;
  final AlarmHandler mHandler = new AlarmHandler();
  private final SparseArray<AlarmManager.AlarmClockInfo> mHandlerSparseAlarmClockArray = new SparseArray();
  Bundle mIdleOptions;
  ArrayList<InFlight> mInFlight = new ArrayList();
  boolean mInteractive = true;
  long mInteractiveStartTime;
  InteractiveStateReceiver mInteractiveStateReceiver;
  boolean mIsEncryptStatus = false;
  boolean mIsPowerOffAlarmSet = false;
  long mLastAlarmDeliveryTime;
  final SparseLongArray mLastAllowWhileIdleDispatch = new SparseLongArray();
  long mLastTimeChangeClockTime;
  long mLastTimeChangeRealtime;
  boolean mLastWakeLockUnimportantForLogging;
  private long mLastWakeup;
  private long mLastWakeupSet;
  DeviceIdleController.LocalService mLocalDeviceIdleController;
  final Object mLock = new Object();
  final LocalLog mLog = new LocalLog("AlarmManager");
  long mMaxDelayTime = 0L;
  long mNativeData;
  private final SparseArray<AlarmManager.AlarmClockInfo> mNextAlarmClockForUser = new SparseArray();
  private boolean mNextAlarmClockMayChange;
  private long mNextNonWakeup;
  long mNextNonWakeupDeliveryTime;
  private long mNextRtcWakeup;
  Alarm mNextWakeFromIdle = null;
  private long mNextWakeup;
  long mNonInteractiveStartTime;
  long mNonInteractiveTime;
  int mNumDelayedAlarms = 0;
  int mNumTimeChanged;
  ArrayMap<String, Integer> mOperationCounts = new ArrayMap();
  private ArrayList<String> mPendingDelayOperation = new ArrayList();
  private ArrayMap<Alarm, String> mPendingDelayWakeupAlarms = new ArrayMap();
  Alarm mPendingIdleUntil = null;
  ArrayList<Alarm> mPendingNonWakeupAlarms = new ArrayList();
  private final SparseBooleanArray mPendingSendNextAlarmClockChangedForUser = new SparseBooleanArray();
  ArrayList<Alarm> mPendingWhileIdleAlarms = new ArrayList();
  final HashMap<String, PriorityClass> mPriorities = new HashMap();
  Random mRandom;
  final LinkedList<WakeupEvent> mRecentWakeups = new LinkedList();
  private final IBinder mService = new IAlarmManager.Stub()
  {
    protected void configLogTag(PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString, int paramAnonymousInt)
    {
      if (paramAnonymousInt >= paramAnonymousArrayOfString.length)
      {
        paramAnonymousPrintWriter.println("  Invalid argument!");
        return;
      }
      if ("on".equals(paramAnonymousArrayOfString[paramAnonymousInt]))
      {
        AlarmManagerService.localLOGV = true;
        AlarmManagerService.DEBUG_BATCH = true;
        AlarmManagerService.DEBUG_VALIDATE = true;
        AlarmManagerService.DEBUG_ALARM_CLOCK = true;
        AlarmManagerService.DEBUG_LISTENER_CALLBACK = true;
        return;
      }
      if ("off".equals(paramAnonymousArrayOfString[paramAnonymousInt]))
      {
        AlarmManagerService.localLOGV = false;
        AlarmManagerService.DEBUG_BATCH = false;
        AlarmManagerService.DEBUG_VALIDATE = false;
        AlarmManagerService.DEBUG_ALARM_CLOCK = false;
        AlarmManagerService.DEBUG_LISTENER_CALLBACK = false;
        return;
      }
      paramAnonymousPrintWriter.println("  Invalid argument!");
    }
    
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      if (AlarmManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramAnonymousPrintWriter.println("Permission Denial: can't dump AlarmManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      int i = 0;
      for (;;)
      {
        if (i < paramAnonymousArrayOfString.length)
        {
          paramAnonymousFileDescriptor = paramAnonymousArrayOfString[i];
          if ((paramAnonymousFileDescriptor != null) && (paramAnonymousFileDescriptor.length() > 0)) {
            break label108;
          }
        }
        label108:
        while (paramAnonymousFileDescriptor.charAt(0) != '-')
        {
          if ((i >= paramAnonymousArrayOfString.length) || (!"log".equals(paramAnonymousArrayOfString[i]))) {
            break;
          }
          configLogTag(paramAnonymousPrintWriter, paramAnonymousArrayOfString, i + 1);
          return;
        }
        i += 1;
        if ("-h".equals(paramAnonymousFileDescriptor))
        {
          paramAnonymousPrintWriter.println("alarm manager dump options:");
          paramAnonymousPrintWriter.println("  log  [on/off]");
          paramAnonymousPrintWriter.println("  Example:");
          paramAnonymousPrintWriter.println("  $adb shell dumpsys alarm log on");
          paramAnonymousPrintWriter.println("  $adb shell dumpsys alarm log off");
          return;
        }
        paramAnonymousPrintWriter.println("Unknown argument: " + paramAnonymousFileDescriptor + "; use -h for help");
      }
      AlarmManagerService.this.dumpImpl(paramAnonymousPrintWriter);
    }
    
    public AlarmManager.AlarmClockInfo getNextAlarmClock(int paramAnonymousInt)
    {
      paramAnonymousInt = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramAnonymousInt, false, false, "getNextAlarmClock", null);
      return AlarmManagerService.this.getNextAlarmClockImpl(paramAnonymousInt);
    }
    
    public long getNextWakeFromIdleTime()
    {
      return AlarmManagerService.this.getNextWakeFromIdleTimeImpl();
    }
    
    public void remove(PendingIntent paramAnonymousPendingIntent, IAlarmListener paramAnonymousIAlarmListener)
    {
      if (AlarmManagerService.localLOGV) {
        Slog.d("AlarmManager", "remove option = " + paramAnonymousPendingIntent);
      }
      if ((paramAnonymousPendingIntent == null) && (paramAnonymousIAlarmListener == null))
      {
        Slog.w("AlarmManager", "remove() with no intent or listener");
        return;
      }
      synchronized (AlarmManagerService.this.mLock)
      {
        AlarmManagerService.-wrap5(AlarmManagerService.this, paramAnonymousPendingIntent, paramAnonymousIAlarmListener);
        return;
      }
    }
    
    public void set(String paramAnonymousString1, int paramAnonymousInt1, long paramAnonymousLong1, long paramAnonymousLong2, long paramAnonymousLong3, int paramAnonymousInt2, PendingIntent paramAnonymousPendingIntent, IAlarmListener paramAnonymousIAlarmListener, String paramAnonymousString2, WorkSource paramAnonymousWorkSource, AlarmManager.AlarmClockInfo paramAnonymousAlarmClockInfo)
    {
      int j = Binder.getCallingUid();
      AlarmManagerService.this.mAppOps.checkPackage(j, paramAnonymousString1);
      if ((paramAnonymousLong3 != 0L) && (paramAnonymousIAlarmListener != null)) {
        throw new IllegalArgumentException("Repeating alarms cannot use AlarmReceivers");
      }
      if (paramAnonymousWorkSource != null) {
        AlarmManagerService.this.getContext().enforcePermission("android.permission.UPDATE_DEVICE_STATS", Binder.getCallingPid(), j, "AlarmManager.set");
      }
      int i = paramAnonymousInt2 & 0xFFFFFFF5;
      paramAnonymousInt2 = i;
      if (j != 1000) {
        paramAnonymousInt2 = i & 0xFFFFFFEF;
      }
      i = paramAnonymousInt2;
      if (paramAnonymousLong2 == 0L) {
        i = paramAnonymousInt2 | 0x1;
      }
      if (paramAnonymousAlarmClockInfo != null) {
        if (paramAnonymousString1.equals("com.iflytek.cmcc")) {
          paramAnonymousInt2 = i | 0x1;
        }
      }
      for (;;)
      {
        i = paramAnonymousInt2;
        if ((paramAnonymousInt2 & 0x4) != 0)
        {
          i = paramAnonymousInt2;
          if (AlarmManagerService.-get1(AlarmManagerService.this).contains(paramAnonymousString1)) {
            i = paramAnonymousInt2 & 0xFFFFFFFB;
          }
        }
        AlarmManagerService.this.setImpl(paramAnonymousInt1, paramAnonymousLong1, paramAnonymousLong2, paramAnonymousLong3, paramAnonymousPendingIntent, paramAnonymousIAlarmListener, paramAnonymousString2, i, paramAnonymousWorkSource, paramAnonymousAlarmClockInfo, j, paramAnonymousString1);
        return;
        paramAnonymousInt2 = i | 0x3;
        continue;
        paramAnonymousInt2 = i;
        if (paramAnonymousWorkSource == null) {
          if (j >= 10000)
          {
            paramAnonymousInt2 = i;
            if (Arrays.binarySearch(AlarmManagerService.this.mDeviceIdleUserWhitelist, UserHandle.getAppId(j)) < 0) {}
          }
          else
          {
            paramAnonymousInt2 = i;
            if (!paramAnonymousString1.equals("com.sankuai.meituan.merchant")) {
              paramAnonymousInt2 = (i | 0x8) & 0xFFFFFFFB;
            }
          }
        }
      }
    }
    
    public void setBlackAlarm(List<String> paramAnonymousList)
    {
      AlarmManagerService.mBlackAlarmOperation.clear();
      AlarmManagerService.mBlackAlarmOperation.addAll(paramAnonymousList);
      Slog.v("AlarmManager", "mBlackAlarmOperation = " + AlarmManagerService.mBlackAlarmOperation);
    }
    
    public void setBlockAlarmUid(String paramAnonymousString, boolean paramAnonymousBoolean, int paramAnonymousInt)
    {
      AlarmManagerService.this.setBlockAlarmUidLock(paramAnonymousString, paramAnonymousBoolean, paramAnonymousInt);
    }
    
    public boolean setTime(long paramAnonymousLong)
    {
      boolean bool = false;
      AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.SET_TIME", "setTime");
      if (AlarmManagerService.this.mNativeData == 0L)
      {
        Slog.w("AlarmManager", "Not setting time since no alarm driver is available.");
        return false;
      }
      synchronized (AlarmManagerService.this.mLock)
      {
        if (AlarmManagerService.localLOGV) {
          Slog.d("AlarmManager", "setKernelTime  setTime = " + paramAnonymousLong);
        }
        int i = AlarmManagerService.-wrap2(AlarmManagerService.this, AlarmManagerService.this.mNativeData, paramAnonymousLong);
        if (i == 0) {
          bool = true;
        }
        return bool;
      }
    }
    
    public void setTimeZone(String paramAnonymousString)
    {
      AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.SET_TIME_ZONE", "setTimeZone");
      long l = Binder.clearCallingIdentity();
      try
      {
        AlarmManagerService.this.setTimeZoneImpl(paramAnonymousString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void updateBlockedUids(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        AlarmManagerService.-get4(AlarmManagerService.this).processBlockedUids(paramAnonymousInt, paramAnonymousBoolean, AlarmManagerService.this.mWakeLock);
        return;
      }
    }
  };
  long mStartCurrentDelayTime;
  PendingIntent mTimeTickSender;
  private final SparseArray<AlarmManager.AlarmClockInfo> mTmpSparseAlarmClockArray = new SparseArray();
  long mTotalDelayTime = 0L;
  ArrayMap<String, ArrayList<String>> mUidOperation = new ArrayMap();
  private UninstallReceiver mUninstallReceiver;
  PowerManager.WakeLock mWakeLock;
  private QCNsrmAlarmExtension qcNsrmExt = new QCNsrmAlarmExtension(this);
  
  static
  {
    boolean bool2 = true;
    localLOGV = false;
    if (!localLOGV)
    {
      bool1 = false;
      DEBUG_BATCH = bool1;
      if (localLOGV) {
        break label147;
      }
      bool1 = false;
      label26:
      DEBUG_VALIDATE = bool1;
      if (localLOGV) {
        break label152;
      }
    }
    label147:
    label152:
    for (boolean bool1 = false;; bool1 = true)
    {
      DEBUG_ALARM_CLOCK = bool1;
      bool1 = bool2;
      if (!localLOGV) {
        bool1 = false;
      }
      DEBUG_LISTENER_CALLBACK = bool1;
      DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
      sIncreasingTimeOrder = new IncreasingTimeOrder();
      WAKEUP_STATS = DEBUG_ONEPLUS;
      NEXT_ALARM_CLOCK_CHANGED_INTENT = new Intent("android.app.action.NEXT_ALARM_CLOCK_CHANGED").addFlags(536870912);
      sBatchOrder = new BatchTimeOrder();
      mFrozeenUids = new ArrayMap();
      mFrozeenTimeUids = new ArrayMap();
      mBlackAlarmOperation = new ArrayList();
      isDozeChangeSupport = false;
      return;
      bool1 = true;
      break;
      bool1 = true;
      break label26;
    }
  }
  
  public AlarmManagerService(Context paramContext)
  {
    super(paramContext);
  }
  
  static boolean addBatchLocked(ArrayList<Batch> paramArrayList, Batch paramBatch)
  {
    boolean bool = false;
    int j = Collections.binarySearch(paramArrayList, paramBatch, sBatchOrder);
    int i = j;
    if (j < 0) {
      i = 0 - j - 1;
    }
    paramArrayList.add(i, paramBatch);
    if (i == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean checkAdjustAlarmPolicy(String paramString1, String paramString2, long paramLong)
  {
    return true;
  }
  
  private boolean checkAlarmOperation(String paramString)
  {
    if ((paramString != null) && ((paramString.contains("AlarmTaskSchedule")) || (paramString.contains("com.igexin.sdk.action")) || (paramString.contains("AlarmTaskScheduleBak")) || (paramString.contains("cn.jpush.android.service.AlarmReceiver")) || (paramString.contains("intent.action.COCKROACH"))))
    {
      if (DEBUG_ONEPLUS) {
        Slog.v("AlarmManager", "sending operation black alarm " + paramString);
      }
      return true;
    }
    if ((paramString != null) && (isContains(paramString)))
    {
      if (DEBUG_ONEPLUS) {
        Slog.v("AlarmManager", "sending operation black alarm " + paramString);
      }
      return true;
    }
    return false;
  }
  
  private boolean checkDelayAlarm(Alarm paramAlarm, Batch paramBatch, long paramLong1, long paramLong2)
  {
    if (paramAlarm.operation != null)
    {
      String str1 = paramAlarm.packageName;
      paramBatch = (Integer)mFrozeenUids.get(str1);
      if ((paramBatch != null) && (paramBatch.intValue() > 0))
      {
        String str2 = paramAlarm.statsTag + str1;
        if ((!this.mPendingDelayOperation.contains(str2)) && (checkAlarmOperation(paramAlarm.statsTag)) && (checkAdjustAlarmPolicy(str1, str2, paramLong1)))
        {
          long l2 = currentNonWakeupDelayLocked(paramLong1);
          if ((paramAlarm.type == 1) || (paramAlarm.type == 0)) {}
          for (int i = 1;; i = 0)
          {
            long l1 = l2;
            if (i != 0) {
              l1 = l2 + (paramLong2 - paramLong1);
            }
            paramAlarm.origWhen = l1;
            paramAlarm.when = l1;
            paramAlarm.whenElapsed = l2;
            removeLocked(paramAlarm.operation, paramAlarm.listener);
            setImplLocked(paramAlarm, false, true);
            if (DEBUG_ONEPLUS) {
              Slog.v("AlarmManager", "delay third part wakeup alarm =  operation =" + paramAlarm.statsTag);
            }
            this.mPendingDelayOperation.remove(str2);
            this.mPendingDelayOperation.add(str2);
            this.mPendingDelayWakeupAlarms.remove(paramAlarm);
            this.mPendingDelayWakeupAlarms.put(paramAlarm, str1);
            paramBatch = (ArrayList)this.mUidOperation.get(str1);
            paramAlarm = paramBatch;
            if (paramBatch == null)
            {
              paramAlarm = new ArrayList();
              this.mUidOperation.put(str1, paramAlarm);
            }
            paramAlarm.remove(str2);
            paramAlarm.add(str2);
            return true;
            if (paramAlarm.type == 5) {
              break;
            }
          }
        }
        paramBatch = (ArrayList)this.mUidOperation.get(str1);
        if (paramBatch != null) {
          paramBatch.remove(str2);
        }
        this.mPendingDelayWakeupAlarms.remove(paramAlarm);
        this.mPendingDelayOperation.remove(str2);
      }
      return false;
    }
    return false;
  }
  
  private native void clear(long paramLong1, int paramInt, long paramLong2, long paramLong3);
  
  private native void close(long paramLong);
  
  static long convertToElapsed(long paramLong, int paramInt)
  {
    int j = 1;
    int i = j;
    if (paramInt != 1)
    {
      if (paramInt != 0) {
        break label39;
      }
      i = j;
    }
    for (;;)
    {
      long l = paramLong;
      if (i != 0) {
        l = paramLong - (System.currentTimeMillis() - SystemClock.elapsedRealtime());
      }
      return l;
      label39:
      i = j;
      if (paramInt != 5) {
        i = 0;
      }
    }
  }
  
  private static final void dumpAlarmList(PrintWriter paramPrintWriter, ArrayList<Alarm> paramArrayList, String paramString, long paramLong1, long paramLong2, SimpleDateFormat paramSimpleDateFormat)
  {
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      Alarm localAlarm = (Alarm)paramArrayList.get(i);
      String str = labelForType(localAlarm.type);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print(str);
      paramPrintWriter.print(" #");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": ");
      paramPrintWriter.println(localAlarm);
      localAlarm.dump(paramPrintWriter, paramString + "  ", paramLong2, paramLong1, paramSimpleDateFormat);
      i -= 1;
    }
  }
  
  private static final void dumpAlarmList(PrintWriter paramPrintWriter, ArrayList<Alarm> paramArrayList, String paramString1, String paramString2, long paramLong1, long paramLong2, SimpleDateFormat paramSimpleDateFormat)
  {
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      Alarm localAlarm = (Alarm)paramArrayList.get(i);
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print(" #");
      paramPrintWriter.print(i);
      paramPrintWriter.print(": ");
      paramPrintWriter.println(localAlarm);
      localAlarm.dump(paramPrintWriter, paramString1 + "  ", paramLong1, paramLong2, paramSimpleDateFormat);
      i -= 1;
    }
  }
  
  private Batch findFirstRtcWakeupBatchLocked()
  {
    int j = this.mAlarmBatches.size();
    int i = 0;
    while (i < j)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      if (localBatch.isRtcPowerOffWakeup()) {
        return localBatch;
      }
      i += 1;
    }
    return null;
  }
  
  private Batch findFirstWakeupBatchLocked()
  {
    int j = this.mAlarmBatches.size();
    int i = 0;
    while (i < j)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      if (localBatch.hasWakeups()) {
        return localBatch;
      }
      i += 1;
    }
    return null;
  }
  
  private static String formatNextAlarm(Context paramContext, AlarmManager.AlarmClockInfo paramAlarmClockInfo, int paramInt)
  {
    if (DateFormat.is24HourFormat(paramContext, paramInt)) {}
    for (paramContext = "EHm";; paramContext = "Ehma")
    {
      paramContext = DateFormat.getBestDateTimePattern(Locale.getDefault(), paramContext);
      if (paramAlarmClockInfo != null) {
        break;
      }
      return "";
    }
    return DateFormat.format(paramContext, paramAlarmClockInfo.getTriggerTime()).toString();
  }
  
  static int fuzzForDuration(long paramLong)
  {
    if (paramLong < 900000L) {
      return (int)paramLong;
    }
    if (paramLong < 5400000L) {
      return 900000;
    }
    return 1800000;
  }
  
  private final BroadcastStats getStatsLocked(int paramInt, String paramString)
  {
    Object localObject2 = (ArrayMap)this.mBroadcastStats.get(paramInt);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new ArrayMap();
      this.mBroadcastStats.put(paramInt, localObject1);
    }
    BroadcastStats localBroadcastStats = (BroadcastStats)((ArrayMap)localObject1).get(paramString);
    localObject2 = localBroadcastStats;
    if (localBroadcastStats == null)
    {
      localObject2 = new BroadcastStats(paramInt, paramString);
      ((ArrayMap)localObject1).put(paramString, localObject2);
    }
    return (BroadcastStats)localObject2;
  }
  
  private final BroadcastStats getStatsLocked(PendingIntent paramPendingIntent)
  {
    String str = paramPendingIntent.getCreatorPackage();
    return getStatsLocked(paramPendingIntent.getCreatorUid(), str);
  }
  
  private native long init();
  
  private static final String labelForType(int paramInt)
  {
    switch (paramInt)
    {
    case 4: 
    default: 
      return "--unknown--";
    case 1: 
      return "RTC";
    case 0: 
      return "RTC_WAKEUP";
    case 3: 
      return "ELAPSED";
    case 2: 
      return "ELAPSED_WAKEUP";
    }
    return "RTC_POWEROFF_WAKEUP";
  }
  
  private void logBatchesLocked(SimpleDateFormat paramSimpleDateFormat)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream);
    long l1 = System.currentTimeMillis();
    long l2 = SystemClock.elapsedRealtime();
    int j = this.mAlarmBatches.size();
    int i = 0;
    while (i < j)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      localPrintWriter.append("Batch ");
      localPrintWriter.print(i);
      localPrintWriter.append(": ");
      localPrintWriter.println(localBatch);
      dumpAlarmList(localPrintWriter, localBatch.alarms, "  ", l2, l1, paramSimpleDateFormat);
      localPrintWriter.flush();
      Slog.v("AlarmManager", localByteArrayOutputStream.toString());
      localByteArrayOutputStream.reset();
      i += 1;
    }
  }
  
  static long maxTriggerTime(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong3 == 0L) {}
    for (paramLong1 = paramLong2 - paramLong1;; paramLong1 = paramLong3)
    {
      paramLong3 = paramLong1;
      if (paramLong1 < 10000L) {
        paramLong3 = 0L;
      }
      return (paramLong3 * 0.75D) + paramLong2;
    }
  }
  
  private void removeLocked(PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener)
  {
    boolean bool = false;
    int i = this.mAlarmBatches.size() - 1;
    while (i >= 0)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      ArrayList localArrayList = localBatch.alarms;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        Alarm localAlarm = (Alarm)localArrayList.get(j);
        if ((localAlarm.type == 5) && (localAlarm.operation.equals(paramPendingIntent)))
        {
          long l1 = localAlarm.when / 1000L;
          long l2 = localAlarm.when;
          clear(this.mNativeData, localAlarm.type, l1, l2 % 1000L * 1000L * 1000L);
          this.mNextRtcWakeup = 0L;
        }
        j -= 1;
      }
      bool |= localBatch.remove(paramPendingIntent, paramIAlarmListener);
      if (localBatch.size() == 0) {
        this.mAlarmBatches.remove(i);
      }
      i -= 1;
    }
    i = this.mPendingWhileIdleAlarms.size() - 1;
    while (i >= 0)
    {
      if (((Alarm)this.mPendingWhileIdleAlarms.get(i)).matches(paramPendingIntent, paramIAlarmListener)) {
        this.mPendingWhileIdleAlarms.remove(i);
      }
      i -= 1;
    }
    if (bool)
    {
      if (DEBUG_BATCH) {
        Slog.v("AlarmManager", "remove(operation) changed bounds; rebatching");
      }
      i = 0;
      bool = i;
      if (this.mPendingIdleUntil != null)
      {
        bool = i;
        if (this.mPendingIdleUntil.matches(paramPendingIntent, paramIAlarmListener))
        {
          this.mPendingIdleUntil = null;
          bool = true;
        }
      }
      if ((this.mNextWakeFromIdle != null) && (this.mNextWakeFromIdle.matches(paramPendingIntent, paramIAlarmListener))) {
        this.mNextWakeFromIdle = null;
      }
      rebatchAllAlarmsLocked(true);
      if (bool) {
        restorePendingWhileIdleAlarmsLocked();
      }
      updateNextAlarmClockLocked();
    }
  }
  
  private void sendNextAlarmClockChanged()
  {
    SparseArray localSparseArray = this.mHandlerSparseAlarmClockArray;
    localSparseArray.clear();
    if (DEBUG_ALARM_CLOCK) {
      Slog.w("AlarmManager", "sendNextAlarmClockChanged begin");
    }
    synchronized (this.mLock)
    {
      int j = this.mPendingSendNextAlarmClockChangedForUser.size();
      int i = 0;
      int k;
      while (i < j)
      {
        k = this.mPendingSendNextAlarmClockChangedForUser.keyAt(i);
        localSparseArray.append(k, (AlarmManager.AlarmClockInfo)this.mNextAlarmClockForUser.get(k));
        i += 1;
      }
      this.mPendingSendNextAlarmClockChangedForUser.clear();
      j = localSparseArray.size();
      i = 0;
      if (i < j)
      {
        k = localSparseArray.keyAt(i);
        ??? = (AlarmManager.AlarmClockInfo)localSparseArray.valueAt(i);
        Settings.System.putStringForUser(getContext().getContentResolver(), "next_alarm_formatted", formatNextAlarm(getContext(), (AlarmManager.AlarmClockInfo)???, k), k);
        getContext().sendBroadcastAsUser(NEXT_ALARM_CLOCK_CHANGED_INTENT, new UserHandle(k));
        i += 1;
      }
    }
    if (DEBUG_ALARM_CLOCK) {
      Slog.w("AlarmManager", "sendNextAlarmClockChanged end");
    }
  }
  
  private native void set(long paramLong1, int paramInt, long paramLong2, long paramLong3);
  
  private void setImplLocked(int paramInt1, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, String paramString1, int paramInt2, boolean paramBoolean, WorkSource paramWorkSource, AlarmManager.AlarmClockInfo paramAlarmClockInfo, int paramInt3, String paramString2)
  {
    paramString1 = new Alarm(paramInt1, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramPendingIntent, paramIAlarmListener, paramString1, paramWorkSource, paramInt2, paramAlarmClockInfo, paramInt3, paramString2);
    try
    {
      if (ActivityManagerNative.getDefault().getAppStartMode(paramInt3, paramString2) == 2)
      {
        Slog.w("AlarmManager", "Not setting alarm from " + paramInt3 + ":" + paramString1 + " -- package not allowed to start");
        return;
      }
    }
    catch (RemoteException paramWorkSource)
    {
      if ((paramPendingIntent != null) && (this.mPendingDelayOperation.contains(paramString1.statsTag + paramString1.packageName)))
      {
        if (DEBUG_ONEPLUS) {
          Slog.v("AlarmManager", " cancel setImplLocked  beause of screenOffAdjust tag =" + paramString1.statsTag);
        }
        paramLong2 = currentNonWakeupDelayLocked(SystemClock.elapsedRealtime());
        if ((paramInt1 == 1) || (paramInt1 == 0)) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramLong1 = paramLong2;
          if (paramInt1 != 0) {
            paramLong1 = paramLong2 + (System.currentTimeMillis() - SystemClock.elapsedRealtime());
          }
          paramString1.origWhen = paramLong1;
          paramString1.when = paramLong1;
          paramString1.whenElapsed = paramLong2;
          removeLocked(paramPendingIntent, paramIAlarmListener);
          setImplLocked(paramString1, false, paramBoolean);
          return;
          if (paramInt1 == 5) {
            break;
          }
        }
      }
      removeLocked(paramPendingIntent, paramIAlarmListener);
      setImplLocked(paramString1, false, paramBoolean);
    }
  }
  
  private void setImplLocked(Alarm paramAlarm, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    label200:
    Batch localBatch;
    if ((paramAlarm.flags & 0x10) != 0)
    {
      if ((this.mNextWakeFromIdle != null) && (paramAlarm.whenElapsed > this.mNextWakeFromIdle.whenElapsed))
      {
        l = this.mNextWakeFromIdle.whenElapsed;
        paramAlarm.maxWhenElapsed = l;
        paramAlarm.whenElapsed = l;
        paramAlarm.when = l;
      }
      long l = SystemClock.elapsedRealtime();
      i = fuzzForDuration(paramAlarm.whenElapsed - l);
      if (i > 0)
      {
        if (this.mRandom == null) {
          this.mRandom = new Random();
        }
        i = this.mRandom.nextInt(i);
        paramAlarm.whenElapsed -= i;
        l = paramAlarm.whenElapsed;
        paramAlarm.maxWhenElapsed = l;
        paramAlarm.when = l;
      }
      if (DEBUG_BATCH) {
        Slog.d("AlarmManager", "a.whenElapsed =" + paramAlarm.whenElapsed + "  a.flags= " + paramAlarm.flags);
      }
      if ((paramAlarm.flags & 0x1) == 0) {
        break label378;
      }
      i = -1;
      if (DEBUG_BATCH) {
        Slog.d("AlarmManager", " whichBatch = " + i);
      }
      if (i >= 0) {
        break label395;
      }
      localBatch = new Batch(paramAlarm);
      addBatchLocked(this.mAlarmBatches, localBatch);
      label259:
      if (paramAlarm.alarmClock != null) {
        this.mNextAlarmClockMayChange = true;
      }
      j = 0;
      if ((paramAlarm.flags & 0x10) == 0) {
        break label484;
      }
      this.mPendingIdleUntil = paramAlarm;
      this.mConstants.updateAllowWhileIdleMinTimeLocked();
      i = 1;
      label299:
      if (!paramBoolean1)
      {
        j = i;
        if (DEBUG_VALIDATE)
        {
          j = i;
          if (paramBoolean2) {
            if (!validateConsistencyLocked()) {
              break label545;
            }
          }
        }
      }
    }
    for (int j = i;; j = 0)
    {
      if (j != 0) {
        rebatchAllAlarmsLocked(false);
      }
      rescheduleKernelAlarmsLocked();
      updateNextAlarmClockLocked();
      return;
      if ((this.mPendingIdleUntil == null) || ((paramAlarm.flags & 0xE) != 0)) {
        break;
      }
      this.mPendingWhileIdleAlarms.add(paramAlarm);
      return;
      label378:
      i = attemptCoalesceLocked(paramAlarm.whenElapsed, paramAlarm.maxWhenElapsed);
      break label200;
      label395:
      localBatch = (Batch)this.mAlarmBatches.get(i);
      if (DEBUG_BATCH) {
        Slog.d("AlarmManager", " alarm = " + paramAlarm + " add to " + localBatch);
      }
      if (!localBatch.add(paramAlarm)) {
        break label259;
      }
      this.mAlarmBatches.remove(i);
      addBatchLocked(this.mAlarmBatches, localBatch);
      break label259;
      label484:
      i = j;
      if ((paramAlarm.flags & 0x2) == 0) {
        break label299;
      }
      if (this.mNextWakeFromIdle != null)
      {
        i = j;
        if (this.mNextWakeFromIdle.whenElapsed <= paramAlarm.whenElapsed) {
          break label299;
        }
      }
      this.mNextWakeFromIdle = paramAlarm;
      i = j;
      if (this.mPendingIdleUntil == null) {
        break label299;
      }
      i = 1;
      break label299;
      label545:
      Slog.v("AlarmManager", "Tipping-point operation: type=" + paramAlarm.type + " when=" + paramAlarm.when + " when(hex)=" + Long.toHexString(paramAlarm.when) + " whenElapsed=" + paramAlarm.whenElapsed + " maxWhenElapsed=" + paramAlarm.maxWhenElapsed + " interval=" + paramAlarm.repeatInterval + " op=" + paramAlarm.operation + " flags=0x" + Integer.toHexString(paramAlarm.flags));
      rebatchAllAlarmsLocked(false);
    }
  }
  
  private native int setKernelTime(long paramLong1, long paramLong2);
  
  private native int setKernelTimezone(long paramLong, int paramInt);
  
  private void setLocked(int paramInt, long paramLong)
  {
    if (this.mNativeData != 0L)
    {
      long l1;
      if (paramLong < 0L) {
        l1 = 0L;
      }
      for (long l2 = 0L;; l2 = paramLong % 1000L * 1000L * 1000L)
      {
        if (localLOGV) {
          Slog.d("AlarmManager", "set alarm to RTC " + paramLong);
        }
        set(this.mNativeData, paramInt, l1, l2);
        return;
        l1 = paramLong / 1000L;
      }
    }
    if (localLOGV) {
      Slog.d("AlarmManager", "the mNativeData from RTC is abnormal,  mNativeData = " + this.mNativeData);
    }
    Message localMessage = Message.obtain();
    localMessage.what = 1;
    this.mHandler.removeMessages(1);
    this.mHandler.sendMessageAtTime(localMessage, paramLong);
  }
  
  private void updateNextAlarmClockLocked()
  {
    if (!this.mNextAlarmClockMayChange) {
      return;
    }
    this.mNextAlarmClockMayChange = false;
    SparseArray localSparseArray = this.mTmpSparseAlarmClockArray;
    localSparseArray.clear();
    int k = this.mAlarmBatches.size();
    int i = 0;
    Object localObject;
    while (i < k)
    {
      localObject = ((Batch)this.mAlarmBatches.get(i)).alarms;
      int m = ((ArrayList)localObject).size();
      j = 0;
      if (j < m)
      {
        Alarm localAlarm = (Alarm)((ArrayList)localObject).get(j);
        int n;
        AlarmManager.AlarmClockInfo localAlarmClockInfo;
        if (localAlarm.alarmClock != null)
        {
          n = UserHandle.getUserId(localAlarm.uid);
          localAlarmClockInfo = (AlarmManager.AlarmClockInfo)this.mNextAlarmClockForUser.get(n);
          if (DEBUG_ALARM_CLOCK) {
            Log.v("AlarmManager", "Found AlarmClockInfo " + localAlarm.alarmClock + " at " + formatNextAlarm(getContext(), localAlarm.alarmClock, n) + " for user " + n);
          }
          if (localSparseArray.get(n) != null) {
            break label212;
          }
          localSparseArray.put(n, localAlarm.alarmClock);
        }
        for (;;)
        {
          j += 1;
          break;
          label212:
          if ((localAlarm.alarmClock.equals(localAlarmClockInfo)) && (localAlarmClockInfo.getTriggerTime() <= ((AlarmManager.AlarmClockInfo)localSparseArray.get(n)).getTriggerTime())) {
            localSparseArray.put(n, localAlarmClockInfo);
          }
        }
      }
      i += 1;
    }
    int j = localSparseArray.size();
    i = 0;
    while (i < j)
    {
      localObject = (AlarmManager.AlarmClockInfo)localSparseArray.valueAt(i);
      k = localSparseArray.keyAt(i);
      if (!((AlarmManager.AlarmClockInfo)localObject).equals((AlarmManager.AlarmClockInfo)this.mNextAlarmClockForUser.get(k))) {
        updateNextAlarmInfoForUserLocked(k, (AlarmManager.AlarmClockInfo)localObject);
      }
      i += 1;
    }
    i = this.mNextAlarmClockForUser.size() - 1;
    while (i >= 0)
    {
      j = this.mNextAlarmClockForUser.keyAt(i);
      if (localSparseArray.get(j) == null) {
        updateNextAlarmInfoForUserLocked(j, null);
      }
      i -= 1;
    }
  }
  
  private void updateNextAlarmInfoForUserLocked(int paramInt, AlarmManager.AlarmClockInfo paramAlarmClockInfo)
  {
    if (paramAlarmClockInfo != null)
    {
      if (DEBUG_ALARM_CLOCK) {
        Log.v("AlarmManager", "Next AlarmClockInfoForUser(" + paramInt + "): " + formatNextAlarm(getContext(), paramAlarmClockInfo, paramInt));
      }
      this.mNextAlarmClockForUser.put(paramInt, paramAlarmClockInfo);
    }
    for (;;)
    {
      this.mPendingSendNextAlarmClockChangedForUser.put(paramInt, true);
      this.mHandler.removeMessages(2);
      this.mHandler.sendEmptyMessage(2);
      return;
      if (DEBUG_ALARM_CLOCK) {
        Log.v("AlarmManager", "Next AlarmClockInfoForUser(" + paramInt + "): None");
      }
      this.mNextAlarmClockForUser.remove(paramInt);
    }
  }
  
  private boolean validateConsistencyLocked()
  {
    if (DEBUG_VALIDATE)
    {
      long l = Long.MIN_VALUE;
      int j = this.mAlarmBatches.size();
      int i = 0;
      while (i < j)
      {
        Batch localBatch = (Batch)this.mAlarmBatches.get(i);
        if (localBatch.start >= l)
        {
          l = localBatch.start;
          i += 1;
        }
        else
        {
          Slog.e("AlarmManager", "CONSISTENCY FAILURE: Batch " + i + " is out of order");
          logBatchesLocked(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
          return false;
        }
      }
    }
    return true;
  }
  
  private native int waitForAlarm(long paramLong);
  
  int attemptCoalesceLocked(long paramLong1, long paramLong2)
  {
    int j = this.mAlarmBatches.size();
    int i = 0;
    while (i < j)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      if (((localBatch.flags & 0x1) == 0) && (localBatch.canHold(paramLong1, paramLong2))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  void calculateDeliveryPriorities(ArrayList<Alarm> paramArrayList)
  {
    int k = paramArrayList.size();
    int j = 0;
    if (j < k)
    {
      Alarm localAlarm = (Alarm)paramArrayList.get(j);
      int i;
      label54:
      Object localObject2;
      String str;
      if ((localAlarm.operation != null) && ("android.intent.action.TIME_TICK".equals(localAlarm.operation.getIntent().getAction())))
      {
        i = 0;
        localObject2 = localAlarm.priorityClass;
        if (localAlarm.operation == null) {
          break label199;
        }
        str = localAlarm.operation.getCreatorPackage();
        label79:
        Object localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = (PriorityClass)this.mPriorities.get(str);
        }
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = new PriorityClass();
          localAlarm.priorityClass = ((PriorityClass)localObject2);
          this.mPriorities.put(str, localObject2);
        }
        localAlarm.priorityClass = ((PriorityClass)localObject2);
        if (((PriorityClass)localObject2).seq == this.mCurrentSeq) {
          break label209;
        }
        ((PriorityClass)localObject2).priority = i;
        ((PriorityClass)localObject2).seq = this.mCurrentSeq;
      }
      for (;;)
      {
        j += 1;
        break;
        if (localAlarm.wakeup)
        {
          i = 1;
          break label54;
        }
        i = 2;
        break label54;
        label199:
        str = localAlarm.packageName;
        break label79;
        label209:
        if (i < ((PriorityClass)localObject2).priority) {
          ((PriorityClass)localObject2).priority = i;
        }
      }
    }
  }
  
  boolean checkAllowNonWakeupDelayLocked(long paramLong)
  {
    boolean bool = false;
    if (this.mInteractive) {
      return false;
    }
    if (this.mLastAlarmDeliveryTime <= 0L) {
      return false;
    }
    if ((this.mPendingNonWakeupAlarms.size() > 0) && (this.mNextNonWakeupDeliveryTime < paramLong)) {
      return false;
    }
    if (paramLong - this.mLastAlarmDeliveryTime <= currentNonWakeupFuzzLocked(paramLong)) {
      bool = true;
    }
    return bool;
  }
  
  long currentNonWakeupDelayLocked(long paramLong)
  {
    int j = (int)((paramLong - this.mInteractiveStartTime) / 600000L) + 1;
    int i = j;
    if (j <= 0) {
      i = 1;
    }
    return this.mInteractiveStartTime + i * 10 * 60 * 1000L;
  }
  
  long currentNonWakeupFuzzLocked(long paramLong)
  {
    paramLong -= this.mNonInteractiveStartTime;
    if (paramLong < 300000L) {
      return 120000L;
    }
    if (paramLong < 1800000L) {
      return 900000L;
    }
    return 3600000L;
  }
  
  void deliverAlarmsLocked(ArrayList<Alarm> paramArrayList, long paramLong)
  {
    this.mLastAlarmDeliveryTime = paramLong;
    int i = 0;
    Alarm localAlarm;
    if (i < paramArrayList.size())
    {
      localAlarm = (Alarm)paramArrayList.get(i);
      if ((localAlarm.flags & 0x4) == 0) {
        break label197;
      }
    }
    label197:
    for (boolean bool = true;; bool = false)
    {
      try
      {
        if (localLOGV) {
          Slog.v("AlarmManager", "sending alarm " + localAlarm);
        }
        int j;
        if ((localAlarm.workSource != null) && (localAlarm.workSource.size() > 0)) {
          j = 0;
        }
        while (j < localAlarm.workSource.size())
        {
          ActivityManagerNative.noteAlarmStart(localAlarm.operation, localAlarm.workSource.get(j), localAlarm.statsTag);
          j += 1;
          continue;
          ActivityManagerNative.noteAlarmStart(localAlarm.operation, localAlarm.uid, localAlarm.statsTag);
        }
        this.mDeliveryTracker.deliverLocked(localAlarm, paramLong, bool);
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;)
        {
          Slog.w("AlarmManager", "Failure sending alarm.", localRuntimeException);
        }
      }
      i += 1;
      break;
      return;
    }
  }
  
  void dumpImpl(PrintWriter paramPrintWriter)
  {
    long l2;
    long l3;
    SimpleDateFormat localSimpleDateFormat;
    long l1;
    for (;;)
    {
      synchronized (this.mLock)
      {
        paramPrintWriter.println("Current Alarm Manager state:");
        this.mConstants.dump(paramPrintWriter);
        paramPrintWriter.println();
        l2 = System.currentTimeMillis();
        l3 = SystemClock.elapsedRealtime();
        localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paramPrintWriter.print("  nowRTC=");
        paramPrintWriter.print(l2);
        paramPrintWriter.print("=");
        paramPrintWriter.print(localSimpleDateFormat.format(new Date(l2)));
        paramPrintWriter.print(" nowELAPSED=");
        paramPrintWriter.print(l3);
        paramPrintWriter.println();
        paramPrintWriter.print("  mLastTimeChangeClockTime=");
        paramPrintWriter.print(this.mLastTimeChangeClockTime);
        paramPrintWriter.print("=");
        paramPrintWriter.println(localSimpleDateFormat.format(new Date(this.mLastTimeChangeClockTime)));
        paramPrintWriter.print("  mLastTimeChangeRealtime=");
        TimeUtils.formatDuration(this.mLastTimeChangeRealtime, paramPrintWriter);
        paramPrintWriter.println();
        if (!this.mInteractive)
        {
          paramPrintWriter.print("  Time since non-interactive: ");
          TimeUtils.formatDuration(l3 - this.mNonInteractiveStartTime, paramPrintWriter);
          paramPrintWriter.println();
          paramPrintWriter.print("  Max wakeup delay: ");
          TimeUtils.formatDuration(currentNonWakeupFuzzLocked(l3), paramPrintWriter);
          paramPrintWriter.println();
          paramPrintWriter.print("  Time since last dispatch: ");
          TimeUtils.formatDuration(l3 - this.mLastAlarmDeliveryTime, paramPrintWriter);
          paramPrintWriter.println();
          paramPrintWriter.print("  Next non-wakeup delivery time: ");
          TimeUtils.formatDuration(l3 - this.mNextNonWakeupDeliveryTime, paramPrintWriter);
          paramPrintWriter.println();
        }
        l1 = this.mNextWakeup;
        long l4 = this.mNextNonWakeup;
        paramPrintWriter.print("  Next non-wakeup alarm: ");
        TimeUtils.formatDuration(this.mNextNonWakeup, l3, paramPrintWriter);
        paramPrintWriter.print(" = ");
        paramPrintWriter.println(localSimpleDateFormat.format(new Date(l4 + (l2 - l3))));
        paramPrintWriter.print("  Next wakeup: ");
        TimeUtils.formatDuration(this.mNextWakeup, l3, paramPrintWriter);
        paramPrintWriter.print(" = ");
        paramPrintWriter.println(localSimpleDateFormat.format(new Date(l1 + (l2 - l3))));
        paramPrintWriter.print("  Last wakeup: ");
        TimeUtils.formatDuration(this.mLastWakeup, l3, paramPrintWriter);
        paramPrintWriter.print(" set at ");
        TimeUtils.formatDuration(this.mLastWakeupSet, l3, paramPrintWriter);
        paramPrintWriter.println();
        paramPrintWriter.print("  Num time change events: ");
        paramPrintWriter.println(this.mNumTimeChanged);
        paramPrintWriter.println("  mDeviceIdleUserWhitelist=" + Arrays.toString(this.mDeviceIdleUserWhitelist));
        paramPrintWriter.println();
        paramPrintWriter.println("  Next alarm clock information: ");
        localObject2 = new TreeSet();
        i = 0;
        if (i >= this.mNextAlarmClockForUser.size()) {
          break label2173;
        }
        ((TreeSet)localObject2).add(Integer.valueOf(this.mNextAlarmClockForUser.keyAt(i)));
        i += 1;
        continue;
        if (i < this.mPendingSendNextAlarmClockChangedForUser.size())
        {
          ((TreeSet)localObject2).add(Integer.valueOf(this.mPendingSendNextAlarmClockChangedForUser.keyAt(i)));
          i += 1;
          continue;
        }
        localObject2 = ((Iterable)localObject2).iterator();
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        i = ((Integer)((Iterator)localObject2).next()).intValue();
        localObject3 = (AlarmManager.AlarmClockInfo)this.mNextAlarmClockForUser.get(i);
        if (localObject3 != null)
        {
          l1 = ((AlarmManager.AlarmClockInfo)localObject3).getTriggerTime();
          boolean bool = this.mPendingSendNextAlarmClockChangedForUser.get(i);
          paramPrintWriter.print("    user:");
          paramPrintWriter.print(i);
          paramPrintWriter.print(" pendingSend:");
          paramPrintWriter.print(bool);
          paramPrintWriter.print(" time:");
          paramPrintWriter.print(l1);
          if (l1 > 0L)
          {
            paramPrintWriter.print(" = ");
            paramPrintWriter.print(localSimpleDateFormat.format(new Date(l1)));
            paramPrintWriter.print(" = ");
            TimeUtils.formatDuration(l1, l2, paramPrintWriter);
          }
          paramPrintWriter.println();
        }
      }
      l1 = 0L;
    }
    if (this.mAlarmBatches.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.print("  Pending alarm batches: ");
      paramPrintWriter.println(this.mAlarmBatches.size());
      localObject2 = this.mAlarmBatches.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Batch)((Iterator)localObject2).next();
        paramPrintWriter.print(localObject3);
        paramPrintWriter.println(':');
        dumpAlarmList(paramPrintWriter, ((Batch)localObject3).alarms, "    ", l3, l2, localSimpleDateFormat);
      }
    }
    if ((this.mPendingIdleUntil != null) || (this.mPendingWhileIdleAlarms.size() > 0))
    {
      paramPrintWriter.println();
      paramPrintWriter.println("    Idle mode state:");
      paramPrintWriter.print("      Idling until: ");
      if (this.mPendingIdleUntil != null)
      {
        paramPrintWriter.println(this.mPendingIdleUntil);
        this.mPendingIdleUntil.dump(paramPrintWriter, "        ", l2, l3, localSimpleDateFormat);
        paramPrintWriter.println("      Pending alarms:");
        dumpAlarmList(paramPrintWriter, this.mPendingWhileIdleAlarms, "      ", l3, l2, localSimpleDateFormat);
      }
    }
    else
    {
      if (this.mNextWakeFromIdle != null)
      {
        paramPrintWriter.println();
        paramPrintWriter.print("  Next wake from idle: ");
        paramPrintWriter.println(this.mNextWakeFromIdle);
        this.mNextWakeFromIdle.dump(paramPrintWriter, "    ", l2, l3, localSimpleDateFormat);
      }
      paramPrintWriter.println();
      paramPrintWriter.print("  Past-due non-wakeup alarms: ");
      if (this.mPendingNonWakeupAlarms.size() <= 0) {
        break label1165;
      }
      paramPrintWriter.println(this.mPendingNonWakeupAlarms.size());
      dumpAlarmList(paramPrintWriter, this.mPendingNonWakeupAlarms, "    ", l3, l2, localSimpleDateFormat);
    }
    for (;;)
    {
      paramPrintWriter.print("    Number of delayed alarms: ");
      paramPrintWriter.print(this.mNumDelayedAlarms);
      paramPrintWriter.print(", total delay time: ");
      TimeUtils.formatDuration(this.mTotalDelayTime, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    Max delay time: ");
      TimeUtils.formatDuration(this.mMaxDelayTime, paramPrintWriter);
      paramPrintWriter.print(", max non-interactive time: ");
      TimeUtils.formatDuration(this.mNonInteractiveTime, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.println();
      paramPrintWriter.print("  Broadcast ref count: ");
      paramPrintWriter.println(this.mBroadcastRefCount);
      paramPrintWriter.println();
      if (this.mInFlight.size() <= 0) {
        break label1179;
      }
      paramPrintWriter.println("Outstanding deliveries:");
      i = 0;
      while (i < this.mInFlight.size())
      {
        paramPrintWriter.print("   #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(this.mInFlight.get(i));
        i += 1;
      }
      paramPrintWriter.println("null");
      break;
      label1165:
      paramPrintWriter.println("(none)");
    }
    paramPrintWriter.println();
    label1179:
    paramPrintWriter.print("  mAllowWhileIdleMinTime=");
    TimeUtils.formatDuration(this.mAllowWhileIdleMinTime, paramPrintWriter);
    paramPrintWriter.println();
    if (this.mLastAllowWhileIdleDispatch.size() > 0)
    {
      paramPrintWriter.println("  Last allow while idle dispatch times:");
      i = 0;
      while (i < this.mLastAllowWhileIdleDispatch.size())
      {
        paramPrintWriter.print("  UID ");
        UserHandle.formatUid(paramPrintWriter, this.mLastAllowWhileIdleDispatch.keyAt(i));
        paramPrintWriter.print(": ");
        TimeUtils.formatDuration(this.mLastAllowWhileIdleDispatch.valueAt(i), l3, paramPrintWriter);
        paramPrintWriter.println();
        i += 1;
      }
    }
    paramPrintWriter.println();
    if (this.mLog.dump(paramPrintWriter, "  Recent problems", "    ")) {
      paramPrintWriter.println();
    }
    Object localObject3 = new FilterStats[10];
    Object localObject2 = new Comparator()
    {
      public int compare(AlarmManagerService.FilterStats paramAnonymousFilterStats1, AlarmManagerService.FilterStats paramAnonymousFilterStats2)
      {
        if (paramAnonymousFilterStats1.aggregateTime < paramAnonymousFilterStats2.aggregateTime) {
          return 1;
        }
        if (paramAnonymousFilterStats1.aggregateTime > paramAnonymousFilterStats2.aggregateTime) {
          return -1;
        }
        return 0;
      }
    };
    int i = 0;
    int j = 0;
    label1325:
    ArrayMap localArrayMap;
    int k;
    label1352:
    Object localObject4;
    int n;
    int m;
    label1429:
    int i1;
    if (j < this.mBroadcastStats.size())
    {
      localArrayMap = (ArrayMap)this.mBroadcastStats.valueAt(j);
      k = 0;
      if (k >= localArrayMap.size()) {
        break label2223;
      }
      localObject4 = (BroadcastStats)localArrayMap.valueAt(k);
      n = 0;
      m = i;
      if (n >= ((BroadcastStats)localObject4).filterStats.size()) {
        break label2211;
      }
      FilterStats localFilterStats = (FilterStats)((BroadcastStats)localObject4).filterStats.valueAt(n);
      if (m <= 0) {
        break label2206;
      }
      i = Arrays.binarySearch((Object[])localObject3, 0, m, localFilterStats, (Comparator)localObject2);
      break label2178;
      i = m;
      if (i1 >= localObject3.length) {
        break label2194;
      }
      i = localObject3.length - i1 - 1;
      if (i > 0) {
        System.arraycopy(localObject3, i1, localObject3, i1 + 1, i);
      }
      localObject3[i1] = localFilterStats;
      i = m;
      if (m >= localObject3.length) {
        break label2194;
      }
      i = m + 1;
      break label2194;
    }
    else
    {
      if (i > 0)
      {
        paramPrintWriter.println("  Top Alarms:");
        j = 0;
        while (j < i)
        {
          localArrayMap = localObject3[j];
          paramPrintWriter.print("    ");
          if (localArrayMap.nesting > 0) {
            paramPrintWriter.print("*ACTIVE* ");
          }
          TimeUtils.formatDuration(localArrayMap.aggregateTime, paramPrintWriter);
          paramPrintWriter.print(" running, ");
          paramPrintWriter.print(localArrayMap.numWakeup);
          paramPrintWriter.print(" wakeups, ");
          paramPrintWriter.print(localArrayMap.count);
          paramPrintWriter.print(" alarms: ");
          UserHandle.formatUid(paramPrintWriter, localArrayMap.mBroadcastStats.mUid);
          paramPrintWriter.print(":");
          paramPrintWriter.print(localArrayMap.mBroadcastStats.mPackageName);
          paramPrintWriter.println();
          paramPrintWriter.print("      ");
          paramPrintWriter.print(localArrayMap.mTag);
          paramPrintWriter.println();
          j += 1;
        }
      }
      paramPrintWriter.println(" ");
      paramPrintWriter.println("  Alarm Stats:");
      localObject3 = new ArrayList();
      i = 0;
    }
    for (;;)
    {
      if (i < this.mBroadcastStats.size())
      {
        localArrayMap = (ArrayMap)this.mBroadcastStats.valueAt(i);
        j = 0;
      }
      for (;;)
      {
        if (j >= localArrayMap.size()) {
          break label2237;
        }
        localObject4 = (BroadcastStats)localArrayMap.valueAt(j);
        paramPrintWriter.print("  ");
        if (((BroadcastStats)localObject4).nesting > 0) {
          paramPrintWriter.print("*ACTIVE* ");
        }
        UserHandle.formatUid(paramPrintWriter, ((BroadcastStats)localObject4).mUid);
        paramPrintWriter.print(":");
        paramPrintWriter.print(((BroadcastStats)localObject4).mPackageName);
        paramPrintWriter.print(" ");
        TimeUtils.formatDuration(((BroadcastStats)localObject4).aggregateTime, paramPrintWriter);
        paramPrintWriter.print(" running, ");
        paramPrintWriter.print(((BroadcastStats)localObject4).numWakeup);
        paramPrintWriter.println(" wakeups:");
        ((ArrayList)localObject3).clear();
        k = 0;
        while (k < ((BroadcastStats)localObject4).filterStats.size())
        {
          ((ArrayList)localObject3).add((FilterStats)((BroadcastStats)localObject4).filterStats.valueAt(k));
          k += 1;
        }
        Collections.sort((List)localObject3, (Comparator)localObject2);
        k = 0;
        for (;;)
        {
          if (k < ((ArrayList)localObject3).size())
          {
            localObject4 = (FilterStats)((ArrayList)localObject3).get(k);
            paramPrintWriter.print("    ");
            if (((FilterStats)localObject4).nesting > 0) {
              paramPrintWriter.print("*ACTIVE* ");
            }
            TimeUtils.formatDuration(((FilterStats)localObject4).aggregateTime, paramPrintWriter);
            paramPrintWriter.print(" ");
            paramPrintWriter.print(((FilterStats)localObject4).numWakeup);
            paramPrintWriter.print(" wakes ");
            paramPrintWriter.print(((FilterStats)localObject4).count);
            paramPrintWriter.print(" alarms, last ");
            TimeUtils.formatDuration(((FilterStats)localObject4).lastTime, l3, paramPrintWriter);
            paramPrintWriter.println(":");
            paramPrintWriter.print("      ");
            paramPrintWriter.print(((FilterStats)localObject4).mTag);
            paramPrintWriter.println();
            k += 1;
            continue;
            if (WAKEUP_STATS)
            {
              paramPrintWriter.println();
              paramPrintWriter.println("  Recent Wakeup History:");
              l1 = -1L;
              localObject2 = this.mRecentWakeups.iterator();
              if (((Iterator)localObject2).hasNext())
              {
                localObject3 = (WakeupEvent)((Iterator)localObject2).next();
                paramPrintWriter.print("    ");
                paramPrintWriter.print(localSimpleDateFormat.format(new Date(((WakeupEvent)localObject3).when)));
                paramPrintWriter.print('|');
                if (l1 < 0L) {
                  paramPrintWriter.print('0');
                }
                for (;;)
                {
                  l1 = ((WakeupEvent)localObject3).when;
                  paramPrintWriter.print('|');
                  paramPrintWriter.print(((WakeupEvent)localObject3).uid);
                  paramPrintWriter.print('|');
                  paramPrintWriter.print(((WakeupEvent)localObject3).action);
                  paramPrintWriter.println();
                  break;
                  paramPrintWriter.print(((WakeupEvent)localObject3).when - l1);
                }
              }
              paramPrintWriter.println();
            }
            return;
            label2173:
            i = 0;
            break;
            for (;;)
            {
              label2178:
              i1 = i;
              if (i >= 0) {
                break label1429;
              }
              i1 = -i - 1;
              break label1429;
              label2194:
              n += 1;
              m = i;
              break;
              label2206:
              i = 0;
            }
            label2211:
            k += 1;
            i = m;
            break label1352;
            label2223:
            j += 1;
            break label1325;
          }
        }
        j += 1;
      }
      label2237:
      i += 1;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close(this.mNativeData);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  AlarmManager.AlarmClockInfo getNextAlarmClockImpl(int paramInt)
  {
    synchronized (this.mLock)
    {
      AlarmManager.AlarmClockInfo localAlarmClockInfo = (AlarmManager.AlarmClockInfo)this.mNextAlarmClockForUser.get(paramInt);
      return localAlarmClockInfo;
    }
  }
  
  long getNextWakeFromIdleTimeImpl()
  {
    synchronized (this.mLock)
    {
      if (this.mNextWakeFromIdle != null)
      {
        l = this.mNextWakeFromIdle.whenElapsed;
        return l;
      }
      long l = Long.MAX_VALUE;
    }
  }
  
  void interactiveStateChangedLocked(boolean paramBoolean)
  {
    long l1;
    if (this.mInteractive != paramBoolean)
    {
      this.mInteractive = paramBoolean;
      l1 = SystemClock.elapsedRealtime();
      if (paramBoolean)
      {
        long l2;
        if (this.mPendingNonWakeupAlarms.size() > 0)
        {
          l2 = l1 - this.mStartCurrentDelayTime;
          this.mTotalDelayTime += l2;
          if (this.mMaxDelayTime < l2) {
            this.mMaxDelayTime = l2;
          }
          deliverAlarmsLocked(this.mPendingNonWakeupAlarms, l1);
          this.mPendingNonWakeupAlarms.clear();
        }
        if (this.mNonInteractiveStartTime > 0L)
        {
          l2 = l1 - this.mNonInteractiveStartTime;
          if (l2 > this.mNonInteractiveTime) {
            this.mNonInteractiveTime = l2;
          }
        }
        this.mInteractiveStartTime = l1;
      }
    }
    else
    {
      return;
    }
    this.mNonInteractiveStartTime = l1;
  }
  
  boolean isContains(String paramString)
  {
    Iterator localIterator = mBlackAlarmOperation.iterator();
    while (localIterator.hasNext()) {
      if (paramString.contains((String)localIterator.next())) {
        return true;
      }
    }
    return false;
  }
  
  boolean lookForPackageLocked(String paramString)
  {
    int i = 0;
    while (i < this.mAlarmBatches.size())
    {
      if (((Batch)this.mAlarmBatches.get(i)).hasPackage(paramString)) {
        return true;
      }
      i += 1;
    }
    i = 0;
    while (i < this.mPendingWhileIdleAlarms.size())
    {
      if (((Alarm)this.mPendingWhileIdleAlarms.get(i)).matches(paramString)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      this.mConstants.start(getContext().getContentResolver());
      this.mAppOps = ((AppOpsManager)getContext().getSystemService("appops"));
      this.mLocalDeviceIdleController = ((DeviceIdleController.LocalService)LocalServices.getService(DeviceIdleController.LocalService.class));
    }
  }
  
  public void onStart()
  {
    this.mNativeData = init();
    this.mNextNonWakeup = 0L;
    this.mNextRtcWakeup = 0L;
    this.mNextWakeup = 0L;
    AlarmManager.writePowerOffAlarmFile("/persist/alarm/powerOffAlarmSet", "0");
    Object localObject = SystemProperties.get("vold.decrypt");
    if (("trigger_restart_min_framework".equals(localObject)) || ("1".equals(localObject))) {
      this.mIsEncryptStatus = true;
    }
    if (this.mIsEncryptStatus) {
      setTimeZoneImpl(AlarmManager.readPowerOffAlarmFile("/persist/alarm/timezone"));
    }
    for (;;)
    {
      this.mWakeLock = ((PowerManager)getContext().getSystemService("power")).newWakeLock(1, "*alarm*");
      this.mDozeBlackList = new ArrayList(Arrays.asList(getContext().getResources().getStringArray(84344841)));
      this.mTimeTickSender = PendingIntent.getBroadcastAsUser(getContext(), 0, new Intent("android.intent.action.TIME_TICK").addFlags(1342177280), 0, UserHandle.ALL);
      localObject = new Intent("android.intent.action.DATE_CHANGED");
      ((Intent)localObject).addFlags(536870912);
      this.mDateChangeSender = PendingIntent.getBroadcastAsUser(getContext(), 0, (Intent)localObject, 67108864, UserHandle.ALL);
      this.mClockReceiver = new ClockReceiver();
      this.mClockReceiver.scheduleTimeTickEvent();
      this.mClockReceiver.scheduleDateChangedEvent();
      this.mInteractiveStateReceiver = new InteractiveStateReceiver();
      this.mUninstallReceiver = new UninstallReceiver();
      if (this.mNativeData != 0L)
      {
        new AlarmThread().start();
        this.mInteractiveStartTime = SystemClock.elapsedRealtime();
      }
      try
      {
        ActivityManagerNative.getDefault().registerUidObserver(new UidObserver(), 4);
        publishBinderService("alarm", this.mService);
        publishLocalService(LocalService.class, new LocalService());
        return;
        setTimeZoneImpl(SystemProperties.get("persist.sys.timezone"));
        continue;
        Slog.w("AlarmManager", "Failed to open alarm driver. Falling back to a handler.");
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  void reAddAlarmLocked(Alarm paramAlarm, long paramLong, boolean paramBoolean)
  {
    paramAlarm.when = paramAlarm.origWhen;
    long l = convertToElapsed(paramAlarm.when, paramAlarm.type);
    if (paramAlarm.windowLength == 0L) {
      paramLong = l;
    }
    for (;;)
    {
      paramAlarm.whenElapsed = l;
      paramAlarm.maxWhenElapsed = paramLong;
      if (DEBUG_BATCH) {
        Slog.d("AlarmManager", "reAddAlarmLocked a.whenElapsed  = " + paramAlarm.whenElapsed + " a.maxWhenElapsed = " + paramAlarm.maxWhenElapsed);
      }
      setImplLocked(paramAlarm, true, paramBoolean);
      return;
      if (paramAlarm.windowLength > 0L) {
        paramLong = l + paramAlarm.windowLength;
      } else {
        paramLong = maxTriggerTime(paramLong, l, paramAlarm.repeatInterval);
      }
    }
  }
  
  void rebatchAllAlarms()
  {
    synchronized (this.mLock)
    {
      rebatchAllAlarmsLocked(true);
      return;
    }
  }
  
  void rebatchAllAlarmsLocked(boolean paramBoolean)
  {
    ArrayList localArrayList = (ArrayList)this.mAlarmBatches.clone();
    this.mAlarmBatches.clear();
    Alarm localAlarm = this.mPendingIdleUntil;
    long l = SystemClock.elapsedRealtime();
    int k = localArrayList.size();
    if (DEBUG_BATCH) {
      Slog.d("AlarmManager", "rebatchAllAlarmsLocked begin oldBatches count = " + k);
    }
    int i = 0;
    while (i < k)
    {
      Batch localBatch = (Batch)localArrayList.get(i);
      int m = localBatch.size();
      if (DEBUG_BATCH) {
        Slog.d("AlarmManager", "rebatchAllAlarmsLocked  batch.size() = " + localBatch.size());
      }
      int j = 0;
      while (j < m)
      {
        reAddAlarmLocked(localBatch.get(j), l, paramBoolean);
        j += 1;
      }
      i += 1;
    }
    if ((localAlarm != null) && (localAlarm != this.mPendingIdleUntil))
    {
      Slog.wtf("AlarmManager", "Rebatching: idle until changed from " + localAlarm + " to " + this.mPendingIdleUntil);
      if (this.mPendingIdleUntil == null) {
        restorePendingWhileIdleAlarmsLocked();
      }
    }
    rescheduleKernelAlarmsLocked();
    updateNextAlarmClockLocked();
  }
  
  void recordWakeupAlarms(ArrayList<Batch> paramArrayList, long paramLong1, long paramLong2)
  {
    int k = paramArrayList.size();
    int i = 0;
    for (;;)
    {
      Batch localBatch;
      if (i < k)
      {
        localBatch = (Batch)paramArrayList.get(i);
        if (localBatch.start <= paramLong1) {}
      }
      else
      {
        return;
      }
      int m = localBatch.alarms.size();
      int j = 0;
      while (j < m)
      {
        Alarm localAlarm = (Alarm)localBatch.alarms.get(j);
        this.mRecentWakeups.add(localAlarm.makeWakeupEvent(paramLong2));
        j += 1;
      }
      i += 1;
    }
  }
  
  void removeForStoppedLocked(int paramInt)
  {
    boolean bool = false;
    int i = this.mAlarmBatches.size() - 1;
    Object localObject;
    while (i >= 0)
    {
      localObject = (Batch)this.mAlarmBatches.get(i);
      bool |= ((Batch)localObject).removeForStopped(paramInt);
      if (((Batch)localObject).size() == 0) {
        this.mAlarmBatches.remove(i);
      }
      i -= 1;
    }
    i = this.mPendingWhileIdleAlarms.size() - 1;
    for (;;)
    {
      if (i >= 0) {
        localObject = (Alarm)this.mPendingWhileIdleAlarms.get(i);
      }
      try
      {
        if ((((Alarm)localObject).uid == paramInt) && (ActivityManagerNative.getDefault().getAppStartMode(paramInt, ((Alarm)localObject).packageName) == 2)) {
          this.mPendingWhileIdleAlarms.remove(i);
        }
        i -= 1;
        continue;
        if (bool)
        {
          if (DEBUG_BATCH) {
            Slog.v("AlarmManager", "remove(package) changed bounds; rebatching");
          }
          rebatchAllAlarmsLocked(true);
          rescheduleKernelAlarmsLocked();
          updateNextAlarmClockLocked();
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  void removeImpl(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {
      return;
    }
    synchronized (this.mLock)
    {
      removeLocked(paramPendingIntent, null);
      return;
    }
  }
  
  void removeLocked(String paramString)
  {
    boolean bool = false;
    int i = this.mAlarmBatches.size() - 1;
    while (i >= 0)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      bool |= localBatch.remove(paramString);
      if (localBatch.size() == 0) {
        this.mAlarmBatches.remove(i);
      }
      i -= 1;
    }
    i = this.mPendingWhileIdleAlarms.size() - 1;
    while (i >= 0)
    {
      if (((Alarm)this.mPendingWhileIdleAlarms.get(i)).matches(paramString)) {
        this.mPendingWhileIdleAlarms.remove(i);
      }
      i -= 1;
    }
    if (bool)
    {
      if (DEBUG_BATCH) {
        Slog.v("AlarmManager", "remove(package) changed bounds; rebatching");
      }
      rebatchAllAlarmsLocked(true);
      rescheduleKernelAlarmsLocked();
      updateNextAlarmClockLocked();
    }
  }
  
  void removeUserLocked(int paramInt)
  {
    boolean bool = false;
    int i = this.mAlarmBatches.size() - 1;
    while (i >= 0)
    {
      Batch localBatch = (Batch)this.mAlarmBatches.get(i);
      bool |= localBatch.remove(paramInt);
      if (localBatch.size() == 0) {
        this.mAlarmBatches.remove(i);
      }
      i -= 1;
    }
    i = this.mPendingWhileIdleAlarms.size() - 1;
    while (i >= 0)
    {
      if (UserHandle.getUserId(((Alarm)this.mPendingWhileIdleAlarms.get(i)).creatorUid) == paramInt) {
        this.mPendingWhileIdleAlarms.remove(i);
      }
      i -= 1;
    }
    i = this.mLastAllowWhileIdleDispatch.size() - 1;
    while (i >= 0)
    {
      if (UserHandle.getUserId(this.mLastAllowWhileIdleDispatch.keyAt(i)) == paramInt) {
        this.mLastAllowWhileIdleDispatch.removeAt(i);
      }
      i -= 1;
    }
    if (bool)
    {
      if (DEBUG_BATCH) {
        Slog.v("AlarmManager", "remove(user) changed bounds; rebatching");
      }
      rebatchAllAlarmsLocked(true);
      rescheduleKernelAlarmsLocked();
      updateNextAlarmClockLocked();
    }
  }
  
  void rescheduleKernelAlarmsLocked()
  {
    long l2 = 0L;
    long l1 = l2;
    Batch localBatch1;
    Batch localBatch2;
    Object localObject;
    if (this.mAlarmBatches.size() > 0)
    {
      localBatch1 = findFirstWakeupBatchLocked();
      localBatch2 = (Batch)this.mAlarmBatches.get(0);
      localObject = findFirstRtcWakeupBatchLocked();
      if ((localBatch1 != null) && (this.mNextWakeup != localBatch1.start))
      {
        this.mNextWakeup = localBatch1.start;
        this.mLastWakeupSet = SystemClock.elapsedRealtime();
        setLocked(2, localBatch1.start);
      }
      if ((localObject == null) || (this.mNextRtcWakeup == ((Batch)localObject).start)) {
        break label299;
      }
      this.mNextRtcWakeup = ((Batch)localObject).start;
      localObject = ((Batch)localObject).getAlarmByElapsedTime(this.mNextRtcWakeup);
      if (localObject != null)
      {
        if (!"com.android.deskclock".equals(((Alarm)localObject).packageName)) {
          break label275;
        }
        this.mIsPowerOffAlarmSet = true;
        AlarmManager.writePowerOffAlarmFile("/persist/alarm/powerOffAlarmSet", "1");
        if (!this.mIsEncryptStatus) {
          AlarmManager.writePowerOffAlarmFile("/persist/alarm/powerOffAlarmInstance", "" + ((Alarm)localObject).when);
        }
        setLocked(5, ((Alarm)localObject).when);
      }
    }
    for (;;)
    {
      l1 = l2;
      if (localBatch2 != localBatch1) {
        l1 = localBatch2.start;
      }
      l2 = l1;
      if (this.mPendingNonWakeupAlarms.size() > 0) {
        if (l1 != 0L)
        {
          l2 = l1;
          if (this.mNextNonWakeupDeliveryTime >= l1) {}
        }
        else
        {
          l2 = this.mNextNonWakeupDeliveryTime;
        }
      }
      if ((l2 != 0L) && (this.mNextNonWakeup != l2))
      {
        this.mNextNonWakeup = l2;
        setLocked(3, l2);
      }
      return;
      label275:
      if (!this.mIsPowerOffAlarmSet) {
        break;
      }
      this.mIsPowerOffAlarmSet = false;
      AlarmManager.writePowerOffAlarmFile("/persist/alarm/powerOffAlarmSet", "0");
      break;
      label299:
      if ((localObject == null) && (this.mIsPowerOffAlarmSet))
      {
        this.mIsPowerOffAlarmSet = false;
        AlarmManager.writePowerOffAlarmFile("/persist/alarm/powerOffAlarmSet", "0");
      }
    }
  }
  
  void restorePendingWhileIdleAlarmsLocked()
  {
    if (this.mPendingWhileIdleAlarms.size() > 0)
    {
      long l3 = SystemClock.elapsedRealtime();
      long l4 = System.currentTimeMillis();
      ArrayList localArrayList = this.mPendingWhileIdleAlarms;
      this.mPendingWhileIdleAlarms = new ArrayList();
      boolean bool1 = false;
      int n = localArrayList.size() - 1;
      int j = 0;
      int i = 0;
      if (n >= 0)
      {
        Alarm localAlarm = (Alarm)localArrayList.get(n);
        label87:
        int k;
        label89:
        label104:
        boolean bool2;
        if ((localAlarm.type == 1) || (localAlarm.type == 0))
        {
          k = 1;
          if ((k == 0) || (localAlarm.origWhen <= l4)) {
            break label144;
          }
          reAddAlarmLocked(localAlarm, l3, false);
          bool2 = bool1;
        }
        for (;;)
        {
          n -= 1;
          bool1 = bool2;
          break;
          if (localAlarm.type == 5) {
            break label87;
          }
          k = 0;
          break label89;
          label144:
          if ((k == 0) && (localAlarm.origWhen > l3)) {
            break label104;
          }
          if (localAlarm.statsTag != null) {
            bool1 = checkAlarmOperation(localAlarm.statsTag);
          }
          int i1;
          long l2;
          long l1;
          int m;
          if (bool1)
          {
            i1 = i + 1;
            l2 = 60000L + l3 + i / 10 * 30 * 1000;
            l1 = l2;
            if (k != 0) {
              l1 = l2 + (l4 - l3);
            }
            localAlarm.origWhen = l1;
            reAddAlarmLocked(localAlarm, l2, false);
            m = j;
          }
          for (k = i1;; k = i)
          {
            bool2 = bool1;
            i = k;
            j = m;
            if (!DEBUG_ONEPLUS) {
              break;
            }
            if (localAlarm.operation == null) {
              break label425;
            }
            Slog.d("AlarmManager", "reAddAlarmLocked a.when  = " + localAlarm.when + " isAdjust = " + bool1 + " time=" + l1 + " a.type = " + localAlarm.type + " operation = " + localAlarm.statsTag);
            bool2 = bool1;
            i = k;
            j = m;
            break;
            m = j + 1;
            l2 = l3 + j / 10 * 30 * 1000;
            l1 = l2;
            if (k != 0) {
              l1 = l2 + (l4 - l3);
            }
            localAlarm.origWhen = l1;
            reAddAlarmLocked(localAlarm, l2, false);
          }
          label425:
          Slog.d("AlarmManager", "reAddAlarmLocked a.when  = " + localAlarm.when + " isAdjust = " + bool1 + " time=" + l1 + " a.type = " + localAlarm.type + " operation = " + " null");
          bool2 = bool1;
          i = k;
          j = m;
        }
      }
    }
    this.mConstants.updateAllowWhileIdleMinTimeLocked();
    rescheduleKernelAlarmsLocked();
    updateNextAlarmClockLocked();
    try
    {
      this.mTimeTickSender.send();
      return;
    }
    catch (PendingIntent.CanceledException localCanceledException) {}
  }
  
  public void setBlockAlarmUidLock(String paramString, boolean paramBoolean, int paramInt)
  {
    long l = SystemClock.elapsedRealtime();
    if (paramBoolean)
    {
      mFrozeenUids.remove(paramString);
      mFrozeenUids.put(paramString, Integer.valueOf(paramInt));
      mFrozeenTimeUids.remove(paramString);
      mFrozeenTimeUids.put(paramString, Long.valueOf(l));
      return;
    }
    Message localMessage = Message.obtain();
    localMessage.what = 5;
    localMessage.obj = paramString;
    this.mHandler.sendMessage(localMessage);
    mFrozeenUids.remove(paramString);
    mFrozeenTimeUids.remove(paramString);
  }
  
  void setDeviceIdleUserWhitelistImpl(int[] paramArrayOfInt)
  {
    synchronized (this.mLock)
    {
      this.mDeviceIdleUserWhitelist = paramArrayOfInt;
      return;
    }
  }
  
  void setImpl(int paramInt1, long paramLong1, long paramLong2, long paramLong3, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, String paramString1, int paramInt2, WorkSource paramWorkSource, AlarmManager.AlarmClockInfo paramAlarmClockInfo, int paramInt3, String paramString2)
  {
    if ((paramPendingIntent == null) && (paramIAlarmListener == null)) {}
    while ((paramPendingIntent != null) && (paramIAlarmListener != null))
    {
      Slog.w("AlarmManager", "Alarms must either supply a PendingIntent or an AlarmReceiver");
      return;
    }
    long l1 = paramLong2;
    if (paramLong2 > 43200000L)
    {
      Slog.w("AlarmManager", "Window length " + paramLong2 + "ms suspiciously long; limiting to 1 hour");
      l1 = 3600000L;
    }
    paramLong2 = this.mConstants.MIN_INTERVAL;
    long l2 = paramLong3;
    if (paramLong3 > 0L)
    {
      l2 = paramLong3;
      if (paramLong3 < paramLong2)
      {
        Slog.w("AlarmManager", "Suspiciously short interval " + paramLong3 + " millis; expanding to " + paramLong2 / 1000L + " seconds");
        l2 = paramLong2;
      }
    }
    if ((paramInt1 < 0) || (paramInt1 > 5)) {
      throw new IllegalArgumentException("Invalid alarm type " + paramInt1);
    }
    paramLong3 = paramLong1;
    if (paramLong1 < 0L)
    {
      paramLong2 = Binder.getCallingPid();
      Slog.w("AlarmManager", "Invalid alarm trigger time! " + paramLong1 + " from uid=" + paramInt3 + " pid=" + paramLong2);
      paramLong3 = 0L;
    }
    long l3 = SystemClock.elapsedRealtime();
    paramLong2 = convertToElapsed(paramLong3, paramInt1);
    if (paramString2.contains("deskclock")) {
      paramLong1 = l3 + 1000L;
    }
    for (;;)
    {
      if (paramLong2 > paramLong1)
      {
        paramLong1 = paramLong2;
        label309:
        if (l1 != 0L) {
          break label491;
        }
        paramLong2 = paramLong1;
      }
      synchronized (this.mLock)
      {
        for (;;)
        {
          if (DEBUG_BATCH) {
            Slog.v("AlarmManager", "set(" + paramPendingIntent + ") : type=" + paramInt1 + " triggerAtTime=" + paramLong3 + " win=" + l1 + " tElapsed=" + paramLong1 + " maxElapsed=" + paramLong2 + " interval=" + l2 + " flags=0x" + Integer.toHexString(paramInt2));
          }
          setImplLocked(paramInt1, paramLong3, paramLong1, l1, paramLong2, l2, paramPendingIntent, paramIAlarmListener, paramString1, paramInt2, true, paramWorkSource, paramAlarmClockInfo, paramInt3, paramString2);
          return;
          paramLong1 = l3 + this.mConstants.MIN_FUTURITY;
          break;
          break label309;
          label491:
          if (l1 >= 0L) {
            break label517;
          }
          paramLong2 = maxTriggerTime(l3, paramLong1, l2);
          l1 = paramLong2 - paramLong1;
        }
        label517:
        paramLong2 = paramLong1 + l1;
      }
    }
  }
  
  void setTimeZoneImpl(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return;
    }
    paramString = TimeZone.getTimeZone(paramString);
    int i = 0;
    Object localObject;
    try
    {
      localObject = SystemProperties.get("persist.sys.timezone");
      if ((localObject != null) && (((String)localObject).equals(paramString.getID()))) {}
      for (;;)
      {
        int j = paramString.getOffset(System.currentTimeMillis());
        setKernelTimezone(this.mNativeData, -(j / 60000));
        TimeZone.setDefault(null);
        if ((i != 0) && (!this.mIsEncryptStatus)) {
          break;
        }
        return;
        if (localLOGV) {
          Slog.v("AlarmManager", "timezone changed: " + (String)localObject + ", new=" + paramString.getID());
        }
        i = 1;
        SystemProperties.set("persist.sys.timezone", paramString.getID());
      }
      localObject = new Intent("android.intent.action.TIMEZONE_CHANGED");
    }
    finally {}
    ((Intent)localObject).addFlags(536870912);
    ((Intent)localObject).putExtra("time-zone", paramString.getID());
    getContext().sendBroadcastAsUser((Intent)localObject, UserHandle.ALL);
  }
  
  void setWakelockWorkSource(PendingIntent paramPendingIntent, WorkSource paramWorkSource, int paramInt1, String paramString, int paramInt2, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        boolean bool;
        if (paramPendingIntent == this.mTimeTickSender)
        {
          bool = true;
          this.mWakeLock.setUnimportantForLogging(bool);
          if ((paramBoolean) || (this.mLastWakeLockUnimportantForLogging))
          {
            this.mWakeLock.setHistoryTag(paramString);
            this.mLastWakeLockUnimportantForLogging = bool;
            if (paramWorkSource != null) {
              this.mWakeLock.setWorkSource(paramWorkSource);
            }
          }
          else
          {
            this.mWakeLock.setHistoryTag(null);
            continue;
            if (paramInt1 >= 0)
            {
              this.mWakeLock.setWorkSource(new WorkSource(paramInt1));
              return;
              paramInt1 = ActivityManagerNative.getDefault().getUidForIntentSender(paramPendingIntent.getTarget());
              continue;
            }
            this.mWakeLock.setWorkSource(null);
          }
        }
        else
        {
          bool = false;
          continue;
        }
        if (paramInt2 < 0) {
          continue;
        }
      }
      catch (Exception paramPendingIntent)
      {
        return;
      }
      paramInt1 = paramInt2;
    }
  }
  
  boolean triggerAlarmsLocked(ArrayList<Alarm> paramArrayList, long paramLong1, long paramLong2)
  {
    boolean bool2 = false;
    Object localObject1;
    int i;
    label62:
    Object localObject2;
    String str;
    if (this.mAlarmBatches.size() > 0)
    {
      localObject1 = (Batch)this.mAlarmBatches.get(0);
      if (((Batch)localObject1).start <= paramLong1) {}
    }
    else
    {
      this.mCurrentSeq += 1;
      calculateDeliveryPriorities(paramArrayList);
      Collections.sort(paramArrayList, this.mAlarmDispatchComparator);
      i = 0;
      if (i >= paramArrayList.size()) {
        break label770;
      }
      localObject1 = (Alarm)paramArrayList.get(i);
      localObject2 = ((Alarm)localObject1).operation;
      str = ((Alarm)localObject1).listenerTag;
      if (localObject2 == null) {
        break label670;
      }
      Slog.v("AlarmManager", "Triggering alarm #" + i + ": " + ((Alarm)localObject1).type + " when =" + ((Alarm)localObject1).when + " package=" + ((Alarm)localObject1).packageName + "operation =" + ((Alarm)localObject1).statsTag);
    }
    for (;;)
    {
      i += 1;
      break label62;
      this.mAlarmBatches.remove(0);
      int k = ((Batch)localObject1).size();
      i = 0;
      boolean bool1 = bool2;
      bool2 = bool1;
      if (i >= k) {
        break;
      }
      localObject2 = ((Batch)localObject1).get(i);
      long l1;
      if ((((Alarm)localObject2).flags & 0x4) != 0)
      {
        l1 = this.mLastAllowWhileIdleDispatch.get(((Alarm)localObject2).uid, 0L) + this.mAllowWhileIdleMinTime;
        if (localLOGV) {
          Slog.v("AlarmManager", "Reschedule the alarm... UID=" + ((Alarm)localObject2).uid + ", elapsed=" + paramLong1 + ", minTime=" + l1 + ", mAllowWhileIdleMinTime=" + this.mAllowWhileIdleMinTime);
        }
        if (paramLong1 < l1)
        {
          ((Alarm)localObject2).whenElapsed = l1;
          if (((Alarm)localObject2).maxWhenElapsed < l1) {
            ((Alarm)localObject2).maxWhenElapsed = l1;
          }
          setImplLocked((Alarm)localObject2, true, false);
          bool2 = bool1;
        }
      }
      do
      {
        i += 1;
        bool1 = bool2;
        break;
        bool2 = bool1;
      } while (checkDelayAlarm((Alarm)localObject2, (Batch)localObject1, paramLong1, paramLong2));
      ((Alarm)localObject2).count = 1;
      paramArrayList.add(localObject2);
      if ((((Alarm)localObject2).flags & 0x2) != 0) {
        if (this.mPendingIdleUntil == null) {
          break label664;
        }
      }
      label664:
      for (int j = 1;; j = 0)
      {
        EventLogTags.writeDeviceIdleWakeFromIdle(j, ((Alarm)localObject2).statsTag);
        if (this.mPendingIdleUntil == localObject2)
        {
          this.mPendingIdleUntil = null;
          rebatchAllAlarmsLocked(false);
          restorePendingWhileIdleAlarmsLocked();
        }
        if (this.mNextWakeFromIdle == localObject2)
        {
          this.mNextWakeFromIdle = null;
          rebatchAllAlarmsLocked(false);
        }
        if (((Alarm)localObject2).repeatInterval > 0L)
        {
          ((Alarm)localObject2).count = ((int)(((Alarm)localObject2).count + (paramLong1 - ((Alarm)localObject2).whenElapsed) / ((Alarm)localObject2).repeatInterval));
          l1 = ((Alarm)localObject2).count * ((Alarm)localObject2).repeatInterval;
          long l2 = ((Alarm)localObject2).whenElapsed + l1;
          setImplLocked(((Alarm)localObject2).type, ((Alarm)localObject2).when + l1, l2, ((Alarm)localObject2).windowLength, maxTriggerTime(paramLong1, l2, ((Alarm)localObject2).repeatInterval), ((Alarm)localObject2).repeatInterval, ((Alarm)localObject2).operation, null, null, ((Alarm)localObject2).flags, true, ((Alarm)localObject2).workSource, ((Alarm)localObject2).alarmClock, ((Alarm)localObject2).uid, ((Alarm)localObject2).packageName);
        }
        if (((Alarm)localObject2).wakeup) {
          bool1 = true;
        }
        bool2 = bool1;
        if (((Alarm)localObject2).alarmClock == null) {
          break;
        }
        this.mNextAlarmClockMayChange = true;
        bool2 = bool1;
        break;
      }
      label670:
      if (str != null) {
        Slog.v("AlarmManager", "Triggering alarm #" + i + ": " + ((Alarm)localObject1).type + " when =" + ((Alarm)localObject1).when + " package=" + ((Alarm)localObject1).packageName + "operation =" + " null" + " listenTag =" + str);
      }
    }
    label770:
    return bool2;
  }
  
  private static class Alarm
  {
    public final AlarmManager.AlarmClockInfo alarmClock;
    public int count;
    public final int creatorUid;
    public final int flags;
    public final IAlarmListener listener;
    public final String listenerTag;
    public long maxWhenElapsed;
    public final PendingIntent operation;
    public long origWhen;
    public final String packageName;
    public AlarmManagerService.PriorityClass priorityClass;
    public long repeatInterval;
    public final String statsTag;
    public final int type;
    public final int uid;
    public final boolean wakeup;
    public long when;
    public long whenElapsed;
    public long windowLength;
    public final WorkSource workSource;
    
    public Alarm(int paramInt1, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, String paramString1, WorkSource paramWorkSource, int paramInt2, AlarmManager.AlarmClockInfo paramAlarmClockInfo, int paramInt3, String paramString2)
    {
      this.type = paramInt1;
      this.origWhen = paramLong1;
      boolean bool;
      if ((paramInt1 == 2) || (paramInt1 == 0))
      {
        bool = true;
        this.wakeup = bool;
        this.when = paramLong1;
        this.whenElapsed = paramLong2;
        this.windowLength = paramLong3;
        this.maxWhenElapsed = paramLong4;
        this.repeatInterval = paramLong5;
        this.operation = paramPendingIntent;
        this.listener = paramIAlarmListener;
        this.listenerTag = paramString1;
        this.statsTag = makeTag(paramPendingIntent, paramString1, paramInt1);
        this.workSource = paramWorkSource;
        if (!this.statsTag.contains("io.rong.push.intent.action.HEART_BEAT")) {
          break label174;
        }
        this.flags = (paramInt2 & 0xFFFFFFFB);
        label118:
        this.alarmClock = paramAlarmClockInfo;
        this.uid = paramInt3;
        this.packageName = paramString2;
        if (this.operation == null) {
          break label183;
        }
      }
      label174:
      label183:
      for (paramInt1 = this.operation.getCreatorUid();; paramInt1 = this.uid)
      {
        this.creatorUid = paramInt1;
        return;
        if (paramInt1 == 5)
        {
          bool = true;
          break;
        }
        bool = false;
        break;
        this.flags = paramInt2;
        break label118;
      }
    }
    
    public static String makeTag(PendingIntent paramPendingIntent, String paramString, int paramInt)
    {
      if ((paramInt == 2) || (paramInt == 0)) {}
      for (String str = "*walarm*:";; str = "*alarm*:")
      {
        if (paramPendingIntent == null) {
          break label33;
        }
        return paramPendingIntent.getTag(str);
        if (paramInt == 5) {
          break;
        }
      }
      label33:
      return str + paramString;
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString, long paramLong1, long paramLong2, SimpleDateFormat paramSimpleDateFormat)
    {
      int i;
      if ((this.type == 1) || (this.type == 0))
      {
        i = 1;
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("tag=");
        paramPrintWriter.println(this.statsTag);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("type=");
        paramPrintWriter.print(this.type);
        paramPrintWriter.print(" whenElapsed=");
        TimeUtils.formatDuration(this.whenElapsed, paramLong2, paramPrintWriter);
        paramPrintWriter.print(" when=");
        if (i == 0) {
          break label316;
        }
        paramPrintWriter.print(paramSimpleDateFormat.format(new Date(this.when)));
      }
      for (;;)
      {
        paramPrintWriter.println();
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("window=");
        TimeUtils.formatDuration(this.windowLength, paramPrintWriter);
        paramPrintWriter.print(" repeatInterval=");
        paramPrintWriter.print(this.repeatInterval);
        paramPrintWriter.print(" count=");
        paramPrintWriter.print(this.count);
        paramPrintWriter.print(" flags=0x");
        paramPrintWriter.println(Integer.toHexString(this.flags));
        if (this.alarmClock != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Alarm clock:");
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  triggerTime=");
          paramPrintWriter.println(paramSimpleDateFormat.format(new Date(this.alarmClock.getTriggerTime())));
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  showIntent=");
          paramPrintWriter.println(this.alarmClock.getShowIntent());
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("operation=");
        paramPrintWriter.println(this.operation);
        if (this.listener != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("listener=");
          paramPrintWriter.println(this.listener.asBinder());
        }
        return;
        if (this.type == 5)
        {
          i = 1;
          break;
        }
        i = 0;
        break;
        label316:
        TimeUtils.formatDuration(this.when, paramLong2, paramPrintWriter);
      }
    }
    
    public AlarmManagerService.WakeupEvent makeWakeupEvent(long paramLong)
    {
      int i = this.creatorUid;
      if (this.operation != null) {}
      for (String str = this.operation.getIntent().getAction();; str = "<listener>:" + this.listenerTag) {
        return new AlarmManagerService.WakeupEvent(paramLong, i, str);
      }
    }
    
    public boolean matches(PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener)
    {
      if (this.operation != null) {
        return this.operation.equals(paramPendingIntent);
      }
      if (paramIAlarmListener != null) {
        return this.listener.asBinder().equals(paramIAlarmListener.asBinder());
      }
      return false;
    }
    
    public boolean matches(String paramString)
    {
      if (this.operation != null) {
        return paramString.equals(this.operation.getTargetPackage());
      }
      return paramString.equals(this.packageName);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Alarm{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" type ");
      localStringBuilder.append(this.type);
      localStringBuilder.append(" when ");
      localStringBuilder.append(this.when);
      localStringBuilder.append(" ");
      if (this.operation != null) {
        localStringBuilder.append(this.operation.getTargetPackage());
      }
      for (;;)
      {
        localStringBuilder.append('}');
        return localStringBuilder.toString();
        localStringBuilder.append(this.packageName);
      }
    }
  }
  
  private class AlarmHandler
    extends Handler
  {
    public static final int ALARM_EVENT = 1;
    public static final int DELEVER_DELAY_ALARM = 5;
    public static final int LISTENER_TIMEOUT = 3;
    public static final int REPORT_ALARMS_ACTIVE = 4;
    public static final int SEND_NEXT_ALARM_CLOCK_CHANGED = 2;
    
    public AlarmHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      }
      for (;;)
      {
        return;
        paramMessage = new ArrayList();
        long l1;
        int i;
        for (;;)
        {
          synchronized (AlarmManagerService.this.mLock)
          {
            l1 = System.currentTimeMillis();
            long l2 = SystemClock.elapsedRealtime();
            AlarmManagerService.this.triggerAlarmsLocked(paramMessage, l2, l1);
            AlarmManagerService.-wrap7(AlarmManagerService.this);
            i = 0;
            if (i >= paramMessage.size()) {
              break;
            }
            ??? = (AlarmManagerService.Alarm)paramMessage.get(i);
          }
          try
          {
            ((AlarmManagerService.Alarm)???).operation.send();
            i += 1;
            continue;
            paramMessage = finally;
            throw paramMessage;
          }
          catch (PendingIntent.CanceledException localCanceledException)
          {
            for (;;)
            {
              if (((AlarmManagerService.Alarm)???).repeatInterval > 0L) {
                AlarmManagerService.this.removeImpl(((AlarmManagerService.Alarm)???).operation);
              }
            }
          }
        }
        AlarmManagerService.-wrap6(AlarmManagerService.this);
        return;
        AlarmManagerService.this.mDeliveryTracker.alarmTimedOut((IBinder)paramMessage.obj);
        return;
        if (AlarmManagerService.this.mLocalDeviceIdleController != null)
        {
          ??? = AlarmManagerService.this.mLocalDeviceIdleController;
          if (paramMessage.arg1 != 0) {}
          for (boolean bool = true;; bool = false)
          {
            ((DeviceIdleController.LocalService)???).setAlarmsActive(bool);
            return;
          }
          l1 = SystemClock.elapsedRealtime();
          paramMessage = (String)paramMessage.obj;
          ??? = new ArrayList();
          i = 0;
          while (i < AlarmManagerService.-get3(AlarmManagerService.this).size())
          {
            ??? = (String)AlarmManagerService.-get3(AlarmManagerService.this).valueAt(i);
            if ((??? != null) && (((String)???).equals(paramMessage)))
            {
              ??? = (AlarmManagerService.Alarm)AlarmManagerService.-get3(AlarmManagerService.this).keyAt(i);
              ((ArrayList)???).add(???);
              Slog.v("AlarmManager", " deliverAlarmsLocked  operation =" + ((AlarmManagerService.Alarm)???).statsTag);
            }
            i += 1;
          }
          synchronized (AlarmManagerService.this.mLock)
          {
            AlarmManagerService.this.deliverAlarmsLocked((ArrayList)???, l1);
            i = 0;
            if (i < ((ArrayList)???).size())
            {
              AlarmManagerService.-get3(AlarmManagerService.this).remove(((ArrayList)???).get(i));
              i += 1;
            }
          }
          while (i < AlarmManagerService.this.mUidOperation.size())
          {
            if (((String)AlarmManagerService.this.mUidOperation.keyAt(i)).equals(paramMessage))
            {
              ??? = (ArrayList)AlarmManagerService.this.mUidOperation.valueAt(i);
              int j = 0;
              while (j < ((ArrayList)???).size())
              {
                ??? = (String)((ArrayList)???).get(j);
                AlarmManagerService.-get2(AlarmManagerService.this).remove(???);
                AlarmManagerService.this.mOperationCounts.remove(???);
                j += 1;
              }
            }
            i += 1;
          }
        }
      }
    }
  }
  
  private class AlarmThread
    extends Thread
  {
    public AlarmThread()
    {
      super();
    }
    
    public void run()
    {
      ArrayList localArrayList1 = new ArrayList();
      int j = AlarmManagerService.-wrap4(AlarmManagerService.this, AlarmManagerService.this.mNativeData);
      AlarmManagerService.-set0(AlarmManagerService.this, SystemClock.elapsedRealtime());
      localArrayList1.clear();
      long l2 = System.currentTimeMillis();
      long l1 = SystemClock.elapsedRealtime();
      int i = j;
      if ((0x10000 & j) != 0) {}
      long l4;
      label119:
      Object localObject5;
      synchronized (AlarmManagerService.this.mLock)
      {
        long l3 = AlarmManagerService.this.mLastTimeChangeClockTime;
        l4 = AlarmManagerService.this.mLastTimeChangeRealtime;
        l4 = l3 + (l1 - l4);
        if ((l3 == 0L) || (l2 < l4 - 500L))
        {
          if (AlarmManagerService.DEBUG_BATCH) {
            Slog.v("AlarmManager", "Time changed notification from kernel; rebatching");
          }
          AlarmManagerService.this.removeImpl(AlarmManagerService.this.mTimeTickSender);
          AlarmManagerService.this.removeImpl(AlarmManagerService.this.mDateChangeSender);
          AlarmManagerService.this.rebatchAllAlarms();
          AlarmManagerService.this.mClockReceiver.scheduleTimeTickEvent();
          AlarmManagerService.this.mClockReceiver.scheduleDateChangedEvent();
          synchronized (AlarmManagerService.this.mLock)
          {
            localObject5 = AlarmManagerService.this;
            ((AlarmManagerService)localObject5).mNumTimeChanged += 1;
            AlarmManagerService.this.mLastTimeChangeClockTime = l2;
            AlarmManagerService.this.mLastTimeChangeRealtime = l1;
            ??? = new Intent("android.intent.action.TIME_SET");
            ((Intent)???).addFlags(872415232);
            AlarmManagerService.this.getContext().sendBroadcastAsUser((Intent)???, UserHandle.ALL);
            i = j | 0x25;
            label277:
            if (i == 65536) {}
          }
        }
      }
      for (;;)
      {
        synchronized (AlarmManagerService.this.mLock)
        {
          if (AlarmManagerService.localLOGV) {
            Slog.v("AlarmManager", "Checking for alarms... rtc=" + l2 + ", elapsed=" + l1);
          }
          if ((AlarmManagerService.WAKEUP_STATS) && ((i & 0x25) != 0))
          {
            i = 0;
            localObject5 = AlarmManagerService.this.mRecentWakeups.iterator();
            if (!((Iterator)localObject5).hasNext()) {
              break label790;
            }
            if (((AlarmManagerService.WakeupEvent)((Iterator)localObject5).next()).when > l2 - 86400000L)
            {
              break label790;
              if (j >= i) {
                continue;
              }
              AlarmManagerService.this.mRecentWakeups.remove();
              j += 1;
              continue;
              localObject1 = finally;
              throw ((Throwable)localObject1);
              i = j;
              if (l2 <= 500L + l4) {
                break label277;
              }
              break label119;
              localArrayList2 = finally;
              throw localArrayList2;
            }
            i += 1;
            continue;
            AlarmManagerService.this.recordWakeupAlarms(AlarmManagerService.this.mAlarmBatches, l1, l2);
          }
          if ((!AlarmManagerService.this.triggerAlarmsLocked(localArrayList2, l1, l2)) && (AlarmManagerService.this.checkAllowNonWakeupDelayLocked(l1)))
          {
            if (AlarmManagerService.this.mPendingNonWakeupAlarms.size() == 0)
            {
              AlarmManagerService.this.mStartCurrentDelayTime = l1;
              AlarmManagerService.this.mNextNonWakeupDeliveryTime = (AlarmManagerService.this.currentNonWakeupFuzzLocked(l1) * 3L / 2L + l1);
            }
            AlarmManagerService.this.mPendingNonWakeupAlarms.addAll(localArrayList2);
            localObject5 = AlarmManagerService.this;
            ((AlarmManagerService)localObject5).mNumDelayedAlarms += localArrayList2.size();
            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
            AlarmManagerService.-wrap7(AlarmManagerService.this);
            break;
          }
          AlarmManagerService.this.rescheduleKernelAlarmsLocked();
          AlarmManagerService.-wrap7(AlarmManagerService.this);
          if (AlarmManagerService.this.mPendingNonWakeupAlarms.size() > 0)
          {
            AlarmManagerService.this.calculateDeliveryPriorities(AlarmManagerService.this.mPendingNonWakeupAlarms);
            localArrayList2.addAll(AlarmManagerService.this.mPendingNonWakeupAlarms);
            Collections.sort(localArrayList2, AlarmManagerService.this.mAlarmDispatchComparator);
            l2 = l1 - AlarmManagerService.this.mStartCurrentDelayTime;
            localObject5 = AlarmManagerService.this;
            ((AlarmManagerService)localObject5).mTotalDelayTime += l2;
            if (AlarmManagerService.this.mMaxDelayTime < l2) {
              AlarmManagerService.this.mMaxDelayTime = l2;
            }
            AlarmManagerService.this.mPendingNonWakeupAlarms.clear();
          }
          AlarmManagerService.this.deliverAlarmsLocked(localArrayList2, l1);
        }
        synchronized (AlarmManagerService.this.mLock)
        {
          AlarmManagerService.this.rescheduleKernelAlarmsLocked();
        }
        label790:
        j = 0;
      }
    }
  }
  
  final class Batch
  {
    final ArrayList<AlarmManagerService.Alarm> alarms = new ArrayList();
    long end;
    int flags;
    long start;
    
    Batch()
    {
      this.start = 0L;
      this.end = Long.MAX_VALUE;
      this.flags = 0;
    }
    
    Batch(AlarmManagerService.Alarm paramAlarm)
    {
      this.start = paramAlarm.whenElapsed;
      this.end = paramAlarm.maxWhenElapsed;
      this.flags = paramAlarm.flags;
      this.alarms.add(paramAlarm);
    }
    
    boolean add(AlarmManagerService.Alarm paramAlarm)
    {
      boolean bool = false;
      int j = Collections.binarySearch(this.alarms, paramAlarm, AlarmManagerService.sIncreasingTimeOrder);
      int i = j;
      if (j < 0) {
        i = 0 - j - 1;
      }
      this.alarms.add(i, paramAlarm);
      if (AlarmManagerService.DEBUG_BATCH) {
        Slog.v("AlarmManager", "Adding " + paramAlarm + " to " + this);
      }
      if (paramAlarm.whenElapsed > this.start)
      {
        this.start = paramAlarm.whenElapsed;
        bool = true;
      }
      if (paramAlarm.maxWhenElapsed < this.end) {
        this.end = paramAlarm.maxWhenElapsed;
      }
      this.flags |= paramAlarm.flags;
      if (AlarmManagerService.DEBUG_BATCH) {
        Slog.v("AlarmManager", "    => now " + this);
      }
      return bool;
    }
    
    boolean canHold(long paramLong1, long paramLong2)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.end >= paramLong1)
      {
        bool1 = bool2;
        if (this.start <= paramLong2) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    AlarmManagerService.Alarm get(int paramInt)
    {
      return (AlarmManagerService.Alarm)this.alarms.get(paramInt);
    }
    
    AlarmManagerService.Alarm getAlarmByElapsedTime(long paramLong)
    {
      AlarmManagerService.Alarm localAlarm = null;
      int i = 0;
      while (i < this.alarms.size())
      {
        if (((AlarmManagerService.Alarm)this.alarms.get(i)).whenElapsed == paramLong) {
          localAlarm = (AlarmManagerService.Alarm)this.alarms.get(i);
        }
        i += 1;
      }
      return localAlarm;
    }
    
    boolean hasPackage(String paramString)
    {
      int j = this.alarms.size();
      int i = 0;
      while (i < j)
      {
        if (((AlarmManagerService.Alarm)this.alarms.get(i)).matches(paramString)) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    boolean hasWakeups()
    {
      int j = this.alarms.size();
      int i = 0;
      while (i < j)
      {
        if ((((AlarmManagerService.Alarm)this.alarms.get(i)).type & 0x1) == 0) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    boolean isRtcPowerOffWakeup()
    {
      int j = this.alarms.size();
      int i = 0;
      while (i < j)
      {
        if (((AlarmManagerService.Alarm)this.alarms.get(i)).type == 5) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    boolean remove(int paramInt)
    {
      boolean bool1 = false;
      long l3 = 0L;
      long l1 = Long.MAX_VALUE;
      int i = 0;
      while (i < this.alarms.size())
      {
        AlarmManagerService.Alarm localAlarm = (AlarmManagerService.Alarm)this.alarms.get(i);
        if (UserHandle.getUserId(localAlarm.creatorUid) == paramInt)
        {
          this.alarms.remove(i);
          boolean bool2 = true;
          bool1 = bool2;
          if (localAlarm.alarmClock != null)
          {
            AlarmManagerService.-set1(AlarmManagerService.this, true);
            bool1 = bool2;
          }
        }
        else
        {
          long l2 = l3;
          if (localAlarm.whenElapsed > l3) {
            l2 = localAlarm.whenElapsed;
          }
          l3 = l1;
          if (localAlarm.maxWhenElapsed < l1) {
            l3 = localAlarm.maxWhenElapsed;
          }
          i += 1;
          l1 = l3;
          l3 = l2;
        }
      }
      if (bool1)
      {
        this.start = l3;
        this.end = l1;
      }
      return bool1;
    }
    
    boolean remove(PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener)
    {
      if ((paramPendingIntent == null) && (paramIAlarmListener == null))
      {
        if (AlarmManagerService.localLOGV) {
          Slog.w("AlarmManager", "requested remove() of null operation", new RuntimeException("here"));
        }
        return false;
      }
      boolean bool1 = false;
      long l3 = 0L;
      long l1 = Long.MAX_VALUE;
      int j = 0;
      int i = 0;
      while (i < this.alarms.size())
      {
        AlarmManagerService.Alarm localAlarm = (AlarmManagerService.Alarm)this.alarms.get(i);
        if (localAlarm.matches(paramPendingIntent, paramIAlarmListener))
        {
          this.alarms.remove(i);
          boolean bool2 = true;
          bool1 = bool2;
          if (localAlarm.alarmClock != null)
          {
            AlarmManagerService.-set1(AlarmManagerService.this, true);
            bool1 = bool2;
          }
        }
        else
        {
          long l2 = l3;
          if (localAlarm.whenElapsed > l3) {
            l2 = localAlarm.whenElapsed;
          }
          l3 = l1;
          if (localAlarm.maxWhenElapsed < l1) {
            l3 = localAlarm.maxWhenElapsed;
          }
          j |= localAlarm.flags;
          i += 1;
          l1 = l3;
          l3 = l2;
        }
      }
      if (bool1)
      {
        this.start = l3;
        this.end = l1;
        this.flags = j;
      }
      return bool1;
    }
    
    boolean remove(String paramString)
    {
      if (paramString == null)
      {
        if (AlarmManagerService.localLOGV) {
          Slog.w("AlarmManager", "requested remove() of null packageName", new RuntimeException("here"));
        }
        return false;
      }
      boolean bool1 = false;
      long l2 = 0L;
      long l1 = Long.MAX_VALUE;
      int j = 0;
      int i = this.alarms.size() - 1;
      if (i >= 0)
      {
        AlarmManagerService.Alarm localAlarm = (AlarmManagerService.Alarm)this.alarms.get(i);
        int k;
        long l4;
        long l3;
        if (localAlarm.matches(paramString))
        {
          this.alarms.remove(i);
          boolean bool2 = true;
          bool1 = bool2;
          k = j;
          l4 = l1;
          l3 = l2;
          if (localAlarm.alarmClock != null)
          {
            AlarmManagerService.-set1(AlarmManagerService.this, true);
            l3 = l2;
            l4 = l1;
            k = j;
            bool1 = bool2;
          }
        }
        for (;;)
        {
          i -= 1;
          j = k;
          l1 = l4;
          l2 = l3;
          break;
          l3 = l2;
          if (localAlarm.whenElapsed > l2) {
            l3 = localAlarm.whenElapsed;
          }
          l2 = l1;
          if (localAlarm.maxWhenElapsed < l1) {
            l2 = localAlarm.maxWhenElapsed;
          }
          k = j | localAlarm.flags;
          l4 = l2;
        }
      }
      if (bool1)
      {
        this.start = l2;
        this.end = l1;
        this.flags = j;
      }
      return bool1;
    }
    
    boolean removeForStopped(int paramInt)
    {
      boolean bool2 = false;
      long l2 = 0L;
      long l1 = Long.MAX_VALUE;
      int j = 0;
      int i = this.alarms.size() - 1;
      boolean bool1;
      long l4;
      long l5;
      label358:
      for (;;)
      {
        AlarmManagerService.Alarm localAlarm;
        if (i >= 0)
        {
          localAlarm = (AlarmManagerService.Alarm)this.alarms.get(i);
          bool1 = bool2;
          l4 = l1;
          l5 = l2;
        }
        try
        {
          if (localAlarm.uid == paramInt)
          {
            bool1 = bool2;
            l4 = l1;
            l5 = l2;
            if (ActivityManagerNative.getDefault().getAppStartMode(paramInt, localAlarm.packageName) == 2)
            {
              bool1 = bool2;
              l4 = l1;
              l5 = l2;
              this.alarms.remove(i);
              boolean bool4 = true;
              boolean bool3 = true;
              bool1 = bool3;
              l4 = l1;
              l5 = l2;
              bool2 = bool4;
              l6 = l1;
              k = j;
              l3 = l2;
              if (localAlarm.alarmClock == null) {
                break label358;
              }
              bool1 = bool3;
              l4 = l1;
              l5 = l2;
              AlarmManagerService.-set1(AlarmManagerService.this, true);
              bool2 = bool4;
              l6 = l1;
              k = j;
              l3 = l2;
              break label358;
            }
          }
          l3 = l2;
          bool1 = bool2;
          l4 = l1;
          l5 = l2;
          if (localAlarm.whenElapsed > l2)
          {
            bool1 = bool2;
            l4 = l1;
            l5 = l2;
            l3 = localAlarm.whenElapsed;
          }
          l2 = l1;
          bool1 = bool2;
          l4 = l1;
          l5 = l3;
          if (localAlarm.maxWhenElapsed < l1)
          {
            bool1 = bool2;
            l4 = l1;
            l5 = l3;
            l2 = localAlarm.maxWhenElapsed;
          }
          bool1 = bool2;
          l4 = l2;
          l5 = l3;
          k = localAlarm.flags;
          k = j | k;
          l6 = l2;
        }
        catch (RemoteException localRemoteException)
        {
          long l3 = l5;
          int k = j;
          long l6 = l4;
          bool2 = bool1;
          i -= 1;
          l1 = l6;
          j = k;
          l2 = l3;
        }
        if (bool2)
        {
          this.start = l2;
          this.end = l1;
          this.flags = j;
        }
        return bool2;
      }
    }
    
    int size()
    {
      return this.alarms.size();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(40);
      localStringBuilder.append("Batch{");
      localStringBuilder.append(Integer.toHexString(hashCode()));
      localStringBuilder.append(" num=");
      localStringBuilder.append(size());
      localStringBuilder.append(" start=");
      localStringBuilder.append(this.start);
      localStringBuilder.append(" end=");
      localStringBuilder.append(this.end);
      if (this.flags != 0)
      {
        localStringBuilder.append(" flgs=0x");
        localStringBuilder.append(Integer.toHexString(this.flags));
      }
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  static class BatchTimeOrder
    implements Comparator<AlarmManagerService.Batch>
  {
    public int compare(AlarmManagerService.Batch paramBatch1, AlarmManagerService.Batch paramBatch2)
    {
      long l1 = paramBatch1.start;
      long l2 = paramBatch2.start;
      if (l1 > l2) {
        return 1;
      }
      if (l1 < l2) {
        return -1;
      }
      return 0;
    }
  }
  
  static final class BroadcastStats
  {
    long aggregateTime;
    int count;
    final ArrayMap<String, AlarmManagerService.FilterStats> filterStats = new ArrayMap();
    final String mPackageName;
    final int mUid;
    int nesting;
    int numWakeup;
    long startTime;
    
    BroadcastStats(int paramInt, String paramString)
    {
      this.mUid = paramInt;
      this.mPackageName = paramString;
    }
  }
  
  class ClockReceiver
    extends BroadcastReceiver
  {
    public ClockReceiver()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIME_TICK");
      localIntentFilter.addAction("android.intent.action.DATE_CHANGED");
      AlarmManagerService.this.getContext().registerReceiver(this, localIntentFilter);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (paramIntent.getAction().equals("android.intent.action.TIME_TICK"))
      {
        if (AlarmManagerService.DEBUG_BATCH) {
          Slog.v("AlarmManager", "Received TIME_TICK alarm; rescheduling");
        }
        scheduleTimeTickEvent();
      }
      while (!paramIntent.getAction().equals("android.intent.action.DATE_CHANGED")) {
        return;
      }
      int i = TimeZone.getTimeZone(SystemProperties.get("persist.sys.timezone")).getOffset(System.currentTimeMillis());
      AlarmManagerService.-wrap3(AlarmManagerService.this, AlarmManagerService.this.mNativeData, -(i / 60000));
      scheduleDateChangedEvent();
    }
    
    public void scheduleDateChangedEvent()
    {
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTimeInMillis(System.currentTimeMillis());
      localCalendar.set(11, 0);
      localCalendar.set(12, 0);
      localCalendar.set(13, 0);
      localCalendar.set(14, 0);
      localCalendar.add(5, 1);
      AlarmManagerService.this.setImpl(1, localCalendar.getTimeInMillis(), 0L, 0L, AlarmManagerService.this.mDateChangeSender, null, null, 1, null, null, Process.myUid(), "android");
    }
    
    public void scheduleTimeTickEvent()
    {
      long l1 = System.currentTimeMillis();
      long l2 = l1 / 60000L;
      AlarmManagerService.this.setImpl(3, SystemClock.elapsedRealtime() + (60000L * (l2 + 1L) - l1), 0L, 0L, AlarmManagerService.this.mTimeTickSender, null, null, 1, null, null, Process.myUid(), "android");
    }
  }
  
  private final class Constants
    extends ContentObserver
  {
    private static final long DEFAULT_ALLOW_WHILE_IDLE_LONG_TIME = 720000L;
    private static final long DEFAULT_ALLOW_WHILE_IDLE_SHORT_TIME = 5000L;
    private static final long DEFAULT_ALLOW_WHILE_IDLE_WHITELIST_DURATION = 10000L;
    private static final long DEFAULT_LISTENER_TIMEOUT = 5000L;
    private static final long DEFAULT_MIN_FUTURITY = 5000L;
    private static final long DEFAULT_MIN_INTERVAL = 60000L;
    private static final String KEY_ALLOW_WHILE_IDLE_LONG_TIME = "allow_while_idle_long_time";
    private static final String KEY_ALLOW_WHILE_IDLE_SHORT_TIME = "allow_while_idle_short_time";
    private static final String KEY_ALLOW_WHILE_IDLE_WHITELIST_DURATION = "allow_while_idle_whitelist_duration";
    private static final String KEY_LISTENER_TIMEOUT = "listener_timeout";
    private static final String KEY_MIN_FUTURITY = "min_futurity";
    private static final String KEY_MIN_INTERVAL = "min_interval";
    public long ALLOW_WHILE_IDLE_LONG_TIME = 720000L;
    public long ALLOW_WHILE_IDLE_SHORT_TIME = 5000L;
    public long ALLOW_WHILE_IDLE_WHITELIST_DURATION = 10000L;
    public long LISTENER_TIMEOUT = 5000L;
    public long MIN_FUTURITY = 5000L;
    public long MIN_INTERVAL = 60000L;
    private long mLastAllowWhileIdleWhitelistDuration = -1L;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private ContentResolver mResolver;
    
    public Constants(Handler paramHandler)
    {
      super();
      updateAllowWhileIdleMinTimeLocked();
      updateAllowWhileIdleWhitelistDurationLocked();
    }
    
    private void updateConstants()
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        try
        {
          this.mParser.setString(Settings.Global.getString(this.mResolver, "alarm_manager_constants"));
          this.MIN_FUTURITY = this.mParser.getLong("min_futurity", 5000L);
          this.MIN_INTERVAL = this.mParser.getLong("min_interval", 60000L);
          this.ALLOW_WHILE_IDLE_SHORT_TIME = this.mParser.getLong("allow_while_idle_short_time", 5000L);
          this.ALLOW_WHILE_IDLE_LONG_TIME = this.mParser.getLong("allow_while_idle_long_time", 720000L);
          this.ALLOW_WHILE_IDLE_WHITELIST_DURATION = this.mParser.getLong("allow_while_idle_whitelist_duration", 10000L);
          this.LISTENER_TIMEOUT = this.mParser.getLong("listener_timeout", 5000L);
          updateAllowWhileIdleMinTimeLocked();
          updateAllowWhileIdleWhitelistDurationLocked();
          return;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            Slog.e("AlarmManager", "Bad device idle settings", localIllegalArgumentException);
          }
        }
      }
    }
    
    void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("  Settings:");
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_futurity");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MIN_FUTURITY, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("min_interval");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.MIN_INTERVAL, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("listener_timeout");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.LISTENER_TIMEOUT, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("allow_while_idle_short_time");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_SHORT_TIME, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("allow_while_idle_long_time");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_LONG_TIME, paramPrintWriter);
      paramPrintWriter.println();
      paramPrintWriter.print("    ");
      paramPrintWriter.print("allow_while_idle_whitelist_duration");
      paramPrintWriter.print("=");
      TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_WHITELIST_DURATION, paramPrintWriter);
      paramPrintWriter.println();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      updateConstants();
    }
    
    public void start(ContentResolver paramContentResolver)
    {
      this.mResolver = paramContentResolver;
      this.mResolver.registerContentObserver(Settings.Global.getUriFor("alarm_manager_constants"), false, this);
      updateConstants();
    }
    
    public void updateAllowWhileIdleMinTimeLocked()
    {
      AlarmManagerService localAlarmManagerService = AlarmManagerService.this;
      if (AlarmManagerService.this.mPendingIdleUntil != null) {}
      for (long l = this.ALLOW_WHILE_IDLE_LONG_TIME;; l = this.ALLOW_WHILE_IDLE_SHORT_TIME)
      {
        localAlarmManagerService.mAllowWhileIdleMinTime = l;
        return;
      }
    }
    
    public void updateAllowWhileIdleWhitelistDurationLocked()
    {
      if (this.mLastAllowWhileIdleWhitelistDuration != this.ALLOW_WHILE_IDLE_WHITELIST_DURATION)
      {
        this.mLastAllowWhileIdleWhitelistDuration = this.ALLOW_WHILE_IDLE_WHITELIST_DURATION;
        BroadcastOptions localBroadcastOptions = BroadcastOptions.makeBasic();
        localBroadcastOptions.setTemporaryAppWhitelistDuration(this.ALLOW_WHILE_IDLE_WHITELIST_DURATION);
        AlarmManagerService.this.mIdleOptions = localBroadcastOptions.toBundle();
      }
    }
  }
  
  class DeliveryTracker
    extends IAlarmCompleteListener.Stub
    implements PendingIntent.OnFinished
  {
    DeliveryTracker() {}
    
    private AlarmManagerService.InFlight removeLocked(PendingIntent paramPendingIntent, Intent paramIntent)
    {
      int i = 0;
      while (i < AlarmManagerService.this.mInFlight.size())
      {
        if (((AlarmManagerService.InFlight)AlarmManagerService.this.mInFlight.get(i)).mPendingIntent == paramPendingIntent) {
          return (AlarmManagerService.InFlight)AlarmManagerService.this.mInFlight.remove(i);
        }
        i += 1;
      }
      AlarmManagerService.this.mLog.w("No in-flight alarm for " + paramPendingIntent + " " + paramIntent);
      return null;
    }
    
    private AlarmManagerService.InFlight removeLocked(IBinder paramIBinder)
    {
      int i = 0;
      while (i < AlarmManagerService.this.mInFlight.size())
      {
        if (((AlarmManagerService.InFlight)AlarmManagerService.this.mInFlight.get(i)).mListener == paramIBinder) {
          return (AlarmManagerService.InFlight)AlarmManagerService.this.mInFlight.remove(i);
        }
        i += 1;
      }
      AlarmManagerService.this.mLog.w("No in-flight alarm for listener " + paramIBinder);
      return null;
    }
    
    private void updateStatsLocked(AlarmManagerService.InFlight paramInFlight)
    {
      long l = SystemClock.elapsedRealtime();
      Object localObject = paramInFlight.mBroadcastStats;
      ((AlarmManagerService.BroadcastStats)localObject).nesting -= 1;
      if (((AlarmManagerService.BroadcastStats)localObject).nesting <= 0)
      {
        ((AlarmManagerService.BroadcastStats)localObject).nesting = 0;
        ((AlarmManagerService.BroadcastStats)localObject).aggregateTime += l - ((AlarmManagerService.BroadcastStats)localObject).startTime;
      }
      localObject = paramInFlight.mFilterStats;
      ((AlarmManagerService.FilterStats)localObject).nesting -= 1;
      if (((AlarmManagerService.FilterStats)localObject).nesting <= 0)
      {
        ((AlarmManagerService.FilterStats)localObject).nesting = 0;
        ((AlarmManagerService.FilterStats)localObject).aggregateTime += l - ((AlarmManagerService.FilterStats)localObject).startTime;
      }
      int i;
      if ((paramInFlight.mWorkSource != null) && (paramInFlight.mWorkSource.size() > 0)) {
        i = 0;
      }
      while (i < paramInFlight.mWorkSource.size())
      {
        ActivityManagerNative.noteAlarmFinish(paramInFlight.mPendingIntent, paramInFlight.mWorkSource.get(i), paramInFlight.mTag);
        i += 1;
        continue;
        ActivityManagerNative.noteAlarmFinish(paramInFlight.mPendingIntent, paramInFlight.mUid, paramInFlight.mTag);
      }
    }
    
    private void updateTrackingLocked(AlarmManagerService.InFlight paramInFlight)
    {
      if (paramInFlight != null)
      {
        updateStatsLocked(paramInFlight);
        AlarmManagerService.-get4(AlarmManagerService.this).removeTriggeredUid(paramInFlight.mUid);
      }
      paramInFlight = AlarmManagerService.this;
      paramInFlight.mBroadcastRefCount -= 1;
      if (AlarmManagerService.this.mBroadcastRefCount == 0)
      {
        AlarmManagerService.this.mHandler.obtainMessage(4, Integer.valueOf(0)).sendToTarget();
        if (AlarmManagerService.this.mWakeLock.isHeld()) {
          AlarmManagerService.this.mWakeLock.release();
        }
        if (AlarmManagerService.this.mInFlight.size() > 0)
        {
          AlarmManagerService.this.mLog.w("Finished all dispatches with " + AlarmManagerService.this.mInFlight.size() + " remaining inflights");
          int i = 0;
          while (i < AlarmManagerService.this.mInFlight.size())
          {
            AlarmManagerService.this.mLog.w("  Remaining #" + i + ": " + AlarmManagerService.this.mInFlight.get(i));
            i += 1;
          }
          AlarmManagerService.this.mInFlight.clear();
        }
        return;
      }
      if (AlarmManagerService.this.mInFlight.size() > 0)
      {
        paramInFlight = (AlarmManagerService.InFlight)AlarmManagerService.this.mInFlight.get(0);
        AlarmManagerService.this.setWakelockWorkSource(paramInFlight.mPendingIntent, paramInFlight.mWorkSource, paramInFlight.mAlarmType, paramInFlight.mTag, -1, false);
        return;
      }
      AlarmManagerService.this.mLog.w("Alarm wakelock still held but sent queue empty");
      AlarmManagerService.this.mWakeLock.setWorkSource(null);
    }
    
    /* Error */
    public void alarmComplete(IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_1
      //   1: ifnonnull +42 -> 43
      //   4: ldc -39
      //   6: new 50	java/lang/StringBuilder
      //   9: dup
      //   10: invokespecial 51	java/lang/StringBuilder:<init>	()V
      //   13: ldc -37
      //   15: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   18: invokestatic 224	android/os/Binder:getCallingUid	()I
      //   21: invokevirtual 191	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   24: ldc -30
      //   26: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   29: invokestatic 229	android/os/Binder:getCallingPid	()I
      //   32: invokevirtual 191	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   35: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   38: invokestatic 234	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   41: pop
      //   42: return
      //   43: invokestatic 237	android/os/Binder:clearCallingIdentity	()J
      //   46: lstore_2
      //   47: aload_0
      //   48: getfield 15	com/android/server/AlarmManagerService$DeliveryTracker:this$0	Lcom/android/server/AlarmManagerService;
      //   51: getfield 241	com/android/server/AlarmManagerService:mLock	Ljava/lang/Object;
      //   54: astore 4
      //   56: aload 4
      //   58: monitorenter
      //   59: aload_0
      //   60: getfield 15	com/android/server/AlarmManagerService$DeliveryTracker:this$0	Lcom/android/server/AlarmManagerService;
      //   63: getfield 156	com/android/server/AlarmManagerService:mHandler	Lcom/android/server/AlarmManagerService$AlarmHandler;
      //   66: iconst_3
      //   67: aload_1
      //   68: invokevirtual 245	com/android/server/AlarmManagerService$AlarmHandler:removeMessages	(ILjava/lang/Object;)V
      //   71: aload_0
      //   72: aload_1
      //   73: invokespecial 247	com/android/server/AlarmManagerService$DeliveryTracker:removeLocked	(Landroid/os/IBinder;)Lcom/android/server/AlarmManagerService$InFlight;
      //   76: astore 5
      //   78: aload 5
      //   80: ifnull +48 -> 128
      //   83: getstatic 251	com/android/server/AlarmManagerService:DEBUG_LISTENER_CALLBACK	Z
      //   86: ifeq +28 -> 114
      //   89: ldc -39
      //   91: new 50	java/lang/StringBuilder
      //   94: dup
      //   95: invokespecial 51	java/lang/StringBuilder:<init>	()V
      //   98: ldc -3
      //   100: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   103: aload_1
      //   104: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   107: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   110: invokestatic 256	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   113: pop
      //   114: aload_0
      //   115: aload 5
      //   117: invokespecial 258	com/android/server/AlarmManagerService$DeliveryTracker:updateTrackingLocked	(Lcom/android/server/AlarmManagerService$InFlight;)V
      //   120: aload 4
      //   122: monitorexit
      //   123: lload_2
      //   124: invokestatic 262	android/os/Binder:restoreCallingIdentity	(J)V
      //   127: return
      //   128: getstatic 251	com/android/server/AlarmManagerService:DEBUG_LISTENER_CALLBACK	Z
      //   131: ifeq -11 -> 120
      //   134: ldc -39
      //   136: new 50	java/lang/StringBuilder
      //   139: dup
      //   140: invokespecial 51	java/lang/StringBuilder:<init>	()V
      //   143: ldc_w 264
      //   146: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   149: aload_1
      //   150: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   153: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   156: invokestatic 256	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   159: pop
      //   160: goto -40 -> 120
      //   163: astore_1
      //   164: aload 4
      //   166: monitorexit
      //   167: aload_1
      //   168: athrow
      //   169: astore_1
      //   170: lload_2
      //   171: invokestatic 262	android/os/Binder:restoreCallingIdentity	(J)V
      //   174: aload_1
      //   175: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	176	0	this	DeliveryTracker
      //   0	176	1	paramIBinder	IBinder
      //   46	125	2	l	long
      //   76	40	5	localInFlight	AlarmManagerService.InFlight
      // Exception table:
      //   from	to	target	type
      //   59	78	163	finally
      //   83	114	163	finally
      //   114	120	163	finally
      //   128	160	163	finally
      //   47	59	169	finally
      //   120	123	169	finally
      //   164	169	169	finally
    }
    
    public void alarmTimedOut(IBinder paramIBinder)
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        AlarmManagerService.InFlight localInFlight = removeLocked(paramIBinder);
        if (localInFlight != null)
        {
          if (AlarmManagerService.DEBUG_LISTENER_CALLBACK) {
            Slog.i("AlarmManager", "Alarm listener " + paramIBinder + " timed out in delivery");
          }
          updateTrackingLocked(localInFlight);
        }
        while (!AlarmManagerService.DEBUG_LISTENER_CALLBACK) {
          return;
        }
        Slog.i("AlarmManager", "Spurious timeout of listener " + paramIBinder);
      }
    }
    
    public void deliverLocked(AlarmManagerService.Alarm paramAlarm, long paramLong, boolean paramBoolean)
    {
      if (paramAlarm.operation != null) {}
      for (;;)
      {
        Object localObject2;
        try
        {
          localObject2 = paramAlarm.operation;
          Object localObject3 = AlarmManagerService.this.getContext();
          Object localObject4 = AlarmManagerService.-get0(AlarmManagerService.this).putExtra("android.intent.extra.ALARM_COUNT", paramAlarm.count);
          DeliveryTracker localDeliveryTracker = AlarmManagerService.this.mDeliveryTracker;
          AlarmManagerService.AlarmHandler localAlarmHandler = AlarmManagerService.this.mHandler;
          Object localObject1;
          if (paramBoolean)
          {
            localObject1 = AlarmManagerService.this.mIdleOptions;
            ((PendingIntent)localObject2).send((Context)localObject3, 0, (Intent)localObject4, localDeliveryTracker, localAlarmHandler, null, (Bundle)localObject1);
            if (AlarmManagerService.localLOGV) {
              Slog.v("AlarmManager", "sending alarm " + paramAlarm + " success");
            }
            int j;
            if (AlarmManagerService.this.mBroadcastRefCount == 0)
            {
              localObject1 = AlarmManagerService.this;
              localObject2 = paramAlarm.operation;
              localObject3 = paramAlarm.workSource;
              j = paramAlarm.type;
              localObject4 = paramAlarm.statsTag;
              if (paramAlarm.operation == null)
              {
                i = paramAlarm.uid;
                ((AlarmManagerService)localObject1).setWakelockWorkSource((PendingIntent)localObject2, (WorkSource)localObject3, j, (String)localObject4, i, true);
                if (!AlarmManagerService.this.mWakeLock.isHeld()) {
                  AlarmManagerService.this.mWakeLock.acquire();
                }
                AlarmManagerService.this.mHandler.obtainMessage(4, Integer.valueOf(1)).sendToTarget();
              }
            }
            else
            {
              localObject1 = new AlarmManagerService.InFlight(AlarmManagerService.this, paramAlarm.operation, paramAlarm.listener, paramAlarm.workSource, paramAlarm.uid, paramAlarm.packageName, paramAlarm.type, paramAlarm.statsTag, paramLong);
              AlarmManagerService.this.mInFlight.add(localObject1);
              localObject2 = AlarmManagerService.this;
              ((AlarmManagerService)localObject2).mBroadcastRefCount += 1;
              localObject2 = AlarmManagerService.-get4(AlarmManagerService.this);
              if (paramAlarm.operation == null) {
                break label754;
              }
              i = paramAlarm.operation.getCreatorUid();
              ((QCNsrmAlarmExtension)localObject2).addTriggeredUid(i);
              if (paramBoolean) {
                AlarmManagerService.this.mLastAllowWhileIdleDispatch.put(paramAlarm.uid, paramLong);
              }
              localObject2 = ((AlarmManagerService.InFlight)localObject1).mBroadcastStats;
              ((AlarmManagerService.BroadcastStats)localObject2).count += 1;
              if (((AlarmManagerService.BroadcastStats)localObject2).nesting != 0) {
                break label763;
              }
              ((AlarmManagerService.BroadcastStats)localObject2).nesting = 1;
              ((AlarmManagerService.BroadcastStats)localObject2).startTime = paramLong;
              localObject1 = ((AlarmManagerService.InFlight)localObject1).mFilterStats;
              ((AlarmManagerService.FilterStats)localObject1).count += 1;
              if (((AlarmManagerService.FilterStats)localObject1).nesting != 0) {
                break label778;
              }
              ((AlarmManagerService.FilterStats)localObject1).nesting = 1;
              ((AlarmManagerService.FilterStats)localObject1).startTime = paramLong;
              if ((paramAlarm.type != 2) && (paramAlarm.type != 0)) {
                break label793;
              }
              ((AlarmManagerService.BroadcastStats)localObject2).numWakeup += 1;
              ((AlarmManagerService.FilterStats)localObject1).numWakeup += 1;
              if ((paramAlarm.workSource == null) || (paramAlarm.workSource.size() <= 0)) {
                break;
              }
              i = 0;
              if (i >= paramAlarm.workSource.size()) {
                break label801;
              }
              localObject1 = paramAlarm.workSource.getName(i);
              localObject2 = paramAlarm.operation;
              j = paramAlarm.workSource.get(i);
              if (localObject1 == null) {
                break label802;
              }
              ActivityManagerNative.noteWakeupAlarm((PendingIntent)localObject2, j, (String)localObject1, paramAlarm.statsTag);
              i += 1;
              continue;
            }
          }
          else
          {
            localObject1 = null;
            continue;
            try
            {
              if (AlarmManagerService.DEBUG_LISTENER_CALLBACK) {
                Slog.v("AlarmManager", "Alarm to uid=" + paramAlarm.uid + " listener=" + paramAlarm.listener.asBinder());
              }
              paramAlarm.listener.doAlarm(this);
              AlarmManagerService.this.mHandler.sendMessageDelayed(AlarmManagerService.this.mHandler.obtainMessage(3, paramAlarm.listener.asBinder()), AlarmManagerService.this.mConstants.LISTENER_TIMEOUT);
            }
            catch (Exception localException)
            {
              if (AlarmManagerService.DEBUG_LISTENER_CALLBACK) {
                Slog.i("AlarmManager", "Alarm undeliverable to listener " + paramAlarm.listener.asBinder(), localException);
              }
              return;
            }
          }
        }
        catch (PendingIntent.CanceledException localCanceledException)
        {
          if (paramAlarm.repeatInterval > 0L) {
            AlarmManagerService.this.removeImpl(paramAlarm.operation);
          }
          return;
        }
        int i = -1;
        continue;
        label754:
        i = paramAlarm.uid;
        continue;
        label763:
        ((AlarmManagerService.BroadcastStats)localObject2).nesting += 1;
        continue;
        label778:
        localException.nesting += 1;
        continue;
        label793:
        if (paramAlarm.type != 5)
        {
          label801:
          return;
          label802:
          String str = paramAlarm.packageName;
        }
      }
      ActivityManagerNative.noteWakeupAlarm(paramAlarm.operation, paramAlarm.uid, paramAlarm.packageName, paramAlarm.statsTag);
    }
    
    public void onSendFinished(PendingIntent paramPendingIntent, Intent paramIntent, int paramInt, String arg4, Bundle paramBundle)
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        updateTrackingLocked(removeLocked(paramPendingIntent, paramIntent));
        return;
      }
    }
  }
  
  static final class FilterStats
  {
    long aggregateTime;
    int count;
    long lastTime;
    final AlarmManagerService.BroadcastStats mBroadcastStats;
    final String mTag;
    int nesting;
    int numWakeup;
    long startTime;
    
    FilterStats(AlarmManagerService.BroadcastStats paramBroadcastStats, String paramString)
    {
      this.mBroadcastStats = paramBroadcastStats;
      this.mTag = paramString;
    }
  }
  
  static final class IdleDispatchEntry
  {
    long argRealtime;
    long elapsedRealtime;
    String op;
    String pkg;
    String tag;
    int uid;
  }
  
  static final class InFlight
  {
    final int mAlarmType;
    final AlarmManagerService.BroadcastStats mBroadcastStats;
    final AlarmManagerService.FilterStats mFilterStats;
    final IBinder mListener;
    final PendingIntent mPendingIntent;
    final String mTag;
    final int mUid;
    final WorkSource mWorkSource;
    
    InFlight(AlarmManagerService paramAlarmManagerService, PendingIntent paramPendingIntent, IAlarmListener paramIAlarmListener, WorkSource paramWorkSource, int paramInt1, String paramString1, int paramInt2, String paramString2, long paramLong)
    {
      this.mPendingIntent = paramPendingIntent;
      if (paramIAlarmListener != null) {
        localIBinder = paramIAlarmListener.asBinder();
      }
      this.mListener = localIBinder;
      this.mWorkSource = paramWorkSource;
      this.mUid = paramInt1;
      this.mTag = paramString2;
      if (paramPendingIntent != null) {}
      for (paramAlarmManagerService = AlarmManagerService.-wrap0(paramAlarmManagerService, paramPendingIntent);; paramAlarmManagerService = AlarmManagerService.-wrap1(paramAlarmManagerService, paramInt1, paramString1))
      {
        this.mBroadcastStats = paramAlarmManagerService;
        paramPendingIntent = (AlarmManagerService.FilterStats)this.mBroadcastStats.filterStats.get(this.mTag);
        paramAlarmManagerService = paramPendingIntent;
        if (paramPendingIntent == null)
        {
          paramAlarmManagerService = new AlarmManagerService.FilterStats(this.mBroadcastStats, this.mTag);
          this.mBroadcastStats.filterStats.put(this.mTag, paramAlarmManagerService);
        }
        paramAlarmManagerService.lastTime = paramLong;
        this.mFilterStats = paramAlarmManagerService;
        this.mAlarmType = paramInt2;
        return;
      }
    }
  }
  
  public static class IncreasingTimeOrder
    implements Comparator<AlarmManagerService.Alarm>
  {
    public int compare(AlarmManagerService.Alarm paramAlarm1, AlarmManagerService.Alarm paramAlarm2)
    {
      long l1 = paramAlarm1.whenElapsed;
      long l2 = paramAlarm2.whenElapsed;
      if (l1 > l2) {
        return 1;
      }
      if (l1 < l2) {
        return -1;
      }
      return 0;
    }
  }
  
  class InteractiveStateReceiver
    extends BroadcastReceiver
  {
    public InteractiveStateReceiver()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.intent.action.SCREEN_ON");
      localIntentFilter.setPriority(1000);
      AlarmManagerService.this.getContext().registerReceiver(this, localIntentFilter);
    }
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        AlarmManagerService.this.interactiveStateChangedLocked("android.intent.action.SCREEN_ON".equals(paramIntent.getAction()));
        return;
      }
    }
  }
  
  public final class LocalService
  {
    public LocalService() {}
    
    public void setDeviceIdleUserWhitelist(int[] paramArrayOfInt)
    {
      AlarmManagerService.this.setDeviceIdleUserWhitelistImpl(paramArrayOfInt);
    }
  }
  
  final class PriorityClass
  {
    int priority = 2;
    int seq = AlarmManagerService.this.mCurrentSeq - 1;
    
    PriorityClass() {}
  }
  
  final class UidObserver
    extends IUidObserver.Stub
  {
    UidObserver() {}
    
    public void onUidActive(int paramInt)
      throws RemoteException
    {}
    
    public void onUidGone(int paramInt)
      throws RemoteException
    {}
    
    public void onUidIdle(int paramInt)
      throws RemoteException
    {
      synchronized (AlarmManagerService.this.mLock)
      {
        AlarmManagerService.this.removeForStoppedLocked(paramInt);
        return;
      }
    }
    
    public void onUidStateChanged(int paramInt1, int paramInt2)
      throws RemoteException
    {}
  }
  
  class UninstallReceiver
    extends BroadcastReceiver
  {
    public UninstallReceiver()
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
      localIntentFilter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
      localIntentFilter.addDataScheme("package");
      AlarmManagerService.this.getContext().registerReceiver(this, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
      localIntentFilter.addAction("android.intent.action.USER_STOPPED");
      localIntentFilter.addAction("android.intent.action.UID_REMOVED");
      AlarmManagerService.this.getContext().registerReceiver(this, localIntentFilter);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      for (;;)
      {
        ArrayMap localArrayMap;
        int i;
        int j;
        synchronized (AlarmManagerService.this.mLock)
        {
          if (AlarmManagerService.localLOGV) {
            Slog.d("AlarmManager", "UninstallReceiver  action = " + paramIntent.getAction());
          }
          paramContext = paramIntent.getAction();
          localArrayMap = null;
          if ("android.intent.action.QUERY_PACKAGE_RESTART".equals(paramContext))
          {
            paramContext = paramIntent.getStringArrayExtra("android.intent.extra.PACKAGES");
            i = 0;
            j = paramContext.length;
            if (i < j)
            {
              paramIntent = paramContext[i];
              if (AlarmManagerService.this.lookForPackageLocked(paramIntent))
              {
                setResultCode(-1);
                return;
              }
              i += 1;
              continue;
            }
            return;
          }
          if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(paramContext))
          {
            paramContext = paramIntent.getStringArrayExtra("android.intent.extra.changed_package_list");
            if ((paramContext == null) || (paramContext.length <= 0)) {
              break label412;
            }
            i = 0;
            int k = paramContext.length;
            if (i >= k) {
              break label412;
            }
            paramIntent = paramContext[i];
            if (!"android".equals(paramIntent)) {
              break label318;
            }
            break label416;
          }
          if ("android.intent.action.USER_STOPPED".equals(paramContext))
          {
            i = paramIntent.getIntExtra("android.intent.extra.user_handle", -1);
            paramContext = localArrayMap;
            if (i < 0) {
              continue;
            }
            AlarmManagerService.this.removeUserLocked(i);
            paramContext = localArrayMap;
          }
        }
        if ("android.intent.action.UID_REMOVED".equals(paramContext))
        {
          i = paramIntent.getIntExtra("android.intent.extra.UID", -1);
          paramContext = localArrayMap;
          if (i >= 0)
          {
            AlarmManagerService.this.mLastAllowWhileIdleDispatch.delete(i);
            paramContext = localArrayMap;
          }
        }
        else
        {
          if ("android.intent.action.PACKAGE_REMOVED".equals(paramContext))
          {
            boolean bool = paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false);
            if (bool) {
              return;
            }
          }
          paramIntent = paramIntent.getData();
          paramContext = localArrayMap;
          if (paramIntent != null)
          {
            paramIntent = paramIntent.getSchemeSpecificPart();
            paramContext = localArrayMap;
            if (paramIntent != null)
            {
              paramContext = new String[1];
              paramContext[0] = paramIntent;
              continue;
              label318:
              AlarmManagerService.this.removeLocked(paramIntent);
              AlarmManagerService.this.mPriorities.remove(paramIntent);
              j = AlarmManagerService.this.mBroadcastStats.size() - 1;
              while (j >= 0)
              {
                localArrayMap = (ArrayMap)AlarmManagerService.this.mBroadcastStats.valueAt(j);
                if ((localArrayMap.remove(paramIntent) != null) && (localArrayMap.size() <= 0)) {
                  AlarmManagerService.this.mBroadcastStats.removeAt(j);
                }
                j -= 1;
                continue;
                label412:
                return;
              }
              label416:
              i += 1;
            }
          }
        }
      }
    }
  }
  
  static final class WakeupEvent
  {
    public String action;
    public int uid;
    public long when;
    
    public WakeupEvent(long paramLong, int paramInt, String paramString)
    {
      this.when = paramLong;
      this.uid = paramInt;
      this.action = paramString;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/AlarmManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */