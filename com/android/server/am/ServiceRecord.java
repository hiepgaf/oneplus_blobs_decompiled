package com.android.server.am;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.OpFeatures;
import android.util.Slog;
import android.util.TimeUtils;
import android.widget.RemoteViews;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv;
import com.android.server.LocalServices;
import com.android.server.notification.NotificationManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ServiceRecord
  extends Binder
{
  static final int MAX_DELIVERY_COUNT = 3;
  static final int MAX_DONE_EXECUTING_COUNT = 6;
  private static final String TAG = "ActivityManager";
  final ActivityManagerService ams;
  ProcessRecord app;
  final ApplicationInfo appInfo;
  final ArrayMap<Intent.FilterComparison, IntentBindRecord> bindings = new ArrayMap();
  boolean callStart;
  final ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = new ArrayMap();
  int crashCount;
  final long createTime;
  boolean createdFromFg;
  boolean delayed;
  boolean delayedStop;
  final ArrayList<StartItem> deliveredStarts = new ArrayList();
  long destroyTime;
  boolean destroying;
  boolean executeFg;
  int executeNesting;
  long executingStart;
  final boolean exported;
  int foregroundId;
  Notification foregroundNoti;
  final Intent.FilterComparison intent;
  boolean isForeground;
  ProcessRecord isolatedProc;
  long lastActivity;
  private int lastStartId;
  final ComponentName name;
  long nextRestartTime;
  final String packageName;
  final ArrayList<StartItem> pendingStarts = new ArrayList();
  final String permission;
  final String processName;
  int relativeRestartCount;
  long relativeRestartTime;
  int restartCount;
  long restartDelay;
  long restartTime;
  ServiceState restartTracker;
  final Runnable restarter;
  final ServiceInfo serviceInfo;
  final String shortName;
  boolean startRequested;
  long startingBgTimeout;
  final BatteryStatsImpl.Uid.Pkg.Serv stats;
  boolean stopIfKilled;
  String stringName;
  int totalRestartCount;
  ServiceState tracker;
  final int userId;
  boolean whitelistManager;
  
  ServiceRecord(ActivityManagerService paramActivityManagerService, BatteryStatsImpl.Uid.Pkg.Serv paramServ, ComponentName paramComponentName, Intent.FilterComparison paramFilterComparison, ServiceInfo paramServiceInfo, boolean paramBoolean, Runnable paramRunnable)
  {
    this.ams = paramActivityManagerService;
    this.stats = paramServ;
    this.name = paramComponentName;
    this.shortName = paramComponentName.flattenToShortString();
    this.intent = paramFilterComparison;
    this.serviceInfo = paramServiceInfo;
    this.appInfo = paramServiceInfo.applicationInfo;
    this.packageName = paramServiceInfo.applicationInfo.packageName;
    this.processName = paramServiceInfo.processName;
    this.permission = paramServiceInfo.permission;
    this.exported = paramServiceInfo.exported;
    this.restarter = paramRunnable;
    this.createTime = SystemClock.elapsedRealtime();
    this.lastActivity = SystemClock.uptimeMillis();
    this.userId = UserHandle.getUserId(this.appInfo.uid);
    this.createdFromFg = paramBoolean;
  }
  
  private boolean isEmptyNotify(Notification paramNotification)
  {
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool1 = false;
    boolean bool2 = false;
    if ((paramNotification.extras != null) && (paramNotification.getSmallIcon() != null))
    {
      String str1 = paramNotification.extras.getString("android.title");
      String str2 = paramNotification.extras.getString("android.text");
      if ((str1 == null) || ("".equals(str1))) {
        bool3 = true;
      }
      if ((str2 == null) || ("".equals(str2))) {
        bool4 = true;
      }
      if (paramNotification.contentView == null)
      {
        bool1 = true;
        if (paramNotification.bigContentView != null) {
          break label192;
        }
        bool2 = true;
      }
    }
    for (;;)
    {
      if (((bool3) || (bool4)) && (bool1) && (bool2))
      {
        Slog.i(TAG, "emptyTitle =" + bool3 + ", emptyContent =" + bool4 + ", defaultOrEmptyContentView =" + bool1 + ", defaultOrEmptyBigContentView =" + bool2);
        return true;
        if (paramNotification.contentView.getLayoutId() != 17367181) {
          break;
        }
        bool1 = true;
        break;
        label192:
        if (paramNotification.bigContentView.getLayoutId() == 17367182)
        {
          bool2 = true;
          continue;
          Slog.w(TAG, "Empty extra!");
          return true;
        }
      }
    }
    return false;
  }
  
  public void cancelNotification()
  {
    final String str = this.packageName;
    final int i = this.foregroundId;
    this.ams.mHandler.post(new Runnable()
    {
      public void run()
      {
        INotificationManager localINotificationManager = NotificationManager.getService();
        if (localINotificationManager == null) {
          return;
        }
        try
        {
          localINotificationManager.cancelNotificationWithTag(str, null, i, ServiceRecord.this.userId);
          return;
        }
        catch (RuntimeException localRuntimeException)
        {
          Slog.w(ServiceRecord.-get0(), "Error canceling notification for service", localRuntimeException);
          return;
        }
        catch (RemoteException localRemoteException) {}
      }
    });
  }
  
  public void clearDeliveredStartsLocked()
  {
    int i = this.deliveredStarts.size() - 1;
    while (i >= 0)
    {
      ((StartItem)this.deliveredStarts.get(i)).removeUriPermissionsLocked();
      i -= 1;
    }
    this.deliveredStarts.clear();
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("intent={");
    paramPrintWriter.print(this.intent.getIntent().toShortString(false, true, false, true));
    paramPrintWriter.println('}');
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("packageName=");
    paramPrintWriter.println(this.packageName);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("processName=");
    paramPrintWriter.println(this.processName);
    if (this.permission != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("permission=");
      paramPrintWriter.println(this.permission);
    }
    long l1 = SystemClock.uptimeMillis();
    long l2 = SystemClock.elapsedRealtime();
    if (this.appInfo != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("baseDir=");
      paramPrintWriter.println(this.appInfo.sourceDir);
      if (!Objects.equals(this.appInfo.sourceDir, this.appInfo.publicSourceDir))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("resDir=");
        paramPrintWriter.println(this.appInfo.publicSourceDir);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("dataDir=");
      paramPrintWriter.println(this.appInfo.dataDir);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("app=");
    paramPrintWriter.println(this.app);
    if (this.isolatedProc != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("isolatedProc=");
      paramPrintWriter.println(this.isolatedProc);
    }
    if (this.whitelistManager)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("whitelistManager=");
      paramPrintWriter.println(this.whitelistManager);
    }
    if (this.delayed)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("delayed=");
      paramPrintWriter.println(this.delayed);
    }
    if ((this.isForeground) || (this.foregroundId != 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("isForeground=");
      paramPrintWriter.print(this.isForeground);
      paramPrintWriter.print(" foregroundId=");
      paramPrintWriter.print(this.foregroundId);
      paramPrintWriter.print(" foregroundNoti=");
      paramPrintWriter.println(this.foregroundNoti);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("createTime=");
    TimeUtils.formatDuration(this.createTime, l2, paramPrintWriter);
    paramPrintWriter.print(" startingBgTimeout=");
    TimeUtils.formatDuration(this.startingBgTimeout, l1, paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("lastActivity=");
    TimeUtils.formatDuration(this.lastActivity, l1, paramPrintWriter);
    paramPrintWriter.print(" restartTime=");
    TimeUtils.formatDuration(this.restartTime, l1, paramPrintWriter);
    paramPrintWriter.print(" createdFromFg=");
    paramPrintWriter.println(this.createdFromFg);
    if ((this.startRequested) || (this.delayedStop) || (this.lastStartId != 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("startRequested=");
      paramPrintWriter.print(this.startRequested);
      paramPrintWriter.print(" delayedStop=");
      paramPrintWriter.print(this.delayedStop);
      paramPrintWriter.print(" stopIfKilled=");
      paramPrintWriter.print(this.stopIfKilled);
      paramPrintWriter.print(" callStart=");
      paramPrintWriter.print(this.callStart);
      paramPrintWriter.print(" lastStartId=");
      paramPrintWriter.println(this.lastStartId);
    }
    if (this.executeNesting != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("executeNesting=");
      paramPrintWriter.print(this.executeNesting);
      paramPrintWriter.print(" executeFg=");
      paramPrintWriter.print(this.executeFg);
      paramPrintWriter.print(" executingStart=");
      TimeUtils.formatDuration(this.executingStart, l1, paramPrintWriter);
      paramPrintWriter.println();
    }
    if ((this.destroying) || (this.destroyTime != 0L))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("destroying=");
      paramPrintWriter.print(this.destroying);
      paramPrintWriter.print(" destroyTime=");
      TimeUtils.formatDuration(this.destroyTime, l1, paramPrintWriter);
      paramPrintWriter.println();
    }
    if ((this.crashCount != 0) || (this.restartCount != 0)) {}
    int i;
    Object localObject;
    for (;;)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("restartCount=");
      paramPrintWriter.print(this.restartCount);
      paramPrintWriter.print(" restartDelay=");
      TimeUtils.formatDuration(this.restartDelay, l1, paramPrintWriter);
      paramPrintWriter.print(" nextRestartTime=");
      TimeUtils.formatDuration(this.nextRestartTime, l1, paramPrintWriter);
      paramPrintWriter.print(" crashCount=");
      paramPrintWriter.println(this.crashCount);
      do
      {
        if (this.deliveredStarts.size() > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Delivered Starts:");
          dumpStartList(paramPrintWriter, paramString, this.deliveredStarts, l1);
        }
        if (this.pendingStarts.size() > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Starts:");
          dumpStartList(paramPrintWriter, paramString, this.pendingStarts, 0L);
        }
        if (this.bindings.size() <= 0) {
          break label993;
        }
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Bindings:");
        i = 0;
        while (i < this.bindings.size())
        {
          localObject = (IntentBindRecord)this.bindings.valueAt(i);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("* IntentBindRecord{");
          paramPrintWriter.print(Integer.toHexString(System.identityHashCode(localObject)));
          if ((((IntentBindRecord)localObject).collectFlags() & 0x1) != 0) {
            paramPrintWriter.append(" CREATE");
          }
          paramPrintWriter.println("}:");
          ((IntentBindRecord)localObject).dumpInService(paramPrintWriter, paramString + "  ");
          i += 1;
        }
        if (this.restartDelay != 0L) {
          break;
        }
      } while (this.nextRestartTime == 0L);
    }
    label993:
    if (this.connections.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("All Connections:");
      i = 0;
      while (i < this.connections.size())
      {
        localObject = (ArrayList)this.connections.valueAt(i);
        int j = 0;
        while (j < ((ArrayList)localObject).size())
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.println(((ArrayList)localObject).get(j));
          j += 1;
        }
        i += 1;
      }
    }
  }
  
  void dumpStartList(PrintWriter paramPrintWriter, String paramString, List<StartItem> paramList, long paramLong)
  {
    int j = paramList.size();
    int i = 0;
    if (i < j)
    {
      StartItem localStartItem = (StartItem)paramList.get(i);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("#");
      paramPrintWriter.print(i);
      paramPrintWriter.print(" id=");
      paramPrintWriter.print(localStartItem.id);
      if (paramLong != 0L)
      {
        paramPrintWriter.print(" dur=");
        TimeUtils.formatDuration(localStartItem.deliveredTime, paramLong, paramPrintWriter);
      }
      if (localStartItem.deliveryCount != 0)
      {
        paramPrintWriter.print(" dc=");
        paramPrintWriter.print(localStartItem.deliveryCount);
      }
      if (localStartItem.doneExecutingCount != 0)
      {
        paramPrintWriter.print(" dxc=");
        paramPrintWriter.print(localStartItem.doneExecutingCount);
      }
      paramPrintWriter.println("");
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  intent=");
      if (localStartItem.intent != null) {
        paramPrintWriter.println(localStartItem.intent.toString());
      }
      for (;;)
      {
        if (localStartItem.neededGrants != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  neededGrants=");
          paramPrintWriter.println(localStartItem.neededGrants);
        }
        if (localStartItem.uriPermissions != null) {
          localStartItem.uriPermissions.dump(paramPrintWriter, paramString);
        }
        i += 1;
        break;
        paramPrintWriter.println("null");
      }
    }
  }
  
  public StartItem findDeliveredStart(int paramInt, boolean paramBoolean)
  {
    int j = this.deliveredStarts.size();
    int i = 0;
    while (i < j)
    {
      StartItem localStartItem = (StartItem)this.deliveredStarts.get(i);
      if (localStartItem.id == paramInt)
      {
        if (paramBoolean) {
          this.deliveredStarts.remove(i);
        }
        return localStartItem;
      }
      i += 1;
    }
    return null;
  }
  
  public void forceClearTracker()
  {
    if (this.tracker != null)
    {
      this.tracker.clearCurrentOwner(this, true);
      this.tracker = null;
    }
  }
  
  public int getLastStartId()
  {
    return this.lastStartId;
  }
  
  public ServiceState getTracker()
  {
    if (this.tracker != null) {
      return this.tracker;
    }
    if ((this.serviceInfo.applicationInfo.flags & 0x8) == 0)
    {
      this.tracker = this.ams.mProcessStats.getServiceStateLocked(this.serviceInfo.packageName, this.serviceInfo.applicationInfo.uid, this.serviceInfo.applicationInfo.versionCode, this.serviceInfo.processName, this.serviceInfo.name);
      this.tracker.applyNewOwner(this);
    }
    return this.tracker;
  }
  
  public boolean hasAutoCreateConnections()
  {
    int i = this.connections.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = (ArrayList)this.connections.valueAt(i);
      int j = 0;
      while (j < localArrayList.size())
      {
        if ((((ConnectionRecord)localArrayList.get(j)).flags & 0x1) != 0) {
          return true;
        }
        j += 1;
      }
      i -= 1;
    }
    return false;
  }
  
  public int makeNextStartId()
  {
    this.lastStartId += 1;
    if (this.lastStartId < 1) {
      this.lastStartId = 1;
    }
    return this.lastStartId;
  }
  
  public void makeRestarting(int paramInt, long paramLong)
  {
    if (this.restartTracker == null)
    {
      if ((this.serviceInfo.applicationInfo.flags & 0x8) == 0) {
        this.restartTracker = this.ams.mProcessStats.getServiceStateLocked(this.serviceInfo.packageName, this.serviceInfo.applicationInfo.uid, this.serviceInfo.applicationInfo.versionCode, this.serviceInfo.processName, this.serviceInfo.name);
      }
      if (this.restartTracker == null) {
        return;
      }
    }
    this.restartTracker.setRestarting(true, paramInt, paramLong);
  }
  
  public void postNotification()
  {
    final int i = this.appInfo.uid;
    final int j = this.app.pid;
    if ((this.foregroundId != 0) && (this.foregroundNoti != null))
    {
      final String str = this.packageName;
      final int k = this.foregroundId;
      final Notification localNotification = this.foregroundNoti;
      if (isEmptyNotify(localNotification)) {
        if (OpFeatures.isSupport(new int[] { 0 }))
        {
          Slog.i(TAG, "it's may be an empty notification from package " + str);
          return;
        }
      }
      this.ams.mHandler.post(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: ldc 45
          //   2: invokestatic 51	com/android/server/LocalServices:getService	(Ljava/lang/Class;)Ljava/lang/Object;
          //   5: checkcast 45	com/android/server/notification/NotificationManagerInternal
          //   8: astore 4
          //   10: aload 4
          //   12: ifnonnull +4 -> 16
          //   15: return
          //   16: aload_0
          //   17: getfield 27	com/android/server/am/ServiceRecord$1:val$_foregroundNoti	Landroid/app/Notification;
          //   20: astore_2
          //   21: aload_2
          //   22: astore_1
          //   23: aload_2
          //   24: invokevirtual 57	android/app/Notification:getSmallIcon	()Landroid/graphics/drawable/Icon;
          //   27: ifnonnull +358 -> 385
          //   30: iconst_1
          //   31: newarray <illegal type>
          //   33: dup
          //   34: iconst_0
          //   35: iconst_0
          //   36: iastore
          //   37: invokestatic 63	android/util/OpFeatures:isSupport	([I)Z
          //   40: ifeq +41 -> 81
          //   43: invokestatic 67	com/android/server/am/ServiceRecord:-get0	()Ljava/lang/String;
          //   46: new 69	java/lang/StringBuilder
          //   49: dup
          //   50: invokespecial 70	java/lang/StringBuilder:<init>	()V
          //   53: ldc 72
          //   55: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   58: aload_0
          //   59: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   62: getfield 79	com/android/server/am/ServiceRecord:packageName	Ljava/lang/String;
          //   65: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   68: ldc 81
          //   70: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   73: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   76: invokestatic 90	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
          //   79: pop
          //   80: return
          //   81: invokestatic 67	com/android/server/am/ServiceRecord:-get0	()Ljava/lang/String;
          //   84: new 69	java/lang/StringBuilder
          //   87: dup
          //   88: invokespecial 70	java/lang/StringBuilder:<init>	()V
          //   91: ldc 92
          //   93: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   96: aload_0
          //   97: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   100: getfield 96	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
          //   103: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   106: ldc 101
          //   108: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   111: aload_2
          //   112: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   115: ldc 103
          //   117: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   120: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   123: invokestatic 106	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
          //   126: pop
          //   127: aload_0
          //   128: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   131: getfield 110	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
          //   134: aload_0
          //   135: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   138: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   141: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   144: invokevirtual 126	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
          //   147: invokevirtual 132	android/content/pm/ApplicationInfo:loadLabel	(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
          //   150: astore_3
          //   151: aload_3
          //   152: astore_1
          //   153: aload_3
          //   154: ifnonnull +14 -> 168
          //   157: aload_0
          //   158: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   161: getfield 110	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
          //   164: getfield 133	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
          //   167: astore_1
          //   168: new 135	android/app/Notification$Builder
          //   171: dup
          //   172: aload_0
          //   173: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   176: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   179: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   182: aload_0
          //   183: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   186: getfield 110	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
          //   189: getfield 133	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
          //   192: iconst_0
          //   193: new 137	android/os/UserHandle
          //   196: dup
          //   197: aload_0
          //   198: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   201: getfield 140	com/android/server/am/ServiceRecord:userId	I
          //   204: invokespecial 143	android/os/UserHandle:<init>	(I)V
          //   207: invokevirtual 147	android/content/Context:createPackageContextAsUser	(Ljava/lang/String;ILandroid/os/UserHandle;)Landroid/content/Context;
          //   210: invokespecial 150	android/app/Notification$Builder:<init>	(Landroid/content/Context;)V
          //   213: astore_3
          //   214: aload_3
          //   215: aload_0
          //   216: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   219: getfield 110	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
          //   222: getfield 153	android/content/pm/ApplicationInfo:icon	I
          //   225: invokevirtual 157	android/app/Notification$Builder:setSmallIcon	(I)Landroid/app/Notification$Builder;
          //   228: pop
          //   229: aload_3
          //   230: bipush 64
          //   232: iconst_1
          //   233: invokevirtual 161	android/app/Notification$Builder:setFlag	(IZ)Landroid/app/Notification$Builder;
          //   236: pop
          //   237: aload_3
          //   238: bipush -2
          //   240: invokevirtual 164	android/app/Notification$Builder:setPriority	(I)Landroid/app/Notification$Builder;
          //   243: pop
          //   244: new 166	android/content/Intent
          //   247: dup
          //   248: ldc -88
          //   250: invokespecial 171	android/content/Intent:<init>	(Ljava/lang/String;)V
          //   253: astore 5
          //   255: aload 5
          //   257: ldc -83
          //   259: aload_0
          //   260: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   263: getfield 110	com/android/server/am/ServiceRecord:appInfo	Landroid/content/pm/ApplicationInfo;
          //   266: getfield 133	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
          //   269: aconst_null
          //   270: invokestatic 179	android/net/Uri:fromParts	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
          //   273: invokevirtual 183	android/content/Intent:setData	(Landroid/net/Uri;)Landroid/content/Intent;
          //   276: pop
          //   277: aload_0
          //   278: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   281: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   284: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   287: iconst_0
          //   288: aload 5
          //   290: ldc -72
          //   292: invokestatic 190	android/app/PendingIntent:getActivity	(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
          //   295: astore 5
          //   297: aload_3
          //   298: aload_0
          //   299: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   302: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   305: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   308: ldc -65
          //   310: invokevirtual 195	android/content/Context:getColor	(I)I
          //   313: invokevirtual 198	android/app/Notification$Builder:setColor	(I)Landroid/app/Notification$Builder;
          //   316: pop
          //   317: aload_3
          //   318: aload_0
          //   319: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   322: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   325: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   328: ldc -57
          //   330: iconst_1
          //   331: anewarray 4	java/lang/Object
          //   334: dup
          //   335: iconst_0
          //   336: aload_1
          //   337: aastore
          //   338: invokevirtual 203	android/content/Context:getString	(I[Ljava/lang/Object;)Ljava/lang/String;
          //   341: invokevirtual 207	android/app/Notification$Builder:setContentTitle	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
          //   344: pop
          //   345: aload_3
          //   346: aload_0
          //   347: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   350: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   353: getfield 120	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
          //   356: ldc -48
          //   358: iconst_1
          //   359: anewarray 4	java/lang/Object
          //   362: dup
          //   363: iconst_0
          //   364: aload_1
          //   365: aastore
          //   366: invokevirtual 203	android/content/Context:getString	(I[Ljava/lang/Object;)Ljava/lang/String;
          //   369: invokevirtual 211	android/app/Notification$Builder:setContentText	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
          //   372: pop
          //   373: aload_3
          //   374: aload 5
          //   376: invokevirtual 215	android/app/Notification$Builder:setContentIntent	(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;
          //   379: pop
          //   380: aload_3
          //   381: invokevirtual 219	android/app/Notification$Builder:build	()Landroid/app/Notification;
          //   384: astore_1
          //   385: aload_1
          //   386: invokevirtual 57	android/app/Notification:getSmallIcon	()Landroid/graphics/drawable/Icon;
          //   389: ifnonnull +113 -> 502
          //   392: new 41	java/lang/RuntimeException
          //   395: dup
          //   396: new 69	java/lang/StringBuilder
          //   399: dup
          //   400: invokespecial 70	java/lang/StringBuilder:<init>	()V
          //   403: ldc -35
          //   405: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   408: aload_0
          //   409: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   412: getfield 224	com/android/server/am/ServiceRecord:foregroundNoti	Landroid/app/Notification;
          //   415: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   418: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   421: invokespecial 225	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
          //   424: athrow
          //   425: astore_1
          //   426: invokestatic 67	com/android/server/am/ServiceRecord:-get0	()Ljava/lang/String;
          //   429: ldc -29
          //   431: aload_1
          //   432: invokestatic 231	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   435: pop
          //   436: aload_0
          //   437: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   440: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   443: aload_0
          //   444: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   447: getfield 96	com/android/server/am/ServiceRecord:name	Landroid/content/ComponentName;
          //   450: aload_0
          //   451: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   454: iconst_0
          //   455: aconst_null
          //   456: iconst_0
          //   457: invokevirtual 235	com/android/server/am/ActivityManagerService:setServiceForeground	(Landroid/content/ComponentName;Landroid/os/IBinder;ILandroid/app/Notification;I)V
          //   460: aload_0
          //   461: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   464: getfield 114	com/android/server/am/ServiceRecord:ams	Lcom/android/server/am/ActivityManagerService;
          //   467: aload_0
          //   468: getfield 31	com/android/server/am/ServiceRecord$1:val$appUid	I
          //   471: aload_0
          //   472: getfield 33	com/android/server/am/ServiceRecord$1:val$appPid	I
          //   475: aload_0
          //   476: getfield 29	com/android/server/am/ServiceRecord$1:val$localPackageName	Ljava/lang/String;
          //   479: new 69	java/lang/StringBuilder
          //   482: dup
          //   483: invokespecial 70	java/lang/StringBuilder:<init>	()V
          //   486: ldc -19
          //   488: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   491: aload_1
          //   492: invokevirtual 99	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   495: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   498: invokevirtual 241	com/android/server/am/ActivityManagerService:crashApplication	(IILjava/lang/String;Ljava/lang/String;)V
          //   501: return
          //   502: iconst_1
          //   503: newarray <illegal type>
          //   505: astore_2
          //   506: aload 4
          //   508: aload_0
          //   509: getfield 29	com/android/server/am/ServiceRecord$1:val$localPackageName	Ljava/lang/String;
          //   512: aload_0
          //   513: getfield 29	com/android/server/am/ServiceRecord$1:val$localPackageName	Ljava/lang/String;
          //   516: aload_0
          //   517: getfield 31	com/android/server/am/ServiceRecord$1:val$appUid	I
          //   520: aload_0
          //   521: getfield 33	com/android/server/am/ServiceRecord$1:val$appPid	I
          //   524: aconst_null
          //   525: aload_0
          //   526: getfield 35	com/android/server/am/ServiceRecord$1:val$localForegroundId	I
          //   529: aload_1
          //   530: aload_2
          //   531: aload_0
          //   532: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   535: getfield 140	com/android/server/am/ServiceRecord:userId	I
          //   538: invokeinterface 245 10 0
          //   543: aload_0
          //   544: getfield 25	com/android/server/am/ServiceRecord$1:this$0	Lcom/android/server/am/ServiceRecord;
          //   547: aload_1
          //   548: putfield 224	com/android/server/am/ServiceRecord:foregroundNoti	Landroid/app/Notification;
          //   551: return
          //   552: astore_1
          //   553: aload_2
          //   554: astore_1
          //   555: goto -170 -> 385
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	558	0	this	1
          //   22	364	1	localObject1	Object
          //   425	123	1	localRuntimeException	RuntimeException
          //   552	1	1	localNameNotFoundException	android.content.pm.PackageManager.NameNotFoundException
          //   554	1	1	localObject2	Object
          //   20	534	2	localObject3	Object
          //   150	231	3	localObject4	Object
          //   8	499	4	localNotificationManagerInternal	NotificationManagerInternal
          //   253	122	5	localObject5	Object
          // Exception table:
          //   from	to	target	type
          //   23	80	425	java/lang/RuntimeException
          //   81	151	425	java/lang/RuntimeException
          //   157	168	425	java/lang/RuntimeException
          //   168	385	425	java/lang/RuntimeException
          //   385	425	425	java/lang/RuntimeException
          //   502	551	425	java/lang/RuntimeException
          //   168	385	552	android/content/pm/PackageManager$NameNotFoundException
        }
      });
    }
  }
  
  public void resetRestartCounter()
  {
    this.restartCount = 0;
    this.restartDelay = 0L;
    this.restartTime = 0L;
  }
  
  public AppBindRecord retrieveAppBindingLocked(Intent paramIntent, ProcessRecord paramProcessRecord)
  {
    Intent.FilterComparison localFilterComparison = new Intent.FilterComparison(paramIntent);
    Object localObject = (IntentBindRecord)this.bindings.get(localFilterComparison);
    paramIntent = (Intent)localObject;
    if (localObject == null)
    {
      paramIntent = new IntentBindRecord(this, localFilterComparison);
      this.bindings.put(localFilterComparison, paramIntent);
    }
    localObject = (AppBindRecord)paramIntent.apps.get(paramProcessRecord);
    if (localObject != null) {
      return (AppBindRecord)localObject;
    }
    localObject = new AppBindRecord(this, paramIntent, paramProcessRecord);
    paramIntent.apps.put(paramProcessRecord, localObject);
    return (AppBindRecord)localObject;
  }
  
  public void stripForegroundServiceFlagFromNotification()
  {
    if (this.foregroundId == 0) {
      return;
    }
    final int i = this.foregroundId;
    final int j = this.userId;
    final String str = this.packageName;
    this.ams.mHandler.post(new Runnable()
    {
      public void run()
      {
        NotificationManagerInternal localNotificationManagerInternal = (NotificationManagerInternal)LocalServices.getService(NotificationManagerInternal.class);
        if (localNotificationManagerInternal == null) {
          return;
        }
        localNotificationManagerInternal.removeForegroundServiceFlagFromNotification(str, i, j);
      }
    });
  }
  
  public String toString()
  {
    if (this.stringName != null) {
      return this.stringName;
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(this))).append(" u").append(this.userId).append(' ').append(this.shortName).append('}');
    localObject = ((StringBuilder)localObject).toString();
    this.stringName = ((String)localObject);
    return (String)localObject;
  }
  
  public void updateWhitelistManager()
  {
    this.whitelistManager = false;
    int i = this.connections.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = (ArrayList)this.connections.valueAt(i);
      int j = 0;
      while (j < localArrayList.size())
      {
        if ((((ConnectionRecord)localArrayList.get(j)).flags & 0x1000000) != 0)
        {
          this.whitelistManager = true;
          return;
        }
        j += 1;
      }
      i -= 1;
    }
  }
  
  static class StartItem
  {
    long deliveredTime;
    int deliveryCount;
    int doneExecutingCount;
    final int id;
    final Intent intent;
    final ActivityManagerService.NeededUriGrants neededGrants;
    final ServiceRecord sr;
    String stringName;
    final boolean taskRemoved;
    UriPermissionOwner uriPermissions;
    
    StartItem(ServiceRecord paramServiceRecord, boolean paramBoolean, int paramInt, Intent paramIntent, ActivityManagerService.NeededUriGrants paramNeededUriGrants)
    {
      this.sr = paramServiceRecord;
      this.taskRemoved = paramBoolean;
      this.id = paramInt;
      this.intent = paramIntent;
      this.neededGrants = paramNeededUriGrants;
    }
    
    UriPermissionOwner getUriPermissionsLocked()
    {
      if (this.uriPermissions == null) {
        this.uriPermissions = new UriPermissionOwner(this.sr.ams, this);
      }
      return this.uriPermissions;
    }
    
    void removeUriPermissionsLocked()
    {
      if (this.uriPermissions != null)
      {
        this.uriPermissions.removeUriPermissionsLocked();
        this.uriPermissions = null;
      }
    }
    
    public String toString()
    {
      if (this.stringName != null) {
        return this.stringName;
      }
      Object localObject = new StringBuilder(128);
      ((StringBuilder)localObject).append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(this.sr))).append(' ').append(this.sr.shortName).append(" StartItem ").append(Integer.toHexString(System.identityHashCode(this))).append(" id=").append(this.id).append('}');
      localObject = ((StringBuilder)localObject).toString();
      this.stringName = ((String)localObject);
      return (String)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ServiceRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */