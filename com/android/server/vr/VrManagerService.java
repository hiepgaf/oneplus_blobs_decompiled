package com.android.server.vr;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.service.vr.IVrListener;
import android.service.vr.IVrListener.Stub;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.service.vr.IVrStateCallbacks;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.SystemService;
import com.android.server.utils.ManagedApplicationService;
import com.android.server.utils.ManagedApplicationService.BinderChecker;
import com.android.server.utils.ManagedApplicationService.PendingEvent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class VrManagerService
  extends SystemService
  implements EnabledComponentsObserver.EnabledComponentChangeListener
{
  private static final int EVENT_LOG_SIZE = 32;
  private static final int INVALID_APPOPS_MODE = -1;
  private static final int MSG_PENDING_VR_STATE_CHANGE = 1;
  private static final int MSG_VR_STATE_CHANGE = 0;
  private static final int PENDING_STATE_DELAY_MS = 300;
  public static final String TAG = "VrManagerService";
  public static final String VR_MANAGER_BINDER_SERVICE = "vrmanager";
  private static final ManagedApplicationService.BinderChecker sBinderChecker = new ManagedApplicationService.BinderChecker()
  {
    public IInterface asInterface(IBinder paramAnonymousIBinder)
    {
      return IVrListener.Stub.asInterface(paramAnonymousIBinder);
    }
    
    public boolean checkType(IInterface paramAnonymousIInterface)
    {
      return paramAnonymousIInterface instanceof IVrListener;
    }
  };
  private EnabledComponentsObserver mComponentObserver;
  private Context mContext;
  private ComponentName mCurrentVrModeComponent;
  private int mCurrentVrModeUser;
  private ManagedApplicationService mCurrentVrService;
  private boolean mGuard;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        throw new IllegalStateException("Unknown message type: " + ???.what);
      case 0: 
        if (???.arg1 == 1) {}
        for (boolean bool = true;; bool = false)
        {
          int i = VrManagerService.-get3(VrManagerService.this).beginBroadcast();
          while (i > 0)
          {
            i -= 1;
            try
            {
              ((IVrStateCallbacks)VrManagerService.-get3(VrManagerService.this).getBroadcastItem(i)).onVrStateChanged(bool);
            }
            catch (RemoteException ???) {}
          }
        }
        VrManagerService.-get3(VrManagerService.this).finishBroadcast();
        return;
      }
      synchronized (VrManagerService.-get2(VrManagerService.this))
      {
        VrManagerService.-wrap4(VrManagerService.this);
        return;
      }
    }
  };
  private final Object mLock = new Object();
  private final ArrayDeque<VrState> mLoggingDeque = new ArrayDeque(32);
  private final NotificationAccessManager mNotifAccessManager = new NotificationAccessManager(null);
  private final IBinder mOverlayToken = new Binder();
  private VrState mPendingState;
  private int mPreviousCoarseLocationMode = -1;
  private int mPreviousManageOverlayMode = -1;
  private final RemoteCallbackList<IVrStateCallbacks> mRemoteCallbacks = new RemoteCallbackList();
  private final IVrManager mVrManager = new IVrManager.Stub()
  {
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      if (VrManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramAnonymousPrintWriter.println("permission denied: can't dump VrManagerService from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      paramAnonymousPrintWriter.println("********* Dump of VrManagerService *********");
      paramAnonymousPrintWriter.println("Previous state transitions:\n");
      VrManagerService.-wrap5(VrManagerService.this, paramAnonymousPrintWriter);
      paramAnonymousPrintWriter.println("\n\nRemote Callbacks:");
      int j;
      for (int i = VrManagerService.-get3(VrManagerService.this).beginBroadcast();; i = j)
      {
        j = i - 1;
        if (i <= 0) {
          break;
        }
        paramAnonymousPrintWriter.print("  ");
        paramAnonymousPrintWriter.print(VrManagerService.-get3(VrManagerService.this).getBroadcastItem(j));
        if (j > 0) {
          paramAnonymousPrintWriter.println(",");
        }
      }
      VrManagerService.-get3(VrManagerService.this).finishBroadcast();
      paramAnonymousPrintWriter.println("\n");
      paramAnonymousPrintWriter.println("Installed VrListenerService components:");
      i = VrManagerService.-get1(VrManagerService.this);
      paramAnonymousFileDescriptor = VrManagerService.-get0(VrManagerService.this).getInstalled(i);
      if ((paramAnonymousFileDescriptor == null) || (paramAnonymousFileDescriptor.size() == 0))
      {
        paramAnonymousPrintWriter.println("None");
        paramAnonymousPrintWriter.println("Enabled VrListenerService components:");
        paramAnonymousFileDescriptor = VrManagerService.-get0(VrManagerService.this).getEnabled(i);
        if ((paramAnonymousFileDescriptor != null) && (paramAnonymousFileDescriptor.size() != 0)) {
          break label294;
        }
        paramAnonymousPrintWriter.println("None");
      }
      for (;;)
      {
        paramAnonymousPrintWriter.println("\n");
        paramAnonymousPrintWriter.println("********* End of VrManagerService Dump *********");
        return;
        paramAnonymousFileDescriptor = paramAnonymousFileDescriptor.iterator();
        while (paramAnonymousFileDescriptor.hasNext())
        {
          paramAnonymousArrayOfString = (ComponentName)paramAnonymousFileDescriptor.next();
          paramAnonymousPrintWriter.print("  ");
          paramAnonymousPrintWriter.println(paramAnonymousArrayOfString.flattenToString());
        }
        break;
        label294:
        paramAnonymousFileDescriptor = paramAnonymousFileDescriptor.iterator();
        while (paramAnonymousFileDescriptor.hasNext())
        {
          paramAnonymousArrayOfString = (ComponentName)paramAnonymousFileDescriptor.next();
          paramAnonymousPrintWriter.print("  ");
          paramAnonymousPrintWriter.println(paramAnonymousArrayOfString.flattenToString());
        }
      }
    }
    
    public boolean getVrModeState()
    {
      return VrManagerService.-wrap0(VrManagerService.this);
    }
    
    public void registerListener(IVrStateCallbacks paramAnonymousIVrStateCallbacks)
    {
      VrManagerService.-wrap6(VrManagerService.this, "android.permission.ACCESS_VR_MANAGER");
      if (paramAnonymousIVrStateCallbacks == null) {
        throw new IllegalArgumentException("Callback binder object is null.");
      }
      VrManagerService.-wrap3(VrManagerService.this, paramAnonymousIVrStateCallbacks);
    }
    
    public void unregisterListener(IVrStateCallbacks paramAnonymousIVrStateCallbacks)
    {
      VrManagerService.-wrap6(VrManagerService.this, "android.permission.ACCESS_VR_MANAGER");
      if (paramAnonymousIVrStateCallbacks == null) {
        throw new IllegalArgumentException("Callback binder object is null.");
      }
      VrManagerService.-wrap10(VrManagerService.this, paramAnonymousIVrStateCallbacks);
    }
  };
  private boolean mVrModeEnabled;
  private boolean mWasDefaultGranted;
  
  public VrManagerService(Context paramContext)
  {
    super(paramContext);
  }
  
  private void addStateCallback(IVrStateCallbacks paramIVrStateCallbacks)
  {
    this.mRemoteCallbacks.register(paramIVrStateCallbacks);
  }
  
  private void changeVrModeLocked(boolean paramBoolean)
  {
    StringBuilder localStringBuilder;
    if (this.mVrModeEnabled != paramBoolean)
    {
      this.mVrModeEnabled = paramBoolean;
      localStringBuilder = new StringBuilder().append("VR mode ");
      if (!this.mVrModeEnabled) {
        break label62;
      }
    }
    label62:
    for (String str = "enabled";; str = "disabled")
    {
      Slog.i("VrManagerService", str);
      setVrModeNative(this.mVrModeEnabled);
      onVrModeChangedLocked();
      return;
    }
  }
  
  private void consumeAndApplyPendingStateLocked()
  {
    if (this.mPendingState != null)
    {
      updateCurrentVrServiceLocked(this.mPendingState.enabled, this.mPendingState.targetPackageName, this.mPendingState.userId, this.mPendingState.callingPackage);
      this.mPendingState = null;
    }
  }
  
  private static ManagedApplicationService create(Context paramContext, ComponentName paramComponentName, int paramInt)
  {
    return ManagedApplicationService.build(paramContext, paramComponentName, paramInt, 17040518, "android.settings.VR_LISTENER_SETTINGS", sBinderChecker);
  }
  
  private void createAndConnectService(ComponentName paramComponentName, int paramInt)
  {
    this.mCurrentVrService = create(this.mContext, paramComponentName, paramInt);
    this.mCurrentVrService.connect();
    Slog.i("VrManagerService", "Connecting " + paramComponentName + " for user " + paramInt);
  }
  
  private void dumpStateTransitions(PrintWriter paramPrintWriter)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    if (this.mLoggingDeque.size() == 0)
    {
      paramPrintWriter.print("  ");
      paramPrintWriter.println("None");
    }
    Iterator localIterator = this.mLoggingDeque.iterator();
    while (localIterator.hasNext())
    {
      VrState localVrState = (VrState)localIterator.next();
      paramPrintWriter.print(localSimpleDateFormat.format(new Date(localVrState.timestamp)));
      paramPrintWriter.print("  ");
      paramPrintWriter.print("State changed to:");
      paramPrintWriter.print("  ");
      if (localVrState.enabled)
      {
        str = "ENABLED";
        label121:
        paramPrintWriter.println(str);
        if (!localVrState.enabled) {
          continue;
        }
        paramPrintWriter.print("  ");
        paramPrintWriter.print("User=");
        paramPrintWriter.println(localVrState.userId);
        paramPrintWriter.print("  ");
        paramPrintWriter.print("Current VR Activity=");
        if (localVrState.callingPackage != null) {
          break label251;
        }
        str = "None";
        label183:
        paramPrintWriter.println(str);
        paramPrintWriter.print("  ");
        paramPrintWriter.print("Bound VrListenerService=");
        if (localVrState.targetPackageName != null) {
          break label263;
        }
      }
      label251:
      label263:
      for (String str = "None";; str = localVrState.targetPackageName.flattenToString())
      {
        paramPrintWriter.println(str);
        if (!localVrState.defaultPermissionsGranted) {
          break;
        }
        paramPrintWriter.print("  ");
        paramPrintWriter.println("Default permissions granted to the bound VrListenerService.");
        break;
        str = "DISABLED";
        break label121;
        str = localVrState.callingPackage.flattenToString();
        break label183;
      }
    }
  }
  
  private void enforceCallerPermission(String paramString)
  {
    if (this.mContext.checkCallingOrSelfPermission(paramString) != 0) {
      throw new SecurityException("Caller does not hold the permission " + paramString);
    }
  }
  
  private static String formatSettings(Collection<String> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      String str = (String)paramCollection.next();
      if (!"".equals(str))
      {
        if (i == 0) {
          localStringBuilder.append(':');
        }
        localStringBuilder.append(str);
        i = 0;
      }
    }
    return localStringBuilder.toString();
  }
  
  private ArraySet<String> getNotificationListeners(ContentResolver paramContentResolver, int paramInt)
  {
    Object localObject = Settings.Secure.getStringForUser(paramContentResolver, "enabled_notification_listeners", paramInt);
    paramContentResolver = new ArraySet();
    if (localObject != null)
    {
      localObject = ((String)localObject).split(":");
      paramInt = 0;
      int i = localObject.length;
      while (paramInt < i)
      {
        CharSequence localCharSequence = localObject[paramInt];
        if (!TextUtils.isEmpty(localCharSequence)) {
          paramContentResolver.add(localCharSequence);
        }
        paramInt += 1;
      }
    }
    return paramContentResolver;
  }
  
  private boolean getVrMode()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mVrModeEnabled;
      return bool;
    }
  }
  
  private void grantCoarseLocationPermissionIfNeeded(String paramString, int paramInt)
  {
    if (!isPermissionUserUpdated("android.permission.ACCESS_COARSE_LOCATION", paramString, paramInt)) {
      this.mContext.getPackageManager().grantRuntimePermission(paramString, "android.permission.ACCESS_COARSE_LOCATION", new UserHandle(paramInt));
    }
  }
  
  private void grantNotificationListenerAccess(String paramString, int paramInt)
  {
    Object localObject = EnabledComponentsObserver.loadComponentNames(this.mContext.getPackageManager(), paramInt, "android.service.notification.NotificationListenerService", "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    ArraySet localArraySet = getNotificationListeners(localContentResolver, paramInt);
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ComponentName localComponentName = (ComponentName)((Iterator)localObject).next();
      String str = localComponentName.flattenToString();
      if ((Objects.equals(localComponentName.getPackageName(), paramString)) && (!localArraySet.contains(str))) {
        localArraySet.add(str);
      }
    }
    if (localArraySet.size() > 0) {
      Settings.Secure.putStringForUser(localContentResolver, "enabled_notification_listeners", formatSettings(localArraySet), paramInt);
    }
  }
  
  private void grantNotificationPolicyAccess(String paramString)
  {
    ((NotificationManager)this.mContext.getSystemService(NotificationManager.class)).setNotificationPolicyAccessGranted(paramString, true);
  }
  
  private int hasVrPackage(ComponentName paramComponentName, int paramInt)
  {
    synchronized (this.mLock)
    {
      paramInt = this.mComponentObserver.isValid(paramComponentName, paramInt);
      return paramInt;
    }
  }
  
  private static native void initializeNative();
  
  private boolean isCurrentVrListener(String paramString, int paramInt)
  {
    boolean bool2 = false;
    synchronized (this.mLock)
    {
      ManagedApplicationService localManagedApplicationService = this.mCurrentVrService;
      if (localManagedApplicationService == null) {
        return false;
      }
      boolean bool1 = bool2;
      if (this.mCurrentVrService.getComponent().getPackageName().equals(paramString))
      {
        int i = this.mCurrentVrService.getUserId();
        bool1 = bool2;
        if (paramInt == i) {
          bool1 = true;
        }
      }
      return bool1;
    }
  }
  
  private boolean isDefaultAllowed(String paramString)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject = null;
    try
    {
      paramString = localPackageManager.getApplicationInfo(paramString, 128);
      if ((paramString != null) && ((paramString.isSystemApp()) || (paramString.isUpdatedSystemApp()))) {
        return true;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;)
      {
        paramString = (String)localObject;
      }
    }
    return false;
  }
  
  private boolean isPermissionUserUpdated(String paramString1, String paramString2, int paramInt)
  {
    boolean bool = false;
    if ((this.mContext.getPackageManager().getPermissionFlags(paramString1, paramString2, new UserHandle(paramInt)) & 0x3) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void logStateLocked()
  {
    if (this.mCurrentVrService == null) {}
    for (Object localObject = null;; localObject = this.mCurrentVrService.getComponent())
    {
      localObject = new VrState(this.mVrModeEnabled, (ComponentName)localObject, this.mCurrentVrModeUser, this.mCurrentVrModeComponent, this.mWasDefaultGranted);
      if (this.mLoggingDeque.size() == 32) {
        this.mLoggingDeque.removeFirst();
      }
      this.mLoggingDeque.add(localObject);
      return;
    }
  }
  
  private void onVrModeChangedLocked()
  {
    Handler localHandler1 = this.mHandler;
    Handler localHandler2 = this.mHandler;
    if (this.mVrModeEnabled) {}
    for (int i = 1;; i = 0)
    {
      localHandler1.sendMessage(localHandler2.obtainMessage(0, i, 0));
      return;
    }
  }
  
  private void removeStateCallback(IVrStateCallbacks paramIVrStateCallbacks)
  {
    this.mRemoteCallbacks.unregister(paramIVrStateCallbacks);
  }
  
  private void revokeCoarseLocationPermissionIfNeeded(String paramString, int paramInt)
  {
    try
    {
      if (!isPermissionUserUpdated("android.permission.ACCESS_COARSE_LOCATION", paramString, paramInt)) {
        this.mContext.getPackageManager().revokeRuntimePermission(paramString, "android.permission.ACCESS_COARSE_LOCATION", new UserHandle(paramInt));
      }
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Slog.e("VrManagerService", "Failed to revoke coarseLocation permission for package " + paramString + ". " + localIllegalArgumentException.getMessage());
    }
  }
  
  private void revokeNotificationListenerAccess(String paramString, int paramInt)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    ArraySet localArraySet = getNotificationListeners(localContentResolver, paramInt);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localArraySet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      ComponentName localComponentName = ComponentName.unflattenFromString(str);
      if ((localComponentName != null) && (localComponentName.getPackageName().equals(paramString))) {
        localArrayList.add(str);
      }
    }
    localArraySet.removeAll(localArrayList);
    Settings.Secure.putStringForUser(localContentResolver, "enabled_notification_listeners", formatSettings(localArraySet), paramInt);
  }
  
  private void revokeNotificationPolicyAccess(String paramString)
  {
    NotificationManager localNotificationManager = (NotificationManager)this.mContext.getSystemService(NotificationManager.class);
    localNotificationManager.removeAutomaticZenRules(paramString);
    localNotificationManager.setNotificationPolicyAccessGranted(paramString, false);
  }
  
  private void setVrMode(boolean paramBoolean1, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2, boolean paramBoolean2)
  {
    Object localObject = this.mLock;
    if (!paramBoolean1) {}
    try
    {
      if ((this.mCurrentVrService == null) || (paramBoolean2))
      {
        this.mHandler.removeMessages(1);
        this.mPendingState = null;
        updateCurrentVrServiceLocked(paramBoolean1, paramComponentName1, paramInt, paramComponentName2);
        return;
      }
      if (this.mPendingState == null) {
        this.mHandler.sendEmptyMessageDelayed(1, 300L);
      }
      this.mPendingState = new VrState(paramBoolean1, paramComponentName1, paramInt, paramComponentName2);
      return;
    }
    finally {}
  }
  
  private static native void setVrModeNative(boolean paramBoolean);
  
  private boolean updateCurrentVrServiceLocked(boolean paramBoolean, final ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2)
  {
    int j = 0;
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        boolean bool;
        if (this.mComponentObserver.isValid(paramComponentName1, paramInt) == 0)
        {
          bool = true;
          if ((this.mVrModeEnabled) || (paramBoolean))
          {
            if (this.mCurrentVrService == null) {
              continue;
            }
            str = this.mCurrentVrService.getComponent().getPackageName();
            int k = this.mCurrentVrModeUser;
            changeVrModeLocked(paramBoolean);
            if ((!paramBoolean) || (!bool)) {
              continue;
            }
            if (this.mCurrentVrService == null) {
              break label376;
            }
            i = j;
            if (this.mCurrentVrService.disconnectIfNotMatching(paramComponentName1, paramInt))
            {
              Slog.i("VrManagerService", "Disconnecting " + this.mCurrentVrService.getComponent() + " for user " + this.mCurrentVrService.getUserId());
              createAndConnectService(paramComponentName1, paramInt);
              i = 1;
            }
            j = i;
            if (paramComponentName2 != null)
            {
              if (!Objects.equals(paramComponentName2, this.mCurrentVrModeComponent)) {
                break label388;
              }
              j = i;
            }
            if (this.mCurrentVrModeUser != paramInt)
            {
              this.mCurrentVrModeUser = paramInt;
              j = 1;
            }
            if (this.mCurrentVrService == null) {
              break label400;
            }
            paramComponentName1 = this.mCurrentVrService.getComponent().getPackageName();
            updateDependentAppOpsLocked(paramComponentName1, this.mCurrentVrModeUser, str, k);
            if ((this.mCurrentVrService != null) && (j != 0))
            {
              paramComponentName1 = this.mCurrentVrModeComponent;
              this.mCurrentVrService.sendEvent(new ManagedApplicationService.PendingEvent()
              {
                public void runEvent(IInterface paramAnonymousIInterface)
                  throws RemoteException
                {
                  ((IVrListener)paramAnonymousIInterface).focusedActivityChanged(paramComponentName1);
                }
              });
            }
            logStateLocked();
            return bool;
          }
        }
        else
        {
          bool = false;
          continue;
        }
        return bool;
        String str = null;
        continue;
        i = j;
        if (this.mCurrentVrService == null) {
          continue;
        }
        Slog.i("VrManagerService", "Disconnecting " + this.mCurrentVrService.getComponent() + " for user " + this.mCurrentVrService.getUserId());
        this.mCurrentVrService.disconnect();
        this.mCurrentVrService = null;
        i = j;
        continue;
        createAndConnectService(paramComponentName1, paramInt);
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      label376:
      int i = 1;
      continue;
      label388:
      this.mCurrentVrModeComponent = paramComponentName2;
      j = 1;
      continue;
      label400:
      paramComponentName1 = null;
    }
  }
  
  private void updateDependentAppOpsLocked(String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    if (Objects.equals(paramString1, paramString2)) {
      return;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      updateOverlayStateLocked(paramString1, paramInt1, paramInt2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void updateOverlayStateLocked(String paramString, int paramInt1, int paramInt2)
  {
    AppOpsManager localAppOpsManager = (AppOpsManager)getContext().getSystemService(AppOpsManager.class);
    if (paramInt2 != paramInt1) {
      localAppOpsManager.setUserRestrictionForUser(24, false, this.mOverlayToken, null, paramInt2);
    }
    if (paramString == null) {}
    String[] arrayOfString;
    for (paramString = new String[0];; paramString = arrayOfString)
    {
      localAppOpsManager.setUserRestrictionForUser(24, this.mVrModeEnabled, this.mOverlayToken, paramString, paramInt1);
      return;
      arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500) {}
    synchronized (this.mLock)
    {
      Looper localLooper = Looper.getMainLooper();
      Handler localHandler = new Handler(localLooper);
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(this);
      this.mComponentObserver = EnabledComponentsObserver.build(this.mContext, localHandler, "enabled_vr_listeners", localLooper, "android.permission.BIND_VR_LISTENER_SERVICE", "android.service.vr.VrListenerService", this.mLock, localArrayList);
      this.mComponentObserver.rebuildAll();
      return;
    }
  }
  
  public void onCleanupUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mComponentObserver.onUsersChanged();
      return;
    }
  }
  
  public void onEnabledComponentChanged()
  {
    synchronized (this.mLock)
    {
      int i = ActivityManager.getCurrentUser();
      Object localObject2 = this.mComponentObserver.getEnabled(i);
      ArraySet localArraySet = new ArraySet();
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ComponentName localComponentName = (ComponentName)((Iterator)localObject2).next();
        if (isDefaultAllowed(localComponentName.getPackageName())) {
          localArraySet.add(localComponentName.getPackageName());
        }
      }
    }
    this.mNotifAccessManager.update(localCollection);
    ManagedApplicationService localManagedApplicationService = this.mCurrentVrService;
    if (localManagedApplicationService == null) {
      return;
    }
    consumeAndApplyPendingStateLocked();
    localManagedApplicationService = this.mCurrentVrService;
    if (localManagedApplicationService == null) {
      return;
    }
    updateCurrentVrServiceLocked(this.mVrModeEnabled, this.mCurrentVrService.getComponent(), this.mCurrentVrService.getUserId(), null);
  }
  
  public void onStart()
  {
    synchronized (this.mLock)
    {
      initializeNative();
      this.mContext = getContext();
      publishLocalService(VrManagerInternal.class, new LocalService(null));
      publishBinderService("vrmanager", this.mVrManager.asBinder());
      return;
    }
  }
  
  public void onStartUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mComponentObserver.onUsersChanged();
      return;
    }
  }
  
  public void onStopUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mComponentObserver.onUsersChanged();
      return;
    }
  }
  
  public void onSwitchUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mComponentObserver.onUsersChanged();
      return;
    }
  }
  
  private final class LocalService
    extends VrManagerInternal
  {
    private LocalService() {}
    
    public int hasVrPackage(ComponentName paramComponentName, int paramInt)
    {
      return VrManagerService.-wrap2(VrManagerService.this, paramComponentName, paramInt);
    }
    
    public boolean isCurrentVrListener(String paramString, int paramInt)
    {
      return VrManagerService.-wrap1(VrManagerService.this, paramString, paramInt);
    }
    
    public void setVrMode(boolean paramBoolean, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2)
    {
      VrManagerService.-wrap14(VrManagerService.this, paramBoolean, paramComponentName1, paramInt, paramComponentName2, false);
    }
    
    public void setVrModeImmediate(boolean paramBoolean, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2)
    {
      VrManagerService.-wrap14(VrManagerService.this, paramBoolean, paramComponentName1, paramInt, paramComponentName2, true);
    }
  }
  
  private final class NotificationAccessManager
  {
    private final SparseArray<ArraySet<String>> mAllowedPackages = new SparseArray();
    private final ArrayMap<String, Integer> mNotificationAccessPackageToUserId = new ArrayMap();
    
    private NotificationAccessManager() {}
    
    public void update(Collection<String> paramCollection)
    {
      int j = ActivityManager.getCurrentUser();
      Object localObject2 = (ArraySet)this.mAllowedPackages.get(j);
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new ArraySet();
      }
      int i = this.mNotificationAccessPackageToUserId.size() - 1;
      while (i >= 0)
      {
        int k = ((Integer)this.mNotificationAccessPackageToUserId.valueAt(i)).intValue();
        if (k != j)
        {
          localObject2 = (String)this.mNotificationAccessPackageToUserId.keyAt(i);
          VrManagerService.-wrap12(VrManagerService.this, (String)localObject2, k);
          VrManagerService.-wrap13(VrManagerService.this, (String)localObject2);
          VrManagerService.-wrap11(VrManagerService.this, (String)localObject2, k);
          this.mNotificationAccessPackageToUserId.removeAt(i);
        }
        i -= 1;
      }
      localObject2 = ((Iterable)localObject1).iterator();
      String str;
      while (((Iterator)localObject2).hasNext())
      {
        str = (String)((Iterator)localObject2).next();
        if (!paramCollection.contains(str))
        {
          VrManagerService.-wrap12(VrManagerService.this, str, j);
          VrManagerService.-wrap13(VrManagerService.this, str);
          VrManagerService.-wrap11(VrManagerService.this, str, j);
          this.mNotificationAccessPackageToUserId.remove(str);
        }
      }
      localObject2 = paramCollection.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        str = (String)((Iterator)localObject2).next();
        if (!((ArraySet)localObject1).contains(str))
        {
          VrManagerService.-wrap9(VrManagerService.this, str);
          VrManagerService.-wrap8(VrManagerService.this, str, j);
          VrManagerService.-wrap7(VrManagerService.this, str, j);
          this.mNotificationAccessPackageToUserId.put(str, Integer.valueOf(j));
        }
      }
      ((ArraySet)localObject1).clear();
      ((ArraySet)localObject1).addAll(paramCollection);
      this.mAllowedPackages.put(j, localObject1);
    }
  }
  
  private static class VrState
  {
    final ComponentName callingPackage;
    final boolean defaultPermissionsGranted;
    final boolean enabled;
    final ComponentName targetPackageName;
    final long timestamp;
    final int userId;
    
    VrState(boolean paramBoolean, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2)
    {
      this.enabled = paramBoolean;
      this.userId = paramInt;
      this.targetPackageName = paramComponentName1;
      this.callingPackage = paramComponentName2;
      this.defaultPermissionsGranted = false;
      this.timestamp = System.currentTimeMillis();
    }
    
    VrState(boolean paramBoolean1, ComponentName paramComponentName1, int paramInt, ComponentName paramComponentName2, boolean paramBoolean2)
    {
      this.enabled = paramBoolean1;
      this.userId = paramInt;
      this.targetPackageName = paramComponentName1;
      this.callingPackage = paramComponentName2;
      this.defaultPermissionsGranted = paramBoolean2;
      this.timestamp = System.currentTimeMillis();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/vr/VrManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */