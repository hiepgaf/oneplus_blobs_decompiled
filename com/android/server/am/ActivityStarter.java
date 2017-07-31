package com.android.server.am;

import android.app.ActivityManager.StackId;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IActivityController;
import android.app.IApplicationThread;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.EphemeralResolveInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.voice.IVoiceInteractionSession;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.app.IVoiceInteractor;
import com.android.server.firewall.IntentFirewall;
import com.android.server.wm.WindowManagerService;
import java.util.ArrayList;

class ActivityStarter
{
  private static final String TAG = "ActivityManager";
  private static final String TAG_CONFIGURATION = TAG + ActivityManagerDebugConfig.POSTFIX_CONFIGURATION;
  private static final String TAG_FOCUS;
  private static final String TAG_RESULTS = TAG + ActivityManagerDebugConfig.POSTFIX_RESULTS;
  private static final String TAG_USER_LEAVING = TAG + ActivityManagerDebugConfig.POSTFIX_USER_LEAVING;
  private static final boolean USE_DEFAULT_EPHEMERAL_LAUNCHER = false;
  private boolean mAddingToTask;
  private boolean mAvoidMoveToFront;
  private int mCallingUid;
  private boolean mDoResume;
  private TaskRecord mInTask;
  private Intent mIntent;
  private ActivityStartInterceptor mInterceptor;
  private boolean mKeepCurTransition;
  private Rect mLaunchBounds;
  private int mLaunchFlags;
  private boolean mLaunchSingleInstance;
  private boolean mLaunchSingleTask;
  private boolean mLaunchSingleTop;
  private boolean mLaunchTaskBehind;
  private boolean mMovedOtherTask;
  private boolean mMovedToFront;
  private ActivityInfo mNewTaskInfo;
  private Intent mNewTaskIntent;
  private boolean mNoAnimation;
  private ActivityRecord mNotTop;
  private ActivityOptions mOptions;
  final ArrayList<ActivityStackSupervisor.PendingActivityLaunch> mPendingActivityLaunches = new ArrayList();
  private boolean mPowerHintSent;
  private TaskRecord mReuseTask;
  private ActivityRecord mReusedActivity;
  private final ActivityManagerService mService;
  private ActivityRecord mSourceRecord;
  private ActivityStack mSourceStack;
  private ActivityRecord mStartActivity;
  private int mStartFlags;
  private final ActivityStackSupervisor mSupervisor;
  private ActivityStack mTargetStack;
  private IVoiceInteractor mVoiceInteractor;
  private IVoiceInteractionSession mVoiceSession;
  private WindowManagerService mWindowManager;
  
  static
  {
    TAG_FOCUS = TAG + ActivityManagerDebugConfig.POSTFIX_FOCUS;
  }
  
  ActivityStarter(ActivityManagerService paramActivityManagerService, ActivityStackSupervisor paramActivityStackSupervisor)
  {
    this.mService = paramActivityManagerService;
    this.mSupervisor = paramActivityStackSupervisor;
    this.mInterceptor = new ActivityStartInterceptor(this.mService, this.mSupervisor);
  }
  
  private int adjustLaunchFlagsToDocumentMode(ActivityRecord paramActivityRecord, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (((paramInt & 0x80000) != 0) && ((paramBoolean1) || (paramBoolean2)))
    {
      Slog.i(TAG, "Ignoring FLAG_ACTIVITY_NEW_DOCUMENT, launchMode is \"singleInstance\" or \"singleTask\"");
      i = paramInt & 0xF7F7FFFF;
      return i;
    }
    int i = paramInt;
    switch (paramActivityRecord.info.documentLaunchMode)
    {
    case 0: 
    default: 
      return paramInt;
    case 1: 
      return paramInt | 0x80000;
    case 2: 
      return paramInt | 0x80000;
    }
    return paramInt & 0xF7FFFFFF;
  }
  
  private Intent buildEphemeralInstallerIntent(Intent paramIntent1, Intent paramIntent2, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    Object localObject = new Intent(paramIntent2);
    ((Intent)localObject).setFlags(((Intent)localObject).getFlags() | 0x200);
    localObject = this.mService.getIntentSenderLocked(2, paramString2, Binder.getCallingUid(), paramInt, null, null, 1, new Intent[] { localObject }, new String[] { paramString3 }, 1409286144, null);
    Intent localIntent = new Intent(paramIntent1);
    paramString2 = this.mService.getIntentSenderLocked(2, paramString2, Binder.getCallingUid(), paramInt, null, null, 0, new Intent[] { localIntent }, new String[] { paramString3 }, 1409286144, null);
    paramInt = paramIntent1.getFlags();
    paramIntent1 = new Intent();
    paramIntent1.setFlags(0x10000000 | paramInt | 0x8000 | 0x40000000 | 0x800000);
    paramIntent1.putExtra("android.intent.extra.PACKAGE_NAME", paramString1);
    paramIntent1.putExtra("android.intent.extra.EPHEMERAL_FAILURE", new IntentSender((IIntentSender)localObject));
    paramIntent1.putExtra("android.intent.extra.EPHEMERAL_SUCCESS", new IntentSender(paramString2));
    paramIntent1.setData(paramIntent2.getData().buildUpon().clearQuery().build());
    return paramIntent1;
  }
  
  private void computeLaunchingTaskFlags()
  {
    if ((this.mSourceRecord == null) && (this.mInTask != null) && (this.mInTask.stack != null))
    {
      Intent localIntent = this.mInTask.getBaseIntent();
      ActivityRecord localActivityRecord = this.mInTask.getRootActivity();
      if (localIntent == null)
      {
        ActivityOptions.abort(this.mOptions);
        throw new IllegalArgumentException("Launching into task without base intent: " + this.mInTask);
      }
      if ((this.mLaunchSingleInstance) || (this.mLaunchSingleTask))
      {
        if (!localIntent.getComponent().equals(this.mStartActivity.intent.getComponent()))
        {
          ActivityOptions.abort(this.mOptions);
          throw new IllegalArgumentException("Trying to launch singleInstance/Task " + this.mStartActivity + " into different task " + this.mInTask);
        }
        if (localActivityRecord != null)
        {
          ActivityOptions.abort(this.mOptions);
          throw new IllegalArgumentException("Caller with mInTask " + this.mInTask + " has root " + localActivityRecord + " but target is singleInstance/Task");
        }
      }
      if (localActivityRecord == null)
      {
        this.mLaunchFlags = (this.mLaunchFlags & 0xE7F7DFFF | localIntent.getFlags() & 0x18082000);
        this.mIntent.setFlags(this.mLaunchFlags);
        this.mInTask.setIntent(this.mStartActivity);
        this.mAddingToTask = true;
        this.mReuseTask = this.mInTask;
        label285:
        if (this.mInTask == null)
        {
          if (this.mSourceRecord != null) {
            break label434;
          }
          if (((this.mLaunchFlags & 0x10000000) == 0) && (this.mInTask == null))
          {
            Slog.w(TAG, "startActivity called from non-Activity context; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + this.mIntent);
            this.mLaunchFlags |= 0x10000000;
          }
        }
      }
    }
    label434:
    do
    {
      return;
      if ((this.mLaunchFlags & 0x10000000) != 0)
      {
        this.mAddingToTask = false;
        break;
      }
      this.mAddingToTask = true;
      break;
      this.mInTask = null;
      if (((!this.mStartActivity.isResolverActivity()) && (!this.mStartActivity.noDisplay)) || (this.mSourceRecord == null) || (!this.mSourceRecord.isFreeform())) {
        break label285;
      }
      this.mAddingToTask = true;
      break label285;
      if (this.mSourceRecord.launchMode == 3)
      {
        this.mLaunchFlags |= 0x10000000;
        return;
      }
    } while ((!this.mLaunchSingleInstance) && (!this.mLaunchSingleTask));
    this.mLaunchFlags |= 0x10000000;
  }
  
  private void computeSourceStack()
  {
    if (this.mSourceRecord == null)
    {
      this.mSourceStack = null;
      return;
    }
    if (!this.mSourceRecord.finishing)
    {
      this.mSourceStack = this.mSourceRecord.task.stack;
      return;
    }
    if ((this.mLaunchFlags & 0x10000000) == 0)
    {
      Slog.w(TAG, "startActivity called from finishing " + this.mSourceRecord + "; forcing " + "Intent.FLAG_ACTIVITY_NEW_TASK for: " + this.mIntent);
      this.mLaunchFlags |= 0x10000000;
      this.mNewTaskInfo = this.mSourceRecord.info;
      this.mNewTaskIntent = this.mSourceRecord.task.intent;
    }
    this.mSourceRecord = null;
    this.mSourceStack = null;
  }
  
  private ActivityStack computeStackFocus(ActivityRecord paramActivityRecord, boolean paramBoolean, Rect paramRect, int paramInt, ActivityOptions paramActivityOptions)
  {
    TaskRecord localTaskRecord = paramActivityRecord.task;
    boolean bool;
    if (!paramActivityRecord.isApplicationActivity())
    {
      if (localTaskRecord == null) {
        break label44;
      }
      bool = localTaskRecord.isApplicationTask();
    }
    while (!bool)
    {
      return this.mSupervisor.mHomeStack;
      bool = true;
      continue;
      label44:
      bool = false;
    }
    paramActivityOptions = getLaunchStack(paramActivityRecord, paramInt, localTaskRecord, paramActivityOptions);
    if (paramActivityOptions != null) {
      return paramActivityOptions;
    }
    if ((localTaskRecord != null) && (localTaskRecord.stack != null))
    {
      paramRect = localTaskRecord.stack;
      if (paramRect.isOnHomeDisplay())
      {
        if (this.mSupervisor.mFocusedStack == paramRect) {
          break label160;
        }
        if ((ActivityManagerDebugConfig.DEBUG_FOCUS) || (ActivityManagerDebugConfig.DEBUG_STACK)) {
          Slog.d(TAG_FOCUS, "computeStackFocus: Setting focused stack to r=" + paramActivityRecord + " task=" + localTaskRecord);
        }
      }
      label160:
      while ((!ActivityManagerDebugConfig.DEBUG_FOCUS) && (!ActivityManagerDebugConfig.DEBUG_STACK)) {
        return paramRect;
      }
      Slog.d(TAG_FOCUS, "computeStackFocus: Focused stack already=" + this.mSupervisor.mFocusedStack);
      return paramRect;
    }
    paramActivityOptions = paramActivityRecord.mInitialActivityContainer;
    if (paramActivityOptions != null)
    {
      paramActivityRecord.mInitialActivityContainer = null;
      return paramActivityOptions.mStack;
    }
    paramInt = this.mSupervisor.mFocusedStack.mStackId;
    if ((paramInt != 1) && ((paramInt != 3) || (!paramActivityRecord.canGoInDockedStack())))
    {
      if (paramInt != 2) {
        break label356;
      }
      bool = paramActivityRecord.isResizeableOrForced();
    }
    while ((bool) && ((!paramBoolean) || (this.mSupervisor.mFocusedStack.mActivityContainer.isEligibleForNewTasks())))
    {
      if ((ActivityManagerDebugConfig.DEBUG_FOCUS) || (ActivityManagerDebugConfig.DEBUG_STACK)) {
        Slog.d(TAG_FOCUS, "computeStackFocus: Have a focused stack=" + this.mSupervisor.mFocusedStack);
      }
      return this.mSupervisor.mFocusedStack;
      bool = true;
      continue;
      label356:
      bool = false;
    }
    paramActivityOptions = this.mSupervisor.mHomeStack.mStacks;
    paramInt = paramActivityOptions.size() - 1;
    while (paramInt >= 0)
    {
      ActivityStack localActivityStack = (ActivityStack)paramActivityOptions.get(paramInt);
      if (!ActivityManager.StackId.isStaticStack(localActivityStack.mStackId))
      {
        if ((ActivityManagerDebugConfig.DEBUG_FOCUS) || (ActivityManagerDebugConfig.DEBUG_STACK)) {
          Slog.d(TAG_FOCUS, "computeStackFocus: Setting focused stack=" + localActivityStack);
        }
        return localActivityStack;
      }
      paramInt -= 1;
    }
    if (localTaskRecord != null) {
      paramInt = localTaskRecord.getLaunchStackId();
    }
    for (;;)
    {
      paramRect = this.mSupervisor.getStack(paramInt, true, true);
      if ((ActivityManagerDebugConfig.DEBUG_FOCUS) || (ActivityManagerDebugConfig.DEBUG_STACK)) {
        Slog.d(TAG_FOCUS, "computeStackFocus: New stack r=" + paramActivityRecord + " stackId=" + paramRect.mStackId);
      }
      return paramRect;
      if (paramRect != null) {
        paramInt = 2;
      } else {
        paramInt = 1;
      }
    }
  }
  
  private ActivityStack getLaunchStack(ActivityRecord paramActivityRecord, int paramInt, TaskRecord paramTaskRecord, ActivityOptions paramActivityOptions)
  {
    if (this.mReuseTask != null) {
      return this.mReuseTask.stack;
    }
    if (paramActivityOptions != null) {}
    for (int i = paramActivityOptions.getLaunchStackId(); isValidLaunchStackId(i, paramActivityRecord); i = -1) {
      return this.mSupervisor.getStack(i, true, true);
    }
    if (i == 3) {
      return this.mSupervisor.getStack(1, true, true);
    }
    if ((paramInt & 0x1000) == 0) {
      return null;
    }
    if (paramTaskRecord != null) {
      paramActivityOptions = paramTaskRecord.stack;
    }
    while (paramActivityOptions != this.mSupervisor.mFocusedStack)
    {
      return paramActivityOptions;
      if (paramActivityRecord.mInitialActivityContainer != null) {
        paramActivityOptions = paramActivityRecord.mInitialActivityContainer.mStack;
      } else {
        paramActivityOptions = this.mSupervisor.mFocusedStack;
      }
    }
    if ((this.mSupervisor.mFocusedStack != null) && (paramTaskRecord == this.mSupervisor.mFocusedStack.topTask())) {
      return this.mSupervisor.mFocusedStack;
    }
    if ((paramActivityOptions != null) && (paramActivityOptions.mStackId == 3)) {
      return this.mSupervisor.getStack(1, true, true);
    }
    paramTaskRecord = this.mSupervisor.getStack(3);
    if ((paramTaskRecord != null) && (paramTaskRecord.getStackVisibilityLocked(paramActivityRecord) == 0)) {
      return null;
    }
    return paramTaskRecord;
  }
  
  private ActivityRecord getReusableIntentActivity()
  {
    boolean bool3 = false;
    boolean bool2;
    label25:
    boolean bool1;
    label44:
    Object localObject;
    if (((this.mLaunchFlags & 0x10000000) != 0) && ((this.mLaunchFlags & 0x8000000) == 0))
    {
      bool2 = true;
      if ((this.mInTask != null) || (this.mStartActivity.resultTo != null)) {
        break label111;
      }
      bool1 = true;
      localObject = null;
      if ((this.mOptions == null) || (this.mOptions.getLaunchTaskId() == -1)) {
        break label118;
      }
      localObject = this.mSupervisor.anyTaskForIdLocked(this.mOptions.getLaunchTaskId());
      if (localObject == null) {
        break label116;
      }
      localObject = ((TaskRecord)localObject).getTopActivity();
    }
    label111:
    label116:
    label118:
    while (!(bool2 & bool1))
    {
      return (ActivityRecord)localObject;
      if (this.mLaunchSingleInstance) {
        break;
      }
      bool2 = this.mLaunchSingleTask;
      break label25;
      bool1 = false;
      break label44;
      return null;
    }
    if (this.mLaunchSingleInstance) {
      return this.mSupervisor.findActivityLocked(this.mIntent, this.mStartActivity.info, false);
    }
    if ((this.mLaunchFlags & 0x1000) != 0)
    {
      localObject = this.mSupervisor;
      Intent localIntent = this.mIntent;
      ActivityInfo localActivityInfo = this.mStartActivity.info;
      if (this.mLaunchSingleTask) {}
      for (bool2 = bool3;; bool2 = true) {
        return ((ActivityStackSupervisor)localObject).findActivityLocked(localIntent, localActivityInfo, bool2);
      }
    }
    return this.mSupervisor.findTaskLocked(this.mStartActivity);
  }
  
  private boolean isValidLaunchStackId(int paramInt, ActivityRecord paramActivityRecord)
  {
    if ((paramInt == -1) || (paramInt == 0)) {}
    while (!ActivityManager.StackId.isStaticStack(paramInt)) {
      return false;
    }
    if ((paramInt == 1) || ((this.mService.mSupportsMultiWindow) && (paramActivityRecord.isResizeableOrForced())))
    {
      if ((paramInt == 3) && (paramActivityRecord.canGoInDockedStack())) {
        return true;
      }
    }
    else {
      return false;
    }
    boolean bool;
    if ((paramInt != 2) || (this.mService.mSupportsFreeformWindowManagement))
    {
      if (!this.mService.mSupportsPictureInPicture) {
        break label114;
      }
      if (paramActivityRecord.supportsPictureInPicture()) {
        break label109;
      }
      bool = this.mService.mForceResizableActivities;
    }
    while ((paramInt != 4) || (bool))
    {
      return true;
      return false;
      label109:
      bool = true;
      continue;
      label114:
      bool = false;
    }
    return false;
  }
  
  private void reset()
  {
    this.mStartActivity = null;
    this.mIntent = null;
    this.mCallingUid = -1;
    this.mOptions = null;
    this.mLaunchSingleTop = false;
    this.mLaunchSingleInstance = false;
    this.mLaunchSingleTask = false;
    this.mLaunchTaskBehind = false;
    this.mLaunchFlags = 0;
    this.mLaunchBounds = null;
    this.mNotTop = null;
    this.mDoResume = false;
    this.mStartFlags = 0;
    this.mSourceRecord = null;
    this.mInTask = null;
    this.mAddingToTask = false;
    this.mReuseTask = null;
    this.mNewTaskInfo = null;
    this.mNewTaskIntent = null;
    this.mSourceStack = null;
    this.mTargetStack = null;
    this.mMovedOtherTask = false;
    this.mMovedToFront = false;
    this.mNoAnimation = false;
    this.mKeepCurTransition = false;
    this.mAvoidMoveToFront = false;
    this.mVoiceSession = null;
    this.mVoiceInteractor = null;
  }
  
  private void resumeTargetStackIfNeeded()
  {
    if (this.mDoResume)
    {
      this.mSupervisor.resumeFocusedStackTopActivityLocked(this.mTargetStack, null, this.mOptions);
      if (!this.mMovedToFront) {
        this.mSupervisor.notifyActivityDrawnForKeyguard();
      }
    }
    for (;;)
    {
      this.mSupervisor.updateUserStackLocked(this.mStartActivity.userId, this.mTargetStack);
      return;
      ActivityOptions.abort(this.mOptions);
    }
  }
  
  private void sendNewTaskResultRequestIfNeeded()
  {
    if ((this.mStartActivity.resultTo != null) && ((this.mLaunchFlags & 0x10000000) != 0) && (this.mStartActivity.resultTo.task.stack != null))
    {
      Slog.w(TAG, "Activity is launching as a new task, so cancelling activity result.");
      this.mStartActivity.resultTo.task.stack.sendActivityResultLocked(-1, this.mStartActivity.resultTo, this.mStartActivity.resultWho, this.mStartActivity.requestCode, 0, null);
      this.mStartActivity.resultTo = null;
    }
  }
  
  private void setInitialState(ActivityRecord paramActivityRecord1, ActivityOptions paramActivityOptions, TaskRecord paramTaskRecord, boolean paramBoolean, int paramInt, ActivityRecord paramActivityRecord2, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor)
  {
    reset();
    this.mStartActivity = paramActivityRecord1;
    this.mIntent = paramActivityRecord1.intent;
    this.mOptions = paramActivityOptions;
    this.mCallingUid = paramActivityRecord1.launchedFromUid;
    this.mSourceRecord = paramActivityRecord2;
    this.mVoiceSession = paramIVoiceInteractionSession;
    this.mVoiceInteractor = paramIVoiceInteractor;
    this.mLaunchBounds = getOverrideBounds(paramActivityRecord1, paramActivityOptions, paramTaskRecord);
    boolean bool;
    if (paramActivityRecord1.launchMode == 1)
    {
      bool = true;
      this.mLaunchSingleTop = bool;
      if (paramActivityRecord1.launchMode != 3) {
        break label505;
      }
      bool = true;
      label87:
      this.mLaunchSingleInstance = bool;
      if (paramActivityRecord1.launchMode != 2) {
        break label511;
      }
      bool = true;
      label104:
      this.mLaunchSingleTask = bool;
      this.mLaunchFlags = adjustLaunchFlagsToDocumentMode(paramActivityRecord1, this.mLaunchSingleInstance, this.mLaunchSingleTask, this.mIntent.getFlags());
      if ((paramActivityRecord1.mLaunchTaskBehind) && (!this.mLaunchSingleTask)) {
        break label517;
      }
      label148:
      bool = false;
      label151:
      this.mLaunchTaskBehind = bool;
      sendNewTaskResultRequestIfNeeded();
      if (((this.mLaunchFlags & 0x80000) != 0) && (paramActivityRecord1.resultTo == null)) {
        this.mLaunchFlags |= 0x10000000;
      }
      if (((this.mLaunchFlags & 0x10000000) != 0) && ((this.mLaunchTaskBehind) || (paramActivityRecord1.info.documentLaunchMode == 2))) {
        this.mLaunchFlags |= 0x8000000;
      }
      paramActivityOptions = this.mSupervisor;
      if ((this.mLaunchFlags & 0x40000) != 0) {
        break label546;
      }
      bool = true;
      label248:
      paramActivityOptions.mUserLeaving = bool;
      if (ActivityManagerDebugConfig.DEBUG_USER_LEAVING) {
        Slog.v(TAG_USER_LEAVING, "startActivity() => mUserLeaving=" + this.mSupervisor.mUserLeaving);
      }
      this.mDoResume = paramBoolean;
      if ((!paramBoolean) || (!this.mSupervisor.okToShowLocked(paramActivityRecord1))) {
        break label552;
      }
      label315:
      if ((this.mOptions != null) && (this.mOptions.getLaunchTaskId() != -1) && (this.mOptions.getTaskOverlay()))
      {
        paramActivityRecord1.mTaskOverlay = true;
        paramActivityOptions = this.mSupervisor.anyTaskForIdLocked(this.mOptions.getLaunchTaskId());
        if (paramActivityOptions == null) {
          break label565;
        }
        paramActivityOptions = paramActivityOptions.getTopActivity();
        label372:
        if ((paramActivityOptions != null) && (!paramActivityOptions.visible)) {
          break label570;
        }
      }
      label383:
      if ((this.mLaunchFlags & 0x1000000) == 0) {
        break label583;
      }
      paramActivityOptions = paramActivityRecord1;
      label396:
      this.mNotTop = paramActivityOptions;
      this.mInTask = paramTaskRecord;
      if ((paramTaskRecord != null) && (!paramTaskRecord.inRecents)) {
        break label588;
      }
      label417:
      this.mStartFlags = paramInt;
      if ((paramInt & 0x1) != 0)
      {
        paramActivityOptions = paramActivityRecord2;
        if (paramActivityRecord2 == null) {
          paramActivityOptions = this.mSupervisor.mFocusedStack.topRunningNonDelayedActivityLocked(this.mNotTop);
        }
        if (!paramActivityOptions.realActivity.equals(paramActivityRecord1.realActivity)) {
          this.mStartFlags &= 0xFFFFFFFE;
        }
      }
      if ((this.mLaunchFlags & 0x10000) == 0) {
        break label623;
      }
    }
    label505:
    label511:
    label517:
    label546:
    label552:
    label565:
    label570:
    label583:
    label588:
    label623:
    for (paramBoolean = true;; paramBoolean = false)
    {
      this.mNoAnimation = paramBoolean;
      return;
      bool = false;
      break;
      bool = false;
      break label87;
      bool = false;
      break label104;
      if (this.mLaunchSingleInstance) {
        break label148;
      }
      if ((this.mLaunchFlags & 0x80000) != 0)
      {
        bool = true;
        break label151;
      }
      bool = false;
      break label151;
      bool = false;
      break label248;
      paramActivityRecord1.delayedResume = true;
      this.mDoResume = false;
      break label315;
      paramActivityOptions = null;
      break label372;
      this.mDoResume = false;
      this.mAvoidMoveToFront = true;
      break label383;
      paramActivityOptions = null;
      break label396;
      Slog.w(TAG, "Starting activity in task not in recents: " + paramTaskRecord);
      this.mInTask = null;
      break label417;
    }
  }
  
  private ActivityRecord setTargetStackAndMoveToFrontIfNeeded(ActivityRecord paramActivityRecord)
  {
    int i = 0;
    this.mTargetStack = paramActivityRecord.task.stack;
    this.mTargetStack.mLastPausedActivity = null;
    ActivityStack localActivityStack = this.mSupervisor.getFocusedStack();
    Object localObject;
    if (localActivityStack == null)
    {
      localObject = null;
      if ((localObject != null) && ((((ActivityRecord)localObject).task != paramActivityRecord.task) || (((ActivityRecord)localObject).task != localActivityStack.topTask())) && (!this.mAvoidMoveToFront)) {
        break label197;
      }
    }
    label197:
    do
    {
      if ((!this.mMovedToFront) && (this.mDoResume))
      {
        if (ActivityManagerDebugConfig.DEBUG_TASKS) {
          Slog.d(ActivityStackSupervisor.TAG_TASKS, "Bring to front target: " + this.mTargetStack + " from " + paramActivityRecord);
        }
        this.mTargetStack.moveToFront("intentActivityFound");
      }
      this.mSupervisor.handleNonResizableTaskIfNeeded(paramActivityRecord.task, -1, this.mTargetStack.mStackId);
      if ((this.mLaunchFlags & 0x200000) == 0) {
        return paramActivityRecord;
      }
      return this.mTargetStack.resetTaskIfNeededLocked(paramActivityRecord, this.mStartActivity);
      localObject = localActivityStack.topRunningNonDelayedActivityLocked(this.mNotTop);
      break;
      this.mStartActivity.intent.addFlags(4194304);
    } while ((this.mSourceRecord != null) && ((this.mSourceStack.topActivity() == null) || (this.mSourceStack.topActivity().task != this.mSourceRecord.task)));
    if ((this.mLaunchTaskBehind) && (this.mSourceRecord != null)) {
      paramActivityRecord.setTaskToAffiliateWith(this.mSourceRecord.task);
    }
    this.mMovedOtherTask = true;
    if ((this.mLaunchFlags & 0x10008000) == 268468224) {
      i = 1;
    }
    if (i == 0)
    {
      localObject = getLaunchStack(this.mStartActivity, this.mLaunchFlags, this.mStartActivity.task, this.mOptions);
      if ((localObject != null) && (localObject != this.mTargetStack)) {
        break label390;
      }
      this.mTargetStack.moveTaskToFrontLocked(paramActivityRecord.task, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "bringingFoundTaskToFront");
      this.mMovedToFront = true;
    }
    label390:
    while ((((ActivityStack)localObject).mStackId != 3) && (((ActivityStack)localObject).mStackId != 1))
    {
      this.mOptions = null;
      updateTaskReturnToType(paramActivityRecord.task, this.mLaunchFlags, localActivityStack);
      break;
    }
    if ((this.mLaunchFlags & 0x1000) != 0) {
      this.mSupervisor.moveTaskToStackLocked(paramActivityRecord.task.taskId, ((ActivityStack)localObject).mStackId, true, true, "launchToSide", true);
    }
    for (;;)
    {
      this.mMovedToFront = true;
      break;
      this.mTargetStack.moveTaskToFrontLocked(paramActivityRecord.task, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "bringToFrontInsteadOfAdjacentLaunch");
    }
    return paramActivityRecord;
  }
  
  private int setTaskFromInTask()
  {
    if (this.mLaunchBounds != null)
    {
      this.mInTask.updateOverrideConfiguration(this.mLaunchBounds);
      int j = this.mInTask.getLaunchStackId();
      int i = j;
      if (j != this.mInTask.stack.mStackId) {
        i = this.mSupervisor.moveTaskToStackUncheckedLocked(this.mInTask, j, true, false, "inTaskToFront").mStackId;
      }
      if (ActivityManager.StackId.resizeStackWithLaunchBounds(i)) {
        this.mService.resizeStack(i, this.mLaunchBounds, true, false, true, -1);
      }
    }
    this.mTargetStack = this.mInTask.stack;
    this.mTargetStack.moveTaskToFrontLocked(this.mInTask, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "inTaskToFront");
    ActivityRecord localActivityRecord = this.mInTask.getTopActivity();
    if ((localActivityRecord != null) && (localActivityRecord.realActivity.equals(this.mStartActivity.realActivity)) && (localActivityRecord.userId == this.mStartActivity.userId) && (((this.mLaunchFlags & 0x20000000) != 0) || (this.mLaunchSingleTop) || (this.mLaunchSingleTask)))
    {
      ActivityStack.logStartActivity(30003, localActivityRecord, localActivityRecord.task);
      if ((this.mStartFlags & 0x1) != 0) {
        return 1;
      }
      localActivityRecord.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
      return 3;
    }
    if (!this.mAddingToTask)
    {
      ActivityOptions.abort(this.mOptions);
      return 2;
    }
    this.mStartActivity.setTask(this.mInTask, null);
    if (ActivityManagerDebugConfig.DEBUG_TASKS) {
      Slog.v(ActivityStackSupervisor.TAG_TASKS, "Starting new activity " + this.mStartActivity + " in explicit task " + this.mStartActivity.task);
    }
    return 0;
  }
  
  private void setTaskFromIntentActivity(ActivityRecord paramActivityRecord)
  {
    boolean bool = false;
    if ((this.mLaunchFlags & 0x10008000) == 268468224)
    {
      paramActivityRecord.task.performClearTaskLocked();
      paramActivityRecord.task.setIntent(this.mStartActivity);
      this.mReuseTask = paramActivityRecord.task;
      this.mMovedOtherTask = true;
    }
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if (((this.mLaunchFlags & 0x4000000) == 0) && (!this.mLaunchSingleInstance) && (!this.mLaunchSingleTask)) {
              break;
            }
          } while (paramActivityRecord.task.performClearTaskLocked(this.mStartActivity, this.mLaunchFlags) != null);
          this.mAddingToTask = true;
          this.mSourceRecord = paramActivityRecord;
          paramActivityRecord = this.mSourceRecord.task;
        } while ((paramActivityRecord == null) || (paramActivityRecord.stack != null));
        this.mTargetStack = computeStackFocus(this.mSourceRecord, false, null, this.mLaunchFlags, this.mOptions);
        ActivityStack localActivityStack = this.mTargetStack;
        if (this.mLaunchTaskBehind) {}
        for (;;)
        {
          localActivityStack.addTask(paramActivityRecord, bool, "startActivityUnchecked");
          return;
          bool = true;
        }
        if (!this.mStartActivity.realActivity.equals(paramActivityRecord.task.realActivity)) {
          break;
        }
        if ((((this.mLaunchFlags & 0x20000000) != 0) || (this.mLaunchSingleTop)) && (paramActivityRecord.realActivity.equals(this.mStartActivity.realActivity)))
        {
          ActivityStack.logStartActivity(30003, this.mStartActivity, paramActivityRecord.task);
          if (paramActivityRecord.frontOfTask) {
            paramActivityRecord.task.setIntent(this.mStartActivity);
          }
          paramActivityRecord.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
          return;
        }
      } while (paramActivityRecord.task.isSameIntentFilter(this.mStartActivity));
      this.mAddingToTask = true;
      this.mSourceRecord = paramActivityRecord;
      return;
      if ((this.mLaunchFlags & 0x200000) == 0)
      {
        this.mAddingToTask = true;
        this.mSourceRecord = paramActivityRecord;
        return;
      }
    } while (paramActivityRecord.task.rootWasReset);
    paramActivityRecord.task.setIntent(this.mStartActivity);
  }
  
  private void setTaskFromReuseOrCreateNewTask(TaskRecord paramTaskRecord)
  {
    this.mTargetStack = computeStackFocus(this.mStartActivity, true, this.mLaunchBounds, this.mLaunchFlags, this.mOptions);
    if (this.mReuseTask == null)
    {
      ActivityStack localActivityStack = this.mTargetStack;
      int i = this.mSupervisor.getNextTaskIdForUserLocked(this.mStartActivity.userId);
      Object localObject;
      Intent localIntent;
      label79:
      boolean bool;
      if (this.mNewTaskInfo != null)
      {
        localObject = this.mNewTaskInfo;
        if (this.mNewTaskIntent == null) {
          break label230;
        }
        localIntent = this.mNewTaskIntent;
        IVoiceInteractionSession localIVoiceInteractionSession = this.mVoiceSession;
        IVoiceInteractor localIVoiceInteractor = this.mVoiceInteractor;
        if (!this.mLaunchTaskBehind) {
          break label239;
        }
        bool = false;
        label100:
        localObject = localActivityStack.createTaskRecord(i, (ActivityInfo)localObject, localIntent, localIVoiceInteractionSession, localIVoiceInteractor, bool);
        this.mStartActivity.setTask((TaskRecord)localObject, paramTaskRecord);
        if (this.mLaunchBounds != null)
        {
          i = this.mTargetStack.mStackId;
          if (!ActivityManager.StackId.resizeStackWithLaunchBounds(i)) {
            break label244;
          }
          this.mService.resizeStack(i, this.mLaunchBounds, true, false, true, -1);
        }
      }
      for (;;)
      {
        if (ActivityManagerDebugConfig.DEBUG_TASKS) {
          Slog.v(ActivityStackSupervisor.TAG_TASKS, "Starting new activity " + this.mStartActivity + " in new task " + this.mStartActivity.task);
        }
        return;
        localObject = this.mStartActivity.info;
        break;
        label230:
        localIntent = this.mIntent;
        break label79;
        label239:
        bool = true;
        break label100;
        label244:
        this.mStartActivity.task.updateOverrideConfiguration(this.mLaunchBounds);
      }
    }
    this.mStartActivity.setTask(this.mReuseTask, paramTaskRecord);
  }
  
  private int setTaskFromSourceRecord()
  {
    TaskRecord localTaskRecord = this.mSourceRecord.task;
    int i;
    if (localTaskRecord.stack.topTask() != localTaskRecord)
    {
      i = 1;
      if (i != 0) {
        this.mTargetStack = getLaunchStack(this.mStartActivity, this.mLaunchFlags, this.mStartActivity.task, this.mOptions);
      }
      if (this.mTargetStack != null) {
        break label215;
      }
      this.mTargetStack = localTaskRecord.stack;
      label67:
      if (this.mDoResume) {
        this.mTargetStack.moveToFront("sourceStackToFront");
      }
      if ((this.mTargetStack.topTask() != localTaskRecord) && (!this.mAvoidMoveToFront)) {
        break label254;
      }
    }
    ActivityRecord localActivityRecord;
    for (;;)
    {
      if ((this.mAddingToTask) || ((this.mLaunchFlags & 0x4000000) == 0)) {
        break label283;
      }
      localActivityRecord = localTaskRecord.performClearTaskLocked(this.mStartActivity, this.mLaunchFlags);
      this.mKeepCurTransition = true;
      if (localActivityRecord == null) {
        break label390;
      }
      ActivityStack.logStartActivity(30003, this.mStartActivity, localActivityRecord.task);
      localActivityRecord.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
      this.mTargetStack.mLastPausedActivity = null;
      if (this.mDoResume) {
        this.mSupervisor.resumeFocusedStackTopActivityLocked();
      }
      ActivityOptions.abort(this.mOptions);
      return 3;
      i = 0;
      break;
      label215:
      if (this.mTargetStack == localTaskRecord.stack) {
        break label67;
      }
      this.mSupervisor.moveTaskToStackLocked(localTaskRecord.taskId, this.mTargetStack.mStackId, true, true, "launchToSide", false);
      break label67;
      label254:
      this.mTargetStack.moveTaskToFrontLocked(localTaskRecord, this.mNoAnimation, this.mOptions, this.mStartActivity.appTimeTracker, "sourceTaskToFront");
    }
    label283:
    if ((!this.mAddingToTask) && ((this.mLaunchFlags & 0x20000) != 0))
    {
      localActivityRecord = localTaskRecord.findActivityInHistoryLocked(this.mStartActivity);
      if (localActivityRecord != null)
      {
        localTaskRecord = localActivityRecord.task;
        localTaskRecord.moveActivityToFrontLocked(localActivityRecord);
        localActivityRecord.updateOptionsLocked(this.mOptions);
        ActivityStack.logStartActivity(30003, this.mStartActivity, localTaskRecord);
        localActivityRecord.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
        this.mTargetStack.mLastPausedActivity = null;
        if (this.mDoResume) {
          this.mSupervisor.resumeFocusedStackTopActivityLocked();
        }
        return 3;
      }
    }
    label390:
    this.mStartActivity.setTask(localTaskRecord, null);
    if (ActivityManagerDebugConfig.DEBUG_TASKS) {
      Slog.v(ActivityStackSupervisor.TAG_TASKS, "Starting new activity " + this.mStartActivity + " in existing task " + this.mStartActivity.task + " from source " + this.mSourceRecord);
    }
    return 0;
  }
  
  private void setTaskToCurrentTopOrCreateNewTask()
  {
    this.mTargetStack = computeStackFocus(this.mStartActivity, false, null, this.mLaunchFlags, this.mOptions);
    if (this.mDoResume) {
      this.mTargetStack.moveToFront("addingToTopTask");
    }
    Object localObject = this.mTargetStack.topActivity();
    if (localObject != null) {}
    for (localObject = ((ActivityRecord)localObject).task;; localObject = this.mTargetStack.createTaskRecord(this.mSupervisor.getNextTaskIdForUserLocked(this.mStartActivity.userId), this.mStartActivity.info, this.mIntent, null, null, true))
    {
      this.mStartActivity.setTask((TaskRecord)localObject, null);
      this.mWindowManager.moveTaskToTop(this.mStartActivity.task.taskId);
      if (ActivityManagerDebugConfig.DEBUG_TASKS) {
        Slog.v(ActivityStackSupervisor.TAG_TASKS, "Starting new activity " + this.mStartActivity + " in new guessed " + this.mStartActivity.task);
      }
      return;
    }
  }
  
  private int startActivityUnchecked(ActivityRecord paramActivityRecord1, ActivityRecord paramActivityRecord2, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor, int paramInt, boolean paramBoolean, ActivityOptions paramActivityOptions, TaskRecord paramTaskRecord)
  {
    setInitialState(paramActivityRecord1, paramActivityOptions, paramTaskRecord, paramBoolean, paramInt, paramActivityRecord2, paramIVoiceInteractionSession, paramIVoiceInteractor);
    computeLaunchingTaskFlags();
    computeSourceStack();
    this.mIntent.setFlags(this.mLaunchFlags);
    this.mReusedActivity = getReusableIntentActivity();
    if (this.mOptions != null)
    {
      paramInt = this.mOptions.getLaunchStackId();
      if (this.mReusedActivity == null) {
        break label352;
      }
      paramActivityRecord1 = this.mSupervisor;
      paramActivityRecord2 = this.mReusedActivity.task;
      if ((this.mLaunchFlags & 0x10008000) != 268468224) {
        break label133;
      }
    }
    label133:
    for (paramBoolean = true;; paramBoolean = false)
    {
      if (!paramActivityRecord1.isLockTaskModeViolation(paramActivityRecord2, paramBoolean)) {
        break label139;
      }
      this.mSupervisor.showLockTaskToast();
      Slog.e(TAG, "startActivityUnchecked: Attempt to violate Lock Task Mode");
      return 5;
      paramInt = -1;
      break;
    }
    label139:
    if (this.mStartActivity.task == null) {
      this.mStartActivity.task = this.mReusedActivity.task;
    }
    if (this.mReusedActivity.task.intent == null) {
      this.mReusedActivity.task.setIntent(this.mStartActivity);
    }
    if (((this.mLaunchFlags & 0x4000000) != 0) || (this.mLaunchSingleInstance) || (this.mLaunchSingleTask))
    {
      paramActivityRecord1 = this.mReusedActivity.task.performClearTaskForReuseLocked(this.mStartActivity, this.mLaunchFlags);
      if (paramActivityRecord1 != null)
      {
        if (paramActivityRecord1.frontOfTask) {
          paramActivityRecord1.task.setIntent(this.mStartActivity);
        }
        ActivityStack.logStartActivity(30003, this.mStartActivity, paramActivityRecord1.task);
        paramActivityRecord1.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
      }
    }
    sendPowerHintForLaunchStartIfNeeded(false);
    this.mReusedActivity = setTargetStackAndMoveToFrontIfNeeded(this.mReusedActivity);
    if ((this.mStartFlags & 0x1) != 0)
    {
      resumeTargetStackIfNeeded();
      return 1;
    }
    setTaskFromIntentActivity(this.mReusedActivity);
    if ((!this.mAddingToTask) && (this.mReuseTask == null))
    {
      resumeTargetStackIfNeeded();
      return 2;
    }
    label352:
    if (this.mStartActivity.packageName == null)
    {
      if ((this.mStartActivity.resultTo != null) && (this.mStartActivity.resultTo.task.stack != null)) {
        this.mStartActivity.resultTo.task.stack.sendActivityResultLocked(-1, this.mStartActivity.resultTo, this.mStartActivity.resultWho, this.mStartActivity.requestCode, 0, null);
      }
      ActivityOptions.abort(this.mOptions);
      return -2;
    }
    paramActivityRecord2 = this.mSupervisor.mFocusedStack;
    paramActivityRecord1 = paramActivityRecord2.topRunningNonDelayedActivityLocked(this.mNotTop);
    if ((paramActivityRecord1 != null) && (this.mStartActivity.resultTo == null) && (paramActivityRecord1.realActivity.equals(this.mStartActivity.realActivity)) && (paramActivityRecord1.userId == this.mStartActivity.userId) && (paramActivityRecord1.app != null) && (paramActivityRecord1.app.thread != null)) {
      if (((this.mLaunchFlags & 0x20000000) == 0) && (!this.mLaunchSingleTop)) {
        paramBoolean = this.mLaunchSingleTask;
      }
    }
    while (paramBoolean)
    {
      ActivityStack.logStartActivity(30003, paramActivityRecord1, paramActivityRecord1.task);
      paramActivityRecord2.mLastPausedActivity = null;
      if (this.mDoResume) {
        this.mSupervisor.resumeFocusedStackTopActivityLocked();
      }
      ActivityOptions.abort(this.mOptions);
      if ((this.mStartFlags & 0x1) != 0)
      {
        return 1;
        paramBoolean = true;
        continue;
        paramBoolean = false;
      }
      else
      {
        paramActivityRecord1.deliverNewIntentLocked(this.mCallingUid, this.mStartActivity.intent, this.mStartActivity.launchedFromPackage);
        this.mSupervisor.handleNonResizableTaskIfNeeded(paramActivityRecord1.task, paramInt, paramActivityRecord2.mStackId);
        return 3;
      }
    }
    paramBoolean = false;
    if ((this.mLaunchTaskBehind) && (this.mSourceRecord != null))
    {
      paramActivityRecord1 = this.mSourceRecord.task;
      if ((this.mStartActivity.resultTo == null) && (this.mInTask == null) && (!this.mAddingToTask)) {
        break label758;
      }
    }
    label758:
    int i;
    for (;;)
    {
      if (this.mSourceRecord != null) {
        if (this.mSupervisor.isLockTaskModeViolation(this.mSourceRecord.task))
        {
          Slog.e(TAG, "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
          return 5;
          paramActivityRecord1 = null;
          break;
          if ((this.mLaunchFlags & 0x10000000) == 0) {
            continue;
          }
          boolean bool = true;
          setTaskFromReuseOrCreateNewTask(paramActivityRecord1);
          if (this.mSupervisor.isLockTaskModeViolation(this.mStartActivity.task))
          {
            Slog.e(TAG, "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
            return 5;
          }
          paramBoolean = bool;
          if (!this.mMovedOtherTask)
          {
            paramIVoiceInteractionSession = this.mStartActivity.task;
            i = this.mLaunchFlags;
            paramActivityRecord1 = paramActivityRecord2;
            if (paramInt != -1) {
              paramActivityRecord1 = this.mTargetStack;
            }
            updateTaskReturnToType(paramIVoiceInteractionSession, i, paramActivityRecord1);
            paramBoolean = bool;
          }
          this.mService.grantUriPermissionFromIntentLocked(this.mCallingUid, this.mStartActivity.packageName, this.mIntent, this.mStartActivity.getUriPermissionsLocked(), this.mStartActivity.userId);
          if ((this.mSourceRecord != null) && (this.mSourceRecord.isRecentsActivity())) {
            this.mStartActivity.task.setTaskToReturnTo(2);
          }
          if (paramBoolean) {
            EventLog.writeEvent(30004, new Object[] { Integer.valueOf(this.mStartActivity.userId), Integer.valueOf(this.mStartActivity.task.taskId) });
          }
          ActivityStack.logStartActivity(30005, this.mStartActivity, this.mStartActivity.task);
          this.mTargetStack.mLastPausedActivity = null;
          sendPowerHintForLaunchStartIfNeeded(false);
          this.mTargetStack.startActivityLocked(this.mStartActivity, paramBoolean, this.mKeepCurTransition, this.mOptions);
          if (!this.mDoResume) {
            break label1275;
          }
          if (!this.mLaunchTaskBehind) {
            this.mService.setFocusedActivityLocked(this.mStartActivity, "startedActivity");
          }
          paramActivityRecord1 = this.mStartActivity.task.topRunningActivityLocked();
          if ((this.mTargetStack.isFocusable()) && ((paramActivityRecord1 == null) || (!paramActivityRecord1.mTaskOverlay) || (this.mStartActivity == paramActivityRecord1))) {
            break label1252;
          }
          this.mTargetStack.ensureActivitiesVisibleLocked(null, 0, false);
          this.mWindowManager.executeAppTransition();
        }
      }
    }
    for (;;)
    {
      this.mSupervisor.updateUserStackLocked(this.mStartActivity.userId, this.mTargetStack);
      this.mSupervisor.handleNonResizableTaskIfNeeded(this.mStartActivity.task, paramInt, this.mTargetStack.mStackId);
      return 0;
      i = setTaskFromSourceRecord();
      if (i == 0) {
        break;
      }
      return i;
      if (this.mInTask != null)
      {
        if (this.mSupervisor.isLockTaskModeViolation(this.mInTask))
        {
          Slog.e(TAG, "Attempted Lock Task Mode violation mStartActivity=" + this.mStartActivity);
          return 5;
        }
        i = setTaskFromInTask();
        if (i == 0) {
          break;
        }
        return i;
      }
      setTaskToCurrentTopOrCreateNewTask();
      break;
      label1252:
      this.mSupervisor.resumeFocusedStackTopActivityLocked(this.mTargetStack, this.mStartActivity, this.mOptions);
      continue;
      label1275:
      this.mTargetStack.addRecentActivityLocked(this.mStartActivity);
    }
  }
  
  private void updateTaskReturnToType(TaskRecord paramTaskRecord, int paramInt, ActivityStack paramActivityStack)
  {
    if ((paramInt & 0x10004000) == 268451840)
    {
      paramTaskRecord.setTaskToReturnTo(1);
      return;
    }
    if ((paramActivityStack == null) || (paramActivityStack.mStackId == 0))
    {
      paramTaskRecord.setTaskToReturnTo(1);
      return;
    }
    paramTaskRecord.setTaskToReturnTo(0);
  }
  
  final void doPendingActivityLaunchesLocked(boolean paramBoolean)
  {
    if (!this.mPendingActivityLaunches.isEmpty())
    {
      ActivityStackSupervisor.PendingActivityLaunch localPendingActivityLaunch = (ActivityStackSupervisor.PendingActivityLaunch)this.mPendingActivityLaunches.remove(0);
      if (paramBoolean) {}
      for (boolean bool = this.mPendingActivityLaunches.isEmpty();; bool = false)
      {
        try
        {
          int i = startActivityUnchecked(localPendingActivityLaunch.r, localPendingActivityLaunch.sourceRecord, null, null, localPendingActivityLaunch.startFlags, bool, null, null);
          postStartActivityUncheckedProcessing(localPendingActivityLaunch.r, i, this.mSupervisor.mFocusedStack.mStackId, this.mSourceRecord, this.mTargetStack);
        }
        catch (Exception localException)
        {
          Slog.e(TAG, "Exception during pending activity launch pal=" + localPendingActivityLaunch, localException);
          localPendingActivityLaunch.sendErrorResult(localException.getMessage());
        }
        break;
      }
    }
  }
  
  Rect getOverrideBounds(ActivityRecord paramActivityRecord, ActivityOptions paramActivityOptions, TaskRecord paramTaskRecord)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramActivityOptions != null) {
      if (!paramActivityRecord.isResizeable())
      {
        localObject1 = localObject2;
        if (paramTaskRecord != null)
        {
          localObject1 = localObject2;
          if (!paramTaskRecord.isResizeable()) {}
        }
      }
      else
      {
        localObject1 = localObject2;
        if (this.mSupervisor.canUseActivityOptionsLaunchBounds(paramActivityOptions, paramActivityOptions.getLaunchStackId())) {
          localObject1 = TaskRecord.validateBounds(paramActivityOptions.getLaunchBounds());
        }
      }
    }
    return (Rect)localObject1;
  }
  
  void postStartActivityUncheckedProcessing(ActivityRecord paramActivityRecord1, int paramInt1, int paramInt2, ActivityRecord paramActivityRecord2, ActivityStack paramActivityStack)
  {
    Object localObject = null;
    if (paramInt1 < 0)
    {
      this.mSupervisor.notifyActivityDrawnForKeyguard();
      return;
    }
    int i;
    label65:
    int j;
    if ((paramInt1 != 2) || (this.mSupervisor.mWaitingActivityLaunched.isEmpty()))
    {
      i = -1;
      if ((paramActivityRecord1.task == null) || (paramActivityRecord1.task.stack == null)) {
        break label183;
      }
      i = paramActivityRecord1.task.stack.mStackId;
      if ((paramActivityRecord2 == null) || (!paramActivityRecord2.noDisplay)) {
        break label206;
      }
      if (paramActivityRecord2.task.getTaskToReturnTo() != 1) {
        break label200;
      }
      j = 1;
    }
    for (;;)
    {
      if ((i != 3) || ((paramInt2 != 0) && (j == 0))) {
        break label212;
      }
      paramActivityRecord2 = this.mSupervisor.getStack(0);
      paramActivityRecord1 = (ActivityRecord)localObject;
      if (paramActivityRecord2 != null) {
        paramActivityRecord1 = paramActivityRecord2.topRunningActivityLocked();
      }
      if ((paramActivityRecord1 != null) && (paramActivityRecord1.mActivityType == 2)) {
        break label212;
      }
      if (ActivityManagerDebugConfig.DEBUG_RECENTS) {
        Slog.d(TAG, "Scheduling recents launch.");
      }
      this.mWindowManager.showRecentApps(true);
      return;
      this.mSupervisor.reportTaskToFrontNoLaunch(this.mStartActivity);
      break;
      label183:
      if (this.mTargetStack == null) {
        break label65;
      }
      i = paramActivityStack.mStackId;
      break label65;
      label200:
      j = 0;
      continue;
      label206:
      j = 0;
    }
    label212:
    if ((i == 4) && ((paramInt1 == 2) || (paramInt1 == 3)))
    {
      this.mService.notifyPinnedActivityRestartAttemptLocked();
      return;
    }
  }
  
  void removePendingActivityLaunchesLocked(ActivityStack paramActivityStack)
  {
    int i = this.mPendingActivityLaunches.size() - 1;
    while (i >= 0)
    {
      if (((ActivityStackSupervisor.PendingActivityLaunch)this.mPendingActivityLaunches.get(i)).stack == paramActivityStack) {
        this.mPendingActivityLaunches.remove(i);
      }
      i -= 1;
    }
  }
  
  void sendPowerHintForLaunchEndIfNeeded()
  {
    if ((this.mPowerHintSent) && (this.mService.mLocalPowerManager != null))
    {
      this.mService.mLocalPowerManager.powerHint(8, 0);
      this.mPowerHintSent = false;
    }
  }
  
  void sendPowerHintForLaunchStartIfNeeded(boolean paramBoolean)
  {
    Object localObject = this.mSupervisor.getFocusedStack();
    if (localObject == null) {}
    for (localObject = null;; localObject = ((ActivityStack)localObject).topRunningNonDelayedActivityLocked(this.mNotTop))
    {
      if (((paramBoolean) || ((!this.mPowerHintSent) && (localObject != null) && (((ActivityRecord)localObject).task != null) && (this.mStartActivity != null) && (((ActivityRecord)localObject).task != this.mStartActivity.task))) && (this.mService.mLocalPowerManager != null))
      {
        this.mService.mLocalPowerManager.powerHint(8, 1);
        this.mPowerHintSent = true;
      }
      return;
    }
  }
  
  void setWindowManager(WindowManagerService paramWindowManagerService)
  {
    this.mWindowManager = paramWindowManagerService;
  }
  
  void showConfirmDeviceCredential(int paramInt)
  {
    Object localObject1 = this.mSupervisor.getStack(1);
    Object localObject2 = this.mSupervisor.getStack(2);
    if ((localObject1 != null) && (((ActivityStack)localObject1).getStackVisibilityLocked(null) != 0)) {}
    while (localObject1 == null)
    {
      return;
      if ((localObject2 != null) && (((ActivityStack)localObject2).getStackVisibilityLocked(null) != 0)) {
        localObject1 = localObject2;
      } else {
        localObject1 = this.mSupervisor.getStack(0);
      }
    }
    localObject2 = ((KeyguardManager)this.mService.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, paramInt);
    if (localObject2 == null) {
      return;
    }
    Object localObject3 = ((ActivityStack)localObject1).topRunningActivityLocked();
    if (localObject3 != null)
    {
      localObject1 = this.mService;
      String str = ((ActivityRecord)localObject3).launchedFromPackage;
      paramInt = ((ActivityRecord)localObject3).launchedFromUid;
      int i = ((ActivityRecord)localObject3).userId;
      Intent localIntent = ((ActivityRecord)localObject3).intent;
      localObject3 = ((ActivityRecord)localObject3).resolvedType;
      ((Intent)localObject2).putExtra("android.intent.extra.INTENT", new IntentSender(((ActivityManagerService)localObject1).getIntentSenderLocked(2, str, paramInt, i, null, null, 0, new Intent[] { localIntent }, new String[] { localObject3 }, 1409286144, null)));
      startConfirmCredentialIntent((Intent)localObject2);
    }
  }
  
  final int startActivities(IApplicationThread paramIApplicationThread, int paramInt1, String paramString, Intent[] paramArrayOfIntent, String[] paramArrayOfString, IBinder paramIBinder, Bundle paramBundle, int paramInt2)
  {
    if (paramArrayOfIntent == null) {
      throw new NullPointerException("intents is null");
    }
    if (paramArrayOfString == null) {
      throw new NullPointerException("resolvedTypes is null");
    }
    if (paramArrayOfIntent.length != paramArrayOfString.length) {
      throw new IllegalArgumentException("intents are length different than resolvedTypes");
    }
    int k = Binder.getCallingPid();
    int m = Binder.getCallingUid();
    int j;
    int i;
    long l;
    ActivityRecord[] arrayOfActivityRecord;
    label101:
    Object localObject;
    if (paramInt1 >= 0)
    {
      j = -1;
      i = paramInt1;
      paramInt1 = j;
      l = Binder.clearCallingIdentity();
      try
      {
        synchronized (this.mService)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          arrayOfActivityRecord = new ActivityRecord[1];
          j = 0;
          if (j >= paramArrayOfIntent.length) {
            break label362;
          }
          localObject = paramArrayOfIntent[j];
          if (localObject == null) {
            break label375;
          }
          if ((localObject != null) && (((Intent)localObject).hasFileDescriptors())) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
          }
        }
        if (((Intent)localObject).getComponent() == null) {}
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    for (boolean bool = true;; bool = false)
    {
      Intent localIntent = new Intent((Intent)localObject);
      localObject = this.mSupervisor.resolveActivity(localIntent, paramArrayOfString[j], 0, null, paramInt2);
      ActivityInfo localActivityInfo = this.mService.getActivityInfoForUser((ActivityInfo)localObject, paramInt2);
      if ((localActivityInfo != null) && ((localActivityInfo.applicationInfo.privateFlags & 0x2) != 0)) {
        throw new IllegalArgumentException("FLAG_CANT_SAVE_STATE not supported here");
      }
      if (j == paramArrayOfIntent.length - 1) {}
      for (localObject = paramBundle;; localObject = null)
      {
        localObject = ActivityOptions.fromBundle((Bundle)localObject);
        int n = startActivityLocked(paramIApplicationThread, localIntent, null, paramArrayOfString[j], localActivityInfo, null, null, null, paramIBinder, null, -1, paramInt1, i, paramString, k, m, 0, (ActivityOptions)localObject, false, bool, arrayOfActivityRecord, null, null);
        if (n >= 0) {
          break;
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(l);
        return n;
      }
      if (arrayOfActivityRecord[0] != null)
      {
        paramIBinder = arrayOfActivityRecord[0].appToken;
      }
      else
      {
        paramIBinder = null;
        break label375;
        label362:
        ActivityManagerService.resetPriorityAfterLockedSection();
        Binder.restoreCallingIdentity(l);
        return 0;
      }
      label375:
      j += 1;
      break label101;
      if (paramIApplicationThread == null)
      {
        paramInt1 = k;
        i = m;
        break;
      }
      i = -1;
      paramInt1 = -1;
      break;
    }
  }
  
  final int startActivityLocked(IApplicationThread paramIApplicationThread, Intent paramIntent1, Intent paramIntent2, String paramString1, ActivityInfo paramActivityInfo, ResolveInfo paramResolveInfo, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor, IBinder paramIBinder, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3, int paramInt4, int paramInt5, int paramInt6, ActivityOptions paramActivityOptions, boolean paramBoolean1, boolean paramBoolean2, ActivityRecord[] paramArrayOfActivityRecord, ActivityStackSupervisor.ActivityContainer paramActivityContainer, TaskRecord paramTaskRecord)
  {
    if (OnePlusAppBootManager.DEBUG) {
      OnePlusAppBootManager.myLog(" startActivityLocked # aInfo = " + paramActivityInfo + ", callingPackage=" + paramString3);
    }
    if (OnePlusAppBootManager.IN_USING) {
      if (paramActivityInfo != null)
      {
        if (!OnePlusAppBootManager.getInstance(null).canActivityGo(paramActivityInfo, paramString3))
        {
          Slog.e("OnePlusAppBootManager", "forbid start " + paramActivityInfo + " (pid=" + paramInt2 + " uid=" + paramInt3 + ")");
          return -4;
        }
      }
      else {
        Slog.e("OnePlusAppBootManager", "# startActivityLocked # aInfo = null");
      }
    }
    if ((OnePlusProcessManager.isSupportFrozenApp()) && (paramActivityInfo != null) && (paramActivityInfo.applicationInfo != null)) {
      OnePlusProcessManager.resumeProcessByUID_out(paramActivityInfo.applicationInfo.uid, "startActivityLocked");
    }
    j = 0;
    localProcessRecord = null;
    i = j;
    m = paramInt2;
    k = paramInt3;
    if (paramIApplicationThread != null)
    {
      localProcessRecord = this.mService.getRecordForAppLocked(paramIApplicationThread);
      if (localProcessRecord != null)
      {
        m = localProcessRecord.pid;
        k = localProcessRecord.info.uid;
        i = j;
      }
    }
    else
    {
      if (paramActivityInfo == null) {
        break label589;
      }
      paramInt3 = UserHandle.getUserId(paramActivityInfo.applicationInfo.uid);
      label244:
      if (i == 0)
      {
        paramIApplicationThread = TAG;
        localObject1 = new StringBuilder().append("START u").append(paramInt3).append(" {").append(paramIntent1.toShortString(true, true, true, false)).append("} from uid ").append(k).append(" pid ").append(m).append(" on display ");
        if (paramActivityContainer != null) {
          break label610;
        }
        if (this.mSupervisor.mFocusedStack != null) {
          break label595;
        }
        paramInt2 = 0;
        label336:
        Slog.i(paramIApplicationThread, paramInt2);
      }
      localObject1 = null;
      str = null;
      paramIApplicationThread = str;
      if (paramIBinder != null)
      {
        localObject2 = this.mSupervisor.isInAnyStackLocked(paramIBinder);
        if (ActivityManagerDebugConfig.DEBUG_RESULTS) {
          Slog.v(TAG_RESULTS, "Will send result to " + paramIBinder + " " + localObject2);
        }
        paramIApplicationThread = str;
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          paramIApplicationThread = str;
          localObject1 = localObject2;
          if (paramInt1 >= 0)
          {
            if (!((ActivityRecord)localObject2).finishing) {
              break label637;
            }
            localObject1 = localObject2;
            paramIApplicationThread = str;
          }
        }
      }
    }
    int n;
    for (;;)
    {
      n = paramIntent1.getFlags();
      paramIBinder = paramIApplicationThread;
      localObject2 = paramString2;
      j = paramInt1;
      str = paramString3;
      if ((0x2000000 & n) == 0) {
        break label750;
      }
      paramIBinder = paramIApplicationThread;
      localObject2 = paramString2;
      j = paramInt1;
      str = paramString3;
      if (localObject1 == null) {
        break label750;
      }
      if (paramInt1 < 0) {
        break label647;
      }
      ActivityOptions.abort(paramActivityOptions);
      return -3;
      Slog.w(TAG, "Unable to find app for caller " + paramIApplicationThread + " (pid=" + paramInt2 + ") when starting: " + paramIntent1.toString());
      i = -4;
      m = paramInt2;
      k = paramInt3;
      break;
      label589:
      paramInt3 = 0;
      break label244;
      label595:
      paramInt2 = this.mSupervisor.mFocusedStack.mDisplayId;
      break label336;
      label610:
      if (paramActivityContainer.mActivityDisplay == null)
      {
        paramInt2 = 0;
        break label336;
      }
      paramInt2 = paramActivityContainer.mActivityDisplay.mDisplayId;
      break label336;
      label637:
      paramIApplicationThread = (IApplicationThread)localObject2;
      localObject1 = localObject2;
    }
    label647:
    paramIBinder = ((ActivityRecord)localObject1).resultTo;
    paramIApplicationThread = paramIBinder;
    if (paramIBinder != null) {
      if (!paramIBinder.isInStackLocked()) {
        break label1024;
      }
    }
    label750:
    label1024:
    for (paramIApplicationThread = paramIBinder;; paramIApplicationThread = null)
    {
      paramString2 = ((ActivityRecord)localObject1).resultWho;
      paramInt1 = ((ActivityRecord)localObject1).requestCode;
      ((ActivityRecord)localObject1).resultTo = null;
      if (paramIApplicationThread != null) {
        paramIApplicationThread.removeResultsLocked((ActivityRecord)localObject1, paramString2, paramInt1);
      }
      paramIBinder = paramIApplicationThread;
      localObject2 = paramString2;
      j = paramInt1;
      str = paramString3;
      if (((ActivityRecord)localObject1).launchedFromUid == k)
      {
        str = ((ActivityRecord)localObject1).launchedFromPackage;
        j = paramInt1;
        localObject2 = paramString2;
        paramIBinder = paramIApplicationThread;
      }
      paramInt1 = i;
      if (i == 0)
      {
        paramInt1 = i;
        if (paramIntent1.getComponent() == null) {
          paramInt1 = -1;
        }
      }
      paramInt2 = paramInt1;
      if (paramInt1 == 0)
      {
        paramInt2 = paramInt1;
        if (paramActivityInfo == null) {
          paramInt2 = -2;
        }
      }
      paramInt1 = paramInt2;
      if (paramInt2 == 0)
      {
        paramInt1 = paramInt2;
        if (localObject1 != null)
        {
          paramInt1 = paramInt2;
          if (((ActivityRecord)localObject1).task.voiceSession != null)
          {
            paramInt1 = paramInt2;
            if ((0x10000000 & n) == 0)
            {
              paramInt1 = paramInt2;
              if (((ActivityRecord)localObject1).info.applicationInfo.uid == paramActivityInfo.applicationInfo.uid) {}
            }
          }
        }
      }
      try
      {
        paramIntent1.addCategory("android.intent.category.VOICE");
        paramInt1 = paramInt2;
        if (!AppGlobals.getPackageManager().activitySupportsIntent(paramIntent1.getComponent(), paramIntent1, paramString1))
        {
          Slog.w(TAG, "Activity being started in current voice task does not support voice: " + paramIntent1);
          paramInt1 = -7;
        }
      }
      catch (RemoteException paramIApplicationThread)
      {
        for (;;)
        {
          Slog.w(TAG, "Failure checking voice capabilities", paramIApplicationThread);
          paramInt1 = -7;
        }
      }
      if ((paramInt1 != 0) || (paramIVoiceInteractionSession == null)) {
        break;
      }
      try
      {
        if (!AppGlobals.getPackageManager().activitySupportsIntent(paramIntent1.getComponent(), paramIntent1, paramString1))
        {
          Slog.w(TAG, "Activity being started in new voice task does not support: " + paramIntent1);
          paramInt1 = -7;
        }
      }
      catch (RemoteException paramIApplicationThread)
      {
        for (;;)
        {
          Slog.w(TAG, "Failure checking voice capabilities", paramIApplicationThread);
          paramInt1 = -7;
          continue;
          paramIApplicationThread = paramIBinder.task.stack;
        }
        if (!this.mSupervisor.checkStartAnyActivityPermission(paramIntent1, paramActivityInfo, (String)localObject2, j, m, k, str, paramBoolean1, localProcessRecord, paramIBinder, paramIApplicationThread, paramActivityOptions)) {
          break label1352;
        }
        paramInt1 = 0;
        if (!this.mService.mIntentFirewall.checkStartActivity(paramIntent1, k, m, paramString1, paramActivityInfo.applicationInfo)) {
          break label1358;
        }
        paramInt2 = 0;
        paramInt2 = paramInt1 | paramInt2;
        paramInt1 = paramInt2;
        if (this.mService.mController == null) {
          break label1211;
        }
        for (;;)
        {
          try
          {
            paramString2 = paramIntent1.cloneFilter();
            paramBoolean1 = this.mService.mController.activityStarting(paramString2, paramActivityInfo.applicationInfo.packageName);
            if (!paramBoolean1) {
              continue;
            }
            paramInt1 = 0;
            paramInt1 = paramInt2 | paramInt1;
          }
          catch (RemoteException paramString2)
          {
            TaskRecord localTaskRecord;
            ActivityOptions localActivityOptions;
            this.mService.mController = null;
            paramInt1 = paramInt2;
            continue;
            paramIApplicationThread = paramString2;
            paramIntent1 = paramActivityOptions;
            paramString1 = paramResolveInfo;
            paramActivityInfo = paramString3;
            paramInt1 = i;
            paramInt2 = k;
            if (!Build.PERMISSIONS_REVIEW_REQUIRED) {
              continue;
            }
            paramIApplicationThread = paramString2;
            paramIntent1 = paramActivityOptions;
            paramString1 = paramResolveInfo;
            paramActivityInfo = paramString3;
            paramInt1 = i;
            paramInt2 = k;
            if (paramResolveInfo == null) {
              continue;
            }
            paramIApplicationThread = paramString2;
            paramIntent1 = paramActivityOptions;
            paramString1 = paramResolveInfo;
            paramActivityInfo = paramString3;
            paramInt1 = i;
            paramInt2 = k;
            if (!this.mService.getPackageManagerInternalLocked().isPermissionsReviewRequired(paramResolveInfo.packageName, paramInt3)) {
              continue;
            }
            paramIApplicationThread = this.mService.getIntentSenderLocked(2, str, k, paramInt3, null, null, 0, new Intent[] { paramString2 }, new String[] { paramActivityOptions }, 1342177280, null);
            paramInt1 = paramString2.getFlags();
            paramString2 = new Intent("android.intent.action.REVIEW_PERMISSIONS");
            paramString2.setFlags(0x800000 | paramInt1);
            paramString2.putExtra("android.intent.extra.PACKAGE_NAME", paramResolveInfo.packageName);
            paramString2.putExtra("android.intent.extra.INTENT", new IntentSender(paramIApplicationThread));
            if (paramIBinder == null) {
              continue;
            }
            paramString2.putExtra("android.intent.extra.RESULT_NEEDED", true);
            paramResolveInfo = paramString2;
            paramString3 = null;
            i = paramInt5;
            k = paramInt4;
            paramActivityOptions = this.mSupervisor.resolveIntent(paramString2, null, paramInt3);
            paramTaskRecord = this.mSupervisor.resolveActivity(paramString2, paramActivityOptions, paramInt6, null);
            paramIApplicationThread = paramResolveInfo;
            paramIntent1 = paramString3;
            paramString1 = paramTaskRecord;
            paramActivityInfo = paramActivityOptions;
            paramInt1 = k;
            paramInt2 = i;
            if (!ActivityManagerDebugConfig.DEBUG_PERMISSIONS_REVIEW) {
              continue;
            }
            paramIApplicationThread = TAG;
            paramIntent1 = new StringBuilder().append("START u").append(paramInt3).append(" {").append(paramString2.toShortString(true, true, true, false)).append("} from uid ").append(paramInt5).append(" on display ");
            if (paramActivityContainer != null) {
              continue;
            }
            if (this.mSupervisor.mFocusedStack != null) {
              continue;
            }
            paramInt1 = 0;
            Slog.i(paramIApplicationThread, paramInt1);
            paramInt2 = i;
            paramInt1 = k;
            paramActivityInfo = paramActivityOptions;
            paramString1 = paramTaskRecord;
            paramIntent1 = paramString3;
            paramIApplicationThread = paramResolveInfo;
            paramString3 = paramIApplicationThread;
            paramString2 = paramIntent1;
            paramResolveInfo = paramString1;
            k = paramInt1;
            i = paramInt2;
            if (paramActivityInfo == null) {
              continue;
            }
            paramString3 = paramIApplicationThread;
            paramString2 = paramIntent1;
            paramResolveInfo = paramString1;
            k = paramInt1;
            i = paramInt2;
            if (paramActivityInfo.ephemeralResolveInfo == null) {
              continue;
            }
            paramString3 = buildEphemeralInstallerIntent(paramIApplicationThread, paramIntent2, paramActivityInfo.ephemeralResolveInfo.getPackageName(), str, paramIntent1, paramInt3);
            paramString2 = null;
            i = paramInt5;
            k = paramInt4;
            paramResolveInfo = this.mSupervisor.resolveActivity(paramString3, paramActivityInfo, paramInt6, null);
            paramIApplicationThread = this.mService;
            paramIntent1 = this.mService.mConfiguration;
            if (paramIVoiceInteractionSession == null) {
              continue;
            }
            paramBoolean1 = true;
            paramIApplicationThread = new ActivityRecord(paramIApplicationThread, localProcessRecord, i, str, paramString3, paramString2, paramResolveInfo, paramIntent1, paramIBinder, (String)localObject2, j, paramBoolean2, paramBoolean1, this.mSupervisor, paramActivityContainer, localActivityOptions, (ActivityRecord)localObject1);
            if (paramArrayOfActivityRecord == null) {
              continue;
            }
            paramArrayOfActivityRecord[0] = paramIApplicationThread;
            if ((paramIApplicationThread.appTimeTracker != null) || (localObject1 == null)) {
              continue;
            }
            paramIApplicationThread.appTimeTracker = ((ActivityRecord)localObject1).appTimeTracker;
            paramIntent1 = this.mSupervisor.mFocusedStack;
            if ((paramIVoiceInteractionSession != null) || ((paramIntent1.mResumedActivity != null) && (paramIntent1.mResumedActivity.info.applicationInfo.uid == i)) || (this.mService.checkAppSwitchAllowedLocked(k, i, paramInt4, paramInt5, "Activity start"))) {
              continue;
            }
            paramIApplicationThread = new ActivityStackSupervisor.PendingActivityLaunch(paramIApplicationThread, (ActivityRecord)localObject1, paramInt6, paramIntent1, localProcessRecord);
            this.mPendingActivityLaunches.add(paramIApplicationThread);
            ActivityOptions.abort(localActivityOptions);
            return 4;
            paramInt1 = this.mSupervisor.mFocusedStack.mDisplayId;
            continue;
            if (paramActivityContainer.mActivityDisplay != null) {
              continue;
            }
            paramInt1 = 0;
            continue;
            paramInt1 = paramActivityContainer.mActivityDisplay.mDisplayId;
            continue;
            paramBoolean1 = false;
            continue;
            if (!this.mService.mDidAppSwitch) {
              continue;
            }
            this.mService.mAppSwitchesAllowedTime = 0L;
            doPendingActivityLaunchesLocked(false);
            try
            {
              this.mService.mWindowManager.deferSurfaceLayout();
              paramInt1 = startActivityUnchecked(paramIApplicationThread, (ActivityRecord)localObject1, paramIVoiceInteractionSession, paramIVoiceInteractor, paramInt6, true, localActivityOptions, localTaskRecord);
              this.mService.mWindowManager.continueSurfaceLayout();
              return paramInt1;
            }
            finally
            {
              this.mService.mWindowManager.continueSurfaceLayout();
            }
            this.mService.mDidAppSwitch = true;
            continue;
          }
          this.mInterceptor.setStates(paramInt3, paramInt4, paramInt5, paramInt6, str);
          this.mInterceptor.intercept(paramIntent1, paramResolveInfo, paramActivityInfo, paramString1, paramTaskRecord, m, k, paramActivityOptions);
          paramString2 = this.mInterceptor.mIntent;
          paramString3 = this.mInterceptor.mRInfo;
          paramResolveInfo = this.mInterceptor.mAInfo;
          paramActivityOptions = this.mInterceptor.mResolvedType;
          localTaskRecord = this.mInterceptor.mInTask;
          i = this.mInterceptor.mCallingPid;
          k = this.mInterceptor.mCallingUid;
          localActivityOptions = this.mInterceptor.mActivityOptions;
          if (paramInt1 == 0) {
            continue;
          }
          if (paramIBinder != null) {
            paramIApplicationThread.sendActivityResultLocked(-1, paramIBinder, (String)localObject2, j, 0, null);
          }
          ActivityOptions.abort(localActivityOptions);
          return 0;
          paramInt1 = 1;
          break;
          paramInt2 = 1;
          break label1145;
          paramInt1 = 1;
        }
      }
      if (paramIBinder != null) {
        break label1070;
      }
      paramIApplicationThread = null;
      if (paramInt1 == 0) {
        break label1082;
      }
      if (paramIBinder != null) {
        paramIApplicationThread.sendActivityResultLocked(-1, paramIBinder, (String)localObject2, j, 0, null);
      }
      ActivityOptions.abort(paramActivityOptions);
      return paramInt1;
    }
  }
  
  /* Error */
  final int startActivityMayWait(IApplicationThread paramIApplicationThread, int paramInt1, String paramString1, Intent paramIntent, String paramString2, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor, IBinder paramIBinder, String paramString3, int paramInt2, int paramInt3, android.app.ProfilerInfo paramProfilerInfo, android.app.IActivityManager.WaitResult paramWaitResult, android.content.res.Configuration paramConfiguration, Bundle paramBundle, boolean paramBoolean, int paramInt4, android.app.IActivityContainer paramIActivityContainer, TaskRecord paramTaskRecord)
  {
    // Byte code:
    //   0: aload 4
    //   2: ifnull +22 -> 24
    //   5: aload 4
    //   7: invokevirtual 1048	android/content/Intent:hasFileDescriptors	()Z
    //   10: ifeq +14 -> 24
    //   13: new 248	java/lang/IllegalArgumentException
    //   16: dup
    //   17: ldc_w 1050
    //   20: invokespecial 256	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   23: athrow
    //   24: aload_0
    //   25: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   28: getfield 1377	com/android/server/am/ActivityStackSupervisor:mActivityMetricsLogger	Lcom/android/server/am/ActivityMetricsLogger;
    //   31: invokevirtual 1382	com/android/server/am/ActivityMetricsLogger:notifyActivityLaunching	()V
    //   34: aload 4
    //   36: invokevirtual 264	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   39: ifnull +248 -> 287
    //   42: iconst_1
    //   43: istore 25
    //   45: new 150	android/content/Intent
    //   48: dup
    //   49: aload 4
    //   51: invokespecial 153	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   54: astore 34
    //   56: new 150	android/content/Intent
    //   59: dup
    //   60: aload 4
    //   62: invokespecial 153	android/content/Intent:<init>	(Landroid/content/Intent;)V
    //   65: astore 29
    //   67: aload_0
    //   68: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   71: aload 29
    //   73: aload 5
    //   75: iload 17
    //   77: invokevirtual 1312	com/android/server/am/ActivityStackSupervisor:resolveIntent	(Landroid/content/Intent;Ljava/lang/String;I)Landroid/content/pm/ResolveInfo;
    //   80: astore 4
    //   82: aload 4
    //   84: ifnonnull +1310 -> 1394
    //   87: aload_0
    //   88: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   91: iload 17
    //   93: invokevirtual 1386	com/android/server/am/ActivityStackSupervisor:getUserInfo	(I)Landroid/content/pm/UserInfo;
    //   96: astore 30
    //   98: aload 30
    //   100: ifnull +193 -> 293
    //   103: aload 30
    //   105: invokevirtual 1391	android/content/pm/UserInfo:isManagedProfile	()Z
    //   108: ifeq +1286 -> 1394
    //   111: aload_0
    //   112: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   115: getfield 1002	com/android/server/am/ActivityManagerService:mContext	Landroid/content/Context;
    //   118: invokestatic 1396	android/os/UserManager:get	(Landroid/content/Context;)Landroid/os/UserManager;
    //   121: astore 30
    //   123: invokestatic 1042	android/os/Binder:clearCallingIdentity	()J
    //   126: lstore 27
    //   128: aload 30
    //   130: iload 17
    //   132: invokevirtual 1399	android/os/UserManager:getProfileParent	(I)Landroid/content/pm/UserInfo;
    //   135: astore 31
    //   137: aload 31
    //   139: ifnull +163 -> 302
    //   142: aload 30
    //   144: aload 31
    //   146: getfield 1402	android/content/pm/UserInfo:id	I
    //   149: invokevirtual 1405	android/os/UserManager:isUserUnlockingOrUnlocked	(I)Z
    //   152: ifeq +150 -> 302
    //   155: aload 30
    //   157: iload 17
    //   159: invokevirtual 1405	android/os/UserManager:isUserUnlockingOrUnlocked	(I)Z
    //   162: istore 26
    //   164: iload 26
    //   166: ifeq +130 -> 296
    //   169: iconst_0
    //   170: istore 20
    //   172: lload 27
    //   174: invokestatic 1057	android/os/Binder:restoreCallingIdentity	(J)V
    //   177: iload 20
    //   179: ifeq +1215 -> 1394
    //   182: aload_0
    //   183: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   186: aload 29
    //   188: aload 5
    //   190: iload 17
    //   192: ldc_w 1406
    //   195: invokevirtual 1409	com/android/server/am/ActivityStackSupervisor:resolveIntent	(Landroid/content/Intent;Ljava/lang/String;II)Landroid/content/pm/ResolveInfo;
    //   198: astore 4
    //   200: aload_0
    //   201: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   204: aload 29
    //   206: aload 4
    //   208: iload 11
    //   210: aload 12
    //   212: invokevirtual 1315	com/android/server/am/ActivityStackSupervisor:resolveActivity	(Landroid/content/Intent;Landroid/content/pm/ResolveInfo;ILandroid/app/ProfilerInfo;)Landroid/content/pm/ActivityInfo;
    //   215: astore 12
    //   217: aload 15
    //   219: invokestatic 1080	android/app/ActivityOptions:fromBundle	(Landroid/os/Bundle;)Landroid/app/ActivityOptions;
    //   222: astore 36
    //   224: aload 18
    //   226: checkcast 383	com/android/server/am/ActivityStackSupervisor$ActivityContainer
    //   229: astore 37
    //   231: aload_0
    //   232: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   235: astore 35
    //   237: aload 35
    //   239: monitorenter
    //   240: invokestatic 1045	com/android/server/am/ActivityManagerService:boostPriorityForLockedSection	()V
    //   243: aload 37
    //   245: ifnull +71 -> 316
    //   248: aload 37
    //   250: getfield 1412	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mParentActivity	Lcom/android/server/am/ActivityRecord;
    //   253: ifnull +63 -> 316
    //   256: aload 37
    //   258: getfield 1412	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mParentActivity	Lcom/android/server/am/ActivityRecord;
    //   261: getfield 1416	com/android/server/am/ActivityRecord:state	Lcom/android/server/am/ActivityStack$ActivityState;
    //   264: astore 15
    //   266: getstatic 1421	com/android/server/am/ActivityStack$ActivityState:RESUMED	Lcom/android/server/am/ActivityStack$ActivityState;
    //   269: astore 18
    //   271: aload 15
    //   273: aload 18
    //   275: if_acmpeq +41 -> 316
    //   278: aload 35
    //   280: monitorexit
    //   281: invokestatic 1053	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   284: bipush -6
    //   286: ireturn
    //   287: iconst_0
    //   288: istore 25
    //   290: goto -245 -> 45
    //   293: goto -93 -> 200
    //   296: iconst_1
    //   297: istore 20
    //   299: goto -127 -> 172
    //   302: iconst_0
    //   303: istore 20
    //   305: goto -133 -> 172
    //   308: astore_1
    //   309: lload 27
    //   311: invokestatic 1057	android/os/Binder:restoreCallingIdentity	(J)V
    //   314: aload_1
    //   315: athrow
    //   316: invokestatic 1038	android/os/Binder:getCallingPid	()I
    //   319: istore 21
    //   321: invokestatic 166	android/os/Binder:getCallingUid	()I
    //   324: istore 22
    //   326: iload_2
    //   327: iflt +517 -> 844
    //   330: iconst_m1
    //   331: istore 20
    //   333: aload 37
    //   335: ifnull +14 -> 349
    //   338: aload 37
    //   340: getfield 386	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mStack	Lcom/android/server/am/ActivityStack;
    //   343: invokevirtual 359	com/android/server/am/ActivityStack:isOnHomeDisplay	()Z
    //   346: ifeq +520 -> 866
    //   349: aload_0
    //   350: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   353: getfield 362	com/android/server/am/ActivityStackSupervisor:mFocusedStack	Lcom/android/server/am/ActivityStack;
    //   356: astore 18
    //   358: aload 14
    //   360: ifnull +1037 -> 1397
    //   363: aload_0
    //   364: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   367: getfield 1335	com/android/server/am/ActivityManagerService:mConfiguration	Landroid/content/res/Configuration;
    //   370: aload 14
    //   372: invokevirtual 1427	android/content/res/Configuration:diff	(Landroid/content/res/Configuration;)I
    //   375: ifeq +1022 -> 1397
    //   378: iconst_1
    //   379: istore 26
    //   381: aload 18
    //   383: iload 26
    //   385: putfield 1430	com/android/server/am/ActivityStack:mConfigWillChange	Z
    //   388: getstatic 1433	com/android/server/am/ActivityManagerDebugConfig:DEBUG_CONFIGURATION	Z
    //   391: ifeq +34 -> 425
    //   394: getstatic 100	com/android/server/am/ActivityStarter:TAG_CONFIGURATION	Ljava/lang/String;
    //   397: new 72	java/lang/StringBuilder
    //   400: dup
    //   401: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   404: ldc_w 1435
    //   407: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   410: aload 18
    //   412: getfield 1430	com/android/server/am/ActivityStack:mConfigWillChange	Z
    //   415: invokevirtual 576	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   418: invokevirtual 88	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   421: invokestatic 579	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   424: pop
    //   425: invokestatic 1042	android/os/Binder:clearCallingIdentity	()J
    //   428: lstore 27
    //   430: aload 12
    //   432: ifnull +971 -> 1403
    //   435: aload 12
    //   437: getfield 1069	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   440: getfield 1074	android/content/pm/ApplicationInfo:privateFlags	I
    //   443: iconst_2
    //   444: iand
    //   445: ifeq +958 -> 1403
    //   448: aload 12
    //   450: getfield 1438	android/content/pm/ActivityInfo:processName	Ljava/lang/String;
    //   453: aload 12
    //   455: getfield 1069	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   458: getfield 1250	android/content/pm/ApplicationInfo:packageName	Ljava/lang/String;
    //   461: invokevirtual 1439	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   464: ifeq +904 -> 1368
    //   467: aload_0
    //   468: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   471: getfield 1442	com/android/server/am/ActivityManagerService:mHeavyWeightProcess	Lcom/android/server/am/ProcessRecord;
    //   474: astore 15
    //   476: aload 15
    //   478: ifnull +42 -> 520
    //   481: aload 15
    //   483: getfield 1146	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   486: getfield 1132	android/content/pm/ApplicationInfo:uid	I
    //   489: aload 12
    //   491: getfield 1069	android/content/pm/ActivityInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   494: getfield 1132	android/content/pm/ApplicationInfo:uid	I
    //   497: if_icmpne +379 -> 876
    //   500: aload 15
    //   502: getfield 1443	com/android/server/am/ProcessRecord:processName	Ljava/lang/String;
    //   505: aload 12
    //   507: getfield 1438	android/content/pm/ActivityInfo:processName	Ljava/lang/String;
    //   510: invokevirtual 1439	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   513: istore 26
    //   515: iload 26
    //   517: ifeq +359 -> 876
    //   520: aload 12
    //   522: astore 31
    //   524: aload 29
    //   526: astore 15
    //   528: aload 5
    //   530: astore 30
    //   532: aload_1
    //   533: astore 29
    //   535: aload 4
    //   537: astore 12
    //   539: aload 31
    //   541: astore 4
    //   543: iconst_1
    //   544: anewarray 137	com/android/server/am/ActivityRecord
    //   547: astore_1
    //   548: aload_0
    //   549: aload 29
    //   551: aload 15
    //   553: aload 34
    //   555: aload 30
    //   557: aload 4
    //   559: aload 12
    //   561: aload 6
    //   563: aload 7
    //   565: aload 8
    //   567: aload 9
    //   569: iload 10
    //   571: iload 20
    //   573: iload_2
    //   574: aload_3
    //   575: iload 21
    //   577: iload 22
    //   579: iload 11
    //   581: aload 36
    //   583: iload 16
    //   585: iload 25
    //   587: aload_1
    //   588: aload 37
    //   590: aload 19
    //   592: invokevirtual 1083	com/android/server/am/ActivityStarter:startActivityLocked	(Landroid/app/IApplicationThread;Landroid/content/Intent;Landroid/content/Intent;Ljava/lang/String;Landroid/content/pm/ActivityInfo;Landroid/content/pm/ResolveInfo;Landroid/service/voice/IVoiceInteractionSession;Lcom/android/internal/app/IVoiceInteractor;Landroid/os/IBinder;Ljava/lang/String;IIILjava/lang/String;IIILandroid/app/ActivityOptions;ZZ[Lcom/android/server/am/ActivityRecord;Lcom/android/server/am/ActivityStackSupervisor$ActivityContainer;Lcom/android/server/am/TaskRecord;)I
    //   595: istore 10
    //   597: lload 27
    //   599: invokestatic 1057	android/os/Binder:restoreCallingIdentity	(J)V
    //   602: aload 18
    //   604: getfield 1430	com/android/server/am/ActivityStack:mConfigWillChange	Z
    //   607: ifeq +50 -> 657
    //   610: aload_0
    //   611: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   614: ldc_w 1445
    //   617: ldc_w 1447
    //   620: invokevirtual 1451	com/android/server/am/ActivityManagerService:enforceCallingPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   623: aload 18
    //   625: iconst_0
    //   626: putfield 1430	com/android/server/am/ActivityStack:mConfigWillChange	Z
    //   629: getstatic 1433	com/android/server/am/ActivityManagerDebugConfig:DEBUG_CONFIGURATION	Z
    //   632: ifeq +13 -> 645
    //   635: getstatic 100	com/android/server/am/ActivityStarter:TAG_CONFIGURATION	Ljava/lang/String;
    //   638: ldc_w 1453
    //   641: invokestatic 579	android/util/Slog:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   644: pop
    //   645: aload_0
    //   646: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   649: aload 14
    //   651: aconst_null
    //   652: iconst_0
    //   653: invokevirtual 1457	com/android/server/am/ActivityManagerService:updateConfigurationLocked	(Landroid/content/res/Configuration;Lcom/android/server/am/ActivityRecord;Z)Z
    //   656: pop
    //   657: iload 10
    //   659: istore 11
    //   661: aload 13
    //   663: ifnull +147 -> 810
    //   666: aload 13
    //   668: iload 10
    //   670: putfield 1462	android/app/IActivityManager$WaitResult:result	I
    //   673: iload 10
    //   675: istore_2
    //   676: iload 10
    //   678: ifne +54 -> 732
    //   681: aload_0
    //   682: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   685: getfield 958	com/android/server/am/ActivityStackSupervisor:mWaitingActivityLaunched	Ljava/util/ArrayList;
    //   688: aload 13
    //   690: invokevirtual 1353	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   693: pop
    //   694: aload_0
    //   695: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   698: invokevirtual 1465	com/android/server/am/ActivityManagerService:wait	()V
    //   701: aload 13
    //   703: getfield 1462	android/app/IActivityManager$WaitResult:result	I
    //   706: iconst_2
    //   707: if_icmpeq +11 -> 718
    //   710: aload 13
    //   712: getfield 1468	android/app/IActivityManager$WaitResult:timeout	Z
    //   715: ifeq +553 -> 1268
    //   718: iload 10
    //   720: istore_2
    //   721: aload 13
    //   723: getfield 1462	android/app/IActivityManager$WaitResult:result	I
    //   726: iconst_2
    //   727: if_icmpne +5 -> 732
    //   730: iconst_2
    //   731: istore_2
    //   732: iload_2
    //   733: istore 11
    //   735: iload_2
    //   736: iconst_2
    //   737: if_icmpne +73 -> 810
    //   740: aload 18
    //   742: invokevirtual 962	com/android/server/am/ActivityStack:topRunningActivityLocked	()Lcom/android/server/am/ActivityRecord;
    //   745: astore_3
    //   746: aload_3
    //   747: getfield 1471	com/android/server/am/ActivityRecord:nowVisible	Z
    //   750: ifeq +529 -> 1279
    //   753: aload_3
    //   754: getfield 1416	com/android/server/am/ActivityRecord:state	Lcom/android/server/am/ActivityStack$ActivityState;
    //   757: getstatic 1421	com/android/server/am/ActivityStack$ActivityState:RESUMED	Lcom/android/server/am/ActivityStack$ActivityState;
    //   760: if_acmpne +519 -> 1279
    //   763: aload 13
    //   765: iconst_0
    //   766: putfield 1468	android/app/IActivityManager$WaitResult:timeout	Z
    //   769: aload 13
    //   771: new 271	android/content/ComponentName
    //   774: dup
    //   775: aload_3
    //   776: getfield 140	com/android/server/am/ActivityRecord:info	Landroid/content/pm/ActivityInfo;
    //   779: getfield 1293	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   782: aload_3
    //   783: getfield 140	com/android/server/am/ActivityRecord:info	Landroid/content/pm/ActivityInfo;
    //   786: getfield 1474	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   789: invokespecial 1476	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   792: putfield 1479	android/app/IActivityManager$WaitResult:who	Landroid/content/ComponentName;
    //   795: aload 13
    //   797: lconst_0
    //   798: putfield 1482	android/app/IActivityManager$WaitResult:totalTime	J
    //   801: aload 13
    //   803: lconst_0
    //   804: putfield 1485	android/app/IActivityManager$WaitResult:thisTime	J
    //   807: iload_2
    //   808: istore 11
    //   810: aload_0
    //   811: getfield 798	com/android/server/am/ActivityStarter:mReusedActivity	Lcom/android/server/am/ActivityRecord;
    //   814: ifnull +518 -> 1332
    //   817: aload_0
    //   818: getfield 798	com/android/server/am/ActivityStarter:mReusedActivity	Lcom/android/server/am/ActivityRecord;
    //   821: astore_1
    //   822: aload_0
    //   823: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   826: getfield 1377	com/android/server/am/ActivityStackSupervisor:mActivityMetricsLogger	Lcom/android/server/am/ActivityMetricsLogger;
    //   829: iload 11
    //   831: aload_1
    //   832: invokevirtual 1489	com/android/server/am/ActivityMetricsLogger:notifyActivityLaunched	(ILcom/android/server/am/ActivityRecord;)V
    //   835: aload 35
    //   837: monitorexit
    //   838: invokestatic 1053	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   841: iload 11
    //   843: ireturn
    //   844: aload_1
    //   845: ifnonnull +13 -> 858
    //   848: iload 21
    //   850: istore 20
    //   852: iload 22
    //   854: istore_2
    //   855: goto -522 -> 333
    //   858: iconst_m1
    //   859: istore_2
    //   860: iconst_m1
    //   861: istore 20
    //   863: goto -530 -> 333
    //   866: aload 37
    //   868: getfield 386	com/android/server/am/ActivityStackSupervisor$ActivityContainer:mStack	Lcom/android/server/am/ActivityStack;
    //   871: astore 18
    //   873: goto -515 -> 358
    //   876: aload_1
    //   877: ifnull +27 -> 904
    //   880: aload_0
    //   881: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   884: aload_1
    //   885: invokevirtual 1141	com/android/server/am/ActivityManagerService:getRecordForAppLocked	(Landroid/app/IApplicationThread;)Lcom/android/server/am/ProcessRecord;
    //   888: astore 4
    //   890: aload 4
    //   892: ifnull +305 -> 1197
    //   895: aload 4
    //   897: getfield 1146	com/android/server/am/ProcessRecord:info	Landroid/content/pm/ApplicationInfo;
    //   900: getfield 1132	android/content/pm/ApplicationInfo:uid	I
    //   903: istore_2
    //   904: aload_0
    //   905: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   908: iconst_2
    //   909: ldc_w 1491
    //   912: iload_2
    //   913: iload 17
    //   915: aconst_null
    //   916: aconst_null
    //   917: iconst_0
    //   918: iconst_1
    //   919: anewarray 150	android/content/Intent
    //   922: dup
    //   923: iconst_0
    //   924: aload 29
    //   926: aastore
    //   927: iconst_1
    //   928: anewarray 168	java/lang/String
    //   931: dup
    //   932: iconst_0
    //   933: aload 5
    //   935: aastore
    //   936: ldc_w 1300
    //   939: aconst_null
    //   940: invokevirtual 175	com/android/server/am/ActivityManagerService:getIntentSenderLocked	(ILjava/lang/String;IILandroid/os/IBinder;Ljava/lang/String;I[Landroid/content/Intent;[Ljava/lang/String;ILandroid/os/Bundle;)Landroid/content/IIntentSender;
    //   943: astore 4
    //   945: new 150	android/content/Intent
    //   948: dup
    //   949: invokespecial 176	android/content/Intent:<init>	()V
    //   952: astore_1
    //   953: iload 10
    //   955: iflt +12 -> 967
    //   958: aload_1
    //   959: ldc_w 1493
    //   962: iconst_1
    //   963: invokevirtual 1308	android/content/Intent:putExtra	(Ljava/lang/String;Z)Landroid/content/Intent;
    //   966: pop
    //   967: aload_1
    //   968: ldc_w 1494
    //   971: new 190	android/content/IntentSender
    //   974: dup
    //   975: aload 4
    //   977: invokespecial 193	android/content/IntentSender:<init>	(Landroid/content/IIntentSender;)V
    //   980: invokevirtual 196	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   983: pop
    //   984: aload 15
    //   986: getfield 1497	com/android/server/am/ProcessRecord:activities	Ljava/util/ArrayList;
    //   989: invokevirtual 409	java/util/ArrayList:size	()I
    //   992: ifle +46 -> 1038
    //   995: aload 15
    //   997: getfield 1497	com/android/server/am/ProcessRecord:activities	Ljava/util/ArrayList;
    //   1000: iconst_0
    //   1001: invokevirtual 413	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1004: checkcast 137	com/android/server/am/ActivityRecord
    //   1007: astore 4
    //   1009: aload_1
    //   1010: ldc_w 1499
    //   1013: aload 4
    //   1015: getfield 826	com/android/server/am/ActivityRecord:packageName	Ljava/lang/String;
    //   1018: invokevirtual 186	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   1021: pop
    //   1022: aload_1
    //   1023: ldc_w 1501
    //   1026: aload 4
    //   1028: getfield 326	com/android/server/am/ActivityRecord:task	Lcom/android/server/am/TaskRecord;
    //   1031: getfield 670	com/android/server/am/TaskRecord:taskId	I
    //   1034: invokevirtual 1504	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   1037: pop
    //   1038: aload_1
    //   1039: ldc_w 1506
    //   1042: aload 12
    //   1044: getfield 1293	android/content/pm/ActivityInfo:packageName	Ljava/lang/String;
    //   1047: invokevirtual 186	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   1050: pop
    //   1051: aload_1
    //   1052: aload 29
    //   1054: invokevirtual 157	android/content/Intent:getFlags	()I
    //   1057: invokevirtual 161	android/content/Intent:setFlags	(I)Landroid/content/Intent;
    //   1060: pop
    //   1061: aload_1
    //   1062: ldc_w 1491
    //   1065: ldc_w 1508
    //   1068: invokevirtual 1513	java/lang/Class:getName	()Ljava/lang/String;
    //   1071: invokevirtual 1516	android/content/Intent:setClassName	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   1074: pop
    //   1075: aload_1
    //   1076: astore 5
    //   1078: aconst_null
    //   1079: astore 32
    //   1081: aconst_null
    //   1082: astore 33
    //   1084: invokestatic 166	android/os/Binder:getCallingUid	()I
    //   1087: istore 23
    //   1089: invokestatic 1038	android/os/Binder:getCallingPid	()I
    //   1092: istore 24
    //   1094: iconst_1
    //   1095: istore 26
    //   1097: aload_0
    //   1098: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   1101: aload_1
    //   1102: aconst_null
    //   1103: iload 17
    //   1105: invokevirtual 1312	com/android/server/am/ActivityStackSupervisor:resolveIntent	(Landroid/content/Intent;Ljava/lang/String;I)Landroid/content/pm/ResolveInfo;
    //   1108: astore 31
    //   1110: aload 31
    //   1112: ifnull +151 -> 1263
    //   1115: aload 31
    //   1117: getfield 1519	android/content/pm/ResolveInfo:activityInfo	Landroid/content/pm/ActivityInfo;
    //   1120: astore_1
    //   1121: aload_1
    //   1122: astore 4
    //   1124: aload 31
    //   1126: astore 12
    //   1128: iload 24
    //   1130: istore 20
    //   1132: iload 26
    //   1134: istore 25
    //   1136: aload 33
    //   1138: astore 29
    //   1140: iload 23
    //   1142: istore_2
    //   1143: aload 5
    //   1145: astore 15
    //   1147: aload 32
    //   1149: astore 30
    //   1151: aload_1
    //   1152: ifnull -609 -> 543
    //   1155: aload_0
    //   1156: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   1159: aload_1
    //   1160: iload 17
    //   1162: invokevirtual 1065	com/android/server/am/ActivityManagerService:getActivityInfoForUser	(Landroid/content/pm/ActivityInfo;I)Landroid/content/pm/ActivityInfo;
    //   1165: astore 4
    //   1167: aload 31
    //   1169: astore 12
    //   1171: iload 24
    //   1173: istore 20
    //   1175: iload 26
    //   1177: istore 25
    //   1179: aload 33
    //   1181: astore 29
    //   1183: iload 23
    //   1185: istore_2
    //   1186: aload 5
    //   1188: astore 15
    //   1190: aload 32
    //   1192: astore 30
    //   1194: goto -651 -> 543
    //   1197: getstatic 70	com/android/server/am/ActivityStarter:TAG	Ljava/lang/String;
    //   1200: new 72	java/lang/StringBuilder
    //   1203: dup
    //   1204: invokespecial 75	java/lang/StringBuilder:<init>	()V
    //   1207: ldc_w 1179
    //   1210: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1213: aload_1
    //   1214: invokevirtual 253	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1217: ldc_w 1118
    //   1220: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1223: iload 20
    //   1225: invokevirtual 435	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1228: ldc_w 1181
    //   1231: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1234: aload 29
    //   1236: invokevirtual 1182	android/content/Intent:toString	()Ljava/lang/String;
    //   1239: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1242: invokevirtual 88	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1245: invokestatic 304	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1248: pop
    //   1249: aload 36
    //   1251: invokestatic 246	android/app/ActivityOptions:abort	(Landroid/app/ActivityOptions;)V
    //   1254: aload 35
    //   1256: monitorexit
    //   1257: invokestatic 1053	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1260: bipush -4
    //   1262: ireturn
    //   1263: aconst_null
    //   1264: astore_1
    //   1265: goto -144 -> 1121
    //   1268: aload 13
    //   1270: getfield 1479	android/app/IActivityManager$WaitResult:who	Landroid/content/ComponentName;
    //   1273: ifnonnull -555 -> 718
    //   1276: goto -582 -> 694
    //   1279: aload 13
    //   1281: invokestatic 1524	android/os/SystemClock:uptimeMillis	()J
    //   1284: putfield 1485	android/app/IActivityManager$WaitResult:thisTime	J
    //   1287: aload_0
    //   1288: getfield 117	com/android/server/am/ActivityStarter:mSupervisor	Lcom/android/server/am/ActivityStackSupervisor;
    //   1291: getfield 1527	com/android/server/am/ActivityStackSupervisor:mWaitingActivityVisible	Ljava/util/ArrayList;
    //   1294: aload 13
    //   1296: invokevirtual 1353	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1299: pop
    //   1300: aload_0
    //   1301: getfield 115	com/android/server/am/ActivityStarter:mService	Lcom/android/server/am/ActivityManagerService;
    //   1304: invokevirtual 1465	com/android/server/am/ActivityManagerService:wait	()V
    //   1307: iload_2
    //   1308: istore 11
    //   1310: aload 13
    //   1312: getfield 1468	android/app/IActivityManager$WaitResult:timeout	Z
    //   1315: ifne -505 -> 810
    //   1318: iload_2
    //   1319: istore 11
    //   1321: aload 13
    //   1323: getfield 1479	android/app/IActivityManager$WaitResult:who	Landroid/content/ComponentName;
    //   1326: ifnonnull -516 -> 810
    //   1329: goto -29 -> 1300
    //   1332: aload_1
    //   1333: iconst_0
    //   1334: aaload
    //   1335: astore_1
    //   1336: goto -514 -> 822
    //   1339: astore_1
    //   1340: aload 35
    //   1342: monitorexit
    //   1343: invokestatic 1053	com/android/server/am/ActivityManagerService:resetPriorityAfterLockedSection	()V
    //   1346: aload_1
    //   1347: athrow
    //   1348: astore_1
    //   1349: goto -9 -> 1340
    //   1352: astore_1
    //   1353: goto -13 -> 1340
    //   1356: astore_1
    //   1357: goto -17 -> 1340
    //   1360: astore_3
    //   1361: goto -54 -> 1307
    //   1364: astore_3
    //   1365: goto -664 -> 701
    //   1368: aload 4
    //   1370: astore 30
    //   1372: aload 29
    //   1374: astore 15
    //   1376: aload 12
    //   1378: astore 4
    //   1380: aload 30
    //   1382: astore 12
    //   1384: aload_1
    //   1385: astore 29
    //   1387: aload 5
    //   1389: astore 30
    //   1391: goto -848 -> 543
    //   1394: goto -1194 -> 200
    //   1397: iconst_0
    //   1398: istore 26
    //   1400: goto -1019 -> 381
    //   1403: aload 4
    //   1405: astore 30
    //   1407: aload 29
    //   1409: astore 15
    //   1411: aload 12
    //   1413: astore 4
    //   1415: aload 30
    //   1417: astore 12
    //   1419: aload_1
    //   1420: astore 29
    //   1422: aload 5
    //   1424: astore 30
    //   1426: goto -883 -> 543
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1429	0	this	ActivityStarter
    //   0	1429	1	paramIApplicationThread	IApplicationThread
    //   0	1429	2	paramInt1	int
    //   0	1429	3	paramString1	String
    //   0	1429	4	paramIntent	Intent
    //   0	1429	5	paramString2	String
    //   0	1429	6	paramIVoiceInteractionSession	IVoiceInteractionSession
    //   0	1429	7	paramIVoiceInteractor	IVoiceInteractor
    //   0	1429	8	paramIBinder	IBinder
    //   0	1429	9	paramString3	String
    //   0	1429	10	paramInt2	int
    //   0	1429	11	paramInt3	int
    //   0	1429	12	paramProfilerInfo	android.app.ProfilerInfo
    //   0	1429	13	paramWaitResult	android.app.IActivityManager.WaitResult
    //   0	1429	14	paramConfiguration	android.content.res.Configuration
    //   0	1429	15	paramBundle	Bundle
    //   0	1429	16	paramBoolean	boolean
    //   0	1429	17	paramInt4	int
    //   0	1429	18	paramIActivityContainer	android.app.IActivityContainer
    //   0	1429	19	paramTaskRecord	TaskRecord
    //   170	1054	20	i	int
    //   319	530	21	j	int
    //   324	529	22	k	int
    //   1087	97	23	m	int
    //   1092	80	24	n	int
    //   43	1135	25	bool1	boolean
    //   162	1237	26	bool2	boolean
    //   126	472	27	l	long
    //   65	1356	29	localObject1	Object
    //   96	1329	30	localObject2	Object
    //   135	1033	31	localObject3	Object
    //   1079	112	32	localObject4	Object
    //   1082	98	33	localObject5	Object
    //   54	500	34	localIntent	Intent
    //   235	1106	35	localActivityManagerService	ActivityManagerService
    //   222	1028	36	localActivityOptions	ActivityOptions
    //   229	638	37	localActivityContainer	ActivityStackSupervisor.ActivityContainer
    // Exception table:
    //   from	to	target	type
    //   128	137	308	finally
    //   142	164	308	finally
    //   240	243	1339	finally
    //   248	271	1339	finally
    //   316	326	1339	finally
    //   338	349	1339	finally
    //   349	358	1339	finally
    //   363	378	1339	finally
    //   381	425	1339	finally
    //   425	430	1339	finally
    //   435	476	1339	finally
    //   481	515	1339	finally
    //   866	873	1339	finally
    //   880	890	1339	finally
    //   895	904	1339	finally
    //   904	953	1339	finally
    //   958	967	1339	finally
    //   967	1038	1339	finally
    //   1038	1075	1339	finally
    //   1197	1254	1339	finally
    //   1084	1094	1348	finally
    //   1097	1110	1348	finally
    //   1115	1121	1352	finally
    //   543	645	1356	finally
    //   645	657	1356	finally
    //   666	673	1356	finally
    //   681	694	1356	finally
    //   694	701	1356	finally
    //   701	718	1356	finally
    //   721	730	1356	finally
    //   740	807	1356	finally
    //   810	822	1356	finally
    //   822	835	1356	finally
    //   1155	1167	1356	finally
    //   1268	1276	1356	finally
    //   1279	1300	1356	finally
    //   1300	1307	1356	finally
    //   1310	1318	1356	finally
    //   1321	1329	1356	finally
    //   1300	1307	1360	java/lang/InterruptedException
    //   694	701	1364	java/lang/InterruptedException
  }
  
  void startConfirmCredentialIntent(Intent paramIntent)
  {
    paramIntent.addFlags(276840448);
    ActivityOptions localActivityOptions = ActivityOptions.makeBasic();
    localActivityOptions.setLaunchTaskId(this.mSupervisor.getHomeActivity().task.taskId);
    this.mService.mContext.startActivityAsUser(paramIntent, localActivityOptions.toBundle(), UserHandle.CURRENT);
  }
  
  void startHomeActivityLocked(Intent paramIntent, ActivityInfo paramActivityInfo, String paramString)
  {
    this.mSupervisor.moveHomeStackTaskToTop(1, paramString);
    startActivityLocked(null, paramIntent, null, null, paramActivityInfo, null, null, null, null, null, 0, 0, 0, null, 0, 0, 0, null, false, false, null, null, null);
    if (this.mSupervisor.inResumeTopActivity) {
      this.mSupervisor.scheduleResumeTopActivities();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityStarter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */