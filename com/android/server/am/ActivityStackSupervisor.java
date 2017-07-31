package com.android.server.am;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.StackId;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityContainer;
import android.app.IActivityContainer.Stub;
import android.app.IActivityContainerCallback;
import android.app.IActivityManager.WaitResult;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.admin.IDevicePolicyManager;
import android.app.admin.IDevicePolicyManager.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.VirtualDisplay;
import android.hardware.input.InputManagerInternal;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Trace;
import android.os.TransactionTooLargeException;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.service.voice.IVoiceInteractionSession;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IApplicationToken.Stub;
import android.view.Surface;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.AppOpsService;
import com.android.server.LocalServices;
import com.android.server.wm.WindowManagerService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ActivityStackSupervisor
  implements DisplayManager.DisplayListener
{
  private static final ArrayMap<String, String> ACTION_TO_RUNTIME_PERMISSION;
  private static final int ACTIVITY_RESTRICTION_APPOP = 2;
  private static final int ACTIVITY_RESTRICTION_NONE = 0;
  private static final int ACTIVITY_RESTRICTION_PERMISSION = 1;
  static final int CONTAINER_CALLBACK_TASK_LIST_EMPTY = 111;
  static final int CONTAINER_CALLBACK_VISIBILITY = 108;
  static final boolean CREATE_IF_NEEDED = true;
  static final boolean DEFER_RESUME = true;
  private static final int FIT_WITHIN_BOUNDS_DIVIDER = 3;
  static final boolean FORCE_FOCUS = true;
  static final int HANDLE_DISPLAY_ADDED = 105;
  static final int HANDLE_DISPLAY_CHANGED = 106;
  static final int HANDLE_DISPLAY_REMOVED = 107;
  static final int IDLE_NOW_MSG = 101;
  static final int IDLE_TIMEOUT = 10000;
  static final int IDLE_TIMEOUT_MSG = 100;
  static final int LAUNCH_TASK_BEHIND_COMPLETE = 112;
  static final int LAUNCH_TIMEOUT = 10000;
  static final int LAUNCH_TIMEOUT_MSG = 104;
  static final int LOCK_TASK_END_MSG = 110;
  static final int LOCK_TASK_START_MSG = 109;
  private static final String LOCK_TASK_TAG = "Lock-to-App";
  private static final int MAX_TASK_IDS_PER_USER = 100000;
  static final boolean MOVING = true;
  static final boolean ON_TOP = true;
  static final boolean PRESERVE_WINDOWS = true;
  static final int REPORT_MULTI_WINDOW_MODE_CHANGED_MSG = 114;
  static final int REPORT_PIP_MODE_CHANGED_MSG = 115;
  static final boolean RESTORE_FROM_RECENTS = true;
  static final int RESUME_TOP_ACTIVITY_MSG = 102;
  static final int SHOW_LOCK_TASK_ESCAPE_MESSAGE_MSG = 113;
  static final int SLEEP_TIMEOUT = 5000;
  static final int SLEEP_TIMEOUT_MSG = 103;
  private static final String TAG = "ActivityManager";
  private static final String TAG_CONTAINERS = TAG + ActivityManagerDebugConfig.POSTFIX_CONTAINERS;
  private static final String TAG_IDLE = TAG + ActivityManagerDebugConfig.POSTFIX_IDLE;
  private static final String TAG_LOCKTASK = TAG + ActivityManagerDebugConfig.POSTFIX_LOCKTASK;
  private static final String TAG_PAUSE = TAG + ActivityManagerDebugConfig.POSTFIX_PAUSE;
  private static final String TAG_RECENTS = TAG + ActivityManagerDebugConfig.POSTFIX_RECENTS;
  private static final String TAG_RELEASE = TAG + ActivityManagerDebugConfig.POSTFIX_RELEASE;
  private static final String TAG_STACK = TAG + ActivityManagerDebugConfig.POSTFIX_STACK;
  private static final String TAG_STATES = TAG + ActivityManagerDebugConfig.POSTFIX_STATES;
  private static final String TAG_SWITCH = TAG + ActivityManagerDebugConfig.POSTFIX_SWITCH;
  static final String TAG_TASKS = TAG + ActivityManagerDebugConfig.POSTFIX_TASKS;
  private static final String TAG_VISIBLE_BEHIND = TAG + ActivityManagerDebugConfig.POSTFIX_VISIBLE_BEHIND;
  static final boolean VALIDATE_WAKE_LOCK_CALLER = false;
  private static final String VIRTUAL_DISPLAY_BASE_NAME = "ActivityViewVirtualDisplay";
  boolean inResumeTopActivity;
  public int[] lBoostCpuParamVal;
  public int[] lBoostPackParamVal;
  public int lBoostTimeOut = 0;
  public int lDisPackTimeOut = 0;
  private SparseArray<ActivityContainer> mActivityContainers = new SparseArray();
  private final SparseArray<ActivityDisplay> mActivityDisplays = new SparseArray();
  final ActivityMetricsLogger mActivityMetricsLogger;
  private boolean mAllowDockedStackResize = true;
  boolean mAppVisibilitiesChangedSinceLastPause;
  private final SparseIntArray mCurTaskIdForUser = new SparseIntArray(20);
  int mCurrentUser;
  int mDefaultMinSizeOfResizeableTask = -1;
  private IDevicePolicyManager mDevicePolicyManager;
  DisplayManager mDisplayManager;
  final ArrayList<ActivityRecord> mFinishingActivities = new ArrayList();
  ActivityStack mFocusedStack;
  PowerManager.WakeLock mGoingToSleep;
  final ArrayList<ActivityRecord> mGoingToSleepActivities = new ArrayList();
  final ActivityStackSupervisorHandler mHandler;
  ActivityStack mHomeStack;
  private ArrayList<ComponentName> mIgnoreSceneEvaluationApps;
  InputManagerInternal mInputManagerInternal;
  boolean mIsDockMinimized;
  public boolean mIsPerfBoostEnabled = false;
  public boolean mIsperfDisablepackingEnable = false;
  private ActivityStack mLastFocusedStack;
  PowerManager.WakeLock mLaunchingActivity;
  private int mLockTaskModeState;
  ArrayList<TaskRecord> mLockTaskModeTasks = new ArrayList();
  private LockTaskNotify mLockTaskNotify;
  final ArrayList<ActivityRecord> mMultiWindowModeChangedActivities = new ArrayList();
  private int mNextFreeStackId = 5;
  public BoostFramework mPerfBoost = null;
  public BoostFramework mPerfIop = null;
  public BoostFramework mPerfPack = null;
  final ArrayList<ActivityRecord> mPipModeChangedActivities = new ArrayList();
  private RecentTasks mRecentTasks;
  private final ResizeDockedStackTimeout mResizeDockedStackTimeout;
  private final ArraySet<Integer> mResizingTasksDuringAnimation = new ArraySet();
  final ActivityManagerService mService;
  boolean mSleepTimeout = false;
  final ArrayList<UserState> mStartingUsers = new ArrayList();
  private IStatusBarService mStatusBarService;
  final ArrayList<ActivityRecord> mStoppingActivities = new ArrayList();
  private boolean mTaskLayersChanged = true;
  private final SparseArray<Rect> mTmpBounds = new SparseArray();
  private final SparseArray<Configuration> mTmpConfigs = new SparseArray();
  private final FindTaskResult mTmpFindTaskResult = new FindTaskResult();
  private final SparseArray<Rect> mTmpInsetBounds = new SparseArray();
  private IBinder mToken = new Binder();
  boolean mUserLeaving = false;
  SparseIntArray mUserStackInFront = new SparseIntArray(2);
  final ArrayList<IActivityManager.WaitResult> mWaitingActivityLaunched = new ArrayList();
  final ArrayList<IActivityManager.WaitResult> mWaitingActivityVisible = new ArrayList();
  final ArrayList<ActivityRecord> mWaitingVisibleActivities = new ArrayList();
  WindowManagerService mWindowManager;
  private final Rect tempRect = new Rect();
  private final Rect tempRect2 = new Rect();
  
  static
  {
    ACTION_TO_RUNTIME_PERMISSION = new ArrayMap();
    ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.IMAGE_CAPTURE", "android.permission.CAMERA");
    ACTION_TO_RUNTIME_PERMISSION.put("android.media.action.VIDEO_CAPTURE", "android.permission.CAMERA");
    ACTION_TO_RUNTIME_PERMISSION.put("android.intent.action.CALL", "android.permission.CALL_PHONE");
  }
  
  public ActivityStackSupervisor(ActivityManagerService paramActivityManagerService)
  {
    this.mService = paramActivityManagerService;
    this.mHandler = new ActivityStackSupervisorHandler(this.mService.mHandler.getLooper());
    this.mActivityMetricsLogger = new ActivityMetricsLogger(this, this.mService.mContext);
    this.mResizeDockedStackTimeout = new ResizeDockedStackTimeout(paramActivityManagerService, this, this.mHandler);
    this.mIsPerfBoostEnabled = this.mService.mContext.getResources().getBoolean(17957046);
    this.mIsperfDisablepackingEnable = this.mService.mContext.getResources().getBoolean(17957049);
    if (this.mIsPerfBoostEnabled)
    {
      this.lBoostTimeOut = this.mService.mContext.getResources().getInteger(17694890);
      this.lBoostCpuParamVal = this.mService.mContext.getResources().getIntArray(17236043);
    }
    if (this.mIsperfDisablepackingEnable)
    {
      this.lDisPackTimeOut = this.mService.mContext.getResources().getInteger(17694893);
      this.lBoostPackParamVal = this.mService.mContext.getResources().getIntArray(17236047);
    }
  }
  
  private void calculateDefaultMinimalSizeOfResizeableTasks(ActivityDisplay paramActivityDisplay)
  {
    this.mDefaultMinSizeOfResizeableTask = this.mService.mContext.getResources().getDimensionPixelSize(17105089);
  }
  
  private boolean checkFinishBootingLocked()
  {
    boolean bool2 = this.mService.mBooting;
    boolean bool1 = false;
    this.mService.mBooting = false;
    if (!this.mService.mBooted)
    {
      this.mService.mBooted = true;
      bool1 = true;
    }
    if ((bool2) || (bool1)) {
      this.mService.postFinishBooting(bool2, bool1);
    }
    return bool2;
  }
  
  /* Error */
  static boolean dumpHistoryList(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, List<ActivityRecord> paramList, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3, boolean paramBoolean4, String paramString4, String paramString5)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 20
    //   3: aconst_null
    //   4: astore 18
    //   6: aconst_null
    //   7: astore 22
    //   9: iconst_0
    //   10: istore 16
    //   12: aload_2
    //   13: invokeinterface 530 1 0
    //   18: iconst_1
    //   19: isub
    //   20: istore 12
    //   22: aload 11
    //   24: astore 21
    //   26: aload 10
    //   28: astore 19
    //   30: aload 22
    //   32: astore 11
    //   34: iload 12
    //   36: iflt +746 -> 782
    //   39: aload_2
    //   40: iload 12
    //   42: invokeinterface 534 2 0
    //   47: checkcast 536	com/android/server/am/ActivityRecord
    //   50: astore 27
    //   52: aload 8
    //   54: ifnull +40 -> 94
    //   57: aload 11
    //   59: astore 22
    //   61: aload 18
    //   63: astore 23
    //   65: aload 20
    //   67: astore 24
    //   69: iload 9
    //   71: istore 14
    //   73: aload 19
    //   75: astore 25
    //   77: aload 21
    //   79: astore 26
    //   81: aload 8
    //   83: aload 27
    //   85: getfield 539	com/android/server/am/ActivityRecord:packageName	Ljava/lang/String;
    //   88: invokevirtual 545	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   91: ifeq +460 -> 551
    //   94: aload 18
    //   96: astore 10
    //   98: aload 18
    //   100: ifnonnull +31 -> 131
    //   103: new 264	java/lang/StringBuilder
    //   106: dup
    //   107: invokespecial 267	java/lang/StringBuilder:<init>	()V
    //   110: aload_3
    //   111: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: ldc_w 547
    //   117: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual 279	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: astore 10
    //   125: iconst_0
    //   126: anewarray 541	java/lang/String
    //   129: astore 11
    //   131: iconst_1
    //   132: istore 17
    //   134: iload 6
    //   136: ifne +16 -> 152
    //   139: iload 5
    //   141: ifne +443 -> 584
    //   144: aload 27
    //   146: invokevirtual 550	com/android/server/am/ActivityRecord:isInHistory	()Z
    //   149: ifeq +435 -> 584
    //   152: iconst_0
    //   153: istore 13
    //   155: iload 9
    //   157: istore 15
    //   159: iload 9
    //   161: ifeq +13 -> 174
    //   164: aload_1
    //   165: ldc_w 552
    //   168: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   171: iconst_0
    //   172: istore 15
    //   174: aload 19
    //   176: astore 18
    //   178: aload 19
    //   180: ifnull +12 -> 192
    //   183: aload_1
    //   184: aload 19
    //   186: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   189: aconst_null
    //   190: astore 18
    //   192: aload 21
    //   194: astore 19
    //   196: aload 21
    //   198: ifnull +12 -> 210
    //   201: aload_1
    //   202: aload 21
    //   204: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   207: aconst_null
    //   208: astore 19
    //   210: aload 20
    //   212: astore 21
    //   214: aload 20
    //   216: aload 27
    //   218: getfield 562	com/android/server/am/ActivityRecord:task	Lcom/android/server/am/TaskRecord;
    //   221: if_acmpeq +72 -> 293
    //   224: aload 27
    //   226: getfield 562	com/android/server/am/ActivityRecord:task	Lcom/android/server/am/TaskRecord;
    //   229: astore 22
    //   231: aload_1
    //   232: aload_3
    //   233: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   236: iload 13
    //   238: ifeq +352 -> 590
    //   241: ldc_w 567
    //   244: astore 20
    //   246: aload_1
    //   247: aload 20
    //   249: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   252: aload_1
    //   253: aload 22
    //   255: invokevirtual 570	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   258: iload 13
    //   260: ifeq +338 -> 598
    //   263: aload 22
    //   265: aload_1
    //   266: new 264	java/lang/StringBuilder
    //   269: dup
    //   270: invokespecial 267	java/lang/StringBuilder:<init>	()V
    //   273: aload_3
    //   274: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: ldc_w 572
    //   280: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   283: invokevirtual 279	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   286: invokevirtual 578	com/android/server/am/TaskRecord:dump	(Ljava/io/PrintWriter;Ljava/lang/String;)V
    //   289: aload 22
    //   291: astore 21
    //   293: aload_1
    //   294: aload_3
    //   295: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   298: iload 13
    //   300: ifeq +350 -> 650
    //   303: ldc_w 580
    //   306: astore 20
    //   308: aload_1
    //   309: aload 20
    //   311: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   314: aload_1
    //   315: aload 4
    //   317: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   320: aload_1
    //   321: ldc_w 582
    //   324: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   327: aload_1
    //   328: iload 12
    //   330: invokevirtual 584	java/io/PrintWriter:print	(I)V
    //   333: aload_1
    //   334: ldc_w 586
    //   337: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   340: aload_1
    //   341: aload 27
    //   343: invokevirtual 570	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   346: iload 13
    //   348: ifeq +310 -> 658
    //   351: aload 27
    //   353: aload_1
    //   354: aload 10
    //   356: invokevirtual 587	com/android/server/am/ActivityRecord:dump	(Ljava/io/PrintWriter;Ljava/lang/String;)V
    //   359: aload 11
    //   361: astore 22
    //   363: aload 10
    //   365: astore 23
    //   367: aload 21
    //   369: astore 24
    //   371: iload 17
    //   373: istore 16
    //   375: iload 15
    //   377: istore 14
    //   379: aload 18
    //   381: astore 25
    //   383: aload 19
    //   385: astore 26
    //   387: iload 7
    //   389: ifeq +162 -> 551
    //   392: aload 11
    //   394: astore 22
    //   396: aload 10
    //   398: astore 23
    //   400: aload 21
    //   402: astore 24
    //   404: iload 17
    //   406: istore 16
    //   408: iload 15
    //   410: istore 14
    //   412: aload 18
    //   414: astore 25
    //   416: aload 19
    //   418: astore 26
    //   420: aload 27
    //   422: getfield 591	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   425: ifnull +126 -> 551
    //   428: aload 11
    //   430: astore 22
    //   432: aload 10
    //   434: astore 23
    //   436: aload 21
    //   438: astore 24
    //   440: iload 17
    //   442: istore 16
    //   444: iload 15
    //   446: istore 14
    //   448: aload 18
    //   450: astore 25
    //   452: aload 19
    //   454: astore 26
    //   456: aload 27
    //   458: getfield 591	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   461: getfield 597	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   464: ifnull +87 -> 551
    //   467: aload_1
    //   468: invokevirtual 600	java/io/PrintWriter:flush	()V
    //   471: new 602	com/android/internal/os/TransferPipe
    //   474: dup
    //   475: invokespecial 603	com/android/internal/os/TransferPipe:<init>	()V
    //   478: astore 20
    //   480: aload 27
    //   482: getfield 591	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   485: getfield 597	com/android/server/am/ProcessRecord:thread	Landroid/app/IApplicationThread;
    //   488: aload 20
    //   490: invokevirtual 607	com/android/internal/os/TransferPipe:getWriteFd	()Landroid/os/ParcelFileDescriptor;
    //   493: invokevirtual 613	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   496: aload 27
    //   498: getfield 617	com/android/server/am/ActivityRecord:appToken	Landroid/view/IApplicationToken$Stub;
    //   501: aload 10
    //   503: aload 11
    //   505: invokeinterface 623 5 0
    //   510: aload 20
    //   512: aload_0
    //   513: ldc2_w 624
    //   516: invokevirtual 629	com/android/internal/os/TransferPipe:go	(Ljava/io/FileDescriptor;J)V
    //   519: aload 20
    //   521: invokevirtual 632	com/android/internal/os/TransferPipe:kill	()V
    //   524: iconst_1
    //   525: istore 14
    //   527: aload 19
    //   529: astore 26
    //   531: aload 18
    //   533: astore 25
    //   535: iload 17
    //   537: istore 16
    //   539: aload 21
    //   541: astore 24
    //   543: aload 10
    //   545: astore 23
    //   547: aload 11
    //   549: astore 22
    //   551: iload 12
    //   553: iconst_1
    //   554: isub
    //   555: istore 12
    //   557: aload 22
    //   559: astore 11
    //   561: aload 23
    //   563: astore 18
    //   565: aload 24
    //   567: astore 20
    //   569: iload 14
    //   571: istore 9
    //   573: aload 25
    //   575: astore 19
    //   577: aload 26
    //   579: astore 21
    //   581: goto -547 -> 34
    //   584: iconst_1
    //   585: istore 13
    //   587: goto -432 -> 155
    //   590: ldc_w 572
    //   593: astore 20
    //   595: goto -349 -> 246
    //   598: aload 22
    //   600: astore 21
    //   602: iload 5
    //   604: ifeq -311 -> 293
    //   607: aload 22
    //   609: astore 21
    //   611: aload 22
    //   613: getfield 636	com/android/server/am/TaskRecord:intent	Landroid/content/Intent;
    //   616: ifnull -323 -> 293
    //   619: aload_1
    //   620: aload_3
    //   621: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   624: aload_1
    //   625: ldc_w 572
    //   628: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   631: aload_1
    //   632: aload 22
    //   634: getfield 636	com/android/server/am/TaskRecord:intent	Landroid/content/Intent;
    //   637: invokevirtual 641	android/content/Intent:toInsecureStringWithClip	()Ljava/lang/String;
    //   640: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   643: aload 22
    //   645: astore 21
    //   647: goto -354 -> 293
    //   650: ldc_w 643
    //   653: astore 20
    //   655: goto -347 -> 308
    //   658: iload 5
    //   660: ifeq -301 -> 359
    //   663: aload_1
    //   664: aload 10
    //   666: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   669: aload_1
    //   670: aload 27
    //   672: getfield 644	com/android/server/am/ActivityRecord:intent	Landroid/content/Intent;
    //   675: invokevirtual 647	android/content/Intent:toInsecureString	()Ljava/lang/String;
    //   678: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   681: aload 27
    //   683: getfield 591	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   686: ifnull -327 -> 359
    //   689: aload_1
    //   690: aload 10
    //   692: invokevirtual 565	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   695: aload_1
    //   696: aload 27
    //   698: getfield 591	com/android/server/am/ActivityRecord:app	Lcom/android/server/am/ProcessRecord;
    //   701: invokevirtual 570	java/io/PrintWriter:println	(Ljava/lang/Object;)V
    //   704: goto -345 -> 359
    //   707: astore 22
    //   709: aload 20
    //   711: invokevirtual 632	com/android/internal/os/TransferPipe:kill	()V
    //   714: aload 22
    //   716: athrow
    //   717: astore 20
    //   719: aload_1
    //   720: new 264	java/lang/StringBuilder
    //   723: dup
    //   724: invokespecial 267	java/lang/StringBuilder:<init>	()V
    //   727: aload 10
    //   729: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   732: ldc_w 649
    //   735: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   738: aload 20
    //   740: invokevirtual 652	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   743: invokevirtual 279	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   746: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   749: goto -225 -> 524
    //   752: astore 20
    //   754: aload_1
    //   755: new 264	java/lang/StringBuilder
    //   758: dup
    //   759: invokespecial 267	java/lang/StringBuilder:<init>	()V
    //   762: aload 10
    //   764: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   767: ldc_w 654
    //   770: invokevirtual 271	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   773: invokevirtual 279	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   776: invokevirtual 558	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   779: goto -255 -> 524
    //   782: iload 16
    //   784: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	785	0	paramFileDescriptor	FileDescriptor
    //   0	785	1	paramPrintWriter	PrintWriter
    //   0	785	2	paramList	List<ActivityRecord>
    //   0	785	3	paramString1	String
    //   0	785	4	paramString2	String
    //   0	785	5	paramBoolean1	boolean
    //   0	785	6	paramBoolean2	boolean
    //   0	785	7	paramBoolean3	boolean
    //   0	785	8	paramString3	String
    //   0	785	9	paramBoolean4	boolean
    //   0	785	10	paramString4	String
    //   0	785	11	paramString5	String
    //   20	536	12	i	int
    //   153	433	13	j	int
    //   71	499	14	bool1	boolean
    //   157	288	15	bool2	boolean
    //   10	773	16	bool3	boolean
    //   132	404	17	bool4	boolean
    //   4	560	18	localObject1	Object
    //   28	548	19	localObject2	Object
    //   1	709	20	localObject3	Object
    //   717	22	20	localIOException	IOException
    //   752	1	20	localRemoteException	RemoteException
    //   24	622	21	localObject4	Object
    //   7	637	22	localObject5	Object
    //   707	8	22	localObject6	Object
    //   63	499	23	localObject7	Object
    //   67	499	24	localObject8	Object
    //   75	499	25	localObject9	Object
    //   79	499	26	localObject10	Object
    //   50	647	27	localActivityRecord	ActivityRecord
    // Exception table:
    //   from	to	target	type
    //   480	519	707	finally
    //   471	480	717	java/io/IOException
    //   519	524	717	java/io/IOException
    //   709	717	717	java/io/IOException
    //   471	480	752	android/os/RemoteException
    //   519	524	752	android/os/RemoteException
    //   709	717	752	android/os/RemoteException
  }
  
  private static void fitWithinBounds(Rect paramRect1, Rect paramRect2)
  {
    if ((paramRect2 == null) || (paramRect2.contains(paramRect1))) {
      return;
    }
    int k;
    int j;
    int i;
    if ((paramRect1.left < paramRect2.left) || (paramRect1.right > paramRect2.right))
    {
      k = paramRect2.right - paramRect2.width() / 3;
      j = paramRect2.left - paramRect1.left;
      if ((j < 0) && (paramRect1.left >= k))
      {
        i = k - paramRect1.left;
        label79:
        paramRect1.left += i;
        paramRect1.right += i;
      }
    }
    else if ((paramRect1.top < paramRect2.top) || (paramRect1.bottom > paramRect2.bottom))
    {
      k = paramRect2.bottom - paramRect2.height() / 3;
      j = paramRect2.top - paramRect1.top;
      if ((j >= 0) || (paramRect1.top < k)) {
        break label202;
      }
    }
    for (;;)
    {
      i = k - paramRect1.top;
      label202:
      do
      {
        paramRect1.top += i;
        paramRect1.bottom += i;
        return;
        i = j;
        if (paramRect1.left + j < k) {
          break label79;
        }
        break;
        i = j;
      } while (paramRect1.top + j < k);
    }
  }
  
  private int getActionRestrictionForCallingPackage(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    if (paramString1 == null) {
      return 0;
    }
    paramString1 = (String)ACTION_TO_RUNTIME_PERMISSION.get(paramString1);
    if (paramString1 == null) {
      return 0;
    }
    try
    {
      PackageInfo localPackageInfo = this.mService.mContext.getPackageManager().getPackageInfo(paramString2, 4096);
      if (!ArrayUtils.contains(localPackageInfo.requestedPermissions, paramString1)) {
        return 0;
      }
    }
    catch (PackageManager.NameNotFoundException paramString1)
    {
      Slog.i(TAG, "Cannot find package info for " + paramString2);
      return 0;
    }
    if (this.mService.checkPermission(paramString1, paramInt1, paramInt2) == -1) {
      return 1;
    }
    paramInt1 = AppOpsManager.permissionToOpCode(paramString1);
    if (paramInt1 == -1) {
      return 0;
    }
    if (this.mService.mAppOpsService.noteOperation(paramInt1, paramInt2, paramString2) != 0) {
      return 2;
    }
    return 0;
  }
  
  private int getComponentRestrictionForCallingPackage(ActivityInfo paramActivityInfo, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((!paramBoolean) && (this.mService.checkComponentPermission(paramActivityInfo.permission, paramInt1, paramInt2, paramActivityInfo.applicationInfo.uid, paramActivityInfo.exported) == -1)) {
      return 1;
    }
    if (paramActivityInfo.permission == null) {
      return 0;
    }
    paramInt1 = AppOpsManager.permissionToOpCode(paramActivityInfo.permission);
    if (paramInt1 == -1) {
      return 0;
    }
    if ((this.mService.mAppOpsService.noteOperation(paramInt1, paramInt2, paramString) != 0) && (!paramBoolean)) {
      return 2;
    }
    return 0;
  }
  
  private IDevicePolicyManager getDevicePolicyManager()
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (this.mDevicePolicyManager == null)
      {
        this.mDevicePolicyManager = IDevicePolicyManager.Stub.asInterface(ServiceManager.checkService("device_policy"));
        if (this.mDevicePolicyManager == null) {
          Slog.w(TAG, "warning: no DEVICE_POLICY_SERVICE");
        }
      }
      IDevicePolicyManager localIDevicePolicyManager = this.mDevicePolicyManager;
      ActivityManagerService.resetPriorityAfterLockedSection();
      return localIDevicePolicyManager;
    }
  }
  
  private ActivityManager.StackInfo getStackInfoLocked(ActivityStack paramActivityStack)
  {
    Object localObject = (ActivityDisplay)this.mActivityDisplays.get(0);
    ActivityManager.StackInfo localStackInfo = new ActivityManager.StackInfo();
    this.mWindowManager.getStackBounds(paramActivityStack.mStackId, localStackInfo.bounds);
    localStackInfo.displayId = 0;
    localStackInfo.stackId = paramActivityStack.mStackId;
    localStackInfo.userId = paramActivityStack.mCurrentUser;
    boolean bool;
    int i;
    label96:
    int[] arrayOfInt1;
    String[] arrayOfString;
    Rect[] arrayOfRect;
    int[] arrayOfInt2;
    label138:
    TaskRecord localTaskRecord;
    if (paramActivityStack.getStackVisibilityLocked(null) == 1)
    {
      bool = true;
      localStackInfo.visible = bool;
      if (localObject == null) {
        break label236;
      }
      i = ((ActivityDisplay)localObject).mStacks.indexOf(paramActivityStack);
      localStackInfo.position = i;
      ArrayList localArrayList = paramActivityStack.getAllTasks();
      int j = localArrayList.size();
      arrayOfInt1 = new int[j];
      arrayOfString = new String[j];
      arrayOfRect = new Rect[j];
      arrayOfInt2 = new int[j];
      i = 0;
      if (i >= j) {
        break label291;
      }
      localTaskRecord = (TaskRecord)localArrayList.get(i);
      arrayOfInt1[i] = localTaskRecord.taskId;
      if (localTaskRecord.origActivity == null) {
        break label241;
      }
      localObject = localTaskRecord.origActivity.flattenToString();
    }
    for (;;)
    {
      arrayOfString[i] = localObject;
      arrayOfRect[i] = new Rect();
      this.mWindowManager.getTaskBounds(localTaskRecord.taskId, arrayOfRect[i]);
      arrayOfInt2[i] = localTaskRecord.userId;
      i += 1;
      break label138;
      bool = false;
      break;
      label236:
      i = 0;
      break label96;
      label241:
      if (localTaskRecord.realActivity != null) {
        localObject = localTaskRecord.realActivity.flattenToString();
      } else if (localTaskRecord.getTopActivity() != null) {
        localObject = localTaskRecord.getTopActivity().packageName;
      } else {
        localObject = "unknown";
      }
    }
    label291:
    localStackInfo.taskIds = arrayOfInt1;
    localStackInfo.taskNames = arrayOfString;
    localStackInfo.taskBounds = arrayOfRect;
    localStackInfo.taskUserIds = arrayOfInt2;
    paramActivityStack = paramActivityStack.topRunningActivityLocked();
    if (paramActivityStack != null) {}
    for (paramActivityStack = paramActivityStack.intent.getComponent();; paramActivityStack = null)
    {
      localStackInfo.topActivity = paramActivityStack;
      return localStackInfo;
    }
  }
  
  private IStatusBarService getStatusBarService()
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (this.mStatusBarService == null)
      {
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
        if (this.mStatusBarService == null) {
          Slog.w("StatusBarManager", "warning: no STATUS_BAR_SERVICE");
        }
      }
      IStatusBarService localIStatusBarService = this.mStatusBarService;
      ActivityManagerService.resetPriorityAfterLockedSection();
      return localIStatusBarService;
    }
  }
  
  private void handleDisplayAdded(int paramInt)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      int i;
      if (this.mActivityDisplays.get(paramInt) == null) {
        i = 1;
      }
      while (i != 0)
      {
        ActivityDisplay localActivityDisplay = new ActivityDisplay(paramInt);
        if (localActivityDisplay.mDisplay == null)
        {
          Slog.w(TAG, "Display " + paramInt + " gone before initialization complete");
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
          i = 0;
        }
        else
        {
          this.mActivityDisplays.put(paramInt, localActivityDisplay);
          calculateDefaultMinimalSizeOfResizeableTasks(localActivityDisplay);
        }
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (i != 0) {
        this.mWindowManager.onDisplayAdded(paramInt);
      }
      return;
    }
  }
  
  private void handleDisplayChanged(int paramInt)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      ActivityDisplay localActivityDisplay = (ActivityDisplay)this.mActivityDisplays.get(paramInt);
      if (localActivityDisplay != null) {}
      ActivityManagerService.resetPriorityAfterLockedSection();
      this.mWindowManager.onDisplayChanged(paramInt);
      return;
    }
  }
  
  private void handleDisplayRemoved(int paramInt)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject1 = (ActivityDisplay)this.mActivityDisplays.get(paramInt);
      if (localObject1 != null)
      {
        localObject1 = ((ActivityDisplay)localObject1).mStacks;
        int i = ((ArrayList)localObject1).size() - 1;
        while (i >= 0)
        {
          ((ActivityStack)((ArrayList)localObject1).get(i)).mActivityContainer.detachLocked();
          i -= 1;
        }
        this.mActivityDisplays.remove(paramInt);
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      this.mWindowManager.onDisplayRemoved(paramInt);
      return;
    }
  }
  
  private String lockTaskModeToString()
  {
    switch (this.mLockTaskModeState)
    {
    default: 
      return "unknown=" + this.mLockTaskModeState;
    case 1: 
      return "LOCKED";
    case 2: 
      return "PINNED";
    }
    return "NONE";
  }
  
  static int nextTaskIdForUser(int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 1;
    paramInt1 = i;
    if (i == (paramInt2 + 1) * 100000) {
      paramInt1 = i - 100000;
    }
    return paramInt1;
  }
  
  static boolean printThisActivity(PrintWriter paramPrintWriter, ActivityRecord paramActivityRecord, String paramString1, boolean paramBoolean, String paramString2)
  {
    if ((paramActivityRecord != null) && ((paramString1 == null) || (paramString1.equals(paramActivityRecord.packageName))))
    {
      if (paramBoolean) {
        paramPrintWriter.println();
      }
      paramPrintWriter.print(paramString2);
      paramPrintWriter.println(paramActivityRecord);
      return true;
    }
    return false;
  }
  
  private boolean restoreRecentTaskLocked(TaskRecord paramTaskRecord, int paramInt)
  {
    int i;
    if (paramInt == -1) {
      i = paramTaskRecord.getLaunchStackId();
    }
    while (paramTaskRecord.stack != null) {
      if (paramTaskRecord.stack.mStackId == i)
      {
        return true;
        if ((paramInt != 3) || (paramTaskRecord.canGoInDockedStack()))
        {
          i = paramInt;
          if (paramInt == 2)
          {
            i = paramInt;
            if (this.mService.mUserController.shouldConfirmCredentials(paramTaskRecord.userId)) {
              i = 1;
            }
          }
        }
        else
        {
          i = 1;
        }
      }
      else
      {
        paramTaskRecord.stack.removeTask(paramTaskRecord, "restoreRecentTaskLocked", 1);
      }
    }
    ActivityStack localActivityStack = getStack(i, true, false);
    if (localActivityStack == null)
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.v(TAG_RECENTS, "Unable to find/create stack to restore recent task=" + paramTaskRecord);
      }
      return false;
    }
    localActivityStack.addTask(paramTaskRecord, false, "restoreRecentTask");
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.v(TAG_RECENTS, "Added restored task=" + paramTaskRecord + " to stack=" + localActivityStack);
    }
    ArrayList localArrayList = paramTaskRecord.mActivities;
    paramInt = localArrayList.size() - 1;
    while (paramInt >= 0)
    {
      localActivityStack.addConfigOverride((ActivityRecord)localArrayList.get(paramInt), paramTaskRecord);
      paramInt -= 1;
    }
    return true;
  }
  
  private void setResizingDuringAnimation(int paramInt)
  {
    this.mResizingTasksDuringAnimation.add(Integer.valueOf(paramInt));
    this.mWindowManager.setTaskDockedResizing(paramInt, true);
  }
  
  private boolean taskContainsActivityFromUser(TaskRecord paramTaskRecord, int paramInt)
  {
    int i = paramTaskRecord.mActivities.size() - 1;
    while (i >= 0)
    {
      if (((ActivityRecord)paramTaskRecord.mActivities.get(i)).userId == paramInt) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  void acquireAppLaunchPerfLock(String paramString)
  {
    if ((this.mIsperfDisablepackingEnable) && (this.mPerfPack == null)) {
      this.mPerfPack = new BoostFramework();
    }
    if (this.mPerfPack != null) {
      this.mPerfPack.perfLockAcquire(this.lDisPackTimeOut, this.lBoostPackParamVal);
    }
    if ((this.mIsPerfBoostEnabled) && (this.mPerfBoost == null)) {
      this.mPerfBoost = new BoostFramework();
    }
    if (this.mPerfBoost != null) {
      this.mPerfBoost.perfLockAcquire(this.lBoostTimeOut, this.lBoostCpuParamVal);
    }
    if (this.mPerfIop == null) {
      this.mPerfIop = new BoostFramework();
    }
    if (this.mPerfIop != null) {
      this.mPerfIop.perfIOPrefetchStart(-1, paramString);
    }
  }
  
  void acquireAppLaunchPerfLock(String paramString, int paramInt)
  {
    if ((this.mIsperfDisablepackingEnable) && (this.mPerfPack == null)) {
      this.mPerfPack = new BoostFramework();
    }
    if (this.mPerfPack != null) {
      this.mPerfPack.perfLockAcquire(this.lDisPackTimeOut, this.lBoostPackParamVal);
    }
    if ((this.mIsPerfBoostEnabled) && (this.mPerfBoost == null)) {
      this.mPerfBoost = new BoostFramework();
    }
    if (this.mPerfBoost != null) {
      this.mPerfBoost.perfLockAcquire(paramInt, this.lBoostCpuParamVal);
    }
    if (this.mPerfIop == null) {
      this.mPerfIop = new BoostFramework();
    }
    if (this.mPerfIop != null) {
      this.mPerfIop.perfIOPrefetchStart(-1, paramString);
    }
  }
  
  void acquireLaunchWakelock()
  {
    this.mLaunchingActivity.acquire();
    if (!this.mHandler.hasMessages(104)) {
      this.mHandler.sendEmptyMessageDelayed(104, 10000L);
    }
  }
  
  final ActivityRecord activityIdleInternalLocked(IBinder paramIBinder, boolean paramBoolean, Configuration paramConfiguration)
  {
    if (ActivityManagerDebugConfig.DEBUG_ALL) {
      Slog.v(TAG, "Activity idle: " + paramIBinder);
    }
    ActivityStack localActivityStack = null;
    ArrayList localArrayList1 = null;
    boolean bool2 = false;
    int k = 0;
    paramIBinder = ActivityRecord.forTokenLocked(paramIBinder);
    boolean bool1 = bool2;
    if (paramIBinder != null)
    {
      if (ActivityManagerDebugConfig.DEBUG_IDLE) {
        Slog.d(TAG_IDLE, "activityIdleInternalLocked: Callers=" + Debug.getCallers(4));
      }
      this.mHandler.removeMessages(100, paramIBinder);
      paramIBinder.finishLaunchTickingLocked();
      if (paramBoolean) {
        reportActivityLaunchedLocked(paramBoolean, paramIBinder, -1L, -1L);
      }
      if (paramConfiguration != null) {
        paramIBinder.configuration = paramConfiguration;
      }
      paramIBinder.idle = true;
      if (!isFocusedStack(paramIBinder.task.stack))
      {
        bool1 = bool2;
        if (!paramBoolean) {}
      }
      else
      {
        bool1 = checkFinishBootingLocked();
      }
    }
    if (allResumedActivitiesIdle())
    {
      if (paramIBinder != null) {
        this.mService.scheduleAppGcsLocked();
      }
      if (this.mLaunchingActivity.isHeld())
      {
        this.mHandler.removeMessages(104);
        this.mLaunchingActivity.release();
      }
      ensureActivitiesVisibleLocked(null, 0, false);
    }
    ArrayList localArrayList2 = processStoppingActivitiesLocked(true);
    int m;
    if (localArrayList2 != null)
    {
      i = localArrayList2.size();
      m = this.mFinishingActivities.size();
      paramConfiguration = localActivityStack;
      if (m > 0)
      {
        paramConfiguration = new ArrayList(this.mFinishingActivities);
        this.mFinishingActivities.clear();
      }
      if (this.mStartingUsers.size() > 0)
      {
        localArrayList1 = new ArrayList(this.mStartingUsers);
        this.mStartingUsers.clear();
      }
      j = 0;
      label305:
      if (j >= i) {
        break label377;
      }
      paramIBinder = (ActivityRecord)localArrayList2.get(j);
      localActivityStack = paramIBinder.task.stack;
      if (localActivityStack != null)
      {
        if (!paramIBinder.finishing) {
          break label368;
        }
        localActivityStack.finishCurrentActivityLocked(paramIBinder, 0, false);
      }
    }
    for (;;)
    {
      j += 1;
      break label305;
      i = 0;
      break;
      label368:
      localActivityStack.stopActivityLocked(paramIBinder);
    }
    label377:
    int j = 0;
    for (int i = k; j < m; i = k)
    {
      paramIBinder = (ActivityRecord)paramConfiguration.get(j);
      localActivityStack = paramIBinder.task.stack;
      k = i;
      if (localActivityStack != null) {
        k = i | localActivityStack.destroyActivityLocked(paramIBinder, true, "finish-idle");
      }
      j += 1;
    }
    if ((!bool1) && (localArrayList1 != null))
    {
      j = 0;
      while (j < localArrayList1.size())
      {
        this.mService.mUserController.finishUserSwitch((UserState)localArrayList1.get(j));
        j += 1;
      }
    }
    this.mService.trimApplications();
    if (i != 0) {
      resumeFocusedStackTopActivityLocked();
    }
    return paramIBinder;
  }
  
  void activityRelaunchedLocked(IBinder paramIBinder)
  {
    this.mWindowManager.notifyAppRelaunchingFinished(paramIBinder);
    if (this.mService.isSleepingOrShuttingDownLocked())
    {
      paramIBinder = ActivityRecord.isInStackLocked(paramIBinder);
      if (paramIBinder != null) {
        paramIBinder.setSleeping(true, true);
      }
    }
  }
  
  void activityRelaunchingLocked(ActivityRecord paramActivityRecord)
  {
    this.mWindowManager.notifyAppRelaunching(paramActivityRecord.appToken);
  }
  
  void activitySleptLocked(ActivityRecord paramActivityRecord)
  {
    this.mGoingToSleepActivities.remove(paramActivityRecord);
    checkReadyForSleepLocked();
  }
  
  boolean allPausedActivitiesComplete()
  {
    boolean bool1 = true;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityRecord localActivityRecord = ((ActivityStack)localArrayList.get(j)).mPausingActivity;
        boolean bool2 = bool1;
        if (localActivityRecord != null)
        {
          bool2 = bool1;
          if (localActivityRecord.state != ActivityStack.ActivityState.PAUSED)
          {
            bool2 = bool1;
            if (localActivityRecord.state != ActivityStack.ActivityState.STOPPED)
            {
              bool2 = bool1;
              if (localActivityRecord.state != ActivityStack.ActivityState.STOPPING)
              {
                if (!ActivityManagerDebugConfig.DEBUG_STATES) {
                  break label169;
                }
                Slog.d(TAG_STATES, "allPausedActivitiesComplete: r=" + localActivityRecord + " state=" + localActivityRecord.state);
                bool2 = false;
              }
            }
          }
        }
        j -= 1;
        bool1 = bool2;
        continue;
        label169:
        return false;
      }
      i -= 1;
    }
    return bool1;
  }
  
  boolean allResumedActivitiesComplete()
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        Object localObject = (ActivityStack)localArrayList.get(j);
        if (isFocusedStack((ActivityStack)localObject))
        {
          localObject = ((ActivityStack)localObject).mResumedActivity;
          if ((localObject != null) && (((ActivityRecord)localObject).state != ActivityStack.ActivityState.RESUMED)) {
            return false;
          }
        }
        j -= 1;
      }
      i -= 1;
    }
    if (ActivityManagerDebugConfig.DEBUG_STACK) {
      Slog.d(TAG_STACK, "allResumedActivitiesComplete: mLastFocusedStack changing from=" + this.mLastFocusedStack + " to=" + this.mFocusedStack);
    }
    this.mLastFocusedStack = this.mFocusedStack;
    return true;
  }
  
  boolean allResumedActivitiesIdle()
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      if (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)localArrayList.get(j);
        if ((!isFocusedStack(localActivityStack)) || (localActivityStack.numActivities() == 0)) {}
        ActivityRecord localActivityRecord;
        do
        {
          j -= 1;
          break;
          localActivityRecord = localActivityStack.mResumedActivity;
        } while ((localActivityRecord != null) && (localActivityRecord.idle));
        if (ActivityManagerDebugConfig.DEBUG_STATES) {
          Slog.d(TAG_STATES, "allResumedActivitiesIdle: stack=" + localActivityStack.mStackId + " " + localActivityRecord + " not idle");
        }
        return false;
      }
      i -= 1;
    }
    this.mService.mActivityStarter.sendPowerHintForLaunchEndIfNeeded();
    return true;
  }
  
  boolean allResumedActivitiesVisible()
  {
    boolean bool = false;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityRecord localActivityRecord = ((ActivityStack)localArrayList.get(j)).mResumedActivity;
        if (localActivityRecord != null)
        {
          if ((!localActivityRecord.nowVisible) || (this.mWaitingVisibleActivities.contains(localActivityRecord))) {
            return false;
          }
          bool = true;
        }
        j -= 1;
      }
      i -= 1;
    }
    return bool;
  }
  
  TaskRecord anyTaskForIdLocked(int paramInt)
  {
    return anyTaskForIdLocked(paramInt, true, -1);
  }
  
  TaskRecord anyTaskForIdLocked(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    int k = this.mActivityDisplays.size();
    int i = 0;
    while (i < k)
    {
      localObject = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = ((ArrayList)localObject).size() - 1;
      while (j >= 0)
      {
        TaskRecord localTaskRecord = ((ActivityStack)((ArrayList)localObject).get(j)).taskForIdLocked(paramInt1);
        if (localTaskRecord != null) {
          return localTaskRecord;
        }
        j -= 1;
      }
      i += 1;
    }
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.v(TAG_RECENTS, "Looking for task id=" + paramInt1 + " in recents");
    }
    Object localObject = this.mRecentTasks.taskForIdLocked(paramInt1);
    if (localObject == null)
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG_RECENTS, "\tDidn't find task id=" + paramInt1 + " in recents");
      }
      return null;
    }
    if (!paramBoolean) {
      return (TaskRecord)localObject;
    }
    if (!restoreRecentTaskLocked((TaskRecord)localObject, paramInt2))
    {
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.w(TAG_RECENTS, "Couldn't restore task id=" + paramInt1 + " found in recents");
      }
      return null;
    }
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.w(TAG_RECENTS, "Restored task id=" + paramInt1 + " from in recents");
    }
    return (TaskRecord)localObject;
  }
  
  boolean attachApplicationLocked(ProcessRecord paramProcessRecord)
    throws RemoteException
  {
    String str = paramProcessRecord.processName;
    boolean bool1 = false;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      if (j >= 0)
      {
        Object localObject = (ActivityStack)localArrayList.get(j);
        boolean bool2;
        if (!isFocusedStack((ActivityStack)localObject)) {
          bool2 = bool1;
        }
        for (;;)
        {
          j -= 1;
          bool1 = bool2;
          break;
          localObject = ((ActivityStack)localObject).topRunningActivityLocked();
          bool2 = bool1;
          if (localObject != null)
          {
            bool2 = bool1;
            if (((ActivityRecord)localObject).app == null)
            {
              bool2 = bool1;
              if (paramProcessRecord.uid == ((ActivityRecord)localObject).info.applicationInfo.uid)
              {
                bool2 = bool1;
                if (str.equals(((ActivityRecord)localObject).processName)) {
                  try
                  {
                    boolean bool3 = realStartActivityLocked((ActivityRecord)localObject, paramProcessRecord, true, true);
                    bool2 = bool1;
                    if (bool3) {
                      bool2 = true;
                    }
                  }
                  catch (RemoteException paramProcessRecord)
                  {
                    Slog.w(TAG, "Exception in new application when starting activity " + ((ActivityRecord)localObject).intent.getComponent().flattenToShortString(), paramProcessRecord);
                    throw paramProcessRecord;
                  }
                }
              }
            }
          }
        }
      }
      i -= 1;
    }
    if (!bool1) {
      ensureActivitiesVisibleLocked(null, 0, false);
    }
    return bool1;
  }
  
  boolean canUseActivityOptionsLaunchBounds(ActivityOptions paramActivityOptions, int paramInt)
  {
    if (paramActivityOptions.getLaunchBounds() == null) {
      return false;
    }
    if ((this.mService.mSupportsPictureInPicture) && (paramInt == 4)) {
      return true;
    }
    return this.mService.mSupportsFreeformWindowManagement;
  }
  
  void cancelInitializingActivities()
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).cancelInitializingActivities();
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void checkReadyForSleepLocked()
  {
    if (!this.mService.isSleepingOrShuttingDownLocked()) {
      return;
    }
    int j;
    ArrayList localArrayList;
    if (!this.mSleepTimeout)
    {
      boolean bool = false;
      j = this.mActivityDisplays.size() - 1;
      while (j >= 0)
      {
        localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(j)).mStacks;
        int k = localArrayList.size() - 1;
        while (k >= 0)
        {
          bool |= ((ActivityStack)localArrayList.get(k)).checkReadyForSleepLocked();
          k -= 1;
        }
        j -= 1;
      }
      if (this.mStoppingActivities.size() > 0)
      {
        if (ActivityManagerDebugConfig.DEBUG_PAUSE) {
          Slog.v(TAG_PAUSE, "Sleep still need to stop " + this.mStoppingActivities.size() + " activities");
        }
        scheduleIdleLocked();
        bool = true;
      }
      if (this.mGoingToSleepActivities.size() > 0)
      {
        if (ActivityManagerDebugConfig.DEBUG_PAUSE) {
          Slog.v(TAG_PAUSE, "Sleep still need to sleep " + this.mGoingToSleepActivities.size() + " activities");
        }
        bool = true;
      }
      if (bool) {
        return;
      }
    }
    this.mService.mActivityStarter.sendPowerHintForLaunchEndIfNeeded();
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).goToSleep();
        j -= 1;
      }
      i -= 1;
    }
    removeSleepTimeouts();
    if (this.mGoingToSleep.isHeld()) {
      this.mGoingToSleep.release();
    }
    if (this.mService.mShuttingDown) {
      this.mService.notifyAll();
    }
  }
  
  boolean checkStartAnyActivityPermission(Intent paramIntent, ActivityInfo paramActivityInfo, String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2, boolean paramBoolean, ProcessRecord paramProcessRecord, ActivityRecord paramActivityRecord, ActivityStack paramActivityStack, ActivityOptions paramActivityOptions)
  {
    if (this.mService.checkPermission("android.permission.START_ANY_ACTIVITY", paramInt2, paramInt3) == 0) {
      return true;
    }
    int i = getComponentRestrictionForCallingPackage(paramActivityInfo, paramString2, paramInt2, paramInt3, paramBoolean);
    int j = getActionRestrictionForCallingPackage(paramIntent.getAction(), paramString2, paramInt2, paramInt3);
    if ((i == 1) || (j == 1))
    {
      if (paramActivityRecord != null) {
        paramActivityStack.sendActivityResultLocked(-1, paramActivityRecord, paramString1, paramInt1, 0, null);
      }
      if (j == 1) {
        paramIntent = "Permission Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ")" + " with revoked permission " + (String)ACTION_TO_RUNTIME_PERMISSION.get(paramIntent.getAction());
      }
      for (;;)
      {
        Slog.w(TAG, paramIntent);
        throw new SecurityException(paramIntent);
        if (!paramActivityInfo.exported) {
          paramIntent = "Permission Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ")" + " not exported from uid " + paramActivityInfo.applicationInfo.uid;
        } else {
          paramIntent = "Permission Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ")" + " requires " + paramActivityInfo.permission;
        }
      }
    }
    if (j == 2)
    {
      paramIntent = "Appop Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ")" + " requires " + AppOpsManager.permissionToOp((String)ACTION_TO_RUNTIME_PERMISSION.get(paramIntent.getAction()));
      Slog.w(TAG, paramIntent);
      return false;
    }
    if (i == 2)
    {
      paramIntent = "Appop Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ")" + " requires appop " + AppOpsManager.permissionToOp(paramActivityInfo.permission);
      Slog.w(TAG, paramIntent);
      return false;
    }
    if ((paramActivityOptions != null) && (paramActivityOptions.getLaunchTaskId() != -1) && (this.mService.checkPermission("android.permission.START_TASKS_FROM_RECENTS", paramInt2, paramInt3) != 0))
    {
      paramIntent = "Permission Denial: starting " + paramIntent.toString() + " from " + paramProcessRecord + " (pid=" + paramInt2 + ", uid=" + paramInt3 + ") with launchTaskId=" + paramActivityOptions.getLaunchTaskId();
      Slog.w(TAG, paramIntent);
      throw new SecurityException(paramIntent);
    }
    return true;
  }
  
  void clearOtherAppTimeTrackers(AppTimeTracker paramAppTimeTracker)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).clearOtherAppTimeTrackers(paramAppTimeTracker);
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void closeSystemDialogsLocked()
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).closeSystemDialogsLocked();
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void comeOutOfSleepIfNeededLocked()
  {
    removeSleepTimeouts();
    if (this.mGoingToSleep.isHeld()) {
      this.mGoingToSleep.release();
    }
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)localArrayList.get(j);
        localActivityStack.awakeFromSleepingLocked();
        if (isFocusedStack(localActivityStack)) {
          resumeFocusedStackTopActivityLocked();
        }
        j -= 1;
      }
      i -= 1;
    }
    this.mGoingToSleepActivities.clear();
  }
  
  void continueUpdateBounds(int paramInt)
  {
    ActivityStack localActivityStack = getStack(paramInt);
    if (localActivityStack != null) {
      localActivityStack.continueUpdateBounds();
    }
  }
  
  ActivityStack createStackOnDisplay(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ActivityDisplay localActivityDisplay = (ActivityDisplay)this.mActivityDisplays.get(paramInt2);
    if (localActivityDisplay == null) {
      return null;
    }
    ActivityContainer localActivityContainer = new ActivityContainer(paramInt1);
    this.mActivityContainers.put(paramInt1, localActivityContainer);
    localActivityContainer.attachToDisplayLocked(localActivityDisplay, paramBoolean);
    return localActivityContainer.mStack;
  }
  
  ActivityContainer createVirtualActivityContainer(ActivityRecord paramActivityRecord, IActivityContainerCallback paramIActivityContainerCallback)
  {
    paramIActivityContainerCallback = new VirtualActivityContainer(paramActivityRecord, paramIActivityContainerCallback);
    this.mActivityContainers.put(paramIActivityContainerCallback.mStackId, paramIActivityContainerCallback);
    if (ActivityManagerDebugConfig.DEBUG_CONTAINERS) {
      Slog.d(TAG_CONTAINERS, "createActivityContainer: " + paramIActivityContainerCallback);
    }
    paramActivityRecord.mChildContainers.add(paramIActivityContainerCallback);
    return paramIActivityContainerCallback;
  }
  
  void deferUpdateBounds(int paramInt)
  {
    ActivityStack localActivityStack = getStack(paramInt);
    if (localActivityStack != null) {
      localActivityStack.deferUpdateBounds();
    }
  }
  
  void deleteActivityContainer(IActivityContainer paramIActivityContainer)
  {
    paramIActivityContainer = (ActivityContainer)paramIActivityContainer;
    if (paramIActivityContainer != null)
    {
      if (ActivityManagerDebugConfig.DEBUG_CONTAINERS) {
        Slog.d(TAG_CONTAINERS, "deleteActivityContainer: callers=" + Debug.getCallers(4));
      }
      int i = paramIActivityContainer.mStackId;
      this.mActivityContainers.remove(i);
      this.mWindowManager.removeStack(i);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFocusedStack=" + this.mFocusedStack);
    paramPrintWriter.print(" mLastFocusedStack=");
    paramPrintWriter.println(this.mLastFocusedStack);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mSleepTimeout=" + this.mSleepTimeout);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mCurTaskIdForUser=" + this.mCurTaskIdForUser);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mUserStackInFront=" + this.mUserStackInFront);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("mActivityContainers=" + this.mActivityContainers);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLockTaskModeState=" + lockTaskModeToString());
    SparseArray localSparseArray = this.mService.mLockTaskPackages;
    if (localSparseArray.size() > 0)
    {
      paramPrintWriter.println(" mLockTaskPackages (userId:packages)=");
      int i = 0;
      while (i < localSparseArray.size())
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print(localSparseArray.keyAt(i));
        paramPrintWriter.print(":");
        paramPrintWriter.println(Arrays.toString((Object[])localSparseArray.valueAt(i)));
        i += 1;
      }
    }
    paramPrintWriter.println(" mLockTaskModeTasks" + this.mLockTaskModeTasks);
  }
  
  boolean dumpActivitiesLocked(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    boolean bool3;
    while (i < this.mActivityDisplays.size())
    {
      localObject1 = (ActivityDisplay)this.mActivityDisplays.valueAt(i);
      paramPrintWriter.print("Display #");
      paramPrintWriter.print(((ActivityDisplay)localObject1).mDisplayId);
      paramPrintWriter.println(" (activities from top to bottom):");
      localObject1 = ((ActivityDisplay)localObject1).mStacks;
      int j = ((ArrayList)localObject1).size() - 1;
      bool3 = bool2;
      if (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)((ArrayList)localObject1).get(j);
        Object localObject2 = new StringBuilder(128);
        ((StringBuilder)localObject2).append("  Stack #");
        ((StringBuilder)localObject2).append(localActivityStack.mStackId);
        ((StringBuilder)localObject2).append(":");
        ((StringBuilder)localObject2).append("\n");
        ((StringBuilder)localObject2).append("  mFullscreen=").append(localActivityStack.mFullscreen);
        ((StringBuilder)localObject2).append("\n");
        ((StringBuilder)localObject2).append("  mBounds=").append(localActivityStack.mBounds);
        bool3 = localActivityStack.dumpActivitiesLocked(paramFileDescriptor, paramPrintWriter, paramBoolean1, paramBoolean2, paramString, bool3, ((StringBuilder)localObject2).toString());
        localObject2 = localActivityStack.mLRUActivities;
        if (paramBoolean1) {}
        for (bool2 = false;; bool2 = true)
        {
          bool3 = bool1 | bool3 | dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject2, "    ", "Run", false, bool2, false, paramString, true, "    Running activities (most recent first):", null);
          bool2 = bool3;
          bool1 = bool3;
          if (printThisActivity(paramPrintWriter, localActivityStack.mPausingActivity, paramString, bool3, "    mPausingActivity: "))
          {
            bool1 = true;
            bool2 = false;
          }
          bool3 = bool2;
          if (printThisActivity(paramPrintWriter, localActivityStack.mResumedActivity, paramString, bool2, "    mResumedActivity: "))
          {
            bool1 = true;
            bool3 = false;
          }
          bool2 = bool1;
          if (paramBoolean1)
          {
            bool2 = bool3;
            if (printThisActivity(paramPrintWriter, localActivityStack.mLastPausedActivity, paramString, bool3, "    mLastPausedActivity: "))
            {
              bool1 = true;
              bool2 = true;
            }
            bool2 = bool1 | printThisActivity(paramPrintWriter, localActivityStack.mLastNoHistoryActivity, paramString, bool2, "    mLastNoHistoryActivity: ");
          }
          bool3 = bool2;
          j -= 1;
          bool1 = bool2;
          break;
        }
      }
      i += 1;
      bool2 = bool3;
    }
    Object localObject1 = this.mFinishingActivities;
    label463:
    label502:
    boolean bool4;
    if (paramBoolean1)
    {
      paramBoolean2 = false;
      bool2 = dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject1, "  ", "Fin", false, paramBoolean2, false, paramString, true, "  Activities waiting to finish:", null);
      localObject1 = this.mStoppingActivities;
      if (!paramBoolean1) {
        break label624;
      }
      paramBoolean2 = false;
      bool3 = dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject1, "  ", "Stop", false, paramBoolean2, false, paramString, true, "  Activities waiting to stop:", null);
      localObject1 = this.mWaitingVisibleActivities;
      if (!paramBoolean1) {
        break label630;
      }
      paramBoolean2 = false;
      bool4 = dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject1, "  ", "Wait", false, paramBoolean2, false, paramString, true, "  Activities waiting for another to become visible:", null);
      localObject1 = this.mGoingToSleepActivities;
      if (!paramBoolean1) {
        break label636;
      }
      paramBoolean2 = false;
      label541:
      paramBoolean2 = dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject1, "  ", "Sleep", false, paramBoolean2, false, paramString, true, "  Activities waiting to sleep:", null);
      localObject1 = this.mGoingToSleepActivities;
      if (!paramBoolean1) {
        break label642;
      }
    }
    label624:
    label630:
    label636:
    label642:
    for (paramBoolean1 = false;; paramBoolean1 = true)
    {
      return bool1 | bool2 | bool3 | bool4 | paramBoolean2 | dumpHistoryList(paramFileDescriptor, paramPrintWriter, (List)localObject1, "  ", "Sleep", false, paramBoolean1, false, paramString, true, "  Activities waiting to sleep:", null);
      paramBoolean2 = true;
      break;
      paramBoolean2 = true;
      break label463;
      paramBoolean2 = true;
      break label502;
      paramBoolean2 = true;
      break label541;
    }
  }
  
  void ensureActivitiesVisibleLocked(ActivityRecord paramActivityRecord, int paramInt, boolean paramBoolean)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).ensureActivitiesVisibleLocked(paramActivityRecord, paramInt, paramBoolean);
        j -= 1;
      }
      i -= 1;
    }
  }
  
  ActivityRecord findActivityLocked(Intent paramIntent, ActivityInfo paramActivityInfo, boolean paramBoolean)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityRecord localActivityRecord = ((ActivityStack)localArrayList.get(j)).findActivityLocked(paramIntent, paramActivityInfo, paramBoolean);
        if (localActivityRecord != null) {
          return localActivityRecord;
        }
        j -= 1;
      }
      i -= 1;
    }
    return null;
  }
  
  ActivityStack findStackBehind(ActivityStack paramActivityStack)
  {
    Object localObject = (ActivityDisplay)this.mActivityDisplays.get(0);
    if (localObject == null) {
      return null;
    }
    localObject = ((ActivityDisplay)localObject).mStacks;
    int i = ((ArrayList)localObject).size() - 1;
    while (i >= 0)
    {
      if ((((ArrayList)localObject).get(i) == paramActivityStack) && (i > 0)) {
        return (ActivityStack)((ArrayList)localObject).get(i - 1);
      }
      i -= 1;
    }
    throw new IllegalStateException("Failed to find a stack behind stack=" + paramActivityStack + " in=" + localObject);
  }
  
  ActivityRecord findTaskLocked(ActivityRecord paramActivityRecord)
  {
    this.mTmpFindTaskResult.r = null;
    this.mTmpFindTaskResult.matchedByRootAffinity = false;
    if (ActivityManagerDebugConfig.DEBUG_TASKS) {
      Slog.d(TAG_TASKS, "Looking for task of " + paramActivityRecord);
    }
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      if (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)localArrayList.get(j);
        if ((paramActivityRecord.isApplicationActivity()) || (localActivityStack.isHomeStack()))
        {
          if (localActivityStack.mActivityContainer.isEligibleForNewTasks()) {
            break label206;
          }
          if (ActivityManagerDebugConfig.DEBUG_TASKS) {
            Slog.d(TAG_TASKS, "Skipping stack: (new task not allowed) " + localActivityStack);
          }
        }
        label206:
        do
        {
          for (;;)
          {
            j -= 1;
            break;
            if (ActivityManagerDebugConfig.DEBUG_TASKS) {
              Slog.d(TAG_TASKS, "Skipping stack: (home activity) " + localActivityStack);
            }
          }
          localActivityStack.findTaskLocked(paramActivityRecord, this.mTmpFindTaskResult);
        } while ((this.mTmpFindTaskResult.r == null) || (this.mTmpFindTaskResult.matchedByRootAffinity));
        i = 0;
        if (getFocusedStack() != localActivityStack)
        {
          acquireAppLaunchPerfLock(paramActivityRecord.packageName, 500);
          i = 1;
        }
        if ((this.mTmpFindTaskResult.r.state == ActivityStack.ActivityState.DESTROYED) && (i == 0)) {
          acquireAppLaunchPerfLock(paramActivityRecord.packageName);
        }
        return this.mTmpFindTaskResult.r;
      }
      i -= 1;
    }
    if ((this.mTmpFindTaskResult.r == null) || (this.mTmpFindTaskResult.r.state == ActivityStack.ActivityState.DESTROYED)) {
      acquireAppLaunchPerfLock(paramActivityRecord.packageName);
    }
    if ((ActivityManagerDebugConfig.DEBUG_TASKS) && (this.mTmpFindTaskResult.r == null)) {
      Slog.d(TAG_TASKS, "No task found");
    }
    return this.mTmpFindTaskResult.r;
  }
  
  void findTaskToMoveToFrontLocked(TaskRecord paramTaskRecord, int paramInt, ActivityOptions paramActivityOptions, String paramString, boolean paramBoolean)
  {
    Object localObject = paramTaskRecord.stack.topRunningActivityLocked();
    if ((localObject != null) && (((ActivityRecord)localObject).state == ActivityStack.ActivityState.DESTROYED)) {
      acquireAppLaunchPerfLock(((ActivityRecord)localObject).packageName);
    }
    if ((paramInt & 0x2) == 0) {
      this.mUserLeaving = true;
    }
    if ((paramInt & 0x1) != 0) {
      paramTaskRecord.setTaskToReturnTo(1);
    }
    if (paramTaskRecord.stack == null)
    {
      Slog.e(TAG, "findTaskToMoveToFrontLocked: can't move task=" + paramTaskRecord + " to front. Stack is null");
      return;
    }
    if ((paramTaskRecord.isResizeable()) && (paramActivityOptions != null))
    {
      int i = paramActivityOptions.getLaunchStackId();
      if (canUseActivityOptionsLaunchBounds(paramActivityOptions, i))
      {
        localObject = TaskRecord.validateBounds(paramActivityOptions.getLaunchBounds());
        paramTaskRecord.updateOverrideConfiguration((Rect)localObject);
        paramInt = i;
        if (i == -1) {
          paramInt = paramTaskRecord.getLaunchStackId();
        }
        i = paramInt;
        if (paramInt != paramTaskRecord.stack.mStackId) {
          i = moveTaskToStackUncheckedLocked(paramTaskRecord, paramInt, true, false, paramString).mStackId;
        }
        if (!ActivityManager.StackId.resizeStackWithLaunchBounds(i)) {
          break label288;
        }
        resizeStackLocked(i, (Rect)localObject, null, null, false, true, false);
      }
    }
    localObject = paramTaskRecord.getTopActivity();
    ActivityStack localActivityStack = paramTaskRecord.stack;
    if (localObject == null) {}
    for (localObject = null;; localObject = ((ActivityRecord)localObject).appTimeTracker)
    {
      localActivityStack.moveTaskToFrontLocked(paramTaskRecord, false, paramActivityOptions, (AppTimeTracker)localObject, paramString);
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.d(TAG_STACK, "findTaskToMoveToFront: moved to front of stack=" + paramTaskRecord.stack);
      }
      handleNonResizableTaskIfNeeded(paramTaskRecord, -1, paramTaskRecord.stack.mStackId, paramBoolean);
      return;
      label288:
      this.mWindowManager.resizeTask(paramTaskRecord.taskId, paramTaskRecord.mBounds, paramTaskRecord.mOverrideConfig, false, false);
      break;
    }
  }
  
  boolean finishDisabledPackageActivitiesLocked(String paramString, Set<String> paramSet, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    boolean bool = false;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        if (((ActivityStack)localArrayList.get(j)).finishDisabledPackageActivitiesLocked(paramString, paramSet, paramBoolean1, paramBoolean2, paramInt)) {
          bool = true;
        }
        j -= 1;
      }
      i -= 1;
    }
    return bool;
  }
  
  TaskRecord finishTopRunningActivityLocked(ProcessRecord paramProcessRecord, String paramString)
  {
    Object localObject1 = null;
    ActivityStack localActivityStack = getFocusedStack();
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int k = localArrayList.size();
      int j = 0;
      while (j < k)
      {
        Object localObject2 = (ActivityStack)localArrayList.get(j);
        TaskRecord localTaskRecord = ((ActivityStack)localObject2).finishTopRunningActivityLocked(paramProcessRecord, paramString);
        if (localObject2 != localActivityStack)
        {
          localObject2 = localObject1;
          if (localObject1 != null) {}
        }
        else
        {
          localObject2 = localTaskRecord;
        }
        j += 1;
        localObject1 = localObject2;
      }
      i -= 1;
    }
    return (TaskRecord)localObject1;
  }
  
  void finishVoiceTask(IVoiceInteractionSession paramIVoiceInteractionSession)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int k = localArrayList.size();
      int j = 0;
      while (j < k)
      {
        ((ActivityStack)localArrayList.get(j)).finishVoiceTask(paramIVoiceInteractionSession);
        j += 1;
      }
      i -= 1;
    }
  }
  
  ArrayList<ActivityManager.StackInfo> getAllStackInfosLocked()
  {
    ArrayList localArrayList1 = new ArrayList();
    int i = 0;
    while (i < this.mActivityDisplays.size())
    {
      ArrayList localArrayList2 = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList2.size() - 1;
      while (j >= 0)
      {
        localArrayList1.add(getStackInfoLocked((ActivityStack)localArrayList2.get(j)));
        j -= 1;
      }
      i += 1;
    }
    return localArrayList1;
  }
  
  ArrayList<ActivityRecord> getDumpActivitiesLocked(String paramString)
  {
    return this.mFocusedStack.getDumpActivitiesLocked(paramString);
  }
  
  ActivityStack getFocusedStack()
  {
    return this.mFocusedStack;
  }
  
  ActivityRecord getHomeActivity()
  {
    return getHomeActivityForUser(this.mCurrentUser);
  }
  
  ActivityRecord getHomeActivityForUser(int paramInt)
  {
    ArrayList localArrayList = this.mHomeStack.getAllTasks();
    int i = localArrayList.size() - 1;
    while (i >= 0)
    {
      Object localObject = (TaskRecord)localArrayList.get(i);
      if (((TaskRecord)localObject).isHomeTask())
      {
        localObject = ((TaskRecord)localObject).mActivities;
        int j = ((ArrayList)localObject).size() - 1;
        while (j >= 0)
        {
          ActivityRecord localActivityRecord = (ActivityRecord)((ArrayList)localObject).get(j);
          if ((localActivityRecord.isHomeActivity()) && ((paramInt == -1) || (localActivityRecord.userId == paramInt))) {
            return localActivityRecord;
          }
          j -= 1;
        }
      }
      i -= 1;
    }
    return null;
  }
  
  IBinder getHomeActivityToken()
  {
    ActivityRecord localActivityRecord = getHomeActivity();
    if (localActivityRecord != null) {
      return localActivityRecord.appToken;
    }
    return null;
  }
  
  ActivityStack getLastStack()
  {
    return this.mLastFocusedStack;
  }
  
  int getLockTaskModeState()
  {
    return this.mLockTaskModeState;
  }
  
  TaskRecord getLockedTaskLocked()
  {
    int i = this.mLockTaskModeTasks.size() - 1;
    if (i >= 0) {
      return (TaskRecord)this.mLockTaskModeTasks.get(i);
    }
    return null;
  }
  
  int getNextStackId()
  {
    for (;;)
    {
      if ((this.mNextFreeStackId >= 5) && (getStack(this.mNextFreeStackId) == null)) {
        return this.mNextFreeStackId;
      }
      this.mNextFreeStackId += 1;
    }
  }
  
  int getNextTaskIdForUserLocked(int paramInt)
  {
    int k = this.mCurTaskIdForUser.get(paramInt, 100000 * paramInt);
    int i = nextTaskIdForUser(k, paramInt);
    while ((this.mRecentTasks.taskIdTakenForUserLocked(i, paramInt)) || (anyTaskForIdLocked(i, false, -1) != null))
    {
      int j = nextTaskIdForUser(i, paramInt);
      i = j;
      if (j == k) {
        throw new IllegalStateException("Cannot get an available task id. Reached limit of 100000 running tasks per user.");
      }
    }
    this.mCurTaskIdForUser.put(paramInt, i);
    return i;
  }
  
  public List<String> getRecentAppLockedPackages()
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      Object localObject = getStatusBarService().getLockedPackageList();
      if (localObject != null)
      {
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          String str = (String)((Iterator)localObject).next();
          if (str != null) {
            localArrayList.add(str.substring(1, str.indexOf('/')));
          }
        }
      }
      return localArrayList;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      Slog.w("StatusBarManager", "warning: getLockedPackageList fail");
    }
    return localArrayList;
  }
  
  ActivityStack getStack(int paramInt)
  {
    return getStack(paramInt, false, false);
  }
  
  ActivityStack getStack(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    ActivityContainer localActivityContainer = (ActivityContainer)this.mActivityContainers.get(paramInt);
    if (localActivityContainer != null) {
      return localActivityContainer.mStack;
    }
    if ((paramBoolean1) && (ActivityManager.StackId.isStaticStack(paramInt))) {
      return createStackOnDisplay(paramInt, 0, paramBoolean2);
    }
    return null;
  }
  
  ActivityManager.StackInfo getStackInfoLocked(int paramInt)
  {
    ActivityStack localActivityStack = getStack(paramInt);
    if (localActivityStack != null) {
      return getStackInfoLocked(localActivityStack);
    }
    return null;
  }
  
  ArrayList<ActivityStack> getStacks()
  {
    ArrayList localArrayList = new ArrayList();
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      localArrayList.addAll(((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks);
      i -= 1;
    }
    return localArrayList;
  }
  
  void getTasksLocked(int paramInt1, List<ActivityManager.RunningTaskInfo> paramList, int paramInt2, boolean paramBoolean)
  {
    ArrayList localArrayList2 = new ArrayList();
    int k = this.mActivityDisplays.size();
    int j = 0;
    int i;
    Object localObject1;
    Object localObject2;
    ArrayList localArrayList1;
    for (;;)
    {
      i = paramInt1;
      if (j >= k) {
        break;
      }
      localObject1 = ((ActivityDisplay)this.mActivityDisplays.valueAt(j)).mStacks;
      i = ((ArrayList)localObject1).size() - 1;
      while (i >= 0)
      {
        localObject2 = (ActivityStack)((ArrayList)localObject1).get(i);
        localArrayList1 = new ArrayList();
        localArrayList2.add(localArrayList1);
        ((ActivityStack)localObject2).getTasksLocked(localArrayList1, paramInt2, paramBoolean);
        i -= 1;
      }
      j += 1;
    }
    while (localObject1 != null)
    {
      paramList.add((ActivityManager.RunningTaskInfo)((ArrayList)localObject1).remove(0));
      i -= 1;
      if (i <= 0) {
        break;
      }
      long l1 = Long.MIN_VALUE;
      localObject1 = null;
      paramInt2 = localArrayList2.size();
      paramInt1 = 0;
      while (paramInt1 < paramInt2)
      {
        localArrayList1 = (ArrayList)localArrayList2.get(paramInt1);
        long l2 = l1;
        localObject2 = localObject1;
        if (!localArrayList1.isEmpty())
        {
          long l3 = ((ActivityManager.RunningTaskInfo)localArrayList1.get(0)).lastActiveTime;
          l2 = l1;
          localObject2 = localObject1;
          if (l3 > l1)
          {
            l2 = l3;
            localObject2 = localArrayList1;
          }
        }
        paramInt1 += 1;
        l1 = l2;
        localObject1 = localObject2;
      }
    }
  }
  
  public List<IBinder> getTopVisibleActivities()
  {
    Object localObject = (ActivityDisplay)this.mActivityDisplays.get(0);
    if (localObject == null) {
      return Collections.EMPTY_LIST;
    }
    ArrayList localArrayList = new ArrayList();
    localObject = ((ActivityDisplay)localObject).mStacks;
    int i = ((ArrayList)localObject).size() - 1;
    if (i >= 0)
    {
      ActivityStack localActivityStack = (ActivityStack)((ArrayList)localObject).get(i);
      ActivityRecord localActivityRecord;
      if (localActivityStack.getStackVisibilityLocked(null) == 1)
      {
        localActivityRecord = localActivityStack.topActivity();
        if (localActivityRecord != null)
        {
          if (localActivityStack != this.mFocusedStack) {
            break label102;
          }
          localArrayList.add(0, localActivityRecord.appToken);
        }
      }
      for (;;)
      {
        i -= 1;
        break;
        label102:
        localArrayList.add(localActivityRecord.appToken);
      }
    }
    return localArrayList;
  }
  
  UserInfo getUserInfo(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      UserInfo localUserInfo = UserManager.get(this.mService.mContext).getUserInfo(paramInt);
      return localUserInfo;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  void goingToSleepLocked()
  {
    scheduleSleepTimeout();
    if (!this.mGoingToSleep.isHeld())
    {
      this.mGoingToSleep.acquire();
      if (this.mLaunchingActivity.isHeld())
      {
        this.mLaunchingActivity.release();
        this.mService.mHandler.removeMessages(104);
      }
    }
    checkReadyForSleepLocked();
  }
  
  void handleAppCrashLocked(ProcessRecord paramProcessRecord)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).handleAppCrashLocked(paramProcessRecord);
        j -= 1;
      }
      i -= 1;
    }
  }
  
  boolean handleAppDiedLocked(ProcessRecord paramProcessRecord)
  {
    boolean bool = false;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        bool |= ((ActivityStack)localArrayList.get(j)).handleAppDiedLocked(paramProcessRecord);
        j -= 1;
      }
      i -= 1;
    }
    return bool;
  }
  
  void handleLaunchTaskBehindCompleteLocked(ActivityRecord paramActivityRecord)
  {
    TaskRecord localTaskRecord = paramActivityRecord.task;
    ActivityStack localActivityStack = localTaskRecord.stack;
    paramActivityRecord.mLaunchTaskBehind = false;
    localTaskRecord.setLastThumbnailLocked(localActivityStack.screenshotActivitiesLocked(paramActivityRecord));
    this.mRecentTasks.addLocked(localTaskRecord);
    this.mService.notifyTaskStackChangedLocked();
    this.mWindowManager.setAppVisibility(paramActivityRecord.appToken, false);
    paramActivityRecord = localActivityStack.topActivity();
    if (paramActivityRecord != null) {
      paramActivityRecord.task.touchActiveTime();
    }
  }
  
  void handleNonResizableTaskIfNeeded(TaskRecord paramTaskRecord, int paramInt1, int paramInt2)
  {
    handleNonResizableTaskIfNeeded(paramTaskRecord, paramInt1, paramInt2, false);
  }
  
  void handleNonResizableTaskIfNeeded(TaskRecord paramTaskRecord, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramTaskRecord == null)
    {
      Slog.e(TAG, "handleNonResizableTaskIfNeeded: task is null. Callers=" + Debug.getCallers(6));
      return;
    }
    if ((!isStackDockedInEffect(paramInt2)) && (paramInt1 != 3)) {}
    while (paramTaskRecord.isHomeTask()) {
      return;
    }
    Object localObject = paramTaskRecord.getTopActivity();
    if ((!paramTaskRecord.canGoInDockedStack()) || (paramBoolean))
    {
      this.mService.mHandler.sendEmptyMessage(68);
      paramBoolean = bool;
      if (paramInt2 == 3) {
        paramBoolean = true;
      }
      moveTasksToFullscreenStackLocked(3, paramBoolean);
    }
    while ((localObject == null) || (!((ActivityRecord)localObject).isNonResizableOrForced()) || (((ActivityRecord)localObject).noDisplay)) {
      return;
    }
    localObject = ((ActivityRecord)localObject).appInfo.packageName;
    this.mService.mHandler.obtainMessage(67, paramTaskRecord.taskId, 0, localObject).sendToTarget();
  }
  
  void initPowerManagement()
  {
    PowerManager localPowerManager = (PowerManager)this.mService.mContext.getSystemService("power");
    this.mGoingToSleep = localPowerManager.newWakeLock(1, "ActivityManager-Sleep");
    this.mLaunchingActivity = localPowerManager.newWakeLock(1, "*launch*");
    this.mLaunchingActivity.setReferenceCounted(false);
  }
  
  void invalidateTaskLayers()
  {
    this.mTaskLayersChanged = true;
  }
  
  boolean isCurrentProfileLocked(int paramInt)
  {
    if (paramInt == this.mCurrentUser) {
      return true;
    }
    return this.mService.mUserController.isCurrentProfileLocked(paramInt);
  }
  
  boolean isFocusedStack(ActivityStack paramActivityStack)
  {
    boolean bool = false;
    if (paramActivityStack == null) {
      return false;
    }
    ActivityRecord localActivityRecord = paramActivityStack.mActivityContainer.mParentActivity;
    if (localActivityRecord != null) {
      paramActivityStack = localActivityRecord.task.stack;
    }
    if (paramActivityStack == this.mFocusedStack) {
      bool = true;
    }
    return bool;
  }
  
  boolean isFrontStack(ActivityStack paramActivityStack)
  {
    boolean bool = false;
    if (paramActivityStack == null) {
      return false;
    }
    ActivityRecord localActivityRecord = paramActivityStack.mActivityContainer.mParentActivity;
    if (localActivityRecord != null) {
      paramActivityStack = localActivityRecord.task.stack;
    }
    if (paramActivityStack == this.mHomeStack.mStacks.get(this.mHomeStack.mStacks.size() - 1)) {
      bool = true;
    }
    return bool;
  }
  
  ActivityRecord isInAnyStackLocked(IBinder paramIBinder)
  {
    int k = this.mActivityDisplays.size();
    int i = 0;
    while (i < k)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityRecord localActivityRecord = ((ActivityStack)localArrayList.get(j)).isInStackLocked(paramIBinder);
        if (localActivityRecord != null) {
          return localActivityRecord;
        }
        j -= 1;
      }
      i += 1;
    }
    return null;
  }
  
  boolean isLastLockedTask(TaskRecord paramTaskRecord)
  {
    if (this.mLockTaskModeTasks.size() == 1) {
      return this.mLockTaskModeTasks.contains(paramTaskRecord);
    }
    return false;
  }
  
  boolean isLockTaskModeViolation(TaskRecord paramTaskRecord)
  {
    return isLockTaskModeViolation(paramTaskRecord, false);
  }
  
  boolean isLockTaskModeViolation(TaskRecord paramTaskRecord, boolean paramBoolean)
  {
    int i;
    if ((getLockedTaskLocked() != paramTaskRecord) || (paramBoolean)) {
      i = paramTaskRecord.mLockTaskAuth;
    }
    switch (i)
    {
    default: 
      Slog.w(TAG, "isLockTaskModeViolation: invalid lockTaskAuth value=" + i);
      return true;
      return false;
    case 0: 
      return !this.mLockTaskModeTasks.isEmpty();
    case 2: 
    case 3: 
    case 4: 
      return false;
    }
    return !this.mLockTaskModeTasks.isEmpty();
  }
  
  boolean isLockedTask(TaskRecord paramTaskRecord)
  {
    return this.mLockTaskModeTasks.contains(paramTaskRecord);
  }
  
  boolean isStackDockedInEffect(int paramInt)
  {
    return (paramInt == 3) || ((ActivityManager.StackId.isResizeableByDockedStack(paramInt)) && (getStack(3) != null));
  }
  
  boolean isUserLockedProfile(int paramInt)
  {
    if (!this.mService.mUserController.shouldConfirmCredentials(paramInt)) {
      return false;
    }
    ActivityStack[] arrayOfActivityStack = new ActivityStack[3];
    arrayOfActivityStack[0] = getStack(3);
    arrayOfActivityStack[1] = getStack(2);
    arrayOfActivityStack[2] = getStack(1);
    int k = arrayOfActivityStack.length;
    int i = 0;
    if (i < k)
    {
      Object localObject = arrayOfActivityStack[i];
      if (localObject == null) {}
      label173:
      do
      {
        for (;;)
        {
          i += 1;
          break;
          if ((((ActivityStack)localObject).topRunningActivityLocked() != null) && (((ActivityStack)localObject).getStackVisibilityLocked(null) != 0) && ((!((ActivityStack)localObject).isDockedStack()) || (!this.mIsDockMinimized)))
          {
            if (((ActivityStack)localObject).mStackId != 2) {
              break label173;
            }
            localObject = ((ActivityStack)localObject).getAllTasks();
            int m = ((List)localObject).size();
            int j = 0;
            while (j < m)
            {
              if (taskContainsActivityFromUser((TaskRecord)((List)localObject).get(j), paramInt)) {
                return true;
              }
              j += 1;
            }
          }
        }
        localObject = ((ActivityStack)localObject).topTask();
      } while ((localObject == null) || (!taskContainsActivityFromUser((TaskRecord)localObject, paramInt)));
      return true;
    }
    return false;
  }
  
  void logStackState()
  {
    this.mActivityMetricsLogger.logWindowState();
  }
  
  boolean moveActivityStackToFront(ActivityRecord paramActivityRecord, String paramString)
  {
    if (paramActivityRecord == null) {
      return false;
    }
    TaskRecord localTaskRecord = paramActivityRecord.task;
    if ((localTaskRecord == null) || (localTaskRecord.stack == null))
    {
      Slog.w(TAG, "Can't move stack to front for r=" + paramActivityRecord + " task=" + localTaskRecord);
      return false;
    }
    localTaskRecord.stack.moveToFront(paramString, localTaskRecord);
    return true;
  }
  
  /* Error */
  void moveActivityToPinnedStackLocked(ActivityRecord paramActivityRecord, String paramString, Rect paramRect)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 794	com/android/server/am/ActivityStackSupervisor:mWindowManager	Lcom/android/server/wm/WindowManagerService;
    //   4: invokevirtual 1985	com/android/server/wm/WindowManagerService:deferSurfaceLayout	()V
    //   7: aload_1
    //   8: getfield 562	com/android/server/am/ActivityRecord:task	Lcom/android/server/am/TaskRecord;
    //   11: astore 4
    //   13: aload_1
    //   14: aload 4
    //   16: getfield 963	com/android/server/am/TaskRecord:stack	Lcom/android/server/am/ActivityStack;
    //   19: invokevirtual 1988	com/android/server/am/ActivityStack:getVisibleBehindActivity	()Lcom/android/server/am/ActivityRecord;
    //   22: if_acmpne +10 -> 32
    //   25: aload_0
    //   26: aload_1
    //   27: iconst_0
    //   28: invokevirtual 1992	com/android/server/am/ActivityStackSupervisor:requestVisibleBehindLocked	(Lcom/android/server/am/ActivityRecord;Z)Z
    //   31: pop
    //   32: aload_0
    //   33: iconst_4
    //   34: iconst_1
    //   35: iconst_1
    //   36: invokevirtual 984	com/android/server/am/ActivityStackSupervisor:getStack	(IZZ)Lcom/android/server/am/ActivityStack;
    //   39: astore 5
    //   41: aload_0
    //   42: iconst_4
    //   43: aload 4
    //   45: getfield 1670	com/android/server/am/TaskRecord:mBounds	Landroid/graphics/Rect;
    //   48: aconst_null
    //   49: aconst_null
    //   50: iconst_0
    //   51: iconst_1
    //   52: iconst_0
    //   53: invokevirtual 1659	com/android/server/am/ActivityStackSupervisor:resizeStackLocked	(ILandroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Rect;ZZZ)V
    //   56: aload 4
    //   58: getfield 1005	com/android/server/am/TaskRecord:mActivities	Ljava/util/ArrayList;
    //   61: invokevirtual 841	java/util/ArrayList:size	()I
    //   64: iconst_1
    //   65: if_icmpne +68 -> 133
    //   68: aload 4
    //   70: invokevirtual 1995	com/android/server/am/TaskRecord:getTaskToReturnTo	()I
    //   73: iconst_1
    //   74: if_icmpne +8 -> 82
    //   77: aload_0
    //   78: aload_2
    //   79: invokevirtual 1998	com/android/server/am/ActivityStackSupervisor:moveHomeStackToFront	(Ljava/lang/String;)V
    //   82: aload_0
    //   83: aload 4
    //   85: getfield 845	com/android/server/am/TaskRecord:taskId	I
    //   88: iconst_4
    //   89: iconst_1
    //   90: iconst_1
    //   91: aload_2
    //   92: iconst_0
    //   93: invokevirtual 2002	com/android/server/am/ActivityStackSupervisor:moveTaskToStackLocked	(IIZZLjava/lang/String;Z)Z
    //   96: pop
    //   97: aload_0
    //   98: getfield 794	com/android/server/am/ActivityStackSupervisor:mWindowManager	Lcom/android/server/wm/WindowManagerService;
    //   101: invokevirtual 2005	com/android/server/wm/WindowManagerService:continueSurfaceLayout	()V
    //   104: aload_0
    //   105: aconst_null
    //   106: iconst_0
    //   107: iconst_0
    //   108: invokevirtual 1123	com/android/server/am/ActivityStackSupervisor:ensureActivitiesVisibleLocked	(Lcom/android/server/am/ActivityRecord;IZ)V
    //   111: aload_0
    //   112: invokevirtual 1162	com/android/server/am/ActivityStackSupervisor:resumeFocusedStackTopActivityLocked	()Z
    //   115: pop
    //   116: aload_0
    //   117: getfield 794	com/android/server/am/ActivityStackSupervisor:mWindowManager	Lcom/android/server/wm/WindowManagerService;
    //   120: aload_3
    //   121: iconst_m1
    //   122: invokevirtual 2009	com/android/server/wm/WindowManagerService:animateResizePinnedStack	(Landroid/graphics/Rect;I)V
    //   125: aload_0
    //   126: getfield 436	com/android/server/am/ActivityStackSupervisor:mService	Lcom/android/server/am/ActivityManagerService;
    //   129: invokevirtual 2012	com/android/server/am/ActivityManagerService:notifyActivityPinnedLocked	()V
    //   132: return
    //   133: aload 5
    //   135: aload_1
    //   136: invokevirtual 2015	com/android/server/am/ActivityStack:moveActivityToStack	(Lcom/android/server/am/ActivityRecord;)V
    //   139: goto -42 -> 97
    //   142: astore_1
    //   143: aload_0
    //   144: getfield 794	com/android/server/am/ActivityStackSupervisor:mWindowManager	Lcom/android/server/wm/WindowManagerService;
    //   147: invokevirtual 2005	com/android/server/wm/WindowManagerService:continueSurfaceLayout	()V
    //   150: aload_1
    //   151: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	152	0	this	ActivityStackSupervisor
    //   0	152	1	paramActivityRecord	ActivityRecord
    //   0	152	2	paramString	String
    //   0	152	3	paramRect	Rect
    //   11	73	4	localTaskRecord	TaskRecord
    //   39	95	5	localActivityStack	ActivityStack
    // Exception table:
    //   from	to	target	type
    //   7	32	142	finally
    //   32	82	142	finally
    //   82	97	142	finally
    //   133	139	142	finally
  }
  
  boolean moveHomeStackTaskToTop(int paramInt, String paramString)
  {
    if (paramInt == 2)
    {
      this.mWindowManager.showRecentApps(false);
      return false;
    }
    this.mHomeStack.moveHomeStackTaskToTop(paramInt);
    ActivityRecord localActivityRecord = getHomeActivity();
    if (localActivityRecord == null) {
      return false;
    }
    this.mService.setFocusedActivityLocked(localActivityRecord, paramString);
    return true;
  }
  
  void moveHomeStackToFront(String paramString)
  {
    this.mHomeStack.moveToFront(paramString);
  }
  
  void moveProfileTasksFromFreeformToFullscreenStackLocked(int paramInt)
  {
    Object localObject1 = getStack(2);
    if (localObject1 == null) {
      return;
    }
    this.mWindowManager.deferSurfaceLayout();
    try
    {
      localObject1 = ((ActivityStack)localObject1).getAllTasks();
      int i = ((ArrayList)localObject1).size() - 1;
      while (i >= 0)
      {
        if (taskContainsActivityFromUser((TaskRecord)((ArrayList)localObject1).get(i), paramInt)) {
          positionTaskInStackLocked(((TaskRecord)((ArrayList)localObject1).get(i)).taskId, 1, 0);
        }
        i -= 1;
      }
      return;
    }
    finally
    {
      this.mWindowManager.continueSurfaceLayout();
    }
  }
  
  boolean moveTaskToStackLocked(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString, boolean paramBoolean3)
  {
    return moveTaskToStackLocked(paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramString, paramBoolean3, false);
  }
  
  boolean moveTaskToStackLocked(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString, boolean paramBoolean3, boolean paramBoolean4)
  {
    TaskRecord localTaskRecord = anyTaskForIdLocked(paramInt1);
    if (localTaskRecord == null)
    {
      Slog.w(TAG, "moveTaskToStack: no task for id=" + paramInt1);
      return false;
    }
    if ((localTaskRecord.stack != null) && (localTaskRecord.stack.mStackId == paramInt2))
    {
      Slog.i(TAG, "moveTaskToStack: taskId=" + paramInt1 + " already in stackId=" + paramInt2);
      return true;
    }
    ActivityRecord localActivityRecord;
    label154:
    boolean bool;
    if ((paramInt2 != 2) || (this.mService.mSupportsFreeformWindowManagement))
    {
      localActivityRecord = localTaskRecord.getTopActivity();
      if (localTaskRecord.stack == null) {
        break label402;
      }
      paramInt1 = localTaskRecord.stack.mStackId;
      if ((!ActivityManager.StackId.replaceWindowsOnTaskMove(paramInt1, paramInt2)) || (localActivityRecord == null)) {
        break label407;
      }
      paramInt1 = 1;
      if (paramInt1 != 0) {
        this.mWindowManager.setReplacingWindow(localActivityRecord.appToken, paramBoolean3);
      }
      this.mWindowManager.deferSurfaceLayout();
      bool = true;
    }
    int i;
    for (;;)
    {
      try
      {
        localActivityStack = moveTaskToStackUncheckedLocked(localTaskRecord, paramInt2, paramBoolean1, paramBoolean2, paramString + " moveTaskToStack");
        i = localActivityStack.mStackId;
        if (!paramBoolean3) {
          localActivityStack.mNoAnimActivities.add(localActivityRecord);
        }
        this.mWindowManager.prepareFreezingTaskBounds(localActivityStack.mStackId);
        if ((i == 1) && (localTaskRecord.mBounds != null))
        {
          paramString = localActivityStack.mBounds;
          if (paramInt1 != 0)
          {
            paramBoolean1 = false;
            paramBoolean1 = resizeTaskLocked(localTaskRecord, paramString, 0, paramBoolean1, paramBoolean4);
            this.mWindowManager.continueSurfaceLayout();
            if (paramInt1 != 0)
            {
              paramString = this.mWindowManager;
              localObject = localActivityRecord.appToken;
              if (!paramBoolean1) {
                break label520;
              }
              paramBoolean1 = false;
              label320:
              paramString.scheduleClearReplacingWindowIfNeeded((IBinder)localObject, paramBoolean1);
            }
            if (!paramBoolean4)
            {
              if (paramInt1 == 0) {
                break label525;
              }
              paramBoolean1 = false;
              label339:
              ensureActivitiesVisibleLocked(null, 0, paramBoolean1);
              resumeFocusedStackTopActivityLocked();
            }
            handleNonResizableTaskIfNeeded(localTaskRecord, paramInt2, i);
            if (paramInt2 != i) {
              break label530;
            }
            return true;
            throw new IllegalArgumentException("moveTaskToStack:Attempt to move task " + paramInt1 + " to unsupported freeform stack");
            label402:
            paramInt1 = -1;
            break;
            label407:
            paramInt1 = 0;
            break label154;
          }
          paramBoolean1 = true;
          continue;
        }
        if (i != 2) {
          break label546;
        }
        Object localObject = localTaskRecord.getLaunchBounds();
        paramString = (String)localObject;
        if (localObject != null) {
          break label532;
        }
        localActivityStack.layoutTaskInStack(localTaskRecord, null);
        paramString = localTaskRecord.mBounds;
      }
      finally
      {
        ActivityStack localActivityStack;
        label458:
        this.mWindowManager.continueSurfaceLayout();
      }
      paramBoolean1 = resizeTaskLocked(localTaskRecord, paramString, 2, paramBoolean1, paramBoolean4);
    }
    for (;;)
    {
      paramString = localActivityStack.mBounds;
      if (paramInt1 != 0) {}
      for (paramBoolean1 = false;; paramBoolean1 = true)
      {
        paramBoolean1 = resizeTaskLocked(localTaskRecord, paramString, 0, paramBoolean1, paramBoolean4);
        break;
      }
      label520:
      paramBoolean1 = true;
      break label320;
      label525:
      paramBoolean1 = true;
      break label339;
      label530:
      return false;
      label532:
      if (paramInt1 != 0)
      {
        paramBoolean1 = false;
        break label458;
      }
      paramBoolean1 = true;
      break label458;
      label546:
      if (i != 3)
      {
        paramBoolean1 = bool;
        if (i != 4) {
          break;
        }
      }
    }
  }
  
  ActivityStack moveTaskToStackUncheckedLocked(TaskRecord paramTaskRecord, int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString)
  {
    ActivityRecord localActivityRecord;
    ActivityStack localActivityStack;
    int j;
    boolean bool2;
    label63:
    boolean bool1;
    label85:
    int i;
    if ((!ActivityManager.StackId.isMultiWindowStack(paramInt)) || (this.mService.mSupportsMultiWindow))
    {
      localActivityRecord = paramTaskRecord.topRunningActivityLocked();
      localActivityStack = paramTaskRecord.stack;
      if ((!isFocusedStack(localActivityStack)) || (topRunningActivityLocked() != localActivityRecord)) {
        break label295;
      }
      j = 1;
      if (localActivityStack.mResumedActivity != localActivityRecord) {
        break label301;
      }
      bool2 = true;
      if (!isFrontStack(localActivityStack)) {
        break label313;
      }
      if (localActivityStack.topRunningActivityLocked() != localActivityRecord) {
        break label307;
      }
      bool1 = true;
      i = paramInt;
      if (paramInt == 3)
      {
        if (!paramTaskRecord.isResizeable()) {
          break label319;
        }
        i = paramInt;
      }
      paramInt = i;
      if (i == 2)
      {
        paramInt = i;
        if (this.mService.mUserController.shouldConfirmCredentials(paramTaskRecord.userId))
        {
          if (localActivityStack == null) {
            break label384;
          }
          paramInt = localActivityStack.mStackId;
          label143:
          Slog.w(TAG, "Can not move locked profile task=" + paramTaskRecord + " to freeform stack. Moving to stackId=" + paramInt + " instead.");
        }
      }
      paramTaskRecord.mTemporarilyUnresizable = true;
      localActivityStack = getStack(paramInt, true, paramBoolean1);
      paramTaskRecord.mTemporarilyUnresizable = false;
      this.mWindowManager.moveTaskToStack(paramTaskRecord.taskId, localActivityStack.mStackId, paramBoolean1);
      localActivityStack.addTask(paramTaskRecord, paramBoolean1, paramString);
      if ((paramBoolean2) || (j != 0)) {
        break label389;
      }
    }
    for (;;)
    {
      localActivityStack.moveToFrontAndResumeStateIfNeeded(localActivityRecord, bool1, bool2, paramString);
      return localActivityStack;
      throw new IllegalStateException("moveTaskToStackUncheckedLocked: Device doesn't support multi-window task=" + paramTaskRecord + " to stackId=" + paramInt);
      label295:
      j = 0;
      break;
      label301:
      bool2 = false;
      break label63;
      label307:
      bool1 = false;
      break label85;
      label313:
      bool1 = false;
      break label85;
      label319:
      if (localActivityStack != null) {}
      for (i = localActivityStack.mStackId;; i = 1)
      {
        Slog.w(TAG, "Can not move unresizeable task=" + paramTaskRecord + " to docked stack. Moving to stackId=" + i + " instead.");
        break;
      }
      label384:
      paramInt = 1;
      break label143;
      label389:
      bool1 = true;
    }
  }
  
  void moveTasksToFullscreenStackLocked(int paramInt, boolean paramBoolean)
  {
    Object localObject1 = getStack(paramInt);
    if (localObject1 == null) {
      return;
    }
    this.mWindowManager.deferSurfaceLayout();
    if (paramInt == 3) {
      paramInt = 0;
    }
    for (;;)
    {
      if (paramInt <= 4) {}
      try
      {
        if ((!ActivityManager.StackId.isResizeableByDockedStack(paramInt)) || (getStack(paramInt) == null)) {
          break label197;
        }
        resizeStackLocked(paramInt, null, null, null, true, true, true);
      }
      finally
      {
        int i;
        this.mAllowDockedStackResize = true;
        this.mWindowManager.continueSurfaceLayout();
      }
      this.mAllowDockedStackResize = false;
      localObject1 = ((ActivityStack)localObject1).getAllTasks();
      i = ((ArrayList)localObject1).size();
      if (paramBoolean)
      {
        paramInt = 0;
        while (paramInt < i)
        {
          moveTaskToStackLocked(((TaskRecord)((ArrayList)localObject1).get(paramInt)).taskId, 1, paramBoolean, paramBoolean, "moveTasksToFullscreenStack", true, true);
          paramInt += 1;
        }
        ensureActivitiesVisibleLocked(null, 0, true);
        resumeFocusedStackTopActivityLocked();
      }
      for (;;)
      {
        this.mAllowDockedStackResize = true;
        this.mWindowManager.continueSurfaceLayout();
        return;
        paramInt = i - 1;
        while (paramInt >= 0)
        {
          positionTaskInStackLocked(((TaskRecord)((ArrayList)localObject1).get(paramInt)).taskId, 1, 0);
          paramInt -= 1;
        }
      }
      label197:
      paramInt += 1;
    }
  }
  
  boolean moveTopStackActivityToPinnedStackLocked(int paramInt, Rect paramRect)
  {
    ActivityStack localActivityStack = getStack(paramInt, false, false);
    if (localActivityStack == null) {
      throw new IllegalArgumentException("moveTopStackActivityToPinnedStackLocked: Unknown stackId=" + paramInt);
    }
    ActivityRecord localActivityRecord = localActivityStack.topRunningActivityLocked();
    if (localActivityRecord == null)
    {
      Slog.w(TAG, "moveTopStackActivityToPinnedStackLocked: No top running activity in stack=" + localActivityStack);
      return false;
    }
    if ((this.mService.mForceResizableActivities) || (localActivityRecord.supportsPictureInPicture()))
    {
      moveActivityToPinnedStackLocked(localActivityRecord, "moveTopActivityToPinnedStack", paramRect);
      return true;
    }
    Slog.w(TAG, "moveTopStackActivityToPinnedStackLocked: Picture-In-Picture not supported for  r=" + localActivityRecord);
    return false;
  }
  
  void notifyActivityDrawnForKeyguard()
  {
    if (ActivityManagerDebugConfig.DEBUG_LOCKSCREEN) {
      this.mService.logLockScreen("");
    }
    this.mWindowManager.notifyActivityDrawnForKeyguard();
  }
  
  void notifyAppTransitionDone()
  {
    continueUpdateBounds(0);
    int i = this.mResizingTasksDuringAnimation.size() - 1;
    while (i >= 0)
    {
      int j = ((Integer)this.mResizingTasksDuringAnimation.valueAt(i)).intValue();
      if (anyTaskForIdLocked(j, false, -1) != null) {
        this.mWindowManager.setTaskDockedResizing(j, false);
      }
      i -= 1;
    }
    this.mResizingTasksDuringAnimation.clear();
  }
  
  boolean okToShowLocked(ActivityRecord paramActivityRecord)
  {
    boolean bool2 = true;
    if (paramActivityRecord != null)
    {
      boolean bool1 = bool2;
      if ((paramActivityRecord.info.flags & 0x400) == 0)
      {
        if (!isCurrentProfileLocked(paramActivityRecord.userId)) {
          break label56;
        }
        bool1 = bool2;
        if (this.mService.mUserController.isUserStoppingOrShuttingDownLocked(paramActivityRecord.userId)) {
          bool1 = false;
        }
      }
      return bool1;
      label56:
      return false;
    }
    return false;
  }
  
  public void onDisplayAdded(int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_STACK) {
      Slog.v(TAG, "Display added displayId=" + paramInt);
    }
    this.mHandler.sendMessage(this.mHandler.obtainMessage(105, paramInt, 0));
  }
  
  public void onDisplayChanged(int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_STACK) {
      Slog.v(TAG, "Display changed displayId=" + paramInt);
    }
    this.mHandler.sendMessage(this.mHandler.obtainMessage(106, paramInt, 0));
  }
  
  public void onDisplayRemoved(int paramInt)
  {
    if (ActivityManagerDebugConfig.DEBUG_STACK) {
      Slog.v(TAG, "Display removed displayId=" + paramInt);
    }
    this.mHandler.sendMessage(this.mHandler.obtainMessage(107, paramInt, 0));
  }
  
  void onLockTaskPackagesUpdatedLocked()
  {
    int i = 0;
    int m = this.mLockTaskModeTasks.size() - 1;
    label52:
    int k;
    if (m >= 0)
    {
      localObject = (TaskRecord)this.mLockTaskModeTasks.get(m);
      if (((TaskRecord)localObject).mLockTaskAuth != 2)
      {
        if (((TaskRecord)localObject).mLockTaskAuth != 3) {
          break label108;
        }
        j = 1;
        ((TaskRecord)localObject).setLockTaskAuth();
        if (((TaskRecord)localObject).mLockTaskAuth == 2) {
          break label113;
        }
        if (((TaskRecord)localObject).mLockTaskAuth != 3) {
          break label118;
        }
        k = 1;
        label77:
        n = i;
        if (j != 0) {
          if (k == 0) {
            break label123;
          }
        }
      }
      for (int n = i;; n = 1)
      {
        m -= 1;
        i = n;
        break;
        j = 1;
        break label52;
        label108:
        j = 0;
        break label52;
        label113:
        k = 1;
        break label77;
        label118:
        k = 0;
        break label77;
        label123:
        if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
          Slog.d(TAG_LOCKTASK, "onLockTaskPackagesUpdated: removing " + localObject + " mLockTaskAuth=" + ((TaskRecord)localObject).lockTaskAuthToString());
        }
        removeLockedTaskLocked((TaskRecord)localObject);
        ((TaskRecord)localObject).performClearTaskLocked();
      }
    }
    int j = this.mActivityDisplays.size() - 1;
    while (j >= 0)
    {
      localObject = ((ActivityDisplay)this.mActivityDisplays.valueAt(j)).mStacks;
      k = ((ArrayList)localObject).size() - 1;
      while (k >= 0)
      {
        ((ActivityStack)((ArrayList)localObject).get(k)).onLockTaskPackagesUpdatedLocked();
        k -= 1;
      }
      j -= 1;
    }
    Object localObject = topRunningActivityLocked();
    if (localObject != null) {}
    for (localObject = ((ActivityRecord)localObject).task;; localObject = null)
    {
      j = i;
      if (this.mLockTaskModeTasks.isEmpty())
      {
        j = i;
        if (localObject != null)
        {
          j = i;
          if (((TaskRecord)localObject).mLockTaskAuth == 2)
          {
            if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
              Slog.d(TAG_LOCKTASK, "onLockTaskPackagesUpdated: starting new locktask task=" + localObject);
            }
            setLockTaskModeLocked((TaskRecord)localObject, 1, "package updated", false);
            j = 1;
          }
        }
      }
      if (j != 0) {
        resumeFocusedStackTopActivityLocked();
      }
      return;
    }
  }
  
  boolean pauseBackStacks(boolean paramBoolean1, ActivityRecord paramActivityRecord, boolean paramBoolean2)
  {
    boolean bool1 = false;
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)localArrayList.get(j);
        boolean bool2 = bool1;
        if (!isFocusedStack(localActivityStack))
        {
          bool2 = bool1;
          if (localActivityStack.mResumedActivity != null)
          {
            if (ActivityManagerDebugConfig.DEBUG_STATES) {
              Slog.d(TAG_STATES, "pauseBackStacks: stack=" + localActivityStack + " mResumedActivity=" + localActivityStack.mResumedActivity);
            }
            bool2 = bool1 | localActivityStack.startPausingLocked(paramBoolean1, false, paramActivityRecord, paramBoolean2);
          }
        }
        j -= 1;
        bool1 = bool2;
      }
      i -= 1;
    }
    return bool1;
  }
  
  void pauseChildStacks(ActivityRecord paramActivityRecord1, boolean paramBoolean1, boolean paramBoolean2, ActivityRecord paramActivityRecord2, boolean paramBoolean3)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ActivityStack localActivityStack = (ActivityStack)localArrayList.get(j);
        if ((localActivityStack.mResumedActivity != null) && (localActivityStack.mActivityContainer.mParentActivity == paramActivityRecord1)) {
          localActivityStack.startPausingLocked(paramBoolean1, paramBoolean2, paramActivityRecord2, paramBoolean3);
        }
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void positionTaskInStackLocked(int paramInt1, int paramInt2, int paramInt3)
  {
    TaskRecord localTaskRecord = anyTaskForIdLocked(paramInt1);
    if (localTaskRecord == null)
    {
      Slog.w(TAG, "positionTaskInStackLocked: no task for id=" + paramInt1);
      return;
    }
    ActivityStack localActivityStack = getStack(paramInt2, true, false);
    localTaskRecord.updateOverrideConfigurationForStack(localActivityStack);
    this.mWindowManager.positionTaskInStack(paramInt1, paramInt2, paramInt3, localTaskRecord.mBounds, localTaskRecord.mOverrideConfig);
    localActivityStack.positionTask(localTaskRecord, paramInt3);
    localActivityStack.ensureActivitiesVisibleLocked(null, 0, false);
    resumeFocusedStackTopActivityLocked();
  }
  
  final ArrayList<ActivityRecord> processStoppingActivitiesLocked(boolean paramBoolean)
  {
    Object localObject1 = null;
    boolean bool3 = allResumedActivitiesVisible();
    int i = this.mStoppingActivities.size() - 1;
    while (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mStoppingActivities.get(i);
      boolean bool2 = this.mWaitingVisibleActivities.contains(localActivityRecord);
      if (ActivityManagerDebugConfig.DEBUG_STATES) {
        Slog.v(TAG, "Stopping " + localActivityRecord + ": nowVisible=" + bool3 + " waitingVisible=" + bool2 + " finishing=" + localActivityRecord.finishing);
      }
      boolean bool1 = bool2;
      if (bool2)
      {
        bool1 = bool2;
        if (bool3)
        {
          this.mWaitingVisibleActivities.remove(localActivityRecord);
          bool2 = false;
          bool1 = bool2;
          if (localActivityRecord.finishing)
          {
            if (ActivityManagerDebugConfig.DEBUG_STATES) {
              Slog.v(TAG, "Before stopping, can hide: " + localActivityRecord);
            }
            this.mWindowManager.setAppVisibility(localActivityRecord.appToken, false);
            bool1 = bool2;
          }
        }
      }
      Object localObject2;
      if (bool1)
      {
        localObject2 = localObject1;
        if (!this.mService.isSleepingOrShuttingDownLocked()) {}
      }
      else
      {
        localObject2 = localObject1;
        if (paramBoolean)
        {
          if (ActivityManagerDebugConfig.DEBUG_STATES) {
            Slog.v(TAG, "Ready to stop: " + localActivityRecord);
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((ArrayList)localObject2).add(localActivityRecord);
          this.mStoppingActivities.remove(i);
        }
      }
      i -= 1;
      localObject1 = localObject2;
    }
    return (ArrayList<ActivityRecord>)localObject1;
  }
  
  void rankTaskLayersIfNeeded()
  {
    if (!this.mTaskLayersChanged) {
      return;
    }
    this.mTaskLayersChanged = false;
    int i = 0;
    while (i < this.mActivityDisplays.size())
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int k = 0;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        k += ((ActivityStack)localArrayList.get(j)).rankTaskLayers(k);
        j -= 1;
      }
      i += 1;
    }
  }
  
  final boolean realStartActivityLocked(ActivityRecord paramActivityRecord, ProcessRecord paramProcessRecord, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException
  {
    if (!allPausedActivitiesComplete())
    {
      if ((ActivityManagerDebugConfig.DEBUG_SWITCH) || (ActivityManagerDebugConfig.DEBUG_PAUSE) || (ActivityManagerDebugConfig.DEBUG_STATES)) {
        Slog.v(TAG_PAUSE, "realStartActivityLocked: Skipping start of r=" + paramActivityRecord + " some activities pausing...");
      }
      return false;
    }
    if (paramBoolean1)
    {
      paramActivityRecord.startFreezingScreenLocked(paramProcessRecord, 0);
      this.mWindowManager.setAppVisibility(paramActivityRecord.appToken, true);
      this.mService.updateProcessLaunchTime(paramActivityRecord.packageName, Long.valueOf(System.currentTimeMillis()));
      paramActivityRecord.startLaunchTickingLocked();
    }
    if (paramBoolean2)
    {
      localObject4 = this.mWindowManager;
      localObject5 = this.mService.mConfiguration;
      if (!paramActivityRecord.mayFreezeScreenLocked(paramProcessRecord)) {
        break label394;
      }
    }
    Object localObject7;
    ActivityStack localActivityStack;
    label394:
    for (Object localObject1 = paramActivityRecord.appToken;; localObject2 = null)
    {
      localObject1 = ((WindowManagerService)localObject4).updateOrientationFromAppTokens((Configuration)localObject5, (IBinder)localObject1);
      this.mService.updateConfigurationLocked((Configuration)localObject1, paramActivityRecord, false, true);
      paramActivityRecord.app = paramProcessRecord;
      paramProcessRecord.waitingToKill = null;
      paramActivityRecord.launchCount += 1;
      paramActivityRecord.lastLaunchTime = SystemClock.uptimeMillis();
      if (ActivityManagerDebugConfig.DEBUG_ALL) {
        Slog.v(TAG, "Launching: " + paramActivityRecord);
      }
      if (paramProcessRecord.activities.indexOf(paramActivityRecord) < 0) {
        paramProcessRecord.activities.add(paramActivityRecord);
      }
      this.mService.updateLruProcessLocked(paramProcessRecord, true, null);
      this.mService.updateOomAdjLocked();
      localObject7 = paramActivityRecord.task;
      if ((((TaskRecord)localObject7).mLockTaskAuth == 2) || (((TaskRecord)localObject7).mLockTaskAuth == 4)) {
        setLockTaskModeLocked((TaskRecord)localObject7, 1, "mLockTaskAuth==LAUNCHABLE", false);
      }
      localActivityStack = ((TaskRecord)localObject7).stack;
      try
      {
        if (paramProcessRecord.thread != null) {
          break;
        }
        throw new RemoteException();
      }
      catch (RemoteException localRemoteException)
      {
        if (!paramActivityRecord.launchFailed) {
          break label1241;
        }
      }
      Slog.e(TAG, "Second failure launching " + paramActivityRecord.intent.getComponent().flattenToShortString() + ", giving up", localRemoteException);
      this.mService.appDiedLocked(paramProcessRecord);
      localActivityStack.requestFinishActivityLocked(paramActivityRecord.appToken, 0, null, "2nd-crash", false);
      return false;
    }
    Object localObject4 = null;
    Object localObject5 = null;
    if (paramBoolean1)
    {
      localObject4 = paramActivityRecord.results;
      localObject5 = paramActivityRecord.newIntents;
    }
    if (ActivityManagerDebugConfig.DEBUG_SWITCH) {
      Slog.v(TAG_SWITCH, "Launching: " + paramActivityRecord + " icicle=" + paramActivityRecord.icicle + " with results=" + localObject4 + " newIntents=" + localObject5 + " andResume=" + paramBoolean1);
    }
    if (paramBoolean1) {
      EventLog.writeEvent(30006, new Object[] { Integer.valueOf(paramActivityRecord.userId), Integer.valueOf(System.identityHashCode(paramActivityRecord)), Integer.valueOf(((TaskRecord)localObject7).taskId), paramActivityRecord.shortComponentName });
    }
    if (paramActivityRecord.isHomeActivity()) {
      this.mService.mHomeProcess = ((ActivityRecord)((TaskRecord)localObject7).mActivities.get(0)).app;
    }
    this.mService.notifyPackageUse(paramActivityRecord.intent.getComponent().getPackageName(), 0);
    paramActivityRecord.sleeping = false;
    paramActivityRecord.forceNewConfig = false;
    this.mService.showUnsupportedZoomDialogIfNeededLocked(paramActivityRecord);
    this.mService.showAskCompatModeDialogLocked(paramActivityRecord);
    paramActivityRecord.compat = this.mService.compatibilityInfoForPackageLocked(paramActivityRecord.info.applicationInfo);
    Object localObject6 = null;
    Object localObject2 = localObject6;
    Object localObject8;
    if (this.mService.mProfileApp != null)
    {
      localObject2 = localObject6;
      if (this.mService.mProfileApp.equals(paramProcessRecord.processName)) {
        if (this.mService.mProfileProc != null)
        {
          localObject2 = localObject6;
          if (this.mService.mProfileProc != paramProcessRecord) {}
        }
        else
        {
          this.mService.mProfileProc = paramProcessRecord;
          localObject8 = this.mService.mProfileFile;
          localObject2 = localObject6;
          if (localObject8 != null)
          {
            localObject6 = this.mService.mProfileFd;
            localObject2 = localObject6;
            if (localObject6 == null) {}
          }
        }
      }
    }
    for (;;)
    {
      try
      {
        localObject2 = ((ParcelFileDescriptor)localObject6).dup();
        localObject2 = new ProfilerInfo((String)localObject8, (ParcelFileDescriptor)localObject2, this.mService.mSamplingInterval, this.mService.mAutoStopProfiler);
        if (paramBoolean1)
        {
          paramProcessRecord.hasShownUi = true;
          paramProcessRecord.pendingUiClean = true;
        }
        paramProcessRecord.forceProcessStateUpTo(this.mService.mTopProcessState);
        acquireAppLaunchPerfLock(paramActivityRecord.packageName, 200);
        localObject6 = paramProcessRecord.thread;
        localObject8 = new Intent(paramActivityRecord.intent);
        IApplicationToken.Stub localStub = paramActivityRecord.appToken;
        int i = System.identityHashCode(paramActivityRecord);
        ActivityInfo localActivityInfo = paramActivityRecord.info;
        Configuration localConfiguration1 = new Configuration(this.mService.mConfiguration);
        Configuration localConfiguration2 = new Configuration(((TaskRecord)localObject7).mOverrideConfig);
        CompatibilityInfo localCompatibilityInfo = paramActivityRecord.compat;
        String str = paramActivityRecord.launchedFromPackage;
        localObject7 = ((TaskRecord)localObject7).voiceInteractor;
        int j = paramProcessRecord.repProcState;
        Bundle localBundle = paramActivityRecord.icicle;
        PersistableBundle localPersistableBundle = paramActivityRecord.persistentState;
        if (paramBoolean1)
        {
          paramBoolean2 = false;
          ((IApplicationThread)localObject6).scheduleLaunchActivity((Intent)localObject8, localStub, i, localActivityInfo, localConfiguration1, localConfiguration2, localCompatibilityInfo, str, (IVoiceInteractor)localObject7, j, localBundle, localPersistableBundle, (List)localObject4, (List)localObject5, paramBoolean2, this.mService.isNextTransitionForward(), (ProfilerInfo)localObject2);
          if (((paramProcessRecord.info.privateFlags & 0x2) != 0) && (paramProcessRecord.processName.equals(paramProcessRecord.info.packageName)))
          {
            if ((this.mService.mHeavyWeightProcess != null) && (this.mService.mHeavyWeightProcess != paramProcessRecord)) {
              Slog.w(TAG, "Starting new heavy weight process " + paramProcessRecord + " when already running " + this.mService.mHeavyWeightProcess);
            }
            this.mService.mHeavyWeightProcess = paramProcessRecord;
            localObject2 = this.mService.mHandler.obtainMessage(24);
            ((Message)localObject2).obj = paramActivityRecord;
            this.mService.mHandler.sendMessage((Message)localObject2);
          }
          paramActivityRecord.launchFailed = false;
          if (localActivityStack.updateLRUListLocked(paramActivityRecord)) {
            Slog.w(TAG, "Activity " + paramActivityRecord + " being launched, but already in LRU list");
          }
          if (!paramBoolean1) {
            break label1253;
          }
          localActivityStack.minimalResumeActivityLocked(paramActivityRecord);
          if (isFocusedStack(localActivityStack)) {
            this.mService.startSetupActivityLocked();
          }
          if (paramActivityRecord.app != null) {
            this.mService.mServices.updateServiceConnectionActivitiesLocked(paramActivityRecord.app);
          }
          return true;
        }
      }
      catch (IOException localIOException1)
      {
        Object localObject3 = localObject6;
        if (localObject6 == null) {
          continue;
        }
        try
        {
          ((ParcelFileDescriptor)localObject6).close();
          localObject3 = null;
        }
        catch (IOException localIOException2)
        {
          continue;
        }
        paramBoolean2 = true;
        continue;
      }
      label1241:
      paramProcessRecord.activities.remove(paramActivityRecord);
      throw localIOException2;
      label1253:
      if (ActivityManagerDebugConfig.DEBUG_STATES) {
        Slog.v(TAG_STATES, "Moving to PAUSED: " + paramActivityRecord + " (starting in paused state)");
      }
      paramActivityRecord.state = ActivityStack.ActivityState.PAUSED;
    }
  }
  
  void releaseSomeActivitiesLocked(ProcessRecord paramProcessRecord, String paramString)
  {
    Object localObject3 = null;
    Object localObject1 = null;
    if (ActivityManagerDebugConfig.DEBUG_RELEASE) {
      Slog.d(TAG_RELEASE, "Trying to release some activities in " + paramProcessRecord);
    }
    int i = 0;
    label165:
    Object localObject2;
    if (i < paramProcessRecord.activities.size())
    {
      ActivityRecord localActivityRecord = (ActivityRecord)paramProcessRecord.activities.get(i);
      if ((localActivityRecord.finishing) || (localActivityRecord.state == ActivityStack.ActivityState.DESTROYING)) {}
      while (localActivityRecord.state == ActivityStack.ActivityState.DESTROYED)
      {
        if (ActivityManagerDebugConfig.DEBUG_RELEASE) {
          Slog.d(TAG_RELEASE, "Abort release; already destroying: " + localActivityRecord);
        }
        return;
      }
      Object localObject4;
      if ((localActivityRecord.visible) || (!localActivityRecord.stopped) || (!localActivityRecord.haveState) || (localActivityRecord.state == ActivityStack.ActivityState.RESUMED))
      {
        localObject4 = localObject3;
        localObject2 = localObject1;
        if (ActivityManagerDebugConfig.DEBUG_RELEASE)
        {
          Slog.d(TAG_RELEASE, "Not releasing in-use activity: " + localActivityRecord);
          localObject2 = localObject1;
          localObject4 = localObject3;
        }
      }
      for (;;)
      {
        i += 1;
        localObject3 = localObject4;
        localObject1 = localObject2;
        break;
        if ((localActivityRecord.state == ActivityStack.ActivityState.PAUSING) || (localActivityRecord.state == ActivityStack.ActivityState.PAUSED) || (localActivityRecord.state == ActivityStack.ActivityState.STOPPING)) {
          break label165;
        }
        localObject4 = localObject3;
        localObject2 = localObject1;
        if (localActivityRecord.task != null)
        {
          if (ActivityManagerDebugConfig.DEBUG_RELEASE) {
            Slog.d(TAG_RELEASE, "Collecting release task " + localActivityRecord.task + " from " + localActivityRecord);
          }
          if (localObject3 == null)
          {
            localObject4 = localActivityRecord.task;
            localObject2 = localObject1;
          }
          else
          {
            localObject4 = localObject3;
            localObject2 = localObject1;
            if (localObject3 != localActivityRecord.task)
            {
              localObject2 = localObject1;
              if (localObject1 == null)
              {
                localObject2 = new ArraySet();
                ((ArraySet)localObject2).add(localObject3);
              }
              ((ArraySet)localObject2).add(localActivityRecord.task);
              localObject4 = localObject3;
            }
          }
        }
      }
    }
    if (localObject1 == null)
    {
      if (ActivityManagerDebugConfig.DEBUG_RELEASE) {
        Slog.d(TAG_RELEASE, "Didn't find two or more tasks to release");
      }
      return;
    }
    int k = this.mActivityDisplays.size();
    i = 0;
    while (i < k)
    {
      localObject2 = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = 0;
      while (j < ((ArrayList)localObject2).size())
      {
        if (((ActivityStack)((ArrayList)localObject2).get(j)).releaseSomeActivitiesLocked(paramProcessRecord, (ArraySet)localObject1, paramString) > 0) {
          return;
        }
        j += 1;
      }
      i += 1;
    }
  }
  
  void removeChildActivityContainers(ActivityRecord paramActivityRecord)
  {
    paramActivityRecord = paramActivityRecord.mChildContainers;
    int i = paramActivityRecord.size() - 1;
    while (i >= 0)
    {
      ActivityContainer localActivityContainer = (ActivityContainer)paramActivityRecord.remove(i);
      if (ActivityManagerDebugConfig.DEBUG_CONTAINERS) {
        Slog.d(TAG_CONTAINERS, "removeChildActivityContainers: removing " + localActivityContainer);
      }
      localActivityContainer.release();
      i -= 1;
    }
  }
  
  void removeLockedTaskLocked(TaskRecord paramTaskRecord)
  {
    if (!this.mLockTaskModeTasks.remove(paramTaskRecord)) {
      return;
    }
    if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
      Slog.w(TAG_LOCKTASK, "removeLockedTaskLocked: removed " + paramTaskRecord);
    }
    if (this.mLockTaskModeTasks.isEmpty())
    {
      if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
        Slog.d(TAG_LOCKTASK, "removeLockedTask: task=" + paramTaskRecord + " last task, reverting locktask mode. Callers=" + Debug.getCallers(3));
      }
      Message localMessage = Message.obtain();
      localMessage.arg1 = paramTaskRecord.userId;
      localMessage.what = 110;
      this.mHandler.sendMessage(localMessage);
    }
  }
  
  void removeSleepTimeouts()
  {
    this.mSleepTimeout = false;
    this.mHandler.removeMessages(103);
  }
  
  void removeTimeoutsForActivityLocked(ActivityRecord paramActivityRecord)
  {
    if (ActivityManagerDebugConfig.DEBUG_IDLE) {
      Slog.d(TAG_IDLE, "removeTimeoutsForActivity: Callers=" + Debug.getCallers(4));
    }
    this.mHandler.removeMessages(100, paramActivityRecord);
  }
  
  void removeUserLocked(int paramInt)
  {
    this.mUserStackInFront.delete(paramInt);
  }
  
  void reportActivityLaunchedLocked(boolean paramBoolean, ActivityRecord paramActivityRecord, long paramLong1, long paramLong2)
  {
    int j = 0;
    int i = this.mWaitingActivityLaunched.size() - 1;
    while (i >= 0)
    {
      IActivityManager.WaitResult localWaitResult = (IActivityManager.WaitResult)this.mWaitingActivityLaunched.remove(i);
      if (localWaitResult.who == null)
      {
        j = 1;
        localWaitResult.timeout = paramBoolean;
        if (paramActivityRecord != null) {
          localWaitResult.who = new ComponentName(paramActivityRecord.info.packageName, paramActivityRecord.info.name);
        }
        localWaitResult.thisTime = paramLong1;
        localWaitResult.totalTime = paramLong2;
      }
      i -= 1;
    }
    if (j != 0) {
      this.mService.notifyAll();
    }
  }
  
  void reportActivityVisibleLocked(ActivityRecord paramActivityRecord)
  {
    sendWaitingVisibleReportLocked(paramActivityRecord);
  }
  
  boolean reportResumedActivityLocked(ActivityRecord paramActivityRecord)
  {
    if (isFocusedStack(paramActivityRecord.task.stack)) {
      this.mService.updateUsageStats(paramActivityRecord, true);
    }
    if (allResumedActivitiesComplete())
    {
      ensureActivitiesVisibleLocked(null, 0, false);
      this.mWindowManager.executeAppTransition();
      return true;
    }
    return false;
  }
  
  void reportTaskToFrontNoLaunch(ActivityRecord paramActivityRecord)
  {
    int j = 0;
    int i = this.mWaitingActivityLaunched.size() - 1;
    while (i >= 0)
    {
      paramActivityRecord = (IActivityManager.WaitResult)this.mWaitingActivityLaunched.remove(i);
      if (paramActivityRecord.who == null)
      {
        j = 1;
        paramActivityRecord.result = 2;
      }
      i -= 1;
    }
    if (j != 0) {
      this.mService.notifyAll();
    }
  }
  
  boolean requestVisibleBehindLocked(ActivityRecord paramActivityRecord, boolean paramBoolean)
  {
    ActivityRecord localActivityRecord1 = null;
    ActivityStack localActivityStack = paramActivityRecord.task.stack;
    if (localActivityStack == null)
    {
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind: r=" + paramActivityRecord + " visible=" + paramBoolean + " stack is null");
      }
      return false;
    }
    boolean bool;
    ActivityRecord localActivityRecord2;
    if ((!paramBoolean) || (ActivityManager.StackId.activitiesCanRequestVisibleBehind(localActivityStack.mStackId)))
    {
      bool = localActivityStack.hasVisibleBehindActivity();
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind r=" + paramActivityRecord + " visible=" + paramBoolean + " isVisible=" + bool);
      }
      localActivityRecord2 = topRunningActivityLocked();
      if ((localActivityRecord2 != null) && (localActivityRecord2 != paramActivityRecord)) {
        break label252;
      }
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind: quick return");
      }
      if (!paramBoolean) {
        break label353;
      }
    }
    for (;;)
    {
      localActivityStack.setVisibleBehindActivity(paramActivityRecord);
      return true;
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind: r=" + paramActivityRecord + " visible=" + paramBoolean + " stackId=" + localActivityStack.mStackId + " can't contain visible behind activities");
      }
      return false;
      label252:
      if (paramBoolean == bool) {
        break;
      }
      if ((!paramBoolean) || (!localActivityRecord2.fullscreen)) {
        break label358;
      }
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind: returning top.fullscreen=" + localActivityRecord2.fullscreen + " top.state=" + localActivityRecord2.state + " top.app=" + localActivityRecord2.app + " top.app.thread=" + localActivityRecord2.app.thread);
      }
      return false;
      label353:
      paramActivityRecord = null;
    }
    label358:
    if ((!paramBoolean) && (localActivityStack.getVisibleBehindActivity() != paramActivityRecord))
    {
      if (ActivityManagerDebugConfig.DEBUG_VISIBLE_BEHIND) {
        Slog.d(TAG_VISIBLE_BEHIND, "requestVisibleBehind: returning visible=" + paramBoolean + " stack.getVisibleBehindActivity()=" + localActivityStack.getVisibleBehindActivity() + " r=" + paramActivityRecord);
      }
      return false;
    }
    if (paramBoolean) {
      localActivityRecord1 = paramActivityRecord;
    }
    localActivityStack.setVisibleBehindActivity(localActivityRecord1);
    if (!paramBoolean)
    {
      paramActivityRecord = localActivityStack.findNextTranslucentActivity(paramActivityRecord);
      if ((paramActivityRecord != null) && (paramActivityRecord.isHomeActivity())) {
        this.mService.convertFromTranslucent(paramActivityRecord.appToken);
      }
    }
    if ((localActivityRecord2.app != null) && (localActivityRecord2.app.thread != null)) {}
    try
    {
      localActivityRecord2.app.thread.scheduleBackgroundVisibleBehindChanged(localActivityRecord2.appToken, paramBoolean);
      return true;
    }
    catch (RemoteException paramActivityRecord) {}
    return true;
  }
  
  void resizeDockedStackLocked(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, boolean paramBoolean)
  {
    resizeDockedStackLocked(paramRect1, paramRect2, paramRect3, paramRect4, paramRect5, paramBoolean, false);
  }
  
  void resizeDockedStackLocked(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!this.mAllowDockedStackResize) {
      return;
    }
    ActivityStack localActivityStack = getStack(3);
    if (localActivityStack == null)
    {
      Slog.w(TAG, "resizeDockedStackLocked: docked stack not found");
      return;
    }
    Trace.traceBegin(64L, "am.resizeDockedStack");
    this.mWindowManager.deferSurfaceLayout();
    for (;;)
    {
      int i;
      try
      {
        this.mAllowDockedStackResize = false;
        ActivityRecord localActivityRecord = localActivityStack.topRunningActivityLocked();
        resizeStackUncheckedLocked(localActivityStack, paramRect1, paramRect2, paramRect3);
        if ((!localActivityStack.mFullscreen) && ((paramRect1 != null) || (localActivityStack.isAttached())))
        {
          this.mWindowManager.getStackDockedModeBounds(0, this.tempRect, true);
          i = 0;
          localObject = localActivityRecord;
          if (i <= 4)
          {
            if ((!ActivityManager.StackId.isResizeableByDockedStack(i)) || (getStack(i) == null)) {
              break label263;
            }
            resizeStackLocked(i, this.tempRect, paramRect4, paramRect5, paramBoolean1, true, paramBoolean2);
            break label263;
          }
        }
        else
        {
          moveTasksToFullscreenStackLocked(3, true);
          localObject = null;
        }
        if (!paramBoolean2) {
          localActivityStack.ensureVisibleActivitiesConfigurationLocked((ActivityRecord)localObject, paramBoolean1);
        }
        this.mAllowDockedStackResize = true;
        this.mWindowManager.continueSurfaceLayout();
        Trace.traceEnd(64L);
        Object localObject = this.mResizeDockedStackTimeout;
        if ((paramRect2 != null) || (paramRect3 != null))
        {
          paramBoolean1 = true;
          ((ResizeDockedStackTimeout)localObject).notifyResizing(paramRect1, paramBoolean1);
          return;
        }
      }
      finally
      {
        this.mAllowDockedStackResize = true;
        this.mWindowManager.continueSurfaceLayout();
        Trace.traceEnd(64L);
      }
      if (paramRect4 == null) {
        if (paramRect5 != null)
        {
          paramBoolean1 = true;
        }
        else
        {
          paramBoolean1 = false;
          continue;
          label263:
          i += 1;
        }
      }
    }
  }
  
  void resizePinnedStackLocked(Rect paramRect1, Rect paramRect2)
  {
    ActivityStack localActivityStack = getStack(4);
    if (localActivityStack == null)
    {
      Slog.w(TAG, "resizePinnedStackLocked: pinned stack not found");
      return;
    }
    Trace.traceBegin(64L, "am.resizePinnedStack");
    this.mWindowManager.deferSurfaceLayout();
    try
    {
      ActivityRecord localActivityRecord = localActivityStack.topRunningActivityLocked();
      resizeStackUncheckedLocked(localActivityStack, paramRect1, paramRect2, null);
      localActivityStack.ensureVisibleActivitiesConfigurationLocked(localActivityRecord, false);
      return;
    }
    finally
    {
      this.mWindowManager.continueSurfaceLayout();
      Trace.traceEnd(64L);
    }
  }
  
  void resizeStackLocked(int paramInt, Rect paramRect1, Rect paramRect2, Rect paramRect3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramInt == 3)
    {
      resizeDockedStackLocked(paramRect1, paramRect2, paramRect3, null, null, paramBoolean1, paramBoolean3);
      return;
    }
    ActivityStack localActivityStack = getStack(paramInt);
    if (localActivityStack == null)
    {
      Slog.w(TAG, "resizeStack: stackId " + paramInt + " not found.");
      return;
    }
    if ((!paramBoolean2) && (getStack(3) != null)) {
      return;
    }
    Trace.traceBegin(64L, "am.resizeStack_" + paramInt);
    this.mWindowManager.deferSurfaceLayout();
    try
    {
      resizeStackUncheckedLocked(localActivityStack, paramRect1, paramRect2, paramRect3);
      if (!paramBoolean3) {
        localActivityStack.ensureVisibleActivitiesConfigurationLocked(localActivityStack.topRunningActivityLocked(), paramBoolean1);
      }
      return;
    }
    finally
    {
      this.mWindowManager.continueSurfaceLayout();
      Trace.traceEnd(64L);
    }
  }
  
  void resizeStackUncheckedLocked(ActivityStack paramActivityStack, Rect paramRect1, Rect paramRect2, Rect paramRect3)
  {
    Rect localRect = TaskRecord.validateBounds(paramRect1);
    if (!paramActivityStack.updateBoundsAllowed(localRect, paramRect2, paramRect3)) {
      return;
    }
    this.mTmpBounds.clear();
    this.mTmpConfigs.clear();
    this.mTmpInsetBounds.clear();
    ArrayList localArrayList = paramActivityStack.getAllTasks();
    label60:
    int i;
    label69:
    TaskRecord localTaskRecord;
    if (paramRect2 != null)
    {
      paramRect1 = paramRect2;
      if (paramRect3 == null) {
        break label201;
      }
      paramRect2 = paramRect3;
      i = localArrayList.size() - 1;
      if (i < 0) {
        break label217;
      }
      localTaskRecord = (TaskRecord)localArrayList.get(i);
      if (localTaskRecord.isResizeable())
      {
        if (paramActivityStack.mStackId != 2) {
          break label206;
        }
        this.tempRect2.set(localTaskRecord.mBounds);
        fitWithinBounds(this.tempRect2, localRect);
        localTaskRecord.updateOverrideConfiguration(this.tempRect2);
      }
    }
    for (;;)
    {
      this.mTmpConfigs.put(localTaskRecord.taskId, localTaskRecord.mOverrideConfig);
      this.mTmpBounds.put(localTaskRecord.taskId, localTaskRecord.mBounds);
      if (paramRect3 != null) {
        this.mTmpInsetBounds.put(localTaskRecord.taskId, paramRect3);
      }
      i -= 1;
      break label69;
      paramRect1 = localRect;
      break;
      label201:
      paramRect2 = paramRect1;
      break label60;
      label206:
      localTaskRecord.updateOverrideConfiguration(paramRect1, paramRect2);
    }
    label217:
    this.mWindowManager.prepareFreezingTaskBounds(paramActivityStack.mStackId);
    paramActivityStack.mFullscreen = this.mWindowManager.resizeStack(paramActivityStack.mStackId, localRect, this.mTmpConfigs, this.mTmpBounds, this.mTmpInsetBounds);
    paramActivityStack.setBounds(localRect);
  }
  
  boolean resizeTaskLocked(TaskRecord paramTaskRecord, Rect paramRect, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramTaskRecord.isResizeable())
    {
      Slog.w(TAG, "resizeTask: task " + paramTaskRecord + " not resizeable.");
      return true;
    }
    if ((paramInt & 0x2) != 0) {}
    for (boolean bool1 = true; (!Objects.equals(paramTaskRecord.mBounds, paramRect)) || (bool1); bool1 = false)
    {
      paramRect = TaskRecord.validateBounds(paramRect);
      if (this.mWindowManager.isValidTaskId(paramTaskRecord.taskId)) {
        break label127;
      }
      paramTaskRecord.updateOverrideConfiguration(paramRect);
      if ((paramTaskRecord.stack != null) && (paramTaskRecord.stack.mStackId != 2)) {
        restoreRecentTaskLocked(paramTaskRecord, 2);
      }
      return true;
    }
    return true;
    label127:
    Trace.traceBegin(64L, "am.resizeTask_" + paramTaskRecord.taskId);
    paramRect = paramTaskRecord.updateOverrideConfiguration(paramRect);
    boolean bool3 = true;
    boolean bool2 = bool3;
    if (paramRect != null)
    {
      paramRect = paramTaskRecord.topRunningActivityLocked();
      bool2 = bool3;
      if (paramRect != null)
      {
        paramBoolean1 = paramTaskRecord.stack.ensureActivityConfigurationLocked(paramRect, 0, paramBoolean1);
        bool2 = paramBoolean1;
        if (!paramBoolean2)
        {
          ensureActivitiesVisibleLocked(paramRect, 0, false);
          bool2 = paramBoolean1;
          if (!paramBoolean1)
          {
            resumeFocusedStackTopActivityLocked();
            bool2 = paramBoolean1;
          }
        }
      }
    }
    this.mWindowManager.resizeTask(paramTaskRecord.taskId, paramTaskRecord.mBounds, paramTaskRecord.mOverrideConfig, bool2, bool1);
    Trace.traceEnd(64L);
    return bool2;
  }
  
  ActivityInfo resolveActivity(Intent paramIntent, ResolveInfo paramResolveInfo, int paramInt, ProfilerInfo paramProfilerInfo)
  {
    ActivityInfo localActivityInfo = null;
    if (paramResolveInfo != null) {
      localActivityInfo = paramResolveInfo.activityInfo;
    }
    if (localActivityInfo != null)
    {
      paramIntent.setComponent(new ComponentName(localActivityInfo.applicationInfo.packageName, localActivityInfo.name));
      if (!localActivityInfo.processName.equals("system"))
      {
        if ((paramInt & 0x2) != 0) {
          this.mService.setDebugApp(localActivityInfo.processName, true, false);
        }
        if ((paramInt & 0x8) != 0) {
          this.mService.setNativeDebuggingAppLocked(localActivityInfo.applicationInfo, localActivityInfo.processName);
        }
        if ((paramInt & 0x4) != 0) {
          this.mService.setTrackAllocationApp(localActivityInfo.applicationInfo, localActivityInfo.processName);
        }
        if (paramProfilerInfo != null) {
          this.mService.setProfileApp(localActivityInfo.applicationInfo, localActivityInfo.processName, paramProfilerInfo);
        }
      }
    }
    return localActivityInfo;
  }
  
  ActivityInfo resolveActivity(Intent paramIntent, String paramString, int paramInt1, ProfilerInfo paramProfilerInfo, int paramInt2)
  {
    return resolveActivity(paramIntent, resolveIntent(paramIntent, paramString, paramInt2), paramInt1, paramProfilerInfo);
  }
  
  ResolveInfo resolveIntent(Intent paramIntent, String paramString, int paramInt)
  {
    return resolveIntent(paramIntent, paramString, paramInt, 0);
  }
  
  ResolveInfo resolveIntent(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramIntent = AppGlobals.getPackageManager().resolveIntent(paramIntent, paramString, 0x10000 | paramInt2 | 0x400, paramInt1);
      return paramIntent;
    }
    catch (RemoteException paramIntent) {}
    return null;
  }
  
  boolean resumeFocusedStackTopActivityLocked()
  {
    return resumeFocusedStackTopActivityLocked(null, null, null);
  }
  
  boolean resumeFocusedStackTopActivityLocked(ActivityStack paramActivityStack, ActivityRecord paramActivityRecord, ActivityOptions paramActivityOptions)
  {
    if ((paramActivityStack != null) && (isFocusedStack(paramActivityStack))) {
      return paramActivityStack.resumeTopActivityUncheckedLocked(paramActivityRecord, paramActivityOptions);
    }
    paramActivityStack = this.mFocusedStack.topRunningActivityLocked();
    if ((paramActivityStack == null) || (paramActivityStack.state != ActivityStack.ActivityState.RESUMED)) {
      this.mFocusedStack.resumeTopActivityUncheckedLocked(null, null);
    }
    return false;
  }
  
  boolean resumeHomeStackTask(int paramInt, ActivityRecord paramActivityRecord, String paramString)
  {
    if ((this.mService.mBooting) || (this.mService.mBooted))
    {
      if (paramInt == 2)
      {
        this.mWindowManager.showRecentApps(false);
        return false;
      }
    }
    else {
      return false;
    }
    if (paramActivityRecord != null) {
      paramActivityRecord.task.setTaskToReturnTo(0);
    }
    this.mHomeStack.moveHomeStackTaskToTop(paramInt);
    ActivityRecord localActivityRecord = getHomeActivity();
    paramString = paramString + " resumeHomeStackTask";
    if ((localActivityRecord == null) || (localActivityRecord.finishing)) {
      return this.mService.startHomeActivityLocked(this.mCurrentUser, paramString);
    }
    this.mService.setFocusedActivityLocked(localActivityRecord, paramString);
    return resumeFocusedStackTopActivityLocked(this.mHomeStack, paramActivityRecord, null);
  }
  
  ActivityRecord resumedAppLocked()
  {
    ActivityStack localActivityStack = this.mFocusedStack;
    if (localActivityStack == null) {
      return null;
    }
    ActivityRecord localActivityRecord2 = localActivityStack.mResumedActivity;
    ActivityRecord localActivityRecord1;
    if (localActivityRecord2 != null)
    {
      localActivityRecord1 = localActivityRecord2;
      if (localActivityRecord2.app != null) {}
    }
    else
    {
      localActivityRecord2 = localActivityStack.mPausingActivity;
      if (localActivityRecord2 != null)
      {
        localActivityRecord1 = localActivityRecord2;
        if (localActivityRecord2.app != null) {}
      }
      else
      {
        localActivityRecord1 = localActivityStack.topRunningActivityLocked();
      }
    }
    return localActivityRecord1;
  }
  
  void scheduleDestroyAllActivities(ProcessRecord paramProcessRecord, String paramString)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int k = localArrayList.size();
      int j = 0;
      while (j < k)
      {
        ((ActivityStack)localArrayList.get(j)).scheduleDestroyActivities(paramProcessRecord, paramString);
        j += 1;
      }
      i -= 1;
    }
  }
  
  final void scheduleIdleLocked()
  {
    this.mHandler.sendEmptyMessage(101);
  }
  
  void scheduleIdleTimeoutLocked(ActivityRecord paramActivityRecord)
  {
    if (ActivityManagerDebugConfig.DEBUG_IDLE) {
      Slog.d(TAG_IDLE, "scheduleIdleTimeoutLocked: Callers=" + Debug.getCallers(4));
    }
    paramActivityRecord = this.mHandler.obtainMessage(100, paramActivityRecord);
    this.mHandler.sendMessageDelayed(paramActivityRecord, 10000L);
  }
  
  void scheduleLaunchTaskBehindComplete(IBinder paramIBinder)
  {
    this.mHandler.obtainMessage(112, paramIBinder).sendToTarget();
  }
  
  void scheduleReportMultiWindowModeChanged(TaskRecord paramTaskRecord)
  {
    int i = paramTaskRecord.mActivities.size() - 1;
    while (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)paramTaskRecord.mActivities.get(i);
      if ((localActivityRecord.app != null) && (localActivityRecord.app.thread != null)) {
        this.mMultiWindowModeChangedActivities.add(localActivityRecord);
      }
      i -= 1;
    }
    if (!this.mHandler.hasMessages(114)) {
      this.mHandler.sendEmptyMessage(114);
    }
  }
  
  void scheduleReportPictureInPictureModeChangedIfNeeded(TaskRecord paramTaskRecord, ActivityStack paramActivityStack)
  {
    ActivityStack localActivityStack = paramTaskRecord.stack;
    if ((paramActivityStack == null) || (paramActivityStack == localActivityStack)) {}
    while ((paramActivityStack.mStackId != 4) && (localActivityStack.mStackId != 4)) {
      return;
    }
    int i = paramTaskRecord.mActivities.size() - 1;
    while (i >= 0)
    {
      paramActivityStack = (ActivityRecord)paramTaskRecord.mActivities.get(i);
      if ((paramActivityStack.app != null) && (paramActivityStack.app.thread != null)) {
        this.mPipModeChangedActivities.add(paramActivityStack);
      }
      i -= 1;
    }
    if (!this.mHandler.hasMessages(115)) {
      this.mHandler.sendEmptyMessage(115);
    }
  }
  
  final void scheduleResumeTopActivities()
  {
    if (!this.mHandler.hasMessages(102)) {
      this.mHandler.sendEmptyMessage(102);
    }
  }
  
  final void scheduleSleepTimeout()
  {
    removeSleepTimeouts();
    this.mHandler.sendEmptyMessageDelayed(103, 5000L);
  }
  
  void sendWaitingVisibleReportLocked(ActivityRecord paramActivityRecord)
  {
    int j = 0;
    int i = this.mWaitingActivityVisible.size() - 1;
    while (i >= 0)
    {
      IActivityManager.WaitResult localWaitResult = (IActivityManager.WaitResult)this.mWaitingActivityVisible.get(i);
      if (localWaitResult.who == null)
      {
        j = 1;
        localWaitResult.timeout = false;
        if (paramActivityRecord != null) {
          localWaitResult.who = new ComponentName(paramActivityRecord.info.packageName, paramActivityRecord.info.name);
        }
        localWaitResult.totalTime = (SystemClock.uptimeMillis() - localWaitResult.thisTime);
        localWaitResult.thisTime = localWaitResult.totalTime;
      }
      i -= 1;
    }
    if (j != 0) {
      this.mService.notifyAll();
    }
  }
  
  void setDockedStackMinimized(boolean paramBoolean)
  {
    this.mIsDockMinimized = paramBoolean;
    if (paramBoolean) {
      return;
    }
    Object localObject = getStack(3);
    if (localObject == null) {
      return;
    }
    localObject = ((ActivityStack)localObject).topRunningActivityLocked();
    if ((localObject != null) && (this.mService.mUserController.shouldConfirmCredentials(((ActivityRecord)localObject).userId))) {
      this.mService.mActivityStarter.showConfirmDeviceCredential(((ActivityRecord)localObject).userId);
    }
  }
  
  void setFocusStackUnchecked(String paramString, ActivityStack paramActivityStack)
  {
    int j = -1;
    ActivityStack localActivityStack = paramActivityStack;
    if (!paramActivityStack.isFocusable()) {
      localActivityStack = paramActivityStack.getNextFocusableStackLocked();
    }
    int i;
    if (localActivityStack != this.mFocusedStack)
    {
      this.mLastFocusedStack = this.mFocusedStack;
      this.mFocusedStack = localActivityStack;
      int k = this.mCurrentUser;
      if (this.mFocusedStack == null)
      {
        i = -1;
        if (this.mLastFocusedStack != null) {
          break label160;
        }
        label64:
        EventLogTags.writeAmFocusedStack(k, i, j, paramString);
      }
    }
    else
    {
      paramActivityStack = topRunningActivityLocked();
      if ((!this.mService.mDoingSetFocusedActivity) && (this.mService.mFocusedActivity != paramActivityStack)) {
        this.mService.setFocusedActivityLocked(paramActivityStack, paramString + " setFocusStack");
      }
      if ((this.mService.mBooting) || (!this.mService.mBooted)) {
        break label172;
      }
    }
    label160:
    label172:
    while ((paramActivityStack == null) || (!paramActivityStack.idle))
    {
      return;
      i = this.mFocusedStack.getStackId();
      break;
      j = this.mLastFocusedStack.getStackId();
      break label64;
    }
    checkFinishBootingLocked();
  }
  
  void setLaunchSource(int paramInt)
  {
    this.mLaunchingActivity.setWorkSource(new WorkSource(paramInt));
  }
  
  void setLockTaskModeLocked(TaskRecord paramTaskRecord, int paramInt, String paramString, boolean paramBoolean)
  {
    boolean bool = true;
    if (paramTaskRecord == null)
    {
      paramTaskRecord = getLockedTaskLocked();
      if (paramTaskRecord != null)
      {
        removeLockedTaskLocked(paramTaskRecord);
        if (!this.mLockTaskModeTasks.isEmpty())
        {
          if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
            Slog.w(TAG_LOCKTASK, "setLockTaskModeLocked: Tasks remaining, can't unlock");
          }
          paramTaskRecord.performClearTaskLocked();
          resumeFocusedStackTopActivityLocked();
          return;
        }
      }
      if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
        Slog.w(TAG_LOCKTASK, "setLockTaskModeLocked: No tasks to unlock. Callers=" + Debug.getCallers(4));
      }
      return;
    }
    if (paramTaskRecord.mLockTaskAuth == 0)
    {
      if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
        Slog.w(TAG_LOCKTASK, "setLockTaskModeLocked: Can't lock due to auth");
      }
      return;
    }
    if (isLockTaskModeViolation(paramTaskRecord))
    {
      Slog.e(TAG_LOCKTASK, "setLockTaskMode: Attempt to start an unauthorized lock task.");
      return;
    }
    if (this.mLockTaskModeTasks.isEmpty())
    {
      Message localMessage = Message.obtain();
      localMessage.obj = paramTaskRecord.intent.getComponent().getPackageName();
      localMessage.arg1 = paramTaskRecord.userId;
      localMessage.what = 109;
      localMessage.arg2 = paramInt;
      this.mHandler.sendMessage(localMessage);
    }
    if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
      Slog.w(TAG_LOCKTASK, "setLockTaskModeLocked: Locking to " + paramTaskRecord + " Callers=" + Debug.getCallers(4));
    }
    this.mLockTaskModeTasks.remove(paramTaskRecord);
    this.mLockTaskModeTasks.add(paramTaskRecord);
    if (paramTaskRecord.mLockTaskUid == -1) {
      paramTaskRecord.mLockTaskUid = paramTaskRecord.effectiveUid;
    }
    if (paramBoolean) {
      if (paramInt != 0)
      {
        paramBoolean = bool;
        findTaskToMoveToFrontLocked(paramTaskRecord, 0, null, paramString, paramBoolean);
        resumeFocusedStackTopActivityLocked();
      }
    }
    while (paramInt == 0) {
      for (;;)
      {
        return;
        paramBoolean = false;
      }
    }
    handleNonResizableTaskIfNeeded(paramTaskRecord, -1, paramTaskRecord.stack.mStackId, true);
  }
  
  void setNextTaskIdForUserLocked(int paramInt1, int paramInt2)
  {
    if (paramInt1 > this.mCurTaskIdForUser.get(paramInt2, -1)) {
      this.mCurTaskIdForUser.put(paramInt2, paramInt1);
    }
  }
  
  void setRecentTasks(RecentTasks paramRecentTasks)
  {
    this.mRecentTasks = paramRecentTasks;
  }
  
  void setWindowManager(WindowManagerService paramWindowManagerService)
  {
    for (;;)
    {
      int i;
      int j;
      ActivityDisplay localActivityDisplay;
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        this.mWindowManager = paramWindowManagerService;
        this.mDisplayManager = ((DisplayManager)this.mService.mContext.getSystemService("display"));
        this.mDisplayManager.registerDisplayListener(this, null);
        paramWindowManagerService = this.mDisplayManager.getDisplays();
        i = paramWindowManagerService.length - 1;
        if (i < 0) {
          break;
        }
        j = paramWindowManagerService[i].getDisplayId();
        localActivityDisplay = new ActivityDisplay(j);
        if (localActivityDisplay.mDisplay == null) {
          throw new IllegalStateException("Default Display does not exist");
        }
      }
      this.mActivityDisplays.put(j, localActivityDisplay);
      calculateDefaultMinimalSizeOfResizeableTasks(localActivityDisplay);
      i -= 1;
    }
    paramWindowManagerService = getStack(0, true, true);
    this.mLastFocusedStack = paramWindowManagerService;
    this.mFocusedStack = paramWindowManagerService;
    this.mHomeStack = paramWindowManagerService;
    this.mInputManagerInternal = ((InputManagerInternal)LocalServices.getService(InputManagerInternal.class));
    ActivityManagerService.resetPriorityAfterLockedSection();
  }
  
  public boolean shouldIgnoreSceneEvaluation(ActivityRecord paramActivityRecord)
  {
    Object localObject2;
    if (this.mIgnoreSceneEvaluationApps == null)
    {
      this.mIgnoreSceneEvaluationApps = new ArrayList();
      localObject1 = new Intent("android.content.pm.action.REQUEST_PERMISSIONS");
      localObject1 = this.mService.mContext.getPackageManager().queryIntentActivities((Intent)localObject1, 1114112);
      if ((localObject1 == null) || (((List)localObject1).isEmpty())) {}
      while (Build.DEBUG_ONEPLUS)
      {
        Slog.d(TAG_STACK, "[scene] scene evaluation ignore list: ");
        if (!this.mIgnoreSceneEvaluationApps.isEmpty())
        {
          localObject1 = this.mIgnoreSceneEvaluationApps.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ComponentName)((Iterator)localObject1).next();
            Slog.d(TAG_STACK, "[scene]    " + localObject2);
          }
          localObject1 = ((Iterable)localObject1).iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ResolveInfo)((Iterator)localObject1).next();
            this.mIgnoreSceneEvaluationApps.add(((ResolveInfo)localObject2).activityInfo.getComponentName());
          }
        }
        else
        {
          Slog.d(TAG_STACK, "[scene]     Empty");
        }
      }
    }
    if (paramActivityRecord == null) {
      return false;
    }
    if (paramActivityRecord.isResolverActivity()) {
      return true;
    }
    Object localObject1 = this.mIgnoreSceneEvaluationApps.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ComponentName)((Iterator)localObject1).next();
      if ((localObject2 != null) && (((ComponentName)localObject2).equals(paramActivityRecord.realActivity))) {
        return true;
      }
    }
    return false;
  }
  
  void showLockTaskEscapeMessageLocked(TaskRecord paramTaskRecord)
  {
    if (this.mLockTaskModeTasks.contains(paramTaskRecord)) {
      this.mHandler.sendEmptyMessage(113);
    }
  }
  
  void showLockTaskToast()
  {
    if (this.mLockTaskNotify != null) {
      this.mLockTaskNotify.showToast(this.mLockTaskModeState);
    }
  }
  
  boolean shutdownLocked(int paramInt)
  {
    goingToSleepLocked();
    boolean bool3 = false;
    long l1 = System.currentTimeMillis();
    long l2 = paramInt;
    for (;;)
    {
      boolean bool1 = false;
      paramInt = this.mActivityDisplays.size() - 1;
      while (paramInt >= 0)
      {
        ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(paramInt)).mStacks;
        int i = localArrayList.size() - 1;
        while (i >= 0)
        {
          bool1 |= ((ActivityStack)localArrayList.get(i)).checkReadyForSleepLocked();
          i -= 1;
        }
        paramInt -= 1;
      }
      bool2 = bool3;
      if (!bool1) {
        break label145;
      }
      long l3 = l1 + l2 - System.currentTimeMillis();
      if (l3 <= 0L) {
        break;
      }
      try
      {
        this.mService.wait(l3);
      }
      catch (InterruptedException localInterruptedException) {}
    }
    Slog.w(TAG, "Activity manager shutdown timed out");
    boolean bool2 = true;
    label145:
    this.mSleepTimeout = true;
    checkReadyForSleepLocked();
    return bool2;
  }
  
  final int startActivityFromRecentsInner(int paramInt, Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      localObject1 = new ActivityOptions(paramBundle);
      if (localObject1 == null) {
        break label69;
      }
    }
    label69:
    for (int i = ((ActivityOptions)localObject1).getLaunchStackId();; i = -1)
    {
      if (i != 0) {
        break label74;
      }
      throw new IllegalArgumentException("startActivityFromRecentsInner: Task " + paramInt + " can't be launch in the home stack.");
      localObject1 = null;
      break;
    }
    label74:
    if (i == 3)
    {
      this.mWindowManager.setDockedStackCreateState(((ActivityOptions)localObject1).getDockCreateMode(), null);
      deferUpdateBounds(0);
      this.mWindowManager.prepareAppTransition(19, false);
    }
    TaskRecord localTaskRecord = anyTaskForIdLocked(paramInt, true, i);
    if (localTaskRecord == null)
    {
      continueUpdateBounds(0);
      this.mWindowManager.executeAppTransition();
      throw new IllegalArgumentException("startActivityFromRecentsInner: Task " + paramInt + " not found.");
    }
    Object localObject1 = getFocusedStack();
    if (localObject1 != null)
    {
      localObject1 = ((ActivityStack)localObject1).topActivity();
      if ((i != -1) && (localTaskRecord.stack.mStackId != i)) {
        moveTaskToStackLocked(paramInt, i, true, true, "startActivityFromRecents", true);
      }
      if ((this.mService.mUserController.shouldConfirmCredentials(localTaskRecord.userId)) || (localTaskRecord.getRootActivity() == null)) {
        break label356;
      }
      this.mService.mActivityStarter.sendPowerHintForLaunchStartIfNeeded(true);
      this.mActivityMetricsLogger.notifyActivityLaunching();
      this.mService.moveTaskToFrontLocked(localTaskRecord.taskId, 0, paramBundle);
      this.mActivityMetricsLogger.notifyActivityLaunched(2, localTaskRecord.getTopActivity());
      if (i == 3) {
        setResizingDuringAnimation(paramInt);
      }
      paramBundle = this.mService.mActivityStarter;
      localObject2 = localTaskRecord.getTopActivity();
      if (localObject1 == null) {
        break label351;
      }
    }
    label351:
    for (paramInt = ((ActivityRecord)localObject1).task.stack.mStackId;; paramInt = -1)
    {
      paramBundle.postStartActivityUncheckedProcessing((ActivityRecord)localObject2, 2, paramInt, (ActivityRecord)localObject1, localTaskRecord.stack);
      return 2;
      localObject1 = null;
      break;
    }
    label356:
    paramInt = localTaskRecord.mCallingUid;
    localObject1 = localTaskRecord.mCallingPackage;
    Object localObject2 = localTaskRecord.intent;
    ((Intent)localObject2).addFlags(1048576);
    int j = localTaskRecord.userId;
    paramInt = this.mService.startActivityInPackage(paramInt, (String)localObject1, (Intent)localObject2, null, null, null, 0, 0, paramBundle, j, null, localTaskRecord);
    if (i == 3) {
      setResizingDuringAnimation(localTaskRecord.taskId);
    }
    return paramInt;
  }
  
  void startSpecificActivityLocked(ActivityRecord paramActivityRecord, boolean paramBoolean1, boolean paramBoolean2)
  {
    ProcessRecord localProcessRecord = this.mService.getProcessRecordLocked(paramActivityRecord.processName, paramActivityRecord.info.applicationInfo.uid, true);
    paramActivityRecord.task.stack.setLaunchTime(paramActivityRecord);
    if ((localProcessRecord != null) && (localProcessRecord.thread != null)) {
      try
      {
        if (((paramActivityRecord.info.flags & 0x1) != 0) && ("android".equals(paramActivityRecord.info.packageName))) {}
        for (;;)
        {
          realStartActivityLocked(paramActivityRecord, localProcessRecord, paramBoolean1, paramBoolean2);
          return;
          localProcessRecord.addPackage(paramActivityRecord.info.packageName, paramActivityRecord.info.applicationInfo.versionCode, this.mService.mProcessStats);
        }
        this.mService.startProcessLocked(paramActivityRecord.processName, paramActivityRecord.info.applicationInfo, true, 0, "activity", paramActivityRecord.intent.getComponent(), false, false, true);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w(TAG, "Exception when starting activity " + paramActivityRecord.intent.getComponent().flattenToShortString(), localRemoteException);
      }
    }
  }
  
  boolean switchUserLocked(int paramInt, UserState paramUserState)
  {
    int i = this.mFocusedStack.getStackId();
    int k;
    if (i == 3)
    {
      bool = true;
      moveTasksToFullscreenStackLocked(3, bool);
      this.mUserStackInFront.put(this.mCurrentUser, i);
      k = this.mUserStackInFront.get(paramInt, 0);
      this.mCurrentUser = paramInt;
      this.mStartingUsers.add(paramUserState);
      i = this.mActivityDisplays.size() - 1;
    }
    for (;;)
    {
      if (i < 0) {
        break label165;
      }
      paramUserState = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = paramUserState.size() - 1;
      for (;;)
      {
        if (j >= 0)
        {
          localObject = (ActivityStack)paramUserState.get(j);
          ((ActivityStack)localObject).switchUserLocked(paramInt);
          localObject = ((ActivityStack)localObject).topTask();
          if (localObject != null) {
            this.mWindowManager.moveTaskToTop(((TaskRecord)localObject).taskId);
          }
          j -= 1;
          continue;
          bool = false;
          break;
        }
      }
      i -= 1;
    }
    label165:
    Object localObject = getStack(k);
    paramUserState = (UserState)localObject;
    if (localObject == null) {
      paramUserState = this.mHomeStack;
    }
    boolean bool = paramUserState.isHomeStack();
    if (paramUserState.isOnHomeDisplay())
    {
      paramUserState.moveToFront("switchUserOnHomeDisplay");
      return bool;
    }
    resumeHomeStackTask(1, null, "switchUserOnOtherDisplay");
    return bool;
  }
  
  ActivityRecord topRunningActivityLocked()
  {
    ActivityStack localActivityStack = this.mFocusedStack;
    Object localObject1 = localActivityStack.topRunningActivityLocked();
    if (localObject1 != null) {
      return (ActivityRecord)localObject1;
    }
    localObject1 = this.mHomeStack.mStacks;
    int i = ((ArrayList)localObject1).size() - 1;
    while (i >= 0)
    {
      Object localObject2 = (ActivityStack)((ArrayList)localObject1).get(i);
      if ((localObject2 != localActivityStack) && (isFrontStack((ActivityStack)localObject2)) && (((ActivityStack)localObject2).isFocusable()))
      {
        localObject2 = ((ActivityStack)localObject2).topRunningActivityLocked();
        if (localObject2 != null) {
          return (ActivityRecord)localObject2;
        }
      }
      i -= 1;
    }
    return null;
  }
  
  void updateActivityApplicationInfoLocked(ApplicationInfo paramApplicationInfo)
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      while (j >= 0)
      {
        ((ActivityStack)localArrayList.get(j)).updateActivityApplicationInfoLocked(paramApplicationInfo);
        j -= 1;
      }
      i -= 1;
    }
  }
  
  void updatePreviousProcessLocked(ActivityRecord paramActivityRecord)
  {
    Object localObject2 = null;
    int i = this.mActivityDisplays.size() - 1;
    if (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      for (;;)
      {
        Object localObject1 = localObject2;
        ActivityStack localActivityStack;
        if (j >= 0)
        {
          localActivityStack = (ActivityStack)localArrayList.get(j);
          if (!isFocusedStack(localActivityStack)) {
            break label123;
          }
          if (localActivityStack.mResumedActivity == null) {
            break label98;
          }
          localObject1 = localActivityStack.mResumedActivity.app;
        }
        for (;;)
        {
          i -= 1;
          localObject2 = localObject1;
          break;
          label98:
          localObject1 = localObject2;
          if (localActivityStack.mPausingActivity != null) {
            localObject1 = localActivityStack.mPausingActivity.app;
          }
        }
        label123:
        j -= 1;
      }
    }
    if ((paramActivityRecord.app != null) && (localObject2 != null) && (paramActivityRecord.app != localObject2) && (paramActivityRecord.lastVisibleTime > this.mService.mPreviousProcessVisibleTime) && (paramActivityRecord.app != this.mService.mHomeProcess))
    {
      this.mService.mPreviousProcess = paramActivityRecord.app;
      this.mService.mPreviousProcessVisibleTime = paramActivityRecord.lastVisibleTime;
    }
  }
  
  void updateUserStackLocked(int paramInt, ActivityStack paramActivityStack)
  {
    SparseIntArray localSparseIntArray;
    if (paramInt != this.mCurrentUser)
    {
      localSparseIntArray = this.mUserStackInFront;
      if (paramActivityStack == null) {
        break label31;
      }
    }
    label31:
    for (int i = paramActivityStack.getStackId();; i = 0)
    {
      localSparseIntArray.put(paramInt, i);
      return;
    }
  }
  
  void validateTopActivitiesLocked()
  {
    int i = this.mActivityDisplays.size() - 1;
    while (i >= 0)
    {
      ArrayList localArrayList = ((ActivityDisplay)this.mActivityDisplays.valueAt(i)).mStacks;
      int j = localArrayList.size() - 1;
      if (j >= 0)
      {
        Object localObject = (ActivityStack)localArrayList.get(j);
        ActivityRecord localActivityRecord = ((ActivityStack)localObject).topRunningActivityLocked();
        ActivityStack.ActivityState localActivityState;
        if (localActivityRecord == null)
        {
          localActivityState = ActivityStack.ActivityState.DESTROYED;
          label69:
          if (!isFocusedStack((ActivityStack)localObject)) {
            break label239;
          }
          if (localActivityRecord != null) {
            break label127;
          }
          Slog.e(TAG, "validateTop...: null top activity, stack=" + localObject);
        }
        for (;;)
        {
          j -= 1;
          break;
          localActivityState = localActivityRecord.state;
          break label69;
          label127:
          localObject = ((ActivityStack)localObject).mPausingActivity;
          if ((localObject != null) && (localObject == localActivityRecord)) {
            Slog.e(TAG, "validateTop...: top stack has pausing activity r=" + localActivityRecord + " state=" + localActivityState);
          }
          if ((localActivityState != ActivityStack.ActivityState.INITIALIZING) && (localActivityState != ActivityStack.ActivityState.RESUMED))
          {
            Slog.e(TAG, "validateTop...: activity in front not resumed r=" + localActivityRecord + " state=" + localActivityState);
            continue;
            label239:
            localObject = ((ActivityStack)localObject).mResumedActivity;
            if ((localObject != null) && (localObject == localActivityRecord)) {
              Slog.e(TAG, "validateTop...: back stack has resumed activity r=" + localActivityRecord + " state=" + localActivityState);
            }
            if ((localActivityRecord != null) && ((localActivityState == ActivityStack.ActivityState.INITIALIZING) || (localActivityState == ActivityStack.ActivityState.RESUMED))) {
              Slog.e(TAG, "validateTop...: activity in back resumed r=" + localActivityRecord + " state=" + localActivityState);
            }
          }
        }
      }
      i -= 1;
    }
  }
  
  class ActivityContainer
    extends IActivityContainer.Stub
  {
    static final int CONTAINER_STATE_FINISHING = 2;
    static final int CONTAINER_STATE_HAS_SURFACE = 0;
    static final int CONTAINER_STATE_NO_SURFACE = 1;
    static final int FORCE_NEW_TASK_FLAGS = 402718720;
    ActivityStackSupervisor.ActivityDisplay mActivityDisplay;
    IActivityContainerCallback mCallback = null;
    int mContainerState = 0;
    String mIdString;
    ActivityRecord mParentActivity = null;
    final ActivityStack mStack;
    final int mStackId;
    boolean mVisible = true;
    
    ActivityContainer(int paramInt)
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        this.mStackId = paramInt;
        this.mStack = new ActivityStack(this, ActivityStackSupervisor.-get6(ActivityStackSupervisor.this));
        this.mIdString = ("ActivtyContainer{" + this.mStackId + "}");
        if (ActivityManagerDebugConfig.DEBUG_STACK) {
          Slog.d(ActivityStackSupervisor.-get2(), "Creating " + this);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public void attachToDisplay(int paramInt)
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityStackSupervisor.ActivityDisplay localActivityDisplay = (ActivityStackSupervisor.ActivityDisplay)ActivityStackSupervisor.-get3(ActivityStackSupervisor.this).get(paramInt);
        if (localActivityDisplay == null)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        attachToDisplayLocked(localActivityDisplay, true);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    void attachToDisplayLocked(ActivityStackSupervisor.ActivityDisplay paramActivityDisplay, boolean paramBoolean)
    {
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.d(ActivityStackSupervisor.-get2(), "attachToDisplayLocked: " + this + " to display=" + paramActivityDisplay + " onTop=" + paramBoolean);
      }
      this.mActivityDisplay = paramActivityDisplay;
      this.mStack.attachDisplay(paramActivityDisplay, paramBoolean);
      paramActivityDisplay.attachActivities(this.mStack, paramBoolean);
    }
    
    void checkEmbeddedAllowedInner(int paramInt, Intent paramIntent, String paramString)
    {
      paramIntent = ActivityStackSupervisor.this.resolveActivity(paramIntent, paramString, 0, null, paramInt);
      if ((paramIntent != null) && ((paramIntent.flags & 0x80000000) == 0)) {
        throw new SecurityException("Attempt to embed activity that has not set allowEmbedded=\"true\"");
      }
    }
    
    protected void detachLocked()
    {
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.d(ActivityStackSupervisor.-get2(), "detachLocked: " + this + " from display=" + this.mActivityDisplay + " Callers=" + Debug.getCallers(2));
      }
      if (this.mActivityDisplay != null)
      {
        this.mActivityDisplay.detachActivitiesLocked(this.mStack);
        this.mActivityDisplay = null;
        this.mStack.detachDisplay();
      }
    }
    
    public int getDisplayId()
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        if (this.mActivityDisplay != null)
        {
          int i = this.mActivityDisplay.mDisplayId;
          ActivityManagerService.resetPriorityAfterLockedSection();
          return i;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return -1;
      }
    }
    
    ActivityStackSupervisor getOuter()
    {
      return ActivityStackSupervisor.this;
    }
    
    public int getStackId()
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int i = this.mStackId;
        ActivityManagerService.resetPriorityAfterLockedSection();
        return i;
      }
    }
    
    /* Error */
    public boolean injectEvent(android.view.InputEvent paramInputEvent)
    {
      // Byte code:
      //   0: invokestatic 205	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_2
      //   4: aload_0
      //   5: getfield 36	com/android/server/am/ActivityStackSupervisor$ActivityContainer:this$0	Lcom/android/server/am/ActivityStackSupervisor;
      //   8: getfield 51	com/android/server/am/ActivityStackSupervisor:mService	Lcom/android/server/am/ActivityManagerService;
      //   11: astore 5
      //   13: aload 5
      //   15: monitorenter
      //   16: invokestatic 56	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
      //   19: aload_0
      //   20: getfield 143	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mActivityDisplay	Lcom/android/server/am/ActivityStackSupervisor$ActivityDisplay;
      //   23: ifnull +37 -> 60
      //   26: aload_0
      //   27: getfield 36	com/android/server/am/ActivityStackSupervisor$ActivityContainer:this$0	Lcom/android/server/am/ActivityStackSupervisor;
      //   30: getfield 209	com/android/server/am/ActivityStackSupervisor:mInputManagerInternal	Landroid/hardware/input/InputManagerInternal;
      //   33: aload_1
      //   34: aload_0
      //   35: getfield 143	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mActivityDisplay	Lcom/android/server/am/ActivityStackSupervisor$ActivityDisplay;
      //   38: getfield 194	com/android/server/am/ActivityStackSupervisor$ActivityDisplay:mDisplayId	I
      //   41: iconst_0
      //   42: invokevirtual 215	android/hardware/input/InputManagerInternal:injectInputEvent	(Landroid/view/InputEvent;II)Z
      //   45: istore 4
      //   47: aload 5
      //   49: monitorexit
      //   50: invokestatic 111	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   53: lload_2
      //   54: invokestatic 219	android/os/Binder:restoreCallingIdentity	(J)V
      //   57: iload 4
      //   59: ireturn
      //   60: aload 5
      //   62: monitorexit
      //   63: invokestatic 111	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   66: lload_2
      //   67: invokestatic 219	android/os/Binder:restoreCallingIdentity	(J)V
      //   70: iconst_0
      //   71: ireturn
      //   72: astore_1
      //   73: aload 5
      //   75: monitorexit
      //   76: invokestatic 111	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload_2
      //   83: invokestatic 219	android/os/Binder:restoreCallingIdentity	(J)V
      //   86: aload_1
      //   87: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	88	0	this	ActivityContainer
      //   0	88	1	paramInputEvent	android.view.InputEvent
      //   3	80	2	l	long
      //   45	13	4	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   16	47	72	finally
      //   4	16	81	finally
      //   47	53	81	finally
      //   60	66	81	finally
      //   73	81	81	finally
    }
    
    boolean isAttachedLocked()
    {
      return this.mActivityDisplay != null;
    }
    
    boolean isEligibleForNewTasks()
    {
      return true;
    }
    
    void onTaskListEmptyLocked()
    {
      detachLocked();
      ActivityStackSupervisor.this.deleteActivityContainer(this);
      ActivityStackSupervisor.this.mHandler.obtainMessage(111, this).sendToTarget();
    }
    
    public void release()
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int i = this.mContainerState;
        if (i == 2)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        this.mContainerState = 2;
        long l = Binder.clearCallingIdentity();
        try
        {
          this.mStack.finishAllActivitiesLocked(false);
          ActivityStackSupervisor.this.mService.mActivityStarter.removePendingActivityLaunchesLocked(this.mStack);
          Binder.restoreCallingIdentity(l);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    void setDrawn() {}
    
    public void setSurface(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
    {
      ActivityStackSupervisor.this.mService.enforceNotIsolatedCaller("ActivityContainer.attachToSurface");
    }
    
    void setVisible(boolean paramBoolean)
    {
      ActivityStackSupervisor.ActivityStackSupervisorHandler localActivityStackSupervisorHandler;
      if (this.mVisible != paramBoolean)
      {
        this.mVisible = paramBoolean;
        if (this.mCallback != null)
        {
          localActivityStackSupervisorHandler = ActivityStackSupervisor.this.mHandler;
          if (!paramBoolean) {
            break label47;
          }
        }
      }
      label47:
      for (int i = 1;; i = 0)
      {
        localActivityStackSupervisorHandler.obtainMessage(108, i, 0, this).sendToTarget();
        return;
      }
    }
    
    public final int startActivity(Intent paramIntent)
    {
      return ActivityStackSupervisor.this.mService.startActivity(paramIntent, this);
    }
    
    public final int startActivityIntentSender(IIntentSender paramIIntentSender)
      throws TransactionTooLargeException
    {
      ActivityStackSupervisor.this.mService.enforceNotIsolatedCaller("ActivityContainer.startActivityIntentSender");
      if (!(paramIIntentSender instanceof PendingIntentRecord)) {
        throw new IllegalArgumentException("Bad PendingIntent object");
      }
      int i = ActivityStackSupervisor.this.mService.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), ActivityStackSupervisor.this.mCurrentUser, false, 2, "ActivityContainer", null);
      paramIIntentSender = (PendingIntentRecord)paramIIntentSender;
      checkEmbeddedAllowedInner(i, paramIIntentSender.key.requestIntent, paramIIntentSender.key.requestResolvedType);
      return paramIIntentSender.sendInner(0, null, null, null, null, null, null, 0, 402718720, 402718720, null, this);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append(this.mIdString);
      if (this.mActivityDisplay == null) {}
      for (String str = "N";; str = "A") {
        return str;
      }
    }
  }
  
  class ActivityDisplay
  {
    Display mDisplay;
    int mDisplayId;
    DisplayInfo mDisplayInfo = new DisplayInfo();
    final ArrayList<ActivityStack> mStacks = new ArrayList();
    ActivityRecord mVisibleBehindActivity;
    
    ActivityDisplay() {}
    
    ActivityDisplay(int paramInt)
    {
      this$1 = ActivityStackSupervisor.this.mDisplayManager.getDisplay(paramInt);
      if (ActivityStackSupervisor.this == null) {
        return;
      }
      init(ActivityStackSupervisor.this);
    }
    
    void attachActivities(ActivityStack paramActivityStack, boolean paramBoolean)
    {
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.v(ActivityStackSupervisor.-get2(), "attachActivities: attaching " + paramActivityStack + " to displayId=" + this.mDisplayId + " onTop=" + paramBoolean);
      }
      if (paramBoolean)
      {
        this.mStacks.add(paramActivityStack);
        return;
      }
      this.mStacks.add(0, paramActivityStack);
    }
    
    void detachActivitiesLocked(ActivityStack paramActivityStack)
    {
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.v(ActivityStackSupervisor.-get2(), "detachActivitiesLocked: detaching " + paramActivityStack + " from displayId=" + this.mDisplayId);
      }
      this.mStacks.remove(paramActivityStack);
    }
    
    boolean hasVisibleBehindActivity()
    {
      return this.mVisibleBehindActivity != null;
    }
    
    void init(Display paramDisplay)
    {
      this.mDisplay = paramDisplay;
      this.mDisplayId = paramDisplay.getDisplayId();
      this.mDisplay.getDisplayInfo(this.mDisplayInfo);
    }
    
    void setVisibleBehindActivity(ActivityRecord paramActivityRecord)
    {
      this.mVisibleBehindActivity = paramActivityRecord;
    }
    
    public String toString()
    {
      return "ActivityDisplay={" + this.mDisplayId + " numStacks=" + this.mStacks.size() + "}";
    }
  }
  
  private final class ActivityStackSupervisorHandler
    extends Handler
  {
    public ActivityStackSupervisorHandler(Looper paramLooper)
    {
      super();
    }
    
    void activityIdleInternal(ActivityRecord paramActivityRecord)
    {
      IApplicationToken.Stub localStub = null;
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityStackSupervisor localActivityStackSupervisor = ActivityStackSupervisor.this;
        if (paramActivityRecord != null) {
          localStub = paramActivityRecord.appToken;
        }
        localActivityStackSupervisor.activityIdleInternalLocked(localStub, true, null);
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
      case 114: 
      case 115: 
      case 100: 
      case 101: 
      case 102: 
      case 103: 
      case 104: 
      case 105: 
      case 106: 
      case 107: 
      case 108: 
      case 109: 
      case 110: 
      case 113: 
      case 111: 
        do
        {
          int i;
          Object localObject7;
          do
          {
            return;
            synchronized (ActivityStackSupervisor.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              i = ActivityStackSupervisor.this.mMultiWindowModeChangedActivities.size() - 1;
              while (i >= 0)
              {
                ((ActivityRecord)ActivityStackSupervisor.this.mMultiWindowModeChangedActivities.remove(i)).scheduleMultiWindowModeChanged();
                i -= 1;
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            synchronized (ActivityStackSupervisor.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              i = ActivityStackSupervisor.this.mPipModeChangedActivities.size() - 1;
              while (i >= 0)
              {
                ((ActivityRecord)ActivityStackSupervisor.this.mPipModeChangedActivities.remove(i)).schedulePictureInPictureModeChanged();
                i -= 1;
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            if (ActivityManagerDebugConfig.DEBUG_IDLE) {
              Slog.d(ActivityStackSupervisor.-get1(), "handleMessage: IDLE_TIMEOUT_MSG: r=" + ???.obj);
            }
            if (ActivityStackSupervisor.this.mService.mDidDexOpt)
            {
              ActivityStackSupervisor.this.mService.mDidDexOpt = false;
              Message localMessage = ActivityStackSupervisor.this.mHandler.obtainMessage(100);
              localMessage.obj = ???.obj;
              ActivityStackSupervisor.this.mHandler.sendMessageDelayed(localMessage, 10000L);
              return;
            }
            activityIdleInternal((ActivityRecord)???.obj);
            return;
            if (ActivityManagerDebugConfig.DEBUG_IDLE) {
              Slog.d(ActivityStackSupervisor.-get1(), "handleMessage: IDLE_NOW_MSG: r=" + ???.obj);
            }
            activityIdleInternal((ActivityRecord)???.obj);
            return;
            synchronized (ActivityStackSupervisor.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              ActivityStackSupervisor.this.resumeFocusedStackTopActivityLocked();
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            synchronized (ActivityStackSupervisor.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              if (ActivityStackSupervisor.this.mService.isSleepingOrShuttingDownLocked())
              {
                Slog.w(ActivityStackSupervisor.-get0(), "Sleep timeout!  Sleeping now.");
                ActivityStackSupervisor.this.mSleepTimeout = true;
                ActivityStackSupervisor.this.checkReadyForSleepLocked();
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            if (ActivityStackSupervisor.this.mService.mDidDexOpt)
            {
              ActivityStackSupervisor.this.mService.mDidDexOpt = false;
              ActivityStackSupervisor.this.mHandler.sendEmptyMessageDelayed(104, 10000L);
              return;
            }
            synchronized (ActivityStackSupervisor.this.mService)
            {
              ActivityManagerService.boostPriorityForLockedSection();
              if (ActivityStackSupervisor.this.mLaunchingActivity.isHeld())
              {
                Slog.w(ActivityStackSupervisor.-get0(), "Launch timeout has expired, giving up wake lock!");
                ActivityStackSupervisor.this.mLaunchingActivity.release();
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return;
            }
            ActivityStackSupervisor.-wrap2(ActivityStackSupervisor.this, ???.arg1);
            return;
            ActivityStackSupervisor.-wrap3(ActivityStackSupervisor.this, ???.arg1);
            return;
            ActivityStackSupervisor.-wrap4(ActivityStackSupervisor.this, ???.arg1);
            return;
            localObject7 = (ActivityStackSupervisor.ActivityContainer)???.obj;
            ??? = ((ActivityStackSupervisor.ActivityContainer)localObject7).mCallback;
          } while (??? == null);
          for (;;)
          {
            try
            {
              localObject7 = ((ActivityStackSupervisor.ActivityContainer)localObject7).asBinder();
              if (???.arg1 == 1)
              {
                bool = true;
                ((IActivityContainerCallback)???).setVisible((IBinder)localObject7, bool);
                return;
              }
            }
            catch (RemoteException ???)
            {
              return;
            }
            boolean bool = false;
          }
          for (;;)
          {
            try
            {
              if (ActivityStackSupervisor.-get5(ActivityStackSupervisor.this) == null) {
                ActivityStackSupervisor.-set1(ActivityStackSupervisor.this, new LockTaskNotify(ActivityStackSupervisor.this.mService.mContext));
              }
              ActivityStackSupervisor.-get5(ActivityStackSupervisor.this).show(true);
              ActivityStackSupervisor.-set0(ActivityStackSupervisor.this, ???.arg2);
              if (ActivityStackSupervisor.-wrap1(ActivityStackSupervisor.this) != null)
              {
                i = 0;
                if (ActivityStackSupervisor.-get4(ActivityStackSupervisor.this) == 1)
                {
                  i = 62849024;
                  ActivityStackSupervisor.-wrap1(ActivityStackSupervisor.this).disable(i, ActivityStackSupervisor.-get7(ActivityStackSupervisor.this), ActivityStackSupervisor.this.mService.mContext.getPackageName());
                }
              }
              else
              {
                ActivityStackSupervisor.this.mWindowManager.disableKeyguard(ActivityStackSupervisor.-get7(ActivityStackSupervisor.this), "Lock-to-App");
                if (ActivityStackSupervisor.-wrap0(ActivityStackSupervisor.this) == null) {
                  break;
                }
                ActivityStackSupervisor.-wrap0(ActivityStackSupervisor.this).notifyLockTaskModeChanged(true, (String)???.obj, ???.arg1);
                return;
              }
            }
            catch (RemoteException ???)
            {
              throw new RuntimeException(???);
            }
            int j = ActivityStackSupervisor.-get4(ActivityStackSupervisor.this);
            if (j == 2) {
              i = 43974656;
            }
          }
          for (;;)
          {
            try
            {
              if (ActivityStackSupervisor.-wrap1(ActivityStackSupervisor.this) != null) {
                ActivityStackSupervisor.-wrap1(ActivityStackSupervisor.this).disable(0, ActivityStackSupervisor.-get7(ActivityStackSupervisor.this), ActivityStackSupervisor.this.mService.mContext.getPackageName());
              }
              ActivityStackSupervisor.this.mWindowManager.reenableKeyguard(ActivityStackSupervisor.-get7(ActivityStackSupervisor.this));
              if (ActivityStackSupervisor.-wrap0(ActivityStackSupervisor.this) != null) {
                ActivityStackSupervisor.-wrap0(ActivityStackSupervisor.this).notifyLockTaskModeChanged(false, null, ???.arg1);
              }
              if (ActivityStackSupervisor.-get5(ActivityStackSupervisor.this) == null) {
                ActivityStackSupervisor.-set1(ActivityStackSupervisor.this, new LockTaskNotify(ActivityStackSupervisor.this.mService.mContext));
              }
              ActivityStackSupervisor.-get5(ActivityStackSupervisor.this).show(false);
            }
            catch (RemoteException ???)
            {
              throw new RuntimeException(???);
            }
            finally
            {
              ActivityStackSupervisor.-set0(ActivityStackSupervisor.this, 0);
            }
            try
            {
              if (Settings.Secure.getIntForUser(ActivityStackSupervisor.this.mService.mContext.getContentResolver(), "lock_to_app_exit_locked", ActivityStackSupervisor.this.mCurrentUser) == 0) {
                continue;
              }
              i = 1;
              if ((ActivityStackSupervisor.-get4(ActivityStackSupervisor.this) == 2) && (i != 0))
              {
                ActivityStackSupervisor.this.mWindowManager.lockNow(null);
                ActivityStackSupervisor.this.mWindowManager.dismissKeyguard();
                new LockPatternUtils(ActivityStackSupervisor.this.mService.mContext).requireCredentialEntry(-1);
              }
            }
            catch (Settings.SettingNotFoundException ???)
            {
              continue;
            }
            ActivityStackSupervisor.-set0(ActivityStackSupervisor.this, 0);
            return;
            i = 0;
          }
          if (ActivityStackSupervisor.-get5(ActivityStackSupervisor.this) == null) {
            ActivityStackSupervisor.-set1(ActivityStackSupervisor.this, new LockTaskNotify(ActivityStackSupervisor.this.mService.mContext));
          }
          ActivityStackSupervisor.-get5(ActivityStackSupervisor.this).showToast(2);
          return;
          ??? = (ActivityStackSupervisor.ActivityContainer)???.obj;
          ??? = ???.mCallback;
        } while (??? == null);
        try
        {
          ((IActivityContainerCallback)???).onAllActivitiesComplete(???.asBinder());
          return;
        }
        catch (RemoteException ???)
        {
          return;
        }
      }
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ??? = ActivityRecord.forTokenLocked((IBinder)???.obj);
        if (??? != null) {
          ActivityStackSupervisor.this.handleLaunchTaskBehindCompleteLocked(???);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
  }
  
  static class FindTaskResult
  {
    boolean matchedByRootAffinity;
    ActivityRecord r;
  }
  
  static class PendingActivityLaunch
  {
    final ProcessRecord callerApp;
    final ActivityRecord r;
    final ActivityRecord sourceRecord;
    final ActivityStack stack;
    final int startFlags;
    
    PendingActivityLaunch(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2, int paramInt, ActivityStack paramActivityStack, ProcessRecord paramProcessRecord)
    {
      this.r = paramActivityRecord1;
      this.sourceRecord = paramActivityRecord2;
      this.startFlags = paramInt;
      this.stack = paramActivityStack;
      this.callerApp = paramProcessRecord;
    }
    
    void sendErrorResult(String paramString)
    {
      try
      {
        if (this.callerApp.thread != null) {
          this.callerApp.thread.scheduleCrash(paramString);
        }
        return;
      }
      catch (RemoteException paramString)
      {
        Slog.e(ActivityStackSupervisor.-get0(), "Exception scheduling crash of failed activity launcher sourceRecord=" + this.sourceRecord, paramString);
      }
    }
  }
  
  private class VirtualActivityContainer
    extends ActivityStackSupervisor.ActivityContainer
  {
    boolean mDrawn = false;
    Surface mSurface;
    
    VirtualActivityContainer(ActivityRecord paramActivityRecord, IActivityContainerCallback paramIActivityContainerCallback)
    {
      super(ActivityStackSupervisor.this.getNextStackId());
      this.mParentActivity = paramActivityRecord;
      this.mCallback = paramIActivityContainerCallback;
      this.mContainerState = 1;
      this.mIdString = ("VirtualActivityContainer{" + this.mStackId + ", parent=" + this.mParentActivity + "}");
    }
    
    private void setSurfaceIfReadyLocked()
    {
      if (ActivityManagerDebugConfig.DEBUG_STACK) {
        Slog.v(ActivityStackSupervisor.-get2(), "setSurfaceIfReadyLocked: mDrawn=" + this.mDrawn + " mContainerState=" + this.mContainerState + " mSurface=" + this.mSurface);
      }
      if ((this.mDrawn) && (this.mSurface != null) && (this.mContainerState == 1))
      {
        ((ActivityStackSupervisor.VirtualActivityDisplay)this.mActivityDisplay).setSurface(this.mSurface);
        this.mContainerState = 0;
      }
    }
    
    private void setSurfaceLocked(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mContainerState == 2) {
        return;
      }
      ActivityStackSupervisor.VirtualActivityDisplay localVirtualActivityDisplay2 = (ActivityStackSupervisor.VirtualActivityDisplay)this.mActivityDisplay;
      ActivityStackSupervisor.VirtualActivityDisplay localVirtualActivityDisplay1 = localVirtualActivityDisplay2;
      if (localVirtualActivityDisplay2 == null)
      {
        localVirtualActivityDisplay1 = new ActivityStackSupervisor.VirtualActivityDisplay(ActivityStackSupervisor.this, paramInt1, paramInt2, paramInt3);
        this.mActivityDisplay = localVirtualActivityDisplay1;
        ActivityStackSupervisor.-get3(ActivityStackSupervisor.this).put(localVirtualActivityDisplay1.mDisplayId, localVirtualActivityDisplay1);
        attachToDisplayLocked(localVirtualActivityDisplay1, true);
      }
      if (this.mSurface != null) {
        this.mSurface.release();
      }
      this.mSurface = paramSurface;
      if (paramSurface != null) {
        ActivityStackSupervisor.this.resumeFocusedStackTopActivityLocked();
      }
      for (;;)
      {
        setSurfaceIfReadyLocked();
        if (ActivityManagerDebugConfig.DEBUG_STACK) {
          Slog.d(ActivityStackSupervisor.-get2(), "setSurface: " + this + " to display=" + localVirtualActivityDisplay1);
        }
        return;
        this.mContainerState = 1;
        ((ActivityStackSupervisor.VirtualActivityDisplay)this.mActivityDisplay).setSurface(null);
        if ((this.mStack.mPausingActivity == null) && (this.mStack.mResumedActivity != null)) {
          this.mStack.startPausingLocked(false, true, null, false);
        }
      }
    }
    
    boolean isAttachedLocked()
    {
      if (this.mSurface != null) {
        return super.isAttachedLocked();
      }
      return false;
    }
    
    boolean isEligibleForNewTasks()
    {
      return false;
    }
    
    void setDrawn()
    {
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        this.mDrawn = true;
        setSurfaceIfReadyLocked();
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public void setSurface(Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
    {
      super.setSurface(paramSurface, paramInt1, paramInt2, paramInt3);
      synchronized (ActivityStackSupervisor.this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        long l = Binder.clearCallingIdentity();
        try
        {
          setSurfaceLocked(paramSurface, paramInt1, paramInt2, paramInt3);
          Binder.restoreCallingIdentity(l);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
  }
  
  class VirtualActivityDisplay
    extends ActivityStackSupervisor.ActivityDisplay
  {
    VirtualDisplay mVirtualDisplay;
    
    VirtualActivityDisplay(int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      this.mVirtualDisplay = DisplayManagerGlobal.getInstance().createVirtualDisplay(ActivityStackSupervisor.this.mService.mContext, null, "ActivityViewVirtualDisplay", paramInt1, paramInt2, paramInt3, null, 9, null, null);
      init(this.mVirtualDisplay.getDisplay());
      ActivityStackSupervisor.this.mWindowManager.handleDisplayAdded(this.mDisplayId);
    }
    
    void detachActivitiesLocked(ActivityStack paramActivityStack)
    {
      super.detachActivitiesLocked(paramActivityStack);
      if (this.mVirtualDisplay != null)
      {
        this.mVirtualDisplay.release();
        this.mVirtualDisplay = null;
      }
    }
    
    void setSurface(Surface paramSurface)
    {
      if (this.mVirtualDisplay != null) {
        this.mVirtualDisplay.setSurface(paramSurface);
      }
    }
    
    public String toString()
    {
      return "VirtualActivityDisplay={" + this.mDisplayId + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityStackSupervisor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */