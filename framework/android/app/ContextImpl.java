package android.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayAdjustments;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class ContextImpl
  extends Context
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ContextImpl";
  @GuardedBy("ContextImpl.class")
  private static ArrayMap<String, ArrayMap<File, SharedPreferencesImpl>> sSharedPrefsCache;
  private final IBinder mActivityToken;
  private final String mBasePackageName;
  @GuardedBy("mSync")
  private File mCacheDir;
  @GuardedBy("mSync")
  private File mCodeCacheDir;
  private final ApplicationContentResolver mContentResolver;
  @GuardedBy("mSync")
  private File mDatabasesDir;
  private Display mDisplay;
  @GuardedBy("mSync")
  private File mFilesDir;
  private final int mFlags;
  final ActivityThread mMainThread;
  @GuardedBy("mSync")
  private File mNoBackupFilesDir;
  private final String mOpPackageName;
  private Context mOuterContext = this;
  final LoadedApk mPackageInfo;
  private PackageManager mPackageManager;
  @GuardedBy("mSync")
  private File mPreferencesDir;
  private Context mReceiverRestrictedContext = null;
  private final Resources mResources;
  private final ResourcesManager mResourcesManager;
  final Object[] mServiceCache = SystemServiceRegistry.createServiceCache();
  @GuardedBy("ContextImpl.class")
  private ArrayMap<String, File> mSharedPrefsPaths;
  private final Object mSync = new Object();
  private Resources.Theme mTheme = null;
  private int mThemeResource = 0;
  private final UserHandle mUser;
  
  private ContextImpl(ContextImpl paramContextImpl, ActivityThread paramActivityThread, LoadedApk paramLoadedApk, IBinder paramIBinder, UserHandle paramUserHandle, int paramInt1, Display paramDisplay, Configuration paramConfiguration, int paramInt2)
  {
    int i = paramInt1;
    Object localObject;
    UserHandle localUserHandle;
    label138:
    label176:
    Resources localResources;
    if ((paramInt1 & 0x18) == 0)
    {
      localObject = paramLoadedApk.getDataDirFile();
      if (Objects.equals(localObject, paramLoadedApk.getCredentialProtectedDataDirFile())) {
        i = paramInt1 | 0x10;
      }
    }
    else
    {
      this.mMainThread = paramActivityThread;
      this.mActivityToken = paramIBinder;
      this.mFlags = i;
      localUserHandle = paramUserHandle;
      if (paramUserHandle == null) {
        localUserHandle = Process.myUserHandle();
      }
      this.mUser = localUserHandle;
      this.mPackageInfo = paramLoadedApk;
      this.mResourcesManager = ResourcesManager.getInstance();
      if (paramInt2 == -1) {
        break label330;
      }
      paramInt1 = paramInt2;
      localObject = null;
      if (paramContextImpl != null) {
        localObject = paramContextImpl.getDisplayAdjustments(paramInt1).getCompatibilityInfo();
      }
      paramUserHandle = (UserHandle)localObject;
      if (localObject == null)
      {
        if (paramInt1 != 0) {
          break label351;
        }
        paramUserHandle = paramLoadedApk.getCompatibilityInfo();
      }
      localResources = paramLoadedApk.getResources(paramActivityThread);
      localObject = localResources;
      if (localResources != null)
      {
        if ((paramInt1 == 0) && (paramConfiguration == null)) {
          break label359;
        }
        label202:
        if (paramContextImpl == null) {
          break label392;
        }
        localObject = this.mResourcesManager.getResources(getPackageName(), paramIBinder, paramLoadedApk.getResDir(), paramLoadedApk.getSplitResDirs(), paramLoadedApk.getOverlayDirs(), paramLoadedApk.getApplicationInfo().sharedLibraryFiles, paramInt1, paramConfiguration, paramUserHandle, paramLoadedApk.getClassLoader());
      }
      label250:
      this.mResources = ((Resources)localObject);
      if (paramInt2 != -1) {
        break label439;
      }
      label262:
      this.mDisplay = paramDisplay;
      if (paramContextImpl == null) {
        break label460;
      }
      this.mBasePackageName = paramContextImpl.mBasePackageName;
      this.mOpPackageName = paramContextImpl.mOpPackageName;
    }
    for (;;)
    {
      this.mContentResolver = new ApplicationContentResolver(this, paramActivityThread, localUserHandle);
      return;
      i = paramInt1;
      if (!Objects.equals(localObject, paramLoadedApk.getDeviceProtectedDataDirFile())) {
        break;
      }
      i = paramInt1 | 0x8;
      break;
      label330:
      if (paramDisplay != null)
      {
        paramInt1 = paramDisplay.getDisplayId();
        break label138;
      }
      paramInt1 = 0;
      break label138;
      label351:
      paramUserHandle = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
      break label176;
      label359:
      localObject = localResources;
      if (paramUserHandle == null) {
        break label250;
      }
      localObject = localResources;
      if (paramUserHandle.applicationScale == localResources.getCompatibilityInfo().applicationScale) {
        break label250;
      }
      break label202;
      label392:
      localObject = this.mResourcesManager.createBaseActivityResources(getPackageName(), paramIBinder, paramLoadedApk.getResDir(), paramLoadedApk.getSplitResDirs(), paramLoadedApk.getOverlayDirs(), paramLoadedApk.getApplicationInfo().sharedLibraryFiles, paramInt1, paramConfiguration, paramUserHandle, paramLoadedApk.getClassLoader());
      break label250;
      label439:
      paramDisplay = this.mResourcesManager.getAdjustedDisplay(paramInt1, this.mResources.getDisplayAdjustments());
      break label262;
      label460:
      this.mBasePackageName = paramLoadedApk.mPackageName;
      paramContextImpl = paramLoadedApk.getApplicationInfo();
      if ((paramContextImpl.uid == 1000) && (paramContextImpl.uid != Process.myUid())) {
        this.mOpPackageName = ActivityThread.currentPackageName();
      } else {
        this.mOpPackageName = this.mBasePackageName;
      }
    }
  }
  
  private boolean bindServiceCommon(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, Handler paramHandler, UserHandle paramUserHandle)
  {
    if (paramServiceConnection == null) {
      throw new IllegalArgumentException("connection is null");
    }
    if (this.mPackageInfo != null)
    {
      paramServiceConnection = this.mPackageInfo.getServiceDispatcher(paramServiceConnection, getOuterContext(), paramHandler, paramInt);
      validateServiceIntent(paramIntent);
      int i = paramInt;
      try
      {
        if (getActivityToken() == null)
        {
          i = paramInt;
          if ((paramInt & 0x1) == 0)
          {
            i = paramInt;
            if (this.mPackageInfo != null)
            {
              i = paramInt;
              if (this.mPackageInfo.getApplicationInfo().targetSdkVersion < 14) {
                i = paramInt | 0x20;
              }
            }
          }
        }
        paramIntent.prepareToLeaveProcess(this);
        paramInt = ActivityManagerNative.getDefault().bindService(this.mMainThread.getApplicationThread(), getActivityToken(), paramIntent, paramIntent.resolveTypeIfNeeded(getContentResolver()), paramServiceConnection, i, getOpPackageName(), paramUserHandle.getIdentifier());
        if (paramInt >= 0) {
          break label190;
        }
        throw new SecurityException("Not allowed to bind to service " + paramIntent);
      }
      catch (RemoteException paramIntent)
      {
        throw paramIntent.rethrowFromSystemServer();
      }
    }
    throw new RuntimeException("Not supported in system context");
    label190:
    return paramInt != 0;
  }
  
  private void checkMode(int paramInt)
  {
    if (getApplicationInfo().targetSdkVersion >= 24)
    {
      if ((paramInt & 0x1) != 0) {
        throw new SecurityException("MODE_WORLD_READABLE no longer supported");
      }
      if ((paramInt & 0x2) != 0) {
        throw new SecurityException("MODE_WORLD_WRITEABLE no longer supported");
      }
    }
  }
  
  static ContextImpl createActivityContext(ActivityThread paramActivityThread, LoadedApk paramLoadedApk, IBinder paramIBinder, int paramInt, Configuration paramConfiguration)
  {
    if (paramLoadedApk == null) {
      throw new IllegalArgumentException("packageInfo");
    }
    return new ContextImpl(null, paramActivityThread, paramLoadedApk, paramIBinder, null, 0, null, paramConfiguration, paramInt);
  }
  
  static ContextImpl createAppContext(ActivityThread paramActivityThread, LoadedApk paramLoadedApk)
  {
    if (paramLoadedApk == null) {
      throw new IllegalArgumentException("packageInfo");
    }
    return new ContextImpl(null, paramActivityThread, paramLoadedApk, null, null, 0, null, null, -1);
  }
  
  static ContextImpl createSystemContext(ActivityThread paramActivityThread)
  {
    paramActivityThread = new ContextImpl(null, paramActivityThread, new LoadedApk(paramActivityThread), null, null, 0, null, null, -1);
    paramActivityThread.mResources.updateConfiguration(paramActivityThread.mResourcesManager.getConfiguration(), paramActivityThread.mResourcesManager.getDisplayMetrics());
    return paramActivityThread;
  }
  
  private void enforce(String paramString1, int paramInt1, boolean paramBoolean, int paramInt2, String paramString2)
  {
    if (paramInt1 != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramString2 != null)
      {
        paramString2 = paramString2 + ": ";
        localStringBuilder = localStringBuilder.append(paramString2);
        if (!paramBoolean) {
          break label119;
        }
      }
      label119:
      for (paramString2 = "Neither user " + paramInt2 + " nor current process has ";; paramString2 = "uid " + paramInt2 + " does not have ")
      {
        throw new SecurityException(paramString2 + paramString1 + ".");
        paramString2 = "";
        break;
      }
    }
  }
  
  private void enforceForUri(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, Uri paramUri, String paramString)
  {
    if (paramInt2 != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramString != null)
      {
        paramString = paramString + ": ";
        localStringBuilder = localStringBuilder.append(paramString);
        if (!paramBoolean) {
          break label134;
        }
      }
      label134:
      for (paramString = "Neither user " + paramInt3 + " nor current process has ";; paramString = "User " + paramInt3 + " does not have ")
      {
        throw new SecurityException(paramString + uriModeFlagToString(paramInt1) + " permission on " + paramUri + ".");
        paramString = "";
        break;
      }
    }
  }
  
  private File[] ensureExternalDirsExistOrFilter(File[] paramArrayOfFile)
  {
    File[] arrayOfFile = new File[paramArrayOfFile.length];
    int i = 0;
    File localFile;
    while (i < paramArrayOfFile.length)
    {
      localFile = paramArrayOfFile[i];
      Object localObject1 = localFile;
      if (!localFile.exists())
      {
        localObject1 = localFile;
        if (!localFile.mkdirs())
        {
          localObject1 = localFile;
          if (!localFile.exists()) {
            localObject1 = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
          }
        }
      }
      try
      {
        int j = ((IMountService)localObject1).mkdirs(getPackageName(), localFile.getAbsolutePath());
        localObject1 = localFile;
        if (j != 0)
        {
          Log.w("ContextImpl", "Failed to ensure " + localFile + ": " + j);
          localObject1 = null;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          Log.w("ContextImpl", "Failed to ensure " + localFile + ": " + localException);
          Object localObject2 = null;
        }
      }
      arrayOfFile[i] = localObject1;
      i += 1;
    }
    return arrayOfFile;
  }
  
  private static File ensurePrivateDirExists(File paramFile)
  {
    if (!paramFile.exists()) {}
    try
    {
      Os.mkdir(paramFile.getAbsolutePath(), 505);
      Os.chmod(paramFile.getAbsolutePath(), 505);
      return paramFile;
    }
    catch (ErrnoException localErrnoException)
    {
      while (localErrnoException.errno == OsConstants.EEXIST) {}
      Log.w("ContextImpl", "Failed to ensure " + paramFile + ": " + localErrnoException.getMessage());
    }
    return paramFile;
  }
  
  private File getDatabasesDir()
  {
    synchronized (this.mSync)
    {
      if (this.mDatabasesDir == null)
      {
        if ("android".equals(getPackageName())) {
          this.mDatabasesDir = new File("/data/system");
        }
      }
      else
      {
        File localFile = ensurePrivateDirExists(this.mDatabasesDir);
        return localFile;
      }
      this.mDatabasesDir = new File(getDataDir(), "databases");
    }
  }
  
  static ContextImpl getImpl(Context paramContext)
  {
    while ((paramContext instanceof ContextWrapper))
    {
      Context localContext = ((ContextWrapper)paramContext).getBaseContext();
      if (localContext == null) {
        break;
      }
      paramContext = localContext;
    }
    return (ContextImpl)paramContext;
  }
  
  private File getPreferencesDir()
  {
    synchronized (this.mSync)
    {
      if (this.mPreferencesDir == null) {
        this.mPreferencesDir = new File(getDataDir(), "shared_prefs");
      }
      File localFile = ensurePrivateDirExists(this.mPreferencesDir);
      return localFile;
    }
  }
  
  private ArrayMap<File, SharedPreferencesImpl> getSharedPreferencesCacheLocked()
  {
    if (sSharedPrefsCache == null) {
      sSharedPrefsCache = new ArrayMap();
    }
    String str = getPackageName();
    ArrayMap localArrayMap2 = (ArrayMap)sSharedPrefsCache.get(str);
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap();
      sSharedPrefsCache.put(str, localArrayMap1);
    }
    return localArrayMap1;
  }
  
  private WallpaperManager getWallpaperManager()
  {
    return (WallpaperManager)getSystemService(WallpaperManager.class);
  }
  
  private void initializeTheme()
  {
    if (this.mTheme == null) {
      this.mTheme = this.mResources.newTheme();
    }
    this.mTheme.applyStyle(this.mThemeResource, true);
  }
  
  private File makeFilename(File paramFile, String paramString)
  {
    if (paramString.indexOf(File.separatorChar) < 0) {
      return new File(paramFile, paramString);
    }
    throw new IllegalArgumentException("File " + paramString + " contains a path separator");
  }
  
  private static int moveFiles(File paramFile1, File paramFile2, String paramString)
  {
    paramFile1 = FileUtils.listFilesOrEmpty(paramFile1, new FilenameFilter()
    {
      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
      {
        return paramAnonymousString.startsWith(this.val$prefix);
      }
    });
    int k = 0;
    int j = 0;
    int m = paramFile1.length;
    if (j < m)
    {
      paramString = paramFile1[j];
      File localFile = new File(paramFile2, paramString.getName());
      Log.d("ContextImpl", "Migrating " + paramString + " to " + localFile);
      int i;
      try
      {
        FileUtils.copyFileOrThrow(paramString, localFile);
        FileUtils.copyPermissions(paramString, localFile);
        if (!paramString.delete()) {
          throw new IOException("Failed to clean up " + paramString);
        }
      }
      catch (IOException localIOException)
      {
        Log.w("ContextImpl", "Failed to migrate " + paramString + ": " + localIOException);
        i = -1;
      }
      for (;;)
      {
        j += 1;
        k = i;
        break;
        i = k;
        if (k != -1) {
          i = k + 1;
        }
      }
    }
    return k;
  }
  
  private Intent registerReceiverInternal(BroadcastReceiver paramBroadcastReceiver, int paramInt, IntentFilter paramIntentFilter, String paramString, Handler paramHandler, Context paramContext)
  {
    Object localObject = null;
    if (paramBroadcastReceiver != null)
    {
      if ((this.mPackageInfo == null) || (paramContext == null)) {
        break label103;
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
    for (localObject = this.mPackageInfo.getReceiverDispatcher(paramBroadcastReceiver, paramContext, (Handler)localObject, this.mMainThread.getInstrumentation(), true);; localObject = new LoadedApk.ReceiverDispatcher(paramBroadcastReceiver, paramContext, (Handler)localObject, null, true).getIIntentReceiver())
    {
      try
      {
        paramBroadcastReceiver = ActivityManagerNative.getDefault().registerReceiver(this.mMainThread.getApplicationThread(), this.mBasePackageName, (IIntentReceiver)localObject, paramIntentFilter, paramString, paramInt);
        if (paramBroadcastReceiver != null)
        {
          paramBroadcastReceiver.setExtrasClassLoader(getClassLoader());
          paramBroadcastReceiver.prepareToEnterProcess();
        }
        return paramBroadcastReceiver;
      }
      catch (RemoteException paramBroadcastReceiver)
      {
        label103:
        throw paramBroadcastReceiver.rethrowFromSystemServer();
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
  }
  
  private int resolveUserId(Uri paramUri)
  {
    return ContentProvider.getUserIdFromUri(paramUri, getUserId());
  }
  
  static void setFilePermissionsFromMode(String paramString, int paramInt1, int paramInt2)
  {
    int i = paramInt2 | 0x1B0;
    paramInt2 = i;
    if ((paramInt1 & 0x1) != 0) {
      paramInt2 = i | 0x4;
    }
    i = paramInt2;
    if ((paramInt1 & 0x2) != 0) {
      i = paramInt2 | 0x2;
    }
    FileUtils.setPermissions(paramString, i, -1, -1);
  }
  
  private ComponentName startServiceCommon(Intent paramIntent, UserHandle paramUserHandle)
  {
    try
    {
      validateServiceIntent(paramIntent);
      paramIntent.prepareToLeaveProcess(this);
      paramUserHandle = ActivityManagerNative.getDefault().startService(this.mMainThread.getApplicationThread(), paramIntent, paramIntent.resolveTypeIfNeeded(getContentResolver()), getOpPackageName(), paramUserHandle.getIdentifier());
      if (paramUserHandle == null) {
        return paramUserHandle;
      }
      if (paramUserHandle.getPackageName().equals("!")) {
        throw new SecurityException("Not allowed to start service " + paramIntent + " without permission " + paramUserHandle.getClassName());
      }
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
    if (paramUserHandle.getPackageName().equals("!!")) {
      throw new SecurityException("Unable to start service " + paramIntent + ": " + paramUserHandle.getClassName());
    }
    return paramUserHandle;
  }
  
  private boolean stopServiceCommon(Intent paramIntent, UserHandle paramUserHandle)
  {
    boolean bool = false;
    int i;
    try
    {
      validateServiceIntent(paramIntent);
      paramIntent.prepareToLeaveProcess(this);
      i = ActivityManagerNative.getDefault().stopService(this.mMainThread.getApplicationThread(), paramIntent, paramIntent.resolveTypeIfNeeded(getContentResolver()), paramUserHandle.getIdentifier());
      if (i < 0) {
        throw new SecurityException("Not allowed to stop service " + paramIntent);
      }
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  private String uriModeFlagToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x1) != 0) {
      localStringBuilder.append("read and ");
    }
    if ((paramInt & 0x2) != 0) {
      localStringBuilder.append("write and ");
    }
    if ((paramInt & 0x40) != 0) {
      localStringBuilder.append("persistable and ");
    }
    if ((paramInt & 0x80) != 0) {
      localStringBuilder.append("prefix and ");
    }
    if (localStringBuilder.length() > 5)
    {
      localStringBuilder.setLength(localStringBuilder.length() - 5);
      return localStringBuilder.toString();
    }
    throw new IllegalArgumentException("Unknown permission mode flags: " + paramInt);
  }
  
  private void validateServiceIntent(Intent paramIntent)
  {
    if ((paramIntent.getComponent() == null) && (paramIntent.getPackage() == null))
    {
      if (getApplicationInfo().targetSdkVersion >= 21) {
        throw new IllegalArgumentException("Service Intent must be explicit: " + paramIntent);
      }
      Log.w("ContextImpl", "Implicit intents with startService are not safe: " + paramIntent + " " + Debug.getCallers(2, 3));
    }
  }
  
  private void warnIfCallingFromSystemProcess()
  {
    if (Process.myUid() == 1000) {
      Slog.w("ContextImpl", "Calling a method in the system process without a qualified user: " + Debug.getCallers(5));
    }
  }
  
  public boolean bindService(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    warnIfCallingFromSystemProcess();
    return bindServiceCommon(paramIntent, paramServiceConnection, paramInt, this.mMainThread.getHandler(), Process.myUserHandle());
  }
  
  public boolean bindServiceAsUser(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, Handler paramHandler, UserHandle paramUserHandle)
  {
    if (paramHandler == null) {
      throw new IllegalArgumentException("handler must not be null.");
    }
    return bindServiceCommon(paramIntent, paramServiceConnection, paramInt, paramHandler, paramUserHandle);
  }
  
  public boolean bindServiceAsUser(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, UserHandle paramUserHandle)
  {
    return bindServiceCommon(paramIntent, paramServiceConnection, paramInt, this.mMainThread.getHandler(), paramUserHandle);
  }
  
  public int checkCallingOrSelfPermission(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    return checkPermission(paramString, Binder.getCallingPid(), Binder.getCallingUid());
  }
  
  public int checkCallingOrSelfUriPermission(Uri paramUri, int paramInt)
  {
    return checkUriPermission(paramUri, Binder.getCallingPid(), Binder.getCallingUid(), paramInt);
  }
  
  public int checkCallingPermission(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    int i = Binder.getCallingPid();
    if (i != Process.myPid()) {
      return checkPermission(paramString, i, Binder.getCallingUid());
    }
    return -1;
  }
  
  public int checkCallingUriPermission(Uri paramUri, int paramInt)
  {
    int i = Binder.getCallingPid();
    if (i != Process.myPid()) {
      return checkUriPermission(paramUri, i, Binder.getCallingUid(), paramInt);
    }
    return -1;
  }
  
  public boolean checkNeedToShowPermissionRequst()
  {
    return this.mMainThread.checkNeedToShowPermissionRequst();
  }
  
  public int checkPermission(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().checkPermission(paramString, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int checkPermission(String paramString, int paramInt1, int paramInt2, IBinder paramIBinder)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().checkPermissionWithToken(paramString, paramInt1, paramInt2, paramIBinder);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int checkSelfPermission(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    return checkPermission(paramString, Process.myPid(), Process.myUid());
  }
  
  public int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().checkUriPermission(ContentProvider.getUriWithoutUserId(paramUri), paramInt1, paramInt2, paramInt3, resolveUserId(paramUri), null);
      return paramInt1;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  public int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, IBinder paramIBinder)
  {
    try
    {
      paramInt1 = ActivityManagerNative.getDefault().checkUriPermission(ContentProvider.getUriWithoutUserId(paramUri), paramInt1, paramInt2, paramInt3, resolveUserId(paramUri), paramIBinder);
      return paramInt1;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  public int checkUriPermission(Uri paramUri, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
  {
    if (((paramInt3 & 0x1) != 0) && ((paramString1 == null) || (checkPermission(paramString1, paramInt1, paramInt2) == 0))) {
      return 0;
    }
    if (((paramInt3 & 0x2) != 0) && ((paramString2 == null) || (checkPermission(paramString2, paramInt1, paramInt2) == 0))) {
      return 0;
    }
    if (paramUri != null) {
      return checkUriPermission(paramUri, paramInt1, paramInt2, paramInt3);
    }
    return -1;
  }
  
  @Deprecated
  public void clearWallpaper()
    throws IOException
  {
    getWallpaperManager().clear();
  }
  
  public Context createApplicationContext(ApplicationInfo paramApplicationInfo, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    Object localObject = this.mMainThread.getPackageInfo(paramApplicationInfo, this.mResources.getCompatibilityInfo(), 0x40000000 | paramInt);
    if (localObject != null)
    {
      localObject = new ContextImpl(this, this.mMainThread, (LoadedApk)localObject, this.mActivityToken, new UserHandle(UserHandle.getUserId(paramApplicationInfo.uid)), paramInt, this.mDisplay, null, -1);
      if (((ContextImpl)localObject).mResources != null) {
        return (Context)localObject;
      }
    }
    throw new PackageManager.NameNotFoundException("Application package " + paramApplicationInfo.packageName + " not found");
  }
  
  public Context createConfigurationContext(Configuration paramConfiguration)
  {
    if (paramConfiguration == null) {
      throw new IllegalArgumentException("overrideConfiguration must not be null");
    }
    return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, this.mFlags, this.mDisplay, paramConfiguration, -1);
  }
  
  public Context createCredentialProtectedStorageContext()
  {
    int i = this.mFlags;
    return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, i & 0xFFFFFFF7 | 0x10, this.mDisplay, null, -1);
  }
  
  public Context createDeviceProtectedStorageContext()
  {
    int i = this.mFlags;
    return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, i & 0xFFFFFFEF | 0x8, this.mDisplay, null, -1);
  }
  
  public Context createDisplayContext(Display paramDisplay)
  {
    if (paramDisplay == null) {
      throw new IllegalArgumentException("display must not be null");
    }
    return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, this.mFlags, paramDisplay, null, -1);
  }
  
  public Context createPackageContext(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    if (this.mUser != null) {}
    for (UserHandle localUserHandle = this.mUser;; localUserHandle = Process.myUserHandle()) {
      return createPackageContextAsUser(paramString, paramInt, localUserHandle);
    }
  }
  
  public Context createPackageContextAsUser(String paramString, int paramInt, UserHandle paramUserHandle)
    throws PackageManager.NameNotFoundException
  {
    if ((paramString.equals("system")) || (paramString.equals("android"))) {
      return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, paramUserHandle, paramInt, this.mDisplay, null, -1);
    }
    LoadedApk localLoadedApk = this.mMainThread.getPackageInfo(paramString, this.mResources.getCompatibilityInfo(), 0x40000000 | paramInt, paramUserHandle.getIdentifier());
    if (localLoadedApk != null)
    {
      paramUserHandle = new ContextImpl(this, this.mMainThread, localLoadedApk, this.mActivityToken, paramUserHandle, paramInt, this.mDisplay, null, -1);
      if (paramUserHandle.mResources != null) {
        return paramUserHandle;
      }
    }
    throw new PackageManager.NameNotFoundException("Application package " + paramString + " not found");
  }
  
  public String[] databaseList()
  {
    return FileUtils.listOrEmpty(getDatabasesDir());
  }
  
  public boolean deleteDatabase(String paramString)
  {
    try
    {
      boolean bool = SQLiteDatabase.deleteDatabase(getDatabasePath(paramString));
      return bool;
    }
    catch (Exception paramString) {}
    return false;
  }
  
  public boolean deleteFile(String paramString)
  {
    return makeFilename(getFilesDir(), paramString).delete();
  }
  
  /* Error */
  public boolean deleteSharedPreferences(String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: aload_1
    //   5: invokevirtual 836	android/app/ContextImpl:getSharedPreferencesPath	(Ljava/lang/String;)Ljava/io/File;
    //   8: astore_1
    //   9: aload_1
    //   10: invokestatic 841	android/app/SharedPreferencesImpl:makeBackupFile	(Ljava/io/File;)Ljava/io/File;
    //   13: astore_3
    //   14: aload_0
    //   15: invokespecial 843	android/app/ContextImpl:getSharedPreferencesCacheLocked	()Landroid/util/ArrayMap;
    //   18: aload_1
    //   19: invokevirtual 846	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   22: pop
    //   23: aload_1
    //   24: invokevirtual 572	java/io/File:delete	()Z
    //   27: pop
    //   28: aload_3
    //   29: invokevirtual 572	java/io/File:delete	()Z
    //   32: pop
    //   33: aload_1
    //   34: invokevirtual 400	java/io/File:exists	()Z
    //   37: ifne +12 -> 49
    //   40: aload_3
    //   41: invokevirtual 400	java/io/File:exists	()Z
    //   44: istore_2
    //   45: iload_2
    //   46: ifeq +10 -> 56
    //   49: iconst_0
    //   50: istore_2
    //   51: ldc 2
    //   53: monitorexit
    //   54: iload_2
    //   55: ireturn
    //   56: iconst_1
    //   57: istore_2
    //   58: goto -7 -> 51
    //   61: astore_1
    //   62: ldc 2
    //   64: monitorexit
    //   65: aload_1
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	ContextImpl
    //   0	67	1	paramString	String
    //   44	14	2	bool	boolean
    //   13	28	3	localFile	File
    // Exception table:
    //   from	to	target	type
    //   3	45	61	finally
  }
  
  public void enforceCallingOrSelfPermission(String paramString1, String paramString2)
  {
    enforce(paramString1, checkCallingOrSelfPermission(paramString1), true, Binder.getCallingUid(), paramString2);
  }
  
  public void enforceCallingOrSelfUriPermission(Uri paramUri, int paramInt, String paramString)
  {
    enforceForUri(paramInt, checkCallingOrSelfUriPermission(paramUri, paramInt), true, Binder.getCallingUid(), paramUri, paramString);
  }
  
  public void enforceCallingPermission(String paramString1, String paramString2)
  {
    enforce(paramString1, checkCallingPermission(paramString1), false, Binder.getCallingUid(), paramString2);
  }
  
  public void enforceCallingUriPermission(Uri paramUri, int paramInt, String paramString)
  {
    enforceForUri(paramInt, checkCallingUriPermission(paramUri, paramInt), false, Binder.getCallingUid(), paramUri, paramString);
  }
  
  public void enforcePermission(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    enforce(paramString1, checkPermission(paramString1, paramInt1, paramInt2), false, paramInt2, paramString2);
  }
  
  public void enforceUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    enforceForUri(paramInt3, checkUriPermission(paramUri, paramInt1, paramInt2, paramInt3), false, paramInt2, paramUri, paramString);
  }
  
  public void enforceUriPermission(Uri paramUri, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3)
  {
    enforceForUri(paramInt3, checkUriPermission(paramUri, paramString1, paramString2, paramInt1, paramInt2, paramInt3), false, paramInt2, paramUri, paramString3);
  }
  
  public String[] fileList()
  {
    return FileUtils.listOrEmpty(getFilesDir());
  }
  
  final IBinder getActivityToken()
  {
    return this.mActivityToken;
  }
  
  public Context getApplicationContext()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getApplication();
    }
    return this.mMainThread.getApplication();
  }
  
  public ApplicationInfo getApplicationInfo()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getApplicationInfo();
    }
    throw new RuntimeException("Not supported in system context");
  }
  
  public AssetManager getAssets()
  {
    return getResources().getAssets();
  }
  
  public String getBasePackageName()
  {
    if (this.mBasePackageName != null) {
      return this.mBasePackageName;
    }
    return getPackageName();
  }
  
  public File getCacheDir()
  {
    synchronized (this.mSync)
    {
      if (this.mCacheDir == null) {
        this.mCacheDir = new File(getDataDir(), "cache");
      }
      File localFile = ensurePrivateDirExists(this.mCacheDir);
      return localFile;
    }
  }
  
  public ClassLoader getClassLoader()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getClassLoader();
    }
    return ClassLoader.getSystemClassLoader();
  }
  
  public File getCodeCacheDir()
  {
    synchronized (this.mSync)
    {
      if (this.mCodeCacheDir == null) {
        this.mCodeCacheDir = new File(getDataDir(), "code_cache");
      }
      File localFile = ensurePrivateDirExists(this.mCodeCacheDir);
      return localFile;
    }
  }
  
  public ContentResolver getContentResolver()
  {
    return this.mContentResolver;
  }
  
  public File getDataDir()
  {
    if (this.mPackageInfo != null)
    {
      File localFile;
      if (isCredentialProtectedStorage()) {
        localFile = this.mPackageInfo.getCredentialProtectedDataDirFile();
      }
      while (localFile != null)
      {
        if ((!localFile.exists()) && (Process.myUid() == 1000)) {
          Log.wtf("ContextImpl", "Data directory doesn't exist for package " + getPackageName(), new Throwable());
        }
        return localFile;
        if (isDeviceProtectedStorage()) {
          localFile = this.mPackageInfo.getDeviceProtectedDataDirFile();
        } else {
          localFile = this.mPackageInfo.getDataDirFile();
        }
      }
      throw new RuntimeException("No data directory found for package " + getPackageName());
    }
    throw new RuntimeException("No package details found for package " + getPackageName());
  }
  
  public File getDatabasePath(String paramString)
  {
    if (paramString.charAt(0) == File.separatorChar)
    {
      File localFile = new File(paramString.substring(0, paramString.lastIndexOf(File.separatorChar)));
      paramString = new File(localFile, paramString.substring(paramString.lastIndexOf(File.separatorChar)));
      if ((!localFile.isDirectory()) && (localFile.mkdir())) {
        FileUtils.setPermissions(localFile.getPath(), 505, -1, -1);
      }
      return paramString;
    }
    return makeFilename(getDatabasesDir(), paramString);
  }
  
  public File getDir(String paramString, int paramInt)
  {
    checkMode(paramInt);
    paramString = "app_" + paramString;
    paramString = makeFilename(getDataDir(), paramString);
    if (!paramString.exists())
    {
      paramString.mkdir();
      setFilePermissionsFromMode(paramString.getPath(), paramInt, 505);
    }
    return paramString;
  }
  
  public Display getDisplay()
  {
    DisplayAdjustments localDisplayAdjustments = this.mResources.getDisplayAdjustments();
    if (this.mDisplay == null) {
      return this.mResourcesManager.getAdjustedDisplay(0, localDisplayAdjustments);
    }
    if (!this.mDisplay.getDisplayAdjustments().equals(localDisplayAdjustments)) {
      this.mDisplay = this.mResourcesManager.getAdjustedDisplay(this.mDisplay.getDisplayId(), localDisplayAdjustments);
    }
    return this.mDisplay;
  }
  
  public DisplayAdjustments getDisplayAdjustments(int paramInt)
  {
    return this.mResources.getDisplayAdjustments();
  }
  
  public File getExternalCacheDir()
  {
    return getExternalCacheDirs()[0];
  }
  
  public File[] getExternalCacheDirs()
  {
    synchronized (this.mSync)
    {
      File[] arrayOfFile = ensureExternalDirsExistOrFilter(Environment.buildExternalStorageAppCacheDirs(getPackageName()));
      return arrayOfFile;
    }
  }
  
  public File getExternalFilesDir(String paramString)
  {
    return getExternalFilesDirs(paramString)[0];
  }
  
  public File[] getExternalFilesDirs(String paramString)
  {
    synchronized (this.mSync)
    {
      File[] arrayOfFile2 = Environment.buildExternalStorageAppFilesDirs(getPackageName());
      File[] arrayOfFile1 = arrayOfFile2;
      if (paramString != null) {
        arrayOfFile1 = Environment.buildPaths(arrayOfFile2, new String[] { paramString });
      }
      paramString = ensureExternalDirsExistOrFilter(arrayOfFile1);
      return paramString;
    }
  }
  
  public File[] getExternalMediaDirs()
  {
    synchronized (this.mSync)
    {
      File[] arrayOfFile = ensureExternalDirsExistOrFilter(Environment.buildExternalStorageAppMediaDirs(getPackageName()));
      return arrayOfFile;
    }
  }
  
  public File getFileStreamPath(String paramString)
  {
    return makeFilename(getFilesDir(), paramString);
  }
  
  public File getFilesDir()
  {
    synchronized (this.mSync)
    {
      if (this.mFilesDir == null) {
        this.mFilesDir = new File(getDataDir(), "files");
      }
      File localFile = ensurePrivateDirExists(this.mFilesDir);
      return localFile;
    }
  }
  
  public Looper getMainLooper()
  {
    return this.mMainThread.getLooper();
  }
  
  public File getNoBackupFilesDir()
  {
    synchronized (this.mSync)
    {
      if (this.mNoBackupFilesDir == null) {
        this.mNoBackupFilesDir = new File(getDataDir(), "no_backup");
      }
      File localFile = ensurePrivateDirExists(this.mNoBackupFilesDir);
      return localFile;
    }
  }
  
  public File getObbDir()
  {
    return getObbDirs()[0];
  }
  
  public File[] getObbDirs()
  {
    synchronized (this.mSync)
    {
      File[] arrayOfFile = ensureExternalDirsExistOrFilter(Environment.buildExternalStorageAppObbDirs(getPackageName()));
      return arrayOfFile;
    }
  }
  
  public String getOpPackageName()
  {
    if (this.mOpPackageName != null) {
      return this.mOpPackageName;
    }
    return getBasePackageName();
  }
  
  final Context getOuterContext()
  {
    return this.mOuterContext;
  }
  
  public String getPackageCodePath()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getAppDir();
    }
    throw new RuntimeException("Not supported in system context");
  }
  
  public PackageManager getPackageManager()
  {
    if (this.mPackageManager != null) {
      return this.mPackageManager;
    }
    Object localObject = ActivityThread.getPackageManager();
    if (localObject != null)
    {
      localObject = new ApplicationPackageManager(this, (IPackageManager)localObject);
      this.mPackageManager = ((PackageManager)localObject);
      return (PackageManager)localObject;
    }
    return null;
  }
  
  public String getPackageName()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getPackageName();
    }
    return "android";
  }
  
  public String getPackageResourcePath()
  {
    if (this.mPackageInfo != null) {
      return this.mPackageInfo.getResDir();
    }
    throw new RuntimeException("Not supported in system context");
  }
  
  public Messenger getPermissionService(int paramInt)
  {
    return this.mMainThread.getPermissionService(paramInt);
  }
  
  final Context getReceiverRestrictedContext()
  {
    if (this.mReceiverRestrictedContext != null) {
      return this.mReceiverRestrictedContext;
    }
    ReceiverRestrictedContext localReceiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
    this.mReceiverRestrictedContext = localReceiverRestrictedContext;
    return localReceiverRestrictedContext;
  }
  
  public Resources getResources()
  {
    return this.mResources;
  }
  
  public SharedPreferences getSharedPreferences(File paramFile, int paramInt)
  {
    checkMode(paramInt);
    try
    {
      ArrayMap localArrayMap = getSharedPreferencesCacheLocked();
      SharedPreferencesImpl localSharedPreferencesImpl = (SharedPreferencesImpl)localArrayMap.get(paramFile);
      if (localSharedPreferencesImpl == null)
      {
        localSharedPreferencesImpl = new SharedPreferencesImpl(paramFile, paramInt);
        localArrayMap.put(paramFile, localSharedPreferencesImpl);
        return localSharedPreferencesImpl;
      }
      if (((paramInt & 0x4) != 0) || (getApplicationInfo().targetSdkVersion < 11)) {
        localSharedPreferencesImpl.startReloadIfChangedUnexpectedly();
      }
      return localSharedPreferencesImpl;
    }
    finally {}
  }
  
  public SharedPreferences getSharedPreferences(String paramString, int paramInt)
  {
    String str = paramString;
    if (this.mPackageInfo.getApplicationInfo().targetSdkVersion < 19)
    {
      str = paramString;
      if (paramString == null) {
        str = "null";
      }
    }
    try
    {
      if (this.mSharedPrefsPaths == null) {
        this.mSharedPrefsPaths = new ArrayMap();
      }
      File localFile = (File)this.mSharedPrefsPaths.get(str);
      paramString = localFile;
      if (localFile == null)
      {
        paramString = getSharedPreferencesPath(str);
        this.mSharedPrefsPaths.put(str, paramString);
      }
      return getSharedPreferences(paramString, paramInt);
    }
    finally {}
  }
  
  public File getSharedPreferencesPath(String paramString)
  {
    return makeFilename(getPreferencesDir(), paramString + ".xml");
  }
  
  public Object getSystemService(String paramString)
  {
    return SystemServiceRegistry.getSystemService(this, paramString);
  }
  
  public String getSystemServiceName(Class<?> paramClass)
  {
    return SystemServiceRegistry.getSystemServiceName(paramClass);
  }
  
  public Resources.Theme getTheme()
  {
    if (this.mTheme != null) {
      return this.mTheme;
    }
    this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getOuterContext().getApplicationInfo().targetSdkVersion);
    initializeTheme();
    return this.mTheme;
  }
  
  public int getThemeResId()
  {
    return this.mThemeResource;
  }
  
  public int getUserId()
  {
    return this.mUser.getIdentifier();
  }
  
  @Deprecated
  public Drawable getWallpaper()
  {
    return getWallpaperManager().getDrawable();
  }
  
  @Deprecated
  public int getWallpaperDesiredMinimumHeight()
  {
    return getWallpaperManager().getDesiredMinimumHeight();
  }
  
  @Deprecated
  public int getWallpaperDesiredMinimumWidth()
  {
    return getWallpaperManager().getDesiredMinimumWidth();
  }
  
  public void grantUriPermission(String paramString, Uri paramUri, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().grantUriPermission(this.mMainThread.getApplicationThread(), paramString, ContentProvider.getUriWithoutUserId(paramUri), paramInt, resolveUserId(paramUri));
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  void installSystemApplicationInfo(ApplicationInfo paramApplicationInfo, ClassLoader paramClassLoader)
  {
    this.mPackageInfo.installSystemApplicationInfo(paramApplicationInfo, paramClassLoader);
  }
  
  public boolean isCredentialProtectedStorage()
  {
    boolean bool = false;
    if ((this.mFlags & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDeviceProtectedStorage()
  {
    boolean bool = false;
    if ((this.mFlags & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isRestricted()
  {
    boolean bool = false;
    if ((this.mFlags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  /* Error */
  public boolean moveDatabaseFrom(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_1
    //   4: aload_2
    //   5: invokevirtual 1094	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   8: astore_1
    //   9: aload_0
    //   10: aload_2
    //   11: invokevirtual 821	android/app/ContextImpl:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   14: astore_2
    //   15: aload_1
    //   16: invokevirtual 1097	java/io/File:getParentFile	()Ljava/io/File;
    //   19: aload_2
    //   20: invokevirtual 1097	java/io/File:getParentFile	()Ljava/io/File;
    //   23: aload_1
    //   24: invokevirtual 555	java/io/File:getName	()Ljava/lang/String;
    //   27: invokestatic 1099	android/app/ContextImpl:moveFiles	(Ljava/io/File;Ljava/io/File;Ljava/lang/String;)I
    //   30: istore_3
    //   31: iload_3
    //   32: iconst_m1
    //   33: if_icmpeq +12 -> 45
    //   36: iconst_1
    //   37: istore 4
    //   39: ldc 2
    //   41: monitorexit
    //   42: iload 4
    //   44: ireturn
    //   45: iconst_0
    //   46: istore 4
    //   48: goto -9 -> 39
    //   51: astore_1
    //   52: ldc 2
    //   54: monitorexit
    //   55: aload_1
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	ContextImpl
    //   0	57	1	paramContext	Context
    //   0	57	2	paramString	String
    //   30	4	3	i	int
    //   37	10	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   3	31	51	finally
  }
  
  public boolean moveSharedPreferencesFrom(Context paramContext, String paramString)
  {
    boolean bool = false;
    try
    {
      paramContext = paramContext.getSharedPreferencesPath(paramString);
      paramString = getSharedPreferencesPath(paramString);
      int i = moveFiles(paramContext.getParentFile(), paramString.getParentFile(), paramContext.getName());
      if (i > 0)
      {
        ArrayMap localArrayMap = getSharedPreferencesCacheLocked();
        localArrayMap.remove(paramContext);
        localArrayMap.remove(paramString);
      }
      if (i != -1) {
        bool = true;
      }
      return bool;
    }
    finally {}
  }
  
  public FileInputStream openFileInput(String paramString)
    throws FileNotFoundException
  {
    return new FileInputStream(makeFilename(getFilesDir(), paramString));
  }
  
  public FileOutputStream openFileOutput(String paramString, int paramInt)
    throws FileNotFoundException
  {
    checkMode(paramInt);
    if ((0x8000 & paramInt) != 0) {}
    for (bool = true;; bool = false)
    {
      paramString = makeFilename(getFilesDir(), paramString);
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(paramString, bool);
        setFilePermissionsFromMode(paramString.getPath(), paramInt, 0);
        return localFileOutputStream;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Object localObject = paramString.getParentFile();
        ((File)localObject).mkdir();
        FileUtils.setPermissions(((File)localObject).getPath(), 505, -1, -1);
        localObject = new FileOutputStream(paramString, bool);
        setFilePermissionsFromMode(paramString.getPath(), paramInt, 0);
        return (FileOutputStream)localObject;
      }
    }
  }
  
  public SQLiteDatabase openOrCreateDatabase(String paramString, int paramInt, SQLiteDatabase.CursorFactory paramCursorFactory)
  {
    return openOrCreateDatabase(paramString, paramInt, paramCursorFactory, null);
  }
  
  public SQLiteDatabase openOrCreateDatabase(String paramString, int paramInt, SQLiteDatabase.CursorFactory paramCursorFactory, DatabaseErrorHandler paramDatabaseErrorHandler)
  {
    checkMode(paramInt);
    paramString = getDatabasePath(paramString);
    int i = 268435456;
    if ((paramInt & 0x8) != 0) {
      i = 805306368;
    }
    int j = i;
    if ((paramInt & 0x10) != 0) {
      j = i | 0x10;
    }
    paramCursorFactory = SQLiteDatabase.openDatabase(paramString.getPath(), paramCursorFactory, j, paramDatabaseErrorHandler);
    setFilePermissionsFromMode(paramString.getPath(), paramInt, 0);
    return paramCursorFactory;
  }
  
  @Deprecated
  public Drawable peekWallpaper()
  {
    return getWallpaperManager().peekDrawable();
  }
  
  final void performFinalCleanup(String paramString1, String paramString2)
  {
    this.mPackageInfo.removeContextRegistrations(getOuterContext(), paramString1, paramString2);
  }
  
  public Intent registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter)
  {
    return registerReceiver(paramBroadcastReceiver, paramIntentFilter, null, null);
  }
  
  public Intent registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter, String paramString, Handler paramHandler)
  {
    return registerReceiverInternal(paramBroadcastReceiver, getUserId(), paramIntentFilter, paramString, paramHandler, getOuterContext());
  }
  
  public Intent registerReceiverAsUser(BroadcastReceiver paramBroadcastReceiver, UserHandle paramUserHandle, IntentFilter paramIntentFilter, String paramString, Handler paramHandler)
  {
    return registerReceiverInternal(paramBroadcastReceiver, paramUserHandle.getIdentifier(), paramIntentFilter, paramString, paramHandler, getOuterContext());
  }
  
  @Deprecated
  public void removeStickyBroadcast(Intent paramIntent)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    Intent localIntent = paramIntent;
    if (str != null)
    {
      localIntent = new Intent(paramIntent);
      localIntent.setDataAndType(localIntent.getData(), str);
    }
    try
    {
      localIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), localIntent, getUserId());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void removeStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    Intent localIntent = paramIntent;
    if (str != null)
    {
      localIntent = new Intent(paramIntent);
      localIntent.setDataAndType(localIntent.getData(), str);
    }
    try
    {
      localIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), localIntent, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void revokeUriPermission(Uri paramUri, int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().revokeUriPermission(this.mMainThread.getApplicationThread(), ContentProvider.getUriWithoutUserId(paramUri), paramInt, resolveUserId(paramUri));
      return;
    }
    catch (RemoteException paramUri)
    {
      throw paramUri.rethrowFromSystemServer();
    }
  }
  
  final void scheduleFinalCleanup(String paramString1, String paramString2)
  {
    this.mMainThread.scheduleContextCleanup(this, paramString1, paramString2);
  }
  
  public void sendBroadcast(Intent paramIntent)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, null, -1, null, false, false, getUserId());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void sendBroadcast(Intent paramIntent, String paramString)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    if (paramString == null) {}
    for (paramString = null;; paramString = arrayOfString)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramString, -1, null, false, false, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        String[] arrayOfString;
        throw paramIntent.rethrowFromSystemServer();
      }
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void sendBroadcast(Intent paramIntent, String paramString, int paramInt)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    if (paramString == null) {}
    for (paramString = null;; paramString = arrayOfString)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramString, paramInt, null, false, false, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        String[] arrayOfString;
        throw paramIntent.rethrowFromSystemServer();
      }
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void sendBroadcast(Intent paramIntent, String paramString, Bundle paramBundle)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    if (paramString == null) {}
    for (paramString = null;; paramString = arrayOfString)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramString, -1, paramBundle, false, false, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        String[] arrayOfString;
        throw paramIntent.rethrowFromSystemServer();
      }
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, null, -1, null, false, false, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString)
  {
    sendBroadcastAsUser(paramIntent, paramUserHandle, paramString, -1);
  }
  
  public void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString, int paramInt)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    if (paramString == null) {}
    for (paramString = null;; paramString = arrayOfString)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramString, paramInt, null, false, false, paramUserHandle.getIdentifier());
        return;
      }
      catch (RemoteException paramIntent)
      {
        String[] arrayOfString;
        throw paramIntent.rethrowFromSystemServer();
      }
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void sendBroadcastMultiplePermissions(Intent paramIntent, String[] paramArrayOfString)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramArrayOfString, -1, null, false, false, getUserId());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void sendOrderedBroadcast(Intent paramIntent, String paramString)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    if (paramString == null) {}
    for (paramString = null;; paramString = arrayOfString)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, paramString, -1, null, true, false, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        String[] arrayOfString;
        throw paramIntent.rethrowFromSystemServer();
      }
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void sendOrderedBroadcast(Intent paramIntent, String paramString1, int paramInt1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle)
  {
    sendOrderedBroadcast(paramIntent, paramString1, paramInt1, paramBroadcastReceiver, paramHandler, paramInt2, paramString2, paramBundle, null);
  }
  
  void sendOrderedBroadcast(Intent paramIntent, String paramString1, int paramInt1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle1, Bundle paramBundle2)
  {
    warnIfCallingFromSystemProcess();
    Object localObject = null;
    if (paramBroadcastReceiver != null)
    {
      if (this.mPackageInfo != null)
      {
        localObject = paramHandler;
        if (paramHandler == null) {
          localObject = this.mMainThread.getHandler();
        }
        localObject = this.mPackageInfo.getReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, this.mMainThread.getInstrumentation(), false);
      }
    }
    else
    {
      paramHandler = paramIntent.resolveTypeIfNeeded(getContentResolver());
      if (paramString1 != null) {
        break label164;
      }
    }
    for (paramString1 = null;; paramString1 = paramBroadcastReceiver)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, paramHandler, (IIntentReceiver)localObject, paramInt2, paramString2, paramBundle1, paramString1, paramInt1, paramBundle2, true, false, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        label164:
        throw paramIntent.rethrowFromSystemServer();
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
      localObject = new LoadedApk.ReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, null, false).getIIntentReceiver();
      break;
      paramBroadcastReceiver = new String[1];
      paramBroadcastReceiver[0] = paramString1;
    }
  }
  
  public void sendOrderedBroadcast(Intent paramIntent, String paramString1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle)
  {
    sendOrderedBroadcast(paramIntent, paramString1, -1, paramBroadcastReceiver, paramHandler, paramInt, paramString2, paramBundle, null);
  }
  
  public void sendOrderedBroadcast(Intent paramIntent, String paramString1, Bundle paramBundle1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle2)
  {
    sendOrderedBroadcast(paramIntent, paramString1, -1, paramBroadcastReceiver, paramHandler, paramInt, paramString2, paramBundle2, paramBundle1);
  }
  
  public void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, int paramInt1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle)
  {
    sendOrderedBroadcastAsUser(paramIntent, paramUserHandle, paramString1, paramInt1, null, paramBroadcastReceiver, paramHandler, paramInt2, paramString2, paramBundle);
  }
  
  public void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, int paramInt1, Bundle paramBundle1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle2)
  {
    Object localObject = null;
    if (paramBroadcastReceiver != null)
    {
      if (this.mPackageInfo != null)
      {
        localObject = paramHandler;
        if (paramHandler == null) {
          localObject = this.mMainThread.getHandler();
        }
        localObject = this.mPackageInfo.getReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, this.mMainThread.getInstrumentation(), false);
      }
    }
    else
    {
      paramHandler = paramIntent.resolveTypeIfNeeded(getContentResolver());
      if (paramString1 != null) {
        break label161;
      }
    }
    for (paramString1 = null;; paramString1 = paramBroadcastReceiver)
    {
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, paramHandler, (IIntentReceiver)localObject, paramInt2, paramString2, paramBundle2, paramString1, paramInt1, paramBundle1, true, false, paramUserHandle.getIdentifier());
        return;
      }
      catch (RemoteException paramIntent)
      {
        label161:
        throw paramIntent.rethrowFromSystemServer();
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
      localObject = new LoadedApk.ReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, null, false).getIIntentReceiver();
      break;
      paramBroadcastReceiver = new String[1];
      paramBroadcastReceiver[0] = paramString1;
    }
  }
  
  public void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle)
  {
    sendOrderedBroadcastAsUser(paramIntent, paramUserHandle, paramString1, -1, null, paramBroadcastReceiver, paramHandler, paramInt, paramString2, paramBundle);
  }
  
  @Deprecated
  public void sendStickyBroadcast(Intent paramIntent)
  {
    warnIfCallingFromSystemProcess();
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, null, -1, null, false, true, getUserId());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void sendStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, null, -1, null, false, true, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void sendStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, Bundle paramBundle)
  {
    String str = paramIntent.resolveTypeIfNeeded(getContentResolver());
    try
    {
      paramIntent.prepareToLeaveProcess(this);
      ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, str, null, -1, null, null, null, -1, paramBundle, false, true, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void sendStickyOrderedBroadcast(Intent paramIntent, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString, Bundle paramBundle)
  {
    warnIfCallingFromSystemProcess();
    Object localObject = null;
    if (paramBroadcastReceiver != null)
    {
      if (this.mPackageInfo == null) {
        break label108;
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
    for (localObject = this.mPackageInfo.getReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, this.mMainThread.getInstrumentation(), false);; localObject = new LoadedApk.ReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, null, false).getIIntentReceiver())
    {
      paramBroadcastReceiver = paramIntent.resolveTypeIfNeeded(getContentResolver());
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, paramBroadcastReceiver, (IIntentReceiver)localObject, paramInt, paramString, paramBundle, null, -1, null, true, true, getUserId());
        return;
      }
      catch (RemoteException paramIntent)
      {
        label108:
        throw paramIntent.rethrowFromSystemServer();
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
  }
  
  @Deprecated
  public void sendStickyOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString, Bundle paramBundle)
  {
    Object localObject = null;
    if (paramBroadcastReceiver != null)
    {
      if (this.mPackageInfo == null) {
        break label106;
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
    for (localObject = this.mPackageInfo.getReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, this.mMainThread.getInstrumentation(), false);; localObject = new LoadedApk.ReceiverDispatcher(paramBroadcastReceiver, getOuterContext(), (Handler)localObject, null, false).getIIntentReceiver())
    {
      paramBroadcastReceiver = paramIntent.resolveTypeIfNeeded(getContentResolver());
      try
      {
        paramIntent.prepareToLeaveProcess(this);
        ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), paramIntent, paramBroadcastReceiver, (IIntentReceiver)localObject, paramInt, paramString, paramBundle, null, -1, null, true, true, paramUserHandle.getIdentifier());
        return;
      }
      catch (RemoteException paramIntent)
      {
        label106:
        throw paramIntent.rethrowFromSystemServer();
      }
      localObject = paramHandler;
      if (paramHandler == null) {
        localObject = this.mMainThread.getHandler();
      }
    }
  }
  
  final void setOuterContext(Context paramContext)
  {
    this.mOuterContext = paramContext;
  }
  
  public void setTheme(int paramInt)
  {
    if (this.mThemeResource != paramInt)
    {
      this.mThemeResource = paramInt;
      initializeTheme();
    }
  }
  
  @Deprecated
  public void setWallpaper(Bitmap paramBitmap)
    throws IOException
  {
    getWallpaperManager().setBitmap(paramBitmap);
  }
  
  @Deprecated
  public void setWallpaper(InputStream paramInputStream)
    throws IOException
  {
    getWallpaperManager().setStream(paramInputStream);
  }
  
  public void startActivities(Intent[] paramArrayOfIntent)
  {
    warnIfCallingFromSystemProcess();
    startActivities(paramArrayOfIntent, null);
  }
  
  public void startActivities(Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    warnIfCallingFromSystemProcess();
    if ((paramArrayOfIntent[0].getFlags() & 0x10000000) == 0) {
      throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
    }
    this.mMainThread.getInstrumentation().execStartActivities(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity)null, paramArrayOfIntent, paramBundle);
  }
  
  public void startActivitiesAsUser(Intent[] paramArrayOfIntent, Bundle paramBundle, UserHandle paramUserHandle)
  {
    if ((paramArrayOfIntent[0].getFlags() & 0x10000000) == 0) {
      throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
    }
    this.mMainThread.getInstrumentation().execStartActivitiesAsUser(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity)null, paramArrayOfIntent, paramBundle, paramUserHandle.getIdentifier());
  }
  
  public void startActivity(Intent paramIntent)
  {
    warnIfCallingFromSystemProcess();
    startActivity(paramIntent, null);
  }
  
  public void startActivity(Intent paramIntent, Bundle paramBundle)
  {
    warnIfCallingFromSystemProcess();
    if (((paramIntent.getFlags() & 0x10000000) == 0) && (paramBundle != null) && (ActivityOptions.fromBundle(paramBundle).getLaunchTaskId() == -1)) {
      throw new AndroidRuntimeException("Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?");
    }
    this.mMainThread.getInstrumentation().execStartActivity(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity)null, paramIntent, -1, paramBundle);
  }
  
  public void startActivityAsUser(Intent paramIntent, Bundle paramBundle, UserHandle paramUserHandle)
  {
    try
    {
      ActivityManagerNative.getDefault().startActivityAsUser(this.mMainThread.getApplicationThread(), getBasePackageName(), paramIntent, paramIntent.resolveTypeIfNeeded(getContentResolver()), null, null, 0, 268435456, null, paramBundle, paramUserHandle.getIdentifier());
      return;
    }
    catch (RemoteException paramIntent)
    {
      throw paramIntent.rethrowFromSystemServer();
    }
  }
  
  public void startActivityAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    startActivityAsUser(paramIntent, null, paramUserHandle);
  }
  
  public boolean startInstrumentation(ComponentName paramComponentName, String paramString, Bundle paramBundle)
  {
    if (paramBundle != null) {}
    try
    {
      paramBundle.setAllowFds(false);
      boolean bool = ActivityManagerNative.getDefault().startInstrumentation(paramComponentName, paramString, 0, paramBundle, null, null, getUserId(), null);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3)
    throws IntentSender.SendIntentException
  {
    startIntentSender(paramIntentSender, paramIntent, paramInt1, paramInt2, paramInt3, null);
  }
  
  public void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    String str = null;
    if (paramIntent != null) {}
    try
    {
      paramIntent.migrateExtraStreamToClipData();
      paramIntent.prepareToLeaveProcess(this);
      str = paramIntent.resolveTypeIfNeeded(getContentResolver());
      paramInt1 = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), paramIntentSender, paramIntent, str, null, null, 0, paramInt1, paramInt2, paramBundle);
      if (paramInt1 == -6) {
        throw new IntentSender.SendIntentException();
      }
    }
    catch (RemoteException paramIntentSender)
    {
      throw paramIntentSender.rethrowFromSystemServer();
    }
    Instrumentation.checkStartActivityResult(paramInt1, null);
  }
  
  public ComponentName startService(Intent paramIntent)
  {
    warnIfCallingFromSystemProcess();
    return startServiceCommon(paramIntent, this.mUser);
  }
  
  public ComponentName startServiceAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    return startServiceCommon(paramIntent, paramUserHandle);
  }
  
  public boolean stopService(Intent paramIntent)
  {
    warnIfCallingFromSystemProcess();
    return stopServiceCommon(paramIntent, this.mUser);
  }
  
  public boolean stopServiceAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    return stopServiceCommon(paramIntent, paramUserHandle);
  }
  
  public void unbindService(ServiceConnection paramServiceConnection)
  {
    if (paramServiceConnection == null) {
      throw new IllegalArgumentException("connection is null");
    }
    if (this.mPackageInfo != null)
    {
      paramServiceConnection = this.mPackageInfo.forgetServiceDispatcher(getOuterContext(), paramServiceConnection);
      try
      {
        ActivityManagerNative.getDefault().unbindService(paramServiceConnection);
        return;
      }
      catch (RemoteException paramServiceConnection)
      {
        throw paramServiceConnection.rethrowFromSystemServer();
      }
    }
    throw new RuntimeException("Not supported in system context");
  }
  
  public void unregisterReceiver(BroadcastReceiver paramBroadcastReceiver)
  {
    if (this.mPackageInfo != null)
    {
      paramBroadcastReceiver = this.mPackageInfo.forgetReceiverDispatcher(getOuterContext(), paramBroadcastReceiver);
      try
      {
        ActivityManagerNative.getDefault().unregisterReceiver(paramBroadcastReceiver);
        return;
      }
      catch (RemoteException paramBroadcastReceiver)
      {
        throw paramBroadcastReceiver.rethrowFromSystemServer();
      }
    }
    throw new RuntimeException("Not supported in system context");
  }
  
  private static final class ApplicationContentResolver
    extends ContentResolver
  {
    private final ActivityThread mMainThread;
    private final UserHandle mUser;
    
    public ApplicationContentResolver(Context paramContext, ActivityThread paramActivityThread, UserHandle paramUserHandle)
    {
      super();
      this.mMainThread = ((ActivityThread)Preconditions.checkNotNull(paramActivityThread));
      this.mUser = ((UserHandle)Preconditions.checkNotNull(paramUserHandle));
    }
    
    protected IContentProvider acquireExistingProvider(Context paramContext, String paramString)
    {
      return this.mMainThread.acquireExistingProvider(paramContext, ContentProvider.getAuthorityWithoutUserId(paramString), resolveUserIdFromAuthority(paramString), true);
    }
    
    protected IContentProvider acquireProvider(Context paramContext, String paramString)
    {
      return this.mMainThread.acquireProvider(paramContext, ContentProvider.getAuthorityWithoutUserId(paramString), resolveUserIdFromAuthority(paramString), true);
    }
    
    protected IContentProvider acquireUnstableProvider(Context paramContext, String paramString)
    {
      return this.mMainThread.acquireProvider(paramContext, ContentProvider.getAuthorityWithoutUserId(paramString), resolveUserIdFromAuthority(paramString), false);
    }
    
    public void appNotRespondingViaProvider(IContentProvider paramIContentProvider)
    {
      this.mMainThread.appNotRespondingViaProvider(paramIContentProvider.asBinder());
    }
    
    public boolean releaseProvider(IContentProvider paramIContentProvider)
    {
      return this.mMainThread.releaseProvider(paramIContentProvider, true);
    }
    
    public boolean releaseUnstableProvider(IContentProvider paramIContentProvider)
    {
      return this.mMainThread.releaseProvider(paramIContentProvider, false);
    }
    
    protected int resolveUserIdFromAuthority(String paramString)
    {
      return ContentProvider.getUserIdFromAuthority(paramString, this.mUser.getIdentifier());
    }
    
    public void unstableProviderDied(IContentProvider paramIContentProvider)
    {
      this.mMainThread.handleUnstableProviderDied(paramIContentProvider.asBinder(), true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ContextImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */