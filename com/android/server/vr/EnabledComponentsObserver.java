package com.android.server.vr;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.content.PackageMonitor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EnabledComponentsObserver
  implements SettingsObserver.SettingChangeListener
{
  public static final int DISABLED = -1;
  private static final String ENABLED_SERVICES_SEPARATOR = ":";
  public static final int NOT_INSTALLED = -2;
  public static final int NO_ERROR = 0;
  private static final String TAG = EnabledComponentsObserver.class.getSimpleName();
  private final Context mContext;
  private final Set<EnabledComponentChangeListener> mEnabledComponentListeners = new ArraySet();
  private final SparseArray<ArraySet<ComponentName>> mEnabledSet = new SparseArray();
  private final SparseArray<ArraySet<ComponentName>> mInstalledSet = new SparseArray();
  private final Object mLock;
  private final String mServiceName;
  private final String mServicePermission;
  private final String mSettingName;
  
  private EnabledComponentsObserver(Context paramContext, String paramString1, String paramString2, String paramString3, Object paramObject, Collection<EnabledComponentChangeListener> paramCollection)
  {
    this.mLock = paramObject;
    this.mContext = paramContext;
    this.mSettingName = paramString1;
    this.mServiceName = paramString3;
    this.mServicePermission = paramString2;
    this.mEnabledComponentListeners.addAll(paramCollection);
  }
  
  public static EnabledComponentsObserver build(Context paramContext, Handler paramHandler, String paramString1, Looper paramLooper, String paramString2, String paramString3, Object paramObject, Collection<EnabledComponentChangeListener> paramCollection)
  {
    paramHandler = SettingsObserver.build(paramContext, paramHandler, paramString1);
    paramString1 = new EnabledComponentsObserver(paramContext, paramString1, paramString2, paramString3, paramObject, paramCollection);
    new PackageMonitor()
    {
      public boolean onHandleForceStop(Intent paramAnonymousIntent, String[] paramAnonymousArrayOfString, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        this.val$o.onPackagesChanged();
        return super.onHandleForceStop(paramAnonymousIntent, paramAnonymousArrayOfString, paramAnonymousInt, paramAnonymousBoolean);
      }
      
      public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
      {
        this.val$o.onPackagesChanged();
      }
      
      public void onPackageModified(String paramAnonymousString)
      {
        this.val$o.onPackagesChanged();
      }
      
      public void onSomePackagesChanged()
      {
        this.val$o.onPackagesChanged();
      }
    }.register(paramContext, paramLooper, UserHandle.ALL, true);
    paramHandler.addListener(paramString1);
    return paramString1;
  }
  
  private int[] getCurrentProfileIds()
  {
    UserManager localUserManager = (UserManager)this.mContext.getSystemService("user");
    if (localUserManager == null) {
      return null;
    }
    return localUserManager.getEnabledProfileIds(ActivityManager.getCurrentUser());
  }
  
  public static ArraySet<ComponentName> loadComponentNames(PackageManager paramPackageManager, int paramInt, String paramString1, String paramString2)
  {
    ArraySet localArraySet = new ArraySet();
    paramPackageManager = paramPackageManager.queryIntentServicesAsUser(new Intent(paramString1), 786564, paramInt);
    if (paramPackageManager != null)
    {
      paramInt = 0;
      int i = paramPackageManager.size();
      if (paramInt < i)
      {
        paramString1 = ((ResolveInfo)paramPackageManager.get(paramInt)).serviceInfo;
        ComponentName localComponentName = new ComponentName(paramString1.packageName, paramString1.name);
        if (!paramString2.equals(paramString1.permission)) {
          Slog.w(TAG, "Skipping service " + paramString1.packageName + "/" + paramString1.name + ": it does not require the permission " + paramString2);
        }
        for (;;)
        {
          paramInt += 1;
          break;
          localArraySet.add(localComponentName);
        }
      }
    }
    return localArraySet;
  }
  
  private ArraySet<ComponentName> loadComponentNamesForUser(int paramInt)
  {
    return loadComponentNames(this.mContext.getPackageManager(), paramInt, this.mServiceName, this.mServicePermission);
  }
  
  private ArraySet<ComponentName> loadComponentNamesFromSetting(String paramString, int paramInt)
  {
    paramString = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), paramString, paramInt);
    if (TextUtils.isEmpty(paramString)) {
      return new ArraySet();
    }
    paramString = paramString.split(":");
    ArraySet localArraySet = new ArraySet(paramString.length);
    paramInt = 0;
    while (paramInt < paramString.length)
    {
      ComponentName localComponentName = ComponentName.unflattenFromString(paramString[paramInt]);
      if (localComponentName != null) {
        localArraySet.add(localComponentName);
      }
      paramInt += 1;
    }
    return localArraySet;
  }
  
  private void sendSettingChanged()
  {
    Iterator localIterator = this.mEnabledComponentListeners.iterator();
    while (localIterator.hasNext()) {
      ((EnabledComponentChangeListener)localIterator.next()).onEnabledComponentChanged();
    }
  }
  
  public ArraySet<ComponentName> getEnabled(int paramInt)
  {
    synchronized (this.mLock)
    {
      ArraySet localArraySet = (ArraySet)this.mEnabledSet.get(paramInt);
      if (localArraySet == null)
      {
        localArraySet = new ArraySet();
        return localArraySet;
      }
      return localArraySet;
    }
  }
  
  public ArraySet<ComponentName> getInstalled(int paramInt)
  {
    synchronized (this.mLock)
    {
      ArraySet localArraySet = (ArraySet)this.mInstalledSet.get(paramInt);
      if (localArraySet == null)
      {
        localArraySet = new ArraySet();
        return localArraySet;
      }
      return localArraySet;
    }
  }
  
  public int isValid(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mLock)
    {
      ArraySet localArraySet = (ArraySet)this.mInstalledSet.get(paramInt);
      if ((localArraySet != null) && (localArraySet.contains(paramComponentName)))
      {
        localArraySet = (ArraySet)this.mEnabledSet.get(paramInt);
        if (localArraySet != null)
        {
          boolean bool = localArraySet.contains(paramComponentName);
          if (bool) {
            return 0;
          }
        }
      }
      else
      {
        return -2;
      }
      return -1;
    }
  }
  
  public void onPackagesChanged()
  {
    rebuildAll();
  }
  
  public void onSettingChanged()
  {
    rebuildAll();
  }
  
  public void onSettingRestored(String paramString1, String paramString2, int paramInt)
  {
    rebuildAll();
  }
  
  public void onUsersChanged()
  {
    rebuildAll();
  }
  
  public void rebuildAll()
  {
    synchronized (this.mLock)
    {
      this.mInstalledSet.clear();
      this.mEnabledSet.clear();
      int[] arrayOfInt = getCurrentProfileIds();
      int i = 0;
      int j = arrayOfInt.length;
      while (i < j)
      {
        int k = arrayOfInt[i];
        ArraySet localArraySet1 = loadComponentNamesForUser(k);
        ArraySet localArraySet2 = loadComponentNamesFromSetting(this.mSettingName, k);
        localArraySet2.retainAll(localArraySet1);
        this.mInstalledSet.put(k, localArraySet1);
        this.mEnabledSet.put(k, localArraySet2);
        i += 1;
      }
      sendSettingChanged();
      return;
    }
  }
  
  public static abstract interface EnabledComponentChangeListener
  {
    public abstract void onEnabledComponentChanged();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/vr/EnabledComponentsObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */