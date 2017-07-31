package com.android.server.tv;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class TvRemoteProviderWatcher
{
  private static final boolean DEBUG = Log.isLoggable("TvRemoteProvWatcher", 2);
  private static final String TAG = "TvRemoteProvWatcher";
  private final Context mContext;
  private final Handler mHandler;
  private final PackageManager mPackageManager;
  private final ProviderMethods mProvider;
  private final ArrayList<TvRemoteProviderProxy> mProviderProxies = new ArrayList();
  private boolean mRunning;
  private final BroadcastReceiver mScanPackagesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (TvRemoteProviderWatcher.-get0()) {
        Slog.d("TvRemoteProvWatcher", "Received package manager broadcast: " + paramAnonymousIntent);
      }
      TvRemoteProviderWatcher.-get1(TvRemoteProviderWatcher.this).post(TvRemoteProviderWatcher.-get2(TvRemoteProviderWatcher.this));
    }
  };
  private final Runnable mScanPackagesRunnable = new Runnable()
  {
    public void run()
    {
      TvRemoteProviderWatcher.-wrap0(TvRemoteProviderWatcher.this);
    }
  };
  private final String mUnbundledServicePackage;
  private final int mUserId;
  
  public TvRemoteProviderWatcher(Context paramContext, ProviderMethods paramProviderMethods, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mProvider = paramProviderMethods;
    this.mHandler = paramHandler;
    this.mUserId = UserHandle.myUserId();
    this.mPackageManager = paramContext.getPackageManager();
    this.mUnbundledServicePackage = paramContext.getString(17039476);
  }
  
  private int findProvider(String paramString1, String paramString2)
  {
    int j = this.mProviderProxies.size();
    int i = 0;
    while (i < j)
    {
      if (((TvRemoteProviderProxy)this.mProviderProxies.get(i)).hasComponentName(paramString1, paramString2)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private boolean hasNecessaryPermissions(String paramString)
  {
    return this.mPackageManager.checkPermission("android.permission.TV_VIRTUAL_REMOTE_CONTROLLER", paramString) == 0;
  }
  
  private void scanPackages()
  {
    if (!this.mRunning) {
      return;
    }
    if (DEBUG) {
      Log.d("TvRemoteProvWatcher", "scanPackages()");
    }
    int i = 0;
    Object localObject1 = new Intent("com.android.media.tv.remoteprovider.TvRemoteProvider");
    localObject1 = this.mPackageManager.queryIntentServicesAsUser((Intent)localObject1, 0, this.mUserId).iterator();
    int j;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = ((ResolveInfo)((Iterator)localObject1).next()).serviceInfo;
      if ((localObject2 != null) && (verifyServiceTrusted((ServiceInfo)localObject2)))
      {
        j = findProvider(((ServiceInfo)localObject2).packageName, ((ServiceInfo)localObject2).name);
        if (j < 0)
        {
          localObject2 = new TvRemoteProviderProxy(this.mContext, new ComponentName(((ServiceInfo)localObject2).packageName, ((ServiceInfo)localObject2).name), this.mUserId, ((ServiceInfo)localObject2).applicationInfo.uid);
          ((TvRemoteProviderProxy)localObject2).start();
          this.mProviderProxies.add(i, localObject2);
          this.mProvider.addProvider((TvRemoteProviderProxy)localObject2);
          i += 1;
        }
        else if (j >= i)
        {
          localObject2 = (TvRemoteProviderProxy)this.mProviderProxies.get(j);
          ((TvRemoteProviderProxy)localObject2).start();
          ((TvRemoteProviderProxy)localObject2).rebindIfDisconnected();
          Collections.swap(this.mProviderProxies, j, i);
          i += 1;
        }
      }
    }
    if (DEBUG) {
      Log.d("TvRemoteProvWatcher", "scanPackages() targetIndex " + i);
    }
    if (i < this.mProviderProxies.size())
    {
      j = this.mProviderProxies.size() - 1;
      while (j >= i)
      {
        localObject1 = (TvRemoteProviderProxy)this.mProviderProxies.get(j);
        this.mProvider.removeProvider((TvRemoteProviderProxy)localObject1);
        this.mProviderProxies.remove(localObject1);
        ((TvRemoteProviderProxy)localObject1).stop();
        j -= 1;
      }
    }
  }
  
  private boolean verifyServiceTrusted(ServiceInfo paramServiceInfo)
  {
    if ((paramServiceInfo.permission != null) && (paramServiceInfo.permission.equals("android.permission.BIND_TV_REMOTE_SERVICE")))
    {
      if (!paramServiceInfo.packageName.equals(this.mUnbundledServicePackage))
      {
        Slog.w("TvRemoteProvWatcher", "Ignoring atv remote provider service because the package has not been set and/or whitelisted: " + paramServiceInfo.packageName + "/" + paramServiceInfo.name);
        return false;
      }
    }
    else
    {
      Slog.w("TvRemoteProvWatcher", "Ignoring atv remote provider service because it did not require the BIND_TV_REMOTE_SERVICE permission in its manifest: " + paramServiceInfo.packageName + "/" + paramServiceInfo.name);
      return false;
    }
    if (!hasNecessaryPermissions(paramServiceInfo.packageName))
    {
      Slog.w("TvRemoteProvWatcher", "Ignoring atv remote provider service because its package does not have TV_VIRTUAL_REMOTE_CONTROLLER permission: " + paramServiceInfo.packageName);
      return false;
    }
    return true;
  }
  
  public void start()
  {
    if (DEBUG) {
      Slog.d("TvRemoteProvWatcher", "start()");
    }
    if (!this.mRunning)
    {
      this.mRunning = true;
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
      localIntentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
      localIntentFilter.addDataScheme("package");
      this.mContext.registerReceiverAsUser(this.mScanPackagesReceiver, new UserHandle(this.mUserId), localIntentFilter, null, this.mHandler);
      this.mHandler.post(this.mScanPackagesRunnable);
    }
  }
  
  public void stop()
  {
    if (this.mRunning)
    {
      this.mRunning = false;
      this.mContext.unregisterReceiver(this.mScanPackagesReceiver);
      this.mHandler.removeCallbacks(this.mScanPackagesRunnable);
      int i = this.mProviderProxies.size() - 1;
      while (i >= 0)
      {
        ((TvRemoteProviderProxy)this.mProviderProxies.get(i)).stop();
        i -= 1;
      }
    }
  }
  
  public static abstract interface ProviderMethods
  {
    public abstract void addProvider(TvRemoteProviderProxy paramTvRemoteProviderProxy);
    
    public abstract void removeProvider(TvRemoteProviderProxy paramTvRemoteProviderProxy);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvRemoteProviderWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */