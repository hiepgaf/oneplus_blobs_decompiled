package com.android.server.am;

import android.app.ActivityManager.StackId;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityOptions;
import android.app.IApplicationThread;
import android.app.ResultInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.service.voice.IVoiceInteractionSession;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.AppTransitionAnimationSpec;
import android.view.IApplicationToken.Stub;
import com.android.internal.R.styleable;
import com.android.internal.app.ResolverActivity;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.util.XmlUtils;
import com.android.server.AttributeCache;
import com.android.server.AttributeCache.Entry;
import com.android.server.wm.WindowManagerService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class ActivityRecord
{
  static final String ACTIVITY_ICON_SUFFIX = "_activity_icon_";
  static final int APPLICATION_ACTIVITY_TYPE = 0;
  private static final String ATTR_COMPONENTSPECIFIED = "component_specified";
  private static final String ATTR_ID = "id";
  private static final String ATTR_LAUNCHEDFROMPACKAGE = "launched_from_package";
  private static final String ATTR_LAUNCHEDFROMUID = "launched_from_uid";
  private static final String ATTR_RESOLVEDTYPE = "resolved_type";
  private static final String ATTR_USERID = "user_id";
  static final int HOME_ACTIVITY_TYPE = 1;
  static final int RECENTS_ACTIVITY_TYPE = 2;
  public static final String RECENTS_PACKAGE_NAME = "com.android.systemui.recents";
  private static final boolean SHOW_ACTIVITY_START_TIME = true;
  static final int STARTING_WINDOW_NOT_SHOWN = 0;
  static final int STARTING_WINDOW_REMOVED = 2;
  static final int STARTING_WINDOW_SHOWN = 1;
  private static final String TAG = "ActivityManager";
  private static final String TAG_INTENT = "intent";
  private static final String TAG_PERSISTABLEBUNDLE = "persistable_bundle";
  private static final String TAG_STATES = TAG + ActivityManagerDebugConfig.POSTFIX_STATES;
  private static final String TAG_SWITCH = TAG + ActivityManagerDebugConfig.POSTFIX_SWITCH;
  private static final String TAG_THUMBNAILS = TAG + ActivityManagerDebugConfig.POSTFIX_THUMBNAILS;
  ProcessRecord app;
  final ApplicationInfo appInfo;
  AppTimeTracker appTimeTracker;
  final IApplicationToken.Stub appToken;
  CompatibilityInfo compat;
  final boolean componentSpecified;
  int configChangeFlags;
  Configuration configuration;
  HashSet<ConnectionRecord> connections;
  long cpuTimeAtResume;
  long createTime = System.currentTimeMillis();
  boolean deferRelaunchUntilPaused;
  boolean delayedResume;
  long displayStartTime;
  boolean finishing;
  boolean forceNewConfig;
  boolean frontOfTask;
  boolean frozenBeforeDestroy;
  boolean fullscreen;
  long fullyDrawnStartTime;
  boolean hasBeenLaunched;
  boolean haveState;
  Bundle icicle;
  int icon;
  boolean idle;
  boolean immersive;
  private boolean inHistory;
  final ActivityInfo info;
  final Intent intent;
  boolean keysPaused;
  int labelRes;
  long lastLaunchTime;
  long lastVisibleTime;
  int launchCount;
  boolean launchFailed;
  int launchMode;
  long launchTickTime;
  final String launchedFromPackage;
  final int launchedFromUid;
  int logo;
  int mActivityType;
  ArrayList<ActivityStackSupervisor.ActivityContainer> mChildContainers = new ArrayList();
  private int[] mHorizontalSizeConfigurations;
  ActivityStackSupervisor.ActivityContainer mInitialActivityContainer;
  boolean mLaunchTaskBehind;
  int mRotationAnimationHint = -1;
  private int[] mSmallestSizeConfigurations;
  final ActivityStackSupervisor mStackSupervisor;
  int mStartingWindowState = 0;
  boolean mTaskOverlay = false;
  boolean mUpdateTaskThumbnailWhenHidden;
  private int[] mVerticalSizeConfigurations;
  ArrayList<ReferrerIntent> newIntents;
  final boolean noDisplay;
  CharSequence nonLocalizedLabel;
  boolean nowVisible;
  final String packageName;
  long pauseTime;
  ActivityOptions pendingOptions;
  HashSet<WeakReference<PendingIntentRecord>> pendingResults;
  boolean pendingVoiceInteractionStart;
  PersistableBundle persistentState;
  boolean preserveWindowOnDeferredRelaunch;
  final String processName;
  final ComponentName realActivity;
  int realTheme;
  final int requestCode;
  ComponentName requestedVrComponent;
  final String resolvedType;
  ActivityRecord resultTo;
  final String resultWho;
  ArrayList<ResultInfo> results;
  ActivityOptions returningOptions;
  final boolean rootVoiceInteraction;
  final ActivityManagerService service;
  final String shortComponentName;
  boolean sleeping;
  long startTime;
  ActivityStack.ActivityState state;
  final boolean stateNotNeeded;
  boolean stopped;
  String stringName;
  TaskRecord task;
  final String taskAffinity;
  Configuration taskConfigOverride;
  ActivityManager.TaskDescription taskDescription;
  int theme;
  UriPermissionOwner uriPermissions;
  final int userId;
  boolean visible;
  IVoiceInteractionSession voiceSession;
  int windowFlags;
  
  ActivityRecord(ActivityManagerService paramActivityManagerService, ProcessRecord paramProcessRecord, int paramInt1, String paramString1, Intent paramIntent, String paramString2, ActivityInfo paramActivityInfo, Configuration paramConfiguration, ActivityRecord paramActivityRecord1, String paramString3, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, ActivityStackSupervisor paramActivityStackSupervisor, ActivityStackSupervisor.ActivityContainer paramActivityContainer, ActivityOptions paramActivityOptions, ActivityRecord paramActivityRecord2)
  {
    this.service = paramActivityManagerService;
    this.appToken = new Token(this, this.service);
    this.info = paramActivityInfo;
    this.launchedFromUid = paramInt1;
    this.launchedFromPackage = paramString1;
    this.userId = UserHandle.getUserId(paramActivityInfo.applicationInfo.uid);
    this.intent = paramIntent;
    this.shortComponentName = paramIntent.getComponent().flattenToShortString();
    this.resolvedType = paramString2;
    this.componentSpecified = paramBoolean1;
    this.rootVoiceInteraction = paramBoolean2;
    this.configuration = paramConfiguration;
    this.taskConfigOverride = Configuration.EMPTY;
    this.resultTo = paramActivityRecord1;
    this.resultWho = paramString3;
    this.requestCode = paramInt2;
    this.state = ActivityStack.ActivityState.INITIALIZING;
    this.frontOfTask = false;
    this.launchFailed = false;
    this.stopped = false;
    this.delayedResume = false;
    this.finishing = false;
    this.deferRelaunchUntilPaused = false;
    this.keysPaused = false;
    this.inHistory = false;
    this.visible = false;
    this.nowVisible = false;
    this.idle = false;
    this.hasBeenLaunched = false;
    this.mStackSupervisor = paramActivityStackSupervisor;
    this.mInitialActivityContainer = paramActivityContainer;
    if (paramActivityOptions != null)
    {
      this.pendingOptions = paramActivityOptions;
      this.mLaunchTaskBehind = this.pendingOptions.getLaunchTaskBehind();
      this.mRotationAnimationHint = this.pendingOptions.getRotationAnimationHint();
      paramActivityManagerService = this.pendingOptions.getUsageTimeReport();
      if (paramActivityManagerService != null) {
        this.appTimeTracker = new AppTimeTracker(paramActivityManagerService);
      }
    }
    this.haveState = true;
    if (paramActivityInfo != null)
    {
      if ((paramActivityInfo.targetActivity == null) || ((paramActivityInfo.targetActivity.equals(paramIntent.getComponent().getClassName())) && ((paramActivityInfo.launchMode == 0) || (paramActivityInfo.launchMode == 1))))
      {
        this.realActivity = paramIntent.getComponent();
        this.taskAffinity = paramActivityInfo.taskAffinity;
        if ((paramActivityInfo.flags & 0x10) == 0) {
          break label822;
        }
        paramBoolean2 = true;
        label379:
        this.stateNotNeeded = paramBoolean2;
        this.appInfo = paramActivityInfo.applicationInfo;
        this.nonLocalizedLabel = paramActivityInfo.nonLocalizedLabel;
        this.labelRes = paramActivityInfo.labelRes;
        if ((this.nonLocalizedLabel == null) && (this.labelRes == 0))
        {
          paramActivityManagerService = paramActivityInfo.applicationInfo;
          this.nonLocalizedLabel = paramActivityManagerService.nonLocalizedLabel;
          this.labelRes = paramActivityManagerService.labelRes;
        }
        this.icon = paramActivityInfo.getIconResource();
        this.logo = paramActivityInfo.getLogoResource();
        this.theme = paramActivityInfo.getThemeResource();
        this.realTheme = this.theme;
        if (this.realTheme == 0)
        {
          if (paramActivityInfo.applicationInfo.targetSdkVersion >= 11) {
            break label828;
          }
          paramInt2 = 16973829;
          label508:
          this.realTheme = paramInt2;
        }
        if ((paramActivityInfo.flags & 0x200) != 0) {
          this.windowFlags |= 0x1000000;
        }
        if (((paramActivityInfo.flags & 0x1) == 0) || (paramProcessRecord == null) || ((paramActivityInfo.applicationInfo.uid != 1000) && (paramActivityInfo.applicationInfo.uid != paramProcessRecord.info.uid))) {
          break label836;
        }
        this.processName = paramProcessRecord.processName;
        label592:
        if ((this.intent != null) && ((paramActivityInfo.flags & 0x20) != 0)) {
          this.intent.addFlags(8388608);
        }
        this.packageName = paramActivityInfo.applicationInfo.packageName;
        this.launchMode = paramActivityInfo.launchMode;
        paramActivityManagerService = AttributeCache.instance().get(this.packageName, this.realTheme, R.styleable.Window, this.userId);
        if (paramActivityManagerService == null) {
          break label860;
        }
        if (paramActivityManagerService.array.getBoolean(5, false)) {
          break label848;
        }
        if (paramActivityManagerService.array.hasValue(5)) {
          break label854;
        }
        paramBoolean2 = paramActivityManagerService.array.getBoolean(25, false);
        label703:
        if ((paramActivityManagerService != null) && (!paramActivityManagerService.array.getBoolean(4, false))) {
          break label866;
        }
        paramBoolean2 = false;
        label722:
        this.fullscreen = paramBoolean2;
        if (paramActivityManagerService == null) {
          break label883;
        }
        paramBoolean2 = paramActivityManagerService.array.getBoolean(10, false);
        label744:
        this.noDisplay = paramBoolean2;
        setActivityType(paramBoolean1, paramInt1, paramIntent, paramActivityRecord2);
        if ((paramActivityInfo.flags & 0x800) == 0) {
          break label889;
        }
        paramBoolean1 = true;
        label776:
        this.immersive = paramBoolean1;
        if (paramActivityInfo.requestedVrComponent != null) {
          break label895;
        }
      }
      label822:
      label828:
      label836:
      label848:
      label854:
      label860:
      label866:
      label883:
      label889:
      label895:
      for (paramActivityManagerService = null;; paramActivityManagerService = ComponentName.unflattenFromString(paramActivityInfo.requestedVrComponent))
      {
        this.requestedVrComponent = paramActivityManagerService;
        return;
        this.realActivity = new ComponentName(paramActivityInfo.packageName, paramActivityInfo.targetActivity);
        break;
        paramBoolean2 = false;
        break label379;
        paramInt2 = 16973931;
        break label508;
        this.processName = paramActivityInfo.processName;
        break label592;
        paramBoolean2 = true;
        break label703;
        paramBoolean2 = false;
        break label703;
        paramBoolean2 = false;
        break label703;
        if (paramBoolean2)
        {
          paramBoolean2 = false;
          break label722;
        }
        paramBoolean2 = true;
        break label722;
        paramBoolean2 = false;
        break label744;
        paramBoolean1 = false;
        break label776;
      }
    }
    this.realActivity = null;
    this.taskAffinity = null;
    this.stateNotNeeded = false;
    this.appInfo = null;
    this.processName = null;
    this.packageName = null;
    this.fullscreen = true;
    this.noDisplay = false;
    this.mActivityType = 0;
    this.immersive = false;
    this.requestedVrComponent = null;
  }
  
  private static String activityTypeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "APPLICATION_ACTIVITY_TYPE";
    case 1: 
      return "HOME_ACTIVITY_TYPE";
    }
    return "RECENTS_ACTIVITY_TYPE";
  }
  
  private boolean canLaunchHomeActivity(int paramInt, ActivityRecord paramActivityRecord)
  {
    boolean bool = false;
    if ((paramInt == Process.myUid()) || (paramInt == 0)) {
      return true;
    }
    if (paramActivityRecord != null) {
      bool = paramActivityRecord.isResolverActivity();
    }
    return bool;
  }
  
  private static String createImageFilename(long paramLong, int paramInt)
  {
    return String.valueOf(paramInt) + "_activity_icon_" + paramLong + ".png";
  }
  
  private static boolean crossesSizeThreshold(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      return false;
    }
    int i = paramArrayOfInt.length - 1;
    while (i >= 0)
    {
      int j = paramArrayOfInt[i];
      if ((paramInt1 < j) && (paramInt2 >= j)) {}
      while ((paramInt1 >= j) && (paramInt2 < j)) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  static ActivityRecord forTokenLocked(IBinder paramIBinder)
  {
    try
    {
      ActivityRecord localActivityRecord = Token.-wrap0((Token)paramIBinder);
      return localActivityRecord;
    }
    catch (ClassCastException localClassCastException)
    {
      Slog.w(TAG, "Bad activity token: " + paramIBinder, localClassCastException);
    }
    return null;
  }
  
  static ActivityStack getStackLocked(IBinder paramIBinder)
  {
    paramIBinder = isInStackLocked(paramIBinder);
    if (paramIBinder != null) {
      return paramIBinder.task.stack;
    }
    return null;
  }
  
  static int getTaskForActivityLocked(IBinder paramIBinder, boolean paramBoolean)
  {
    paramIBinder = forTokenLocked(paramIBinder);
    if (paramIBinder == null) {
      return -1;
    }
    TaskRecord localTaskRecord = paramIBinder.task;
    int i = localTaskRecord.mActivities.indexOf(paramIBinder);
    if ((i < 0) || ((paramBoolean) && (i > localTaskRecord.findEffectiveRootIndex()))) {
      return -1;
    }
    return localTaskRecord.taskId;
  }
  
  private boolean isHomeIntent(Intent paramIntent)
  {
    if (("android.intent.action.MAIN".equals(paramIntent.getAction())) && (paramIntent.hasCategory("android.intent.category.HOME")) && (paramIntent.getCategories().size() == 1) && (paramIntent.getData() == null)) {
      return paramIntent.getType() == null;
    }
    return false;
  }
  
  static ActivityRecord isInStackLocked(IBinder paramIBinder)
  {
    Object localObject = null;
    ActivityRecord localActivityRecord = forTokenLocked(paramIBinder);
    paramIBinder = (IBinder)localObject;
    if (localActivityRecord != null) {
      paramIBinder = localActivityRecord.task.stack.isInStackLocked(localActivityRecord);
    }
    return paramIBinder;
  }
  
  static boolean isMainIntent(Intent paramIntent)
  {
    if (("android.intent.action.MAIN".equals(paramIntent.getAction())) && (paramIntent.hasCategory("android.intent.category.LAUNCHER")) && (paramIntent.getCategories().size() == 1) && (paramIntent.getData() == null)) {
      return paramIntent.getType() == null;
    }
    return false;
  }
  
  private void reportLaunchTimeLocked(long paramLong)
  {
    ActivityStack localActivityStack = this.task.stack;
    if (localActivityStack == null) {
      return;
    }
    long l = paramLong - this.displayStartTime;
    if (localActivityStack.mLaunchStartTime != 0L) {}
    for (paramLong -= localActivityStack.mLaunchStartTime;; paramLong = l)
    {
      Trace.asyncTraceEnd(64L, "launching: " + this.packageName, 0);
      EventLog.writeEvent(30009, new Object[] { Integer.valueOf(this.userId), Integer.valueOf(System.identityHashCode(this)), this.shortComponentName, Long.valueOf(l), Long.valueOf(paramLong) });
      StringBuilder localStringBuilder = this.service.mStringBuilder;
      localStringBuilder.setLength(0);
      localStringBuilder.append("Displayed ");
      localStringBuilder.append(this.shortComponentName);
      localStringBuilder.append(": ");
      TimeUtils.formatDuration(l, localStringBuilder);
      if (l != paramLong)
      {
        localStringBuilder.append(" (total ");
        TimeUtils.formatDuration(paramLong, localStringBuilder);
        localStringBuilder.append(")");
      }
      Log.i(TAG, localStringBuilder.toString());
      this.mStackSupervisor.reportActivityLaunchedLocked(false, this, l, paramLong);
      if (paramLong > 0L) {}
      this.displayStartTime = 0L;
      localActivityStack.mLaunchStartTime = 0L;
      return;
    }
  }
  
  static ActivityRecord restoreFromXml(XmlPullParser paramXmlPullParser, ActivityStackSupervisor paramActivityStackSupervisor)
    throws IOException, XmlPullParserException
  {
    Object localObject5 = null;
    Object localObject6 = null;
    int j = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    boolean bool = false;
    int k = 0;
    long l = -1L;
    int m = paramXmlPullParser.getDepth();
    ActivityManager.TaskDescription localTaskDescription = new ActivityManager.TaskDescription();
    int i = paramXmlPullParser.getAttributeCount() - 1;
    Object localObject4 = localObject5;
    Object localObject3 = localObject6;
    if (i >= 0)
    {
      localObject4 = paramXmlPullParser.getAttributeName(i);
      localObject3 = paramXmlPullParser.getAttributeValue(i);
      if ("id".equals(localObject4)) {
        l = Long.valueOf((String)localObject3).longValue();
      }
      for (;;)
      {
        i -= 1;
        break;
        if ("launched_from_uid".equals(localObject4)) {
          j = Integer.parseInt((String)localObject3);
        } else if ("launched_from_package".equals(localObject4)) {
          localObject1 = localObject3;
        } else if ("resolved_type".equals(localObject4)) {
          localObject2 = localObject3;
        } else if ("component_specified".equals(localObject4)) {
          bool = Boolean.valueOf((String)localObject3).booleanValue();
        } else if ("user_id".equals(localObject4)) {
          k = Integer.parseInt((String)localObject3);
        } else if (((String)localObject4).startsWith("task_description_")) {
          localTaskDescription.restoreFromXml((String)localObject4, (String)localObject3);
        } else {
          Log.d(TAG, "Unknown ActivityRecord attribute=" + (String)localObject4);
        }
      }
      Slog.w(TAG, "restoreActivity: unexpected name=" + (String)localObject5);
      XmlUtils.skipCurrentTag(paramXmlPullParser);
    }
    for (;;)
    {
      i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() < m))) {
        break label369;
      }
      if (i == 2)
      {
        localObject5 = paramXmlPullParser.getName();
        if ("intent".equals(localObject5))
        {
          localObject4 = Intent.restoreFromXml(paramXmlPullParser);
        }
        else
        {
          if (!"persistable_bundle".equals(localObject5)) {
            break;
          }
          localObject3 = PersistableBundle.restoreFromXml(paramXmlPullParser);
        }
      }
    }
    label369:
    if (localObject4 == null) {
      throw new XmlPullParserException("restoreActivity error intent=" + localObject4);
    }
    paramXmlPullParser = paramActivityStackSupervisor.mService;
    localObject5 = paramActivityStackSupervisor.resolveActivity((Intent)localObject4, (String)localObject2, 0, null, k);
    if (localObject5 == null) {
      throw new XmlPullParserException("restoreActivity resolver error. Intent=" + localObject4 + " resolvedType=" + (String)localObject2);
    }
    paramXmlPullParser = new ActivityRecord(paramXmlPullParser, null, j, (String)localObject1, (Intent)localObject4, (String)localObject2, (ActivityInfo)localObject5, paramXmlPullParser.getConfiguration(), null, null, 0, bool, false, paramActivityStackSupervisor, null, null, null);
    paramXmlPullParser.persistentState = ((PersistableBundle)localObject3);
    paramXmlPullParser.taskDescription = localTaskDescription;
    paramXmlPullParser.createTime = l;
    return paramXmlPullParser;
  }
  
  private void setActivityType(boolean paramBoolean, int paramInt, Intent paramIntent, ActivityRecord paramActivityRecord)
  {
    if (((paramBoolean) && (!canLaunchHomeActivity(paramInt, paramActivityRecord))) || (!isHomeIntent(paramIntent)) || (isResolverActivity()))
    {
      if (this.realActivity.getClassName().contains("com.android.systemui.recents")) {
        this.mActivityType = 2;
      }
    }
    else
    {
      this.mActivityType = 1;
      return;
    }
    this.mActivityType = 0;
  }
  
  private static String startingWindowStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown state=" + paramInt;
    case 0: 
      return "STARTING_WINDOW_NOT_SHOWN";
    case 1: 
      return "STARTING_WINDOW_SHOWN";
    }
    return "STARTING_WINDOW_REMOVED";
  }
  
  void addNewIntentLocked(ReferrerIntent paramReferrerIntent)
  {
    if (this.newIntents == null) {
      this.newIntents = new ArrayList();
    }
    this.newIntents.add(paramReferrerIntent);
  }
  
  void addResultLocked(ActivityRecord paramActivityRecord, String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    paramActivityRecord = new ActivityResult(paramActivityRecord, paramString, paramInt1, paramInt2, paramIntent);
    if (this.results == null) {
      this.results = new ArrayList();
    }
    this.results.add(paramActivityRecord);
  }
  
  void applyOptionsLocked()
  {
    int i;
    if ((this.pendingOptions != null) && (this.pendingOptions.getAnimationType() != 5))
    {
      i = this.pendingOptions.getAnimationType();
      switch (i)
      {
      case 5: 
      case 6: 
      case 7: 
      case 10: 
      default: 
        Slog.e(TAG, "applyOptionsLocked: Unknown animationType=" + i);
      }
    }
    for (;;)
    {
      this.pendingOptions = null;
      return;
      this.service.mWindowManager.overridePendingAppTransition(this.pendingOptions.getPackageName(), this.pendingOptions.getCustomEnterResId(), this.pendingOptions.getCustomExitResId(), this.pendingOptions.getOnAnimationStartListener());
      continue;
      this.service.mWindowManager.overridePendingAppTransitionClipReveal(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getWidth(), this.pendingOptions.getHeight());
      if (this.intent.getSourceBounds() == null)
      {
        this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getHeight()));
        continue;
        this.service.mWindowManager.overridePendingAppTransitionScaleUp(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getWidth(), this.pendingOptions.getHeight());
        if (this.intent.getSourceBounds() == null)
        {
          this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getHeight()));
          continue;
          if (i == 3) {}
          for (bool = true;; bool = false)
          {
            this.service.mWindowManager.overridePendingAppTransitionThumb(this.pendingOptions.getThumbnail(), this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getOnAnimationStartListener(), bool);
            if (this.intent.getSourceBounds() != null) {
              break;
            }
            this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getThumbnail().getWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getThumbnail().getHeight()));
            break;
          }
          localObject = this.pendingOptions.getAnimSpecs();
          if ((i != 9) || (localObject == null)) {
            break;
          }
          this.service.mWindowManager.overridePendingAppTransitionMultiThumb((AppTransitionAnimationSpec[])localObject, this.pendingOptions.getOnAnimationStartListener(), this.pendingOptions.getAnimationFinishedListener(), false);
        }
      }
    }
    Object localObject = this.service.mWindowManager;
    Bitmap localBitmap = this.pendingOptions.getThumbnail();
    int j = this.pendingOptions.getStartX();
    int k = this.pendingOptions.getStartY();
    int m = this.pendingOptions.getWidth();
    int n = this.pendingOptions.getHeight();
    IRemoteCallback localIRemoteCallback = this.pendingOptions.getOnAnimationStartListener();
    if (i == 8) {}
    for (boolean bool = true;; bool = false)
    {
      ((WindowManagerService)localObject).overridePendingAppTransitionAspectScaledThumb(localBitmap, j, k, m, n, localIRemoteCallback, bool);
      if (this.intent.getSourceBounds() != null) {
        break;
      }
      this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getHeight()));
      break;
    }
  }
  
  boolean canGoInDockedStack()
  {
    if (!isHomeActivity()) {
      return (isResizeableOrForced()) || (this.info.resizeMode == 1);
    }
    return false;
  }
  
  boolean changeWindowTranslucency(boolean paramBoolean)
  {
    if (this.fullscreen == paramBoolean) {
      return false;
    }
    TaskRecord localTaskRecord = this.task;
    int j = localTaskRecord.numFullscreen;
    if (paramBoolean) {}
    for (int i = 1;; i = -1)
    {
      localTaskRecord.numFullscreen = (i + j);
      this.fullscreen = paramBoolean;
      return true;
    }
  }
  
  void clearOptionsLocked()
  {
    if (this.pendingOptions != null)
    {
      this.pendingOptions.abort();
      this.pendingOptions = null;
    }
  }
  
  void clearVoiceSessionLocked()
  {
    this.voiceSession = null;
    this.pendingVoiceInteractionStart = false;
  }
  
  boolean continueLaunchTickingLocked()
  {
    if (this.launchTickTime == 0L) {
      return false;
    }
    ActivityStack localActivityStack = this.task.stack;
    if (localActivityStack == null) {
      return false;
    }
    Message localMessage = localActivityStack.mHandler.obtainMessage(103, this);
    localActivityStack.mHandler.removeMessages(103);
    localActivityStack.mHandler.sendMessageDelayed(localMessage, 500L);
    return true;
  }
  
  public boolean crossesHorizontalSizeThreshold(int paramInt1, int paramInt2)
  {
    return crossesSizeThreshold(this.mHorizontalSizeConfigurations, paramInt1, paramInt2);
  }
  
  public boolean crossesSmallestSizeThreshold(int paramInt1, int paramInt2)
  {
    return crossesSizeThreshold(this.mSmallestSizeConfigurations, paramInt1, paramInt2);
  }
  
  public boolean crossesVerticalSizeThreshold(int paramInt1, int paramInt2)
  {
    return crossesSizeThreshold(this.mVerticalSizeConfigurations, paramInt1, paramInt2);
  }
  
  final void deliverNewIntentLocked(int paramInt, Intent paramIntent, String paramString)
  {
    this.service.grantUriPermissionFromIntentLocked(paramInt, this.packageName, paramIntent, getUriPermissionsLocked(), this.userId);
    paramIntent = new ReferrerIntent(paramIntent, paramString);
    j = 1;
    paramString = this.task.stack;
    if ((paramString != null) && (paramString.topRunningActivityLocked() == this))
    {
      paramInt = 1;
      if (!this.service.isSleepingLocked()) {
        break label185;
      }
      label66:
      if ((this.state != ActivityStack.ActivityState.RESUMED) && (this.state != ActivityStack.ActivityState.PAUSED)) {
        break label190;
      }
      label86:
      i = j;
      if (this.app != null)
      {
        i = j;
        if (this.app.thread == null) {}
      }
    }
    for (;;)
    {
      try
      {
        paramString = new ArrayList(1);
        paramString.add(paramIntent);
        IApplicationThread localIApplicationThread = this.app.thread;
        IApplicationToken.Stub localStub = this.appToken;
        if (this.state != ActivityStack.ActivityState.PAUSED) {
          continue;
        }
        bool = true;
        localIApplicationThread.scheduleNewIntent(paramString, localStub, bool);
        i = 0;
      }
      catch (NullPointerException paramString)
      {
        boolean bool;
        Slog.w(TAG, "Exception thrown sending new intent to " + this, paramString);
        i = j;
        continue;
      }
      catch (RemoteException paramString)
      {
        label185:
        label190:
        Slog.w(TAG, "Exception thrown sending new intent to " + this, paramString);
        i = j;
        continue;
      }
      if (i != 0) {
        addNewIntentLocked(paramIntent);
      }
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label66;
      i = j;
      if (paramInt != 0)
      {
        break label86;
        bool = false;
      }
    }
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    long l = SystemClock.uptimeMillis();
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("packageName=");
    paramPrintWriter.print(this.packageName);
    paramPrintWriter.print(" processName=");
    paramPrintWriter.println(this.processName);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("launchedFromUid=");
    paramPrintWriter.print(this.launchedFromUid);
    paramPrintWriter.print(" launchedFromPackage=");
    paramPrintWriter.print(this.launchedFromPackage);
    paramPrintWriter.print(" userId=");
    paramPrintWriter.println(this.userId);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("app=");
    paramPrintWriter.println(this.app);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println(this.intent.toInsecureStringWithClip());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("frontOfTask=");
    paramPrintWriter.print(this.frontOfTask);
    paramPrintWriter.print(" task=");
    paramPrintWriter.println(this.task);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("taskAffinity=");
    paramPrintWriter.println(this.taskAffinity);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("realActivity=");
    paramPrintWriter.println(this.realActivity.flattenToShortString());
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
      if (this.appInfo.splitSourceDirs != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("splitDir=");
        paramPrintWriter.println(Arrays.toString(this.appInfo.splitSourceDirs));
      }
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("stateNotNeeded=");
    paramPrintWriter.print(this.stateNotNeeded);
    paramPrintWriter.print(" componentSpecified=");
    paramPrintWriter.print(this.componentSpecified);
    paramPrintWriter.print(" mActivityType=");
    paramPrintWriter.println(this.mActivityType);
    if (this.rootVoiceInteraction)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("rootVoiceInteraction=");
      paramPrintWriter.println(this.rootVoiceInteraction);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("compat=");
    paramPrintWriter.print(this.compat);
    paramPrintWriter.print(" labelRes=0x");
    paramPrintWriter.print(Integer.toHexString(this.labelRes));
    paramPrintWriter.print(" icon=0x");
    paramPrintWriter.print(Integer.toHexString(this.icon));
    paramPrintWriter.print(" theme=0x");
    paramPrintWriter.println(Integer.toHexString(this.theme));
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("config=");
    paramPrintWriter.println(this.configuration);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("taskConfigOverride=");
    paramPrintWriter.println(this.taskConfigOverride);
    if ((this.resultTo != null) || (this.resultWho != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("resultTo=");
      paramPrintWriter.print(this.resultTo);
      paramPrintWriter.print(" resultWho=");
      paramPrintWriter.print(this.resultWho);
      paramPrintWriter.print(" resultCode=");
      paramPrintWriter.println(this.requestCode);
    }
    Object localObject;
    if (this.taskDescription != null)
    {
      localObject = this.taskDescription.getIconFilename();
      if ((localObject == null) && (this.taskDescription.getLabel() == null)) {
        break label856;
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("taskDescription:");
      paramPrintWriter.print(" iconFilename=");
      paramPrintWriter.print(this.taskDescription.getIconFilename());
      paramPrintWriter.print(" label=\"");
      paramPrintWriter.print(this.taskDescription.getLabel());
      paramPrintWriter.print("\"");
      paramPrintWriter.print(" color=");
      paramPrintWriter.println(Integer.toHexString(this.taskDescription.getPrimaryColor()));
      label698:
      if ((localObject == null) && (this.taskDescription.getIcon() != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("taskDescription contains Bitmap");
      }
    }
    if (this.results != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("results=");
      paramPrintWriter.println(this.results);
    }
    if ((this.pendingResults != null) && (this.pendingResults.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Pending Results:");
      Iterator localIterator = this.pendingResults.iterator();
      for (;;)
      {
        label792:
        if (!localIterator.hasNext()) {
          break label910;
        }
        localObject = (WeakReference)localIterator.next();
        if (localObject != null) {}
        for (localObject = (PendingIntentRecord)((WeakReference)localObject).get();; localObject = null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  - ");
          if (localObject != null) {
            break label875;
          }
          paramPrintWriter.println("null");
          break label792;
          label856:
          if (this.taskDescription.getPrimaryColor() == 0) {
            break label698;
          }
          break;
        }
        label875:
        paramPrintWriter.println(localObject);
        ((PendingIntentRecord)localObject).dump(paramPrintWriter, paramString + "    ");
      }
    }
    label910:
    if ((this.newIntents != null) && (this.newIntents.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Pending New Intents:");
      int i = 0;
      if (i < this.newIntents.size())
      {
        localObject = (Intent)this.newIntents.get(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  - ");
        if (localObject == null) {
          paramPrintWriter.println("null");
        }
        for (;;)
        {
          i += 1;
          break;
          paramPrintWriter.println(((Intent)localObject).toShortString(false, true, false, true));
        }
      }
    }
    if (this.pendingOptions != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("pendingOptions=");
      paramPrintWriter.println(this.pendingOptions);
    }
    if (this.appTimeTracker != null) {
      this.appTimeTracker.dumpWithHeader(paramPrintWriter, paramString, false);
    }
    if (this.uriPermissions != null) {
      this.uriPermissions.dump(paramPrintWriter, paramString);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("launchFailed=");
    paramPrintWriter.print(this.launchFailed);
    paramPrintWriter.print(" launchCount=");
    paramPrintWriter.print(this.launchCount);
    paramPrintWriter.print(" lastLaunchTime=");
    if (this.lastLaunchTime == 0L)
    {
      paramPrintWriter.print("0");
      paramPrintWriter.println();
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("haveState=");
      paramPrintWriter.print(this.haveState);
      paramPrintWriter.print(" icicle=");
      paramPrintWriter.println(this.icicle);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("state=");
      paramPrintWriter.print(this.state);
      paramPrintWriter.print(" stopped=");
      paramPrintWriter.print(this.stopped);
      paramPrintWriter.print(" delayedResume=");
      paramPrintWriter.print(this.delayedResume);
      paramPrintWriter.print(" finishing=");
      paramPrintWriter.println(this.finishing);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("keysPaused=");
      paramPrintWriter.print(this.keysPaused);
      paramPrintWriter.print(" inHistory=");
      paramPrintWriter.print(this.inHistory);
      paramPrintWriter.print(" visible=");
      paramPrintWriter.print(this.visible);
      paramPrintWriter.print(" sleeping=");
      paramPrintWriter.print(this.sleeping);
      paramPrintWriter.print(" idle=");
      paramPrintWriter.print(this.idle);
      paramPrintWriter.print(" mStartingWindowState=");
      paramPrintWriter.println(startingWindowStateToString(this.mStartingWindowState));
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("fullscreen=");
      paramPrintWriter.print(this.fullscreen);
      paramPrintWriter.print(" noDisplay=");
      paramPrintWriter.print(this.noDisplay);
      paramPrintWriter.print(" immersive=");
      paramPrintWriter.print(this.immersive);
      paramPrintWriter.print(" launchMode=");
      paramPrintWriter.println(this.launchMode);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("frozenBeforeDestroy=");
      paramPrintWriter.print(this.frozenBeforeDestroy);
      paramPrintWriter.print(" forceNewConfig=");
      paramPrintWriter.println(this.forceNewConfig);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mActivityType=");
      paramPrintWriter.println(activityTypeToString(this.mActivityType));
      if (this.requestedVrComponent != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("requestedVrComponent=");
        paramPrintWriter.println(this.requestedVrComponent);
      }
      if ((this.displayStartTime != 0L) || (this.startTime != 0L))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("displayStartTime=");
        if (this.displayStartTime != 0L) {
          break label1786;
        }
        paramPrintWriter.print("0");
        label1528:
        paramPrintWriter.print(" startTime=");
        if (this.startTime != 0L) {
          break label1799;
        }
        paramPrintWriter.print("0");
        label1551:
        paramPrintWriter.println();
      }
      boolean bool = this.mStackSupervisor.mWaitingVisibleActivities.contains(this);
      if ((this.lastVisibleTime != 0L) || (bool) || (this.nowVisible))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("waitingVisible=");
        paramPrintWriter.print(bool);
        paramPrintWriter.print(" nowVisible=");
        paramPrintWriter.print(this.nowVisible);
        paramPrintWriter.print(" lastVisibleTime=");
        if (this.lastVisibleTime != 0L) {
          break label1812;
        }
        paramPrintWriter.print("0");
      }
    }
    for (;;)
    {
      paramPrintWriter.println();
      if ((this.deferRelaunchUntilPaused) || (this.configChangeFlags != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("deferRelaunchUntilPaused=");
        paramPrintWriter.print(this.deferRelaunchUntilPaused);
        paramPrintWriter.print(" configChangeFlags=");
        paramPrintWriter.println(Integer.toHexString(this.configChangeFlags));
      }
      if (this.connections != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("connections=");
        paramPrintWriter.println(this.connections);
      }
      if (this.info != null) {
        paramPrintWriter.println(paramString + "resizeMode=" + ActivityInfo.resizeModeToString(this.info.resizeMode));
      }
      return;
      TimeUtils.formatDuration(this.lastLaunchTime, l, paramPrintWriter);
      break;
      label1786:
      TimeUtils.formatDuration(this.displayStartTime, l, paramPrintWriter);
      break label1528;
      label1799:
      TimeUtils.formatDuration(this.startTime, l, paramPrintWriter);
      break label1551;
      label1812:
      TimeUtils.formatDuration(this.lastVisibleTime, l, paramPrintWriter);
    }
  }
  
  void finishLaunchTickingLocked()
  {
    this.launchTickTime = 0L;
    ActivityStack localActivityStack = this.task.stack;
    if (localActivityStack != null) {
      localActivityStack.mHandler.removeMessages(103);
    }
  }
  
  ActivityOptions getOptionsForTargetActivityLocked()
  {
    ActivityOptions localActivityOptions = null;
    if (this.pendingOptions != null) {
      localActivityOptions = this.pendingOptions.forTargetActivity();
    }
    return localActivityOptions;
  }
  
  UriPermissionOwner getUriPermissionsLocked()
  {
    if (this.uriPermissions == null) {
      this.uriPermissions = new UriPermissionOwner(this.service, this);
    }
    return this.uriPermissions;
  }
  
  ActivityRecord getWaitingHistoryRecordLocked()
  {
    if ((this.mStackSupervisor.mWaitingVisibleActivities.contains(this)) || (this.stopped))
    {
      ActivityStack localActivityStack = this.mStackSupervisor.getFocusedStack();
      ActivityRecord localActivityRecord2 = localActivityStack.mResumedActivity;
      ActivityRecord localActivityRecord1 = localActivityRecord2;
      if (localActivityRecord2 == null) {
        localActivityRecord1 = localActivityStack.mPausingActivity;
      }
      if (localActivityRecord1 != null) {
        return localActivityRecord1;
      }
    }
    return this;
  }
  
  boolean isAlwaysFocusable()
  {
    boolean bool = false;
    if ((this.info.flags & 0x40000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isApplicationActivity()
  {
    boolean bool = false;
    if (this.mActivityType == 0) {
      bool = true;
    }
    return bool;
  }
  
  final boolean isDestroyable()
  {
    if ((this.finishing) || (this.app == null)) {}
    while ((this.state == ActivityStack.ActivityState.DESTROYING) || (this.state == ActivityStack.ActivityState.DESTROYED)) {
      return false;
    }
    if ((this.task == null) || (this.task.stack == null)) {}
    while ((this == this.task.stack.mResumedActivity) || (this == this.task.stack.mPausingActivity) || (!this.haveState) || (!this.stopped)) {
      return false;
    }
    return !this.visible;
  }
  
  boolean isFocusable()
  {
    if (!ActivityManager.StackId.canReceiveKeys(this.task.stack.mStackId)) {
      return isAlwaysFocusable();
    }
    return true;
  }
  
  boolean isFreeform()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.task != null)
    {
      bool1 = bool2;
      if (this.task.stack != null)
      {
        bool1 = bool2;
        if (this.task.stack.mStackId == 2) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  boolean isHomeActivity()
  {
    return this.mActivityType == 1;
  }
  
  boolean isInHistory()
  {
    return this.inHistory;
  }
  
  boolean isInStackLocked()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.task != null)
    {
      bool1 = bool2;
      if (this.task.stack != null)
      {
        bool1 = bool2;
        if (this.task.stack.isInStackLocked(this) != null) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean isInterestingToUserLocked()
  {
    if ((this.visible) || (this.nowVisible) || (this.state == ActivityStack.ActivityState.PAUSING)) {}
    while (this.state == ActivityStack.ActivityState.RESUMED) {
      return true;
    }
    return false;
  }
  
  boolean isNonResizableOrForced()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (!isHomeActivity())
    {
      bool1 = bool2;
      if (this.info.resizeMode != 2)
      {
        bool1 = bool2;
        if (this.info.resizeMode != 3) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  boolean isPersistable()
  {
    boolean bool2 = true;
    boolean bool1 = false;
    if ((this.info.persistableMode == 0) || (this.info.persistableMode == 2))
    {
      bool1 = bool2;
      if (this.intent != null) {
        if ((this.intent.getFlags() & 0x800000) != 0) {
          break label52;
        }
      }
    }
    label52:
    for (bool1 = bool2;; bool1 = false) {
      return bool1;
    }
  }
  
  boolean isRecentsActivity()
  {
    return this.mActivityType == 2;
  }
  
  boolean isResizeable()
  {
    if (!isHomeActivity()) {
      return ActivityInfo.isResizeableMode(this.info.resizeMode);
    }
    return false;
  }
  
  boolean isResizeableOrForced()
  {
    if (!isHomeActivity())
    {
      if (!isResizeable()) {
        return this.service.mForceResizableActivities;
      }
      return true;
    }
    return false;
  }
  
  boolean isResolverActivity()
  {
    return ResolverActivity.class.getName().equals(this.realActivity.getClassName());
  }
  
  void makeFinishingLocked()
  {
    if (!this.finishing)
    {
      if ((this.task != null) && (this.task.stack != null) && (this == this.task.stack.getVisibleBehindActivity())) {
        this.mStackSupervisor.requestVisibleBehindLocked(this, false);
      }
      this.finishing = true;
      if (this.stopped) {
        clearOptionsLocked();
      }
    }
  }
  
  public boolean mayFreezeScreenLocked(ProcessRecord paramProcessRecord)
  {
    if ((paramProcessRecord == null) || (paramProcessRecord.crashing)) {}
    while (paramProcessRecord.notResponding) {
      return false;
    }
    return true;
  }
  
  void pauseKeyDispatchingLocked()
  {
    if (!this.keysPaused)
    {
      this.keysPaused = true;
      this.service.mWindowManager.pauseKeyDispatching(this.appToken);
    }
  }
  
  void putInHistory()
  {
    if (!this.inHistory) {
      this.inHistory = true;
    }
  }
  
  void removeResultsLocked(ActivityRecord paramActivityRecord, String paramString, int paramInt)
  {
    if (this.results != null)
    {
      int i = this.results.size() - 1;
      if (i >= 0)
      {
        ActivityResult localActivityResult = (ActivityResult)this.results.get(i);
        if (localActivityResult.mFrom != paramActivityRecord) {
          label45:
          break label67;
        }
        for (;;)
        {
          i -= 1;
          break;
          if (localActivityResult.mResultWho == null)
          {
            if (paramString == null) {
              label67:
              if (localActivityResult.mRequestCode == paramInt) {
                this.results.remove(i);
              }
            }
          }
          else {
            if (localActivityResult.mResultWho.equals(paramString)) {
              break label45;
            }
          }
        }
      }
    }
  }
  
  void removeUriPermissionsLocked()
  {
    if (this.uriPermissions != null)
    {
      this.uriPermissions.removeUriPermissionsLocked();
      this.uriPermissions = null;
    }
  }
  
  public void reportFullyDrawnLocked()
  {
    long l1 = SystemClock.uptimeMillis();
    if (this.displayStartTime != 0L) {
      reportLaunchTimeLocked(l1);
    }
    ActivityStack localActivityStack = this.task.stack;
    long l2;
    if ((this.fullyDrawnStartTime != 0L) && (localActivityStack != null))
    {
      l2 = l1 - this.fullyDrawnStartTime;
      if (localActivityStack.mFullyDrawnStartTime == 0L) {
        break label237;
      }
    }
    label237:
    for (l1 -= localActivityStack.mFullyDrawnStartTime;; l1 = l2)
    {
      Trace.asyncTraceEnd(64L, "drawing", 0);
      EventLog.writeEvent(30042, new Object[] { Integer.valueOf(this.userId), Integer.valueOf(System.identityHashCode(this)), this.shortComponentName, Long.valueOf(l2), Long.valueOf(l1) });
      StringBuilder localStringBuilder = this.service.mStringBuilder;
      localStringBuilder.setLength(0);
      localStringBuilder.append("Fully drawn ");
      localStringBuilder.append(this.shortComponentName);
      localStringBuilder.append(": ");
      TimeUtils.formatDuration(l2, localStringBuilder);
      if (l2 != l1)
      {
        localStringBuilder.append(" (total ");
        TimeUtils.formatDuration(l1, localStringBuilder);
        localStringBuilder.append(")");
      }
      Log.i(TAG, localStringBuilder.toString());
      if (l1 > 0L) {}
      localActivityStack.mFullyDrawnStartTime = 0L;
      this.fullyDrawnStartTime = 0L;
      return;
    }
  }
  
  void resumeKeyDispatchingLocked()
  {
    if (this.keysPaused)
    {
      this.keysPaused = false;
      this.service.mWindowManager.resumeKeyDispatching(this.appToken);
    }
  }
  
  void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException, XmlPullParserException
  {
    paramXmlSerializer.attribute(null, "id", String.valueOf(this.createTime));
    paramXmlSerializer.attribute(null, "launched_from_uid", String.valueOf(this.launchedFromUid));
    if (this.launchedFromPackage != null) {
      paramXmlSerializer.attribute(null, "launched_from_package", this.launchedFromPackage);
    }
    if (this.resolvedType != null) {
      paramXmlSerializer.attribute(null, "resolved_type", this.resolvedType);
    }
    paramXmlSerializer.attribute(null, "component_specified", String.valueOf(this.componentSpecified));
    paramXmlSerializer.attribute(null, "user_id", String.valueOf(this.userId));
    if (this.taskDescription != null) {
      this.taskDescription.saveToXml(paramXmlSerializer);
    }
    paramXmlSerializer.startTag(null, "intent");
    this.intent.saveToXml(paramXmlSerializer);
    paramXmlSerializer.endTag(null, "intent");
    if ((isPersistable()) && (this.persistentState != null))
    {
      paramXmlSerializer.startTag(null, "persistable_bundle");
      this.persistentState.saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "persistable_bundle");
    }
  }
  
  void scheduleConfigurationChanged(Configuration paramConfiguration, boolean paramBoolean)
  {
    if ((this.app == null) || (this.app.thread == null)) {
      return;
    }
    try
    {
      paramConfiguration = new Configuration(paramConfiguration);
      paramConfiguration.fontScale = this.service.mConfiguration.fontScale;
      if (ActivityManagerDebugConfig.DEBUG_CONFIGURATION) {
        Slog.v(TAG, "Sending new config to " + this + " " + "reportToActivity=" + paramBoolean + " and config: " + paramConfiguration);
      }
      this.app.thread.scheduleActivityConfigurationChanged(this.appToken, paramConfiguration, paramBoolean);
      return;
    }
    catch (RemoteException paramConfiguration) {}
  }
  
  void scheduleMultiWindowModeChanged()
  {
    if ((this.task == null) || (this.task.stack == null)) {}
    while ((this.app == null) || (this.app.thread == null)) {
      return;
    }
    try
    {
      IApplicationThread localIApplicationThread = this.app.thread;
      IApplicationToken.Stub localStub = this.appToken;
      if (this.task.mFullscreen) {}
      for (boolean bool = false;; bool = true)
      {
        localIApplicationThread.scheduleMultiWindowModeChanged(localStub, bool);
        return;
      }
      return;
    }
    catch (Exception localException) {}
  }
  
  void schedulePictureInPictureModeChanged()
  {
    if ((this.task == null) || (this.task.stack == null)) {}
    while ((this.app == null) || (this.app.thread == null)) {
      return;
    }
    try
    {
      IApplicationThread localIApplicationThread = this.app.thread;
      IApplicationToken.Stub localStub = this.appToken;
      if (this.task.stack.mStackId == 4) {}
      for (boolean bool = true;; bool = false)
      {
        localIApplicationThread.schedulePictureInPictureModeChanged(localStub, bool);
        return;
      }
      return;
    }
    catch (Exception localException) {}
  }
  
  public void setSizeConfigurations(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
  {
    this.mHorizontalSizeConfigurations = paramArrayOfInt1;
    this.mVerticalSizeConfigurations = paramArrayOfInt2;
    this.mSmallestSizeConfigurations = paramArrayOfInt3;
  }
  
  void setSleeping(boolean paramBoolean)
  {
    setSleeping(paramBoolean, false);
  }
  
  void setSleeping(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.sleeping == paramBoolean1)) {
      return;
    }
    if ((this.app != null) && (this.app.thread != null)) {
      try
      {
        this.app.thread.scheduleSleeping(this.appToken, paramBoolean1);
        if ((!paramBoolean1) || (this.mStackSupervisor.mGoingToSleepActivities.contains(this))) {}
        for (;;)
        {
          this.sleeping = paramBoolean1;
          return;
          this.mStackSupervisor.mGoingToSleepActivities.add(this);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w(TAG, "Exception thrown when sleeping: " + this.intent.getComponent(), localRemoteException);
      }
    }
  }
  
  void setTask(TaskRecord paramTaskRecord1, TaskRecord paramTaskRecord2)
  {
    if ((this.task != null) && (this.task.removeActivity(this)) && (this.task != paramTaskRecord1) && (this.task.stack != null)) {
      this.task.stack.removeTask(this.task, "setTask");
    }
    this.task = paramTaskRecord1;
    setTaskToAffiliateWith(paramTaskRecord2);
  }
  
  void setTaskDescription(ActivityManager.TaskDescription paramTaskDescription)
  {
    if (paramTaskDescription.getIconFilename() == null)
    {
      Bitmap localBitmap = paramTaskDescription.getIcon();
      if (localBitmap != null)
      {
        String str = createImageFilename(this.createTime, this.task.taskId);
        str = new File(TaskPersister.getUserImagesDir(this.userId), str).getAbsolutePath();
        this.service.mRecentTasks.saveImage(localBitmap, str);
        paramTaskDescription.setIconFilename(str);
      }
    }
    this.taskDescription = paramTaskDescription;
  }
  
  void setTaskToAffiliateWith(TaskRecord paramTaskRecord)
  {
    if ((paramTaskRecord != null) && (this.launchMode != 3) && (this.launchMode != 2)) {
      this.task.setTaskToAffiliateWith(paramTaskRecord);
    }
  }
  
  void setVoiceSessionLocked(IVoiceInteractionSession paramIVoiceInteractionSession)
  {
    this.voiceSession = paramIVoiceInteractionSession;
    this.pendingVoiceInteractionStart = false;
  }
  
  void showStartingWindow(ActivityRecord paramActivityRecord, boolean paramBoolean)
  {
    IApplicationToken.Stub localStub1 = null;
    CompatibilityInfo localCompatibilityInfo = this.service.compatibilityInfoForPackageLocked(this.info.applicationInfo);
    WindowManagerService localWindowManagerService = this.service.mWindowManager;
    IApplicationToken.Stub localStub2 = this.appToken;
    String str = this.packageName;
    int i = this.theme;
    CharSequence localCharSequence = this.nonLocalizedLabel;
    int j = this.labelRes;
    int k = this.icon;
    int m = this.logo;
    int n = this.windowFlags;
    if (paramActivityRecord != null) {
      localStub1 = paramActivityRecord.appToken;
    }
    if (localWindowManagerService.setAppStartingWindow(localStub2, str, i, localCompatibilityInfo, localCharSequence, j, k, m, n, localStub1, paramBoolean)) {
      this.mStartingWindowState = 1;
    }
  }
  
  public void startFreezingScreenLocked(ProcessRecord paramProcessRecord, int paramInt)
  {
    if (mayFreezeScreenLocked(paramProcessRecord)) {
      this.service.mWindowManager.startAppFreezingScreen(this.appToken, paramInt);
    }
  }
  
  void startLaunchTickingLocked()
  {
    if (ActivityManagerService.IS_USER_BUILD) {
      return;
    }
    if (this.launchTickTime == 0L)
    {
      this.launchTickTime = SystemClock.uptimeMillis();
      continueLaunchTickingLocked();
    }
  }
  
  public void stopFreezingScreenLocked(boolean paramBoolean)
  {
    if ((paramBoolean) || (this.frozenBeforeDestroy))
    {
      this.frozenBeforeDestroy = false;
      this.service.mWindowManager.stopAppFreezingScreen(this.appToken, paramBoolean);
    }
  }
  
  boolean supportsPictureInPicture()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (!isHomeActivity())
    {
      bool1 = bool2;
      if (this.info.resizeMode == 3) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  void takeFromHistory()
  {
    if (this.inHistory)
    {
      this.inHistory = false;
      if ((this.task != null) && (!this.finishing)) {
        break label31;
      }
    }
    for (;;)
    {
      clearOptionsLocked();
      return;
      label31:
      this.task = null;
    }
  }
  
  ActivityOptions takeOptionsLocked()
  {
    ActivityOptions localActivityOptions = this.pendingOptions;
    this.pendingOptions = null;
    return localActivityOptions;
  }
  
  public String toString()
  {
    if (this.stringName != null)
    {
      localObject = new StringBuilder().append(this.stringName).append(" t");
      int i;
      StringBuilder localStringBuilder;
      if (this.task == null)
      {
        i = -1;
        localStringBuilder = ((StringBuilder)localObject).append(i);
        if (!this.finishing) {
          break label74;
        }
      }
      label74:
      for (localObject = " f}";; localObject = "}")
      {
        return (String)localObject;
        i = this.task.taskId;
        break;
      }
    }
    Object localObject = new StringBuilder(128);
    ((StringBuilder)localObject).append("ActivityRecord{");
    ((StringBuilder)localObject).append(Integer.toHexString(System.identityHashCode(this)));
    ((StringBuilder)localObject).append(" u");
    ((StringBuilder)localObject).append(this.userId);
    ((StringBuilder)localObject).append(' ');
    ((StringBuilder)localObject).append(this.intent.getComponent().flattenToShortString());
    this.stringName = ((StringBuilder)localObject).toString();
    return toString();
  }
  
  void updateOptionsLocked(ActivityOptions paramActivityOptions)
  {
    if (paramActivityOptions != null)
    {
      if (this.pendingOptions != null) {
        this.pendingOptions.abort();
      }
      this.pendingOptions = paramActivityOptions;
    }
  }
  
  void updateThumbnailLocked(Bitmap paramBitmap, CharSequence paramCharSequence)
  {
    if (paramBitmap != null)
    {
      if (ActivityManagerDebugConfig.DEBUG_THUMBNAILS) {
        Slog.i(TAG_THUMBNAILS, "Setting thumbnail of " + this + " to " + paramBitmap);
      }
      if ((this.task.setLastThumbnailLocked(paramBitmap)) && (isPersistable())) {
        this.mStackSupervisor.mService.notifyTaskPersisterLocked(this.task, false);
      }
    }
    this.task.lastDescription = paramCharSequence;
  }
  
  void windowsDrawnLocked()
  {
    this.mStackSupervisor.mActivityMetricsLogger.notifyWindowsDrawn();
    if (this.displayStartTime != 0L) {
      reportLaunchTimeLocked(SystemClock.uptimeMillis());
    }
    this.mStackSupervisor.sendWaitingVisibleReportLocked(this);
    this.startTime = 0L;
    finishLaunchTickingLocked();
    if (this.task != null) {
      this.task.hasBeenVisible = true;
    }
  }
  
  void windowsVisibleLocked()
  {
    this.mStackSupervisor.reportActivityVisibleLocked(this);
    if (ActivityManagerDebugConfig.DEBUG_SWITCH) {
      Log.v(TAG_SWITCH, "windowsVisibleLocked(): " + this);
    }
    if (!this.nowVisible)
    {
      this.nowVisible = true;
      this.lastVisibleTime = SystemClock.uptimeMillis();
      if (this.idle) {
        break label84;
      }
      this.mStackSupervisor.processStoppingActivitiesLocked(false);
    }
    for (;;)
    {
      this.service.scheduleAppGcsLocked();
      return;
      label84:
      int j = this.mStackSupervisor.mWaitingVisibleActivities.size();
      if (j > 0)
      {
        int i = 0;
        while (i < j)
        {
          ActivityRecord localActivityRecord = (ActivityRecord)this.mStackSupervisor.mWaitingVisibleActivities.get(i);
          if (ActivityManagerDebugConfig.DEBUG_SWITCH) {
            Log.v(TAG_SWITCH, "Was waiting for visible: " + localActivityRecord);
          }
          i += 1;
        }
        this.mStackSupervisor.mWaitingVisibleActivities.clear();
        this.mStackSupervisor.scheduleIdleLocked();
      }
    }
  }
  
  static class Token
    extends IApplicationToken.Stub
  {
    private final ActivityManagerService mService;
    private final WeakReference<ActivityRecord> weakActivity;
    
    Token(ActivityRecord paramActivityRecord, ActivityManagerService paramActivityManagerService)
    {
      this.weakActivity = new WeakReference(paramActivityRecord);
      this.mService = paramActivityManagerService;
    }
    
    private static final ActivityRecord tokenToActivityRecordLocked(Token paramToken)
    {
      if (paramToken == null) {
        return null;
      }
      paramToken = (ActivityRecord)paramToken.weakActivity.get();
      if ((paramToken == null) || (paramToken.task == null)) {}
      while (paramToken.task.stack == null) {
        return null;
      }
      return paramToken;
    }
    
    public void addNoHistory()
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord = tokenToActivityRecordLocked(this);
        if (localActivityRecord != null)
        {
          localActivityRecord.intent.addFlags(1073741824);
          Slog.i(ActivityRecord.-get0(), "QuickPay: addNoHistory(): " + localActivityRecord);
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public long getKeyDispatchingTimeout()
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord = tokenToActivityRecordLocked(this);
        if (localActivityRecord == null)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return 0L;
        }
        long l = ActivityManagerService.getInputDispatchingTimeoutLocked(localActivityRecord.getWaitingHistoryRecordLocked());
        ActivityManagerService.resetPriorityAfterLockedSection();
        return l;
      }
    }
    
    public boolean keyDispatchingTimedOut(String paramString)
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord1 = tokenToActivityRecordLocked(this);
        if (localActivityRecord1 == null)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return false;
        }
        ActivityRecord localActivityRecord2 = localActivityRecord1.getWaitingHistoryRecordLocked();
        if (localActivityRecord1 != null)
        {
          localProcessRecord = localActivityRecord1.app;
          ActivityManagerService.resetPriorityAfterLockedSection();
          return this.mService.inputDispatchingTimedOut(localProcessRecord, localActivityRecord2, localActivityRecord1, false, paramString);
        }
        ProcessRecord localProcessRecord = null;
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("Token{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(' ');
      localStringBuilder.append(this.weakActivity.get());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void windowsDrawn()
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord = tokenToActivityRecordLocked(this);
        if (localActivityRecord != null) {
          localActivityRecord.windowsDrawnLocked();
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public void windowsGone()
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord = tokenToActivityRecordLocked(this);
        if (localActivityRecord != null)
        {
          if (ActivityManagerDebugConfig.DEBUG_SWITCH) {
            Log.v(ActivityRecord.-get0(), "windowsGone(): " + localActivityRecord);
          }
          localActivityRecord.nowVisible = false;
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
    
    public void windowsVisible()
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        ActivityRecord localActivityRecord = tokenToActivityRecordLocked(this);
        if (localActivityRecord != null) {
          localActivityRecord.windowsVisibleLocked();
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */