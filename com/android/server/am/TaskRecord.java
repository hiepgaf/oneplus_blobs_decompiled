package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityManager.StackId;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityManager.TaskThumbnail;
import android.app.ActivityManager.TaskThumbnailInfo;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo.WindowLayout;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Debug;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.voice.IVoiceInteractionSession;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.util.XmlUtils;
import com.android.server.wm.WindowManagerService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class TaskRecord
{
  private static final String ATTR_AFFINITY = "affinity";
  private static final String ATTR_ASKEDCOMPATMODE = "asked_compat_mode";
  private static final String ATTR_AUTOREMOVERECENTS = "auto_remove_recents";
  private static final String ATTR_CALLING_PACKAGE = "calling_package";
  private static final String ATTR_CALLING_UID = "calling_uid";
  private static final String ATTR_EFFECTIVE_UID = "effective_uid";
  private static final String ATTR_FIRSTACTIVETIME = "first_active_time";
  private static final String ATTR_LASTACTIVETIME = "last_active_time";
  private static final String ATTR_LASTDESCRIPTION = "last_description";
  private static final String ATTR_LASTTIMEMOVED = "last_time_moved";
  private static final String ATTR_MIN_HEIGHT = "min_height";
  private static final String ATTR_MIN_WIDTH = "min_width";
  private static final String ATTR_NEVERRELINQUISH = "never_relinquish_identity";
  private static final String ATTR_NEXT_AFFILIATION = "next_affiliation";
  private static final String ATTR_NON_FULLSCREEN_BOUNDS = "non_fullscreen_bounds";
  private static final String ATTR_ORIGACTIVITY = "orig_activity";
  private static final String ATTR_PREV_AFFILIATION = "prev_affiliation";
  private static final String ATTR_PRIVILEGED = "privileged";
  static final String ATTR_REALACTIVITY = "real_activity";
  static final String ATTR_REALACTIVITY_SUSPENDED = "real_activity_suspended";
  private static final String ATTR_RESIZE_MODE = "resize_mode";
  private static final String ATTR_ROOTHASRESET = "root_has_reset";
  private static final String ATTR_ROOT_AFFINITY = "root_affinity";
  static final String ATTR_TASKID = "task_id";
  private static final String ATTR_TASKTYPE = "task_type";
  static final String ATTR_TASK_AFFILIATION = "task_affiliation";
  private static final String ATTR_TASK_AFFILIATION_COLOR = "task_affiliation_color";
  private static final String ATTR_USERID = "user_id";
  private static final String ATTR_USER_SETUP_COMPLETE = "user_setup_complete";
  static final int INVALID_MIN_SIZE = -1;
  static final int INVALID_TASK_ID = -1;
  static final int LOCK_TASK_AUTH_DONT_LOCK = 0;
  static final int LOCK_TASK_AUTH_LAUNCHABLE = 2;
  static final int LOCK_TASK_AUTH_LAUNCHABLE_PRIV = 4;
  static final int LOCK_TASK_AUTH_PINNABLE = 1;
  static final int LOCK_TASK_AUTH_WHITELISTED = 3;
  private static final String TAG = "ActivityManager";
  private static final String TAG_ACTIVITY = "activity";
  private static final String TAG_ADD_REMOVE = TAG + ActivityManagerDebugConfig.POSTFIX_ADD_REMOVE;
  private static final String TAG_AFFINITYINTENT = "affinity_intent";
  private static final String TAG_INTENT = "intent";
  private static final String TAG_LOCKTASK = TAG + ActivityManagerDebugConfig.POSTFIX_LOCKTASK;
  private static final String TAG_RECENTS = TAG + ActivityManagerDebugConfig.POSTFIX_RECENTS;
  private static final String TAG_TASKS = TAG + ActivityManagerDebugConfig.POSTFIX_TASKS;
  private static final String TASK_THUMBNAIL_SUFFIX = "_task_thumbnail";
  String affinity;
  Intent affinityIntent;
  boolean askedCompatMode;
  boolean autoRemoveRecents;
  int effectiveUid;
  long firstActiveTime;
  boolean hasBeenVisible;
  boolean inRecents;
  Intent intent;
  boolean isAvailable;
  boolean isPersistable = false;
  long lastActiveTime;
  CharSequence lastDescription;
  ActivityManager.TaskDescription lastTaskDescription = new ActivityManager.TaskDescription();
  final ArrayList<ActivityRecord> mActivities;
  int mAffiliatedTaskColor;
  int mAffiliatedTaskId;
  Rect mBounds = null;
  String mCallingPackage;
  int mCallingUid;
  private final String mFilename;
  boolean mFullscreen = true;
  Rect mLastNonFullscreenBounds = null;
  private Bitmap mLastThumbnail;
  private final File mLastThumbnailFile;
  private ActivityManager.TaskThumbnailInfo mLastThumbnailInfo;
  long mLastTimeMoved = System.currentTimeMillis();
  int mLayerRank = -1;
  int mLockTaskAuth = 1;
  int mLockTaskMode;
  int mLockTaskUid = -1;
  int mMinHeight;
  int mMinWidth;
  boolean mNeverRelinquishIdentity = true;
  TaskRecord mNextAffiliate;
  int mNextAffiliateTaskId = -1;
  Configuration mOverrideConfig = Configuration.EMPTY;
  TaskRecord mPrevAffiliate;
  int mPrevAffiliateTaskId = -1;
  private boolean mPrivileged;
  int mResizeMode;
  private boolean mReuseTask = false;
  final ActivityManagerService mService;
  private int mTaskToReturnTo = 0;
  boolean mTemporarilyUnresizable;
  private final Rect mTmpNonDecorBounds = new Rect();
  private final Rect mTmpRect = new Rect();
  private final Rect mTmpRect2 = new Rect();
  private final Rect mTmpStableBounds = new Rect();
  boolean mUserSetupComplete;
  int maxRecents;
  int numFullscreen;
  ComponentName origActivity;
  ComponentName realActivity;
  boolean realActivitySuspended;
  String rootAffinity;
  boolean rootWasReset;
  ActivityStack stack;
  String stringName;
  final int taskId;
  int taskType;
  int userId;
  final IVoiceInteractor voiceInteractor;
  final IVoiceInteractionSession voiceSession;
  
  private TaskRecord(ActivityManagerService paramActivityManagerService, int paramInt1, Intent paramIntent1, Intent paramIntent2, String paramString1, String paramString2, ComponentName paramComponentName1, ComponentName paramComponentName2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, int paramInt3, int paramInt4, String paramString3, ArrayList<ActivityRecord> paramArrayList, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean4, ActivityManager.TaskDescription paramTaskDescription, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, String paramString4, int paramInt10, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, int paramInt11, int paramInt12)
  {
    this.mService = paramActivityManagerService;
    this.mFilename = (String.valueOf(paramInt1) + "_task_thumbnail" + ".png");
    this.mLastThumbnailFile = new File(TaskPersister.getUserImagesDir(paramInt3), this.mFilename);
    this.mLastThumbnailInfo = paramTaskThumbnailInfo;
    this.taskId = paramInt1;
    this.intent = paramIntent1;
    this.affinityIntent = paramIntent2;
    this.affinity = paramString1;
    this.rootAffinity = paramString2;
    this.voiceSession = null;
    this.voiceInteractor = null;
    this.realActivity = paramComponentName1;
    this.realActivitySuspended = paramBoolean6;
    this.origActivity = paramComponentName2;
    this.rootWasReset = paramBoolean1;
    this.isAvailable = true;
    this.autoRemoveRecents = paramBoolean2;
    this.askedCompatMode = paramBoolean3;
    this.taskType = paramInt2;
    this.mTaskToReturnTo = 1;
    this.userId = paramInt3;
    this.mUserSetupComplete = paramBoolean7;
    this.effectiveUid = paramInt4;
    this.firstActiveTime = paramLong1;
    this.lastActiveTime = paramLong2;
    this.lastDescription = paramString3;
    this.mActivities = paramArrayList;
    this.mLastTimeMoved = paramLong3;
    this.mNeverRelinquishIdentity = paramBoolean4;
    this.lastTaskDescription = paramTaskDescription;
    this.mAffiliatedTaskId = paramInt5;
    this.mAffiliatedTaskColor = paramInt8;
    this.mPrevAffiliateTaskId = paramInt6;
    this.mNextAffiliateTaskId = paramInt7;
    this.mCallingUid = paramInt9;
    this.mCallingPackage = paramString4;
    this.mResizeMode = paramInt10;
    this.mPrivileged = paramBoolean5;
    this.mMinWidth = paramInt11;
    this.mMinHeight = paramInt12;
  }
  
  TaskRecord(ActivityManagerService paramActivityManagerService, int paramInt, ActivityInfo paramActivityInfo, Intent paramIntent, ActivityManager.TaskDescription paramTaskDescription, ActivityManager.TaskThumbnailInfo paramTaskThumbnailInfo)
  {
    this.mService = paramActivityManagerService;
    this.mFilename = (String.valueOf(paramInt) + "_task_thumbnail" + ".png");
    this.userId = UserHandle.getUserId(paramActivityInfo.applicationInfo.uid);
    this.mLastThumbnailFile = new File(TaskPersister.getUserImagesDir(this.userId), this.mFilename);
    this.mLastThumbnailInfo = paramTaskThumbnailInfo;
    this.taskId = paramInt;
    this.mAffiliatedTaskId = paramInt;
    this.voiceSession = null;
    this.voiceInteractor = null;
    this.isAvailable = true;
    this.mActivities = new ArrayList();
    this.mCallingUid = paramActivityInfo.applicationInfo.uid;
    this.mCallingPackage = paramActivityInfo.packageName;
    setIntent(paramIntent, paramActivityInfo);
    setMinDimensions(paramActivityInfo);
    this.taskType = 0;
    this.isPersistable = true;
    this.maxRecents = Math.min(Math.max(paramActivityInfo.maxRecents, 1), ActivityManager.getMaxAppRecentsLimitStatic());
    this.taskType = 0;
    this.mTaskToReturnTo = 1;
    this.lastTaskDescription = paramTaskDescription;
    touchActiveTime();
  }
  
  TaskRecord(ActivityManagerService paramActivityManagerService, int paramInt, ActivityInfo paramActivityInfo, Intent paramIntent, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor)
  {
    this.mService = paramActivityManagerService;
    this.mFilename = (String.valueOf(paramInt) + "_task_thumbnail" + ".png");
    this.userId = UserHandle.getUserId(paramActivityInfo.applicationInfo.uid);
    this.mLastThumbnailFile = new File(TaskPersister.getUserImagesDir(this.userId), this.mFilename);
    this.mLastThumbnailInfo = new ActivityManager.TaskThumbnailInfo();
    this.taskId = paramInt;
    this.mAffiliatedTaskId = paramInt;
    this.voiceSession = paramIVoiceInteractionSession;
    this.voiceInteractor = paramIVoiceInteractor;
    this.isAvailable = true;
    this.mActivities = new ArrayList();
    this.mCallingUid = paramActivityInfo.applicationInfo.uid;
    this.mCallingPackage = paramActivityInfo.packageName;
    setIntent(paramIntent, paramActivityInfo);
    setMinDimensions(paramActivityInfo);
    touchActiveTime();
  }
  
  private void adjustForMinimalTaskDimensions(Rect paramRect)
  {
    if (paramRect == null) {
      return;
    }
    int m = this.mMinWidth;
    int n = this.mMinHeight;
    int k = n;
    int j = m;
    int i;
    if (this.stack.mStackId != 4)
    {
      i = m;
      if (m == -1) {
        i = this.mService.mStackSupervisor.mDefaultMinSizeOfResizeableTask;
      }
      k = n;
      j = i;
      if (n == -1)
      {
        k = this.mService.mStackSupervisor.mDefaultMinSizeOfResizeableTask;
        j = i;
      }
    }
    if (j > paramRect.width())
    {
      m = 1;
      if (k <= paramRect.height()) {
        break label123;
      }
      i = 1;
      label103:
      if (m != 0) {
        break label128;
      }
    }
    label123:
    label128:
    for (n = i;; n = 1)
    {
      if (n != 0) {
        break label134;
      }
      return;
      m = 0;
      break;
      i = 0;
      break label103;
    }
    label134:
    if (m != 0)
    {
      if ((this.mBounds == null) || (paramRect.right != this.mBounds.right)) {
        break label207;
      }
      paramRect.left = (paramRect.right - j);
    }
    for (;;)
    {
      if (i != 0)
      {
        if ((this.mBounds == null) || (paramRect.bottom != this.mBounds.bottom)) {
          break;
        }
        paramRect.top = (paramRect.bottom - k);
      }
      return;
      label207:
      paramRect.right = (paramRect.left + j);
    }
    paramRect.bottom = (paramRect.top + k);
  }
  
  private Configuration calculateOverrideConfig(Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mTmpNonDecorBounds.set(paramRect1);
    this.mTmpStableBounds.set(paramRect1);
    Object localObject2 = this.mTmpNonDecorBounds;
    Object localObject1;
    label53:
    int i;
    if (paramRect2 != null)
    {
      localObject1 = paramRect2;
      subtractNonDecorInsets((Rect)localObject2, (Rect)localObject1, paramBoolean1, paramBoolean2);
      localObject2 = this.mTmpStableBounds;
      if (paramRect2 == null) {
        break label271;
      }
      localObject1 = paramRect2;
      subtractStableInsets((Rect)localObject2, (Rect)localObject1, paramBoolean1, paramBoolean2);
      localObject2 = this.mService.mConfiguration;
      localObject1 = new Configuration(Configuration.EMPTY);
      float f = ((Configuration)localObject2).densityDpi * 0.00625F;
      ((Configuration)localObject1).screenWidthDp = Math.min((int)(this.mTmpStableBounds.width() / f), ((Configuration)localObject2).screenWidthDp);
      ((Configuration)localObject1).screenHeightDp = Math.min((int)(this.mTmpStableBounds.height() / f), ((Configuration)localObject2).screenHeightDp);
      if (((Configuration)localObject1).screenWidthDp > ((Configuration)localObject1).screenHeightDp) {
        break label277;
      }
      i = 1;
      label163:
      ((Configuration)localObject1).orientation = i;
      ((Configuration)localObject1).fontScale = ((Configuration)localObject2).fontScale;
      i = (int)(this.mTmpNonDecorBounds.width() / f);
      int j = (int)(this.mTmpNonDecorBounds.height() / f);
      ((Configuration)localObject1).screenLayout = Configuration.reduceScreenLayout(Configuration.resetScreenLayout(((Configuration)localObject2).screenLayout), Math.max(j, i), Math.min(j, i));
      localObject2 = this.mService.mWindowManager;
      if (paramRect2 == null) {
        break label283;
      }
    }
    for (;;)
    {
      ((Configuration)localObject1).smallestScreenWidthDp = ((WindowManagerService)localObject2).getSmallestWidthForTaskBounds(paramRect2);
      return (Configuration)localObject1;
      localObject1 = paramRect1;
      break;
      label271:
      localObject1 = paramRect1;
      break label53;
      label277:
      i = 2;
      break label163;
      label283:
      paramRect2 = paramRect1;
    }
  }
  
  static TaskRecord restoreFromXml(XmlPullParser paramXmlPullParser, ActivityStackSupervisor paramActivityStackSupervisor)
    throws IOException, XmlPullParserException
  {
    Object localObject16 = null;
    Object localObject17 = null;
    ArrayList localArrayList = new ArrayList();
    Object localObject9 = null;
    boolean bool2 = false;
    Object localObject8 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    int i2 = 0;
    boolean bool7 = false;
    boolean bool6 = false;
    boolean bool5 = false;
    int i9 = 0;
    int i8 = 0;
    boolean bool1 = true;
    i = -1;
    Object localObject7 = null;
    long l3 = -1L;
    long l2 = -1L;
    long l1 = 0L;
    boolean bool4 = true;
    int i10 = -1;
    int i23 = paramXmlPullParser.getDepth();
    ActivityManager.TaskDescription localTaskDescription = new ActivityManager.TaskDescription();
    ActivityManager.TaskThumbnailInfo localTaskThumbnailInfo = new ActivityManager.TaskThumbnailInfo();
    int i7 = -1;
    int i3 = 0;
    int i6 = -1;
    int i5 = -1;
    int n = -1;
    Object localObject6 = "";
    int i1 = 4;
    boolean bool3 = false;
    Object localObject5 = null;
    int m = -1;
    int k = -1;
    int i4 = paramXmlPullParser.getAttributeCount() - 1;
    Object localObject1 = localObject16;
    Object localObject4 = localObject17;
    Object localObject10;
    if (i4 >= 0)
    {
      localObject4 = paramXmlPullParser.getAttributeName(i4);
      localObject1 = paramXmlPullParser.getAttributeValue(i4);
      int i11;
      Object localObject11;
      Object localObject12;
      boolean bool8;
      boolean bool9;
      boolean bool10;
      int i12;
      int i13;
      int i14;
      Object localObject13;
      long l4;
      long l5;
      long l6;
      boolean bool11;
      int i15;
      int i16;
      int i17;
      int i18;
      int i19;
      Object localObject14;
      boolean bool12;
      boolean bool13;
      boolean bool14;
      int i20;
      int i21;
      Object localObject15;
      int i22;
      if ("task_id".equals(localObject4))
      {
        i11 = i10;
        localObject4 = localObject2;
        localObject10 = localObject3;
        localObject11 = localObject9;
        localObject12 = localObject8;
        bool8 = bool7;
        bool9 = bool6;
        bool10 = bool5;
        i12 = i9;
        i13 = i8;
        i14 = i;
        localObject13 = localObject7;
        l4 = l3;
        l5 = l2;
        l6 = l1;
        bool11 = bool4;
        i15 = i7;
        i16 = i6;
        i17 = i5;
        i18 = i3;
        i19 = n;
        localObject14 = localObject6;
        j = i1;
        bool12 = bool3;
        bool13 = bool2;
        bool14 = bool1;
        i20 = m;
        i21 = k;
        localObject15 = localObject5;
        i22 = i2;
        if (i10 == -1)
        {
          i11 = Integer.parseInt((String)localObject1);
          i22 = i2;
          localObject15 = localObject5;
          i21 = k;
          i20 = m;
          bool14 = bool1;
          bool13 = bool2;
          bool12 = bool3;
          j = i1;
          localObject14 = localObject6;
          i19 = n;
          i18 = i3;
          i17 = i5;
          i16 = i6;
          i15 = i7;
          bool11 = bool4;
          l6 = l1;
          l5 = l2;
          l4 = l3;
          localObject13 = localObject7;
          i14 = i;
          i13 = i8;
          i12 = i9;
          bool10 = bool5;
          bool9 = bool6;
          bool8 = bool7;
          localObject12 = localObject8;
          localObject11 = localObject9;
          localObject10 = localObject3;
          localObject4 = localObject2;
        }
      }
      for (;;)
      {
        i4 -= 1;
        i10 = i11;
        localObject2 = localObject4;
        localObject3 = localObject10;
        localObject9 = localObject11;
        localObject8 = localObject12;
        bool7 = bool8;
        bool6 = bool9;
        bool5 = bool10;
        i9 = i12;
        i8 = i13;
        i = i14;
        localObject7 = localObject13;
        l3 = l4;
        l2 = l5;
        l1 = l6;
        bool4 = bool11;
        i7 = i15;
        i6 = i16;
        i5 = i17;
        i3 = i18;
        n = i19;
        localObject6 = localObject14;
        i1 = j;
        bool3 = bool12;
        bool2 = bool13;
        bool1 = bool14;
        m = i20;
        k = i21;
        localObject5 = localObject15;
        i2 = i22;
        break;
        if ("real_activity".equals(localObject4))
        {
          localObject11 = ComponentName.unflattenFromString((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("real_activity_suspended".equals(localObject4))
        {
          bool13 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("orig_activity".equals(localObject4))
        {
          localObject12 = ComponentName.unflattenFromString((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("affinity".equals(localObject4))
        {
          i11 = i10;
          localObject4 = localObject1;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("root_affinity".equals(localObject4))
        {
          i22 = 1;
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject1;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
        }
        else if ("root_has_reset".equals(localObject4))
        {
          bool8 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("auto_remove_recents".equals(localObject4))
        {
          bool9 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("asked_compat_mode".equals(localObject4))
        {
          bool10 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("user_id".equals(localObject4))
        {
          i13 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("user_setup_complete".equals(localObject4))
        {
          bool14 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("effective_uid".equals(localObject4))
        {
          i14 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("task_type".equals(localObject4))
        {
          i12 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("first_active_time".equals(localObject4))
        {
          l4 = Long.valueOf((String)localObject1).longValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("last_active_time".equals(localObject4))
        {
          l5 = Long.valueOf((String)localObject1).longValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("last_description".equals(localObject4))
        {
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject1;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("last_time_moved".equals(localObject4))
        {
          l6 = Long.valueOf((String)localObject1).longValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("never_relinquish_identity".equals(localObject4))
        {
          bool11 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if (((String)localObject4).startsWith("task_thumbnailinfo_"))
        {
          localTaskThumbnailInfo.restoreFromXml((String)localObject4, (String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if (((String)localObject4).startsWith("task_description_"))
        {
          localTaskDescription.restoreFromXml((String)localObject4, (String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("task_affiliation".equals(localObject4))
        {
          i15 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("prev_affiliation".equals(localObject4))
        {
          i16 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("next_affiliation".equals(localObject4))
        {
          i17 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("task_affiliation_color".equals(localObject4))
        {
          i18 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("calling_uid".equals(localObject4))
        {
          i19 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("calling_package".equals(localObject4))
        {
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject1;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("resize_mode".equals(localObject4))
        {
          i1 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
          if (i1 == 1)
          {
            j = 4;
            i11 = i10;
            localObject4 = localObject2;
            localObject10 = localObject3;
            localObject11 = localObject9;
            localObject12 = localObject8;
            bool8 = bool7;
            bool9 = bool6;
            bool10 = bool5;
            i12 = i9;
            i13 = i8;
            i14 = i;
            localObject13 = localObject7;
            l4 = l3;
            l5 = l2;
            l6 = l1;
            bool11 = bool4;
            i15 = i7;
            i16 = i6;
            i17 = i5;
            i18 = i3;
            i19 = n;
            localObject14 = localObject6;
            bool12 = bool3;
            bool13 = bool2;
            bool14 = bool1;
            i20 = m;
            i21 = k;
            localObject15 = localObject5;
            i22 = i2;
          }
        }
        else if ("privileged".equals(localObject4))
        {
          bool12 = Boolean.valueOf((String)localObject1).booleanValue();
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("non_fullscreen_bounds".equals(localObject4))
        {
          localObject15 = Rect.unflattenFromString((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          i22 = i2;
        }
        else if ("min_width".equals(localObject4))
        {
          i20 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
        else if ("min_height".equals(localObject4))
        {
          i21 = Integer.parseInt((String)localObject1);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          localObject15 = localObject5;
          i22 = i2;
        }
        else
        {
          Slog.w(TAG, "TaskRecord: Unknown attribute=" + (String)localObject4);
          i11 = i10;
          localObject4 = localObject2;
          localObject10 = localObject3;
          localObject11 = localObject9;
          localObject12 = localObject8;
          bool8 = bool7;
          bool9 = bool6;
          bool10 = bool5;
          i12 = i9;
          i13 = i8;
          i14 = i;
          localObject13 = localObject7;
          l4 = l3;
          l5 = l2;
          l6 = l1;
          bool11 = bool4;
          i15 = i7;
          i16 = i6;
          i17 = i5;
          i18 = i3;
          i19 = n;
          localObject14 = localObject6;
          j = i1;
          bool12 = bool3;
          bool13 = bool2;
          bool14 = bool1;
          i20 = m;
          i21 = k;
          localObject15 = localObject5;
          i22 = i2;
        }
      }
      if (!"activity".equals(localObject10)) {
        break label4998;
      }
      localObject10 = ActivityRecord.restoreFromXml(paramXmlPullParser, paramActivityStackSupervisor);
      if (localObject10 != null) {
        localArrayList.add(localObject10);
      }
    }
    for (;;)
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() < i23))) {
        break label5033;
      }
      if (j == 2)
      {
        localObject10 = paramXmlPullParser.getName();
        if ("affinity_intent".equals(localObject10))
        {
          localObject4 = Intent.restoreFromXml(paramXmlPullParser);
        }
        else
        {
          if (!"intent".equals(localObject10)) {
            break;
          }
          localObject1 = Intent.restoreFromXml(paramXmlPullParser);
          continue;
          label4998:
          Slog.e(TAG, "restoreTask: Unexpected name=" + (String)localObject10);
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
    label5033:
    if (i2 == 0)
    {
      paramXmlPullParser = (XmlPullParser)localObject2;
      j = i;
      if (i <= 0) {
        if (localObject1 == null) {
          break label5290;
        }
      }
    }
    label5290:
    for (localObject3 = localObject1;; localObject3 = localObject4)
    {
      j = 0;
      i = j;
      if (localObject3 != null) {
        localObject10 = AppGlobals.getPackageManager();
      }
      try
      {
        localObject10 = ((IPackageManager)localObject10).getApplicationInfo(((Intent)localObject3).getComponent().getPackageName(), 8704, i8);
        i = j;
        if (localObject10 != null) {
          i = ((ApplicationInfo)localObject10).uid;
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          i = j;
        }
      }
      Slog.w(TAG, "Updating task #" + i10 + " for " + localObject3 + ": effectiveUid=" + i);
      j = i;
      paramXmlPullParser = new TaskRecord(paramActivityStackSupervisor.mService, i10, (Intent)localObject1, (Intent)localObject4, (String)localObject2, paramXmlPullParser, (ComponentName)localObject9, (ComponentName)localObject8, bool7, bool6, bool5, i9, i8, j, (String)localObject7, localArrayList, l3, l2, l1, bool4, localTaskDescription, localTaskThumbnailInfo, i7, i6, i5, i3, n, (String)localObject6, i1, bool3, bool2, bool1, m, k);
      paramXmlPullParser.updateOverrideConfiguration((Rect)localObject5);
      i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((ActivityRecord)localArrayList.get(i)).task = paramXmlPullParser;
        i -= 1;
      }
      paramXmlPullParser = (XmlPullParser)localObject3;
      if (!"@".equals(localObject3)) {
        break;
      }
      paramXmlPullParser = null;
      break;
    }
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.d(TAG_RECENTS, "Restored task=" + paramXmlPullParser);
    }
    return paramXmlPullParser;
  }
  
  private void setIntent(Intent paramIntent, ActivityInfo paramActivityInfo)
  {
    boolean bool2 = true;
    label30:
    Object localObject;
    label183:
    label193:
    int i;
    if (this.intent == null) {
      if ((paramActivityInfo.flags & 0x1000) == 0)
      {
        bool1 = true;
        this.mNeverRelinquishIdentity = bool1;
        this.affinity = paramActivityInfo.taskAffinity;
        if (this.intent == null) {
          this.rootAffinity = this.affinity;
        }
        this.effectiveUid = paramActivityInfo.applicationInfo.uid;
        this.stringName = null;
        if (paramActivityInfo.targetActivity != null) {
          break label340;
        }
        localObject = paramIntent;
        if (paramIntent != null) {
          if (paramIntent.getSelector() == null)
          {
            localObject = paramIntent;
            if (paramIntent.getSourceBounds() == null) {}
          }
          else
          {
            localObject = new Intent(paramIntent);
            ((Intent)localObject).setSelector(null);
            ((Intent)localObject).setSourceBounds(null);
          }
        }
        if (ActivityManagerDebugConfig.DEBUG_TASKS) {
          Slog.v(TAG_TASKS, "Setting Intent of " + this + " to " + localObject);
        }
        this.intent = ((Intent)localObject);
        if (localObject == null) {
          break label335;
        }
        paramIntent = ((Intent)localObject).getComponent();
        this.realActivity = paramIntent;
        this.origActivity = null;
        if (this.intent != null) {
          break label491;
        }
        i = 0;
        label202:
        if ((0x200000 & i) != 0) {
          this.rootWasReset = true;
        }
        this.userId = UserHandle.getUserId(paramActivityInfo.applicationInfo.uid);
        if (Settings.Secure.getIntForUser(this.mService.mContext.getContentResolver(), "user_setup_complete", 0, this.userId) == 0) {
          break label502;
        }
        bool1 = true;
        label255:
        this.mUserSetupComplete = bool1;
        if ((paramActivityInfo.flags & 0x2000) == 0) {
          break label508;
        }
        this.autoRemoveRecents = true;
        label277:
        this.mResizeMode = paramActivityInfo.resizeMode;
        this.mLockTaskMode = paramActivityInfo.lockTaskLaunchMode;
        if ((paramActivityInfo.applicationInfo.privateFlags & 0x8) == 0) {
          break label550;
        }
      }
    }
    label335:
    label340:
    label491:
    label502:
    label508:
    label550:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mPrivileged = bool1;
      setLockTaskAuth();
      return;
      bool1 = false;
      break;
      if (!this.mNeverRelinquishIdentity) {
        break label30;
      }
      return;
      paramIntent = null;
      break label183;
      localObject = new ComponentName(paramActivityInfo.packageName, paramActivityInfo.targetActivity);
      if (paramIntent != null)
      {
        Intent localIntent = new Intent(paramIntent);
        localIntent.setComponent((ComponentName)localObject);
        localIntent.setSelector(null);
        localIntent.setSourceBounds(null);
        if (ActivityManagerDebugConfig.DEBUG_TASKS) {
          Slog.v(TAG_TASKS, "Setting Intent of " + this + " to target " + localIntent);
        }
        this.intent = localIntent;
        this.realActivity = ((ComponentName)localObject);
        this.origActivity = paramIntent.getComponent();
        break label193;
      }
      this.intent = null;
      this.realActivity = ((ComponentName)localObject);
      this.origActivity = new ComponentName(paramActivityInfo.packageName, paramActivityInfo.name);
      break label193;
      i = this.intent.getFlags();
      break label202;
      bool1 = false;
      break label255;
      if ((0x82000 & i) == 524288)
      {
        if (paramActivityInfo.documentLaunchMode != 0)
        {
          this.autoRemoveRecents = false;
          break label277;
        }
        this.autoRemoveRecents = true;
        break label277;
      }
      this.autoRemoveRecents = false;
      break label277;
    }
  }
  
  private boolean setLastThumbnailLocked(Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mLastThumbnail != paramBitmap)
    {
      this.mLastThumbnail = paramBitmap;
      this.mLastThumbnailInfo.taskWidth = paramInt1;
      this.mLastThumbnailInfo.taskHeight = paramInt2;
      this.mLastThumbnailInfo.screenOrientation = paramInt3;
      if (paramBitmap == null) {
        if (this.mLastThumbnailFile != null) {
          this.mLastThumbnailFile.delete();
        }
      }
      for (;;)
      {
        return true;
        this.mService.mRecentTasks.saveImage(paramBitmap, this.mLastThumbnailFile.getAbsolutePath());
      }
    }
    return false;
  }
  
  private void setMinDimensions(ActivityInfo paramActivityInfo)
  {
    if ((paramActivityInfo != null) && (paramActivityInfo.windowLayout != null))
    {
      this.mMinWidth = paramActivityInfo.windowLayout.minWidth;
      this.mMinHeight = paramActivityInfo.windowLayout.minHeight;
      return;
    }
    this.mMinWidth = -1;
    this.mMinHeight = -1;
  }
  
  private void subtractNonDecorInsets(Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mTmpRect2.set(paramRect2);
    this.mService.mWindowManager.subtractNonDecorInsets(this.mTmpRect2);
    int k = this.mTmpRect2.left;
    int m = paramRect2.left;
    int n = this.mTmpRect2.top;
    int i1 = paramRect2.top;
    int i;
    if (paramBoolean1)
    {
      i = 0;
      if (!paramBoolean2) {
        break label103;
      }
    }
    label103:
    for (int j = 0;; j = paramRect2.bottom - this.mTmpRect2.bottom)
    {
      paramRect1.inset(k - m, n - i1, i, j);
      return;
      i = paramRect2.right - this.mTmpRect2.right;
      break;
    }
  }
  
  private void subtractStableInsets(Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mTmpRect2.set(paramRect2);
    this.mService.mWindowManager.subtractStableInsets(this.mTmpRect2);
    int k = this.mTmpRect2.left;
    int m = paramRect2.left;
    int n = this.mTmpRect2.top;
    int i1 = paramRect2.top;
    int i;
    if (paramBoolean1)
    {
      i = 0;
      if (!paramBoolean2) {
        break label103;
      }
    }
    label103:
    for (int j = 0;; j = paramRect2.bottom - this.mTmpRect2.bottom)
    {
      paramRect1.inset(k - m, n - i1, i, j);
      return;
      i = paramRect2.right - this.mTmpRect2.right;
      break;
    }
  }
  
  static Rect validateBounds(Rect paramRect)
  {
    if ((paramRect != null) && (paramRect.isEmpty()))
    {
      Slog.wtf(TAG, "Received strange task bounds: " + paramRect, new Throwable());
      return null;
    }
    return paramRect;
  }
  
  void addActivityAtBottom(ActivityRecord paramActivityRecord)
  {
    addActivityAtIndex(0, paramActivityRecord);
  }
  
  void addActivityAtIndex(int paramInt, ActivityRecord paramActivityRecord)
  {
    if ((!this.mActivities.remove(paramActivityRecord)) && (paramActivityRecord.fullscreen)) {
      this.numFullscreen += 1;
    }
    if (this.mActivities.isEmpty())
    {
      this.taskType = paramActivityRecord.mActivityType;
      this.isPersistable = paramActivityRecord.isPersistable();
      this.mCallingUid = paramActivityRecord.launchedFromUid;
      this.mCallingPackage = paramActivityRecord.launchedFromPackage;
      this.maxRecents = Math.min(Math.max(paramActivityRecord.info.maxRecents, 1), ActivityManager.getMaxAppRecentsLimitStatic());
    }
    for (;;)
    {
      int j = this.mActivities.size();
      int i = paramInt;
      if (paramInt == j)
      {
        i = paramInt;
        if (j > 0)
        {
          i = paramInt;
          if (((ActivityRecord)this.mActivities.get(j - 1)).mTaskOverlay) {
            i = paramInt - 1;
          }
        }
      }
      this.mActivities.add(i, paramActivityRecord);
      updateEffectiveIntent();
      if (paramActivityRecord.isPersistable()) {
        this.mService.notifyTaskPersisterLocked(this, false);
      }
      return;
      paramActivityRecord.mActivityType = this.taskType;
    }
  }
  
  void addActivityToTop(ActivityRecord paramActivityRecord)
  {
    addActivityAtIndex(this.mActivities.size(), paramActivityRecord);
  }
  
  boolean autoRemoveFromRecents()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (!this.autoRemoveRecents) {
      if (this.mActivities.isEmpty())
      {
        bool1 = bool2;
        if (!this.hasBeenVisible) {}
      }
      else
      {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  boolean canGoInDockedStack()
  {
    if (!isResizeable()) {
      return inCropWindowsResizeMode();
    }
    return true;
  }
  
  boolean canMatchRootAffinity()
  {
    return (this.rootAffinity != null) && ((this.stack == null) || (this.stack.mStackId != 4));
  }
  
  void closeRecentsChain()
  {
    if (this.mPrevAffiliate != null) {
      this.mPrevAffiliate.setNextAffiliate(this.mNextAffiliate);
    }
    if (this.mNextAffiliate != null) {
      this.mNextAffiliate.setPrevAffiliate(this.mPrevAffiliate);
    }
    setPrevAffiliate(null);
    setNextAffiliate(null);
  }
  
  void disposeThumbnail()
  {
    this.mLastThumbnailInfo.reset();
    this.mLastThumbnail = null;
    this.lastDescription = null;
  }
  
  void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("userId=");
    paramPrintWriter.print(this.userId);
    paramPrintWriter.print(" effectiveUid=");
    UserHandle.formatUid(paramPrintWriter, this.effectiveUid);
    paramPrintWriter.print(" mCallingUid=");
    UserHandle.formatUid(paramPrintWriter, this.mCallingUid);
    paramPrintWriter.print(" mUserSetupComplete=");
    paramPrintWriter.print(this.mUserSetupComplete);
    paramPrintWriter.print(" mCallingPackage=");
    paramPrintWriter.println(this.mCallingPackage);
    if ((this.affinity != null) || (this.rootAffinity != null))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("affinity=");
      paramPrintWriter.print(this.affinity);
      if ((this.affinity != null) && (this.affinity.equals(this.rootAffinity))) {
        paramPrintWriter.println();
      }
    }
    else
    {
      if ((this.voiceSession != null) || (this.voiceInteractor != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("VOICE: session=0x");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.voiceSession)));
        paramPrintWriter.print(" interactor=0x");
        paramPrintWriter.println(Integer.toHexString(System.identityHashCode(this.voiceInteractor)));
      }
      StringBuilder localStringBuilder;
      if (this.intent != null)
      {
        localStringBuilder = new StringBuilder(128);
        localStringBuilder.append(paramString);
        localStringBuilder.append("intent={");
        this.intent.toShortString(localStringBuilder, false, true, false, true);
        localStringBuilder.append('}');
        paramPrintWriter.println(localStringBuilder.toString());
      }
      if (this.affinityIntent != null)
      {
        localStringBuilder = new StringBuilder(128);
        localStringBuilder.append(paramString);
        localStringBuilder.append("affinityIntent={");
        this.affinityIntent.toShortString(localStringBuilder, false, true, false, true);
        localStringBuilder.append('}');
        paramPrintWriter.println(localStringBuilder.toString());
      }
      if (this.origActivity != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("origActivity=");
        paramPrintWriter.println(this.origActivity.flattenToShortString());
      }
      if (this.realActivity != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("realActivity=");
        paramPrintWriter.println(this.realActivity.flattenToShortString());
      }
      if ((!this.autoRemoveRecents) && (!this.isPersistable) && (this.taskType == 0)) {
        break label1022;
      }
      label399:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("autoRemoveRecents=");
      paramPrintWriter.print(this.autoRemoveRecents);
      paramPrintWriter.print(" isPersistable=");
      paramPrintWriter.print(this.isPersistable);
      paramPrintWriter.print(" numFullscreen=");
      paramPrintWriter.print(this.numFullscreen);
      paramPrintWriter.print(" taskType=");
      paramPrintWriter.print(this.taskType);
      paramPrintWriter.print(" mTaskToReturnTo=");
      paramPrintWriter.println(this.mTaskToReturnTo);
      label479:
      if ((this.rootWasReset) || (this.mNeverRelinquishIdentity) || (this.mReuseTask) || (this.mLockTaskAuth != 1))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("rootWasReset=");
        paramPrintWriter.print(this.rootWasReset);
        paramPrintWriter.print(" mNeverRelinquishIdentity=");
        paramPrintWriter.print(this.mNeverRelinquishIdentity);
        paramPrintWriter.print(" mReuseTask=");
        paramPrintWriter.print(this.mReuseTask);
        paramPrintWriter.print(" mLockTaskAuth=");
        paramPrintWriter.println(lockTaskAuthToString());
      }
      if ((this.mAffiliatedTaskId == this.taskId) && (this.mPrevAffiliateTaskId == -1)) {
        break label1039;
      }
      label592:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("affiliation=");
      paramPrintWriter.print(this.mAffiliatedTaskId);
      paramPrintWriter.print(" prevAffiliation=");
      paramPrintWriter.print(this.mPrevAffiliateTaskId);
      paramPrintWriter.print(" (");
      if (this.mPrevAffiliate != null) {
        break label1064;
      }
      paramPrintWriter.print("null");
      label648:
      paramPrintWriter.print(") nextAffiliation=");
      paramPrintWriter.print(this.mNextAffiliateTaskId);
      paramPrintWriter.print(" (");
      if (this.mNextAffiliate != null) {
        break label1081;
      }
      paramPrintWriter.print("null");
      label684:
      paramPrintWriter.println(")");
      label691:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Activities=");
      paramPrintWriter.println(this.mActivities);
      if ((!this.askedCompatMode) || (!this.inRecents) || (!this.isAvailable)) {
        break label1098;
      }
    }
    for (;;)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("lastThumbnail=");
      paramPrintWriter.print(this.mLastThumbnail);
      paramPrintWriter.print(" lastThumbnailFile=");
      paramPrintWriter.println(this.mLastThumbnailFile);
      if (this.lastDescription != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("lastDescription=");
        paramPrintWriter.println(this.lastDescription);
      }
      if (this.stack != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("stackId=");
        paramPrintWriter.println(this.stack.mStackId);
      }
      paramPrintWriter.print(paramString + "hasBeenVisible=" + this.hasBeenVisible);
      paramPrintWriter.print(" mResizeMode=" + ActivityInfo.resizeModeToString(this.mResizeMode));
      paramPrintWriter.print(" isResizeable=" + isResizeable());
      paramPrintWriter.print(" firstActiveTime=" + this.lastActiveTime);
      paramPrintWriter.print(" lastActiveTime=" + this.lastActiveTime);
      paramPrintWriter.println(" (inactive for " + getInactiveDuration() / 1000L + "s)");
      return;
      paramPrintWriter.print(" root=");
      paramPrintWriter.println(this.rootAffinity);
      break;
      label1022:
      if (this.mTaskToReturnTo != 0) {
        break label399;
      }
      if (this.numFullscreen == 0) {
        break label479;
      }
      break label399;
      label1039:
      if ((this.mPrevAffiliate != null) || (this.mNextAffiliateTaskId != -1)) {
        break label592;
      }
      if (this.mNextAffiliate == null) {
        break label691;
      }
      break label592;
      label1064:
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mPrevAffiliate)));
      break label648;
      label1081:
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mNextAffiliate)));
      break label684;
      label1098:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("askedCompatMode=");
      paramPrintWriter.print(this.askedCompatMode);
      paramPrintWriter.print(" inRecents=");
      paramPrintWriter.print(this.inRecents);
      paramPrintWriter.print(" isAvailable=");
      paramPrintWriter.println(this.isAvailable);
    }
  }
  
  Configuration extractOverrideConfig(Configuration paramConfiguration)
  {
    Configuration localConfiguration = new Configuration(Configuration.EMPTY);
    localConfiguration.screenWidthDp = paramConfiguration.screenWidthDp;
    localConfiguration.screenHeightDp = paramConfiguration.screenHeightDp;
    localConfiguration.smallestScreenWidthDp = paramConfiguration.smallestScreenWidthDp;
    localConfiguration.orientation = paramConfiguration.orientation;
    localConfiguration.screenLayout = paramConfiguration.screenLayout;
    localConfiguration.fontScale = paramConfiguration.fontScale;
    return localConfiguration;
  }
  
  final ActivityRecord findActivityInHistoryLocked(ActivityRecord paramActivityRecord)
  {
    paramActivityRecord = paramActivityRecord.realActivity;
    int i = this.mActivities.size() - 1;
    if (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if (localActivityRecord.finishing) {}
      while (!localActivityRecord.realActivity.equals(paramActivityRecord))
      {
        i -= 1;
        break;
      }
      return localActivityRecord;
    }
    return null;
  }
  
  int findEffectiveRootIndex()
  {
    int j = 0;
    int m = this.mActivities.size();
    int i = 0;
    int k = j;
    if (i <= m - 1)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if (localActivityRecord.finishing) {
        k = j;
      }
      do
      {
        i += 1;
        j = k;
        break;
        j = i;
        k = j;
      } while ((localActivityRecord.info.flags & 0x1000) != 0);
      k = j;
    }
    return k;
  }
  
  void freeLastThumbnail()
  {
    this.mLastThumbnail = null;
  }
  
  Intent getBaseIntent()
  {
    if (this.intent != null) {
      return this.intent;
    }
    return this.affinityIntent;
  }
  
  long getInactiveDuration()
  {
    return System.currentTimeMillis() - this.lastActiveTime;
  }
  
  void getLastThumbnail(ActivityManager.TaskThumbnail paramTaskThumbnail)
  {
    paramTaskThumbnail.mainThumbnail = this.mLastThumbnail;
    paramTaskThumbnail.thumbnailInfo = this.mLastThumbnailInfo;
    paramTaskThumbnail.thumbnailFileDescriptor = null;
    if (this.mLastThumbnail == null) {
      paramTaskThumbnail.mainThumbnail = this.mService.mRecentTasks.getImageFromWriteQueue(this.mLastThumbnailFile.getAbsolutePath());
    }
    if ((paramTaskThumbnail.mainThumbnail == null) && (this.mLastThumbnailFile.exists())) {}
    try
    {
      paramTaskThumbnail.thumbnailFileDescriptor = ParcelFileDescriptor.open(this.mLastThumbnailFile, 268435456);
      return;
    }
    catch (IOException paramTaskThumbnail) {}
  }
  
  Rect getLaunchBounds()
  {
    Rect localRect = null;
    if (this.mService.mLockScreenShown == 2) {
      return null;
    }
    if (this.stack == null) {
      return null;
    }
    int i = this.stack.mStackId;
    if ((i == 0) || (i == 1)) {}
    while ((i == 3) && (!isResizeable()))
    {
      if (isResizeable()) {
        localRect = this.stack.mBounds;
      }
      return localRect;
    }
    if (!ActivityManager.StackId.persistTaskBounds(i)) {
      return this.stack.mBounds;
    }
    return this.mLastNonFullscreenBounds;
  }
  
  int getLaunchStackId()
  {
    if (!isApplicationTask()) {
      return 0;
    }
    if (this.mBounds != null) {
      return 2;
    }
    return 1;
  }
  
  ActivityRecord getRootActivity()
  {
    int i = 0;
    while (i < this.mActivities.size())
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if (localActivityRecord.finishing) {
        i += 1;
      } else {
        return localActivityRecord;
      }
    }
    return null;
  }
  
  public ActivityManager.TaskThumbnail getTaskThumbnailLocked()
  {
    if (this.stack != null)
    {
      localObject = this.stack.mResumedActivity;
      if ((localObject != null) && (((ActivityRecord)localObject).task == this)) {
        setLastThumbnailLocked(this.stack.screenshotActivitiesLocked((ActivityRecord)localObject));
      }
    }
    Object localObject = new ActivityManager.TaskThumbnail();
    getLastThumbnail((ActivityManager.TaskThumbnail)localObject);
    return (ActivityManager.TaskThumbnail)localObject;
  }
  
  int getTaskToReturnTo()
  {
    return this.mTaskToReturnTo;
  }
  
  ActivityRecord getTopActivity()
  {
    int i = this.mActivities.size() - 1;
    while (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if (localActivityRecord.finishing) {
        i -= 1;
      } else {
        return localActivityRecord;
      }
    }
    return null;
  }
  
  boolean inCropWindowsResizeMode()
  {
    return (!isResizeable()) && (this.mResizeMode == 1);
  }
  
  boolean isApplicationTask()
  {
    boolean bool = false;
    if (this.taskType == 0) {
      bool = true;
    }
    return bool;
  }
  
  boolean isHomeTask()
  {
    return this.taskType == 1;
  }
  
  boolean isLockTaskWhitelistedLocked()
  {
    String str = null;
    if (this.realActivity != null) {
      str = this.realActivity.getPackageName();
    }
    if (str == null) {
      return false;
    }
    String[] arrayOfString = (String[])this.mService.mLockTaskPackages.get(this.userId);
    if (arrayOfString == null) {
      return false;
    }
    int i = arrayOfString.length - 1;
    while (i >= 0)
    {
      if (str.equals(arrayOfString[i])) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  boolean isOverHomeStack()
  {
    return (this.mTaskToReturnTo == 1) || (this.mTaskToReturnTo == 2);
  }
  
  boolean isRecentsTask()
  {
    return this.taskType == 2;
  }
  
  boolean isResizeable()
  {
    return (!isHomeTask()) && ((this.mService.mForceResizableActivities) || (ActivityInfo.isResizeableMode(this.mResizeMode))) && (!this.mTemporarilyUnresizable);
  }
  
  boolean isSameIntentFilter(ActivityRecord paramActivityRecord)
  {
    Intent localIntent = new Intent(paramActivityRecord.intent);
    localIntent.setComponent(paramActivityRecord.realActivity);
    return this.intent.filterEquals(localIntent);
  }
  
  String lockTaskAuthToString()
  {
    switch (this.mLockTaskAuth)
    {
    default: 
      return "unknown=" + this.mLockTaskAuth;
    case 0: 
      return "LOCK_TASK_AUTH_DONT_LOCK";
    case 1: 
      return "LOCK_TASK_AUTH_PINNABLE";
    case 2: 
      return "LOCK_TASK_AUTH_LAUNCHABLE";
    case 3: 
      return "LOCK_TASK_AUTH_WHITELISTED";
    }
    return "LOCK_TASK_AUTH_LAUNCHABLE_PRIV";
  }
  
  final void moveActivityToFrontLocked(ActivityRecord paramActivityRecord)
  {
    if (ActivityManagerDebugConfig.DEBUG_ADD_REMOVE) {
      Slog.i(TAG_ADD_REMOVE, "Removing and adding activity " + paramActivityRecord + " to stack at top callers=" + Debug.getCallers(4));
    }
    this.mActivities.remove(paramActivityRecord);
    this.mActivities.add(paramActivityRecord);
    updateEffectiveIntent();
    setFrontOfTask(paramActivityRecord);
  }
  
  final void performClearTaskAtIndexLocked(int paramInt)
  {
    int k = this.mActivities.size();
    int i = paramInt;
    if (i < k)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      int j;
      if (localActivityRecord.finishing)
      {
        j = i;
        paramInt = k;
      }
      for (;;)
      {
        i = j + 1;
        k = paramInt;
        break;
        if (this.stack == null)
        {
          localActivityRecord.takeFromHistory();
          this.mActivities.remove(i);
          j = i - 1;
          paramInt = k - 1;
        }
        else
        {
          paramInt = k;
          j = i;
          if (this.stack.finishActivityLocked(localActivityRecord, 0, null, "clear-task-index", false))
          {
            j = i - 1;
            paramInt = k - 1;
          }
        }
      }
    }
  }
  
  ActivityRecord performClearTaskForReuseLocked(ActivityRecord paramActivityRecord, int paramInt)
  {
    this.mReuseTask = true;
    paramActivityRecord = performClearTaskLocked(paramActivityRecord, paramInt);
    this.mReuseTask = false;
    return paramActivityRecord;
  }
  
  final ActivityRecord performClearTaskLocked(ActivityRecord paramActivityRecord, int paramInt)
  {
    int j = this.mActivities.size();
    int i = j - 1;
    if (i >= 0)
    {
      ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if (localActivityRecord.finishing) {}
      while (!localActivityRecord.realActivity.equals(paramActivityRecord.realActivity))
      {
        i -= 1;
        break;
      }
      i += 1;
      if (i < j)
      {
        paramActivityRecord = (ActivityRecord)this.mActivities.get(i);
        int m;
        int k;
        if (paramActivityRecord.finishing)
        {
          m = j;
          k = i;
        }
        for (;;)
        {
          i = k + 1;
          j = m;
          break;
          ActivityOptions localActivityOptions = paramActivityRecord.takeOptionsLocked();
          if (localActivityOptions != null) {
            localActivityRecord.updateOptionsLocked(localActivityOptions);
          }
          k = i;
          m = j;
          if (this.stack != null)
          {
            k = i;
            m = j;
            if (this.stack.finishActivityLocked(paramActivityRecord, 0, null, "clear-task-stack", false))
            {
              k = i - 1;
              m = j - 1;
            }
          }
        }
      }
      if ((localActivityRecord.launchMode == 0) && ((0x20000000 & paramInt) == 0) && (!localActivityRecord.finishing))
      {
        if (this.stack != null) {
          this.stack.finishActivityLocked(localActivityRecord, 0, null, "clear-task-top", false);
        }
        return null;
      }
      return localActivityRecord;
    }
    return null;
  }
  
  final void performClearTaskLocked()
  {
    this.mReuseTask = true;
    performClearTaskAtIndexLocked(0);
    this.mReuseTask = false;
  }
  
  boolean removeActivity(ActivityRecord paramActivityRecord)
  {
    if ((this.mActivities.remove(paramActivityRecord)) && (paramActivityRecord.fullscreen)) {
      this.numFullscreen -= 1;
    }
    if (paramActivityRecord.isPersistable()) {
      this.mService.notifyTaskPersisterLocked(this, false);
    }
    if ((this.stack != null) && (this.stack.mStackId == 4)) {
      this.mService.notifyTaskStackChangedLocked();
    }
    if (this.mActivities.isEmpty()) {
      return !this.mReuseTask;
    }
    updateEffectiveIntent();
    return false;
  }
  
  public void removeTaskActivitiesLocked()
  {
    performClearTaskAtIndexLocked(0);
  }
  
  void removedFromRecents()
  {
    disposeThumbnail();
    closeRecentsChain();
    if (this.inRecents)
    {
      this.inRecents = false;
      this.mService.notifyTaskPersisterLocked(this, false);
    }
  }
  
  void sanitizeOverrideConfiguration(Configuration paramConfiguration)
  {
    if (this.mFullscreen) {
      return;
    }
    int i = this.mOverrideConfig.screenLayout;
    int j = paramConfiguration.screenLayout;
    this.mOverrideConfig.screenLayout = ((j & 0xFFFFFFCF | i & 0x30) & 0xFFFFFFF0 | i & 0xF);
    this.mOverrideConfig.fontScale = paramConfiguration.fontScale;
  }
  
  void saveToXml(XmlSerializer paramXmlSerializer)
    throws IOException, XmlPullParserException
  {
    if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
      Slog.i(TAG_RECENTS, "Saving task=" + this);
    }
    paramXmlSerializer.attribute(null, "task_id", String.valueOf(this.taskId));
    if (this.realActivity != null) {
      paramXmlSerializer.attribute(null, "real_activity", this.realActivity.flattenToShortString());
    }
    paramXmlSerializer.attribute(null, "real_activity_suspended", String.valueOf(this.realActivitySuspended));
    if (this.origActivity != null) {
      paramXmlSerializer.attribute(null, "orig_activity", this.origActivity.flattenToShortString());
    }
    Object localObject;
    label175:
    label508:
    int j;
    int i;
    if (this.affinity != null)
    {
      paramXmlSerializer.attribute(null, "affinity", this.affinity);
      if (!this.affinity.equals(this.rootAffinity))
      {
        if (this.rootAffinity != null)
        {
          localObject = this.rootAffinity;
          paramXmlSerializer.attribute(null, "root_affinity", (String)localObject);
        }
      }
      else
      {
        paramXmlSerializer.attribute(null, "root_has_reset", String.valueOf(this.rootWasReset));
        paramXmlSerializer.attribute(null, "auto_remove_recents", String.valueOf(this.autoRemoveRecents));
        paramXmlSerializer.attribute(null, "asked_compat_mode", String.valueOf(this.askedCompatMode));
        paramXmlSerializer.attribute(null, "user_id", String.valueOf(this.userId));
        paramXmlSerializer.attribute(null, "user_setup_complete", String.valueOf(this.mUserSetupComplete));
        paramXmlSerializer.attribute(null, "effective_uid", String.valueOf(this.effectiveUid));
        paramXmlSerializer.attribute(null, "task_type", String.valueOf(this.taskType));
        paramXmlSerializer.attribute(null, "first_active_time", String.valueOf(this.firstActiveTime));
        paramXmlSerializer.attribute(null, "last_active_time", String.valueOf(this.lastActiveTime));
        paramXmlSerializer.attribute(null, "last_time_moved", String.valueOf(this.mLastTimeMoved));
        paramXmlSerializer.attribute(null, "never_relinquish_identity", String.valueOf(this.mNeverRelinquishIdentity));
        if (this.lastDescription != null) {
          paramXmlSerializer.attribute(null, "last_description", this.lastDescription.toString());
        }
        if (this.lastTaskDescription != null) {
          this.lastTaskDescription.saveToXml(paramXmlSerializer);
        }
        this.mLastThumbnailInfo.saveToXml(paramXmlSerializer);
        paramXmlSerializer.attribute(null, "task_affiliation_color", String.valueOf(this.mAffiliatedTaskColor));
        paramXmlSerializer.attribute(null, "task_affiliation", String.valueOf(this.mAffiliatedTaskId));
        paramXmlSerializer.attribute(null, "prev_affiliation", String.valueOf(this.mPrevAffiliateTaskId));
        paramXmlSerializer.attribute(null, "next_affiliation", String.valueOf(this.mNextAffiliateTaskId));
        paramXmlSerializer.attribute(null, "calling_uid", String.valueOf(this.mCallingUid));
        if (this.mCallingPackage != null) {
          break label802;
        }
        localObject = "";
        paramXmlSerializer.attribute(null, "calling_package", (String)localObject);
        paramXmlSerializer.attribute(null, "resize_mode", String.valueOf(this.mResizeMode));
        paramXmlSerializer.attribute(null, "privileged", String.valueOf(this.mPrivileged));
        if (this.mLastNonFullscreenBounds != null) {
          paramXmlSerializer.attribute(null, "non_fullscreen_bounds", this.mLastNonFullscreenBounds.flattenToString());
        }
        paramXmlSerializer.attribute(null, "min_width", String.valueOf(this.mMinWidth));
        paramXmlSerializer.attribute(null, "min_height", String.valueOf(this.mMinHeight));
        if (this.affinityIntent != null)
        {
          paramXmlSerializer.startTag(null, "affinity_intent");
          this.affinityIntent.saveToXml(paramXmlSerializer);
          paramXmlSerializer.endTag(null, "affinity_intent");
        }
        paramXmlSerializer.startTag(null, "intent");
        this.intent.saveToXml(paramXmlSerializer);
        paramXmlSerializer.endTag(null, "intent");
        localObject = this.mActivities;
        j = ((ArrayList)localObject).size();
        i = 0;
      }
    }
    for (;;)
    {
      ActivityRecord localActivityRecord;
      if (i < j)
      {
        localActivityRecord = (ActivityRecord)((ArrayList)localObject).get(i);
        if ((localActivityRecord.info.persistableMode != 0) && (localActivityRecord.isPersistable()) && (((localActivityRecord.intent.getFlags() & 0x80000 | 0x2000) != 524288) || (i <= 0))) {}
      }
      else
      {
        return;
        localObject = "@";
        break;
        if (this.rootAffinity == null) {
          break label175;
        }
        if (this.rootAffinity != null) {}
        for (localObject = this.rootAffinity;; localObject = "@")
        {
          paramXmlSerializer.attribute(null, "root_affinity", (String)localObject);
          break;
        }
        label802:
        localObject = this.mCallingPackage;
        break label508;
      }
      paramXmlSerializer.startTag(null, "activity");
      localActivityRecord.saveToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "activity");
      i += 1;
    }
  }
  
  void setFrontOfTask()
  {
    setFrontOfTask(null);
  }
  
  void setFrontOfTask(ActivityRecord paramActivityRecord)
  {
    int i;
    int m;
    int j;
    label23:
    ActivityRecord localActivityRecord;
    if (paramActivityRecord != null)
    {
      i = 1;
      m = this.mActivities.size();
      int k = 0;
      j = i;
      i = k;
      if (i >= m) {
        break label83;
      }
      localActivityRecord = (ActivityRecord)this.mActivities.get(i);
      if ((j == 0) && (!localActivityRecord.finishing)) {
        break label72;
      }
      localActivityRecord.frontOfTask = false;
    }
    for (;;)
    {
      i += 1;
      break label23;
      i = 0;
      break;
      label72:
      localActivityRecord.frontOfTask = true;
      j = 1;
    }
    label83:
    if ((j == 0) && (m > 0)) {
      ((ActivityRecord)this.mActivities.get(0)).frontOfTask = true;
    }
    if (paramActivityRecord != null) {
      paramActivityRecord.frontOfTask = true;
    }
  }
  
  void setIntent(ActivityRecord paramActivityRecord)
  {
    this.mCallingUid = paramActivityRecord.launchedFromUid;
    this.mCallingPackage = paramActivityRecord.launchedFromPackage;
    setIntent(paramActivityRecord.intent, paramActivityRecord.info);
  }
  
  boolean setLastThumbnailLocked(Bitmap paramBitmap)
  {
    Configuration localConfiguration = this.mService.mConfiguration;
    int j = 0;
    int i = 0;
    if (this.mBounds != null)
    {
      j = this.mBounds.width();
      i = this.mBounds.height();
    }
    for (;;)
    {
      return setLastThumbnailLocked(paramBitmap, j, i, localConfiguration.orientation);
      if (this.stack != null)
      {
        Point localPoint = new Point();
        this.stack.getDisplaySize(localPoint);
        j = localPoint.x;
        i = localPoint.y;
      }
      else
      {
        Slog.e(TAG, "setLastThumbnailLocked() called on Task without stack");
      }
    }
  }
  
  void setLockTaskAuth()
  {
    int j = 1;
    int i = 1;
    if ((!this.mPrivileged) && ((this.mLockTaskMode == 2) || (this.mLockTaskMode == 1))) {
      this.mLockTaskMode = 0;
    }
    switch (this.mLockTaskMode)
    {
    }
    for (;;)
    {
      if (ActivityManagerDebugConfig.DEBUG_LOCKTASK) {
        Slog.d(TAG_LOCKTASK, "setLockTaskAuth: task=" + this + " mLockTaskAuth=" + lockTaskAuthToString());
      }
      return;
      if (isLockTaskWhitelistedLocked()) {
        i = 3;
      }
      this.mLockTaskAuth = i;
      continue;
      this.mLockTaskAuth = 0;
      continue;
      this.mLockTaskAuth = 4;
      continue;
      i = j;
      if (isLockTaskWhitelistedLocked()) {
        i = 2;
      }
      this.mLockTaskAuth = i;
    }
  }
  
  void setNextAffiliate(TaskRecord paramTaskRecord)
  {
    this.mNextAffiliate = paramTaskRecord;
    if (paramTaskRecord == null) {}
    for (int i = -1;; i = paramTaskRecord.taskId)
    {
      this.mNextAffiliateTaskId = i;
      return;
    }
  }
  
  void setPrevAffiliate(TaskRecord paramTaskRecord)
  {
    this.mPrevAffiliate = paramTaskRecord;
    if (paramTaskRecord == null) {}
    for (int i = -1;; i = paramTaskRecord.taskId)
    {
      this.mPrevAffiliateTaskId = i;
      return;
    }
  }
  
  void setTaskToAffiliateWith(TaskRecord paramTaskRecord)
  {
    closeRecentsChain();
    this.mAffiliatedTaskId = paramTaskRecord.mAffiliatedTaskId;
    this.mAffiliatedTaskColor = paramTaskRecord.mAffiliatedTaskColor;
    for (;;)
    {
      TaskRecord localTaskRecord;
      if (paramTaskRecord.mNextAffiliate != null)
      {
        localTaskRecord = paramTaskRecord.mNextAffiliate;
        if (localTaskRecord.mAffiliatedTaskId != this.mAffiliatedTaskId)
        {
          Slog.e(TAG, "setTaskToAffiliateWith: nextRecents=" + localTaskRecord + " affilTaskId=" + localTaskRecord.mAffiliatedTaskId + " should be " + this.mAffiliatedTaskId);
          if (localTaskRecord.mPrevAffiliate == paramTaskRecord) {
            localTaskRecord.setPrevAffiliate(null);
          }
          paramTaskRecord.setNextAffiliate(null);
        }
      }
      else
      {
        paramTaskRecord.setNextAffiliate(this);
        setPrevAffiliate(paramTaskRecord);
        setNextAffiliate(null);
        return;
      }
      paramTaskRecord = localTaskRecord;
    }
  }
  
  void setTaskToReturnTo(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 2) {
      i = 1;
    }
    this.mTaskToReturnTo = i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    if (this.stringName != null)
    {
      localStringBuilder.append(this.stringName);
      localStringBuilder.append(" U=");
      localStringBuilder.append(this.userId);
      localStringBuilder.append(" StackId=");
      if (this.stack != null) {}
      for (int i = this.stack.mStackId;; i = -1)
      {
        localStringBuilder.append(i);
        localStringBuilder.append(" sz=");
        localStringBuilder.append(this.mActivities.size());
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }
    localStringBuilder.append("TaskRecord{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" #");
    localStringBuilder.append(this.taskId);
    if (this.affinity != null)
    {
      localStringBuilder.append(" A=");
      localStringBuilder.append(this.affinity);
    }
    for (;;)
    {
      this.stringName = localStringBuilder.toString();
      return toString();
      if (this.intent != null)
      {
        localStringBuilder.append(" I=");
        localStringBuilder.append(this.intent.getComponent().flattenToShortString());
      }
      else if (this.affinityIntent != null)
      {
        localStringBuilder.append(" aI=");
        localStringBuilder.append(this.affinityIntent.getComponent().flattenToShortString());
      }
      else
      {
        localStringBuilder.append(" ??");
      }
    }
  }
  
  ActivityRecord topRunningActivityLocked()
  {
    if (this.stack != null)
    {
      int i = this.mActivities.size() - 1;
      while (i >= 0)
      {
        ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
        if ((!localActivityRecord.finishing) && (this.stack.okToShowLocked(localActivityRecord))) {
          return localActivityRecord;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  ActivityRecord topRunningActivityWithStartingWindowLocked()
  {
    if (this.stack != null)
    {
      int i = this.mActivities.size() - 1;
      while (i >= 0)
      {
        ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(i);
        if ((localActivityRecord.mStartingWindowState == 1) && (!localActivityRecord.finishing) && (this.stack.okToShowLocked(localActivityRecord))) {
          return localActivityRecord;
        }
        i -= 1;
      }
    }
    return null;
  }
  
  void touchActiveTime()
  {
    this.lastActiveTime = System.currentTimeMillis();
    if (this.firstActiveTime == 0L) {
      this.firstActiveTime = this.lastActiveTime;
    }
  }
  
  void updateEffectiveIntent()
  {
    int i = findEffectiveRootIndex();
    setIntent((ActivityRecord)this.mActivities.get(i));
  }
  
  Configuration updateOverrideConfiguration(Rect paramRect)
  {
    return updateOverrideConfiguration(paramRect, null);
  }
  
  Configuration updateOverrideConfiguration(Rect paramRect1, Rect paramRect2)
  {
    boolean bool2 = true;
    if (Objects.equals(this.mBounds, paramRect1)) {
      return null;
    }
    Configuration localConfiguration = this.mOverrideConfig;
    boolean bool3 = this.mFullscreen;
    if (paramRect1 == null) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mFullscreen = bool1;
      if (!this.mFullscreen) {
        break;
      }
      if ((this.mBounds != null) && (ActivityManager.StackId.persistTaskBounds(this.stack.mStackId))) {
        this.mLastNonFullscreenBounds = this.mBounds;
      }
      this.mBounds = null;
      this.mOverrideConfig = Configuration.EMPTY;
      if (this.mFullscreen != bool3) {
        this.mService.mStackSupervisor.scheduleReportMultiWindowModeChanged(this);
      }
      if (this.mOverrideConfig.equals(localConfiguration)) {
        break label272;
      }
      return this.mOverrideConfig;
    }
    this.mTmpRect.set(paramRect1);
    adjustForMinimalTaskDimensions(this.mTmpRect);
    label166:
    Rect localRect;
    if (this.mBounds == null)
    {
      this.mBounds = new Rect(this.mTmpRect);
      if ((this.stack == null) || (ActivityManager.StackId.persistTaskBounds(this.stack.mStackId))) {
        this.mLastNonFullscreenBounds = this.mBounds;
      }
      localRect = this.mTmpRect;
      if (this.mTmpRect.right == paramRect1.right) {
        break label261;
      }
      bool1 = true;
      label216:
      if (this.mTmpRect.bottom == paramRect1.bottom) {
        break label266;
      }
    }
    for (;;)
    {
      this.mOverrideConfig = calculateOverrideConfig(localRect, paramRect2, bool1, bool2);
      break;
      this.mBounds.set(this.mTmpRect);
      break label166;
      label261:
      bool1 = false;
      break label216;
      label266:
      bool2 = false;
    }
    label272:
    return null;
  }
  
  void updateOverrideConfigurationForStack(ActivityStack paramActivityStack)
  {
    if ((this.stack != null) && (this.stack == paramActivityStack)) {
      return;
    }
    if (paramActivityStack.mStackId == 2)
    {
      if (!isResizeable()) {
        throw new IllegalArgumentException("Can not position non-resizeable task=" + this + " in stack=" + paramActivityStack);
      }
      if (this.mBounds != null) {
        return;
      }
      if (this.mLastNonFullscreenBounds != null)
      {
        updateOverrideConfiguration(this.mLastNonFullscreenBounds);
        return;
      }
      paramActivityStack.layoutTaskInStack(this, null);
      return;
    }
    updateOverrideConfiguration(paramActivityStack.mBounds);
  }
  
  Rect updateOverrideConfigurationFromLaunchBounds()
  {
    Rect localRect = validateBounds(getLaunchBounds());
    updateOverrideConfiguration(localRect);
    if (localRect != null) {
      localRect.set(this.mBounds);
    }
    return localRect;
  }
  
  void updateTaskDescription()
  {
    int m = this.mActivities.size();
    int i;
    int j;
    if (m == 0)
    {
      i = 0;
      j = Math.min(m, 1);
    }
    Object localObject1;
    Object localObject3;
    for (;;)
    {
      int k = j;
      if (j < m)
      {
        localObject1 = (ActivityRecord)this.mActivities.get(j);
        if ((i == 0) || ((((ActivityRecord)localObject1).info.flags & 0x1000) != 0)) {
          break label276;
        }
        k = j + 1;
      }
      label276:
      do
      {
        if (k <= 0) {
          return;
        }
        localObject3 = null;
        localObject1 = null;
        i = 0;
        m = 0;
        k -= 1;
        while (k >= 0)
        {
          ActivityRecord localActivityRecord = (ActivityRecord)this.mActivities.get(k);
          Object localObject4 = localObject3;
          Object localObject5 = localObject1;
          int n = i;
          int i1 = m;
          if (localActivityRecord.taskDescription != null)
          {
            Object localObject2 = localObject3;
            if (localObject3 == null) {
              localObject2 = localActivityRecord.taskDescription.getLabel();
            }
            localObject3 = localObject1;
            if (localObject1 == null) {
              localObject3 = localActivityRecord.taskDescription.getIconFilename();
            }
            j = i;
            if (i == 0) {
              j = localActivityRecord.taskDescription.getPrimaryColor();
            }
            localObject4 = localObject2;
            localObject5 = localObject3;
            n = j;
            i1 = m;
            if (m == 0)
            {
              i1 = localActivityRecord.taskDescription.getBackgroundColor();
              n = j;
              localObject5 = localObject3;
              localObject4 = localObject2;
            }
          }
          k -= 1;
          localObject3 = localObject4;
          localObject1 = localObject5;
          i = n;
          m = i1;
        }
        if ((((ActivityRecord)this.mActivities.get(0)).info.flags & 0x1000) != 0)
        {
          i = 1;
          break;
        }
        i = 0;
        break;
        if (((ActivityRecord)localObject1).intent == null) {
          break label301;
        }
        k = j;
      } while ((((ActivityRecord)localObject1).intent.getFlags() & 0x80000) != 0);
      label301:
      j += 1;
    }
    this.lastTaskDescription = new ActivityManager.TaskDescription((String)localObject3, null, (String)localObject1, i, m);
    if (this.taskId == this.mAffiliatedTaskId) {
      this.mAffiliatedTaskColor = this.lastTaskDescription.getPrimaryColor();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/TaskRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */