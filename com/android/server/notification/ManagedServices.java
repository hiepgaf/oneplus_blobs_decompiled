package com.android.server.notification;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.am.OnePlusAppBootManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class ManagedServices
{
  protected static final String ENABLED_SERVICES_SEPARATOR = ":";
  protected final boolean DEBUG = Log.isLoggable(this.TAG, 3);
  protected final String TAG = getClass().getSimpleName();
  private final Config mConfig;
  protected final Context mContext;
  private ArraySet<ComponentName> mEnabledServicesForCurrentProfiles = new ArraySet();
  private ArraySet<String> mEnabledServicesPackageNames = new ArraySet();
  private int[] mLastSeenProfileIds;
  protected final Object mMutex;
  private final BroadcastReceiver mRestoreReceiver;
  private ArraySet<String> mRestored;
  private ArraySet<String> mRestoredPackages = new ArraySet();
  protected final ArrayList<ManagedServiceInfo> mServices = new ArrayList();
  private final ArrayList<String> mServicesBinding = new ArrayList();
  private final SettingsObserver mSettingsObserver;
  private ArraySet<ComponentName> mSnoozingForCurrentProfiles = new ArraySet();
  private final UserProfiles mUserProfiles;
  
  public ManagedServices(Context paramContext, Handler paramHandler, Object paramObject, UserProfiles paramUserProfiles)
  {
    this.mContext = paramContext;
    this.mMutex = paramObject;
    this.mUserProfiles = paramUserProfiles;
    this.mConfig = getConfig();
    this.mSettingsObserver = new SettingsObserver(paramHandler, null);
    this.mRestoreReceiver = new SettingRestoredReceiver();
    paramHandler = new IntentFilter("android.os.action.SETTING_RESTORED");
    paramContext.registerReceiver(this.mRestoreReceiver, paramHandler);
    rebuildRestoredPackages();
  }
  
  private void checkNotNull(IInterface paramIInterface)
  {
    if (paramIInterface == null) {
      throw new IllegalArgumentException(getCaption() + " must not be null");
    }
  }
  
  private String getCaption()
  {
    return this.mConfig.caption;
  }
  
  private ManagedServiceInfo newServiceInfo(IInterface paramIInterface, ComponentName paramComponentName, int paramInt1, boolean paramBoolean, ServiceConnection paramServiceConnection, int paramInt2)
  {
    return new ManagedServiceInfo(paramIInterface, paramComponentName, paramInt1, paramBoolean, paramServiceConnection, paramInt2);
  }
  
  private void rebindServices(boolean paramBoolean)
  {
    if (this.DEBUG) {
      Slog.d(this.TAG, "rebindServices");
    }
    int[] arrayOfInt1 = this.mUserProfiles.getCurrentProfileIds();
    int k = arrayOfInt1.length;
    Object localObject2 = new SparseArray();
    int i = 0;
    while (i < k)
    {
      ((SparseArray)localObject2).put(arrayOfInt1[i], loadComponentNamesFromSetting(this.mConfig.secureSettingName, arrayOfInt1[i]));
      if (this.mConfig.secondarySettingName != null) {
        ((ArraySet)((SparseArray)localObject2).get(arrayOfInt1[i])).addAll(loadComponentNamesFromSetting(this.mConfig.secondarySettingName, arrayOfInt1[i]));
      }
      i += 1;
    }
    Object localObject3 = new ArrayList();
    SparseArray localSparseArray = new SparseArray();
    Object localObject4;
    Object localObject5;
    synchronized (this.mMutex)
    {
      localObject4 = this.mServices.iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (ManagedServiceInfo)((Iterator)localObject4).next();
        if ((!((ManagedServiceInfo)localObject5).isSystem) && (!((ManagedServiceInfo)localObject5).isGuest(this))) {
          ((ArrayList)localObject3).add(localObject5);
        }
      }
    }
    this.mEnabledServicesForCurrentProfiles.clear();
    this.mEnabledServicesPackageNames.clear();
    i = 0;
    for (;;)
    {
      if (i < k)
      {
        localObject4 = (ArraySet)((SparseArray)localObject2).get(arrayOfInt2[i]);
        if (localObject4 == null)
        {
          localSparseArray.put(arrayOfInt2[i], new ArraySet());
        }
        else
        {
          localObject5 = new HashSet((Collection)localObject4);
          ((Set)localObject5).removeAll(this.mSnoozingForCurrentProfiles);
          localSparseArray.put(arrayOfInt2[i], localObject5);
          this.mEnabledServicesForCurrentProfiles.addAll((ArraySet)localObject4);
          int j = 0;
          while (j < ((ArraySet)localObject4).size())
          {
            localObject5 = (ComponentName)((ArraySet)localObject4).valueAt(j);
            this.mEnabledServicesPackageNames.add(((ComponentName)localObject5).getPackageName());
            j += 1;
          }
        }
      }
      else
      {
        ??? = ((Iterable)localObject3).iterator();
        while (((Iterator)???).hasNext())
        {
          localObject3 = (ManagedServiceInfo)((Iterator)???).next();
          localObject2 = ((ManagedServiceInfo)localObject3).component;
          i = ((ManagedServiceInfo)localObject3).userid;
          localObject3 = (Set)localSparseArray.get(((ManagedServiceInfo)localObject3).userid);
          if (localObject3 != null) {
            if ((!((Set)localObject3).contains(localObject2)) || (paramBoolean))
            {
              Slog.v(this.TAG, "disabling " + getCaption() + " for user " + i + ": " + localObject2);
              unregisterService((ComponentName)localObject2, i);
            }
            else
            {
              ((Set)localObject3).remove(localObject2);
            }
          }
        }
        i = 0;
        while (i < k)
        {
          ??? = ((Set)localSparseArray.get(arrayOfInt2[i])).iterator();
          while (((Iterator)???).hasNext())
          {
            localObject2 = (ComponentName)((Iterator)???).next();
            Slog.v(this.TAG, "enabling " + getCaption() + " for " + arrayOfInt2[i] + ": " + localObject2);
            registerService((ComponentName)localObject2, arrayOfInt2[i]);
          }
          i += 1;
        }
        this.mLastSeenProfileIds = arrayOfInt2;
        return;
      }
      i += 1;
    }
  }
  
  private void rebuildRestoredPackages()
  {
    this.mRestoredPackages.clear();
    this.mSnoozingForCurrentProfiles.clear();
    String str2 = restoredSettingName(this.mConfig.secureSettingName);
    String str1;
    int[] arrayOfInt;
    int j;
    int i;
    if (this.mConfig.secondarySettingName == null)
    {
      str1 = null;
      arrayOfInt = this.mUserProfiles.getCurrentProfileIds();
      j = arrayOfInt.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j) {
        return;
      }
      Object localObject = loadComponentNamesFromSetting(str2, arrayOfInt[i]);
      if (str1 != null) {
        ((ArraySet)localObject).addAll(loadComponentNamesFromSetting(str1, arrayOfInt[i]));
      }
      localObject = ((Iterable)localObject).iterator();
      for (;;)
      {
        if (((Iterator)localObject).hasNext())
        {
          ComponentName localComponentName = (ComponentName)((Iterator)localObject).next();
          this.mRestoredPackages.add(localComponentName.getPackageName());
          continue;
          str1 = restoredSettingName(this.mConfig.secondarySettingName);
          break;
        }
      }
      i += 1;
    }
  }
  
  private void registerService(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mMutex)
    {
      registerServiceLocked(paramComponentName, paramInt);
      return;
    }
  }
  
  private ManagedServiceInfo registerServiceImpl(IInterface paramIInterface, ComponentName paramComponentName, int paramInt)
  {
    return registerServiceImpl(newServiceInfo(paramIInterface, paramComponentName, paramInt, true, null, 21));
  }
  
  private ManagedServiceInfo registerServiceImpl(ManagedServiceInfo paramManagedServiceInfo)
  {
    synchronized (this.mMutex)
    {
      try
      {
        paramManagedServiceInfo.service.asBinder().linkToDeath(paramManagedServiceInfo, 0);
        this.mServices.add(paramManagedServiceInfo);
        return paramManagedServiceInfo;
      }
      catch (RemoteException paramManagedServiceInfo)
      {
        return null;
      }
    }
  }
  
  private void registerServiceLocked(ComponentName paramComponentName, int paramInt)
  {
    registerServiceLocked(paramComponentName, paramInt, false);
  }
  
  private void registerServiceLocked(ComponentName paramComponentName, final int paramInt, final boolean paramBoolean)
  {
    if (this.DEBUG) {
      Slog.v(this.TAG, "registerService: " + paramComponentName + " u=" + paramInt);
    }
    final String str = paramComponentName.toString() + "/" + paramInt;
    if (this.mServicesBinding.contains(str)) {
      return;
    }
    this.mServicesBinding.add(str);
    i = this.mServices.size() - 1;
    while (i >= 0)
    {
      localObject = (ManagedServiceInfo)this.mServices.get(i);
      if ((paramComponentName.equals(((ManagedServiceInfo)localObject).component)) && (((ManagedServiceInfo)localObject).userid == paramInt))
      {
        if (this.DEBUG) {
          Slog.v(this.TAG, "    disconnecting old " + getCaption() + ": " + ((ManagedServiceInfo)localObject).service);
        }
        removeServiceLocked(i);
        if (((ManagedServiceInfo)localObject).connection != null) {
          this.mContext.unbindService(((ManagedServiceInfo)localObject).connection);
        }
      }
      i -= 1;
    }
    Intent localIntent = new Intent(this.mConfig.serviceInterface);
    localIntent.setComponent(paramComponentName);
    localIntent.putExtra("android.intent.extra.client_label", this.mConfig.clientLabel);
    localIntent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent(this.mConfig.settingsAction), 0));
    Object localObject = null;
    try
    {
      ApplicationInfo localApplicationInfo = this.mContext.getPackageManager().getApplicationInfo(paramComponentName.getPackageName(), 0);
      localObject = localApplicationInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        continue;
        i = 1;
      }
    }
    if (localObject != null)
    {
      i = ((ApplicationInfo)localObject).targetSdkVersion;
      try
      {
        if (this.DEBUG) {
          Slog.v(this.TAG, "binding: " + localIntent);
        }
        localObject = new ServiceConnection()
        {
          IInterface mService;
          
          public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
          {
            int i = 0;
            Object localObject2 = null;
            synchronized (ManagedServices.this.mMutex)
            {
              ManagedServices.-get2(ManagedServices.this).remove(str);
              localObject1 = localObject2;
              try
              {
                this.mService = ManagedServices.this.asInterface(paramAnonymousIBinder);
                localObject1 = localObject2;
                paramAnonymousComponentName = ManagedServices.-wrap0(ManagedServices.this, this.mService, paramAnonymousComponentName, paramInt, paramBoolean, this, i);
                localObject1 = paramAnonymousComponentName;
                paramAnonymousIBinder.linkToDeath(paramAnonymousComponentName, 0);
                localObject1 = paramAnonymousComponentName;
                boolean bool = ManagedServices.this.mServices.add(paramAnonymousComponentName);
                i = bool;
              }
              catch (RemoteException paramAnonymousComponentName)
              {
                for (;;)
                {
                  paramAnonymousComponentName = (ComponentName)localObject1;
                }
              }
              if (i != 0) {
                ManagedServices.this.onServiceAdded(paramAnonymousComponentName);
              }
              return;
            }
          }
          
          public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
          {
            Slog.v(ManagedServices.this.TAG, ManagedServices.-wrap2(ManagedServices.this) + " connection lost: " + paramAnonymousComponentName);
          }
        };
        if (OnePlusAppBootManager.DEBUG) {
          OnePlusAppBootManager.myLog(" registerServiceLocked # servicesBindingTag=" + str);
        }
        if ((OnePlusAppBootManager.IN_USING) && (!OnePlusAppBootManager.getInstance(null).canNotificationListenerServiceGo(paramComponentName)))
        {
          this.mServicesBinding.remove(str);
          Slog.e("OnePlusAppBootManager", "forbid to to bind " + getCaption() + " service: " + localIntent);
          return;
        }
        if (!this.mContext.bindServiceAsUser(localIntent, (ServiceConnection)localObject, 83886081, new UserHandle(paramInt)))
        {
          this.mServicesBinding.remove(str);
          Slog.w(this.TAG, "Unable to bind " + getCaption() + " service: " + localIntent);
          return;
        }
      }
      catch (SecurityException paramComponentName)
      {
        Slog.e(this.TAG, "Unable to bind " + getCaption() + " service: " + localIntent, paramComponentName);
        return;
      }
      return;
    }
  }
  
  private ManagedServiceInfo removeServiceImpl(IInterface paramIInterface, int paramInt)
  {
    if (this.DEBUG) {
      Slog.d(this.TAG, "removeServiceImpl service=" + paramIInterface + " u=" + paramInt);
    }
    Object localObject1 = null;
    synchronized (this.mMutex)
    {
      int i = this.mServices.size() - 1;
      while (i >= 0)
      {
        ManagedServiceInfo localManagedServiceInfo = (ManagedServiceInfo)this.mServices.get(i);
        Object localObject2 = localObject1;
        if (localManagedServiceInfo.service.asBinder() == paramIInterface.asBinder())
        {
          localObject2 = localObject1;
          if (localManagedServiceInfo.userid == paramInt)
          {
            if (this.DEBUG) {
              Slog.d(this.TAG, "Removing active service " + localManagedServiceInfo.component);
            }
            localObject2 = removeServiceLocked(i);
          }
        }
        i -= 1;
        localObject1 = localObject2;
      }
      return (ManagedServiceInfo)localObject1;
    }
  }
  
  private ManagedServiceInfo removeServiceLocked(int paramInt)
  {
    ManagedServiceInfo localManagedServiceInfo = (ManagedServiceInfo)this.mServices.remove(paramInt);
    onServiceRemovedLocked(localManagedServiceInfo);
    return localManagedServiceInfo;
  }
  
  public static String restoredSettingName(String paramString)
  {
    return paramString + ":restored";
  }
  
  private void storeComponentsToSetting(Set<ComponentName> paramSet, String paramString, int paramInt)
  {
    Object localObject = null;
    if (paramSet != null)
    {
      String[] arrayOfString = new String[paramSet.size()];
      int i = 0;
      paramSet = paramSet.iterator();
      for (;;)
      {
        localObject = arrayOfString;
        if (!paramSet.hasNext()) {
          break;
        }
        arrayOfString[i] = ((ComponentName)paramSet.next()).flattenToString();
        i += 1;
      }
    }
    if (localObject == null) {}
    for (paramSet = "";; paramSet = TextUtils.join(":", (Object[])localObject))
    {
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), paramString, paramSet, paramInt);
      return;
    }
  }
  
  private void unregisterService(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mMutex)
    {
      unregisterServiceLocked(paramComponentName, paramInt);
      return;
    }
  }
  
  private void unregisterServiceImpl(IInterface paramIInterface, int paramInt)
  {
    paramIInterface = removeServiceImpl(paramIInterface, paramInt);
    if ((paramIInterface == null) || (paramIInterface.connection == null) || (paramIInterface.isGuest(this))) {
      return;
    }
    this.mContext.unbindService(paramIInterface.connection);
  }
  
  private void unregisterServiceLocked(ComponentName paramComponentName, int paramInt)
  {
    int i = this.mServices.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        ManagedServiceInfo localManagedServiceInfo = (ManagedServiceInfo)this.mServices.get(i);
        if ((paramComponentName.equals(localManagedServiceInfo.component)) && (localManagedServiceInfo.userid == paramInt))
        {
          removeServiceLocked(i);
          if (localManagedServiceInfo.connection == null) {}
        }
        try
        {
          this.mContext.unbindService(localManagedServiceInfo.connection);
          i -= 1;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          for (;;)
          {
            Slog.e(this.TAG, getCaption() + " " + paramComponentName + " could not be unbound: " + localIllegalArgumentException);
          }
        }
      }
    }
  }
  
  private void updateSettingsAccordingToInstalledServices()
  {
    int[] arrayOfInt = this.mUserProfiles.getCurrentProfileIds();
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      updateSettingsAccordingToInstalledServices(this.mConfig.secureSettingName, arrayOfInt[i]);
      if (this.mConfig.secondarySettingName != null) {
        updateSettingsAccordingToInstalledServices(this.mConfig.secondarySettingName, arrayOfInt[i]);
      }
      i += 1;
    }
    rebuildRestoredPackages();
  }
  
  private void updateSettingsAccordingToInstalledServices(String paramString, int paramInt)
  {
    int m = 0;
    int j = 0;
    int i = 0;
    ArraySet localArraySet1 = loadComponentNamesFromSetting(restoredSettingName(paramString), paramInt);
    ArraySet localArraySet2 = loadComponentNamesFromSetting(paramString, paramInt);
    Object localObject = queryPackageForServices(null, paramInt);
    ArraySet localArraySet3 = new ArraySet();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ComponentName localComponentName = (ComponentName)((Iterator)localObject).next();
      if ((localArraySet1 != null) && (localArraySet1.remove(localComponentName)))
      {
        if (this.DEBUG) {
          Slog.v(this.TAG, "Restoring " + localComponentName + " for user " + paramInt);
        }
        j = 1;
        i = 1;
        localArraySet3.add(localComponentName);
      }
      else if ((localArraySet2 != null) && (localArraySet2.contains(localComponentName)))
      {
        localArraySet3.add(localComponentName);
      }
    }
    if (localArraySet2 == null) {}
    for (int k = 0;; k = localArraySet2.size())
    {
      if (k != localArraySet3.size()) {
        m = 1;
      }
      if ((i | m) != 0)
      {
        if (this.DEBUG) {
          Slog.v(this.TAG, "List of  " + getCaption() + " services was updated " + localArraySet2);
        }
        storeComponentsToSetting(localArraySet3, paramString, paramInt);
      }
      if (j != 0)
      {
        if (this.DEBUG) {
          Slog.v(this.TAG, "List of  " + getCaption() + " restored services was updated " + localArraySet1);
        }
        storeComponentsToSetting(localArraySet1, restoredSettingName(paramString), paramInt);
      }
      return;
    }
  }
  
  protected abstract IInterface asInterface(IBinder paramIBinder);
  
  public ManagedServiceInfo checkServiceTokenLocked(IInterface paramIInterface)
  {
    checkNotNull(paramIInterface);
    ManagedServiceInfo localManagedServiceInfo = getServiceFromTokenLocked(paramIInterface);
    if (localManagedServiceInfo != null) {
      return localManagedServiceInfo;
    }
    throw new SecurityException("Disallowed call from unknown " + getCaption() + ": " + paramIInterface);
  }
  
  protected abstract boolean checkType(IInterface paramIInterface);
  
  public void dump(PrintWriter paramPrintWriter, NotificationManagerService.DumpFilter paramDumpFilter)
  {
    paramPrintWriter.println("    All " + getCaption() + "s (" + this.mEnabledServicesForCurrentProfiles.size() + ") enabled for current profiles:");
    Object localObject1 = this.mEnabledServicesForCurrentProfiles.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ComponentName)((Iterator)localObject1).next();
      if ((paramDumpFilter == null) || (paramDumpFilter.matches((ComponentName)localObject2))) {
        paramPrintWriter.println("      " + localObject2);
      }
    }
    paramPrintWriter.println("    Live " + getCaption() + "s (" + this.mServices.size() + "):");
    Object localObject2 = this.mServices.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ManagedServiceInfo localManagedServiceInfo = (ManagedServiceInfo)((Iterator)localObject2).next();
      if ((paramDumpFilter == null) || (paramDumpFilter.matches(localManagedServiceInfo.component)))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("      ").append(localManagedServiceInfo.component).append(" (user ").append(localManagedServiceInfo.userid).append("): ").append(localManagedServiceInfo.service);
        if (localManagedServiceInfo.isSystem)
        {
          localObject1 = " SYSTEM";
          label281:
          localStringBuilder = localStringBuilder.append((String)localObject1);
          if (!localManagedServiceInfo.isGuest(this)) {
            break label325;
          }
        }
        label325:
        for (localObject1 = " GUEST";; localObject1 = "")
        {
          paramPrintWriter.println((String)localObject1);
          break;
          localObject1 = "";
          break label281;
        }
      }
    }
    paramPrintWriter.println("    Snoozed " + getCaption() + "s (" + this.mSnoozingForCurrentProfiles.size() + "):");
    paramDumpFilter = this.mSnoozingForCurrentProfiles.iterator();
    while (paramDumpFilter.hasNext())
    {
      localObject1 = (ComponentName)paramDumpFilter.next();
      paramPrintWriter.println("      " + ((ComponentName)localObject1).flattenToShortString());
    }
  }
  
  protected abstract Config getConfig();
  
  public ManagedServiceInfo getServiceFromTokenLocked(IInterface paramIInterface)
  {
    if (paramIInterface == null) {
      return null;
    }
    paramIInterface = paramIInterface.asBinder();
    int j = this.mServices.size();
    int i = 0;
    while (i < j)
    {
      ManagedServiceInfo localManagedServiceInfo = (ManagedServiceInfo)this.mServices.get(i);
      if (localManagedServiceInfo.service.asBinder() == paramIInterface) {
        return localManagedServiceInfo;
      }
      i += 1;
    }
    return null;
  }
  
  public boolean isComponentEnabledForCurrentProfiles(ComponentName paramComponentName)
  {
    return this.mEnabledServicesForCurrentProfiles.contains(paramComponentName);
  }
  
  public boolean isComponentEnabledForPackage(String paramString)
  {
    return this.mEnabledServicesPackageNames.contains(paramString);
  }
  
  protected ArraySet<ComponentName> loadComponentNamesFromSetting(String paramString, int paramInt)
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
  
  public void onBootPhaseAppsCanStart()
  {
    SettingsObserver.-wrap0(this.mSettingsObserver);
  }
  
  public void onPackagesChanged(boolean paramBoolean, String[] paramArrayOfString)
  {
    Object localObject = null;
    String str;
    StringBuilder localStringBuilder;
    if (this.DEBUG)
    {
      str = this.TAG;
      localStringBuilder = new StringBuilder().append("onPackagesChanged removingPackage=").append(paramBoolean).append(" pkgList=");
      if (paramArrayOfString != null) {
        break label154;
      }
    }
    int k;
    for (;;)
    {
      Slog.d(str, localObject + " mEnabledServicesPackageNames=" + this.mEnabledServicesPackageNames);
      int j = 0;
      int i = 0;
      k = j;
      if (paramArrayOfString == null) {
        break;
      }
      k = j;
      if (paramArrayOfString.length <= 0) {
        break;
      }
      int m = paramArrayOfString.length;
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= m) {
          break;
        }
        localObject = paramArrayOfString[j];
        if ((this.mEnabledServicesPackageNames.contains(localObject)) || (this.mRestoredPackages.contains(localObject))) {
          i = 1;
        }
        j += 1;
      }
      label154:
      localObject = Arrays.asList(paramArrayOfString);
    }
    if (k != 0)
    {
      if (paramBoolean)
      {
        updateSettingsAccordingToInstalledServices();
        rebuildRestoredPackages();
      }
      rebindServices(false);
    }
  }
  
  protected abstract void onServiceAdded(ManagedServiceInfo paramManagedServiceInfo);
  
  protected void onServiceRemovedLocked(ManagedServiceInfo paramManagedServiceInfo) {}
  
  public void onUserSwitched(int paramInt)
  {
    if (this.DEBUG) {
      Slog.d(this.TAG, "onUserSwitched u=" + paramInt);
    }
    rebuildRestoredPackages();
    if (Arrays.equals(this.mLastSeenProfileIds, this.mUserProfiles.getCurrentProfileIds()))
    {
      if (this.DEBUG) {
        Slog.d(this.TAG, "Current profile IDs didn't change, skipping rebindServices().");
      }
      return;
    }
    rebindServices(true);
  }
  
  public void onUserUnlocked(int paramInt)
  {
    if (this.DEBUG) {
      Slog.d(this.TAG, "onUserUnlocked u=" + paramInt);
    }
    rebuildRestoredPackages();
    rebindServices(false);
  }
  
  protected Set<ComponentName> queryPackageForServices(String paramString, int paramInt)
  {
    ArraySet localArraySet = new ArraySet();
    Object localObject1 = this.mContext.getPackageManager();
    Object localObject2 = new Intent(this.mConfig.serviceInterface);
    if (!TextUtils.isEmpty(paramString)) {
      ((Intent)localObject2).setPackage(paramString);
    }
    paramString = ((PackageManager)localObject1).queryIntentServicesAsUser((Intent)localObject2, 132, paramInt);
    if (this.DEBUG) {
      Slog.v(this.TAG, this.mConfig.serviceInterface + " services: " + paramString);
    }
    if (paramString != null)
    {
      paramInt = 0;
      int i = paramString.size();
      if (paramInt < i)
      {
        localObject1 = ((ResolveInfo)paramString.get(paramInt)).serviceInfo;
        localObject2 = new ComponentName(((ServiceInfo)localObject1).packageName, ((ServiceInfo)localObject1).name);
        if (!this.mConfig.bindPermission.equals(((ServiceInfo)localObject1).permission)) {
          Slog.w(this.TAG, "Skipping " + getCaption() + " service " + ((ServiceInfo)localObject1).packageName + "/" + ((ServiceInfo)localObject1).name + ": it does not require the permission " + this.mConfig.bindPermission);
        }
        for (;;)
        {
          paramInt += 1;
          break;
          localArraySet.add(localObject2);
        }
      }
    }
    return localArraySet;
  }
  
  public void registerGuestService(ManagedServiceInfo paramManagedServiceInfo)
  {
    checkNotNull(paramManagedServiceInfo.service);
    if (!checkType(paramManagedServiceInfo.service)) {
      throw new IllegalArgumentException();
    }
    if (registerServiceImpl(paramManagedServiceInfo) != null) {
      onServiceAdded(paramManagedServiceInfo);
    }
  }
  
  public void registerService(IInterface paramIInterface, ComponentName paramComponentName, int paramInt)
  {
    checkNotNull(paramIInterface);
    paramIInterface = registerServiceImpl(paramIInterface, paramComponentName, paramInt);
    if (paramIInterface != null) {
      onServiceAdded(paramIInterface);
    }
  }
  
  public void registerSystemService(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mMutex)
    {
      registerServiceLocked(paramComponentName, paramInt, true);
      return;
    }
  }
  
  public void setComponentState(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (this.mSnoozingForCurrentProfiles.contains(paramComponentName)) {}
    for (boolean bool = false; bool == paramBoolean; bool = true) {
      return;
    }
    if (paramBoolean) {
      this.mSnoozingForCurrentProfiles.remove(paramComponentName);
    }
    for (;;)
    {
      Object localObject2;
      if (this.DEBUG)
      {
        localObject2 = this.TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        if (!paramBoolean) {
          break label167;
        }
        ??? = "Enabling ";
        label71:
        Slog.d((String)localObject2, (String)??? + "component " + paramComponentName.flattenToShortString());
      }
      synchronized (this.mMutex)
      {
        localObject2 = this.mUserProfiles.getCurrentProfileIds();
        int j = localObject2.length;
        int i = 0;
        for (;;)
        {
          if (i < j)
          {
            int k = localObject2[i];
            if (paramBoolean)
            {
              registerServiceLocked(paramComponentName, k);
              i += 1;
              continue;
              this.mSnoozingForCurrentProfiles.add(paramComponentName);
              break;
              label167:
              ??? = "Disabling ";
              break label71;
            }
            unregisterServiceLocked(paramComponentName, k);
          }
        }
      }
    }
  }
  
  public void settingRestored(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (this.DEBUG) {
      Slog.d(this.TAG, "Restored managed service setting: " + paramString1 + " ovalue=" + paramString2 + " nvalue=" + paramString3);
    }
    if (((this.mConfig.secureSettingName.equals(paramString1)) || (this.mConfig.secondarySettingName.equals(paramString1))) && (paramString1 != null))
    {
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), restoredSettingName(paramString1), paramString3, paramInt);
      updateSettingsAccordingToInstalledServices(paramString1, paramInt);
      rebuildRestoredPackages();
    }
  }
  
  public void unregisterService(IInterface paramIInterface, int paramInt)
  {
    checkNotNull(paramIInterface);
    unregisterServiceImpl(paramIInterface, paramInt);
  }
  
  public static class Config
  {
    public String bindPermission;
    public String caption;
    public int clientLabel;
    public String secondarySettingName;
    public String secureSettingName;
    public String serviceInterface;
    public String settingsAction;
  }
  
  public class ManagedServiceInfo
    implements IBinder.DeathRecipient
  {
    public ComponentName component;
    public ServiceConnection connection;
    public boolean isSystem;
    public IInterface service;
    public int targetSdkVersion;
    public int userid;
    
    public ManagedServiceInfo(IInterface paramIInterface, ComponentName paramComponentName, int paramInt1, boolean paramBoolean, ServiceConnection paramServiceConnection, int paramInt2)
    {
      this.service = paramIInterface;
      this.component = paramComponentName;
      this.userid = paramInt1;
      this.isSystem = paramBoolean;
      this.connection = paramServiceConnection;
      this.targetSdkVersion = paramInt2;
    }
    
    public void binderDied()
    {
      if (ManagedServices.this.DEBUG) {
        Slog.d(ManagedServices.this.TAG, "binderDied");
      }
      ManagedServices.-wrap1(ManagedServices.this, this.service, this.userid);
    }
    
    public boolean enabledAndUserMatches(int paramInt)
    {
      boolean bool = false;
      if (!isEnabledForCurrentProfiles()) {
        return false;
      }
      if (this.userid == -1) {
        return true;
      }
      if (this.isSystem) {
        return true;
      }
      if ((paramInt == -1) || (paramInt == this.userid)) {
        return true;
      }
      if (supportsProfiles()) {
        bool = ManagedServices.-get3(ManagedServices.this).isCurrentProfile(paramInt);
      }
      return bool;
    }
    
    public ManagedServices getOwner()
    {
      return ManagedServices.this;
    }
    
    public boolean isEnabledForCurrentProfiles()
    {
      if (this.isSystem) {
        return true;
      }
      if (this.connection == null) {
        return false;
      }
      return ManagedServices.-get1(ManagedServices.this).contains(this.component);
    }
    
    public boolean isGuest(ManagedServices paramManagedServices)
    {
      return ManagedServices.this != paramManagedServices;
    }
    
    public boolean supportsProfiles()
    {
      return this.targetSdkVersion >= 21;
    }
    
    public String toString()
    {
      String str = null;
      StringBuilder localStringBuilder = new StringBuilder("ManagedServiceInfo[").append("component=").append(this.component).append(",userid=").append(this.userid).append(",isSystem=").append(this.isSystem).append(",targetSdkVersion=").append(this.targetSdkVersion).append(",connection=");
      if (this.connection == null) {}
      for (;;)
      {
        return str + ",service=" + this.service + ']';
        str = "<connection>";
      }
    }
  }
  
  class SettingRestoredReceiver
    extends BroadcastReceiver
  {
    SettingRestoredReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.os.action.SETTING_RESTORED".equals(paramIntent.getAction()))
      {
        paramContext = paramIntent.getStringExtra("setting_name");
        if ((Objects.equals(paramContext, ManagedServices.-get0(ManagedServices.this).secureSettingName)) || (Objects.equals(paramContext, ManagedServices.-get0(ManagedServices.this).secondarySettingName)))
        {
          String str = paramIntent.getStringExtra("previous_value");
          paramIntent = paramIntent.getStringExtra("new_value");
          ManagedServices.this.settingRestored(paramContext, str, paramIntent, getSendingUserId());
        }
      }
    }
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    private final Uri mSecondarySettingsUri;
    private final Uri mSecureSettingsUri = Settings.Secure.getUriFor(ManagedServices.-get0(ManagedServices.this).secureSettingName);
    
    private SettingsObserver(Handler paramHandler)
    {
      super();
      if (ManagedServices.-get0(ManagedServices.this).secondarySettingName != null)
      {
        this.mSecondarySettingsUri = Settings.Secure.getUriFor(ManagedServices.-get0(ManagedServices.this).secondarySettingName);
        return;
      }
      this.mSecondarySettingsUri = null;
    }
    
    private void observe()
    {
      ContentResolver localContentResolver = ManagedServices.this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(this.mSecureSettingsUri, false, this, -1);
      if (this.mSecondarySettingsUri != null) {
        localContentResolver.registerContentObserver(this.mSecondarySettingsUri, false, this, -1);
      }
      update(null);
    }
    
    private void update(Uri paramUri)
    {
      if ((paramUri == null) || (this.mSecureSettingsUri.equals(paramUri)) || (paramUri.equals(this.mSecondarySettingsUri)))
      {
        if (ManagedServices.this.DEBUG) {
          Slog.d(ManagedServices.this.TAG, "Setting changed: uri=" + paramUri);
        }
        ManagedServices.-wrap3(ManagedServices.this, false);
        ManagedServices.-wrap4(ManagedServices.this);
      }
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      update(paramUri);
    }
  }
  
  public static class UserProfiles
  {
    private final SparseArray<UserInfo> mCurrentProfiles = new SparseArray();
    
    public int[] getCurrentProfileIds()
    {
      synchronized (this.mCurrentProfiles)
      {
        int[] arrayOfInt = new int[this.mCurrentProfiles.size()];
        int j = this.mCurrentProfiles.size();
        int i = 0;
        while (i < j)
        {
          arrayOfInt[i] = this.mCurrentProfiles.keyAt(i);
          i += 1;
        }
        return arrayOfInt;
      }
    }
    
    public boolean isCurrentProfile(int paramInt)
    {
      synchronized (this.mCurrentProfiles)
      {
        Object localObject1 = this.mCurrentProfiles.get(paramInt);
        if (localObject1 != null)
        {
          bool = true;
          return bool;
        }
        boolean bool = false;
      }
    }
    
    public void updateCache(Context arg1)
    {
      ??? = (UserManager)???.getSystemService("user");
      if (??? != null)
      {
        Object localObject1 = ???.getProfiles(ActivityManager.getCurrentUser());
        synchronized (this.mCurrentProfiles)
        {
          this.mCurrentProfiles.clear();
          localObject1 = ((Iterable)localObject1).iterator();
          if (((Iterator)localObject1).hasNext())
          {
            UserInfo localUserInfo = (UserInfo)((Iterator)localObject1).next();
            this.mCurrentProfiles.put(localUserInfo.id, localUserInfo);
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ManagedServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */