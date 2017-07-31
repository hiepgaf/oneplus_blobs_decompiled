package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;

public class ActivityManager
{
  public static final String ACTION_REPORT_HEAP_LIMIT = "android.app.action.REPORT_HEAP_LIMIT";
  public static final int APP_CONTROL_MODE_APPBOOT = 1;
  public static final int APP_CONTROL_MODE_CGROUP = 2;
  public static final int APP_CONTROL_MODE_DEFAULT = 0;
  public static final int APP_CONTROL_MODE_DOZE = 3;
  public static final int APP_START_MODE_DELAYED = 1;
  public static final int APP_START_MODE_DISABLED = 2;
  public static final int APP_START_MODE_NORMAL = 0;
  public static final int ASSIST_CONTEXT_BASIC = 0;
  public static final int ASSIST_CONTEXT_FULL = 1;
  public static final int BROADCAST_FAILED_USER_STOPPED = -2;
  public static final int BROADCAST_STICKY_CANT_HAVE_PERMISSION = -1;
  public static final int BROADCAST_SUCCESS = 0;
  public static final int BUGREPORT_OPTION_FULL = 0;
  public static final int BUGREPORT_OPTION_INTERACTIVE = 1;
  public static final int BUGREPORT_OPTION_REMOTE = 2;
  public static final int BUGREPORT_OPTION_WEAR = 3;
  public static final int COMPAT_MODE_ALWAYS = -1;
  public static final int COMPAT_MODE_DISABLED = 0;
  public static final int COMPAT_MODE_ENABLED = 1;
  public static final int COMPAT_MODE_NEVER = -2;
  public static final int COMPAT_MODE_TOGGLE = 2;
  public static final int COMPAT_MODE_UNKNOWN = -3;
  public static final int DOCKED_STACK_CREATE_MODE_BOTTOM_OR_RIGHT = 1;
  public static final int DOCKED_STACK_CREATE_MODE_TOP_OR_LEFT = 0;
  public static final int FLAG_AND_LOCKED = 2;
  public static final int FLAG_AND_UNLOCKED = 4;
  public static final int FLAG_AND_UNLOCKING_OR_UNLOCKED = 8;
  public static final int FLAG_OR_STOPPED = 1;
  public static final int INTENT_SENDER_ACTIVITY = 2;
  public static final int INTENT_SENDER_ACTIVITY_RESULT = 3;
  public static final int INTENT_SENDER_BROADCAST = 1;
  public static final int INTENT_SENDER_SERVICE = 4;
  public static final int LOCK_TASK_MODE_LOCKED = 1;
  public static final int LOCK_TASK_MODE_NONE = 0;
  public static final int LOCK_TASK_MODE_PINNED = 2;
  public static final int MAX_PROCESS_STATE = 16;
  public static final String META_HOME_ALTERNATE = "android.app.home.alternate";
  public static final int MIN_PROCESS_STATE = -1;
  public static final int MOVE_TASK_NO_USER_ACTION = 2;
  public static final int MOVE_TASK_WITH_HOME = 1;
  public static final int PROCESS_STATE_BACKUP = 8;
  public static final int PROCESS_STATE_BOUND_FOREGROUND_SERVICE = 3;
  public static final int PROCESS_STATE_CACHED_ACTIVITY = 14;
  public static final int PROCESS_STATE_CACHED_ACTIVITY_CLIENT = 15;
  public static final int PROCESS_STATE_CACHED_EMPTY = 16;
  public static final int PROCESS_STATE_FOREGROUND_SERVICE = 4;
  public static final int PROCESS_STATE_HEAVY_WEIGHT = 9;
  public static final int PROCESS_STATE_HOME = 12;
  public static final int PROCESS_STATE_IMPORTANT_BACKGROUND = 7;
  public static final int PROCESS_STATE_IMPORTANT_FOREGROUND = 6;
  public static final int PROCESS_STATE_LAST_ACTIVITY = 13;
  public static final int PROCESS_STATE_NONEXISTENT = -1;
  public static final int PROCESS_STATE_PERSISTENT = 0;
  public static final int PROCESS_STATE_PERSISTENT_UI = 1;
  public static final int PROCESS_STATE_RECEIVER = 11;
  public static final int PROCESS_STATE_SERVICE = 10;
  public static final int PROCESS_STATE_TOP = 2;
  public static final int PROCESS_STATE_TOP_SLEEPING = 5;
  public static final int RECENT_IGNORE_HOME_STACK_TASKS = 8;
  public static final int RECENT_IGNORE_UNAVAILABLE = 2;
  public static final int RECENT_INCLUDE_PROFILES = 4;
  public static final int RECENT_INGORE_DOCKED_STACK_TOP_TASK = 16;
  public static final int RECENT_INGORE_PINNED_STACK_TASKS = 32;
  public static final int RECENT_WITH_EXCLUDED = 1;
  public static final int RESIZE_MODE_FORCED = 2;
  public static final int RESIZE_MODE_PRESERVE_WINDOW = 1;
  public static final int RESIZE_MODE_SYSTEM = 0;
  public static final int RESIZE_MODE_SYSTEM_SCREEN_ROTATION = 1;
  public static final int RESIZE_MODE_USER = 1;
  public static final int RESIZE_MODE_USER_FORCED = 3;
  public static final int START_CANCELED = -6;
  public static final int START_CLASS_NOT_FOUND = -2;
  public static final int START_DELIVERED_TO_TOP = 3;
  public static final int START_FLAG_DEBUG = 2;
  public static final int START_FLAG_NATIVE_DEBUGGING = 8;
  public static final int START_FLAG_ONLY_IF_NEEDED = 1;
  public static final int START_FLAG_TRACK_ALLOCATION = 4;
  public static final int START_FORWARD_AND_REQUEST_CONFLICT = -3;
  public static final int START_INTENT_NOT_RESOLVED = -1;
  public static final int START_NOT_ACTIVITY = -5;
  public static final int START_NOT_CURRENT_USER_ACTIVITY = -8;
  public static final int START_NOT_VOICE_COMPATIBLE = -7;
  public static final int START_PERMISSION_DENIED = -4;
  public static final int START_RETURN_INTENT_TO_CALLER = 1;
  public static final int START_RETURN_LOCK_TASK_MODE_VIOLATION = 5;
  public static final int START_SUCCESS = 0;
  public static final int START_SWITCHES_CANCELED = 4;
  public static final int START_TASK_TO_FRONT = 2;
  public static final int START_VOICE_HIDDEN_SESSION = -10;
  public static final int START_VOICE_NOT_ACTIVE_SESSION = -9;
  private static String TAG = "ActivityManager";
  public static final int UID_OBSERVER_ACTIVE = 8;
  public static final int UID_OBSERVER_GONE = 2;
  public static final int UID_OBSERVER_IDLE = 4;
  public static final int UID_OBSERVER_PROCSTATE = 1;
  public static final int USER_OP_ERROR_IS_SYSTEM = -3;
  public static final int USER_OP_ERROR_RELATED_USERS_CANNOT_STOP = -4;
  public static final int USER_OP_IS_CURRENT = -2;
  public static final int USER_OP_SUCCESS = 0;
  public static final int USER_OP_UNKNOWN_USER = -1;
  private static int gMaxRecentTasks = -1;
  Point mAppTaskThumbnailSize;
  private final Context mContext;
  private final Handler mHandler;
  
  ActivityManager(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
  }
  
  public static int checkComponentPermission(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = UserHandle.getAppId(paramInt1);
    if ((i == 0) || (i == 1000)) {
      return 0;
    }
    if (UserHandle.isIsolated(paramInt1)) {
      return -1;
    }
    if ((paramInt2 >= 0) && (UserHandle.isSameApp(paramInt1, paramInt2))) {
      return 0;
    }
    if (!paramBoolean) {
      return -1;
    }
    if (paramString == null) {
      return 0;
    }
    try
    {
      paramInt1 = AppGlobals.getPackageManager().checkUidPermission(paramString, paramInt1);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public static int checkUidPermission(String paramString, int paramInt)
  {
    try
    {
      paramInt = AppGlobals.getPackageManager().checkUidPermission(paramString, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public static void dumpPackageStateStatic(FileDescriptor paramFileDescriptor, String paramString)
  {
    FastPrintWriter localFastPrintWriter = new FastPrintWriter(new FileOutputStream(paramFileDescriptor));
    dumpService(localFastPrintWriter, paramFileDescriptor, "package", new String[] { paramString });
    localFastPrintWriter.println();
    dumpService(localFastPrintWriter, paramFileDescriptor, "activity", new String[] { "-a", "package", paramString });
    localFastPrintWriter.println();
    dumpService(localFastPrintWriter, paramFileDescriptor, "meminfo", new String[] { "--local", "--package", paramString });
    localFastPrintWriter.println();
    dumpService(localFastPrintWriter, paramFileDescriptor, "procstats", new String[] { paramString });
    localFastPrintWriter.println();
    dumpService(localFastPrintWriter, paramFileDescriptor, "usagestats", new String[] { "--packages", paramString });
    localFastPrintWriter.println();
    dumpService(localFastPrintWriter, paramFileDescriptor, "batterystats", new String[] { paramString });
    localFastPrintWriter.flush();
  }
  
  private static void dumpService(PrintWriter paramPrintWriter, FileDescriptor paramFileDescriptor, String paramString, String[] paramArrayOfString)
  {
    paramPrintWriter.print("DUMP OF SERVICE ");
    paramPrintWriter.print(paramString);
    paramPrintWriter.println(":");
    IBinder localIBinder = ServiceManager.checkService(paramString);
    if (localIBinder == null)
    {
      paramPrintWriter.println("  (Service not found)");
      return;
    }
    paramString = null;
    try
    {
      paramPrintWriter.flush();
      localTransferPipe = new TransferPipe();
      if (paramString == null) {
        break label96;
      }
    }
    catch (Throwable paramFileDescriptor)
    {
      try
      {
        localTransferPipe.setBufferPrefix("  ");
        localIBinder.dumpAsync(localTransferPipe.getWriteFd().getFileDescriptor(), paramArrayOfString);
        localTransferPipe.go(paramFileDescriptor, 10000L);
        return;
      }
      catch (Throwable paramFileDescriptor)
      {
        for (;;)
        {
          TransferPipe localTransferPipe;
          paramString = localTransferPipe;
        }
      }
      paramFileDescriptor = paramFileDescriptor;
    }
    paramString.kill();
    label96:
    paramPrintWriter.println("Failure dumping service:");
    paramFileDescriptor.printStackTrace(paramPrintWriter);
  }
  
  private void ensureAppTaskThumbnailSizeLocked()
  {
    if (this.mAppTaskThumbnailSize == null) {}
    try
    {
      this.mAppTaskThumbnailSize = ActivityManagerNative.getDefault().getAppTaskThumbnailSize();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static int getCurrentUser()
  {
    try
    {
      UserInfo localUserInfo = ActivityManagerNative.getDefault().getCurrentUser();
      if (localUserInfo != null)
      {
        int i = localUserInfo.id;
        return i;
      }
      return 0;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static int getDefaultAppRecentsLimitStatic()
  {
    return getMaxRecentTasksStatic() / 6;
  }
  
  static int getLauncherLargeIconSizeInner(Context paramContext)
  {
    paramContext = paramContext.getResources();
    int i = paramContext.getDimensionPixelSize(17104896);
    if (paramContext.getConfiguration().smallestScreenWidthDp < 600) {
      return i;
    }
    switch (paramContext.getDisplayMetrics().densityDpi)
    {
    default: 
      return (int)(i * 1.5F + 0.5F);
    case 120: 
      return i * 160 / 120;
    case 160: 
      return i * 240 / 160;
    case 213: 
      return i * 320 / 240;
    case 240: 
      return i * 320 / 240;
    case 320: 
      return i * 480 / 320;
    }
    return i * 320 * 2 / 480;
  }
  
  public static int getMaxAppRecentsLimitStatic()
  {
    return getMaxRecentTasksStatic() / 2;
  }
  
  public static int getMaxRecentTasksStatic()
  {
    if (gMaxRecentTasks < 0)
    {
      if (isLowRamDeviceStatic()) {}
      for (int i = 36;; i = 48)
      {
        gMaxRecentTasks = i;
        return i;
      }
    }
    return gMaxRecentTasks;
  }
  
  public static void getMyMemoryState(RunningAppProcessInfo paramRunningAppProcessInfo)
  {
    try
    {
      ActivityManagerNative.getDefault().getMyMemoryState(paramRunningAppProcessInfo);
      return;
    }
    catch (RemoteException paramRunningAppProcessInfo)
    {
      throw paramRunningAppProcessInfo.rethrowFromSystemServer();
    }
  }
  
  public static int handleIncomingUser(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2)
  {
    if (UserHandle.getUserId(paramInt2) == paramInt3) {
      return paramInt3;
    }
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().handleIncomingUser(paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2, paramString1, paramString2);
      return paramInt1;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public static boolean isHighEndGfx()
  {
    if (("1".equals(SystemProperties.get("persist.sys.force_sw_gles", "0"))) || (isLowRamDeviceStatic())) {}
    while (Resources.getSystem().getBoolean(17956883)) {
      return false;
    }
    return true;
  }
  
  public static boolean isLowRamDeviceStatic()
  {
    return "true".equals(SystemProperties.get("ro.config.low_ram", "false"));
  }
  
  public static final boolean isProcStateBackground(int paramInt)
  {
    return paramInt >= 8;
  }
  
  public static boolean isRunningInTestHarness()
  {
    return SystemProperties.getBoolean("ro.test_harness", false);
  }
  
  public static boolean isUserAMonkey()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserAMonkey();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static void logoutCurrentUser()
  {
    int i = getCurrentUser();
    if (i != 0) {}
    try
    {
      ActivityManagerNative.getDefault().switchUser(0);
      ActivityManagerNative.getDefault().stopUser(i, false, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public static void setVrThread(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().setVrThread(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public static int staticGetLargeMemoryClass()
  {
    String str = SystemProperties.get("dalvik.vm.heapsize", "16m");
    return Integer.parseInt(str.substring(0, str.length() - 1));
  }
  
  public static int staticGetMemoryClass()
  {
    String str = SystemProperties.get("dalvik.vm.heapgrowthlimit", "");
    if ((str == null) || ("".equals(str))) {
      return staticGetLargeMemoryClass();
    }
    return Integer.parseInt(str.substring(0, str.length() - 1));
  }
  
  public static boolean supportsMultiWindow()
  {
    if (!isLowRamDeviceStatic()) {
      return Resources.getSystem().getBoolean(17957043);
    }
    return false;
  }
  
  /* Error */
  public int addAppTask(Activity paramActivity, Intent paramIntent, TaskDescription paramTaskDescription, Bitmap paramBitmap)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 537	android/app/ActivityManager:ensureAppTaskThumbnailSizeLocked	()V
    //   6: aload_0
    //   7: getfield 376	android/app/ActivityManager:mAppTaskThumbnailSize	Landroid/graphics/Point;
    //   10: astore 11
    //   12: aload_0
    //   13: monitorexit
    //   14: aload 4
    //   16: invokevirtual 542	android/graphics/Bitmap:getWidth	()I
    //   19: istore 8
    //   21: aload 4
    //   23: invokevirtual 545	android/graphics/Bitmap:getHeight	()I
    //   26: istore 9
    //   28: iload 8
    //   30: aload 11
    //   32: getfield 550	android/graphics/Point:x	I
    //   35: if_icmpne +17 -> 52
    //   38: aload 4
    //   40: astore 10
    //   42: iload 9
    //   44: aload 11
    //   46: getfield 553	android/graphics/Point:y	I
    //   49: if_icmpeq +136 -> 185
    //   52: aload 11
    //   54: getfield 550	android/graphics/Point:x	I
    //   57: aload 11
    //   59: getfield 553	android/graphics/Point:y	I
    //   62: aload 4
    //   64: invokevirtual 557	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
    //   67: invokestatic 561	android/graphics/Bitmap:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
    //   70: astore 10
    //   72: fconst_0
    //   73: fstore 5
    //   75: aload 11
    //   77: getfield 550	android/graphics/Point:x	I
    //   80: iload 8
    //   82: imul
    //   83: aload 11
    //   85: getfield 553	android/graphics/Point:y	I
    //   88: iload 9
    //   90: imul
    //   91: if_icmple +137 -> 228
    //   94: aload 11
    //   96: getfield 550	android/graphics/Point:x	I
    //   99: i2f
    //   100: iload 9
    //   102: i2f
    //   103: fdiv
    //   104: fstore 6
    //   106: aload 11
    //   108: getfield 553	android/graphics/Point:y	I
    //   111: i2f
    //   112: iload 8
    //   114: i2f
    //   115: fload 6
    //   117: fmul
    //   118: fsub
    //   119: ldc_w 436
    //   122: fmul
    //   123: fstore 5
    //   125: new 563	android/graphics/Matrix
    //   128: dup
    //   129: invokespecial 564	android/graphics/Matrix:<init>	()V
    //   132: astore 11
    //   134: aload 11
    //   136: fload 6
    //   138: fload 6
    //   140: invokevirtual 568	android/graphics/Matrix:setScale	(FF)V
    //   143: aload 11
    //   145: ldc_w 436
    //   148: fload 5
    //   150: fadd
    //   151: f2i
    //   152: i2f
    //   153: fconst_0
    //   154: invokevirtual 572	android/graphics/Matrix:postTranslate	(FF)Z
    //   157: pop
    //   158: new 574	android/graphics/Canvas
    //   161: dup
    //   162: aload 10
    //   164: invokespecial 577	android/graphics/Canvas:<init>	(Landroid/graphics/Bitmap;)V
    //   167: astore 12
    //   169: aload 12
    //   171: aload 4
    //   173: aload 11
    //   175: aconst_null
    //   176: invokevirtual 581	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
    //   179: aload 12
    //   181: aconst_null
    //   182: invokevirtual 584	android/graphics/Canvas:setBitmap	(Landroid/graphics/Bitmap;)V
    //   185: aload_3
    //   186: astore 4
    //   188: aload_3
    //   189: ifnonnull +12 -> 201
    //   192: new 62	android/app/ActivityManager$TaskDescription
    //   195: dup
    //   196: invokespecial 585	android/app/ActivityManager$TaskDescription:<init>	()V
    //   199: astore 4
    //   201: invokestatic 382	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   204: aload_1
    //   205: invokevirtual 591	android/app/Activity:getActivityToken	()Landroid/os/IBinder;
    //   208: aload_2
    //   209: aload 4
    //   211: aload 10
    //   213: invokeinterface 594 5 0
    //   218: istore 8
    //   220: iload 8
    //   222: ireturn
    //   223: astore_1
    //   224: aload_0
    //   225: monitorexit
    //   226: aload_1
    //   227: athrow
    //   228: aload 11
    //   230: getfield 553	android/graphics/Point:y	I
    //   233: i2f
    //   234: iload 8
    //   236: i2f
    //   237: fdiv
    //   238: fstore 6
    //   240: aload 11
    //   242: getfield 550	android/graphics/Point:x	I
    //   245: i2f
    //   246: fstore 7
    //   248: iload 9
    //   250: i2f
    //   251: fstore 7
    //   253: goto -128 -> 125
    //   256: astore_1
    //   257: aload_1
    //   258: invokevirtual 268	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   261: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	262	0	this	ActivityManager
    //   0	262	1	paramActivity	Activity
    //   0	262	2	paramIntent	Intent
    //   0	262	3	paramTaskDescription	TaskDescription
    //   0	262	4	paramBitmap	Bitmap
    //   73	76	5	f1	float
    //   104	135	6	f2	float
    //   246	6	7	f3	float
    //   19	216	8	i	int
    //   26	223	9	j	int
    //   40	172	10	localBitmap	Bitmap
    //   10	231	11	localObject	Object
    //   167	13	12	localCanvas	android.graphics.Canvas
    // Exception table:
    //   from	to	target	type
    //   2	12	223	finally
    //   201	220	256	android/os/RemoteException
  }
  
  public boolean clearApplicationUserData()
  {
    return clearApplicationUserData(this.mContext.getPackageName(), null);
  }
  
  public boolean clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().clearApplicationUserData(paramString, paramIPackageDataObserver, UserHandle.myUserId());
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearGrantedUriPermissions(String paramString)
  {
    try
    {
      ActivityManagerNative.getDefault().clearGrantedUriPermissions(paramString, UserHandle.myUserId());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearWatchHeapLimit()
  {
    try
    {
      ActivityManagerNative.getDefault().setDumpHeapDebugLimit(null, 0, 0L, null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void dumpPackageState(FileDescriptor paramFileDescriptor, String paramString)
  {
    dumpPackageStateStatic(paramFileDescriptor, paramString);
  }
  
  public void forceStopPackage(String paramString)
  {
    forceStopPackageAsUser(paramString, UserHandle.myUserId());
  }
  
  public void forceStopPackageAsUser(String paramString, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().forceStopPackage(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<AppBootMode> getAllAppBootModes(int paramInt)
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getAllAppBootModes(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public List<AppControlMode> getAllAppControlModes(int paramInt)
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getAllAppControlModes(paramInt);
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public int getAppBootMode(String paramString)
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getAppBootMode(paramString);
      return i;
    }
    catch (RemoteException paramString) {}
    return -1;
  }
  
  public boolean getAppBootState()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().getAppBootState();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public int getAppControlMode(String paramString, int paramInt)
  {
    try
    {
      paramInt = ActivityManagerNative.getDefault().getAppControlMode(paramString, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString) {}
    return -1;
  }
  
  public int getAppControlState(int paramInt)
  {
    try
    {
      paramInt = ActivityManagerNative.getDefault().getAppControlState(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException) {}
    return -1;
  }
  
  public Size getAppTaskThumbnailSize()
  {
    try
    {
      ensureAppTaskThumbnailSizeLocked();
      Size localSize = new Size(this.mAppTaskThumbnailSize.x, this.mAppTaskThumbnailSize.y);
      return localSize;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public List<AppTask> getAppTasks()
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      List localList = ActivityManagerNative.getDefault().getAppTasks(this.mContext.getPackageName());
      int j = localList.size();
      int i = 0;
      while (i < j)
      {
        localArrayList.add(new AppTask((IAppTask)localList.get(i)));
        i += 1;
      }
      return localRemoteException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean getBgMonitorMode()
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().getBgMonitorMode();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
      Log.e(TAG, "getBgMonitorMode error, return false");
    }
    return false;
  }
  
  public List<HighPowerApp> getBgPowerHungryList()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getBgPowerHungryList();
      return localList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public String[] getCalleePackageArray(String paramString)
  {
    try
    {
      paramString = ActivityManagerNative.getDefault().getCalleePackageArray(paramString);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  public String[] getCallerPackageArray(String paramString)
  {
    try
    {
      paramString = ActivityManagerNative.getDefault().getCallerPackageArray(paramString);
      return paramString;
    }
    catch (RemoteException paramString) {}
    return null;
  }
  
  public ConfigurationInfo getDeviceConfigurationInfo()
  {
    try
    {
      ConfigurationInfo localConfigurationInfo = ActivityManagerNative.getDefault().getDeviceConfigurationInfo();
      return localConfigurationInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getFrontActivityScreenCompatMode()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getFrontActivityScreenCompatMode();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ParceledListSlice<UriPermission> getGrantedUriPermissions(String paramString)
  {
    try
    {
      paramString = ActivityManagerNative.getDefault().getGrantedUriPermissions(paramString, UserHandle.myUserId());
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getLargeMemoryClass()
  {
    return staticGetLargeMemoryClass();
  }
  
  public int getLauncherLargeIconDensity()
  {
    Resources localResources = this.mContext.getResources();
    int i = localResources.getDisplayMetrics().densityDpi;
    if (localResources.getConfiguration().smallestScreenWidthDp < 600) {
      return i;
    }
    switch (i)
    {
    default: 
      return (int)(i * 1.5F + 0.5F);
    case 120: 
      return 160;
    case 160: 
      return 240;
    case 213: 
      return 320;
    case 240: 
      return 320;
    case 320: 
      return 480;
    }
    return 640;
  }
  
  public int getLauncherLargeIconSize()
  {
    return getLauncherLargeIconSizeInner(this.mContext);
  }
  
  public int getLockTaskModeState()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getLockTaskModeState();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getMemoryClass()
  {
    return staticGetMemoryClass();
  }
  
  public void getMemoryInfo(MemoryInfo paramMemoryInfo)
  {
    try
    {
      ActivityManagerNative.getDefault().getMemoryInfo(paramMemoryInfo);
      return;
    }
    catch (RemoteException paramMemoryInfo)
    {
      throw paramMemoryInfo.rethrowFromSystemServer();
    }
  }
  
  public boolean getPackageAskScreenCompat(String paramString)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().getPackageAskScreenCompat(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getPackageImportance(String paramString)
  {
    try
    {
      int i = RunningAppProcessInfo.procStateToImportance(ActivityManagerNative.getDefault().getPackageProcessState(paramString, this.mContext.getOpPackageName()));
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int getPackageScreenCompatMode(String paramString)
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getPackageScreenCompatMode(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Debug.MemoryInfo[] getProcessMemoryInfo(int[] paramArrayOfInt)
  {
    try
    {
      paramArrayOfInt = ActivityManagerNative.getDefault().getProcessMemoryInfo(paramArrayOfInt);
      return paramArrayOfInt;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public List<ProcessErrorStateInfo> getProcessesInErrorState()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getProcessesInErrorState();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public List<RecentTaskInfo> getRecentTasks(int paramInt1, int paramInt2)
    throws SecurityException
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getRecentTasks(paramInt1, paramInt2, UserHandle.myUserId()).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<RecentTaskInfo> getRecentTasksForUser(int paramInt1, int paramInt2, int paramInt3)
    throws SecurityException
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getRecentTasks(paramInt1, paramInt2, paramInt3).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<RunningAppProcessInfo> getRunningAppProcesses()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getRunningAppProcesses();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<ApplicationInfo> getRunningExternalApplications()
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getRunningExternalApplications();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public PendingIntent getRunningServiceControlPanel(ComponentName paramComponentName)
    throws SecurityException
  {
    try
    {
      paramComponentName = ActivityManagerNative.getDefault().getRunningServiceControlPanel(paramComponentName);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public List<RunningServiceInfo> getRunningServices(int paramInt)
    throws SecurityException
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getServices(paramInt, 0);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public List<RunningTaskInfo> getRunningTasks(int paramInt)
    throws SecurityException
  {
    try
    {
      List localList = ActivityManagerNative.getDefault().getTasks(paramInt, 0);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public TaskThumbnail getTaskThumbnail(int paramInt)
    throws SecurityException
  {
    try
    {
      TaskThumbnail localTaskThumbnail = ActivityManagerNative.getDefault().getTaskThumbnail(paramInt);
      return localTaskThumbnail;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isInHomeStack(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isInHomeStack(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isInLockTaskMode()
  {
    boolean bool = false;
    if (getLockTaskModeState() != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isLowRamDevice()
  {
    return isLowRamDeviceStatic();
  }
  
  public void isRequestPermission(boolean paramBoolean)
  {
    try
    {
      ActivityManagerNative.getDefault().isRequestPermission(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public boolean isUserRunning(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramInt, 0);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isVrModePackageEnabled(ComponentName paramComponentName)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isVrModePackageEnabled(paramComponentName);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void killBackgroundProcesses(String paramString)
  {
    try
    {
      ActivityManagerNative.getDefault().killBackgroundProcesses(paramString, UserHandle.myUserId());
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void killUid(int paramInt, String paramString)
  {
    try
    {
      ActivityManagerNative.getDefault().killUid(UserHandle.getAppId(paramInt), UserHandle.getUserId(paramInt), paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void moveTaskToFront(int paramInt1, int paramInt2)
  {
    moveTaskToFront(paramInt1, paramInt2, null);
  }
  
  public void moveTaskToFront(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    try
    {
      ActivityManagerNative.getDefault().moveTaskToFront(paramInt1, paramInt2, paramBundle);
      return;
    }
    catch (RemoteException paramBundle)
    {
      throw paramBundle.rethrowFromSystemServer();
    }
  }
  
  public boolean removeTask(int paramInt)
    throws SecurityException
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().removeTask(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void restartPackage(String paramString)
  {
    killBackgroundProcesses(paramString);
  }
  
  public int setAppBootMode(String paramString, int paramInt)
  {
    try
    {
      paramInt = ActivityManagerNative.getDefault().setAppBootMode(paramString, paramInt);
      return paramInt;
    }
    catch (RemoteException paramString) {}
    return -1;
  }
  
  public void setAppBootState(boolean paramBoolean)
  {
    try
    {
      ActivityManagerNative.getDefault().setAppBootState(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public int setAppControlMode(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().setAppControlMode(paramString, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (RemoteException paramString) {}
    return -1;
  }
  
  public int setAppControlState(int paramInt1, int paramInt2)
  {
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().setAppControlState(paramInt1, paramInt2);
      return paramInt1;
    }
    catch (RemoteException localRemoteException) {}
    return -1;
  }
  
  public void setBgMonitorMode(boolean paramBoolean)
  {
    try
    {
      ActivityManagerNative.getDefault().setBgMonitorMode(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void setFrontActivityScreenCompatMode(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().setFrontActivityScreenCompatMode(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setIgnoredAnrProcess(String paramString)
  {
    try
    {
      ActivityManagerNative.getDefault().setIgnoredAnrProcess(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void setPackageAskScreenCompat(String paramString, boolean paramBoolean)
  {
    try
    {
      ActivityManagerNative.getDefault().setPackageAskScreenCompat(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setPackageScreenCompatMode(String paramString, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().setPackageScreenCompatMode(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setPermissionServiceBinderProxy(IBinder paramIBinder, int paramInt)
    throws SecurityException
  {
    try
    {
      ActivityManagerNative.getDefault().setPermissionServiceBinderProxy(paramIBinder, paramInt);
      return;
    }
    catch (RemoteException paramIBinder) {}
  }
  
  public boolean setProcessMemoryTrimLevel(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().setProcessMemoryTrimLevel(paramString, paramInt1, paramInt2);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setWatchHeapLimit(long paramLong)
  {
    try
    {
      ActivityManagerNative.getDefault().setDumpHeapDebugLimit(null, 0, paramLong, this.mContext.getPackageName());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void startLockTaskMode(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().startLockTaskMode(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void stopBgPowerHungryApp(String paramString, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().stopBgPowerHungryApp(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void stopLockTaskMode()
  {
    try
    {
      ActivityManagerNative.getDefault().stopLockTaskMode();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean switchUser(int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().switchUser(paramInt);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void updateAccesibilityServiceFlag(String paramString, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().updateAccesibilityServiceFlag(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public static class AppBootMode
    implements Parcelable
  {
    public static final Parcelable.Creator<AppBootMode> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.AppBootMode createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.AppBootMode(paramAnonymousParcel, null);
      }
      
      public ActivityManager.AppBootMode[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.AppBootMode[paramAnonymousInt];
      }
    };
    public int callerPkgNum;
    public int mode;
    public String packageName;
    
    private AppBootMode(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public AppBootMode(String paramString, int paramInt1, int paramInt2)
    {
      this.packageName = paramString;
      this.mode = paramInt1;
      this.callerPkgNum = paramInt2;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.packageName = paramParcel.readString();
      this.mode = paramParcel.readInt();
      this.callerPkgNum = paramParcel.readInt();
    }
    
    public String toString()
    {
      return toString("");
    }
    
    public String toString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append(" packageName=");
      localStringBuilder.append(this.packageName);
      localStringBuilder.append(" mode=");
      localStringBuilder.append(this.mode);
      localStringBuilder.append(" callerPkgNum=");
      localStringBuilder.append(this.callerPkgNum);
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.packageName);
      paramParcel.writeInt(this.mode);
      paramParcel.writeInt(this.callerPkgNum);
    }
  }
  
  public static class AppControlMode
    implements Parcelable
  {
    public static final Parcelable.Creator<AppControlMode> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.AppControlMode createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.AppControlMode(paramAnonymousParcel, null);
      }
      
      public ActivityManager.AppControlMode[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.AppControlMode[paramAnonymousInt];
      }
    };
    public int mode;
    public String packageName;
    public int value;
    
    private AppControlMode(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public AppControlMode(String paramString, int paramInt1, int paramInt2)
    {
      this.packageName = paramString;
      this.mode = paramInt1;
      this.value = paramInt2;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.packageName = paramParcel.readString();
      this.mode = paramParcel.readInt();
      this.value = paramParcel.readInt();
    }
    
    public String toString()
    {
      return toString("");
    }
    
    public String toString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append(" packageName=");
      localStringBuilder.append(this.packageName);
      localStringBuilder.append(" mode=");
      localStringBuilder.append(this.mode);
      localStringBuilder.append(" value=");
      localStringBuilder.append(this.value);
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.packageName);
      paramParcel.writeInt(this.mode);
      paramParcel.writeInt(this.value);
    }
  }
  
  public static class AppTask
  {
    private IAppTask mAppTaskImpl;
    
    public AppTask(IAppTask paramIAppTask)
    {
      this.mAppTaskImpl = paramIAppTask;
    }
    
    public void finishAndRemoveTask()
    {
      try
      {
        this.mAppTaskImpl.finishAndRemoveTask();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public ActivityManager.RecentTaskInfo getTaskInfo()
    {
      try
      {
        ActivityManager.RecentTaskInfo localRecentTaskInfo = this.mAppTaskImpl.getTaskInfo();
        return localRecentTaskInfo;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void moveToFront()
    {
      try
      {
        this.mAppTaskImpl.moveToFront();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void setExcludeFromRecents(boolean paramBoolean)
    {
      try
      {
        this.mAppTaskImpl.setExcludeFromRecents(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void startActivity(Context paramContext, Intent paramIntent, Bundle paramBundle)
    {
      ActivityThread localActivityThread = ActivityThread.currentActivityThread();
      localActivityThread.getInstrumentation().execStartActivityFromAppTask(paramContext, localActivityThread.getApplicationThread(), this.mAppTaskImpl, paramIntent, paramBundle);
    }
  }
  
  public static class HighPowerApp
    implements Parcelable
  {
    public static final Parcelable.Creator<HighPowerApp> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.HighPowerApp createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.HighPowerApp(paramAnonymousParcel);
      }
      
      public ActivityManager.HighPowerApp[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.HighPowerApp[paramAnonymousInt];
      }
    };
    public boolean isLocked;
    public boolean isStopped;
    public String pkgName;
    public int powerLevel;
    public long timeStamp;
    
    public HighPowerApp(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public HighPowerApp(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
    {
      this.pkgName = paramString;
      this.powerLevel = paramInt;
      this.isLocked = paramBoolean1;
      this.isStopped = paramBoolean2;
      this.timeStamp = paramLong;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool2 = true;
      this.pkgName = paramParcel.readString();
      this.powerLevel = paramParcel.readInt();
      if (paramParcel.readInt() == 1)
      {
        bool1 = true;
        this.isLocked = bool1;
        if (paramParcel.readInt() != 1) {
          break label62;
        }
      }
      label62:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.isStopped = bool1;
        this.timeStamp = paramParcel.readLong();
        return;
        bool1 = false;
        break;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeString(this.pkgName);
      paramParcel.writeInt(this.powerLevel);
      if (this.isLocked)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (!this.isStopped) {
          break label60;
        }
      }
      label60:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeLong(this.timeStamp);
        return;
        paramInt = 0;
        break;
      }
    }
  }
  
  public static class MemoryInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.MemoryInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.MemoryInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.MemoryInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.MemoryInfo[paramAnonymousInt];
      }
    };
    public long availMem;
    public long foregroundAppThreshold;
    public long hiddenAppThreshold;
    public boolean lowMemory;
    public long secondaryServerThreshold;
    public long threshold;
    public long totalMem;
    public long visibleAppThreshold;
    
    public MemoryInfo() {}
    
    private MemoryInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool = false;
      this.availMem = paramParcel.readLong();
      this.totalMem = paramParcel.readLong();
      this.threshold = paramParcel.readLong();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.lowMemory = bool;
      this.hiddenAppThreshold = paramParcel.readLong();
      this.secondaryServerThreshold = paramParcel.readLong();
      this.visibleAppThreshold = paramParcel.readLong();
      this.foregroundAppThreshold = paramParcel.readLong();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.availMem);
      paramParcel.writeLong(this.totalMem);
      paramParcel.writeLong(this.threshold);
      if (this.lowMemory) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeLong(this.hiddenAppThreshold);
        paramParcel.writeLong(this.secondaryServerThreshold);
        paramParcel.writeLong(this.visibleAppThreshold);
        paramParcel.writeLong(this.foregroundAppThreshold);
        return;
      }
    }
  }
  
  public static class ProcessErrorStateInfo
    implements Parcelable
  {
    public static final int CRASHED = 1;
    public static final Parcelable.Creator<ProcessErrorStateInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.ProcessErrorStateInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.ProcessErrorStateInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.ProcessErrorStateInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.ProcessErrorStateInfo[paramAnonymousInt];
      }
    };
    public static final int NOT_RESPONDING = 2;
    public static final int NO_ERROR = 0;
    public int condition;
    public byte[] crashData = null;
    public String longMsg;
    public int pid;
    public String processName;
    public String shortMsg;
    public String stackTrace;
    public String tag;
    public int uid;
    
    public ProcessErrorStateInfo() {}
    
    private ProcessErrorStateInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.condition = paramParcel.readInt();
      this.processName = paramParcel.readString();
      this.pid = paramParcel.readInt();
      this.uid = paramParcel.readInt();
      this.tag = paramParcel.readString();
      this.shortMsg = paramParcel.readString();
      this.longMsg = paramParcel.readString();
      this.stackTrace = paramParcel.readString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.condition);
      paramParcel.writeString(this.processName);
      paramParcel.writeInt(this.pid);
      paramParcel.writeInt(this.uid);
      paramParcel.writeString(this.tag);
      paramParcel.writeString(this.shortMsg);
      paramParcel.writeString(this.longMsg);
      paramParcel.writeString(this.stackTrace);
    }
  }
  
  public static class RecentTaskInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RecentTaskInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.RecentTaskInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.RecentTaskInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.RecentTaskInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.RecentTaskInfo[paramAnonymousInt];
      }
    };
    public int affiliatedTaskColor;
    public int affiliatedTaskId;
    public ComponentName baseActivity;
    public Intent baseIntent;
    public Rect bounds;
    public CharSequence description;
    public long firstActiveTime;
    public int id;
    public boolean isDockable;
    public boolean isTopAppLocked;
    public long lastActiveTime;
    public int numActivities;
    public ComponentName origActivity;
    public int persistentId;
    public ComponentName realActivity;
    public int resizeMode;
    public int stackId;
    public ActivityManager.TaskDescription taskDescription;
    public ComponentName topActivity;
    public int userId;
    
    public RecentTaskInfo() {}
    
    private RecentTaskInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool2 = true;
      this.id = paramParcel.readInt();
      this.persistentId = paramParcel.readInt();
      Object localObject;
      if (paramParcel.readInt() > 0)
      {
        localObject = (Intent)Intent.CREATOR.createFromParcel(paramParcel);
        this.baseIntent = ((Intent)localObject);
        this.origActivity = ComponentName.readFromParcel(paramParcel);
        this.realActivity = ComponentName.readFromParcel(paramParcel);
        this.description = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
        if (paramParcel.readInt() <= 0) {
          break label247;
        }
        localObject = (ActivityManager.TaskDescription)ActivityManager.TaskDescription.CREATOR.createFromParcel(paramParcel);
        label98:
        this.taskDescription = ((ActivityManager.TaskDescription)localObject);
        this.stackId = paramParcel.readInt();
        this.userId = paramParcel.readInt();
        this.firstActiveTime = paramParcel.readLong();
        this.lastActiveTime = paramParcel.readLong();
        this.affiliatedTaskId = paramParcel.readInt();
        this.affiliatedTaskColor = paramParcel.readInt();
        this.baseActivity = ComponentName.readFromParcel(paramParcel);
        this.topActivity = ComponentName.readFromParcel(paramParcel);
        this.numActivities = paramParcel.readInt();
        if (paramParcel.readInt() <= 0) {
          break label253;
        }
        localObject = (Rect)Rect.CREATOR.createFromParcel(paramParcel);
        label197:
        this.bounds = ((Rect)localObject);
        if (paramParcel.readInt() != 1) {
          break label259;
        }
        bool1 = true;
        label213:
        this.isDockable = bool1;
        this.resizeMode = paramParcel.readInt();
        if (paramParcel.readInt() == 0) {
          break label264;
        }
      }
      label247:
      label253:
      label259:
      label264:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.isTopAppLocked = bool1;
        return;
        localObject = null;
        break;
        localObject = null;
        break label98;
        localObject = null;
        break label197;
        bool1 = false;
        break label213;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeInt(this.id);
      paramParcel.writeInt(this.persistentId);
      if (this.baseIntent != null)
      {
        paramParcel.writeInt(1);
        this.baseIntent.writeToParcel(paramParcel, 0);
        ComponentName.writeToParcel(this.origActivity, paramParcel);
        ComponentName.writeToParcel(this.realActivity, paramParcel);
        TextUtils.writeToParcel(this.description, paramParcel, 1);
        if (this.taskDescription == null) {
          break label223;
        }
        paramParcel.writeInt(1);
        this.taskDescription.writeToParcel(paramParcel, 0);
        label85:
        paramParcel.writeInt(this.stackId);
        paramParcel.writeInt(this.userId);
        paramParcel.writeLong(this.firstActiveTime);
        paramParcel.writeLong(this.lastActiveTime);
        paramParcel.writeInt(this.affiliatedTaskId);
        paramParcel.writeInt(this.affiliatedTaskColor);
        ComponentName.writeToParcel(this.baseActivity, paramParcel);
        ComponentName.writeToParcel(this.topActivity, paramParcel);
        paramParcel.writeInt(this.numActivities);
        if (this.bounds == null) {
          break label231;
        }
        paramParcel.writeInt(1);
        this.bounds.writeToParcel(paramParcel, 0);
        label178:
        if (!this.isDockable) {
          break label239;
        }
        paramInt = 1;
        label187:
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.resizeMode);
        if (!this.isTopAppLocked) {
          break label244;
        }
      }
      label223:
      label231:
      label239:
      label244:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
        paramParcel.writeInt(0);
        break;
        paramParcel.writeInt(0);
        break label85;
        paramParcel.writeInt(0);
        break label178;
        paramInt = 0;
        break label187;
      }
    }
  }
  
  public static class RunningAppProcessInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RunningAppProcessInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.RunningAppProcessInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.RunningAppProcessInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.RunningAppProcessInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.RunningAppProcessInfo[paramAnonymousInt];
      }
    };
    public static final int FLAG_CANT_SAVE_STATE = 1;
    public static final int FLAG_HAS_ACTIVITIES = 4;
    public static final int FLAG_PERSISTENT = 2;
    public static final int IMPORTANCE_BACKGROUND = 400;
    public static final int IMPORTANCE_CANT_SAVE_STATE = 170;
    public static final int IMPORTANCE_EMPTY = 500;
    public static final int IMPORTANCE_FOREGROUND = 100;
    public static final int IMPORTANCE_FOREGROUND_SERVICE = 125;
    public static final int IMPORTANCE_GONE = 1000;
    public static final int IMPORTANCE_PERCEPTIBLE = 130;
    public static final int IMPORTANCE_SERVICE = 300;
    public static final int IMPORTANCE_TOP_SLEEPING = 150;
    public static final int IMPORTANCE_VISIBLE = 200;
    public static final int REASON_PROVIDER_IN_USE = 1;
    public static final int REASON_SERVICE_IN_USE = 2;
    public static final int REASON_UNKNOWN = 0;
    public int flags;
    public int importance;
    public int importanceReasonCode;
    public ComponentName importanceReasonComponent;
    public int importanceReasonImportance;
    public int importanceReasonPid;
    public int lastTrimLevel;
    public int lru;
    public int pid;
    public String[] pkgList;
    public String processName;
    public int processState;
    public int uid;
    
    public RunningAppProcessInfo()
    {
      this.importance = 100;
      this.importanceReasonCode = 0;
      this.processState = 6;
    }
    
    private RunningAppProcessInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public RunningAppProcessInfo(String paramString, int paramInt, String[] paramArrayOfString)
    {
      this.processName = paramString;
      this.pid = paramInt;
      this.pkgList = paramArrayOfString;
    }
    
    public static int procStateToImportance(int paramInt)
    {
      if (paramInt == -1) {
        return 1000;
      }
      if (paramInt >= 12) {
        return 400;
      }
      if (paramInt >= 10) {
        return 300;
      }
      if (paramInt > 9) {
        return 170;
      }
      if (paramInt >= 7) {
        return 130;
      }
      if (paramInt >= 6) {
        return 200;
      }
      if (paramInt >= 5) {
        return 150;
      }
      if (paramInt >= 4) {
        return 125;
      }
      return 100;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.processName = paramParcel.readString();
      this.pid = paramParcel.readInt();
      this.uid = paramParcel.readInt();
      this.pkgList = paramParcel.readStringArray();
      this.flags = paramParcel.readInt();
      this.lastTrimLevel = paramParcel.readInt();
      this.importance = paramParcel.readInt();
      this.lru = paramParcel.readInt();
      this.importanceReasonCode = paramParcel.readInt();
      this.importanceReasonPid = paramParcel.readInt();
      this.importanceReasonComponent = ComponentName.readFromParcel(paramParcel);
      this.importanceReasonImportance = paramParcel.readInt();
      this.processState = paramParcel.readInt();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.processName);
      paramParcel.writeInt(this.pid);
      paramParcel.writeInt(this.uid);
      paramParcel.writeStringArray(this.pkgList);
      paramParcel.writeInt(this.flags);
      paramParcel.writeInt(this.lastTrimLevel);
      paramParcel.writeInt(this.importance);
      paramParcel.writeInt(this.lru);
      paramParcel.writeInt(this.importanceReasonCode);
      paramParcel.writeInt(this.importanceReasonPid);
      ComponentName.writeToParcel(this.importanceReasonComponent, paramParcel);
      paramParcel.writeInt(this.importanceReasonImportance);
      paramParcel.writeInt(this.processState);
    }
  }
  
  public static class RunningServiceInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RunningServiceInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.RunningServiceInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.RunningServiceInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.RunningServiceInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.RunningServiceInfo[paramAnonymousInt];
      }
    };
    public static final int FLAG_FOREGROUND = 2;
    public static final int FLAG_PERSISTENT_PROCESS = 8;
    public static final int FLAG_STARTED = 1;
    public static final int FLAG_SYSTEM_PROCESS = 4;
    public long activeSince;
    public int clientCount;
    public int clientLabel;
    public String clientPackage;
    public int crashCount;
    public int flags;
    public boolean foreground;
    public long lastActivityTime;
    public int pid;
    public String process;
    public long restarting;
    public ComponentName service;
    public boolean started;
    public int uid;
    
    public RunningServiceInfo() {}
    
    private RunningServiceInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool2 = true;
      this.service = ComponentName.readFromParcel(paramParcel);
      this.pid = paramParcel.readInt();
      this.uid = paramParcel.readInt();
      this.process = paramParcel.readString();
      if (paramParcel.readInt() != 0)
      {
        bool1 = true;
        this.foreground = bool1;
        this.activeSince = paramParcel.readLong();
        if (paramParcel.readInt() == 0) {
          break label132;
        }
      }
      label132:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.started = bool1;
        this.clientCount = paramParcel.readInt();
        this.crashCount = paramParcel.readInt();
        this.lastActivityTime = paramParcel.readLong();
        this.restarting = paramParcel.readLong();
        this.flags = paramParcel.readInt();
        this.clientPackage = paramParcel.readString();
        this.clientLabel = paramParcel.readInt();
        return;
        bool1 = false;
        break;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      ComponentName.writeToParcel(this.service, paramParcel);
      paramParcel.writeInt(this.pid);
      paramParcel.writeInt(this.uid);
      paramParcel.writeString(this.process);
      if (this.foreground)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        paramParcel.writeLong(this.activeSince);
        if (!this.started) {
          break label132;
        }
      }
      label132:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.clientCount);
        paramParcel.writeInt(this.crashCount);
        paramParcel.writeLong(this.lastActivityTime);
        paramParcel.writeLong(this.restarting);
        paramParcel.writeInt(this.flags);
        paramParcel.writeString(this.clientPackage);
        paramParcel.writeInt(this.clientLabel);
        return;
        paramInt = 0;
        break;
      }
    }
  }
  
  public static class RunningTaskInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<RunningTaskInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.RunningTaskInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.RunningTaskInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.RunningTaskInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.RunningTaskInfo[paramAnonymousInt];
      }
    };
    public ComponentName baseActivity;
    public CharSequence description;
    public int id;
    public boolean isDockable;
    public long lastActiveTime;
    public int numActivities;
    public int numRunning;
    public int resizeMode;
    public int stackId;
    public Bitmap thumbnail;
    public ComponentName topActivity;
    
    public RunningTaskInfo() {}
    
    private RunningTaskInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.id = paramParcel.readInt();
      this.stackId = paramParcel.readInt();
      this.baseActivity = ComponentName.readFromParcel(paramParcel);
      this.topActivity = ComponentName.readFromParcel(paramParcel);
      if (paramParcel.readInt() != 0)
      {
        this.thumbnail = ((Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel));
        this.description = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
        this.numActivities = paramParcel.readInt();
        this.numRunning = paramParcel.readInt();
        if (paramParcel.readInt() == 0) {
          break label118;
        }
      }
      label118:
      for (boolean bool = true;; bool = false)
      {
        this.isDockable = bool;
        this.resizeMode = paramParcel.readInt();
        return;
        this.thumbnail = null;
        break;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = 1;
      paramParcel.writeInt(this.id);
      paramParcel.writeInt(this.stackId);
      ComponentName.writeToParcel(this.baseActivity, paramParcel);
      ComponentName.writeToParcel(this.topActivity, paramParcel);
      if (this.thumbnail != null)
      {
        paramParcel.writeInt(1);
        this.thumbnail.writeToParcel(paramParcel, 0);
        TextUtils.writeToParcel(this.description, paramParcel, 1);
        paramParcel.writeInt(this.numActivities);
        paramParcel.writeInt(this.numRunning);
        if (!this.isDockable) {
          break label109;
        }
      }
      for (;;)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.resizeMode);
        return;
        paramParcel.writeInt(0);
        break;
        label109:
        paramInt = 0;
      }
    }
  }
  
  public static class StackId
  {
    public static final int DOCKED_STACK_ID = 3;
    public static final int FIRST_DYNAMIC_STACK_ID = 5;
    public static final int FIRST_STATIC_STACK_ID = 0;
    public static final int FREEFORM_WORKSPACE_STACK_ID = 2;
    public static final int FULLSCREEN_WORKSPACE_STACK_ID = 1;
    public static final int HOME_STACK_ID = 0;
    public static final int INVALID_STACK_ID = -1;
    public static final int LAST_STATIC_STACK_ID = 4;
    public static final int PINNED_STACK_ID = 4;
    
    public static boolean activitiesCanRequestVisibleBehind(int paramInt)
    {
      return paramInt == 1;
    }
    
    public static boolean allowTopTaskToReturnHome(int paramInt)
    {
      return paramInt != 4;
    }
    
    public static boolean canReceiveKeys(int paramInt)
    {
      return paramInt != 4;
    }
    
    public static boolean hasMovementAnimations(int paramInt)
    {
      return paramInt != 4;
    }
    
    public static boolean hasWindowDecor(int paramInt)
    {
      return paramInt == 2;
    }
    
    public static boolean hasWindowShadow(int paramInt)
    {
      return (paramInt == 2) || (paramInt == 4);
    }
    
    public static boolean isAllowedOverLockscreen(int paramInt)
    {
      return (paramInt == 0) || (paramInt == 1);
    }
    
    public static boolean isAlwaysOnTop(int paramInt)
    {
      return paramInt == 4;
    }
    
    public static boolean isDynamicStacksVisibleBehindAllowed(int paramInt)
    {
      return paramInt == 4;
    }
    
    public static boolean isMultiWindowStack(int paramInt)
    {
      if ((isStaticStack(paramInt)) || (paramInt == 4)) {}
      while ((paramInt == 2) || (paramInt == 3)) {
        return true;
      }
      return false;
    }
    
    public static boolean isResizeableByDockedStack(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (isStaticStack(paramInt))
      {
        bool1 = bool2;
        if (paramInt != 3)
        {
          bool1 = bool2;
          if (paramInt != 4) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public static boolean isStaticStack(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramInt >= 0)
      {
        bool1 = bool2;
        if (paramInt <= 4) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public static boolean isTaskResizeAllowed(int paramInt)
    {
      return paramInt == 2;
    }
    
    public static boolean isTaskResizeableByDockedStack(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (isStaticStack(paramInt))
      {
        bool1 = bool2;
        if (paramInt != 2)
        {
          bool1 = bool2;
          if (paramInt != 3)
          {
            bool1 = bool2;
            if (paramInt != 4) {
              bool1 = true;
            }
          }
        }
      }
      return bool1;
    }
    
    public static boolean keepFocusInStackIfPossible(int paramInt)
    {
      if ((paramInt == 2) || (paramInt == 3)) {}
      while (paramInt == 4) {
        return true;
      }
      return false;
    }
    
    public static boolean keepVisibleDeadAppWindowOnScreen(int paramInt)
    {
      return paramInt != 4;
    }
    
    public static boolean normallyFullscreenWindows(int paramInt)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramInt != 4)
      {
        bool1 = bool2;
        if (paramInt != 2)
        {
          bool1 = bool2;
          if (paramInt != 3) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public static boolean persistTaskBounds(int paramInt)
    {
      return paramInt == 2;
    }
    
    public static boolean replaceWindowsOnTaskMove(int paramInt1, int paramInt2)
    {
      return (paramInt1 == 2) || (paramInt2 == 2);
    }
    
    public static boolean resizeStackWithLaunchBounds(int paramInt)
    {
      return paramInt == 4;
    }
    
    public static boolean tasksAreFloating(int paramInt)
    {
      return (paramInt == 2) || (paramInt == 4);
    }
    
    public static boolean useAnimationSpecForAppTransition(int paramInt)
    {
      if ((paramInt == 2) || (paramInt == 1)) {}
      while ((paramInt == 3) || (paramInt == -1)) {
        return true;
      }
      return false;
    }
    
    public static boolean useWindowFrameForBackdrop(int paramInt)
    {
      return (paramInt == 2) || (paramInt == 4);
    }
    
    public static boolean windowsAreScaleable(int paramInt)
    {
      return paramInt == 4;
    }
  }
  
  public static class StackInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<StackInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.StackInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.StackInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.StackInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.StackInfo[paramAnonymousInt];
      }
    };
    public Rect bounds = new Rect();
    public int displayId;
    public int position;
    public int stackId;
    public Rect[] taskBounds;
    public int[] taskIds;
    public String[] taskNames;
    public int[] taskUserIds;
    public ComponentName topActivity;
    public int userId;
    public boolean visible;
    
    public StackInfo() {}
    
    private StackInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool = false;
      this.stackId = paramParcel.readInt();
      this.bounds = new Rect(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
      this.taskIds = paramParcel.createIntArray();
      this.taskNames = paramParcel.createStringArray();
      int j = paramParcel.readInt();
      if (j > 0)
      {
        this.taskBounds = new Rect[j];
        int i = 0;
        while (i < j)
        {
          this.taskBounds[i] = new Rect();
          this.taskBounds[i].set(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
          i += 1;
        }
      }
      this.taskBounds = null;
      this.taskUserIds = paramParcel.createIntArray();
      this.displayId = paramParcel.readInt();
      this.userId = paramParcel.readInt();
      if (paramParcel.readInt() > 0) {
        bool = true;
      }
      this.visible = bool;
      this.position = paramParcel.readInt();
      if (paramParcel.readInt() > 0) {
        this.topActivity = ComponentName.readFromParcel(paramParcel);
      }
    }
    
    public String toString()
    {
      return toString("");
    }
    
    public String toString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder(256);
      localStringBuilder.append(paramString);
      localStringBuilder.append("Stack id=");
      localStringBuilder.append(this.stackId);
      localStringBuilder.append(" bounds=");
      localStringBuilder.append(this.bounds.toShortString());
      localStringBuilder.append(" displayId=");
      localStringBuilder.append(this.displayId);
      localStringBuilder.append(" userId=");
      localStringBuilder.append(this.userId);
      localStringBuilder.append("\n");
      paramString = paramString + "  ";
      int i = 0;
      while (i < this.taskIds.length)
      {
        localStringBuilder.append(paramString);
        localStringBuilder.append("taskId=");
        localStringBuilder.append(this.taskIds[i]);
        localStringBuilder.append(": ");
        localStringBuilder.append(this.taskNames[i]);
        if (this.taskBounds != null)
        {
          localStringBuilder.append(" bounds=");
          localStringBuilder.append(this.taskBounds[i].toShortString());
        }
        localStringBuilder.append(" userId=").append(this.taskUserIds[i]);
        localStringBuilder.append(" visible=").append(this.visible);
        if (this.topActivity != null) {
          localStringBuilder.append(" topActivity=").append(this.topActivity);
        }
        localStringBuilder.append("\n");
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.stackId);
      paramParcel.writeInt(this.bounds.left);
      paramParcel.writeInt(this.bounds.top);
      paramParcel.writeInt(this.bounds.right);
      paramParcel.writeInt(this.bounds.bottom);
      paramParcel.writeIntArray(this.taskIds);
      paramParcel.writeStringArray(this.taskNames);
      if (this.taskBounds == null) {}
      for (paramInt = 0;; paramInt = this.taskBounds.length)
      {
        paramParcel.writeInt(paramInt);
        int i = 0;
        while (i < paramInt)
        {
          paramParcel.writeInt(this.taskBounds[i].left);
          paramParcel.writeInt(this.taskBounds[i].top);
          paramParcel.writeInt(this.taskBounds[i].right);
          paramParcel.writeInt(this.taskBounds[i].bottom);
          i += 1;
        }
      }
      paramParcel.writeIntArray(this.taskUserIds);
      paramParcel.writeInt(this.displayId);
      paramParcel.writeInt(this.userId);
      if (this.visible) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.position);
        if (this.topActivity == null) {
          break;
        }
        paramParcel.writeInt(1);
        this.topActivity.writeToParcel(paramParcel, 0);
        return;
      }
      paramParcel.writeInt(0);
    }
  }
  
  public static class TaskDescription
    implements Parcelable
  {
    private static final String ATTR_TASKDESCRIPTIONCOLOR_BACKGROUND = "task_description_colorBackground";
    private static final String ATTR_TASKDESCRIPTIONCOLOR_PRIMARY = "task_description_color";
    private static final String ATTR_TASKDESCRIPTIONICONFILENAME = "task_description_icon_filename";
    private static final String ATTR_TASKDESCRIPTIONLABEL = "task_description_label";
    public static final String ATTR_TASKDESCRIPTION_PREFIX = "task_description_";
    public static final Parcelable.Creator<TaskDescription> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.TaskDescription createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.TaskDescription(paramAnonymousParcel, null);
      }
      
      public ActivityManager.TaskDescription[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.TaskDescription[paramAnonymousInt];
      }
    };
    private int mColorBackground;
    private int mColorPrimary;
    private Bitmap mIcon;
    private String mIconFilename;
    private String mLabel;
    
    public TaskDescription()
    {
      this(null, null, null, 0, 0);
    }
    
    public TaskDescription(TaskDescription paramTaskDescription)
    {
      copyFrom(paramTaskDescription);
    }
    
    private TaskDescription(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public TaskDescription(String paramString)
    {
      this(paramString, null, null, 0, 0);
    }
    
    public TaskDescription(String paramString, Bitmap paramBitmap)
    {
      this(paramString, paramBitmap, null, 0, 0);
    }
    
    public TaskDescription(String paramString, Bitmap paramBitmap, int paramInt)
    {
      this(paramString, paramBitmap, null, paramInt, 0);
      if ((paramInt != 0) && (Color.alpha(paramInt) != 255)) {
        throw new RuntimeException("A TaskDescription's primary color should be opaque");
      }
    }
    
    public TaskDescription(String paramString1, Bitmap paramBitmap, String paramString2, int paramInt1, int paramInt2)
    {
      this.mLabel = paramString1;
      this.mIcon = paramBitmap;
      this.mIconFilename = paramString2;
      this.mColorPrimary = paramInt1;
      this.mColorBackground = paramInt2;
    }
    
    public static Bitmap loadTaskDescriptionIcon(String paramString, int paramInt)
    {
      if (paramString != null) {
        try
        {
          paramString = ActivityManagerNative.getDefault().getTaskDescriptionIcon(paramString, paramInt);
          return paramString;
        }
        catch (RemoteException paramString)
        {
          throw paramString.rethrowFromSystemServer();
        }
      }
      return null;
    }
    
    public void copyFrom(TaskDescription paramTaskDescription)
    {
      this.mLabel = paramTaskDescription.mLabel;
      this.mIcon = paramTaskDescription.mIcon;
      this.mIconFilename = paramTaskDescription.mIconFilename;
      this.mColorPrimary = paramTaskDescription.mColorPrimary;
      this.mColorBackground = paramTaskDescription.mColorBackground;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getBackgroundColor()
    {
      return this.mColorBackground;
    }
    
    public Bitmap getIcon()
    {
      if (this.mIcon != null) {
        return this.mIcon;
      }
      return loadTaskDescriptionIcon(this.mIconFilename, UserHandle.myUserId());
    }
    
    public String getIconFilename()
    {
      return this.mIconFilename;
    }
    
    public Bitmap getInMemoryIcon()
    {
      return this.mIcon;
    }
    
    public String getLabel()
    {
      return this.mLabel;
    }
    
    public int getPrimaryColor()
    {
      return this.mColorPrimary;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      Object localObject2 = null;
      if (paramParcel.readInt() > 0)
      {
        localObject1 = paramParcel.readString();
        this.mLabel = ((String)localObject1);
        if (paramParcel.readInt() <= 0) {
          break label85;
        }
      }
      label85:
      for (Object localObject1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel);; localObject1 = null)
      {
        this.mIcon = ((Bitmap)localObject1);
        this.mColorPrimary = paramParcel.readInt();
        this.mColorBackground = paramParcel.readInt();
        localObject1 = localObject2;
        if (paramParcel.readInt() > 0) {
          localObject1 = paramParcel.readString();
        }
        this.mIconFilename = ((String)localObject1);
        return;
        localObject1 = null;
        break;
      }
    }
    
    public void restoreFromXml(String paramString1, String paramString2)
    {
      if ("task_description_label".equals(paramString1)) {
        setLabel(paramString2);
      }
      do
      {
        return;
        if ("task_description_color".equals(paramString1))
        {
          setPrimaryColor((int)Long.parseLong(paramString2, 16));
          return;
        }
        if ("task_description_colorBackground".equals(paramString1))
        {
          setBackgroundColor((int)Long.parseLong(paramString2, 16));
          return;
        }
      } while (!"task_description_icon_filename".equals(paramString1));
      setIconFilename(paramString2);
    }
    
    public void saveToXml(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      if (this.mLabel != null) {
        paramXmlSerializer.attribute(null, "task_description_label", this.mLabel);
      }
      if (this.mColorPrimary != 0) {
        paramXmlSerializer.attribute(null, "task_description_color", Integer.toHexString(this.mColorPrimary));
      }
      if (this.mColorBackground != 0) {
        paramXmlSerializer.attribute(null, "task_description_colorBackground", Integer.toHexString(this.mColorBackground));
      }
      if (this.mIconFilename != null) {
        paramXmlSerializer.attribute(null, "task_description_icon_filename", this.mIconFilename);
      }
    }
    
    public void setBackgroundColor(int paramInt)
    {
      if ((paramInt != 0) && (Color.alpha(paramInt) != 255)) {
        throw new RuntimeException("A TaskDescription's background color should be opaque");
      }
      this.mColorBackground = paramInt;
    }
    
    public void setIcon(Bitmap paramBitmap)
    {
      this.mIcon = paramBitmap;
    }
    
    public void setIconFilename(String paramString)
    {
      this.mIconFilename = paramString;
      this.mIcon = null;
    }
    
    public void setLabel(String paramString)
    {
      this.mLabel = paramString;
    }
    
    public void setPrimaryColor(int paramInt)
    {
      if ((paramInt != 0) && (Color.alpha(paramInt) != 255)) {
        throw new RuntimeException("A TaskDescription's primary color should be opaque");
      }
      this.mColorPrimary = paramInt;
    }
    
    public String toString()
    {
      return "TaskDescription Label: " + this.mLabel + " Icon: " + this.mIcon + " IconFilename: " + this.mIconFilename + " colorPrimary: " + this.mColorPrimary + " colorBackground: " + this.mColorBackground;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mLabel == null)
      {
        paramParcel.writeInt(0);
        if (this.mIcon != null) {
          break label69;
        }
        paramParcel.writeInt(0);
      }
      for (;;)
      {
        paramParcel.writeInt(this.mColorPrimary);
        paramParcel.writeInt(this.mColorBackground);
        if (this.mIconFilename != null) {
          break label86;
        }
        paramParcel.writeInt(0);
        return;
        paramParcel.writeInt(1);
        paramParcel.writeString(this.mLabel);
        break;
        label69:
        paramParcel.writeInt(1);
        this.mIcon.writeToParcel(paramParcel, 0);
      }
      label86:
      paramParcel.writeInt(1);
      paramParcel.writeString(this.mIconFilename);
    }
  }
  
  public static class TaskThumbnail
    implements Parcelable
  {
    public static final Parcelable.Creator<TaskThumbnail> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.TaskThumbnail createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.TaskThumbnail(paramAnonymousParcel, null);
      }
      
      public ActivityManager.TaskThumbnail[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.TaskThumbnail[paramAnonymousInt];
      }
    };
    public Bitmap mainThumbnail;
    public ParcelFileDescriptor thumbnailFileDescriptor;
    public ActivityManager.TaskThumbnailInfo thumbnailInfo;
    
    public TaskThumbnail() {}
    
    private TaskThumbnail(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      if (this.thumbnailFileDescriptor != null) {
        return this.thumbnailFileDescriptor.describeContents();
      }
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      if (paramParcel.readInt() != 0)
      {
        this.mainThumbnail = ((Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel));
        if (paramParcel.readInt() == 0) {
          break label78;
        }
      }
      label78:
      for (this.thumbnailFileDescriptor = ((ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel));; this.thumbnailFileDescriptor = null)
      {
        if (paramParcel.readInt() == 0) {
          break label86;
        }
        this.thumbnailInfo = ((ActivityManager.TaskThumbnailInfo)ActivityManager.TaskThumbnailInfo.CREATOR.createFromParcel(paramParcel));
        return;
        this.mainThumbnail = null;
        break;
      }
      label86:
      this.thumbnailInfo = null;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mainThumbnail != null)
      {
        paramParcel.writeInt(1);
        this.mainThumbnail.writeToParcel(paramParcel, paramInt);
        if (this.thumbnailFileDescriptor == null) {
          break label72;
        }
        paramParcel.writeInt(1);
        this.thumbnailFileDescriptor.writeToParcel(paramParcel, paramInt);
      }
      for (;;)
      {
        if (this.thumbnailInfo == null) {
          break label80;
        }
        paramParcel.writeInt(1);
        this.thumbnailInfo.writeToParcel(paramParcel, paramInt);
        return;
        paramParcel.writeInt(0);
        break;
        label72:
        paramParcel.writeInt(0);
      }
      label80:
      paramParcel.writeInt(0);
    }
  }
  
  public static class TaskThumbnailInfo
    implements Parcelable
  {
    private static final String ATTR_SCREEN_ORIENTATION = "task_thumbnailinfo_screen_orientation";
    private static final String ATTR_TASK_HEIGHT = "task_thumbnailinfo_task_height";
    public static final String ATTR_TASK_THUMBNAILINFO_PREFIX = "task_thumbnailinfo_";
    private static final String ATTR_TASK_WIDTH = "task_thumbnailinfo_task_width";
    public static final Parcelable.Creator<TaskThumbnailInfo> CREATOR = new Parcelable.Creator()
    {
      public ActivityManager.TaskThumbnailInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActivityManager.TaskThumbnailInfo(paramAnonymousParcel, null);
      }
      
      public ActivityManager.TaskThumbnailInfo[] newArray(int paramAnonymousInt)
      {
        return new ActivityManager.TaskThumbnailInfo[paramAnonymousInt];
      }
    };
    public int screenOrientation = 0;
    public int taskHeight;
    public int taskWidth;
    
    public TaskThumbnailInfo() {}
    
    private TaskThumbnailInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public void copyFrom(TaskThumbnailInfo paramTaskThumbnailInfo)
    {
      this.taskWidth = paramTaskThumbnailInfo.taskWidth;
      this.taskHeight = paramTaskThumbnailInfo.taskHeight;
      this.screenOrientation = paramTaskThumbnailInfo.screenOrientation;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.taskWidth = paramParcel.readInt();
      this.taskHeight = paramParcel.readInt();
      this.screenOrientation = paramParcel.readInt();
    }
    
    public void reset()
    {
      this.taskWidth = 0;
      this.taskHeight = 0;
      this.screenOrientation = 0;
    }
    
    public void restoreFromXml(String paramString1, String paramString2)
    {
      if ("task_thumbnailinfo_task_width".equals(paramString1)) {
        this.taskWidth = Integer.parseInt(paramString2);
      }
      do
      {
        return;
        if ("task_thumbnailinfo_task_height".equals(paramString1))
        {
          this.taskHeight = Integer.parseInt(paramString2);
          return;
        }
      } while (!"task_thumbnailinfo_screen_orientation".equals(paramString1));
      this.screenOrientation = Integer.parseInt(paramString2);
    }
    
    public void saveToXml(XmlSerializer paramXmlSerializer)
      throws IOException
    {
      paramXmlSerializer.attribute(null, "task_thumbnailinfo_task_width", Integer.toString(this.taskWidth));
      paramXmlSerializer.attribute(null, "task_thumbnailinfo_task_height", Integer.toString(this.taskHeight));
      paramXmlSerializer.attribute(null, "task_thumbnailinfo_screen_orientation", Integer.toString(this.screenOrientation));
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.taskWidth);
      paramParcel.writeInt(this.taskHeight);
      paramParcel.writeInt(this.screenOrientation);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */