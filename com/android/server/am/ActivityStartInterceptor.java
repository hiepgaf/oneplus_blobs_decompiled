package com.android.server.am;

import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.server.LocalServices;

class ActivityStartInterceptor
{
  ActivityInfo mAInfo;
  ActivityOptions mActivityOptions;
  private String mCallingPackage;
  int mCallingPid;
  int mCallingUid;
  TaskRecord mInTask;
  Intent mIntent;
  ResolveInfo mRInfo;
  private int mRealCallingPid;
  private int mRealCallingUid;
  String mResolvedType;
  private final ActivityManagerService mService;
  private int mStartFlags;
  private final ActivityStackSupervisor mSupervisor;
  private int mUserId;
  private UserManager mUserManager;
  
  ActivityStartInterceptor(ActivityManagerService paramActivityManagerService, ActivityStackSupervisor paramActivityStackSupervisor)
  {
    this.mService = paramActivityManagerService;
    this.mSupervisor = paramActivityStackSupervisor;
  }
  
  private boolean interceptQuietProfileIfNeeded()
  {
    if (!this.mUserManager.isQuietModeEnabled(UserHandle.of(this.mUserId))) {
      return false;
    }
    Object localObject = this.mService;
    String str1 = this.mCallingPackage;
    int i = this.mCallingUid;
    int j = this.mUserId;
    Intent localIntent = this.mIntent;
    String str2 = this.mResolvedType;
    localObject = ((ActivityManagerService)localObject).getIntentSenderLocked(2, str1, i, j, null, null, 0, new Intent[] { localIntent }, new String[] { str2 }, 1342177280, null);
    this.mIntent = UnlaunchableAppActivity.createInQuietModeDialogIntent(this.mUserId, new IntentSender((IIntentSender)localObject));
    this.mCallingPid = this.mRealCallingPid;
    this.mCallingUid = this.mRealCallingUid;
    this.mResolvedType = null;
    localObject = this.mUserManager.getProfileParent(this.mUserId);
    this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, ((UserInfo)localObject).id);
    this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, this.mRInfo, this.mStartFlags, null);
    return true;
  }
  
  private boolean interceptSuspendPackageIfNeed()
  {
    if ((this.mAInfo == null) || (this.mAInfo.applicationInfo == null)) {}
    while ((this.mAInfo.applicationInfo.flags & 0x40000000) == 0) {
      return false;
    }
    Object localObject = (DevicePolicyManagerInternal)LocalServices.getService(DevicePolicyManagerInternal.class);
    if (localObject == null) {
      return false;
    }
    this.mIntent = ((DevicePolicyManagerInternal)localObject).createPackageSuspendedDialogIntent(this.mAInfo.packageName, this.mUserId);
    this.mCallingPid = this.mRealCallingPid;
    this.mCallingUid = this.mRealCallingUid;
    this.mResolvedType = null;
    localObject = this.mUserManager.getProfileParent(this.mUserId);
    if (localObject != null) {}
    for (this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, ((UserInfo)localObject).id);; this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, this.mUserId))
    {
      this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, this.mRInfo, this.mStartFlags, null);
      return true;
    }
  }
  
  private Intent interceptWithConfirmCredentialsIfNeeded(Intent paramIntent, String paramString1, ActivityInfo paramActivityInfo, String paramString2, int paramInt)
  {
    if (!this.mService.mUserController.shouldConfirmCredentials(paramInt)) {
      return null;
    }
    if ((paramActivityInfo.directBootAware) && (this.mService.mUserController.isUserRunningLocked(paramInt, 2))) {
      return null;
    }
    paramIntent = this.mService.getIntentSenderLocked(2, paramString2, Binder.getCallingUid(), paramInt, null, null, 0, new Intent[] { paramIntent }, new String[] { paramString1 }, 1409286144, null);
    paramString1 = ((KeyguardManager)this.mService.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, paramInt);
    if (paramString1 == null) {
      return null;
    }
    paramString1.setFlags(276840448);
    paramString1.putExtra("android.intent.extra.PACKAGE_NAME", paramActivityInfo.packageName);
    paramString1.putExtra("android.intent.extra.INTENT", new IntentSender(paramIntent));
    return paramString1;
  }
  
  private boolean interceptWorkProfileChallengeIfNeeded()
  {
    Object localObject = interceptWithConfirmCredentialsIfNeeded(this.mIntent, this.mResolvedType, this.mAInfo, this.mCallingPackage, this.mUserId);
    if (localObject == null) {
      return false;
    }
    this.mIntent = ((Intent)localObject);
    this.mCallingPid = this.mRealCallingPid;
    this.mCallingUid = this.mRealCallingUid;
    this.mResolvedType = null;
    if (this.mInTask != null)
    {
      this.mIntent.putExtra("android.intent.extra.TASK_ID", this.mInTask.taskId);
      this.mInTask = null;
    }
    if (this.mActivityOptions == null) {
      this.mActivityOptions = ActivityOptions.makeBasic();
    }
    localObject = this.mSupervisor.getHomeActivity();
    if ((localObject != null) && (((ActivityRecord)localObject).task != null)) {
      this.mActivityOptions.setLaunchTaskId(((ActivityRecord)localObject).task.taskId);
    }
    localObject = this.mUserManager.getProfileParent(this.mUserId);
    this.mRInfo = this.mSupervisor.resolveIntent(this.mIntent, this.mResolvedType, ((UserInfo)localObject).id);
    this.mAInfo = this.mSupervisor.resolveActivity(this.mIntent, this.mRInfo, this.mStartFlags, null);
    return true;
  }
  
  void intercept(Intent paramIntent, ResolveInfo paramResolveInfo, ActivityInfo paramActivityInfo, String paramString, TaskRecord paramTaskRecord, int paramInt1, int paramInt2, ActivityOptions paramActivityOptions)
  {
    this.mUserManager = UserManager.get(this.mService.mContext);
    this.mIntent = paramIntent;
    this.mCallingPid = paramInt1;
    this.mCallingUid = paramInt2;
    this.mRInfo = paramResolveInfo;
    this.mAInfo = paramActivityInfo;
    this.mResolvedType = paramString;
    this.mInTask = paramTaskRecord;
    this.mActivityOptions = paramActivityOptions;
    if (interceptSuspendPackageIfNeed()) {
      return;
    }
    if (interceptQuietProfileIfNeeded()) {
      return;
    }
    interceptWorkProfileChallengeIfNeeded();
  }
  
  void setStates(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    this.mRealCallingPid = paramInt2;
    this.mRealCallingUid = paramInt3;
    this.mUserId = paramInt1;
    this.mStartFlags = paramInt4;
    this.mCallingPackage = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityStartInterceptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */