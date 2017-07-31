package com.android.server.net;

import android.annotation.IntDef;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.IUidObserver;
import android.app.IUidObserver.Stub;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.usage.UsageStatsManagerInternal;
import android.app.usage.UsageStatsManagerInternal.AppIdleStateChangeListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager.Stub;
import android.net.INetworkStatsService;
import android.net.LinkProperties;
import android.net.NetworkIdentity;
import android.net.NetworkInfo;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkQuotaInfo;
import android.net.NetworkState;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IDeviceIdleController;
import android.os.IDeviceIdleController.Stub;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.PowerManagerInternal;
import android.os.PowerManagerInternal.LowPowerModeListener;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.text.format.Time;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.DebugUtils;
import android.util.Log;
import android.util.NtpTrustedTime;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TrustedTime;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.NetPluginDelegate;
import com.android.server.SystemConfig;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class NetworkPolicyManagerService
  extends INetworkPolicyManager.Stub
{
  private static final String ACTION_ALLOW_BACKGROUND = "com.android.server.net.action.ALLOW_BACKGROUND";
  public static final String ACTION_DATAUSAGE_ALARM = "com.oneplus.security.action.DATAUSAGE_ALARM";
  private static final String ACTION_SNOOZE_WARNING = "com.android.server.net.action.SNOOZE_WARNING";
  private static final String ATTR_APP_ID = "appId";
  private static final String ATTR_CYCLE_DAY = "cycleDay";
  private static final String ATTR_CYCLE_TIMEZONE = "cycleTimezone";
  private static final String ATTR_INFERRED = "inferred";
  private static final String ATTR_LAST_LIMIT_SNOOZE = "lastLimitSnooze";
  private static final String ATTR_LAST_SNOOZE = "lastSnooze";
  private static final String ATTR_LAST_WARNING_SNOOZE = "lastWarningSnooze";
  private static final String ATTR_LIMIT_BYTES = "limitBytes";
  private static final String ATTR_METERED = "metered";
  private static final String ATTR_NETWORK_ID = "networkId";
  private static final String ATTR_NETWORK_TEMPLATE = "networkTemplate";
  private static final String ATTR_POLICY = "policy";
  private static final String ATTR_RESTRICT_BACKGROUND = "restrictBackground";
  private static final String ATTR_SUBSCRIBER_ID = "subscriberId";
  private static final String ATTR_UID = "uid";
  private static final String ATTR_VERSION = "version";
  private static final String ATTR_WARNING_BYTES = "warningBytes";
  private static final int CHAIN_TOGGLE_DISABLE = 2;
  private static final int CHAIN_TOGGLE_ENABLE = 1;
  private static final int CHAIN_TOGGLE_NONE = 0;
  private static boolean LOGD = false;
  private static boolean LOGV = false;
  private static final int MSG_ADVISE_PERSIST_THRESHOLD = 7;
  private static final int MSG_LIMIT_REACHED = 5;
  private static final int MSG_METERED_IFACES_CHANGED = 2;
  private static final int MSG_REMOVE_INTERFACE_QUOTA = 11;
  private static final int MSG_RESTRICT_BACKGROUND_BLACKLIST_CHANGED = 12;
  private static final int MSG_RESTRICT_BACKGROUND_CHANGED = 6;
  private static final int MSG_RESTRICT_BACKGROUND_WHITELIST_CHANGED = 9;
  private static final int MSG_RULES_CHANGED = 1;
  private static final int MSG_SCREEN_ON_CHANGED = 8;
  private static final int MSG_SET_FIREWALL_RULES = 13;
  private static final int MSG_UID_GONE = 54088;
  private static final int MSG_UID_STATE_CHANGED = 7788;
  private static final int MSG_UPDATE_INTERFACE_QUOTA = 10;
  private static String SCREEN_DOZE_NETWORKPOLICY = "com.oneplus.android.checkDozeNetworkplicy";
  static final String TAG = "NetworkPolicy";
  private static final String TAG_APP_POLICY = "app-policy";
  private static final String TAG_NETWORK_POLICY = "network-policy";
  private static final String TAG_POLICY_LIST = "policy-list";
  private static final String TAG_RESTRICT_BACKGROUND = "restrict-background";
  private static final String TAG_REVOKED_RESTRICT_BACKGROUND = "revoked-restrict-background";
  private static final String TAG_UID_POLICY = "uid-policy";
  private static final String TAG_WHITELIST = "whitelist";
  private static final long TIME_CACHE_MAX_AGE = 172800000L;
  public static final int TYPE_LIMIT = 2;
  public static final int TYPE_LIMIT_SNOOZED = 3;
  private static final int TYPE_RESTRICT_BACKGROUND = 1;
  private static final int TYPE_RESTRICT_POWER = 2;
  public static final int TYPE_WARNING = 1;
  private static final int VERSION_ADDED_INFERRED = 7;
  private static final int VERSION_ADDED_METERED = 4;
  private static final int VERSION_ADDED_NETWORK_ID = 9;
  private static final int VERSION_ADDED_RESTRICT_BACKGROUND = 3;
  private static final int VERSION_ADDED_SNOOZE = 2;
  private static final int VERSION_ADDED_TIMEZONE = 6;
  private static final int VERSION_INIT = 1;
  private static final int VERSION_LATEST = 10;
  private static final int VERSION_SPLIT_SNOOZE = 5;
  private static final int VERSION_SWITCH_APP_ID = 8;
  private static final int VERSION_SWITCH_UID = 10;
  private static boolean isDozeChangeSupport = false;
  private static AlarmManager mAlarmManager;
  private static Intent mDozeNetworkIntent;
  private static PendingIntent mDozenNetworkPendingIntent = null;
  static boolean mFirstDeviceMode = false;
  private static long mFirstDeviceModeTime = Long.MIN_VALUE;
  static long screenOffCheckDelayTime = 1800000L;
  @GuardedBy("mNetworkPoliciesSecondLock")
  private final ArraySet<String> mActiveNotifs = new ArraySet();
  private final IActivityManager mActivityManager;
  private final INetworkManagementEventObserver mAlertObserver = new BaseNetworkObserver()
  {
    public void limitReached(String paramAnonymousString1, String paramAnonymousString2)
    {
      NetworkPolicyManagerService.-get3(NetworkPolicyManagerService.this).enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkPolicy");
      if (!"globalAlert".equals(paramAnonymousString1)) {
        NetworkPolicyManagerService.this.mHandler.obtainMessage(5, paramAnonymousString2).sendToTarget();
      }
    }
  };
  private final BroadcastReceiver mAllowReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      NetworkPolicyManagerService.this.setRestrictBackground(false);
    }
  };
  private final AppOpsManager mAppOps;
  private IConnectivityManager mConnManager;
  private BroadcastReceiver mConnReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
      synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
      {
        NetworkPolicyManagerService.-wrap8(NetworkPolicyManagerService.this);
        NetworkPolicyManagerService.-wrap9(NetworkPolicyManagerService.this);
        NetworkPolicyManagerService.this.updateNetworkEnabledNL();
        NetworkPolicyManagerService.this.updateNetworkRulesNL();
        NetworkPolicyManagerService.this.updateNotificationsNL();
        return;
      }
    }
  };
  private INetworkPolicyListener mConnectivityListener;
  private final Context mContext;
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mDefaultRestrictBackgroundWhitelistUids = new SparseBooleanArray();
  private IDeviceIdleController mDeviceIdleController;
  @GuardedBy("mUidRulesFirstLock")
  volatile boolean mDeviceIdleMode;
  private final BroadcastReceiver mDozeChangeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      Log.e("NetworkPolicy", "setDeviceIdleMode PolicyHandler ");
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.-wrap17(NetworkPolicyManagerService.this);
        return;
      }
    }
  };
  @GuardedBy("mUidRulesFirstLock")
  final SparseBooleanArray mFirewallChainStates = new SparseBooleanArray();
  final Handler mHandler;
  private Handler.Callback mHandlerCallback = new Handler.Callback()
  {
    public boolean handleMessage(Message arg1)
    {
      int j;
      int k;
      int m;
      int i;
      Object localObject1;
      boolean bool;
      switch (???.what)
      {
      default: 
        return false;
      case 1: 
        j = ???.arg1;
        k = ???.arg2;
        NetworkPolicyManagerService.-wrap6(NetworkPolicyManagerService.this, NetworkPolicyManagerService.-get2(NetworkPolicyManagerService.this), j, k);
        m = NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).beginBroadcast();
        i = 0;
        while (i < m)
        {
          ??? = (INetworkPolicyListener)NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).getBroadcastItem(i);
          NetworkPolicyManagerService.-wrap6(NetworkPolicyManagerService.this, ???, j, k);
          i += 1;
        }
        NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).finishBroadcast();
        return true;
      case 2: 
        ??? = (String[])???.obj;
        NetworkPolicyManagerService.-wrap2(NetworkPolicyManagerService.this, NetworkPolicyManagerService.-get2(NetworkPolicyManagerService.this), ???);
        j = NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).beginBroadcast();
        i = 0;
        while (i < j)
        {
          localObject1 = (INetworkPolicyListener)NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).getBroadcastItem(i);
          NetworkPolicyManagerService.-wrap2(NetworkPolicyManagerService.this, (INetworkPolicyListener)localObject1, ???);
          i += 1;
        }
        NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).finishBroadcast();
        return true;
      case 5: 
        localObject1 = (String)???.obj;
        NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
        {
          bool = NetworkPolicyManagerService.-get5(NetworkPolicyManagerService.this).contains(localObject1);
          if (!bool) {
            break;
          }
        }
      }
      try
      {
        NetworkPolicyManagerService.-get6(NetworkPolicyManagerService.this).forceUpdate();
        NetworkPolicyManagerService.this.updateNetworkEnabledNL();
        NetworkPolicyManagerService.this.updateNotificationsNL();
        return true;
        localObject2 = finally;
        throw ((Throwable)localObject2);
        if (???.arg1 != 0) {}
        for (bool = true;; bool = false)
        {
          NetworkPolicyManagerService.-wrap4(NetworkPolicyManagerService.this, NetworkPolicyManagerService.-get2(NetworkPolicyManagerService.this), bool);
          j = NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).beginBroadcast();
          i = 0;
          while (i < j)
          {
            ??? = (INetworkPolicyListener)NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).getBroadcastItem(i);
            NetworkPolicyManagerService.-wrap4(NetworkPolicyManagerService.this, ???, bool);
            i += 1;
          }
        }
        NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).finishBroadcast();
        ??? = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
        ???.setFlags(1073741824);
        NetworkPolicyManagerService.-get3(NetworkPolicyManagerService.this).sendBroadcastAsUser(???, UserHandle.ALL);
        return true;
        k = ???.arg1;
        if (???.arg2 == 1) {}
        for (i = 1;; i = 0)
        {
          ??? = (Boolean)???.obj;
          if (??? == null) {
            break label629;
          }
          bool = ???.booleanValue();
          NetworkPolicyManagerService.-wrap5(NetworkPolicyManagerService.this, NetworkPolicyManagerService.-get2(NetworkPolicyManagerService.this), k, bool);
          m = NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).beginBroadcast();
          j = 0;
          while (j < m)
          {
            ??? = (INetworkPolicyListener)NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).getBroadcastItem(j);
            NetworkPolicyManagerService.-wrap5(NetworkPolicyManagerService.this, ???, k, bool);
            j += 1;
          }
        }
        NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).finishBroadcast();
        label629:
        ??? = NetworkPolicyManagerService.-get3(NetworkPolicyManagerService.this).getPackageManager().getPackagesForUid(k);
        if ((i != 0) && (??? != null))
        {
          j = UserHandle.getUserId(k);
          i = 0;
          k = ???.length;
          while (i < k)
          {
            ??? = ???[i];
            Intent localIntent = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
            localIntent.setPackage((String)???);
            localIntent.setFlags(1073741824);
            NetworkPolicyManagerService.-get3(NetworkPolicyManagerService.this).sendBroadcastAsUser(localIntent, UserHandle.of(j));
            i += 1;
          }
        }
        return true;
        j = ???.arg1;
        if (???.arg2 == 1) {}
        for (bool = true;; bool = false)
        {
          NetworkPolicyManagerService.-wrap3(NetworkPolicyManagerService.this, NetworkPolicyManagerService.-get2(NetworkPolicyManagerService.this), j, bool);
          k = NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).beginBroadcast();
          i = 0;
          while (i < k)
          {
            ??? = (INetworkPolicyListener)NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).getBroadcastItem(i);
            NetworkPolicyManagerService.-wrap3(NetworkPolicyManagerService.this, ???, j, bool);
            i += 1;
          }
        }
        NetworkPolicyManagerService.-get4(NetworkPolicyManagerService.this).finishBroadcast();
        return true;
        long l = ((Long)???.obj).longValue();
        try
        {
          l /= 1000L;
          NetworkPolicyManagerService.-get6(NetworkPolicyManagerService.this).advisePersistThreshold(l);
          return true;
          NetworkPolicyManagerService.-wrap19(NetworkPolicyManagerService.this);
          return true;
          NetworkPolicyManagerService.-wrap10(NetworkPolicyManagerService.this, (String)???.obj);
          NetworkPolicyManagerService.-wrap12(NetworkPolicyManagerService.this, (String)???.obj, ???.arg1 << 32 | ???.arg2 & 0xFFFFFFFF);
          return true;
          NetworkPolicyManagerService.-wrap10(NetworkPolicyManagerService.this, (String)???.obj);
          return true;
          i = ???.arg1;
          j = ???.arg2;
          ??? = (SparseIntArray)???.obj;
          if (??? != null) {
            NetworkPolicyManagerService.-wrap13(NetworkPolicyManagerService.this, i, ???);
          }
          if (j != 0)
          {
            ??? = NetworkPolicyManagerService.this;
            if (j != 1) {
              break label998;
            }
          }
          label998:
          for (bool = true;; bool = false)
          {
            NetworkPolicyManagerService.-wrap7(???, i, bool);
            return true;
          }
          Trace.traceBegin(2097152L, "onUidStateChanged");
          try
          {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
            {
              i = ???.arg1;
              j = ???.arg2;
              NetworkPolicyManagerService.-wrap20(NetworkPolicyManagerService.this, i, j);
              return true;
            }
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
            {
              i = ???.arg1;
              NetworkPolicyManagerService.-wrap11(NetworkPolicyManagerService.this, i);
              return true;
            }
          }
          finally
          {
            Trace.traceEnd(2097152L);
          }
        }
        catch (RemoteException ???)
        {
          for (;;) {}
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
    }
  };
  private final IPackageManager mIPm;
  private PowerManager.WakeLock mIdleWakeLock;
  private final RemoteCallbackList<INetworkPolicyListener> mListeners = new RemoteCallbackList();
  @GuardedBy("mNetworkPoliciesSecondLock")
  private ArraySet<String> mMeteredIfaces = new ArraySet();
  private final INetworkManagementService mNetworkManager;
  final Object mNetworkPoliciesSecondLock = new Object();
  final ArrayMap<NetworkTemplate, NetworkPolicy> mNetworkPolicy = new ArrayMap();
  final ArrayMap<NetworkPolicy, String[]> mNetworkRules = new ArrayMap();
  private final INetworkStatsService mNetworkStats;
  private INotificationManager mNotifManager;
  @GuardedBy("mNetworkPoliciesSecondLock")
  private final ArraySet<NetworkTemplate> mOverLimitNotified = new ArraySet();
  private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      ??? = paramAnonymousIntent.getAction();
      int i = paramAnonymousIntent.getIntExtra("android.intent.extra.UID", -1);
      if (i == -1) {
        return;
      }
      if ("android.intent.action.PACKAGE_ADDED".equals(???)) {
        if (NetworkPolicyManagerService.-get1()) {
          Slog.v("NetworkPolicy", "ACTION_PACKAGE_ADDED for uid=" + i);
        }
      }
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.-wrap14(NetworkPolicyManagerService.this, i);
        return;
      }
    }
  };
  @GuardedBy("allLocks")
  private final AtomicFile mPolicyFile;
  private PowerManagerInternal mPowerManagerInternal;
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mPowerSaveTempWhitelistAppIds = new SparseBooleanArray();
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mPowerSaveWhitelistAppIds = new SparseBooleanArray();
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mPowerSaveWhitelistExceptIdleAppIds = new SparseBooleanArray();
  private final BroadcastReceiver mPowerSaveWhitelistReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.this.updatePowerSaveWhitelistUL();
        NetworkPolicyManagerService.-wrap17(NetworkPolicyManagerService.this);
        NetworkPolicyManagerService.this.updateRulesForAppIdleUL();
        return;
      }
    }
  };
  @GuardedBy("mUidRulesFirstLock")
  volatile boolean mRestrictBackground;
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mRestrictBackgroundWhitelistRevokedUids = new SparseBooleanArray();
  @GuardedBy("mUidRulesFirstLock")
  private final SparseBooleanArray mRestrictBackgroundWhitelistUids = new SparseBooleanArray();
  @GuardedBy("mUidRulesFirstLock")
  volatile boolean mRestrictPower;
  private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      NetworkPolicyManagerService.this.mHandler.obtainMessage(8).sendToTarget();
    }
  };
  private final BroadcastReceiver mSnoozeWarningReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = (NetworkTemplate)paramAnonymousIntent.getParcelableExtra("android.net.NETWORK_TEMPLATE");
      NetworkPolicyManagerService.this.performSnooze(paramAnonymousContext, 1);
    }
  };
  private final BroadcastReceiver mStatsReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      NetworkPolicyManagerService.this.maybeRefreshTrustedTime();
      synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
      {
        NetworkPolicyManagerService.this.updateNetworkEnabledNL();
        NetworkPolicyManagerService.this.updateNotificationsNL();
        return;
      }
    }
  };
  private final boolean mSuppressDefaultPolicy;
  @GuardedBy("allLocks")
  volatile boolean mSystemReady;
  private final Runnable mTempPowerSaveChangedCallback = new Runnable()
  {
    public void run()
    {
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.this.updatePowerSaveTempWhitelistUL();
        NetworkPolicyManagerService.-wrap18(NetworkPolicyManagerService.this);
        NetworkPolicyManagerService.this.purgePowerSaveTempWhitelistUL();
        return;
      }
    }
  };
  private final TrustedTime mTime;
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidFirewallDozableRules = new SparseIntArray();
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidFirewallPowerSaveRules = new SparseIntArray();
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidFirewallStandbyRules = new SparseIntArray();
  private final IUidObserver mUidObserver = new IUidObserver.Stub()
  {
    public void onUidActive(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidGone(int paramAnonymousInt)
      throws RemoteException
    {
      Message localMessage = Message.obtain(NetworkPolicyManagerService.this.mHandler, 54088);
      localMessage.arg1 = paramAnonymousInt;
      NetworkPolicyManagerService.this.mHandler.sendMessage(localMessage);
    }
    
    public void onUidIdle(int paramAnonymousInt)
      throws RemoteException
    {}
    
    public void onUidStateChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {
      Message localMessage = Message.obtain(NetworkPolicyManagerService.this.mHandler, 7788);
      localMessage.arg1 = paramAnonymousInt1;
      localMessage.arg2 = paramAnonymousInt2;
      NetworkPolicyManagerService.this.mHandler.sendMessage(localMessage);
    }
  };
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidPolicy = new SparseIntArray();
  private final BroadcastReceiver mUidRemovedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent arg2)
    {
      int i = ???.getIntExtra("android.intent.extra.UID", -1);
      if (i == -1) {
        return;
      }
      if (NetworkPolicyManagerService.-get1()) {
        Slog.v("NetworkPolicy", "ACTION_UID_REMOVED for uid=" + i);
      }
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.this.mUidPolicy.delete(i);
        NetworkPolicyManagerService.-wrap1(NetworkPolicyManagerService.this, i, true, true);
        NetworkPolicyManagerService.-wrap14(NetworkPolicyManagerService.this, i);
        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
        {
          NetworkPolicyManagerService.this.writePolicyAL();
          return;
        }
      }
    }
  };
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidRules = new SparseIntArray();
  final Object mUidRulesFirstLock = new Object();
  @GuardedBy("mUidRulesFirstLock")
  final SparseIntArray mUidState = new SparseIntArray();
  private UsageStatsManagerInternal mUsageStats;
  private final UserManager mUserManager;
  private final BroadcastReceiver mUserReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent arg2)
    {
      String str = ???.getAction();
      int i = ???.getIntExtra("android.intent.extra.user_handle", -1);
      if (i == -1) {
        return;
      }
      if (str.equals("android.intent.action.USER_REMOVED")) {}
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.this.removeUserStateUL(i, true);
        if (str == "android.intent.action.USER_ADDED") {
          NetworkPolicyManagerService.-wrap0(NetworkPolicyManagerService.this, i);
        }
      }
    }
  };
  private final BroadcastReceiver mWifiConfigReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent arg2)
    {
      NetworkTemplate localNetworkTemplate;
      if (???.getIntExtra("changeReason", 0) == 1)
      {
        ??? = (WifiConfiguration)???.getParcelableExtra("wifiConfiguration");
        if (???.SSID != null) {
          localNetworkTemplate = NetworkTemplate.buildTemplateWifi(???.SSID);
        }
      }
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
        {
          if (NetworkPolicyManagerService.this.mNetworkPolicy.containsKey(localNetworkTemplate))
          {
            NetworkPolicyManagerService.this.mNetworkPolicy.remove(localNetworkTemplate);
            NetworkPolicyManagerService.this.writePolicyAL();
          }
          return;
        }
      }
    }
  };
  private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if (!((NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo")).isConnected()) {
        return;
      }
      ??? = (WifiInfo)paramAnonymousIntent.getParcelableExtra("wifiInfo");
      boolean bool = ???.getMeteredHint();
      paramAnonymousIntent = NetworkTemplate.buildTemplateWifi(???.getSSID());
      synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock)
      {
        NetworkPolicy localNetworkPolicy = (NetworkPolicy)NetworkPolicyManagerService.this.mNetworkPolicy.get(paramAnonymousIntent);
        if ((localNetworkPolicy == null) && (bool))
        {
          paramAnonymousIntent = NetworkPolicyManagerService.newWifiPolicy(paramAnonymousIntent, bool);
          NetworkPolicyManagerService.this.addNetworkPolicyNL(paramAnonymousIntent);
        }
        while ((localNetworkPolicy == null) || (!localNetworkPolicy.inferred)) {
          return;
        }
        localNetworkPolicy.metered = bool;
        NetworkPolicyManagerService.this.updateNetworkRulesNL();
      }
    }
  };
  
  public NetworkPolicyManagerService(Context paramContext, IActivityManager paramIActivityManager, INetworkStatsService paramINetworkStatsService, INetworkManagementService paramINetworkManagementService)
  {
    this(paramContext, paramIActivityManager, paramINetworkStatsService, paramINetworkManagementService, NtpTrustedTime.getInstance(paramContext), getSystemDir(), false);
  }
  
  public NetworkPolicyManagerService(Context paramContext, IActivityManager paramIActivityManager, INetworkStatsService paramINetworkStatsService, INetworkManagementService paramINetworkManagementService, TrustedTime paramTrustedTime, File paramFile, boolean paramBoolean)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext, "missing context"));
    this.mActivityManager = ((IActivityManager)Preconditions.checkNotNull(paramIActivityManager, "missing activityManager"));
    this.mNetworkStats = ((INetworkStatsService)Preconditions.checkNotNull(paramINetworkStatsService, "missing networkStats"));
    this.mNetworkManager = ((INetworkManagementService)Preconditions.checkNotNull(paramINetworkManagementService, "missing networkManagement"));
    this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    this.mTime = ((TrustedTime)Preconditions.checkNotNull(paramTrustedTime, "missing TrustedTime"));
    this.mUserManager = ((UserManager)this.mContext.getSystemService("user"));
    this.mIPm = AppGlobals.getPackageManager();
    paramIActivityManager = new HandlerThread("NetworkPolicy");
    paramIActivityManager.start();
    this.mHandler = new Handler(paramIActivityManager.getLooper(), this.mHandlerCallback);
    this.mSuppressDefaultPolicy = paramBoolean;
    this.mPolicyFile = new AtomicFile(new File(paramFile, "netpolicy.xml"));
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService(AppOpsManager.class));
    LocalServices.addService(NetworkPolicyManagerInternal.class, new NetworkPolicyManagerInternalImpl(null));
    this.mIdleWakeLock = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, "setDeviceIdleMode");
    this.mIdleWakeLock.setReferenceCounted(false);
  }
  
  private boolean addDefaultRestrictBackgroundWhitelistUidsUL(int paramInt)
  {
    Object localObject = SystemConfig.getInstance();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    localObject = ((SystemConfig)localObject).getAllowInDataUsageSave();
    boolean bool1 = false;
    int i = 0;
    String str;
    for (;;)
    {
      if (i >= ((ArraySet)localObject).size()) {
        break label371;
      }
      str = (String)((ArraySet)localObject).valueAt(i);
      if (LOGD) {
        Slog.d("NetworkPolicy", "checking restricted background whitelisting for package " + str + " and user " + paramInt);
      }
      try
      {
        ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfoAsUser(str, 1048576, paramInt);
        if (localApplicationInfo.isPrivilegedApp()) {
          break;
        }
        Slog.e("NetworkPolicy", "addDefaultRestrictBackgroundWhitelistUidsUL(): skipping non-privileged app  " + str);
        bool2 = bool1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          boolean bool2 = bool1;
          if (LOGD)
          {
            Slog.d("NetworkPolicy", "No ApplicationInfo for package " + str);
            bool2 = bool1;
            continue;
            int j = UserHandle.getUid(paramInt, localNameNotFoundException.uid);
            this.mDefaultRestrictBackgroundWhitelistUids.append(j, true);
            if (LOGD) {
              Slog.d("NetworkPolicy", "Adding uid " + j + " (user " + paramInt + ") to default restricted " + "background whitelist. Revoked status: " + this.mRestrictBackgroundWhitelistRevokedUids.get(j));
            }
            bool2 = bool1;
            if (!this.mRestrictBackgroundWhitelistRevokedUids.get(j))
            {
              if (LOGD) {
                Slog.d("NetworkPolicy", "adding default package " + str + " (uid " + j + " for user " + paramInt + ") to restrict background whitelist");
              }
              this.mRestrictBackgroundWhitelistUids.append(j, true);
              bool2 = true;
            }
          }
        }
      }
      i += 1;
      bool1 = bool2;
    }
    label371:
    return bool1;
  }
  
  private SparseIntArray adjustUidRulesForStandby(int paramInt, SparseIntArray paramSparseIntArray)
  {
    if (paramInt == 2)
    {
      paramInt = paramSparseIntArray.size() - 1;
      while (paramInt >= 0)
      {
        if (paramSparseIntArray.valueAt(paramInt) != 2) {
          paramSparseIntArray.removeAt(paramInt);
        }
        paramInt -= 1;
      }
      Log.e("NetworkPolicy", "setUidFirewallRules uidRulesTemp " + paramSparseIntArray);
    }
    return paramSparseIntArray;
  }
  
  private void broadcastDataUsageAlarm()
  {
    Intent localIntent = new Intent("com.oneplus.security.action.DATAUSAGE_ALARM");
    this.mContext.sendBroadcast(localIntent);
  }
  
  private static Intent buildAllowBackgroundDataIntent()
  {
    return new Intent("com.android.server.net.action.ALLOW_BACKGROUND");
  }
  
  private static Intent buildNetworkOverLimitIntent(NetworkTemplate paramNetworkTemplate)
  {
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.net.NetworkOverLimitActivity"));
    localIntent.addFlags(268435456);
    localIntent.putExtra("android.net.NETWORK_TEMPLATE", paramNetworkTemplate);
    return localIntent;
  }
  
  private String buildNotificationTag(NetworkPolicy paramNetworkPolicy, int paramInt)
  {
    return "NetworkPolicy:" + paramNetworkPolicy.template.hashCode() + ":" + paramInt;
  }
  
  private static Intent buildSnoozeWarningIntent(NetworkTemplate paramNetworkTemplate)
  {
    Intent localIntent = new Intent("com.android.server.net.action.SNOOZE_WARNING");
    localIntent.putExtra("android.net.NETWORK_TEMPLATE", paramNetworkTemplate);
    return localIntent;
  }
  
  private static Intent buildViewDataUsageIntent(NetworkTemplate paramNetworkTemplate)
  {
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
    localIntent.addFlags(268435456);
    localIntent.putExtra("android.net.NETWORK_TEMPLATE", paramNetworkTemplate);
    return localIntent;
  }
  
  private void cancelNotification(String paramString)
  {
    try
    {
      String str = this.mContext.getPackageName();
      this.mNotifManager.cancelNotificationWithTag(str, paramString, 0, -1);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  private static void collectKeys(SparseIntArray paramSparseIntArray, SparseBooleanArray paramSparseBooleanArray)
  {
    int j = paramSparseIntArray.size();
    int i = 0;
    while (i < j)
    {
      paramSparseBooleanArray.put(paramSparseIntArray.keyAt(i), true);
      i += 1;
    }
  }
  
  private long currentTimeMillis()
  {
    if (this.mTime.hasCache()) {
      return this.mTime.currentTimeMillis();
    }
    return System.currentTimeMillis();
  }
  
  private void dispatchMeteredIfacesChanged(INetworkPolicyListener paramINetworkPolicyListener, String[] paramArrayOfString)
  {
    if (paramINetworkPolicyListener != null) {}
    try
    {
      paramINetworkPolicyListener.onMeteredIfacesChanged(paramArrayOfString);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener) {}
  }
  
  private void dispatchRestrictBackgroundBlacklistChanged(INetworkPolicyListener paramINetworkPolicyListener, int paramInt, boolean paramBoolean)
  {
    if (paramINetworkPolicyListener != null) {}
    try
    {
      paramINetworkPolicyListener.onRestrictBackgroundBlacklistChanged(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener) {}
  }
  
  private void dispatchRestrictBackgroundChanged(INetworkPolicyListener paramINetworkPolicyListener, boolean paramBoolean)
  {
    if (paramINetworkPolicyListener != null) {}
    try
    {
      paramINetworkPolicyListener.onRestrictBackgroundChanged(paramBoolean);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener) {}
  }
  
  private void dispatchRestrictBackgroundWhitelistChanged(INetworkPolicyListener paramINetworkPolicyListener, int paramInt, boolean paramBoolean)
  {
    if (paramINetworkPolicyListener != null) {}
    try
    {
      paramINetworkPolicyListener.onRestrictBackgroundWhitelistChanged(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener) {}
  }
  
  private void dispatchUidRulesChanged(INetworkPolicyListener paramINetworkPolicyListener, int paramInt1, int paramInt2)
  {
    if (paramINetworkPolicyListener != null) {}
    try
    {
      paramINetworkPolicyListener.onUidRulesChanged(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException paramINetworkPolicyListener) {}
  }
  
  private void enableFirewallChainUL(int paramInt, boolean paramBoolean)
  {
    if ((this.mFirewallChainStates.indexOfKey(paramInt) >= 0) && (this.mFirewallChainStates.get(paramInt) == paramBoolean)) {
      return;
    }
    this.mFirewallChainStates.put(paramInt, paramBoolean);
    try
    {
      this.mNetworkManager.setFirewallChainEnabled(paramInt, paramBoolean);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.wtf("NetworkPolicy", "problem enable firewall chain", localIllegalStateException);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void enqueueNotification(NetworkPolicy paramNetworkPolicy, int paramInt, long paramLong)
  {
    String str = buildNotificationTag(paramNetworkPolicy, paramInt);
    Notification.Builder localBuilder = new Notification.Builder(this.mContext);
    localBuilder.setOnlyAlertOnce(true);
    localBuilder.setWhen(0L);
    localBuilder.setColor(this.mContext.getColor(17170523));
    Object localObject1 = this.mContext.getResources();
    switch (paramInt)
    {
    }
    for (;;)
    {
      try
      {
        paramNetworkPolicy = this.mContext.getPackageName();
        localObject1 = new int[1];
        this.mNotifManager.enqueueNotificationWithTag(paramNetworkPolicy, paramNetworkPolicy, str, 0, localBuilder.getNotification(), (int[])localObject1, -1);
        this.mActiveNotifs.add(str);
        return;
      }
      catch (RemoteException paramNetworkPolicy) {}
      Object localObject2 = ((Resources)localObject1).getText(17040612);
      localObject1 = ((Resources)localObject1).getString(17040613);
      localBuilder.setSmallIcon(17301624);
      localBuilder.setTicker((CharSequence)localObject2);
      localBuilder.setContentTitle((CharSequence)localObject2);
      localBuilder.setContentText((CharSequence)localObject1);
      localBuilder.setDefaults(-1);
      localBuilder.setPriority(1);
      localObject1 = buildSnoozeWarningIntent(paramNetworkPolicy.template);
      localBuilder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, (Intent)localObject1, 134217728));
      paramNetworkPolicy = buildViewDataUsageIntent(paramNetworkPolicy.template);
      localBuilder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, paramNetworkPolicy, 134217728));
      continue;
      localObject2 = ((Resources)localObject1).getText(17040618);
      paramInt = 17303254;
      switch (paramNetworkPolicy.template.getMatchRule())
      {
      default: 
        localObject1 = null;
      }
      for (;;)
      {
        localBuilder.setOngoing(true);
        localBuilder.setSmallIcon(paramInt);
        localBuilder.setTicker((CharSequence)localObject1);
        localBuilder.setContentTitle((CharSequence)localObject1);
        localBuilder.setContentText((CharSequence)localObject2);
        paramNetworkPolicy = buildNetworkOverLimitIntent(paramNetworkPolicy.template);
        localBuilder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, paramNetworkPolicy, 134217728));
        break;
        localObject1 = ((Resources)localObject1).getText(17040614);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040615);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040616);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040617);
        paramInt = 17301624;
      }
      long l = paramNetworkPolicy.limitBytes;
      localObject2 = ((Resources)localObject1).getString(17040623, new Object[] { Formatter.formatFileSize(this.mContext, paramLong - l) });
      switch (paramNetworkPolicy.template.getMatchRule())
      {
      default: 
        localObject1 = null;
      }
      for (;;)
      {
        localBuilder.setOngoing(true);
        localBuilder.setSmallIcon(17301624);
        localBuilder.setTicker((CharSequence)localObject1);
        localBuilder.setContentTitle((CharSequence)localObject1);
        localBuilder.setContentText((CharSequence)localObject2);
        paramNetworkPolicy = buildViewDataUsageIntent(paramNetworkPolicy.template);
        localBuilder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, paramNetworkPolicy, 134217728));
        break;
        localObject1 = ((Resources)localObject1).getText(17040619);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040620);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040621);
        continue;
        localObject1 = ((Resources)localObject1).getText(17040622);
      }
    }
  }
  
  private void ensureActiveMobilePolicyNL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "ensureActiveMobilePolicyNL()");
    }
    if (this.mSuppressDefaultPolicy) {
      return;
    }
    TelephonyManager localTelephonyManager = TelephonyManager.from(this.mContext);
    int[] arrayOfInt = SubscriptionManager.from(this.mContext).getActiveSubscriptionIdList();
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      ensureActiveMobilePolicyNL(localTelephonyManager.getSubscriberId(arrayOfInt[i]));
      i += 1;
    }
  }
  
  private void ensureActiveMobilePolicyNL(String paramString)
  {
    Object localObject = new NetworkIdentity(0, 0, paramString, null, false, true);
    int i = this.mNetworkPolicy.size() - 1;
    while (i >= 0)
    {
      NetworkTemplate localNetworkTemplate = (NetworkTemplate)this.mNetworkPolicy.keyAt(i);
      if (localNetworkTemplate.matches((NetworkIdentity)localObject))
      {
        if (LOGD) {
          Slog.d("NetworkPolicy", "Found template " + localNetworkTemplate + " which matches subscriber " + NetworkIdentity.scrubSubscriberId(paramString));
        }
        return;
      }
      i -= 1;
    }
    Slog.i("NetworkPolicy", "No policy for subscriber " + NetworkIdentity.scrubSubscriberId(paramString) + "; generating default policy");
    i = this.mContext.getResources().getInteger(17694855);
    if (i == -1L) {}
    for (long l = -1L;; l = i * 1048576L)
    {
      localObject = new Time();
      ((Time)localObject).setToNow();
      i = ((Time)localObject).monthDay;
      localObject = ((Time)localObject).timezone;
      addNetworkPolicyNL(new NetworkPolicy(NetworkTemplate.buildTemplateMobileAll(paramString), i, (String)localObject, l, -1L, -1L, -1L, true, true));
      return;
    }
  }
  
  private NetworkPolicy findPolicyForNetworkNL(NetworkIdentity paramNetworkIdentity)
  {
    int i = this.mNetworkPolicy.size() - 1;
    while (i >= 0)
    {
      NetworkPolicy localNetworkPolicy = (NetworkPolicy)this.mNetworkPolicy.valueAt(i);
      if (localNetworkPolicy.template.matches(paramNetworkIdentity)) {
        return localNetworkPolicy;
      }
      i -= 1;
    }
    return null;
  }
  
  private NetworkQuotaInfo getNetworkQuotaInfoUnchecked(NetworkState arg1)
  {
    Object localObject1 = NetworkIdentity.buildNetworkIdentity(this.mContext, ???);
    for (;;)
    {
      synchronized (this.mNetworkPoliciesSecondLock)
      {
        localObject1 = findPolicyForNetworkNL((NetworkIdentity)localObject1);
        if ((localObject1 != null) && (((NetworkPolicy)localObject1).hasCycle()))
        {
          l1 = currentTimeMillis();
          l2 = NetworkPolicyManager.computeLastCycleBoundary(l1, (NetworkPolicy)localObject1);
          long l3 = getTotalBytes(((NetworkPolicy)localObject1).template, l2, l1);
          if (((NetworkPolicy)localObject1).warningBytes == -1L) {
            break label126;
          }
          l1 = ((NetworkPolicy)localObject1).warningBytes;
          if (((NetworkPolicy)localObject1).limitBytes == -1L) {
            break label133;
          }
          l2 = ((NetworkPolicy)localObject1).limitBytes;
          return new NetworkQuotaInfo(l3, l1, l2);
        }
      }
      return null;
      label126:
      long l1 = -1L;
      continue;
      label133:
      long l2 = -1L;
    }
  }
  
  private static File getSystemDir()
  {
    return new File(Environment.getDataDirectory(), "system");
  }
  
  private long getTotalBytes(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
  {
    try
    {
      paramLong1 = this.mNetworkStats.getNetworkTotalBytes(paramNetworkTemplate, paramLong1, paramLong2);
      return paramLong1;
    }
    catch (RemoteException paramNetworkTemplate)
    {
      return 0L;
    }
    catch (RuntimeException paramNetworkTemplate)
    {
      Slog.w("NetworkPolicy", "problem reading network stats: " + paramNetworkTemplate);
    }
    return 0L;
  }
  
  private boolean hasInternetPermissions(int paramInt)
  {
    try
    {
      paramInt = this.mIPm.checkUidPermission("android.permission.INTERNET", paramInt);
      if (paramInt != 0) {
        return false;
      }
    }
    catch (RemoteException localRemoteException) {}
    return true;
  }
  
  private boolean isBandwidthControlEnabled()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      boolean bool = this.mNetworkManager.isBandwidthControlEnabled();
      Binder.restoreCallingIdentity(l);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      Binder.restoreCallingIdentity(l);
      return false;
    }
    finally
    {
      localObject = finally;
      Binder.restoreCallingIdentity(l);
      throw ((Throwable)localObject);
    }
  }
  
  static boolean isProcStateAllowedWhileIdleOrPowerSaveMode(int paramInt)
  {
    if ((isDozeChangeSupport) && (mFirstDeviceMode))
    {
      long l = SystemClock.elapsedRealtime();
      if (paramInt <= 4) {
        return l - mFirstDeviceModeTime <= screenOffCheckDelayTime - 300000L;
      }
      return false;
    }
    return paramInt <= 4;
  }
  
  static boolean isProcStateAllowedWhileOnRestrictBackground(int paramInt)
  {
    return paramInt <= 4;
  }
  
  private boolean isTemplateRelevant(NetworkTemplate paramNetworkTemplate)
  {
    if (paramNetworkTemplate.isMatchRuleMobile())
    {
      TelephonyManager localTelephonyManager = TelephonyManager.from(this.mContext);
      int[] arrayOfInt = SubscriptionManager.from(this.mContext).getActiveSubscriptionIdList();
      int j = arrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        if (paramNetworkTemplate.matches(new NetworkIdentity(0, 0, localTelephonyManager.getSubscriberId(arrayOfInt[i]), null, false, true))) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    return true;
  }
  
  private boolean isUidForegroundOnRestrictBackgroundUL(int paramInt)
  {
    return isProcStateAllowedWhileOnRestrictBackground(this.mUidState.get(paramInt, 16));
  }
  
  private boolean isUidForegroundOnRestrictPowerUL(int paramInt)
  {
    return isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.get(paramInt, 16));
  }
  
  private boolean isUidForegroundUL(int paramInt)
  {
    return isUidStateForegroundUL(this.mUidState.get(paramInt, 16));
  }
  
  private boolean isUidIdle(int paramInt)
  {
    String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(paramInt);
    int j = UserHandle.getUserId(paramInt);
    if (!ArrayUtils.isEmpty(arrayOfString))
    {
      int k = arrayOfString.length;
      int i = 0;
      while (i < k)
      {
        String str = arrayOfString[i];
        if (!this.mUsageStats.isAppIdle(str, paramInt, j)) {
          return false;
        }
        i += 1;
      }
    }
    return true;
  }
  
  private boolean isUidStateForegroundUL(int paramInt)
  {
    return paramInt <= 2;
  }
  
  private boolean isUidValidForBlacklistRules(int paramInt)
  {
    if ((paramInt == 1013) || (paramInt == 1019)) {}
    while ((UserHandle.isApp(paramInt)) && (hasInternetPermissions(paramInt))) {
      return true;
    }
    return false;
  }
  
  private boolean isUidValidForWhitelistRules(int paramInt)
  {
    if (UserHandle.isApp(paramInt)) {
      return hasInternetPermissions(paramInt);
    }
    return false;
  }
  
  private boolean isWhitelistedBatterySaverUL(int paramInt)
  {
    paramInt = UserHandle.getAppId(paramInt);
    if (!this.mPowerSaveTempWhitelistAppIds.get(paramInt)) {
      return this.mPowerSaveWhitelistAppIds.get(paramInt);
    }
    return true;
  }
  
  static NetworkPolicy newWifiPolicy(NetworkTemplate paramNetworkTemplate, boolean paramBoolean)
  {
    return new NetworkPolicy(paramNetworkTemplate, -1, "UTC", -1L, -1L, -1L, -1L, paramBoolean, true);
  }
  
  private void normalizePoliciesNL()
  {
    normalizePoliciesNL(getNetworkPolicies(this.mContext.getOpPackageName()));
  }
  
  private void normalizePoliciesNL(NetworkPolicy[] paramArrayOfNetworkPolicy)
  {
    int i = 0;
    String[] arrayOfString = TelephonyManager.from(this.mContext).getMergedSubscriberIds();
    this.mNetworkPolicy.clear();
    int j = paramArrayOfNetworkPolicy.length;
    while (i < j)
    {
      NetworkPolicy localNetworkPolicy1 = paramArrayOfNetworkPolicy[i];
      localNetworkPolicy1.template = NetworkTemplate.normalize(localNetworkPolicy1.template, arrayOfString);
      NetworkPolicy localNetworkPolicy2 = (NetworkPolicy)this.mNetworkPolicy.get(localNetworkPolicy1.template);
      if ((localNetworkPolicy2 == null) || (localNetworkPolicy2.compareTo(localNetworkPolicy1) > 0))
      {
        if (localNetworkPolicy2 != null) {
          Slog.d("NetworkPolicy", "Normalization replaced " + localNetworkPolicy2 + " with " + localNetworkPolicy1);
        }
        this.mNetworkPolicy.put(localNetworkPolicy1.template, localNetworkPolicy1);
      }
      i += 1;
    }
  }
  
  private void notifyOverLimitNL(NetworkTemplate paramNetworkTemplate)
  {
    if (!this.mOverLimitNotified.contains(paramNetworkTemplate))
    {
      this.mContext.startActivity(buildNetworkOverLimitIntent(paramNetworkTemplate));
      this.mOverLimitNotified.add(paramNetworkTemplate);
    }
  }
  
  private void notifyUnderLimitNL(NetworkTemplate paramNetworkTemplate)
  {
    this.mOverLimitNotified.remove(paramNetworkTemplate);
  }
  
  private void readPolicyAL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "readPolicyAL()");
    }
    this.mNetworkPolicy.clear();
    this.mUidPolicy.clear();
    Object localObject4 = null;
    Object localObject5 = null;
    Object localObject1 = null;
    Object localObject3 = null;
    XmlPullParser localXmlPullParser;
    int i;
    int k;
    Object localObject6;
    boolean bool1;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mPolicyFile.openRead();
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localXmlPullParser = Xml.newPullParser();
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
        j = 1;
        i = 0;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        FileInputStream localFileInputStream;
        localObject2 = localObject3;
        upgradeLegacyBackgroundDataUL();
        return;
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        this.mRestrictBackground = false;
        continue;
      }
      catch (IOException localIOException)
      {
        localObject2 = localObject4;
        Log.wtf("NetworkPolicy", "problem reading network policy", localIOException);
        return;
        j = 0;
        continue;
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        if (!"network-policy".equals(localObject6)) {
          continue;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        k = XmlUtils.readIntAttribute(localXmlPullParser, "networkTemplate");
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        str2 = localXmlPullParser.getAttributeValue(null, "subscriberId");
        if (j < 9) {
          continue;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        localObject6 = localXmlPullParser.getAttributeValue(null, "networkId");
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        m = XmlUtils.readIntAttribute(localXmlPullParser, "cycleDay");
        if (j < 6) {
          continue;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        str1 = localXmlPullParser.getAttributeValue(null, "cycleTimezone");
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        l3 = XmlUtils.readLongAttribute(localXmlPullParser, "warningBytes");
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        l4 = XmlUtils.readLongAttribute(localXmlPullParser, "limitBytes");
        if (j < 5) {
          continue;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        l1 = XmlUtils.readLongAttribute(localXmlPullParser, "lastLimitSnooze");
        if (j < 4) {
          break label1601;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        bool1 = XmlUtils.readBooleanAttribute(localXmlPullParser, "metered");
        if (j < 5) {
          break label1640;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        l2 = XmlUtils.readLongAttribute(localXmlPullParser, "lastWarningSnooze");
        if (j < 7) {
          break label1648;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        bool2 = XmlUtils.readBooleanAttribute(localXmlPullParser, "inferred");
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        localObject6 = new NetworkTemplate(k, str2, (String)localObject6);
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        if (!((NetworkTemplate)localObject6).isPersistable()) {
          continue;
        }
        localObject3 = localIOException;
        localObject4 = localIOException;
        localObject5 = localIOException;
        localObject2 = localIOException;
        this.mNetworkPolicy.put(localObject6, new NetworkPolicy((NetworkTemplate)localObject6, m, str1, l3, l4, l2, l1, bool1, bool2));
        continue;
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        int j;
        localObject2 = localObject5;
        Log.wtf("NetworkPolicy", "problem reading network policy", localXmlPullParserException);
        return;
        localObject6 = null;
        continue;
        str1 = "UTC";
        continue;
        if (j < 2) {
          break label1593;
        }
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        l1 = XmlUtils.readLongAttribute(localXmlPullParser, "lastSnooze");
        continue;
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        if (!"uid-policy".equals(localObject6)) {
          break label1150;
        }
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        k = XmlUtils.readIntAttribute(localXmlPullParser, "uid");
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        m = XmlUtils.readIntAttribute(localXmlPullParser, "policy");
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        if (!UserHandle.isApp(k)) {
          break label1099;
        }
        localObject3 = localXmlPullParserException;
        localObject4 = localXmlPullParserException;
        localObject5 = localXmlPullParserException;
        localObject2 = localXmlPullParserException;
        setUidPolicyUncheckedUL(k, m, false);
        continue;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject2);
      }
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      k = localXmlPullParser.next();
      if (k == 1) {
        break label1587;
      }
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject6 = localXmlPullParser.getName();
      if (k != 2) {
        break label1547;
      }
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      if (!"policy-list".equals(localObject6)) {
        continue;
      }
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      bool1 = this.mRestrictBackground;
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      k = XmlUtils.readIntAttribute(localXmlPullParser, "version");
      if (k < 3) {
        continue;
      }
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      this.mRestrictBackground = XmlUtils.readBooleanAttribute(localXmlPullParser, "restrictBackground");
      j = k;
      localObject3 = localFileInputStream;
      localObject4 = localFileInputStream;
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      if (this.mRestrictBackground != bool1)
      {
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject6 = this.mHandler;
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        if (!this.mRestrictBackground) {
          continue;
        }
        j = 1;
        localObject3 = localFileInputStream;
        localObject4 = localFileInputStream;
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        ((Handler)localObject6).obtainMessage(6, j, 0).sendToTarget();
        j = k;
      }
    }
    for (;;)
    {
      String str2;
      int m;
      String str1;
      long l3;
      long l4;
      long l2;
      boolean bool2;
      label1099:
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      Object localObject2 = localAutoCloseable;
      Slog.w("NetworkPolicy", "unable to apply policy to UID " + k + "; ignoring");
      break;
      label1150:
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      if ("app-policy".equals(localObject6))
      {
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        m = XmlUtils.readIntAttribute(localXmlPullParser, "appId");
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        k = XmlUtils.readIntAttribute(localXmlPullParser, "policy");
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        m = UserHandle.getUid(0, m);
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        if (UserHandle.isApp(m))
        {
          localObject3 = localAutoCloseable;
          localObject4 = localAutoCloseable;
          localObject5 = localAutoCloseable;
          localObject2 = localAutoCloseable;
          setUidPolicyUncheckedUL(m, k, false);
          break;
        }
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        Slog.w("NetworkPolicy", "unable to apply policy to UID " + m + "; ignoring");
        break;
      }
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      if ("whitelist".equals(localObject6))
      {
        i = 1;
        break;
      }
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      if (("restrict-background".equals(localObject6)) && (i != 0))
      {
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        k = XmlUtils.readIntAttribute(localXmlPullParser, "uid");
        localObject3 = localAutoCloseable;
        localObject4 = localAutoCloseable;
        localObject5 = localAutoCloseable;
        localObject2 = localAutoCloseable;
        this.mRestrictBackgroundWhitelistUids.put(k, true);
        break;
      }
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      if ((!"revoked-restrict-background".equals(localObject6)) || (i == 0)) {
        break;
      }
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      k = XmlUtils.readIntAttribute(localXmlPullParser, "uid");
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      this.mRestrictBackgroundWhitelistRevokedUids.put(k, true);
      break;
      label1547:
      if (k != 3) {
        break;
      }
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      localObject5 = localAutoCloseable;
      localObject2 = localAutoCloseable;
      bool1 = "whitelist".equals(localObject6);
      if (!bool1) {
        break;
      }
      i = 0;
      break;
      label1587:
      IoUtils.closeQuietly(localAutoCloseable);
      return;
      label1593:
      long l1 = -1L;
      continue;
      switch (k)
      {
      default: 
        bool1 = false;
        break;
      case 1: 
      case 2: 
      case 3: 
        label1601:
        bool1 = true;
        continue;
        label1640:
        l2 = -1L;
        continue;
        label1648:
        bool2 = false;
      }
    }
  }
  
  private void removeInterfaceQuota(String paramString)
  {
    try
    {
      this.mNetworkManager.removeInterfaceQuota(paramString);
      return;
    }
    catch (IllegalStateException paramString)
    {
      Log.wtf("NetworkPolicy", "problem removing interface quota", paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  private boolean removeRestrictBackgroundWhitelistedUidUL(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool2 = this.mRestrictBackgroundWhitelistUids.get(paramInt);
    boolean bool1;
    if ((bool2) || (paramBoolean1))
    {
      if (paramBoolean1) {
        break label178;
      }
      bool1 = isUidValidForWhitelistRules(paramInt);
    }
    for (;;)
    {
      if (bool2)
      {
        Slog.i("NetworkPolicy", "removing uid " + paramInt + " from restrict background whitelist");
        this.mRestrictBackgroundWhitelistUids.delete(paramInt);
      }
      if ((!this.mDefaultRestrictBackgroundWhitelistUids.get(paramInt)) || (this.mRestrictBackgroundWhitelistRevokedUids.get(paramInt)))
      {
        if (bool1) {
          updateRulesForDataUsageRestrictionsUL(paramInt, paramBoolean1);
        }
        if (!paramBoolean2) {}
      }
      synchronized (this.mNetworkPoliciesSecondLock)
      {
        writePolicyAL();
        if (this.mRestrictBackground)
        {
          return bool1;
          if (LOGD) {
            Slog.d("NetworkPolicy", "uid " + paramInt + " was not whitelisted before");
          }
          return false;
          label178:
          bool1 = true;
          continue;
          if (LOGD) {
            Slog.d("NetworkPolicy", "Adding uid " + paramInt + " to revoked restrict background whitelist");
          }
          this.mRestrictBackgroundWhitelistRevokedUids.append(paramInt, true);
        }
      }
    }
    return false;
  }
  
  private void removeUidStateUL(int paramInt)
  {
    int i = this.mUidState.indexOfKey(paramInt);
    if (i >= 0)
    {
      int j = this.mUidState.valueAt(i);
      this.mUidState.removeAt(i);
      if (j != 16)
      {
        updateRestrictBackgroundRulesOnUidStatusChangedUL(paramInt, j, 16);
        if (this.mDeviceIdleMode) {
          updateRuleForDeviceIdleUL(paramInt);
        }
        if (this.mRestrictPower) {
          updateRuleForRestrictPowerUL(paramInt);
        }
        updateRulesForPowerRestrictionsUL(paramInt);
        updateNetworkStats(paramInt, false);
      }
    }
  }
  
  private void setInterfaceQuota(String paramString, long paramLong)
  {
    try
    {
      this.mNetworkManager.setInterfaceQuota(paramString, paramLong);
      NetPluginDelegate.setQuota(paramString, paramLong);
      return;
    }
    catch (IllegalStateException paramString)
    {
      Log.wtf("NetworkPolicy", "problem setting interface quota", paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  private void setMeteredNetworkBlacklist(int paramInt, boolean paramBoolean)
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "setMeteredNetworkBlacklist " + paramInt + ": " + paramBoolean);
    }
    try
    {
      this.mNetworkManager.setUidMeteredNetworkBlacklist(paramInt, paramBoolean);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.wtf("NetworkPolicy", "problem setting blacklist (" + paramBoolean + ") rules for " + paramInt, localIllegalStateException);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setMeteredNetworkWhitelist(int paramInt, boolean paramBoolean)
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "setMeteredNetworkWhitelist " + paramInt + ": " + paramBoolean);
    }
    try
    {
      this.mNetworkManager.setUidMeteredNetworkWhitelist(paramInt, paramBoolean);
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.wtf("NetworkPolicy", "problem setting whitelist (" + paramBoolean + ") rules for " + paramInt, localIllegalStateException);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setNetworkTemplateEnabled(NetworkTemplate paramNetworkTemplate, boolean paramBoolean)
  {
    if (paramNetworkTemplate.getMatchRule() == 1)
    {
      Object localObject = SubscriptionManager.from(this.mContext);
      TelephonyManager localTelephonyManager = TelephonyManager.from(this.mContext);
      localObject = ((SubscriptionManager)localObject).getActiveSubscriptionIdList();
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        int k = localObject[i];
        if (paramNetworkTemplate.matches(new NetworkIdentity(0, 0, localTelephonyManager.getSubscriberId(k), null, false, true))) {
          localTelephonyManager.setPolicyDataEnabled(paramBoolean, k);
        }
        i += 1;
      }
    }
  }
  
  private void setRestrictBackgroundUL(boolean paramBoolean)
  {
    Slog.d("NetworkPolicy", "setRestrictBackgroundUL(): " + paramBoolean);
    boolean bool = this.mRestrictBackground;
    this.mRestrictBackground = paramBoolean;
    updateRulesForRestrictBackgroundUL();
    try
    {
      if (!this.mNetworkManager.setDataSaverModeEnabled(this.mRestrictBackground))
      {
        Slog.e("NetworkPolicy", "Could not change Data Saver Mode on NMS to " + this.mRestrictBackground);
        this.mRestrictBackground = bool;
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      synchronized (this.mNetworkPoliciesSecondLock)
      {
        updateNotificationsNL();
        writePolicyAL();
        return;
      }
    }
  }
  
  private void setUidFirewallRule(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 1) {
      this.mUidFirewallDozableRules.put(paramInt2, paramInt3);
    }
    for (;;)
    {
      if (LOGV) {
        Log.e("NetworkPolicy", "setUidFirewallRule chain = " + paramInt1 + " uid = " + paramInt2 + " rule " + paramInt3);
      }
      try
      {
        this.mNetworkManager.setFirewallUidRule(paramInt1, paramInt2, paramInt3);
        return;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        Log.w("NetworkPolicy", "problem setting firewall uid rules ", localArrayIndexOutOfBoundsException);
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Log.wtf("NetworkPolicy", "problem setting firewall uid rules", localIllegalStateException);
        return;
      }
      catch (RemoteException localRemoteException) {}
      if (paramInt1 == 2) {
        this.mUidFirewallStandbyRules.put(paramInt2, paramInt3);
      } else if (paramInt1 == 3) {
        this.mUidFirewallPowerSaveRules.put(paramInt2, paramInt3);
      }
    }
  }
  
  private void setUidFirewallRules(int paramInt, SparseIntArray paramSparseIntArray)
  {
    try
    {
      SparseIntArray localSparseIntArray = adjustUidRulesForStandby(paramInt, paramSparseIntArray);
      int i = localSparseIntArray.size();
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      i -= 1;
      while (i >= 0)
      {
        arrayOfInt1[i] = localSparseIntArray.keyAt(i);
        arrayOfInt2[i] = localSparseIntArray.valueAt(i);
        i -= 1;
      }
      Log.e("NetworkPolicy", "setUidFirewallRules uidRules " + paramSparseIntArray + " chain =" + paramInt);
      this.mNetworkManager.setFirewallUidRules(paramInt, arrayOfInt1, arrayOfInt2);
      return;
    }
    catch (IllegalStateException paramSparseIntArray)
    {
      Log.wtf("NetworkPolicy", "problem setting firewall uid rules", paramSparseIntArray);
      return;
    }
    catch (RemoteException paramSparseIntArray) {}
  }
  
  private void setUidFirewallRulesAsync(int paramInt1, SparseIntArray paramSparseIntArray, int paramInt2)
  {
    this.mHandler.obtainMessage(13, paramInt1, paramInt2, paramSparseIntArray).sendToTarget();
  }
  
  private void setUidPolicyUncheckedUL(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int j = 0;
    setUidPolicyUncheckedUL(paramInt1, paramInt3, paramBoolean);
    int i;
    if (paramInt3 == 1)
    {
      i = 1;
      Handler localHandler = this.mHandler;
      if (i != 0) {
        j = 1;
      }
      localHandler.obtainMessage(12, paramInt1, j).sendToTarget();
      if (paramInt2 != 1) {
        break label94;
      }
    }
    label94:
    for (j = 1;; j = 0)
    {
      if (((paramInt2 == 0) && (i != 0)) || ((j != 0) && (paramInt3 == 0))) {
        this.mHandler.obtainMessage(9, paramInt1, 1, null).sendToTarget();
      }
      return;
      i = 0;
      break;
    }
  }
  
  private void setUidPolicyUncheckedUL(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mUidPolicy.put(paramInt1, paramInt2);
    updateRulesForDataUsageRestrictionsUL(paramInt1);
    if (paramBoolean) {}
    synchronized (this.mNetworkPoliciesSecondLock)
    {
      writePolicyAL();
      return;
    }
  }
  
  private void updateNetworkStats(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mNetworkStats.setUidForeground(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void updateRestrictBackgroundRulesOnUidStatusChangedUL(int paramInt1, int paramInt2, int paramInt3)
  {
    if (isProcStateAllowedWhileOnRestrictBackground(paramInt2) != isProcStateAllowedWhileOnRestrictBackground(paramInt3)) {
      updateRulesForDataUsageRestrictionsUL(paramInt1);
    }
  }
  
  private void updateRestrictionRulesForUidUL(int paramInt)
  {
    updateRuleForDeviceIdleUL(paramInt);
    updateRuleForAppIdleUL(paramInt);
    updateRuleForRestrictPowerUL(paramInt);
    updateRulesForPowerRestrictionsUL(paramInt);
    updateRulesForDataUsageRestrictionsUL(paramInt);
  }
  
  private void updateRulesForAllAppsUL(int paramInt)
  {
    if (Trace.isTagEnabled(2097152L)) {
      Trace.traceBegin(2097152L, "updateRulesForRestrictPowerUL-" + paramInt);
    }
    for (;;)
    {
      int i;
      int j;
      int n;
      try
      {
        Object localObject2 = this.mContext.getPackageManager();
        List localList = this.mUserManager.getUsers();
        localObject2 = ((PackageManager)localObject2).getInstalledApplications(795136);
        int k = localList.size();
        int m = ((List)localObject2).size();
        i = 0;
        if (i >= k) {
          break label234;
        }
        UserInfo localUserInfo = (UserInfo)localList.get(i);
        j = 0;
        if (j >= m) {
          break label227;
        }
        ApplicationInfo localApplicationInfo = (ApplicationInfo)((List)localObject2).get(j);
        n = UserHandle.getUid(localUserInfo.id, localApplicationInfo.uid);
        switch (paramInt)
        {
        case 1: 
          Slog.w("NetworkPolicy", "Invalid type for updateRulesForAllApps: " + paramInt);
        }
      }
      finally
      {
        if (!Trace.isTagEnabled(2097152L)) {
          continue;
        }
        Trace.traceEnd(2097152L);
      }
      updateRulesForDataUsageRestrictionsUL(n);
      break label253;
      updateRulesForPowerRestrictionsUL(n);
      break label253;
      label227:
      i += 1;
      continue;
      label234:
      if (Trace.isTagEnabled(2097152L)) {
        Trace.traceEnd(2097152L);
      }
      return;
      continue;
      label253:
      j += 1;
    }
  }
  
  private void updateRulesForDataUsageRestrictionsUL(int paramInt)
  {
    updateRulesForDataUsageRestrictionsUL(paramInt, false);
  }
  
  private void updateRulesForDataUsageRestrictionsUL(int paramInt, boolean paramBoolean)
  {
    int i;
    int k;
    boolean bool1;
    boolean bool2;
    int m;
    int j;
    if ((paramBoolean) || (isUidValidForWhitelistRules(paramInt)))
    {
      i = this.mUidPolicy.get(paramInt, 0);
      k = this.mUidRules.get(paramInt, 0);
      bool1 = isUidForegroundOnRestrictBackgroundUL(paramInt);
      if ((i & 0x1) == 0) {
        break label323;
      }
      paramBoolean = true;
      bool2 = this.mRestrictBackgroundWhitelistUids.get(paramInt);
      m = k & 0xF;
      j = 0;
      if (!bool1) {
        break label333;
      }
      if ((paramBoolean) || ((this.mRestrictBackground) && (!bool2))) {
        break label328;
      }
      i = j;
      if (bool2) {
        i = 1;
      }
      label99:
      j = i | k & 0xF0;
      if (LOGV) {
        Log.v("NetworkPolicy", "updateRuleForRestrictBackgroundUL(" + paramInt + ")" + ": isForeground=" + bool1 + ", isBlacklisted=" + paramBoolean + ", isWhitelisted=" + bool2 + ", oldRule=" + NetworkPolicyManager.uidRulesToString(m) + ", newRule=" + NetworkPolicyManager.uidRulesToString(i) + ", newUidRules=" + NetworkPolicyManager.uidRulesToString(j) + ", oldUidRules=" + NetworkPolicyManager.uidRulesToString(k));
      }
      if (j != 0) {
        break label365;
      }
      this.mUidRules.delete(paramInt);
      label247:
      if (i != m)
      {
        if ((i & 0x2) == 0) {
          break label378;
        }
        setMeteredNetworkWhitelist(paramInt, true);
        if (paramBoolean) {
          setMeteredNetworkBlacklist(paramInt, false);
        }
      }
    }
    for (;;)
    {
      this.mHandler.obtainMessage(1, paramInt, j).sendToTarget();
      return;
      if (LOGD) {
        Slog.d("NetworkPolicy", "no need to update restrict data rules for uid " + paramInt);
      }
      return;
      label323:
      paramBoolean = false;
      break;
      label328:
      i = 2;
      break label99;
      label333:
      if (paramBoolean)
      {
        i = 4;
        break label99;
      }
      i = j;
      if (!this.mRestrictBackground) {
        break label99;
      }
      i = j;
      if (!bool2) {
        break label99;
      }
      i = 1;
      break label99;
      label365:
      this.mUidRules.put(paramInt, j);
      break label247;
      label378:
      if ((m & 0x2) != 0)
      {
        if (!bool2) {
          setMeteredNetworkWhitelist(paramInt, false);
        }
        if (paramBoolean) {
          setMeteredNetworkBlacklist(paramInt, true);
        }
      }
      else if (((i & 0x4) != 0) || ((m & 0x4) != 0))
      {
        setMeteredNetworkBlacklist(paramInt, paramBoolean);
        if (((m & 0x4) != 0) && (bool2)) {
          setMeteredNetworkWhitelist(paramInt, bool2);
        }
      }
      else if (((i & 0x1) != 0) || ((m & 0x1) != 0))
      {
        setMeteredNetworkWhitelist(paramInt, bool2);
      }
      else
      {
        Log.wtf("NetworkPolicy", "Unexpected change of metered UID state for " + paramInt + ": foreground=" + bool1 + ", whitelisted=" + bool2 + ", blacklisted=" + paramBoolean + ", newRule=" + NetworkPolicyManager.uidRulesToString(j) + ", oldRule=" + NetworkPolicyManager.uidRulesToString(k));
      }
    }
  }
  
  private void updateRulesForGlobalChangeAL(boolean paramBoolean)
  {
    Trace.traceBegin(2097152L, "updateRulesForGlobalChangeAL");
    try
    {
      updateRulesForAppIdleUL();
      updateRulesForRestrictPowerUL();
      updateRulesForRestrictBackgroundUL();
      if (paramBoolean)
      {
        normalizePoliciesNL();
        updateNetworkRulesNL();
      }
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  private int updateRulesForPowerRestrictionsUL(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    boolean bool1 = false;
    if (!isUidValidForBlacklistRules(paramInt1))
    {
      if (LOGD) {
        Slog.d("NetworkPolicy", "no need to update restrict power rules for uid " + paramInt1);
      }
      return 0;
    }
    if (!paramBoolean) {
      bool1 = isUidIdle(paramInt1);
    }
    boolean bool2;
    boolean bool3;
    int i;
    label110:
    int k;
    if ((!bool1) && (!this.mRestrictPower))
    {
      paramBoolean = this.mDeviceIdleMode;
      bool2 = isUidForegroundOnRestrictPowerUL(paramInt1);
      bool3 = isWhitelistedBatterySaverUL(paramInt1);
      int j = paramInt2 & 0xF0;
      i = 0;
      if (!bool2) {
        break label345;
      }
      if (paramBoolean) {
        i = 32;
      }
      k = paramInt2 & 0xF | i;
      if (LOGV) {
        Log.v("NetworkPolicy", "updateRulesForPowerRestrictionsUL(" + paramInt1 + ")" + ", isIdle: " + bool1 + ", mRestrictPower: " + this.mRestrictPower + ", mDeviceIdleMode: " + this.mDeviceIdleMode + ", isForeground=" + bool2 + ", isWhitelisted=" + bool3 + ", oldRule=" + NetworkPolicyManager.uidRulesToString(j) + ", newRule=" + NetworkPolicyManager.uidRulesToString(i) + ", newUidRules=" + NetworkPolicyManager.uidRulesToString(k) + ", oldUidRules=" + NetworkPolicyManager.uidRulesToString(paramInt2));
      }
      if (i != j)
      {
        if ((i != 0) && ((i & 0x20) == 0)) {
          break label368;
        }
        if (LOGV) {
          Log.v("NetworkPolicy", "Allowing non-metered access for UID " + paramInt1);
        }
      }
    }
    for (;;)
    {
      this.mHandler.obtainMessage(1, paramInt1, k).sendToTarget();
      return k;
      paramBoolean = true;
      break;
      label345:
      if (!paramBoolean) {
        break label110;
      }
      if (bool3)
      {
        i = 32;
        break label110;
      }
      i = 64;
      break label110;
      label368:
      if ((i & 0x40) != 0)
      {
        if (LOGV) {
          Log.v("NetworkPolicy", "Rejecting non-metered access for UID " + paramInt1);
        }
      }
      else {
        Log.wtf("NetworkPolicy", "Unexpected change of non-metered UID state for " + paramInt1 + ": foreground=" + bool2 + ", whitelisted=" + bool3 + ", newRule=" + NetworkPolicyManager.uidRulesToString(k) + ", oldRule=" + NetworkPolicyManager.uidRulesToString(paramInt2));
      }
    }
  }
  
  private void updateRulesForPowerRestrictionsUL(int paramInt)
  {
    int i = updateRulesForPowerRestrictionsUL(paramInt, this.mUidRules.get(paramInt, 0), false);
    if (i == 0)
    {
      this.mUidRules.delete(paramInt);
      return;
    }
    this.mUidRules.put(paramInt, i);
  }
  
  private void updateRulesForRestrictBackgroundUL()
  {
    Trace.traceBegin(2097152L, "updateRulesForRestrictBackgroundUL");
    try
    {
      updateRulesForAllAppsUL(1);
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  private void updateRulesForRestrictPowerUL()
  {
    Trace.traceBegin(2097152L, "updateRulesForRestrictPowerUL");
    try
    {
      updateRulesForDeviceIdleUL();
      updateRulesForPowerSaveUL();
      updateRulesForAllAppsUL(2);
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  private void updateRulesForScreenUL()
  {
    int j = this.mUidState.size();
    int i = 0;
    while (i < j)
    {
      if (this.mUidState.valueAt(i) <= 4) {
        updateRestrictionRulesForUidUL(this.mUidState.keyAt(i));
      }
      i += 1;
    }
  }
  
  private void updateRulesForTempWhitelistChangeUL()
  {
    List localList = this.mUserManager.getUsers();
    int i = 0;
    while (i < localList.size())
    {
      UserInfo localUserInfo = (UserInfo)localList.get(i);
      int j = this.mPowerSaveTempWhitelistAppIds.size() - 1;
      while (j >= 0)
      {
        int k = this.mPowerSaveTempWhitelistAppIds.keyAt(j);
        k = UserHandle.getUid(localUserInfo.id, k);
        updateRuleForAppIdleUL(k);
        updateRuleForDeviceIdleUL(k);
        updateRuleForRestrictPowerUL(k);
        updateRulesForPowerRestrictionsUL(k);
        j -= 1;
      }
      i += 1;
    }
  }
  
  private void updateRulesForWhitelistedPowerSaveUL(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    if (paramBoolean)
    {
      if ((isWhitelistedBatterySaverUL(paramInt1)) || (isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.get(paramInt1)))) {
        setUidFirewallRule(paramInt2, paramInt1, 1);
      }
    }
    else {
      return;
    }
    setUidFirewallRule(paramInt2, paramInt1, 0);
  }
  
  private void updateRulesForWhitelistedPowerSaveUL(boolean paramBoolean, int paramInt, SparseIntArray paramSparseIntArray)
  {
    if (paramBoolean)
    {
      paramSparseIntArray.clear();
      List localList = this.mUserManager.getUsers();
      int i = localList.size() - 1;
      while (i >= 0)
      {
        UserInfo localUserInfo = (UserInfo)localList.get(i);
        int j = this.mPowerSaveTempWhitelistAppIds.size() - 1;
        int k;
        while (j >= 0)
        {
          if (this.mPowerSaveTempWhitelistAppIds.valueAt(j))
          {
            k = this.mPowerSaveTempWhitelistAppIds.keyAt(j);
            paramSparseIntArray.put(UserHandle.getUid(localUserInfo.id, k), 1);
          }
          j -= 1;
        }
        j = this.mPowerSaveWhitelistAppIds.size() - 1;
        while (j >= 0)
        {
          k = this.mPowerSaveWhitelistAppIds.keyAt(j);
          paramSparseIntArray.put(UserHandle.getUid(localUserInfo.id, k), 1);
          j -= 1;
        }
        i -= 1;
      }
      i = this.mUidState.size() - 1;
      while (i >= 0)
      {
        if (isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.valueAt(i))) {
          paramSparseIntArray.put(this.mUidState.keyAt(i), 1);
        }
        i -= 1;
      }
      setUidFirewallRulesAsync(paramInt, paramSparseIntArray, 1);
      return;
    }
    setUidFirewallRulesAsync(paramInt, null, 2);
  }
  
  private void updateScreenOn()
  {
    if (mFirstDeviceMode)
    {
      mFirstDeviceMode = false;
      if (mDozenNetworkPendingIntent != null) {
        mAlarmManager.cancel(mDozenNetworkPendingIntent);
      }
    }
    synchronized (this.mUidRulesFirstLock)
    {
      updateRulesForScreenUL();
      return;
    }
  }
  
  private void updateUidStateUL(int paramInt1, int paramInt2)
  {
    Trace.traceBegin(2097152L, "updateUidStateUL");
    try
    {
      int i = this.mUidState.get(paramInt1, 16);
      if (i != paramInt2)
      {
        this.mUidState.put(paramInt1, paramInt2);
        updateRestrictBackgroundRulesOnUidStatusChangedUL(paramInt1, i, paramInt2);
        if (isProcStateAllowedWhileIdleOrPowerSaveMode(i) != isProcStateAllowedWhileIdleOrPowerSaveMode(paramInt2))
        {
          if (isUidIdle(paramInt1)) {
            updateRuleForAppIdleUL(paramInt1);
          }
          if (this.mDeviceIdleMode) {
            updateRuleForDeviceIdleUL(paramInt1);
          }
          if (this.mRestrictPower) {
            updateRuleForRestrictPowerUL(paramInt1);
          }
          updateRulesForPowerRestrictionsUL(paramInt1);
        }
        updateNetworkStats(paramInt1, isUidStateForegroundUL(paramInt2));
      }
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  private void upgradeLegacyBackgroundDataUL()
  {
    boolean bool = true;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "background_data", 1) != 1) {}
    for (;;)
    {
      this.mRestrictBackground = bool;
      if (this.mRestrictBackground)
      {
        Intent localIntent = new Intent("android.net.conn.BACKGROUND_DATA_SETTING_CHANGED");
        this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
      }
      return;
      bool = false;
    }
  }
  
  boolean addDefaultRestrictBackgroundWhitelistUidsUL()
  {
    List localList = this.mUserManager.getUsers();
    int j = localList.size();
    boolean bool = false;
    int i = 0;
    if (i < j)
    {
      if (!addDefaultRestrictBackgroundWhitelistUidsUL(((UserInfo)localList.get(i)).id)) {}
      for (;;)
      {
        i += 1;
        break;
        bool = true;
      }
    }
    return bool;
  }
  
  public void addIdleHandler(MessageQueue.IdleHandler paramIdleHandler)
  {
    this.mHandler.getLooper().getQueue().addIdleHandler(paramIdleHandler);
  }
  
  void addNetworkPolicyNL(NetworkPolicy paramNetworkPolicy)
  {
    setNetworkPolicies((NetworkPolicy[])ArrayUtils.appendElement(NetworkPolicy.class, getNetworkPolicies(this.mContext.getOpPackageName()), paramNetworkPolicy));
  }
  
  public void addRestrictBackgroundWhitelistedUid(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    for (;;)
    {
      boolean bool2;
      int i;
      synchronized (this.mUidRulesFirstLock)
      {
        boolean bool1 = this.mRestrictBackgroundWhitelistUids.get(paramInt);
        if (bool1)
        {
          if (LOGD) {
            Slog.d("NetworkPolicy", "uid " + paramInt + " is already whitelisted");
          }
          return;
        }
        bool2 = isUidValidForWhitelistRules(paramInt);
        Slog.i("NetworkPolicy", "adding uid " + paramInt + " to restrict background whitelist");
        this.mRestrictBackgroundWhitelistUids.append(paramInt, true);
        if ((this.mDefaultRestrictBackgroundWhitelistUids.get(paramInt)) && (this.mRestrictBackgroundWhitelistRevokedUids.get(paramInt)))
        {
          if (LOGD) {
            Slog.d("NetworkPolicy", "Removing uid " + paramInt + " from revoked restrict background whitelist");
          }
          this.mRestrictBackgroundWhitelistRevokedUids.delete(paramInt);
        }
        if (bool2) {
          updateRulesForDataUsageRestrictionsUL(paramInt);
        }
        synchronized (this.mNetworkPoliciesSecondLock)
        {
          writePolicyAL();
          boolean bool3 = this.mRestrictBackground;
          if ((!bool3) || (bool1))
          {
            i = 0;
            this.mHandler.obtainMessage(9, paramInt, i, Boolean.TRUE).sendToTarget();
            return;
          }
        }
      }
      if (bool2) {
        i = 1;
      }
    }
  }
  
  public void addUidPolicy(int paramInt1, int paramInt2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    if (!UserHandle.isApp(paramInt1)) {
      throw new IllegalArgumentException("cannot apply policy to UID " + paramInt1);
    }
    synchronized (this.mUidRulesFirstLock)
    {
      int i = this.mUidPolicy.get(paramInt1, 0);
      paramInt2 |= i;
      if (i != paramInt2) {
        setUidPolicyUncheckedUL(paramInt1, i, paramInt2, true);
      }
      return;
    }
  }
  
  public void bindConnectivityManager(IConnectivityManager paramIConnectivityManager)
  {
    this.mConnManager = ((IConnectivityManager)Preconditions.checkNotNull(paramIConnectivityManager, "missing IConnectivityManager"));
  }
  
  public void bindNotificationManager(INotificationManager paramINotificationManager)
  {
    this.mNotifManager = ((INotificationManager)Preconditions.checkNotNull(paramINotificationManager, "missing INotificationManager"));
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "NetworkPolicy");
    IndentingPrintWriter localIndentingPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    ArraySet localArraySet = new ArraySet(paramArrayOfString.length);
    int i = 0;
    int j = paramArrayOfString.length;
    while (i < j)
    {
      localArraySet.add(paramArrayOfString[i]);
      i += 1;
    }
    for (;;)
    {
      int k;
      int m;
      synchronized (this.mUidRulesFirstLock)
      {
        synchronized (this.mNetworkPoliciesSecondLock)
        {
          if (localArraySet.contains("--unsnooze"))
          {
            i = this.mNetworkPolicy.size() - 1;
            if (i >= 0)
            {
              ((NetworkPolicy)this.mNetworkPolicy.valueAt(i)).clearSnooze();
              i -= 1;
              continue;
            }
            normalizePoliciesNL();
            updateNetworkEnabledNL();
            updateNetworkRulesNL();
            updateNotificationsNL();
            writePolicyAL();
            localIndentingPrintWriter.println("Cleared snooze timestamps");
            return;
          }
          boolean bool = dynamicallyConfigNetworkPolicyManagerServiceLogTag(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
          if (bool) {
            return;
          }
          localIndentingPrintWriter.print("System ready: ");
          localIndentingPrintWriter.println(this.mSystemReady);
          localIndentingPrintWriter.print("Restrict background: ");
          localIndentingPrintWriter.println(this.mRestrictBackground);
          localIndentingPrintWriter.print("Restrict power: ");
          localIndentingPrintWriter.println(this.mRestrictPower);
          localIndentingPrintWriter.print("Device idle: ");
          localIndentingPrintWriter.println(this.mDeviceIdleMode);
          localIndentingPrintWriter.println("Network policies:");
          localIndentingPrintWriter.increaseIndent();
          i = 0;
          if (i < this.mNetworkPolicy.size())
          {
            localIndentingPrintWriter.println(((NetworkPolicy)this.mNetworkPolicy.valueAt(i)).toString());
            i += 1;
            continue;
          }
          localIndentingPrintWriter.decreaseIndent();
          localIndentingPrintWriter.print("Metered ifaces: ");
          localIndentingPrintWriter.println(String.valueOf(this.mMeteredIfaces));
          localIndentingPrintWriter.println("Policy for UIDs:");
          localIndentingPrintWriter.increaseIndent();
          j = this.mUidPolicy.size();
          i = 0;
          if (i < j)
          {
            k = this.mUidPolicy.keyAt(i);
            m = this.mUidPolicy.valueAt(i);
            localIndentingPrintWriter.print("UID=");
            localIndentingPrintWriter.print(k);
            localIndentingPrintWriter.print(" policy=");
            localIndentingPrintWriter.print(DebugUtils.flagsToString(NetworkPolicyManager.class, "POLICY_", m));
            localIndentingPrintWriter.println();
            i += 1;
            continue;
          }
          localIndentingPrintWriter.decreaseIndent();
          j = this.mPowerSaveWhitelistExceptIdleAppIds.size();
          if (j > 0)
          {
            localIndentingPrintWriter.println("Power save whitelist (except idle) app ids:");
            localIndentingPrintWriter.increaseIndent();
            i = 0;
            if (i < j)
            {
              localIndentingPrintWriter.print("UID=");
              localIndentingPrintWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.keyAt(i));
              localIndentingPrintWriter.print(": ");
              localIndentingPrintWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.valueAt(i));
              localIndentingPrintWriter.println();
              i += 1;
              continue;
            }
            localIndentingPrintWriter.decreaseIndent();
          }
          j = this.mPowerSaveWhitelistAppIds.size();
          if (j > 0)
          {
            localIndentingPrintWriter.println("Power save whitelist app ids:");
            localIndentingPrintWriter.increaseIndent();
            i = 0;
            if (i < j)
            {
              localIndentingPrintWriter.print("UID=");
              localIndentingPrintWriter.print(this.mPowerSaveWhitelistAppIds.keyAt(i));
              localIndentingPrintWriter.print(": ");
              localIndentingPrintWriter.print(this.mPowerSaveWhitelistAppIds.valueAt(i));
              localIndentingPrintWriter.println();
              i += 1;
              continue;
            }
            localIndentingPrintWriter.decreaseIndent();
          }
          j = this.mRestrictBackgroundWhitelistUids.size();
          if (j > 0)
          {
            localIndentingPrintWriter.println("Restrict background whitelist uids:");
            localIndentingPrintWriter.increaseIndent();
            i = 0;
            if (i < j)
            {
              localIndentingPrintWriter.print("UID=");
              localIndentingPrintWriter.print(this.mRestrictBackgroundWhitelistUids.keyAt(i));
              localIndentingPrintWriter.println();
              i += 1;
              continue;
            }
            localIndentingPrintWriter.decreaseIndent();
          }
          j = this.mDefaultRestrictBackgroundWhitelistUids.size();
          if (j > 0)
          {
            localIndentingPrintWriter.println("Default restrict background whitelist uids:");
            localIndentingPrintWriter.increaseIndent();
            i = 0;
            if (i < j)
            {
              localIndentingPrintWriter.print("UID=");
              localIndentingPrintWriter.print(this.mDefaultRestrictBackgroundWhitelistUids.keyAt(i));
              localIndentingPrintWriter.println();
              i += 1;
              continue;
            }
            localIndentingPrintWriter.decreaseIndent();
          }
          j = this.mRestrictBackgroundWhitelistRevokedUids.size();
          if (j > 0)
          {
            localIndentingPrintWriter.println("Default restrict background whitelist uids revoked by users:");
            localIndentingPrintWriter.increaseIndent();
            i = 0;
            if (i < j)
            {
              localIndentingPrintWriter.print("UID=");
              localIndentingPrintWriter.print(this.mRestrictBackgroundWhitelistRevokedUids.keyAt(i));
              localIndentingPrintWriter.println();
              i += 1;
              continue;
            }
            localIndentingPrintWriter.decreaseIndent();
          }
          paramPrintWriter = new SparseBooleanArray();
          collectKeys(this.mUidState, paramPrintWriter);
          collectKeys(this.mUidRules, paramPrintWriter);
          localIndentingPrintWriter.println("Status for all known UIDs:");
          localIndentingPrintWriter.increaseIndent();
          j = paramPrintWriter.size();
          i = 0;
          if (i < j)
          {
            k = paramPrintWriter.keyAt(i);
            localIndentingPrintWriter.print("UID=");
            localIndentingPrintWriter.print(k);
            m = this.mUidState.get(k, 16);
            localIndentingPrintWriter.print(" state=");
            localIndentingPrintWriter.print(m);
            if (m > 2) {
              break label1199;
            }
            localIndentingPrintWriter.print(" (fg)");
            k = this.mUidRules.get(k, 0);
            localIndentingPrintWriter.print(" rules=");
            localIndentingPrintWriter.print(NetworkPolicyManager.uidRulesToString(k));
            localIndentingPrintWriter.println();
            i += 1;
            continue;
            localIndentingPrintWriter.print(paramFileDescriptor);
          }
        }
      }
      label1199:
      do
      {
        paramFileDescriptor = " (bg)";
        break;
        localIndentingPrintWriter.decreaseIndent();
        localIndentingPrintWriter.println("Status for just UIDs with rules:");
        localIndentingPrintWriter.increaseIndent();
        j = this.mUidRules.size();
        i = 0;
        while (i < j)
        {
          k = this.mUidRules.keyAt(i);
          localIndentingPrintWriter.print("UID=");
          localIndentingPrintWriter.print(k);
          k = this.mUidRules.get(k, 0);
          localIndentingPrintWriter.print(" rules=");
          localIndentingPrintWriter.print(NetworkPolicyManager.uidRulesToString(k));
          localIndentingPrintWriter.println();
          i += 1;
        }
        localIndentingPrintWriter.decreaseIndent();
        return;
      } while (m > 4);
      paramFileDescriptor = " (fg svc)";
    }
  }
  
  protected boolean dynamicallyConfigNetworkPolicyManagerServiceLogTag(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length >= 1)
    {
      if (!"log".equals(paramArrayOfString[0])) {
        return false;
      }
      if (paramArrayOfString.length != 3)
      {
        paramPrintWriter.println("Invalid argument! Get detail help as bellow:");
        logOutNetworkPolicyManagerServiceLogTagHelp(paramPrintWriter);
        return true;
      }
    }
    else
    {
      return false;
    }
    paramPrintWriter.println("dynamicallyConfigNetworkPolicyManagerServiceLogTag, args.length:" + paramArrayOfString.length);
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      paramPrintWriter.println("dynamicallyConfigNetworkPolicyManagerServiceLogTag, args[" + i + "]:" + paramArrayOfString[i]);
      i += 1;
    }
    paramFileDescriptor = paramArrayOfString[1];
    if ("1".equals(paramArrayOfString[2])) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println("dynamicallyConfigNetworkPolicyManagerServiceLogTag, logCategoryTag:" + paramFileDescriptor + ", on:" + bool);
      if ("netpolicy".equals(paramFileDescriptor))
      {
        LOGD = bool;
        LOGV = bool;
      }
      return true;
    }
  }
  
  public void factoryReset(String paramString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkPolicy");
    if (this.mUserManager.hasUserRestriction("no_network_reset")) {
      return;
    }
    NetworkPolicy[] arrayOfNetworkPolicy = getNetworkPolicies(this.mContext.getOpPackageName());
    paramString = NetworkTemplate.buildTemplateMobileAll(paramString);
    int j = arrayOfNetworkPolicy.length;
    int i = 0;
    while (i < j)
    {
      NetworkPolicy localNetworkPolicy = arrayOfNetworkPolicy[i];
      if (localNetworkPolicy.template.equals(paramString))
      {
        localNetworkPolicy.limitBytes = -1L;
        localNetworkPolicy.inferred = false;
        localNetworkPolicy.clearSnooze();
      }
      i += 1;
    }
    setNetworkPolicies(arrayOfNetworkPolicy);
    setRestrictBackground(false);
    if (!this.mUserManager.hasUserRestriction("no_control_apps"))
    {
      paramString = getUidsWithPolicy(1);
      j = paramString.length;
      i = 0;
      while (i < j)
      {
        setUidPolicy(paramString[i], 0);
        i += 1;
      }
    }
  }
  
  /* Error */
  public NetworkPolicy[] getNetworkPolicies(String arg1)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 1772
    //   7: ldc -107
    //   9: invokevirtual 1775	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   16: ldc_w 1970
    //   19: ldc -107
    //   21: invokevirtual 1775	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   24: aload_0
    //   25: getfield 480	com/android/server/net/NetworkPolicyManagerService:mNetworkPoliciesSecondLock	Ljava/lang/Object;
    //   28: astore_1
    //   29: aload_1
    //   30: monitorenter
    //   31: aload_0
    //   32: getfield 485	com/android/server/net/NetworkPolicyManagerService:mNetworkPolicy	Landroid/util/ArrayMap;
    //   35: invokevirtual 1108	android/util/ArrayMap:size	()I
    //   38: istore_3
    //   39: iload_3
    //   40: anewarray 872	android/net/NetworkPolicy
    //   43: astore 4
    //   45: iconst_0
    //   46: istore_2
    //   47: iload_2
    //   48: iload_3
    //   49: if_icmpge +60 -> 109
    //   52: aload 4
    //   54: iload_2
    //   55: aload_0
    //   56: getfield 485	com/android/server/net/NetworkPolicyManagerService:mNetworkPolicy	Landroid/util/ArrayMap;
    //   59: iload_2
    //   60: invokevirtual 1163	android/util/ArrayMap:valueAt	(I)Ljava/lang/Object;
    //   63: checkcast 872	android/net/NetworkPolicy
    //   66: aastore
    //   67: iload_2
    //   68: iconst_1
    //   69: iadd
    //   70: istore_2
    //   71: goto -24 -> 47
    //   74: astore 4
    //   76: aload_0
    //   77: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   80: ldc_w 1972
    //   83: ldc -107
    //   85: invokevirtual 1775	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   88: aload_0
    //   89: getfield 685	com/android/server/net/NetworkPolicyManagerService:mAppOps	Landroid/app/AppOpsManager;
    //   92: bipush 51
    //   94: invokestatic 1975	android/os/Binder:getCallingUid	()I
    //   97: aload_1
    //   98: invokevirtual 1979	android/app/AppOpsManager:noteOp	(IILjava/lang/String;)I
    //   101: ifeq -77 -> 24
    //   104: iconst_0
    //   105: anewarray 872	android/net/NetworkPolicy
    //   108: areturn
    //   109: aload_1
    //   110: monitorexit
    //   111: aload 4
    //   113: areturn
    //   114: astore 4
    //   116: aload_1
    //   117: monitorexit
    //   118: aload 4
    //   120: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	this	NetworkPolicyManagerService
    //   46	25	2	i	int
    //   38	12	3	j	int
    //   43	10	4	arrayOfNetworkPolicy	NetworkPolicy[]
    //   74	38	4	localSecurityException	SecurityException
    //   114	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	24	74	java/lang/SecurityException
    //   31	45	114	finally
    //   52	67	114	finally
  }
  
  public NetworkQuotaInfo getNetworkQuotaInfo(NetworkState paramNetworkState)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", "NetworkPolicy");
    long l = Binder.clearCallingIdentity();
    try
    {
      paramNetworkState = getNetworkQuotaInfoUnchecked(paramNetworkState);
      return paramNetworkState;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public boolean getRestrictBackground()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    synchronized (this.mUidRulesFirstLock)
    {
      boolean bool = this.mRestrictBackground;
      return bool;
    }
  }
  
  public int getRestrictBackgroundByCaller()
  {
    int i = 3;
    this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", "NetworkPolicy");
    int j = Binder.getCallingUid();
    synchronized (this.mUidRulesFirstLock)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        int k = getUidPolicy(j);
        Binder.restoreCallingIdentity(l);
        if (k == 1) {
          return 3;
        }
      }
      finally
      {
        localObject2 = finally;
        Binder.restoreCallingIdentity(l);
        throw ((Throwable)localObject2);
      }
    }
    boolean bool = this.mRestrictBackground;
    if (!bool) {
      return 1;
    }
    bool = this.mRestrictBackgroundWhitelistUids.get(j);
    if (bool) {
      i = 2;
    }
    return i;
  }
  
  public int[] getRestrictBackgroundWhitelistedUids()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    synchronized (this.mUidRulesFirstLock)
    {
      int j = this.mRestrictBackgroundWhitelistUids.size();
      int[] arrayOfInt = new int[j];
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = this.mRestrictBackgroundWhitelistUids.keyAt(i);
        i += 1;
      }
      if (LOGV) {
        Slog.v("NetworkPolicy", "getRestrictBackgroundWhitelistedUids(): " + this.mRestrictBackgroundWhitelistUids);
      }
      return arrayOfInt;
    }
  }
  
  public int getUidPolicy(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    synchronized (this.mUidRulesFirstLock)
    {
      paramInt = this.mUidPolicy.get(paramInt, 0);
      return paramInt;
    }
  }
  
  public int[] getUidsWithPolicy(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    Object localObject1 = new int[0];
    Object localObject4 = this.mUidRulesFirstLock;
    int i = 0;
    try
    {
      while (i < this.mUidPolicy.size())
      {
        int j = this.mUidPolicy.keyAt(i);
        Object localObject3 = localObject1;
        if (this.mUidPolicy.valueAt(i) == paramInt) {
          localObject3 = ArrayUtils.appendInt((int[])localObject1, j);
        }
        i += 1;
        localObject1 = localObject3;
      }
      return (int[])localObject1;
    }
    finally {}
  }
  
  public boolean isNetworkMetered(NetworkState paramNetworkState)
  {
    if (paramNetworkState.networkInfo == null) {
      return false;
    }
    NetworkIdentity localNetworkIdentity = NetworkIdentity.buildNetworkIdentity(this.mContext, paramNetworkState);
    synchronized (this.mNetworkPoliciesSecondLock)
    {
      NetworkPolicy localNetworkPolicy = findPolicyForNetworkNL(localNetworkIdentity);
      if (localNetworkPolicy != null) {
        return localNetworkPolicy.metered;
      }
    }
    int i = paramNetworkState.networkInfo.getType();
    return ((ConnectivityManager.isNetworkTypeMobile(i)) && (localNetworkIdentity.getMetered())) || (i == 6);
  }
  
  public boolean isUidForeground(int paramInt)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    synchronized (this.mUidRulesFirstLock)
    {
      boolean bool = isUidForegroundUL(paramInt);
      return bool;
    }
  }
  
  protected void logOutNetworkPolicyManagerServiceLogTagHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("********************** Help begin:**********************");
    paramPrintWriter.println("1 All NetworkPolicyManagerService log");
    paramPrintWriter.println("cmd: dumpsys power log all 0/1");
    paramPrintWriter.println("2 All needed log when oem log is on");
    paramPrintWriter.println("cmd: dumpsys networkpolicy log switch 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("********************** Help end.  **********************");
  }
  
  void maybeRefreshTrustedTime()
  {
    if (this.mTime.getCacheAge() > 172800000L) {
      this.mTime.forceRefresh();
    }
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
    throws RemoteException
  {
    new NetworkPolicyManagerShellCommand(this.mContext, this).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public void onTetheringChanged(String paramString, boolean paramBoolean)
  {
    if (LOGD) {
      Log.d("NetworkPolicy", "onTetherStateChanged(" + paramString + ", " + paramBoolean + ")");
    }
    synchronized (this.mUidRulesFirstLock)
    {
      if ((this.mRestrictBackground) && (paramBoolean))
      {
        Log.d("NetworkPolicy", "Tethering on (" + paramString + "); disable Data Saver");
        setRestrictBackground(false);
      }
      return;
    }
  }
  
  void performSnooze(NetworkTemplate paramNetworkTemplate, int paramInt)
  {
    maybeRefreshTrustedTime();
    long l = currentTimeMillis();
    NetworkPolicy localNetworkPolicy;
    synchronized (this.mUidRulesFirstLock)
    {
      synchronized (this.mNetworkPoliciesSecondLock)
      {
        localNetworkPolicy = (NetworkPolicy)this.mNetworkPolicy.get(paramNetworkTemplate);
        if (localNetworkPolicy == null) {
          throw new IllegalArgumentException("unable to find policy for " + paramNetworkTemplate);
        }
      }
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("unexpected type");
    case 1: 
      localNetworkPolicy.lastWarningSnooze = l;
    }
    for (;;)
    {
      normalizePoliciesNL();
      updateNetworkEnabledNL();
      updateNetworkRulesNL();
      updateNotificationsNL();
      writePolicyAL();
      return;
      localNetworkPolicy.lastLimitSnooze = l;
    }
  }
  
  void purgePowerSaveTempWhitelistUL()
  {
    int i = this.mPowerSaveTempWhitelistAppIds.size() - 1;
    while (i >= 0)
    {
      if (!this.mPowerSaveTempWhitelistAppIds.valueAt(i)) {
        this.mPowerSaveTempWhitelistAppIds.removeAt(i);
      }
      i -= 1;
    }
  }
  
  public void registerListener(INetworkPolicyListener paramINetworkPolicyListener)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkPolicy");
    this.mListeners.register(paramINetworkPolicyListener);
  }
  
  public void removeRestrictBackgroundWhitelistedUid(int paramInt)
  {
    int i = 1;
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    for (;;)
    {
      synchronized (this.mUidRulesFirstLock)
      {
        boolean bool = removeRestrictBackgroundWhitelistedUidUL(paramInt, false, true);
        ??? = this.mHandler;
        if (bool)
        {
          ((Handler)???).obtainMessage(9, paramInt, i, Boolean.FALSE).sendToTarget();
          return;
        }
      }
      i = 0;
    }
  }
  
  public void removeUidPolicy(int paramInt1, int paramInt2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    if (!UserHandle.isApp(paramInt1)) {
      throw new IllegalArgumentException("cannot apply policy to UID " + paramInt1);
    }
    synchronized (this.mUidRulesFirstLock)
    {
      int i = this.mUidPolicy.get(paramInt1, 0);
      paramInt2 = i & paramInt2;
      if (i != paramInt2) {
        setUidPolicyUncheckedUL(paramInt1, i, paramInt2, true);
      }
      return;
    }
  }
  
  boolean removeUserStateUL(int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (LOGV) {
      Slog.v("NetworkPolicy", "removeUserStateUL()");
    }
    boolean bool = false;
    ??? = new int[0];
    int i = 0;
    int k;
    Object localObject2;
    while (i < this.mRestrictBackgroundWhitelistUids.size())
    {
      k = this.mRestrictBackgroundWhitelistUids.keyAt(i);
      localObject2 = ???;
      if (UserHandle.getUserId(k) == paramInt) {
        localObject2 = ArrayUtils.appendInt((int[])???, k);
      }
      i += 1;
      ??? = localObject2;
    }
    if (???.length > 0)
    {
      k = ???.length;
      i = 0;
      while (i < k)
      {
        removeRestrictBackgroundWhitelistedUidUL(???[i], false, false);
        i += 1;
      }
      bool = true;
    }
    i = this.mRestrictBackgroundWhitelistRevokedUids.size() - 1;
    while (i >= 0)
    {
      if (UserHandle.getUserId(this.mRestrictBackgroundWhitelistRevokedUids.keyAt(i)) == paramInt)
      {
        this.mRestrictBackgroundWhitelistRevokedUids.removeAt(i);
        bool = true;
      }
      i -= 1;
    }
    ??? = new int[0];
    i = 0;
    while (i < this.mUidPolicy.size())
    {
      k = this.mUidPolicy.keyAt(i);
      localObject2 = ???;
      if (UserHandle.getUserId(k) == paramInt) {
        localObject2 = ArrayUtils.appendInt((int[])???, k);
      }
      i += 1;
      ??? = localObject2;
    }
    if (???.length > 0)
    {
      i = ???.length;
      paramInt = j;
      while (paramInt < i)
      {
        j = ???[paramInt];
        this.mUidPolicy.delete(j);
        paramInt += 1;
      }
      bool = true;
    }
    synchronized (this.mNetworkPoliciesSecondLock)
    {
      updateRulesForGlobalChangeAL(true);
      if ((paramBoolean) && (bool)) {
        writePolicyAL();
      }
      return bool;
    }
  }
  
  public void setConnectivityListener(INetworkPolicyListener paramINetworkPolicyListener)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkPolicy");
    if (this.mConnectivityListener != null) {
      throw new IllegalStateException("Connectivity listener already registered");
    }
    this.mConnectivityListener = paramINetworkPolicyListener;
  }
  
  public void setDeviceIdleMode(boolean paramBoolean)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    Trace.traceBegin(2097152L, "setDeviceIdleMode");
    if (this.mIdleWakeLock != null) {
      this.mIdleWakeLock.acquire(10000L);
    }
    Log.d("NetworkPolicy", "setDeviceIdleMode begin, " + paramBoolean + " , " + this.mDeviceIdleMode);
    for (;;)
    {
      try
      {
        synchronized (this.mUidRulesFirstLock)
        {
          boolean bool = this.mDeviceIdleMode;
          if (bool == paramBoolean) {
            return;
          }
          this.mDeviceIdleMode = paramBoolean;
          Log.e("NetworkPolicy", "setDeviceIdleMode enabled = " + paramBoolean);
          if (this.mSystemReady)
          {
            Log.e("NetworkPolicy", "setDeviceIdleMode start mDozenChange = " + isDozeChangeSupport);
            if ((this.mDeviceIdleMode) && (isDozeChangeSupport) && (!mFirstDeviceMode))
            {
              mFirstDeviceMode = true;
              Log.e("NetworkPolicy", "setDeviceIdleMode start ");
              mFirstDeviceModeTime = SystemClock.elapsedRealtime();
              mAlarmManager.setExactAndAllowWhileIdle(0, System.currentTimeMillis() + screenOffCheckDelayTime, mDozenNetworkPendingIntent);
            }
            updateRulesForRestrictPowerUL();
          }
          if (paramBoolean)
          {
            EventLogTags.writeDeviceIdleOnPhase("net");
            Trace.traceEnd(2097152L);
            Log.d("NetworkPolicy", "setDeviceIdleMode end");
            if ((this.mIdleWakeLock != null) && (this.mIdleWakeLock.isHeld())) {
              this.mIdleWakeLock.release();
            }
            return;
          }
        }
        EventLogTags.writeDeviceIdleOffPhase("net");
      }
      finally
      {
        Trace.traceEnd(2097152L);
      }
    }
  }
  
  /* Error */
  public void setNetworkPolicies(NetworkPolicy[] paramArrayOfNetworkPolicy)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 1772
    //   7: ldc -107
    //   9: invokevirtual 1775	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: invokestatic 1224	android/os/Binder:clearCallingIdentity	()J
    //   15: lstore_2
    //   16: aload_0
    //   17: invokevirtual 2070	com/android/server/net/NetworkPolicyManagerService:maybeRefreshTrustedTime	()V
    //   20: aload_0
    //   21: getfield 478	com/android/server/net/NetworkPolicyManagerService:mUidRulesFirstLock	Ljava/lang/Object;
    //   24: astore 4
    //   26: aload 4
    //   28: monitorenter
    //   29: aload_0
    //   30: getfield 480	com/android/server/net/NetworkPolicyManagerService:mNetworkPoliciesSecondLock	Ljava/lang/Object;
    //   33: astore 5
    //   35: aload 5
    //   37: monitorenter
    //   38: aload_0
    //   39: aload_1
    //   40: invokespecial 1303	com/android/server/net/NetworkPolicyManagerService:normalizePoliciesNL	([Landroid/net/NetworkPolicy;)V
    //   43: aload_0
    //   44: invokevirtual 1832	com/android/server/net/NetworkPolicyManagerService:updateNetworkEnabledNL	()V
    //   47: aload_0
    //   48: invokevirtual 1661	com/android/server/net/NetworkPolicyManagerService:updateNetworkRulesNL	()V
    //   51: aload_0
    //   52: invokevirtual 1530	com/android/server/net/NetworkPolicyManagerService:updateNotificationsNL	()V
    //   55: aload_0
    //   56: invokevirtual 1457	com/android/server/net/NetworkPolicyManagerService:writePolicyAL	()V
    //   59: aload 5
    //   61: monitorexit
    //   62: aload 4
    //   64: monitorexit
    //   65: lload_2
    //   66: invokestatic 1230	android/os/Binder:restoreCallingIdentity	(J)V
    //   69: return
    //   70: astore_1
    //   71: aload 5
    //   73: monitorexit
    //   74: aload_1
    //   75: athrow
    //   76: astore_1
    //   77: aload 4
    //   79: monitorexit
    //   80: aload_1
    //   81: athrow
    //   82: astore_1
    //   83: lload_2
    //   84: invokestatic 1230	android/os/Binder:restoreCallingIdentity	(J)V
    //   87: aload_1
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	NetworkPolicyManagerService
    //   0	89	1	paramArrayOfNetworkPolicy	NetworkPolicy[]
    //   15	69	2	l	long
    // Exception table:
    //   from	to	target	type
    //   38	59	70	finally
    //   29	38	76	finally
    //   59	62	76	finally
    //   71	76	76	finally
    //   16	29	82	finally
    //   62	65	82	finally
    //   77	82	82	finally
  }
  
  public void setRestrictBackground(boolean paramBoolean)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    long l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        maybeRefreshTrustedTime();
        synchronized (this.mUidRulesFirstLock)
        {
          if (paramBoolean == this.mRestrictBackground)
          {
            Slog.w("NetworkPolicy", "setRestrictBackground: already " + paramBoolean);
            return;
          }
          setRestrictBackgroundUL(paramBoolean);
          Binder.restoreCallingIdentity(l);
          ??? = this.mHandler;
          if (paramBoolean)
          {
            i = 1;
            ((Handler)???).obtainMessage(6, i, 0).sendToTarget();
            return;
          }
        }
        int i = 0;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  public void setUidPolicy(int paramInt1, int paramInt2)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    if (!UserHandle.isApp(paramInt1)) {
      throw new IllegalArgumentException("cannot apply policy to UID " + paramInt1);
    }
    synchronized (this.mUidRulesFirstLock)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        int i = this.mUidPolicy.get(paramInt1, 0);
        if (i != paramInt2) {
          setUidPolicyUncheckedUL(paramInt1, i, paramInt2, true);
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
  
  public void snoozeLimit(NetworkTemplate paramNetworkTemplate)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
    long l = Binder.clearCallingIdentity();
    try
    {
      performSnooze(paramNetworkTemplate, 2);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public void systemReady()
  {
    // Byte code:
    //   0: ldc2_w 1576
    //   3: ldc_w 2145
    //   6: invokestatic 1589	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   9: aload_0
    //   10: invokespecial 2146	com/android/server/net/NetworkPolicyManagerService:isBandwidthControlEnabled	()Z
    //   13: ifne +19 -> 32
    //   16: ldc -107
    //   18: ldc_w 2148
    //   21: invokestatic 1209	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   24: pop
    //   25: ldc2_w 1576
    //   28: invokestatic 1613	android/os/Trace:traceEnd	(J)V
    //   31: return
    //   32: aload_0
    //   33: ldc_w 1274
    //   36: invokestatic 2150	com/android/server/LocalServices:getService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   39: checkcast 1274	android/app/usage/UsageStatsManagerInternal
    //   42: putfield 1272	com/android/server/net/NetworkPolicyManagerService:mUsageStats	Landroid/app/usage/UsageStatsManagerInternal;
    //   45: aload_0
    //   46: getfield 478	com/android/server/net/NetworkPolicyManagerService:mUidRulesFirstLock	Ljava/lang/Object;
    //   49: astore_1
    //   50: aload_1
    //   51: monitorenter
    //   52: aload_0
    //   53: getfield 480	com/android/server/net/NetworkPolicyManagerService:mNetworkPoliciesSecondLock	Ljava/lang/Object;
    //   56: astore_2
    //   57: aload_2
    //   58: monitorenter
    //   59: aload_0
    //   60: invokevirtual 2153	com/android/server/net/NetworkPolicyManagerService:updatePowerSaveWhitelistUL	()V
    //   63: aload_0
    //   64: ldc_w 2155
    //   67: invokestatic 2150	com/android/server/LocalServices:getService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   70: checkcast 2155	android/os/PowerManagerInternal
    //   73: putfield 2157	com/android/server/net/NetworkPolicyManagerService:mPowerManagerInternal	Landroid/os/PowerManagerInternal;
    //   76: aload_0
    //   77: getfield 2157	com/android/server/net/NetworkPolicyManagerService:mPowerManagerInternal	Landroid/os/PowerManagerInternal;
    //   80: new 22	com/android/server/net/NetworkPolicyManagerService$17
    //   83: dup
    //   84: aload_0
    //   85: invokespecial 2158	com/android/server/net/NetworkPolicyManagerService$17:<init>	(Lcom/android/server/net/NetworkPolicyManagerService;)V
    //   88: invokevirtual 2162	android/os/PowerManagerInternal:registerLowPowerModeObserver	(Landroid/os/PowerManagerInternal$LowPowerModeListener;)V
    //   91: aload_0
    //   92: aload_0
    //   93: getfield 2157	com/android/server/net/NetworkPolicyManagerService:mPowerManagerInternal	Landroid/os/PowerManagerInternal;
    //   96: invokevirtual 2165	android/os/PowerManagerInternal:getLowPowerModeEnabled	()Z
    //   99: putfield 1475	com/android/server/net/NetworkPolicyManagerService:mRestrictPower	Z
    //   102: aload_0
    //   103: iconst_1
    //   104: putfield 1848	com/android/server/net/NetworkPolicyManagerService:mSystemReady	Z
    //   107: aload_0
    //   108: invokespecial 2167	com/android/server/net/NetworkPolicyManagerService:readPolicyAL	()V
    //   111: aload_0
    //   112: invokevirtual 2169	com/android/server/net/NetworkPolicyManagerService:addDefaultRestrictBackgroundWhitelistUidsUL	()Z
    //   115: ifeq +7 -> 122
    //   118: aload_0
    //   119: invokevirtual 1457	com/android/server/net/NetworkPolicyManagerService:writePolicyAL	()V
    //   122: aload_0
    //   123: aload_0
    //   124: getfield 1387	com/android/server/net/NetworkPolicyManagerService:mRestrictBackground	Z
    //   127: invokespecial 2140	com/android/server/net/NetworkPolicyManagerService:setRestrictBackgroundUL	(Z)V
    //   130: aload_0
    //   131: iconst_0
    //   132: invokespecial 371	com/android/server/net/NetworkPolicyManagerService:updateRulesForGlobalChangeAL	(Z)V
    //   135: aload_0
    //   136: invokevirtual 1530	com/android/server/net/NetworkPolicyManagerService:updateNotificationsNL	()V
    //   139: aload_2
    //   140: monitorexit
    //   141: aload_1
    //   142: monitorexit
    //   143: aload_0
    //   144: getfield 594	com/android/server/net/NetworkPolicyManagerService:mActivityManager	Landroid/app/IActivityManager;
    //   147: aload_0
    //   148: getfield 533	com/android/server/net/NetworkPolicyManagerService:mUidObserver	Landroid/app/IUidObserver;
    //   151: iconst_3
    //   152: invokeinterface 2173 3 0
    //   157: aload_0
    //   158: getfield 604	com/android/server/net/NetworkPolicyManagerService:mNetworkManager	Landroid/os/INetworkManagementService;
    //   161: aload_0
    //   162: getfield 569	com/android/server/net/NetworkPolicyManagerService:mAlertObserver	Landroid/net/INetworkManagementEventObserver;
    //   165: invokeinterface 2177 2 0
    //   170: new 2179	android/content/IntentFilter
    //   173: dup
    //   174: invokespecial 2180	android/content/IntentFilter:<init>	()V
    //   177: astore_1
    //   178: aload_1
    //   179: ldc_w 2182
    //   182: invokevirtual 2185	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   185: aload_0
    //   186: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   189: aload_0
    //   190: getfield 542	com/android/server/net/NetworkPolicyManagerService:mScreenReceiver	Landroid/content/BroadcastReceiver;
    //   193: aload_1
    //   194: aconst_null
    //   195: aload_0
    //   196: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   199: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   202: pop
    //   203: new 2179	android/content/IntentFilter
    //   206: dup
    //   207: ldc_w 2191
    //   210: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   213: astore_1
    //   214: aload_0
    //   215: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   218: aload_0
    //   219: getfield 536	com/android/server/net/NetworkPolicyManagerService:mPowerSaveWhitelistReceiver	Landroid/content/BroadcastReceiver;
    //   222: aload_1
    //   223: aconst_null
    //   224: aload_0
    //   225: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   228: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   231: pop
    //   232: ldc_w 2194
    //   235: invokestatic 2150	com/android/server/LocalServices:getService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   238: checkcast 2194	com/android/server/DeviceIdleController$LocalService
    //   241: aload_0
    //   242: getfield 539	com/android/server/net/NetworkPolicyManagerService:mTempPowerSaveChangedCallback	Ljava/lang/Runnable;
    //   245: invokevirtual 2198	com/android/server/DeviceIdleController$LocalService:setNetworkPolicyTempWhitelistCallback	(Ljava/lang/Runnable;)V
    //   248: new 2179	android/content/IntentFilter
    //   251: dup
    //   252: ldc_w 2200
    //   255: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   258: astore_1
    //   259: aload_0
    //   260: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   263: aload_0
    //   264: getfield 572	com/android/server/net/NetworkPolicyManagerService:mConnReceiver	Landroid/content/BroadcastReceiver;
    //   267: aload_1
    //   268: ldc_w 1945
    //   271: aload_0
    //   272: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   275: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   278: pop
    //   279: new 2179	android/content/IntentFilter
    //   282: dup
    //   283: invokespecial 2180	android/content/IntentFilter:<init>	()V
    //   286: astore_1
    //   287: aload_1
    //   288: ldc_w 2202
    //   291: invokevirtual 2185	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   294: aload_1
    //   295: ldc_w 2204
    //   298: invokevirtual 2207	android/content/IntentFilter:addDataScheme	(Ljava/lang/String;)V
    //   301: aload_0
    //   302: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   305: aload_0
    //   306: getfield 545	com/android/server/net/NetworkPolicyManagerService:mPackageReceiver	Landroid/content/BroadcastReceiver;
    //   309: aload_1
    //   310: aconst_null
    //   311: aload_0
    //   312: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   315: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   318: pop
    //   319: aload_0
    //   320: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   323: aload_0
    //   324: getfield 548	com/android/server/net/NetworkPolicyManagerService:mUidRemovedReceiver	Landroid/content/BroadcastReceiver;
    //   327: new 2179	android/content/IntentFilter
    //   330: dup
    //   331: ldc_w 2209
    //   334: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   337: aconst_null
    //   338: aload_0
    //   339: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   342: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   345: pop
    //   346: new 2179	android/content/IntentFilter
    //   349: dup
    //   350: invokespecial 2180	android/content/IntentFilter:<init>	()V
    //   353: astore_1
    //   354: aload_1
    //   355: ldc_w 2211
    //   358: invokevirtual 2185	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   361: aload_1
    //   362: ldc_w 2213
    //   365: invokevirtual 2185	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   368: aload_0
    //   369: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   372: aload_0
    //   373: getfield 551	com/android/server/net/NetworkPolicyManagerService:mUserReceiver	Landroid/content/BroadcastReceiver;
    //   376: aload_1
    //   377: aconst_null
    //   378: aload_0
    //   379: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   382: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   385: pop
    //   386: new 2179	android/content/IntentFilter
    //   389: dup
    //   390: ldc_w 2215
    //   393: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   396: astore_1
    //   397: aload_0
    //   398: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   401: aload_0
    //   402: getfield 554	com/android/server/net/NetworkPolicyManagerService:mStatsReceiver	Landroid/content/BroadcastReceiver;
    //   405: aload_1
    //   406: ldc_w 2217
    //   409: aload_0
    //   410: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   413: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   416: pop
    //   417: new 2179	android/content/IntentFilter
    //   420: dup
    //   421: ldc 54
    //   423: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   426: astore_1
    //   427: aload_0
    //   428: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   431: aload_0
    //   432: getfield 557	com/android/server/net/NetworkPolicyManagerService:mAllowReceiver	Landroid/content/BroadcastReceiver;
    //   435: aload_1
    //   436: ldc_w 1772
    //   439: aload_0
    //   440: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   443: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   446: pop
    //   447: new 2179	android/content/IntentFilter
    //   450: dup
    //   451: ldc 60
    //   453: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   456: astore_1
    //   457: aload_0
    //   458: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   461: aload_0
    //   462: getfield 560	com/android/server/net/NetworkPolicyManagerService:mSnoozeWarningReceiver	Landroid/content/BroadcastReceiver;
    //   465: aload_1
    //   466: ldc_w 1772
    //   469: aload_0
    //   470: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   473: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   476: pop
    //   477: new 2179	android/content/IntentFilter
    //   480: dup
    //   481: ldc_w 2219
    //   484: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   487: astore_1
    //   488: aload_0
    //   489: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   492: aload_0
    //   493: getfield 563	com/android/server/net/NetworkPolicyManagerService:mWifiConfigReceiver	Landroid/content/BroadcastReceiver;
    //   496: aload_1
    //   497: aconst_null
    //   498: aload_0
    //   499: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   502: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   505: pop
    //   506: new 2179	android/content/IntentFilter
    //   509: dup
    //   510: ldc_w 2221
    //   513: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   516: astore_1
    //   517: aload_0
    //   518: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   521: aload_0
    //   522: getfield 566	com/android/server/net/NetworkPolicyManagerService:mWifiStateReceiver	Landroid/content/BroadcastReceiver;
    //   525: aload_1
    //   526: aconst_null
    //   527: aload_0
    //   528: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   531: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   534: pop
    //   535: aload_0
    //   536: getfield 1272	com/android/server/net/NetworkPolicyManagerService:mUsageStats	Landroid/app/usage/UsageStatsManagerInternal;
    //   539: new 40	com/android/server/net/NetworkPolicyManagerService$AppIdleStateChangeListener
    //   542: dup
    //   543: aload_0
    //   544: aconst_null
    //   545: invokespecial 2224	com/android/server/net/NetworkPolicyManagerService$AppIdleStateChangeListener:<init>	(Lcom/android/server/net/NetworkPolicyManagerService;Lcom/android/server/net/NetworkPolicyManagerService$AppIdleStateChangeListener;)V
    //   548: invokevirtual 2228	android/app/usage/UsageStatsManagerInternal:addAppIdleStateChangeListener	(Landroid/app/usage/UsageStatsManagerInternal$AppIdleStateChangeListener;)V
    //   551: ldc2_w 1576
    //   554: invokestatic 1613	android/os/Trace:traceEnd	(J)V
    //   557: aload_0
    //   558: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   561: invokevirtual 723	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   564: ldc_w 2230
    //   567: invokevirtual 2233	android/content/pm/PackageManager:hasSystemFeature	(Ljava/lang/String;)Z
    //   570: putstatic 440	com/android/server/net/NetworkPolicyManagerService:isDozeChangeSupport	Z
    //   573: aload_0
    //   574: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   577: ldc_w 2235
    //   580: invokevirtual 632	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   583: checkcast 1718	android/app/AlarmManager
    //   586: putstatic 1716	com/android/server/net/NetworkPolicyManagerService:mAlarmManager	Landroid/app/AlarmManager;
    //   589: new 832	android/content/Intent
    //   592: dup
    //   593: getstatic 454	com/android/server/net/NetworkPolicyManagerService:SCREEN_DOZE_NETWORKPOLICY	Ljava/lang/String;
    //   596: invokespecial 833	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   599: putstatic 2237	com/android/server/net/NetworkPolicyManagerService:mDozeNetworkIntent	Landroid/content/Intent;
    //   602: aload_0
    //   603: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   606: iconst_0
    //   607: getstatic 2237	com/android/server/net/NetworkPolicyManagerService:mDozeNetworkIntent	Landroid/content/Intent;
    //   610: iconst_0
    //   611: invokestatic 1035	android/app/PendingIntent:getBroadcast	(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
    //   614: putstatic 456	com/android/server/net/NetworkPolicyManagerService:mDozenNetworkPendingIntent	Landroid/app/PendingIntent;
    //   617: new 2179	android/content/IntentFilter
    //   620: dup
    //   621: getstatic 454	com/android/server/net/NetworkPolicyManagerService:SCREEN_DOZE_NETWORKPOLICY	Ljava/lang/String;
    //   624: invokespecial 2192	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   627: astore_1
    //   628: aload_0
    //   629: getfield 313	com/android/server/net/NetworkPolicyManagerService:mContext	Landroid/content/Context;
    //   632: aload_0
    //   633: getfield 575	com/android/server/net/NetworkPolicyManagerService:mDozeChangeReceiver	Landroid/content/BroadcastReceiver;
    //   636: aload_1
    //   637: aconst_null
    //   638: aload_0
    //   639: getfield 662	com/android/server/net/NetworkPolicyManagerService:mHandler	Landroid/os/Handler;
    //   642: invokevirtual 2189	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   645: pop
    //   646: return
    //   647: astore_3
    //   648: aload_2
    //   649: monitorexit
    //   650: aload_3
    //   651: athrow
    //   652: astore_2
    //   653: aload_1
    //   654: monitorexit
    //   655: aload_2
    //   656: athrow
    //   657: astore_1
    //   658: ldc2_w 1576
    //   661: invokestatic 1613	android/os/Trace:traceEnd	(J)V
    //   664: aload_1
    //   665: athrow
    //   666: astore_1
    //   667: goto -497 -> 170
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	670	0	this	NetworkPolicyManagerService
    //   657	8	1	localObject2	Object
    //   666	1	1	localRemoteException	RemoteException
    //   652	4	2	localObject4	Object
    //   647	4	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   59	122	647	finally
    //   122	139	647	finally
    //   52	59	652	finally
    //   139	141	652	finally
    //   648	652	652	finally
    //   9	25	657	finally
    //   32	52	657	finally
    //   141	143	657	finally
    //   143	170	657	finally
    //   170	551	657	finally
    //   653	657	657	finally
    //   143	170	666	android/os/RemoteException
  }
  
  public void unregisterListener(INetworkPolicyListener paramINetworkPolicyListener)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "NetworkPolicy");
    this.mListeners.unregister(paramINetworkPolicyListener);
  }
  
  void updateNetworkEnabledNL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "updateNetworkEnabledNL()");
    }
    long l1 = currentTimeMillis();
    int j = this.mNetworkPolicy.size() - 1;
    if (j >= 0)
    {
      NetworkPolicy localNetworkPolicy = (NetworkPolicy)this.mNetworkPolicy.valueAt(j);
      int i;
      if ((localNetworkPolicy.limitBytes != -1L) && (localNetworkPolicy.hasCycle()))
      {
        long l2 = NetworkPolicyManager.computeLastCycleBoundary(l1, localNetworkPolicy);
        if (!localNetworkPolicy.isOverLimit(getTotalBytes(localNetworkPolicy.template, l2, l1))) {
          break label151;
        }
        if (localNetworkPolicy.lastLimitSnooze >= l2) {
          break label146;
        }
        i = 1;
        label108:
        if (i == 0) {
          break label156;
        }
      }
      label146:
      label151:
      label156:
      for (boolean bool = false;; bool = true)
      {
        setNetworkTemplateEnabled(localNetworkPolicy.template, bool);
        for (;;)
        {
          j -= 1;
          break;
          setNetworkTemplateEnabled(localNetworkPolicy.template, true);
        }
        i = 0;
        break label108;
        i = 0;
        break label108;
      }
    }
  }
  
  void updateNetworkRulesNL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "updateNetworkRulesNL()");
    }
    Object localObject3;
    int j;
    Object localObject5;
    Object localObject6;
    for (;;)
    {
      try
      {
        localObject2 = this.mConnManager.getAllNetworkState();
        localObject3 = new ArrayList(localObject2.length);
        ArraySet localArraySet = new ArraySet(localObject2.length);
        i = 0;
        j = localObject2.length;
        if (i >= j) {
          break;
        }
        localObject5 = localObject2[i];
        if ((((NetworkState)localObject5).networkInfo != null) && (((NetworkState)localObject5).networkInfo.isConnected()))
        {
          localObject4 = NetworkIdentity.buildNetworkIdentity(this.mContext, (NetworkState)localObject5);
          localObject6 = ((NetworkState)localObject5).linkProperties.getInterfaceName();
          if (localObject6 != null) {
            ((ArrayList)localObject3).add(Pair.create(localObject6, localObject4));
          }
          localObject5 = ((NetworkState)localObject5).linkProperties.getStackedLinks().iterator();
          if (((Iterator)localObject5).hasNext())
          {
            localObject6 = ((LinkProperties)((Iterator)localObject5).next()).getInterfaceName();
            if (localObject6 == null) {
              continue;
            }
            ((ArrayList)localObject3).add(Pair.create(localObject6, localObject4));
            continue;
          }
        }
        i += 1;
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
    }
    this.mNetworkRules.clear();
    Object localObject4 = Lists.newArrayList();
    int i = this.mNetworkPolicy.size() - 1;
    while (i >= 0)
    {
      localObject5 = (NetworkPolicy)this.mNetworkPolicy.valueAt(i);
      ((ArrayList)localObject4).clear();
      j = ((ArrayList)localObject3).size() - 1;
      while (j >= 0)
      {
        localObject6 = (Pair)((ArrayList)localObject3).get(j);
        if (((NetworkPolicy)localObject5).template.matches((NetworkIdentity)((Pair)localObject6).second)) {
          ((ArrayList)localObject4).add((String)((Pair)localObject6).first);
        }
        j -= 1;
      }
      if (((ArrayList)localObject4).size() > 0)
      {
        localObject6 = (String[])((ArrayList)localObject4).toArray(new String[((ArrayList)localObject4).size()]);
        this.mNetworkRules.put(localObject5, localObject6);
      }
      i -= 1;
    }
    long l1 = Long.MAX_VALUE;
    Object localObject2 = new ArraySet(localObject2.length);
    long l4 = currentTimeMillis();
    i = this.mNetworkRules.size() - 1;
    while (i >= 0)
    {
      localObject3 = (NetworkPolicy)this.mNetworkRules.keyAt(i);
      localObject4 = (String[])this.mNetworkRules.valueAt(i);
      long l3;
      label504:
      int k;
      if (((NetworkPolicy)localObject3).hasCycle())
      {
        l2 = NetworkPolicyManager.computeLastCycleBoundary(l4, (NetworkPolicy)localObject3);
        l3 = getTotalBytes(((NetworkPolicy)localObject3).template, l2, l4);
        if (LOGD) {
          Slog.d("NetworkPolicy", "applying policy " + localObject3 + " to ifaces " + Arrays.toString((Object[])localObject4));
        }
        if (((NetworkPolicy)localObject3).warningBytes == -1L) {
          break label632;
        }
        j = 1;
        if (((NetworkPolicy)localObject3).limitBytes == -1L) {
          break label637;
        }
        k = 1;
        label518:
        if ((k == 0) && (!((NetworkPolicy)localObject3).metered)) {
          break label678;
        }
        if (k != 0) {
          break label642;
        }
        l2 = Long.MAX_VALUE;
      }
      for (;;)
      {
        if (localObject4.length > 1) {
          Slog.w("NetworkPolicy", "shared quota unsupported; generating rule for each iface");
        }
        int m = 0;
        int n = localObject4.length;
        while (m < n)
        {
          localObject5 = localObject4[m];
          this.mHandler.obtainMessage(10, (int)(l2 >> 32), (int)(0xFFFFFFFFFFFFFFFF & l2), localObject5).sendToTarget();
          ((ArraySet)localObject2).add(localObject5);
          m += 1;
        }
        l2 = Long.MAX_VALUE;
        l3 = 0L;
        break;
        label632:
        j = 0;
        break label504;
        label637:
        k = 0;
        break label518;
        label642:
        if (((NetworkPolicy)localObject3).lastLimitSnooze >= l2) {
          l2 = Long.MAX_VALUE;
        } else {
          l2 = Math.max(1L, ((NetworkPolicy)localObject3).limitBytes - l3);
        }
      }
      label678:
      long l2 = l1;
      if (j != 0)
      {
        l2 = l1;
        if (((NetworkPolicy)localObject3).warningBytes < l1) {
          l2 = ((NetworkPolicy)localObject3).warningBytes;
        }
      }
      l1 = l2;
      if (k != 0)
      {
        l1 = l2;
        if (((NetworkPolicy)localObject3).limitBytes < l2) {
          l1 = ((NetworkPolicy)localObject3).limitBytes;
        }
      }
      i -= 1;
    }
    i = localRemoteException.size() - 1;
    while (i >= 0)
    {
      localObject3 = (String)localRemoteException.valueAt(i);
      this.mHandler.obtainMessage(10, Integer.MAX_VALUE, -1, localObject3).sendToTarget();
      ((ArraySet)localObject2).add(localObject3);
      i -= 1;
    }
    this.mHandler.obtainMessage(7, Long.valueOf(l1)).sendToTarget();
    i = this.mMeteredIfaces.size() - 1;
    while (i >= 0)
    {
      localObject1 = (String)this.mMeteredIfaces.valueAt(i);
      if (!((ArraySet)localObject2).contains(localObject1)) {
        this.mHandler.obtainMessage(11, localObject1).sendToTarget();
      }
      i -= 1;
    }
    this.mMeteredIfaces = ((ArraySet)localObject2);
    Object localObject1 = (String[])this.mMeteredIfaces.toArray(new String[this.mMeteredIfaces.size()]);
    this.mHandler.obtainMessage(2, localObject1).sendToTarget();
  }
  
  void updateNotificationsNL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "updateNotificationsNL()");
    }
    ArraySet localArraySet = new ArraySet(this.mActiveNotifs);
    this.mActiveNotifs.clear();
    long l1 = currentTimeMillis();
    int i = this.mNetworkPolicy.size() - 1;
    Object localObject;
    if (i >= 0)
    {
      localObject = (NetworkPolicy)this.mNetworkPolicy.valueAt(i);
      if (!isTemplateRelevant(((NetworkPolicy)localObject).template)) {}
      for (;;)
      {
        i -= 1;
        break;
        if (((NetworkPolicy)localObject).hasCycle())
        {
          long l2 = NetworkPolicyManager.computeLastCycleBoundary(l1, (NetworkPolicy)localObject);
          long l3 = getTotalBytes(((NetworkPolicy)localObject).template, l2, l1);
          if (((NetworkPolicy)localObject).isOverLimit(l3))
          {
            if (((NetworkPolicy)localObject).lastLimitSnooze >= l2)
            {
              broadcastDataUsageAlarm();
            }
            else
            {
              broadcastDataUsageAlarm();
              notifyOverLimitNL(((NetworkPolicy)localObject).template);
            }
          }
          else
          {
            notifyUnderLimitNL(((NetworkPolicy)localObject).template);
            if ((((NetworkPolicy)localObject).isOverWarning(l3)) && (((NetworkPolicy)localObject).lastWarningSnooze < l2)) {
              broadcastDataUsageAlarm();
            }
          }
        }
      }
    }
    i = localArraySet.size() - 1;
    while (i >= 0)
    {
      localObject = (String)localArraySet.valueAt(i);
      if (!this.mActiveNotifs.contains(localObject)) {
        cancelNotification((String)localObject);
      }
      i -= 1;
    }
  }
  
  void updatePowerSaveTempWhitelistUL()
  {
    int j = 0;
    try
    {
      int k = this.mPowerSaveTempWhitelistAppIds.size();
      int i = 0;
      while (i < k)
      {
        this.mPowerSaveTempWhitelistAppIds.setValueAt(i, false);
        i += 1;
      }
      int[] arrayOfInt = this.mDeviceIdleController.getAppIdTempWhitelist();
      if (arrayOfInt != null)
      {
        k = arrayOfInt.length;
        i = j;
        while (i < k)
        {
          j = arrayOfInt[i];
          this.mPowerSaveTempWhitelistAppIds.put(j, true);
          i += 1;
        }
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void updatePowerSaveWhitelistUL()
  {
    int j = 0;
    try
    {
      int[] arrayOfInt = this.mDeviceIdleController.getAppIdWhitelistExceptIdle();
      this.mPowerSaveWhitelistExceptIdleAppIds.clear();
      int k;
      int i;
      if (arrayOfInt != null)
      {
        k = arrayOfInt.length;
        i = 0;
        while (i < k)
        {
          int m = arrayOfInt[i];
          this.mPowerSaveWhitelistExceptIdleAppIds.put(m, true);
          i += 1;
        }
      }
      arrayOfInt = this.mDeviceIdleController.getAppIdWhitelist();
      this.mPowerSaveWhitelistAppIds.clear();
      if (arrayOfInt != null)
      {
        k = arrayOfInt.length;
        i = j;
        while (i < k)
        {
          j = arrayOfInt[i];
          this.mPowerSaveWhitelistAppIds.put(j, true);
          i += 1;
        }
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  void updateRuleForAppIdleUL(int paramInt)
  {
    if (!isUidValidForBlacklistRules(paramInt)) {
      return;
    }
    int i = UserHandle.getAppId(paramInt);
    if (LOGV) {
      Log.e("NetworkPolicy", "setUidFirewallRule mPowerSaveTempWhitelistAppIds = " + this.mPowerSaveTempWhitelistAppIds.get(i) + " idle = " + isUidIdle(paramInt) + "  isUidForegroundOnRestrictPowerUL(uid) " + isUidForegroundOnRestrictPowerUL(paramInt));
    }
    if ((this.mPowerSaveTempWhitelistAppIds.get(i)) || (!isUidIdle(paramInt)) || (isUidForegroundOnRestrictPowerUL(paramInt)))
    {
      setUidFirewallRule(2, paramInt, 0);
      return;
    }
    setUidFirewallRule(2, paramInt, 2);
  }
  
  void updateRuleForDeviceIdleUL(int paramInt)
  {
    updateRulesForWhitelistedPowerSaveUL(paramInt, this.mDeviceIdleMode, 1);
  }
  
  void updateRuleForRestrictPowerUL(int paramInt)
  {
    updateRulesForWhitelistedPowerSaveUL(paramInt, this.mRestrictPower, 3);
  }
  
  void updateRulesForAppIdleParoleUL()
  {
    boolean bool1 = false;
    boolean bool2 = this.mUsageStats.isAppIdleParoleOn();
    int i;
    label35:
    int k;
    int j;
    if (bool2)
    {
      enableFirewallChainUL(2, bool1);
      int m = this.mUidFirewallStandbyRules.size();
      i = 0;
      if (i >= m) {
        return;
      }
      int n = this.mUidFirewallStandbyRules.keyAt(i);
      k = this.mUidRules.get(n);
      if (!bool1) {
        break label94;
      }
      j = k & 0xF;
      label71:
      updateRulesForPowerRestrictionsUL(n, j, bool2);
    }
    for (;;)
    {
      i += 1;
      break label35;
      bool1 = true;
      break;
      label94:
      j = k;
      if ((k & 0xF0) != 0) {
        break label71;
      }
    }
  }
  
  void updateRulesForAppIdleUL()
  {
    Trace.traceBegin(2097152L, "updateRulesForAppIdleUL");
    for (;;)
    {
      int i;
      int j;
      try
      {
        SparseIntArray localSparseIntArray = this.mUidFirewallStandbyRules;
        localSparseIntArray.clear();
        List localList = this.mUserManager.getUsers();
        i = localList.size() - 1;
        if (i >= 0)
        {
          Object localObject2 = (UserInfo)localList.get(i);
          localObject2 = this.mUsageStats.getIdleUidsForUser(((UserInfo)localObject2).id);
          int k = localObject2.length;
          j = 0;
          if (j >= k) {
            break label189;
          }
          int m = localObject2[j];
          if ((!this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(m), false)) && (hasInternetPermissions(m))) {
            localSparseIntArray.put(m, 2);
          }
        }
        else
        {
          if (LOGV) {
            Log.e("NetworkPolicy", "setUidFirewallRules FIREWALL_CHAIN_STANDBY " + localSparseIntArray);
          }
          setUidFirewallRulesAsync(2, localSparseIntArray, 0);
          return;
        }
      }
      finally
      {
        Trace.traceEnd(2097152L);
      }
      j += 1;
      continue;
      label189:
      i -= 1;
    }
  }
  
  void updateRulesForDeviceIdleUL()
  {
    Trace.traceBegin(2097152L, "updateRulesForDeviceIdleUL");
    try
    {
      updateRulesForWhitelistedPowerSaveUL(this.mDeviceIdleMode, 1, this.mUidFirewallDozableRules);
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  void updateRulesForPowerSaveUL()
  {
    Trace.traceBegin(2097152L, "updateRulesForPowerSaveUL");
    try
    {
      updateRulesForWhitelistedPowerSaveUL(this.mRestrictPower, 3, this.mUidFirewallPowerSaveRules);
      return;
    }
    finally
    {
      Trace.traceEnd(2097152L);
    }
  }
  
  void writePolicyAL()
  {
    if (LOGV) {
      Slog.v("NetworkPolicy", "writePolicyAL()");
    }
    Object localObject1 = null;
    FastXmlSerializer localFastXmlSerializer;
    try
    {
      FileOutputStream localFileOutputStream = this.mPolicyFile.startWrite();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer = new FastXmlSerializer();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "policy-list");
      localObject1 = localFileOutputStream;
      XmlUtils.writeIntAttribute(localFastXmlSerializer, "version", 10);
      localObject1 = localFileOutputStream;
      XmlUtils.writeBooleanAttribute(localFastXmlSerializer, "restrictBackground", this.mRestrictBackground);
      i = 0;
      localObject1 = localFileOutputStream;
      if (i < this.mNetworkPolicy.size())
      {
        localObject1 = localFileOutputStream;
        NetworkPolicy localNetworkPolicy = (NetworkPolicy)this.mNetworkPolicy.valueAt(i);
        localObject1 = localFileOutputStream;
        Object localObject2 = localNetworkPolicy.template;
        localObject1 = localFileOutputStream;
        if (!((NetworkTemplate)localObject2).isPersistable()) {
          break label781;
        }
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "network-policy");
        localObject1 = localFileOutputStream;
        XmlUtils.writeIntAttribute(localFastXmlSerializer, "networkTemplate", ((NetworkTemplate)localObject2).getMatchRule());
        localObject1 = localFileOutputStream;
        String str = ((NetworkTemplate)localObject2).getSubscriberId();
        if (str != null)
        {
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.attribute(null, "subscriberId", str);
        }
        localObject1 = localFileOutputStream;
        localObject2 = ((NetworkTemplate)localObject2).getNetworkId();
        if (localObject2 != null)
        {
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.attribute(null, "networkId", (String)localObject2);
        }
        localObject1 = localFileOutputStream;
        XmlUtils.writeIntAttribute(localFastXmlSerializer, "cycleDay", localNetworkPolicy.cycleDay);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "cycleTimezone", localNetworkPolicy.cycleTimezone);
        localObject1 = localFileOutputStream;
        XmlUtils.writeLongAttribute(localFastXmlSerializer, "warningBytes", localNetworkPolicy.warningBytes);
        localObject1 = localFileOutputStream;
        XmlUtils.writeLongAttribute(localFastXmlSerializer, "limitBytes", localNetworkPolicy.limitBytes);
        localObject1 = localFileOutputStream;
        XmlUtils.writeLongAttribute(localFastXmlSerializer, "lastWarningSnooze", localNetworkPolicy.lastWarningSnooze);
        localObject1 = localFileOutputStream;
        XmlUtils.writeLongAttribute(localFastXmlSerializer, "lastLimitSnooze", localNetworkPolicy.lastLimitSnooze);
        localObject1 = localFileOutputStream;
        XmlUtils.writeBooleanAttribute(localFastXmlSerializer, "metered", localNetworkPolicy.metered);
        localObject1 = localFileOutputStream;
        XmlUtils.writeBooleanAttribute(localFastXmlSerializer, "inferred", localNetworkPolicy.inferred);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "network-policy");
      }
    }
    catch (IOException localIOException)
    {
      if (localObject1 != null) {
        this.mPolicyFile.failWrite((FileOutputStream)localObject1);
      }
      return;
    }
    int i = 0;
    for (;;)
    {
      localObject1 = localIOException;
      int j;
      int k;
      if (i < this.mUidPolicy.size())
      {
        localObject1 = localIOException;
        j = this.mUidPolicy.keyAt(i);
        localObject1 = localIOException;
        k = this.mUidPolicy.valueAt(i);
        if (k != 0)
        {
          localObject1 = localIOException;
          localFastXmlSerializer.startTag(null, "uid-policy");
          localObject1 = localIOException;
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "uid", j);
          localObject1 = localIOException;
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "policy", k);
          localObject1 = localIOException;
          localFastXmlSerializer.endTag(null, "uid-policy");
        }
      }
      else
      {
        localObject1 = localIOException;
        localFastXmlSerializer.endTag(null, "policy-list");
        localObject1 = localIOException;
        localFastXmlSerializer.startTag(null, "whitelist");
        localObject1 = localIOException;
        j = this.mRestrictBackgroundWhitelistUids.size();
        i = 0;
        while (i < j)
        {
          localObject1 = localIOException;
          k = this.mRestrictBackgroundWhitelistUids.keyAt(i);
          localObject1 = localIOException;
          localFastXmlSerializer.startTag(null, "restrict-background");
          localObject1 = localIOException;
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "uid", k);
          localObject1 = localIOException;
          localFastXmlSerializer.endTag(null, "restrict-background");
          i += 1;
        }
        localObject1 = localIOException;
        j = this.mRestrictBackgroundWhitelistRevokedUids.size();
        i = 0;
        while (i < j)
        {
          localObject1 = localIOException;
          k = this.mRestrictBackgroundWhitelistRevokedUids.keyAt(i);
          localObject1 = localIOException;
          localFastXmlSerializer.startTag(null, "revoked-restrict-background");
          localObject1 = localIOException;
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "uid", k);
          localObject1 = localIOException;
          localFastXmlSerializer.endTag(null, "revoked-restrict-background");
          i += 1;
        }
        localObject1 = localIOException;
        localFastXmlSerializer.endTag(null, "whitelist");
        localObject1 = localIOException;
        localFastXmlSerializer.endDocument();
        localObject1 = localIOException;
        this.mPolicyFile.finishWrite(localIOException);
        return;
        label781:
        i += 1;
        break;
      }
      i += 1;
    }
  }
  
  private class AppIdleStateChangeListener
    extends UsageStatsManagerInternal.AppIdleStateChangeListener
  {
    private AppIdleStateChangeListener() {}
    
    public void onAppIdleStateChanged(String arg1, int paramInt, boolean paramBoolean)
    {
      try
      {
        paramInt = NetworkPolicyManagerService.-get3(NetworkPolicyManagerService.this).getPackageManager().getPackageUidAsUser(???, 8192, paramInt);
        if (NetworkPolicyManagerService.-get1()) {
          Log.v("NetworkPolicy", "onAppIdleStateChanged(): uid=" + paramInt + ", idle=" + paramBoolean);
        }
        synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
        {
          NetworkPolicyManagerService.this.updateRuleForAppIdleUL(paramInt);
          NetworkPolicyManagerService.-wrap16(NetworkPolicyManagerService.this, paramInt);
          return;
        }
        return;
      }
      catch (PackageManager.NameNotFoundException ???) {}
    }
    
    public void onParoleStateChanged(boolean paramBoolean)
    {
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        NetworkPolicyManagerService.this.updateRulesForAppIdleParoleUL();
        return;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(flag=false, value={0L, 1L, 2L})
  public static @interface ChainToggleType {}
  
  private class NetworkPolicyManagerInternalImpl
    extends NetworkPolicyManagerInternal
  {
    private NetworkPolicyManagerInternalImpl() {}
    
    public void resetUserState(int paramInt)
    {
      boolean bool;
      synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock)
      {
        bool = NetworkPolicyManagerService.this.removeUserStateUL(paramInt, false);
        if (!NetworkPolicyManagerService.-wrap0(NetworkPolicyManagerService.this, paramInt)) {
          if (!bool) {}
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(flag=false, value={1L, 2L})
  public static @interface RestrictType {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkPolicyManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */