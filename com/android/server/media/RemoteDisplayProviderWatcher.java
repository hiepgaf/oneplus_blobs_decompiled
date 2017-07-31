package com.android.server.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class RemoteDisplayProviderWatcher
{
  private static final boolean DEBUG = Log.isLoggable("RemoteDisplayProvider", 3);
  private static final String TAG = "RemoteDisplayProvider";
  private final Callback mCallback;
  private final Context mContext;
  private final Handler mHandler;
  private final PackageManager mPackageManager;
  private final ArrayList<RemoteDisplayProviderProxy> mProviders = new ArrayList();
  private boolean mRunning;
  private final BroadcastReceiver mScanPackagesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (RemoteDisplayProviderWatcher.-get0()) {
        Slog.d("RemoteDisplayProvider", "Received package manager broadcast: " + paramAnonymousIntent);
      }
      RemoteDisplayProviderWatcher.-wrap0(RemoteDisplayProviderWatcher.this);
    }
  };
  private final Runnable mScanPackagesRunnable = new Runnable()
  {
    public void run()
    {
      RemoteDisplayProviderWatcher.-wrap0(RemoteDisplayProviderWatcher.this);
    }
  };
  private final int mUserId;
  
  public RemoteDisplayProviderWatcher(Context paramContext, Callback paramCallback, Handler paramHandler, int paramInt)
  {
    this.mContext = paramContext;
    this.mCallback = paramCallback;
    this.mHandler = paramHandler;
    this.mUserId = paramInt;
    this.mPackageManager = paramContext.getPackageManager();
  }
  
  private int findProvider(String paramString1, String paramString2)
  {
    int j = this.mProviders.size();
    int i = 0;
    while (i < j)
    {
      if (((RemoteDisplayProviderProxy)this.mProviders.get(i)).hasComponentName(paramString1, paramString2)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private boolean hasCaptureVideoPermission(String paramString)
  {
    if (this.mPackageManager.checkPermission("android.permission.CAPTURE_VIDEO_OUTPUT", paramString) == 0) {
      return true;
    }
    return this.mPackageManager.checkPermission("android.permission.CAPTURE_SECURE_VIDEO_OUTPUT", paramString) == 0;
  }
  
  private void scanPackages()
  {
    if (!this.mRunning) {
      return;
    }
    int i = 0;
    Object localObject1 = new Intent("com.android.media.remotedisplay.RemoteDisplayProvider");
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
          localObject2 = new RemoteDisplayProviderProxy(this.mContext, new ComponentName(((ServiceInfo)localObject2).packageName, ((ServiceInfo)localObject2).name), this.mUserId);
          ((RemoteDisplayProviderProxy)localObject2).start();
          this.mProviders.add(i, localObject2);
          this.mCallback.addProvider((RemoteDisplayProviderProxy)localObject2);
          i += 1;
        }
        else if (j >= i)
        {
          localObject2 = (RemoteDisplayProviderProxy)this.mProviders.get(j);
          ((RemoteDisplayProviderProxy)localObject2).start();
          ((RemoteDisplayProviderProxy)localObject2).rebindIfDisconnected();
          Collections.swap(this.mProviders, j, i);
          i += 1;
        }
      }
    }
    if (i < this.mProviders.size())
    {
      j = this.mProviders.size() - 1;
      while (j >= i)
      {
        localObject1 = (RemoteDisplayProviderProxy)this.mProviders.get(j);
        this.mCallback.removeProvider((RemoteDisplayProviderProxy)localObject1);
        this.mProviders.remove(localObject1);
        ((RemoteDisplayProviderProxy)localObject1).stop();
        j -= 1;
      }
    }
  }
  
  private boolean verifyServiceTrusted(ServiceInfo paramServiceInfo)
  {
    if ((paramServiceInfo.permission != null) && (paramServiceInfo.permission.equals("android.permission.BIND_REMOTE_DISPLAY")))
    {
      if (!hasCaptureVideoPermission(paramServiceInfo.packageName))
      {
        Slog.w("RemoteDisplayProvider", "Ignoring remote display provider service because it does not have the CAPTURE_VIDEO_OUTPUT or CAPTURE_SECURE_VIDEO_OUTPUT permission: " + paramServiceInfo.packageName + "/" + paramServiceInfo.name);
        return false;
      }
    }
    else
    {
      Slog.w("RemoteDisplayProvider", "Ignoring remote display provider service because it did not require the BIND_REMOTE_DISPLAY permission in its manifest: " + paramServiceInfo.packageName + "/" + paramServiceInfo.name);
      return false;
    }
    return true;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.println(paramString + "Watcher");
    paramPrintWriter.println(paramString + "  mUserId=" + this.mUserId);
    paramPrintWriter.println(paramString + "  mRunning=" + this.mRunning);
    paramPrintWriter.println(paramString + "  mProviders.size()=" + this.mProviders.size());
  }
  
  public void start()
  {
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
      int i = this.mProviders.size() - 1;
      while (i >= 0)
      {
        ((RemoteDisplayProviderProxy)this.mProviders.get(i)).stop();
        i -= 1;
      }
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void addProvider(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy);
    
    public abstract void removeProvider(RemoteDisplayProviderProxy paramRemoteDisplayProviderProxy);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/media/RemoteDisplayProviderWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */