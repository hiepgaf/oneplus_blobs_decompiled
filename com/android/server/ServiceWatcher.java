package com.android.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ServiceWatcher
  implements ServiceConnection
{
  private static final boolean D = false;
  public static final String EXTRA_SERVICE_IS_MULTIUSER = "serviceIsMultiuser";
  public static final String EXTRA_SERVICE_VERSION = "serviceVersion";
  private final String mAction;
  @GuardedBy("mLock")
  private ComponentName mBoundComponent;
  @GuardedBy("mLock")
  private String mBoundPackageName;
  @GuardedBy("mLock")
  private IBinder mBoundService;
  @GuardedBy("mLock")
  private int mBoundUserId = 55536;
  @GuardedBy("mLock")
  private int mBoundVersion = Integer.MIN_VALUE;
  private final Context mContext;
  @GuardedBy("mLock")
  private int mCurrentUserId = 0;
  private final Handler mHandler;
  private final Object mLock = new Object();
  private final Runnable mNewServiceWork;
  private final PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      synchronized (ServiceWatcher.-get1(ServiceWatcher.this))
      {
        boolean bool = Objects.equals(paramAnonymousString, ServiceWatcher.-get0(ServiceWatcher.this));
        ServiceWatcher.-wrap0(ServiceWatcher.this, null, bool);
        return;
      }
    }
    
    public boolean onPackageChanged(String paramAnonymousString, int paramAnonymousInt, String[] paramAnonymousArrayOfString)
    {
      synchronized (ServiceWatcher.-get1(ServiceWatcher.this))
      {
        boolean bool = Objects.equals(paramAnonymousString, ServiceWatcher.-get0(ServiceWatcher.this));
        ServiceWatcher.-wrap0(ServiceWatcher.this, null, bool);
        return super.onPackageChanged(paramAnonymousString, paramAnonymousInt, paramAnonymousArrayOfString);
      }
    }
    
    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      synchronized (ServiceWatcher.-get1(ServiceWatcher.this))
      {
        boolean bool = Objects.equals(paramAnonymousString, ServiceWatcher.-get0(ServiceWatcher.this));
        ServiceWatcher.-wrap0(ServiceWatcher.this, null, bool);
        return;
      }
    }
    
    public void onPackageUpdateFinished(String paramAnonymousString, int paramAnonymousInt)
    {
      synchronized (ServiceWatcher.-get1(ServiceWatcher.this))
      {
        boolean bool = Objects.equals(paramAnonymousString, ServiceWatcher.-get0(ServiceWatcher.this));
        ServiceWatcher.-wrap0(ServiceWatcher.this, null, bool);
        return;
      }
    }
  };
  private final PackageManager mPm;
  private final String mServicePackageName;
  private final List<HashSet<Signature>> mSignatureSets;
  private final String mTag;
  
  public ServiceWatcher(Context paramContext, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Runnable paramRunnable, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mTag = paramString1;
    this.mAction = paramString2;
    this.mPm = this.mContext.getPackageManager();
    this.mNewServiceWork = paramRunnable;
    this.mHandler = paramHandler;
    paramString2 = paramContext.getResources();
    boolean bool = paramString2.getBoolean(paramInt1);
    paramString1 = new ArrayList();
    if (bool)
    {
      paramString2 = paramString2.getStringArray(paramInt3);
      if (paramString2 != null) {
        paramString1.addAll(Arrays.asList(paramString2));
      }
    }
    for (this.mServicePackageName = null;; this.mServicePackageName = paramString2)
    {
      this.mSignatureSets = getSignatureSets(paramContext, paramString1);
      return;
      paramString2 = paramString2.getString(paramInt2);
      if (paramString2 != null) {
        paramString1.add(paramString2);
      }
    }
  }
  
  private boolean bindBestPackageLocked(String paramString, boolean paramBoolean)
  {
    Object localObject1 = new Intent(this.mAction);
    if (paramString != null) {
      ((Intent)localObject1).setPackage(paramString);
    }
    Object localObject2 = this.mPm.queryIntentServicesAsUser((Intent)localObject1, 268435584, this.mCurrentUserId);
    int i = Integer.MIN_VALUE;
    localObject1 = null;
    paramString = null;
    boolean bool2 = false;
    boolean bool1 = false;
    int j = i;
    if (localObject2 != null)
    {
      localObject2 = ((Iterable)localObject2).iterator();
      for (;;)
      {
        localObject1 = paramString;
        bool2 = bool1;
        j = i;
        if (!((Iterator)localObject2).hasNext()) {
          break;
        }
        ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject2).next();
        localObject1 = localResolveInfo.serviceInfo.getComponentName();
        String str = ((ComponentName)localObject1).getPackageName();
        try
        {
          if (isSignatureMatch(this.mPm.getPackageInfo(str, 268435520).signatures)) {
            break label200;
          }
          Log.w(this.mTag, str + " resolves service " + this.mAction + ", but has wrong signature, ignoring");
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.wtf(this.mTag, localNameNotFoundException);
        }
        continue;
        label200:
        j = Integer.MIN_VALUE;
        bool2 = false;
        if (localResolveInfo.serviceInfo.metaData != null)
        {
          j = localResolveInfo.serviceInfo.metaData.getInt("serviceVersion", Integer.MIN_VALUE);
          bool2 = localResolveInfo.serviceInfo.metaData.getBoolean("serviceIsMultiuser");
        }
        if (j > i)
        {
          i = j;
          paramString = localNameNotFoundException;
          bool1 = bool2;
        }
      }
    }
    if (localNameNotFoundException == null)
    {
      Slog.w(this.mTag, "Odd, no component found for service " + this.mAction);
      unbindLocked();
      return false;
    }
    int k;
    if (bool2)
    {
      k = 0;
      if ((!Objects.equals(localNameNotFoundException, this.mBoundComponent)) || (j != this.mBoundVersion)) {
        break label374;
      }
      if (k != this.mBoundUserId) {
        break label369;
      }
      i = 1;
      label350:
      if ((paramBoolean) || (i == 0)) {
        break label379;
      }
    }
    for (;;)
    {
      return true;
      k = this.mCurrentUserId;
      break;
      label369:
      i = 0;
      break label350;
      label374:
      i = 0;
      break label350;
      label379:
      unbindLocked();
      bindToPackageLocked(localNameNotFoundException, j, k);
    }
  }
  
  private void bindToPackageLocked(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent(this.mAction);
    localIntent.setComponent(paramComponentName);
    this.mBoundComponent = paramComponentName;
    this.mBoundPackageName = paramComponentName.getPackageName();
    this.mBoundVersion = paramInt1;
    this.mBoundUserId = paramInt2;
    this.mContext.bindServiceAsUser(localIntent, this, 1073741829, new UserHandle(paramInt2));
  }
  
  public static ArrayList<HashSet<Signature>> getSignatureSets(Context paramContext, List<String> paramList)
  {
    paramContext = paramContext.getPackageManager();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j = paramList.size();
    for (;;)
    {
      if (i < j)
      {
        String str = (String)paramList.get(i);
        try
        {
          HashSet localHashSet = new HashSet();
          localHashSet.addAll(Arrays.asList(paramContext.getPackageInfo(str, 1048640).signatures));
          localArrayList.add(localHashSet);
          i += 1;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          for (;;)
          {
            Log.w("ServiceWatcher", str + " not found");
          }
        }
      }
    }
    return localArrayList;
  }
  
  private boolean isServiceMissing()
  {
    Intent localIntent = new Intent(this.mAction);
    return this.mPm.queryIntentServicesAsUser(localIntent, 786432, this.mCurrentUserId).isEmpty();
  }
  
  private boolean isSignatureMatch(Signature[] paramArrayOfSignature)
  {
    return isSignatureMatch(paramArrayOfSignature, this.mSignatureSets);
  }
  
  public static boolean isSignatureMatch(Signature[] paramArrayOfSignature, List<HashSet<Signature>> paramList)
  {
    if (paramArrayOfSignature == null) {
      return false;
    }
    HashSet localHashSet = new HashSet();
    int j = paramArrayOfSignature.length;
    int i = 0;
    while (i < j)
    {
      localHashSet.add(paramArrayOfSignature[i]);
      i += 1;
    }
    paramArrayOfSignature = paramList.iterator();
    while (paramArrayOfSignature.hasNext()) {
      if (((HashSet)paramArrayOfSignature.next()).equals(localHashSet)) {
        return true;
      }
    }
    return false;
  }
  
  private void unbindLocked()
  {
    ComponentName localComponentName = this.mBoundComponent;
    this.mBoundComponent = null;
    this.mBoundPackageName = null;
    this.mBoundVersion = Integer.MIN_VALUE;
    this.mBoundUserId = 55536;
    if (localComponentName != null) {
      this.mContext.unbindService(this);
    }
  }
  
  public String getBestPackageName()
  {
    synchronized (this.mLock)
    {
      String str = this.mBoundPackageName;
      return str;
    }
  }
  
  public int getBestVersion()
  {
    synchronized (this.mLock)
    {
      int i = this.mBoundVersion;
      return i;
    }
  }
  
  public IBinder getBinder()
  {
    synchronized (this.mLock)
    {
      IBinder localIBinder = this.mBoundService;
      return localIBinder;
    }
  }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    synchronized (this.mLock)
    {
      if (paramComponentName.equals(this.mBoundComponent))
      {
        this.mBoundService = paramIBinder;
        if ((this.mHandler != null) && (this.mNewServiceWork != null)) {
          this.mHandler.post(this.mNewServiceWork);
        }
        return;
      }
      Log.w(this.mTag, "unexpected onServiceConnected: " + paramComponentName);
    }
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    synchronized (this.mLock)
    {
      if (paramComponentName.equals(this.mBoundComponent)) {
        this.mBoundService = null;
      }
      return;
    }
  }
  
  public boolean start()
  {
    if (isServiceMissing()) {
      return false;
    }
    synchronized (this.mLock)
    {
      bindBestPackageLocked(this.mServicePackageName, false);
      ??? = new IntentFilter();
      ((IntentFilter)???).addAction("android.intent.action.USER_SWITCHED");
      ((IntentFilter)???).addAction("android.intent.action.USER_UNLOCKED");
      this.mContext.registerReceiverAsUser(new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          paramAnonymousContext = paramAnonymousIntent.getAction();
          int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536);
          if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext)) {
            ServiceWatcher.this.switchUser(i);
          }
          while (!"android.intent.action.USER_UNLOCKED".equals(paramAnonymousContext)) {
            return;
          }
          ServiceWatcher.this.unlockUser(i);
        }
      }, UserHandle.ALL, (IntentFilter)???, null, this.mHandler);
      if (this.mServicePackageName == null) {
        this.mPackageMonitor.register(this.mContext, null, UserHandle.ALL, true);
      }
      return true;
    }
  }
  
  public void switchUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mCurrentUserId = paramInt;
      bindBestPackageLocked(this.mServicePackageName, false);
      return;
    }
  }
  
  public void unlockUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (paramInt == this.mCurrentUserId) {
        bindBestPackageLocked(this.mServicePackageName, false);
      }
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/ServiceWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */