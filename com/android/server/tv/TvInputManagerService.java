package com.android.server.tv;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.tv.DvbDeviceInfo;
import android.media.tv.ITvInputClient;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.ITvInputManager.Stub;
import android.media.tv.ITvInputManagerCallback;
import android.media.tv.ITvInputService;
import android.media.tv.ITvInputService.Stub;
import android.media.tv.ITvInputServiceCallback.Stub;
import android.media.tv.ITvInputSession;
import android.media.tv.ITvInputSessionCallback.Stub;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvContract.Programs;
import android.media.tv.TvContract.WatchedPrograms;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputInfo.Builder;
import android.media.tv.TvStreamConfig;
import android.media.tv.TvTrackInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.Surface;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.IoThread;
import com.android.server.SystemService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TvInputManagerService
  extends SystemService
{
  private static final boolean DEBUG = false;
  private static final String TAG = "TvInputManagerService";
  private static final Pattern sFrontEndDevicePattern = Pattern.compile("^dvb([0-9]+)\\.frontend([0-9]+)$");
  private final Context mContext;
  private int mCurrentUserId = 0;
  private final Object mLock = new Object();
  private final TvInputHardwareManager mTvInputHardwareManager;
  private final SparseArray<UserState> mUserStates = new SparseArray();
  private final WatchLogHandler mWatchLogHandler;
  
  public TvInputManagerService(Context arg1)
  {
    super(???);
    this.mContext = ???;
    this.mWatchLogHandler = new WatchLogHandler(this.mContext.getContentResolver(), IoThread.get().getLooper());
    this.mTvInputHardwareManager = new TvInputHardwareManager(???, new HardwareListener(null));
    synchronized (this.mLock)
    {
      getOrCreateUserStateLocked(this.mCurrentUserId);
      return;
    }
  }
  
  private void abortPendingCreateSessionRequestsLocked(ServiceState paramServiceState, String paramString, int paramInt)
  {
    Object localObject1 = getOrCreateUserStateLocked(paramInt);
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ServiceState.-get8(paramServiceState).iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (IBinder)localIterator.next();
      localObject2 = (SessionState)UserState.-get8((UserState)localObject1).get(localObject2);
      if ((SessionState.-get8((SessionState)localObject2) == null) && ((paramString == null) || (SessionState.-get4((SessionState)localObject2).equals(paramString)))) {
        localArrayList.add(localObject2);
      }
    }
    paramString = localArrayList.iterator();
    while (paramString.hasNext())
    {
      localObject1 = (SessionState)paramString.next();
      removeSessionStateLocked(SessionState.-get9((SessionState)localObject1), SessionState.-get10((SessionState)localObject1));
      sendSessionTokenToClientLocked(SessionState.-get1((SessionState)localObject1), SessionState.-get4((SessionState)localObject1), null, null, SessionState.-get7((SessionState)localObject1));
    }
    updateServiceConnectionLocked(ServiceState.-get2(paramServiceState), paramInt);
  }
  
  private void buildTvContentRatingSystemListLocked(int paramInt)
  {
    UserState localUserState = getOrCreateUserStateLocked(paramInt);
    UserState.-get2(localUserState).clear();
    Iterator localIterator = this.mContext.getPackageManager().queryBroadcastReceivers(new Intent("android.media.tv.action.QUERY_CONTENT_RATING_SYSTEMS"), 128).iterator();
    while (localIterator.hasNext())
    {
      ActivityInfo localActivityInfo = ((ResolveInfo)localIterator.next()).activityInfo;
      Bundle localBundle = localActivityInfo.metaData;
      if (localBundle != null)
      {
        paramInt = localBundle.getInt("android.media.tv.metadata.CONTENT_RATING_SYSTEMS");
        if (paramInt == 0) {
          Slog.w("TvInputManagerService", "Missing meta-data 'android.media.tv.metadata.CONTENT_RATING_SYSTEMS' on receiver " + localActivityInfo.packageName + "/" + localActivityInfo.name);
        } else {
          UserState.-get2(localUserState).add(TvContentRatingSystemInfo.createTvContentRatingSystemInfo(paramInt, localActivityInfo.applicationInfo));
        }
      }
    }
  }
  
  private void buildTvInputListLocked(int paramInt, String[] paramArrayOfString)
  {
    UserState localUserState = getOrCreateUserStateLocked(paramInt);
    UserState.-get5(localUserState).clear();
    Object localObject2 = this.mContext.getPackageManager();
    Object localObject3 = ((PackageManager)localObject2).queryIntentServicesAsUser(new Intent("android.media.tv.TvInputService"), 132, paramInt);
    Object localObject1 = new ArrayList();
    localObject3 = ((Iterable)localObject3).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      Object localObject6 = (ResolveInfo)((Iterator)localObject3).next();
      localObject4 = ((ResolveInfo)localObject6).serviceInfo;
      if (!"android.permission.BIND_TV_INPUT".equals(((ServiceInfo)localObject4).permission))
      {
        Slog.w("TvInputManagerService", "Skipping TV input " + ((ServiceInfo)localObject4).name + ": it does not require the permission " + "android.permission.BIND_TV_INPUT");
      }
      else
      {
        ComponentName localComponentName = new ComponentName(((ServiceInfo)localObject4).packageName, ((ServiceInfo)localObject4).name);
        if (hasHardwarePermission((PackageManager)localObject2, localComponentName))
        {
          localObject6 = (ServiceState)UserState.-get7(localUserState).get(localComponentName);
          if (localObject6 == null)
          {
            localObject6 = new ServiceState(localComponentName, paramInt, null);
            UserState.-get7(localUserState).put(localComponentName, localObject6);
            updateServiceConnectionLocked(localComponentName, paramInt);
          }
        }
        for (;;)
        {
          UserState.-get5(localUserState).add(((ServiceInfo)localObject4).packageName);
          break;
          ((List)localObject1).addAll(ServiceState.-get4((ServiceState)localObject6));
          continue;
          try
          {
            ((List)localObject1).add(new TvInputInfo.Builder(this.mContext, (ResolveInfo)localObject6).build());
          }
          catch (Exception localException)
          {
            Slog.e("TvInputManagerService", "failed to load TV input " + ((ServiceInfo)localObject4).name, localException);
          }
        }
      }
    }
    localObject3 = new HashMap();
    Object localObject4 = ((Iterable)localObject1).iterator();
    Object localObject5;
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (TvInputInfo)((Iterator)localObject4).next();
      localObject2 = (TvInputState)UserState.-get3(localUserState).get(((TvInputInfo)localObject5).getId());
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = new TvInputState(null);
      }
      TvInputState.-set0((TvInputState)localObject1, (TvInputInfo)localObject5);
      ((Map)localObject3).put(((TvInputInfo)localObject5).getId(), localObject1);
    }
    localObject1 = ((Map)localObject3).keySet().iterator();
    label583:
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      if (!UserState.-get3(localUserState).containsKey(localObject2))
      {
        notifyInputAddedLocked(localUserState, (String)localObject2);
      }
      else if (paramArrayOfString != null)
      {
        localObject4 = TvInputState.-get0((TvInputState)((Map)localObject3).get(localObject2)).getComponent();
        int i = 0;
        int j = paramArrayOfString.length;
        for (;;)
        {
          if (i >= j) {
            break label583;
          }
          localObject5 = paramArrayOfString[i];
          if (((ComponentName)localObject4).getPackageName().equals(localObject5))
          {
            updateServiceConnectionLocked((ComponentName)localObject4, paramInt);
            notifyInputUpdatedLocked(localUserState, (String)localObject2);
            break;
          }
          i += 1;
        }
      }
    }
    paramArrayOfString = UserState.-get3(localUserState).keySet().iterator();
    while (paramArrayOfString.hasNext())
    {
      localObject1 = (String)paramArrayOfString.next();
      if (!((Map)localObject3).containsKey(localObject1))
      {
        localObject2 = TvInputState.-get0((TvInputState)UserState.-get3(localUserState).get(localObject1));
        localObject2 = (ServiceState)UserState.-get7(localUserState).get(((TvInputInfo)localObject2).getComponent());
        if (localObject2 != null) {
          abortPendingCreateSessionRequestsLocked((ServiceState)localObject2, (String)localObject1, paramInt);
        }
        notifyInputRemovedLocked(localUserState, (String)localObject1);
      }
    }
    UserState.-get3(localUserState).clear();
    UserState.-set0(localUserState, (Map)localObject3);
  }
  
  private void clearSessionAndNotifyClientLocked(SessionState paramSessionState)
  {
    if (SessionState.-get1(paramSessionState) != null) {}
    try
    {
      SessionState.-get1(paramSessionState).onSessionReleased(SessionState.-get7(paramSessionState));
      Iterator localIterator = UserState.-get8(getOrCreateUserStateLocked(SessionState.-get10(paramSessionState))).values().iterator();
      while (localIterator.hasNext())
      {
        SessionState localSessionState = (SessionState)localIterator.next();
        if (SessionState.-get9(paramSessionState) == SessionState.-get3(localSessionState))
        {
          releaseSessionLocked(SessionState.-get9(localSessionState), 1000, SessionState.-get10(paramSessionState));
          try
          {
            SessionState.-get1(localSessionState).onSessionReleased(SessionState.-get7(localSessionState));
          }
          catch (RemoteException localRemoteException2)
          {
            Slog.e("TvInputManagerService", "error in onSessionReleased", localRemoteException2);
          }
        }
      }
    }
    catch (RemoteException localRemoteException1)
    {
      for (;;)
      {
        Slog.e("TvInputManagerService", "error in onSessionReleased", localRemoteException1);
      }
      removeSessionStateLocked(SessionState.-get9(paramSessionState), SessionState.-get10(paramSessionState));
    }
  }
  
  private void createSessionInternalLocked(ITvInputService paramITvInputService, IBinder paramIBinder, int paramInt)
  {
    SessionState localSessionState = (SessionState)UserState.-get8(getOrCreateUserStateLocked(paramInt)).get(paramIBinder);
    InputChannel[] arrayOfInputChannel = InputChannel.openInputChannelPair(paramIBinder.toString());
    SessionCallback localSessionCallback = new SessionCallback(localSessionState, arrayOfInputChannel);
    try
    {
      if (SessionState.-get5(localSessionState)) {
        paramITvInputService.createRecordingSession(localSessionCallback, SessionState.-get4(localSessionState));
      }
      for (;;)
      {
        arrayOfInputChannel[1].dispose();
        return;
        paramITvInputService.createSession(arrayOfInputChannel[1], localSessionCallback, SessionState.-get4(localSessionState));
      }
    }
    catch (RemoteException paramITvInputService)
    {
      for (;;)
      {
        Slog.e("TvInputManagerService", "error in createSession", paramITvInputService);
        removeSessionStateLocked(paramIBinder, paramInt);
        sendSessionTokenToClientLocked(SessionState.-get1(localSessionState), SessionState.-get4(localSessionState), null, null, SessionState.-get7(localSessionState));
      }
    }
  }
  
  private ContentResolver getContentResolverForUser(int paramInt)
  {
    UserHandle localUserHandle = new UserHandle(paramInt);
    try
    {
      Context localContext1 = this.mContext.createPackageContextAsUser("android", 0, localUserHandle);
      return localContext1.getContentResolver();
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Slog.e("TvInputManagerService", "failed to create package context as user " + localUserHandle);
        Context localContext2 = this.mContext;
      }
    }
  }
  
  private UserState getOrCreateUserStateLocked(int paramInt)
  {
    UserState localUserState2 = (UserState)this.mUserStates.get(paramInt);
    UserState localUserState1 = localUserState2;
    if (localUserState2 == null)
    {
      localUserState1 = new UserState(this.mContext, paramInt, null);
      this.mUserStates.put(paramInt, localUserState1);
    }
    return localUserState1;
  }
  
  private ServiceState getServiceStateLocked(ComponentName paramComponentName, int paramInt)
  {
    ServiceState localServiceState = (ServiceState)UserState.-get7(getOrCreateUserStateLocked(paramInt)).get(paramComponentName);
    if (localServiceState == null) {
      throw new IllegalStateException("Service state not found for " + paramComponentName + " (userId=" + paramInt + ")");
    }
    return localServiceState;
  }
  
  private ITvInputSession getSessionLocked(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    return getSessionLocked(getSessionStateLocked(paramIBinder, paramInt1, paramInt2));
  }
  
  private ITvInputSession getSessionLocked(SessionState paramSessionState)
  {
    ITvInputSession localITvInputSession = SessionState.-get8(paramSessionState);
    if (localITvInputSession == null) {
      throw new IllegalStateException("Session not yet created for token " + SessionState.-get9(paramSessionState));
    }
    return localITvInputSession;
  }
  
  private SessionState getSessionStateLocked(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    SessionState localSessionState = (SessionState)UserState.-get8(getOrCreateUserStateLocked(paramInt2)).get(paramIBinder);
    if (localSessionState == null) {
      throw new SessionNotFoundException("Session state not found for token " + paramIBinder);
    }
    if ((paramInt1 != 1000) && (paramInt1 != SessionState.-get0(localSessionState))) {
      throw new SecurityException("Illegal access to the session with token " + paramIBinder + " from uid " + paramInt1);
    }
    return localSessionState;
  }
  
  private static boolean hasHardwarePermission(PackageManager paramPackageManager, ComponentName paramComponentName)
  {
    boolean bool = false;
    if (paramPackageManager.checkPermission("android.permission.TV_INPUT_HARDWARE", paramComponentName.getPackageName()) == 0) {
      bool = true;
    }
    return bool;
  }
  
  private void notifyInputAddedLocked(UserState paramUserState, String paramString)
  {
    paramUserState = UserState.-get0(paramUserState).iterator();
    while (paramUserState.hasNext())
    {
      ITvInputManagerCallback localITvInputManagerCallback = (ITvInputManagerCallback)paramUserState.next();
      try
      {
        localITvInputManagerCallback.onInputAdded(paramString);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("TvInputManagerService", "failed to report added input to callback", localRemoteException);
      }
    }
  }
  
  private void notifyInputRemovedLocked(UserState paramUserState, String paramString)
  {
    paramUserState = UserState.-get0(paramUserState).iterator();
    while (paramUserState.hasNext())
    {
      ITvInputManagerCallback localITvInputManagerCallback = (ITvInputManagerCallback)paramUserState.next();
      try
      {
        localITvInputManagerCallback.onInputRemoved(paramString);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("TvInputManagerService", "failed to report removed input to callback", localRemoteException);
      }
    }
  }
  
  private void notifyInputStateChangedLocked(UserState paramUserState, String paramString, int paramInt, ITvInputManagerCallback paramITvInputManagerCallback)
  {
    if (paramITvInputManagerCallback == null)
    {
      paramUserState = UserState.-get0(paramUserState).iterator();
      while (paramUserState.hasNext())
      {
        paramITvInputManagerCallback = (ITvInputManagerCallback)paramUserState.next();
        try
        {
          paramITvInputManagerCallback.onInputStateChanged(paramString, paramInt);
        }
        catch (RemoteException paramITvInputManagerCallback)
        {
          Slog.e("TvInputManagerService", "failed to report state change to callback", paramITvInputManagerCallback);
        }
      }
    }
    try
    {
      paramITvInputManagerCallback.onInputStateChanged(paramString, paramInt);
      return;
    }
    catch (RemoteException paramUserState)
    {
      Slog.e("TvInputManagerService", "failed to report state change to callback", paramUserState);
    }
  }
  
  private void notifyInputUpdatedLocked(UserState paramUserState, String paramString)
  {
    paramUserState = UserState.-get0(paramUserState).iterator();
    while (paramUserState.hasNext())
    {
      ITvInputManagerCallback localITvInputManagerCallback = (ITvInputManagerCallback)paramUserState.next();
      try
      {
        localITvInputManagerCallback.onInputUpdated(paramString);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("TvInputManagerService", "failed to report updated input to callback", localRemoteException);
      }
    }
  }
  
  private void registerBroadcastReceivers()
  {
    new PackageMonitor()
    {
      private void buildTvInputList(String[] paramAnonymousArrayOfString)
      {
        synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
        {
          if (TvInputManagerService.-get1(TvInputManagerService.this) == getChangingUserId())
          {
            TvInputManagerService.-wrap10(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this), paramAnonymousArrayOfString);
            TvInputManagerService.-wrap9(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this));
          }
          return;
        }
      }
      
      public boolean onPackageChanged(String paramAnonymousString, int paramAnonymousInt, String[] paramAnonymousArrayOfString)
      {
        return true;
      }
      
      public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
      {
        synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
        {
          boolean bool = TvInputManagerService.UserState.-get5(TvInputManagerService.-wrap6(TvInputManagerService.this, getChangingUserId())).contains(paramAnonymousString);
          if (!bool) {
            return;
          }
          ??? = new ArrayList();
          String[] arrayOfString = new String[1];
          arrayOfString[0] = paramAnonymousString;
          ((ArrayList)???).add(ContentProviderOperation.newDelete(TvContract.Channels.CONTENT_URI).withSelection("package_name=?", arrayOfString).build());
          ((ArrayList)???).add(ContentProviderOperation.newDelete(TvContract.Programs.CONTENT_URI).withSelection("package_name=?", arrayOfString).build());
          ((ArrayList)???).add(ContentProviderOperation.newDelete(TvContract.WatchedPrograms.CONTENT_URI).withSelection("package_name=?", arrayOfString).build());
        }
        try
        {
          TvInputManagerService.-wrap0(TvInputManagerService.this, getChangingUserId()).applyBatch("android.media.tv", (ArrayList)???);
          return;
        }
        catch (RemoteException|OperationApplicationException paramAnonymousString)
        {
          Slog.e("TvInputManagerService", "error in applyBatch", paramAnonymousString);
        }
        paramAnonymousString = finally;
        throw paramAnonymousString;
      }
      
      public void onPackageUpdateFinished(String paramAnonymousString, int paramAnonymousInt)
      {
        buildTvInputList(new String[] { paramAnonymousString });
      }
      
      public void onPackagesAvailable(String[] paramAnonymousArrayOfString)
      {
        if (isReplacing()) {
          buildTvInputList(paramAnonymousArrayOfString);
        }
      }
      
      public void onPackagesUnavailable(String[] paramAnonymousArrayOfString)
      {
        if (isReplacing()) {
          buildTvInputList(paramAnonymousArrayOfString);
        }
      }
      
      public void onSomePackagesChanged()
      {
        if (isReplacing()) {
          return;
        }
        buildTvInputList(null);
      }
    }.register(this.mContext, null, UserHandle.ALL, true);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiverAsUser(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        paramAnonymousContext = paramAnonymousIntent.getAction();
        if ("android.intent.action.USER_SWITCHED".equals(paramAnonymousContext)) {
          TvInputManagerService.-wrap20(TvInputManagerService.this, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0));
        }
        while (!"android.intent.action.USER_REMOVED".equals(paramAnonymousContext)) {
          return;
        }
        TvInputManagerService.-wrap16(TvInputManagerService.this, paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0));
      }
    }, UserHandle.ALL, localIntentFilter, null, null);
  }
  
  private void releaseSessionLocked(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    localObject2 = null;
    localObject1 = null;
    try
    {
      localSessionState = getSessionStateLocked(paramIBinder, paramInt1, paramInt2);
      localObject1 = localSessionState;
      localObject2 = localSessionState;
      if (SessionState.-get8(localSessionState) != null)
      {
        localObject1 = localSessionState;
        localObject2 = localSessionState;
        if (paramIBinder == UserState.-get4(getOrCreateUserStateLocked(paramInt2)))
        {
          localObject1 = localSessionState;
          localObject2 = localSessionState;
          setMainLocked(paramIBinder, false, paramInt1, paramInt2);
        }
        localObject1 = localSessionState;
        localObject2 = localSessionState;
        SessionState.-get8(localSessionState).release();
      }
    }
    catch (RemoteException|SessionNotFoundException localRemoteException)
    {
      for (;;)
      {
        SessionState localSessionState;
        localObject2 = localObject1;
        Slog.e("TvInputManagerService", "error in releaseSession", localRemoteException);
        if (localObject1 != null) {
          SessionState.-set1((SessionState)localObject1, null);
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label149;
      }
      SessionState.-set1((SessionState)localObject2, null);
    }
    removeSessionStateLocked(paramIBinder, paramInt2);
  }
  
  private void removeSessionStateLocked(IBinder paramIBinder, int paramInt)
  {
    Object localObject2 = getOrCreateUserStateLocked(paramInt);
    if (paramIBinder == UserState.-get4((UserState)localObject2)) {
      UserState.-set1((UserState)localObject2, null);
    }
    Object localObject1 = (SessionState)UserState.-get8((UserState)localObject2).remove(paramIBinder);
    if (localObject1 == null) {
      return;
    }
    ClientState localClientState = (ClientState)UserState.-get1((UserState)localObject2).get(SessionState.-get1((SessionState)localObject1).asBinder());
    if (localClientState != null)
    {
      ClientState.-get1(localClientState).remove(paramIBinder);
      if (localClientState.isEmpty()) {
        UserState.-get1((UserState)localObject2).remove(SessionState.-get1((SessionState)localObject1).asBinder());
      }
    }
    localObject2 = (ServiceState)UserState.-get7((UserState)localObject2).get(SessionState.-get2((SessionState)localObject1));
    if (localObject2 != null) {
      ServiceState.-get8((ServiceState)localObject2).remove(paramIBinder);
    }
    updateServiceConnectionLocked(SessionState.-get2((SessionState)localObject1), paramInt);
    localObject1 = SomeArgs.obtain();
    ((SomeArgs)localObject1).arg1 = paramIBinder;
    ((SomeArgs)localObject1).arg2 = Long.valueOf(System.currentTimeMillis());
    this.mWatchLogHandler.obtainMessage(2, localObject1).sendToTarget();
  }
  
  private void removeUser(int paramInt)
  {
    Object localObject2;
    synchronized (this.mLock)
    {
      UserState localUserState1 = (UserState)this.mUserStates.get(paramInt);
      if (localUserState1 == null) {
        return;
      }
      localIterator = UserState.-get8(localUserState1).values().iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          SessionState localSessionState = (SessionState)localIterator.next();
          localObject2 = SessionState.-get8(localSessionState);
          if (localObject2 == null) {
            continue;
          }
          try
          {
            SessionState.-get8(localSessionState).release();
          }
          catch (RemoteException localRemoteException1)
          {
            Slog.e("TvInputManagerService", "error in release", localRemoteException1);
          }
        }
      }
    }
    UserState.-get8(localUserState2).clear();
    Iterator localIterator = UserState.-get7(localUserState2).values().iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        ServiceState localServiceState = (ServiceState)localIterator.next();
        if (ServiceState.-get7(localServiceState) == null) {
          continue;
        }
        localObject2 = ServiceState.-get1(localServiceState);
        if (localObject2 != null) {}
        try
        {
          ServiceState.-get7(localServiceState).unregisterCallback(ServiceState.-get1(localServiceState));
          this.mContext.unbindService(ServiceState.-get3(localServiceState));
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in unregisterCallback", localRemoteException2);
          }
        }
      }
    }
    UserState.-get7(localUserState2).clear();
    UserState.-get3(localUserState2).clear();
    UserState.-get5(localUserState2).clear();
    UserState.-get2(localUserState2).clear();
    UserState.-get1(localUserState2).clear();
    UserState.-get0(localUserState2).clear();
    UserState.-set1(localUserState2, null);
    this.mUserStates.remove(paramInt);
  }
  
  private int resolveCallingUserId(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    return ActivityManager.handleIncomingUser(paramInt1, paramInt2, paramInt3, false, false, paramString, null);
  }
  
  private void sendSessionTokenToClientLocked(ITvInputClient paramITvInputClient, String paramString, IBinder paramIBinder, InputChannel paramInputChannel, int paramInt)
  {
    try
    {
      paramITvInputClient.onSessionCreated(paramString, paramIBinder, paramInputChannel, paramInt);
      return;
    }
    catch (RemoteException paramITvInputClient)
    {
      Slog.e("TvInputManagerService", "error in onSessionCreated", paramITvInputClient);
    }
  }
  
  private void setMainLocked(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    try
    {
      SessionState localSessionState = getSessionStateLocked(paramIBinder, paramInt1, paramInt2);
      paramIBinder = localSessionState;
      if (SessionState.-get3(localSessionState) != null) {
        paramIBinder = getSessionStateLocked(SessionState.-get3(localSessionState), 1000, paramInt2);
      }
      if (!ServiceState.-get5(getServiceStateLocked(SessionState.-get2(paramIBinder), paramInt2))) {
        return;
      }
      getSessionLocked(paramIBinder).setMain(paramBoolean);
      return;
    }
    catch (RemoteException|SessionNotFoundException paramIBinder)
    {
      Slog.e("TvInputManagerService", "error in setMain", paramIBinder);
    }
  }
  
  private void setStateLocked(String paramString, int paramInt1, int paramInt2)
  {
    UserState localUserState = getOrCreateUserStateLocked(paramInt2);
    TvInputState localTvInputState = (TvInputState)UserState.-get3(localUserState).get(paramString);
    ServiceState localServiceState = (ServiceState)UserState.-get7(localUserState).get(TvInputState.-get0(localTvInputState).getComponent());
    paramInt2 = TvInputState.-get1(localTvInputState);
    TvInputState.-set1(localTvInputState, paramInt1);
    if ((localServiceState != null) && (ServiceState.-get7(localServiceState) == null) && ((!ServiceState.-get8(localServiceState).isEmpty()) || (ServiceState.-get5(localServiceState)))) {
      return;
    }
    if (paramInt2 != paramInt1) {
      notifyInputStateChangedLocked(localUserState, paramString, paramInt1, null);
    }
  }
  
  private void switchUser(int paramInt)
  {
    Object localObject3;
    synchronized (this.mLock)
    {
      int i = this.mCurrentUserId;
      if (i == paramInt) {
        return;
      }
      UserState localUserState1 = (UserState)this.mUserStates.get(this.mCurrentUserId);
      localObject2 = new ArrayList();
      localObject3 = UserState.-get8(localUserState1).values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        SessionState localSessionState = (SessionState)((Iterator)localObject3).next();
        if ((SessionState.-get8(localSessionState) != null) && (!SessionState.-get5(localSessionState))) {
          ((List)localObject2).add(localSessionState);
        }
      }
    }
    Object localObject2 = ((Iterable)localObject2).iterator();
    for (;;)
    {
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (SessionState)((Iterator)localObject2).next();
        try
        {
          SessionState.-get8((SessionState)localObject3).release();
          clearSessionAndNotifyClientLocked((SessionState)localObject3);
        }
        catch (RemoteException localRemoteException1)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in release", localRemoteException1);
          }
        }
      }
    }
    localObject2 = UserState.-get7(localUserState2).keySet().iterator();
    for (;;)
    {
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ComponentName)((Iterator)localObject2).next();
        localObject3 = (ServiceState)UserState.-get7(localUserState2).get(localObject3);
        if ((localObject3 == null) || (!ServiceState.-get8((ServiceState)localObject3).isEmpty())) {
          continue;
        }
        ServiceCallback localServiceCallback = ServiceState.-get1((ServiceState)localObject3);
        if (localServiceCallback != null) {}
        try
        {
          ServiceState.-get7((ServiceState)localObject3).unregisterCallback(ServiceState.-get1((ServiceState)localObject3));
          this.mContext.unbindService(ServiceState.-get3((ServiceState)localObject3));
          ((Iterator)localObject2).remove();
        }
        catch (RemoteException localRemoteException2)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in unregisterCallback", localRemoteException2);
          }
        }
      }
    }
    this.mCurrentUserId = paramInt;
    getOrCreateUserStateLocked(paramInt);
    buildTvInputListLocked(paramInt, null);
    buildTvContentRatingSystemListLocked(paramInt);
    this.mWatchLogHandler.obtainMessage(3, getContentResolverForUser(paramInt)).sendToTarget();
  }
  
  private void updateServiceConnectionLocked(ComponentName paramComponentName, int paramInt)
  {
    UserState localUserState = getOrCreateUserStateLocked(paramInt);
    ServiceState localServiceState = (ServiceState)UserState.-get7(localUserState).get(paramComponentName);
    if (localServiceState == null) {
      return;
    }
    if (ServiceState.-get6(localServiceState))
    {
      if (!ServiceState.-get8(localServiceState).isEmpty()) {
        return;
      }
      ServiceState.-set2(localServiceState, false);
    }
    boolean bool;
    if (paramInt == this.mCurrentUserId) {
      if (ServiceState.-get8(localServiceState).isEmpty()) {
        bool = ServiceState.-get5(localServiceState);
      }
    }
    while ((ServiceState.-get7(localServiceState) == null) && (bool)) {
      if (ServiceState.-get0(localServiceState))
      {
        return;
        bool = true;
        continue;
        if (ServiceState.-get8(localServiceState).isEmpty()) {
          bool = false;
        } else {
          bool = true;
        }
      }
      else
      {
        paramComponentName = new Intent("android.media.tv.TvInputService").setComponent(paramComponentName);
        ServiceState.-set0(localServiceState, this.mContext.bindServiceAsUser(paramComponentName, ServiceState.-get3(localServiceState), 33554433, new UserHandle(paramInt)));
      }
    }
    while ((ServiceState.-get7(localServiceState) == null) || (bool)) {
      return;
    }
    this.mContext.unbindService(ServiceState.-get3(localServiceState));
    UserState.-get7(localUserState).remove(paramComponentName);
  }
  
  private void updateTvInputInfoLocked(UserState paramUserState, TvInputInfo paramTvInputInfo)
  {
    Object localObject = paramTvInputInfo.getId();
    TvInputState localTvInputState = (TvInputState)UserState.-get3(paramUserState).get(localObject);
    if (localTvInputState == null)
    {
      Slog.e("TvInputManagerService", "failed to set input info - unknown input id " + (String)localObject);
      return;
    }
    TvInputState.-set0(localTvInputState, paramTvInputInfo);
    paramUserState = UserState.-get0(paramUserState).iterator();
    while (paramUserState.hasNext())
    {
      localObject = (ITvInputManagerCallback)paramUserState.next();
      try
      {
        ((ITvInputManagerCallback)localObject).onTvInputInfoUpdated(paramTvInputInfo);
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("TvInputManagerService", "failed to report updated input info to callback", localRemoteException);
      }
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500) {
      registerBroadcastReceivers();
    }
    for (;;)
    {
      this.mTvInputHardwareManager.onBootPhase(paramInt);
      return;
      if (paramInt != 600) {
        continue;
      }
      synchronized (this.mLock)
      {
        buildTvInputListLocked(this.mCurrentUserId, null);
        buildTvContentRatingSystemListLocked(this.mCurrentUserId);
      }
    }
  }
  
  public void onStart()
  {
    publishBinderService("tv_input", new BinderService(null));
  }
  
  public void onUnlockUser(int paramInt)
  {
    synchronized (this.mLock)
    {
      int i = this.mCurrentUserId;
      if (i != paramInt) {
        return;
      }
      buildTvInputListLocked(this.mCurrentUserId, null);
      buildTvContentRatingSystemListLocked(this.mCurrentUserId);
      return;
    }
  }
  
  private final class BinderService
    extends ITvInputManager.Stub
  {
    private BinderService() {}
    
    private void ensureParentalControlsPermission()
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.MODIFY_PARENTAL_CONTROLS") != 0) {
        throw new SecurityException("The caller does not have parental controls permission");
      }
    }
    
    private String getCallingPackageName()
    {
      String[] arrayOfString = TvInputManagerService.-get0(TvInputManagerService.this).getPackageManager().getPackagesForUid(Binder.getCallingUid());
      if ((arrayOfString != null) && (arrayOfString.length > 0)) {
        return arrayOfString[0];
      }
      return "unknown";
    }
    
    public ITvInputHardware acquireTvInputHardware(int paramInt1, ITvInputHardwareCallback paramITvInputHardwareCallback, TvInputInfo paramTvInputInfo, int paramInt2)
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
        return null;
      }
      long l = Binder.clearCallingIdentity();
      int i = Binder.getCallingUid();
      paramInt2 = TvInputManagerService.-wrap7(TvInputManagerService.this, Binder.getCallingPid(), i, paramInt2, "acquireTvInputHardware");
      try
      {
        paramITvInputHardwareCallback = TvInputManagerService.-get3(TvInputManagerService.this).acquireHardware(paramInt1, paramITvInputHardwareCallback, paramTvInputInfo, i, paramInt2);
        return paramITvInputHardwareCallback;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void addBlockedRating(String paramString, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 99	com/android/server/tv/TvInputManagerService$BinderService:ensureParentalControlsPermission	()V
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   14: iload_2
      //   15: ldc 100
      //   17: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   20: istore_2
      //   21: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore_3
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 5
      //   34: aload 5
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: iload_2
      //   42: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   45: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   48: aload_1
      //   49: invokestatic 120	android/media/tv/TvContentRating:unflattenFromString	(Ljava/lang/String;)Landroid/media/tv/TvContentRating;
      //   52: invokevirtual 125	com/android/server/tv/PersistentDataStore:addBlockedRating	(Landroid/media/tv/TvContentRating;)V
      //   55: aload 5
      //   57: monitorexit
      //   58: lload_3
      //   59: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   62: return
      //   63: astore_1
      //   64: aload 5
      //   66: monitorexit
      //   67: aload_1
      //   68: athrow
      //   69: astore_1
      //   70: lload_3
      //   71: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   74: aload_1
      //   75: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	76	0	this	BinderService
      //   0	76	1	paramString	String
      //   0	76	2	paramInt	int
      //   24	47	3	l	long
      // Exception table:
      //   from	to	target	type
      //   37	55	63	finally
      //   25	37	69	finally
      //   55	58	69	finally
      //   64	69	69	finally
    }
    
    public boolean captureFrame(String paramString, Surface paramSurface, TvStreamConfig paramTvStreamConfig, int paramInt)
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.CAPTURE_TV_INPUT") != 0) {
        throw new SecurityException("Requires CAPTURE_TV_INPUT permission");
      }
      long l = Binder.clearCallingIdentity();
      int i = Binder.getCallingUid();
      paramInt = TvInputManagerService.-wrap7(TvInputManagerService.this, Binder.getCallingPid(), i, paramInt, "captureFrame");
      TvInputHardwareManager localTvInputHardwareManager = null;
      for (;;)
      {
        try
        {
          synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
          {
            TvInputManagerService.UserState localUserState = TvInputManagerService.-wrap6(TvInputManagerService.this, paramInt);
            if (TvInputManagerService.UserState.-get3(localUserState).get(paramString) == null)
            {
              Slog.e("TvInputManagerService", "input not found for " + paramString);
              return false;
            }
            Iterator localIterator = TvInputManagerService.UserState.-get8(localUserState).values().iterator();
            localObject1 = localTvInputHardwareManager;
            if (localIterator.hasNext())
            {
              localObject1 = (TvInputManagerService.SessionState)localIterator.next();
              if ((!TvInputManagerService.SessionState.-get4((TvInputManagerService.SessionState)localObject1).equals(paramString)) || (TvInputManagerService.SessionState.-get3((TvInputManagerService.SessionState)localObject1) == null)) {
                continue;
              }
              localObject1 = TvInputManagerService.SessionState.-get4((TvInputManagerService.SessionState)TvInputManagerService.UserState.-get8(localUserState).get(TvInputManagerService.SessionState.-get3((TvInputManagerService.SessionState)localObject1)));
            }
            localTvInputHardwareManager = TvInputManagerService.-get3(TvInputManagerService.this);
            if (localObject1 != null)
            {
              boolean bool = localTvInputHardwareManager.captureFrame((String)localObject1, paramSurface, paramTvStreamConfig, i, paramInt);
              return bool;
            }
          }
          Object localObject1 = paramString;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    /* Error */
    public void createOverlayView(IBinder paramIBinder1, IBinder paramIBinder2, android.graphics.Rect paramRect, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 5
      //   14: iload 4
      //   16: ldc -49
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore 4
      //   23: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   26: lstore 6
      //   28: aload_0
      //   29: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   32: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   35: astore 8
      //   37: aload 8
      //   39: monitorenter
      //   40: aload_0
      //   41: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   44: aload_1
      //   45: iload 5
      //   47: iload 4
      //   49: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   52: aload_2
      //   53: aload_3
      //   54: invokeinterface 216 3 0
      //   59: aload 8
      //   61: monitorexit
      //   62: lload 6
      //   64: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   67: return
      //   68: astore_1
      //   69: ldc -113
      //   71: ldc -38
      //   73: aload_1
      //   74: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   77: pop
      //   78: goto -19 -> 59
      //   81: astore_1
      //   82: aload 8
      //   84: monitorexit
      //   85: aload_1
      //   86: athrow
      //   87: astore_1
      //   88: lload 6
      //   90: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   93: aload_1
      //   94: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	95	0	this	BinderService
      //   0	95	1	paramIBinder1	IBinder
      //   0	95	2	paramIBinder2	IBinder
      //   0	95	3	paramRect	android.graphics.Rect
      //   0	95	4	paramInt	int
      //   3	43	5	i	int
      //   26	63	6	l	long
      // Exception table:
      //   from	to	target	type
      //   40	59	68	android/os/RemoteException
      //   40	59	68	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   40	59	81	finally
      //   69	78	81	finally
      //   28	40	87	finally
      //   59	62	87	finally
      //   82	87	87	finally
    }
    
    /* Error */
    public void createSession(ITvInputClient paramITvInputClient, String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 6
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 6
      //   14: iload 5
      //   16: ldc -32
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore 7
      //   23: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   26: lstore 8
      //   28: aload_0
      //   29: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   32: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   35: astore 12
      //   37: aload 12
      //   39: monitorenter
      //   40: iload 5
      //   42: aload_0
      //   43: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   46: invokestatic 228	com/android/server/tv/TvInputManagerService:-get1	(Lcom/android/server/tv/TvInputManagerService;)I
      //   49: if_icmpeq +7 -> 56
      //   52: iload_3
      //   53: ifeq +82 -> 135
      //   56: aload_0
      //   57: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   60: iload 7
      //   62: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   65: astore 13
      //   67: aload 13
      //   69: invokestatic 135	com/android/server/tv/TvInputManagerService$UserState:-get3	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   72: aload_2
      //   73: invokeinterface 141 2 0
      //   78: checkcast 230	com/android/server/tv/TvInputManagerService$TvInputState
      //   81: astore 10
      //   83: aload 10
      //   85: ifnonnull +72 -> 157
      //   88: ldc -113
      //   90: new 145	java/lang/StringBuilder
      //   93: dup
      //   94: invokespecial 146	java/lang/StringBuilder:<init>	()V
      //   97: ldc -24
      //   99: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   102: aload_2
      //   103: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   106: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   109: invokestatic 235	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   112: pop
      //   113: aload_0
      //   114: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   117: aload_1
      //   118: aload_2
      //   119: aconst_null
      //   120: aconst_null
      //   121: iload 4
      //   123: invokestatic 239	com/android/server/tv/TvInputManagerService:-wrap17	(Lcom/android/server/tv/TvInputManagerService;Landroid/media/tv/ITvInputClient;Ljava/lang/String;Landroid/os/IBinder;Landroid/view/InputChannel;I)V
      //   126: aload 12
      //   128: monitorexit
      //   129: lload 8
      //   131: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   134: return
      //   135: aload_0
      //   136: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   139: aload_1
      //   140: aload_2
      //   141: aconst_null
      //   142: aconst_null
      //   143: iload 4
      //   145: invokestatic 239	com/android/server/tv/TvInputManagerService:-wrap17	(Lcom/android/server/tv/TvInputManagerService;Landroid/media/tv/ITvInputClient;Ljava/lang/String;Landroid/os/IBinder;Landroid/view/InputChannel;I)V
      //   148: aload 12
      //   150: monitorexit
      //   151: lload 8
      //   153: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   156: return
      //   157: aload 10
      //   159: invokestatic 242	com/android/server/tv/TvInputManagerService$TvInputState:-get0	(Lcom/android/server/tv/TvInputManagerService$TvInputState;)Landroid/media/tv/TvInputInfo;
      //   162: astore 14
      //   164: aload 13
      //   166: invokestatic 245	com/android/server/tv/TvInputManagerService$UserState:-get7	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   169: aload 14
      //   171: invokevirtual 251	android/media/tv/TvInputInfo:getComponent	()Landroid/content/ComponentName;
      //   174: invokeinterface 141 2 0
      //   179: checkcast 253	com/android/server/tv/TvInputManagerService$ServiceState
      //   182: astore 11
      //   184: aload 11
      //   186: astore 10
      //   188: aload 11
      //   190: ifnonnull +42 -> 232
      //   193: new 253	com/android/server/tv/TvInputManagerService$ServiceState
      //   196: dup
      //   197: aload_0
      //   198: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   201: aload 14
      //   203: invokevirtual 251	android/media/tv/TvInputInfo:getComponent	()Landroid/content/ComponentName;
      //   206: iload 7
      //   208: aconst_null
      //   209: invokespecial 256	com/android/server/tv/TvInputManagerService$ServiceState:<init>	(Lcom/android/server/tv/TvInputManagerService;Landroid/content/ComponentName;ILcom/android/server/tv/TvInputManagerService$ServiceState;)V
      //   212: astore 10
      //   214: aload 13
      //   216: invokestatic 245	com/android/server/tv/TvInputManagerService$UserState:-get7	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   219: aload 14
      //   221: invokevirtual 251	android/media/tv/TvInputInfo:getComponent	()Landroid/content/ComponentName;
      //   224: aload 10
      //   226: invokeinterface 260 3 0
      //   231: pop
      //   232: aload 10
      //   234: invokestatic 263	com/android/server/tv/TvInputManagerService$ServiceState:-get6	(Lcom/android/server/tv/TvInputManagerService$ServiceState;)Z
      //   237: ifeq +25 -> 262
      //   240: aload_0
      //   241: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   244: aload_1
      //   245: aload_2
      //   246: aconst_null
      //   247: aconst_null
      //   248: iload 4
      //   250: invokestatic 239	com/android/server/tv/TvInputManagerService:-wrap17	(Lcom/android/server/tv/TvInputManagerService;Landroid/media/tv/ITvInputClient;Ljava/lang/String;Landroid/os/IBinder;Landroid/view/InputChannel;I)V
      //   253: aload 12
      //   255: monitorexit
      //   256: lload 8
      //   258: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   261: return
      //   262: new 50	android/os/Binder
      //   265: dup
      //   266: invokespecial 264	android/os/Binder:<init>	()V
      //   269: astore_2
      //   270: new 186	com/android/server/tv/TvInputManagerService$SessionState
      //   273: dup
      //   274: aload_0
      //   275: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   278: aload_2
      //   279: aload 14
      //   281: invokevirtual 267	android/media/tv/TvInputInfo:getId	()Ljava/lang/String;
      //   284: aload 14
      //   286: invokevirtual 251	android/media/tv/TvInputInfo:getComponent	()Landroid/content/ComponentName;
      //   289: iload_3
      //   290: aload_1
      //   291: iload 4
      //   293: iload 6
      //   295: iload 7
      //   297: aconst_null
      //   298: invokespecial 270	com/android/server/tv/TvInputManagerService$SessionState:<init>	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;Ljava/lang/String;Landroid/content/ComponentName;ZLandroid/media/tv/ITvInputClient;IIILcom/android/server/tv/TvInputManagerService$SessionState;)V
      //   301: astore_1
      //   302: aload 13
      //   304: invokestatic 164	com/android/server/tv/TvInputManagerService$UserState:-get8	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   307: aload_2
      //   308: aload_1
      //   309: invokeinterface 260 3 0
      //   314: pop
      //   315: aload 10
      //   317: invokestatic 273	com/android/server/tv/TvInputManagerService$ServiceState:-get8	(Lcom/android/server/tv/TvInputManagerService$ServiceState;)Ljava/util/List;
      //   320: aload_2
      //   321: invokeinterface 278 2 0
      //   326: pop
      //   327: aload 10
      //   329: invokestatic 281	com/android/server/tv/TvInputManagerService$ServiceState:-get7	(Lcom/android/server/tv/TvInputManagerService$ServiceState;)Landroid/media/tv/ITvInputService;
      //   332: ifnull +27 -> 359
      //   335: aload_0
      //   336: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   339: aload 10
      //   341: invokestatic 281	com/android/server/tv/TvInputManagerService$ServiceState:-get7	(Lcom/android/server/tv/TvInputManagerService$ServiceState;)Landroid/media/tv/ITvInputService;
      //   344: aload_2
      //   345: iload 7
      //   347: invokestatic 285	com/android/server/tv/TvInputManagerService:-wrap12	(Lcom/android/server/tv/TvInputManagerService;Landroid/media/tv/ITvInputService;Landroid/os/IBinder;I)V
      //   350: aload 12
      //   352: monitorexit
      //   353: lload 8
      //   355: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   358: return
      //   359: aload_0
      //   360: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   363: aload 14
      //   365: invokevirtual 251	android/media/tv/TvInputInfo:getComponent	()Landroid/content/ComponentName;
      //   368: iload 7
      //   370: invokestatic 289	com/android/server/tv/TvInputManagerService:-wrap21	(Lcom/android/server/tv/TvInputManagerService;Landroid/content/ComponentName;I)V
      //   373: goto -23 -> 350
      //   376: astore_1
      //   377: aload 12
      //   379: monitorexit
      //   380: aload_1
      //   381: athrow
      //   382: astore_1
      //   383: lload 8
      //   385: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   388: aload_1
      //   389: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	390	0	this	BinderService
      //   0	390	1	paramITvInputClient	ITvInputClient
      //   0	390	2	paramString	String
      //   0	390	3	paramBoolean	boolean
      //   0	390	4	paramInt1	int
      //   0	390	5	paramInt2	int
      //   3	291	6	i	int
      //   21	348	7	j	int
      //   26	358	8	l	long
      //   81	259	10	localObject1	Object
      //   182	7	11	localServiceState	TvInputManagerService.ServiceState
      //   65	238	13	localUserState	TvInputManagerService.UserState
      //   162	202	14	localTvInputInfo	TvInputInfo
      // Exception table:
      //   from	to	target	type
      //   40	52	376	finally
      //   56	83	376	finally
      //   88	126	376	finally
      //   135	148	376	finally
      //   157	184	376	finally
      //   193	232	376	finally
      //   232	253	376	finally
      //   262	350	376	finally
      //   359	373	376	finally
      //   28	40	382	finally
      //   126	129	382	finally
      //   148	151	382	finally
      //   253	256	382	finally
      //   350	353	382	finally
      //   377	382	382	finally
    }
    
    /* Error */
    public void dispatchSurfaceChanged(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 6
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 6
      //   14: iload 5
      //   16: ldc_w 292
      //   19: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   22: istore 5
      //   24: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   27: lstore 7
      //   29: aload_0
      //   30: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   33: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   36: astore 9
      //   38: aload 9
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   45: aload_1
      //   46: iload 6
      //   48: iload 5
      //   50: invokestatic 296	com/android/server/tv/TvInputManagerService:-wrap5	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Lcom/android/server/tv/TvInputManagerService$SessionState;
      //   53: astore_1
      //   54: aload_0
      //   55: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   58: aload_1
      //   59: invokestatic 300	com/android/server/tv/TvInputManagerService:-wrap2	(Lcom/android/server/tv/TvInputManagerService;Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/media/tv/ITvInputSession;
      //   62: iload_2
      //   63: iload_3
      //   64: iload 4
      //   66: invokeinterface 303 4 0
      //   71: aload_1
      //   72: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   75: ifnull +28 -> 103
      //   78: aload_0
      //   79: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   82: aload_1
      //   83: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   86: sipush 1000
      //   89: iload 5
      //   91: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   94: iload_2
      //   95: iload_3
      //   96: iload 4
      //   98: invokeinterface 303 4 0
      //   103: aload 9
      //   105: monitorexit
      //   106: lload 7
      //   108: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   111: return
      //   112: astore_1
      //   113: ldc -113
      //   115: ldc_w 305
      //   118: aload_1
      //   119: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   122: pop
      //   123: goto -20 -> 103
      //   126: astore_1
      //   127: aload 9
      //   129: monitorexit
      //   130: aload_1
      //   131: athrow
      //   132: astore_1
      //   133: lload 7
      //   135: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   138: aload_1
      //   139: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	140	0	this	BinderService
      //   0	140	1	paramIBinder	IBinder
      //   0	140	2	paramInt1	int
      //   0	140	3	paramInt2	int
      //   0	140	4	paramInt3	int
      //   0	140	5	paramInt4	int
      //   3	44	6	i	int
      //   27	107	7	l	long
      // Exception table:
      //   from	to	target	type
      //   41	103	112	android/os/RemoteException
      //   41	103	112	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   41	103	126	finally
      //   113	123	126	finally
      //   29	41	132	finally
      //   103	106	132	finally
      //   127	132	132	finally
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      IndentingPrintWriter localIndentingPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        localIndentingPrintWriter.println("Permission Denial: can't dump TvInputManager from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      for (;;)
      {
        int i;
        TvInputManagerService.UserState localUserState;
        Object localObject2;
        synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
        {
          localIndentingPrintWriter.println("User Ids (Current user: " + TvInputManagerService.-get1(TvInputManagerService.this) + "):");
          localIndentingPrintWriter.increaseIndent();
          i = 0;
          if (i < TvInputManagerService.-get4(TvInputManagerService.this).size())
          {
            localIndentingPrintWriter.println(Integer.valueOf(TvInputManagerService.-get4(TvInputManagerService.this).keyAt(i)));
            i += 1;
            continue;
          }
          localIndentingPrintWriter.decreaseIndent();
          i = 0;
          if (i >= TvInputManagerService.-get4(TvInputManagerService.this).size()) {
            break;
          }
          int j = TvInputManagerService.-get4(TvInputManagerService.this).keyAt(i);
          localUserState = TvInputManagerService.-wrap6(TvInputManagerService.this, j);
          localIndentingPrintWriter.println("UserState (" + j + "):");
          localIndentingPrintWriter.increaseIndent();
          localIndentingPrintWriter.println("inputMap: inputId -> TvInputState");
          localIndentingPrintWriter.increaseIndent();
          localIterator = TvInputManagerService.UserState.-get3(localUserState).entrySet().iterator();
          if (localIterator.hasNext())
          {
            localObject2 = (Map.Entry)localIterator.next();
            localIndentingPrintWriter.println((String)((Map.Entry)localObject2).getKey() + ": " + ((Map.Entry)localObject2).getValue());
          }
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("packageSet:");
        localIndentingPrintWriter.increaseIndent();
        Iterator localIterator = TvInputManagerService.UserState.-get5(localUserState).iterator();
        while (localIterator.hasNext()) {
          localIndentingPrintWriter.println((String)localIterator.next());
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("clientStateMap: ITvInputClient -> ClientState");
        localIndentingPrintWriter.increaseIndent();
        localIterator = TvInputManagerService.UserState.-get1(localUserState).entrySet().iterator();
        Object localObject3;
        IBinder localIBinder;
        while (localIterator.hasNext())
        {
          localObject3 = (Map.Entry)localIterator.next();
          localObject2 = (TvInputManagerService.ClientState)((Map.Entry)localObject3).getValue();
          localIndentingPrintWriter.println(((Map.Entry)localObject3).getKey() + ": " + localObject2);
          localIndentingPrintWriter.increaseIndent();
          localIndentingPrintWriter.println("sessionTokens:");
          localIndentingPrintWriter.increaseIndent();
          localObject3 = TvInputManagerService.ClientState.-get1((TvInputManagerService.ClientState)localObject2).iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localIBinder = (IBinder)((Iterator)localObject3).next();
            localIndentingPrintWriter.println("" + localIBinder);
          }
          localIndentingPrintWriter.decreaseIndent();
          localIndentingPrintWriter.println("clientTokens: " + TvInputManagerService.ClientState.-get0((TvInputManagerService.ClientState)localObject2));
          localIndentingPrintWriter.println("userId: " + TvInputManagerService.ClientState.-get2((TvInputManagerService.ClientState)localObject2));
          localIndentingPrintWriter.decreaseIndent();
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("serviceStateMap: ComponentName -> ServiceState");
        localIndentingPrintWriter.increaseIndent();
        localIterator = TvInputManagerService.UserState.-get7(localUserState).entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject3 = (Map.Entry)localIterator.next();
          localObject2 = (TvInputManagerService.ServiceState)((Map.Entry)localObject3).getValue();
          localIndentingPrintWriter.println(((Map.Entry)localObject3).getKey() + ": " + localObject2);
          localIndentingPrintWriter.increaseIndent();
          localIndentingPrintWriter.println("sessionTokens:");
          localIndentingPrintWriter.increaseIndent();
          localObject3 = TvInputManagerService.ServiceState.-get8((TvInputManagerService.ServiceState)localObject2).iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localIBinder = (IBinder)((Iterator)localObject3).next();
            localIndentingPrintWriter.println("" + localIBinder);
          }
          localIndentingPrintWriter.decreaseIndent();
          localIndentingPrintWriter.println("service: " + TvInputManagerService.ServiceState.-get7((TvInputManagerService.ServiceState)localObject2));
          localIndentingPrintWriter.println("callback: " + TvInputManagerService.ServiceState.-get1((TvInputManagerService.ServiceState)localObject2));
          localIndentingPrintWriter.println("bound: " + TvInputManagerService.ServiceState.-get0((TvInputManagerService.ServiceState)localObject2));
          localIndentingPrintWriter.println("reconnecting: " + TvInputManagerService.ServiceState.-get6((TvInputManagerService.ServiceState)localObject2));
          localIndentingPrintWriter.decreaseIndent();
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("sessionStateMap: ITvInputSession -> SessionState");
        localIndentingPrintWriter.increaseIndent();
        localIterator = TvInputManagerService.UserState.-get8(localUserState).entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (Map.Entry)localIterator.next();
          localObject3 = (TvInputManagerService.SessionState)((Map.Entry)localObject2).getValue();
          localIndentingPrintWriter.println(((Map.Entry)localObject2).getKey() + ": " + localObject3);
          localIndentingPrintWriter.increaseIndent();
          localIndentingPrintWriter.println("inputId: " + TvInputManagerService.SessionState.-get4((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("client: " + TvInputManagerService.SessionState.-get1((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("seq: " + TvInputManagerService.SessionState.-get7((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("callingUid: " + TvInputManagerService.SessionState.-get0((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("userId: " + TvInputManagerService.SessionState.-get10((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("sessionToken: " + TvInputManagerService.SessionState.-get9((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("session: " + TvInputManagerService.SessionState.-get8((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("logUri: " + TvInputManagerService.SessionState.-get6((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.println("hardwareSessionToken: " + TvInputManagerService.SessionState.-get3((TvInputManagerService.SessionState)localObject3));
          localIndentingPrintWriter.decreaseIndent();
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("callbackSet:");
        localIndentingPrintWriter.increaseIndent();
        localIterator = TvInputManagerService.UserState.-get0(localUserState).iterator();
        while (localIterator.hasNext()) {
          localIndentingPrintWriter.println(((ITvInputManagerCallback)localIterator.next()).toString());
        }
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("mainSessionToken: " + TvInputManagerService.UserState.-get4(localUserState));
        localIndentingPrintWriter.decreaseIndent();
        i += 1;
      }
      TvInputManagerService.-get3(TvInputManagerService.this).dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    public List<TvStreamConfig> getAvailableTvStreamConfigList(String paramString, int paramInt)
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.CAPTURE_TV_INPUT") != 0) {
        throw new SecurityException("Requires CAPTURE_TV_INPUT permission");
      }
      long l = Binder.clearCallingIdentity();
      int i = Binder.getCallingUid();
      paramInt = TvInputManagerService.-wrap7(TvInputManagerService.this, Binder.getCallingPid(), i, paramInt, "getAvailableTvStreamConfigList");
      try
      {
        paramString = TvInputManagerService.-get3(TvInputManagerService.this).getAvailableTvStreamConfigList(paramString, i, paramInt);
        return paramString;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public List<String> getBlockedRatings(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_1
      //   11: ldc_w 495
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_1
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_3
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_1
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: astore 7
      //   44: new 497	java/util/ArrayList
      //   47: dup
      //   48: invokespecial 498	java/util/ArrayList:<init>	()V
      //   51: astore 6
      //   53: aload 7
      //   55: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   58: invokevirtual 501	com/android/server/tv/PersistentDataStore:getBlockedRatings	()[Landroid/media/tv/TvContentRating;
      //   61: astore 7
      //   63: iconst_0
      //   64: istore_1
      //   65: aload 7
      //   67: arraylength
      //   68: istore_2
      //   69: iload_1
      //   70: iload_2
      //   71: if_icmpge +25 -> 96
      //   74: aload 6
      //   76: aload 7
      //   78: iload_1
      //   79: aaload
      //   80: invokevirtual 504	android/media/tv/TvContentRating:flattenToString	()Ljava/lang/String;
      //   83: invokeinterface 278 2 0
      //   88: pop
      //   89: iload_1
      //   90: iconst_1
      //   91: iadd
      //   92: istore_1
      //   93: goto -24 -> 69
      //   96: aload 5
      //   98: monitorexit
      //   99: lload_3
      //   100: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   103: aload 6
      //   105: areturn
      //   106: astore 6
      //   108: aload 5
      //   110: monitorexit
      //   111: aload 6
      //   113: athrow
      //   114: astore 5
      //   116: lload_3
      //   117: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   120: aload 5
      //   122: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	123	0	this	BinderService
      //   0	123	1	paramInt	int
      //   68	4	2	i	int
      //   21	96	3	l	long
      //   114	7	5	localObject2	Object
      //   51	53	6	localArrayList	ArrayList
      //   106	6	6	localObject3	Object
      //   42	35	7	localObject4	Object
      // Exception table:
      //   from	to	target	type
      //   34	63	106	finally
      //   65	69	106	finally
      //   74	89	106	finally
      //   22	34	114	finally
      //   96	99	114	finally
      //   108	114	114	finally
    }
    
    public List<DvbDeviceInfo> getDvbDeviceList()
      throws RemoteException
    {
      int i = 0;
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.DVB_DEVICE") != 0) {
        throw new SecurityException("Requires DVB_DEVICE permission");
      }
      long l = Binder.clearCallingIdentity();
      for (;;)
      {
        try
        {
          Object localObject1 = new ArrayList();
          String[] arrayOfString = new File("/dev").list();
          int j = arrayOfString.length;
          if (i < j)
          {
            Object localObject3 = arrayOfString[i];
            localObject3 = TvInputManagerService.-get6().matcher((CharSequence)localObject3);
            if (((Matcher)localObject3).find()) {
              ((ArrayList)localObject1).add(new DvbDeviceInfo(Integer.parseInt(((Matcher)localObject3).group(1)), Integer.parseInt(((Matcher)localObject3).group(2))));
            }
          }
          else
          {
            localObject1 = Collections.unmodifiableList((List)localObject1);
            return (List<DvbDeviceInfo>)localObject1;
          }
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        i += 1;
      }
    }
    
    public List<TvInputHardwareInfo> getHardwareList()
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
        return null;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        List localList = TvInputManagerService.-get3(TvInputManagerService.this).getHardwareList();
        return localList;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public List<TvContentRatingSystemInfo> getTvContentRatingSystemList(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_1
      //   11: ldc_w 560
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_1
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_2
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 4
      //   31: aload 4
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_1
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: invokestatic 563	com/android/server/tv/TvInputManagerService$UserState:-get2	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/List;
      //   45: astore 5
      //   47: aload 4
      //   49: monitorexit
      //   50: lload_2
      //   51: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   54: aload 5
      //   56: areturn
      //   57: astore 5
      //   59: aload 4
      //   61: monitorexit
      //   62: aload 5
      //   64: athrow
      //   65: astore 4
      //   67: lload_2
      //   68: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   71: aload 4
      //   73: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	74	0	this	BinderService
      //   0	74	1	paramInt	int
      //   21	47	2	l	long
      //   65	7	4	localObject2	Object
      //   45	10	5	localList	List
      //   57	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   34	47	57	finally
      //   22	34	65	finally
      //   47	50	65	finally
      //   59	65	65	finally
    }
    
    /* Error */
    public TvInputInfo getTvInputInfo(String paramString, int paramInt)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore 5
      //   3: aload_0
      //   4: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   7: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   10: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   13: iload_2
      //   14: ldc_w 567
      //   17: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   20: istore_2
      //   21: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore_3
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: iload_2
      //   42: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   45: invokestatic 135	com/android/server/tv/TvInputManagerService$UserState:-get3	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   48: aload_1
      //   49: invokeinterface 141 2 0
      //   54: checkcast 230	com/android/server/tv/TvInputManagerService$TvInputState
      //   57: astore_1
      //   58: aload_1
      //   59: ifnonnull +15 -> 74
      //   62: aload 5
      //   64: astore_1
      //   65: aload 6
      //   67: monitorexit
      //   68: lload_3
      //   69: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   72: aload_1
      //   73: areturn
      //   74: aload_1
      //   75: invokestatic 242	com/android/server/tv/TvInputManagerService$TvInputState:-get0	(Lcom/android/server/tv/TvInputManagerService$TvInputState;)Landroid/media/tv/TvInputInfo;
      //   78: astore_1
      //   79: goto -14 -> 65
      //   82: astore_1
      //   83: aload 6
      //   85: monitorexit
      //   86: aload_1
      //   87: athrow
      //   88: astore_1
      //   89: lload_3
      //   90: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   93: aload_1
      //   94: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	95	0	this	BinderService
      //   0	95	1	paramString	String
      //   0	95	2	paramInt	int
      //   24	66	3	l	long
      //   1	62	5	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   37	58	82	finally
      //   74	79	82	finally
      //   25	37	88	finally
      //   65	68	88	finally
      //   83	88	88	finally
    }
    
    public List<TvInputInfo> getTvInputList(int paramInt)
    {
      paramInt = TvInputManagerService.-wrap7(TvInputManagerService.this, Binder.getCallingPid(), Binder.getCallingUid(), paramInt, "getTvInputList");
      long l = Binder.clearCallingIdentity();
      try
      {
        synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
        {
          Object localObject3 = TvInputManagerService.-wrap6(TvInputManagerService.this, paramInt);
          ArrayList localArrayList = new ArrayList();
          localObject3 = TvInputManagerService.UserState.-get3((TvInputManagerService.UserState)localObject3).values().iterator();
          if (((Iterator)localObject3).hasNext()) {
            localArrayList.add(TvInputManagerService.TvInputState.-get0((TvInputManagerService.TvInputState)((Iterator)localObject3).next()));
          }
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      Binder.restoreCallingIdentity(l);
      return localList;
    }
    
    /* Error */
    public int getTvInputState(String paramString, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_2
      //   11: ldc_w 573
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_2
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_3
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_2
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: invokestatic 135	com/android/server/tv/TvInputManagerService$UserState:-get3	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   45: aload_1
      //   46: invokeinterface 141 2 0
      //   51: checkcast 230	com/android/server/tv/TvInputManagerService$TvInputState
      //   54: astore_1
      //   55: aload_1
      //   56: ifnonnull +14 -> 70
      //   59: iconst_0
      //   60: istore_2
      //   61: aload 5
      //   63: monitorexit
      //   64: lload_3
      //   65: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: iload_2
      //   69: ireturn
      //   70: aload_1
      //   71: invokestatic 576	com/android/server/tv/TvInputManagerService$TvInputState:-get1	(Lcom/android/server/tv/TvInputManagerService$TvInputState;)I
      //   74: istore_2
      //   75: goto -14 -> 61
      //   78: astore_1
      //   79: aload 5
      //   81: monitorexit
      //   82: aload_1
      //   83: athrow
      //   84: astore_1
      //   85: lload_3
      //   86: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   89: aload_1
      //   90: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	91	0	this	BinderService
      //   0	91	1	paramString	String
      //   0	91	2	paramInt	int
      //   21	65	3	l	long
      // Exception table:
      //   from	to	target	type
      //   34	55	78	finally
      //   70	75	78	finally
      //   22	34	84	finally
      //   61	64	84	finally
      //   79	84	84	finally
    }
    
    /* Error */
    public boolean isParentalControlsEnabled(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_1
      //   11: ldc_w 579
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_1
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_2
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_1
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   45: invokevirtual 581	com/android/server/tv/PersistentDataStore:isParentalControlsEnabled	()Z
      //   48: istore 4
      //   50: aload 5
      //   52: monitorexit
      //   53: lload_2
      //   54: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   57: iload 4
      //   59: ireturn
      //   60: astore 6
      //   62: aload 5
      //   64: monitorexit
      //   65: aload 6
      //   67: athrow
      //   68: astore 5
      //   70: lload_2
      //   71: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   74: aload 5
      //   76: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	BinderService
      //   0	77	1	paramInt	int
      //   21	50	2	l	long
      //   48	10	4	bool	boolean
      //   68	7	5	localObject2	Object
      //   60	6	6	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   34	50	60	finally
      //   22	34	68	finally
      //   50	53	68	finally
      //   62	68	68	finally
    }
    
    /* Error */
    public boolean isRatingBlocked(String paramString, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_2
      //   11: ldc_w 584
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_2
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_3
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 6
      //   31: aload 6
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_2
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   45: aload_1
      //   46: invokestatic 120	android/media/tv/TvContentRating:unflattenFromString	(Ljava/lang/String;)Landroid/media/tv/TvContentRating;
      //   49: invokevirtual 587	com/android/server/tv/PersistentDataStore:isRatingBlocked	(Landroid/media/tv/TvContentRating;)Z
      //   52: istore 5
      //   54: aload 6
      //   56: monitorexit
      //   57: lload_3
      //   58: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   61: iload 5
      //   63: ireturn
      //   64: astore_1
      //   65: aload 6
      //   67: monitorexit
      //   68: aload_1
      //   69: athrow
      //   70: astore_1
      //   71: lload_3
      //   72: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   75: aload_1
      //   76: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	BinderService
      //   0	77	1	paramString	String
      //   0	77	2	paramInt	int
      //   21	51	3	l	long
      //   52	10	5	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   34	54	64	finally
      //   22	34	70	finally
      //   54	57	70	finally
      //   65	70	70	finally
    }
    
    /* Error */
    public boolean isSingleSessionActive(int paramInt)
      throws RemoteException
    {
      // Byte code:
      //   0: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_3
      //   4: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   7: istore_2
      //   8: aload_0
      //   9: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   12: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   15: iload_2
      //   16: iload_1
      //   17: ldc_w 589
      //   20: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   23: istore_1
      //   24: aload_0
      //   25: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   28: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   31: astore 5
      //   33: aload 5
      //   35: monitorenter
      //   36: aload_0
      //   37: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   40: iload_1
      //   41: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   44: astore 6
      //   46: aload 6
      //   48: invokestatic 164	com/android/server/tv/TvInputManagerService$UserState:-get8	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   51: invokeinterface 590 1 0
      //   56: istore_1
      //   57: iload_1
      //   58: iconst_1
      //   59: if_icmpne +12 -> 71
      //   62: aload 5
      //   64: monitorexit
      //   65: lload_3
      //   66: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   69: iconst_1
      //   70: ireturn
      //   71: aload 6
      //   73: invokestatic 164	com/android/server/tv/TvInputManagerService$UserState:-get8	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   76: invokeinterface 590 1 0
      //   81: iconst_2
      //   82: if_icmpne +60 -> 142
      //   85: aload 6
      //   87: invokestatic 164	com/android/server/tv/TvInputManagerService$UserState:-get8	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   90: invokeinterface 168 1 0
      //   95: iconst_2
      //   96: anewarray 186	com/android/server/tv/TvInputManagerService$SessionState
      //   99: invokeinterface 596 2 0
      //   104: checkcast 598	[Lcom/android/server/tv/TvInputManagerService$SessionState;
      //   107: astore 6
      //   109: aload 6
      //   111: iconst_0
      //   112: aaload
      //   113: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   116: ifnonnull +17 -> 133
      //   119: aload 6
      //   121: iconst_1
      //   122: aaload
      //   123: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   126: astore 6
      //   128: aload 6
      //   130: ifnull +12 -> 142
      //   133: aload 5
      //   135: monitorexit
      //   136: lload_3
      //   137: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   140: iconst_1
      //   141: ireturn
      //   142: aload 5
      //   144: monitorexit
      //   145: lload_3
      //   146: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   149: iconst_0
      //   150: ireturn
      //   151: astore 6
      //   153: aload 5
      //   155: monitorexit
      //   156: aload 6
      //   158: athrow
      //   159: astore 5
      //   161: lload_3
      //   162: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   165: aload 5
      //   167: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	168	0	this	BinderService
      //   0	168	1	paramInt	int
      //   7	9	2	i	int
      //   3	159	3	l	long
      //   159	7	5	localObject2	Object
      //   44	85	6	localObject3	Object
      //   151	6	6	localObject4	Object
      // Exception table:
      //   from	to	target	type
      //   36	57	151	finally
      //   71	128	151	finally
      //   24	36	159	finally
      //   62	65	159	finally
      //   133	136	159	finally
      //   142	145	159	finally
      //   153	159	159	finally
    }
    
    public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo paramDvbDeviceInfo, int paramInt)
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.DVB_DEVICE") != 0) {
        throw new SecurityException("Requires DVB_DEVICE permission");
      }
      long l = Binder.clearCallingIdentity();
      switch (paramInt)
      {
      default: 
        try
        {
          throw new IllegalArgumentException("Invalid DVB device: " + paramInt);
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      case 0: 
        paramDvbDeviceInfo = String.format("/dev/dvb%d.demux%d", new Object[] { Integer.valueOf(paramDvbDeviceInfo.getAdapterId()), Integer.valueOf(paramDvbDeviceInfo.getDeviceId()) });
      }
      try
      {
        paramDvbDeviceInfo = new File(paramDvbDeviceInfo);
        if (2 == paramInt) {}
        for (paramInt = 805306368;; paramInt = 268435456)
        {
          paramDvbDeviceInfo = ParcelFileDescriptor.open(paramDvbDeviceInfo, paramInt);
          Binder.restoreCallingIdentity(l);
          return paramDvbDeviceInfo;
          paramDvbDeviceInfo = String.format("/dev/dvb%d.dvr%d", new Object[] { Integer.valueOf(paramDvbDeviceInfo.getAdapterId()), Integer.valueOf(paramDvbDeviceInfo.getDeviceId()) });
          break;
          paramDvbDeviceInfo = String.format("/dev/dvb%d.frontend%d", new Object[] { Integer.valueOf(paramDvbDeviceInfo.getAdapterId()), Integer.valueOf(paramDvbDeviceInfo.getDeviceId()) });
          break;
        }
        return null;
      }
      catch (FileNotFoundException paramDvbDeviceInfo)
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void registerCallback(final ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_2
      //   11: ldc_w 634
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_2
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_3
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_2
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: astore 6
      //   44: aload 6
      //   46: invokestatic 472	com/android/server/tv/TvInputManagerService$UserState:-get0	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Set;
      //   49: aload_1
      //   50: invokeinterface 637 2 0
      //   55: pop
      //   56: aload_1
      //   57: invokeinterface 641 1 0
      //   62: new 9	com/android/server/tv/TvInputManagerService$BinderService$1
      //   65: dup
      //   66: aload_0
      //   67: aload 6
      //   69: aload_1
      //   70: invokespecial 644	com/android/server/tv/TvInputManagerService$BinderService$1:<init>	(Lcom/android/server/tv/TvInputManagerService$BinderService;Lcom/android/server/tv/TvInputManagerService$UserState;Landroid/media/tv/ITvInputManagerCallback;)V
      //   73: iconst_0
      //   74: invokeinterface 648 3 0
      //   79: aload 5
      //   81: monitorexit
      //   82: lload_3
      //   83: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   86: return
      //   87: astore_1
      //   88: ldc -113
      //   90: ldc_w 650
      //   93: aload_1
      //   94: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   97: pop
      //   98: goto -19 -> 79
      //   101: astore_1
      //   102: aload 5
      //   104: monitorexit
      //   105: aload_1
      //   106: athrow
      //   107: astore_1
      //   108: lload_3
      //   109: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   112: aload_1
      //   113: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	114	0	this	BinderService
      //   0	114	1	paramITvInputManagerCallback	ITvInputManagerCallback
      //   0	114	2	paramInt	int
      //   21	88	3	l	long
      //   42	26	6	localUserState	TvInputManagerService.UserState
      // Exception table:
      //   from	to	target	type
      //   56	79	87	android/os/RemoteException
      //   34	56	101	finally
      //   56	79	101	finally
      //   88	98	101	finally
      //   22	34	107	finally
      //   79	82	107	finally
      //   102	107	107	finally
    }
    
    /* Error */
    public void relayoutOverlayView(IBinder paramIBinder, android.graphics.Rect paramRect, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 653
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: aload_2
      //   51: invokeinterface 656 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 658
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramRect	android.graphics.Rect
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void releaseSession(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 661
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore_2
      //   20: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore 4
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: aload_1
      //   42: iload_3
      //   43: iload_2
      //   44: invokestatic 665	com/android/server/tv/TvInputManagerService:-wrap14	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)V
      //   47: aload 6
      //   49: monitorexit
      //   50: lload 4
      //   52: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   55: return
      //   56: astore_1
      //   57: aload 6
      //   59: monitorexit
      //   60: aload_1
      //   61: athrow
      //   62: astore_1
      //   63: lload 4
      //   65: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: aload_1
      //   69: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	70	0	this	BinderService
      //   0	70	1	paramIBinder	IBinder
      //   0	70	2	paramInt	int
      //   3	40	3	i	int
      //   23	41	4	l	long
      // Exception table:
      //   from	to	target	type
      //   37	47	56	finally
      //   25	37	62	finally
      //   47	50	62	finally
      //   57	62	62	finally
    }
    
    public void releaseTvInputHardware(int paramInt1, ITvInputHardware paramITvInputHardware, int paramInt2)
      throws RemoteException
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      int i = Binder.getCallingUid();
      paramInt2 = TvInputManagerService.-wrap7(TvInputManagerService.this, Binder.getCallingPid(), i, paramInt2, "releaseTvInputHardware");
      try
      {
        TvInputManagerService.-get3(TvInputManagerService.this).releaseHardware(paramInt1, paramITvInputHardware, i, paramInt2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    /* Error */
    public void removeBlockedRating(String paramString, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 99	com/android/server/tv/TvInputManagerService$BinderService:ensureParentalControlsPermission	()V
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   14: iload_2
      //   15: ldc_w 674
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_2
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore_3
      //   26: aload_0
      //   27: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   30: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   33: astore 5
      //   35: aload 5
      //   37: monitorenter
      //   38: aload_0
      //   39: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   42: iload_2
      //   43: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   46: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   49: aload_1
      //   50: invokestatic 120	android/media/tv/TvContentRating:unflattenFromString	(Ljava/lang/String;)Landroid/media/tv/TvContentRating;
      //   53: invokevirtual 676	com/android/server/tv/PersistentDataStore:removeBlockedRating	(Landroid/media/tv/TvContentRating;)V
      //   56: aload 5
      //   58: monitorexit
      //   59: lload_3
      //   60: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   63: return
      //   64: astore_1
      //   65: aload 5
      //   67: monitorexit
      //   68: aload_1
      //   69: athrow
      //   70: astore_1
      //   71: lload_3
      //   72: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   75: aload_1
      //   76: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	BinderService
      //   0	77	1	paramString	String
      //   0	77	2	paramInt	int
      //   25	47	3	l	long
      // Exception table:
      //   from	to	target	type
      //   38	56	64	finally
      //   26	38	70	finally
      //   56	59	70	finally
      //   65	70	70	finally
    }
    
    /* Error */
    public void removeOverlayView(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 678
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore_2
      //   20: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore 4
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: aload_1
      //   42: iload_3
      //   43: iload_2
      //   44: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   47: invokeinterface 680 1 0
      //   52: aload 6
      //   54: monitorexit
      //   55: lload 4
      //   57: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   60: return
      //   61: astore_1
      //   62: ldc -113
      //   64: ldc_w 682
      //   67: aload_1
      //   68: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   71: pop
      //   72: goto -20 -> 52
      //   75: astore_1
      //   76: aload 6
      //   78: monitorexit
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload 4
      //   84: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   87: aload_1
      //   88: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	BinderService
      //   0	89	1	paramIBinder	IBinder
      //   0	89	2	paramInt	int
      //   3	40	3	i	int
      //   23	60	4	l	long
      // Exception table:
      //   from	to	target	type
      //   37	52	61	android/os/RemoteException
      //   37	52	61	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   37	52	75	finally
      //   62	72	75	finally
      //   25	37	81	finally
      //   52	55	81	finally
      //   76	81	81	finally
    }
    
    /* Error */
    public void selectTrack(IBinder paramIBinder, int paramInt1, String paramString, int paramInt2)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 5
      //   14: iload 4
      //   16: ldc_w 685
      //   19: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   22: istore 4
      //   24: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   27: lstore 6
      //   29: aload_0
      //   30: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   33: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   36: astore 8
      //   38: aload 8
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   45: aload_1
      //   46: iload 5
      //   48: iload 4
      //   50: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   53: iload_2
      //   54: aload_3
      //   55: invokeinterface 688 3 0
      //   60: aload 8
      //   62: monitorexit
      //   63: lload 6
      //   65: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: return
      //   69: astore_1
      //   70: ldc -113
      //   72: ldc_w 690
      //   75: aload_1
      //   76: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   79: pop
      //   80: goto -20 -> 60
      //   83: astore_1
      //   84: aload 8
      //   86: monitorexit
      //   87: aload_1
      //   88: athrow
      //   89: astore_1
      //   90: lload 6
      //   92: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   95: aload_1
      //   96: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	BinderService
      //   0	97	1	paramIBinder	IBinder
      //   0	97	2	paramInt1	int
      //   0	97	3	paramString	String
      //   0	97	4	paramInt2	int
      //   3	44	5	i	int
      //   27	64	6	l	long
      // Exception table:
      //   from	to	target	type
      //   41	60	69	android/os/RemoteException
      //   41	60	69	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   41	60	83	finally
      //   70	80	83	finally
      //   29	41	89	finally
      //   60	63	89	finally
      //   84	89	89	finally
    }
    
    /* Error */
    public void sendAppPrivateCommand(IBinder paramIBinder, String paramString, Bundle paramBundle, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 5
      //   14: iload 4
      //   16: ldc_w 693
      //   19: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   22: istore 4
      //   24: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   27: lstore 6
      //   29: aload_0
      //   30: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   33: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   36: astore 8
      //   38: aload 8
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   45: aload_1
      //   46: iload 5
      //   48: iload 4
      //   50: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   53: aload_2
      //   54: aload_3
      //   55: invokeinterface 697 3 0
      //   60: aload 8
      //   62: monitorexit
      //   63: lload 6
      //   65: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: return
      //   69: astore_1
      //   70: ldc -113
      //   72: ldc_w 699
      //   75: aload_1
      //   76: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   79: pop
      //   80: goto -20 -> 60
      //   83: astore_1
      //   84: aload 8
      //   86: monitorexit
      //   87: aload_1
      //   88: athrow
      //   89: astore_1
      //   90: lload 6
      //   92: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   95: aload_1
      //   96: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	BinderService
      //   0	97	1	paramIBinder	IBinder
      //   0	97	2	paramString	String
      //   0	97	3	paramBundle	Bundle
      //   0	97	4	paramInt	int
      //   3	44	5	i	int
      //   27	64	6	l	long
      // Exception table:
      //   from	to	target	type
      //   41	60	69	android/os/RemoteException
      //   41	60	69	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   41	60	83	finally
      //   70	80	83	finally
      //   29	41	89	finally
      //   60	63	89	finally
      //   84	89	89	finally
    }
    
    /* Error */
    public void setCaptionEnabled(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 702
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: iload_2
      //   51: invokeinterface 705 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 707
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramBoolean	boolean
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void setMainSession(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 709
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore 4
      //   21: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   24: lstore 5
      //   26: aload_0
      //   27: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   30: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   33: astore 7
      //   35: aload 7
      //   37: monitorenter
      //   38: aload_0
      //   39: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   42: iload 4
      //   44: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   47: astore 8
      //   49: aload 8
      //   51: invokestatic 482	com/android/server/tv/TvInputManagerService$UserState:-get4	(Lcom/android/server/tv/TvInputManagerService$UserState;)Landroid/os/IBinder;
      //   54: astore 9
      //   56: aload 9
      //   58: aload_1
      //   59: if_acmpne +12 -> 71
      //   62: aload 7
      //   64: monitorexit
      //   65: lload 5
      //   67: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   70: return
      //   71: aload 8
      //   73: invokestatic 482	com/android/server/tv/TvInputManagerService$UserState:-get4	(Lcom/android/server/tv/TvInputManagerService$UserState;)Landroid/os/IBinder;
      //   76: astore 9
      //   78: aload 8
      //   80: aload_1
      //   81: invokestatic 713	com/android/server/tv/TvInputManagerService$UserState:-set1	(Lcom/android/server/tv/TvInputManagerService$UserState;Landroid/os/IBinder;)Landroid/os/IBinder;
      //   84: pop
      //   85: aload_1
      //   86: ifnull +14 -> 100
      //   89: aload_0
      //   90: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   93: aload_1
      //   94: iconst_1
      //   95: iload_3
      //   96: iload_2
      //   97: invokestatic 717	com/android/server/tv/TvInputManagerService:-wrap18	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;ZII)V
      //   100: aload 9
      //   102: ifnull +17 -> 119
      //   105: aload_0
      //   106: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   109: aload 9
      //   111: iconst_0
      //   112: sipush 1000
      //   115: iload_2
      //   116: invokestatic 717	com/android/server/tv/TvInputManagerService:-wrap18	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;ZII)V
      //   119: aload 7
      //   121: monitorexit
      //   122: lload 5
      //   124: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   127: return
      //   128: astore_1
      //   129: aload 7
      //   131: monitorexit
      //   132: aload_1
      //   133: athrow
      //   134: astore_1
      //   135: lload 5
      //   137: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   140: aload_1
      //   141: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	142	0	this	BinderService
      //   0	142	1	paramIBinder	IBinder
      //   0	142	2	paramInt	int
      //   3	93	3	i	int
      //   19	24	4	j	int
      //   24	112	5	l	long
      //   47	32	8	localUserState	TvInputManagerService.UserState
      //   54	56	9	localIBinder	IBinder
      // Exception table:
      //   from	to	target	type
      //   38	56	128	finally
      //   71	85	128	finally
      //   89	100	128	finally
      //   105	119	128	finally
      //   26	38	134	finally
      //   62	65	134	finally
      //   119	122	134	finally
      //   129	134	134	finally
    }
    
    /* Error */
    public void setParentalControlsEnabled(boolean paramBoolean, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 99	com/android/server/tv/TvInputManagerService$BinderService:ensureParentalControlsPermission	()V
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   14: iload_2
      //   15: ldc_w 720
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_2
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore_3
      //   26: aload_0
      //   27: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   30: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   33: astore 5
      //   35: aload 5
      //   37: monitorenter
      //   38: aload_0
      //   39: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   42: iload_2
      //   43: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   46: invokestatic 114	com/android/server/tv/TvInputManagerService$UserState:-get6	(Lcom/android/server/tv/TvInputManagerService$UserState;)Lcom/android/server/tv/PersistentDataStore;
      //   49: iload_1
      //   50: invokevirtual 722	com/android/server/tv/PersistentDataStore:setParentalControlsEnabled	(Z)V
      //   53: aload 5
      //   55: monitorexit
      //   56: lload_3
      //   57: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   60: return
      //   61: astore 6
      //   63: aload 5
      //   65: monitorexit
      //   66: aload 6
      //   68: athrow
      //   69: astore 5
      //   71: lload_3
      //   72: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   75: aload 5
      //   77: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	78	0	this	BinderService
      //   0	78	1	paramBoolean	boolean
      //   0	78	2	paramInt	int
      //   25	47	3	l	long
      //   69	7	5	localObject2	Object
      //   61	6	6	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   38	53	61	finally
      //   26	38	69	finally
      //   53	56	69	finally
      //   63	69	69	finally
    }
    
    /* Error */
    public void setSurface(IBinder paramIBinder, Surface paramSurface, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 725
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 296	com/android/server/tv/TvInputManagerService:-wrap5	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Lcom/android/server/tv/TvInputManagerService$SessionState;
      //   50: astore_1
      //   51: aload_1
      //   52: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   55: ifnonnull +34 -> 89
      //   58: aload_0
      //   59: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   62: aload_1
      //   63: invokestatic 300	com/android/server/tv/TvInputManagerService:-wrap2	(Lcom/android/server/tv/TvInputManagerService;Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/media/tv/ITvInputSession;
      //   66: aload_2
      //   67: invokeinterface 728 2 0
      //   72: aload 7
      //   74: monitorexit
      //   75: aload_2
      //   76: ifnull +7 -> 83
      //   79: aload_2
      //   80: invokevirtual 733	android/view/Surface:release	()V
      //   83: lload 5
      //   85: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   88: return
      //   89: aload_0
      //   90: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   93: aload_1
      //   94: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   97: sipush 1000
      //   100: iload_3
      //   101: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   104: aload_2
      //   105: invokeinterface 728 2 0
      //   110: goto -38 -> 72
      //   113: astore_1
      //   114: ldc -113
      //   116: ldc_w 735
      //   119: aload_1
      //   120: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   123: pop
      //   124: goto -52 -> 72
      //   127: astore_1
      //   128: aload 7
      //   130: monitorexit
      //   131: aload_1
      //   132: athrow
      //   133: astore_1
      //   134: aload_2
      //   135: ifnull +7 -> 142
      //   138: aload_2
      //   139: invokevirtual 733	android/view/Surface:release	()V
      //   142: lload 5
      //   144: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   147: aload_1
      //   148: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	149	0	this	BinderService
      //   0	149	1	paramIBinder	IBinder
      //   0	149	2	paramSurface	Surface
      //   0	149	3	paramInt	int
      //   3	42	4	i	int
      //   25	118	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	72	113	android/os/RemoteException
      //   39	72	113	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   89	110	113	android/os/RemoteException
      //   89	110	113	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	72	127	finally
      //   89	110	127	finally
      //   114	124	127	finally
      //   27	39	133	finally
      //   72	75	133	finally
      //   128	133	133	finally
    }
    
    /* Error */
    public void setVolume(IBinder paramIBinder, float paramFloat, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 738
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 296	com/android/server/tv/TvInputManagerService:-wrap5	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Lcom/android/server/tv/TvInputManagerService$SessionState;
      //   50: astore_1
      //   51: aload_0
      //   52: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   55: aload_1
      //   56: invokestatic 300	com/android/server/tv/TvInputManagerService:-wrap2	(Lcom/android/server/tv/TvInputManagerService;Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/media/tv/ITvInputSession;
      //   59: fload_2
      //   60: invokeinterface 741 2 0
      //   65: aload_1
      //   66: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   69: ifnull +34 -> 103
      //   72: aload_0
      //   73: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   76: aload_1
      //   77: invokestatic 199	com/android/server/tv/TvInputManagerService$SessionState:-get3	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/os/IBinder;
      //   80: sipush 1000
      //   83: iload_3
      //   84: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   87: astore_1
      //   88: fload_2
      //   89: fconst_0
      //   90: fcmpl
      //   91: ifle +21 -> 112
      //   94: fconst_1
      //   95: fstore_2
      //   96: aload_1
      //   97: fload_2
      //   98: invokeinterface 741 2 0
      //   103: aload 7
      //   105: monitorexit
      //   106: lload 5
      //   108: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   111: return
      //   112: fconst_0
      //   113: fstore_2
      //   114: goto -18 -> 96
      //   117: astore_1
      //   118: ldc -113
      //   120: ldc_w 743
      //   123: aload_1
      //   124: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   127: pop
      //   128: goto -25 -> 103
      //   131: astore_1
      //   132: aload 7
      //   134: monitorexit
      //   135: aload_1
      //   136: athrow
      //   137: astore_1
      //   138: lload 5
      //   140: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   143: aload_1
      //   144: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	145	0	this	BinderService
      //   0	145	1	paramIBinder	IBinder
      //   0	145	2	paramFloat	float
      //   0	145	3	paramInt	int
      //   3	42	4	i	int
      //   25	114	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	88	117	android/os/RemoteException
      //   39	88	117	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   96	103	117	android/os/RemoteException
      //   96	103	117	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	88	131	finally
      //   96	103	131	finally
      //   118	128	131	finally
      //   27	39	137	finally
      //   103	106	137	finally
      //   132	137	137	finally
    }
    
    /* Error */
    public void startRecording(IBinder paramIBinder, Uri paramUri, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 746
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: aload_2
      //   51: invokeinterface 749 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 751
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramUri	Uri
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void stopRecording(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 753
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore_2
      //   20: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore 4
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: aload_1
      //   42: iload_3
      //   43: iload_2
      //   44: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   47: invokeinterface 755 1 0
      //   52: aload 6
      //   54: monitorexit
      //   55: lload 4
      //   57: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   60: return
      //   61: astore_1
      //   62: ldc -113
      //   64: ldc_w 757
      //   67: aload_1
      //   68: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   71: pop
      //   72: goto -20 -> 52
      //   75: astore_1
      //   76: aload 6
      //   78: monitorexit
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload 4
      //   84: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   87: aload_1
      //   88: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	BinderService
      //   0	89	1	paramIBinder	IBinder
      //   0	89	2	paramInt	int
      //   3	40	3	i	int
      //   23	60	4	l	long
      // Exception table:
      //   from	to	target	type
      //   37	52	61	android/os/RemoteException
      //   37	52	61	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   37	52	75	finally
      //   62	72	75	finally
      //   25	37	81	finally
      //   52	55	81	finally
      //   76	81	81	finally
    }
    
    /* Error */
    public void timeShiftEnablePositionTracking(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 759
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: iload_2
      //   51: invokeinterface 761 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 763
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramBoolean	boolean
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void timeShiftPause(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 765
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore_2
      //   20: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore 4
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: aload_1
      //   42: iload_3
      //   43: iload_2
      //   44: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   47: invokeinterface 767 1 0
      //   52: aload 6
      //   54: monitorexit
      //   55: lload 4
      //   57: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   60: return
      //   61: astore_1
      //   62: ldc -113
      //   64: ldc_w 769
      //   67: aload_1
      //   68: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   71: pop
      //   72: goto -20 -> 52
      //   75: astore_1
      //   76: aload 6
      //   78: monitorexit
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload 4
      //   84: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   87: aload_1
      //   88: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	BinderService
      //   0	89	1	paramIBinder	IBinder
      //   0	89	2	paramInt	int
      //   3	40	3	i	int
      //   23	60	4	l	long
      // Exception table:
      //   from	to	target	type
      //   37	52	61	android/os/RemoteException
      //   37	52	61	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   37	52	75	finally
      //   62	72	75	finally
      //   25	37	81	finally
      //   52	55	81	finally
      //   76	81	81	finally
    }
    
    /* Error */
    public void timeShiftPlay(IBinder paramIBinder, Uri paramUri, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 771
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: aload_2
      //   51: invokeinterface 773 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 775
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramUri	Uri
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void timeShiftResume(IBinder paramIBinder, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   8: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   11: iload_3
      //   12: iload_2
      //   13: ldc_w 777
      //   16: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   19: istore_2
      //   20: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   23: lstore 4
      //   25: aload_0
      //   26: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   29: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   32: astore 6
      //   34: aload 6
      //   36: monitorenter
      //   37: aload_0
      //   38: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   41: aload_1
      //   42: iload_3
      //   43: iload_2
      //   44: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   47: invokeinterface 779 1 0
      //   52: aload 6
      //   54: monitorexit
      //   55: lload 4
      //   57: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   60: return
      //   61: astore_1
      //   62: ldc -113
      //   64: ldc_w 781
      //   67: aload_1
      //   68: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   71: pop
      //   72: goto -20 -> 52
      //   75: astore_1
      //   76: aload 6
      //   78: monitorexit
      //   79: aload_1
      //   80: athrow
      //   81: astore_1
      //   82: lload 4
      //   84: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   87: aload_1
      //   88: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	BinderService
      //   0	89	1	paramIBinder	IBinder
      //   0	89	2	paramInt	int
      //   3	40	3	i	int
      //   23	60	4	l	long
      // Exception table:
      //   from	to	target	type
      //   37	52	61	android/os/RemoteException
      //   37	52	61	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   37	52	75	finally
      //   62	72	75	finally
      //   25	37	81	finally
      //   52	55	81	finally
      //   76	81	81	finally
    }
    
    /* Error */
    public void timeShiftSeekTo(IBinder paramIBinder, long paramLong, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 5
      //   14: iload 4
      //   16: ldc_w 784
      //   19: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   22: istore 4
      //   24: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   27: lstore 6
      //   29: aload_0
      //   30: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   33: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   36: astore 8
      //   38: aload 8
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   45: aload_1
      //   46: iload 5
      //   48: iload 4
      //   50: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   53: lload_2
      //   54: invokeinterface 786 3 0
      //   59: aload 8
      //   61: monitorexit
      //   62: lload 6
      //   64: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   67: return
      //   68: astore_1
      //   69: ldc -113
      //   71: ldc_w 788
      //   74: aload_1
      //   75: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   78: pop
      //   79: goto -20 -> 59
      //   82: astore_1
      //   83: aload 8
      //   85: monitorexit
      //   86: aload_1
      //   87: athrow
      //   88: astore_1
      //   89: lload 6
      //   91: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   94: aload_1
      //   95: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	96	0	this	BinderService
      //   0	96	1	paramIBinder	IBinder
      //   0	96	2	paramLong	long
      //   0	96	4	paramInt	int
      //   3	44	5	i	int
      //   27	63	6	l	long
      // Exception table:
      //   from	to	target	type
      //   41	59	68	android/os/RemoteException
      //   41	59	68	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   41	59	82	finally
      //   69	79	82	finally
      //   29	41	88	finally
      //   59	62	88	finally
      //   83	88	88	finally
    }
    
    /* Error */
    public void timeShiftSetPlaybackParams(IBinder paramIBinder, android.media.PlaybackParams paramPlaybackParams, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 4
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 4
      //   14: iload_3
      //   15: ldc_w 791
      //   18: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   21: istore_3
      //   22: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   25: lstore 5
      //   27: aload_0
      //   28: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   31: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   34: astore 7
      //   36: aload 7
      //   38: monitorenter
      //   39: aload_0
      //   40: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   43: aload_1
      //   44: iload 4
      //   46: iload_3
      //   47: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   50: aload_2
      //   51: invokeinterface 794 2 0
      //   56: aload 7
      //   58: monitorexit
      //   59: lload 5
      //   61: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   64: return
      //   65: astore_1
      //   66: ldc -113
      //   68: ldc_w 796
      //   71: aload_1
      //   72: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   75: pop
      //   76: goto -20 -> 56
      //   79: astore_1
      //   80: aload 7
      //   82: monitorexit
      //   83: aload_1
      //   84: athrow
      //   85: astore_1
      //   86: lload 5
      //   88: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   91: aload_1
      //   92: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	93	0	this	BinderService
      //   0	93	1	paramIBinder	IBinder
      //   0	93	2	paramPlaybackParams	android.media.PlaybackParams
      //   0	93	3	paramInt	int
      //   3	42	4	i	int
      //   25	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   39	56	65	android/os/RemoteException
      //   39	56	65	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   39	56	79	finally
      //   66	76	79	finally
      //   27	39	85	finally
      //   56	59	85	finally
      //   80	85	85	finally
    }
    
    /* Error */
    public void tune(IBinder paramIBinder, Uri paramUri, Bundle paramBundle, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   3: istore 5
      //   5: aload_0
      //   6: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   9: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   12: iload 5
      //   14: iload 4
      //   16: ldc_w 799
      //   19: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   22: istore 4
      //   24: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   27: lstore 6
      //   29: aload_0
      //   30: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   33: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   36: astore 9
      //   38: aload 9
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   45: aload_1
      //   46: iload 5
      //   48: iload 4
      //   50: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   53: aload_2
      //   54: aload_3
      //   55: invokeinterface 802 3 0
      //   60: aload_2
      //   61: invokestatic 808	android/media/tv/TvContract:isChannelUriForPassthroughInput	(Landroid/net/Uri;)Z
      //   64: istore 8
      //   66: iload 8
      //   68: ifeq +12 -> 80
      //   71: aload 9
      //   73: monitorexit
      //   74: lload 6
      //   76: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   79: return
      //   80: aload_0
      //   81: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   84: iload 4
      //   86: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   89: invokestatic 164	com/android/server/tv/TvInputManagerService$UserState:-get8	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Map;
      //   92: aload_1
      //   93: invokeinterface 141 2 0
      //   98: checkcast 186	com/android/server/tv/TvInputManagerService$SessionState
      //   101: astore 10
      //   103: aload 10
      //   105: invokestatic 811	com/android/server/tv/TvInputManagerService$SessionState:-get5	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Z
      //   108: istore 8
      //   110: iload 8
      //   112: ifeq +12 -> 124
      //   115: aload 9
      //   117: monitorexit
      //   118: lload 6
      //   120: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   123: return
      //   124: invokestatic 817	com/android/internal/os/SomeArgs:obtain	()Lcom/android/internal/os/SomeArgs;
      //   127: astore 11
      //   129: aload 11
      //   131: aload 10
      //   133: invokestatic 820	com/android/server/tv/TvInputManagerService$SessionState:-get2	(Lcom/android/server/tv/TvInputManagerService$SessionState;)Landroid/content/ComponentName;
      //   136: invokevirtual 825	android/content/ComponentName:getPackageName	()Ljava/lang/String;
      //   139: putfield 829	com/android/internal/os/SomeArgs:arg1	Ljava/lang/Object;
      //   142: aload 11
      //   144: invokestatic 834	java/lang/System:currentTimeMillis	()J
      //   147: invokestatic 839	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   150: putfield 842	com/android/internal/os/SomeArgs:arg2	Ljava/lang/Object;
      //   153: aload 11
      //   155: aload_2
      //   156: invokestatic 848	android/content/ContentUris:parseId	(Landroid/net/Uri;)J
      //   159: invokestatic 839	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   162: putfield 851	com/android/internal/os/SomeArgs:arg3	Ljava/lang/Object;
      //   165: aload 11
      //   167: aload_3
      //   168: putfield 854	com/android/internal/os/SomeArgs:arg4	Ljava/lang/Object;
      //   171: aload 11
      //   173: aload_1
      //   174: putfield 857	com/android/internal/os/SomeArgs:arg5	Ljava/lang/Object;
      //   177: aload_0
      //   178: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   181: invokestatic 860	com/android/server/tv/TvInputManagerService:-get5	(Lcom/android/server/tv/TvInputManagerService;)Lcom/android/server/tv/TvInputManagerService$WatchLogHandler;
      //   184: iconst_1
      //   185: aload 11
      //   187: invokevirtual 866	com/android/server/tv/TvInputManagerService$WatchLogHandler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
      //   190: invokevirtual 871	android/os/Message:sendToTarget	()V
      //   193: aload 9
      //   195: monitorexit
      //   196: lload 6
      //   198: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   201: return
      //   202: astore_1
      //   203: ldc -113
      //   205: ldc_w 873
      //   208: aload_1
      //   209: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   212: pop
      //   213: goto -20 -> 193
      //   216: astore_1
      //   217: aload 9
      //   219: monitorexit
      //   220: aload_1
      //   221: athrow
      //   222: astore_1
      //   223: lload 6
      //   225: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   228: aload_1
      //   229: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	230	0	this	BinderService
      //   0	230	1	paramIBinder	IBinder
      //   0	230	2	paramUri	Uri
      //   0	230	3	paramBundle	Bundle
      //   0	230	4	paramInt	int
      //   3	44	5	i	int
      //   27	197	6	l	long
      //   64	47	8	bool	boolean
      //   101	31	10	localSessionState	TvInputManagerService.SessionState
      //   127	59	11	localSomeArgs	SomeArgs
      // Exception table:
      //   from	to	target	type
      //   41	66	202	android/os/RemoteException
      //   41	66	202	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   80	110	202	android/os/RemoteException
      //   80	110	202	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   124	193	202	android/os/RemoteException
      //   124	193	202	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   41	66	216	finally
      //   80	110	216	finally
      //   124	193	216	finally
      //   203	213	216	finally
      //   29	41	222	finally
      //   71	74	222	finally
      //   115	118	222	finally
      //   193	196	222	finally
      //   217	222	222	finally
    }
    
    /* Error */
    public void unblockContent(IBinder paramIBinder, String paramString, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 99	com/android/server/tv/TvInputManagerService$BinderService:ensureParentalControlsPermission	()V
      //   4: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   7: istore 4
      //   9: aload_0
      //   10: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   13: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   16: iload 4
      //   18: iload_3
      //   19: ldc_w 876
      //   22: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   25: istore_3
      //   26: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   29: lstore 5
      //   31: aload_0
      //   32: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   35: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   38: astore 7
      //   40: aload 7
      //   42: monitorenter
      //   43: aload_0
      //   44: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   47: aload_1
      //   48: iload 4
      //   50: iload_3
      //   51: invokestatic 211	com/android/server/tv/TvInputManagerService:-wrap1	(Lcom/android/server/tv/TvInputManagerService;Landroid/os/IBinder;II)Landroid/media/tv/ITvInputSession;
      //   54: aload_2
      //   55: invokeinterface 878 2 0
      //   60: aload 7
      //   62: monitorexit
      //   63: lload 5
      //   65: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   68: return
      //   69: astore_1
      //   70: ldc -113
      //   72: ldc_w 880
      //   75: aload_1
      //   76: invokestatic 221	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   79: pop
      //   80: goto -20 -> 60
      //   83: astore_1
      //   84: aload 7
      //   86: monitorexit
      //   87: aload_1
      //   88: athrow
      //   89: astore_1
      //   90: lload 5
      //   92: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   95: aload_1
      //   96: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	97	0	this	BinderService
      //   0	97	1	paramIBinder	IBinder
      //   0	97	2	paramString	String
      //   0	97	3	paramInt	int
      //   7	42	4	i	int
      //   29	62	5	l	long
      // Exception table:
      //   from	to	target	type
      //   43	60	69	android/os/RemoteException
      //   43	60	69	com/android/server/tv/TvInputManagerService$SessionNotFoundException
      //   43	60	83	finally
      //   70	80	83	finally
      //   31	43	89	finally
      //   60	63	89	finally
      //   84	89	89	finally
    }
    
    /* Error */
    public void unregisterCallback(ITvInputManagerCallback paramITvInputManagerCallback, int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   4: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   7: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   10: iload_2
      //   11: ldc_w 882
      //   14: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   17: istore_2
      //   18: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   21: lstore_3
      //   22: aload_0
      //   23: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   26: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   38: iload_2
      //   39: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   42: invokestatic 472	com/android/server/tv/TvInputManagerService$UserState:-get0	(Lcom/android/server/tv/TvInputManagerService$UserState;)Ljava/util/Set;
      //   45: aload_1
      //   46: invokeinterface 885 2 0
      //   51: pop
      //   52: aload 5
      //   54: monitorexit
      //   55: lload_3
      //   56: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   59: return
      //   60: astore_1
      //   61: aload 5
      //   63: monitorexit
      //   64: aload_1
      //   65: athrow
      //   66: astore_1
      //   67: lload_3
      //   68: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   71: aload_1
      //   72: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	73	0	this	BinderService
      //   0	73	1	paramITvInputManagerCallback	ITvInputManagerCallback
      //   0	73	2	paramInt	int
      //   21	47	3	l	long
      // Exception table:
      //   from	to	target	type
      //   34	52	60	finally
      //   22	34	66	finally
      //   52	55	66	finally
      //   61	66	66	finally
    }
    
    /* Error */
    public void updateTvInputInfo(TvInputInfo paramTvInputInfo, int paramInt)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 891	android/media/tv/TvInputInfo:getServiceInfo	()Landroid/content/pm/ServiceInfo;
      //   4: getfield 897	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
      //   7: astore 5
      //   9: aload_0
      //   10: invokespecial 899	com/android/server/tv/TvInputManagerService$BinderService:getCallingPackageName	()Ljava/lang/String;
      //   13: astore 6
      //   15: aload 5
      //   17: aload 6
      //   19: invokestatic 904	android/text/TextUtils:equals	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z
      //   22: ifne +43 -> 65
      //   25: new 604	java/lang/IllegalArgumentException
      //   28: dup
      //   29: new 145	java/lang/StringBuilder
      //   32: dup
      //   33: invokespecial 146	java/lang/StringBuilder:<init>	()V
      //   36: ldc_w 906
      //   39: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   42: aload 6
      //   44: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   47: ldc_w 908
      //   50: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   53: aload 5
      //   55: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   58: invokevirtual 155	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   61: invokespecial 607	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
      //   64: athrow
      //   65: aload_0
      //   66: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   69: invokestatic 75	android/os/Binder:getCallingPid	()I
      //   72: invokestatic 54	android/os/Binder:getCallingUid	()I
      //   75: iload_2
      //   76: ldc_w 909
      //   79: invokestatic 80	com/android/server/tv/TvInputManagerService:-wrap7	(Lcom/android/server/tv/TvInputManagerService;IIILjava/lang/String;)I
      //   82: istore_2
      //   83: invokestatic 72	android/os/Binder:clearCallingIdentity	()J
      //   86: lstore_3
      //   87: aload_0
      //   88: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   91: invokestatic 104	com/android/server/tv/TvInputManagerService:-get2	(Lcom/android/server/tv/TvInputManagerService;)Ljava/lang/Object;
      //   94: astore 5
      //   96: aload 5
      //   98: monitorenter
      //   99: aload_0
      //   100: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   103: iload_2
      //   104: invokestatic 108	com/android/server/tv/TvInputManagerService:-wrap6	(Lcom/android/server/tv/TvInputManagerService;I)Lcom/android/server/tv/TvInputManagerService$UserState;
      //   107: astore 6
      //   109: aload_0
      //   110: getfield 15	com/android/server/tv/TvInputManagerService$BinderService:this$0	Lcom/android/server/tv/TvInputManagerService;
      //   113: aload 6
      //   115: aload_1
      //   116: invokestatic 913	com/android/server/tv/TvInputManagerService:-wrap22	(Lcom/android/server/tv/TvInputManagerService;Lcom/android/server/tv/TvInputManagerService$UserState;Landroid/media/tv/TvInputInfo;)V
      //   119: aload 5
      //   121: monitorexit
      //   122: lload_3
      //   123: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   126: return
      //   127: astore_1
      //   128: aload 5
      //   130: monitorexit
      //   131: aload_1
      //   132: athrow
      //   133: astore_1
      //   134: lload_3
      //   135: invokestatic 94	android/os/Binder:restoreCallingIdentity	(J)V
      //   138: aload_1
      //   139: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	140	0	this	BinderService
      //   0	140	1	paramTvInputInfo	TvInputInfo
      //   0	140	2	paramInt	int
      //   86	49	3	l	long
      //   13	101	6	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   99	119	127	finally
      //   87	99	133	finally
      //   119	122	133	finally
      //   128	133	133	finally
    }
  }
  
  private final class ClientState
    implements IBinder.DeathRecipient
  {
    private IBinder clientToken;
    private final List<IBinder> sessionTokens = new ArrayList();
    private final int userId;
    
    ClientState(IBinder paramIBinder, int paramInt)
    {
      this.clientToken = paramIBinder;
      this.userId = paramInt;
    }
    
    public void binderDied()
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        ClientState localClientState = (ClientState)TvInputManagerService.UserState.-get1(TvInputManagerService.-wrap6(TvInputManagerService.this, this.userId)).get(this.clientToken);
        if ((localClientState != null) && (localClientState.sessionTokens.size() > 0)) {
          TvInputManagerService.-wrap14(TvInputManagerService.this, (IBinder)localClientState.sessionTokens.get(0), 1000, this.userId);
        }
      }
      this.clientToken = null;
    }
    
    public boolean isEmpty()
    {
      return this.sessionTokens.isEmpty();
    }
  }
  
  private final class HardwareListener
    implements TvInputHardwareManager.Listener
  {
    private HardwareListener() {}
    
    public void onHardwareDeviceAdded(TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        Iterator localIterator = TvInputManagerService.UserState.-get7(TvInputManagerService.-wrap6(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this))).values().iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            TvInputManagerService.ServiceState localServiceState = (TvInputManagerService.ServiceState)localIterator.next();
            if (!TvInputManagerService.ServiceState.-get5(localServiceState)) {
              continue;
            }
            ITvInputService localITvInputService = TvInputManagerService.ServiceState.-get7(localServiceState);
            if (localITvInputService == null) {
              continue;
            }
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHardwareAdded(paramTvInputHardwareInfo);
            }
            catch (RemoteException localRemoteException)
            {
              Slog.e("TvInputManagerService", "error in notifyHardwareAdded", localRemoteException);
            }
          }
        }
      }
    }
    
    public void onHardwareDeviceRemoved(TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        Iterator localIterator = TvInputManagerService.UserState.-get7(TvInputManagerService.-wrap6(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this))).values().iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            TvInputManagerService.ServiceState localServiceState = (TvInputManagerService.ServiceState)localIterator.next();
            if (!TvInputManagerService.ServiceState.-get5(localServiceState)) {
              continue;
            }
            ITvInputService localITvInputService = TvInputManagerService.ServiceState.-get7(localServiceState);
            if (localITvInputService == null) {
              continue;
            }
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHardwareRemoved(paramTvInputHardwareInfo);
            }
            catch (RemoteException localRemoteException)
            {
              Slog.e("TvInputManagerService", "error in notifyHardwareRemoved", localRemoteException);
            }
          }
        }
      }
    }
    
    public void onHdmiDeviceAdded(HdmiDeviceInfo paramHdmiDeviceInfo)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        Iterator localIterator = TvInputManagerService.UserState.-get7(TvInputManagerService.-wrap6(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this))).values().iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            TvInputManagerService.ServiceState localServiceState = (TvInputManagerService.ServiceState)localIterator.next();
            if (!TvInputManagerService.ServiceState.-get5(localServiceState)) {
              continue;
            }
            ITvInputService localITvInputService = TvInputManagerService.ServiceState.-get7(localServiceState);
            if (localITvInputService == null) {
              continue;
            }
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHdmiDeviceAdded(paramHdmiDeviceInfo);
            }
            catch (RemoteException localRemoteException)
            {
              Slog.e("TvInputManagerService", "error in notifyHdmiDeviceAdded", localRemoteException);
            }
          }
        }
      }
    }
    
    public void onHdmiDeviceRemoved(HdmiDeviceInfo paramHdmiDeviceInfo)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        Iterator localIterator = TvInputManagerService.UserState.-get7(TvInputManagerService.-wrap6(TvInputManagerService.this, TvInputManagerService.-get1(TvInputManagerService.this))).values().iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            TvInputManagerService.ServiceState localServiceState = (TvInputManagerService.ServiceState)localIterator.next();
            if (!TvInputManagerService.ServiceState.-get5(localServiceState)) {
              continue;
            }
            ITvInputService localITvInputService = TvInputManagerService.ServiceState.-get7(localServiceState);
            if (localITvInputService == null) {
              continue;
            }
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHdmiDeviceRemoved(paramHdmiDeviceInfo);
            }
            catch (RemoteException localRemoteException)
            {
              Slog.e("TvInputManagerService", "error in notifyHdmiDeviceRemoved", localRemoteException);
            }
          }
        }
      }
    }
    
    public void onHdmiDeviceUpdated(String paramString, HdmiDeviceInfo paramHdmiDeviceInfo)
    {
      for (;;)
      {
        synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
        {
          switch (paramHdmiDeviceInfo.getDevicePowerStatus())
          {
          case 0: 
            if (paramHdmiDeviceInfo != null) {
              TvInputManagerService.-wrap19(TvInputManagerService.this, paramString, paramHdmiDeviceInfo.intValue(), TvInputManagerService.-get1(TvInputManagerService.this));
            }
            return;
            paramHdmiDeviceInfo = Integer.valueOf(0);
            break;
          case 1: 
          case 2: 
          case 3: 
            paramHdmiDeviceInfo = Integer.valueOf(1);
          }
        }
        paramHdmiDeviceInfo = null;
      }
    }
    
    public void onStateChanged(String paramString, int paramInt)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        TvInputManagerService.-wrap19(TvInputManagerService.this, paramString, paramInt, TvInputManagerService.-get1(TvInputManagerService.this));
        return;
      }
    }
  }
  
  private final class InputServiceConnection
    implements ServiceConnection
  {
    private final ComponentName mComponent;
    private final int mUserId;
    
    private InputServiceConnection(ComponentName paramComponentName, int paramInt)
    {
      this.mComponent = paramComponentName;
      this.mUserId = paramInt;
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      TvInputManagerService.UserState localUserState;
      TvInputManagerService.ServiceState localServiceState;
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        localUserState = (TvInputManagerService.UserState)TvInputManagerService.-get4(TvInputManagerService.this).get(this.mUserId);
        if (localUserState == null)
        {
          TvInputManagerService.-get0(TvInputManagerService.this).unbindService(this);
          return;
        }
        localServiceState = (TvInputManagerService.ServiceState)TvInputManagerService.UserState.-get7(localUserState).get(this.mComponent);
        TvInputManagerService.ServiceState.-set3(localServiceState, ITvInputService.Stub.asInterface(paramIBinder));
        if ((TvInputManagerService.ServiceState.-get5(localServiceState)) && (TvInputManagerService.ServiceState.-get1(localServiceState) == null)) {
          TvInputManagerService.ServiceState.-set1(localServiceState, new TvInputManagerService.ServiceCallback(TvInputManagerService.this, this.mComponent, this.mUserId));
        }
      }
      try
      {
        TvInputManagerService.ServiceState.-get7(localServiceState).registerCallback(TvInputManagerService.ServiceState.-get1(localServiceState));
        paramIBinder = TvInputManagerService.ServiceState.-get8(localServiceState).iterator();
        while (paramIBinder.hasNext())
        {
          localObject2 = (IBinder)paramIBinder.next();
          TvInputManagerService.-wrap12(TvInputManagerService.this, TvInputManagerService.ServiceState.-get7(localServiceState), (IBinder)localObject2, this.mUserId);
          continue;
          paramComponentName = finally;
          throw paramComponentName;
        }
      }
      catch (RemoteException paramIBinder)
      {
        Object localObject2;
        for (;;)
        {
          Slog.e("TvInputManagerService", "error in registerCallback", paramIBinder);
        }
        paramIBinder = TvInputManagerService.UserState.-get3(localUserState).values().iterator();
        while (paramIBinder.hasNext())
        {
          localObject2 = (TvInputManagerService.TvInputState)paramIBinder.next();
          if ((TvInputManagerService.TvInputState.-get0((TvInputManagerService.TvInputState)localObject2).getComponent().equals(paramComponentName)) && (TvInputManagerService.TvInputState.-get1((TvInputManagerService.TvInputState)localObject2) != 0)) {
            TvInputManagerService.-wrap13(TvInputManagerService.this, localUserState, TvInputManagerService.TvInputState.-get0((TvInputManagerService.TvInputState)localObject2).getId(), TvInputManagerService.TvInputState.-get1((TvInputManagerService.TvInputState)localObject2), null);
          }
        }
        if (TvInputManagerService.ServiceState.-get5(localServiceState))
        {
          TvInputManagerService.ServiceState.-get4(localServiceState).clear();
          paramComponentName = TvInputManagerService.-get3(TvInputManagerService.this).getHardwareList().iterator();
          while (paramComponentName.hasNext())
          {
            paramIBinder = (TvInputHardwareInfo)paramComponentName.next();
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHardwareAdded(paramIBinder);
            }
            catch (RemoteException paramIBinder)
            {
              Slog.e("TvInputManagerService", "error in notifyHardwareAdded", paramIBinder);
            }
          }
          paramComponentName = TvInputManagerService.-get3(TvInputManagerService.this).getHdmiDeviceList().iterator();
          while (paramComponentName.hasNext())
          {
            paramIBinder = (HdmiDeviceInfo)paramComponentName.next();
            try
            {
              TvInputManagerService.ServiceState.-get7(localServiceState).notifyHdmiDeviceAdded(paramIBinder);
            }
            catch (RemoteException paramIBinder)
            {
              Slog.e("TvInputManagerService", "error in notifyHdmiDeviceAdded", paramIBinder);
            }
          }
        }
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      if (!this.mComponent.equals(???)) {
        throw new IllegalArgumentException("Mismatched ComponentName: " + this.mComponent + " (expected), " + ??? + " (actual).");
      }
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        TvInputManagerService.ServiceState localServiceState = (TvInputManagerService.ServiceState)TvInputManagerService.UserState.-get7(TvInputManagerService.-wrap6(TvInputManagerService.this, this.mUserId)).get(this.mComponent);
        if (localServiceState != null)
        {
          TvInputManagerService.ServiceState.-set2(localServiceState, true);
          TvInputManagerService.ServiceState.-set0(localServiceState, false);
          TvInputManagerService.ServiceState.-set3(localServiceState, null);
          TvInputManagerService.ServiceState.-set1(localServiceState, null);
          TvInputManagerService.-wrap8(TvInputManagerService.this, localServiceState, null, this.mUserId);
        }
        return;
      }
    }
  }
  
  private final class ServiceCallback
    extends ITvInputServiceCallback.Stub
  {
    private final ComponentName mComponent;
    private final int mUserId;
    
    ServiceCallback(ComponentName paramComponentName, int paramInt)
    {
      this.mComponent = paramComponentName;
      this.mUserId = paramInt;
    }
    
    private void addHardwareInputLocked(TvInputInfo paramTvInputInfo)
    {
      TvInputManagerService.ServiceState.-get4(TvInputManagerService.-wrap4(TvInputManagerService.this, this.mComponent, this.mUserId)).add(paramTvInputInfo);
      TvInputManagerService.-wrap10(TvInputManagerService.this, this.mUserId, null);
    }
    
    private void ensureHardwarePermission()
    {
      if (TvInputManagerService.-get0(TvInputManagerService.this).checkCallingPermission("android.permission.TV_INPUT_HARDWARE") != 0) {
        throw new SecurityException("The caller does not have hardware permission");
      }
    }
    
    private void ensureValidInput(TvInputInfo paramTvInputInfo)
    {
      if ((paramTvInputInfo.getId() != null) && (this.mComponent.equals(paramTvInputInfo.getComponent()))) {
        return;
      }
      throw new IllegalArgumentException("Invalid TvInputInfo");
    }
    
    public void addHardwareInput(int paramInt, TvInputInfo paramTvInputInfo)
    {
      ensureHardwarePermission();
      ensureValidInput(paramTvInputInfo);
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        TvInputManagerService.-get3(TvInputManagerService.this).addHardwareInput(paramInt, paramTvInputInfo);
        addHardwareInputLocked(paramTvInputInfo);
        return;
      }
    }
    
    public void addHdmiInput(int paramInt, TvInputInfo paramTvInputInfo)
    {
      ensureHardwarePermission();
      ensureValidInput(paramTvInputInfo);
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        TvInputManagerService.-get3(TvInputManagerService.this).addHdmiInput(paramInt, paramTvInputInfo);
        addHardwareInputLocked(paramTvInputInfo);
        return;
      }
    }
    
    public void removeHardwareInput(String paramString)
    {
      ensureHardwarePermission();
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        Object localObject2 = TvInputManagerService.-wrap4(TvInputManagerService.this, this.mComponent, this.mUserId);
        int j = 0;
        localObject2 = TvInputManagerService.ServiceState.-get4((TvInputManagerService.ServiceState)localObject2).iterator();
        do
        {
          i = j;
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
        } while (!((TvInputInfo)((Iterator)localObject2).next()).getId().equals(paramString));
        ((Iterator)localObject2).remove();
        int i = 1;
        if (i != 0)
        {
          TvInputManagerService.-wrap10(TvInputManagerService.this, this.mUserId, null);
          TvInputManagerService.-get3(TvInputManagerService.this).removeHardwareInput(paramString);
          return;
        }
        Slog.e("TvInputManagerService", "failed to remove input " + paramString);
      }
    }
  }
  
  private final class ServiceState
  {
    private boolean bound;
    private TvInputManagerService.ServiceCallback callback;
    private final ComponentName component;
    private final ServiceConnection connection;
    private final List<TvInputInfo> hardwareInputList = new ArrayList();
    private final boolean isHardware;
    private boolean reconnecting;
    private ITvInputService service;
    private final List<IBinder> sessionTokens = new ArrayList();
    
    private ServiceState(ComponentName paramComponentName, int paramInt)
    {
      this.component = paramComponentName;
      this.connection = new TvInputManagerService.InputServiceConnection(TvInputManagerService.this, paramComponentName, paramInt, null);
      this.isHardware = TvInputManagerService.-wrap3(TvInputManagerService.-get0(TvInputManagerService.this).getPackageManager(), paramComponentName);
    }
  }
  
  private final class SessionCallback
    extends ITvInputSessionCallback.Stub
  {
    private final InputChannel[] mChannels;
    private final TvInputManagerService.SessionState mSessionState;
    
    SessionCallback(TvInputManagerService.SessionState paramSessionState, InputChannel[] paramArrayOfInputChannel)
    {
      this.mSessionState = paramSessionState;
      this.mChannels = paramArrayOfInputChannel;
    }
    
    private boolean addSessionTokenToClientStateLocked(ITvInputSession paramITvInputSession)
    {
      try
      {
        paramITvInputSession.asBinder().linkToDeath(this.mSessionState, 0);
        IBinder localIBinder = TvInputManagerService.SessionState.-get1(this.mSessionState).asBinder();
        TvInputManagerService.UserState localUserState = TvInputManagerService.-wrap6(TvInputManagerService.this, TvInputManagerService.SessionState.-get10(this.mSessionState));
        TvInputManagerService.ClientState localClientState = (TvInputManagerService.ClientState)TvInputManagerService.UserState.-get1(localUserState).get(localIBinder);
        paramITvInputSession = localClientState;
        if (localClientState == null) {
          paramITvInputSession = new TvInputManagerService.ClientState(TvInputManagerService.this, localIBinder, TvInputManagerService.SessionState.-get10(this.mSessionState));
        }
        return false;
      }
      catch (RemoteException paramITvInputSession)
      {
        try
        {
          localIBinder.linkToDeath(paramITvInputSession, 0);
          TvInputManagerService.UserState.-get1(localUserState).put(localIBinder, paramITvInputSession);
          TvInputManagerService.ClientState.-get1(paramITvInputSession).add(TvInputManagerService.SessionState.-get9(this.mSessionState));
          return true;
        }
        catch (RemoteException paramITvInputSession)
        {
          Slog.e("TvInputManagerService", "client process has already died", paramITvInputSession);
        }
        paramITvInputSession = paramITvInputSession;
        Slog.e("TvInputManagerService", "session process has already died", paramITvInputSession);
        return false;
      }
    }
    
    public void onChannelRetuned(Uri paramUri)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onChannelRetuned(paramUri, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramUri)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onChannelRetuned", paramUri);
          }
        }
      }
    }
    
    public void onContentAllowed()
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onContentAllowed(TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onContentAllowed", localRemoteException);
          }
        }
      }
    }
    
    public void onContentBlocked(String paramString)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onContentBlocked(paramString, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramString)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onContentBlocked", paramString);
          }
        }
      }
    }
    
    public void onError(int paramInt)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onError(paramInt, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onError", localRemoteException);
          }
        }
      }
    }
    
    public void onLayoutSurface(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onLayoutSurface(paramInt1, paramInt2, paramInt3, paramInt4, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onLayoutSurface", localRemoteException);
          }
        }
      }
    }
    
    public void onRecordingStopped(Uri paramUri)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onRecordingStopped(paramUri, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramUri)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onRecordingStopped", paramUri);
          }
        }
      }
    }
    
    public void onSessionCreated(ITvInputSession paramITvInputSession, IBinder paramIBinder)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        TvInputManagerService.SessionState.-set1(this.mSessionState, paramITvInputSession);
        TvInputManagerService.SessionState.-set0(this.mSessionState, paramIBinder);
        if ((paramITvInputSession != null) && (addSessionTokenToClientStateLocked(paramITvInputSession)))
        {
          TvInputManagerService.-wrap17(TvInputManagerService.this, TvInputManagerService.SessionState.-get1(this.mSessionState), TvInputManagerService.SessionState.-get4(this.mSessionState), TvInputManagerService.SessionState.-get9(this.mSessionState), this.mChannels[0], TvInputManagerService.SessionState.-get7(this.mSessionState));
          this.mChannels[0].dispose();
          return;
        }
        TvInputManagerService.-wrap15(TvInputManagerService.this, TvInputManagerService.SessionState.-get9(this.mSessionState), TvInputManagerService.SessionState.-get10(this.mSessionState));
        TvInputManagerService.-wrap17(TvInputManagerService.this, TvInputManagerService.SessionState.-get1(this.mSessionState), TvInputManagerService.SessionState.-get4(this.mSessionState), null, null, TvInputManagerService.SessionState.-get7(this.mSessionState));
      }
    }
    
    public void onSessionEvent(String paramString, Bundle paramBundle)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onSessionEvent(paramString, paramBundle, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramString)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onSessionEvent", paramString);
          }
        }
      }
    }
    
    public void onTimeShiftCurrentPositionChanged(long paramLong)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTimeShiftCurrentPositionChanged(paramLong, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTimeShiftCurrentPositionChanged", localRemoteException);
          }
        }
      }
    }
    
    public void onTimeShiftStartPositionChanged(long paramLong)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTimeShiftStartPositionChanged(paramLong, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTimeShiftStartPositionChanged", localRemoteException);
          }
        }
      }
    }
    
    public void onTimeShiftStatusChanged(int paramInt)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTimeShiftStatusChanged(paramInt, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTimeShiftStatusChanged", localRemoteException);
          }
        }
      }
    }
    
    public void onTrackSelected(int paramInt, String paramString)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTrackSelected(paramInt, paramString, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramString)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTrackSelected", paramString);
          }
        }
      }
    }
    
    public void onTracksChanged(List<TvTrackInfo> paramList)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTracksChanged(paramList, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException paramList)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTracksChanged", paramList);
          }
        }
      }
    }
    
    public void onTuned(Uri paramUri)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onTuned(TvInputManagerService.SessionState.-get7(this.mSessionState), paramUri);
          return;
        }
        catch (RemoteException paramUri)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onTuned", paramUri);
          }
        }
      }
    }
    
    public void onVideoAvailable()
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onVideoAvailable(TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onVideoAvailable", localRemoteException);
          }
        }
      }
    }
    
    public void onVideoUnavailable(int paramInt)
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        if (TvInputManagerService.SessionState.-get8(this.mSessionState) != null)
        {
          ITvInputClient localITvInputClient = TvInputManagerService.SessionState.-get1(this.mSessionState);
          if (localITvInputClient != null) {}
        }
        else
        {
          return;
        }
        try
        {
          TvInputManagerService.SessionState.-get1(this.mSessionState).onVideoUnavailable(paramInt, TvInputManagerService.SessionState.-get7(this.mSessionState));
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TvInputManagerService", "error in onVideoUnavailable", localRemoteException);
          }
        }
      }
    }
  }
  
  private static class SessionNotFoundException
    extends IllegalArgumentException
  {
    public SessionNotFoundException(String paramString)
    {
      super();
    }
  }
  
  private final class SessionState
    implements IBinder.DeathRecipient
  {
    private final int callingUid;
    private final ITvInputClient client;
    private final ComponentName componentName;
    private IBinder hardwareSessionToken;
    private final String inputId;
    private final boolean isRecordingSession;
    private Uri logUri;
    private final int seq;
    private ITvInputSession session;
    private final IBinder sessionToken;
    private final int userId;
    
    private SessionState(IBinder paramIBinder, String paramString, ComponentName paramComponentName, boolean paramBoolean, ITvInputClient paramITvInputClient, int paramInt1, int paramInt2, int paramInt3)
    {
      this.sessionToken = paramIBinder;
      this.inputId = paramString;
      this.componentName = paramComponentName;
      this.isRecordingSession = paramBoolean;
      this.client = paramITvInputClient;
      this.seq = paramInt1;
      this.callingUid = paramInt2;
      this.userId = paramInt3;
    }
    
    public void binderDied()
    {
      synchronized (TvInputManagerService.-get2(TvInputManagerService.this))
      {
        this.session = null;
        TvInputManagerService.-wrap11(TvInputManagerService.this, this);
        return;
      }
    }
  }
  
  private static final class TvInputState
  {
    private TvInputInfo info;
    private int state = 0;
    
    public String toString()
    {
      return "info: " + this.info + "; state: " + this.state;
    }
  }
  
  private static final class UserState
  {
    private final Set<ITvInputManagerCallback> callbackSet = new HashSet();
    private final Map<IBinder, TvInputManagerService.ClientState> clientStateMap = new HashMap();
    private final List<TvContentRatingSystemInfo> contentRatingSystemList = new ArrayList();
    private Map<String, TvInputManagerService.TvInputState> inputMap = new HashMap();
    private IBinder mainSessionToken = null;
    private final Set<String> packageSet = new HashSet();
    private final PersistentDataStore persistentDataStore;
    private final Map<ComponentName, TvInputManagerService.ServiceState> serviceStateMap = new HashMap();
    private final Map<IBinder, TvInputManagerService.SessionState> sessionStateMap = new HashMap();
    
    private UserState(Context paramContext, int paramInt)
    {
      this.persistentDataStore = new PersistentDataStore(paramContext, paramInt);
    }
  }
  
  private static final class WatchLogHandler
    extends Handler
  {
    static final int MSG_LOG_WATCH_END = 2;
    static final int MSG_LOG_WATCH_START = 1;
    static final int MSG_SWITCH_CONTENT_RESOLVER = 3;
    private ContentResolver mContentResolver;
    
    WatchLogHandler(ContentResolver paramContentResolver, Looper paramLooper)
    {
      super();
      this.mContentResolver = paramContentResolver;
    }
    
    private String encodeTuneParams(Bundle paramBundle)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = paramBundle.get(str);
        if (localObject != null)
        {
          localStringBuilder.append(replaceEscapeCharacters(str));
          localStringBuilder.append("=");
          localStringBuilder.append(replaceEscapeCharacters(localObject.toString()));
          if (localIterator.hasNext()) {
            localStringBuilder.append(", ");
          }
        }
      }
      return localStringBuilder.toString();
    }
    
    private String replaceEscapeCharacters(String paramString)
    {
      int i = 0;
      StringBuilder localStringBuilder = new StringBuilder();
      paramString = paramString.toCharArray();
      int j = paramString.length;
      while (i < j)
      {
        char c = paramString[i];
        if ("%=,".indexOf(c) >= 0) {
          localStringBuilder.append('%');
        }
        localStringBuilder.append(c);
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject1;
      long l1;
      Object localObject2;
      switch (paramMessage.what)
      {
      default: 
        Slog.w("TvInputManagerService", "unhandled message code: " + paramMessage.what);
        return;
      case 1: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject1 = (String)paramMessage.arg1;
        l1 = ((Long)paramMessage.arg2).longValue();
        long l2 = ((Long)paramMessage.arg3).longValue();
        localObject2 = (Bundle)paramMessage.arg4;
        IBinder localIBinder = (IBinder)paramMessage.arg5;
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("package_name", (String)localObject1);
        localContentValues.put("watch_start_time_utc_millis", Long.valueOf(l1));
        localContentValues.put("channel_id", Long.valueOf(l2));
        if (localObject2 != null) {
          localContentValues.put("tune_params", encodeTuneParams((Bundle)localObject2));
        }
        localContentValues.put("session_token", localIBinder.toString());
        this.mContentResolver.insert(TvContract.WatchedPrograms.CONTENT_URI, localContentValues);
        paramMessage.recycle();
        return;
      case 2: 
        paramMessage = (SomeArgs)paramMessage.obj;
        localObject1 = (IBinder)paramMessage.arg1;
        l1 = ((Long)paramMessage.arg2).longValue();
        localObject2 = new ContentValues();
        ((ContentValues)localObject2).put("watch_end_time_utc_millis", Long.valueOf(l1));
        ((ContentValues)localObject2).put("session_token", localObject1.toString());
        this.mContentResolver.insert(TvContract.WatchedPrograms.CONTENT_URI, (ContentValues)localObject2);
        paramMessage.recycle();
        return;
      }
      this.mContentResolver = ((ContentResolver)paramMessage.obj);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvInputManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */