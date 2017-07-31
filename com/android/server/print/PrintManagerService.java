package com.android.server.print;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.print.IPrintDocumentAdapter;
import android.print.IPrintJobStateChangeListener;
import android.print.IPrintManager.Stub;
import android.print.IPrintServicesChangeListener;
import android.print.IPrinterDiscoveryObserver;
import android.print.PrintAttributes;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.printservice.PrintServiceInfo;
import android.printservice.recommendation.IRecommendationsChangeListener;
import android.printservice.recommendation.RecommendationInfo;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public final class PrintManagerService
  extends SystemService
{
  private static final String LOG_TAG = "PrintManagerService";
  private final PrintManagerImpl mPrintManagerImpl;
  
  public PrintManagerService(Context paramContext)
  {
    super(paramContext);
    this.mPrintManagerImpl = new PrintManagerImpl(paramContext);
  }
  
  public void onStart()
  {
    publishBinderService("print", this.mPrintManagerImpl);
  }
  
  public void onStopUser(int paramInt)
  {
    PrintManagerImpl.-wrap1(this.mPrintManagerImpl, paramInt);
  }
  
  public void onUnlockUser(int paramInt)
  {
    PrintManagerImpl.-wrap2(this.mPrintManagerImpl, paramInt);
  }
  
  class PrintManagerImpl
    extends IPrintManager.Stub
  {
    private static final int BACKGROUND_USER_ID = -10;
    private final Context mContext;
    private final Object mLock = new Object();
    private final UserManager mUserManager;
    private final SparseArray<UserState> mUserStates = new SparseArray();
    
    PrintManagerImpl(Context paramContext)
    {
      this.mContext = paramContext;
      this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
      registerContentObservers();
      registerBroadcastReceivers();
    }
    
    private int getCurrentUserId()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        int i = ActivityManager.getCurrentUser();
        return i;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    private UserState getOrCreateUserStateLocked(int paramInt, boolean paramBoolean)
    {
      if (!this.mUserManager.isUserUnlockingOrUnlocked(paramInt)) {
        throw new IllegalStateException("User " + paramInt + " must be unlocked for printing to be available");
      }
      UserState localUserState2 = (UserState)this.mUserStates.get(paramInt);
      UserState localUserState1 = localUserState2;
      if (localUserState2 == null)
      {
        localUserState1 = new UserState(this.mContext, paramInt, this.mLock, paramBoolean);
        this.mUserStates.put(paramInt, localUserState1);
      }
      if (!paramBoolean) {
        localUserState1.increasePriority();
      }
      return localUserState1;
    }
    
    private void handleUserStopped(final int paramInt)
    {
      BackgroundThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            UserState localUserState = (UserState)PrintManagerService.PrintManagerImpl.-get3(PrintManagerService.PrintManagerImpl.this).get(paramInt);
            if (localUserState != null)
            {
              localUserState.destroyLocked();
              PrintManagerService.PrintManagerImpl.-get3(PrintManagerService.PrintManagerImpl.this).remove(paramInt);
            }
            return;
          }
        }
      });
    }
    
    private void handleUserUnlocked(final int paramInt)
    {
      BackgroundThread.getHandler().post(new Runnable()
      {
        public void run()
        {
          if (!PrintManagerService.PrintManagerImpl.-get2(PrintManagerService.PrintManagerImpl.this).isUserUnlockingOrUnlocked(paramInt)) {
            return;
          }
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            UserState localUserState = PrintManagerService.PrintManagerImpl.-wrap0(PrintManagerService.PrintManagerImpl.this, paramInt, true);
            localUserState.updateIfNeededLocked();
            localUserState.removeObsoletePrintJobs();
            return;
          }
        }
      });
    }
    
    private void registerBroadcastReceivers()
    {
      new PackageMonitor()
      {
        private boolean hadPrintService(UserState paramAnonymousUserState, String paramAnonymousString)
        {
          paramAnonymousUserState = paramAnonymousUserState.getPrintServices(3);
          if (paramAnonymousUserState == null) {
            return false;
          }
          int j = paramAnonymousUserState.size();
          int i = 0;
          while (i < j)
          {
            if (((PrintServiceInfo)paramAnonymousUserState.get(i)).getResolveInfo().serviceInfo.packageName.equals(paramAnonymousString)) {
              return true;
            }
            i += 1;
          }
          return false;
        }
        
        private boolean hasPrintService(String paramAnonymousString)
        {
          Intent localIntent = new Intent("android.printservice.PrintService");
          localIntent.setPackage(paramAnonymousString);
          paramAnonymousString = PrintManagerService.PrintManagerImpl.-get0(PrintManagerService.PrintManagerImpl.this).getPackageManager().queryIntentServicesAsUser(localIntent, 268435460, getChangingUserId());
          return (paramAnonymousString != null) && (!paramAnonymousString.isEmpty());
        }
        
        public boolean onHandleForceStop(Intent arg1, String[] paramAnonymousArrayOfString, int paramAnonymousInt, boolean paramAnonymousBoolean)
        {
          if (!PrintManagerService.PrintManagerImpl.-get2(PrintManagerService.PrintManagerImpl.this).isUserUnlockingOrUnlocked(getChangingUserId())) {
            return false;
          }
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            UserState localUserState = PrintManagerService.PrintManagerImpl.-wrap0(PrintManagerService.PrintManagerImpl.this, getChangingUserId(), false);
            paramAnonymousInt = 0;
            Object localObject = localUserState.getPrintServices(1);
            if (localObject == null) {
              return false;
            }
            localObject = ((List)localObject).iterator();
            label150:
            while (((Iterator)localObject).hasNext())
            {
              String str = ((PrintServiceInfo)((Iterator)localObject).next()).getComponentName().getPackageName();
              int i = 0;
              int j = paramAnonymousArrayOfString.length;
              for (;;)
              {
                if (i >= j) {
                  break label150;
                }
                boolean bool = str.equals(paramAnonymousArrayOfString[i]);
                if (bool)
                {
                  if (!paramAnonymousBoolean) {
                    return true;
                  }
                  paramAnonymousInt = 1;
                  break;
                }
                i += 1;
              }
            }
            if (paramAnonymousInt != 0) {
              localUserState.updateIfNeededLocked();
            }
            return false;
          }
        }
        
        public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
        {
          if (!PrintManagerService.PrintManagerImpl.-get2(PrintManagerService.PrintManagerImpl.this).isUserUnlockingOrUnlocked(getChangingUserId())) {
            return;
          }
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            if (hasPrintService(paramAnonymousString)) {
              PrintManagerService.PrintManagerImpl.-wrap0(PrintManagerService.PrintManagerImpl.this, getChangingUserId(), false).updateIfNeededLocked();
            }
            return;
          }
        }
        
        public void onPackageModified(String paramAnonymousString)
        {
          if (!PrintManagerService.PrintManagerImpl.-get2(PrintManagerService.PrintManagerImpl.this).isUserUnlockingOrUnlocked(getChangingUserId())) {
            return;
          }
          UserState localUserState = PrintManagerService.PrintManagerImpl.-wrap0(PrintManagerService.PrintManagerImpl.this, getChangingUserId(), false);
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            if ((hadPrintService(localUserState, paramAnonymousString)) || (hasPrintService(paramAnonymousString))) {
              localUserState.updateIfNeededLocked();
            }
            localUserState.prunePrintServices();
            return;
          }
        }
        
        public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
        {
          if (!PrintManagerService.PrintManagerImpl.-get2(PrintManagerService.PrintManagerImpl.this).isUserUnlockingOrUnlocked(getChangingUserId())) {
            return;
          }
          UserState localUserState = PrintManagerService.PrintManagerImpl.-wrap0(PrintManagerService.PrintManagerImpl.this, getChangingUserId(), false);
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            if (hadPrintService(localUserState, paramAnonymousString)) {
              localUserState.updateIfNeededLocked();
            }
            localUserState.prunePrintServices();
            return;
          }
        }
      }.register(this.mContext, BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
    }
    
    private void registerContentObservers()
    {
      final Uri localUri = Settings.Secure.getUriFor("disabled_print_services");
      ContentObserver local1 = new ContentObserver(BackgroundThread.getHandler())
      {
        public void onChange(boolean paramAnonymousBoolean, Uri arg2, int paramAnonymousInt)
        {
          if (localUri.equals(???)) {}
          synchronized (PrintManagerService.PrintManagerImpl.-get1(PrintManagerService.PrintManagerImpl.this))
          {
            int j = PrintManagerService.PrintManagerImpl.-get3(PrintManagerService.PrintManagerImpl.this).size();
            int i = 0;
            while (i < j)
            {
              if ((paramAnonymousInt == -1) || (paramAnonymousInt == PrintManagerService.PrintManagerImpl.-get3(PrintManagerService.PrintManagerImpl.this).keyAt(i))) {
                ((UserState)PrintManagerService.PrintManagerImpl.-get3(PrintManagerService.PrintManagerImpl.this).valueAt(i)).updateIfNeededLocked();
              }
              i += 1;
            }
            return;
          }
        }
      };
      this.mContext.getContentResolver().registerContentObserver(localUri, false, local1, -1);
    }
    
    private int resolveCallingAppEnforcingPermissions(int paramInt)
    {
      int i = Binder.getCallingUid();
      if ((i == 0) || (i == 1000)) {}
      while (i == 2000) {
        return paramInt;
      }
      i = UserHandle.getAppId(i);
      if (paramInt == i) {
        return paramInt;
      }
      if (this.mContext.checkCallingPermission("com.android.printspooler.permission.ACCESS_ALL_PRINT_JOBS") != 0) {
        throw new SecurityException("Call from app " + i + " as app " + paramInt + " without com.android.printspooler.permission" + ".ACCESS_ALL_PRINT_JOBS");
      }
      return paramInt;
    }
    
    private String resolveCallingPackageNameEnforcingSecurity(String paramString)
    {
      String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid());
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        if (paramString.equals(arrayOfString[i])) {
          return paramString;
        }
        i += 1;
      }
      throw new IllegalArgumentException("packageName has to belong to the caller");
    }
    
    private int resolveCallingProfileParentLocked(int paramInt)
    {
      if (paramInt != getCurrentUserId())
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          UserInfo localUserInfo = this.mUserManager.getProfileParent(paramInt);
          if (localUserInfo != null)
          {
            paramInt = localUserInfo.getUserHandle().getIdentifier();
            return paramInt;
          }
          return -10;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
      return paramInt;
    }
    
    private int resolveCallingUserEnforcingPermissions(int paramInt)
    {
      try
      {
        int i = ActivityManagerNative.getDefault().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, true, true, "", null);
        return i;
      }
      catch (RemoteException localRemoteException) {}
      return paramInt;
    }
    
    public void addPrintJobStateChangeListener(IPrintJobStateChangeListener arg1, int paramInt1, int paramInt2)
      throws RemoteException
    {
      IPrintJobStateChangeListener localIPrintJobStateChangeListener = (IPrintJobStateChangeListener)Preconditions.checkNotNull(???);
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void addPrintServiceRecommendationsChangeListener(IRecommendationsChangeListener arg1, int paramInt)
      throws RemoteException
    {
      IRecommendationsChangeListener localIRecommendationsChangeListener = (IRecommendationsChangeListener)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void addPrintServicesChangeListener(IPrintServicesChangeListener arg1, int paramInt)
      throws RemoteException
    {
      IPrintServicesChangeListener localIPrintServicesChangeListener = (IPrintServicesChangeListener)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void cancelPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    {
      if (paramPrintJobId == null) {
        return;
      }
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void createPrinterDiscoverySession(IPrinterDiscoveryObserver arg1, int paramInt)
    {
      IPrinterDiscoveryObserver localIPrinterDiscoveryObserver = (IPrinterDiscoveryObserver)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void destroyPrinterDiscoverySession(IPrinterDiscoveryObserver arg1, int paramInt)
    {
      IPrinterDiscoveryObserver localIPrinterDiscoveryObserver = (IPrinterDiscoveryObserver)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramArrayOfString = (FileDescriptor)Preconditions.checkNotNull(???);
      paramPrintWriter = (PrintWriter)Preconditions.checkNotNull(paramPrintWriter);
      if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump PrintManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      synchronized (this.mLock)
      {
        long l = Binder.clearCallingIdentity();
        try
        {
          paramPrintWriter.println("PRINT MANAGER STATE (dumpsys print)");
          int j = this.mUserStates.size();
          int i = 0;
          while (i < j)
          {
            ((UserState)this.mUserStates.valueAt(i)).dump(paramArrayOfString, paramPrintWriter, "");
            paramPrintWriter.println();
            i += 1;
          }
          Binder.restoreCallingIdentity(l);
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    public Icon getCustomPrinterIcon(PrinterId arg1, int paramInt)
    {
      PrinterId localPrinterId = (PrinterId)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public PrintJobInfo getPrintJobInfo(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    {
      if (paramPrintJobId == null) {
        return null;
      }
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public List<PrintJobInfo> getPrintJobInfos(int paramInt1, int paramInt2)
    {
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public List<RecommendationInfo> getPrintServiceRecommendations(int paramInt)
    {
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public List<PrintServiceInfo> getPrintServices(int paramInt1, int paramInt2)
    {
      Preconditions.checkFlagsArgument(paramInt1, 3);
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public Bundle print(String paramString1, IPrintDocumentAdapter paramIPrintDocumentAdapter, PrintAttributes paramPrintAttributes, String arg4, int paramInt1, int paramInt2)
    {
      paramString1 = (String)Preconditions.checkStringNotEmpty(paramString1);
      paramIPrintDocumentAdapter = (IPrintDocumentAdapter)Preconditions.checkNotNull(paramIPrintDocumentAdapter);
      String str = (String)Preconditions.checkStringNotEmpty(???);
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return null;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        str = resolveCallingPackageNameEnforcingSecurity(str);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void removePrintJobStateChangeListener(IPrintJobStateChangeListener arg1, int paramInt)
    {
      IPrintJobStateChangeListener localIPrintJobStateChangeListener = (IPrintJobStateChangeListener)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void removePrintServiceRecommendationsChangeListener(IRecommendationsChangeListener arg1, int paramInt)
    {
      IRecommendationsChangeListener localIRecommendationsChangeListener = (IRecommendationsChangeListener)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void removePrintServicesChangeListener(IPrintServicesChangeListener arg1, int paramInt)
    {
      IPrintServicesChangeListener localIPrintServicesChangeListener = (IPrintServicesChangeListener)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void restartPrintJob(PrintJobId paramPrintJobId, int paramInt1, int paramInt2)
    {
      if (paramPrintJobId == null) {
        return;
      }
      paramInt2 = resolveCallingUserEnforcingPermissions(paramInt2);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt2);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        paramInt1 = resolveCallingAppEnforcingPermissions(paramInt1);
        localUserState = getOrCreateUserStateLocked(paramInt2, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void setPrintServiceEnabled(ComponentName arg1, boolean paramBoolean, int paramInt)
    {
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      int i = UserHandle.getAppId(Binder.getCallingUid());
      if (i != 1000) {
        try
        {
          if (i != UserHandle.getAppId(this.mContext.getPackageManager().getPackageUidAsUser("com.android.printspooler", paramInt))) {
            throw new SecurityException("Only system and print spooler can call this");
          }
        }
        catch (PackageManager.NameNotFoundException ???)
        {
          Log.e("PrintManagerService", "Could not verify caller", ???);
          return;
        }
      }
      ComponentName localComponentName = (ComponentName)Preconditions.checkNotNull(???);
      UserState localUserState;
      synchronized (this.mLock)
      {
        i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void startPrinterDiscovery(IPrinterDiscoveryObserver paramIPrinterDiscoveryObserver, List<PrinterId> arg2, int paramInt)
    {
      IPrinterDiscoveryObserver localIPrinterDiscoveryObserver = (IPrinterDiscoveryObserver)Preconditions.checkNotNull(paramIPrinterDiscoveryObserver);
      paramIPrinterDiscoveryObserver = ???;
      if (??? != null) {
        paramIPrinterDiscoveryObserver = (List)Preconditions.checkCollectionElementsNotNull(???, "PrinterId");
      }
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void startPrinterStateTracking(PrinterId arg1, int paramInt)
    {
      PrinterId localPrinterId = (PrinterId)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void stopPrinterDiscovery(IPrinterDiscoveryObserver arg1, int paramInt)
    {
      IPrinterDiscoveryObserver localIPrinterDiscoveryObserver = (IPrinterDiscoveryObserver)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void stopPrinterStateTracking(PrinterId arg1, int paramInt)
    {
      PrinterId localPrinterId = (PrinterId)Preconditions.checkNotNull(???);
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
    
    public void validatePrinters(List<PrinterId> arg1, int paramInt)
    {
      List localList = (List)Preconditions.checkCollectionElementsNotNull(???, "PrinterId");
      paramInt = resolveCallingUserEnforcingPermissions(paramInt);
      UserState localUserState;
      synchronized (this.mLock)
      {
        int i = resolveCallingProfileParentLocked(paramInt);
        int j = getCurrentUserId();
        if (i != j) {
          return;
        }
        localUserState = getOrCreateUserStateLocked(paramInt, false);
        l = Binder.clearCallingIdentity();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/print/PrintManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */