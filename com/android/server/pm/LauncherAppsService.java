package com.android.server.pm;

import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ILauncherApps.Stub;
import android.content.pm.IOnAppsChangedListener;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.ShortcutServiceInternal.ShortcutChangeListener;
import android.content.pm.UserInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.Slog;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.util.ArrayList;
import java.util.List;

public class LauncherAppsService
  extends SystemService
{
  private final LauncherAppsImpl mLauncherAppsImpl;
  
  public LauncherAppsService(Context paramContext)
  {
    super(paramContext);
    this.mLauncherAppsImpl = new LauncherAppsImpl(paramContext);
  }
  
  public void onStart()
  {
    publishBinderService("launcherapps", this.mLauncherAppsImpl);
  }
  
  static class BroadcastCookie
  {
    public final String packageName;
    public final UserHandle user;
    
    BroadcastCookie(UserHandle paramUserHandle, String paramString)
    {
      this.user = paramUserHandle;
      this.packageName = paramString;
    }
  }
  
  static class LauncherAppsImpl
    extends ILauncherApps.Stub
  {
    private static final boolean DEBUG = false;
    private static final String TAG = "LauncherAppsService";
    private final ActivityManagerInternal mActivityManagerInternal;
    private final Handler mCallbackHandler;
    private final Context mContext;
    private final PackageCallbackList<IOnAppsChangedListener> mListeners = new PackageCallbackList();
    private final MyPackageMonitor mPackageMonitor = new MyPackageMonitor(null);
    private final PackageManager mPm;
    private final ShortcutServiceInternal mShortcutServiceInternal;
    private final UserManager mUm;
    
    public LauncherAppsImpl(Context paramContext)
    {
      this.mContext = paramContext;
      this.mPm = this.mContext.getPackageManager();
      this.mUm = ((UserManager)this.mContext.getSystemService("user"));
      this.mActivityManagerInternal = ((ActivityManagerInternal)Preconditions.checkNotNull((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)));
      this.mShortcutServiceInternal = ((ShortcutServiceInternal)Preconditions.checkNotNull((ShortcutServiceInternal)LocalServices.getService(ShortcutServiceInternal.class)));
      this.mShortcutServiceInternal.addListener(this.mPackageMonitor);
      this.mCallbackHandler = BackgroundThread.getHandler();
    }
    
    private void ensureInUserProfiles(int paramInt, String paramString)
    {
      int i = injectCallingUserId();
      if (paramInt == i) {
        return;
      }
      long l = injectClearCallingIdentity();
      do
      {
        UserInfo localUserInfo1;
        UserInfo localUserInfo2;
        try
        {
          localUserInfo1 = this.mUm.getUserInfo(i);
          localUserInfo2 = this.mUm.getUserInfo(paramInt);
          if ((localUserInfo2 == null) || (localUserInfo2.profileGroupId == 55536)) {
            throw new SecurityException(paramString);
          }
        }
        finally
        {
          injectRestoreCallingIdentity(l);
        }
        paramInt = localUserInfo2.profileGroupId;
        i = localUserInfo1.profileGroupId;
      } while (paramInt != i);
      injectRestoreCallingIdentity(l);
    }
    
    private void ensureInUserProfiles(UserHandle paramUserHandle, String paramString)
    {
      ensureInUserProfiles(paramUserHandle.getIdentifier(), paramString);
    }
    
    private void ensureShortcutPermission(String paramString, int paramInt)
    {
      verifyCallingPackage(paramString);
      ensureInUserProfiles(paramInt, "Cannot start activity for unrelated profile " + paramInt);
      if (!this.mShortcutServiceInternal.hasShortcutHostPermission(getCallingUserId(), paramString)) {
        throw new SecurityException("Caller can't access shortcut information");
      }
    }
    
    private void ensureShortcutPermission(String paramString, UserHandle paramUserHandle)
    {
      ensureShortcutPermission(paramString, paramUserHandle.getIdentifier());
    }
    
    private int getCallingUserId()
    {
      return UserHandle.getUserId(injectBinderCallingUid());
    }
    
    private boolean isEnabledProfileOf(UserHandle paramUserHandle1, UserHandle paramUserHandle2, String paramString)
    {
      if (paramUserHandle1.getIdentifier() == paramUserHandle2.getIdentifier()) {
        return true;
      }
      long l = injectClearCallingIdentity();
      try
      {
        paramUserHandle1 = this.mUm.getUserInfo(paramUserHandle1.getIdentifier());
        paramUserHandle2 = this.mUm.getUserInfo(paramUserHandle2.getIdentifier());
        if ((paramUserHandle1 == null) || (paramUserHandle2 == null)) {}
        boolean bool;
        do
        {
          do
          {
            return false;
          } while ((paramUserHandle1.profileGroupId == 55536) || (paramUserHandle1.profileGroupId != paramUserHandle2.profileGroupId));
          bool = paramUserHandle1.isEnabled();
        } while (!bool);
        return true;
      }
      finally
      {
        injectRestoreCallingIdentity(l);
      }
    }
    
    /* Error */
    private boolean isUserEnabled(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 128	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectClearCallingIdentity	()J
      //   4: lstore_2
      //   5: aload_0
      //   6: getfield 88	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:mUm	Landroid/os/UserManager;
      //   9: iload_1
      //   10: invokevirtual 132	android/os/UserManager:getUserInfo	(I)Landroid/content/pm/UserInfo;
      //   13: astore 5
      //   15: aload 5
      //   17: ifnull +18 -> 35
      //   20: aload 5
      //   22: invokevirtual 199	android/content/pm/UserInfo:isEnabled	()Z
      //   25: istore 4
      //   27: aload_0
      //   28: lload_2
      //   29: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   32: iload 4
      //   34: ireturn
      //   35: iconst_0
      //   36: istore 4
      //   38: goto -11 -> 27
      //   41: astore 5
      //   43: aload_0
      //   44: lload_2
      //   45: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   48: aload 5
      //   50: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	51	0	this	LauncherAppsImpl
      //   0	51	1	paramInt	int
      //   4	41	2	l	long
      //   25	12	4	bool	boolean
      //   13	8	5	localUserInfo	UserInfo
      //   41	8	5	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   5	15	41	finally
      //   20	27	41	finally
    }
    
    private boolean isUserEnabled(UserHandle paramUserHandle)
    {
      return isUserEnabled(paramUserHandle.getIdentifier());
    }
    
    /* Error */
    private boolean startShortcutIntentsAsPublisher(Intent[] paramArrayOfIntent, String paramString, Bundle paramBundle, int paramInt)
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore 5
      //   3: aload_0
      //   4: invokevirtual 128	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectClearCallingIdentity	()J
      //   7: lstore 6
      //   9: aload_0
      //   10: getfield 104	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:mActivityManagerInternal	Landroid/app/ActivityManagerInternal;
      //   13: aload_2
      //   14: iload 4
      //   16: aload_1
      //   17: aload_3
      //   18: invokevirtual 210	android/app/ActivityManagerInternal:startActivitiesAsPackage	(Ljava/lang/String;I[Landroid/content/Intent;Landroid/os/Bundle;)I
      //   21: istore 4
      //   23: iload 4
      //   25: iflt +11 -> 36
      //   28: aload_0
      //   29: lload 6
      //   31: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   34: iconst_1
      //   35: ireturn
      //   36: ldc 23
      //   38: new 162	java/lang/StringBuilder
      //   41: dup
      //   42: invokespecial 163	java/lang/StringBuilder:<init>	()V
      //   45: ldc -44
      //   47: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   50: iload 4
      //   52: invokevirtual 172	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   55: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   58: invokestatic 218	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   61: pop
      //   62: iload 4
      //   64: iflt +12 -> 76
      //   67: aload_0
      //   68: lload 6
      //   70: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   73: iload 5
      //   75: ireturn
      //   76: iconst_0
      //   77: istore 5
      //   79: goto -12 -> 67
      //   82: astore_1
      //   83: aload_0
      //   84: lload 6
      //   86: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   89: iconst_0
      //   90: ireturn
      //   91: astore_1
      //   92: aload_0
      //   93: lload 6
      //   95: invokevirtual 147	com/android/server/pm/LauncherAppsService$LauncherAppsImpl:injectRestoreCallingIdentity	(J)V
      //   98: aload_1
      //   99: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	100	0	this	LauncherAppsImpl
      //   0	100	1	paramArrayOfIntent	Intent[]
      //   0	100	2	paramString	String
      //   0	100	3	paramBundle	Bundle
      //   0	100	4	paramInt	int
      //   1	77	5	bool	boolean
      //   7	87	6	l	long
      // Exception table:
      //   from	to	target	type
      //   9	23	82	java/lang/SecurityException
      //   36	62	82	java/lang/SecurityException
      //   9	23	91	finally
      //   36	62	91	finally
    }
    
    private void startWatchingPackageBroadcasts()
    {
      this.mPackageMonitor.register(this.mContext, UserHandle.ALL, true, this.mCallbackHandler);
    }
    
    private void stopWatchingPackageBroadcasts()
    {
      this.mPackageMonitor.unregister();
    }
    
    public void addOnAppsChangedListener(String paramString, IOnAppsChangedListener paramIOnAppsChangedListener)
      throws RemoteException
    {
      verifyCallingPackage(paramString);
      synchronized (this.mListeners)
      {
        if (this.mListeners.getRegisteredCallbackCount() == 0) {
          startWatchingPackageBroadcasts();
        }
        this.mListeners.unregister(paramIOnAppsChangedListener);
        this.mListeners.register(paramIOnAppsChangedListener, new LauncherAppsService.BroadcastCookie(UserHandle.of(getCallingUserId()), paramString));
        return;
      }
    }
    
    void checkCallbackCount()
    {
      synchronized (this.mListeners)
      {
        if (this.mListeners.getRegisteredCallbackCount() == 0) {
          stopWatchingPackageBroadcasts();
        }
        return;
      }
    }
    
    public ApplicationInfo getApplicationInfo(String paramString, int paramInt, UserHandle paramUserHandle)
      throws RemoteException
    {
      ensureInUserProfiles(paramUserHandle, "Cannot check package for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        return null;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = AppGlobals.getPackageManager().getApplicationInfo(paramString, paramInt, paramUserHandle.getIdentifier());
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public ParceledListSlice<ResolveInfo> getLauncherActivities(String paramString, UserHandle paramUserHandle)
      throws RemoteException
    {
      ensureInUserProfiles(paramUserHandle, "Cannot retrieve activities for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        return null;
      }
      Intent localIntent = new Intent("android.intent.action.MAIN", null);
      localIntent.addCategory("android.intent.category.LAUNCHER");
      localIntent.setPackage(paramString);
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = new ParceledListSlice(this.mPm.queryIntentActivitiesAsUser(localIntent, 786432, paramUserHandle.getIdentifier()));
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public ParcelFileDescriptor getShortcutIconFd(String paramString1, String paramString2, String paramString3, int paramInt)
    {
      ensureShortcutPermission(paramString1, paramInt);
      if (!isUserEnabled(paramInt)) {
        return null;
      }
      return this.mShortcutServiceInternal.getShortcutIconFd(getCallingUserId(), paramString1, paramString2, paramString3, paramInt);
    }
    
    public int getShortcutIconResId(String paramString1, String paramString2, String paramString3, int paramInt)
    {
      ensureShortcutPermission(paramString1, paramInt);
      if (!isUserEnabled(paramInt)) {
        return 0;
      }
      return this.mShortcutServiceInternal.getShortcutIconResId(getCallingUserId(), paramString1, paramString2, paramString3, paramInt);
    }
    
    public ParceledListSlice getShortcuts(String paramString1, long paramLong, String paramString2, List paramList, ComponentName paramComponentName, int paramInt, UserHandle paramUserHandle)
    {
      if (("com.android.settings".equals(paramString1)) || ("android".equals(paramString1))) {}
      while (!isUserEnabled(paramUserHandle))
      {
        return new ParceledListSlice(new ArrayList(0));
        ensureShortcutPermission(paramString1, paramUserHandle);
      }
      if ((paramList != null) && (paramString2 == null)) {
        throw new IllegalArgumentException("To query by shortcut ID, package name must also be set");
      }
      return new ParceledListSlice(this.mShortcutServiceInternal.getShortcuts(getCallingUserId(), paramString1, paramLong, paramString2, paramList, paramComponentName, paramInt, paramUserHandle.getIdentifier()));
    }
    
    public boolean hasShortcutHostPermission(String paramString)
    {
      verifyCallingPackage(paramString);
      return this.mShortcutServiceInternal.hasShortcutHostPermission(getCallingUserId(), paramString);
    }
    
    int injectBinderCallingUid()
    {
      return getCallingUid();
    }
    
    final int injectCallingUserId()
    {
      return UserHandle.getUserId(injectBinderCallingUid());
    }
    
    long injectClearCallingIdentity()
    {
      return Binder.clearCallingIdentity();
    }
    
    void injectRestoreCallingIdentity(long paramLong)
    {
      Binder.restoreCallingIdentity(paramLong);
    }
    
    public boolean isActivityEnabled(ComponentName paramComponentName, UserHandle paramUserHandle)
      throws RemoteException
    {
      boolean bool = false;
      ensureInUserProfiles(paramUserHandle, "Cannot check component for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        return false;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramComponentName = AppGlobals.getPackageManager().getActivityInfo(paramComponentName, 786432, paramUserHandle.getIdentifier());
        if (paramComponentName != null) {
          bool = true;
        }
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isPackageEnabled(String paramString, UserHandle paramUserHandle)
      throws RemoteException
    {
      boolean bool = false;
      ensureInUserProfiles(paramUserHandle, "Cannot check package for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        return false;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramString = AppGlobals.getPackageManager().getPackageInfo(paramString, 786432, paramUserHandle.getIdentifier());
        if (paramString != null) {
          bool = paramString.applicationInfo.enabled;
        }
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void pinShortcuts(String paramString1, String paramString2, List<String> paramList, UserHandle paramUserHandle)
    {
      ensureShortcutPermission(paramString1, paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        throw new IllegalStateException("Cannot pin shortcuts for disabled profile " + paramUserHandle);
      }
      this.mShortcutServiceInternal.pinShortcuts(getCallingUserId(), paramString1, paramString2, paramList, paramUserHandle.getIdentifier());
    }
    
    void postToPackageMonitorHandler(Runnable paramRunnable)
    {
      this.mCallbackHandler.post(paramRunnable);
    }
    
    public void removeOnAppsChangedListener(IOnAppsChangedListener paramIOnAppsChangedListener)
      throws RemoteException
    {
      synchronized (this.mListeners)
      {
        this.mListeners.unregister(paramIOnAppsChangedListener);
        if (this.mListeners.getRegisteredCallbackCount() == 0) {
          stopWatchingPackageBroadcasts();
        }
        return;
      }
    }
    
    public ActivityInfo resolveActivity(ComponentName paramComponentName, UserHandle paramUserHandle)
      throws RemoteException
    {
      ensureInUserProfiles(paramUserHandle, "Cannot resolve activity for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        return null;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramComponentName = AppGlobals.getPackageManager().getActivityInfo(paramComponentName, 786432, paramUserHandle.getIdentifier());
        return paramComponentName;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void showAppDetailsAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
      throws RemoteException
    {
      ensureInUserProfiles(paramUserHandle, "Cannot show app details for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        throw new IllegalStateException("Cannot show app details for disabled profile " + paramUserHandle);
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramComponentName = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", paramComponentName.getPackageName(), null));
        paramComponentName.setFlags(268468224);
        paramComponentName.setSourceBounds(paramRect);
        this.mContext.startActivityAsUser(paramComponentName, paramBundle, paramUserHandle);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void startActivityAsUser(ComponentName paramComponentName, Rect paramRect, Bundle paramBundle, UserHandle paramUserHandle)
      throws RemoteException
    {
      ensureInUserProfiles(paramUserHandle, "Cannot start activity for unrelated profile " + paramUserHandle);
      if (!isUserEnabled(paramUserHandle)) {
        throw new IllegalStateException("Cannot start activity for disabled profile " + paramUserHandle);
      }
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.addCategory("android.intent.category.LAUNCHER");
      localIntent.setSourceBounds(paramRect);
      localIntent.addFlags(270532608);
      localIntent.setPackage(paramComponentName.getPackageName());
      long l = Binder.clearCallingIdentity();
      try
      {
        if (!AppGlobals.getPackageManager().getActivityInfo(paramComponentName, 786432, paramUserHandle.getIdentifier()).exported) {
          throw new SecurityException("Cannot launch non-exported components " + paramComponentName);
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      paramRect = this.mPm.queryIntentActivitiesAsUser(localIntent, 786432, paramUserHandle.getIdentifier());
      int j = paramRect.size();
      int i = 0;
      while (i < j)
      {
        ActivityInfo localActivityInfo = ((ResolveInfo)paramRect.get(i)).activityInfo;
        if ((localActivityInfo.packageName.equals(paramComponentName.getPackageName())) && (localActivityInfo.name.equals(paramComponentName.getClassName())))
        {
          localIntent.setComponent(paramComponentName);
          this.mContext.startActivityAsUser(localIntent, paramBundle, paramUserHandle);
          Binder.restoreCallingIdentity(l);
          return;
        }
        i += 1;
      }
      throw new SecurityException("Attempt to launch activity without  category Intent.CATEGORY_LAUNCHER " + paramComponentName);
    }
    
    public boolean startShortcut(String paramString1, String paramString2, String paramString3, Rect paramRect, Bundle paramBundle, int paramInt)
    {
      verifyCallingPackage(paramString1);
      ensureInUserProfiles(paramInt, "Cannot start activity for unrelated profile " + paramInt);
      if (!isUserEnabled(paramInt)) {
        throw new IllegalStateException("Cannot start a shortcut for disabled profile " + paramInt);
      }
      if ((this.mShortcutServiceInternal.isPinnedByCaller(getCallingUserId(), paramString1, paramString2, paramString3, paramInt)) || (("android".equals(paramString1)) && (paramInt == 0))) {}
      for (;;)
      {
        paramString1 = this.mShortcutServiceInternal.createShortcutIntents(getCallingUserId(), paramString1, paramString2, paramString3, paramInt);
        if ((paramString1 != null) && (paramString1.length != 0)) {
          break;
        }
        return false;
        ensureShortcutPermission(paramString1, paramInt);
      }
      paramString1[0].addFlags(268435456);
      paramString1[0].setSourceBounds(paramRect);
      return startShortcutIntentsAsPublisher(paramString1, paramString2, paramBundle, paramInt);
    }
    
    void verifyCallingPackage(String paramString)
    {
      int i = -1;
      try
      {
        int j = this.mPm.getPackageUidAsUser(paramString, 794624, UserHandle.getUserId(getCallingUid()));
        i = j;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          Log.e("LauncherAppsService", "Package not found: " + paramString);
        }
      }
      if (i != Binder.getCallingUid()) {
        throw new SecurityException("Calling package name mismatch");
      }
    }
    
    private class MyPackageMonitor
      extends PackageMonitor
      implements ShortcutServiceInternal.ShortcutChangeListener
    {
      private MyPackageMonitor() {}
      
      private void onShortcutChangedInner(String paramString, int paramInt)
      {
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        label220:
        for (;;)
        {
          try
          {
            UserHandle localUserHandle = UserHandle.of(paramInt);
            int i = 0;
            if (i < j)
            {
              IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
              Object localObject = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
              if (!LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, ((LauncherAppsService.BroadcastCookie)localObject).user, "onShortcutChanged")) {
                break label220;
              }
              int k = ((LauncherAppsService.BroadcastCookie)localObject).user.getIdentifier();
              if (!LauncherAppsService.LauncherAppsImpl.-get1(LauncherAppsService.LauncherAppsImpl.this).hasShortcutHostPermission(k, ((LauncherAppsService.BroadcastCookie)localObject).packageName)) {
                break label220;
              }
              localObject = LauncherAppsService.LauncherAppsImpl.-get1(LauncherAppsService.LauncherAppsImpl.this).getShortcuts(k, ((LauncherAppsService.BroadcastCookie)localObject).packageName, 0L, paramString, null, null, 15, paramInt);
              try
              {
                localIOnAppsChangedListener.onShortcutChanged(localUserHandle, paramString, new ParceledListSlice((List)localObject));
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
            i += 1;
          }
          catch (RuntimeException paramString)
          {
            Log.w("LauncherAppsService", paramString.getMessage(), paramString);
            return;
            return;
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
      }
      
      public void onPackageAdded(String paramString, int paramInt)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackageAdded");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackageAdded(localUserHandle, paramString);
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackageAdded(paramString, paramInt);
      }
      
      public void onPackageModified(String paramString)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackageModified");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackageChanged(localUserHandle, paramString);
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackageModified(paramString);
      }
      
      public void onPackageRemoved(String paramString, int paramInt)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackageRemoved");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackageRemoved(localUserHandle, paramString);
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackageRemoved(paramString, paramInt);
      }
      
      public void onPackagesAvailable(String[] paramArrayOfString)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackagesAvailable");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackagesAvailable(localUserHandle, paramArrayOfString, isReplacing());
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackagesAvailable(paramArrayOfString);
      }
      
      public void onPackagesSuspended(String[] paramArrayOfString)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackagesSuspended");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackagesSuspended(localUserHandle, paramArrayOfString);
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackagesSuspended(paramArrayOfString);
      }
      
      public void onPackagesUnavailable(String[] paramArrayOfString)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackagesUnavailable");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackagesUnavailable(localUserHandle, paramArrayOfString, isReplacing());
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackagesUnavailable(paramArrayOfString);
      }
      
      public void onPackagesUnsuspended(String[] paramArrayOfString)
      {
        UserHandle localUserHandle = new UserHandle(getChangingUserId());
        int j = LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).beginBroadcast();
        int i = 0;
        while (i < j) {
          try
          {
            IOnAppsChangedListener localIOnAppsChangedListener = (IOnAppsChangedListener)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastItem(i);
            LauncherAppsService.BroadcastCookie localBroadcastCookie = (LauncherAppsService.BroadcastCookie)LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).getBroadcastCookie(i);
            boolean bool = LauncherAppsService.LauncherAppsImpl.-wrap0(LauncherAppsService.LauncherAppsImpl.this, localUserHandle, localBroadcastCookie.user, "onPackagesUnsuspended");
            if (!bool) {}
            for (;;)
            {
              i += 1;
              break;
              try
              {
                localIOnAppsChangedListener.onPackagesUnsuspended(localUserHandle, paramArrayOfString);
              }
              catch (RemoteException localRemoteException)
              {
                Slog.d("LauncherAppsService", "Callback failed ", localRemoteException);
              }
            }
          }
          finally
          {
            LauncherAppsService.LauncherAppsImpl.-get0(LauncherAppsService.LauncherAppsImpl.this).finishBroadcast();
          }
        }
        super.onPackagesUnsuspended(paramArrayOfString);
      }
      
      public void onShortcutChanged(String paramString, int paramInt)
      {
        LauncherAppsService.LauncherAppsImpl.this.postToPackageMonitorHandler(new -void_onShortcutChanged_java_lang_String_packageName_int_userId_LambdaImpl0(paramString, paramInt));
      }
    }
    
    class PackageCallbackList<T extends IInterface>
      extends RemoteCallbackList<T>
    {
      PackageCallbackList() {}
      
      public void onCallbackDied(T paramT, Object paramObject)
      {
        LauncherAppsService.LauncherAppsImpl.this.checkCallbackCount();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/LauncherAppsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */