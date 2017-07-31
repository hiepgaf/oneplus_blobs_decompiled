package com.android.server.notification;

import android.app.INotificationManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.IConditionProvider.Stub;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ConditionProviders
  extends ManagedServices
{
  private Callback mCallback;
  private final ArrayList<ConditionRecord> mRecords = new ArrayList();
  private final ArraySet<String> mSystemConditionProviderNames = safeSet(PropConfig.getStringArray(this.mContext, "system.condition.providers", 17236039));
  private final ArraySet<SystemConditionProviderService> mSystemConditionProviders = new ArraySet();
  
  public ConditionProviders(Context paramContext, Handler paramHandler, ManagedServices.UserProfiles paramUserProfiles)
  {
    super(paramContext, paramHandler, new Object(), paramUserProfiles);
  }
  
  private ConditionRecord getRecordLocked(Uri paramUri, ComponentName paramComponentName, boolean paramBoolean)
  {
    if ((paramUri == null) || (paramComponentName == null)) {
      return null;
    }
    int j = this.mRecords.size();
    int i = 0;
    while (i < j)
    {
      ConditionRecord localConditionRecord = (ConditionRecord)this.mRecords.get(i);
      if ((localConditionRecord.id.equals(paramUri)) && (localConditionRecord.component.equals(paramComponentName))) {
        return localConditionRecord;
      }
      i += 1;
    }
    if (paramBoolean)
    {
      paramUri = new ConditionRecord(paramUri, paramComponentName, null);
      this.mRecords.add(paramUri);
      return paramUri;
    }
    return null;
  }
  
  private static IConditionProvider provider(ConditionRecord paramConditionRecord)
  {
    if (paramConditionRecord == null) {
      return null;
    }
    return provider(paramConditionRecord.info);
  }
  
  private static IConditionProvider provider(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    if (paramManagedServiceInfo == null) {
      return null;
    }
    return (IConditionProvider)paramManagedServiceInfo.service;
  }
  
  private Condition[] removeDuplicateConditions(String paramString, Condition[] paramArrayOfCondition)
  {
    if ((paramArrayOfCondition == null) || (paramArrayOfCondition.length == 0)) {
      return null;
    }
    int j = paramArrayOfCondition.length;
    ArrayMap localArrayMap = new ArrayMap(j);
    int i = 0;
    if (i < j)
    {
      Uri localUri = paramArrayOfCondition[i].id;
      if (localArrayMap.containsKey(localUri)) {
        Slog.w(this.TAG, "Ignoring condition from " + paramString + " for duplicate id: " + localUri);
      }
      for (;;)
      {
        i += 1;
        break;
        localArrayMap.put(localUri, paramArrayOfCondition[i]);
      }
    }
    if (localArrayMap.size() == 0) {
      return null;
    }
    if (localArrayMap.size() == j) {
      return paramArrayOfCondition;
    }
    paramString = new Condition[localArrayMap.size()];
    i = 0;
    while (i < paramString.length)
    {
      paramString[i] = ((Condition)localArrayMap.valueAt(i));
      i += 1;
    }
    return paramString;
  }
  
  @SafeVarargs
  private static <T> ArraySet<T> safeSet(T... paramVarArgs)
  {
    ArraySet localArraySet = new ArraySet();
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      return localArraySet;
    }
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      T ? = paramVarArgs[i];
      if (? != null) {
        localArraySet.add(?);
      }
      i += 1;
    }
    return localArraySet;
  }
  
  private void subscribeLocked(ConditionRecord paramConditionRecord)
  {
    Uri localUri = null;
    if (this.DEBUG) {
      Slog.d(this.TAG, "subscribeLocked " + paramConditionRecord);
    }
    IConditionProvider localIConditionProvider = provider(paramConditionRecord);
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (localIConditionProvider != null) {}
    try
    {
      Slog.d(this.TAG, "Subscribing to " + paramConditionRecord.id + " with " + paramConditionRecord.component);
      localIConditionProvider.onSubscribe(paramConditionRecord.id);
      paramConditionRecord.subscribed = true;
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w(this.TAG, "Error subscribing to " + paramConditionRecord, localRemoteException);
      }
    }
    if (paramConditionRecord != null) {
      localUri = paramConditionRecord.id;
    }
    ZenLog.traceSubscribe(localUri, localIConditionProvider, (RemoteException)localObject1);
  }
  
  private void unsubscribeLocked(ConditionRecord paramConditionRecord)
  {
    Uri localUri = null;
    if (this.DEBUG) {
      Slog.d(this.TAG, "unsubscribeLocked " + paramConditionRecord);
    }
    IConditionProvider localIConditionProvider = provider(paramConditionRecord);
    Object localObject1 = null;
    Object localObject2 = null;
    if (localIConditionProvider != null) {}
    try
    {
      localIConditionProvider.onUnsubscribe(paramConditionRecord.id);
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w(this.TAG, "Error unsubscribing to " + paramConditionRecord, localRemoteException);
      }
    }
    paramConditionRecord.subscribed = false;
    if (paramConditionRecord != null) {
      localUri = paramConditionRecord.id;
    }
    ZenLog.traceUnsubscribe(localUri, localIConditionProvider, (RemoteException)localObject1);
  }
  
  public void addSystemProvider(SystemConditionProviderService paramSystemConditionProviderService)
  {
    this.mSystemConditionProviders.add(paramSystemConditionProviderService);
    paramSystemConditionProviderService.attachBase(this.mContext);
    registerService(paramSystemConditionProviderService.asInterface(), paramSystemConditionProviderService.getComponent(), 0);
  }
  
  protected IInterface asInterface(IBinder paramIBinder)
  {
    return IConditionProvider.Stub.asInterface(paramIBinder);
  }
  
  public ManagedServices.ManagedServiceInfo checkServiceToken(IConditionProvider paramIConditionProvider)
  {
    synchronized (this.mMutex)
    {
      paramIConditionProvider = checkServiceTokenLocked(paramIConditionProvider);
      return paramIConditionProvider;
    }
  }
  
  protected boolean checkType(IInterface paramIInterface)
  {
    return paramIInterface instanceof IConditionProvider;
  }
  
  public void dump(PrintWriter paramPrintWriter, NotificationManagerService.DumpFilter paramDumpFilter)
  {
    super.dump(paramPrintWriter, paramDumpFilter);
    synchronized (this.mMutex)
    {
      paramPrintWriter.print("    mRecords(");
      paramPrintWriter.print(this.mRecords.size());
      paramPrintWriter.println("):");
      int i = 0;
      while (i < this.mRecords.size())
      {
        Object localObject2 = (ConditionRecord)this.mRecords.get(i);
        if ((paramDumpFilter == null) || (paramDumpFilter.matches(((ConditionRecord)localObject2).component)))
        {
          paramPrintWriter.print("      ");
          paramPrintWriter.println(localObject2);
          localObject2 = CountdownConditionProvider.tryParseDescription(((ConditionRecord)localObject2).id);
          if (localObject2 != null)
          {
            paramPrintWriter.print("        (");
            paramPrintWriter.print((String)localObject2);
            paramPrintWriter.println(")");
          }
        }
        i += 1;
      }
      paramPrintWriter.print("    mSystemConditionProviders: ");
      paramPrintWriter.println(this.mSystemConditionProviderNames);
      i = 0;
      if (i < this.mSystemConditionProviders.size())
      {
        ((SystemConditionProviderService)this.mSystemConditionProviders.valueAt(i)).dump(paramPrintWriter, paramDumpFilter);
        i += 1;
      }
    }
  }
  
  public void ensureRecordExists(ComponentName paramComponentName, Uri paramUri, IConditionProvider paramIConditionProvider)
  {
    paramComponentName = getRecordLocked(paramUri, paramComponentName, true);
    if (paramComponentName.info == null) {
      paramComponentName.info = checkServiceTokenLocked(paramIConditionProvider);
    }
  }
  
  public Condition findCondition(ComponentName paramComponentName, Uri paramUri)
  {
    Object localObject1 = null;
    if ((paramComponentName == null) || (paramUri == null)) {
      return null;
    }
    synchronized (this.mMutex)
    {
      paramUri = getRecordLocked(paramUri, paramComponentName, false);
      paramComponentName = (ComponentName)localObject1;
      if (paramUri != null) {
        paramComponentName = paramUri.condition;
      }
      return paramComponentName;
    }
  }
  
  public IConditionProvider findConditionProvider(ComponentName paramComponentName)
  {
    if (paramComponentName == null) {
      return null;
    }
    Iterator localIterator = this.mServices.iterator();
    while (localIterator.hasNext())
    {
      ManagedServices.ManagedServiceInfo localManagedServiceInfo = (ManagedServices.ManagedServiceInfo)localIterator.next();
      if (paramComponentName.equals(localManagedServiceInfo.component)) {
        return provider(localManagedServiceInfo);
      }
    }
    return null;
  }
  
  protected ManagedServices.Config getConfig()
  {
    ManagedServices.Config localConfig = new ManagedServices.Config();
    localConfig.caption = "condition provider";
    localConfig.serviceInterface = "android.service.notification.ConditionProviderService";
    localConfig.secureSettingName = "enabled_notification_policy_access_packages";
    localConfig.secondarySettingName = "enabled_notification_listeners";
    localConfig.bindPermission = "android.permission.BIND_CONDITION_PROVIDER_SERVICE";
    localConfig.settingsAction = "android.settings.ACTION_CONDITION_PROVIDER_SETTINGS";
    localConfig.clientLabel = 17040519;
    return localConfig;
  }
  
  public Iterable<SystemConditionProviderService> getSystemProviders()
  {
    return this.mSystemConditionProviders;
  }
  
  public boolean isSystemProviderEnabled(String paramString)
  {
    return this.mSystemConditionProviderNames.contains(paramString);
  }
  
  protected ArraySet<ComponentName> loadComponentNamesFromSetting(String paramString, int paramInt)
  {
    paramString = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), paramString, paramInt);
    if (TextUtils.isEmpty(paramString)) {
      return new ArraySet();
    }
    paramString = paramString.split(":");
    ArraySet localArraySet = new ArraySet(paramString.length);
    int i = 0;
    if (i < paramString.length)
    {
      if (!TextUtils.isEmpty(paramString[i]))
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(paramString[i]);
        if (localComponentName == null) {
          break label100;
        }
        localArraySet.addAll(queryPackageForServices(localComponentName.getPackageName(), paramInt));
      }
      for (;;)
      {
        i += 1;
        break;
        label100:
        localArraySet.addAll(queryPackageForServices(paramString[i], paramInt));
      }
    }
    return localArraySet;
  }
  
  public void notifyConditions(String paramString, ManagedServices.ManagedServiceInfo paramManagedServiceInfo, Condition[] paramArrayOfCondition)
  {
    Object localObject1 = null;
    synchronized (this.mMutex)
    {
      String str;
      StringBuilder localStringBuilder;
      if (this.DEBUG)
      {
        str = this.TAG;
        localStringBuilder = new StringBuilder().append("notifyConditions pkg=").append(paramString).append(" info=").append(paramManagedServiceInfo).append(" conditions=");
        if (paramArrayOfCondition != null) {
          break label104;
        }
      }
      for (;;)
      {
        Slog.d(str, localObject1);
        paramString = removeDuplicateConditions(paramString, paramArrayOfCondition);
        if (paramString != null)
        {
          i = paramString.length;
          if (i != 0) {
            break;
          }
        }
        return;
        label104:
        localObject1 = Arrays.asList(paramArrayOfCondition);
      }
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        paramArrayOfCondition = paramString[i];
        localObject1 = getRecordLocked(paramArrayOfCondition.id, paramManagedServiceInfo.component, true);
        ((ConditionRecord)localObject1).info = paramManagedServiceInfo;
        ((ConditionRecord)localObject1).condition = paramArrayOfCondition;
        i += 1;
      }
      j = paramString.length;
      i = 0;
      if (i < j)
      {
        paramManagedServiceInfo = paramString[i];
        if (this.mCallback != null) {
          this.mCallback.onConditionChanged(paramManagedServiceInfo.id, paramManagedServiceInfo);
        }
        i += 1;
      }
    }
  }
  
  public void onBootPhaseAppsCanStart()
  {
    super.onBootPhaseAppsCanStart();
    int i = 0;
    while (i < this.mSystemConditionProviders.size())
    {
      ((SystemConditionProviderService)this.mSystemConditionProviders.valueAt(i)).onBootComplete();
      i += 1;
    }
    if (this.mCallback != null) {
      this.mCallback.onBootComplete();
    }
  }
  
  public void onPackagesChanged(boolean paramBoolean, String[] paramArrayOfString)
  {
    int i = 0;
    if (paramBoolean)
    {
      INotificationManager localINotificationManager = NotificationManager.getService();
      if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
      {
        int j = paramArrayOfString.length;
        for (;;)
        {
          if (i < j)
          {
            String str = paramArrayOfString[i];
            try
            {
              localINotificationManager.removeAutomaticZenRules(str);
              localINotificationManager.setNotificationPolicyAccessGranted(str, false);
              i += 1;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                Slog.e(this.TAG, "Failed to clean up rules for " + str, localException);
              }
            }
          }
        }
      }
    }
    super.onPackagesChanged(paramBoolean, paramArrayOfString);
  }
  
  protected void onServiceAdded(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    IConditionProvider localIConditionProvider = provider(paramManagedServiceInfo);
    try
    {
      localIConditionProvider.onConnected();
      if (this.mCallback != null) {
        this.mCallback.onServiceAdded(paramManagedServiceInfo.component);
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  protected void onServiceRemovedLocked(ManagedServices.ManagedServiceInfo paramManagedServiceInfo)
  {
    if (paramManagedServiceInfo == null) {
      return;
    }
    int i = this.mRecords.size() - 1;
    if (i >= 0)
    {
      if (!((ConditionRecord)this.mRecords.get(i)).component.equals(paramManagedServiceInfo.component)) {}
      for (;;)
      {
        i -= 1;
        break;
        this.mRecords.remove(i);
      }
    }
  }
  
  public void onUserSwitched(int paramInt)
  {
    super.onUserSwitched(paramInt);
    if (this.mCallback != null) {
      this.mCallback.onUserSwitched();
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public boolean subscribeIfNecessary(ComponentName paramComponentName, Uri paramUri)
  {
    synchronized (this.mMutex)
    {
      ConditionRecord localConditionRecord = getRecordLocked(paramUri, paramComponentName, false);
      if (localConditionRecord == null)
      {
        Slog.w(this.TAG, "Unable to subscribe to " + paramComponentName + " " + paramUri);
        return false;
      }
      boolean bool = localConditionRecord.subscribed;
      if (bool) {
        return true;
      }
      subscribeLocked(localConditionRecord);
      bool = localConditionRecord.subscribed;
      return bool;
    }
  }
  
  public void unsubscribeIfNecessary(ComponentName paramComponentName, Uri paramUri)
  {
    synchronized (this.mMutex)
    {
      ConditionRecord localConditionRecord = getRecordLocked(paramUri, paramComponentName, false);
      if (localConditionRecord == null)
      {
        Slog.w(this.TAG, "Unable to unsubscribe to " + paramComponentName + " " + paramUri);
        return;
      }
      boolean bool = localConditionRecord.subscribed;
      if (!bool) {
        return;
      }
      unsubscribeLocked(localConditionRecord);
      return;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onBootComplete();
    
    public abstract void onConditionChanged(Uri paramUri, Condition paramCondition);
    
    public abstract void onServiceAdded(ComponentName paramComponentName);
    
    public abstract void onUserSwitched();
  }
  
  private static class ConditionRecord
  {
    public final ComponentName component;
    public Condition condition;
    public final Uri id;
    public ManagedServices.ManagedServiceInfo info;
    public boolean subscribed;
    
    private ConditionRecord(Uri paramUri, ComponentName paramComponentName)
    {
      this.id = paramUri;
      this.component = paramComponentName;
    }
    
    public String toString()
    {
      return "ConditionRecord[id=" + this.id + ",component=" + this.component + ",subscribed=" + this.subscribed + ']';
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ConditionProviders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */