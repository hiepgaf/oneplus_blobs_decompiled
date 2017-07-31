package com.android.server.print;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.print.IPrintDocumentAdapter;
import android.print.IPrintJobStateChangeListener;
import android.print.IPrintServicesChangeListener;
import android.print.IPrinterDiscoveryObserver;
import android.print.IPrinterDiscoveryObserver.Stub;
import android.print.PrintAttributes;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.print.PrinterInfo.Builder;
import android.printservice.PrintServiceInfo;
import android.printservice.recommendation.IRecommendationsChangeListener;
import android.printservice.recommendation.RecommendationInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

final class UserState
  implements RemotePrintSpooler.PrintSpoolerCallbacks, RemotePrintService.PrintServiceCallbacks, RemotePrintServiceRecommendationService.RemotePrintServiceRecommendationServiceCallbacks
{
  private static final char COMPONENT_NAME_SEPARATOR = ':';
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "UserState";
  private final ArrayMap<ComponentName, RemotePrintService> mActiveServices = new ArrayMap();
  private final Context mContext;
  private boolean mDestroyed;
  private final Set<ComponentName> mDisabledServices = new ArraySet();
  private final Handler mHandler;
  private final List<PrintServiceInfo> mInstalledServices = new ArrayList();
  private final Object mLock;
  private final PrintJobForAppCache mPrintJobForAppCache = new PrintJobForAppCache(null);
  private List<PrintJobStateChangeListenerRecord> mPrintJobStateChangeListenerRecords;
  private List<RecommendationInfo> mPrintServiceRecommendations;
  private List<ListenerRecord<IRecommendationsChangeListener>> mPrintServiceRecommendationsChangeListenerRecords;
  private RemotePrintServiceRecommendationService mPrintServiceRecommendationsService;
  private List<ListenerRecord<IPrintServicesChangeListener>> mPrintServicesChangeListenerRecords;
  private PrinterDiscoverySessionMediator mPrinterDiscoverySession;
  private final Intent mQueryIntent = new Intent("android.printservice.PrintService");
  private final RemotePrintSpooler mSpooler;
  private final TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
  private final int mUserId;
  
  public UserState(Context arg1, int paramInt, Object paramObject, boolean paramBoolean)
  {
    this.mContext = ???;
    this.mUserId = paramInt;
    this.mLock = paramObject;
    this.mSpooler = new RemotePrintSpooler(???, paramInt, paramBoolean, this);
    this.mHandler = new UserStateHandler(???.getMainLooper());
    synchronized (this.mLock)
    {
      readInstalledPrintServicesLocked();
      upgradePersistentStateIfNeeded();
      readDisabledPrintServicesLocked();
      prunePrintServices();
      onConfigurationChangedLocked();
      return;
    }
  }
  
  private void addServiceLocked(RemotePrintService paramRemotePrintService)
  {
    this.mActiveServices.put(paramRemotePrintService.getComponentName(), paramRemotePrintService);
    if (this.mPrinterDiscoverySession != null) {
      this.mPrinterDiscoverySession.onServiceAddedLocked(paramRemotePrintService);
    }
  }
  
  private void failActivePrintJobsForService(final ComponentName paramComponentName)
  {
    if (Looper.getMainLooper().isCurrentThread())
    {
      BackgroundThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          UserState.-wrap0(UserState.this, paramComponentName);
        }
      });
      return;
    }
    failScheduledPrintJobsForServiceInternal(paramComponentName);
  }
  
  private void failScheduledPrintJobsForServiceInternal(ComponentName paramComponentName)
  {
    paramComponentName = this.mSpooler.getPrintJobInfos(paramComponentName, -4, -2);
    if (paramComponentName == null) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      int j = paramComponentName.size();
      int i = 0;
      while (i < j)
      {
        PrintJobInfo localPrintJobInfo = (PrintJobInfo)paramComponentName.get(i);
        this.mSpooler.setPrintJobState(localPrintJobInfo.getId(), 6, this.mContext.getString(17040804));
        i += 1;
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private ArrayList<ComponentName> getInstalledComponents()
  {
    ArrayList localArrayList = new ArrayList();
    int j = this.mInstalledServices.size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = ((PrintServiceInfo)this.mInstalledServices.get(i)).getResolveInfo();
      localArrayList.add(new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name));
      i += 1;
    }
    return localArrayList;
  }
  
  private void handleDispatchPrintJobStateChanged(PrintJobId paramPrintJobId, int paramInt)
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        Object localObject2 = this.mPrintJobStateChangeListenerRecords;
        if (localObject2 == null) {
          return;
        }
        localObject2 = new ArrayList(this.mPrintJobStateChangeListenerRecords);
        int j = ((List)localObject2).size();
        i = 0;
        if (i >= j) {
          return;
        }
        ??? = (PrintJobStateChangeListenerRecord)((List)localObject2).get(i);
        if (((PrintJobStateChangeListenerRecord)???).appId != -2) {
          if (((PrintJobStateChangeListenerRecord)???).appId != paramInt) {
            break label100;
          }
        }
      }
      try
      {
        ((PrintJobStateChangeListenerRecord)???).listener.onPrintJobStateChanged(paramPrintJobId);
        label100:
        i += 1;
        continue;
        paramPrintJobId = finally;
        throw paramPrintJobId;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("UserState", "Error notifying for print job state change", localRemoteException);
        }
      }
    }
  }
  
  private void handleDispatchPrintServiceRecommendationsUpdated(List<RecommendationInfo> paramList)
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        Object localObject2 = this.mPrintServiceRecommendationsChangeListenerRecords;
        if (localObject2 == null) {
          return;
        }
        localObject2 = new ArrayList(this.mPrintServiceRecommendationsChangeListenerRecords);
        this.mPrintServiceRecommendations = paramList;
        int j = ((List)localObject2).size();
        i = 0;
        if (i >= j) {
          return;
        }
        paramList = (ListenerRecord)((List)localObject2).get(i);
      }
      try
      {
        ((IRecommendationsChangeListener)paramList.listener).onRecommendationsChanged();
        i += 1;
        continue;
        paramList = finally;
        throw paramList;
      }
      catch (RemoteException paramList)
      {
        for (;;)
        {
          Log.e("UserState", "Error notifying for print service recommendations change", paramList);
        }
      }
    }
  }
  
  private void handleDispatchPrintServicesChanged()
  {
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        Object localObject2 = this.mPrintServicesChangeListenerRecords;
        if (localObject2 == null) {
          return;
        }
        localObject2 = new ArrayList(this.mPrintServicesChangeListenerRecords);
        int j = ((List)localObject2).size();
        i = 0;
        if (i >= j) {
          return;
        }
        ??? = (ListenerRecord)((List)localObject2).get(i);
      }
      try
      {
        ((IPrintServicesChangeListener)((ListenerRecord)???).listener).onPrintServicesChanged();
        i += 1;
        continue;
        localObject3 = finally;
        throw ((Throwable)localObject3);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("UserState", "Error notifying for print services change", localRemoteException);
        }
      }
    }
  }
  
  private void onConfigurationChangedLocked()
  {
    ArrayList localArrayList = getInstalledComponents();
    int j = localArrayList.size();
    int i = 0;
    if (i < j)
    {
      localObject1 = (ComponentName)localArrayList.get(i);
      if (!this.mDisabledServices.contains(localObject1)) {
        if (!this.mActiveServices.containsKey(localObject1)) {
          addServiceLocked(new RemotePrintService(this.mContext, (ComponentName)localObject1, this.mUserId, this.mSpooler, this));
        }
      }
      for (;;)
      {
        i += 1;
        break;
        localObject1 = (RemotePrintService)this.mActiveServices.remove(localObject1);
        if (localObject1 != null) {
          removeServiceLocked((RemotePrintService)localObject1);
        }
      }
    }
    Object localObject1 = this.mActiveServices.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (Map.Entry)((Iterator)localObject1).next();
      ComponentName localComponentName = (ComponentName)((Map.Entry)localObject2).getKey();
      localObject2 = (RemotePrintService)((Map.Entry)localObject2).getValue();
      if (!localArrayList.contains(localComponentName))
      {
        removeServiceLocked((RemotePrintService)localObject2);
        ((Iterator)localObject1).remove();
      }
    }
    onPrintServicesChanged();
  }
  
  private void readConfigurationLocked()
  {
    readInstalledPrintServicesLocked();
    readDisabledPrintServicesLocked();
  }
  
  private void readDisabledPrintServicesLocked()
  {
    HashSet localHashSet = new HashSet();
    readPrintServicesFromSettingLocked("disabled_print_services", localHashSet);
    if (!localHashSet.equals(this.mDisabledServices))
    {
      this.mDisabledServices.clear();
      this.mDisabledServices.addAll(localHashSet);
    }
  }
  
  private void readInstalledPrintServicesLocked()
  {
    HashSet localHashSet = new HashSet();
    List localList = this.mContext.getPackageManager().queryIntentServicesAsUser(this.mQueryIntent, 268435588, this.mUserId);
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      Object localObject = (ResolveInfo)localList.get(i);
      if (!"android.permission.BIND_PRINT_SERVICE".equals(((ResolveInfo)localObject).serviceInfo.permission))
      {
        localObject = new ComponentName(((ResolveInfo)localObject).serviceInfo.packageName, ((ResolveInfo)localObject).serviceInfo.name);
        Slog.w("UserState", "Skipping print service " + ((ComponentName)localObject).flattenToShortString() + " since it does not require permission " + "android.permission.BIND_PRINT_SERVICE");
      }
      for (;;)
      {
        i += 1;
        break;
        localHashSet.add(PrintServiceInfo.create((ResolveInfo)localObject, this.mContext));
      }
    }
    this.mInstalledServices.clear();
    this.mInstalledServices.addAll(localHashSet);
  }
  
  private void readPrintServicesFromSettingLocked(String paramString, Set<ComponentName> paramSet)
  {
    Object localObject = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), paramString, this.mUserId);
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      paramString = this.mStringColonSplitter;
      paramString.setString((String)localObject);
      while (paramString.hasNext())
      {
        localObject = paramString.next();
        if (!TextUtils.isEmpty((CharSequence)localObject))
        {
          localObject = ComponentName.unflattenFromString((String)localObject);
          if (localObject != null) {
            paramSet.add(localObject);
          }
        }
      }
    }
  }
  
  private void removeServiceLocked(RemotePrintService paramRemotePrintService)
  {
    failActivePrintJobsForService(paramRemotePrintService.getComponentName());
    if (this.mPrinterDiscoverySession != null)
    {
      this.mPrinterDiscoverySession.onServiceRemovedLocked(paramRemotePrintService);
      return;
    }
    paramRemotePrintService.destroy();
  }
  
  private void throwIfDestroyedLocked()
  {
    if (this.mDestroyed) {
      throw new IllegalStateException("Cannot interact with a destroyed instance.");
    }
  }
  
  private void upgradePersistentStateIfNeeded()
  {
    if (Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "enabled_print_services", this.mUserId) != null)
    {
      HashSet localHashSet = new HashSet();
      readPrintServicesFromSettingLocked("enabled_print_services", localHashSet);
      ArraySet localArraySet = new ArraySet();
      int j = this.mInstalledServices.size();
      int i = 0;
      while (i < j)
      {
        ComponentName localComponentName = ((PrintServiceInfo)this.mInstalledServices.get(i)).getComponentName();
        if (!localHashSet.contains(localComponentName)) {
          localArraySet.add(localComponentName);
        }
        i += 1;
      }
      writeDisabledPrintServicesLocked(localArraySet);
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "enabled_print_services", null, this.mUserId);
    }
  }
  
  private void writeDisabledPrintServicesLocked(Set<ComponentName> paramSet)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramSet = paramSet.iterator();
    while (paramSet.hasNext())
    {
      ComponentName localComponentName = (ComponentName)paramSet.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(':');
      }
      localStringBuilder.append(localComponentName.flattenToShortString());
    }
    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "disabled_print_services", localStringBuilder.toString(), this.mUserId);
  }
  
  public void addPrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      if (this.mPrintJobStateChangeListenerRecords == null) {
        this.mPrintJobStateChangeListenerRecords = new ArrayList();
      }
      this.mPrintJobStateChangeListenerRecords.add(new PrintJobStateChangeListenerRecord(this, paramIPrintJobStateChangeListener, paramInt)
      {
        public void onBinderDied()
        {
          synchronized (UserState.-get2(jdField_this))
          {
            if (UserState.-get3(jdField_this) != null) {
              UserState.-get3(jdField_this).remove(this);
            }
            return;
          }
        }
      });
      return;
    }
  }
  
  public void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      if (this.mPrintServiceRecommendationsChangeListenerRecords == null)
      {
        this.mPrintServiceRecommendationsChangeListenerRecords = new ArrayList();
        this.mPrintServiceRecommendationsService = new RemotePrintServiceRecommendationService(this.mContext, UserHandle.getUserHandleForUid(this.mUserId), this);
      }
      this.mPrintServiceRecommendationsChangeListenerRecords.add(new ListenerRecord(this, paramIRecommendationsChangeListener)
      {
        public void onBinderDied()
        {
          synchronized (UserState.-get2(jdField_this))
          {
            if (UserState.-get4(jdField_this) != null) {
              UserState.-get4(jdField_this).remove(this);
            }
            return;
          }
        }
      });
      return;
    }
  }
  
  public void addPrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener)
    throws RemoteException
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      if (this.mPrintServicesChangeListenerRecords == null) {
        this.mPrintServicesChangeListenerRecords = new ArrayList();
      }
      this.mPrintServicesChangeListenerRecords.add(new ListenerRecord(this, paramIPrintServicesChangeListener)
      {
        public void onBinderDied()
        {
          synchronized (UserState.-get2(jdField_this))
          {
            if (UserState.-get5(jdField_this) != null) {
              UserState.-get5(jdField_this).remove(this);
            }
            return;
          }
        }
      });
      return;
    }
  }
  
  public void cancelPrintJob(PrintJobId arg1, int paramInt)
  {
    PrintJobInfo localPrintJobInfo1 = this.mSpooler.getPrintJobInfo(???, paramInt);
    if (localPrintJobInfo1 == null) {
      return;
    }
    this.mSpooler.setPrintJobCancelling(???, true);
    if (localPrintJobInfo1.getState() != 6)
    {
      ??? = localPrintJobInfo1.getPrinterId();
      if (??? != null)
      {
        Object localObject = ???.getServiceName();
        synchronized (this.mLock)
        {
          localObject = (RemotePrintService)this.mActiveServices.get(localObject);
          if (localObject == null) {
            return;
          }
        }
        ((RemotePrintService)localObject).onRequestCancelPrintJob(localPrintJobInfo2);
      }
      return;
    }
    this.mSpooler.setPrintJobState(???, 7, null);
  }
  
  public void createPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
  {
    this.mSpooler.clearCustomPrinterIconCache();
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      if (this.mPrinterDiscoverySession == null)
      {
        this.mPrinterDiscoverySession = new PrinterDiscoverySessionMediator(this, this.mContext)
        {
          public void onDestroyed()
          {
            UserState.-set0(jdField_this, null);
          }
        };
        this.mPrinterDiscoverySession.addObserverLocked(paramIPrinterDiscoveryObserver);
        return;
      }
      this.mPrinterDiscoverySession.addObserverLocked(paramIPrinterDiscoveryObserver);
    }
  }
  
  public void destroyLocked()
  {
    throwIfDestroyedLocked();
    this.mSpooler.destroy();
    Iterator localIterator = this.mActiveServices.values().iterator();
    while (localIterator.hasNext()) {
      ((RemotePrintService)localIterator.next()).destroy();
    }
    this.mActiveServices.clear();
    this.mInstalledServices.clear();
    this.mDisabledServices.clear();
    if (this.mPrinterDiscoverySession != null)
    {
      this.mPrinterDiscoverySession.destroyLocked();
      this.mPrinterDiscoverySession = null;
    }
    this.mDestroyed = true;
  }
  
  public void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
  {
    synchronized (this.mLock)
    {
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.removeObserverLocked(paramIPrinterDiscoveryObserver);
      return;
    }
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String paramString)
  {
    paramPrintWriter.append(paramString).append("user state ").append(String.valueOf(this.mUserId)).append(":");
    paramPrintWriter.println();
    paramPrintWriter.append(paramString).append("  ").append("installed services:").println();
    int j = this.mInstalledServices.size();
    int i = 0;
    Object localObject2;
    Object localObject3;
    while (i < j)
    {
      localObject1 = (PrintServiceInfo)this.mInstalledServices.get(i);
      localObject2 = paramString + "  " + "  ";
      paramPrintWriter.append((CharSequence)localObject2).append("service:").println();
      localObject3 = ((PrintServiceInfo)localObject1).getResolveInfo();
      localObject3 = new ComponentName(((ResolveInfo)localObject3).serviceInfo.packageName, ((ResolveInfo)localObject3).serviceInfo.name);
      paramPrintWriter.append((CharSequence)localObject2).append("  ").append("componentName=").append(((ComponentName)localObject3).flattenToString()).println();
      paramPrintWriter.append((CharSequence)localObject2).append("  ").append("settingsActivity=").append(((PrintServiceInfo)localObject1).getSettingsActivityName()).println();
      paramPrintWriter.append((CharSequence)localObject2).append("  ").append("addPrintersActivity=").append(((PrintServiceInfo)localObject1).getAddPrintersActivityName()).println();
      paramPrintWriter.append((CharSequence)localObject2).append("  ").append("avancedOptionsActivity=").append(((PrintServiceInfo)localObject1).getAdvancedOptionsActivityName()).println();
      i += 1;
    }
    paramPrintWriter.append(paramString).append("  ").append("disabled services:").println();
    Object localObject1 = this.mDisabledServices.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ComponentName)((Iterator)localObject1).next();
      localObject3 = paramString + "  " + "  ";
      paramPrintWriter.append((CharSequence)localObject3).append("service:").println();
      paramPrintWriter.append((CharSequence)localObject3).append("  ").append("componentName=").append(((ComponentName)localObject2).flattenToString());
      paramPrintWriter.println();
    }
    paramPrintWriter.append(paramString).append("  ").append("active services:").println();
    j = this.mActiveServices.size();
    i = 0;
    while (i < j)
    {
      ((RemotePrintService)this.mActiveServices.valueAt(i)).dump(paramPrintWriter, paramString + "  " + "  ");
      paramPrintWriter.println();
      i += 1;
    }
    paramPrintWriter.append(paramString).append("  ").append("cached print jobs:").println();
    this.mPrintJobForAppCache.dump(paramPrintWriter, paramString + "  " + "  ");
    paramPrintWriter.append(paramString).append("  ").append("discovery mediator:").println();
    if (this.mPrinterDiscoverySession != null) {
      this.mPrinterDiscoverySession.dump(paramPrintWriter, paramString + "  " + "  ");
    }
    paramPrintWriter.append(paramString).append("  ").append("print spooler:").println();
    this.mSpooler.dump(paramFileDescriptor, paramPrintWriter, paramString + "  " + "  ");
    paramPrintWriter.println();
  }
  
  public Icon getCustomPrinterIcon(PrinterId paramPrinterId)
  {
    Icon localIcon = this.mSpooler.getCustomPrinterIcon(paramPrinterId);
    if (localIcon == null)
    {
      RemotePrintService localRemotePrintService = (RemotePrintService)this.mActiveServices.get(paramPrinterId.getServiceName());
      if (localRemotePrintService != null) {
        localRemotePrintService.requestCustomPrinterIcon(paramPrinterId);
      }
    }
    return localIcon;
  }
  
  public PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId, int paramInt)
  {
    PrintJobInfo localPrintJobInfo2 = this.mPrintJobForAppCache.getPrintJob(paramPrintJobId, paramInt);
    PrintJobInfo localPrintJobInfo1 = localPrintJobInfo2;
    if (localPrintJobInfo2 == null) {
      localPrintJobInfo1 = this.mSpooler.getPrintJobInfo(paramPrintJobId, paramInt);
    }
    if (localPrintJobInfo1 != null)
    {
      localPrintJobInfo1.setTag(null);
      localPrintJobInfo1.setAdvancedOptions(null);
    }
    return localPrintJobInfo1;
  }
  
  public List<PrintJobInfo> getPrintJobInfos(int paramInt)
  {
    List localList = this.mPrintJobForAppCache.getPrintJobs(paramInt);
    ArrayMap localArrayMap = new ArrayMap();
    int j = localList.size();
    int i = 0;
    PrintJobInfo localPrintJobInfo;
    while (i < j)
    {
      localPrintJobInfo = (PrintJobInfo)localList.get(i);
      localArrayMap.put(localPrintJobInfo.getId(), localPrintJobInfo);
      localPrintJobInfo.setTag(null);
      localPrintJobInfo.setAdvancedOptions(null);
      i += 1;
    }
    localList = this.mSpooler.getPrintJobInfos(null, -1, paramInt);
    if (localList != null)
    {
      i = localList.size();
      paramInt = 0;
      while (paramInt < i)
      {
        localPrintJobInfo = (PrintJobInfo)localList.get(paramInt);
        localArrayMap.put(localPrintJobInfo.getId(), localPrintJobInfo);
        localPrintJobInfo.setTag(null);
        localPrintJobInfo.setAdvancedOptions(null);
        paramInt += 1;
      }
    }
    return new ArrayList(localArrayMap.values());
  }
  
  public List<RecommendationInfo> getPrintServiceRecommendations()
  {
    return this.mPrintServiceRecommendations;
  }
  
  public List<PrintServiceInfo> getPrintServices(int paramInt)
  {
    int i;
    synchronized (this.mLock)
    {
      int j = this.mInstalledServices.size();
      i = 0;
      ArrayList localArrayList = null;
      if (i < j) {}
      try
      {
        localPrintServiceInfo = (PrintServiceInfo)this.mInstalledServices.get(i);
        ComponentName localComponentName = new ComponentName(localPrintServiceInfo.getResolveInfo().serviceInfo.packageName, localPrintServiceInfo.getResolveInfo().serviceInfo.name);
        localPrintServiceInfo.setIsEnabled(this.mActiveServices.containsKey(localComponentName));
        if (!localPrintServiceInfo.isEnabled()) {
          break label162;
        }
        if ((paramInt & 0x1) == 0) {
          break label155;
        }
        label106:
        if (localArrayList != null) {
          break label152;
        }
        localArrayList = new ArrayList();
      }
      finally
      {
        for (;;)
        {
          PrintServiceInfo localPrintServiceInfo;
        }
      }
      localArrayList.add(localPrintServiceInfo);
    }
    return localList;
    for (;;)
    {
      label152:
      label155:
      i += 1;
      break;
      label162:
      if ((paramInt & 0x2) != 0) {
        break label106;
      }
    }
  }
  
  public void increasePriority()
  {
    this.mSpooler.increasePriority();
  }
  
  public void onAllPrintJobsForServiceHandled(ComponentName paramComponentName)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      paramComponentName = (RemotePrintService)this.mActiveServices.get(paramComponentName);
      if (paramComponentName != null) {
        paramComponentName.onAllPrintJobsHandled();
      }
      return;
    }
  }
  
  public void onCustomPrinterIconLoaded(PrinterId paramPrinterId, Icon arg2)
  {
    this.mSpooler.onCustomPrinterIconLoaded(paramPrinterId, ???);
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.onCustomPrinterIconLoadedLocked(paramPrinterId);
      return;
    }
  }
  
  public void onPrintJobQueued(PrintJobInfo paramPrintJobInfo)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      Object localObject2 = paramPrintJobInfo.getPrinterId().getServiceName();
      localObject2 = (RemotePrintService)this.mActiveServices.get(localObject2);
      if (localObject2 != null)
      {
        ((RemotePrintService)localObject2).onPrintJobQueued(paramPrintJobInfo);
        return;
      }
    }
    this.mSpooler.setPrintJobState(paramPrintJobInfo.getId(), 6, this.mContext.getString(17040804));
  }
  
  public void onPrintJobStateChanged(PrintJobInfo paramPrintJobInfo)
  {
    this.mPrintJobForAppCache.onPrintJobStateChanged(paramPrintJobInfo);
    this.mHandler.obtainMessage(1, paramPrintJobInfo.getAppId(), 0, paramPrintJobInfo.getId()).sendToTarget();
  }
  
  public void onPrintServiceRecommendationsUpdated(List<RecommendationInfo> paramList)
  {
    this.mHandler.obtainMessage(3, 0, 0, paramList).sendToTarget();
  }
  
  public void onPrintServicesChanged()
  {
    this.mHandler.obtainMessage(2).sendToTarget();
  }
  
  public void onPrintersAdded(List<PrinterInfo> paramList)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.onPrintersAddedLocked(paramList);
      return;
    }
  }
  
  public void onPrintersRemoved(List<PrinterId> paramList)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.onPrintersRemovedLocked(paramList);
      return;
    }
  }
  
  public void onServiceDied(RemotePrintService paramRemotePrintService)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      failActivePrintJobsForService(paramRemotePrintService.getComponentName());
      paramRemotePrintService.onAllPrintJobsHandled();
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.onServiceDiedLocked(paramRemotePrintService);
      return;
    }
  }
  
  public Bundle print(String paramString1, IPrintDocumentAdapter paramIPrintDocumentAdapter, PrintAttributes paramPrintAttributes, String paramString2, int paramInt)
  {
    final PrintJobInfo localPrintJobInfo = new PrintJobInfo();
    localPrintJobInfo.setId(new PrintJobId());
    localPrintJobInfo.setAppId(paramInt);
    localPrintJobInfo.setLabel(paramString1);
    localPrintJobInfo.setAttributes(paramPrintAttributes);
    localPrintJobInfo.setState(1);
    localPrintJobInfo.setCopies(1);
    localPrintJobInfo.setCreationTime(System.currentTimeMillis());
    if (!this.mPrintJobForAppCache.onPrintJobCreated(paramIPrintDocumentAdapter.asBinder(), paramInt, localPrintJobInfo)) {
      return null;
    }
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        UserState.-get6(UserState.this).createPrintJob(localPrintJobInfo);
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    long l = Binder.clearCallingIdentity();
    try
    {
      paramString1 = new Intent("android.print.PRINT_DIALOG");
      paramString1.setData(Uri.fromParts("printjob", localPrintJobInfo.getId().flattenToString(), null));
      paramString1.putExtra("android.print.intent.extra.EXTRA_PRINT_DOCUMENT_ADAPTER", paramIPrintDocumentAdapter.asBinder());
      paramString1.putExtra("android.print.intent.extra.EXTRA_PRINT_JOB", localPrintJobInfo);
      paramString1.putExtra("android.content.extra.PACKAGE_NAME", paramString2);
      paramString1 = PendingIntent.getActivityAsUser(this.mContext, 0, paramString1, 1409286144, null, new UserHandle(this.mUserId)).getIntentSender();
      paramIPrintDocumentAdapter = new Bundle();
      paramIPrintDocumentAdapter.putParcelable("android.print.intent.extra.EXTRA_PRINT_JOB", localPrintJobInfo);
      paramIPrintDocumentAdapter.putParcelable("android.print.intent.extra.EXTRA_PRINT_DIALOG_INTENT", paramString1);
      return paramIPrintDocumentAdapter;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public void prunePrintServices()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = getInstalledComponents();
      if (this.mDisabledServices.retainAll(localArrayList)) {
        writeDisabledPrintServicesLocked(this.mDisabledServices);
      }
      this.mSpooler.pruneApprovedPrintServices(localArrayList);
      return;
    }
  }
  
  public void removeObsoletePrintJobs()
  {
    this.mSpooler.removeObsoletePrintJobs();
  }
  
  public void removePrintJobStateChangeListener(IPrintJobStateChangeListener paramIPrintJobStateChangeListener)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      List localList = this.mPrintJobStateChangeListenerRecords;
      if (localList == null) {
        return;
      }
      int j = this.mPrintJobStateChangeListenerRecords.size();
      int i = 0;
      if (i < j)
      {
        if (((PrintJobStateChangeListenerRecord)this.mPrintJobStateChangeListenerRecords.get(i)).listener.asBinder().equals(paramIPrintJobStateChangeListener.asBinder())) {
          this.mPrintJobStateChangeListenerRecords.remove(i);
        }
      }
      else
      {
        if (this.mPrintJobStateChangeListenerRecords.isEmpty()) {
          this.mPrintJobStateChangeListenerRecords = null;
        }
        return;
      }
      i += 1;
    }
  }
  
  public void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener paramIRecommendationsChangeListener)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      List localList = this.mPrintServiceRecommendationsChangeListenerRecords;
      if (localList == null) {
        return;
      }
      int j = this.mPrintServiceRecommendationsChangeListenerRecords.size();
      int i = 0;
      if (i < j)
      {
        if (((IRecommendationsChangeListener)((ListenerRecord)this.mPrintServiceRecommendationsChangeListenerRecords.get(i)).listener).asBinder().equals(paramIRecommendationsChangeListener.asBinder())) {
          this.mPrintServiceRecommendationsChangeListenerRecords.remove(i);
        }
      }
      else
      {
        if (this.mPrintServiceRecommendationsChangeListenerRecords.isEmpty())
        {
          this.mPrintServiceRecommendationsChangeListenerRecords = null;
          this.mPrintServiceRecommendations = null;
          this.mPrintServiceRecommendationsService.close();
          this.mPrintServiceRecommendationsService = null;
        }
        return;
      }
      i += 1;
    }
  }
  
  public void removePrintServicesChangeListener(IPrintServicesChangeListener paramIPrintServicesChangeListener)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      List localList = this.mPrintServicesChangeListenerRecords;
      if (localList == null) {
        return;
      }
      int j = this.mPrintServicesChangeListenerRecords.size();
      int i = 0;
      if (i < j)
      {
        if (((IPrintServicesChangeListener)((ListenerRecord)this.mPrintServicesChangeListenerRecords.get(i)).listener).asBinder().equals(paramIPrintServicesChangeListener.asBinder())) {
          this.mPrintServicesChangeListenerRecords.remove(i);
        }
      }
      else
      {
        if (this.mPrintServicesChangeListenerRecords.isEmpty()) {
          this.mPrintServicesChangeListenerRecords = null;
        }
        return;
      }
      i += 1;
    }
  }
  
  public void restartPrintJob(PrintJobId paramPrintJobId, int paramInt)
  {
    PrintJobInfo localPrintJobInfo = getPrintJobInfo(paramPrintJobId, paramInt);
    if ((localPrintJobInfo == null) || (localPrintJobInfo.getState() != 6)) {
      return;
    }
    this.mSpooler.setPrintJobState(paramPrintJobId, 2, null);
  }
  
  public void setPrintServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    localObject = this.mLock;
    boolean bool = false;
    if (paramBoolean) {}
    try
    {
      paramBoolean = this.mDisabledServices.remove(paramComponentName);
      if (paramBoolean)
      {
        writeDisabledPrintServicesLocked(this.mDisabledServices);
        onConfigurationChangedLocked();
      }
      return;
    }
    finally {}
    int j = this.mInstalledServices.size();
    int i = 0;
    for (;;)
    {
      paramBoolean = bool;
      if (i >= j) {
        break;
      }
      if (((PrintServiceInfo)this.mInstalledServices.get(i)).getComponentName().equals(paramComponentName))
      {
        this.mDisabledServices.add(paramComponentName);
        paramBoolean = true;
        break;
      }
      i += 1;
    }
  }
  
  public void startPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> paramList)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.startPrinterDiscoveryLocked(paramIPrinterDiscoveryObserver, paramList);
      return;
    }
  }
  
  public void startPrinterStateTracking(PrinterId paramPrinterId)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.startPrinterStateTrackingLocked(paramPrinterId);
      return;
    }
  }
  
  public void stopPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.stopPrinterDiscoveryLocked(paramIPrinterDiscoveryObserver);
      return;
    }
  }
  
  public void stopPrinterStateTracking(PrinterId paramPrinterId)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.stopPrinterStateTrackingLocked(paramPrinterId);
      return;
    }
  }
  
  public void updateIfNeededLocked()
  {
    throwIfDestroyedLocked();
    readConfigurationLocked();
    onConfigurationChangedLocked();
  }
  
  public void validatePrinters(List<PrinterId> paramList)
  {
    synchronized (this.mLock)
    {
      throwIfDestroyedLocked();
      boolean bool = this.mActiveServices.isEmpty();
      if (bool) {
        return;
      }
      PrinterDiscoverySessionMediator localPrinterDiscoverySessionMediator = this.mPrinterDiscoverySession;
      if (localPrinterDiscoverySessionMediator == null) {
        return;
      }
      this.mPrinterDiscoverySession.validatePrintersLocked(paramList);
      return;
    }
  }
  
  private abstract class ListenerRecord<T extends IInterface>
    implements IBinder.DeathRecipient
  {
    final T listener;
    
    public ListenerRecord()
      throws RemoteException
    {
      IInterface localIInterface;
      this.listener = localIInterface;
      localIInterface.asBinder().linkToDeath(this, 0);
    }
    
    public void binderDied()
    {
      this.listener.asBinder().unlinkToDeath(this, 0);
      onBinderDied();
    }
    
    public abstract void onBinderDied();
  }
  
  private final class PrintJobForAppCache
  {
    private final SparseArray<List<PrintJobInfo>> mPrintJobsForRunningApp = new SparseArray();
    
    private PrintJobForAppCache() {}
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      synchronized (UserState.-get2(UserState.this))
      {
        int k = this.mPrintJobsForRunningApp.size();
        int i = 0;
        while (i < k)
        {
          int j = this.mPrintJobsForRunningApp.keyAt(i);
          paramPrintWriter.append(paramString).append("appId=" + j).append(':').println();
          List localList = (List)this.mPrintJobsForRunningApp.valueAt(i);
          int m = localList.size();
          j = 0;
          while (j < m)
          {
            PrintJobInfo localPrintJobInfo = (PrintJobInfo)localList.get(j);
            paramPrintWriter.append(paramString).append("  ").append(localPrintJobInfo.toString()).println();
            j += 1;
          }
          i += 1;
        }
        return;
      }
    }
    
    public PrintJobInfo getPrintJob(PrintJobId paramPrintJobId, int paramInt)
    {
      synchronized (UserState.-get2(UserState.this))
      {
        List localList = (List)this.mPrintJobsForRunningApp.get(paramInt);
        if (localList == null) {
          return null;
        }
        int i = localList.size();
        paramInt = 0;
        while (paramInt < i)
        {
          PrintJobInfo localPrintJobInfo = (PrintJobInfo)localList.get(paramInt);
          boolean bool = localPrintJobInfo.getId().equals(paramPrintJobId);
          if (bool) {
            return localPrintJobInfo;
          }
          paramInt += 1;
        }
        return null;
      }
    }
    
    public List<PrintJobInfo> getPrintJobs(int paramInt)
    {
      Object localObject5 = UserState.-get2(UserState.this);
      Object localObject1 = null;
      Object localObject4;
      if (paramInt == -2) {
        for (;;)
        {
          try
          {
            int i = this.mPrintJobsForRunningApp.size();
            paramInt = 0;
            localObject1 = null;
            if (paramInt >= i) {
              break label153;
            }
            localObject4 = localObject1;
          }
          finally {}
          try
          {
            localList = (List)this.mPrintJobsForRunningApp.valueAt(paramInt);
            if (localObject1 != null) {
              break label150;
            }
            localObject4 = localObject1;
            localObject1 = new ArrayList();
          }
          finally
          {
            continue;
            continue;
          }
          ((List)localObject1).addAll(localList);
          paramInt += 1;
        }
      }
      List localList = (List)this.mPrintJobsForRunningApp.get(paramInt);
      if (localList != null)
      {
        localObject1 = new ArrayList();
        localObject4 = localObject1;
        ((List)localObject1).addAll(localList);
      }
      label150:
      label153:
      for (;;)
      {
        if (localObject1 != null) {
          return (List<PrintJobInfo>)localObject1;
        }
        localObject1 = Collections.emptyList();
        return (List<PrintJobInfo>)localObject1;
      }
    }
    
    /* Error */
    public boolean onPrintJobCreated(final IBinder paramIBinder, final int paramInt, PrintJobInfo paramPrintJobInfo)
    {
      // Byte code:
      //   0: aload_1
      //   1: new 9	com/android/server/print/UserState$PrintJobForAppCache$1
      //   4: dup
      //   5: aload_0
      //   6: aload_1
      //   7: iload_2
      //   8: invokespecial 125	com/android/server/print/UserState$PrintJobForAppCache$1:<init>	(Lcom/android/server/print/UserState$PrintJobForAppCache;Landroid/os/IBinder;I)V
      //   11: iconst_0
      //   12: invokeinterface 131 3 0
      //   17: aload_0
      //   18: getfield 23	com/android/server/print/UserState$PrintJobForAppCache:this$0	Lcom/android/server/print/UserState;
      //   21: invokestatic 38	com/android/server/print/UserState:-get2	(Lcom/android/server/print/UserState;)Ljava/lang/Object;
      //   24: astore 5
      //   26: aload 5
      //   28: monitorenter
      //   29: aload_0
      //   30: getfield 18	com/android/server/print/UserState$PrintJobForAppCache:mPrintJobsForRunningApp	Landroid/util/SparseArray;
      //   33: iload_2
      //   34: invokevirtual 91	android/util/SparseArray:get	(I)Ljava/lang/Object;
      //   37: checkcast 79	java/util/List
      //   40: astore 4
      //   42: aload 4
      //   44: astore_1
      //   45: aload 4
      //   47: ifnonnull +20 -> 67
      //   50: new 105	java/util/ArrayList
      //   53: dup
      //   54: invokespecial 106	java/util/ArrayList:<init>	()V
      //   57: astore_1
      //   58: aload_0
      //   59: getfield 18	com/android/server/print/UserState$PrintJobForAppCache:mPrintJobsForRunningApp	Landroid/util/SparseArray;
      //   62: iload_2
      //   63: aload_1
      //   64: invokevirtual 135	android/util/SparseArray:put	(ILjava/lang/Object;)V
      //   67: aload_1
      //   68: aload_3
      //   69: invokeinterface 138 2 0
      //   74: pop
      //   75: aload 5
      //   77: monitorexit
      //   78: iconst_1
      //   79: ireturn
      //   80: astore_1
      //   81: iconst_0
      //   82: ireturn
      //   83: astore_1
      //   84: aload 5
      //   86: monitorexit
      //   87: aload_1
      //   88: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	PrintJobForAppCache
      //   0	89	1	paramIBinder	IBinder
      //   0	89	2	paramInt	int
      //   0	89	3	paramPrintJobInfo	PrintJobInfo
      //   40	6	4	localList	List
      // Exception table:
      //   from	to	target	type
      //   0	17	80	android/os/RemoteException
      //   29	42	83	finally
      //   50	67	83	finally
      //   67	75	83	finally
    }
    
    public void onPrintJobStateChanged(PrintJobInfo paramPrintJobInfo)
    {
      synchronized (UserState.-get2(UserState.this))
      {
        List localList = (List)this.mPrintJobsForRunningApp.get(paramPrintJobInfo.getAppId());
        if (localList == null) {
          return;
        }
        int j = localList.size();
        int i = 0;
        while (i < j)
        {
          if (((PrintJobInfo)localList.get(i)).getId().equals(paramPrintJobInfo.getId())) {
            localList.set(i, paramPrintJobInfo);
          }
          i += 1;
        }
        return;
      }
    }
  }
  
  private abstract class PrintJobStateChangeListenerRecord
    implements IBinder.DeathRecipient
  {
    final int appId;
    final IPrintJobStateChangeListener listener;
    
    public PrintJobStateChangeListenerRecord(IPrintJobStateChangeListener paramIPrintJobStateChangeListener, int paramInt)
      throws RemoteException
    {
      this.listener = paramIPrintJobStateChangeListener;
      this.appId = paramInt;
      paramIPrintJobStateChangeListener.asBinder().linkToDeath(this, 0);
    }
    
    public void binderDied()
    {
      this.listener.asBinder().unlinkToDeath(this, 0);
      onBinderDied();
    }
    
    public abstract void onBinderDied();
  }
  
  private class PrinterDiscoverySessionMediator
  {
    private final RemoteCallbackList<IPrinterDiscoveryObserver> mDiscoveryObservers = new RemoteCallbackList()
    {
      public void onCallbackDied(IPrinterDiscoveryObserver paramAnonymousIPrinterDiscoveryObserver)
      {
        synchronized (UserState.-get2(UserState.this))
        {
          UserState.PrinterDiscoverySessionMediator.this.stopPrinterDiscoveryLocked(paramAnonymousIPrinterDiscoveryObserver);
          UserState.PrinterDiscoverySessionMediator.this.removeObserverLocked(paramAnonymousIPrinterDiscoveryObserver);
          return;
        }
      }
    };
    private boolean mIsDestroyed;
    private final ArrayMap<PrinterId, PrinterInfo> mPrinters = new ArrayMap();
    private final Handler mSessionHandler;
    private final List<IBinder> mStartedPrinterDiscoveryTokens = new ArrayList();
    private final List<PrinterId> mStateTrackedPrinters = new ArrayList();
    
    public PrinterDiscoverySessionMediator(Context paramContext)
    {
      this.mSessionHandler = new SessionHandler(paramContext.getMainLooper());
      this$1 = new ArrayList(UserState.-get0(UserState.this).values());
      this.mSessionHandler.obtainMessage(9, UserState.this).sendToTarget();
    }
    
    private void handleDispatchCreatePrinterDiscoverySession(List<RemotePrintService> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ((RemotePrintService)paramList.get(i)).createPrinterDiscoverySession();
        i += 1;
      }
    }
    
    private void handleDispatchDestroyPrinterDiscoverySession(List<RemotePrintService> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ((RemotePrintService)paramList.get(i)).destroyPrinterDiscoverySession();
        i += 1;
      }
      onDestroyed();
    }
    
    private void handleDispatchPrintersAdded(List<PrinterInfo> paramList)
    {
      int j = this.mDiscoveryObservers.beginBroadcast();
      int i = 0;
      while (i < j)
      {
        handlePrintersAdded((IPrinterDiscoveryObserver)this.mDiscoveryObservers.getBroadcastItem(i), paramList);
        i += 1;
      }
      this.mDiscoveryObservers.finishBroadcast();
    }
    
    private void handleDispatchPrintersRemoved(List<PrinterId> paramList)
    {
      int j = this.mDiscoveryObservers.beginBroadcast();
      int i = 0;
      while (i < j)
      {
        handlePrintersRemoved((IPrinterDiscoveryObserver)this.mDiscoveryObservers.getBroadcastItem(i), paramList);
        i += 1;
      }
      this.mDiscoveryObservers.finishBroadcast();
    }
    
    private void handleDispatchStartPrinterDiscovery(List<RemotePrintService> paramList, List<PrinterId> paramList1)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ((RemotePrintService)paramList.get(i)).startPrinterDiscovery(paramList1);
        i += 1;
      }
    }
    
    private void handleDispatchStopPrinterDiscovery(List<RemotePrintService> paramList)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ((RemotePrintService)paramList.get(i)).stopPrinterDiscovery();
        i += 1;
      }
    }
    
    private void handlePrintersAdded(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterInfo> paramList)
    {
      try
      {
        paramIPrinterDiscoveryObserver.onPrintersAdded(new ParceledListSlice(paramList));
        return;
      }
      catch (RemoteException paramIPrinterDiscoveryObserver)
      {
        Log.e("UserState", "Error sending added printers", paramIPrinterDiscoveryObserver);
      }
    }
    
    private void handlePrintersRemoved(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> paramList)
    {
      try
      {
        paramIPrinterDiscoveryObserver.onPrintersRemoved(new ParceledListSlice(paramList));
        return;
      }
      catch (RemoteException paramIPrinterDiscoveryObserver)
      {
        Log.e("UserState", "Error sending removed printers", paramIPrinterDiscoveryObserver);
      }
    }
    
    private void handleStartPrinterStateTracking(RemotePrintService paramRemotePrintService, PrinterId paramPrinterId)
    {
      paramRemotePrintService.startPrinterStateTracking(paramPrinterId);
    }
    
    private void handleStopPrinterStateTracking(RemotePrintService paramRemotePrintService, PrinterId paramPrinterId)
    {
      paramRemotePrintService.stopPrinterStateTracking(paramPrinterId);
    }
    
    private void handleValidatePrinters(RemotePrintService paramRemotePrintService, List<PrinterId> paramList)
    {
      paramRemotePrintService.validatePrinters(paramList);
    }
    
    private void removePrintersForServiceLocked(ComponentName paramComponentName)
    {
      if (this.mPrinters.isEmpty()) {
        return;
      }
      Object localObject1 = null;
      int j = this.mPrinters.size();
      int i = 0;
      while (i < j)
      {
        PrinterId localPrinterId = (PrinterId)this.mPrinters.keyAt(i);
        Object localObject2 = localObject1;
        if (localPrinterId.getServiceName().equals(paramComponentName))
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrinterId);
        }
        i += 1;
        localObject1 = localObject2;
      }
      if (localObject1 != null)
      {
        j = ((List)localObject1).size();
        i = 0;
        while (i < j)
        {
          this.mPrinters.remove(((List)localObject1).get(i));
          i += 1;
        }
        this.mSessionHandler.obtainMessage(4, localObject1).sendToTarget();
      }
    }
    
    public void addObserverLocked(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
    {
      this.mDiscoveryObservers.register(paramIPrinterDiscoveryObserver);
      if (!this.mPrinters.isEmpty())
      {
        ArrayList localArrayList = new ArrayList(this.mPrinters.values());
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramIPrinterDiscoveryObserver;
        localSomeArgs.arg2 = localArrayList;
        this.mSessionHandler.obtainMessage(1, localSomeArgs).sendToTarget();
      }
    }
    
    public void destroyLocked()
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not destroying - session destroyed");
        return;
      }
      this.mIsDestroyed = true;
      int j = this.mStateTrackedPrinters.size();
      int i = 0;
      while (i < j)
      {
        localObject = (PrinterId)this.mStateTrackedPrinters.get(i);
        UserState.this.stopPrinterStateTracking((PrinterId)localObject);
        i += 1;
      }
      j = this.mStartedPrinterDiscoveryTokens.size();
      i = 0;
      while (i < j)
      {
        stopPrinterDiscoveryLocked(IPrinterDiscoveryObserver.Stub.asInterface((IBinder)this.mStartedPrinterDiscoveryTokens.get(i)));
        i += 1;
      }
      Object localObject = new ArrayList(UserState.-get0(UserState.this).values());
      this.mSessionHandler.obtainMessage(10, localObject).sendToTarget();
    }
    
    public void dump(PrintWriter paramPrintWriter, String paramString)
    {
      paramPrintWriter.append(paramString).append("destroyed=").append(String.valueOf(UserState.-get1(UserState.this))).println();
      Object localObject = paramPrintWriter.append(paramString).append("printDiscoveryInProgress=");
      if (this.mStartedPrinterDiscoveryTokens.isEmpty()) {}
      for (boolean bool = false;; bool = true)
      {
        ((PrintWriter)localObject).append(String.valueOf(bool)).println();
        paramPrintWriter.append(paramString).append("  ").append("printer discovery observers:").println();
        j = this.mDiscoveryObservers.beginBroadcast();
        i = 0;
        while (i < j)
        {
          localObject = (IPrinterDiscoveryObserver)this.mDiscoveryObservers.getBroadcastItem(i);
          paramPrintWriter.append(paramString).append(paramString).append(localObject.toString());
          paramPrintWriter.println();
          i += 1;
        }
      }
      this.mDiscoveryObservers.finishBroadcast();
      paramPrintWriter.append(paramString).append("  ").append("start discovery requests:").println();
      int j = this.mStartedPrinterDiscoveryTokens.size();
      int i = 0;
      while (i < j)
      {
        localObject = (IBinder)this.mStartedPrinterDiscoveryTokens.get(i);
        paramPrintWriter.append(paramString).append("  ").append("  ").append(localObject.toString()).println();
        i += 1;
      }
      paramPrintWriter.append(paramString).append("  ").append("tracked printer requests:").println();
      j = this.mStateTrackedPrinters.size();
      i = 0;
      while (i < j)
      {
        localObject = (PrinterId)this.mStateTrackedPrinters.get(i);
        paramPrintWriter.append(paramString).append("  ").append("  ").append(((PrinterId)localObject).toString()).println();
        i += 1;
      }
      paramPrintWriter.append(paramString).append("  ").append("printers:").println();
      j = this.mPrinters.size();
      i = 0;
      while (i < j)
      {
        localObject = (PrinterInfo)this.mPrinters.valueAt(i);
        paramPrintWriter.append(paramString).append("  ").append("  ").append(((PrinterInfo)localObject).toString()).println();
        i += 1;
      }
    }
    
    public void onCustomPrinterIconLoadedLocked(PrinterId paramPrinterId)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not updating printer - session destroyed");
        return;
      }
      PrinterInfo localPrinterInfo = (PrinterInfo)this.mPrinters.get(paramPrinterId);
      if (localPrinterInfo != null)
      {
        localPrinterInfo = new PrinterInfo.Builder(localPrinterInfo).incCustomPrinterIconGen().build();
        this.mPrinters.put(paramPrinterId, localPrinterInfo);
        paramPrinterId = new ArrayList(1);
        paramPrinterId.add(localPrinterInfo);
        this.mSessionHandler.obtainMessage(3, paramPrinterId).sendToTarget();
      }
    }
    
    public void onDestroyed() {}
    
    public void onPrintersAddedLocked(List<PrinterInfo> paramList)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not adding printers - session destroyed");
        return;
      }
      Object localObject1 = null;
      int j = paramList.size();
      int i = 0;
      if (i < j)
      {
        PrinterInfo localPrinterInfo = (PrinterInfo)paramList.get(i);
        Object localObject2 = (PrinterInfo)this.mPrinters.put(localPrinterInfo.getId(), localPrinterInfo);
        if ((localObject2 != null) && (((PrinterInfo)localObject2).equals(localPrinterInfo))) {}
        for (;;)
        {
          i += 1;
          break;
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrinterInfo);
          localObject1 = localObject2;
        }
      }
      if (localObject1 != null) {
        this.mSessionHandler.obtainMessage(3, localObject1).sendToTarget();
      }
    }
    
    public void onPrintersRemovedLocked(List<PrinterId> paramList)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not removing printers - session destroyed");
        return;
      }
      Object localObject1 = null;
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        PrinterId localPrinterId = (PrinterId)paramList.get(i);
        Object localObject2 = localObject1;
        if (this.mPrinters.remove(localPrinterId) != null)
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArrayList();
          }
          ((List)localObject2).add(localPrinterId);
        }
        i += 1;
        localObject1 = localObject2;
      }
      if (localObject1 != null) {
        this.mSessionHandler.obtainMessage(4, localObject1).sendToTarget();
      }
    }
    
    public void onServiceAddedLocked(RemotePrintService paramRemotePrintService)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not updating added service - session destroyed");
        return;
      }
      this.mSessionHandler.obtainMessage(5, paramRemotePrintService).sendToTarget();
      if (!this.mStartedPrinterDiscoveryTokens.isEmpty()) {
        this.mSessionHandler.obtainMessage(7, paramRemotePrintService).sendToTarget();
      }
      int j = this.mStateTrackedPrinters.size();
      int i = 0;
      while (i < j)
      {
        PrinterId localPrinterId = (PrinterId)this.mStateTrackedPrinters.get(i);
        if (localPrinterId.getServiceName().equals(paramRemotePrintService.getComponentName()))
        {
          SomeArgs localSomeArgs = SomeArgs.obtain();
          localSomeArgs.arg1 = paramRemotePrintService;
          localSomeArgs.arg2 = localPrinterId;
          this.mSessionHandler.obtainMessage(14, localSomeArgs).sendToTarget();
        }
        i += 1;
      }
    }
    
    public void onServiceDiedLocked(RemotePrintService paramRemotePrintService)
    {
      removePrintersForServiceLocked(paramRemotePrintService.getComponentName());
    }
    
    public void onServiceRemovedLocked(RemotePrintService paramRemotePrintService)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not updating removed service - session destroyed");
        return;
      }
      removePrintersForServiceLocked(paramRemotePrintService.getComponentName());
      paramRemotePrintService.destroy();
    }
    
    public void removeObserverLocked(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
    {
      this.mDiscoveryObservers.unregister(paramIPrinterDiscoveryObserver);
      if (this.mDiscoveryObservers.getRegisteredCallbackCount() == 0) {
        destroyLocked();
      }
    }
    
    public final void startPrinterDiscoveryLocked(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> paramList)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not starting dicovery - session destroyed");
        return;
      }
      if (this.mStartedPrinterDiscoveryTokens.isEmpty()) {}
      for (int i = 0;; i = 1)
      {
        this.mStartedPrinterDiscoveryTokens.add(paramIPrinterDiscoveryObserver.asBinder());
        if ((i != 0) && (paramList != null) && (!paramList.isEmpty())) {
          break;
        }
        if (this.mStartedPrinterDiscoveryTokens.size() <= 1) {
          break label92;
        }
        return;
      }
      UserState.this.validatePrinters(paramList);
      return;
      label92:
      paramIPrinterDiscoveryObserver = new ArrayList(UserState.-get0(UserState.this).values());
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramIPrinterDiscoveryObserver;
      localSomeArgs.arg2 = paramList;
      this.mSessionHandler.obtainMessage(11, localSomeArgs).sendToTarget();
    }
    
    public final void startPrinterStateTrackingLocked(PrinterId paramPrinterId)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not starting printer state tracking - session destroyed");
        return;
      }
      if (this.mStartedPrinterDiscoveryTokens.isEmpty()) {
        return;
      }
      boolean bool = this.mStateTrackedPrinters.contains(paramPrinterId);
      this.mStateTrackedPrinters.add(paramPrinterId);
      if (bool) {
        return;
      }
      RemotePrintService localRemotePrintService = (RemotePrintService)UserState.-get0(UserState.this).get(paramPrinterId.getServiceName());
      if (localRemotePrintService == null) {
        return;
      }
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = localRemotePrintService;
      localSomeArgs.arg2 = paramPrinterId;
      this.mSessionHandler.obtainMessage(14, localSomeArgs).sendToTarget();
    }
    
    public final void stopPrinterDiscoveryLocked(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not stopping dicovery - session destroyed");
        return;
      }
      if (!this.mStartedPrinterDiscoveryTokens.remove(paramIPrinterDiscoveryObserver.asBinder())) {
        return;
      }
      if (!this.mStartedPrinterDiscoveryTokens.isEmpty()) {
        return;
      }
      paramIPrinterDiscoveryObserver = new ArrayList(UserState.-get0(UserState.this).values());
      this.mSessionHandler.obtainMessage(12, paramIPrinterDiscoveryObserver).sendToTarget();
    }
    
    public final void stopPrinterStateTrackingLocked(PrinterId paramPrinterId)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not stopping printer state tracking - session destroyed");
        return;
      }
      if (this.mStartedPrinterDiscoveryTokens.isEmpty()) {
        return;
      }
      if (!this.mStateTrackedPrinters.remove(paramPrinterId)) {
        return;
      }
      RemotePrintService localRemotePrintService = (RemotePrintService)UserState.-get0(UserState.this).get(paramPrinterId.getServiceName());
      if (localRemotePrintService == null) {
        return;
      }
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = localRemotePrintService;
      localSomeArgs.arg2 = paramPrinterId;
      this.mSessionHandler.obtainMessage(15, localSomeArgs).sendToTarget();
    }
    
    public void validatePrintersLocked(List<PrinterId> paramList)
    {
      if (this.mIsDestroyed)
      {
        Log.w("UserState", "Not validating pritners - session destroyed");
        return;
      }
      ArrayList localArrayList1 = new ArrayList(paramList);
      while (!localArrayList1.isEmpty())
      {
        Object localObject = localArrayList1.iterator();
        ArrayList localArrayList2 = new ArrayList();
        paramList = null;
        while (((Iterator)localObject).hasNext())
        {
          PrinterId localPrinterId = (PrinterId)((Iterator)localObject).next();
          if (localPrinterId != null) {
            if (localArrayList2.isEmpty())
            {
              localArrayList2.add(localPrinterId);
              paramList = localPrinterId.getServiceName();
              ((Iterator)localObject).remove();
            }
            else if (localPrinterId.getServiceName().equals(paramList))
            {
              localArrayList2.add(localPrinterId);
              ((Iterator)localObject).remove();
            }
          }
        }
        paramList = (RemotePrintService)UserState.-get0(UserState.this).get(paramList);
        if (paramList != null)
        {
          localObject = SomeArgs.obtain();
          ((SomeArgs)localObject).arg1 = paramList;
          ((SomeArgs)localObject).arg2 = localArrayList2;
          this.mSessionHandler.obtainMessage(13, localObject).sendToTarget();
        }
      }
    }
    
    private final class SessionHandler
      extends Handler
    {
      public static final int MSG_CREATE_PRINTER_DISCOVERY_SESSION = 5;
      public static final int MSG_DESTROY_PRINTER_DISCOVERY_SESSION = 6;
      public static final int MSG_DESTROY_SERVICE = 16;
      public static final int MSG_DISPATCH_CREATE_PRINTER_DISCOVERY_SESSION = 9;
      public static final int MSG_DISPATCH_DESTROY_PRINTER_DISCOVERY_SESSION = 10;
      public static final int MSG_DISPATCH_PRINTERS_ADDED = 3;
      public static final int MSG_DISPATCH_PRINTERS_REMOVED = 4;
      public static final int MSG_DISPATCH_START_PRINTER_DISCOVERY = 11;
      public static final int MSG_DISPATCH_STOP_PRINTER_DISCOVERY = 12;
      public static final int MSG_PRINTERS_ADDED = 1;
      public static final int MSG_PRINTERS_REMOVED = 2;
      public static final int MSG_START_PRINTER_DISCOVERY = 7;
      public static final int MSG_START_PRINTER_STATE_TRACKING = 14;
      public static final int MSG_STOP_PRINTER_DISCOVERY = 8;
      public static final int MSG_STOP_PRINTER_STATE_TRACKING = 15;
      public static final int MSG_VALIDATE_PRINTERS = 13;
      
      SessionHandler(Looper paramLooper)
      {
        super(null, false);
      }
      
      public void handleMessage(Message paramMessage)
      {
        Object localObject1;
        Object localObject2;
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          paramMessage = (SomeArgs)paramMessage.obj;
          localObject1 = (IPrinterDiscoveryObserver)paramMessage.arg1;
          localObject2 = (List)paramMessage.arg2;
          paramMessage.recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap6(UserState.PrinterDiscoverySessionMediator.this, (IPrinterDiscoveryObserver)localObject1, (List)localObject2);
          return;
        case 2: 
          localObject1 = (SomeArgs)paramMessage.obj;
          localObject2 = (IPrinterDiscoveryObserver)((SomeArgs)localObject1).arg1;
          List localList = (List)((SomeArgs)localObject1).arg2;
          ((SomeArgs)localObject1).recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap7(UserState.PrinterDiscoverySessionMediator.this, (IPrinterDiscoveryObserver)localObject2, localList);
        case 3: 
          paramMessage = (List)paramMessage.obj;
          UserState.PrinterDiscoverySessionMediator.-wrap2(UserState.PrinterDiscoverySessionMediator.this, paramMessage);
          return;
        case 4: 
          paramMessage = (List)paramMessage.obj;
          UserState.PrinterDiscoverySessionMediator.-wrap3(UserState.PrinterDiscoverySessionMediator.this, paramMessage);
          return;
        case 5: 
          ((RemotePrintService)paramMessage.obj).createPrinterDiscoverySession();
          return;
        case 6: 
          ((RemotePrintService)paramMessage.obj).destroyPrinterDiscoverySession();
          return;
        case 7: 
          ((RemotePrintService)paramMessage.obj).startPrinterDiscovery(null);
          return;
        case 8: 
          ((RemotePrintService)paramMessage.obj).stopPrinterDiscovery();
          return;
        case 9: 
          paramMessage = (List)paramMessage.obj;
          UserState.PrinterDiscoverySessionMediator.-wrap0(UserState.PrinterDiscoverySessionMediator.this, paramMessage);
          return;
        case 10: 
          paramMessage = (List)paramMessage.obj;
          UserState.PrinterDiscoverySessionMediator.-wrap1(UserState.PrinterDiscoverySessionMediator.this, paramMessage);
          return;
        case 11: 
          paramMessage = (SomeArgs)paramMessage.obj;
          localObject1 = (List)paramMessage.arg1;
          localObject2 = (List)paramMessage.arg2;
          paramMessage.recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap4(UserState.PrinterDiscoverySessionMediator.this, (List)localObject1, (List)localObject2);
          return;
        case 12: 
          paramMessage = (List)paramMessage.obj;
          UserState.PrinterDiscoverySessionMediator.-wrap5(UserState.PrinterDiscoverySessionMediator.this, paramMessage);
          return;
        case 13: 
          paramMessage = (SomeArgs)paramMessage.obj;
          localObject1 = (RemotePrintService)paramMessage.arg1;
          localObject2 = (List)paramMessage.arg2;
          paramMessage.recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap10(UserState.PrinterDiscoverySessionMediator.this, (RemotePrintService)localObject1, (List)localObject2);
          return;
        case 14: 
          paramMessage = (SomeArgs)paramMessage.obj;
          localObject1 = (RemotePrintService)paramMessage.arg1;
          localObject2 = (PrinterId)paramMessage.arg2;
          paramMessage.recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap8(UserState.PrinterDiscoverySessionMediator.this, (RemotePrintService)localObject1, (PrinterId)localObject2);
          return;
        case 15: 
          paramMessage = (SomeArgs)paramMessage.obj;
          localObject1 = (RemotePrintService)paramMessage.arg1;
          localObject2 = (PrinterId)paramMessage.arg2;
          paramMessage.recycle();
          UserState.PrinterDiscoverySessionMediator.-wrap9(UserState.PrinterDiscoverySessionMediator.this, (RemotePrintService)localObject1, (PrinterId)localObject2);
          return;
        }
        ((RemotePrintService)paramMessage.obj).destroy();
      }
    }
  }
  
  private final class UserStateHandler
    extends Handler
  {
    public static final int MSG_DISPATCH_PRINT_JOB_STATE_CHANGED = 1;
    public static final int MSG_DISPATCH_PRINT_SERVICES_CHANGED = 2;
    public static final int MSG_DISPATCH_PRINT_SERVICES_RECOMMENDATIONS_UPDATED = 3;
    
    public UserStateHandler(Looper paramLooper)
    {
      super(null, false);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        PrintJobId localPrintJobId = (PrintJobId)paramMessage.obj;
        int i = paramMessage.arg1;
        UserState.-wrap1(UserState.this, localPrintJobId, i);
        return;
      case 2: 
        UserState.-wrap3(UserState.this);
        return;
      }
      UserState.-wrap2(UserState.this, (List)paramMessage.obj);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/print/UserState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */