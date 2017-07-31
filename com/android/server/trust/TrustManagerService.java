package com.android.server.trust;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.ITrustListener;
import android.app.trust.ITrustManager.Stub;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.provider.Settings.Secure;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.Xml;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.R.styleable;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternUtils.StrongAuthTracker;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class TrustManagerService
  extends SystemService
{
  private static final boolean DEBUG = false;
  private static final int MSG_CLEANUP_USER = 8;
  private static final int MSG_DISPATCH_UNLOCK_ATTEMPT = 3;
  private static final int MSG_ENABLED_AGENTS_CHANGED = 4;
  private static final int MSG_FLUSH_TRUST_USUALLY_MANAGED = 10;
  private static final int MSG_KEYGUARD_SHOWING_CHANGED = 6;
  private static final int MSG_REGISTER_LISTENER = 1;
  private static final int MSG_START_USER = 7;
  private static final int MSG_SWITCH_USER = 9;
  private static final int MSG_UNLOCK_USER = 11;
  private static final int MSG_UNREGISTER_LISTENER = 2;
  private static final String PERMISSION_PROVIDE_AGENT = "android.permission.PROVIDE_TRUST_AGENT";
  private static final String TAG = "TrustManagerService";
  private static final Intent TRUST_AGENT_INTENT = new Intent("android.service.trust.TrustAgentService");
  private static final int TRUST_USUALLY_MANAGED_FLUSH_DELAY = 120000;
  private final ArraySet<AgentInfo> mActiveAgents = new ArraySet();
  private final ActivityManager mActivityManager;
  final TrustArchive mArchive = new TrustArchive();
  private final Context mContext;
  private int mCurrentUser = 0;
  @GuardedBy("mDeviceLockedForUser")
  private final SparseBooleanArray mDeviceLockedForUser = new SparseBooleanArray();
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      boolean bool = false;
      switch (???.what)
      {
      }
      for (;;)
      {
        return;
        TrustManagerService.-wrap4(TrustManagerService.this, (ITrustListener)???.obj);
        return;
        TrustManagerService.-wrap9(TrustManagerService.this, (ITrustListener)???.obj);
        return;
        Object localObject1 = TrustManagerService.this;
        if (???.arg1 != 0) {
          bool = true;
        }
        TrustManagerService.-wrap5((TrustManagerService)localObject1, bool, ???.arg2);
        return;
        TrustManagerService.this.refreshAgentList(-1);
        TrustManagerService.-wrap7(TrustManagerService.this, -1);
        return;
        TrustManagerService.-wrap7(TrustManagerService.this, TrustManagerService.-get2(TrustManagerService.this));
        return;
        TrustManagerService.this.refreshAgentList(???.arg1);
        return;
        TrustManagerService.-set0(TrustManagerService.this, ???.arg1);
        TrustManagerService.-wrap7(TrustManagerService.this, -1);
        return;
        synchronized (TrustManagerService.-get8(TrustManagerService.this))
        {
          localObject1 = TrustManagerService.-get8(TrustManagerService.this).clone();
          int i = 0;
          if (i >= ((SparseBooleanArray)localObject1).size()) {
            continue;
          }
          int j = ((SparseBooleanArray)localObject1).keyAt(i);
          bool = ((SparseBooleanArray)localObject1).valueAt(i);
          if (bool != TrustManagerService.-get5(TrustManagerService.this).isTrustUsuallyManaged(j)) {
            TrustManagerService.-get5(TrustManagerService.this).setTrustUsuallyManaged(bool, j);
          }
          i += 1;
        }
      }
    }
  };
  private final LockPatternUtils mLockPatternUtils;
  private final PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    public boolean onPackageChanged(String paramAnonymousString, int paramAnonymousInt, String[] paramAnonymousArrayOfString)
    {
      return true;
    }
    
    public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
    {
      TrustManagerService.-wrap8(TrustManagerService.this, paramAnonymousString);
    }
    
    public void onSomePackagesChanged()
    {
      TrustManagerService.this.refreshAgentList(-1);
    }
  };
  private final Receiver mReceiver = new Receiver(null);
  private final IBinder mService = new ITrustManager.Stub()
  {
    private String dumpBool(boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean) {
        return "1";
      }
      return "0";
    }
    
    private String dumpHex(int paramAnonymousInt)
    {
      return "0x" + Integer.toHexString(paramAnonymousInt);
    }
    
    private void dumpUser(PrintWriter paramAnonymousPrintWriter, UserInfo paramAnonymousUserInfo, boolean paramAnonymousBoolean)
    {
      paramAnonymousPrintWriter.printf(" User \"%s\" (id=%d, flags=%#x)", new Object[] { paramAnonymousUserInfo.name, Integer.valueOf(paramAnonymousUserInfo.id), Integer.valueOf(paramAnonymousUserInfo.flags) });
      if (!paramAnonymousUserInfo.supportsSwitchToByUser())
      {
        paramAnonymousPrintWriter.println("(managed profile)");
        paramAnonymousPrintWriter.println("   disabled because switching to this user is not possible.");
        return;
      }
      if (paramAnonymousBoolean) {
        paramAnonymousPrintWriter.print(" (current)");
      }
      paramAnonymousPrintWriter.print(": trusted=" + dumpBool(TrustManagerService.-wrap1(TrustManagerService.this, paramAnonymousUserInfo.id)));
      paramAnonymousPrintWriter.print(", trustManaged=" + dumpBool(TrustManagerService.-wrap0(TrustManagerService.this, paramAnonymousUserInfo.id)));
      paramAnonymousPrintWriter.print(", deviceLocked=" + dumpBool(TrustManagerService.this.isDeviceLockedInner(paramAnonymousUserInfo.id)));
      paramAnonymousPrintWriter.print(", strongAuthRequired=" + dumpHex(TrustManagerService.-get6(TrustManagerService.this).getStrongAuthForUser(paramAnonymousUserInfo.id)));
      paramAnonymousPrintWriter.println();
      paramAnonymousPrintWriter.println("   Enabled agents:");
      paramAnonymousBoolean = false;
      ArraySet localArraySet = new ArraySet();
      Iterator localIterator = TrustManagerService.-get0(TrustManagerService.this).iterator();
      while (localIterator.hasNext())
      {
        TrustManagerService.AgentInfo localAgentInfo = (TrustManagerService.AgentInfo)localIterator.next();
        if (localAgentInfo.userId == paramAnonymousUserInfo.id)
        {
          boolean bool = localAgentInfo.agent.isTrusted();
          paramAnonymousPrintWriter.print("    ");
          paramAnonymousPrintWriter.println(localAgentInfo.component.flattenToShortString());
          paramAnonymousPrintWriter.print("     bound=" + dumpBool(localAgentInfo.agent.isBound()));
          paramAnonymousPrintWriter.print(", connected=" + dumpBool(localAgentInfo.agent.isConnected()));
          paramAnonymousPrintWriter.print(", managingTrust=" + dumpBool(localAgentInfo.agent.isManagingTrust()));
          paramAnonymousPrintWriter.print(", trusted=" + dumpBool(bool));
          paramAnonymousPrintWriter.println();
          if (bool) {
            paramAnonymousPrintWriter.println("      message=\"" + localAgentInfo.agent.getMessage() + "\"");
          }
          if (!localAgentInfo.agent.isConnected())
          {
            String str = TrustArchive.formatDuration(localAgentInfo.agent.getScheduledRestartUptimeMillis() - SystemClock.uptimeMillis());
            paramAnonymousPrintWriter.println("      restartScheduledAt=" + str);
          }
          if (!localArraySet.add(TrustArchive.getSimpleName(localAgentInfo.component))) {
            paramAnonymousBoolean = true;
          }
        }
      }
      paramAnonymousPrintWriter.println("   Events:");
      TrustManagerService.this.mArchive.dump(paramAnonymousPrintWriter, 50, paramAnonymousUserInfo.id, "    ", paramAnonymousBoolean);
      paramAnonymousPrintWriter.println();
    }
    
    private void enforceListenerPermission()
    {
      TrustManagerService.-get1(TrustManagerService.this).enforceCallingPermission("android.permission.TRUST_LISTENER", "register trust listener");
    }
    
    private void enforceReportPermission()
    {
      TrustManagerService.-get1(TrustManagerService.this).enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "reporting trust events");
    }
    
    protected void dump(final FileDescriptor paramAnonymousFileDescriptor, final PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      TrustManagerService.-get1(TrustManagerService.this).enforceCallingPermission("android.permission.DUMP", "dumping TrustManagerService");
      if (TrustManagerService.this.isSafeMode())
      {
        paramAnonymousPrintWriter.println("disabled because the system is in safe mode.");
        return;
      }
      if (!TrustManagerService.-get7(TrustManagerService.this))
      {
        paramAnonymousPrintWriter.println("disabled because the third-party apps can't run yet.");
        return;
      }
      paramAnonymousFileDescriptor = TrustManagerService.-get10(TrustManagerService.this).getUsers(true);
      TrustManagerService.-get4(TrustManagerService.this).runWithScissors(new Runnable()
      {
        public void run()
        {
          paramAnonymousPrintWriter.println("Trust manager state:");
          Iterator localIterator = paramAnonymousFileDescriptor.iterator();
          if (localIterator.hasNext())
          {
            UserInfo localUserInfo = (UserInfo)localIterator.next();
            TrustManagerService.1 local1 = TrustManagerService.1.this;
            PrintWriter localPrintWriter = paramAnonymousPrintWriter;
            if (localUserInfo.id == TrustManagerService.-get2(TrustManagerService.this)) {}
            for (boolean bool = true;; bool = false)
            {
              TrustManagerService.1.-wrap0(local1, localPrintWriter, localUserInfo, bool);
              break;
            }
          }
        }
      }, 1500L);
    }
    
    public boolean isDeviceLocked(int paramAnonymousInt)
      throws RemoteException
    {
      int i = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), paramAnonymousInt, false, true, "isDeviceLocked", null);
      long l = Binder.clearCallingIdentity();
      paramAnonymousInt = i;
      try
      {
        if (!TrustManagerService.-get5(TrustManagerService.this).isSeparateProfileChallengeEnabled(i)) {
          paramAnonymousInt = TrustManagerService.-wrap3(TrustManagerService.this, i);
        }
        boolean bool = TrustManagerService.this.isDeviceLockedInner(paramAnonymousInt);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isDeviceSecure(int paramAnonymousInt)
      throws RemoteException
    {
      int i = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), paramAnonymousInt, false, true, "isDeviceSecure", null);
      long l = Binder.clearCallingIdentity();
      paramAnonymousInt = i;
      try
      {
        if (!TrustManagerService.-get5(TrustManagerService.this).isSeparateProfileChallengeEnabled(i)) {
          paramAnonymousInt = TrustManagerService.-wrap3(TrustManagerService.this, i);
        }
        boolean bool = TrustManagerService.-get5(TrustManagerService.this).isSecure(paramAnonymousInt);
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public boolean isTrustUsuallyManaged(int paramAnonymousInt)
    {
      TrustManagerService.-get1(TrustManagerService.this).enforceCallingPermission("android.permission.TRUST_LISTENER", "query trust state");
      return TrustManagerService.-wrap2(TrustManagerService.this, paramAnonymousInt);
    }
    
    public void registerTrustListener(ITrustListener paramAnonymousITrustListener)
      throws RemoteException
    {
      enforceListenerPermission();
      TrustManagerService.-get4(TrustManagerService.this).obtainMessage(1, paramAnonymousITrustListener).sendToTarget();
    }
    
    public void reportEnabledTrustAgentsChanged(int paramAnonymousInt)
      throws RemoteException
    {
      enforceReportPermission();
      TrustManagerService.-get4(TrustManagerService.this).removeMessages(4);
      TrustManagerService.-get4(TrustManagerService.this).sendEmptyMessage(4);
    }
    
    public void reportKeyguardShowingChanged()
      throws RemoteException
    {
      enforceReportPermission();
      TrustManagerService.-get4(TrustManagerService.this).removeMessages(6);
      TrustManagerService.-get4(TrustManagerService.this).sendEmptyMessage(6);
    }
    
    public void reportUnlockAttempt(boolean paramAnonymousBoolean, int paramAnonymousInt)
      throws RemoteException
    {
      enforceReportPermission();
      Handler localHandler = TrustManagerService.-get4(TrustManagerService.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(3, i, paramAnonymousInt).sendToTarget();
        return;
      }
    }
    
    /* Error */
    public void setDeviceLockedForUser(int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 372	com/android/server/trust/TrustManagerService$1:enforceReportPermission	()V
      //   4: invokestatic 326	android/os/Binder:clearCallingIdentity	()J
      //   7: lstore_3
      //   8: aload_0
      //   9: getfield 21	com/android/server/trust/TrustManagerService$1:this$0	Lcom/android/server/trust/TrustManagerService;
      //   12: invokestatic 330	com/android/server/trust/TrustManagerService:-get5	(Lcom/android/server/trust/TrustManagerService;)Lcom/android/internal/widget/LockPatternUtils;
      //   15: iload_1
      //   16: invokevirtual 335	com/android/internal/widget/LockPatternUtils:isSeparateProfileChallengeEnabled	(I)Z
      //   19: ifeq +43 -> 62
      //   22: aload_0
      //   23: getfield 21	com/android/server/trust/TrustManagerService$1:this$0	Lcom/android/server/trust/TrustManagerService;
      //   26: invokestatic 390	com/android/server/trust/TrustManagerService:-get3	(Lcom/android/server/trust/TrustManagerService;)Landroid/util/SparseBooleanArray;
      //   29: astore 5
      //   31: aload 5
      //   33: monitorenter
      //   34: aload_0
      //   35: getfield 21	com/android/server/trust/TrustManagerService$1:this$0	Lcom/android/server/trust/TrustManagerService;
      //   38: invokestatic 390	com/android/server/trust/TrustManagerService:-get3	(Lcom/android/server/trust/TrustManagerService;)Landroid/util/SparseBooleanArray;
      //   41: iload_1
      //   42: iload_2
      //   43: invokevirtual 395	android/util/SparseBooleanArray:put	(IZ)V
      //   46: aload 5
      //   48: monitorexit
      //   49: iload_2
      //   50: ifeq +12 -> 62
      //   53: invokestatic 401	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
      //   56: iload_1
      //   57: invokeinterface 406 2 0
      //   62: lload_3
      //   63: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
      //   66: return
      //   67: astore 6
      //   69: aload 5
      //   71: monitorexit
      //   72: aload 6
      //   74: athrow
      //   75: astore 5
      //   77: lload_3
      //   78: invokestatic 343	android/os/Binder:restoreCallingIdentity	(J)V
      //   81: aload 5
      //   83: athrow
      //   84: astore 5
      //   86: goto -24 -> 62
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	89	0	this	1
      //   0	89	1	paramAnonymousInt	int
      //   0	89	2	paramAnonymousBoolean	boolean
      //   7	71	3	l	long
      //   75	7	5	localObject1	Object
      //   84	1	5	localRemoteException	RemoteException
      //   67	6	6	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   34	46	67	finally
      //   8	34	75	finally
      //   46	49	75	finally
      //   53	62	75	finally
      //   69	75	75	finally
      //   53	62	84	android/os/RemoteException
    }
    
    public void unregisterTrustListener(ITrustListener paramAnonymousITrustListener)
      throws RemoteException
    {
      enforceListenerPermission();
      TrustManagerService.-get4(TrustManagerService.this).obtainMessage(2, paramAnonymousITrustListener).sendToTarget();
    }
  };
  private final StrongAuthTracker mStrongAuthTracker;
  private boolean mTrustAgentsCanRun = false;
  private final ArrayList<ITrustListener> mTrustListeners = new ArrayList();
  @GuardedBy("mDeviceLockedForUser")
  private final SparseBooleanArray mTrustUsuallyManagedForUser = new SparseBooleanArray();
  @GuardedBy("mUserIsTrusted")
  private final SparseBooleanArray mUserIsTrusted = new SparseBooleanArray();
  private final UserManager mUserManager;
  
  public TrustManagerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mActivityManager = ((ActivityManager)this.mContext.getSystemService("activity"));
    this.mLockPatternUtils = new LockPatternUtils(paramContext);
    this.mStrongAuthTracker = new StrongAuthTracker(paramContext);
  }
  
  private void addListener(ITrustListener paramITrustListener)
  {
    int i = 0;
    while (i < this.mTrustListeners.size())
    {
      if (((ITrustListener)this.mTrustListeners.get(i)).asBinder() == paramITrustListener.asBinder()) {
        return;
      }
      i += 1;
    }
    this.mTrustListeners.add(paramITrustListener);
    updateTrustAll();
  }
  
  private boolean aggregateIsTrustManaged(int paramInt)
  {
    if (!this.mStrongAuthTracker.isTrustAllowedForUser(paramInt)) {
      return false;
    }
    int i = 0;
    while (i < this.mActiveAgents.size())
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(i);
      if ((localAgentInfo.userId == paramInt) && (localAgentInfo.agent.isManagingTrust())) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean aggregateIsTrusted(int paramInt)
  {
    if (!this.mStrongAuthTracker.isTrustAllowedForUser(paramInt)) {
      return false;
    }
    int i = 0;
    while (i < this.mActiveAgents.size())
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(i);
      if ((localAgentInfo.userId == paramInt) && (localAgentInfo.agent.isTrusted())) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void dispatchDeviceLocked(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mActiveAgents.size())
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(i);
      if (localAgentInfo.userId == paramInt)
      {
        if (!paramBoolean) {
          break label54;
        }
        localAgentInfo.agent.onDeviceLocked();
      }
      for (;;)
      {
        i += 1;
        break;
        label54:
        localAgentInfo.agent.onDeviceUnlocked();
      }
    }
  }
  
  private void dispatchOnTrustChanged(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (!paramBoolean) {
      i = 0;
    }
    paramInt2 = 0;
    for (;;)
    {
      if (paramInt2 < this.mTrustListeners.size()) {
        try
        {
          ((ITrustListener)this.mTrustListeners.get(paramInt2)).onTrustChanged(paramBoolean, paramInt1, i);
          paramInt2 += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TrustManagerService", "Exception while notifying TrustListener.", localRemoteException);
          }
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            Slog.d("TrustManagerService", "Removing dead TrustListener.");
            this.mTrustListeners.remove(paramInt2);
            paramInt2 -= 1;
          }
        }
      }
    }
  }
  
  private void dispatchOnTrustManagedChanged(boolean paramBoolean, int paramInt)
  {
    int i = 0;
    for (;;)
    {
      if (i < this.mTrustListeners.size()) {
        try
        {
          ((ITrustListener)this.mTrustListeners.get(i)).onTrustManagedChanged(paramBoolean, paramInt);
          i += 1;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.e("TrustManagerService", "Exception while notifying TrustListener.", localRemoteException);
          }
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            Slog.d("TrustManagerService", "Removing dead TrustListener.");
            this.mTrustListeners.remove(i);
            i -= 1;
          }
        }
      }
    }
  }
  
  private void dispatchUnlockAttempt(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {
      this.mStrongAuthTracker.allowTrustFromUnlock(paramInt);
    }
    int i = 0;
    while (i < this.mActiveAgents.size())
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(i);
      if (localAgentInfo.userId == paramInt) {
        localAgentInfo.agent.onUnlockAttempt(paramBoolean);
      }
      i += 1;
    }
  }
  
  private ComponentName getComponentName(ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {
      return null;
    }
    return new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name);
  }
  
  private ComponentName getSettingsComponentName(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {}
    while (paramResolveInfo.serviceInfo.metaData == null) {
      return null;
    }
    Object localObject9 = null;
    Object localObject10 = null;
    String str1 = null;
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject2 = null;
    Object localObject5 = null;
    Object localObject8 = null;
    String str2 = str1;
    Object localObject3 = localObject9;
    Object localObject4 = localObject10;
    try
    {
      XmlResourceParser localXmlResourceParser = paramResolveInfo.serviceInfo.loadXmlMetaData(paramPackageManager, "android.service.trust.trustagent");
      if (localXmlResourceParser == null)
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        Slog.w("TrustManagerService", "Can't find android.service.trust.trustagent meta-data");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramResolveInfo.serviceInfo.applicationInfo);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        i = localXmlResourceParser.next();
      } while ((i != 1) && (i != 2));
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      if (!"trust-agent".equals(localXmlResourceParser.getName()))
      {
        str2 = str1;
        localObject5 = localXmlResourceParser;
        localObject3 = localObject9;
        localObject6 = localXmlResourceParser;
        localObject4 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        Slog.w("TrustManagerService", "Meta-data does not start with trust-agent tag");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.obtainAttributes(localAttributeSet, R.styleable.TrustAgent);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = localObject9;
      localObject6 = localXmlResourceParser;
      localObject4 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      str1 = paramPackageManager.getString(2);
      str2 = str1;
      localObject5 = localXmlResourceParser;
      localObject3 = str1;
      localObject6 = localXmlResourceParser;
      localObject4 = str1;
      localObject7 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      paramPackageManager.recycle();
      localObject2 = localObject8;
      paramPackageManager = str1;
      if (localXmlResourceParser != null)
      {
        localXmlResourceParser.close();
        paramPackageManager = str1;
        localObject2 = localObject8;
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        localObject2 = localXmlPullParserException;
        paramPackageManager = str2;
        if (localObject5 != null)
        {
          ((XmlResourceParser)localObject5).close();
          localObject2 = localXmlPullParserException;
          paramPackageManager = str2;
        }
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localObject2 = localIOException;
        paramPackageManager = (PackageManager)localObject3;
        if (localObject6 != null)
        {
          ((XmlResourceParser)localObject6).close();
          localObject2 = localIOException;
          paramPackageManager = (PackageManager)localObject3;
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        localObject2 = localNameNotFoundException;
        paramPackageManager = (PackageManager)localObject4;
        if (localObject7 != null)
        {
          ((XmlResourceParser)localObject7).close();
          localObject2 = localNameNotFoundException;
          paramPackageManager = (PackageManager)localObject4;
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label622;
      }
      ((XmlResourceParser)localObject2).close();
    }
    if (localObject2 != null)
    {
      Slog.w("TrustManagerService", "Error parsing : " + paramResolveInfo.serviceInfo.packageName, (Throwable)localObject2);
      return null;
    }
    label622:
    if (paramPackageManager == null) {
      return null;
    }
    Object localObject1 = paramPackageManager;
    if (paramPackageManager.indexOf('/') < 0) {
      localObject1 = paramResolveInfo.serviceInfo.packageName + "/" + paramPackageManager;
    }
    return ComponentName.unflattenFromString((String)localObject1);
  }
  
  private boolean isTrustUsuallyManagedInternal(int paramInt)
  {
    int i;
    boolean bool;
    synchronized (this.mTrustUsuallyManagedForUser)
    {
      i = this.mTrustUsuallyManagedForUser.indexOfKey(paramInt);
      if (i >= 0)
      {
        bool = this.mTrustUsuallyManagedForUser.valueAt(i);
        return bool;
      }
      bool = this.mLockPatternUtils.isTrustUsuallyManaged(paramInt);
    }
    synchronized (this.mTrustUsuallyManagedForUser)
    {
      i = this.mTrustUsuallyManagedForUser.indexOfKey(paramInt);
      if (i >= 0)
      {
        bool = this.mTrustUsuallyManagedForUser.valueAt(i);
        return bool;
        localObject1 = finally;
        throw ((Throwable)localObject1);
      }
      this.mTrustUsuallyManagedForUser.put(paramInt, bool);
      return bool;
    }
  }
  
  private void maybeEnableFactoryTrustAgents(LockPatternUtils paramLockPatternUtils, int paramInt)
  {
    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 0, paramInt) != 0) {
      return;
    }
    Object localObject = resolveAllowedTrustAgents(this.mContext.getPackageManager(), paramInt);
    ArraySet localArraySet = new ArraySet();
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
      ComponentName localComponentName = getComponentName(localResolveInfo);
      if ((localResolveInfo.serviceInfo.applicationInfo.flags & 0x1) == 0) {
        Log.i("TrustManagerService", "Leaving agent " + localComponentName + " disabled because package " + "is not a system package.");
      } else {
        localArraySet.add(localComponentName);
      }
    }
    localObject = paramLockPatternUtils.getEnabledTrustAgents(paramInt);
    if (localObject != null) {
      localArraySet.addAll((Collection)localObject);
    }
    paramLockPatternUtils.setEnabledTrustAgents(localArraySet, paramInt);
    Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 1, paramInt);
  }
  
  private void refreshDeviceLockedForUser(int paramInt)
  {
    int i = paramInt;
    if (paramInt != -1)
    {
      i = paramInt;
      if (paramInt < 0)
      {
        Log.e("TrustManagerService", "refreshDeviceLockedForUser(userId=" + paramInt + "): Invalid user handle," + " must be USER_ALL or a specific user.", new Throwable("here"));
        i = -1;
      }
    }
    Object localObject1;
    if (i == -1) {
      localObject1 = this.mUserManager.getUsers(true);
    }
    for (;;)
    {
      IWindowManager localIWindowManager = WindowManagerGlobal.getWindowManagerService();
      paramInt = 0;
      label85:
      if (paramInt < ((List)localObject1).size())
      {
        ??? = (UserInfo)((List)localObject1).get(paramInt);
        int j;
        boolean bool3;
        boolean bool4;
        boolean bool2;
        boolean bool1;
        if ((??? != null) && (!((UserInfo)???).partial) && (((UserInfo)???).isEnabled()) && (!((UserInfo)???).guestToRemove) && (((UserInfo)???).supportsSwitchToByUser()))
        {
          j = ((UserInfo)???).id;
          bool3 = this.mLockPatternUtils.isSecure(j);
          bool4 = aggregateIsTrusted(j);
          bool2 = true;
          bool1 = bool2;
          if (this.mCurrentUser != j) {}
        }
        try
        {
          bool1 = localIWindowManager.isKeyguardLocked();
          if ((!bool3) || (!bool1) || (bool4)) {
            bool1 = false;
          }
        }
        catch (RemoteException localRemoteException)
        {
          synchronized (this.mDeviceLockedForUser)
          {
            for (;;)
            {
              if (isDeviceLockedInner(j) == bool1) {
                break label306;
              }
              i = 1;
              this.mDeviceLockedForUser.put(j, bool1);
              if (i != 0) {
                dispatchDeviceLocked(j, bool1);
              }
              paramInt += 1;
              break label85;
              localObject1 = new ArrayList();
              ((List)localObject1).add(this.mUserManager.getUserInfo(i));
              break;
              localRemoteException = localRemoteException;
              bool1 = bool2;
              continue;
              bool1 = true;
            }
            label306:
            i = 0;
          }
        }
      }
    }
  }
  
  private void removeAgentsOfPackage(String paramString)
  {
    int i = 0;
    int j = this.mActiveAgents.size() - 1;
    while (j >= 0)
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(j);
      int k = i;
      if (paramString.equals(localAgentInfo.component.getPackageName()))
      {
        Log.i("TrustManagerService", "Resetting agent " + localAgentInfo.component.flattenToShortString());
        if (localAgentInfo.agent.isManagingTrust()) {
          i = 1;
        }
        localAgentInfo.agent.destroy();
        this.mActiveAgents.removeAt(j);
        k = i;
      }
      j -= 1;
      i = k;
    }
    if (i != 0) {
      updateTrustAll();
    }
  }
  
  private void removeListener(ITrustListener paramITrustListener)
  {
    int i = 0;
    while (i < this.mTrustListeners.size())
    {
      if (((ITrustListener)this.mTrustListeners.get(i)).asBinder() == paramITrustListener.asBinder())
      {
        this.mTrustListeners.remove(i);
        return;
      }
      i += 1;
    }
  }
  
  private List<ResolveInfo> resolveAllowedTrustAgents(PackageManager paramPackageManager, int paramInt)
  {
    Object localObject1 = paramPackageManager.queryIntentServicesAsUser(TRUST_AGENT_INTENT, 786432, paramInt);
    ArrayList localArrayList = new ArrayList(((List)localObject1).size());
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (ResolveInfo)((Iterator)localObject1).next();
      if ((((ResolveInfo)localObject2).serviceInfo != null) && (((ResolveInfo)localObject2).serviceInfo.applicationInfo != null)) {
        if (paramPackageManager.checkPermission("android.permission.PROVIDE_TRUST_AGENT", ((ResolveInfo)localObject2).serviceInfo.packageName) != 0)
        {
          localObject2 = getComponentName((ResolveInfo)localObject2);
          Log.w("TrustManagerService", "Skipping agent " + localObject2 + " because package does not have" + " permission " + "android.permission.PROVIDE_TRUST_AGENT" + ".");
        }
        else
        {
          localArrayList.add(localObject2);
        }
      }
    }
    return localArrayList;
  }
  
  private int resolveProfileParent(int paramInt)
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
      return paramInt;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private void updateTrustAll()
  {
    Iterator localIterator = this.mUserManager.getUsers(true).iterator();
    while (localIterator.hasNext()) {
      updateTrust(((UserInfo)localIterator.next()).id, 0);
    }
  }
  
  private void updateTrustUsuallyManaged(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mTrustUsuallyManagedForUser)
    {
      this.mTrustUsuallyManagedForUser.put(paramInt, paramBoolean);
      this.mHandler.removeMessages(10);
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(10), 120000L);
      return;
    }
  }
  
  boolean isDeviceLockedInner(int paramInt)
  {
    synchronized (this.mDeviceLockedForUser)
    {
      boolean bool = this.mDeviceLockedForUser.get(paramInt, true);
      return bool;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    if (isSafeMode()) {
      return;
    }
    if (paramInt == 500)
    {
      this.mPackageMonitor.register(this.mContext, this.mHandler.getLooper(), UserHandle.ALL, true);
      this.mReceiver.register(this.mContext);
      this.mLockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
    }
    do
    {
      return;
      if (paramInt == 600)
      {
        this.mTrustAgentsCanRun = true;
        refreshAgentList(-1);
        return;
      }
    } while (paramInt != 1000);
    maybeEnableFactoryTrustAgents(this.mLockPatternUtils, 0);
  }
  
  public void onCleanupUser(int paramInt)
  {
    this.mHandler.obtainMessage(8, paramInt, 0, null).sendToTarget();
  }
  
  public void onStart()
  {
    publishBinderService("trust", this.mService);
  }
  
  public void onStartUser(int paramInt)
  {
    this.mHandler.obtainMessage(7, paramInt, 0, null).sendToTarget();
  }
  
  public void onSwitchUser(int paramInt)
  {
    this.mHandler.obtainMessage(9, paramInt, 0, null).sendToTarget();
  }
  
  public void onUnlockUser(int paramInt)
  {
    this.mHandler.obtainMessage(11, paramInt, 0, null).sendToTarget();
  }
  
  void refreshAgentList(int paramInt)
  {
    if (!this.mTrustAgentsCanRun) {
      return;
    }
    int i = paramInt;
    if (paramInt != -1)
    {
      i = paramInt;
      if (paramInt < 0)
      {
        Log.e("TrustManagerService", "refreshAgentList(userId=" + paramInt + "): Invalid user handle," + " must be USER_ALL or a specific user.", new Throwable("here"));
        i = -1;
      }
    }
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject1;
    LockPatternUtils localLockPatternUtils;
    ArraySet localArraySet;
    if (i == -1)
    {
      localObject1 = this.mUserManager.getUsers(true);
      localLockPatternUtils = this.mLockPatternUtils;
      localArraySet = new ArraySet();
      localArraySet.addAll(this.mActiveAgents);
      localObject1 = ((Iterable)localObject1).iterator();
    }
    label267:
    label543:
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext()) {
        break label545;
      }
      UserInfo localUserInfo = (UserInfo)((Iterator)localObject1).next();
      if ((localUserInfo != null) && (!localUserInfo.partial) && (localUserInfo.isEnabled()) && (!localUserInfo.guestToRemove) && (localUserInfo.supportsSwitchToByUser()) && (StorageManager.isUserKeyUnlocked(localUserInfo.id)) && (this.mActivityManager.isUserRunning(localUserInfo.id)) && (localLockPatternUtils.isSecure(localUserInfo.id)) && (this.mStrongAuthTracker.canAgentsRunForUser(localUserInfo.id)))
      {
        DevicePolicyManager localDevicePolicyManager = localLockPatternUtils.getDevicePolicyManager();
        List localList;
        Iterator localIterator;
        if ((localDevicePolicyManager.getKeyguardDisabledFeatures(null, localUserInfo.id) & 0x10) != 0)
        {
          paramInt = 1;
          localList = localLockPatternUtils.getEnabledTrustAgents(localUserInfo.id);
          if (localList != null) {
            localIterator = resolveAllowedTrustAgents(localPackageManager, localUserInfo.id).iterator();
          }
        }
        else
        {
          for (;;)
          {
            if (!localIterator.hasNext()) {
              break label543;
            }
            ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
            ComponentName localComponentName = getComponentName(localResolveInfo);
            if (localList.contains(localComponentName))
            {
              Object localObject2;
              if (paramInt != 0)
              {
                localObject2 = localDevicePolicyManager.getTrustAgentConfiguration(null, localComponentName, localUserInfo.id);
                if ((localObject2 == null) || (((List)localObject2).isEmpty())) {}
              }
              else
              {
                localObject2 = new AgentInfo(null);
                ((AgentInfo)localObject2).component = localComponentName;
                ((AgentInfo)localObject2).userId = localUserInfo.id;
                if (!this.mActiveAgents.contains(localObject2))
                {
                  ((AgentInfo)localObject2).label = localResolveInfo.loadLabel(localPackageManager);
                  ((AgentInfo)localObject2).icon = localResolveInfo.loadIcon(localPackageManager);
                  ((AgentInfo)localObject2).settings = getSettingsComponentName(localPackageManager, localResolveInfo);
                  ((AgentInfo)localObject2).agent = new TrustAgentWrapper(this.mContext, this, new Intent().setComponent(localComponentName), localUserInfo.getUserHandle());
                  this.mActiveAgents.add(localObject2);
                  continue;
                  localObject1 = new ArrayList();
                  ((List)localObject1).add(this.mUserManager.getUserInfo(i));
                  break;
                  paramInt = 0;
                  break label267;
                }
                localArraySet.remove(localObject2);
              }
            }
          }
        }
      }
    }
    label545:
    paramInt = 0;
    int j = 0;
    while (j < localArraySet.size())
    {
      localObject1 = (AgentInfo)localArraySet.valueAt(j);
      int k;
      if (i != -1)
      {
        k = paramInt;
        if (i != ((AgentInfo)localObject1).userId) {}
      }
      else
      {
        if (((AgentInfo)localObject1).agent.isManagingTrust()) {
          paramInt = 1;
        }
        ((AgentInfo)localObject1).agent.destroy();
        this.mActiveAgents.remove(localObject1);
        k = paramInt;
      }
      j += 1;
      paramInt = k;
    }
    if (paramInt != 0)
    {
      if (i == -1) {
        updateTrustAll();
      }
    }
    else {
      return;
    }
    updateTrust(i, 0);
  }
  
  public void resetAgent(ComponentName paramComponentName, int paramInt)
  {
    int i = 0;
    int j = this.mActiveAgents.size() - 1;
    while (j >= 0)
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(j);
      int k = i;
      if (paramComponentName.equals(localAgentInfo.component))
      {
        k = i;
        if (paramInt == localAgentInfo.userId)
        {
          Log.i("TrustManagerService", "Resetting agent " + localAgentInfo.component.flattenToShortString());
          if (localAgentInfo.agent.isManagingTrust()) {
            i = 1;
          }
          localAgentInfo.agent.destroy();
          this.mActiveAgents.removeAt(j);
          k = i;
        }
      }
      j -= 1;
      i = k;
    }
    if (i != 0) {
      updateTrust(paramInt, 0);
    }
    refreshAgentList(paramInt);
  }
  
  void updateDevicePolicyFeatures()
  {
    int j = 0;
    int i = 0;
    while (i < this.mActiveAgents.size())
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mActiveAgents.valueAt(i);
      if (localAgentInfo.agent.isConnected())
      {
        localAgentInfo.agent.updateDevicePolicyFeatures();
        j = 1;
      }
      i += 1;
    }
    if (j != 0) {
      this.mArchive.logDevicePolicyChanged();
    }
  }
  
  public void updateTrust(int paramInt1, int paramInt2)
  {
    boolean bool = aggregateIsTrustManaged(paramInt1);
    dispatchOnTrustManagedChanged(bool, paramInt1);
    if ((this.mStrongAuthTracker.isTrustAllowedForUser(paramInt1)) && (isTrustUsuallyManagedInternal(paramInt1) != bool)) {
      updateTrustUsuallyManaged(paramInt1, bool);
    }
    bool = aggregateIsTrusted(paramInt1);
    synchronized (this.mUserIsTrusted)
    {
      if (this.mUserIsTrusted.get(paramInt1) != bool)
      {
        i = 1;
        this.mUserIsTrusted.put(paramInt1, bool);
        dispatchOnTrustChanged(bool, paramInt1, paramInt2);
        if (i != 0) {
          refreshDeviceLockedForUser(paramInt1);
        }
        return;
      }
      int i = 0;
    }
  }
  
  private static final class AgentInfo
  {
    TrustAgentWrapper agent;
    ComponentName component;
    Drawable icon;
    CharSequence label;
    ComponentName settings;
    int userId;
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof AgentInfo)) {
        return false;
      }
      paramObject = (AgentInfo)paramObject;
      boolean bool1 = bool2;
      if (this.component.equals(((AgentInfo)paramObject).component))
      {
        bool1 = bool2;
        if (this.userId == ((AgentInfo)paramObject).userId) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return this.component.hashCode() * 31 + this.userId;
    }
  }
  
  private class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    private int getUserId(Intent paramIntent)
    {
      int i = paramIntent.getIntExtra("android.intent.extra.user_handle", -100);
      if (i > 0) {
        return i;
      }
      Slog.wtf("TrustManagerService", "EXTRA_USER_HANDLE missing or invalid, value=" + i);
      return -100;
    }
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      ??? = paramIntent.getAction();
      if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(???))
      {
        TrustManagerService.this.refreshAgentList(getSendingUserId());
        TrustManagerService.this.updateDevicePolicyFeatures();
      }
      int i;
      do
      {
        do
        {
          do
          {
            return;
            if (!"android.intent.action.USER_ADDED".equals(???)) {
              break;
            }
            i = getUserId(paramIntent);
          } while (i <= 0);
          TrustManagerService.-wrap6(TrustManagerService.this, TrustManagerService.-get5(TrustManagerService.this), i);
          return;
        } while (!"android.intent.action.USER_REMOVED".equals(???));
        i = getUserId(paramIntent);
      } while (i <= 0);
      synchronized (TrustManagerService.-get9(TrustManagerService.this))
      {
        TrustManagerService.-get9(TrustManagerService.this).delete(i);
      }
      synchronized (TrustManagerService.-get3(TrustManagerService.this))
      {
        TrustManagerService.-get3(TrustManagerService.this).delete(i);
        TrustManagerService.this.refreshAgentList(i);
        TrustManagerService.-wrap7(TrustManagerService.this, i);
        return;
        paramIntent = finally;
        throw paramIntent;
      }
    }
    
    public void register(Context paramContext)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
      localIntentFilter.addAction("android.intent.action.USER_ADDED");
      localIntentFilter.addAction("android.intent.action.USER_REMOVED");
      paramContext.registerReceiverAsUser(this, UserHandle.ALL, localIntentFilter, null, null);
    }
  }
  
  private class StrongAuthTracker
    extends LockPatternUtils.StrongAuthTracker
  {
    SparseBooleanArray mStartFromSuccessfulUnlock = new SparseBooleanArray();
    
    public StrongAuthTracker(Context paramContext)
    {
      super();
    }
    
    void allowTrustFromUnlock(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("userId must be a valid user: " + paramInt);
      }
      boolean bool = canAgentsRunForUser(paramInt);
      this.mStartFromSuccessfulUnlock.put(paramInt, true);
      if (canAgentsRunForUser(paramInt) != bool) {
        TrustManagerService.this.refreshAgentList(paramInt);
      }
    }
    
    boolean canAgentsRunForUser(int paramInt)
    {
      if (!this.mStartFromSuccessfulUnlock.get(paramInt)) {
        return super.isTrustAllowedForUser(paramInt);
      }
      return true;
    }
    
    public void onStrongAuthRequiredChanged(int paramInt)
    {
      this.mStartFromSuccessfulUnlock.delete(paramInt);
      Log.i("TrustManagerService", "onStrongAuthRequiredChanged(" + paramInt + ") ->" + " trustAllowed=" + isTrustAllowedForUser(paramInt) + " agentsCanRun=" + canAgentsRunForUser(paramInt));
      TrustManagerService.this.refreshAgentList(paramInt);
      TrustManagerService.this.updateTrust(paramInt, 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/trust/TrustManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */