package com.android.server.am;

import android.app.Dialog;
import android.app.IStopUserCallback;
import android.app.IUserSwitchObserver;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentReceiver.Stub;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IProgressListener;
import android.os.IRemoteCallback;
import android.os.IRemoteCallback.Stub;
import android.os.IUserManager.Stub;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.os.storage.StorageManager;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.OpFeatures;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.ProgressReporter;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.SystemServiceManager;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.WindowManagerService;
import com.oem.os.IOemExService;
import com.oem.os.IOemExService.Stub;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

final class UserController
{
  private static final boolean IS_SCENE_MODES_FEATURED = OpFeatures.isSupport(new int[] { 25 });
  static final int MAX_RUNNING_USERS = 3;
  private static final String OEM_ACTION_BOOT_COMPLETED = "com.oem.intent.action.BOOT_COMPLETED";
  private static final String TAG = "ActivityManager";
  static final int USER_SWITCH_TIMEOUT = 2000;
  private volatile ArraySet<String> mCurWaitingUserSwitchCallbacks;
  private int[] mCurrentProfileIds = new int[0];
  private int mCurrentUserId = 0;
  private final Handler mHandler;
  private final LockPatternUtils mLockPatternUtils;
  private IOemExService mOemExSvc;
  private final ActivityManagerService mService;
  private int[] mStartedUserArray = { 0 };
  @GuardedBy("mService")
  private final SparseArray<UserState> mStartedUsers = new SparseArray();
  private int mTargetUserId = 55536;
  private final ArrayList<Integer> mUserLru = new ArrayList();
  private volatile UserManagerService mUserManager;
  private UserManagerInternal mUserManagerInternal;
  private final SparseIntArray mUserProfileGroupIdsSelfLocked = new SparseIntArray();
  private final RemoteCallbackList<IUserSwitchObserver> mUserSwitchObservers = new RemoteCallbackList();
  
  UserController(ActivityManagerService paramActivityManagerService)
  {
    this.mService = paramActivityManagerService;
    this.mHandler = this.mService.mHandler;
    paramActivityManagerService = new UserState(UserHandle.SYSTEM);
    this.mStartedUsers.put(0, paramActivityManagerService);
    this.mUserLru.add(Integer.valueOf(0));
    this.mLockPatternUtils = new LockPatternUtils(this.mService.mContext);
    updateStartedUserArrayLocked();
  }
  
  private void finishUserBoot(UserState paramUserState)
  {
    finishUserBoot(paramUserState, null);
  }
  
  private void finishUserBoot(UserState paramUserState, IIntentReceiver paramIIntentReceiver)
  {
    int i = paramUserState.mHandle.getIdentifier();
    Slog.d(TAG, "Finishing user boot " + i);
    for (;;)
    {
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        Object localObject = this.mStartedUsers.get(i);
        if (localObject != paramUserState)
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        if (paramUserState.setState(0, 1))
        {
          getUserManagerInternal().setUserState(i, paramUserState.state);
          int j = (int)(SystemClock.elapsedRealtime() / 1000L);
          MetricsLogger.histogram(this.mService.mContext, "framework_locked_boot_completed", j);
          paramUserState = new Intent("android.intent.action.LOCKED_BOOT_COMPLETED", null);
          paramUserState.putExtra("android.intent.extra.user_handle", i);
          paramUserState.addFlags(150994944);
          localObject = this.mService;
          j = ActivityManagerService.MY_PID;
          ((ActivityManagerService)localObject).broadcastIntentLocked(null, null, paramUserState, null, paramIIntentReceiver, 0, null, null, new String[] { "android.permission.RECEIVE_BOOT_COMPLETED" }, -1, null, true, false, j, 1000, i);
        }
        if (!getUserManager().isManagedProfile(i)) {
          break label357;
        }
        paramUserState = getUserManager().getProfileParent(i);
        if ((paramUserState != null) && (isUserRunningLocked(paramUserState.id, 4)))
        {
          Slog.d(TAG, "User " + i + " (parent " + paramUserState.id + "): attempting unlock because parent is unlocked");
          maybeUnlockUser(i);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
        if (paramUserState == null)
        {
          paramUserState = "<null>";
          Slog.d(TAG, "User " + i + " (parent " + paramUserState + "): delaying unlock because parent is locked");
        }
      }
      paramUserState = String.valueOf(paramUserState.id);
      continue;
      label357:
      maybeUnlockUser(i);
    }
  }
  
  private void finishUserUnlockedCompleted(final UserState paramUserState)
  {
    int i = paramUserState.mHandle.getIdentifier();
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject = this.mStartedUsers.get(paramUserState.mHandle.getIdentifier());
      if (localObject != paramUserState)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      paramUserState = getUserInfo(i);
      if (paramUserState == null)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      boolean bool = StorageManager.isUserKeyUnlocked(i);
      if (!bool)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      this.mUserManager.onUserLoggedIn(i);
      if ((!paramUserState.isInitialized()) && (i != 0))
      {
        Slog.d(TAG, "Initializing user #" + i);
        localObject = new Intent("android.intent.action.USER_INITIALIZE");
        ((Intent)localObject).addFlags(268435456);
        this.mService.broadcastIntentLocked(null, null, (Intent)localObject, null, new IIntentReceiver.Stub()
        {
          public void performReceive(Intent paramAnonymousIntent, int paramAnonymousInt1, String paramAnonymousString, Bundle paramAnonymousBundle, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt2)
          {
            UserController.-wrap0(UserController.this).makeInitialized(paramUserState.id);
          }
        }, 0, null, null, null, -1, null, true, false, ActivityManagerService.MY_PID, 1000, i);
      }
      Slog.d(TAG, "Sending BOOT_COMPLETE user #" + i);
      int j = (int)(SystemClock.elapsedRealtime() / 1000L);
      MetricsLogger.histogram(this.mService.mContext, "framework_boot_completed", j);
      paramUserState = new Intent("android.intent.action.BOOT_COMPLETED", null);
      paramUserState.putExtra("android.intent.extra.user_handle", i);
      paramUserState.addFlags(150994944);
      localObject = this.mService;
      j = ActivityManagerService.MY_PID;
      ((ActivityManagerService)localObject).broadcastIntentLocked(null, null, paramUserState, null, null, 0, null, null, new String[] { "android.permission.RECEIVE_BOOT_COMPLETED" }, -1, null, true, false, j, 1000, i);
      paramUserState = new Intent("com.oem.intent.action.BOOT_COMPLETED", null);
      paramUserState.putExtra("android.intent.extra.user_handle", i);
      paramUserState.addFlags(268435456);
      localObject = this.mService;
      j = ActivityManagerService.MY_PID;
      ((ActivityManagerService)localObject).broadcastIntentLocked(null, null, paramUserState, null, null, 0, null, null, new String[] { "android.permission.RECEIVE_BOOT_COMPLETED" }, -1, null, true, false, j, 1000, i);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  private void finishUserUnlocking(UserState paramUserState)
  {
    int j = paramUserState.mHandle.getIdentifier();
    int i = 0;
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject = this.mStartedUsers.get(paramUserState.mHandle.getIdentifier());
      if (localObject != paramUserState)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      boolean bool = StorageManager.isUserKeyUnlocked(j);
      if (!bool)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      if (paramUserState.setState(1, 2))
      {
        getUserManagerInternal().setUserState(j, paramUserState.state);
        i = 1;
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (i != 0)
      {
        paramUserState.mUnlockProgress.start();
        paramUserState.mUnlockProgress.setProgress(5, this.mService.mContext.getString(17040322));
        this.mUserManager.onBeforeUnlockUser(j);
        paramUserState.mUnlockProgress.setProgress(20);
        this.mHandler.obtainMessage(61, j, 0, paramUserState).sendToTarget();
      }
      return;
    }
  }
  
  private void forceStopUserLocked(int paramInt, String paramString)
  {
    this.mService.forceStopPackageLocked(null, -1, false, false, true, false, false, paramInt, paramString);
    paramString = new Intent("android.intent.action.USER_STOPPED");
    paramString.addFlags(1342177280);
    paramString.putExtra("android.intent.extra.user_handle", paramInt);
    this.mService.broadcastIntentLocked(null, null, paramString, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, -1);
  }
  
  private IMountService getMountService()
  {
    return IMountService.Stub.asInterface(ServiceManager.getService("mount"));
  }
  
  private UserManagerService getUserManager()
  {
    UserManagerService localUserManagerService2 = this.mUserManager;
    UserManagerService localUserManagerService1 = localUserManagerService2;
    if (localUserManagerService2 == null)
    {
      localUserManagerService1 = (UserManagerService)IUserManager.Stub.asInterface(ServiceManager.getService("user"));
      this.mUserManager = localUserManagerService1;
    }
    return localUserManagerService1;
  }
  
  private UserManagerInternal getUserManagerInternal()
  {
    if (this.mUserManagerInternal == null) {
      this.mUserManagerInternal = ((UserManagerInternal)LocalServices.getService(UserManagerInternal.class));
    }
    return this.mUserManagerInternal;
  }
  
  private int[] getUsersToStopLocked(int paramInt)
  {
    int m = this.mStartedUsers.size();
    IntArray localIntArray = new IntArray();
    localIntArray.add(paramInt);
    label132:
    label157:
    label162:
    label171:
    for (;;)
    {
      int j;
      int i1;
      int i;
      synchronized (this.mUserProfileGroupIdsSelfLocked)
      {
        int n = this.mUserProfileGroupIdsSelfLocked.get(paramInt, 55536);
        j = 0;
        if (j < m)
        {
          i1 = ((UserState)this.mStartedUsers.valueAt(j)).mHandle.getIdentifier();
          i = this.mUserProfileGroupIdsSelfLocked.get(i1, 55536);
          if (n == 55536) {
            break label162;
          }
          if (n != i) {
            break label157;
          }
          i = 1;
          break label132;
          localIntArray.add(i1);
        }
      }
      return ((IntArray)localObject).toArray();
      if (i1 == paramInt) {}
      for (int k = 1;; k = 0)
      {
        if ((i != 0) && (k == 0)) {
          break label171;
        }
        j += 1;
        break;
        i = 0;
        break label132;
        i = 0;
        break label132;
      }
    }
  }
  
  private boolean isCurrentUserLocked(int paramInt)
  {
    return paramInt == getCurrentOrTargetUserIdLocked();
  }
  
  private static void notifyFinished(int paramInt, IProgressListener paramIProgressListener)
  {
    if (paramIProgressListener == null) {
      return;
    }
    try
    {
      paramIProgressListener.onFinished(paramInt, null);
      return;
    }
    catch (RemoteException paramIProgressListener) {}
  }
  
  private void stopBackgroundUsersIfEnforced(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    if (!hasUserRestriction("no_run_in_background", paramInt)) {
      return;
    }
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      if (ActivityManagerDebugConfig.DEBUG_MU) {
        Slog.i(TAG, "stopBackgroundUsersIfEnforced stopping " + paramInt + " and related users");
      }
      stopUsersLocked(paramInt, false, null);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  private void stopGuestOrEphemeralUserIfBackground()
  {
    for (;;)
    {
      int i;
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int j = this.mUserLru.size();
        i = 0;
        if (i < j)
        {
          Integer localInteger = (Integer)this.mUserLru.get(i);
          Object localObject2 = (UserState)this.mStartedUsers.get(localInteger.intValue());
          if ((localInteger.intValue() != 0) && (localInteger.intValue() != this.mCurrentUserId) && (((UserState)localObject2).state != 4) && (((UserState)localObject2).state != 5))
          {
            localObject2 = getUserInfo(localInteger.intValue());
            if (((UserInfo)localObject2).isEphemeral()) {
              ((UserManagerInternal)LocalServices.getService(UserManagerInternal.class)).onEphemeralUserStop(localInteger.intValue());
            }
            if ((((UserInfo)localObject2).isGuest()) || (((UserInfo)localObject2).isEphemeral())) {
              stopUsersLocked(localInteger.intValue(), true, null);
            }
          }
        }
        else
        {
          ActivityManagerService.resetPriorityAfterLockedSection();
          return;
        }
      }
      i += 1;
    }
  }
  
  private void stopSingleUserLocked(final int paramInt, final IStopUserCallback paramIStopUserCallback)
  {
    if (ActivityManagerDebugConfig.DEBUG_MU) {
      Slog.i(TAG, "stopSingleUserLocked userId=" + paramInt);
    }
    final Object localObject = (UserState)this.mStartedUsers.get(paramInt);
    if (localObject == null)
    {
      if (paramIStopUserCallback != null) {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            try
            {
              paramIStopUserCallback.userStopped(paramInt);
              return;
            }
            catch (RemoteException localRemoteException) {}
          }
        });
      }
      return;
    }
    if (paramIStopUserCallback != null) {
      ((UserState)localObject).mStopCallbacks.add(paramIStopUserCallback);
    }
    long l;
    if ((((UserState)localObject).state != 4) && (((UserState)localObject).state != 5))
    {
      ((UserState)localObject).setState(4);
      getUserManagerInternal().setUserState(paramInt, ((UserState)localObject).state);
      updateStartedUserArrayLocked();
      l = Binder.clearCallingIdentity();
    }
    try
    {
      paramIStopUserCallback = new Intent("android.intent.action.USER_STOPPING");
      paramIStopUserCallback.addFlags(1073741824);
      paramIStopUserCallback.putExtra("android.intent.extra.user_handle", paramInt);
      paramIStopUserCallback.putExtra("android.intent.extra.SHUTDOWN_USERSPACE_ONLY", true);
      localObject = new IIntentReceiver.Stub()
      {
        public void performReceive(Intent paramAnonymousIntent, int paramAnonymousInt1, String paramAnonymousString, Bundle paramAnonymousBundle, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt2)
        {
          UserController.-get1(UserController.this).post(new Runnable()
          {
            public void run()
            {
              UserController.this.finishUserStopping(this.val$userId, this.val$uss);
            }
          });
        }
      };
      this.mService.clearBroadcastQueueForUserLocked(paramInt);
      ActivityManagerService localActivityManagerService = this.mService;
      paramInt = ActivityManagerService.MY_PID;
      localActivityManagerService.broadcastIntentLocked(null, null, paramIStopUserCallback, null, (IIntentReceiver)localObject, 0, null, null, new String[] { "android.permission.INTERACT_ACROSS_USERS" }, -1, null, true, false, paramInt, 1000, -1);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private int stopUsersLocked(int paramInt, boolean paramBoolean, IStopUserCallback paramIStopUserCallback)
  {
    if (paramInt == 0) {
      return -3;
    }
    if (isCurrentUserLocked(paramInt)) {
      return -2;
    }
    int[] arrayOfInt = getUsersToStopLocked(paramInt);
    int i = 0;
    while (i < arrayOfInt.length)
    {
      j = arrayOfInt[i];
      if ((j == 0) || (isCurrentUserLocked(j)))
      {
        if (ActivityManagerDebugConfig.DEBUG_MU) {
          Slog.i(TAG, "stopUsersLocked cannot stop related user " + j);
        }
        if (paramBoolean)
        {
          Slog.i(TAG, "Force stop user " + paramInt + ". Related users will not be stopped");
          stopSingleUserLocked(paramInt, paramIStopUserCallback);
          return 0;
        }
        return -4;
      }
      i += 1;
    }
    if (ActivityManagerDebugConfig.DEBUG_MU) {
      Slog.i(TAG, "stopUsersLocked usersToStop=" + Arrays.toString(arrayOfInt));
    }
    int j = arrayOfInt.length;
    i = 0;
    if (i < j)
    {
      int k = arrayOfInt[i];
      if (k == paramInt) {}
      for (IStopUserCallback localIStopUserCallback = paramIStopUserCallback;; localIStopUserCallback = null)
      {
        stopSingleUserLocked(k, localIStopUserCallback);
        i += 1;
        break;
      }
    }
    return 0;
  }
  
  private void updateCurrentProfileIdsLocked()
  {
    ??? = getUserManager().getProfiles(this.mCurrentUserId, false);
    Object localObject2 = new int[((List)???).size()];
    int i = 0;
    while (i < localObject2.length)
    {
      localObject2[i] = ((UserInfo)((List)???).get(i)).id;
      i += 1;
    }
    this.mCurrentProfileIds = ((int[])localObject2);
    synchronized (this.mUserProfileGroupIdsSelfLocked)
    {
      this.mUserProfileGroupIdsSelfLocked.clear();
      localObject2 = getUserManager().getUsers(false);
      i = 0;
      while (i < ((List)localObject2).size())
      {
        UserInfo localUserInfo = (UserInfo)((List)localObject2).get(i);
        if (localUserInfo.profileGroupId != 55536) {
          this.mUserProfileGroupIdsSelfLocked.put(localUserInfo.id, localUserInfo.profileGroupId);
        }
        i += 1;
      }
      return;
    }
  }
  
  private void updateStartedUserArrayLocked()
  {
    int j = 0;
    int i = 0;
    UserState localUserState;
    int k;
    while (i < this.mStartedUsers.size())
    {
      localUserState = (UserState)this.mStartedUsers.valueAt(i);
      k = j;
      if (localUserState.state != 4)
      {
        k = j;
        if (localUserState.state != 5) {
          k = j + 1;
        }
      }
      i += 1;
      j = k;
    }
    this.mStartedUserArray = new int[j];
    j = 0;
    i = 0;
    while (i < this.mStartedUsers.size())
    {
      localUserState = (UserState)this.mStartedUsers.valueAt(i);
      k = j;
      if (localUserState.state != 4)
      {
        k = j;
        if (localUserState.state != 5)
        {
          this.mStartedUserArray[j] = this.mStartedUsers.keyAt(i);
          k = j + 1;
        }
      }
      i += 1;
      j = k;
    }
  }
  
  void continueUserSwitch(UserState paramUserState, int paramInt1, int paramInt2)
  {
    Slog.d(TAG, "Continue user switch oldUser #" + paramInt1 + ", newUser #" + paramInt2);
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      this.mService.mWindowManager.stopFreezingScreen();
      ActivityManagerService.resetPriorityAfterLockedSection();
      paramUserState.switching = false;
      this.mHandler.removeMessages(56);
      this.mHandler.sendMessage(this.mHandler.obtainMessage(56, paramInt2, 0));
      stopGuestOrEphemeralUserIfBackground();
      stopBackgroundUsersIfEnforced(paramInt1);
      return;
    }
  }
  
  void dispatchForegroundProfileChanged(int paramInt)
  {
    int j = this.mUserSwitchObservers.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        ((IUserSwitchObserver)this.mUserSwitchObservers.getBroadcastItem(i)).onForegroundProfileSwitch(paramInt);
        i += 1;
        continue;
        this.mUserSwitchObservers.finishBroadcast();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  void dispatchUserSwitch(final UserState paramUserState, final int paramInt1, final int paramInt2)
  {
    Slog.d(TAG, "Dispatch onUserSwitching oldUser #" + paramInt1 + " newUser #" + paramInt2);
    int j = this.mUserSwitchObservers.beginBroadcast();
    if (j > 0)
    {
      ??? = new ArraySet();
      for (;;)
      {
        int i;
        synchronized (this.mService)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          paramUserState.switching = true;
          this.mCurWaitingUserSwitchCallbacks = ((ArraySet)???);
          ActivityManagerService.resetPriorityAfterLockedSection();
          ??? = new AtomicInteger(j);
          i = 0;
          if (i >= j) {
            break label275;
          }
        }
        for (;;)
        {
          try
          {
            str = "#" + i + " " + this.mUserSwitchObservers.getBroadcastCookie(i);
          }
          catch (RemoteException localRemoteException)
          {
            final String str;
            continue;
          }
          synchronized (this.mService)
          {
            ActivityManagerService.boostPriorityForLockedSection();
            ((ArraySet)???).add(str);
            ActivityManagerService.resetPriorityAfterLockedSection();
            ??? = new IRemoteCallback.Stub()
            {
              public void sendResult(Bundle arg1)
                throws RemoteException
              {
                synchronized (UserController.-get2(UserController.this))
                {
                  ActivityManagerService.boostPriorityForLockedSection();
                  ArraySet localArraySet1 = localObject1;
                  ArraySet localArraySet2 = UserController.-get0(UserController.this);
                  if (localArraySet1 != localArraySet2)
                  {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                  }
                  localObject1.remove(str);
                  if (localObject2.decrementAndGet() == 0) {
                    UserController.this.sendContinueUserSwitchLocked(paramUserState, paramInt1, paramInt2);
                  }
                  ActivityManagerService.resetPriorityAfterLockedSection();
                  return;
                }
              }
            };
            ((IUserSwitchObserver)this.mUserSwitchObservers.getBroadcastItem(i)).onUserSwitching(paramInt2, (IRemoteCallback)???);
            i += 1;
            break;
            paramUserState = finally;
            ActivityManagerService.resetPriorityAfterLockedSection();
            throw paramUserState;
          }
        }
      }
    }
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      sendContinueUserSwitchLocked(paramUserState, paramInt1, paramInt2);
      ActivityManagerService.resetPriorityAfterLockedSection();
      label275:
      this.mUserSwitchObservers.finishBroadcast();
      return;
    }
  }
  
  void dispatchUserSwitchComplete(int paramInt)
  {
    int j = this.mUserSwitchObservers.beginBroadcast();
    int i = 0;
    for (;;)
    {
      if (i < j) {}
      try
      {
        ((IUserSwitchObserver)this.mUserSwitchObservers.getBroadcastItem(i)).onUserSwitchComplete(paramInt);
        i += 1;
        continue;
        this.mUserSwitchObservers.finishBroadcast();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  }
  
  void dump(PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    paramPrintWriter.println("  mStartedUsers:");
    int i = 0;
    while (i < this.mStartedUsers.size())
    {
      ??? = (UserState)this.mStartedUsers.valueAt(i);
      paramPrintWriter.print("    User #");
      paramPrintWriter.print(((UserState)???).mHandle.getIdentifier());
      paramPrintWriter.print(": ");
      ((UserState)???).dump("", paramPrintWriter);
      i += 1;
    }
    paramPrintWriter.print("  mStartedUserArray: [");
    i = 0;
    while (i < this.mStartedUserArray.length)
    {
      if (i > 0) {
        paramPrintWriter.print(", ");
      }
      paramPrintWriter.print(this.mStartedUserArray[i]);
      i += 1;
    }
    paramPrintWriter.println("]");
    paramPrintWriter.print("  mUserLru: [");
    i = 0;
    while (i < this.mUserLru.size())
    {
      if (i > 0) {
        paramPrintWriter.print(", ");
      }
      paramPrintWriter.print(this.mUserLru.get(i));
      i += 1;
    }
    paramPrintWriter.println("]");
    if (paramBoolean)
    {
      paramPrintWriter.print("  mStartedUserArray: ");
      paramPrintWriter.println(Arrays.toString(this.mStartedUserArray));
    }
    synchronized (this.mUserProfileGroupIdsSelfLocked)
    {
      if (this.mUserProfileGroupIdsSelfLocked.size() > 0)
      {
        paramPrintWriter.println("  mUserProfileGroupIds:");
        i = 0;
        while (i < this.mUserProfileGroupIdsSelfLocked.size())
        {
          paramPrintWriter.print("    User #");
          paramPrintWriter.print(this.mUserProfileGroupIdsSelfLocked.keyAt(i));
          paramPrintWriter.print(" -> profile #");
          paramPrintWriter.println(this.mUserProfileGroupIdsSelfLocked.valueAt(i));
          i += 1;
        }
      }
      return;
    }
  }
  
  boolean exists(int paramInt)
  {
    return getUserManager().exists(paramInt);
  }
  
  void finishUserStopped(UserState arg1)
  {
    int k = ???.mHandle.getIdentifier();
    int i;
    for (;;)
    {
      ArrayList localArrayList;
      int j;
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        localArrayList = new ArrayList(???.mStopCallbacks);
        Object localObject2 = this.mStartedUsers.get(k);
        if (localObject2 != ???)
        {
          i = 0;
          ActivityManagerService.resetPriorityAfterLockedSection();
          j = 0;
          label61:
          if (j >= localArrayList.size()) {
            break label200;
          }
          if (i == 0) {
            break label177;
          }
        }
      }
      try
      {
        ((IStopUserCallback)localArrayList.get(j)).userStopped(k);
        for (;;)
        {
          j += 1;
          break label61;
          if (???.state != 5)
          {
            i = 0;
            break;
          }
          i = 1;
          this.mStartedUsers.remove(k);
          getUserManagerInternal().removeUserState(k);
          this.mUserLru.remove(Integer.valueOf(k));
          updateStartedUserArrayLocked();
          this.mService.onUserStoppedLocked(k);
          forceStopUserLocked(k, "finish user");
          break;
          ??? = finally;
          ActivityManagerService.resetPriorityAfterLockedSection();
          throw ???;
          label177:
          ((IStopUserCallback)localArrayList.get(j)).userStopAborted(k);
        }
      }
      catch (RemoteException ???)
      {
        for (;;) {}
      }
    }
    label200:
    if (i != 0) {
      this.mService.mSystemServiceManager.cleanupUser(k);
    }
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      this.mService.mStackSupervisor.removeUserLocked(k);
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (getUserInfo(k).isEphemeral()) {
        this.mUserManager.removeUser(k);
      }
      return;
    }
  }
  
  void finishUserStopping(int paramInt, UserState arg2)
  {
    Intent localIntent = new Intent("android.intent.action.ACTION_SHUTDOWN");
    IIntentReceiver.Stub local5 = new IIntentReceiver.Stub()
    {
      public void performReceive(Intent paramAnonymousIntent, int paramAnonymousInt1, String paramAnonymousString, Bundle paramAnonymousBundle, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt2)
      {
        UserController.-get1(UserController.this).post(new Runnable()
        {
          public void run()
          {
            UserController.this.finishUserStopped(this.val$uss);
          }
        });
      }
    };
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      int i = ???.state;
      if (i != 4)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      ???.setState(5);
      ActivityManagerService.resetPriorityAfterLockedSection();
      getUserManagerInternal().setUserState(paramInt, ???.state);
      this.mService.mBatteryStatsService.noteEvent(16391, Integer.toString(paramInt), paramInt);
      this.mService.mSystemServiceManager.stopUser(paramInt);
    }
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      this.mService.broadcastIntentLocked(null, null, localIntent, null, local5, 0, null, null, null, -1, null, true, false, ActivityManagerService.MY_PID, 1000, paramInt);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
      ??? = finally;
      ActivityManagerService.resetPriorityAfterLockedSection();
      throw ???;
    }
  }
  
  void finishUserSwitch(UserState paramUserState)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      finishUserBoot(paramUserState);
      startProfilesLocked();
      stopRunningUsersLocked(3);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  void finishUserUnlocked(final UserState paramUserState)
  {
    int i = paramUserState.mHandle.getIdentifier();
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject = this.mStartedUsers.get(paramUserState.mHandle.getIdentifier());
      if (localObject != paramUserState)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      boolean bool = StorageManager.isUserKeyUnlocked(i);
      if (!bool)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      if (paramUserState.setState(2, 3))
      {
        getUserManagerInternal().setUserState(i, paramUserState.state);
        paramUserState.mUnlockProgress.finish();
        localObject = new Intent("android.intent.action.USER_UNLOCKED");
        ((Intent)localObject).putExtra("android.intent.extra.user_handle", i);
        ((Intent)localObject).addFlags(1342177280);
        this.mService.broadcastIntentLocked(null, null, (Intent)localObject, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, i);
        if (getUserInfo(i).isManagedProfile())
        {
          localObject = getUserManager().getProfileParent(i);
          if (localObject != null)
          {
            Intent localIntent = new Intent("android.intent.action.MANAGED_PROFILE_UNLOCKED");
            localIntent.putExtra("android.intent.extra.USER", UserHandle.of(i));
            localIntent.addFlags(1342177280);
            this.mService.broadcastIntentLocked(null, null, localIntent, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, ((UserInfo)localObject).id);
          }
        }
        localObject = getUserInfo(i);
        if (Objects.equals(((UserInfo)localObject).lastLoggedInFingerprint, Build.FINGERPRINT)) {
          break label336;
        }
        if (!((UserInfo)localObject).isManagedProfile()) {
          break label331;
        }
        if (!paramUserState.tokenProvided) {
          break label321;
        }
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i)) {
          break label326;
        }
        bool = false;
      }
      for (;;)
      {
        new PreBootBroadcaster(this.mService, i, null, bool)
        {
          public void onFinished()
          {
            UserController.-wrap1(UserController.this, paramUserState);
          }
        }.sendNext();
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
        label321:
        bool = true;
        continue;
        label326:
        bool = true;
        continue;
        label331:
        bool = false;
      }
      label336:
      finishUserUnlockedCompleted(paramUserState);
    }
  }
  
  int getCurrentOrTargetUserIdLocked()
  {
    if (this.mTargetUserId != 55536) {
      return this.mTargetUserId;
    }
    return this.mCurrentUserId;
  }
  
  int[] getCurrentProfileIdsLocked()
  {
    return this.mCurrentProfileIds;
  }
  
  UserInfo getCurrentUser()
  {
    if ((this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") != 0) && (this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0))
    {
      ??? = "Permission Denial: getCurrentUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS";
      Slog.w(TAG, (String)???);
      throw new SecurityException((String)???);
    }
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      UserInfo localUserInfo = getCurrentUserLocked();
      ActivityManagerService.resetPriorityAfterLockedSection();
      return localUserInfo;
    }
  }
  
  int getCurrentUserIdLocked()
  {
    return this.mCurrentUserId;
  }
  
  UserInfo getCurrentUserLocked()
  {
    if (this.mTargetUserId != 55536) {}
    for (int i = this.mTargetUserId;; i = this.mCurrentUserId) {
      return getUserInfo(i);
    }
  }
  
  Set<Integer> getProfileIds(int paramInt)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = getUserManager().getProfiles(paramInt, false).iterator();
    while (localIterator.hasNext()) {
      localHashSet.add(Integer.valueOf(((UserInfo)localIterator.next()).id));
    }
    return localHashSet;
  }
  
  int[] getStartedUserArrayLocked()
  {
    return this.mStartedUserArray;
  }
  
  UserState getStartedUserStateLocked(int paramInt)
  {
    return (UserState)this.mStartedUsers.get(paramInt);
  }
  
  int[] getUserIds()
  {
    return getUserManager().getUserIds();
  }
  
  UserInfo getUserInfo(int paramInt)
  {
    return getUserManager().getUserInfo(paramInt);
  }
  
  int[] getUsers()
  {
    UserManagerService localUserManagerService = getUserManager();
    if (localUserManagerService != null) {
      return localUserManagerService.getUserIds();
    }
    return new int[] { 0 };
  }
  
  int handleIncomingUser(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, String paramString1, String paramString2)
  {
    int j = UserHandle.getUserId(paramInt2);
    if (j == paramInt3) {
      return paramInt3;
    }
    int k = unsafeConvertIncomingUserLocked(paramInt3);
    int i = k;
    boolean bool;
    if (paramInt2 != 0)
    {
      i = k;
      if (paramInt2 != 1000)
      {
        if (this.mService.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS_FULL", paramInt1, paramInt2, -1, true) != 0) {
          break label118;
        }
        bool = true;
      }
    }
    for (;;)
    {
      i = k;
      if (!bool)
      {
        if (paramInt3 != -3) {
          break label212;
        }
        i = j;
      }
      if ((paramBoolean) || (i >= 0)) {
        break label366;
      }
      throw new IllegalArgumentException("Call does not support special user #" + i);
      label118:
      if (paramInt4 == 2)
      {
        bool = false;
      }
      else if (this.mService.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS", paramInt1, paramInt2, -1, true) != 0)
      {
        bool = false;
      }
      else if (paramInt4 == 0)
      {
        bool = true;
      }
      else
      {
        if (paramInt4 != 1) {
          break;
        }
        bool = isSameProfileGroup(j, k);
      }
    }
    throw new IllegalArgumentException("Unknown mode: " + paramInt4);
    label212:
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("Permission Denial: ");
    localStringBuilder.append(paramString1);
    if (paramString2 != null)
    {
      localStringBuilder.append(" from ");
      localStringBuilder.append(paramString2);
    }
    localStringBuilder.append(" asks to run as user ");
    localStringBuilder.append(paramInt3);
    localStringBuilder.append(" but is calling from user ");
    localStringBuilder.append(UserHandle.getUserId(paramInt2));
    localStringBuilder.append("; this requires ");
    localStringBuilder.append("android.permission.INTERACT_ACROSS_USERS_FULL");
    if (paramInt4 != 2)
    {
      localStringBuilder.append(" or ");
      localStringBuilder.append("android.permission.INTERACT_ACROSS_USERS");
    }
    paramString1 = localStringBuilder.toString();
    Slog.w(TAG, paramString1);
    throw new SecurityException(paramString1);
    label366:
    if ((paramInt2 == 2000) && (i >= 0) && (hasUserRestriction("no_debugging_features", i))) {
      throw new SecurityException("Shell does not have permission to access user " + i + "\n " + Debug.getCallers(3));
    }
    return i;
  }
  
  boolean hasStartedUserState(int paramInt)
  {
    return this.mStartedUsers.get(paramInt) != null;
  }
  
  boolean hasUserRestriction(String paramString, int paramInt)
  {
    return getUserManager().hasUserRestriction(paramString, paramInt);
  }
  
  boolean isCurrentProfileLocked(int paramInt)
  {
    return ArrayUtils.contains(this.mCurrentProfileIds, paramInt);
  }
  
  boolean isLockScreenDisabled(int paramInt)
  {
    return this.mLockPatternUtils.isLockScreenDisabled(paramInt);
  }
  
  boolean isSameProfileGroup(int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (paramInt1 == paramInt2) {
      return true;
    }
    synchronized (this.mUserProfileGroupIdsSelfLocked)
    {
      paramInt1 = this.mUserProfileGroupIdsSelfLocked.get(paramInt1, 55536);
      paramInt2 = this.mUserProfileGroupIdsSelfLocked.get(paramInt2, 55536);
      if (paramInt1 != 55536)
      {
        if (paramInt1 == paramInt2) {}
        for (;;)
        {
          return bool;
          bool = false;
        }
      }
      bool = false;
    }
  }
  
  boolean isUserRunningLocked(int paramInt1, int paramInt2)
  {
    UserState localUserState = getStartedUserStateLocked(paramInt1);
    if (localUserState == null) {
      return false;
    }
    if ((paramInt2 & 0x1) != 0) {
      return true;
    }
    if ((paramInt2 & 0x2) != 0)
    {
      switch (localUserState.state)
      {
      default: 
        return false;
      }
      return true;
    }
    if ((paramInt2 & 0x8) != 0)
    {
      switch (localUserState.state)
      {
      default: 
        return false;
      }
      return true;
    }
    if ((paramInt2 & 0x4) != 0)
    {
      switch (localUserState.state)
      {
      default: 
        return false;
      }
      return true;
    }
    return true;
  }
  
  boolean isUserStoppingOrShuttingDownLocked(int paramInt)
  {
    UserState localUserState = getStartedUserStateLocked(paramInt);
    if (localUserState == null) {
      return false;
    }
    return (localUserState.state == 4) || (localUserState.state == 5);
  }
  
  boolean maybeUnlockUser(int paramInt)
  {
    return unlockUserCleared(paramInt, null, null, null);
  }
  
  void moveUserToForegroundLocked(UserState paramUserState, int paramInt1, int paramInt2)
  {
    if (this.mService.mStackSupervisor.switchUserLocked(paramInt2, paramUserState)) {
      this.mService.startHomeActivityLocked(paramInt2, "moveUserToForeground");
    }
    for (;;)
    {
      EventLogTags.writeAmSwitchUser(paramInt2);
      sendUserSwitchBroadcastsLocked(paramInt1, paramInt2);
      return;
      this.mService.mStackSupervisor.resumeFocusedStackTopActivityLocked();
    }
  }
  
  void onSystemReady()
  {
    updateCurrentProfileIdsLocked();
  }
  
  void registerUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver, String paramString)
  {
    Preconditions.checkNotNull(paramString, "Observer name cannot be null");
    if (this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)
    {
      paramIUserSwitchObserver = "Permission Denial: registerUserSwitchObserver() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
      Slog.w(TAG, paramIUserSwitchObserver);
      throw new SecurityException(paramIUserSwitchObserver);
    }
    this.mUserSwitchObservers.register(paramIUserSwitchObserver, paramString);
  }
  
  void sendBootCompletedLocked(IIntentReceiver paramIIntentReceiver)
  {
    int i = 0;
    while (i < this.mStartedUsers.size())
    {
      finishUserBoot((UserState)this.mStartedUsers.valueAt(i), paramIIntentReceiver);
      i += 1;
    }
  }
  
  void sendContinueUserSwitchLocked(UserState paramUserState, int paramInt1, int paramInt2)
  {
    this.mCurWaitingUserSwitchCallbacks = null;
    this.mHandler.removeMessages(36);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(35, paramInt1, paramInt2, paramUserState));
  }
  
  void sendUserSwitchBroadcastsLocked(int paramInt1, int paramInt2)
  {
    long l = Binder.clearCallingIdentity();
    if (paramInt1 >= 0) {}
    try
    {
      Object localObject1 = getUserManager().getProfiles(paramInt1, false);
      int i = ((List)localObject1).size();
      paramInt1 = 0;
      int j;
      Object localObject3;
      while (paramInt1 < i)
      {
        j = ((UserInfo)((List)localObject1).get(paramInt1)).id;
        localObject3 = new Intent("android.intent.action.USER_BACKGROUND");
        ((Intent)localObject3).addFlags(1342177280);
        ((Intent)localObject3).putExtra("android.intent.extra.user_handle", j);
        this.mService.broadcastIntentLocked(null, null, (Intent)localObject3, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, j);
        paramInt1 += 1;
      }
      if (paramInt2 >= 0)
      {
        localObject1 = getUserManager().getProfiles(paramInt2, false);
        i = ((List)localObject1).size();
        paramInt1 = 0;
        while (paramInt1 < i)
        {
          j = ((UserInfo)((List)localObject1).get(paramInt1)).id;
          localObject3 = new Intent("android.intent.action.USER_FOREGROUND");
          ((Intent)localObject3).addFlags(1342177280);
          ((Intent)localObject3).putExtra("android.intent.extra.user_handle", j);
          this.mService.broadcastIntentLocked(null, null, (Intent)localObject3, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, j);
          paramInt1 += 1;
        }
        localObject1 = new Intent("android.intent.action.USER_SWITCHED");
        ((Intent)localObject1).addFlags(1342177280);
        ((Intent)localObject1).putExtra("android.intent.extra.user_handle", paramInt2);
        localObject3 = this.mService;
        paramInt1 = ActivityManagerService.MY_PID;
        ((ActivityManagerService)localObject3).broadcastIntentLocked(null, null, (Intent)localObject1, null, null, 0, null, null, new String[] { "android.permission.MANAGE_USERS" }, -1, null, false, false, paramInt1, 1000, -1);
        if (IS_SCENE_MODES_FEATURED) {
          Slog.i(TAG, "[scene] end switching user");
        }
      }
      try
      {
        if (this.mOemExSvc == null) {
          this.mOemExSvc = IOemExService.Stub.asInterface(ServiceManager.getService("OEMExService"));
        }
        if (this.mService.isKeyguardDone()) {
          this.mOemExSvc.monitorSceneChanging(true);
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.e(TAG, "[scene] error while operating the scene mode controller: " + localRemoteException);
        }
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  int setTargetUserIdLocked(int paramInt)
  {
    this.mTargetUserId = paramInt;
    return paramInt;
  }
  
  boolean shouldConfirmCredentials(int paramInt)
  {
    boolean bool = false;
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Object localObject2 = this.mStartedUsers.get(paramInt);
      if (localObject2 == null)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return false;
      }
      ActivityManagerService.resetPriorityAfterLockedSection();
      if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt)) {
        return false;
      }
    }
    ??? = (KeyguardManager)this.mService.mContext.getSystemService("keyguard");
    if (((KeyguardManager)???).isDeviceLocked(paramInt)) {
      bool = ((KeyguardManager)???).isDeviceSecure(paramInt);
    }
    return bool;
  }
  
  void showUserSwitchDialog(Pair<UserInfo, UserInfo> paramPair)
  {
    new UserSwitchingDialog(this.mService, this.mService.mContext, (UserInfo)paramPair.first, (UserInfo)paramPair.second, true).show();
    if (IS_SCENE_MODES_FEATURED) {
      Slog.i(TAG, "[scene] start switching user");
    }
    try
    {
      if (this.mOemExSvc == null) {
        this.mOemExSvc = IOemExService.Stub.asInterface(ServiceManager.getService("OEMExService"));
      }
      this.mOemExSvc.monitorSceneChanging(false);
      return;
    }
    catch (RemoteException paramPair)
    {
      Slog.e(TAG, "[scene] error while operating the scene mode controller: " + paramPair);
    }
  }
  
  void startProfilesLocked()
  {
    if (ActivityManagerDebugConfig.DEBUG_MU) {
      Slog.i(TAG, "startProfilesLocked");
    }
    Object localObject = getUserManager().getProfiles(this.mCurrentUserId, false);
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      UserInfo localUserInfo = (UserInfo)((Iterator)localObject).next();
      if (((localUserInfo.flags & 0x10) == 16) && (localUserInfo.id != this.mCurrentUserId) && (!localUserInfo.isQuietModeEnabled())) {
        localArrayList.add(localUserInfo);
      }
    }
    int j = localArrayList.size();
    int i = 0;
    while ((i < j) && (i < 2))
    {
      startUser(((UserInfo)localArrayList.get(i)).id, false);
      i += 1;
    }
    if (i < j) {
      Slog.w(TAG, "More profiles than MAX_RUNNING_USERS");
    }
  }
  
  boolean startUser(int paramInt, boolean paramBoolean)
  {
    if (this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)
    {
      ??? = "Permission Denial: switchUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
      Slog.w(TAG, (String)???);
      throw new SecurityException((String)???);
    }
    Slog.i(TAG, "Starting userid:" + paramInt + " fg:" + paramBoolean);
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        synchronized (this.mService)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          int j = this.mCurrentUserId;
          if (j == paramInt)
          {
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
          }
          this.mService.mStackSupervisor.setLockTaskModeLocked(null, 0, "startUser", false);
          Object localObject3 = getUserInfo(paramInt);
          if (localObject3 == null)
          {
            Slog.w(TAG, "No user info for user #" + paramInt);
            ActivityManagerService.resetPriorityAfterLockedSection();
            return false;
          }
          if ((paramBoolean) && (((UserInfo)localObject3).isManagedProfile()))
          {
            Slog.w(TAG, "Cannot switch to User #" + paramInt + ": not a full user");
            ActivityManagerService.resetPriorityAfterLockedSection();
            return false;
          }
          if (paramBoolean) {
            this.mService.mWindowManager.startFreezingScreen(17432706, 17432705);
          }
          i = 0;
          if (this.mStartedUsers.get(paramInt) == null)
          {
            localObject3 = new UserState(UserHandle.of(paramInt));
            this.mStartedUsers.put(paramInt, localObject3);
            getUserManagerInternal().setUserState(paramInt, ((UserState)localObject3).state);
            updateStartedUserArrayLocked();
            i = 1;
          }
          localObject3 = (UserState)this.mStartedUsers.get(paramInt);
          Object localObject4 = Integer.valueOf(paramInt);
          this.mUserLru.remove(localObject4);
          this.mUserLru.add(localObject4);
          if (paramBoolean)
          {
            this.mCurrentUserId = paramInt;
            this.mService.updateUserConfigurationLocked();
            this.mTargetUserId = 55536;
            updateCurrentProfileIdsLocked();
            this.mService.mWindowManager.setCurrentUser(paramInt, this.mCurrentProfileIds);
            this.mService.mWindowManager.lockNow(null);
            if (((UserState)localObject3).state == 4)
            {
              ((UserState)localObject3).setState(((UserState)localObject3).lastState);
              getUserManagerInternal().setUserState(paramInt, ((UserState)localObject3).state);
              updateStartedUserArrayLocked();
              i = 1;
              if (((UserState)localObject3).state == 0)
              {
                getUserManager().onBeforeStartUser(paramInt);
                this.mHandler.sendMessage(this.mHandler.obtainMessage(42, paramInt, 0));
              }
              if (paramBoolean)
              {
                this.mHandler.sendMessage(this.mHandler.obtainMessage(43, paramInt, j));
                this.mHandler.removeMessages(34);
                this.mHandler.removeMessages(36);
                this.mHandler.sendMessage(this.mHandler.obtainMessage(34, j, paramInt, localObject3));
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(36, j, paramInt, localObject3), 2000L);
              }
              if (i != 0)
              {
                localObject4 = new Intent("android.intent.action.USER_STARTED");
                ((Intent)localObject4).addFlags(1342177280);
                ((Intent)localObject4).putExtra("android.intent.extra.user_handle", paramInt);
                this.mService.broadcastIntentLocked(null, null, (Intent)localObject4, null, null, 0, null, null, null, -1, null, false, false, ActivityManagerService.MY_PID, 1000, paramInt);
              }
              if (!paramBoolean) {
                break label902;
              }
              moveUserToForegroundLocked((UserState)localObject3, j, paramInt);
              if (i != 0)
              {
                localObject3 = new Intent("android.intent.action.USER_STARTING");
                ((Intent)localObject3).addFlags(1073741824);
                ((Intent)localObject3).putExtra("android.intent.extra.user_handle", paramInt);
                localObject4 = this.mService;
                IIntentReceiver.Stub local6 = new IIntentReceiver.Stub()
                {
                  public void performReceive(Intent paramAnonymousIntent, int paramAnonymousInt1, String paramAnonymousString, Bundle paramAnonymousBundle, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, int paramAnonymousInt2)
                    throws RemoteException
                  {}
                };
                paramInt = ActivityManagerService.MY_PID;
                ((ActivityManagerService)localObject4).broadcastIntentLocked(null, null, (Intent)localObject3, null, local6, 0, null, null, new String[] { "android.permission.INTERACT_ACROSS_USERS" }, -1, null, true, false, paramInt, 1000, -1);
              }
              ActivityManagerService.resetPriorityAfterLockedSection();
              return true;
            }
          }
          else
          {
            localObject4 = Integer.valueOf(this.mCurrentUserId);
            updateCurrentProfileIdsLocked();
            this.mService.mWindowManager.setCurrentProfileIds(this.mCurrentProfileIds);
            this.mUserLru.remove(localObject4);
            this.mUserLru.add(localObject4);
          }
        }
        if (localUserState.state != 5) {
          continue;
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      localUserState.setState(0);
      getUserManagerInternal().setUserState(paramInt, localUserState.state);
      updateStartedUserArrayLocked();
      int i = 1;
      continue;
      label902:
      this.mService.mUserController.finishUserBoot(localUserState);
    }
  }
  
  boolean startUserInForeground(int paramInt, Dialog paramDialog)
  {
    boolean bool = startUser(paramInt, true);
    paramDialog.dismiss();
    return bool;
  }
  
  void stopRunningUsersLocked(int paramInt)
  {
    int i = this.mUserLru.size();
    int j = 0;
    while ((i > paramInt) && (j < this.mUserLru.size()))
    {
      Integer localInteger = (Integer)this.mUserLru.get(j);
      UserState localUserState = (UserState)this.mStartedUsers.get(localInteger.intValue());
      if (localUserState == null)
      {
        this.mUserLru.remove(j);
        i -= 1;
      }
      else if ((localUserState.state == 4) || (localUserState.state == 5))
      {
        i -= 1;
        j += 1;
      }
      else
      {
        int k;
        if ((localInteger.intValue() == 0) || (localInteger.intValue() == this.mCurrentUserId))
        {
          k = i;
          if (UserInfo.isSystemOnly(localInteger.intValue())) {
            k = i - 1;
          }
          j += 1;
          i = k;
        }
        else
        {
          k = i;
          if (stopUsersLocked(localInteger.intValue(), false, null) != 0) {
            k = i - 1;
          }
          i = k - 1;
          j += 1;
        }
      }
    }
  }
  
  int stopUser(int paramInt, boolean paramBoolean, IStopUserCallback paramIStopUserCallback)
  {
    if (this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)
    {
      paramIStopUserCallback = "Permission Denial: switchUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
      Slog.w(TAG, paramIStopUserCallback);
      throw new SecurityException(paramIStopUserCallback);
    }
    if ((paramInt < 0) || (paramInt == 0)) {
      throw new IllegalArgumentException("Can't stop system user " + paramInt);
    }
    this.mService.enforceShellRestriction("no_debugging_features", paramInt);
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      paramInt = stopUsersLocked(paramInt, paramBoolean, paramIStopUserCallback);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return paramInt;
    }
  }
  
  void timeoutUserSwitch(UserState paramUserState, int paramInt1, int paramInt2)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Slog.wtf(TAG, "User switch timeout: from " + paramInt1 + " to " + paramInt2 + ". Observers that didn't send results: " + this.mCurWaitingUserSwitchCallbacks);
      sendContinueUserSwitchLocked(paramUserState, paramInt1, paramInt2);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return;
    }
  }
  
  boolean unlockUser(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, IProgressListener paramIProgressListener)
  {
    if (this.mService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)
    {
      paramArrayOfByte1 = "Permission Denial: unlockUser() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS_FULL";
      Slog.w(TAG, paramArrayOfByte1);
      throw new SecurityException(paramArrayOfByte1);
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = unlockUserCleared(paramInt, paramArrayOfByte1, paramArrayOfByte2, paramIProgressListener);
      return bool;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  boolean unlockUserCleared(int paramInt, byte[] arg2, byte[] paramArrayOfByte2, IProgressListener paramIProgressListener)
  {
    synchronized (this.mService)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      UserInfo localUserInfo;
      IMountService localIMountService;
      if (!StorageManager.isUserKeyUnlocked(paramInt))
      {
        localUserInfo = getUserInfo(paramInt);
        localIMountService = getMountService();
      }
      try
      {
        localIMountService.unlockUserKey(paramInt, localUserInfo.serialNumber, ???, paramArrayOfByte2);
        paramArrayOfByte2 = (UserState)this.mStartedUsers.get(paramInt);
        if (paramArrayOfByte2 == null)
        {
          notifyFinished(paramInt, paramIProgressListener);
          ActivityManagerService.resetPriorityAfterLockedSection();
          return false;
        }
      }
      catch (RemoteException|RuntimeException paramArrayOfByte2)
      {
        for (;;)
        {
          Slog.w(TAG, "Failed to unlock: " + paramArrayOfByte2.getMessage());
        }
      }
    }
    paramArrayOfByte2.mUnlockProgress.addListener(paramIProgressListener);
    boolean bool;
    if (??? != null) {
      bool = true;
    }
    for (;;)
    {
      paramArrayOfByte2.tokenProvided = bool;
      ActivityManagerService.resetPriorityAfterLockedSection();
      finishUserUnlocking(paramArrayOfByte2);
      paramArrayOfByte2 = new ArraySet();
      synchronized (this.mService)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        int i = 0;
        for (;;)
        {
          if (i < this.mStartedUsers.size())
          {
            int j = this.mStartedUsers.keyAt(i);
            paramIProgressListener = getUserManager().getProfileParent(j);
            if ((paramIProgressListener != null) && (paramIProgressListener.id == paramInt) && (j != paramInt))
            {
              Slog.d(TAG, "User " + j + " (parent " + paramIProgressListener.id + "): attempting unlock because parent was just unlocked");
              paramArrayOfByte2.add(Integer.valueOf(j));
            }
            i += 1;
            continue;
            bool = false;
            break;
          }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        i = paramArrayOfByte2.size();
        paramInt = 0;
        if (paramInt < i)
        {
          maybeUnlockUser(((Integer)paramArrayOfByte2.valueAt(paramInt)).intValue());
          paramInt += 1;
        }
      }
    }
    return true;
  }
  
  void unregisterUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver)
  {
    this.mUserSwitchObservers.unregister(paramIUserSwitchObserver);
  }
  
  int unsafeConvertIncomingUserLocked(int paramInt)
  {
    int i;
    if (paramInt != -2)
    {
      i = paramInt;
      if (paramInt != -3) {}
    }
    else
    {
      i = getCurrentUserIdLocked();
    }
    return i;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/UserController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */