package com.android.server.retaildemo;

import android.app.AppGlobals;
import android.app.PackageInstallObserver;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

class PreloadAppsInstaller
{
  private static boolean DEBUG = Log.isLoggable(TAG, 3);
  private static final String PRELOAD_APK_EXT = ".apk.preload";
  private static final String SYSTEM_SERVER_PACKAGE_NAME = "android";
  private static String TAG = PreloadAppsInstaller.class.getSimpleName();
  private final Map<String, String> mApkToPackageMap;
  private final Context mContext;
  private final IPackageManager mPackageManager;
  private final File preloadsAppsDirectory;
  
  PreloadAppsInstaller(Context paramContext)
  {
    this(paramContext, AppGlobals.getPackageManager(), Environment.getDataPreloadsAppsDirectory());
  }
  
  PreloadAppsInstaller(Context paramContext, IPackageManager paramIPackageManager, File paramFile)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramIPackageManager;
    this.mApkToPackageMap = Collections.synchronizedMap(new ArrayMap());
    this.preloadsAppsDirectory = paramFile;
  }
  
  private void installExistingPackage(String paramString, int paramInt, AppInstallCounter paramAppInstallCounter)
  {
    if (DEBUG) {
      Log.d(TAG, "installExistingPackage " + paramString + " u" + paramInt);
    }
    try
    {
      this.mPackageManager.installExistingPackageAsUser(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    finally
    {
      paramAppInstallCounter.appInstallFinished();
    }
  }
  
  private void installPackage(File paramFile, final int paramInt, final AppInstallCounter paramAppInstallCounter)
    throws IOException, RemoteException
  {
    final String str = paramFile.getName();
    if (DEBUG) {
      Log.d(TAG, "installPackage " + str + " u" + paramInt);
    }
    this.mPackageManager.installPackageAsUser(paramFile.getPath(), new PackageInstallObserver()
    {
      public void onPackageInstalled(String paramAnonymousString1, int paramAnonymousInt, String paramAnonymousString2, Bundle paramAnonymousBundle)
      {
        if (PreloadAppsInstaller.-get0()) {
          Log.d(PreloadAppsInstaller.-get1(), "Package " + paramAnonymousString1 + " installed u" + paramInt + " returnCode: " + paramAnonymousInt + " msg: " + paramAnonymousString2);
        }
        if (paramAnonymousInt == 1)
        {
          PreloadAppsInstaller.-get2(PreloadAppsInstaller.this).put(str, paramAnonymousString1);
          PreloadAppsInstaller.-wrap0(PreloadAppsInstaller.this, paramAnonymousString1, 0, paramAppInstallCounter);
          return;
        }
        if (paramAnonymousInt == -1)
        {
          if (!PreloadAppsInstaller.-get2(PreloadAppsInstaller.this).containsKey(str)) {
            PreloadAppsInstaller.-get2(PreloadAppsInstaller.this).put(str, paramAnonymousString1);
          }
          PreloadAppsInstaller.-wrap0(PreloadAppsInstaller.this, paramAnonymousString1, paramInt, paramAppInstallCounter);
          return;
        }
        Log.e(PreloadAppsInstaller.-get1(), "Package " + paramAnonymousString1 + " cannot be installed from " + str + ": " + paramAnonymousString2 + " (returnCode " + paramAnonymousInt + ")");
        paramAppInstallCounter.appInstallFinished();
      }
    }.getBinder(), 0, "android", paramInt);
  }
  
  void installApps(int paramInt)
  {
    int j = 0;
    File[] arrayOfFile = this.preloadsAppsDirectory.listFiles();
    AppInstallCounter localAppInstallCounter = new AppInstallCounter(this.mContext, paramInt);
    if (ArrayUtils.isEmpty(arrayOfFile))
    {
      localAppInstallCounter.setExpectedAppsCount(0);
      return;
    }
    int k = 0;
    int m = arrayOfFile.length;
    if (j < m)
    {
      File localFile = arrayOfFile[j];
      String str = localFile.getName();
      int i = k;
      if (str.endsWith(".apk.preload"))
      {
        i = k;
        if (localFile.isFile())
        {
          str = (String)this.mApkToPackageMap.get(str);
          if (str == null) {
            break label170;
          }
          i = k + 1;
        }
      }
      for (;;)
      {
        try
        {
          installExistingPackage(str, paramInt, localAppInstallCounter);
          j += 1;
          k = i;
        }
        catch (Exception localException1)
        {
          Slog.e(TAG, "Failed to install existing package " + str, localException1);
          continue;
        }
        try
        {
          label170:
          installPackage(localException1, paramInt, localAppInstallCounter);
          i = k + 1;
        }
        catch (Exception localException2)
        {
          Slog.e(TAG, "Failed to install package from " + localException1, localException2);
          i = k;
        }
      }
    }
    localAppInstallCounter.setExpectedAppsCount(k);
  }
  
  private static class AppInstallCounter
  {
    private int expectedCount = -1;
    private int finishedCount;
    private final Context mContext;
    private final int userId;
    
    AppInstallCounter(Context paramContext, int paramInt)
    {
      this.mContext = paramContext;
      this.userId = paramInt;
    }
    
    private void checkIfAllFinished()
    {
      if (this.expectedCount == this.finishedCount)
      {
        Log.i(PreloadAppsInstaller.-get1(), "All preloads finished installing for user " + this.userId);
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "demo_user_setup_complete", "1", this.userId);
      }
    }
    
    void appInstallFinished()
    {
      try
      {
        this.finishedCount += 1;
        checkIfAllFinished();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setExpectedAppsCount(int paramInt)
    {
      try
      {
        this.expectedCount = paramInt;
        checkIfAllFinished();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/retaildemo/PreloadAppsInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */